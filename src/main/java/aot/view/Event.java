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

package aot.view;

import java.util.Map;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Event implements Comparable<Event> {
    protected final LogFile file;
    protected final long time;
    protected final String logger;
    protected final String message;
    protected final Map<String, String> tags;

    protected Event(LogFile file, long time, String logger, String message, Map<String, String> tags) {
        this.file = file;
        this.time = time;
        this.logger = logger;
        this.message = message;
        this.tags = tags;
    }

    public LogFile getFile() {
        return file;
    }

    public long getTime() {
        return time;
    }

    public String getLogger() {
        return logger;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public int compareTo(Event event) {
        if (time < event.time) {
            return -1;
        } else if (time > event.time) {
            return 1;
        } else {
            return 0;
        }
    }
}
