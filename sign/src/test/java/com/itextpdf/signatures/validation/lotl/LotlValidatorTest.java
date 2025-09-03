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

import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.AssertValidationReport;
import com.itextpdf.signatures.validation.SafeCallingAvoidantException;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("BouncyCastleIntegrationTest")
public class LotlValidatorTest extends ExtendedITextTest {
    private static final String SOURCE = "./src/test/resources/com/itextpdf/signatures/validation/lotl" +
            "/LotlValidatorTest/";

    private static final String SOURCE_FOLDER_LOTL_FILES = "./src/test/resources/com/itextpdf/signatures/validation" +
            "/lotl/LotlState2025_08_08/";

    @BeforeAll
    public static void beforeAll() {
        // Initialize the LotlService with a default EuropeanResourceFetcher
        LotlService service = new LotlService(getLotlFetchingProperties());
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
        service.withLotlValidator(() -> new LotlValidator(service));
        LotlService.GLOBAL_SERVICE = service;
        service.initializeCache();
    }

    @AfterAll
    public static void afterAll() {
        LotlService.GLOBAL_SERVICE.close();
        LotlService.GLOBAL_SERVICE = null;
    }

    @Test
    public void validationTest() {
        LotlValidator validator = LotlService.GLOBAL_SERVICE.getLotlValidator();
        ValidationReport report = validator.validate();
        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
    }

    @Test
    public void validationWithForcedInitializationWithExceptionBecauseOfCustomImplementation() {
        Exception e;
        try (LotlService lotlService = new LotlService(
                new LotlFetchingProperties(new ThrowExceptionOnFailingCountryData()))) {
            lotlService.withCustomResourceRetriever(new IResourceRetriever() {
                @Override
                public InputStream getInputStreamByUrl(URL url) throws IOException {
                    throw new IOException("Failed to fetch Lotl");
                }

                @Override
                public byte[] getByteArrayByUrl(URL url) throws IOException {
                    throw new IOException("Failed to fetch Lotl");
                }
            });

            e = assertThrows(PdfException.class, () -> lotlService.initializeCache());
        }
        Assertions.assertTrue(e.getMessage().contains("Failed to "),
                "Expected exception message to contain 'Failed to ', but got: " + e.getMessage());
    }


