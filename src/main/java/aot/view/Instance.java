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
public class Instance {
    protected final Version version;
    protected final String id;
    protected final LogView logView;
    protected final StatView statView;

    protected Instance(Version version, String id) {
        this.version = version;
        this.id = id;
        this.logView = new LogView(this);
        this.statView = new StatView(this);
    }

    public Version getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public LogView getLogView() {
        return logView;
    }

    public StatView getStatView() {
        return statView;
    }
}
