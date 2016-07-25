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

package aot.util.json;

import aot.util.binary.Binarilizer;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class JsonBinarilizer<T> extends Binarilizer<T> {
    private static final long serialVersionUID = 1;

    public JsonBinarilizer(Class<T> type) {
        super(type);
    }

    @Override
    public byte[] binarilize(T object) {
        return JsonUtil.toBytes(object);
    }

    @Override
    public T debinarilize(byte[] bytes) {
        return JsonUtil.fromBytes(bytes, type);
    }
}
