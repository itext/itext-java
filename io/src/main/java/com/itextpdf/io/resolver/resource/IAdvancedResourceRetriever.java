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
package com.itextpdf.io.resolver.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Extended interface for classes that can retrieve data from resources by URL.
 * An extra method with http headers and request data is added.
 */
public interface IAdvancedResourceRetriever extends IResourceRetriever {

    /**
     * Gets the {@link InputStream} with the data from a provided URL by instantiating an HTTP connection to the URL.
     *
     * @param url the source URL
     * @param request data to send to the URL
     * @param headers HTTP headers to set for the outgoing connection
     *
     * @return the input stream with the retrieved data
     *
     * @throws IOException if any input/output issue occurs
     */
    InputStream get(URL url, byte[] request, Map<String, String> headers) throws IOException;
}
