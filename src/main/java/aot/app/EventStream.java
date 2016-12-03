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

package aot.app;

import aot.util.CborUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class EventStream implements Iterable<Event> {
    protected final HashMap<Integer, String> strings = new HashMap<>(4096);
    protected final HashMap<Integer, Map<String, String>> tags = new HashMap<>(4096);
    protected final ByteBuffer buffer;

    public EventStream(byte[] buffer) {
        this.buffer = ByteBuffer.wrap(buffer);
    }

    public int skip(int offset) {
        return offset + 5 + getLength(offset);
    }

    public byte getType(int offset) {
        return buffer.get(offset);
    }

    public int getLength(int offset) {
        return buffer.getInt(offset + 1);
    }

    public byte[] getBytes(int offset) {
        byte[] bytes = new byte[getLength(offset)];
        System.arraycopy(buffer.array(), offset + 5, bytes, 0, bytes.length);
        return bytes;
    }

    public String getString(int offset) {
        String s = strings.get(offset);
        if (s == null) {
            s = CborUtil.fromBytes(buffer.array(), offset + 5, buffer.getInt(offset + 1), String.class);
            strings.put(offset, s);
        }
        return s;
    }

    public Map<String, String> getTags(int offset) {
        Map<String, String> ts = tags.get(offset);
        if (ts == null) {
            int[] tsi = CborUtil.fromBytes(buffer.array(), offset + 5, buffer.getInt(offset + 1), int[].class);
            ts = new TreeMap<>();
            for (int i = 0, ci = tsi.length; i < ci; i += 2) {
                ts.put(getString(tsi[i]), getString(tsi[i + 1]));
            }
            tags.put(offset, ts);
        }
        return ts;
    }

    public Event getEvent(int offset) {
        byte type = getType(offset);
        if (type == 3) {
            return Event.valueOf(getBytes(offset));
        } else if (type == 4) {
            return BinaryEvent.valueOf(getBytes(offset));
        } else if (type == 5) {
            return null;
        } else if (type == 6) {
            return null;
        } else if (type == 7) {
            return null;
        } else {
            throw new EventTypeNotFoundException(String.format("Event type '%d' is not found", type));
        }
    }

    @Override
    public Iterator<Event> iterator() {
        return new Iterator<Event>() {
            private int offset = 24;
            private final int size = EventStream.this.buffer.array().length;

            @Override
            public boolean hasNext() {
                while (offset < size) {
                    if (EventStream.this.getType(offset) >= 3) {
                        return true;
                    } else {
                        offset = skip(offset);
                    }
                }
                return false;
            }

            @Override
            public Event next() {
                if (hasNext()) {
                    return EventStream.this.getEvent(offset);
                } else {
                    throw new NoSuchElementException(String.format("Event is not found at offset '%d' of size '%d'", offset, size));
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
}
