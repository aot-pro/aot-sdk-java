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
import aot.util.io.IOUtil;
import aot.util.map.MapUtil;
import aot.util.string.StringUtil;
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
    public static final int OFFSET = 40;

    private final AtomicInteger threads = new AtomicInteger(0);
    private final AtomicInteger events = new AtomicInteger(0);
    private final AtomicInteger offset = new AtomicInteger(OFFSET);
    private final AtomicInteger size = new AtomicInteger(OFFSET);
    private final int capacity;
    private final ByteBuffer buffer;
    private final byte[] array;
    private final AtomicLong revision = new AtomicLong(0L);
    private final AtomicLong begin = new AtomicLong(Long.MAX_VALUE);
    private final ConcurrentHashMap<String, Integer> strings = new ConcurrentHashMap<>(4096);
    private final ThreadLocal<ThreadTags> threadTags = new ThreadLocal<ThreadTags>() {
        @Override
        protected ThreadTags initialValue() {
            return new ThreadTags();
        }
    };

    public Buffer(int capacity) {
        this.capacity = capacity;
        this.buffer = ByteBuffer.allocate(capacity);
        this.array = buffer.array();
    }

    public int log(long time, String logger, short shift, long tagsRevision, Map<String, String> tags, String message) {
        if (offset.get() < capacity) {
            threads.incrementAndGet();
            try {
                return putEvent(time, putString(logger), shift, putTags(tagsRevision, tags), message);
            } finally {
                threads.decrementAndGet();
            }
        } else {
            throw new BufferException();
        }
    }

    private int putString(String string) {
        Integer off = strings.get(string);
        if (off == null) {
            byte[] sd = string.getBytes(StringUtil.CHARSET_UTF8);
            int l = sd.length + 5;
            int o = offset.getAndAdd(l);
            if (o + l < capacity) {
                size.getAndAdd(l);
                buffer.put(o, BufferElementType.STRING.id);
                buffer.putInt(o + 1, l - 5);
                System.arraycopy(sd, 0, array, o + 5, sd.length);
                return MapUtil.putIfAbsent(strings, string, o);
            } else {
                throw new BufferException();
            }
        } else {
            return off;
        }
    }

    private int putTags(long tagsRevision, Map<String, String> tags) {
        long bufferRevision = revision.get();
        ThreadTags tts = threadTags.get();
        if ((tts.bufferRevision != bufferRevision) || (tts.tagsRevision != tagsRevision)) {
            int l = tags.size() * 4 * 2 + 5;
            int o = offset.getAndAdd(l);
            if (o + l < capacity) {
                size.getAndAdd(l);
                buffer.put(o, BufferElementType.TAGS.id);
                buffer.putInt(o + 1, l - 5);
                int i = 0;
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    buffer.putInt(o + 5 + i, putString(tag.getKey()));
                    buffer.putInt(o + 5 + i + 1, putString(tag.getValue()));
                    i += 2;
                }
                tts.bufferRevision = bufferRevision;
                tts.tagsRevision = tagsRevision;
                tts.offset = o;
                return o;
            } else {
                throw new BufferException();
            }
        } else {
            return tts.offset;
        }
    }

    private int putEvent(long time, int logger, short shift, int tags, String message) {
        byte[] md = message.getBytes(StringUtil.CHARSET_UTF8);
        int l = md.length + 23;
        int o = offset.getAndAdd(l);
        if (o + l < capacity) {
            size.getAndAdd(l);
            buffer.put(o, BufferElementType.EVENT.id);
            buffer.putInt(o + 1, l - 5);
            buffer.putLong(o + 5, time);
            buffer.putInt(o + 13, logger);
            buffer.putShort(o + 17, shift);
            buffer.putInt(o + 19, tags);
            System.arraycopy(md, 0, array, o + 23, md.length);
            if (events.incrementAndGet() == 1) {
                begin.set(time);
            }
            return o;
        } else {
            throw new BufferException();
        }
    }

    public boolean upload(Storage storage, long time, long span, AtomicLong lost) {
        if (time - span >= begin.get()) {
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
            long b = Long.MAX_VALUE;
            long e = Long.MIN_VALUE;
            int es = events.get();
            for (int i = 0, j = OFFSET; i < es; j = j + 1 + buffer.getInt(j + 1)) {
                byte t = buffer.get(j);
                if (BufferElementType.isEvent(t)) {
                    long evt = buffer.getLong(j + 5);
                    if (evt < b) {
                        b = evt;
                    }
                    if (evt > e) {
                        e = evt;
                    }
                }
            }
            int s = size.get();
            long l = lost.getAndSet(0L);
            buffer.putInt(0, 0x4C4F4720);
            buffer.putInt(4, 0x00010000);
            buffer.putLong(8, b);
            buffer.putLong(16, e);
            buffer.putInt(24, s);
            buffer.putInt(28, es);
            buffer.putLong(32, l);
            Calendar calendar = new GregorianCalendar(TimeUtil.TIMEZONE_UTC);
            calendar.setTimeInMillis(b);
            String key = String.format("/%04d/%02d/%02d/%02d/%02d/%02d/%03d/%d-%d-%d-%d-%d.log",
                                       calendar.get(Calendar.YEAR),
                                       calendar.get(Calendar.MONTH) + 1,
                                       calendar.get(Calendar.DAY_OF_MONTH),
                                       calendar.get(Calendar.HOUR),
                                       calendar.get(Calendar.MINUTE),
                                       calendar.get(Calendar.SECOND),
                                       calendar.get(Calendar.MILLISECOND),
                                       b, e, s, es, l);
            storage.put(key, IOUtil.compress(array, 0, s));
            strings.clear();
            begin.set(Long.MAX_VALUE);
            if (revision.get() < Long.MAX_VALUE) {
                revision.incrementAndGet();
            } else {
                revision.set(0L);
            }
            events.set(0);
            size.set(OFFSET);
            offset.set(OFFSET);
            return true;
        } else {
            return false;
        }
    }

    private static final class ThreadTags {
        public long bufferRevision = -1L;
        public long tagsRevision = -1L;
        public int offset = -1;
    }
}
