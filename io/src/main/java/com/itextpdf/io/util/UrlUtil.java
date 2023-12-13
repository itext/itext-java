/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class UrlUtil {

    private UrlUtil() {
    }

    /**
     * This method makes a valid URL from a given filename.
     * <p>
     * This method makes the conversion of this library from the JAVA 2 platform
     * to a JDK1.1.x-version easier.
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

    /**
     * This method makes a normalized URI from a given filename.
     * @param filename a given filename
     * @return a valid URI
     */
    public static URI toNormalizedURI(String filename) {
        return toNormalizedURI(new File(filename));
    }

    /**
     * This method makes a normalized URI from a given file.
     * @param file a given filename
     * @return a valid URI
     */
    public static URI toNormalizedURI(File file) {
        return file.toURI().normalize();
    }

    public static InputStream openStream(URL url) throws IOException {
        return url.openStream();
    }

    /**
     * This method gets the last redirected url.
     * @param initialUrl an initial URL
     * @return the last redirected url
     * @throws IOException
     */
    public static URL getFinalURL(URL initialUrl) throws IOException {
        URL finalUrl = null;
        URL nextUrl = initialUrl;
        while (nextUrl != null) {
            finalUrl = nextUrl;
            URLConnection connection = finalUrl.openConnection();
            String location = connection.getHeaderField("location");
            // Close input stream deliberately to close the handle which is created during getHeaderField invocation
            connection.getInputStream().close();
            nextUrl = location != null ? new URL(location) : null;
        }
        return finalUrl;
    }

    /**
     * This method gets uri string from a file.
     * @param filename a given filename
     * @return a uri string
     */
    public static String getFileUriString(String filename) throws MalformedURLException {
        return new File(filename).toURI().toURL().toExternalForm();
    }

    /**
     * This method gets normalized uri string from a file.
     * @param filename a given filename
     * @return a normalized uri string
     */
    public static String getNormalizedFileUriString(String filename) {
        return "file://" + UrlUtil.toNormalizedURI(filename).getPath();
    }
}
