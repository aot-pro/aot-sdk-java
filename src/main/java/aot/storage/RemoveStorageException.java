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
public class RemoveStorageException extends StorageException {
    private static final long serialVersionUID = 1L;

    public RemoveStorageException(String url) {
        super(url);
    }

    public RemoveStorageException(String url, String message) {
        super(url, message);
    }

    public RemoveStorageException(String url, String message, Throwable cause) {
        super(url, message, cause);
    }

    public RemoveStorageException(String url, Throwable cause) {
        super(url, cause);
    }

    public RemoveStorageException(String url, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(url, message, cause, enableSuppression, writableStackTrace);
    }
}
