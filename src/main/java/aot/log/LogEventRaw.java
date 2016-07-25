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

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
class LogEventRaw implements Serializable, Binariable {
    private static final long serialVersionUID = 1;

    public final long time;
    public final int level;
    public final int logger;
    public final String message;
    public final int tags;

    @JsonCreator
    public LogEventRaw(@JsonProperty("time") long time,
                       @JsonProperty("level") int level,
                       @JsonProperty("logger") int logger,
                       @JsonProperty("message") String message,
                       @JsonProperty("tags") int tags) {
        this.time = time;
        this.level = level;
        this.logger = logger;
        this.message = message;
        this.tags = tags;
    }

    public LogEvent toEvent(LogStream stream) {
        return new LogEvent(time,
                            stream.getString(level),
                            stream.getString(logger),
                            message,
                            stream.getTags(tags));
    }

    @Override
    public byte[] toBytes() {
        return CborUtil.toBytes(this);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public static LogEventRaw valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, LogEventRaw.class);
    }

    public static LogEventRaw valueOf(String string) {
        return JsonUtil.fromString(string, LogEventRaw.class);
    }
}
