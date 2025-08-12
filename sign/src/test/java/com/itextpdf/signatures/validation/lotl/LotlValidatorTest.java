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
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("BouncyCastleIntegrationTest")
@DisabledInNativeImage
public class LotlValidatorTest extends ExtendedITextTest {
    private static final String SOURCE = "./src/test/resources/com/itextpdf/signatures/validation/lotl" +
            "/LotlValidatorTest/";

    private static final String SOURCE_FOLDER_LOL_FILES = "./src/test/resources/com/itextpdf/signatures/validation" +
            "/lotl/LotlState2025_08_08/";

    @BeforeAll
    public static void beforeAll() {
        // Initialize the LotlService with a default EuropeanResourceFetcher
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        chainBuilder.withLotlValidator(() -> new LotlValidator(chainBuilder).withService(service));
        LotlValidator.GLOBAL_SERVICE = service;
        service.initializeCache();
    }

    @Test
    public void validationTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        LotlValidator validator = chainBuilder.getLotlValidator();
        ValidationReport report = validator.validate();
        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
    }

    @Test
    public void validationWithForcedInitializationWithExceptionBecauseOfCustomImplementation() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.withLotlFetchingProperties(new LotlFetchingProperties(new ThrowExceptionIOnFailureStrategy()));
        LotlService lotlService = new LotlService(chainBuilder);
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

        Exception e = assertThrows(PdfException.class, () -> {
            lotlService.initializeCache();
        });
        Assertions.assertTrue(e.getMessage().contains("Failed to "),
                "Expected exception message to contain 'Failed to ', but got: " + e.getMessage());
    }


    @Test
    public void validationWithCallingPropertiesInitializeCacheFailsAndGuidesToInitializeCache() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();

        LotlService lotlService = new LotlService(chainBuilder);

        chainBuilder.withLotlValidator(() -> new LotlValidator(chainBuilder).withService(lotlService));
        LotlValidator validator = chainBuilder.getLotlValidator();

        Exception e = assertThrows(PdfException.class, () -> {
            // This should throw an exception because the cache is not initialized
            validator.validate();
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CACHE_NOT_INITIALIZED, e.getMessage());
    }

    @Test
    public void validationWithForcedInitializationWithIgnoredFailuresWorksAsExpected() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();

        LotlService lotlService = new LotlService(chainBuilder);
        lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        lotlService.initializeCache();

        ValidatorChainBuilder chainBuilder2 = new LotlEnableValidatorChainBuilder().withLotlValidator(
                () -> new LotlValidator(chainBuilder).withService(lotlService));
        LotlValidator validator2 = chainBuilder2.getLotlValidator();
        ValidationReport report2 = validator2.validate();
        AssertValidationReport.assertThat(report2, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
    }

    @Test
    public void validationWithOnlyAFewCountriesWorksAsExpected() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        // gutentag
        chainBuilder.getLotlFetchingProperties().setCountryNames("DE", "ES");

        LotlService lotlService = new LotlService(chainBuilder);
        lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        lotlService.initializeCache();

        ValidatorChainBuilder chainBuilder2 = new LotlEnableValidatorChainBuilder().withLotlValidator(
                () -> new LotlValidator(chainBuilder).withService(lotlService));
        LotlValidator validator2 = chainBuilder2.getLotlValidator();
        ValidationReport report2 = validator2.validate();
        AssertValidationReport.assertThat(report2, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
    }

    @Test
    public void primeCacheAndRunValidationTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        LotlService lotlService = new LotlService(chainBuilder);
        lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        lotlService.initializeCache();
        chainBuilder.withLotlValidator(() -> new LotlValidator(chainBuilder).withService(lotlService));
        LotlValidator validator = chainBuilder.getLotlValidator();
        ValidationReport report = validator.validate();
        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        List<IServiceContext> trustedCertificates = validator.getNationalTrustedCertificates();
        Assertions.assertFalse(trustedCertificates.isEmpty());
    }


    @Test
    public void lotlWithConfiguredSchemaNamesTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new IgnoreCountrySpecificCertificates());
        lotlFetchingProperties.setCountryNames("HU");
        lotlFetchingProperties.setCountryNames("EE");
        chainBuilder.withLotlFetchingProperties(lotlFetchingProperties);
        LotlValidator validator = chainBuilder.getLotlValidator();
        ValidationReport report = validator.validate();
        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
        List<IServiceContext> trustedCertificates = validator.getNationalTrustedCertificates();
        Assertions.assertFalse(trustedCertificates.isEmpty());
        // Assuming Estonian and Hungarian Lotl files don't have more than a thousand certificates.
        Assertions.assertTrue(trustedCertificates.size() < 1000);
    }

    @Test
    public void lotlWithInvalidSchemaNameTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        LotlFetchingProperties lotlFetchingProperties = new LotlFetchingProperties(
                new IgnoreCountrySpecificCertificates());
        lotlFetchingProperties.setCountryNames("Invalid");
        chainBuilder.withLotlFetchingProperties(lotlFetchingProperties);
        LotlValidator validator = chainBuilder.getLotlValidator();
        ValidationReport report = validator.validate();
        AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfFailures(0));
        List<IServiceContext> trustedCertificates = validator.getNationalTrustedCertificates();
        Assertions.assertTrue(trustedCertificates.isEmpty());
    }

    @Test
    public void lotlUnavailableTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService lotlService = new LotlService(chainBuilder).withEULotlFetcher(new EuropeanLotlFetcher(null) {
            @Override
            public Result fetch() {
                return new Result(null);
            }
        });

        Exception e = assertThrows(PdfException.class, () -> {
            lotlService.initializeCache();
        });

        Assertions.assertEquals(LotlValidator.UNABLE_TO_RETRIEVE_Lotl, e.getMessage());
    }

    @Test
    public void euJournalCertificatesEmptyTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();

        LotlService service = new LotlService(chainBuilder).withDefaultEuropeanResourceFetcher(
                new EuropeanResourceFetcher() {
                    @Override
                    public EuropeanResourceFetcher.Result getEUJournalCertificates() {
                        Result result = new Result();
                        result.setCertificates(Collections.<Certificate>emptyList());
                        return result;
                    }
                });
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        Exception e = assertThrows(PdfException.class, () -> {
            service.initializeCache();
        });
        Assertions.assertEquals(LotlValidator.LOTL_VALIDATION_UNSUCCESSFUL, e.getMessage());

    }

    @Test
    public void lotlWithBrokenPivotsTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("DE");

        IResourceRetriever resourceRetriever = new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES) {
            @Override
            public byte[] getByteArrayByUrl(URL url) {
                return new byte[0];
            }
        };

        LotlService lotlService = new LotlService(chainBuilder).withCustomResourceRetriever(resourceRetriever)
                .withEULotlFetcher(new EuropeanLotlFetcher(null) {
                    @Override
                    public Result fetch() {
                        try {
                            return new Result(Files.readAllBytes(Paths.get(SOURCE + "eu-lotl-withBrokenPivot.xml")));
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                });
        lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        assertThrows(PdfException.class, () -> {
            // This should throw an exception because the cache is not initialized
            lotlService.initializeCache();
        });
    }

    @Test
    public void withCustomEuropeanFetcher() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        LotlService service = new LotlService(chainBuilder).withDefaultEuropeanResourceFetcher(
                new EuropeanResourceFetcher() {
                    @Override
                    public Result getEUJournalCertificates() {
                        Result result = new Result();
                        result.setCertificates(Collections.<Certificate>emptyList());
                        return result;
                    }
                });
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        Assertions.assertThrows(PdfException.class, () -> {
            // This should throw an exception because the cache is not initialized
            service.initializeCache();
        });
    }


    @Test
    public void tryRefetchCatchManually() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();

        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();
        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }


    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_MAIN_LOTL_TO_CACHE_FAILED,
                            count = 1),
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED,
                            count = 1)
            }
    )
    public void cacheRefreshFailingLotlDoesNotUpdateMainLotlAndPivotFiles() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withEULotlFetcher(new EuropeanLotlFetcher(service) {
            @Override
            public Result fetch() {
                throw new RuntimeException("Simulated failure");
            }
        });
        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }


    @Test
    public void inMemoryCacheThrowsException() throws InterruptedException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");
        chainBuilder.getLotlFetchingProperties().setCacheStalenessInMilliseconds(100);
        chainBuilder.getLotlFetchingProperties().setRefreshIntervalCalculator((f) -> 100000);

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        chainBuilder.withLotlValidator(() -> new LotlValidator(chainBuilder).withService(service));

        Thread.sleep(1000);

        LotlValidator validator = chainBuilder.getLotlValidator();

        Exception e = assertThrows(PdfException.class, () -> {
            // This should throw an exception because the cache is stale
            validator.validate();
        });
        Assertions.assertEquals(SignExceptionMessageConstant.STALE_DATA_IS_USED, e.getMessage());

    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_MAIN_LOTL_TO_CACHE_FAILED,
                            count = 1),
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED,
                            count = 1)
            }
    )
    public void cacheRefreshInvalidLotlDoesNotUpdateMainLotlAndPivotFiles() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withEULotlFetcher(new EuropeanLotlFetcher(service) {
            @Override
            public Result fetch() {
                Result result = new Result();
                result.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                        "Simulated invalid Lotl", ReportItem.ReportItemStatus.INVALID));
                return result;
            }
        });

        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED,
                            count = 1)
            }
    )
    public void cacheRefreshWithInvalidPivotFileDoesNotUpdateCache() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withPivotFetcher(new PivotFetcher(service, chainBuilder) {
            @Override
            public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates,
                    SignatureValidationProperties properties) {
                Result result = new Result();
                result.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                        "Simulated invalid pivot file", ReportItem.ReportItemStatus.INVALID));
                return result;
            }
        });

        AssertUtil.doesNotThrow(() -> service.tryAndRefreshCache());
    }


    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED,
                            count = 1)
            }
    )
    public void cacheRefreshWithExceptionInPivot() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withPivotFetcher(new PivotFetcher(service, chainBuilder) {
            @Override
            public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates,
                    SignatureValidationProperties properties) {
                throw new RuntimeException("Simulated failure in pivot file download");
            }
        });

        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.FAILED_TO_FETCH_COUNTRY_SPECIFIC_LOTL,
                            count = 1)
            }
    )
    public void cacheRefreshWithExceptionDoesNotUpdateCacheWithCountrySpecific2() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
            @Override
            public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                    ValidatorChainBuilder builder) {
                throw new RuntimeException("Simulated failure in country specific Lotl file download");
            }
        });

        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }


    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.NO_COUNTRY_SPECIFIC_LOTL_FETCHED,
                            count = 1)
            }
    )
    public void cacheRefreshWithReturningNullDoesNotThrow() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
            @Override
            public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                    ValidatorChainBuilder builder) {
                return null;
            }
        });

        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED,
                            count = 1)
            }
    )
    public void cacheRefreshWithSomeSpecificCountryFailuresDoesNotUpdateCache() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();

        LotlService service = new LotlService(chainBuilder);
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        service.initializeCache();

        // Simulate a failure in the cache refresh
        service.withCountrySpecificLotlFetcher(new CountrySpecificLotlFetcher(service) {
            @Override
            public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                    ValidatorChainBuilder builder) {
                HashMap<String, Result> result = new HashMap<>();
                Result r = new Result();
                r.getLocalReport().addReportItem(new ReportItem(LotlValidator.LOTL_VALIDATION,
                        "Simulated invalid country specific Lotl", ReportItem.ReportItemStatus.INVALID));
                r.setCountrySpecificLotl(new CountrySpecificLotl("NL", "NL", "application/xml"));
                result.put("NL", r);
                return result;

            }
        });

        AssertUtil.doesNotThrow(() -> {
            service.tryAndRefreshCache();
        });
    }

    @Test
    public void useOwnCountrySpecificLotlFetcher() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        LotlService service = new LotlService(chainBuilder);
        CountrySpecificLotlFetcher f = new CountrySpecificLotlFetcher(service) {
            @Override
            public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                    ValidatorChainBuilder builder) {
                return Collections.<String, Result>emptyMap();
            }
        };
        AssertUtil.doesNotThrow(() -> {
            chainBuilder.withLotlValidator(() -> new LotlValidator(chainBuilder).withService(
                    new LotlService(chainBuilder).withCountrySpecificLotlFetcher(f)));
        });
    }

    @Test
    public void lotlBytesThrowsPdfException() {
        LotlFetchingProperties p = new LotlFetchingProperties(new IgnoreCountrySpecificCertificates());

        p.setCountryNames("NL");
        LotlService service = new LotlService(new ValidatorChainBuilder().withLotlFetchingProperties(p));

        EuropeanLotlFetcher lotlByteFetcher = new EuropeanLotlFetcher(service) {
            @Override
            public Result fetch() {
                throw new RuntimeException("Test exception");
            }
        };

        service.withEULotlFetcher(lotlByteFetcher);

        Exception e = assertThrows(RuntimeException.class, () -> {
            service.initializeCache();
        });
        Assertions.assertEquals("Test exception", e.getMessage());
    }

    @Test
    public void cacheInitializationWithSomeSpecificCountryThatWorksTest() {
        ValidatorChainBuilder chainBuilder = new LotlEnableValidatorChainBuilder();
        chainBuilder.getLotlFetchingProperties().setCountryNames("NL");
        LotlService lotlService = new LotlService(chainBuilder);
        lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOL_FILES));
        AssertUtil.doesNotThrow(() -> {
            lotlService.initializeCache();
        });
    }

    static final class LotlEnableValidatorChainBuilder extends ValidatorChainBuilder {

        @Override
        public LotlFetchingProperties getLotlFetchingProperties() {
            LotlFetchingProperties properties = super.getLotlFetchingProperties();
            if (properties == null) {
                properties = new LotlFetchingProperties(new IgnoreCountrySpecificCertificates());
                withLotlFetchingProperties(properties);
            }
            return super.getLotlFetchingProperties();
        }
    }


}

