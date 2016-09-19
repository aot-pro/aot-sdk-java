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
import aot.util.string.StringUtil;
import aot.util.time.TimeUtil;

import java.util.Iterator;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Layer implements Iterable<LogFile>, EventSource {
    protected final Instance instance;
    protected final Storage storage;
    protected final String id;

    protected Layer(Instance instance, String id) {
        this.instance = instance;
        this.storage = null;
        this.id = id;
    }

    public Instance getInstance() {
        return instance;
    }

    public Storage getStorage() {
        return storage;
    }

    public String getId() {
        return id;
    }

    public Iterable<LogFile> getFiles(long beginTime, long endTime) {
        String beginTimePath = TimeUtil.formatPath(beginTime);
        String endTimePath = TimeUtil.formatPath(endTime);
        String timePath = StringUtil.getCommonPrefix(beginTimePath, endTimePath);
        return null;
    }

    @Override
    public Iterator<LogFile> iterator() {
        return null;
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        return null;
    }
}
