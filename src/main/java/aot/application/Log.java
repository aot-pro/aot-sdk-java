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

package aot.application;

import aot.util.ThreadLock;
import aot.util.ThreadUtil;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Log {
    private static final AtomicReference<Config> config = new AtomicReference<>(null);
    private static final AtomicReference<LinkedHashMap<String, Layer>> layers = new AtomicReference<>(null);
    private static final ThreadLocal<ThreadInfo> threadInfo = new ThreadLocal<ThreadInfo>() {
        @Override
        protected ThreadInfo initialValue() {
            return new ThreadInfo();
        }
    };

    private Log() {
    }

    static {
        Thread t = new Thread("aot-application-log") {
            @Override
            public void run() {
                try (ThreadLock tl = new ThreadLock()) {
                    while (!ThreadUtil.isShutdown()) {
                    }
                }
            }
        };
        t.setDaemon(false);
        t.start();
    }

    public static void log(String layer, String logger, String message) {
        Layer l = Log.layers.get().get(layer);
        if (l != null) {
            ThreadInfo ti = Log.threadInfo.get();
            l.log(logger, ti.shift, ti.tagsRevision, ti.tags, message);
        }
    }

    public static boolean addTag(String key, String value) {
        ThreadInfo ti = Log.threadInfo.get();
        if (!ti.tags.containsKey(key)) {
            ti.tags.put(key, value);
            if (ti.tagsRevision < Long.MAX_VALUE) {
                ti.tagsRevision++;
            } else {
                ti.tagsRevision = 0L;
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeTag(String key) {
        ThreadInfo ti = Log.threadInfo.get();
        if (ti.tags.remove(key) != null) {
            if (ti.tagsRevision < Long.MAX_VALUE) {
                ti.tagsRevision++;
            } else {
                ti.tagsRevision = 0L;
            }
            return true;
        } else {
            return false;
        }
    }

    private static final class ThreadInfo {
        public short shift = 0;
        public long tagsRevision = 0L;
        public final LinkedHashMap<String, String> tags = new LinkedHashMap<>();
    }
}
