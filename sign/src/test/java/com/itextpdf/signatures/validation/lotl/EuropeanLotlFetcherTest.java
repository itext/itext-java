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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
class EuropeanLotlFetcherTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER_LOTL = "./src/test/resources/com/itextpdf/signatures/validation" +
            "/lotl/LotlState2025_08_08/";

    @Test
    public void simpleTestFetchesLotlCorrectly() {
        EuropeanLotlFetcher fetcher;
        try (LotlService service = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL));
            fetcher = new EuropeanLotlFetcher(service);
        }

        assertNotNull(fetcher);
        EuropeanLotlFetcher.Result result = fetcher.fetch();
        assertNotNull(result);
        String xmlString = new String(result.getLotlXml(), StandardCharsets.UTF_8);
        assertTrue(xmlString.contains("<X509Certificate>"));
    }

    @Test
    public void loadReloadsTheLotl() {
        EuropeanLotlFetcher fetcher;
        try (LotlService service = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {
            fetcher = new EuropeanLotlFetcher(service);
        }
        EuropeanLotlFetcher.Result result = fetcher.fetch();
        assertNotNull(result);
        EuropeanLotlFetcher.Result result2 = fetcher.fetch();
        assertNotNull(result2);

    }

    @Test
    public void dummyRetrieverCausesException() {
        EuropeanLotlFetcher fetcher;
        try (LotlService service = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))
                .withCustomResourceRetriever(
                        new IResourceRetriever() {
                            @Override
                            public InputStream getInputStreamByUrl(URL url) {
                                return null;
                            }

                            @Override
                            public byte[] getByteArrayByUrl(URL url) {
                                return null;
                            }
                        })) {

            fetcher = new EuropeanLotlFetcher(service);
        }
        EuropeanLotlFetcher.Result result = fetcher.fetch();
        assertNotNull(result);
        assertNull(result.getLotlXml());
        assertFalse(result.getLocalReport().getLogs().isEmpty());
    }
}
