package com.itextpdf.io.source;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;

/**
 * Utility class with commonly used stream operations
 */
public final class StreamUtil {

    private StreamUtil() {
    }

    /**
     * Reads the full content of a stream and returns them in a byte array
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

    public static void CopyBytes(RandomAccessSource source, long start, long length, java.io.OutputStream output) throws java.io.IOException {
        if (length <= 0)
            return;
        long idx = start;
        byte[] buf = new byte[8192];
        while (length > 0) {
            long n = source.get(idx, buf,0, (int)Math.min((long)buf.length, length));
            if (n <= 0)
                throw new EOFException();
            output.write(buf, 0, (int)n);
            idx += n;
            length -= n;
        }
    }

    /**
     * Gets the resource's inputstream.
     * @param key the full name of the resource
     * @return the {@code InputStream} to get the resource or {@code null} if not found
     */
    public static InputStream getResourceStream(String key) {
        return getResourceStream(key, null);
    }

    /**
     * Gets the resource's inputstream
     * .
     * @param key the full name of the resource
     * @param loader the ClassLoader to load the resource or null to try the ones available
     * @return the {@code InputStream} to get the resource or {@code null} if not found
     */
    public static InputStream getResourceStream(String key, ClassLoader loader) {
        if (key.startsWith("/"))
            key = key.substring(1);
        InputStream stream = null;
        if (loader != null) {
            stream = loader.getResourceAsStream(key);
            if (stream != null)
                return stream;
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
            stream = StreamUtil.class.getResourceAsStream("/" + key);
        }
        if (stream == null) {
            stream = ClassLoader.getSystemResourceAsStream(key);
        }
        return stream;
    }
}
