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

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class DefaultEventFilter implements Serializable, Binariable, EventFilter {
    private static final long serialVersionUID = 1;

    public final long begin;
    public final long end;
    public final Pattern logger;
    public final Pattern message;
    public final List<Tag> tags;
    public final Pattern dataType;
    public final int dataLength;

    public DefaultEventFilter(long begin,
                              long end,
                              Pattern logger,
                              Pattern message,
                              List<Tag> tags,
                              Pattern dataType,
                              int dataLength) {
        this.begin = begin;
        this.end = end;
        this.logger = logger;
        this.message = message;
        this.tags = tags;
        this.dataType = dataType;
        this.dataLength = dataLength;
    }

    protected boolean matchesTags(Event event) {
        if (tags == null) {
            return true;
        }
        for (Tag tag : tags) {
            String value = event.tags.get(tag.key);
            if (value != null) {
                if (tag.value.matcher(value).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getApplication() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getInstance() {
        return null;
    }

    @Override
    public String getLayer() {
        return null;
    }

    @Override
    public long getBeginTime() {
        return begin;
    }

    @Override
    public long getEndTime() {
        return end;
    }

    @Override
    public boolean matchEvent(Event event) {
        if ((event.time < begin) || (event.time >= end)) {
            return false;
        } else if ((logger != null) && (!logger.matcher(event.logger).matches())) {
            return false;
        } else if ((message != null) && (!message.matcher(event.message).matches())) {
            return false;
        } else if (!matchesTags(event)) {
            return false;
        } else if (event instanceof BinaryEvent) {
            BinaryEvent dataEvent = (BinaryEvent) event;
            if ((dataType != null) && (!dataType.matcher(dataEvent.binaryType).matches())) {
                return false;
            } else if ((dataLength >= 0) && (dataEvent.binaryData.length > dataLength)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public byte[] toBytes() {
        return CborUtil.toBytes(this);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public static DefaultEventFilter valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, DefaultEventFilter.class);
    }

    public static DefaultEventFilter valueOf(String string) {
        return JsonUtil.fromString(string, DefaultEventFilter.class);
    }

    public static class Tag implements Serializable {
        private static final long serialVersionUID = 1;

        public final String key;
        public final Pattern value;

        public Tag(String key, String value) {
            this(key, Pattern.compile(value));
        }

        public Tag(String key, Pattern value) {
            this.key = key;
            this.value = value;
        }
    }
}
