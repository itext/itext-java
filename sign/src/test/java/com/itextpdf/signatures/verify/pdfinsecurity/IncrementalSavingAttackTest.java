/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.verify.pdfinsecurity;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class IncrementalSavingAttackTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/verify/pdfinsecurity/IncrementalSavingAttackTest/";

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void testISA03() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "isa-3.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.readSignatureData(signatureName);
        Assertions.assertTrue(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
        Assertions.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));
        document.close();
    }

    @Test
    public void testISAValidPdf() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "isaValidPdf.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.readSignatureData(signatureName);
        Assertions.assertTrue(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
        Assertions.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));

        String textFromPage = PdfTextExtractor.getTextFromPage(document.getPage(1));
        // We are working with the latest revision of the document, that's why we should get amended page text.
        // However Signature shall be marked as not covering the complete document, indicating its invalidity
        // for the current revision.
        Assertions.assertEquals("This is manipulated malicious text, ha-ha!", textFromPage);

        Assertions.assertEquals(2, sigUtil.getTotalRevisions());
        Assertions.assertEquals(1, sigUtil.getRevision(signatureName));

        InputStream sigInputStream = sigUtil.extractRevision(signatureName);
        PdfDocument sigRevDocument = new PdfDocument(new PdfReader(sigInputStream));

        SignatureUtil sigRevUtil = new SignatureUtil(sigRevDocument);
        PdfPKCS7 sigRevSignatureData = sigRevUtil.readSignatureData(signatureName);
        Assertions.assertTrue(sigRevSignatureData.verifySignatureIntegrityAndAuthenticity());
        Assertions.assertTrue(sigRevUtil.signatureCoversWholeDocument(signatureName));

        sigRevDocument.close();
        document.close();
    }
}
