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

package aot.util.ref;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class CacheReference<T> implements Serializable {
    private static final long serialVersionUID = 1;

    protected final T referent;
    protected final long span;
    protected final AtomicLong access;

    public CacheReference(T referent, long span) {
        this.referent = referent;
        this.span = span;
        this.access = new AtomicLong(System.currentTimeMillis());
    }

    public T getReferent() {
        return referent;
    }

    public long getSpan() {
        return span;
    }

    public long getAccess() {
        return access.get();
    }

    public T get() {
        access.set(System.currentTimeMillis());
        return referent;
    }
}
