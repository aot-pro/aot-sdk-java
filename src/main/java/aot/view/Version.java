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
public class Version implements Iterable<Instance>, EventSource {
    protected final Application application;
    protected final String id;
    protected final Storage storage;

    protected Version(Application application, String id) {
        this.application = application;
        this.id = id;
        this.storage = application.getStorage().createSubstorage(String.format("/%s", id));
    }

    public Application getApplication() {
        return application;
    }

    public String getId() {
        return id;
    }

    public Storage getStorage() {
        return storage;
    }

    public TreeMap<String, Instance> getInstances() {
        return getInstances(null);
    }

    public TreeMap<String, Instance> getInstances(String filter) {
        TreeMap<String, Instance> instances = new TreeMap<>();
        for (String instanceId : storage.find("", filter)) {
            Instance instance = new Instance(this, instanceId);
            instances.put(instance.getId(), instance);
        }
        return instances;
    }

    @Override
    public Iterator<Instance> iterator() {
        return getInstances().values().iterator();
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        return new EventMixer(filter, getInstances(filter.getInstance()).values());
    }
}
