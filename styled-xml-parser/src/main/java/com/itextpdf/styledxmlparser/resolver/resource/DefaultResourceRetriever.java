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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.styledxmlparser.exceptions.ReadingByteLimitException;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Default implementation of the {@link IResourceRetriever} interface, which can set a limit
 * on the size of retrieved resources using input stream with a limit on the number of bytes read.
 * @deprecated In favor of {@link com.itextpdf.io.resolver.resource.DefaultResourceRetriever}
 */
@Deprecated
public class DefaultResourceRetriever implements IResourceRetriever{
    private static final Logger logger = LoggerFactory.getLogger(DefaultResourceRetriever.class);

    private com.itextpdf.io.resolver.resource.DefaultResourceRetriever proxy;
    /**
     * Creates a new {@link DefaultResourceRetriever} instance.
     * The limit on the size of retrieved resources is by default equal to {@link Long#MAX_VALUE} bytes.
     */
    public DefaultResourceRetriever() {
        // empty constructor
        this.proxy = new com.itextpdf.io.resolver.resource.DefaultResourceRetriever();
    }

    /**
     * Gets the resource size byte limit.
     *
     * The resourceSizeByteLimit is used to create input stream with a limit on the number of bytes read.
     *
     * @return the resource size byte limit
     */
    public long getResourceSizeByteLimit() {
        return proxy.getResourceSizeByteLimit();
    }

    /**
     * Sets the resource size byte limit.
     *
     * The resourceSizeByteLimit is used to create input stream with a limit on the number of bytes read.
     *
     * @param resourceSizeByteLimit the resource size byte limit
     * @return the {@link IResourceRetriever} instance
     */
    public IResourceRetriever setResourceSizeByteLimit(long resourceSizeByteLimit) {
         proxy.setResourceSizeByteLimit(resourceSizeByteLimit);
        return this;
    }

    /**
     * Gets the connect timeout.
     *
     * The connect timeout is used to create input stream with a limited time to establish connection to resource.
     *
     * @return the connect timeout in milliseconds
     */
    public int getConnectTimeout() {
        return proxy.getConnectTimeout();
    }

    /**
     * Sets the connect timeout.
     *
     * The connect timeout is used to create input stream with a limited time to establish connection to resource.
     *
     * @param connectTimeout the connect timeout in milliseconds
     *
     * @return the {@link IResourceRetriever} instance
     */
    public IResourceRetriever setConnectTimeout(int connectTimeout) {
        proxy.setConnectTimeout(connectTimeout);
        return this;
    }

    /**
     * Gets the read timeout.
     *
     * The read timeout is used to create input stream with a limited time to receive data from resource.
     *
     * @return the read timeout in milliseconds
     */
    public int getReadTimeout() {
        return proxy.getReadTimeout();
    }

    /**
     * Sets the read timeout.
     *
     * The read timeout is used to create input stream with a limited time to receive data from resource.
     *
     * @param readTimeout the read timeout in milliseconds
     *
     * @return the {@link IResourceRetriever} instance
     */
    public IResourceRetriever setReadTimeout(int readTimeout) {
        proxy.setReadTimeout(readTimeout);
        return this;
    }

    /**
     * Gets the input stream with current limit on the number of bytes read,
     * that connect with source URL for retrieving data from that connection.
     *
     * @param url the source URL
     * @return the limited input stream or null if the URL was filtered
     */
    public InputStream getInputStreamByUrl(URL url) throws IOException {
        if (!urlFilter(url)) {
            logger.warn(
                    MessageFormatUtil.format(StyledXmlParserLogMessageConstant.RESOURCE_WITH_GIVEN_URL_WAS_FILTERED_OUT,
                            url));
            return null;
        }
        return new LimitedInputStream(UrlUtil.getInputStreamOfFinalConnection(url, proxy.getConnectTimeout(),
                proxy.getReadTimeout()),
                proxy.getResourceSizeByteLimit());
    }

    /**
     * Gets the byte array that are retrieved from the source URL.
     *
     * @param url the source URL
     * @return the byte array or null if the retrieving failed or the
     * URL was filtered or the resourceSizeByteLimit was violated
     */
    public byte[] getByteArrayByUrl(URL url) throws IOException {
        try (InputStream stream = getInputStreamByUrl(url)){
            if (stream == null) {
                return null;
            }

            return StreamUtil.inputStreamToArray(stream);
        } catch (ReadingByteLimitException ex) {
            logger.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_RESOURCE_WITH_GIVEN_RESOURCE_SIZE_BYTE_LIMIT,
                    url, proxy.getResourceSizeByteLimit()));
        }
        return null;
    }

    /**
     * Method for filtering resources by URL.
     *
     * The default implementation allows for all URLs. Override this method if want to set filtering.
     *
     * @param url the source URL
     * @return true if the resource can be retrieved and false otherwise
     */
    protected boolean urlFilter(URL url) {
        return true;
    }
}
