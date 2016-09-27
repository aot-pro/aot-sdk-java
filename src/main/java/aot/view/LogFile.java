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

import aot.util.time.TimeUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
        this.time = toTime(id);
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

    private static long toTime(String id) {
        String[] ids = id.split("/");
        int year1 = Integer.parseInt(ids[1]);
        int month1 = Integer.parseInt(ids[2]);
        int day1 = Integer.parseInt(ids[3]);
        int hour1 = Integer.parseInt(ids[4]);
        int minute1 = Integer.parseInt(ids[5]);
        int second1 = Integer.parseInt(ids[6]);
        int millisecond1 = Integer.parseInt(ids[7]);
        String name = ids[8];
        String[] names = name.split(".");
        String n = names[0];
        String e = names[1];
        String[] ns = n.split("-");
        int year2 = Integer.parseInt(ns[0]);
        int month2 = Integer.parseInt(ns[1]);
        int day2 = Integer.parseInt(ns[2]);
        int hour2 = Integer.parseInt(ns[3]);
        int minute2 = Integer.parseInt(ns[4]);
        int second2 = Integer.parseInt(ns[5]);
        int millisecond2 = Integer.parseInt(ns[6]);
        if ((year1 == year2) &&
            (month1 == month2) &&
            (day1 == day2) &&
            (hour1 == hour2) &&
            (minute1 == minute2) &&
            (second1 == second2) &&
            (millisecond1 == millisecond2) &&
            (e.equals("log"))) {
            Calendar calendar = new GregorianCalendar(TimeUtil.TIMEZONE_UTC);
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1 - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day1);
            calendar.set(Calendar.HOUR, hour1);
            calendar.set(Calendar.MINUTE, minute1);
            calendar.set(Calendar.SECOND, second1);
            calendar.set(Calendar.MILLISECOND, millisecond1);
            return calendar.getTimeInMillis();
        } else {
            throw new LogFileException(String.format("Illegal log file id '%s'", id));
        }
    }
}
