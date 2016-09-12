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
public class LogMonth implements Iterable<LogDay> {
    protected final LogYear year;
    protected final String id;

    protected LogMonth(LogYear year, String id) {
        this.year = year;
        this.id = id;
    }

    public LogYear getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    @Override
    public Iterator<LogDay> iterator() {
        return null;
    }
}
