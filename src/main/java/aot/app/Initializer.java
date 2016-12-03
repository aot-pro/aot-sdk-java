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

import aot.storage.Storage;
import aot.util.ManifestUtil;
import aot.util.NetUtil;
import aot.util.PropertyUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Initializer {
    public Initializer() {
    }

    public String getApplication() {
        return ManifestUtil.getAttribute("Application");
    }

    public String getVersion() {
        return ManifestUtil.getAttribute("Version");
    }

    public String getInstance() {
        return NetUtil.getLocalMac();
    }

    public Storage getStorage() {
        String url = String.format("file://%s/.aot", System.getProperty("java.io.tmpdir"));
        String p = System.getProperty("user.home");
        if (p != null) {
            url = String.format("file://%s/.aot", p);
        }
        p = System.getProperty("catalina.base");
        if (p != null) {
            url = String.format("file://%s/logs/.aot", p);
        }
        url = PropertyUtil.getProperty("aot.storage", "AOT_STORAGE", url);
        return Storage.createStorage(url);
    }

    public Storage getConfigStorage() {
        String url = String.format("file://%s/.aot", System.getProperty("java.io.tmpdir"));
        String p = System.getProperty("user.home");
        if (p != null) {
            url = String.format("file://%s/.aot", p);
        }
        p = System.getProperty("catalina.base");
        if (p != null) {
            url = String.format("file://%s/logs/.aot", p);
        }
        url = PropertyUtil.getProperty("aot.storage", "AOT_STORAGE", url);
        url = PropertyUtil.getProperty("aot.config", "AOT_CONFIG", url);
        return Storage.createStorage(url);
    }

    public boolean readConfigOnStart() {
        return true;
    }

    public Config getConfig() {
        return new Config(new Config.Log(new LinkedHashMap<String, Config.Log.Layer>()), new Config.Stat());
    }

    public Config readConfig(Storage storage, String application, String version, String instance) {
        try {
            return storage.getJson(String.format("%s/%s/%s/config.json", application, version, instance), Config.class);
        } catch (Exception e1) {
            try {
                return storage.getJson(String.format("%s/%s/config.json", application, version), Config.class);
            } catch (Exception e2) {
                try {
                    return storage.getJson(String.format("%s/config.json", application), Config.class);
                } catch (Exception e3) {
                    try {
                        return storage.getJson("config.json", Config.class);
                    } catch (Exception e4) {
                        return null;
                    }
                }
            }
        }
    }
}
