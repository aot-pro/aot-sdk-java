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
import java.util.Set;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class StartEvent extends Event {
    protected final String application;
    protected final String version;
    protected final String instance;
    protected final String id;
    protected final Map<String, String> manifestAttributes;
    protected final Map<String, String> systemProperties;
    protected final Map<String, String> environmentProperties;
    protected final String securityManager;

    protected StartEvent(LogFile file, long time, String logger, String message, Map<String, String> tags, String application, String version, String instance, String id, Map<String, String> manifestAttributes, Map<String, String> systemProperties, Map<String, String> environmentProperties, String securityManager) {
        super(file, time, logger, message, tags);

        this.application = application;
        this.version = version;
        this.instance = instance;
        this.id = id;
        this.manifestAttributes = manifestAttributes;
        this.systemProperties = systemProperties;
        this.environmentProperties = environmentProperties;
        this.securityManager = securityManager;
    }

    public String getApplication() {
        return application;
    }

    public String getVersion() {
        return version;
    }

    public String getInstance() {
        return instance;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getManifestAttributes() {
        return manifestAttributes;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public Map<String, String> getEnvironmentProperties() {
        return environmentProperties;
    }

    public String getSecurityManager() {
        return securityManager;
    }
}
