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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.utils.EncodingUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CertificateInfoTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String EXPECTED_EXCEPTION_MESSAGE = FACTORY.getBouncyCastleFactoryTestUtil().getCertificateInfoTestConst();

    private static final String ENCODED_DN =
            "MD0xCzAJBgNVBAYMAkJFMQ4wDAYDVQQKDAVpVGV4dDEeMBwGA1UEAwwVaVRleHRUZXN0SW50ZXJtZWRpYXRl";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures"
            + "/CertificateInfoTest/";


    @Test
    public void X500InvalidDirectoryConstructorTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CertificateInfo.X500Name("some_dir"));
    }

    @Test
    public void X500ValidDirectoryConstructorTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=dir,another=dir,some=value");

        Map<String, List<String>> values = name.getFields();
        Assertions.assertEquals(2, values.size());

        Assertions.assertEquals(Arrays.asList("dir", "value"), values.get("SOME"));
        Assertions.assertEquals(Collections.singletonList("dir"), values.get("ANOTHER"));
    }

    @Test
    public void X500GetFieldTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=value,another=dir,some=dir");

        Assertions.assertEquals("value", name.getField("SOME"));
        Assertions.assertEquals("dir", name.getField("ANOTHER"));
    }

    @Test
    public void X500GetFieldArrayTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=value,another=dir,some=dir");

        Assertions.assertEquals(Arrays.asList("value", "dir"), name.getFieldArray("SOME"));
        Assertions.assertEquals(Collections.singletonList("dir"), name.getFieldArray("ANOTHER"));
    }

    @Test
    public void X509NameTokenizerNextTokenComplicatedTest() {
        CertificateInfo.X509NameTokenizer tokenizer = new CertificateInfo.X509NameTokenizer("quoted\",\"comma=escaped\\,comma_escaped\\\"quote");
        String token = tokenizer.nextToken();

        Assertions.assertEquals("quoted,comma=escaped,comma_escaped\"quote", token);
        Assertions.assertNull(tokenizer.nextToken());
    }

    @Test
    public void getIssuerFieldsExceptionTest() {
        Exception exception =
                Assertions.assertThrows(PdfException.class, () -> CertificateInfo.getIssuer(new byte[] {4, 8, 15, 16, 23, 42}));
        Assertions.assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getCause().getMessage());
    }

    @Test
    public void getSubjectExceptionTest() {
        Exception exception =
                Assertions.assertThrows(PdfException.class, () -> CertificateInfo.getSubject(new byte[] {4, 8, 15, 16, 23, 42}));
        Assertions.assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getCause().getMessage());
    }


    @Test
    public void distinguishedNameEncodingAndComparisonTest() throws CertificateException, IOException {

        X509Certificate cert = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "intermediate.pem")[0];
        IX500Name name = FACTORY.createX500Name(cert);
        IX500Name differentlyEncodedName = FACTORY.createX500Name(
                FACTORY.createASN1Sequence(EncodingUtil.fromBase64(ENCODED_DN)));

        Assertions.assertTrue(differentlyEncodedName.equals(name));
        Assertions.assertTrue(name.equals(differentlyEncodedName));
    }

    @Test
    public void distinguishedNameEncodingAndComparisonIntegrationTest() {
        AssertUtil.doesNotThrow(() ->
        {
            PdfDocument doc = new PdfDocument(
                    new PdfReader(SOURCE_FOLDER + "signatureWithNameEncodingDifferences.pdf"));
            SignatureUtil signUtil = new SignatureUtil(doc);
            List<String> signNames = signUtil.getSignatureNames();
            PdfPKCS7 pkcs7 = signUtil.readSignatureData(signNames.get(0));
            Assertions.assertNotNull(pkcs7);
        });
    }
}
