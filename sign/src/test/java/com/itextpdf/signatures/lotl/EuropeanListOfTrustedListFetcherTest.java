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