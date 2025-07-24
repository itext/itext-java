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
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

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
        LotlFetchingProperties properties = new LotlFetchingProperties(new IgnoreCountrySpecificCertificates());
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            LotlFetchingProperties f = properties.setCacheStalenessInMilliseconds(0L);
        }, "Maximum staleness must be greater than 0");
        assertEquals(SignExceptionMessageConstant.STALENESS_MUST_BE_POSITIVE, e.getMessage());
    }

    @Test
    public void testWithPivotFetcher() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates())
                .setCountryNames("NL")
        );
        LotlService lotlService = new LotlService(builder);
        AssertUtil.doesNotThrow(() -> {
            lotlService.withPivotFetcher(new PivotFetcher(lotlService, builder));
        });
    }

    @Test
    public void testCacheFailCallsDownloadOfMainLotlFile() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        LotlService lotlService = new LotlService(builder);

        lotlService.withCache(new CacheReturnsNull());
        lotlService.withEULotlFetcher(
                new EuropeanLotlFetcher(lotlService) {
                    @Override
                    public Result fetch() {
                        return new Result("abc".getBytes(StandardCharsets.UTF_8));
                    }
                }
        );

        EuropeanLotlFetcher.Result f = lotlService.getLotlBytes();
        Assertions.assertNotNull(f);
        Assertions.assertArrayEquals("abc".getBytes(StandardCharsets.UTF_8), f.getLotlXml());

    }

    @Test
    public void testCacheFailCallsDownloadOfEUJournalCertificates() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        LotlService lotlService = new LotlService(builder);

        lotlService.withCache(new CacheReturnsNull());

        lotlService.withDefaultEuropeanResourceFetcher(
                new EuropeanResourceFetcher() {
                    @Override
                    public Result getEUJournalCertificates() {
                        Result result = new Result();
                        result.setCertificates(Collections.<Certificate>emptyList());
                        return result;
                    }
                }
        );
        EuropeanResourceFetcher.Result f = lotlService.getEUJournalCertificates();
        Assertions.assertNotNull(f);
        Assertions.assertTrue(f.getCertificates().isEmpty());
    }

    @Test
    public void testCacheFailCallsDownloadOfPivotFile() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        LotlService lotlService = new LotlService(builder);

        lotlService.withCache(new CacheReturnsNull());

        lotlService.withPivotFetcher(
                new PivotFetcher(lotlService, builder) {

                    @Override
                    public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates,
                            SignatureValidationProperties properties) {
                        Result result = new Result();
                        result.setPivotUrls(Collections.singletonList("http://example.com/pivot.xml"));
                        return result;
                    }
                }
        );
        PivotFetcher.Result f = lotlService.getAndValidatePivotFiles("abc".getBytes(StandardCharsets.UTF_8),
                Collections.<Certificate>emptyList(),
                new SignatureValidationProperties());
        Assertions.assertNotNull(f);
        Assertions.assertEquals(1, f.getPivotUrls().size());
    }


    @Test
    public void testCacheFailCallsDownloadOfCountrySpecificLotl() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        LotlService lotlService = new LotlService(builder);

        lotlService.withCache(new CacheReturnsNull());

        lotlService.withCountrySpecificLotlFetcher(
                new CountrySpecificLotlFetcher(lotlService) {

                    @Override
                    public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml,
                            ValidatorChainBuilder builder) {
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
        List<CountrySpecificLotlFetcher.Result> f = lotlService.getCountrySpecificLotlFiles(null,
                new ValidatorChainBuilder());
        Assertions.assertNotNull(f);
        Assertions.assertEquals(1, f.size());
        Assertions.assertEquals("NL", f.get(0).getCountrySpecificLotl().getSchemeTerritory());
        Assertions.assertEquals("http://example.com/lotl.xml", f.get(0).getCountrySpecificLotl().getTslLocation());
    }


    @Test
    public void testCacheRefreshIsFiringButWaitsDelay() throws InterruptedException {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        builder.getLotlFetchingProperties().setRefreshIntervalCalculator(
                (l) -> {
                    return 500;
                }
        );
        AtomicLong refreshCounter = new AtomicLong(0);
        LotlService lotlService = new LotlService(builder) {
            @Override
            public void initializeCache() {
            }

            @Override
            protected void tryAndRefreshCache() {
                refreshCounter.incrementAndGet();
            }
        };
        Thread.sleep(50);
        Assertions.assertTrue(refreshCounter.get() == 0,
                "Refresh counter should be greater than 8, but was: " + refreshCounter.get());
    }


    @Test
    public void testCacheRefreshIsFiring() throws InterruptedException {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        builder.getLotlFetchingProperties().setRefreshIntervalCalculator(
                (l) -> {
                    //100 milliseconds
                    return 100;
                }
        );
        AtomicLong refreshCounter = new AtomicLong(0);
        LotlService lotlService = new LotlService(builder) {
            @Override
            public void initializeCache() {
            }

            @Override
            protected void tryAndRefreshCache() {
                refreshCounter.incrementAndGet();
            }
        };
        Thread.sleep(1000);
        Assertions.assertTrue(refreshCounter.get() >= 8,
                "Refresh counter should be greater than 8, but was: " + refreshCounter.get());
    }

    @Test
    public void withCustomRefreshRate() throws InterruptedException {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        builder.withLotlFetchingProperties(new LotlFetchingProperties(new IgnoreCountrySpecificCertificates()));
        AtomicLong refreshCounter = new AtomicLong(0);
        builder.getLotlFetchingProperties().setRefreshIntervalCalculator(
                (l) -> {
                    //100 milliseconds
                    return 100;
                }
        );
        LotlService lotlService = new LotlService(builder) {
            @Override
            public void initializeCache() {
            }

            @Override
            protected void tryAndRefreshCache() {
                refreshCounter.incrementAndGet();
            }
        };
        Thread.sleep(1000);
        Assertions.assertTrue(refreshCounter.get() > 5,
                "Refresh counter should be greater than 10, but was: " + refreshCounter.get());
    }

    static final class CacheReturnsNull implements LotlServiceCache {

        @Override
        public void setAllValues(EuropeanLotlFetcher.Result lotlXml,
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
        public EuropeanLotlFetcher.Result getLotlResult() {
            return null;
        }

        @Override
        public void setLotlResult(EuropeanLotlFetcher.Result data) {

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