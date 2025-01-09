/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 * {@code PdfDate} is the PDF date object.
 * <p>
 * PDF defines a standard date format. The PDF date format closely follows the format
 * defined by the international standard ASN.1 (Abstract Syntax Notation One, defined
 * in CCITT X.208 or ISO/IEC 8824). A date is a {@code PdfString} of the form:
 * <p>
 * {@code (D:YYYYMMDDHHmmSSOHH'mm') }
 * <p>
 * See also ISO-320001 7.9.4, "Dates".
 *
 * @see PdfString
 * @see java.util.GregorianCalendar
 */

public class PdfDate extends PdfObjectWrapper<PdfString> {

	private static final int[] DATE_SPACE = {Calendar.YEAR, 4, 0, Calendar.MONTH, 2, -1, Calendar.DAY_OF_MONTH, 2, 0,
            Calendar.HOUR_OF_DAY, 2, 0, Calendar.MINUTE, 2, 0, Calendar.SECOND, 2, 0};

    /**
     * Constructs a {@code PdfDate}-object.
     *
     * @param d the date that has to be turned into a {@code PdfDate} &gt;-object
     */
    public PdfDate(Calendar d) {
        super(new PdfString(generateStringByCalendar(d)));
    }

    /**
     * Constructs a {@code PdfDate}-object, representing the current day and time.
     */
    public PdfDate() {
        this(new GregorianCalendar());
    }

    /**
     * Gives the W3C format of the PdfDate.
     * @return a formatted date
     */
    public String getW3CDate() {
        return getW3CDate(getPdfObject().getValue());
    }

    /**
     * Gives the W3C format of the {@code PdfDate}.
     * @param d the date in the format D:YYYYMMDDHHmmSSOHH'mm'
     * @return a formatted date
     */
    public static String getW3CDate(String d) {
        if (d.startsWith("D:"))
            d = d.substring(2);
        StringBuilder sb = new StringBuilder();
        if (d.length() < 4)
            return "0000";
        //year
        sb.append(d.substring(0, 4));
        d = d.substring(4);
        if (d.length() < 2)
            return sb.toString();
        //month
        sb.append('-').append(d.substring(0, 2));
        d = d.substring(2);
        if (d.length() < 2)
            return sb.toString();
        //day
        sb.append('-').append(d.substring(0, 2));
        d = d.substring(2);
        if (d.length() < 2)
            return sb.toString();
        //hour
        sb.append('T').append(d.substring(0, 2));
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append(":00Z");
            return sb.toString();
        }
        //minute
        sb.append(':').append(d.substring(0, 2));
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append('Z');
            return sb.toString();
        }
        //second
        sb.append(':').append(d.substring(0, 2));
        d = d.substring(2);
        if (d.startsWith("-") || d.startsWith("+")) {
            String sign = d.substring(0, 1);
            d = d.substring(1);
            if (d.length() >= 2) {
                String h = d.substring(0, 2);
                String m = "00";
                if (d.length() > 2) {
                    d = d.substring(3);
                    if (d.length() >= 2)
                        m = d.substring(0, 2);
                }
                sb.append(sign).append(h).append(':').append(m);
                return sb.toString();
            }
        }
        sb.append('Z');
        return sb.toString();
    }

    /**
     * Converts a PDF string representing a date into a {@code Calendar}.
     * @param s the PDF string representing a date
     * @return a {@code Calendar} representing the date or {@code null} if the string
     * was not a date
     */
    public static Calendar decode(String s) {
        try {
            if (s.startsWith("D:"))
                s = s.substring(2);
            GregorianCalendar calendar;
            int slen = s.length();
            int idx = s.indexOf('Z');
            if (idx >= 0) {
                slen = idx;
                calendar = new GregorianCalendar(new SimpleTimeZone(0, "ZPDF"));
            }
            else {
                int sign = 1;
                idx = s.indexOf('+');
                if (idx < 0) {
                    idx = s.indexOf('-');
                    if (idx >= 0)
                        sign = -1;
                }
                if (idx < 0)
                    calendar = new GregorianCalendar();
                else {
                    int offset = Integer.parseInt(s.substring(idx + 1, idx + 3)) * 60;
                    if (idx + 5 < s.length())
                        offset += Integer.parseInt(s.substring(idx + 4, idx + 6));
                    calendar = new GregorianCalendar(new SimpleTimeZone(offset * sign * 60000, "ZPDF"));
                    slen = idx;
                }
            }
            calendar.clear();
            idx = 0;
            for (int k = 0; k < DATE_SPACE.length; k += 3) {
                if (idx >= slen)
                    break;
                calendar.set(DATE_SPACE[k], Integer.parseInt(s.substring(idx, idx + DATE_SPACE[k + 1])) + DATE_SPACE[k + 2]);
                idx += DATE_SPACE[k + 1];
            }
            return calendar;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private static String generateStringByCalendar(Calendar d) {
        StringBuilder date = new StringBuilder("D:");
        date.append(setLength(d.get(Calendar.YEAR), 4));
        date.append(setLength(d.get(Calendar.MONTH) + 1, 2));
        date.append(setLength(d.get(Calendar.DATE), 2));
        date.append(setLength(d.get(Calendar.HOUR_OF_DAY), 2));
        date.append(setLength(d.get(Calendar.MINUTE), 2));
        date.append(setLength(d.get(Calendar.SECOND), 2));
        int timezone = (d.get(Calendar.ZONE_OFFSET) + d.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000);
        if (timezone == 0) {
            date.append('Z');
        }
        else if (timezone < 0) {
            date.append('-');
            timezone = -timezone;
        }
        else {
            date.append('+');
        }
        if (timezone != 0) {
            date.append(setLength(timezone, 2)).append('\'');
            int zone = Math.abs((d.get(Calendar.ZONE_OFFSET) + d.get(Calendar.DST_OFFSET)) / (60 * 1000)) - (timezone * 60);
            date.append(setLength(zone, 2)).append('\'');
        }
        return date.toString();
    }

    /**
     * Adds a number of leading zeros to a given {@code String} in order to get a {@code String}
     * of a certain length.
     *
     * @param i a given number
     * @param length the length of the resulting {@code String}
     * @return the resulting {@code String}
     */
    private static String setLength(int i, int length) {
        // 1.3-1.4 problem fixed by Finn Bock
        StringBuilder tmp = new StringBuilder();
        tmp.append(i);
        while (tmp.length() < length) {
            tmp.insert(0, "0");
        }
        tmp.setLength(length);
        return tmp.toString();
    }
}
