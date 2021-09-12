package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Category(UnitTest.class)
public class LtvVerifierUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/LtvVerifierUnitTest/";

    @Test
    public void setVerifierTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier1 = new LtvVerifier(new PdfDocument(new PdfReader(new FileInputStream(SOURCE_FOLDER + "ltvDoc.pdf"))));
        LtvVerifier verifier2 = new LtvVerifier(new PdfDocument(new PdfReader(new FileInputStream(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier1.setVerifier(verifier2);
        Assert.assertSame(verifier2, verifier1.verifier);
    }

    @Test
    public void setVerifyRootCertificateTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(new FileInputStream(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier.setVerifyRootCertificate(true);
        Assert.assertTrue(verifier.verifyRootCertificate);
    }

    @Test
    public void verifyNotNullTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(new FileInputStream(SOURCE_FOLDER + "ltvDoc.pdf"))));
        verifier.pkcs7 = null;

        List<VerificationOK> list = Collections.<VerificationOK>emptyList();
        Assert.assertSame(list, verifier.verify(list));
    }

    @Test
    public void getCRLsFromDSSCRLsNullTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(new FileInputStream(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier.dss = new PdfDictionary();
        Assert.assertEquals(new ArrayList<>(), verifier.getCRLsFromDSS());
    }

    @Test
    public void getOCSPResponsesFromDSSOCSPsNullTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(new FileInputStream(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier.dss = new PdfDictionary();
        Assert.assertEquals(new ArrayList<>(), verifier.getOCSPResponsesFromDSS());
    }
}
