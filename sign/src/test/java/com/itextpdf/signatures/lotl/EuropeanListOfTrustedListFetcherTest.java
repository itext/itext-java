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
package com.itextpdf.signatures.lotl;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
class EuropeanListOfTrustedListFetcherTest extends ExtendedITextTest {


    @Test
    public void simpleTestFetchesLotlCorrectly() throws IOException {
        EuropeanListOfTrustedListFetcher fetcher = new EuropeanListOfTrustedListFetcher(new DefaultResourceRetriever());
        assertNotNull(fetcher);
        byte[] xml = fetcher.getLotlData();
        assertNotNull(xml);
        String xmlString = new String(xml, StandardCharsets.UTF_8);
        assertTrue(xmlString.contains("<X509Certificate>"));
        Date lastLoaded = fetcher.getLastLoaded();

        assertNotNull(lastLoaded);

        byte[] xml2 = fetcher.getLotlData();
        assertNotNull(xml2);

    }


    @Test
    public void loadReloadsTheLotl() throws IOException {
        EuropeanListOfTrustedListFetcher fetcher = new EuropeanListOfTrustedListFetcher(new DefaultResourceRetriever());
        byte[] xml = fetcher.getLotlData();
        assertNotNull(xml);
        Date lastLoaded = fetcher.getLastLoaded();
        fetcher.load();

        byte[] xml2 = fetcher.getLotlData();
        assertNotNull(xml2);

    }

    @Test
    public void dummmyRetrieverCausesException() {
        EuropeanListOfTrustedListFetcher fetcher = new EuropeanListOfTrustedListFetcher(new IResourceRetriever() {
            @Override
            public InputStream getInputStreamByUrl(URL url) {
                return null;
            }

            @Override
            public byte[] getByteArrayByUrl(URL url) {
                return null;
            }
        });
        assertThrows(ITextException.class, () -> fetcher.getLotlData());
    }
}