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

package aot.app;

import aot.util.Binariable;
import aot.util.CborUtil;
import aot.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Config implements Serializable, Binariable {
    private static final long serialVersionUID = 1;

    public final Log log;
    public final Stat stat;

    @JsonCreator
    public Config(@JsonProperty("log") Log log,
                  @JsonProperty("stat") Stat stat) {
        this.log = log;
        this.stat = stat;
    }

    @Override
    public byte[] toBytes() {
        return CborUtil.toBytes(this);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }

    public static Config valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, Config.class);
    }

    public static Config valueOf(String string) {
        return JsonUtil.fromString(string, Config.class);
    }

    public static class Log implements Serializable {
        private static final long serialVersionUID = 1;

        public final List<Layer> layers;

        public Log(@JsonProperty(value="layers", required=false) List<Layer> layers) {
            this.layers = Collections.unmodifiableList(layers);
        }

        public static class Layer implements Serializable {
            private static final long serialVersionUID = 1;

            public final String id;
            public final boolean enabled;
            public final int span;
            public final int capacity;

            public Layer(@JsonProperty(value="id", required=true) String id,
                         @JsonProperty(value="enabled", required=true) boolean enabled,
                         @JsonProperty(value="span", required=false, defaultValue="1") int span,
                         @JsonProperty(value="capacity", required=false, defaultValue="1024") int capacity) {
                this.id = id;
                this.enabled = enabled;
                this.span = span;
                this.capacity = capacity;
            }
        }
    }

    public static class Stat implements Serializable {
        private static final long serialVersionUID = 1;
    }
}
