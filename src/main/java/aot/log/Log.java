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

package aot.log;

import aot.Config;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Log {
    private static Config config;
    private static final AtomicReference<Layer[]> layers = new AtomicReference<>(new Layer[0]);
    private static final ThreadLocal<LinkedHashMap<String, String>> tags = new ThreadLocal<LinkedHashMap<String, String>>() {
        @Override
        protected LinkedHashMap<String, String> initialValue() {
            return new LinkedHashMap<>();
        }
    };

    private Log() {
    }

    public static void log(String channel, String module, String message) {
    }

    public static boolean addTag(String key, String value) {
        LinkedHashMap<String, String> tags = Log.tags.get();
        if (!tags.containsKey(key)) {
            tags.put(key, value);
            return true;
        } else {
            return false;
        }
    }

    public static void removeTag(String key) {
        Log.tags.get().remove(key);
    }
}
