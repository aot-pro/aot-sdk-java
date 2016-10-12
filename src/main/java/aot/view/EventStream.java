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

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class EventStream implements Iterable<Event> {
    protected final EventFilter filter;
    protected final Iterable<EventSource> sources;

    public <T extends EventSource> EventStream(EventFilter filter, final Iterable<T> sources) {
        this.filter = filter;
        this.sources = new Iterable<EventSource>() {
            @Override
            public Iterator<EventSource> iterator() {
                return new Iterator<EventSource>() {
                    private final Iterator<T> iterator = sources.iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public EventSource next() {
                        return iterator.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                };
            }
        };
    }

    public EventFilter getFilter() {
        return filter;
    }

    @Override
    public Iterator<Event> iterator() {
        return new Iterator<Event>() {
            private final Iterator<EventSource> sourceIterator = sources.iterator();
            private Iterator<Event> eventIterator = sourceIterator.hasNext() ? sourceIterator.next().getEvents(filter).iterator() : Collections.<Event>emptyIterator();
            private Event event = findNext();

            private Event findNext() {
                if (eventIterator.hasNext()) {
                    return eventIterator.next();
                } else {
                    while (sourceIterator.hasNext()) {
                        eventIterator = sourceIterator.next().getEvents(filter).iterator();
                        if (eventIterator.hasNext()) {
                            return eventIterator.next();
                        }
                    }
                    return null;
                }
            }

            @Override
            public boolean hasNext() {
                return event != null;
            }

            @Override
            public Event next() {
                Event ev = event;
                if (ev != null) {
                    event = findNext();
                    return ev;
                } else {
                    throw new NoSuchElementException("Next event is not found");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
}
