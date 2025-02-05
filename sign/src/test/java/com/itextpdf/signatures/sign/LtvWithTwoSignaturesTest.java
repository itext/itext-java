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
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class LtvWithTwoSignaturesTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/LtvWithTwoSignaturesTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/LtvWithTwoSignaturesTest/";

    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void addLtvInfo() throws GeneralSecurityException, java.io.IOException, AbstractPKCSException,
            AbstractOperatorCreationException {
        String caCertFileName = certsSrc + "rootRsa.pem";
        String interCertFileName = certsSrc + "intermediateRsa.pem";
        String srcFileName = sourceFolder + "signedTwice.pdf";
        String ltvFileName = destinationFolder + "ltvEnabledTest01.pdf";
        String ltvFileName2 = destinationFolder + "ltvEnabledTest02.pdf";

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        X509Certificate interCert = (X509Certificate) PemFileHelper.readFirstChain(interCertFileName)[0];
        PrivateKey interPrivateKey = PemFileHelper.readFirstKey(interCertFileName, password);

        TestOcspClient testOcspClient = new TestOcspClient()
                .addBuilderForCertIssuer(interCert, interPrivateKey)
                .addBuilderForCertIssuer(caCert, caPrivateKey);
        TestCrlClient testCrlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        addLtvInfo(srcFileName, ltvFileName, "Signature1", testOcspClient, testCrlClient);
        addLtvInfo(ltvFileName, ltvFileName2, "Signature2", testOcspClient, testCrlClient);

        PdfReader reader = new PdfReader(ltvFileName2);
        PdfDocument document = new PdfDocument(reader);
        PdfDictionary catalogDictionary = document.getCatalog().getPdfObject();
        PdfDictionary dssDictionary = catalogDictionary.getAsDictionary(PdfName.DSS);

        PdfDictionary vri = dssDictionary.getAsDictionary(PdfName.VRI);
        Assertions.assertNotNull(vri);
        Assertions.assertEquals(2, vri.size());

        PdfArray ocsps = dssDictionary.getAsArray(PdfName.OCSPs);
        Assertions.assertNotNull(ocsps);
        Assertions.assertEquals(5, ocsps.size());

        PdfArray certs = dssDictionary.getAsArray(PdfName.Certs);
        Assertions.assertNotNull(certs);
        Assertions.assertEquals(5, certs.size());

        PdfArray crls = dssDictionary.getAsArray(PdfName.CRLs);
        Assertions.assertNotNull(crls);
        Assertions.assertEquals(2, crls.size());
    }

    private void addLtvInfo(String src, String dest, String sigName, TestOcspClient testOcspClient,
            TestCrlClient testCrlClient ) throws java.io.IOException, GeneralSecurityException {
        PdfDocument document =
                new PdfDocument(new PdfReader(src), new PdfWriter(dest), new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document, FACTORY.getProviderName());
        ltvVerification.addVerification(sigName, testOcspClient, testCrlClient,
                LtvVerification.CertificateOption.WHOLE_CHAIN,
                LtvVerification.Level.OCSP_CRL,
                LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();
    }
}
