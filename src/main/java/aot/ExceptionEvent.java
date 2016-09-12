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

package aot;

import aot.util.cbor.CborUtil;
import aot.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class ExceptionEvent extends BinaryEvent {
    private static final long serialVersionUID = 1;

    public final String exceptionType;
    public final String exceptionMessage;
    public final String exceptionStackTrace;

    @JsonCreator
    public ExceptionEvent(@JsonProperty("time") long time,
                          @JsonProperty("level") String level,
                          @JsonProperty("logger") String logger,
                          @JsonProperty("message") String message,
                          @JsonProperty("tags") Map<String, String> tags,
                          @JsonProperty("binaryType") String binaryType,
                          @JsonProperty("binaryData") byte[] binaryData,
                          @JsonProperty("exceptionType") String exceptionType,
                          @JsonProperty("exceptionMessage") String exceptionMessage,
                          @JsonProperty("exceptionStackTrace") String exceptionStackTrace) {
        super(time, level, logger, message, tags, binaryType, binaryData);

        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public static ExceptionEvent valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, ExceptionEvent.class);
    }

    public static ExceptionEvent valueOf(String string) {
        return JsonUtil.fromString(string, ExceptionEvent.class);
    }
}