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

import aot.storage.Storage;
import aot.util.Util;
import aot.util.cbor.CborUtil;
import aot.util.io.IOUtil;
import aot.util.map.MapUtil;
import aot.util.time.TimeUtil;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Buffer {
    private final AtomicInteger threads = new AtomicInteger(0);
    private final AtomicInteger offset = new AtomicInteger(24);
    private final AtomicInteger size = new AtomicInteger(24);
    private final int capacity;
    private final ByteBuffer dataBuffer;
    private final byte[] dataArray;
    private final AtomicLong beginTime = new AtomicLong(Long.MAX_VALUE);
    private final ConcurrentHashMap<String, Integer> strings = new ConcurrentHashMap<>(4096);
    private final ThreadLocal<ThreadTags> threadTags = new ThreadLocal<ThreadTags>() {
        @Override
        protected ThreadTags initialValue() {
            return new ThreadTags();
        }
    };

    public Buffer(int capacity) {
        this.capacity = capacity;
        this.dataBuffer = ByteBuffer.allocate(capacity);
        this.dataArray = dataBuffer.array();
    }

    public int log(String logger, short shift, long tagsRevision, Map<String, String> tags, String message) {
        if (offset.get() < capacity) {
            threads.incrementAndGet();
            try {
                return putEvent(putString(logger), shift, putTags(tagsRevision, tags), message);
            } finally {
                threads.decrementAndGet();
            }
        } else {
            throw new BufferException();
        }
    }

    private int putBytes(byte type, byte[] bytes) {
        int l = bytes.length + 5;
        int o = offset.getAndAdd(l);
        if (o + l < capacity) {
            size.getAndAdd(l);
            dataBuffer.put(o, type);
            dataBuffer.putInt(o + 1, bytes.length);
            System.arraycopy(bytes, 0, dataBuffer.array(), o + 5, bytes.length);
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

    private int putEvent(int logger, short shift, int tags, String message) {
        byte[] md = message.getBytes(Util.CHARSET_UTF8);
        int l = md.length + 23;
        int o = offset.getAndAdd(l);
        long time = System.currentTimeMillis();
        if (o + l < capacity) {
            size.getAndAdd(l);
            dataBuffer.put(o, BufferElementType.EVENT.id);
            dataBuffer.putInt(o + 1, l - 5);
            dataBuffer.putLong(o + 5, time);
            dataBuffer.putInt(o + 13, logger);
            dataBuffer.putShort(o + 17, shift);
            dataBuffer.putInt(o + 19, tags);
            System.arraycopy(md, 0, dataArray, o + 23, md.length);
            return o;
        } else {
            throw new BufferException();
        }
    }

    public boolean upload(Storage storage, long time, long span, AtomicLong lost) {
        if (time - span >= beginTime.get()) {
            offset.getAndAdd(capacity);
        }
        if (offset.get() >= capacity) {
            while (threads.get() > 0) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            long bt = beginTime.get();
            int s = size.get();
            long l = lost.getAndSet(0L);
            dataBuffer.putInt(0, 0x4C4F4720);
            dataBuffer.putLong(4, bt);
            dataBuffer.putInt(12, s);
            dataBuffer.putLong(16, l);
            Calendar calendar = new GregorianCalendar(TimeUtil.TIMEZONE_UTC);
            calendar.setTimeInMillis(bt);
            String key = String.format("/%04d/%02d/%02d/%02d/%02d/%02d/%03d/%d-%d-%d-%d.log",
                                       calendar.get(Calendar.YEAR),
                                       calendar.get(Calendar.MONTH) + 1,
                                       calendar.get(Calendar.DAY_OF_MONTH),
                                       calendar.get(Calendar.HOUR),
                                       calendar.get(Calendar.MINUTE),
                                       calendar.get(Calendar.SECOND),
                                       calendar.get(Calendar.MILLISECOND),
                                       bt, s, 0, l);
            storage.put(key, IOUtil.compress(dataArray, 0, s));
            strings.clear();
            beginTime.set(Long.MAX_VALUE);
            size.set(24);
            offset.set(24);
            return true;
        } else {
            return false;
        }
    }

    private static final class ThreadTags {
        public long revision = 0;
        public int offset = 0;
    }
}
