/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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

}
