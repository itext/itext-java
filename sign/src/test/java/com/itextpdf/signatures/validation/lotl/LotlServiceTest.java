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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.lotl.EuropeanLotlFetcher.Result;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map.Entry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("IntegrationTest")
public class LotlServiceTest extends ExtendedITextTest {


    private static final String SOURCE_FOLDER_LOTL_FILES =
            "./src/test/resources/com/itextpdf/signatures/validation" + "/lotl/LotlState2025_08_08/";
    private static final String SOURCE =
            "./src/test/resources/com/itextpdf/signatures/validation" + "/lotl/LotlServiceTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/signatures/sign/LotlTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }


    public static Iterable<Object[]> allCountries() {
        return Arrays.asList(new Object[][] {
                {LotlCountryCodeConstants.AUSTRIA},
                {LotlCountryCodeConstants.BELGIUM},
                {LotlCountryCodeConstants.BULGARIA},
                {LotlCountryCodeConstants.CYPRUS},
                {LotlCountryCodeConstants.CZECHIA},
                {LotlCountryCodeConstants.GERMANY},
                {LotlCountryCodeConstants.DENMARK},
                {LotlCountryCodeConstants.ESTONIA},
                {LotlCountryCodeConstants.GREECE},
                {LotlCountryCodeConstants.SPAIN},
                {LotlCountryCodeConstants.FINLAND},
                {LotlCountryCodeConstants.FRANCE},
                {LotlCountryCodeConstants.CROATIA},
                {LotlCountryCodeConstants.HUNGARY},
                {LotlCountryCodeConstants.IRELAND},
                {LotlCountryCodeConstants.ICELAND},
                {LotlCountryCodeConstants.ITALY},
                {LotlCountryCodeConstants.LIECHTENSTEIN},
                {LotlCountryCodeConstants.LITHUANIA},
                {LotlCountryCodeConstants.LUXEMBOURG},
                {LotlCountryCodeConstants.LATVIA},
                {LotlCountryCodeConstants.MALTA},
                {LotlCountryCodeConstants.NETHERLANDS},
                {LotlCountryCodeConstants.NORWAY},
                {LotlCountryCodeConstants.POLAND},
                {LotlCountryCodeConstants.PORTUGAL},
                {LotlCountryCodeConstants.ROMANIA},
                {LotlCountryCodeConstants.SWEDEN},
                {LotlCountryCodeConstants.SLOVENIA},
                {LotlCountryCodeConstants.SLOVAKIA},
                {LotlCountryCodeConstants.UNITED_KINGDOM},
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allCountries")
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore )
    public void serializeIndividualCountry(String country) throws IOException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setCountryNames(country);
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            lotlService.serializeCache(outputStream);
            byte[] actual = outputStream.toByteArray();

            String fileName = "single-country-" + country + ".json";
            byte[] expected = Files.readAllBytes(Paths.get(SOURCE + fileName));

            LotlCacheDataV1 actualData = LotlCacheDataV1.deserialize(new ByteArrayInputStream(actual));
            LotlCacheDataV1 expectedData = LotlCacheDataV1.deserialize(new ByteArrayInputStream(expected));

            assert2LotlCacheDataV1(expectedData, actualData);
        }
    }

