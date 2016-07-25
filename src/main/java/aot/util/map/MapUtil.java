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

package aot.util.map;

import java.util.concurrent.ConcurrentMap;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class MapUtil {
    private MapUtil() {
    }

    public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
        V v = map.putIfAbsent(key, value);
        if (v != null) {
            return v;
        } else {
            return value;
        }
    }
}