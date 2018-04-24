package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

@Category(IntegrationTest.class)
public class SignatureUtilTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/SignatureUtilTest/";

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

}
