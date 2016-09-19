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

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Util {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private static final Object sleep = new Object();
    private static final AtomicLong threads = new AtomicLong(0);
    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private Util() {
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            }
        });
    }

    public static void addThread() {
        threads.incrementAndGet();
    }

    public static void removeThread() {
        threads.decrementAndGet();
    }

    public static void sleep(long timeout) {
        if (!shutdown.get()) {
            synchronized (sleep) {
                try {
                    sleep.wait(timeout);
                } catch (InterruptedException e) {
                    if (!shutdown.get()) {
                        throw new InterruptedRuntimeException(e);
                    } else {
                        throw new ShutdownException(e);
                    }
                }
            }
        } else {
            throw new ShutdownException();
        }
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static <T> String formatClassName(Class<T> clazz) {
        return clazz.getName().replace('.', '/');
    }
}
