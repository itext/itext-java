/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.validation.mocks;

import com.itextpdf.kernel.validation.context.XrefTableValidationContext;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;

public class MockResourceRetriever implements IResourceRetriever {

    private Function<URL, byte[]> getByteArrayByUrlHandler = u -> null;
    private Function<URL, InputStream> getInputStreamByUrlHandler = u -> null;

    @Override
    public InputStream getInputStreamByUrl(URL url) throws IOException {
        return getInputStreamByUrlHandler.apply(url);
    }

    @Override
    public byte[] getByteArrayByUrl(URL url) throws IOException {
        return getByteArrayByUrlHandler.apply(url);
    }

    public MockResourceRetriever onGetInputStreamByUrl(Function<URL, InputStream> handler) {
        getInputStreamByUrlHandler = handler;
        return this;
    }

    public MockResourceRetriever onGetByteArrayByUrl(Function<URL, byte[]> handler) {
        getByteArrayByUrlHandler = handler;
        return this;
    }
}