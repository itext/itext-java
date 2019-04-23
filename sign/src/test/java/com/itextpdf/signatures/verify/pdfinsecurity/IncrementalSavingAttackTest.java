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
package com.itextpdf.signatures.verify.pdfinsecurity;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IncrementalSavingAttackTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/verify/pdfinsecurity/IncrementalSavingAttackTest/";

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR))
    public void testISA03() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "isa-3.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.readSignatureData(signatureName);
        Assert.assertTrue(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
        Assert.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));
        document.close();
    }

    @Test
    public void testISAValidPdf() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "isaValidPdf.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.readSignatureData(signatureName);
        Assert.assertTrue(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
        Assert.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));

        String textFromPage = PdfTextExtractor.getTextFromPage(document.getPage(1));
        // We are working with the latest revision of the document, that's why we should get amended page text.
        // However Signature shall be marked as not covering the complete document, indicating its invalidity
        // for the current revision.
        Assert.assertEquals("This is manipulated malicious text, ha-ha!", textFromPage);

        Assert.assertEquals(2, sigUtil.getTotalRevisions());
        Assert.assertEquals(1, sigUtil.getRevision(signatureName));

        InputStream sigInputStream = sigUtil.extractRevision(signatureName);
        PdfDocument sigRevDocument = new PdfDocument(new PdfReader(sigInputStream));

        SignatureUtil sigRevUtil = new SignatureUtil(sigRevDocument);
        PdfPKCS7 sigRevSignatureData = sigRevUtil.readSignatureData(signatureName);
        Assert.assertTrue(sigRevSignatureData.verifySignatureIntegrityAndAuthenticity());
        Assert.assertTrue(sigRevUtil.signatureCoversWholeDocument(signatureName));

        sigRevDocument.close();
        document.close();
    }
}
