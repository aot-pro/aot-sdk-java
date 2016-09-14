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

import java.util.NoSuchElementException;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class EventMixerIterator implements EventIterator {
    protected final EventFilter filter;
    protected final EventIterator[] iterators;
    protected final Event[] prevEvents;
    protected final Event[] nextEvents;

    public EventMixerIterator(EventFilter filter, EventIterator[] iterators) {
        this.filter = filter;
        this.iterators = iterators;
        this.prevEvents = new Event[iterators.length];
        this.nextEvents = new Event[iterators.length];
    }

    @Override
    public EventFilter filter() {
        return filter;
    }

    @Override
    public boolean hasPrev() {
        return false;
    }

    @Override
    public Event prev() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Event next() {
        int min = -1;
        Event minEvent = null;
        for (int i = 0, ci = nextEvents.length; i < ci; ++i) {
            Event nextEvent = nextEvents[i];
            if (nextEvent != null) {
                if (minEvent != null) {
                    if (nextEvent.compareTo(minEvent) < 0) {
                        min = i;
                        minEvent = nextEvent;
                    }
                } else {
                    min = i;
                    minEvent = nextEvent;
                }
            }
        }
        if (minEvent != null) {
            EventIterator iterator = iterators[min];
            if (iterator.hasNext()) {
                nextEvents[min] = iterator.next();
            } else {
                nextEvents[min] = null;
            }
            prevEvents[min] = minEvent;
            return minEvent;
        } else {
            throw new NoSuchElementException("Next event is not found");
        }
    }
}
