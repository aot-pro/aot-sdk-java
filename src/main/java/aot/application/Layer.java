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

import aot.log.Buffer;
import aot.log.BufferException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
final class Layer {
    private final String id;
    private final Buffer buffer1;
    private final Buffer buffer2;
    private final AtomicBoolean bufferFlag = new AtomicBoolean(true);
    private final AtomicLong lost = new AtomicLong(0L);

    public Layer(String id, int capacity) {
        this.id = id;
        this.buffer1 = new Buffer(capacity);
        this.buffer2 = new Buffer(capacity);
    }

    public void log(String logger, short shift, long tagsRevision, Map<String, String> tags, String message) {
        if (bufferFlag.get()) {
            try {
                buffer1.log(logger, shift, tagsRevision, tags, message);
            } catch (BufferException e1) {
                try {
                    buffer2.log(logger, shift, tagsRevision, tags, message);
                    bufferFlag.set(false);
                } catch (BufferException e2) {
                    lost.incrementAndGet();
                }
            }
        } else {
            try {
                buffer2.log(logger, shift, tagsRevision, tags, message);
            } catch (BufferException e2) {
                try {
                    buffer1.log(logger, shift, tagsRevision, tags, message);
                    bufferFlag.set(true);
                } catch (BufferException e1) {
                    lost.incrementAndGet();
                }
            }
        }
    }
}
