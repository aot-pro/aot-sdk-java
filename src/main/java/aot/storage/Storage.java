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

package aot.storage;

import aot.util.CborUtil;
import aot.util.JobjUtil;
import aot.util.JsonUtil;
import aot.util.MapUtil;
import aot.util.XmlUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public abstract class Storage {
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

    public abstract Storage getSubstorage(String prefix);

    public Iterable<String> find(String prefix) {
        return find(prefix, null);
    }

    public abstract Iterable<String> find(String prefix, String filter);

    public Iterable<String> list(String prefix) {
        return list(prefix, null);
    }

    public abstract Iterable<String> list(String prefix, String filter);

    public byte[] get(String key) {
        return get(key, null);
    }

    public abstract byte[] get(String key, Map<String, String> meta);

    public InputStream getStream(String key) {
        return getStream(key, null);
    }

    public abstract InputStream getStream(String key, Map<String, String> meta);

    public <T> T getCbor(String key, Class<T> type) {
        return getCbor(key, type, null);
    }

    public <T> T getCbor(String key, Class<T> type, Map<String, String> meta) {
        return CborUtil.readBytes(getStream(key, meta), type);
    }

    public <T> T getJobj(String key, Class<T> type) {
        return getJobj(key, type, null);
    }

    public <T> T getJobj(String key, Class<T> type, Map<String, String> meta) {
        return JobjUtil.readBytes(getStream(key, meta), type);
    }

    public <T> T getJson(String key, Class<T> type) {
        return getJson(key, type, null);
    }

    public <T> T getJson(String key, Class<T> type, Map<String, String> meta) {
        return JsonUtil.readBytes(getStream(key, meta), type);
    }

    public <T> T getXml(String key, Class<T> type) {
        return getXml(key, type, null);
    }

    public <T> T getXml(String key, Class<T> type, Map<String, String> meta) {
        return XmlUtil.readBytes(getStream(key, meta), type);
    }

    public long download(String key, OutputStream output) {
        return download(key, output, null);
    }

    public long download(String key, OutputStream output, Map<String, String> meta) {
        long size = 0L;
        try (InputStream input = getStream(key, meta)) {
            int len = 0;
            byte[] buf = new byte[65536];
            while ((len = input.read(buf)) > 0) {
                output.write(buf, 0, len);
                size += len;
            }
        } catch (IOException e) {
            throw new StorageException(e);
        }
        return size;
    }

    public long put(String key, byte[] data) {
        return put(key, data, null);
    }

    public abstract long put(String key, byte[] data, Map<String, String> meta);

    public <T> long putCbor(String key, T value) {
        return putCbor(key, value, null);
    }

    public <T> long putCbor(String key, T value, Map<String, String> meta) {
        byte[] data = CborUtil.toBytes(value);
        if (meta == null) {
            meta = new LinkedHashMap<>();
        } else {
            meta = new LinkedHashMap<>(meta);
        }
        meta.put("Content-Type", CborUtil.APPLICATION_CBOR);
        return put(key, data, meta);
    }

    public <T> long putJobj(String key, T value) {
        return putJobj(key, value, null);
    }

    public <T> long putJobj(String key, T value, Map<String, String> meta) {
        byte[] data = JobjUtil.toBytes(value);
        if (meta == null) {
            meta = new LinkedHashMap<>();
        } else {
            meta = new LinkedHashMap<>(meta);
        }
        meta.put("Content-Type", JobjUtil.APPLICATION_JOBJ);
        return put(key, data, meta);
    }

    public <T> long putJson(String key, T value) {
        return putJson(key, value, null);
    }

    public <T> long putJson(String key, T value, Map<String, String> meta) {
        byte[] data = JsonUtil.toBytes(value);
        if (meta == null) {
            meta = new LinkedHashMap<>();
        } else {
            meta = new LinkedHashMap<>(meta);
        }
        meta.put("Content-Type", JsonUtil.APPLICATION_JSON);
        return put(key, data, meta);
    }

    public <T> long putXml(String key, T value) {
        return putXml(key, value, null);
    }

    public <T> long putXml(String key, T value, Map<String, String> meta) {
        byte[] data = XmlUtil.toBytes(value);
        if (meta == null) {
            meta = new LinkedHashMap<>();
        } else {
            meta = new LinkedHashMap<>(meta);
        }
        meta.put("Content-Type", XmlUtil.APPLICATION_XML);
        return put(key, data, meta);
    }

    public long upload(String key, InputStream input, long size) {
        return upload(key, input, size, null);
    }

    public abstract long upload(String key, InputStream input, long size, Map<String, String> meta);

    public abstract void delete(String key);

    public abstract String getUrl(String key);

    public abstract String getHttpsUrl(String key);

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
}
