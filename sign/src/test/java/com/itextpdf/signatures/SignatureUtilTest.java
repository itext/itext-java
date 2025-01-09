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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.List;

@Tag("BouncyCastleIntegrationTest")
public class SignatureUtilTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/SignatureUtilTest/";
    private static final double EPS = 0.001;

    @BeforeAll
    public static void before() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
    }

    @Test
    public void getSignaturesTest01() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        List<String> signatureNames = signatureUtil.getSignatureNames();

        Assertions.assertEquals(1, signatureNames.size());
        Assertions.assertEquals("Signature1", signatureNames.get(0));
    }

    @Test
    public void getSignaturesTest02() throws IOException {
        String inPdf = sourceFolder + "simpleDocument.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        List<String> signatureNames = signatureUtil.getSignatureNames();

        Assertions.assertEquals(0, signatureNames.size());
    }

    @Test
    public void eolNotIncludedIntoByteRangeTest1() throws IOException {
        String inPdf = sourceFolder + "eolNotIncludedIntoByteRange1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void eolNotIncludedIntoByteRangeTest2() throws IOException {
        String inPdf = sourceFolder + "eolNotIncludedIntoByteRange2.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void eolNotIncludedIntoByteRangeTest3() throws IOException {
        String inPdf = sourceFolder + "eolNotIncludedIntoByteRange3.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void firstBytesNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "firstBytesNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void lastBytesNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "lastBytesNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void lastBytesNotCoveredTest02() throws IOException {
        String inPdf = sourceFolder + "lastBytesNotCoveredTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void exclusionSmallerThenContentsTest01() throws IOException {
        String inPdf = sourceFolder + "exclusionSmallerThenContentsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void bytesAreCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "bytesAreCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void bytesAreCoveredTest02() throws IOException {
        String inPdf = sourceFolder + "bytesAreCoveredTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("sig"));
    }

    @Test
    public void indirectBytesAreCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "indirectBytesAreCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void commentsBytesAreCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "commentsBytesAreCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void commentsBytesAreNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "commentsBytesAreNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void indirectBytesAreCoveredTest02() throws IOException {
        String inPdf = sourceFolder + "indirectBytesAreCoveredTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void indirectBytesAreNotCoveredTest01() throws IOException {
        String inPdf = sourceFolder + "indirectBytesAreNotCoveredTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }


    @Test
    public void twoContentsTest01() throws IOException {
        String inPdf = sourceFolder + "twoContentsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void spacesBeforeContentsTest01() throws IOException {
        String inPdf = sourceFolder + "spacesBeforeContentsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void notIndirectSigDictionaryTest() throws IOException {
        String inPdf = sourceFolder + "notIndirectSigDictionaryTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument("Signature1"));
    }

    @Test
    public void emptySignatureReadSignatureDataTest() throws IOException {
        String inPdf = sourceFolder + "emptySignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertNull(signatureUtil.readSignatureData("Signature1", null));
    }

    @Test
    public void readSignatureDataTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        PdfPKCS7 pkcs7 = signatureUtil.readSignatureData("Signature1");
        Assertions.assertNotNull(pkcs7);
        Assertions.assertEquals("Test 1", pkcs7.getReason());
        Assertions.assertNull(pkcs7.getSignName());
        Assertions.assertEquals("TestCity", pkcs7.getLocation());
        // The number corresponds to 18 May, 2021 17:23:59.
        double expectedMillis = (double) 1621347839000L;
        Assertions.assertEquals(
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
        Assertions.assertNotNull(pkcs7);
        Assertions.assertNotNull(pkcs7);
        Assertions.assertEquals("Test", pkcs7.getReason());
        Assertions.assertNull(pkcs7.getSignName());
        Assertions.assertEquals("TestCity", pkcs7.getLocation());
        // The number corresponds to 18 May, 2021 11:28:40.
        double expectedMillis = (double) 1621326520000L;
        Assertions.assertEquals(
                TimeTestUtil.getFullDaysMillis(expectedMillis),
                TimeTestUtil.getFullDaysMillis(DateTimeUtil.getUtcMillisFromEpoch(pkcs7.getSignDate())),
                EPS);
    }

    @Test
    public void byteRangeAndContentsEntriesTest() throws IOException {
        String inPdf = sourceFolder + "byteRangeAndContentsEntries.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertThrows(PdfException.class,
                () -> signatureUtil.readSignatureData("Signature1"));
    }

    @Test
    public void doesSignatureFieldExistTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.doesSignatureFieldExist("Signature1"));
    }

    @Test
    public void doesSignatureFieldExistEmptySignatureTest() throws IOException {
        String inPdf = sourceFolder + "emptySignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertTrue(signatureUtil.doesSignatureFieldExist("Signature1"));
    }

    @Test
    public void signatureInTextTypeFieldTest() throws IOException {
        String inPdf = sourceFolder + "signatureInTextTypeField.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertFalse(signatureUtil.doesSignatureFieldExist("Signature1"));
    }

    @Test
    public void getTotalRevisionsTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertEquals(1, signatureUtil.getTotalRevisions());
    }

    @Test
    public void getRevisionTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertEquals(1, signatureUtil.getRevision("Signature1"));
    }

    @Test
    public void getRevisionEmptyFieldsTest() throws IOException {
        String inPdf = sourceFolder + "emptySignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertEquals(0, signatureUtil.getRevision("Signature1"));
    }

    @Test
    public void getRevisionXfaFormTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignatureWithXfa.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertEquals(1, signatureUtil.getRevision("Signature1"));
    }

    @Test
    public void extractRevisionTest() throws IOException {
        String inPdf = sourceFolder + "simpleSignature.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertNotNull(signatureUtil.extractRevision("Signature1"));
    }

    @Test
    public void extractRevisionNotSignatureFieldTest() throws IOException {
        String inPdf = sourceFolder + "signatureInTextTypeField.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf));
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        Assertions.assertNull(signatureUtil.extractRevision("Signature1"));
    }
}
