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

package aot.application;

import aot.util.CborUtil;
import aot.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class BinaryEvent extends Event {
    private static final long serialVersionUID = 1;

    public final int binaryType;
    public final byte[] binaryData;

    @JsonCreator
    public BinaryEvent(@JsonProperty("time") long time,
                       @JsonProperty("logger") int logger,
                       @JsonProperty("message") String message,
                       @JsonProperty("tags") int tags,
                       @JsonProperty("binaryType") int binaryType,
                       @JsonProperty("binaryData") byte[] binaryData) {
        super(time, logger, message, tags);

        this.binaryType = binaryType;
        this.binaryData = binaryData;
    }

    @Override
    public aot.view.BinaryEvent toEvent(EventStream stream) {
        return null;/*new BinaryEvent(time,
                                  stream.getString(level),
                                  stream.getString(logger),
                                  message,
                                  stream.getTags(tags),
                                  stream.getString(binaryType),
                                  binaryData);*/
    }

    public static BinaryEvent valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, BinaryEvent.class);
    }

    public static BinaryEvent valueOf(String string) {
        return JsonUtil.fromString(string, BinaryEvent.class);
    }
}
