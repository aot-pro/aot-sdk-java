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

package aot.storage;

import aot.util.map.MapUtil;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public abstract class Storage {
    private static final ConcurrentHashMap<String, Holder> storages = new ConcurrentHashMap<>(4096);

    protected final String bucket;
    protected final String prefix;

    protected Storage(String bucket, String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }

    public String getBucket() {
        return bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public abstract Storage substorage(String prefix);

    public abstract Iterable<String> find(String prefix);
    public abstract Iterable<String> find(String prefix, String filter);
    public abstract Iterable<String> find(String prefix, Pattern filter);

    public abstract byte[] get(String key);
    public abstract void put(String key, byte[] data);
    public abstract void delete(String key);

    public abstract String publish(String key);
    public abstract void hide(String key);
    public abstract String url(String key);

    public static Storage createStorage(String id) {
        try {
            String[] ids = id.split("|");
            URL url = new URL(ids[0]);
            String protocol = url.getProtocol();
            return (Storage) Class.forName(String.format("aot.storage.%s.%sStorage", protocol, protocol.toUpperCase())).getConstructor(URL.class, String[].class).newInstance(url, ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Storage getStorage(String id) {
        Holder holder = storages.get(id);
        if (holder == null) {
            holder = MapUtil.putIfAbsent(storages, id, new Holder(id, createStorage(id)));
        }
        return holder.getStorage();
    }

    private static final class Holder {
        private final String id;
        private final Storage storage;
        private final AtomicLong accessTime;

        public Holder(String id, Storage storage) {
            this.id = id;
            this.storage = storage;
            this.accessTime = new AtomicLong(System.currentTimeMillis());
        }

        public String getId() {
            return id;
        }

        public Storage getStorage() {
            accessTime.set(System.currentTimeMillis());
            return storage;
        }

        public long getAccessTime() {
            return accessTime.get();
        }
    }
}
