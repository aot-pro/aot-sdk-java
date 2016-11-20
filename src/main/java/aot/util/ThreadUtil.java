/*
 * Copyright (C) 2016 Dmitry Kotlyarov.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aot.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class ThreadUtil {
    private static final Object sleep = new Object();
    private static final AtomicLong threads = new AtomicLong(0L);
    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private ThreadUtil() {
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
                while (threads.get() > 0L) {
                    synchronized (sleep) {
                        sleep.notifyAll();
                    }
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    public static void sleep(long timeout) {
        if (!shutdown.get()) {
            try {
                synchronized (sleep) {
                    if (!shutdown.get()) {
                        sleep.wait(timeout);
                        if (shutdown.get()) {
                            throw new ThreadShutdownException();
                        }
                    } else {
                        throw new ThreadShutdownException();
                    }
                }
            } catch (InterruptedException e) {
                if (!shutdown.get()) {
                    throw new ThreadInterruptedException(e);
                } else {
                    throw new ThreadShutdownException(e);
                }
            }
        } else {
            throw new ThreadShutdownException();
        }
    }

    public static long lock() {
        return threads.incrementAndGet();
    }

    public static long unlock() {
        return threads.decrementAndGet();
    }

    public static boolean isShutdown() {
        return shutdown.get();
    }

    public static boolean shutdown() {
        if (!shutdown.get()) {
            synchronized (sleep) {
                if (!shutdown.get()) {
                    shutdown.set(true);
                    sleep.notifyAll();
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
