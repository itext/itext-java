package com.itextpdf.basics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Utilities {

    /**
     * This method is an alternative for the <CODE>InputStream.skip()</CODE>
     * -method that doesn't seem to work properly for big values of <CODE>size
     * </CODE>.
     *
     * @param is   the <CODE>InputStream</CODE>
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
        URL url = null;
        try {
            url = new URL(filename);
        } catch (MalformedURLException e) {
            url = new File(filename).toURI().toURL();
        }
        return url;
    }


}
