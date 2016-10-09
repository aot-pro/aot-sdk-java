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

import java.util.LinkedHashMap;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public enum BufferElementType {
    TAGS((byte) -2),
    STRING((byte) -1),
    END((byte) 0),
    EVENT((byte) 1),
    BINARY_EVENT((byte) 2),
    EXCEPTION_EVENT((byte) 3),
    START_EVENT((byte) 4),
    FINISH_EVENT((byte) 5);

    private static final LinkedHashMap<Byte, BufferElementType> events = new LinkedHashMap<>(64);

    public final byte id;

    BufferElementType(byte id) {
        this.id = id;
    }

    static {
        events.put(EVENT.id, EVENT);
        events.put(BINARY_EVENT.id, BINARY_EVENT);
        events.put(EXCEPTION_EVENT.id, EXCEPTION_EVENT);
        events.put(START_EVENT.id, START_EVENT);
        events.put(FINISH_EVENT.id, FINISH_EVENT);
    }

    public static boolean isEvent(Byte id) {
        return events.containsKey(id);
    }
}
