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

import aot.util.ThreadLock;
import aot.util.ThreadUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class Log {
    private static final AtomicReference<Config> config = new AtomicReference<>(null);
    private static final AtomicReference<LinkedHashMap<String, Layer>> layers = new AtomicReference<>(null);
    private static final ThreadLocal<ThreadInfo> threadInfo = new ThreadLocal<ThreadInfo>() {
        @Override
        protected ThreadInfo initialValue() {
            return new ThreadInfo();
        }
    };
    private static final Thread thread;

    private Log() {
    }

    static {
        thread = new Thread("aot-log") {
            @Override
            public void run() {
                try (ThreadLock tl = new ThreadLock()) {
                    while (!ThreadUtil.isShutdown()) {
                        try {
                            Config config = Log.config.get();
                            Config newConfig = Audit.getConfig();
                            if (newConfig != config) {
                                LinkedList<Layer> oldLayers = new LinkedList<>();
                                LinkedHashMap<String, Layer> layers = Log.layers.get();
                                LinkedHashMap<String, Layer> newLayers = new LinkedHashMap<>(layers);
                                for (Map.Entry<String, Config.Log.Layer> newConfigLayerEntry : newConfig.log.layers.entrySet()) {
                                    String newConfigLayerId = newConfigLayerEntry.getKey();
                                    Config.Log.Layer newConfigLayer = newConfigLayerEntry.getValue();
                                    if (newConfigLayer.enabled) {
                                        Layer layer = newLayers.get(newConfigLayerId);
                                        if (layer == null) {
                                            newLayers.put(newConfigLayerId, new Layer(newConfigLayerId, newConfigLayer));
                                        } else {
                                            if (!layer.compareConfig(newConfigLayer)) {
                                                oldLayers.add(layer);
                                                newLayers.put(newConfigLayerId, new Layer(newConfigLayerId, newConfigLayer));
                                            }
                                        }
                                    }
                                }
                                for (Layer layer : newLayers.values().toArray(new Layer[newLayers.size()])) {
                                    String layerId = layer.getId();
                                    Config.Log.Layer newConfigLayer = newConfig.log.layers.get(layerId);
                                    if (newConfigLayer == null) {
                                        oldLayers.add(layer);
                                        newLayers.remove(layerId);
                                    }
                                }
                                Log.config.set(newConfig);
                                Log.layers.set(newLayers);
                                for (Layer oldLayer : oldLayers) {
                                    oldLayer.upload(Audit.getStorage(), true);
                                }
                            }
                            for (Layer layer : Log.layers.get().values()) {
                                layer.upload(Audit.getStorage(), false);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        };
        thread.setDaemon(false);
        thread.start();
    }

    public static void log(String layer, String logger, String message) {
        Layer l = Log.layers.get().get(layer);
        if (l != null) {
            ThreadInfo ti = Log.threadInfo.get();
            l.log(logger, ti.shift, ti.tagsRevision, ti.tags, message);
        }
    }

    public static boolean addTag(String key, String value) {
        ThreadInfo ti = Log.threadInfo.get();
        if (!ti.tags.containsKey(key)) {
            ti.tags.put(key, value);
            if (ti.tagsRevision < Long.MAX_VALUE) {
                ti.tagsRevision++;
            } else {
                ti.tagsRevision = 0L;
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeTag(String key) {
        ThreadInfo ti = Log.threadInfo.get();
        if (ti.tags.remove(key) != null) {
            if (ti.tagsRevision < Long.MAX_VALUE) {
                ti.tagsRevision++;
            } else {
                ti.tagsRevision = 0L;
            }
            return true;
        } else {
            return false;
        }
    }

    private static final class ThreadInfo {
        public short shift = 0;
        public long tagsRevision = 0L;
        public final LinkedHashMap<String, String> tags = new LinkedHashMap<>();
    }
}
