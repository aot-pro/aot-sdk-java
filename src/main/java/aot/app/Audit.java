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
import aot.util.ThreadLock;
import aot.util.ThreadUtil;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Audit {
    private static final Initializer initializer;
    private static final String application;
    private static final String version;
    private static final String instance;
    private static final Storage storage;
    private static final Storage configStorage;
    private static final boolean readConfigOnStart;
    private static final AtomicReference<Config> config;
    private static final Thread configThread;

    private Audit() {
    }

    static {
        Initializer init;
        try {
            Class clazz = Class.forName("aot.application.CustomInitializer");
            try {
                init = (Initializer) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            init = new Initializer();
        }
        initializer = init;
        application = initializer.getApplication();
        version = initializer.getVersion();
        instance = initializer.getInstance();
        storage = initializer.getStorage();
        configStorage = initializer.getConfigStorage();
        readConfigOnStart = initializer.readConfigOnStart();
        config = new AtomicReference<>(createConfig());
        configThread = new Thread("aot-config") {
            @Override
            public void run() {
                try (ThreadLock tl = new ThreadLock()) {
                    while (!ThreadUtil.isShutdown()) {
                        try {
                            Config c = initializer.readConfig(configStorage, application, version, instance);
                            if (c != null) {
                                config.set(c);
                            }
                        } catch (Exception e) {
                        }
                        try {
                            ThreadUtil.sleep(60000L);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        };
        configThread.setDaemon(false);
        configThread.start();
    }

    private static Config createConfig() {
        if (readConfigOnStart) {
            Config c = initializer.readConfig(configStorage, application, version, instance);
            if (c != null) {
                return c;
            }
        }
        Config c = initializer.getConfig();
        if (c != null) {
            return c;
        } else {
            throw new CreateConfigException("Config is not found");
        }
    }
}
