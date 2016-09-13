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

import aot.util.binary.Binariable;
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
public class Event implements Serializable, Binariable, Comparable<Event> {
    private static final long serialVersionUID = 1;

    public final long time;
    public final String level;
    public final String logger;
    public final String message;
    public final Map<String, String> tags;

    @JsonCreator
    public Event(@JsonProperty("time") long time,
                 @JsonProperty("level") String level,
                 @JsonProperty("logger") String logger,
                 @JsonProperty("message") String message,
                 @JsonProperty("tags") Map<String, String> tags) {
        this.time = time;
        this.level = level;
        this.logger = logger;
        this.message = message;
        this.tags = tags;
    }

    @Override
    public int compareTo(Event event) {
        if (time < event.time) {
            return -1;
        } else if (time > event.time) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public byte[] toBytes() {
        return CborUtil.toBytes(this);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public static Event valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, Event.class);
    }

    public static Event valueOf(String string) {
        return JsonUtil.fromString(string, Event.class);
    }
}
