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

import java.util.Arrays;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class LogMixer implements LogIterator {
    protected final LogIterator[] iterators;
    protected final boolean[] head;
    protected final boolean[] tail;
    protected final LogEvent[] events;

    public LogMixer(LogIterator[] iterators) {
        this.iterators = iterators.clone();
        this.head = new boolean[this.iterators.length];
        this.tail = new boolean[this.iterators.length];
        this.events = new LogEvent[this.iterators.length];

        Arrays.fill(this.head, true);
        Arrays.fill(this.tail, false);
    }

    @Override
    public boolean hasPrev() {
        for (LogIterator it : iterators) {
        }
        return false;
    }

    @Override
    public LogEvent prev() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public LogEvent next() {
        return null;
    }

    @Override
    public void remove() {
    }
}
