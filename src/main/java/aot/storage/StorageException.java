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

package aot.storage;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class StorageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected final String url;

    public StorageException(String url) {
        this.url = url;
    }

    public StorageException(String url, String message) {
        super(String.format("[%s]: %s", url, message));

        this.url = url;
    }

    public StorageException(String url, String message, Throwable cause) {
        super(String.format("[%s]: %s", url, message), cause);

        this.url = url;
    }

    public StorageException(String url, Throwable cause) {
        super(String.format("[%s]: ", url), cause);

        this.url = url;
    }

    public StorageException(String url, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(String.format("[%s]: %s", url, message), cause, enableSuppression, writableStackTrace);

        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
