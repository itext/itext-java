package com.itextpdf.signatures.verify.pdfinsecurity;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignatureWrappingAttackTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/verify/pdfinsecurity/SignatureWrappingAttackTest/";

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testSWA01() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "siwa.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.verifySignature(signatureName);
        Assert.assertTrue(pdfPKCS7.verify());
        Assert.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));
        document.close();
    }
}
