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

import aot.util.Binariable;
import aot.util.CborUtil;
import aot.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Event implements Serializable, Binariable {
    private static final long serialVersionUID = 1;

    public final long time;
    public final int logger;
    public final String message;
    public final int tags;

    @JsonCreator
    public Event(@JsonProperty("time") long time,
                 @JsonProperty("logger") int logger,
                 @JsonProperty("message") String message,
                 @JsonProperty("tags") int tags) {
        this.time = time;
        this.logger = logger;
        this.message = message;
        this.tags = tags;
    }

    public aot.view.Event toEvent(EventStream stream) {
        return null;/*new Event(null,
                         time,
                         stream.getString(logger),
                         message,
                         stream.getTags(tags));*/
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
