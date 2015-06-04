package com.itextpdf.basics.io;

import com.itextpdf.basics.PdfRuntimeException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class OutputStream<T extends OutputStream> extends java.io.OutputStream {

    private static class ByteUtils {
        protected int count;
        private byte buffer[];

        public ByteUtils(int size) {
            buffer = new byte[size];
        }

        public ByteUtils prepend(byte b) {
            buffer[buffer.length - count - 1] = b;
            count++;
            return this;
        }

        public ByteUtils prepend(byte b[]) {
            System.arraycopy(b, 0, buffer, buffer.length - b.length, b.length);
            count += b.length;
            return this;
        }

        public ByteUtils reset() {
            count = 0;
            return this;
        }

        public byte[] toByteArray() {
            byte newBuf[] = new byte[count];
            System.arraycopy(buffer, startPos(), newBuf, 0, count);
            return newBuf;
        }

        public byte[] getBuffer() {
            return buffer;
        }

        public int size() {
            return count;
        }

        public int startPos() {
            return buffer.length - count;
        }
    }

    public static boolean HighPrecision = false;

    private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static final byte[] zero = new byte[]{48};
    private static final byte[] one = new byte[]{49};
    private static final byte[] negOne = new byte[]{'-', 49};
    //long=19 + max frac=6 => 26 => round to 32.
    private final ByteUtils numBuffer = new ByteUtils(32);


    protected java.io.OutputStream outputStream = null;
    protected long currentPos = 0;
    protected boolean closeStream = true;

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

    protected static byte[] getIsoBytes(int n, ByteUtils buffer) {
        boolean negative = false;
        if (n < 0) {
            negative = true;
            n = -n;
        }
        int intLen = intSize(n);
        ByteUtils buf = buffer == null ? new ByteUtils(intLen + (negative ? 1 : 0)) : buffer;
        for (int i = 0; i < intLen; i++) {
            buf.prepend(bytes[n % 10]);
            n /= 10;
        }
        if (negative)
            buf.prepend((byte) '-');

        return buffer == null ? buf.buffer : null;
    }

    protected static byte[] getIsoBytes(double d, ByteUtils buffer) {
        if (HighPrecision) {
            DecimalFormat dn = new DecimalFormat("0.######", dfs);
            byte[] result = dn.format(d).getBytes();
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
        ByteUtils buf;
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
            buf = buffer != null ? buffer : new ByteUtils(negative ? len + 3 : len + 2);
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
            buf = buffer != null ? buffer : new ByteUtils(intLen + fracLen + (negative ? 1 : 0));
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
            buf = buffer == null ? new ByteUtils(intLen + (negative ? 1 : 0)) : buffer;
            for (int i = 0; i < intLen; i++) {
                buf.prepend(bytes[(int) (v % 10)]);
                v /= 10;
            }
            if (negative) {
                buf.prepend((byte) '-');
            }
        }

        return buffer == null ? buf.buffer : null;
    }

    static int longSize(long l) {
        long m = 10;
        for (int i = 1; i < 19; i++) {
            if (l < m)
                return i;
            m *= 10;
        }
        return 19;
    }

    static int intSize(int l) {
        long m = 10;
        for (int i = 1; i < 10; i++) {
            if (l < m)
                return i;
            m *= 10;
        }
        return 10;
    }


    public OutputStream(java.io.OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        currentPos++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        currentPos += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        currentPos += len;
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        if (closeStream)
            outputStream.close();
    }

    public T writeLong(long value) {
        try {
            getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getBuffer(), numBuffer.startPos(), numBuffer.size());
            return (T) this;
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotWriteIntNumber, e);
        }
    }

    public T writeInteger(int value) {
        try {
            getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getBuffer(), numBuffer.startPos(), numBuffer.size());
            return (T) this;
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotWriteIntNumber, e);
        }
    }

    public T writeFloat(float value) {
        return writeDouble(value);
    }

    public T writeFloats(float[] value) {
        for (int i = 0; i < value.length; i++) {
            writeFloat(value[i]);
            if (i < value.length - 1)
                writeSpace();
        }
        return (T) this;
    }

    public T writeDouble(double value) {
        try {
            getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getBuffer(), numBuffer.startPos(), numBuffer.size());
            return (T) this;
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotWriteFloatNumber, e);
        }
    }

    public T writeByte(byte value) {
        try {
            write(value);
            return (T) this;
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotWriteByte, e);
        }
    }

    public T writeSpace() {
        return writeByte((byte) ' ');
    }

    public T writeNewLine() {
        return writeByte((byte) '\n');
    }

    public T writeString(String value) {
        return writeBytes(getIsoBytes(value));
    }

    public T writeBytes(byte[] b) {
        try {
            write(b);
            return (T) this;
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotWriteBytes, e);
        }
    }

    public T writeBytes(byte[] b, int off, int len) {
        try {
            write(b, off, len);
            return (T) this;
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotWriteBytes, e);
        }
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public java.io.OutputStream getOutputStream() {
        return outputStream;
    }

    public boolean isCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    public void assignBytes(byte[] bytes, int count) {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).assignBytes(bytes, count);
            currentPos = count;
        } else
            throw new PdfRuntimeException(PdfRuntimeException.BytesCanBeAssignedToByteArrayOutputStreamOnly);
    }
}
