/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
     * Gets the offset of time zone from UTC
     *
     * @return the offset of time zone from UTC
     *
     * @deprecated Unused and will be removed in the next major release.
     * Use {@link DateTimeUtil#getCurrentTimeZoneOffset(Date)} instead.
     */
    @Deprecated
    public static long getCurrentTimeZoneOffset() {
        return getCurrentTimeZoneOffset(getCurrentTimeDate());
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

    private static DateFormat initParserSDF(String pattern) {
        final SimpleDateFormat parserSDF = new SimpleDateFormat(pattern);
        parserSDF.setCalendar(new GregorianCalendar());
        return parserSDF;
    }
}
