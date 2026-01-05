/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class UrlUtil {
    private static final int DEFAULT_CONNECT_TIMEOUT = 300000;
    private static final int DEFAULT_READ_TIMEOUT = 300000;

    private UrlUtil() {
    }

    /**
     * This method makes a valid URL from a given filename.
     * <p>
     * This method makes the conversion of this library from the JAVA 2 platform
     * to a JDK1.1.x-version easier.
     *
     * @param filename a given filename
     *
     * @return a valid URL
     *
     * @throws java.net.MalformedURLException  If a protocol handler for the URL could not be found,
     * or if some other error occurred while constructing the URL
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
     *
     * @param filename a given filename
     *
     * @return a valid URI
     */
    public static URI toNormalizedURI(String filename) {
        return toNormalizedURI(new File(filename));
    }

    /**
     * This method makes a normalized URI from a given file.
     *
     * @param file a given filename
     *
     * @return a valid URI
     */
    public static URI toNormalizedURI(File file) {
        return file.toURI().normalize();
    }

    /**
     * Get the entire URI string which is properly encoded.
     *
     * @param uri URI which convert to string
     *
     * @return URI string representation
     */
    public static String toAbsoluteURI(URI uri) {
        return uri.toString();
    }

    public static InputStream openStream(URL url) throws IOException {
        return url.openStream();
    }

    /**
     * This method gets uri string from a file.
     *
     * @param filename a given filename
     *
     * @return a uri string
     *
     * @throws MalformedURLException  If a protocol handler for the URL could not be found,
     * or if some other error occurred while constructing the URL
     */
    public static String getFileUriString(String filename) throws MalformedURLException {
        return new File(filename).toURI().toURL().toExternalForm();
    }

    /**
     * This method gets normalized uri string from a file.
     *
     * @param filename a given filename
     *
     * @return a normalized uri string
     */
    public static String getNormalizedFileUriString(String filename) {
        return "file://" + UrlUtil.toNormalizedURI(filename).getPath();
    }

    /**
     * Gets the input stream of connection related to last redirected url. You should manually close input stream after
     * calling this method to not hold any open resources.
     *
     * @param initialUrl an initial URL
     *
     * @return an input stream of connection related to the last redirected url
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public static InputStream getInputStreamOfFinalConnection(URL initialUrl) throws IOException {
        return getInputStreamOfFinalConnection(initialUrl, DEFAULT_CONNECT_TIMEOUT,
                DEFAULT_READ_TIMEOUT);
    }

    /**
     * Gets the input stream of connection related to last redirected url. You should manually close input stream after
     * calling this method to not hold any open resources.
     *
     * @param initialUrl an initial URL
     * @param connectTimeout a connect timeout in milliseconds
     * @param readTimeout a read timeout in milliseconds
     *
     * @return an input stream of connection related to the last redirected url
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public static InputStream getInputStreamOfFinalConnection(URL initialUrl, int connectTimeout, int readTimeout)
            throws IOException {
        return getInputStreamOfFinalConnection(initialUrl, connectTimeout, readTimeout, null);
    }

    /**
     * Gets the input stream of connection related to last redirected url. You should manually close input stream after
     * calling this method to not hold any open resources.
     *
     * @param initialUrl an initial URL
     * @param connectTimeout a connect timeout in milliseconds
     * @param readTimeout a read timeout in milliseconds
     * @param sslContext {@link SSLContext} to configure ssl connection
     *
     * @return an input stream of connection related to the last redirected url
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public static InputStream getInputStreamOfFinalConnection(URL initialUrl, int connectTimeout, int readTimeout,
                                                              SSLContext sslContext) throws IOException {
        final URLConnection finalConnection = getFinalConnection(initialUrl, connectTimeout, readTimeout, sslContext);
        return finalConnection.getInputStream();
    }

    /**
     * Gets the {@link InputStream} with the data from a provided URL by instantiating an HTTP connection to the URL.
     *
     * @param url a URL to connect to
     * @param request data to send to the URL
     * @param headers HTTP headers to set for the outgoing connection
     * @param connectTimeout a connect timeout in milliseconds
     * @param readTimeout a read timeout in milliseconds
     *
     * @return the input stream with the retrieved data
     *
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static InputStream get(URL url, byte[] request, Map<String, String> headers,
            int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        connection.setDoOutput(true);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        OutputStream out = connection.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.write(request);
        dataOut.flush();
        dataOut.close();
        if (connection.getResponseCode() / 100 != 2) {
            throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.INVALID_HTTP_RESPONSE)
                    .setMessageParams(connection.getResponseCode());
        }
        return connection.getInputStream();
    }

    /**
     * Gets the connection related to the last redirected url. You should close connection manually after calling
     * this method, to not hold any open resources.
     *
     * @param initialUrl an initial URL
     * @param connectTimeout a connect timeout in milliseconds
     * @param readTimeout a read timeout in milliseconds
     *
     * @return connection related to the last redirected url
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    static URLConnection getFinalConnection(URL initialUrl, int connectTimeout, int readTimeout,
                                            SSLContext sslContext) throws IOException {
        URL nextUrl = initialUrl;
        URLConnection connection = null;
        while (nextUrl != null) {
            connection = nextUrl.openConnection();
            if (sslContext != null && connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
            }
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            final String location = connection.getHeaderField("location");
            nextUrl = location == null ? null : new URL(location);
            if (nextUrl != null) {
                // close input stream deliberately to close the handle which is created during getHeaderField invocation
                connection.getInputStream().close();
            }
        }
        return connection;
    }
}
