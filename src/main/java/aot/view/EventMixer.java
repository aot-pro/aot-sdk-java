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
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class EventMixer implements Iterable<Event> {
    protected final EventFilter filter;
    protected final EventSource[] sources;

    public <T extends EventSource> EventMixer(EventFilter filter, Iterable<T> sources) {
        this.filter = filter;
        this.sources = toArray(sources);
    }

    public EventFilter getFilter() {
        return filter;
    }

    public EventSource[] getSources() {
        return sources;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Event> iterator() {
        final Iterator<Event>[] iters = new Iterator[sources.length];
        int eventCount = 0;
        Event[] events = new Event[sources.length];
        for (int i = 0, ci = sources.length; i < ci; ++i) {
            Iterator<Event> iter = sources[i].getEvents(filter).iterator();
            iters[i] = iter;
            if (iter.hasNext()) {
                eventCount++;
                events[i] = iter.next();
            } else {
                events[i] = null;
            }
        }
        final Event[] evs = (eventCount > 0) ? events : null;
        return new Iterator<Event>() {
            private final Iterator<Event>[] iterators = iters;
            private Event[] events = evs;

            @Override
            public boolean hasNext() {
                return events != null;
            }

            @Override
            public Event next() {
                int eventCount = 0;
                int min = -1;
                Event minEvent = null;
                for (int i = 0, ci = events.length; i < ci; ++i) {
                    Event event = events[i];
                    if (event != null) {
                        eventCount++;
                        if (minEvent != null) {
                            if (event.compareTo(minEvent) < 0) {
                                min = i;
                                minEvent = event;
                            }
                        } else {
                            min = i;
                            minEvent = event;
                        }
                    }
                }
                if (minEvent != null) {
                    Iterator<Event> iterator = iterators[min];
                    if (iterator.hasNext()) {
                        events[min] = iterator.next();
                    } else {
                        events[min] = null;
                    }
                    if (events[min] == null) {
                        eventCount--;
                        if (eventCount == 0) {
                            events = null;
                        }
                    }
                    return minEvent;
                } else {
                    throw new NoSuchElementException("Next event is not found");
                }
            }
        };
    }

    private static <T extends EventSource> EventSource[] toArray(Iterable<T> sources) {
        LinkedList<EventSource> list = new LinkedList<>();
        for (EventSource source : sources) {
            list.add(source);
        }
        return list.toArray(new EventSource[list.size()]);
    }
}