    @Test
    public void refreshCalculatorZeroThrowsException() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        Exception e = Assertions.assertThrows(PdfException.class, () -> properties.setCacheStalenessInMilliseconds(0L));
        assertEquals(SignExceptionMessageConstant.STALENESS_MUST_BE_POSITIVE, e.getMessage());
    }

    @Test
    public void testWithPivotFetcher() {
        try (LotlService lotlService = new LotlService(
                new LotlFetchingProperties(new RemoveOnFailingCountryData()).setCountryNames("NL"))) {
            AssertUtil.doesNotThrow(() -> lotlService.withPivotFetcher(new PivotFetcher(lotlService)));
        }
    }

    @Test
    public void testCacheFailCallsDownloadOfMainLotlFile() {
        Result f;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {

            lotlService.withLotlServiceCache(new CacheReturnsNull());
            lotlService.withEuropeanLotlFetcher(new EuropeanLotlFetcher(lotlService) {
                @Override
                public Result fetch() {
                    return new Result("abc".getBytes(StandardCharsets.UTF_8));
                }
            });

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

            lotlService.withEuropeanResourceFetcher(new EuropeanResourceFetcher() {
                @Override
                public Result getEUJournalCertificates() {
                    Result result = new Result();
                    result.setCertificates(Collections.<Certificate>emptyList());
                    return result;
                }
            });
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

            lotlService.withPivotFetcher(new PivotFetcher(lotlService) {

                @Override
                public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates) {
                    Result result = new Result();
                    result.setPivotUrls(Collections.singletonList("http://example.com/pivot.xml"));
                    return result;
                }
            });
            f = lotlService.getAndValidatePivotFiles("abc".getBytes(StandardCharsets.UTF_8),
                    Collections.<Certificate>emptyList(), null);
        }
        Assertions.assertNotNull(f);
        assertEquals(1, f.getPivotUrls().size());
    }


    @Test
    public void testCacheFailCallsDownloadOfCountrySpecificLotl() {
        List<CountrySpecificLotlFetcher.Result> f;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {

            lotlService.withLotlServiceCache(new CacheReturnsNull());

            lotlService.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(lotlService) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                        LotlService lotlService) {
                    HashMap<String, Result> resultMap = new HashMap<>();
                    Result result = new Result();
                    result.setCountrySpecificLotl(
                            new CountrySpecificLotl("NL", "http://example.com/lotl.xml", "application/xml"));
                    resultMap.put(result.createUniqueIdentifier(), result);
                    return resultMap;
                }
            });
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
        lotlFetchingProperties.setRefreshIntervalCalculator(l -> 100); // 100 milliseconds
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
            Thread.sleep(2000);
        }
        Assertions.assertTrue(refreshCounter.get() >= 5 && refreshCounter.get() <= 20,
                "Refresh counter should be between 5 and 20, but was: " + refreshCounter.get());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            SignLogMessageConstant.FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES))
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
                        () -> LotlService.initializeGlobalCache(new LotlFetchingProperties(new RemoveOnFailingCountryData())))
                .getMessage();
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
        try (LotlService lotlService = new LotlService(
                new LotlFetchingProperties(new ThrowExceptionOnFailingCountryData()))) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
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

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore )
    public void serializationAllCountriesTest() throws IOException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStream);
            byte[] expected = Files.readAllBytes(Paths.get(SOURCE + "all-countries.json"));
            byte[] actual = outputStream.toByteArray();

            LotlCacheDataV1 actualData = LotlCacheDataV1.deserialize(new ByteArrayInputStream(actual));
            LotlCacheDataV1 expectedData = LotlCacheDataV1.deserialize(new ByteArrayInputStream(expected));

            assert2LotlCacheDataV1(expectedData, actualData);
        }
    }

    @Test
    public void serializationPassNullAsStreamFallsBackToNetwork() {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            AssertUtil.doesNotThrow(() -> {
                lotlService.initializeCache(null);
            });
        }
    }

    @Test
    public void serializationPassNonExistingFile() {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            Assertions.assertThrows(IOException.class, () -> {
                lotlService.initializeCache(Files.newInputStream(Paths.get(SOURCE + "nonExistingFile.json")));
            });
        }
    }

    @Test
    public void serializationPassEmptyJson() {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            Assertions.assertThrows(PdfException.class, () -> {
                lotlService.initializeCache(Files.newInputStream(Paths.get(SOURCE + "empty.json")));
            });
        }
    }

    @Test
    public void serializationInvalidTopLevel() {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            Assertions.assertThrows(PdfException.class, () -> {
                lotlService.initializeCache(Files.newInputStream(Paths.get(SOURCE + "invalid-top-level.json")));
            });
        }
    }

    @Test
    public void serializationBroken() {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            Assertions.assertThrows(PdfException.class, () -> {
                lotlService.initializeCache(Files.newInputStream(Paths.get(SOURCE + "invalid-top-level.json")));
            });
        }
    }

    @Test
    public void loadAllCountriesFromValue() throws IOException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            AssertUtil.doesNotThrow(() -> {
                lotlService.initializeCache(Files.newInputStream(Paths.get(SOURCE + "all-countries.json")));
            });
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStream);
            byte[] expected = Files.readAllBytes(Paths.get(SOURCE + "all-countries.json"));
            byte[] actual = outputStream.toByteArray();

            LotlCacheDataV1 actualData = LotlCacheDataV1.deserialize(new ByteArrayInputStream(actual));
            LotlCacheDataV1 expectedData = LotlCacheDataV1.deserialize(new ByteArrayInputStream(expected));

            assert2LotlCacheDataV1(expectedData, actualData);

        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_NOT_REQUIRED_BY_CONFIGURATION))
    public void loadStateButItDoesNotContainRequiredCountryThrowsException() throws IOException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setCountryNames(LotlCountryCodeConstants.NETHERLANDS, LotlCountryCodeConstants.POLAND);
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStream);

            LotlFetchingProperties props2 = new LotlFetchingProperties(new RemoveOnFailingCountryData());
            props2.setCountryNames(LotlCountryCodeConstants.NETHERLANDS, LotlCountryCodeConstants.BELGIUM);
            try (LotlService lotlService2 = new LotlService(props2)) {
                lotlService2.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
                Exception e = Assertions.assertThrows(PdfException.class,
                        () -> lotlService2.initializeCache(new ByteArrayInputStream(outputStream.toByteArray())));
                Assertions.assertEquals(MessageFormatUtil.format(
                                SignExceptionMessageConstant.INITIALIZED_CACHE_DOES_NOT_CONTAIN_REQUIRED_COUNTRY, "BE"),
                        e.getMessage());
            }
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_NOT_REQUIRED_BY_CONFIGURATION))
    public void loadStateWithLessRequiredCountriesLogs() throws IOException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setCountryNames(LotlCountryCodeConstants.NETHERLANDS, LotlCountryCodeConstants.POLAND);
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStream);

            LotlFetchingProperties props2 = new LotlFetchingProperties(new RemoveOnFailingCountryData());
            props2.setCountryNames(LotlCountryCodeConstants.NETHERLANDS);
            try (LotlService lotlService2 = new LotlService(props2)) {
                lotlService2.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
                lotlService2.initializeCache(new ByteArrayInputStream(outputStream.toByteArray()));
                HashMap<String, CountrySpecificLotlFetcher.Result> f = lotlService2.getCachedCountrySpecificLotls();
                Assertions.assertEquals(1, f.size());
                for (Entry<String, CountrySpecificLotlFetcher.Result> stringResultEntry : f.entrySet()) {
                    if (stringResultEntry.getKey().startsWith("NL")) {
                        Assertions.assertEquals("NL",
                                stringResultEntry.getValue().getCountrySpecificLotl().getSchemeTerritory());
                    } else {
                        Assertions.fail("Only NL should be present");
                    }
                }
            }
        }
    }


    @Test
    public void serializeDeserializedWithTimestampsToOldThrows() throws IOException, InterruptedException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setCountryNames(LotlCountryCodeConstants.BELGIUM);
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStream);
            LotlFetchingProperties props2 = new LotlFetchingProperties(new RemoveOnFailingCountryData());
            props2.setCountryNames(LotlCountryCodeConstants.BELGIUM);
            props2.setCacheStalenessInMilliseconds(10L);
            props2.setRefreshIntervalCalculator(l -> Integer.MAX_VALUE);
            try (LotlService lotlService2 = new LotlService(props2)) {
                lotlService2.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
                lotlService2.initializeCache(new ByteArrayInputStream(outputStream.toByteArray()));
                Thread.sleep(150);
                Exception e = Assertions.assertThrows(InvalidLotlDataException.class, () -> {
                    lotlService2.getLotlBytes();
                });
                Assertions.assertEquals(SignExceptionMessageConstant.STALE_DATA_IS_USED, e.getMessage());
            }
        }
    }

    @Test
    public void serializeDeserializedWithTimestampsOkDoesntThrow() throws IOException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setCountryNames(LotlCountryCodeConstants.BELGIUM);
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStream);
            LotlFetchingProperties props2 = new LotlFetchingProperties(new RemoveOnFailingCountryData());
            props2.setCountryNames(LotlCountryCodeConstants.BELGIUM);
            props2.setRefreshIntervalCalculator(l -> Integer.MAX_VALUE);
            try (LotlService lotlService2 = new LotlService(props2)) {
                lotlService2.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
                lotlService2.initializeCache(new ByteArrayInputStream(outputStream.toByteArray()));
                Result result = lotlService2.getLotlBytes();
                Assertions.assertNotNull(result);
                Assertions.assertNotNull(result.getLotlXml());
            }
        }
    }

    @Test
    public void serializeDeserializedWithOlderTimestampsThrows() throws IOException, InterruptedException {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setRefreshIntervalCalculator(l -> Integer.MAX_VALUE);
        props.setCountryNames(LotlCountryCodeConstants.BELGIUM);
        try (LotlService lotlService = new LotlService(props)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            ByteArrayOutputStream outputStreamOldest = new ByteArrayOutputStream();
            lotlService.serializeCache(outputStreamOldest);

            Thread.sleep(50);
            ByteArrayOutputStream outputStreamNewer = new ByteArrayOutputStream();
            try (LotlService lotlService1 = new LotlService(props)) {
                lotlService1.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
                lotlService1.initializeCache();
                lotlService1.serializeCache(outputStreamNewer);

            }

            LotlFetchingProperties props2 = new LotlFetchingProperties(new RemoveOnFailingCountryData());
            props2.setCountryNames(LotlCountryCodeConstants.BELGIUM);
            props2.setRefreshIntervalCalculator(l -> Integer.MAX_VALUE);

            try (LotlService lotlService2 = new LotlService(props2)) {
                lotlService2.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
                lotlService2.initializeCache(new ByteArrayInputStream(outputStreamNewer.toByteArray()));
                Exception e = Assertions.assertThrows(PdfException.class, () -> {
                    lotlService2.initializeCache(new ByteArrayInputStream(outputStreamOldest.toByteArray()));
                });
                Assertions.assertEquals(SignExceptionMessageConstant.CACHE_INCOMING_DATA_IS_STALER, e.getMessage());
            }
        }
    }

    static void assert2LotlCacheDataV1(LotlCacheDataV1 expected, LotlCacheDataV1 actual) {
        //Parsing is slightly different, so byte arrays differ even if logically they are the same
        //Just check if it's not null and has some content
        Assertions.assertNotNull(actual.getLotlCache().getLotlXml(), "Main lotl xml is null");
        Assertions.assertTrue(actual.getLotlCache().getLotlXml().length > 0, "Main lotl xml is empty");

        int logsAmount = expected.getLotlCache().getLocalReport().getLogs().size();
        Assertions.assertEquals(logsAmount, actual.getLotlCache().getLocalReport().getLogs().size(),
                "Amount of logs in main lotl differs");

        assertEquals(expected.getCountrySpecificLotlCache().size(), actual.getCountrySpecificLotlCache().size());
        for (Entry<String, CountrySpecificLotlFetcher.Result> entry : expected.getCountrySpecificLotlCache()
                .entrySet()) {
            CountrySpecificLotlFetcher.Result actualResult = actual.getCountrySpecificLotlCache().get(entry.getKey());
            Assertions.assertNotNull(actualResult, "Country specific lotl for key " + entry.getKey() + " is missing");
            assertEquals(entry.getValue().getCountrySpecificLotl().getMimeType(),
                    actualResult.getCountrySpecificLotl().getMimeType());
            assertEquals(entry.getValue().getCountrySpecificLotl().getTslLocation(),
                    actualResult.getCountrySpecificLotl().getTslLocation());
            assertEquals(entry.getValue().getCountrySpecificLotl().getSchemeTerritory(),
                    actualResult.getCountrySpecificLotl().getSchemeTerritory());
            int amountOfLogs = entry.getValue().getLocalReport().getLogs().size();
            Assertions.assertEquals(amountOfLogs, actualResult.getLocalReport().getLogs().size(),
                    "Amount of logs for country " + entry.getKey() + " differs");

            assertEquals(entry.getValue().getContexts().size(), actualResult.getContexts().size(),
                    "Amount of certificates for country " + entry.getKey() + " differs");

        }
        assertEquals(expected.getTimeStamps().size(), actual.getTimeStamps().size(), "Timestamps map size differs");
        for (Entry<String, Long> entry : expected.getTimeStamps().entrySet()) {
            Long actualTimestamp = actual.getTimeStamps().get(entry.getKey());
            Assertions.assertNotNull(actualTimestamp, "Timestamp for key " + entry.getKey() + " is missing");
        }

        assertEquals(expected.getPivotCache().getPivotUrls().size(), actual.getPivotCache().getPivotUrls().size(),
                "Amount of pivot urls differs");

        assertEquals(expected.getPivotCache().getLocalReport().getLogs().size(),
                actual.getPivotCache().getLocalReport().getLogs().size(), "Amount of pivot logs differs");

        //expected.getEuropeanResourceFetcherCache().getCertificates().size()
        assertEquals(expected.getEuropeanResourceFetcherCache().getCertificates().size(),
                actual.getEuropeanResourceFetcherCache().getCertificates().size(),
                "Amount of EU journal certificates differs");

        assertEquals(expected.getEuropeanResourceFetcherCache().getCurrentlySupportedPublication(),
                actual.getEuropeanResourceFetcherCache().getCurrentlySupportedPublication(),
                "Currently supported publication differs");

        assertEquals(expected.getEuropeanResourceFetcherCache().getLocalReport().getLogs().size(),
                actual.getEuropeanResourceFetcherCache().getLocalReport().getLogs().size(),
                "Amount of EU journal logs differs");


    }

    static final class CacheReturnsNull implements LotlServiceCache {

        @Override
        public void setAllValues(Result lotlXml,
                EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates, PivotFetcher.Result result,
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

