/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.util;

import java.io.InputStream;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
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
        } catch (SecurityException ignored) {
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
