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
import aot.util.io.IOUtil;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
final class Buffer {
    private final AtomicBoolean ready = new AtomicBoolean(true);
    private final AtomicInteger thread = new AtomicInteger(0);
    private final AtomicInteger offset = new AtomicInteger(24);
    private final AtomicInteger size = new AtomicInteger(0);
    private final int capacity;
    private final ByteBuffer data;
    private final Storage storage;
    private long begin;

    Buffer(int capacity, Storage storage) {
        this.capacity = capacity;
        this.data = ByteBuffer.allocate(capacity);
        this.storage = storage;

        data.putInt(0, 1);

        this.begin = System.currentTimeMillis();
    }

    public boolean log(String logger, String message, LinkedHashMap<String, String> tags) {
        if (ready.get()) {
            thread.incrementAndGet();
            try {
                if (ready.get()) {
                    String tn = Thread.currentThread().getName();
                    int sz = getEventSize(logger, tn, message, tags);
                    if (offset.get() + sz <= capacity) {
                        int off = offset.getAndAdd(sz);
                        if (off + sz <= capacity) {
                            long time = System.currentTimeMillis();
                            size.addAndGet(sz);
                            int delta = putEvent(data, off, time, sz, logger, tn, message, tags) - off;
                            if (delta != sz) {
                                throw new Error(String.format("Log event size %s is not equal offset delta %s", sz, delta));
                            }
                            return true;
                        } else {
                            if (ready.get()) {
                                ready.set(false);
                                synchronized (this) {
                                    this.notifyAll();
                                }
                            }
                        }
                    } else {
                        if (ready.get()) {
                            ready.set(false);
                            synchronized (this) {
                                this.notifyAll();
                            }
                        }
                    }
                }
            } finally {
                thread.decrementAndGet();
            }
        }
        return false;
    }

    public void upload(AtomicLong lost) {
        if (size.get() > 0) {
            ready.set(false);
            try {
                while (thread.get() > 0) {
                    Util.sleep(300);
                }
                int sz = size.get();
                data.putInt(4, sz);
                data.putLong(8, begin);
                data.putLong(16, lost.getAndSet(0));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(begin);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DATE);
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);
                int millisecond = cal.get(Calendar.MILLISECOND);
                String key = String.format("%d/%d/%d/%d/%d/%d-%d-%d_%d:%d:%d.%d",
                                           year, month, day, hour, minute,
                                           year, month, day, hour, minute, second, millisecond);
                storage.put(key, IOUtil.compress(data.array(), 0, 24 + sz));
                size.set(0);
                offset.set(24);
                begin = System.currentTimeMillis();
            } finally {
                ready.set(true);
            }
        }
    }

    private static int getEventSize(String logger, String thread, String message, LinkedHashMap<String, String> tags) {
        int size = 20 + logger.length() + thread.length() + message.length();
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            size += 2 + tag.getKey().length() + tag.getValue().length();
        }
        return size;
    }

    private static int putString(ByteBuffer data, int offset, String value) {
        short l = (short) value.length();
        data.putShort(offset, l);
        offset += 2;
        for (int i = 0, ci = l; i < ci; ++i) {
            data.putChar(offset, value.charAt(i));
            offset += 2;
        }
        return offset;
    }

    private static int putEvent(ByteBuffer data, int offset, long time, int size, String logger, String thread, String message, LinkedHashMap<String, String> tags) {
        data.putLong(offset, time);
        offset += 8;
        data.putInt(offset, size);
        offset += 4;
        offset = putString(data, offset, logger);
        offset = putString(data, offset, thread);
        offset = putString(data, offset, message);
        data.putShort(offset, (short) tags.size());
        offset += 2;
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            offset = putString(data, offset, tag.getKey());
            offset = putString(data, offset, tag.getValue());
        }
        return offset;
    }
}
