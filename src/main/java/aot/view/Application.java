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

package aot.view;

import aot.storage.Storage;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Application implements Iterable<Version>, EventSource {
    protected final Environment environment;
    protected final String id;
    protected final Storage storage;

    protected Application(Environment environment, String id) {
        this.environment = environment;
        this.id = id;
        this.storage = environment.getDataStorage().createSubstorage(String.format("/%s", id));
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getId() {
        return id;
    }

    public Storage getStorage() {
        return storage;
    }

    public TreeMap<String, Version> getVersions() {
        return getVersions(null);
    }

    public TreeMap<String, Version> getVersions(String filter) {
        TreeMap<String, Version> versions = new TreeMap<>();
        for (String versionId : storage.find("", filter)) {
            Version version = new Version(this, versionId);
            versions.put(version.getId(), version);
        }
        return versions;
    }

    @Override
    public Iterator<Version> iterator() {
        return getVersions().values().iterator();
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        return new EventMixer(filter, getVersions(filter.getVersion()).values());
    }
}
