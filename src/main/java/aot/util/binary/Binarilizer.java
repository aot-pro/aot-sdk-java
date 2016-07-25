/*
 * Copyright (C) 2014 Dmitry Kotlyarov.
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

package aot.util.binary;

import java.io.Serializable;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public abstract class Binarilizer<T> implements Serializable {
    private static final long serialVersionUID = 1;

    protected final Class<T> type;

    protected Binarilizer(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public abstract byte[] binarilize(T object);
    public abstract T debinarilize(byte[] bytes);
}
