/*
 * Copyright (C) 2014 Dmitry Kotlyarov.
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

import java.util.LinkedHashMap;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Logger {
    private static final Level trace = new Level("trace", 4096, null, null);
    private static final Level debug = new Level("debug", 4096, null, null);
    private static final Level info = new Level("info", 4096, null, null);
    private static final Level warn = new Level("warn", 4096, null, null);
    private static final Level error = new Level("error", 4096, null, null);
    private static final ThreadLocal<LinkedHashMap<String, String>> tags = new ThreadLocal<LinkedHashMap<String, String>>() {
        @Override
        protected LinkedHashMap<String, String> initialValue() {
            return new LinkedHashMap<>();
        }
    };

    private Logger() {
    }

    public static boolean addTag(String id, String value) {
        LinkedHashMap<String, String> tags = Logger.tags.get();
        if (!tags.containsKey(id)) {
            tags.put(id, value);
            return true;
        } else {
            return false;
        }
    }

    public static void removeTag(String id) {
        Logger.tags.get().remove(id);
    }

    public static void log(String channel, String module, String message) {
    }
}
