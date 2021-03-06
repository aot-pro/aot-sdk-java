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

package aot.util;

import java.util.Properties;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class PropertyUtil {
    private PropertyUtil() {
    }

    public static String getSystemProperty(String key) {
        String property = System.getProperty(key);
        if (property != null) {
            return property;
        } else {
            throw new PropertyNotFoundException(String.format("System property '%s' is not found", key));
        }
    }

    public static String getEnvironmentProperty(String key) {
        String property = System.getenv(key);
        if (property != null) {
            return property;
        } else {
            throw new PropertyNotFoundException(String.format("Environment property '%s' is not found", key));
        }
    }

    public static String getProperty(String systemKey, String environmentKey) {
        String property = System.getProperty(systemKey);
        if (property != null) {
            return property;
        } else {
            property = System.getenv(environmentKey);
            if (property != null) {
                return property;
            } else {
                throw new PropertyNotFoundException(String.format("System property '%s' or environment property '%s' are not found", systemKey, environmentKey));
            }
        }
    }

    public static String getProperty(String systemKey, String environmentKey, String defaultValue) {
        String property = System.getProperty(systemKey);
        if (property != null) {
            return property;
        } else {
            property = System.getenv(environmentKey);
            if (property != null) {
                return property;
            } else {
                return defaultValue;
            }
        }
    }

    public static String getProperty(Properties properties, String key) {
        String property = properties.getProperty(key);
        if (property != null) {
            return property;
        } else {
            throw new PropertyNotFoundException(String.format("Property '%s' is not found", key));
        }
    }

    public static Properties toProperties(String[][] properties) {
        Properties props = new Properties();
        for (String[] property : properties) {
            props.setProperty(property[0], property[1]);
        }
        return props;
    }
}
