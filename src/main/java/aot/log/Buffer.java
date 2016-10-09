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
    private static final int DO = 40;

    private final AtomicInteger threads = new AtomicInteger(0);
    private final AtomicInteger events = new AtomicInteger(0);
    private final AtomicInteger offset = new AtomicInteger(DO);
    private final AtomicInteger size = new AtomicInteger(DO);
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
                dataBuffer.put(o, BufferElementType.STRING.id);
                dataBuffer.putInt(o + 1, l - 5);
                System.arraycopy(sd, 0, dataArray, o + 5, sd.length);
                return MapUtil.putIfAbsent(strings, string, o);
            } else {
                throw new BufferException();
            }
        } else {
            return off;
        }
    }

    private int putTags(long tagsRevision, Map<String, String> tags) {
        ThreadTags tts = threadTags.get();
        if (tts.revision != tagsRevision) {
            int l = tags.size() * 4 * 2 + 5;
            int o = offset.getAndAdd(l);
            if (o + l < capacity) {
                size.getAndAdd(l);
                dataBuffer.put(o, BufferElementType.TAGS.id);
                dataBuffer.putInt(o + 1, l - 5);
                int i = 0;
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    dataBuffer.putInt(o + 5 + i, putString(tag.getKey()));
                    dataBuffer.putInt(o + 5 + i + 1, putString(tag.getValue()));
                    i += 2;
                }
                tts.revision = tagsRevision;
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
            dataBuffer.put(o, BufferElementType.EVENT.id);
            dataBuffer.putInt(o + 1, l - 5);
            dataBuffer.putLong(o + 5, time);
            dataBuffer.putInt(o + 13, logger);
            dataBuffer.putShort(o + 17, shift);
            dataBuffer.putInt(o + 19, tags);
            System.arraycopy(md, 0, dataArray, o + 23, md.length);
            if (events.incrementAndGet() == 1) {
                beginTime.set(time);
            }
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
            long bt = Long.MAX_VALUE;
            long et = Long.MIN_VALUE;
            int es = events.get();
            for (int i = 0, j = DO; i < es; j = j + 1 + dataBuffer.getInt(j + 1)) {
                byte t = dataBuffer.get(j);
                if (BufferElementType.isEvent(t)) {
                    long evt = dataBuffer.getLong(j + 5);
                    if (evt < bt) {
                        bt = evt;
                    }
                    if (evt > et) {
                        et = evt;
                    }
                }
            }
            int s = size.get();
            long l = lost.getAndSet(0L);
            dataBuffer.putInt(0, 0x4C4F4720);
            dataBuffer.putInt(4, 0x00010000);
            dataBuffer.putLong(8, bt);
            dataBuffer.putLong(16, et);
            dataBuffer.putInt(24, s);
            dataBuffer.putInt(28, es);
            dataBuffer.putLong(32, l);
            Calendar calendar = new GregorianCalendar(TimeUtil.TIMEZONE_UTC);
            calendar.setTimeInMillis(bt);
            String key = String.format("/%04d/%02d/%02d/%02d/%02d/%02d/%03d/%d-%d-%d-%d-%d.log",
                                       calendar.get(Calendar.YEAR),
                                       calendar.get(Calendar.MONTH) + 1,
                                       calendar.get(Calendar.DAY_OF_MONTH),
                                       calendar.get(Calendar.HOUR),
                                       calendar.get(Calendar.MINUTE),
                                       calendar.get(Calendar.SECOND),
                                       calendar.get(Calendar.MILLISECOND),
                                       bt, et, s, es, l);
            storage.put(key, IOUtil.compress(dataArray, 0, s));
            strings.clear();
            beginTime.set(Long.MAX_VALUE);
            events.set(0);
            size.set(DO);
            offset.set(DO);
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