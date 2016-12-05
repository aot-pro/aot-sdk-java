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

package aot.storage.file;

import aot.storage.CreateSubstorageException;
import aot.storage.GetStorageException;
import aot.storage.PutStorageException;
import aot.storage.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class CustomStorage extends Storage {
    public CustomStorage(URL url) {
        super("file", url.getHost(), url.getPath());
    }

    @Override
    public String getHttpsUrl() {
        return null;
    }

    @Override
    public Storage createSubstorage(String prefix) {
        try {
            return new CustomStorage(new URL(getUrl() + prefix));
        } catch (Exception e) {
            throw new CreateSubstorageException(getUrl(), e);
        }
    }

    @Override
    public Iterable<String> find(String prefix, String filter) {
        return null;
    }

    @Override
    public Iterable<String> list(String prefix, String filter) {
        return null;
    }

    @Override
    public void getMeta(String key, Map<String, String> meta) {
        try {
            Map<String, Object> attributes = Files.readAttributes(Paths.get(String.format("%s%s", prefix, key)), "*");
            for (Map.Entry<String, Object> e : attributes.entrySet()) {
                String k = e.getKey();
                Object v = e.getValue();
                if (v instanceof String) {
                    meta.put(k, (String) v);
                }
            }
        } catch (Exception e) {
            throw new GetStorageException(getUrl(), e);
        }
    }

    @Override
    public byte[] get(String key, Map<String, String> meta) {
        if (meta != null) {
            getMeta(key, meta);
        }
        try {
            return Files.readAllBytes(Paths.get(String.format("%s%s", prefix, key)));
        } catch (Exception e) {
            throw new GetStorageException(getUrl(), e);
        }
    }

    @Override
    public InputStream getStream(String key, Map<String, String> meta) {
        if (meta != null) {
            getMeta(key, meta);
        }
        try {
            return Files.newInputStream(Paths.get(String.format("%s%s", prefix, key)));
        } catch (Exception e) {
            throw new GetStorageException(getUrl(), e);
        }
    }

    @Override
    public long put(String key, byte[] data, Map<String, String> meta) {
        try {
            Path path = Paths.get(String.format("%s%s", prefix, key));
            Files.write(path, data);
            if (meta != null) {
                for (Map.Entry<String, String> e : meta.entrySet()) {
                    Files.setAttribute(path, e.getKey(), e.getValue());
                }
            }
            return data.length;
        } catch (Exception e) {
            throw new PutStorageException(getUrl(), e);
        }
    }

    @Override
    public long upload(String key, InputStream input, long size, Map<String, String> meta) {
        return 0;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public String getUrl(String key) {
        return null;
    }

    @Override
    public String getHttpsUrl(String key) {
        return null;
    }
}
