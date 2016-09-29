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

package aot.util.thread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class ThreadUtil {
    private static final Object sleep = new Object();
    private static final AtomicLong threads = new AtomicLong(0);
    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private ThreadUtil() {
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                while (threads.get() > 0L) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    public static long lockThread() {
        return threads.incrementAndGet();
    }

    public static long unlockThread() {
        return threads.decrementAndGet();
    }

    public static void sleep(long timeout) {
        if (!shutdown.get()) {
            synchronized (sleep) {
                try {
                    sleep.wait(timeout);
                } catch (InterruptedException e) {
                    if (!shutdown.get()) {
                        throw new ThreadInterruptedException(e);
                    } else {
                        throw new ThreadShutdownException(e);
                    }
                }
            }
        } else {
            throw new ThreadShutdownException();
        }
    }
}
