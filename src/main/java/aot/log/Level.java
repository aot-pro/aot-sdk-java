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

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
final class Level implements Runnable {
    private final String id;
    private final Buffer buffer1;
    private final Buffer buffer2;
    private final AtomicBoolean bufferFlag = new AtomicBoolean(true);
    private final ExecutorService executor;
    private final AtomicLong lost = new AtomicLong(0);

    Level(String id, int capacity, Storage storage, ExecutorService executor) {
        this.id = id;
        this.buffer1 = new Buffer(capacity, storage);
        this.buffer2 = new Buffer(capacity, storage);
        this.executor = executor;

        executor.execute(this);
    }

    public void log(String logger, String message, LinkedHashMap<String, String> tags) {
        if (bufferFlag.get()) {
            if (!buffer1.log(logger, message, tags)) {
                if (!buffer2.log(logger, message, tags)) {
                    lost.incrementAndGet();
                }
            }
        } else {
            if (!buffer2.log(logger, message, tags)) {
                if (!buffer1.log(logger, message, tags)) {
                    lost.incrementAndGet();
                }
            }
        }
    }

    private void upload(Buffer buffer, boolean flag) {
        long begin = System.currentTimeMillis();
        buffer.upload(lost);
        long span = System.currentTimeMillis() - begin;
        if (span < 60000) {
            synchronized (buffer) {
                try {
                    buffer.wait(60000 - span);
                } catch (InterruptedException e) {
                }
            }
        }
        bufferFlag.set(flag);
    }

    @Override
    public void run() {
        while (!executor.isShutdown()) {
            try {
                upload(buffer2, false);
                upload(buffer1, true);
            } catch (Exception e) {
            }
        }
    }
}
