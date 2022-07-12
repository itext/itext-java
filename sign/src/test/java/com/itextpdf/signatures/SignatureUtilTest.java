/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.security.Security;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

@Category(IntegrationTest.class)
public class SignatureUtilTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/SignatureUtilTest/";
    private static final double EPS = 0.001;

    @BeforeClass
    public static void before() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.createProvider());
    }

    @Test
    public void getSignaturesTest01() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        List<String> signatureNames = signatureUtil.getSignatureNames();

        Assert.assertEquals(1, signatureNames.size());
        Assert.assertEquals("Signature1", signatureNames.get(0));
    }

    @Test
    public void getSignaturesTest02() throws IOException {
        String inPdf = sourceFolder + "simpleDocument.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        List<String> signatureNames = signatureUtil.getSignatureNames();

        Assert.assertEquals(0, signatureNames.size());
    }

    @Test
    public void firstBytesNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "firstBytesNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void lastBytesNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "lastBytesNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void lastBytesNotCoveredTest02() throws IOException {
        String inPdf = sourceFolder + "lastBytesNotCoveredTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void bytesAreNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "bytesAreNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void bytesAreCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "bytesAreCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void bytesAreCoveredTest02() throws IOException {
        String inPdf = sourceFolder + "bytesAreCoveredTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.signatureCoversWholeDocument("sig"));
    }

    @Test
    public void twoContentsTest01() throws IOException {
        String inPdf = sourceFolder + "twoContentsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void spacesBeforeContentsTest01() throws IOException {
        String inPdf = sourceFolder + "spacesBeforeContentsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void spacesBeforeContentsTest02() throws IOException {
        String inPdf = sourceFolder + "spacesBeforeContentsTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void notIndirectSigDictionaryTest() throws IOException {
        String inPdf = sourceFolder + "notIndirectSigDictionaryTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void emptySignatureReadSignatureDataTest() throws IOException {
        String inPdf = sourceFolder + "emptySignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertNull(signatureUtil.readSignatureData("Signature1", null));
    }

    @Test
    public void readSignatureDataTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        PdfPKCS7 pkcs7 = signatureUtil.readSignatureData("Signature1");
        Assert.assertNotNull(pkcs7);
        Assert.assertEquals("Test 1", pkcs7.getReason());
        Assert.assertNull(pkcs7.getSignName());
        Assert.assertEquals("TestCity", pkcs7.getLocation());
        // The number corresponds to 18 May, 2021 17:23:59.
        double expectedMillis = (double) 1621347839000L;
        Assert.assertEquals(
                TimeTestUtil.getFullDaysMillis(expectedMillis),
                TimeTestUtil.getFullDaysMillis(DateTimeUtil.getUtcMillisFromEpoch(pkcs7.getSignDate())),
                EPS);
    }

    @Test
    public void readSignatureDataWithSpecialSubFilterTest() throws IOException {
        String inPdf = sourceFolder + "adbe.x509.rsa_sha1_signature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        PdfPKCS7 pkcs7 = signatureUtil.readSignatureData("Signature1");
        Assert.assertNotNull(pkcs7);
        Assert.assertNotNull(pkcs7);
        Assert.assertEquals("Test", pkcs7.getReason());
        Assert.assertNull(pkcs7.getSignName());
        Assert.assertEquals("TestCity", pkcs7.getLocation());
        // The number corresponds to 18 May, 2021 11:28:40.
        double expectedMillis = (double) 1621326520000L;
        Assert.assertEquals(
                TimeTestUtil.getFullDaysMillis(expectedMillis),
                TimeTestUtil.getFullDaysMillis(DateTimeUtil.getUtcMillisFromEpoch(pkcs7.getSignDate())),
                EPS);
    }

    @Test
    public void byteRangeAndContentsEntriesTest() throws IOException {
        String inPdf = sourceFolder + "byteRangeAndContentsEntries.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertThrows(PdfException.class,
                () -> signatureUtil.readSignatureData("Signature1"));
    }

    @Test
    public void doesSignatureFieldExistTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.doesSignatureFieldExist("Signature1"));
    }

    @Test
    public void doesSignatureFieldExistEmptySignatureTest() throws IOException {
        String inPdf = sourceFolder + "emptySignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertTrue(signatureUtil.doesSignatureFieldExist("Signature1"));
    }

    @Test
    public void signatureInTextTypeFieldTest() throws IOException {
        String inPdf = sourceFolder + "signatureInTextTypeField.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertFalse(signatureUtil.doesSignatureFieldExist("Signature1"));
    }

    @Test
    public void getTotalRevisionsTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertEquals(1, signatureUtil.getTotalRevisions());
    }

    @Test
    public void getRevisionTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertEquals(1, signatureUtil.getRevision("Signature1"));
    }

    @Test
    public void getRevisionEmptyFieldsTest() throws IOException {
        String inPdf = sourceFolder + "emptySignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertEquals(0, signatureUtil.getRevision("Signature1"));
    }

    @Test
    public void getRevisionXfaFormTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignatureWithXfa.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertEquals(1, signatureUtil.getRevision("Signature1"));
    }

    @Test
    public void extractRevisionTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertNotNull(signatureUtil.extractRevision("Signature1"));
    }

    @Test
    public void extractRevisionNotSignatureFieldTest() throws IOException {
        String inPdf = sourceFolder + "signatureInTextTypeField.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assert.assertNull(signatureUtil.extractRevision("Signature1"));
    }
}
