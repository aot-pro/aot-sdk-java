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
public class Environment implements Iterable<Application>, EventSource {
    protected final Storage configStorage;
    protected final Storage dataStorage;

    public Environment(Storage configStorage, Storage dataStorage) {
        this.configStorage = configStorage;
        this.dataStorage = dataStorage;
    }

    public Storage getConfigStorage() {
        return configStorage;
    }

    public Storage getDataStorage() {
        return dataStorage;
    }

    public TreeMap<String, Application> getApplications() {
        return getApplications(null);
    }

    public TreeMap<String, Application> getApplications(String filter) {
        TreeMap<String, Application> applications = new TreeMap<>();
        for (String applicationId : dataStorage.find("", filter)) {
            Application application = new Application(this, applicationId);
            applications.put(application.getId(), application);
        }
        return applications;
    }

    @Override
    public Iterator<Application> iterator() {
        return getApplications().values().iterator();
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        return new EventMixer(filter, getApplications(filter.getApplication()).values());
    }
}
