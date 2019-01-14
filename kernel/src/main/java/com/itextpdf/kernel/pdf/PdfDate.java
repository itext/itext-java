/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 * {@code PdfDate} is the PDF date object.
 * <P>
 * PDF defines a standard date format. The PDF date format closely follows the format
 * defined by the international standard ASN.1 (Abstract Syntax Notation One, defined
 * in CCITT X.208 or ISO/IEC 8824). A date is a {@code PdfString} of the form:
 * <P><BLOCKQUOTE>
 * (D:YYYYMMDDHHmmSSOHH'mm')
 * </BLOCKQUOTE><P>
 * See also ISO-320001 7.9.4, "Dates".
 *
 * @see PdfString
 * @see java.util.GregorianCalendar
 */

public class PdfDate extends PdfObjectWrapper<PdfString> {

    private static final long serialVersionUID = -7424858548790000216L;
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
        sb.append(d.substring(0, 4)); //year
        d = d.substring(4);
        if (d.length() < 2)
            return sb.toString();
        sb.append('-').append(d.substring(0, 2)); //month
        d = d.substring(2);
        if (d.length() < 2)
            return sb.toString();
        sb.append('-').append(d.substring(0, 2)); //day
        d = d.substring(2);
        if (d.length() < 2)
            return sb.toString();
        sb.append('T').append(d.substring(0, 2)); //hour
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append(":00Z");
            return sb.toString();
        }
        sb.append(':').append(d.substring(0, 2)); //minute
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append('Z');
            return sb.toString();
        }
        sb.append(':').append(d.substring(0, 2)); //second
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
    private static String setLength(int i, int length) { // 1.3-1.4 problem fixed by Finn Bock
        StringBuilder tmp = new StringBuilder();
        tmp.append(i);
        while (tmp.length() < length) {
            tmp.insert(0, "0");
        }
        tmp.setLength(length);
        return tmp.toString();
    }
}
