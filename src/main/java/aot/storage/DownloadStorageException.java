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
public class DownloadStorageException extends StorageException {
    private static final long serialVersionUID = 1L;

    public DownloadStorageException(Storage storage) {
        super(storage);
    }

    public DownloadStorageException(Storage storage, String message) {
        super(storage, message);
    }

    public DownloadStorageException(Storage storage, String message, Throwable cause) {
        super(storage, message, cause);
    }

    public DownloadStorageException(Storage storage, Throwable cause) {
        super(storage, cause);
    }

    public DownloadStorageException(Storage storage, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(storage, message, cause, enableSuppression, writableStackTrace);
    }
}
