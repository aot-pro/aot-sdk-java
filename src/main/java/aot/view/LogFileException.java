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
public class LogFileException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public LogFileException() {
    }

    public LogFileException(String message) {
        super(message);
    }

    public LogFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogFileException(Throwable cause) {
        super(cause);
    }

    public LogFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