    @Test
    public void validationWithCallingPropertiesInitializeCacheFailsAndGuidesToInitializeCache() {
        ValidatorChainBuilder validatorChainBuilder;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {
            validatorChainBuilder = new ValidatorChainBuilder();
            validatorChainBuilder.withLotlService(() -> lotlService);
        }
        validatorChainBuilder.trustEuropeanLotl(true);

        Exception e = assertThrows(PdfException.class, () -> {
            // This should throw an exception because the cache is not initialized
            validatorChainBuilder.getLotlTrustedStore();
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CACHE_NOT_INITIALIZED, e.getMessage());
    }

    @Test
    public void validationWithForcedInitializationWithIgnoredFailuresWorksAsExpected() {
        LotlValidator validator2 = new ValidatorChainBuilder().getLotlService().getLotlValidator();
        ValidationReport report2 = validator2.validate();
        AssertValidationReport.assertThat(report2, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
    }

    @Test
    public void validationWithOnlyAFewCountriesWorksAsExpected() {
        ValidationReport report2;
        try (LotlService lotlService = new LotlService(
                new LotlFetchingProperties(new RemoveOnFailingCountryData()).setCountryNames("DE", "ES"))) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();

            report2 = lotlService.getLotlValidator().validate();
        }
        AssertValidationReport.assertThat(report2, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
    }

    @Test
    public void primeCacheAndRunValidationTest() {
        LotlValidator lotlValidator;
        try (LotlService lotlService = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();

            lotlValidator = lotlService.getLotlValidator();
        }
        ValidationReport report = lotlValidator.validate();
        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        List<IServiceContext> trustedCertificates = lotlValidator.getNationalTrustedCertificates();
        Assertions.assertFalse(trustedCertificates.isEmpty());
    }


    @Test
    public void lotlWithConfiguredSchemaNamesTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("HU");
        lotlFetchingProperties.setCountryNames("EE");

        LotlValidator validator;
        try (LotlService lotlService = new LotlService(lotlFetchingProperties)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();
            validator = lotlService.getLotlValidator();
        }
        ValidationReport report = validator.validate();

        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
        List<IServiceContext> trustedCertificates = validator.getNationalTrustedCertificates();
        Assertions.assertFalse(trustedCertificates.isEmpty());
        // Assuming Estonian and Hungarian Lotl files don't have more than a thousand certificates.
        Assertions.assertTrue(trustedCertificates.size() < 1000);
    }

    @Test
    public void lotlWithInvalidSchemaNameTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("Invalid");

        LotlValidator validator;
        try (LotlService lotlService = new LotlService(lotlFetchingProperties).withCustomResourceRetriever(
                new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES))) {
            lotlService.initializeCache();
            validator = lotlService.getLotlValidator();
        }
        ValidationReport report = validator.validate();

        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
        List<IServiceContext> trustedCertificates = validator.getNationalTrustedCertificates();
        Assertions.assertTrue(trustedCertificates.isEmpty());
    }

    @Test
    public void lotlUnavailableTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        Exception e;
        try (LotlService lotlService = new LotlService(lotlFetchingProperties).withEuropeanLotlFetcher(
                new EuropeanLotlFetcher(null) {
                    @Override
                    public Result fetch() {
                        return new Result(null);
                    }
                })) {

            e = assertThrows(PdfException.class, () -> lotlService.initializeCache());
        }

        Assertions.assertEquals(LotlValidator.UNABLE_TO_RETRIEVE_LOTL, e.getMessage());
    }

    @Test
    public void euJournalCertificatesEmptyTest() {
        Exception e;
        try (LotlService service = new LotlService(
                new LotlFetchingProperties(new RemoveOnFailingCountryData())).withEuropeanResourceFetcher(
                new EuropeanResourceFetcher() {
                    @Override
                    public Result getEUJournalCertificates() {
                        Result result = super.getEUJournalCertificates();
                        result.setCertificates(Collections.<Certificate>emptyList());
                        return result;
                    }
                })) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            e = assertThrows(PdfException.class, () -> service.initializeCache());
        }
        Assertions.assertEquals(LotlValidator.LOTL_VALIDATION_UNSUCCESSFUL, e.getMessage());
    }

    @Test
    public void euJournalEmptyResultTest() {
        Exception e;
        try (LotlService service = new LotlService(
                new LotlFetchingProperties(new RemoveOnFailingCountryData())).withEuropeanResourceFetcher(
                new EuropeanResourceFetcher() {
                    @Override
                    public Result getEUJournalCertificates() {
                        Result result = new Result();
                        result.setCertificates(Collections.<Certificate>emptyList());
                        return result;
                    }
                })) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            e = assertThrows(PdfException.class, () -> service.initializeCache());
        }
        Assertions.assertEquals(SignExceptionMessageConstant.OFFICIAL_JOURNAL_CERTIFICATES_OUTDATED, e.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.OJ_TRANSITION_PERIOD))
    public void mainLotlFileContainsTwoJournalsTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("DE");

        try (LotlService lotlService = new LotlService(lotlFetchingProperties)) {
            PivotFetcher customPivotFetcher = new PivotFetcher(lotlService) {
                @Override
                protected List<String> getPivotsUrlList(byte[] lotlXml) {
                    return Arrays.asList(new String[]{
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-341.xml",
                            "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.9999.999.99.9999.99.ENG.test",
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-335.xml",
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-300.xml",
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-282.xml",
                            "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG"
                    });
                }
            };
            lotlService.withPivotFetcher(customPivotFetcher);
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            lotlService.initializeCache();

            LotlValidator validator = lotlService.getLotlValidator();
            ValidationReport report = validator.validate();
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.OJ_TRANSITION_PERIOD))
    public void mainLotlFileContainsTwoJournalsAndNewOneIsUsedTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("DE");

        try (LotlService lotlService = new LotlService(lotlFetchingProperties)) {
            PivotFetcher customPivotFetcher = new PivotFetcher(lotlService) {
                @Override
                protected List<String> getPivotsUrlList(byte[] lotlXml) {
                    return Arrays.asList(new String[]{
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-341.xml",
                            "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.9999.999.99.9999.99.ENG.test",
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-335.xml",
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-300.xml",
                            "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-282.xml",
                            "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG"
                    });
                }
            };
            EuropeanResourceFetcher customEuropeanResourceFetcher = new EuropeanResourceFetcher() {
                @Override
                public Result getEUJournalCertificates() {
                    Result result = super.getEUJournalCertificates();
                    result.setCurrentlySupportedPublication("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.9999.999.99.9999.99.ENG.test");
                    return result;
                }
            };
            lotlService.withEuropeanResourceFetcher(customEuropeanResourceFetcher);
            lotlService.withPivotFetcher(customPivotFetcher);
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            assertThrows(PdfException.class, () -> {
                // This should throw an exception because only one pivot was fetched and used for validation.
                lotlService.initializeCache();
            });
        }
    }

    @Test
    public void lotlWithBrokenPivotsTest() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("DE");

        IResourceRetriever resourceRetriever = new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES) {
            @Override
            public byte[] getByteArrayByUrl(URL url) {
                return new byte[0];
            }
        };

        try (LotlService lotlService = new LotlService(lotlFetchingProperties).withCustomResourceRetriever(
                        resourceRetriever)
                .withEuropeanLotlFetcher(new EuropeanLotlFetcher(null) {
                    @Override
                    public Result fetch() {
                        try {
                            return new Result(Files.readAllBytes(Paths.get(SOURCE + "eu-lotl-withBrokenPivot.xml")));
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                })) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            assertThrows(PdfException.class, () -> {
                // This should throw an exception because the cache is not initialized
                lotlService.initializeCache();
            });
        }
    }

    @Test
    public void withCustomEuropeanFetcher() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        try (LotlService service = new LotlService(lotlFetchingProperties).withEuropeanResourceFetcher(
                new EuropeanResourceFetcher() {
                    @Override
                    public Result getEUJournalCertificates() {
                        Result result = new Result();
                        result.setCertificates(Collections.<Certificate>emptyList());
                        return result;
                    }
                })) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            Assertions.assertThrows(PdfException.class, () -> {
                // This should throw an exception because the cache is not initialized
                service.initializeCache();
            });
        }
    }


    @Test
    public void tryRefetchCatchManually() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();
            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }


    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_MAIN_LOTL_TO_CACHE_FAILED),
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED)
            }
    )
    public void cacheRefreshFailingLotlDoesNotUpdateMainLotlAndPivotFiles() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withEuropeanLotlFetcher(new EuropeanLotlFetcher(service) {
                @Override
                public Result fetch() {
                    throw new RuntimeException("Simulated failure");
                }
            });
            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }


    @Test
    public void inMemoryCacheThrowsException() throws InterruptedException {
        LotlFetchingProperties lotlFetchingProperties = getLotlFetchingProperties();
        lotlFetchingProperties.setCountryNames("NL");
        lotlFetchingProperties.setCacheStalenessInMilliseconds(100);
        lotlFetchingProperties.setRefreshIntervalCalculator(f -> 100000);

        LotlValidator validator;
        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            Thread.sleep(1000);

            validator = service.getLotlValidator();
        }

        Exception e = assertThrows(PdfException.class, () -> {
            // This should throw an exception because the cache is stale
            validator.validate();
        });
        Assertions.assertEquals(SignExceptionMessageConstant.STALE_DATA_IS_USED, e.getMessage());

    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_MAIN_LOTL_TO_CACHE_FAILED),
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED)
            }
    )
    public void cacheRefreshInvalidLotlDoesNotUpdateMainLotlAndPivotFiles() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withEuropeanLotlFetcher(new EuropeanLotlFetcher(service) {
                @Override
                public Result fetch() {
                    Result result = new Result();
                    result.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                            "Simulated invalid Lotl", ReportItem.ReportItemStatus.INVALID));
                    return result;
                }
            });

            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED))
    public void cacheRefreshWithInvalidPivotFileDoesNotUpdateCache() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withPivotFetcher(new PivotFetcher(service) {
                @Override
                public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates) {
                    Result result = new Result();
                    result.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                            "Simulated invalid pivot file", ReportItem.ReportItemStatus.INVALID));
                    return result;
                }
            });

            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED))
    public void cacheRefreshWithExceptionInPivot() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withPivotFetcher(new PivotFetcher(service) {
                @Override
                public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates) {
                    throw new RuntimeException("Simulated failure in pivot file download");
                }
            });

            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.FAILED_TO_FETCH_COUNTRY_SPECIFIC_LOTL))
    public void cacheRefreshWithExceptionDoesNotUpdateCacheWithCountrySpecific2() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml, LotlService service) {
                    throw new RuntimeException("Simulated failure in country specific Lotl file download");
                }
            });

            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.NO_COUNTRY_SPECIFIC_LOTL_FETCHED))
    public void cacheRefreshWithReturningNullDoesNotThrow() {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml, LotlService service) {
                    return null;
                }
            });

            AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
        }
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED))
    public void cacheRefreshWithSomeSpecificCountryFailuresDoesNotUpdateCache() throws InterruptedException {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");
        lotlFetchingProperties.setCacheStalenessInMilliseconds(100L);
        lotlFetchingProperties.setRefreshIntervalCalculator(f -> 10000L);

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml, LotlService service) {
                    HashMap<String, Result> result = new HashMap<>();
                    Result r = new Result();
                    r.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                            "Simulated invalid country specific Lotl", ReportItem.ReportItemStatus.INVALID));
                    r.setCountrySpecificLotl(new CountrySpecificLotl("NL",
                            "https://www.rdi.nl/site/binaries/site-content/collections/documents/current-tsl.xml",
                            "application/xml"));
                    result.put(r.createUniqueIdentifier(), r);
                    return result;
                }
            });
            service.tryAndRefreshCache();
            Thread.sleep(1000); // Wait for the cache refresh to complete

            Assertions.assertThrows(SafeCallingAvoidantException.class, () -> service.getLotlValidator().validate());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED))
    public void cacheRefreshWithSomeSpecificCountryFailuresDoesNotUpdateCacheAndIgnores() throws InterruptedException {
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        lotlFetchingProperties.setCountryNames("NL");
        lotlFetchingProperties.setCacheStalenessInMilliseconds(2000L);
        lotlFetchingProperties.setRefreshIntervalCalculator(f -> 1000000L);

        try (LotlService service = new LotlService(lotlFetchingProperties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            service.initializeCache();

            // Simulate a failure in the cache refresh
            service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                        LotlService lotlService) {
                    HashMap<String, Result> result = new HashMap<>();
                    Result r = new Result();
                    r.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                            "Simulated invalid country specific Lotl", ReportItem.ReportItemStatus.INVALID));
                    r.setCountrySpecificLotl(new CountrySpecificLotl("NL",
                            "https://www.rdi.nl/site/binaries/site-content/collections/documents/current-tsl.xml",
                            "application/xml"));
                    result.put(r.createUniqueIdentifier(), r);
                    return result;
                }
            });
            Thread.sleep(2100);
            service.tryAndRefreshCache();
            AssertUtil.doesNotThrow(() -> service.getLotlValidator().validate());
        }
    }


    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED
                    )
            }
    )
    public void cacheRefreshWithValidationWorksButCertsNotIncluded() throws InterruptedException {
        LotlFetchingProperties properties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        properties.setCountryNames("NL");
        properties.setCacheStalenessInMilliseconds(1000);
        properties.setRefreshIntervalCalculator((f) -> Integer.MAX_VALUE);

        int originalAmountOfCertificates;
        LotlValidator validator2;
        try (LotlService service = new LotlService(properties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));

            // Simulate a failure in the cache refresh
            service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
                boolean firstTime = true;

                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                        LotlService lotlService) {
                    if (firstTime) {
                        firstTime = false;
                        return super.getAndValidateCountrySpecificLotlFiles(lotlXml, lotlService);
                    }
                    HashMap<String, Result> result = new HashMap<>();
                    Result r = new Result();
                    r.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                            "Simulated invalid country specific Lotl", ReportItem.ReportItemStatus.INVALID));
                    r.setCountrySpecificLotl(new CountrySpecificLotl("NL",
                            "https://www.rdi.nl/site/binaries/site-content/collections/documents/current-tsl.xml",
                            "application/xml"));
                    result.put(r.createUniqueIdentifier(), r);
                    return result;
                }
            });

            service.initializeCache();

            LotlValidator validator = service.getLotlValidator();
            validator.validate();
            originalAmountOfCertificates = validator.getNationalTrustedCertificates().size();
            Thread.sleep(2000);
            service.tryAndRefreshCache();
            validator2 = service.getLotlValidator();
        }
        validator2.validate();
        int newAmountOfCertificates = validator2.getNationalTrustedCertificates().size();
        Assertions.assertTrue(originalAmountOfCertificates > newAmountOfCertificates,
                "Expected the number of certificates to decrease after a failed refresh, but got: " +
                        originalAmountOfCertificates + " and " + newAmountOfCertificates);
        Assertions.assertEquals(0, newAmountOfCertificates,
                "Expected the number of certificates to be 0 after a failed refresh, but got: "
                        + newAmountOfCertificates);
    }


    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED
                    )
            }
    )
    public void cacheRefreshWithValidationWorksButCertsNotIncludedMultipleCountries() throws InterruptedException {
        LotlFetchingProperties properties = new LotlFetchingProperties(
                new RemoveOnFailingCountryData());
        properties.setCountryNames("NL", "BE");
        properties.setCacheStalenessInMilliseconds(1800);
        properties.setRefreshIntervalCalculator((f) -> Integer.MAX_VALUE);

        int originalAmountOfCertificates;
        LotlValidator validator2;
        try (LotlService service = new LotlService(properties)) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));

            // Simulate a failure in the cache refresh
            service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
                boolean firstTime = true;

                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                        LotlService lotlService) {
                    if (firstTime) {
                        firstTime = false;
                        return super.getAndValidateCountrySpecificLotlFiles(lotlXml, lotlService);
                    }
                    Map<String, Result> result = super.getAndValidateCountrySpecificLotlFiles(lotlXml, lotlService);
                    Result r = new Result();
                    r.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                            "Simulated invalid country specific Lotl", ReportItem.ReportItemStatus.INVALID));
                    r.setCountrySpecificLotl(new CountrySpecificLotl("NL",
                            "https://www.rdi.nl/site/binaries/site-content/collections/documents/current-tsl.xml",
                            "application/xml"));
                    result.put(r.createUniqueIdentifier(), r);
                    return result;

                }
            });

            service.initializeCache();

            LotlValidator validator = service.getLotlValidator();
            validator.validate();
            originalAmountOfCertificates = validator.getNationalTrustedCertificates().size();
            Thread.sleep(2000);
            service.tryAndRefreshCache();
            validator2 = service.getLotlValidator();
        }
        validator2.validate();
        int newAmountOfCertificates = validator2.getNationalTrustedCertificates().size();
        Assertions.assertTrue(originalAmountOfCertificates > newAmountOfCertificates,
                "Expected the number of certificates to decrease after a failed refresh, but got: " +
                        originalAmountOfCertificates + " and " + newAmountOfCertificates);
    }

    @Test
    public void useOwnCountrySpecificLotlFetcher() {
        try (LotlService service = new LotlService(new LotlFetchingProperties(new RemoveOnFailingCountryData()))) {
            service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            CountrySpecificLotlFetcher lotlFetcher = new CountrySpecificLotlFetcher(service) {
                @Override
                public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml, LotlService service) {
                    return Collections.<String, Result>emptyMap();
                }
            };
            service.withCountrySpecificLotlFetcher(lotlFetcher);
            service.initializeCache();
            AssertUtil.doesNotThrow(() -> service.getLotlValidator().validate());
        }
    }

    @Test
    public void lotlBytesThrowsPdfException() {
        LotlFetchingProperties p = getLotlFetchingProperties();
        p.setCountryNames("NL");
        Exception e;
        try (LotlService service = new LotlService(p)) {

            EuropeanLotlFetcher lotlByteFetcher = new EuropeanLotlFetcher(service) {
                @Override
                public Result fetch() {
                    throw new RuntimeException("Test exception");
                }
            };
            service.withEuropeanLotlFetcher(lotlByteFetcher);

            e = assertThrows(RuntimeException.class, () -> service.initializeCache());
        }
        Assertions.assertEquals("Test exception", e.getMessage());
    }

    @Test
    public void cacheInitializationWithSomeSpecificCountryThatWorksTest() {
        LotlFetchingProperties p = getLotlFetchingProperties();
        p.setCountryNames("NL");
        try (LotlService lotlService = new LotlService(p)) {

            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            AssertUtil.doesNotThrow(() -> lotlService.initializeCache());
        }
    }

    private static LotlFetchingProperties getLotlFetchingProperties() {
        return new LotlFetchingProperties(new RemoveOnFailingCountryData());
    }
}

