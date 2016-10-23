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

import aot.storage.Storage;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class CustomStorage extends Storage {
    public CustomStorage(URL url, String[] ids) {
        super("file", url.getHost(), url.getPath());
    }

    @Override
    public String getHttpsUrl() {
        return null;
    }

    @Override
    public Storage getSubstorage(String prefix) {
        return null;
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
    public byte[] get(String key, Map<String, String> meta) {
        return new byte[0];
    }

    @Override
    public InputStream getStream(String key, Map<String, String> meta) {
        return null;
    }

    @Override
    public long put(String key, byte[] data, Map<String, String> meta) {
        return 0;
    }

    @Override
    public long upload(String key, InputStream input, long size, Map<String, String> meta) {
        return 0;
    }

    @Override
    public void delete(String key) {

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
