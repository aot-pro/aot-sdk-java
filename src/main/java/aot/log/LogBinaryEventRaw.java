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

package aot.log;

import aot.util.cbor.CborUtil;
import aot.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
class LogBinaryEventRaw extends LogEventRaw {
    private static final long serialVersionUID = 1;

    public final int binaryType;
    public final byte[] binaryData;

    @JsonCreator
    public LogBinaryEventRaw(@JsonProperty("time") long time,
                             @JsonProperty("level") int level,
                             @JsonProperty("logger") int logger,
                             @JsonProperty("message") String message,
                             @JsonProperty("tags") int tags,
                             @JsonProperty("binaryType") int binaryType,
                             @JsonProperty("binaryData") byte[] binaryData) {
        super(time, level, logger, message, tags);

        this.binaryType = binaryType;
        this.binaryData = binaryData;
    }

    @Override
    public LogBinaryEvent toEvent(LogStream stream) {
        return new LogBinaryEvent(time,
                                  stream.getString(level),
                                  stream.getString(logger),
                                  message,
                                  stream.getTags(tags),
                                  stream.getString(binaryType),
                                  binaryData);
    }

    public static LogBinaryEventRaw valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, LogBinaryEventRaw.class);
    }

    public static LogBinaryEventRaw valueOf(String string) {
        return JsonUtil.fromString(string, LogBinaryEventRaw.class);
    }
}
