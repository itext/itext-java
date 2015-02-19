package com.itextpdf.basics;

import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.io.OutputStream;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.InflaterInputStream;

public class Utilities {

    private static final int transferSize = 64 * 1024;
    private static final byte[] escR = OutputStream.getIsoBytes("\\r");
    private static final byte[] escN = OutputStream.getIsoBytes("\\n");
    private static final byte[] escT = OutputStream.getIsoBytes("\\t");
    private static final byte[] escB = OutputStream.getIsoBytes("\\b");
    private static final byte[] escF = OutputStream.getIsoBytes("\\f");

    /**
     * This method is an alternative for the {@code InputStream.skip()}
     * -method that doesn't seem to work properly for big values of {@code size
     * }.
     *
     * @param is   the {@code InputStream}
     * @param size the number of bytes to skip
     * @throws java.io.IOException
     */
    static public void skip(final InputStream is, int size) throws IOException {
        long n;
        while (size > 0) {
            n = is.skip(size);
            if (n <= 0)
                break;
            size -= n;
        }
    }

    /**
     * This method makes a valid URL from a given filename.
     * <P>
     * This method makes the conversion of this library from the JAVA 2 platform
     * to a JDK1.1.x-version easier.
     *
     * @param filename
     *            a given filename
     * @return a valid URL
     * @throws java.net.MalformedURLException
     */
    public static URL toURL(final String filename) throws MalformedURLException {
        URL url;
        try {
            url = new URL(filename);
        } catch (MalformedURLException e) {
            url = new File(filename).toURI().toURL();
        }
        return url;
    }

    public static boolean equalsArray(final byte ar1[], final byte ar2[], final int size) {
        for (int k = 0; k < size; ++k) {
            if (ar1[k] != ar2[k])
                return false;
        }
        return true;
    }

    /**
     * Escapes a {@code byte} array according to the PDF conventions.
     *
     * @param bytes the {@code byte} array to escape
     * @return an escaped {@code byte} array
     */
    public static byte[] createEscapedString(final byte bytes[]) {
        return createBufferedEscapedString(bytes).toByteArray();
    }

    /**
     * Escapes a {@code byte} array according to the PDF conventions.
     *
     * @param outputStream the {@code OutputStream} an escaped {@code byte} array write to.
     * @param bytes the {@code byte}> array to escape.
     */
    public static void writeEscapedString(OutputStream outputStream, final byte[] bytes) throws PdfException {
        ByteBuffer buf = createBufferedEscapedString(bytes);
        outputStream.writeBytes(buf.getInternalBuffer(), 0, buf.size());
    }

    public static ByteBuffer createBufferedEscapedString(final byte[] bytes) {
        ByteBuffer buf = new ByteBuffer(bytes.length*2 + 2);
        buf.append('(');
        for (byte b : bytes) {
            switch (b) {
                case '\r':
                    buf.append(escR);
                    break;
                case '\n':
                    buf.append(escN);
                    break;
                case '\t':
                    buf.append(escT);
                    break;
                case '\b':
                    buf.append(escB);
                    break;
                case '\f':
                    buf.append(escF);
                    break;
                case '(':
                case ')':
                case '\\':
                    buf.append('\\').append(b);
                    break;
                default:
                    buf.append(b);
            }
        }
        buf.append(')');
        return buf;
    }

    /**
     * A helper to FlateDecode.
     * @param in the input data
     * @param strict <CODE>true</CODE> to read a correct stream. <CODE>false</CODE>
     * to try to read a corrupted stream
     * @return the decoded data
     */
    public static byte[] flateDecode(final byte in[], final boolean strict) {
        ByteArrayInputStream stream = new ByteArrayInputStream(in);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte b[] = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                out.write(b, 0, n);
            }
            zip.close();
            out.close();
            return out.toByteArray();
        }
        catch (Exception e) {
            if (strict)
                return null;
            return out.toByteArray();
        }
    }

    /**
     * Decodes a stream that has the FlateDecode filter.
     * @param in the input data
     * @return the decoded data
     */
    public static byte[] flateDecode(final byte in[]) {
        byte b[] = flateDecode(in, true);
        if (b == null)
            return flateDecode(in, false);
        return b;
    }

    public static void transferBytes(InputStream in, java.io.OutputStream out) throws IOException {
        byte[] buffer = new byte[transferSize];
        for (; ; ) {
            int len = in.read(buffer, 0, transferSize);
            if (len > 0)
                out.write(buffer, 0, len);
            else
                break;
        }
    }
}
