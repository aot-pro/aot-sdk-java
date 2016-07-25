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

import java.util.Map;
import java.util.Set;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class LogStartEvent extends LogEvent {
    private static final long serialVersionUID = 1;

    public final String startId;
    public final String application;
    public final String version;
    public final Map<String, String> systemProperties;
    public final Map<String, String> environmentProperties;
    public final String securityManager;
    public final Set<String> transformers;
    public final Set<String> protocolHandlers;

    @JsonCreator
    public LogStartEvent(@JsonProperty("time") long time,
                         @JsonProperty("level") String level,
                         @JsonProperty("logger") String logger,
                         @JsonProperty("message") String message,
                         @JsonProperty("tags") Map<String, String> tags,
                         @JsonProperty("startId") String startId,
                         @JsonProperty("application") String application,
                         @JsonProperty("version") String version,
                         @JsonProperty("systemProperties") Map<String, String> systemProperties,
                         @JsonProperty("environmentProperties") Map<String, String> environmentProperties,
                         @JsonProperty("securityManager") String securityManager,
                         @JsonProperty("transformers") Set<String> transformers,
                         @JsonProperty("protocolHandlers") Set<String> protocolHandlers) {
        super(time, level, logger, message, tags);

        this.startId = startId;
        this.application = application;
        this.version = version;
        this.systemProperties = systemProperties;
        this.environmentProperties = environmentProperties;
        this.securityManager = securityManager;
        this.transformers = transformers;
        this.protocolHandlers = protocolHandlers;
    }

    public static LogStartEvent valueOf(byte[] bytes) {
        return CborUtil.fromBytes(bytes, LogStartEvent.class);
    }

    public static LogStartEvent valueOf(String string) {
        return JsonUtil.fromString(string, LogStartEvent.class);
    }
}
