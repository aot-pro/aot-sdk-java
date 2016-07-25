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

package aot.util.resource;

import aot.util.NotFoundException;
import aot.util.Util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class ResourceUtil {
    private ResourceUtil() {
    }

    public static byte[] findAsBytes(Class clazz, String name) {
        InputStream input = clazz.getResourceAsStream(name);
        if (input != null) {
            try (InputStream input1 = input) {
                byte[] data = new byte[input1.available()];
                int read = input1.read(data);
                if (read == data.length) {
                    return data;
                } else {
                    throw new RuntimeException(String.format("Read %d bytes from resource '%s:%s' of size %d", read, clazz.getName(), name, data.length));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    public static byte[] getAsBytes(Class clazz, String name) {
        byte[] b = findAsBytes(clazz, name);
        if (b != null) {
            return b;
        } else {
            throw new NotFoundException(String.format("Resource '%s:%s' is not found", clazz.getName(), name));
        }
    }

    public static String findAsString(Class clazz, String name) {
        byte[] b = findAsBytes(clazz, name);
        if (b != null) {
            return new String(b, Util.CHARSET);
        } else {
            return null;
        }
    }

    public static String getAsString(Class clazz, String name) {
        String s = findAsString(clazz, name);
        if (s != null) {
            return s;
        } else {
            throw new NotFoundException(String.format("Resource '%s:%s' is not found", clazz.getName(), name));
        }
    }
}
