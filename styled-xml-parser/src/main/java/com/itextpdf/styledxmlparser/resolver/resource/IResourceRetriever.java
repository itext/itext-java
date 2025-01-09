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

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

/**
 * Interface for classes that can retrieve data from resources by URL.
 */
public interface IResourceRetriever {
    /**
     * Gets the {@link InputStream} that connect with source URL for retrieving data from that connection.
     *
     * @param url the source URL
     * @return the input stream or null if the retrieving failed
     * @throws IOException if any input/output issue occurs
     */
    InputStream getInputStreamByUrl(URL url) throws IOException;

    /**
     * Gets the byte array that are retrieved from the source URL.
     *
     * @param url the source URL
     * @return the byte array or null if the retrieving failed
     * @throws IOException if any input/output issue occurs
     */
    byte[] getByteArrayByUrl(URL url) throws IOException;
}
