package com.itextpdf.io.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class UrlUtil {

    private UrlUtil() {
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
}
