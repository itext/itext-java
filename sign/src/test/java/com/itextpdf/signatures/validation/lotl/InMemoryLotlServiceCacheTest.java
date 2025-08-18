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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("UnitTest")
public class InMemoryLotlServiceCacheTest extends ExtendedITextTest {


    @Test
    public void setByteDataWorks() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        byte[] data = "test data".getBytes(StandardCharsets.UTF_8);
        EuropeanLotlFetcher.Result result = new EuropeanLotlFetcher.Result(data);

        cache.setLotlResult(result);
        assertArrayEquals(data, cache.getLotlResult().getLotlXml(), "The byte data should match the set data.");
    }

    @Test
    public void setNullByteDataWorks() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        cache.setLotlResult(null);
        assertNull(cache.getLotlResult(), "The byte data should be null after setting it to null.");
    }

    @Test
    public void setByteDataStaleDataThrowsException() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        byte[] data = "test data".getBytes(StandardCharsets.UTF_8);
        EuropeanLotlFetcher.Result result = new EuropeanLotlFetcher.Result(data);
        cache.setLotlResult(result);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);
        Assertions.assertThrows(PdfException.class, () -> {
            cache.getLotlResult();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
    }


    @Test
    public void cacheInvalidationWorks() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        byte[] data = "test data".getBytes(StandardCharsets.UTF_8);
        EuropeanLotlFetcher.Result result = new EuropeanLotlFetcher.Result(data);
        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);

        cache.setLotlResult(result);
        assertArrayEquals(data, cache.getLotlResult().getLotlXml(), "The byte data should match the set data.");
    }

    @Test
    public void setCountrySpecificLotlCacheWorks() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        CountrySpecificLotlFetcher.Result result = new CountrySpecificLotlFetcher.Result();
        result.setContexts(new ArrayList<>());

        CountrySpecificLotl f = new CountrySpecificLotl("BE",
                "https://example.be/lotl.xml", "application/xml");
        result.setCountrySpecificLotl(f);

        final String cacheId = result.createUniqueIdentifier();

        cache.setCountrySpecificLotlResult(result);
        Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache = cache.getCountrySpecificLotls();
        Assertions.assertTrue(countrySpecificLotlCache.containsKey(cacheId),
                "The cache should contain the country-specific Lotl entry.");
    }

    @Test
    public void getCountrySpecificLotlReturnsEmptyMapWhenNoEntries() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache = cache.getCountrySpecificLotls();
        Assertions.assertTrue(countrySpecificLotlCache.isEmpty(),
                "The cache should be empty when no country-specific Lotl entries are set.");
    }

    @Test
    public void getCountrySpecificCacheWithStaleDataThrowsException() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        CountrySpecificLotlFetcher.Result result = new CountrySpecificLotlFetcher.Result();
        result.setContexts(new ArrayList<>());

        CountrySpecificLotl f = new CountrySpecificLotl("BE",
                "https://example.be/lotl.xml", "application/xml");
        result.setCountrySpecificLotl(f);

        cache.setCountrySpecificLotlResult(result);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);

        Assertions.assertThrows(PdfException.class, () -> {
            cache.getCountrySpecificLotls();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
    }

    @Test
    public void europeanResultUpdatedDoesNotThrowException() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        CountrySpecificLotlFetcher.Result result = new CountrySpecificLotlFetcher.Result();
        result.setContexts(new ArrayList<>());

        CountrySpecificLotl f = new CountrySpecificLotl("BE",
                "https://example.be/lotl.xml", "application/xml");
        result.setCountrySpecificLotl(f);

        cache.setCountrySpecificLotlResult(result);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);
        cache.setCountrySpecificLotlResult(result);
        AssertUtil.doesNotThrow(() -> {

            cache.getCountrySpecificLotls();
        });
    }

    @Test
    public void pivotFilesCacheWorks() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        PivotFetcher.Result result = new PivotFetcher.Result();
        result.setPivotUrls(Arrays.asList("https://example.com/pivot1.xml", "https://example.com/pivot2.xml"));
        cache.setPivotResult(result);
        Assertions.assertNotNull(cache.getPivotResult(), "The pivot files should not be null after setting them.");
    }

    @Test
    public void pivotFilesStaleThrowsException() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        PivotFetcher.Result result = new PivotFetcher.Result();
        result.setPivotUrls(Arrays.asList("https://example.com/pivot1.xml", "https://example.com/pivot2.xml"));
        cache.setPivotResult(result);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);

        Assertions.assertThrows(PdfException.class, () -> {
            cache.getPivotResult();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
    }

    @Test
    public void europeanResultCacheWorks() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        EuropeanResourceFetcher.Result result = new EuropeanResourceFetcher.Result();
        result.setCertificates(new ArrayList<>());

        cache.setEuropeanResourceFetcherResult(result);

        Assertions.assertNotNull(cache.getEUJournalCertificates(),
                "The European result should not be null after setting it.");
    }

    @Test
    public void europeanResultCacheStaleDataThrowsException() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        EuropeanResourceFetcher.Result result = new EuropeanResourceFetcher.Result();
        result.setCertificates(new ArrayList<>());

        cache.setEuropeanResourceFetcherResult(result);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);

        Assertions.assertThrows(PdfException.class, () -> {
            cache.getEUJournalCertificates();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
    }


    @Test
    public void europeanResultCacheStaleDataDoesNotThrowExceptionAfterUpdate() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());
        EuropeanResourceFetcher.Result result = new EuropeanResourceFetcher.Result();
        result.setCertificates(new ArrayList<>());

        cache.setEuropeanResourceFetcherResult(result);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);

        cache.setEuropeanResourceFetcherResult(result);

        AssertUtil.doesNotThrow(() -> {
            cache.getEUJournalCertificates();
        });
    }

    @Test
    public void setAllData() {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(1000, new ThrowExceptionOnFailingCountryData());
        byte[] lotlData = "lotl data".getBytes(StandardCharsets.UTF_8);
        EuropeanLotlFetcher.Result lotlResult = new EuropeanLotlFetcher.Result(lotlData);

        CountrySpecificLotlFetcher.Result countryResult = new CountrySpecificLotlFetcher.Result();
        countryResult.setContexts(new ArrayList<>());
        CountrySpecificLotl f = new CountrySpecificLotl("BE",
                "https://example.be/lotl.xml", "application/xml");
        countryResult.setCountrySpecificLotl(f);

        EuropeanResourceFetcher.Result europeanResult = new EuropeanResourceFetcher.Result();
        europeanResult.setCertificates(new ArrayList<>());

        PivotFetcher.Result pivotResult = new PivotFetcher.Result();
        pivotResult.setPivotUrls(Arrays.asList("https://example.com/pivot1.xml", "https://example.com/pivot2.xml"));

        HashMap<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache = new HashMap<>();
        countrySpecificLotlCache.put(countryResult.createUniqueIdentifier(), countryResult);
        cache.setAllValues(lotlResult, europeanResult, pivotResult, countrySpecificLotlCache);

        Assertions.assertArrayEquals(lotlData, cache.getLotlResult().getLotlXml(),
                "The Lotl data should match the set data.");
        Assertions.assertFalse(cache.getCountrySpecificLotls().isEmpty(),
                "The country-specific Lotl cache should not be empty.");
        Assertions.assertNotNull(cache.getEUJournalCertificates(), "The European result should not be null.");
        Assertions.assertNotNull(cache.getPivotResult(), "The pivot result should not be null.");

    }

    @Test
    public void setAllDataAfterStaleNessThrowsException() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());

        byte[] lotlData = "lotl data".getBytes(StandardCharsets.UTF_8);
        EuropeanLotlFetcher.Result lotlResult = new EuropeanLotlFetcher.Result(lotlData);

        CountrySpecificLotlFetcher.Result countryResult = new CountrySpecificLotlFetcher.Result();
        countryResult.setContexts(new ArrayList<>());
        CountrySpecificLotl f = new CountrySpecificLotl("BE",
                "https://example.be/lotl.xml", "application/xml");
        countryResult.setCountrySpecificLotl(f);

        EuropeanResourceFetcher.Result europeanResult = new EuropeanResourceFetcher.Result();
        europeanResult.setCertificates(new ArrayList<>());

        PivotFetcher.Result pivotResult = new PivotFetcher.Result();
        pivotResult.setPivotUrls(Arrays.asList("https://example.com/pivot1.xml", "https://example.com/pivot2.xml"));

        HashMap<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache = new HashMap<>();
        countrySpecificLotlCache.put(countryResult.createUniqueIdentifier(), countryResult);
        cache.setAllValues(lotlResult, europeanResult, pivotResult, countrySpecificLotlCache);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);
        Assertions.assertThrows(PdfException.class, () -> {
            cache.getLotlResult();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);

        Assertions.assertThrows(PdfException.class, () -> {
            cache.getEUJournalCertificates();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
        Assertions.assertThrows(PdfException.class, () -> {
            cache.getPivotResult();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
        Assertions.assertThrows(PdfException.class, () -> {
            cache.getCountrySpecificLotls();
        }, SignExceptionMessageConstant.STALE_DATA_IS_USED);
    }


    @Test
    public void setAllDataResetAfterStalenessWorks() throws InterruptedException {
        InMemoryLotlServiceCache cache = new InMemoryLotlServiceCache(200, new ThrowExceptionOnFailingCountryData());

        byte[] lotlData = "lotl data".getBytes(StandardCharsets.UTF_8);

        EuropeanLotlFetcher.Result lotlResult = new EuropeanLotlFetcher.Result(lotlData);

        CountrySpecificLotlFetcher.Result countryResult = new CountrySpecificLotlFetcher.Result();
        countryResult.setContexts(new ArrayList<>());
        CountrySpecificLotl f = new CountrySpecificLotl("BE",
                "https://example.be/lotl.xml", "application/xml");
        countryResult.setCountrySpecificLotl(f);

        EuropeanResourceFetcher.Result europeanResult = new EuropeanResourceFetcher.Result();
        europeanResult.setCertificates(new ArrayList<>());

        PivotFetcher.Result pivotResult = new PivotFetcher.Result();
        pivotResult.setPivotUrls(Arrays.asList("https://example.com/pivot1.xml", "https://example.com/pivot2.xml"));

        HashMap<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache = new HashMap<>();
        countrySpecificLotlCache.put(countryResult.createUniqueIdentifier(), countryResult);
        cache.setAllValues(lotlResult, europeanResult, pivotResult, countrySpecificLotlCache);

        // Simulate staleness by waiting longer than the max allowed staleness
        Thread.sleep(500);
        cache.setAllValues(lotlResult, europeanResult, pivotResult, countrySpecificLotlCache);

        AssertUtil.doesNotThrow(() -> {
            cache.getLotlResult();
            cache.getEUJournalCertificates();
            cache.getPivotResult();
            cache.getCountrySpecificLotls();
        }, "After updating the cache, it should not throw an exception for stale data.");

    }
}