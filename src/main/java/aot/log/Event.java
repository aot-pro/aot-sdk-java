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

import aot.util.cbor.CborUtil;
import aot.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Event implements Serializable {
    private static final long serialVersionUID = 1;

    public final long time;
    public final Map<String, String> tags;
    public final String message;

    public Event(Map<String, String> tags, String message) {
        this.time = System.currentTimeMillis();
        this.tags = tags;
        this.message = message;
    }

    @JsonCreator
    public Event(@JsonProperty("time") long time,
                 @JsonProperty("tags") Map<String, String> tags,
                 @JsonProperty("message") String message) {
        this.time = time;
        this.tags = tags;
        this.message = message;
    }

    public byte[] toBytes() {
        return CborUtil.toBytes(this);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public static Event valueOf(byte[] data) {
        return CborUtil.fromBytes(data, Event.class);
    }

    public static Event valueOf(String content) {
        return JsonUtil.fromString(content, Event.class);
    }
}
