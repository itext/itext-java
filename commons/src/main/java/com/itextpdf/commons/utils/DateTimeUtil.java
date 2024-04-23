/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.commons.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class DateTimeUtil {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

    private DateTimeUtil() {
        // Empty constructor.
    }

    /**
     * Gets the {@link Calendar} as UTC milliseconds from the epoch.
     *
     * @param calendar the calendar to be converted to millis
     *
     * @return the date as UTC milliseconds from the epoch
     */
    public static double getUtcMillisFromEpoch(Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        return calendar.getTimeInMillis();
    }

    /**
     * Gets the date as {@link Calendar}.
     *
     * @param date the date to be returned as {@link Calendar}
     *
     * @return the date as {@link Calendar}
     */
    public static Calendar getCalendar(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Gets a default {@link GregorianCalendar}.
     *
     * @return a default {@link GregorianCalendar} using the current time in the default
     * time zone with the default locale
     */
    public static Calendar getCurrentTimeCalendar() {
        return new GregorianCalendar();
    }


    /**
     * Gets current time consistently.
     *
     * @return the time at which it was allocated, measured to the nearest millisecond
     */
    public static Date getCurrentTimeDate() {
        return new Date();
    }

    /**
     * Adds the specified amount of days to the given calendar field.
     *
     * @param calendar the calendar field where to add
     * @param days the amount of days to be added
     *
     * @return the time at which it was allocated, measured to the nearest millisecond
     */
    public static Calendar addDaysToCalendar(Calendar calendar, int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar;
    }

    /**
     * Defines if date is in past.
     *
     * @param date the date to be compared with current date
     *
     * @return <code>true</code> if given date is in past, <code>false</code> instead
     */
    public static boolean isInPast(Date date) {
        return date.before(getCurrentTimeDate());
    }

    /**
     * Gets the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by specified date.
     *
     * @param date the specified date to get time
     *
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by the specified date
     */
    public static long getRelativeTime(Date date) {
        return date.getTime();
    }

    /**
     * Adds provided number of milliseconds to the Date.
     * 
     * @param date {@link Date} date to increase
     * @param millis number of milliseconds to add
     * 
     * @return updated {@link Date}
     */
    public static Date addMillisToDate(Date date, long millis) {
        return new Date(DateTimeUtil.getRelativeTime(date) + millis);
    }

    /**
     * Adds the specified amount of days to the given date.
     *
     * @param date the specified date to add
     * @param days the amount of days to be added
     *
     * @return a {@link Date} object representing the calendar's time value (millisecond
     * offset from the Epoch)
     */
    public static Date addDaysToDate(Date date, int days) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /**
     * Adds the specified amount of years to the given date.
     *
     * @param date the specified date to add
     * @param years the amount of years to be added
     *
     * @return a {@link Date} object representing the calendar's time value (millisecond
     * offset from the Epoch)
     */
    public static Date addYearsToDate(Date date, int years) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    /**
     * Parses passing date with default yyyy-MM-dd pattern.
     *
     * @param date is date to be parse
     *
     * @return parse date
     */
    public static Date parseWithDefaultPattern(String date) {
        return parse(date, DEFAULT_PATTERN);
    }

    /**
     * Parses passing date with specified format.
     *
     * @param date the date to be parsed
     * @param format the format of parsing the date
     *
     * @return parsed date
     */
    public static Date parse(String date, String format) {
        try {
            return initParserSDF(format).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Format passing date with default yyyy-MM-dd pattern.
     *
     * @param date the date to be formatted
     *
     * @return formatted date
     */
    public static String formatWithDefaultPattern(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    /**
     * Format passing date with specified pattern.
     *
     * @param date date to be formatted
     * @param pattern pattern for format
     *
     * @return formatted date
     */
    public static String format(Date date, String pattern) {
        return initParserSDF(pattern).format(date);
    }

    /**
     * Gets the offset of time zone from UTC at the specified date.
     *
     * @param date the date represented in milliseconds since January 1, 1970 00:00:00 GMT
     *
     * @return the offset of time zone from UTC at the specified date adjusted with the amount
     * of daylight saving.
     */
    public static long getCurrentTimeZoneOffset(Date date) {
        TimeZone tz = TimeZone.getDefault();
        return tz.getOffset(date.getTime());
    }

    /**
     * Converts {@link Calendar} date to string of "yyyy.MM.dd HH:mm:ss z" format.
     *
     * @param date to convert.
     *
     * @return string date value.
     */
    public static String dateToString(Calendar date) {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z").format(date.getTime());
    }

    private static DateFormat initParserSDF(String pattern) {
        final SimpleDateFormat parserSDF = new SimpleDateFormat(pattern);
        parserSDF.setCalendar(new GregorianCalendar());
        return parserSDF;
    }
}
