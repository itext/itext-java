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
        PdfPKCS7 pdfPKCS7 = sigUtil.verifySignature(signatureName);
        Assert.assertTrue(pdfPKCS7.verify());
        Assert.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));
        document.close();
    }

    @Test
    public void testISAValidPdf() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "isaValidPdf.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.verifySignature(signatureName);
        Assert.assertTrue(pdfPKCS7.verify());
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
        PdfPKCS7 sigRevSignatureData = sigRevUtil.verifySignature(signatureName);
        Assert.assertTrue(sigRevSignatureData.verify());
        Assert.assertTrue(sigRevUtil.signatureCoversWholeDocument(signatureName));

        sigRevDocument.close();
        document.close();
    }
}
