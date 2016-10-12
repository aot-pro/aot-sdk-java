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

package aot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class CborUtil {
    public static final String APPLICATION_CBOR = "application/cbor";

    private static final CBORFactory factory = new CBORFactory();
    private static final ObjectMapper mapper = new ObjectMapper(factory);

    private CborUtil() {
    }

    public static byte[] toBytes(Object value) {
        try {
            return mapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> type) {
        try {
            return mapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, int offset, int length, Class<T> type) {
        try {
            return mapper.readValue(bytes, offset, length, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBytes(OutputStream output, Object value) {
        try {
            mapper.writeValue(output, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readBytes(InputStream input, Class<T> type) {
        try {
            return mapper.readValue(input, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
