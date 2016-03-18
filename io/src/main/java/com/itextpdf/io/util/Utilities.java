package com.itextpdf.io.util;

import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.OutputStream;
import com.itextpdf.io.source.RandomAccessSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
     * @param stream   the {@code InputStream}
     * @param size the number of bytes to skip
     * @throws java.io.IOException
     */
    public static void skip(InputStream stream, int size) throws java.io.IOException {
        long n;
        while (size > 0) {
            n = stream.skip(size);
            if (n <= 0)
                break;
            size -= n;
        }
    }

    /**
     * This method makes a valid URL from a given filename.
     * <p/>
     * This method makes the conversion of this library from the JAVA 2 platform
     * to a JDK1.1.x-version easier.
     *
     * @param filename a given filename
     * @return a valid URL
     * @throws java.net.MalformedURLException
     */
    public static URL toURL(String filename) throws MalformedURLException {
        URL url;
        try {
            url = new URL(filename);
        } catch (MalformedURLException e) {
            url = new File(filename).toURI().toURL();
        }
        return url;
    }

    public static boolean equalsArray(byte ar1[], byte ar2[], int size) {
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
    public static byte[] createEscapedString(byte bytes[]) {
        return createBufferedEscapedString(bytes).toByteArray();
    }

    /**
     * Escapes a {@code byte} array according to the PDF conventions.
     *
     * @param outputStream the {@code OutputStream} an escaped {@code byte} array write to.
     * @param bytes the {@code byte}> array to escape.
     */
    public static void writeEscapedString(OutputStream outputStream, byte[] bytes) {
        ByteBuffer buf = createBufferedEscapedString(bytes);
        outputStream.writeBytes(buf.getInternalBuffer(), 0, buf.size());
    }

    public static void writeHexedString(OutputStream outputStream, byte[] bytes) {
        ByteBuffer buf = createBufferedHexedString(bytes);
        outputStream.writeBytes(buf.getInternalBuffer(), 0, buf.size());
    }

    public static ByteBuffer createBufferedEscapedString(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer(bytes.length * 2 + 2);
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
                    if (b < 8 && b >= 0) {
                        buf.append("\\00").append(Integer.toOctalString(b));
                    } else if (b >= 8 && b < 32) {
                        buf.append("\\0").append(Integer.toOctalString(b));
                    } else {
                        buf.append(b);
                    }
            }
        }
        buf.append(')');
        return buf;
    }

    public static ByteBuffer createBufferedHexedString(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer(bytes.length * 2 + 2);
        buf.append('<');
        for (byte b : bytes) {
            buf.appendHex(b);
        }
        buf.append('>');
        return buf;
    }

    /**
     * A helper to FlateDecode.
     *
     * @param input     the input data
     * @param strict <CODE>true</CODE> to read a correct stream. <CODE>false</CODE>
     *               to try to read a corrupted stream
     * @return the decoded data
     */
    public static byte[] flateDecode(byte[] input, boolean strict) {
        ByteArrayInputStream stream = new ByteArrayInputStream(input);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] b = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                output.write(b, 0, n);
            }
            zip.close();
            output.close();
            return output.toByteArray();
        } catch (Exception e) {
            if (strict)
                return null;
            return output.toByteArray();
        }
    }

    /**
     * Decodes a stream that has the FlateDecode filter.
     *
     * @param input the input data
     * @return the decoded data
     */
    public static byte[] flateDecode(byte[] input) {
        byte[] b = flateDecode(input, true);
        if (b == null)
            return flateDecode(input, false);
        return b;
    }

    public static void transferBytes(InputStream input, java.io.OutputStream output) throws java.io.IOException {
        byte[] buffer = new byte[transferSize];
        for (; ; ) {
            int len = input.read(buffer, 0, transferSize);
            if (len > 0)
                output.write(buffer, 0, len);
            else
                break;
        }
    }

    /**
     * Reads the full content of a stream and returns them in a byte array
     *
     * @param stream the stream to read
     * @return a byte array containing all of the bytes from the stream
     * @throws java.io.IOException if there is a problem reading from the input stream
     */
    public static byte[] inputStreamToArray(InputStream stream) throws java.io.IOException {
        byte b[] = new byte[8192];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int read = stream.read(b);
            if (read < 1)
                break;
            output.write(b, 0, read);
        }
        output.close();
        return output.toByteArray();
    }

    /**
     * Copy bytes from the {@code RandomAccessSource} to {@code OutputStream}.
     *
     * @param source the {@code RandomAccessSource} copy from.
     * @param start  start position of source copy from.
     * @param length length copy to.
     * @param output   the {@code OutputStream} copy to.
     * @throws java.io.IOException on error.
     */
    public static void copyBytes(RandomAccessSource source, long start, long length, java.io.OutputStream output) throws java.io.IOException {
        if (length <= 0)
            return;
        long idx = start;
        byte[] buf = new byte[8192];
        while (length > 0) {
            long n = source.get(idx, buf, 0, (int) Math.min((long) buf.length, length));
            if (n <= 0) {
                throw new EOFException();
            }
            output.write(buf, 0, (int) n);
            idx += n;
            length -= n;
        }
    }

    /**
     * Gets the resource's inputstream.
     *
     * @param key the full name of the resource.
     * @return the {@code InputStream} to get the resource or {@code null} if not found.
     */
    public static InputStream getResourceStream(String key) {
        return getResourceStream(key, null);
    }

    /**
     * Gets the resource's inputstream.
     *
     * @param key    the full name of the resource.
     * @param loader the ClassLoader to load the resource or null to try the ones available.
     * @return the {@code InputStream} to get the resource or {@code null} if not found.
     */
    public static InputStream getResourceStream(String key, ClassLoader loader) {
        if (key.startsWith("/"))
            key = key.substring(1);
        InputStream stream = null;
        if (loader != null) {
            stream = loader.getResourceAsStream(key);
            if (stream != null) {
                return stream;
            }
        }
        // Try to use Context Class Loader to load the properties file.
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                stream = contextClassLoader.getResourceAsStream(key);
            }
        } catch (Throwable e) {
            // empty body
        }

        if (stream == null) {
            stream = Utilities.class.getResourceAsStream("/" + key);
        }
        if (stream == null) {
            stream = ClassLoader.getSystemResourceAsStream(key);
        }
        return stream;
    }

    /**
     * Check if the value of a character belongs to a certain interval
     * that indicates it's the higher part of a surrogate pair.
     *
     * @param c the character
     * @return true if the character belongs to the interval
     */
    public static boolean isSurrogateHigh(char c) {
        return c >= '\ud800' && c <= '\udbff';
    }

    /**
     * Check if the value of a character belongs to a certain interval
     * that indicates it's the lower part of a surrogate pair.
     *
     * @param c the character
     * @return true if the character belongs to the interval
     */
    public static boolean isSurrogateLow(char c) {
        return c >= '\udc00' && c <= '\udfff';
    }

    /**
     * Checks if two subsequent characters in a String are
     * are the higher and the lower character in a surrogate
     * pair (and therefore eligible for conversion to a UTF 32 character).
     *
     * @param text the String with the high and low surrogate characters
     * @param idx  the index of the 'high' character in the pair
     * @return true if the characters are surrogate pairs
     */
    public static boolean isSurrogatePair(String text, int idx) {
        if (idx < 0 || idx > text.length() - 2) {
            return false;
        } else {
            return isSurrogateHigh(text.charAt(idx)) && isSurrogateLow(text.charAt(idx + 1));
        }
    }

    /**
     * Checks if two subsequent characters in a character array are
     * are the higher and the lower character in a surrogate
     * pair (and therefore eligible for conversion to a UTF 32 character).
     *
     * @param text the character array with the high and low surrogate characters
     * @param idx  the index of the 'high' character in the pair
     * @return true if the characters are surrogate pairs
     */
    public static boolean isSurrogatePair(char[] text, int idx) {
        if (idx < 0 || idx > text.length - 2) {
            return false;
        } else {
            return isSurrogateHigh(text[idx]) && isSurrogateLow(text[idx + 1]);
        }
    }

    /**
     * Returns the code point of a UTF32 character corresponding with
     * a high and a low surrogate value.
     *
     * @param highSurrogate the high surrogate value
     * @param lowSurrogate  the low surrogate value
     * @return a code point value
     */
    public static int convertToUtf32(char highSurrogate, char lowSurrogate) {
        return (highSurrogate - 0xd800) * 0x400 + lowSurrogate - 0xdc00 + 0x10000;
    }

    /**
     * Converts a unicode character in a character array to a UTF 32 code point value.
     *
     * @param text a character array that has the unicode character(s)
     * @param idx  the index of the 'high' character
     * @return the code point value
     */
    public static int convertToUtf32(char[] text, int idx) {
        return (text[idx] - 0xd800) * 0x400 + text[idx + 1] - 0xdc00 + 0x10000;
    }

    /**
     * Converts a unicode character in a String to a UTF32 code point value
     *
     * @param text a String that has the unicode character(s)
     * @param idx  the index of the 'high' character
     * @return the codepoint value
     */
    public static int convertToUtf32(String text, int idx) {
        return (text.charAt(idx) - 0xd800) * 0x400 + text.charAt(idx + 1) - 0xdc00 + 0x10000;
    }

    public static int[] convertToUtf32(String text) {
        if (text == null) {
            return null;
        }

        List<Integer> charCodes = new ArrayList<>(text.length());
        int pos = 0;
        while (pos < text.length()) {
            if (isSurrogatePair(text, pos)) {
                charCodes.add(convertToUtf32(text, pos));
                pos += 2;
            } else {
                charCodes.add((int) text.charAt(pos));
                pos++;
            }
        }
        return toArray(charCodes);
    }

    /**
     * Converts a UTF32 code point value to a String with the corresponding character(s).
     *
     * @param codePoint a Unicode value
     * @return the corresponding characters in a String
     */
    public static char[] convertFromUtf32(int codePoint) {
        if (codePoint < 0x10000)
            return new char[]{(char) codePoint};
        codePoint -= 0x10000;
        return new char[]{(char) (codePoint / 0x400 + 0xd800), (char) (codePoint % 0x400 + 0xdc00)};
    }

    /**
     /**
     * Converts a UTF32 code point sequence to a String with the corresponding character(s).
     *
     * @param text a Unicode text sequence
     * @param startPos start position of text to convert, inclusive
     * @param endPos end position of txt to convert, exclusive
     * @return the corresponding characters in a String
     */
    public static String convertFromUtf32(int[] text, int startPos, int endPos) {
        StringBuilder sb = new StringBuilder();
        for (int i = startPos; i < endPos; i++) {
            sb.append(convertFromUtf32ToCharArray(text[i]));
        }
        return sb.toString();
    }

    /**
     * Converts a UTF32 code point value to a char array with the corresponding character(s).
     *
     * @param codePoint a Unicode value
     * @return the corresponding characters in a char arrat
     */
    public static char[] convertFromUtf32ToCharArray(int codePoint) {
        if (codePoint < 0x10000)
            return new char[]{(char) codePoint};
        codePoint -= 0x10000;
        return new char[]{(char) (codePoint / 0x400 + 0xd800), (char) (codePoint % 0x400 + 0xdc00)};
    }

    public static int[] toArray(Collection<Integer> collection) {
        int[] array = new int[collection.size()];
        int k = 0;
        for (Integer key : collection) {
            array[k++] = key;
        }
        return array;
    }

    public static byte[] shortenArray(byte[] src, int length) {
        if (length < src.length) {
            byte[] shortened = new byte[length];
            System.arraycopy(src, 0, shortened, 0, length);
            return shortened;
        }
        return src;
    }
}
