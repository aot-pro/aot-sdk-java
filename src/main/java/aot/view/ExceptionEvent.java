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

package aot.view;

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
    protected final String exceptionType;
    protected final String exceptionMessage;
    protected final String exceptionStackTrace;

    protected ExceptionEvent(LogFile file, long time, String logger, String message, Map<String, String> tags, String binaryType, byte[] binaryData, String exceptionType, String exceptionMessage, String exceptionStackTrace) {
        super(file, time, logger, message, tags, binaryType, binaryData);

        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStackTrace = exceptionStackTrace;
    }
}
