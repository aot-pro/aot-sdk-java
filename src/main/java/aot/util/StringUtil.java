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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class StringUtil {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private StringUtil() {
    }

    public static byte[] toBytes(String value) {
        int offset = 0;
        int length = value.length();
        ByteBuffer buffer = ByteBuffer.allocate(length * 2);
        for (int i = 0; i < length; ++i) {
            buffer.putChar(offset, value.charAt(i));
            offset += 2;
        }
        return buffer.array();
    }

    public static String fromBytes(byte[] data) {
        int offset = 0;
        int length = data.length / 2;
        char[] value = new char[length];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        for (int i = 0; i < length; ++i) {
            value[i] = buffer.getChar(offset);
            offset += 2;
        }
        return new String(value);
    }

    public static String cut(String value, int length) {
        if (value != null) {
            if (value.length() <= length) {
                return value;
            } else {
                return value.substring(0, length);
            }
        } else {
            return null;
        }
    }

    public static String getCommonPrefix(String value1, String value2) {
        int minLength = Math.min(value1.length(), value2.length());
        for (int i = 0; i < minLength; ++i) {
            if (value1.charAt(i) != value2.charAt(i)) {
                return value1.substring(0, i);
            }
        }
        return value1.substring(0, minLength);
    }
}
