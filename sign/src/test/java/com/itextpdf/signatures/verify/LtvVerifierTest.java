package com.itextpdf.signatures.verify;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.LtvVerifier;
import com.itextpdf.signatures.VerificationOK;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LtvVerifierTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/verify/LtvVerifierTest/";
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        ITextTest.removeCryptographyRestrictions();
    }

    @AfterClass
    public static void after() {
        ITextTest.restoreCryptographyRestrictions();
    }

    @Test
    public void validLtvDocTest01() throws IOException, GeneralSecurityException {
        String ltvTsFileName = sourceFolder + "ltvDoc.pdf";

        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(ltvTsFileName)));
        verifier.setCertificateOption(LtvVerification.CertificateOption.WHOLE_CHAIN);
        verifier.setRootStore(Pkcs12FileHelper.initStore(certsSrc + "rootStore.p12", password));
        List<VerificationOK> verificationMessages = verifier.verify(null);

        Assert.assertEquals(7, verificationMessages.size());
    }
}
