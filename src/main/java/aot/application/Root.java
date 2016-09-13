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

import aot.storage.Storage;
import aot.util.NotFoundException;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Root {
    private static final Config baseConfig;
    private static final Storage configLocalStorage;
    private static final Storage configRemoteStorage;
    private static final Storage dataLocalStorage;
    private static final Storage dataRemoteStorage;
    private static final String application;
    private static final String version;
    private static final String instance;
    private static final AtomicReference<Config> config;

    private Root() {
    }

    static {
        Initializer initializer;
        try {
            Class clazz = Class.forName("aot.CustomInitializer");
            try {
                initializer = (Initializer) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            initializer = new Initializer();
        }
        baseConfig = initializer.getBaseConfig();
        configLocalStorage = initializer.getConfigLocalStorage();
        configRemoteStorage = initializer.getConfigRemoteStorage();
        dataLocalStorage = initializer.getDataLocalStorage();
        dataRemoteStorage = initializer.getDataRemoteStorage();
        application = initializer.getApplication();
        version = initializer.getVersion();
        instance = initializer.getInstance();
        config = new AtomicReference<>(null);
        if (configLocalStorage == null) {
            try {
                config.set(configRemoteStorage.getJson(String.format("%s/%s/%s/config.json", application, version, instance), Config.class));
            } catch (NotFoundException e1) {
                try {
                    config.set(configRemoteStorage.getJson(String.format("%s/%s/config.json", application, version), Config.class));
                } catch (NotFoundException e2) {
                    try {
                        config.set(configRemoteStorage.getJson(String.format("%s/config.json", application), Config.class));
                    } catch (NotFoundException e3) {
                        try {
                            config.set(configRemoteStorage.getJson("config.json", Config.class));
                        } catch (NotFoundException e4) {
                            config.set(baseConfig);
                        }
                    }
                }
            }
        } else {
            try {
                config.set(configLocalStorage.getJson(String.format("%s/%s/%s/config.json", application, version, instance), Config.class));
            } catch (NotFoundException e1) {
                try {
                    config.set(configLocalStorage.getJson(String.format("%s/%s/config.json", application, version), Config.class));
                } catch (NotFoundException e2) {
                    try {
                        config.set(configLocalStorage.getJson(String.format("%s/config.json", application), Config.class));
                    } catch (NotFoundException e3) {
                        try {
                            config.set(configLocalStorage.getJson("config.json", Config.class));
                        } catch (NotFoundException e4) {
                            config.set(baseConfig);
                        }
                    }
                }
            }
        }
        Thread t = new Thread("aot-root-config") {
            @Override
            public void run() {
                while (true) {
                    if (configLocalStorage == null) {
                        try {
                            config.set(configRemoteStorage.getJson(String.format("%s/%s/%s/config.json", application, version, instance), Config.class));
                        } catch (NotFoundException e1) {
                            try {
                                config.set(configRemoteStorage.getJson(String.format("%s/%s/config.json", application, version), Config.class));
                            } catch (NotFoundException e2) {
                                try {
                                    config.set(configRemoteStorage.getJson(String.format("%s/config.json", application), Config.class));
                                } catch (NotFoundException e3) {
                                    try {
                                        config.set(configRemoteStorage.getJson("config.json", Config.class));
                                    } catch (NotFoundException e4) {
                                        config.set(baseConfig);
                                    }
                                }
                            }
                        }
                    } else {
                        try {
                            config.set(configLocalStorage.getJson(String.format("%s/%s/%s/config.json", application, version, instance), Config.class));
                        } catch (NotFoundException e1) {
                            try {
                                config.set(configLocalStorage.getJson(String.format("%s/%s/config.json", application, version), Config.class));
                            } catch (NotFoundException e2) {
                                try {
                                    config.set(configLocalStorage.getJson(String.format("%s/config.json", application), Config.class));
                                } catch (NotFoundException e3) {
                                    try {
                                        config.set(configLocalStorage.getJson("config.json", Config.class));
                                    } catch (NotFoundException e4) {
                                        config.set(baseConfig);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
        t.start();
    }

    public static Config getBaseConfig() {
        return baseConfig;
    }

    public static Storage getConfigLocalStorage() {
        return configLocalStorage;
    }

    public static Storage getConfigRemoteStorage() {
        return configRemoteStorage;
    }

    public static Storage getDataLocalStorage() {
        return dataLocalStorage;
    }

    public static Storage getDataRemoteStorage() {
        return dataRemoteStorage;
    }

    public static String getApplication() {
        return application;
    }

    public static String getVersion() {
        return version;
    }

    public static String getInstance() {
        return instance;
    }

    public static Config getConfig() {
        return config.get();
    }
}
