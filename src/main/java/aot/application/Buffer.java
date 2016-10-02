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

package aot.application;

import aot.storage.Storage;
import aot.util.cbor.CborUtil;
import aot.util.map.MapUtil;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
final class Buffer {
    private final AtomicInteger offset = new AtomicInteger(24);
    private final int size;
    private final ByteBuffer buffer;
    private final AtomicLong beginTime = new AtomicLong(Long.MAX_VALUE);
    private final ConcurrentHashMap<String, Integer> strings = new ConcurrentHashMap<>(4096);
    private final ThreadLocal<ThreadTags> threadTags = new ThreadLocal<ThreadTags>() {
        @Override
        protected ThreadTags initialValue() {
            return new ThreadTags();
        }
    };

    public Buffer(int size) {
        this.size = size;
        this.buffer = ByteBuffer.allocate(size);
    }

    public int log(String logger, String message, long tagsRevision, Map<String, String> tags) {
        long t = System.currentTimeMillis();
        int o = putEvent(new Event(t, putString(logger), message, putTags(tagsRevision, tags)));
        for (long bt = beginTime.get(); t < bt; bt = beginTime.get()) {
            beginTime.compareAndSet(bt, t);
        }
        return o;
    }

    private int putBytes(byte type, byte[] bytes) {
        int l = bytes.length + 5;
        int o = offset.getAndAdd(l);
        if (o + l <= size) {
            buffer.put(o, type);
            buffer.putInt(o + 1, bytes.length);
            System.arraycopy(bytes, 0, buffer.array(), o + 5, bytes.length);
            return o;
        } else {
            throw new BufferException();
        }
    }

    private int putString(String string) {
        Integer o = strings.get(string);
        if (o == null) {
            o = MapUtil.putIfAbsent(strings, string, putBytes((byte) 1, CborUtil.toBytes(string)));
        }
        return o;
    }

    private int putTags(long tagsRevision, Map<String, String> tags) {
        ThreadTags tts = threadTags.get();
        if (tts.revision != tagsRevision) {
            int[] tsi = new int[tags.size() * 2];
            int i = 0;
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                tsi[i] = putString(tag.getKey());
                tsi[i + 1] = putString(tag.getValue());
                i += 2;
            }
            tts.offset = putBytes((byte) 2, CborUtil.toBytes(tsi));
            tts.revision = tagsRevision;
        }
        return tts.offset;
    }

    private int putEvent(Event event) {
        return putBytes((byte) 3, event.toBytes());
    }

    public boolean upload(long time, long span) {
        if (time >= beginTime.get() + span) {
            offset.getAndAdd(size);
        }
        if (offset.get() >= size) {
        }
        return false;
    }

    private static final class ThreadTags {
        public long revision = 0;
        public int offset = 0;
    }
}
