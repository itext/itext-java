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

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

// This test suite is taken from https://eidas.ec.europa.eu/efda/validation-tests#/screen/home
@Tag("BouncyCastleIntegrationTest")
public class QualifiedValidatorTest extends ExtendedITextTest {

    private static final String CERTS = "./src/test/resources/com/itextpdf/signatures/validation/lotl/QualifiedValidatorTest/test_certificates/";
    private static final String SOURCE_FOLDER_LOTL_FILES = "./src/test/resources/com/itextpdf/signatures/validation/lotl/QualifiedValidatorTest/test_lotl_snapshot/";

    private static final ValidationContext SIGN_CONTEXT = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private static final Supplier<EuropeanTrustedListConfigurationFactory> FACTORY = EuropeanTrustedListConfigurationFactory.getFactory();
    private static final Date PRESENT_DATE = DateTimeUtil.createUtcDateTime(2025, 9, 26, 2, 0, 56);
    private static final Date PRE_EIDAS_DATE = DateTimeUtil.createUtcDateTime(2014, 2, 3, 15, 0, 0);
    private static final Date PRE_EIDAS_DATE2 = DateTimeUtil.createUtcDateTime(2015, 5, 6, 15, 0, 0);

    private static final String LOTL_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDPDCCAiSgAwIBAgIBATANBgkqhkiG9w0BAQ0FADBQMRQwEgYDVQQDDAtDRVJU\n" +
            "LUxPVEwtMzEYMBYGA1UECgwPRVUgT3JnYW5pemF0aW9uMREwDwYDVQQLDAhQS0kt\n" +
            "VEVTVDELMAkGA1UEBhMCTFUwHhcNMjQxMDI1MjMwMDAzWhcNMjYxMDI2MDAwMDAz\n" +
            "WjBQMRQwEgYDVQQDDAtDRVJULUxPVEwtMzEYMBYGA1UECgwPRVUgT3JnYW5pemF0\n" +
            "aW9uMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCTFUwggEiMA0GCSqGSIb3\n" +
            "DQEBAQUAA4IBDwAwggEKAoIBAQDeU/iKtAqrfGrHB1N6gFh+d56+W46IxUFEWiS+\n" +
            "Q+zER1/6hZEKVk0IWhCw2yS5p43Z5h9H3LSMfexTLqSbwhve5+accma+Q6It0vg3\n" +
            "rrBGnMPGOqta7Zc5zZ3kv83jJCQ8EU6FnCp7OqQY2ymiqgIWHwbDWooNUsYnu+wv\n" +
            "bcYx/AYweMZLdWSogt3iu5Sh1zNhubU4tasn/A5x0pDV97BSGIvs5mmqIndF8uDc\n" +
            "mmxmjn105LGEQqwT6GN1r99kwd2UZewbVztlbvDoI6eTDkZ1ffomDHnNjEIBhcgG\n" +
            "TlI3zpRmIVcj6Vckh8zGmewTt6FJhGlIb83iqB9ah8ki03NzAgMBAAGjITAfMB0G\n" +
            "A1UdDgQWBBRJ79BepQX9cyVvwvG/Xp1yxwYvnTANBgkqhkiG9w0BAQ0FAAOCAQEA\n" +
            "PQJNKkMNUGO5gM/CC6D7e4EBvkCBwgjtIhAFoXEzmqij/0Da+dNY1xk6hPMR8jd3\n" +
            "YFpwsBP3h72hSoq8wZhJ3erP0uIo4qmOPDeJsmkpRsKqDFmTg04bE3bGV1pBI06o\n" +
            "AqwQr5JAoQAIrMFDobxXsTXC1abUKO9BId72rUy5Mxv227aVNx8nWcZoKeg37FVk\n" +
            "bLgd+mjfh8LzxM02i3WIM+Z2wdq/h8SVlupPPkrJr2edBv/CzCf1VFa8L7tDMpxP\n" +
            "9HdHBJz+nUfTe5mXzqHS0MxogW5sBUk8Rj9KCvNO5wdPZhfg8nGrEnGWXj8gl9Km\n" +
            "MwsoJseoWfQ6GjmQCv0kpQ==\n" +
            "-----END CERTIFICATE-----";

}
