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
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Layer implements Iterable<LogFile>, EventSource {
    protected final Instance instance;
    protected final String id;
    protected final Storage storage;

    protected Layer(Instance instance, String id) {
        this.instance = instance;
        this.id = id;
        this.storage = instance.getStorage().substorage(String.format("/%s", id));
    }

    public Instance getInstance() {
        return instance;
    }

    public String getId() {
        return id;
    }

    public Storage getStorage() {
        return storage;
    }

    public TreeMap<Long, LogFile> getFiles() {
        TreeMap<Long, LogFile> files = new TreeMap<>();
        for (String fileId : storage.find("")) {
            LogFile file = new LogFile(this, fileId);
            files.put(file.getTime(), file);
        }
        return files;
    }

    public TreeMap<Long, LogFile> getFiles(long beginTime, long endTime) {
        String beginTimePath = TimeUtil.formatPath(beginTime);
        String endTimePath = TimeUtil.formatPath(endTime);
        String timePath = StringUtil.getCommonPrefix(beginTimePath, endTimePath);
        TreeMap<Long, LogFile> files = new TreeMap<>();
        for (String path = beginTimePath; true; path = path.substring(0, path.lastIndexOf("/"))) {
            LogFile minFile = null;
            for (String fileId : storage.find(path)) {
                LogFile file = new LogFile(this, fileId);
                if (file.getTime() < beginTime) {
                    if ((minFile == null) || (file.getTime() > minFile.getTime())) {
                        minFile = file;
                    }
                }
            }
            if (minFile != null) {
                files.put(minFile.getTime(), minFile);
                break;
            }
            if (path.isEmpty()) {
                break;
            }
        }
        for (String fileId : storage.find(timePath)) {
            LogFile file = new LogFile(this, fileId);
            files.put(file.getTime(), file);
        }
        return files;
    }

    @Override
    public Iterator<LogFile> iterator() {
        TreeMap<Long, LogFile> files = getFiles();
        return files.values().iterator();
    }

    @Override
    public Iterable<Event> getEvents(EventFilter filter) {
        TreeMap<Long, LogFile> files = getFiles(filter.getBeginTime(), filter.getEndTime());
        return new EventStream(filter, files.values());
    }
}
