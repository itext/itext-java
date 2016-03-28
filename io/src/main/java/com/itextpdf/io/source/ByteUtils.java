package com.itextpdf.io.source;

import com.itextpdf.io.util.DecimalFormatUtils;

public class ByteUtils {

    static boolean HighPrecision = false;

    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static final byte[] zero = new byte[]{48};
    private static final byte[] one = new byte[]{49};
    private static final byte[] negOne = new byte[]{'-', 49};

    public static byte[] getIsoBytes(String text) {
        if (text == null)
            return null;
        int len = text.length();
        byte b[] = new byte[len];
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
        byte b[] = new byte[len];
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
            byte[] result = DecimalFormatUtils.formatNumber(d, "0.######").getBytes();
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
            long v = (long) d;
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