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

package aot.log;

import aot.util.binary.Binariable;
import aot.util.cbor.CborUtil;
import aot.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class LogConfig implements Serializable, Binariable {
    private static final long serialVersionUID = 1;

    public final Map<String, Level> levels;

    @JsonCreator
    public LogConfig(@JsonProperty("levels") Map<String, Level> levels) {
        this.levels = Collections.unmodifiableMap(levels);
    }

    @Override
    public byte[] toBytes() {
        return CborUtil.toBytes(this);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public static LogConfig valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, LogConfig.class);
    }

    public static LogConfig valueOf(String string) {
        return JsonUtil.fromString(string, LogConfig.class);
    }

    public static class Level implements Serializable {
        private static final long serialVersionUID = 1;
    }
}
