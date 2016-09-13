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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
final class Layer {
    private final String id;
    private final EventBuffer buffer1;
    private final EventBuffer buffer2;
    private final AtomicBoolean bufferFlag = new AtomicBoolean(true);
    private final AtomicLong lost = new AtomicLong(0);

    public Layer(String id, int size) {
        this.id = id;
        this.buffer1 = new EventBuffer(size, null);
        this.buffer2 = new EventBuffer(size, null);
    }
}
