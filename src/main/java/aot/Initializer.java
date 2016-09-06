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
import aot.util.manifest.ManifestUtil;
import aot.util.net.NetUtil;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Initializer {
    public Initializer() {
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

    public String getApplication() {
        return ManifestUtil.getAttribute("Application");
    }

    public String getVersion() {
        return ManifestUtil.getAttribute("Version");
    }

    public String getInstance() {
        return NetUtil.getLocalMac();
    }
}
