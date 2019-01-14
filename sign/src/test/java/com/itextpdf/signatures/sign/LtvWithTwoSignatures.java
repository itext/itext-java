/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures.sign;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category( IntegrationTest.class)
public class LtvWithTwoSignatures extends ExtendedITextTest {

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/LtvWithTwoSignaturesTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/LtvWithTwoSignaturesTest/";

    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void AddLtvInfo() throws GeneralSecurityException, java.io.IOException {
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";
        String caCertFileName = certsSrc + "rootRsa.p12";
        String srcFileName = sourceFolder + "signedDoc.pdf";
        String ltvFileName = destinationFolder + "ltvEnabledTest01.pdf";
        String ltvFileName2 = destinationFolder + "ltvEnabledTest02.pdf";

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestOcspClient testOcspClient = new TestOcspClient(caCert, caPrivateKey);
        TestCrlClient testCrlClient = new TestCrlClient(caCert, caPrivateKey);

        AddLtvInfo(srcFileName,ltvFileName,"sig",testOcspClient,testCrlClient);
        AddLtvInfo(ltvFileName,ltvFileName2,"sig2",testOcspClient,testCrlClient);

        PdfReader reader = new PdfReader(ltvFileName2);
        PdfDocument document = new PdfDocument(reader);
        PdfDictionary catalogDictionary = document.getCatalog().getPdfObject();
        PdfDictionary dssDictionary = catalogDictionary.getAsDictionary(PdfName.DSS);

        PdfDictionary vri = dssDictionary.getAsDictionary(PdfName.VRI);
        Assert.assertNotNull(vri);
        Assert.assertEquals(2, vri.size());

        PdfArray ocsps = dssDictionary.getAsArray(PdfName.OCSPs);
        Assert.assertNotNull(ocsps);
        Assert.assertEquals(2, ocsps.size());

        PdfArray certs = dssDictionary.getAsArray(PdfName.Certs);
        Assert.assertNotNull(certs);
        Assert.assertEquals(2, certs.size());

        PdfArray crls = dssDictionary.getAsArray(PdfName.CRLs);
        Assert.assertNotNull(crls);
        Assert.assertEquals(1, crls.size());
    }

    private void AddLtvInfo(String src, String dest, String sigName, TestOcspClient testOcspClient,TestCrlClient testCrlClient ) throws java.io.IOException, GeneralSecurityException {
        PdfDocument document = new PdfDocument(new PdfReader(src), new PdfWriter(dest), new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document, "BC");
        ltvVerification.addVerification(sigName, testOcspClient, testCrlClient, LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL, LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();
    }
}
