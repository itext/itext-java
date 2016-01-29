package com.itextpdf.basics.source;


import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class with commonly used stream operations
 */
public final class StreamUtil {

    private StreamUtil() {
    }

    /**
     * Reads the full content of a stream and returns them in a byte array
     * @param is the stream to read
     * @return a byte array containing all of the bytes from the stream
     * @throws java.io.IOException if there is a problem reading from the input stream
     */
    public static byte[] inputStreamToArray(InputStream is) throws IOException {
        byte b[] = new byte[8192];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int read = is.read(b);
            if (read < 1)
                break;
            out.write(b, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public static void CopyBytes(RandomAccessSource source, long start, long length, java.io.OutputStream outs) throws IOException {
        if (length <= 0)
            return;
        long idx = start;
        byte[] buf = new byte[8192];
        while (length > 0) {
            long n = source.get(idx, buf,0, (int)Math.min((long)buf.length, length));
            if (n <= 0)
                throw new EOFException();
            outs.write(buf, 0, (int)n);
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
        InputStream is = null;
        if (loader != null) {
            is = loader.getResourceAsStream(key);
            if (is != null)
                return is;
        }
        // Try to use Context Class Loader to load the properties file.
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                is = contextClassLoader.getResourceAsStream(key);
            }
        } catch (Throwable e) {
            // empty body
        }

        if (is == null) {
            is = StreamUtil.class.getResourceAsStream("/" + key);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(key);
        }
        return is;
    }
}
