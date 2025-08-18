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
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.lotl.EuropeanLotlFetcher.Result;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("IntegrationTest")
public class LotlServiceTest extends ExtendedITextTest {

    @Test
    public void refreshCalculatorZeroThrowsException() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        Exception e = Assertions.assertThrows(PdfException.class, () -> properties.setCacheStalenessInMilliseconds(0L));
        assertEquals(SignExceptionMessageConstant.STALENESS_MUST_BE_POSITIVE, e.getMessage());
    }

    @Test
    public void testWithPivotFetcher() {
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData())
                .setCountryNames("NL"))) {
            AssertUtil.doesNotThrow(() -> lotlService.withPivotFetcher(new PivotFetcher(lotlService)));
        }
    }

    @Test
    public void testCacheFailCallsDownloadOfMainLotlFile() {
        Result f;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {

            lotlService.withLotlServiceCache(new CacheReturnsNull());
            lotlService.withEuropeanLotlFetcher(
                    new EuropeanLotlFetcher(lotlService) {
                        @Override
                        public Result fetch() {
                            return new Result("abc".getBytes(StandardCharsets.UTF_8));
                        }
                    }
            );

            f = lotlService.getLotlBytes();
        }
        Assertions.assertNotNull(f);
        Assertions.assertArrayEquals("abc".getBytes(StandardCharsets.UTF_8), f.getLotlXml());

    }

    @Test
    public void testCacheFailCallsDownloadOfEUJournalCertificates() {
        EuropeanResourceFetcher.Result f;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {

            lotlService.withLotlServiceCache(new CacheReturnsNull());

            lotlService.withEuropeanResourceFetcher(
                    new EuropeanResourceFetcher() {
                        @Override
                        public Result getEUJournalCertificates() {
                            Result result = new Result();
                            result.setCertificates(Collections.<Certificate>emptyList());
                            return result;
                        }
                    }
            );
            f = lotlService.getEUJournalCertificates();
        }
        Assertions.assertNotNull(f);
        Assertions.assertTrue(f.getCertificates().isEmpty());
    }

    @Test
    public void testCacheFailCallsDownloadOfPivotFile() {
        PivotFetcher.Result f;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {

            lotlService.withLotlServiceCache(new CacheReturnsNull());

            lotlService.withPivotFetcher(
                    new PivotFetcher(lotlService) {

                        @Override
                        public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates) {
                            Result result = new Result();
                            result.setPivotUrls(Collections.singletonList("http://example.com/pivot.xml"));
                            return result;
                        }
                    }
            );
            f = lotlService.getAndValidatePivotFiles("abc".getBytes(StandardCharsets.UTF_8),
                    Collections.<Certificate>emptyList());
        }
        Assertions.assertNotNull(f);
        assertEquals(1, f.getPivotUrls().size());
    }


    @Test
    public void testCacheFailCallsDownloadOfCountrySpecificLotl() {
        List<CountrySpecificLotlFetcher.Result> f;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {

            lotlService.withLotlServiceCache(new CacheReturnsNull());

            lotlService.withCountrySpecificLotlFetcher(
                    new CountrySpecificLotlFetcher(lotlService) {
                        @Override
                        public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                                LotlService lotlService) {
                            HashMap<String, Result> resultMap = new HashMap<>();
                            Result result = new Result();
                            result.setCountrySpecificLotl(
                                    new CountrySpecificLotl("NL", "http://example.com/lotl.xml",
                                            "application/xml"));
                            resultMap.put(result.createUniqueIdentifier(), result);
                            return resultMap;
                        }
                    }
            );
            f = lotlService.getCountrySpecificLotlFiles(null);
        }
        Assertions.assertNotNull(f);
        assertEquals(1, f.size());
        assertEquals("NL", f.get(0).getCountrySpecificLotl().getSchemeTerritory());
        assertEquals("http://example.com/lotl.xml", f.get(0).getCountrySpecificLotl().getTslLocation());
    }


    @Test
    public void testCacheRefreshIsFiringButWaitsDelay() throws InterruptedException {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        lotlFetchingProperties.setRefreshIntervalCalculator(l -> 500);

        AtomicLong refreshCounter = new AtomicLong(0);
        try (LotlService lotlService = new LotlService(lotlFetchingProperties) {
            @Override
            public void initializeCache() {
            }

            @Override
            protected void tryAndRefreshCache() {
                refreshCounter.incrementAndGet();
            }
        }) {
        }
        Thread.sleep(50);
        assertEquals(0, refreshCounter.get(),
                "Refresh counter should be greater than 8, but was: " + refreshCounter.get());
    }


    @Test
    public void testCacheRefreshIsFiring() throws InterruptedException {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        lotlFetchingProperties.setRefreshIntervalCalculator(
                (l) -> {
                    //100 milliseconds
                    return 100;
                }
        );
        AtomicLong refreshCounter = new AtomicLong(0);
        try (LotlService lotlService = new LotlService(lotlFetchingProperties) {
            @Override
            public void initializeCache() {
            }

            @Override
            protected void tryAndRefreshCache() {
                refreshCounter.incrementAndGet();
            }
        }) {
            lotlService.setupTimer();
            Thread.sleep(1000);
        }
        Assertions.assertTrue(refreshCounter.get() >= 8,
                "Refresh counter should be greater than 8, but was: " + refreshCounter.get());
    }

    @Test
    public void withCustomRefreshRate() throws InterruptedException {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        AtomicLong refreshCounter = new AtomicLong(0);
        lotlFetchingProperties.setRefreshIntervalCalculator(
                (l) -> {
                    //100 milliseconds
                    return 100;
                }
        );
        try (LotlService lotlService = new LotlService(lotlFetchingProperties) {
            @Override
            public void initializeCache() {
            }

            @Override
            protected void tryAndRefreshCache() {
                refreshCounter.incrementAndGet();
            }
        }) {
            lotlService.setupTimer();
            Thread.sleep(1000);
        }
        Assertions.assertTrue(refreshCounter.get() > 5,
                "Refresh counter should be greater than 10, but was: " + refreshCounter.get());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES))
    public void euJournalInvalidOnRefreshTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            EuropeanResourceFetcher europeanResourceFetcher = new EuropeanResourceFetcher() {
                @Override
                public Result getEUJournalCertificates() {
                    Result result = super.getEUJournalCertificates();
                    result.getLocalReport()
                            .addReportItem(new ReportItem("check", "test failure", ReportItemStatus.INVALID));
                    return result;
                }
            };
            service.withEuropeanResourceFetcher(europeanResourceFetcher);
            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }

    @Test
    public void serviceStaticInitializedTwiceTest() {
        LotlService.GLOBAL_SERVICE = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()));

        String exceptionMessage = Assertions.assertThrows(PdfException.class,
                () -> LotlService.initializeGlobalCache(new LotlFetchingProperties(new RemoveOnFailingCountryData()))).getMessage();
        assertEquals(SignExceptionMessageConstant.CACHE_ALREADY_INITIALIZED, exceptionMessage);
        LotlService.GLOBAL_SERVICE.close();
        LotlService.GLOBAL_SERVICE = null;
    }

    @Test
    public void getLotlbytesUnsuccessfulTest() {
        ValidationReport report;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData())) {
            @Override
            Result getLotlBytes() {
                Result result = new Result();
                result.getLocalReport()
                        .addReportItem(new ReportItem("check", "test invalid", ReportItemStatus.INVALID));
                return result;
            }
        }) {
            report = lotlService.getLotlValidator().validate();
        }
        List<ReportItem> reportItems = report.getLogs();
        assertEquals(1, reportItems.size());
        assertEquals("test invalid", reportItems.get(0).getMessage());
    }

    @Test
    public void initializeCacheWithCountrySpecificFailure() {
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new ThrowExceptionOnFailingCountryData()))) {
            CountrySpecificLotlFetcher countrySpecificLotlFetcher = new CountrySpecificLotlFetcher(lotlService) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                        LotlService lotlService) {
                    HashMap<String, Result> results = new HashMap<>();
                    ValidationReport report = new ValidationReport();
                    report.addReportItem(new ReportItem("check", "test invalid", ReportItemStatus.INVALID));
                    Result result = new Result();
                    result.setCountrySpecificLotl(new CountrySpecificLotl("DE", "Germany", "xml"));
                    result.setLocalReport(report);
                    results.put("DE", result);
                    return results;
                }
            };
            lotlService.withCountrySpecificLotlFetcher(countrySpecificLotlFetcher);

            Assertions.assertThrows(InvalidLotlDataException.class, () -> lotlService.initializeCache());
        }
    }

    @Test
    public void cancelTimerWhenItsNotSet() {
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {
            AssertUtil.doesNotThrow(() -> lotlService.cancelTimer());
        }
    }

    static final class CacheReturnsNull implements LotlServiceCache {

        @Override
        public void setAllValues(Result lotlXml,
                EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates,
                PivotFetcher.Result result,
                Map<String, CountrySpecificLotlFetcher.Result> countrySpecificResult) {
        }

        @Override
        public PivotFetcher.Result getPivotResult() {
            return null;
        }

        @Override
        public void setPivotResult(PivotFetcher.Result newResult) {
        }

        @Override
        public Map<String, CountrySpecificLotlFetcher.Result> getCountrySpecificLotls() {
            return null;
        }

        @Override
        public void setCountrySpecificLotlResult(CountrySpecificLotlFetcher.Result countrySpecificLotlResult) {
        }

        @Override
        public Result getLotlResult() {
            return null;
        }

        @Override
        public void setLotlResult(Result data) {
        }

        @Override
        public void setEuropeanResourceFetcherResult(EuropeanResourceFetcher.Result result) {
        }

        @Override
        public EuropeanResourceFetcher.Result getEUJournalCertificates() {
            return null;
        }
    }
}