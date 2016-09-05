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

import aot.storage.Storage;
import aot.util.net.NetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Initializer {
    public Initializer() {
    }

    public Map<String, String> getAttributes() {
        try {
            LinkedHashMap<String, String> attributes = new LinkedHashMap<>(256);
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                try (InputStream in = resources.nextElement().openStream()) {
                    Manifest manifest = new Manifest(in);
                    for (Map.Entry<Object, Object> attr : manifest.getMainAttributes().entrySet()) {
                        Object key = attr.getKey();
                        Object value = attr.getValue();
                        if (key instanceof Attributes.Name && value instanceof String) {
                            attributes.put(key.toString(), value.toString());
                        }
                    }
                }
            }
            return attributes;
        } catch (IOException e) {
            throw new InitializerException(e);
        }
    }

    public Config getBaseConfig() {
        return null;
    }

    public Storage getConfigLocalStorage() {
        return null;
    }

    public Storage getConfigRemoteStorage() {
        return null;
    }

    public Storage getDataLocalStorage() {
        return null;
    }

    public Storage getDataRemoteStorage() {
        return null;
    }

    public String getInstance() {
        return NetUtil.getLocalMac();
    }
}
