package com.itextpdf.io.util;

import java.io.InputStream;

public final class ResourceUtil {

    private ResourceUtil() {
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
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
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
            stream = ResourceUtil.class.getResourceAsStream("/" + key);
        }
        if (stream == null) {
            stream = ClassLoader.getSystemResourceAsStream(key);
        }
        return stream;
    }
}
