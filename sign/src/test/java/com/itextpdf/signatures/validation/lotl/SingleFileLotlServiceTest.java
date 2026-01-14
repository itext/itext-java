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

import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class SingleFileLotlServiceTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/lotl/SingleFileLotlServiceTest/";
    private static final String argentinaCertificate =
            //Argentina
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIH0jCCBbqgAwIBAgITfwAatU+P4G1P34vbHgABABq1TzANBgkqhkiG9w0BAQsFADCCAUwxCzAJ\n" +
                    "BgNVBAYTAkFSMSkwJwYDVQQIDCBDaXVkYWQgQXV0w7Nub21hIGRlIEJ1ZW5vcyBBaXJlczEzMDEG\n" +
                    "A1UECwwqU3Vic2VjcmV0YXLDrWEgZGUgVGVjbm9sb2fDrWFzIGRlIEdlc3Rpw7NuMSkwJwYDVQQL\n" +
                    "DCBTZWNyZXRhcsOtYSBkZSBHZXN0acOzbiBQw7pibGljYTE5MDcGA1UECwwwT2ZpY2luYSBOYWNp\n" +
                    "b25hbCBkZSBUZWNub2xvZ8OtYXMgZGUgSW5mb3JtYWNpw7NuMSowKAYDVQQKDCFKZWZhdHVyYSBk\n" +
                    "ZSBHYWJpbmV0ZSBkZSBNaW5pc3Ryb3MxGTAXBgNVBAUTEENVSVQgMzA2ODA2MDQ1NzIxMDAuBgNV\n" +
                    "BAMMJ0F1dG9yaWRhZCBDZXJ0aWZpY2FudGUgZGUgRmlybWEgRGlnaXRhbDAeFw0yNTA3MTYxNzU0\n" +
                    "MzFaFw0yNzA3MTYxNzU0MzFaMEkxGTAXBgNVBAUTEENVSUwgMjAyODQyMTAzMzcxCzAJBgNVBAYT\n" +
                    "AkFSMR8wHQYDVQQDExZDQVJSSVpPIEVucmlxdWUgTWFudWVsMIIBIjANBgkqhkiG9w0BAQEFAAOC\n" +
                    "AQ8AMIIBCgKCAQEA4SVCvix4shhKgqI/mR0S5br+LvYUDb2BYBAxvYgZ3MXzw3jyNB+UTCubWP01\n" +
                    "wawXTlPIr5HXoRVK7fIJVe5MD5wzqZ2s5yf2Hoy1MVYrNGv/CpY6jyqsy3momOeuiekjdMqLlnpy\n" +
                    "BQbPoCBTyigrwuyqUVVwjGIwAJniCiT10Vmbk7uQmdJZLHC8zTCLZWlMX6pGLbHSfBThoeII1uc4\n" +
                    "FUuUkRJWsovGldMPzVkqadF8r524sllgp/+uRJGysa+cWm1+0e7aXGHcbn+VOZdMwW0fn0ghGRpZ\n" +
                    "xiKsosVBhRIb/8D05pbFbFqHSh8PRAiUG02x/H7yqhtIPtWw1YdkRQIDAQABo4ICrDCCAqgwDgYD\n" +
                    "VR0PAQH/BAQDAgTwMCQGCCsGAQUFBwEDBBgwFjAUBggrBgEFBQcLAjAIBgZgIAEKAgIwHQYDVR0O\n" +
                    "BBYEFG5jlfV+0DMzizj9EmgBdnMPukyDMB0GA1UdEQQWMBSBEnFjYXJyaXpvQGdtYWlsLmNvbTAf\n" +
                    "BgNVHSMEGDAWgBSMG/0ubHBsM5IabSTMm8w/mcXrqDBXBgNVHR8EUDBOMEygSqBIhiBodHRwOi8v\n" +
                    "cGtpLmpnbS5nb3YuYXIvY3JsL0ZELmNybIYkaHR0cDovL3BraWNvbnQuamdtLmdvdi5hci9jcmwv\n" +
                    "RkQuY3JsMIHWBggrBgEFBQcBAQSByTCBxjA1BggrBgEFBQcwAoYpaHR0cDovL3BraS5qZ20uZ292\n" +
                    "LmFyL2FpYS9jYWZkT05USSgxKS5jcnQwOQYIKwYBBQUHMAKGLWh0dHA6Ly9wa2ljb250LmpnbS5n\n" +
                    "b3YuYXIvYWlhL2NhZmRPTlRJKDEpLmNydDAmBggrBgEFBQcwAYYaaHR0cDovL3BraS5qZ20uZ292\n" +
                    "LmFyL29jc3AwKgYIKwYBBQUHMAGGHmh0dHA6Ly9wa2ljb250LmpnbS5nb3YuYXIvb2NzcDAMBgNV\n" +
                    "HRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDAnBgkrBgEEAYI3FQoEGjAY\n" +
                    "MAoGCCsGAQUFBwMCMAoGCCsGAQUFBwMEMEQGCSqGSIb3DQEJDwQ3MDUwDgYIKoZIhvcNAwICAgCA\n" +
                    "MA4GCCqGSIb3DQMEAgIAgDAHBgUrDgMCBzAKBggqhkiG9w0DBzBDBgNVHSAEPDA6MDgGBWAgAQED\n" +
                    "MC8wLQYIKwYBBQUHAgEWIWh0dHA6Ly9wa2kuamdtLmdvdi5hci9jcHMvY3BzLnBkZjANBgkqhkiG\n" +
                    "9w0BAQsFAAOCAgEAyCrHfYvfMKXqrVbxGlAySQSVsDz7fZrYxKU5qXbB/+jCj1IY0ENHLN5q77HY\n" +
                    "M7eomHM8nX9zpXl0e4nV5PLaSwykmW9ERw6EdqhzzcFGJopYriLfQMbVGvhsJVY+/zhQ4//RwFle\n" +
                    "SlkZUZZt92qEQwuau/DLdCfOGq/Xm0c1qpx+I8+sdQKRz7x3iLLExHa7U+dO6pe/ropNSJabKyP5\n" +
                    "ZOXHa2B4s2LRx/qTScfPhg8B06nrLLWnG0QMgpjfEWHL3uPcNI9iMJxRTp7S6Q+a8fQK4Jv2WyLy\n" +
                    "9m2ETKAw5HpF0Mbu6yb9/xNkg/zQCY4Ooy7DHxs67j9ETJ+iP1Fl4+nBFYkCkiqlKreeSicPl+fn\n" +
                    "vcXcdDZto3UJBqpbW6PVGaYkUQRy8cgy2FANMS8y6P6u8dddiQ0a4XatvVAUgrDlPKxyYQ0ZK4Hw\n" +
                    "ggSqcOklFplKSJZyj0Wt1jqF6dtTjxqaMUvqbco/0Zp8fKtMXlqvNu1DbhpIF0Ast/M8DocHuxTM\n" +
                    "+b7B1IYmiSZ292aT1ZVYoFww3o3NQuuxNM7CN8GPOF3a09dptvb1NIyWX54DeRX/HF/V2R92vjvE\n" +
                    "ukxaNWIIgv/J496j/Hp+p/N5OR0TEIy42DMj1UHWpCy9CGWYS9sYRW8d1EKUu7JHMFkbRdZ4b22b\n" +
                    "uRsuF6vI08u1AQE=\n" +
                    "-----END CERTIFICATE-----";

    @ParameterizedTest(name = "Test {index}: {0} - {1}")
    @MethodSource("provideCountryData")
    public void testDifferentCountries(CountrySpecificLotl countrySpecificLotl, String certificate) {
        // TODO DEVSIX-9626: To create an investigation ticket
        if ("MX".equals(countrySpecificLotl.getSchemeTerritory()) ||
                "RS".equals(countrySpecificLotl.getSchemeTerritory())) {
            return;
        }

        System.out.println("Testing country: " + countrySpecificLotl.getSchemeTerritory() +
                " with TSL: " + countrySpecificLotl.getTslLocation());

        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        LotlService lotlService = new SingleFileLotlService(props, countrySpecificLotl,
                Collections.singletonList(certificate));
        lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER));
        LotlValidator validator = new LotlValidator(lotlService);
        lotlService.withLotlValidator(() -> validator);
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder()
                .withLotlService(() -> lotlService)
                .trustEuropeanLotl(true);
        lotlService.initializeCache();

        LotlTrustedStore lotlTrustedStore = chainBuilder.getLotlTrustedStore();
        Set<Certificate> certificates =  lotlTrustedStore.getCertificates();
        System.out.println(
                "total Certificates for " + countrySpecificLotl.getTslLocation() + " : " + certificates.size());
        ValidationReport report = lotlTrustedStore.getLotlValidationReport();
        System.out.println(report);
        Assertions.assertTrue(report.getFailures().isEmpty());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SignLogMessageConstant.SCHEMA_NAMES_CONFIGURATION_PROPERTY_IGNORED,
                    logLevel = LogLevelConstants.WARN)
    })
    public void countryNamesIgnoredTest() {
        LotlFetchingProperties props = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        props.setCountryNames("AR");
        AssertUtil.doesNotThrow(() -> {
            LotlService lotlService = new SingleFileLotlService(props,
                    new CountrySpecificLotl("AR", "https://pki.jgm.gov.ar/TSL/tsl-AR.xml",
                    "application/vnd.etsi.tsl+xml"),
                    Collections.singletonList(argentinaCertificate));
        });
    }

    private static Iterable<Object[]> provideCountryData() {
        final List<CountrySpecificLotl> countrySpecificLotls = Arrays.asList(
                new CountrySpecificLotl("AR", "https://pki.jgm.gov.ar/TSL/tsl-AR.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("CH", "https://trustedlist.tsl-switzerland.ch/tsl-ch.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("BR", "https://validar.iti.gov.br/trustlist/trust-list-BR.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("MX", "https://www.cloudb.sat.gob.mx/TSP_MX.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("UK", "https://tl.ico.org.uk/uktrustedlist/UKTL.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("GE", "https://dga.gov.ge/files/GeorgianTSL.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("RS", "https://www.mit.gov.rs/TrustedList/TSL-RS.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("MK", "https://trusteid.mdt.gov.mk/TrustedList/TL-MK.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("CL",
                        "https://www.entidadacreditadora.gob.cl/wp-content/uploads/2025/06/CL-TSL.xml",
                        "application/vnd.etsi.tsl+xml"),
                new CountrySpecificLotl("UR",
                        "https://www.gub.uy/unidad-certificacion-electronica/sites/unidad-certificacion-electronica/files/2022-11/tsl_uy.xml",
                        "application/vnd.etsi.tsl+xml")
        );

        final List<String> certificates = Arrays.asList(
                //Argentina
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIH0jCCBbqgAwIBAgITfwAatU+P4G1P34vbHgABABq1TzANBgkqhkiG9w0BAQsFADCCAUwxCzAJ\n" +
                        "BgNVBAYTAkFSMSkwJwYDVQQIDCBDaXVkYWQgQXV0w7Nub21hIGRlIEJ1ZW5vcyBBaXJlczEzMDEG\n" +
                        "A1UECwwqU3Vic2VjcmV0YXLDrWEgZGUgVGVjbm9sb2fDrWFzIGRlIEdlc3Rpw7NuMSkwJwYDVQQL\n" +
                        "DCBTZWNyZXRhcsOtYSBkZSBHZXN0acOzbiBQw7pibGljYTE5MDcGA1UECwwwT2ZpY2luYSBOYWNp\n" +
                        "b25hbCBkZSBUZWNub2xvZ8OtYXMgZGUgSW5mb3JtYWNpw7NuMSowKAYDVQQKDCFKZWZhdHVyYSBk\n" +
                        "ZSBHYWJpbmV0ZSBkZSBNaW5pc3Ryb3MxGTAXBgNVBAUTEENVSVQgMzA2ODA2MDQ1NzIxMDAuBgNV\n" +
                        "BAMMJ0F1dG9yaWRhZCBDZXJ0aWZpY2FudGUgZGUgRmlybWEgRGlnaXRhbDAeFw0yNTA3MTYxNzU0\n" +
                        "MzFaFw0yNzA3MTYxNzU0MzFaMEkxGTAXBgNVBAUTEENVSUwgMjAyODQyMTAzMzcxCzAJBgNVBAYT\n" +
                        "AkFSMR8wHQYDVQQDExZDQVJSSVpPIEVucmlxdWUgTWFudWVsMIIBIjANBgkqhkiG9w0BAQEFAAOC\n" +
                        "AQ8AMIIBCgKCAQEA4SVCvix4shhKgqI/mR0S5br+LvYUDb2BYBAxvYgZ3MXzw3jyNB+UTCubWP01\n" +
                        "wawXTlPIr5HXoRVK7fIJVe5MD5wzqZ2s5yf2Hoy1MVYrNGv/CpY6jyqsy3momOeuiekjdMqLlnpy\n" +
                        "BQbPoCBTyigrwuyqUVVwjGIwAJniCiT10Vmbk7uQmdJZLHC8zTCLZWlMX6pGLbHSfBThoeII1uc4\n" +
                        "FUuUkRJWsovGldMPzVkqadF8r524sllgp/+uRJGysa+cWm1+0e7aXGHcbn+VOZdMwW0fn0ghGRpZ\n" +
                        "xiKsosVBhRIb/8D05pbFbFqHSh8PRAiUG02x/H7yqhtIPtWw1YdkRQIDAQABo4ICrDCCAqgwDgYD\n" +
                        "VR0PAQH/BAQDAgTwMCQGCCsGAQUFBwEDBBgwFjAUBggrBgEFBQcLAjAIBgZgIAEKAgIwHQYDVR0O\n" +
                        "BBYEFG5jlfV+0DMzizj9EmgBdnMPukyDMB0GA1UdEQQWMBSBEnFjYXJyaXpvQGdtYWlsLmNvbTAf\n" +
                        "BgNVHSMEGDAWgBSMG/0ubHBsM5IabSTMm8w/mcXrqDBXBgNVHR8EUDBOMEygSqBIhiBodHRwOi8v\n" +
                        "cGtpLmpnbS5nb3YuYXIvY3JsL0ZELmNybIYkaHR0cDovL3BraWNvbnQuamdtLmdvdi5hci9jcmwv\n" +
                        "RkQuY3JsMIHWBggrBgEFBQcBAQSByTCBxjA1BggrBgEFBQcwAoYpaHR0cDovL3BraS5qZ20uZ292\n" +
                        "LmFyL2FpYS9jYWZkT05USSgxKS5jcnQwOQYIKwYBBQUHMAKGLWh0dHA6Ly9wa2ljb250LmpnbS5n\n" +
                        "b3YuYXIvYWlhL2NhZmRPTlRJKDEpLmNydDAmBggrBgEFBQcwAYYaaHR0cDovL3BraS5qZ20uZ292\n" +
                        "LmFyL29jc3AwKgYIKwYBBQUHMAGGHmh0dHA6Ly9wa2ljb250LmpnbS5nb3YuYXIvb2NzcDAMBgNV\n" +
                        "HRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDAnBgkrBgEEAYI3FQoEGjAY\n" +
                        "MAoGCCsGAQUFBwMCMAoGCCsGAQUFBwMEMEQGCSqGSIb3DQEJDwQ3MDUwDgYIKoZIhvcNAwICAgCA\n" +
                        "MA4GCCqGSIb3DQMEAgIAgDAHBgUrDgMCBzAKBggqhkiG9w0DBzBDBgNVHSAEPDA6MDgGBWAgAQED\n" +
                        "MC8wLQYIKwYBBQUHAgEWIWh0dHA6Ly9wa2kuamdtLmdvdi5hci9jcHMvY3BzLnBkZjANBgkqhkiG\n" +
                        "9w0BAQsFAAOCAgEAyCrHfYvfMKXqrVbxGlAySQSVsDz7fZrYxKU5qXbB/+jCj1IY0ENHLN5q77HY\n" +
                        "M7eomHM8nX9zpXl0e4nV5PLaSwykmW9ERw6EdqhzzcFGJopYriLfQMbVGvhsJVY+/zhQ4//RwFle\n" +
                        "SlkZUZZt92qEQwuau/DLdCfOGq/Xm0c1qpx+I8+sdQKRz7x3iLLExHa7U+dO6pe/ropNSJabKyP5\n" +
                        "ZOXHa2B4s2LRx/qTScfPhg8B06nrLLWnG0QMgpjfEWHL3uPcNI9iMJxRTp7S6Q+a8fQK4Jv2WyLy\n" +
                        "9m2ETKAw5HpF0Mbu6yb9/xNkg/zQCY4Ooy7DHxs67j9ETJ+iP1Fl4+nBFYkCkiqlKreeSicPl+fn\n" +
                        "vcXcdDZto3UJBqpbW6PVGaYkUQRy8cgy2FANMS8y6P6u8dddiQ0a4XatvVAUgrDlPKxyYQ0ZK4Hw\n" +
                        "ggSqcOklFplKSJZyj0Wt1jqF6dtTjxqaMUvqbco/0Zp8fKtMXlqvNu1DbhpIF0Ast/M8DocHuxTM\n" +
                        "+b7B1IYmiSZ292aT1ZVYoFww3o3NQuuxNM7CN8GPOF3a09dptvb1NIyWX54DeRX/HF/V2R92vjvE\n" +
                        "ukxaNWIIgv/J496j/Hp+p/N5OR0TEIy42DMj1UHWpCy9CGWYS9sYRW8d1EKUu7JHMFkbRdZ4b22b\n" +
                        "uRsuF6vI08u1AQE=\n" +
                        "-----END CERTIFICATE-----",
                //Switzerland
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIILTCCBhWgAwIBAgIQCxu1oDgenaG7qadjNX4HcDANBgkqhkiG9w0BAQsFADCB\n" +
                        "tzELMAkGA1UEBhMCQ0gxHjAcBgNVBGETFVZBVENILUNIRS0yMjEuMDMyLjU3MzE+\n" +
                        "MDwGA1UEChM1QnVuZGVzYW10IGZ1ZXIgSW5mb3JtYXRpayB1bmQgVGVsZWtvbW11\n" +
                        "bmlrYXRpb24gKEJJVCkxHTAbBgNVBAsTFFN3aXNzIEdvdmVybm1lbnQgUEtJMSkw\n" +
                        "JwYDVQQDEyBTd2lzcyBHb3Zlcm5tZW50IFJlZ3VsYXRlZCBDQSAwMjAeFw0yMzEy\n" +
                        "MTUxMDU1MDZaFw0yNjEyMTUxMDU1MDZaMIIBUjELMAkGA1UEBhMCQ0gxCzAJBgNV\n" +
                        "BAgMAkJFMQ0wCwYDVQQHDARCZXJuMRowGAYDVQQPDBFHb3Zlcm5tZW50IEVudGl0\n" +
                        "eTEeMBwGA1UEYRMVTlRSQ0gtQ0hFLTExMy44NDYuODkyMTEwLwYDVQQKDChTY2h3\n" +
                        "ZWl6ZXJpc2NoZSBBa2tyZWRpdGllcnVuZ3NzdGVsbGUgU0FTMRowGAYDVQQLDBFH\n" +
                        "RSAtIDAyMjAg4oCTIFNBUzFHMEUGA1UECww+U2NoZW1lIG9wZXJhdG9yIGluIGNo\n" +
                        "YXJnZSBvZiBwdWJsaXNoaW5nIHRoZSBTd2lzcyB0cnVzdGVkIGxpc3QxUzBRBgNV\n" +
                        "BAMMSlN3aXNzIEFjY3JlZGl0YXRpb24gU2VydmljZSBTQVMgLyBTY2h3ZWl6ZXJp\n" +
                        "c2NoZSBBa2tyZWRpdGllcnVuZ3NzdGVsbGUgU0FTMIIBIjANBgkqhkiG9w0BAQEF\n" +
                        "AAOCAQ8AMIIBCgKCAQEAuD+WxXh/UqTMEJRWjWyQq5El21opL/mvtD2P1jhdnUYZ\n" +
                        "MTBVzI4UH5KYy1owy4x/aykhSwLrwE51Rwc0ZI41n0FYy+HV2lV0I6bYsbDJZm/y\n" +
                        "Mw57WzCQLXfXsfdYdHL2hjn/OnZHtbqg05wQHVE1IW2Ujb4Wyo9Ty9GFo3VpQvdW\n" +
                        "/rT4EP7f8xWg8cBljVZilfqZd2v7cFYzMaOHDbpINcJeA0q0oR6p3qutR6uTBlHP\n" +
                        "K52IYAlUZZq2hSM6oEp5+GtRfu2qK9Hu2byuh+UhGvM4IszJX3GdsDAAEC2O3mRM\n" +
                        "BK30XrxIR2HONV9GzVWZZnT4MzwLsiE2XtIIXZiBRQIDAQABo4IClTCCApEwFgYD\n" +
                        "VR0RBA8wDYELaW5mb0BzYXMuY2gwDgYDVR0PAQH/BAQDAgeAMIHXBgNVHSAEgc8w\n" +
                        "gcwwgckGCWCFdAERAwUCBzCBuzBDBggrBgEFBQcCARY3aHR0cDovL3d3dy5wa2ku\n" +
                        "YWRtaW4uY2gvY3BzL0NQU18yXzE2Xzc1Nl8xXzE3XzNfNV8wLnBkZjB0BggrBgEF\n" +
                        "BQcCAjBoDGZUaGlzIGlzIGEgcmVndWxhdGVkIGNlcnRpZmljYXRlIGZvciBsZWdh\n" +
                        "bCBwZXJzb25zIGFzIGRlZmluZWQgYnkgdGhlIFN3aXNzIGZlZGVyYWwgbGF3IFNS\n" +
                        "IDk0My4wMyBaZXJ0RVMwPgYDVR0fBDcwNTAzoDGgL4YtaHR0cDovL3d3dy5wa2ku\n" +
                        "YWRtaW4uY2gvY3JsL1JlZ3VsYXRlZENBMDIuY3JsMHcGCCsGAQUFBwEBBGswaTA5\n" +
                        "BggrBgEFBQcwAoYtaHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvYWlhL1JlZ3VsYXRl\n" +
                        "ZENBMDIuY3J0MCwGCCsGAQUFBzABhiBodHRwOi8vd3d3LnBraS5hZG1pbi5jaC9h\n" +
                        "aWEvb2NzcDCBkwYIKwYBBQUHAQMEgYYwgYMwCgYIKwYBBQUHCwIwCQYHBACL7EkB\n" +
                        "AjAIBgYEAI5GAQQwSwYGBACORgEFMEEwPxY5aHR0cDovL3d3dy5wa2kuYWRtaW4u\n" +
                        "Y2gvY3BzL1BEUy1TR1BLSV9SZWd1bGF0ZWRfQ0FfMDIucGRmEwJFTjATBgYEAI5G\n" +
                        "AQYwCQYHBACORgEGAjAfBgNVHSMEGDAWgBQ0C77yFT6kD1cH4kFkizIR0SkLYTAd\n" +
                        "BgNVHQ4EFgQU+AwDWo0KwmB6Am9xxQhZ4/DaceQwDQYJKoZIhvcNAQELBQADggIB\n" +
                        "AH1wZJuqTlReUT2bmNMqYC2VKA7gkXP1Ov7Q3E218P9qBPmF+laPIdXPrHzOVllN\n" +
                        "JQ/SH9/vOupbd34tHfaQkNwyQOXnLzPhXS8/mScQ+aGXIHYlsWvXYr5UoCuEOUuy\n" +
                        "koahiU7ahb9cVQHyPa5PA1zs+uK91GHKc4I6cnD7A93OW1iroVkonOTWkzdGisok\n" +
                        "diyw6YMahWbXdM3YfuufGlL8NBccf6P6G3WsLh/UOjGXGZ7RwB/kJ0odlirYsTBV\n" +
                        "oMFwarDc5n/PapK7hDYrUzAu/IWnITngR2lp++Q1aCIIt0iUrE3Jh/fvuXAiTG8z\n" +
                        "BgZNxfCtmLaXw8i+ykxU10ScpTshzPh5bJNvBX342sJtCsD2k4A1Kwp6yEV6rHG5\n" +
                        "8pdSDNS8rLMd4cpVnHV2quEJ7gtQr/jqP/m2EZ1/2+Acf6tv6Yl9+p5G3kCBfQoQ\n" +
                        "Z6tcIJE4zMIIZhAdgcWyHnNYa2qiu1LDbE75zarB3x2Lhlev9nCKtIwaSKFxP2Pr\n" +
                        "PthhJuAYLiJpUxvylK+Zhd/it50I3W6+STDp+ezl1KpZ0gUfsgR91TYE2eQqh66S\n" +
                        "OiA32fv/BXV5g615PuumJrp0AowyPkK4XUTUKfcD0Sm9Su7KxlWjoMTPDfv+ec6D\n" +
                        "+ZwULHi36S20fOPR2eLfdUsLZTiyQW1jxiPhaGm9jc7d\n" +
                        "-----END CERTIFICATE-----",
                //Brazil
                "-----BEGIN CERTIFICATE-----\n" +
                        "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\n"
                        +
                        "-----END CERTIFICATE-----",
                //Mexiko
                "-----BEGIN CERTIFICATE-----\n" +
                        "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\n"
                        +
                        "-----END CERTIFICATE-----",
                //UK
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIEdjCCAt6gAwIBAgIEYy22yDANBgkqhkiG9w0BAQsFADBTMQswCQYDVQQGEwJVSzEsMCoGA1UECgwjSW5mb3JtYXRpb24gQ29tbWlzc2lvbmVy4oCZcyBPZmZpY2UxFjAUBgNVBAMMDVRMIFNpZ25pbmcgMDEwHhcNMjIwOTIzMTMzODE2WhcNMjgwOTIzMTMzODE2WjBTMQswCQYDVQQGEwJVSzEsMCoGA1UECgwjSW5mb3JtYXRpb24gQ29tbWlzc2lvbmVy4oCZcyBPZmZpY2UxFjAUBgNVBAMMDVRMIFNpZ25pbmcgMDEwggGiMA0GCSqGSIb3DQEBAQUAA4IBjwAwggGKAoIBgQCW2DauxDAvJsHGQcR+AMWE9L5p69pb7z50O7OLxNv/PMnTZCEaXwru2VQND6w39HZ0fF0RpNixJDOM8+oCSIdK6OhXozOZQwM/FsLDF3rQQMpKwYp76YFJo7C4CVVTN1mKo1HdZr3XE3KjI6rfqe1eeRr8zFy+IZIC0lLhoU/I6XYQqdaus5Mlp4yWswmTmkeFRYij7eLyPA5bQj2LG2svp/kVQ75uIZifJQbvzQSzbv8nOqMSPPTTrypcjIL7OQKk3TQa7ejsHo3jQOpjV3uVbJcj+EATpDqZ5c5ELOWpUJ5lW1oR2Jke7uNeMrcsrLLOBw+KyGsAq5YSs9hFM5whdwP7ZVHjUvKV0pBfVmsmSg0XDODrUtZY2mG9NfJCi8S079AWByeoFpzIKyEbeRBkAZX24f/BM1KX2/6JFFxv3JUTuFOA1t+ZjlcZxOXIePI/RVBrpbew/tOgTPEdrJPe/Ye2VGr8Ih/7iTTCfXA2hf7asBv9fhWom7YfgBa1e0kCAwEAAaNSMFAwDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBkAwHQYDVR0OBBYEFL83w5OEBK4qbQEKUIxmn5zWHqLpMBEGA1UdJQQKMAgGBgQAkTcDADANBgkqhkiG9w0BAQsFAAOCAYEATxzQSuhdfPQSmtvbgIO33cOoTYz9344G+WKtl/wrsy5NrmVuAaHdckDz4BOY1mWx0b0VQ4Y9GDJL1ZfIEuSzUbAbSSagHcDRiC4sRVSq1g/y06neKVPz+/uWUiNsJRf/4OkZaOEQmi1a/Mf7fRZnnLVTsudJRJhgPGVRFM6hkpWbPDKRH+pa9TZcRHhcv3EGFlGZ+w5M4+ly5PCeCeXJ7cHX6Lpp73IUSDMLfjgDoLJFuxs0a5YI9E5uJRJNrm8r4BFhZuiBL1XYAGdNKk/2UKRBjg9XZXFYOrzZPJQs/rgE3iq/ysxbE3+6upAYJalmHeok7R1oaUsQLzoPDkieopDWuBjryx6h2Ewh1PisvDP2Rq37ErLxDbv+0bhn6l3CmkWxEunKsnX1qo2C+C1v7ig2uMLb63uFKvkmtx5qRUafIR6FB1snc9Gjd7PVx+MYIHsIzbO+fqJVPyAJEJwWspJRwP6vJZFFeF6Q6j2WB3ntxFTNI7IWLD8CRuveAv3m\n"
                        +
                        "-----END CERTIFICATE-----",
                //Georgia
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIGUzCCBDugAwIBAgIUE6qOf0MFS8vTupRjcS2"
                        +
                        "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\n"
                        +
                        "-----END CERTIFICATE-----",
                //Serbia
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIEazCCA1OgAwIBAgIQFvY"
                        +
                        "/pZ0tvaBDxQlFq6TCIzANBgkqhkiG9w0BAQ0FADCBtTELMAkGA1UEBhMCUlMxFzAVBgNVBGEMDk1COlJTLTE4ODIwNzkwMRgwFgYDVQRhDA9WQVRSUy0xMTMzNDQ1NjMxSzBJBgNVBAoMQlJlcHVibGljIG9mIFNlcmJpYSwgTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIFRlbGVjb21tdW5pY2F0aW9uczEmMCQGA1UEAwwdU2VyYmlhbiBUcnVzdGVkIExpc3QgU2lnbmVyIDEwHhcNMjUwNjEwMTA0NjM0WhcNMjgwNjEwMTA1NjM0WjCBtTELMAkGA1UEBhMCUlMxFzAVBgNVBGEMDk1COlJTLTE4ODIwNzkwMRgwFgYDVQRhDA9WQVRSUy0xMTMzNDQ1NjMxSzBJBgNVBAoMQlJlcHVibGljIG9mIFNlcmJpYSwgTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIFRlbGVjb21tdW5pY2F0aW9uczEmMCQGA1UEAwwdU2VyYmlhbiBUcnVzdGVkIExpc3QgU2lnbmVyIDEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDIYwVZ8MlJ26Za0GQyrvfp1lq7XHcvQOjGuogc+/sQtCNy19WJLJM3hGGr2ygjcJpeqXyc43SsNnZ95xqryBg8VTo9Gm80jcfJ4j58bzuQevbd4kpnVwVQbTZY14JGGOQSBTKmcYuXh/lT9szDPeC+8gBI3Y1jxGqzXyvEbrnxthg1AT9hc7SjS5LX1kkpUHuVrutTTYyoob5AHKhdo5rd1Dy0Vc5j14TrFIYBrSpRC/Fho+Sb8kyD3KsMQQcD9j1UW8yG7AgkvqxfaEzFMNq2AryKdwI8Sa2cSdym1r3JUMoh3/f/YMmqkz6VGIlRi/bz0pwAofwzrtRda7i5CtXpAgMBAAGjdTBzMAkGA1UdEwQCMAAwCwYDVR0PBAQDAgZAMBEGA1UdJQQKMAgGBgQAkTcDADAnBgNVHREEIDAegRx1c2x1Z2VvZHBvdmVyZW5qYUBtaXQuZ292LnJzMB0GA1UdDgQWBBQXdAAU1u9Oi4JZ93051g5BNP4lLDANBgkqhkiG9w0BAQ0FAAOCAQEAY49r2tTNG2JWCOP5oS2dMcPeQC2wZ5smSArgpZZ85/JZ+TjnhoAVEsCGOR/l4ruFfrI4lBpxDeXkPoN+mk5mQ+fabiw/i2VuShbsu0SBz9SZ9ISDUP44jhiR2+Ixqb7uLcZNKlxLgBIyuKCPFaYvNcXttCjAUCPP8a7YolbVzxJiMInzvdKPW0lzg/hoZCAVbjSLqRlakwPPrJun4BQjvL+vywjlGr9Fzon8kK8oFF4ek4Y67tCrcO6ovofHq9KjgjQIj7ufu3isA/d8kHupG8pV+Xvo6yKWY1V5pDd9o80lkjG+C/GgguWpfJadocm50tPZmAuTQaDD+XTM9iMPAQ==\n"
                        +
                        "-----END CERTIFICATE-----",
                //Makedonia
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIErzCCA5egAwIBAgIQcZPNPOnDxYZB0HRR1FxZaTANBgkqhkiG9w0BAQsFADCBnDELMAkGA1UEBhMCTUsxWDBWBgNVBAoMT1JlcHVibGljIG9mIE5vcnRoIE1hY2Vkb25pYSwgTWluaXN0cnkgb2YgaW5mb3JtYXRpb24gc29jaWV0eSBhbmQgYWRtaW5pc3RyYXRpb24xMzAxBgNVBAMMKlRydXN0ZWQgTGlzdCBBZG1pbmlzdHJhdG9yIE5vcnRoIE1hY2Vkb25pYTAeFw0yMjAxMTQxMzA2MjVaFw0yNDAxMTQxMzE2MjVaMIGcMQswCQYDVQQGEwJNSzFYMFYGA1UECgxPUmVwdWJsaWMgb2YgTm9ydGggTWFjZWRvbmlhLCBNaW5pc3RyeSBvZiBpbmZvcm1hdGlvbiBzb2NpZXR5IGFuZCBhZG1pbmlzdHJhdGlvbjEzMDEGA1UEAwwqVHJ1c3RlZCBMaXN0IEFkbWluaXN0cmF0b3IgTm9ydGggTWFjZWRvbmlhMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6pEiVlpY83NVgWWNpItk75524Ar2WKnYc08ad9es/gwSK0PGQUkKociAIcyKb5D96LRBgEHHYcWRE7VKoY8KTikU9hy+Ikk4PopuCpZbAHhGkNkPa9Nn2/VNbo/rLmc9gIAHLGC214E7ROrbSxnKFYeM+33mtX1FemZhspvTBk9Qm2ilUkGMfekbMvYEyjE5YmOCuQeusQek6F32J22fvARjrhmt/No0GVFEGxlkwj1i7gMkKXyDDCOmZXn0mVy/U7B0Dv2FSaRZGlJXyC8HgUy9/4DmBwLlmsEjq9pxO9qzCdRbddBsxV6nNjzlVhhq95AXtMkRrwaGU7sAXc6XsQIDAQABo4HqMIHnMA4GA1UdDwEB/wQEAwIGQDARBgNVHSUECjAIBgYEAJE3AwAwDAYDVR0TAQH/BAIwADCBlAYJKoZIhvcNAQkPBIGGMIGDMAsGCWCGSAFlAwQBKjALBglghkgBZQMEAS0wCwYJYIZIAWUDBAEWMAsGCWCGSAFlAwQBGTALBglghkgBZQMEAQIwCwYJYIZIAWUDBAEFMAoGCCqGSIb3DQMHMAcGBSsOAwIHMA4GCCqGSIb3DQMCAgIAgDAOBggqhkiG9w0DBAICAgAwHQYDVR0OBBYEFDlvWbOlCWHwnjvByxCXIeIxReScMA0GCSqGSIb3DQEBCwUAA4IBAQCo5Xb7+TgcA2J+W+ndLr+4kZpNwaWq4XHdKp5HSTpu1+yFKXIAIVYdSEn3x3oBaswjz4htlyfbBU18KhvnUhJYL6gmsSsVZ4TPaSZ7Ib/sJ0Wz1DyaDbeOryVEdyUuhQ7GaPF3/JTTluLG+t9EI0pxJeOZgE3dM3aLtUE4fl1JpDabuCX40EL0fdT8p7aDU9YjKpoQL15qtQKpfjWe2yU3nqVv5j/JG2IhPh+VhowStYuL2w1ZfFhEWS9y/dGntUKWJf7qCG4jZoOw8Ui6wO2U+x0Y3DESGq6jRd0PckLYEeE4UKUoI+bYmpeX0L3hz9VUoH28fGwiOEN61keyPN8d\n"
                        +
                        "-----END CERTIFICATE-----",
                //Chile
                "-----BEGIN CERTIFICATE-----\n" +
                        "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\n"
                        +
                        "-----END CERTIFICATE-----",
                //Uruguay
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIFsDCCA5igAwIBAgIQEbTm669/QzljW76Ha7c78zANBgkqhkiG9w0BAQsFADBtMTwwOgYDVQQD\n" +
                        "EzNBdXRvcmlkYWQgQ2VydGlmaWNhZG9yYSBkZWwgTWluaXN0ZXJpbyBkZWwgSW50ZXJpb3IxIDAe\n" +
                        "BgNVBAoTF01pbmlzdGVyaW8gZGVsIEludGVyaW9yMQswCQYDVQQGEwJVWTAeFw0yMjEwMjgxMTM1\n" +
                        "MzVaFw0yNzEwMjgxMTM1MzVaMEMxCzAJBgNVBAYTAlVZMRQwEgYDVQQFEwtETkkxNDkxODcxMDEe\n" +
                        "MBwGA1UEAwwVREFOSUVMIE1PUkRFQ0tJIFBVUEtPMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" +
                        "CgKCAQEAqGgkil8nMl6I+QEVPlPcn0WpFn/AuR/x7VyzzrwONRCUX5CfRVkoF1Om+UaU+AuMpA2m\n" +
                        "EeUxxhEGcK/T306zMn/tLgHzXyaDxwUJ3dM2rO5ouOZnJDcX4EaTgfv32xNIxidCcBVKrtpTSrE8\n" +
                        "Uh9KtjOp+YE/rx6ING+Ms51yUM9/Q3Fz7Fl/uInuFeTTCetV01mmLpWMbxNyzyp9lwA4BcsrI8Gl\n" +
                        "C0umz+d/8DW+LG5BGmrT4iisUUUOgoYzLjTFXyVSw3QZof6Da4HWk4o5qw8oNBzT1c1+l5SKTYuC\n" +
                        "+4ABDwBydPYaoEMcuM/uJ3GBv1LEXKCaZBtZpIlH2o4h8QIDAQABo4IBdDCCAXAwCQYDVR0TBAIw\n" +
                        "ADAOBgNVHQ8BAf8EBAMCBPAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMB0GA1UdDgQW\n" +
                        "BBRWN450nJy9hJTDPQpZQvyAbFm7GjBNBggrBgEFBQcBAQRBMD8wPQYIKwYBBQUHMAKGMWh0dHBz\n" +
                        "Oi8vY2EubWludGVyaW9yLmd1Yi51eS9jZXJ0aWZpY2Fkb3MvTUlDQS5jZXIwHwYDVR0jBBgwFoAU\n" +
                        "n7M0PJs6qWAQyshZBG3trWIsSP8wagYDVR0gBGMwYTBfBgtghlqE4q4dhIgFAjBQME4GCCsGAQUF\n" +
                        "BwIBFkJ3d3cudWNlLmd1Yi51eS9pbmZvcm1hY2lvbi10ZWNuaWNhL3BvbGl0aWNhcy9jcF9wZXJz\n" +
                        "b25hX2Zpc2ljYS5wZGYwOQYDVR0fBDIwMDAuoCygKoYoaHR0cHM6Ly9jYS5taW50ZXJpb3IuZ3Vi\n" +
                        "LnV5L2NybHMvY3JsLmNybDANBgkqhkiG9w0BAQsFAAOCAgEAuyOOA2bJvnutmUd2iTOHp0fjFHG+\n" +
                        "1Y+94q6KaEH1xc92rKk07fwvrZlP4SBSXCJPK9VrOsEKoXCH39Xpz6FkL1dyPbd0i4t6jY6GWxAI\n" +
                        "zk2ZX13R4xpZYwUOephVe8sNBmTxt4ghUe73nnU1+zrSRxGAyOt8mp3jzEtUz05tNqQq5DzvGUR8\n" +
                        "4YfD83KYRs3Q7EO2cyAmMTRJ8yTnlAL0osYBmL6X/MRdl/7v1SeP5hvmR5R2nkSKciuDU0glFZVz\n" +
                        "FmgFmMf5hLt7pFGcAooiFkpxKh9p4WM9BM85okpeFk4Pi1/hmhgKAF19t7j7XRRLFihq+bxcqEkC\n" +
                        "g3MZjWr+1vWlyevEV8hcF2J8QlTcm8oM9bkWyz64NXeLPaV+54g7EScEnLwA+AxB1alJfeLbYpqJ\n" +
                        "gtIxiWtDkZYlB2linKHhoRAHBFsrcPYxzaEJJXRbp992cP+UYR77VjO2eCiYxyhIYDDWT1f2vCgX\n" +
                        "ts+ftYtVLDK5ijn6F3Cup++dueyWav7c9sMgjRtqs0Pv0ybesKoJptewPP0L8IilpRQ0Hx6vqG6V\n" +
                        "ZYaM4SvIiO0rovMdIAIKuuj0AGBYhDa72U9YovbujpKruvCzfW9+V+TcgRqgTfTXDS9+PztHkvO9\n" +
                        "CHa9CdUmbtgqiEcsBL2itBWlHpmwGPTHij05OjS+d/uQuzU=\n" +
                        "-----END CERTIFICATE-----"
        );

        Object[][] data = new Object[countrySpecificLotls.size()][];
        for (int i = 0; i < countrySpecificLotls.size(); ++i) {
            Object[] rec = new Object[2];
            rec[0] = countrySpecificLotls.get(i);
            rec[1] = certificates.get(i);

            data[i] = rec;
        }

        return Arrays.asList(data);
    }
}
