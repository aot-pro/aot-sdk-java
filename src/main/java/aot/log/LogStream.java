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

import aot.util.cbor.CborUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
final class LogStream {
    private final HashMap<Integer, String> strings = new HashMap<>(4096);
    private final HashMap<Integer, Map<String, String>> tags = new HashMap<>(4096);
    private final ByteBuffer buffer;

    LogStream(byte[] buffer) {
        this.buffer = ByteBuffer.wrap(buffer);
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
}
