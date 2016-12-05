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
public class Instance implements Iterable<Layer>, EventSource {
    protected final Version version;
    protected final String id;
    protected final Storage storage;

    protected Instance(Version version, String id) {
        this.version = version;
        this.id = id;
        this.storage = version.getStorage().createSubstorage(String.format("/%s", id));
    }

    public Version getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public Storage getStorage() {
        return storage;
    }

    public TreeMap<String, Layer> getLayers() {
        return getLayers(null);
    }

    public TreeMap<String, Layer> getLayers(String filter) {
        TreeMap<String, Layer> layers = new TreeMap<>();
        for (String layerId : storage.find("", filter)) {
            Layer layer = new Layer(this, layerId);
            layers.put(layer.getId(), layer);
        }
        return layers;
    }

    @Override
    public Iterator<Layer> iterator() {
        return getLayers().values().iterator();
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        return new EventMixer(filter, getLayers(filter.getLayer()).values());
    }
}
