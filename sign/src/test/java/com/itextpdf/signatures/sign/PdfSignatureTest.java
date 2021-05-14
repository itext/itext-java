package com.itextpdf.signatures.sign;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSignatureApp;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfSignatureTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureTest/";

    @Test
    public void setByteRangeTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(
                sourceFolder + "simpleSignature.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(doc);
            PdfSignature signature = sigUtil.getSignature("Signature1");

            int[] byteRange = {0, 141, 16526, 2494};
            signature.setByteRange(byteRange);

            PdfArray expected = new PdfArray((new int[] {0, 141, 16526, 2494}));

            Assert.assertArrayEquals(expected.toIntArray(), signature.getByteRange().toIntArray());
        }
    }

    @Test
    public void setContentsTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(
                sourceFolder + "simpleSignature.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(doc);
            PdfSignature signature = sigUtil.getSignature("Signature1");

            byte[] newContents = new PdfString("new iText signature").getValueBytes();
            signature.setContents(newContents);

            Assert.assertEquals("new iText signature", signature.getContents().getValue());
        }
    }

    @Test
    public void setAndGetCertTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(
                sourceFolder + "adbe.x509.rsa_sha1_signature.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(doc);
            PdfSignature signature = sigUtil.getSignature("Signature1");

            byte[] certChain = new PdfString("Hello, iText!!").getValueBytes();
            signature.setCert(certChain);

            Assert.assertEquals("Hello, iText!!", signature.getCertObject().toString());
        }
    }

    @Test
    public void getCertObjectTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(
                sourceFolder + "adbe.x509.rsa_sha1_signature.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(doc);
            PdfSignature signature = sigUtil.getSignature("Signature1");

            Assert.assertTrue(signature.getCertObject().isArray());
        }
    }

    @Test
    public void setAndGetNameTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(
                sourceFolder + "simpleSignature.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(doc);
            PdfSignature signature = sigUtil.getSignature("Signature1");

            Assert.assertNull(signature.getName());

            String name = "iText person";
            signature.setName(name);

            Assert.assertEquals(name, signature.getName());
        }
    }

    @Test
    public void setSignatureCreatorTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(
                sourceFolder + "noPropBuilds.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(doc);
            PdfSignature signature = sigUtil.getSignature("Signature1");

            Assert.assertNull(signature.getPdfObject().getAsDictionary(PdfName.Prop_Build));

            signature.setSignatureCreator("iText.Name");

            String propBuild = signature.getPdfObject().getAsDictionary(PdfName.Prop_Build)
                    .getAsDictionary(PdfName.App).getAsName(PdfName.Name).getValue();

            Assert.assertEquals("iText.Name", propBuild);
        }
    }

    @Test
    public void pdfSignatureAppDefaultConstructorTest() {
        PdfSignatureApp signatureApp = new PdfSignatureApp();
        Assert.assertTrue(signatureApp.getPdfObject().isDictionary());
    }
}
