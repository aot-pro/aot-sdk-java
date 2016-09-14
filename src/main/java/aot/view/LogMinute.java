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

import java.util.Iterator;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class LogMinute implements Iterable<LogFile>, EventSource {
    protected final LogHour hour;
    protected final String id;

    protected LogMinute(LogHour hour, String id) {
        this.hour = hour;
        this.id = id;
    }

    public LogHour getHour() {
        return hour;
    }

    public String getId() {
        return id;
    }

    @Override
    public Iterator<LogFile> iterator() {
        return null;
    }

    @Override
    public EventIterable getEvents(EventFilter filter) {
        return null;
    }
}
