/*
 * Copyright (C) 2014 Dmitry Kotlyarov.
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

package aot.util.time;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class TimeUtil {
    private TimeUtil() {
    }

    public static long microTime() {
        return System.nanoTime() / 1000L;
    }

    public static Timestamp currentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String formatTime(long time) {
        return String.format("%1$tY.%1$tm.%1$td %1$tH:%1$tM:%1$tS.%1$tL", time);
    }

    public static String formatMicroSpan(long time) {
        final long timeHigh = time / 1000L;
        final long timeLow = time % 1000L;
        return String.format("%d.%03d", timeHigh, timeLow);
    }

    public static Date parseDate(String date) {
        try {
            if (date != null) {
                String[] items = date.split("/");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(items[2]), Integer.parseInt(items[0]) - 1, Integer.parseInt(items[1]));
                return new Date(calendar.getTimeInMillis());
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public static Integer getAge(Date dateOfBirth) {
        try {
            if (dateOfBirth != null) {
                Calendar today = Calendar.getInstance();
                Calendar birthDate = Calendar.getInstance();
                birthDate.setTime(dateOfBirth);
                int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
                if (age > 0) {
                    return age;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
