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

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class LogFile implements EventSource {
    protected final Layer layer;
    protected final String id;
    protected final long time;

    protected LogFile(Layer layer, String id) {
        this.layer = layer;
        this.id = id;
        this.time = 0;
    }

    public Layer getLayer() {
        return layer;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        return null;
    }
}
