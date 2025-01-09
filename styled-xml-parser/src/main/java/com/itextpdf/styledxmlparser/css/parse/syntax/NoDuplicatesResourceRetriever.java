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
package com.itextpdf.styledxmlparser.css.parse.syntax;

import com.itextpdf.styledxmlparser.resolver.resource.DefaultResourceRetriever;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DefaultResourceRetriever} which returns {@code null} if the {@code URL}
 * from {@link #getInputStreamByUrl(URL)} has been already processed by the current instance.
 */
class NoDuplicatesResourceRetriever extends DefaultResourceRetriever {
    private final Set<String> processedUrls = new HashSet<>();

    /**
     * Creates a new {@link NoDuplicatesResourceRetriever} instance.
     */
    NoDuplicatesResourceRetriever() {
        super();
    }

    @Override
    public InputStream getInputStreamByUrl(URL url) throws IOException {
        if (processedUrls.contains(url.toExternalForm())) {
            return null;
        }
        processedUrls.add(url.toExternalForm());
        return super.getInputStreamByUrl(url);
    }
}
