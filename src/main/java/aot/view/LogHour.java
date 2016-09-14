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
public class LogHour implements Iterable<LogMinute>, EventSource {
    protected final LogDay day;
    protected final String id;

    protected LogHour(LogDay day, String id) {
        this.day = day;
        this.id = id;
    }

    public LogDay getDay() {
        return day;
    }

    public String getId() {
        return id;
    }

    @Override
    public Iterator<LogMinute> iterator() {
        return null;
    }

    @Override
    public EventIterable getEvents(EventFilter filter) {
        return null;
    }
}