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

package aot.util.io;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class IOUtil {
    private IOUtil() {
    }

    public static Properties loadProperties(Class clazz, String name) {
//        log.info("Load properties '{}/{}'", clazz.getPackage().getName(), name);
        try (InputStream input = clazz.getResourceAsStream(name)) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logProperties(String name, Properties properties) {
//        log.info("Log properties '{}'", name);
        for (Map.Entry<Object, Object> prop : properties.entrySet()) {
//            log.info("{}: '{}'", prop.getKey(), prop.getValue());
        }
    }

    public static byte[] serialize(Object object) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream(4096)) {
            try (ObjectOutputStream output = new ObjectOutputStream(buffer)) {
                output.writeObject(object);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(byte[] data) {
        try (ByteArrayInputStream buffer = new ByteArrayInputStream(data)) {
            try (ObjectInputStream input = new ObjectInputStream(buffer)) {
                return input.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String serialize(ObjectMapper mapper, Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(ObjectMapper mapper, String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] compress(byte[] data, int offset, int length) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream(data.length)) {
            try (GZIPOutputStream output = new GZIPOutputStream(buffer)) {
                output.write(data, offset, length);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decompress(byte[] data, int offset, int length) {
        try (ByteArrayInputStream buffer = new ByteArrayInputStream(data)) {
            try (GZIPInputStream input = new GZIPInputStream(buffer)) {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
