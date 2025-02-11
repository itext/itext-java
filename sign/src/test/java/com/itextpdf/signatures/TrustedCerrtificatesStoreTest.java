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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.validation.AssertValidationReport;
import com.itextpdf.signatures.validation.CRLValidator;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.mocks.MockChainValidator;
import com.itextpdf.signatures.validation.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class TrustedCerrtificatesStoreTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] KEY_PASSWORD = "testpassphrase".toCharArray();
    private static X509Certificate crlCert;
    private static X509Certificate crlRootCert;
    private static X509Certificate intermediateCert;
    private static X509Certificate ocspCert;
    private static X509Certificate rootCert;
    private static X509Certificate signCert;
    private static X509Certificate tsaCert;
    private static X509Certificate tsaRootCert;

    @BeforeAll
    public static void setUpOnce() throws CertificateException, IOException {
        crlCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "crlCert.pem")[0];
        crlRootCert= (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "crlRoot.pem")[0];
        intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "intermediate.pem")[0];
        ocspCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "ocspCert.pem")[0];
        rootCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "root.pem")[0];
        signCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "sign.pem")[0];
        tsaCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "tsaCert.pem")[0];
        tsaRootCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "tsaRoot.pem")[0];
    }

    @Test
    public void testIsCertificateGenerallyTrusted() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertFalse(sut.isCertificateGenerallyTrusted(rootCert));
        sut.addGenerallyTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertTrue(sut.isCertificateGenerallyTrusted(rootCert));
    }

    @Test
    public void testIsCertificateTrustedForCA() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertFalse(sut.isCertificateTrustedForCA(rootCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertTrue(sut.isCertificateTrustedForCA(rootCert));
    }

    @Test
    public void testIsCertificateTrustedForTimestamp() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertFalse(sut.isCertificateTrustedForTimestamp(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertTrue(sut.isCertificateTrustedForTimestamp(rootCert));
    }

    @Test
    public void testIsCertificateTrustedForOcsp() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertFalse(sut.isCertificateTrustedForOcsp(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertTrue(sut.isCertificateTrustedForOcsp(rootCert));
    }

    @Test
    public void testIsCertificateTrustedForCrl() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(rootCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertFalse(sut.isCertificateTrustedForCrl(rootCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(rootCert));
        Assertions.assertTrue(sut.isCertificateTrustedForCrl(rootCert));
    }

    @Test
    public void testGetKnownCertificates() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addCrlTrustedCertificates(Collections.singletonList(crlCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(ocspCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addGenerallyTrustedCertificates(Collections.singletonList(tsaRootCert));

        Assertions.assertEquals(1, sut.getKnownCertificates(crlCert.getSubjectX500Principal().getName()).size());
        Assertions.assertEquals(1, sut.getKnownCertificates(rootCert.getSubjectX500Principal().getName()).size());
    }


    @Test
    public void testGetAllTrustedCertificates() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addCrlTrustedCertificates(Collections.singletonList(tsaRootCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(tsaRootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addGenerallyTrustedCertificates(Collections.singletonList(tsaRootCert));

        //duplicates should be removed
        Assertions.assertEquals(3, sut.getAllTrustedCertificates().size());
        Assertions.assertTrue(sut.getAllTrustedCertificates().contains(tsaRootCert));
        Assertions.assertTrue(sut.getAllTrustedCertificates().contains(rootCert));
        Assertions.assertTrue(sut.getAllTrustedCertificates().contains(tsaCert));
    }

    @Test
    public void testGetAllTrustedCertificatesByName() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addCrlTrustedCertificates(Collections.singletonList(tsaRootCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(tsaRootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addGenerallyTrustedCertificates(Collections.singletonList(tsaRootCert));

        //duplicates should be removed
        Assertions.assertEquals(1, sut.getAllTrustedCertificates(tsaRootCert.getSubjectX500Principal().getName()).size());
        Assertions.assertTrue(sut.getAllTrustedCertificates(tsaRootCert.getSubjectX500Principal().getName()).contains(tsaRootCert));
        Assertions.assertTrue(sut.getAllTrustedCertificates(rootCert.getSubjectX500Principal().getName()).contains(rootCert));
        Assertions.assertTrue(sut.getAllTrustedCertificates(tsaCert.getSubjectX500Principal().getName()).contains(tsaCert));
    }

     @Test
    public void testGetGenerallyTrustedCertificates() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(signCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(ocspCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(crlCert));

        String name = signCert.getSubjectX500Principal().getName();

        Assertions.assertEquals(1,sut.getGenerallyTrustedCertificates(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCA(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCrl(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForOcsp(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForTimestamp(name).size());
    }


    @Test
    public void testGetCertificatesTrustedForCA() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(signCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(ocspCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(crlCert));

        String name = rootCert.getSubjectX500Principal().getName();

        Assertions.assertEquals(0,sut.getGenerallyTrustedCertificates(name).size());
        Assertions.assertEquals(1,sut.getCertificatesTrustedForCA(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCrl(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForOcsp(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForTimestamp(name).size());
    }
    @Test
    public void testGetCertificatesTrustedForTimeStamp() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(signCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(ocspCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(crlCert));

        String name = tsaCert.getSubjectX500Principal().getName();

        Assertions.assertEquals(0,sut.getGenerallyTrustedCertificates(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCA(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCrl(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForOcsp(name).size());
        Assertions.assertEquals(1,sut.getCertificatesTrustedForTimestamp(name).size());
    }
    @Test
    public void testGetCertificatesTrustedForOcsp() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(signCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(ocspCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(crlCert));

        String name = ocspCert.getSubjectX500Principal().getName();

        Assertions.assertEquals(0,sut.getGenerallyTrustedCertificates(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCA(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCrl(name).size());
        Assertions.assertEquals(1,sut.getCertificatesTrustedForOcsp(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForTimestamp(name).size());
    }
    @Test
    public void testGetCertificatesTrustedForCrl() {
        TrustedCertificatesStore sut = new TrustedCertificatesStore();
        sut.addGenerallyTrustedCertificates(Collections.singletonList(signCert));
        sut.addCATrustedCertificates(Collections.singletonList(rootCert));
        sut.addTimestampTrustedCertificates(Collections.singletonList(tsaCert));
        sut.addOcspTrustedCertificates(Collections.singletonList(ocspCert));
        sut.addCrlTrustedCertificates(Collections.singletonList(crlCert));

        String name = crlCert.getSubjectX500Principal().getName();

        Assertions.assertEquals(0,sut.getGenerallyTrustedCertificates(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForCA(name).size());
        Assertions.assertEquals(1,sut.getCertificatesTrustedForCrl(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForOcsp(name).size());
        Assertions.assertEquals(0,sut.getCertificatesTrustedForTimestamp(name).size());
    }
}
