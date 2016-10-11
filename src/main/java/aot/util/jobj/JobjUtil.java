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

package aot.util.jobj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class JobjUtil {
    public static final String APPLICATION_JOBJ = "application/jobj";

    private JobjUtil() {
    }

    public static byte[] toBytes(Object value) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream(4096);
            try (ByteArrayOutputStream buf = b) {
                try (ObjectOutputStream out = new ObjectOutputStream(buf)) {
                    out.writeObject(value);
                }
            }
            return b.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> type) {
        return fromBytes(bytes, 0, bytes.length, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromBytes(byte[] bytes, int offset, int length, Class<T> type) {
        try {
            try (ByteArrayInputStream buf = new ByteArrayInputStream(bytes, offset, length)) {
                try (ObjectInputStream in = new ObjectInputStream(buf)) {
                    return (T) in.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBytes(OutputStream output, Object value) {
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(output)) {
                out.writeObject(value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T readBytes(InputStream input, Class<T> type) {
        try {
            try (ObjectInputStream in = new ObjectInputStream(input)) {
                return (T) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
