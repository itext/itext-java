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
package com.itextpdf.io.source;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.DecimalFormatUtil;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ByteUtils {

    static boolean HighPrecision = false;

    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static final byte[] zero = new byte[]{48};
    private static final byte[] one = new byte[]{49};
    private static final byte[] negOne = new byte[]{(byte) '-', 49};

    public static byte[] getIsoBytes(String text) {
        if (text == null)
            return null;
        int len = text.length();
        byte[] b = new byte[len];
        for (int k = 0; k < len; ++k)
            b[k] = (byte) text.charAt(k);
        return b;
    }

    public static byte[] getIsoBytes(byte pre, String text) {
        return getIsoBytes(pre, text, (byte) 0);
    }

    public static byte[] getIsoBytes(byte pre, String text, byte post) {
        if (text == null)
            return null;
        int len = text.length();
        int start = 0;
        if (pre != 0) {
            len++;
            start = 1;
        }
        if (post != 0) {
            len++;
        }
        byte[] b = new byte[len];
        if (pre != 0) {
            b[0] = pre;
        }
        if (post != 0) {
            b[len - 1] = post;
        }
        for (int k = 0; k < text.length(); ++k)
            b[k + start] = (byte) text.charAt(k);
        return b;
    }

    public static byte[] getIsoBytes(int n) {
        return getIsoBytes(n, null);
    }

    public static byte[] getIsoBytes(double d) {
        return getIsoBytes(d, null);
    }

    static byte[] getIsoBytes(int n, ByteBuffer buffer) {
        boolean negative = false;
        if (n < 0) {
            negative = true;
            n = -n;
        }
        int intLen = intSize(n);
        ByteBuffer buf = buffer == null ? new ByteBuffer(intLen + (negative ? 1 : 0)) : buffer;
        for (int i = 0; i < intLen; i++) {
            buf.prepend(bytes[n % 10]);
            n /= 10;
        }
        if (negative)
            buf.prepend((byte) '-');

        return buffer == null ? buf.getInternalBuffer() : null;
    }

    static byte[] getIsoBytes(double d, ByteBuffer buffer) {
        return getIsoBytes(d, buffer, HighPrecision);
    }

    static byte[] getIsoBytes(double d, ByteBuffer buffer, boolean highPrecision) {
        if (highPrecision) {
            if (Math.abs(d) < 0.000001) {
                if (buffer != null) {
                    buffer.prepend(zero);
                    return null;
                } else {
                    return zero;
                }
            }
            if (Double.isNaN(d)) {
                Logger logger = LoggerFactory.getLogger(ByteUtils.class);
                logger.error(LogMessageConstant.ATTEMPT_PROCESS_NAN);
                d = 0;
            }
            byte[] result = DecimalFormatUtil.formatNumber(d, "0.######").getBytes(StandardCharsets.ISO_8859_1);
            if (buffer != null) {
                buffer.prepend(result);
                return null;
            } else {
                return result;
            }
        }
        boolean negative = false;
        if (Math.abs(d) < 0.000015) {
            if (buffer != null) {
                buffer.prepend(zero);
                return null;
            } else {
                return zero;
            }
        }
        ByteBuffer buf;
        if (d < 0) {
            negative = true;
            d = -d;
        }
        if (d < 1.0) {
            d += 0.000005;
            if (d >= 1) {
                byte[] result;
                if (negative) {
                    result = negOne;
                } else {
                    result = one;
                }
                if (buffer != null) {
                    buffer.prepend(result);
                    return null;
                } else {
                    return result;
                }
            }
            int v = (int) (d * 100000);
            int len = 5;
            for (; len > 0; len--) {
                if (v % 10 != 0) break;
                v /= 10;
            }
            buf = buffer != null ? buffer : new ByteBuffer(negative ? len + 3 : len + 2);
            for (int i = 0; i < len; i++) {
                buf.prepend(bytes[v % 10]);
                v /= 10;
            }
            buf.prepend((byte) '.').prepend((byte) '0');
            if (negative) {
                buf.prepend((byte) '-');
            }
        } else if (d <= 32767) {
            d += 0.005;
            int v = (int) (d * 100);
            int intLen;
            if (v >= 1000000) {
                intLen = 5;
            } else if (v >= 100000) {
                intLen = 4;
            } else if (v >= 10000) {
                intLen = 3;
            } else if (v >= 1000) {
                intLen = 2;
            } else {
                intLen = 1;
            }
            int fracLen = 0;
            if (v % 100 != 0) {
                fracLen = 2;                             //fracLen include '.'
                if (v % 10 != 0) {
                    fracLen++;
                } else {
                    v /= 10;
                }
            } else {
                v /= 100;
            }
            buf = buffer != null ? buffer : new ByteBuffer(intLen + fracLen + (negative ? 1 : 0));
            for (int i = 0; i < fracLen - 1; i++) {     //-1 because fracLen include '.'
                buf.prepend(bytes[v % 10]);
                v /= 10;
            }
            if (fracLen > 0) {
                buf.prepend((byte) '.');
            }
            for (int i = 0; i < intLen; i++) {
                buf.prepend(bytes[v % 10]);
                v /= 10;
            }
            if (negative) {
                buf.prepend((byte) '-');
            }
        } else {
            d += 0.5;
            long v;
            if (d > Long.MAX_VALUE) {
                // by default cast logic do the same, but not in .NET
                v = Long.MAX_VALUE;
            } else {
                if (Double.isNaN(d)) {
                    Logger logger = LoggerFactory.getLogger(ByteUtils.class);
                    logger.error(LogMessageConstant.ATTEMPT_PROCESS_NAN);
                    // in java NaN casted to long results in 0, but in .NET it results in long.MIN_VALUE
                    d = 0;
                }
                v = (long) d;
            }
            int intLen = longSize(v);
            buf = buffer == null ? new ByteBuffer(intLen + (negative ? 1 : 0)) : buffer;
            for (int i = 0; i < intLen; i++) {
                buf.prepend(bytes[(int) (v % 10)]);
                v /= 10;
            }
            if (negative) {
                buf.prepend((byte) '-');
            }
        }

        return buffer == null ? buf.getInternalBuffer() : null;
    }

    private static int longSize(long l) {
        long m = 10;
        for (int i = 1; i < 19; i++) {
            if (l < m)
                return i;
            m *= 10;
        }
        return 19;
    }

    private static int intSize(int l) {
        long m = 10;
        for (int i = 1; i < 10; i++) {
            if (l < m)
                return i;
            m *= 10;
        }
        return 10;
    }
}
