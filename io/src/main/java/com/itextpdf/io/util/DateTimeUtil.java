/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class DateTimeUtil {

    public static double getUtcMillisFromEpoch(Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        return calendar.getTimeInMillis();
    }

    public static Calendar getCurrentTimeCalendar() {
        return new GregorianCalendar();
    }

    public static Date getCurrentTimeDate() {
        return new Date();
    }

    public static Calendar addDaysToCalendar(Calendar calendar, int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar;
    }

    public static Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static Date parseSimpleFormat(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
