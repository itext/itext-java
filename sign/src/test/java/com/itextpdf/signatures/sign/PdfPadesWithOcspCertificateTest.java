package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class PdfPadesWithOcspCertificateTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithOcspCertificateTest/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithOcspCertificateTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesWithOcspCertificateTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }
    
    @Test
    public void signCertWithOcspTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "signCertWithOcspTest.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signRsaWithOcsp.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + "rootRsa2.pem";
        String ocspCertFileName = certsSrc + "ocspCert.pem";

        Certificate signRsaCert = PemFileHelper.readFirstChain(signCertFileName)[0];
        Certificate rootCert = PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] signRsaChain = new Certificate[2];
        signRsaChain[0] = signRsaCert;
        signRsaChain[1] = rootCert;
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        Certificate ocspCert = PemFileHelper.readFirstChain(ocspCertFileName)[0];
        PrivateKey ocspPrivateKey = PemFileHelper.readFirstKey(ocspCertFileName, PASSWORD);
        
        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient(null);
        ocspClient.addBuilderForCertIssuer((X509Certificate) signRsaCert, (X509Certificate) ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer((X509Certificate) ocspCert, (X509Certificate) ocspCert, ocspPrivateKey);

        try (OutputStream outputStream = FileUtil.getFileOutputStream(outFileName)) {
            PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
            padesSigner.setOcspClient(ocspClient);
            // It is expected to have two OCSP responses, one for signing cert and another for OCSP response.
            padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);

            PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

            Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
        }
    }

    @Test
    public void signCertWithoutOcspTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "signCertWithoutOcspTest.pdf";
        String outFileName = destinationFolder + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signRsaWithoutOcsp.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + "rootRsa.pem";
        String ocspCertFileName = certsSrc + "ocspCert.pem";

        Certificate signRsaCert = PemFileHelper.readFirstChain(signCertFileName)[0];
        Certificate rootCert = PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] signRsaChain = new Certificate[2];
        signRsaChain[0] = signRsaCert;
        signRsaChain[1] = rootCert;
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        Certificate ocspCert = PemFileHelper.readFirstChain(ocspCertFileName)[0];
        PrivateKey ocspPrivateKey = PemFileHelper.readFirstKey(ocspCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient(null);
        ocspClient.addBuilderForCertIssuer((X509Certificate) signRsaCert, (X509Certificate) ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer((X509Certificate) ocspCert, (X509Certificate) ocspCert, ocspPrivateKey);

        try (OutputStream outputStream = FileUtil.getFileOutputStream(outFileName)) {
            PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
            padesSigner.setOcspClient(ocspClient);
            
            Exception exception = Assert.assertThrows(PdfException.class, () -> 
                    padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa));
            Assert.assertEquals(SignExceptionMessageConstant.NO_REVOCATION_DATA_FOR_SIGNING_CERTIFICATE, exception.getMessage());
        }
    }

    @Test
    public void signCertWithOcspOcspCertSameAsSignCertTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "signCertWithOcspOcspCertSameAsSignCertTest.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signRsaWithOcsp.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + "rootRsa2.pem";

        Certificate signRsaCert = PemFileHelper.readFirstChain(signCertFileName)[0];
        Certificate rootCert = PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] signRsaChain = new Certificate[2];
        signRsaChain[0] = signRsaCert;
        signRsaChain[1] = rootCert;
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient(null);
        ocspClient.addBuilderForCertIssuer((X509Certificate) signRsaCert, (X509Certificate) signRsaCert, signRsaPrivateKey);

        try (OutputStream outputStream = FileUtil.getFileOutputStream(outFileName)) {
            PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
            padesSigner.setOcspClient(ocspClient);
            // It is expected to have one OCSP response, only for signing cert.
            padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);

            PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

            Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
        }
    }

    private SignerProperties createSignerProperties() {
        SignerProperties signerProperties = new SignerProperties();
        signerProperties.setFieldName("Signature1");
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signerProperties.getFieldName())
                .setContent("Approval test signature.\nCreated by iText.");
        signerProperties.setPageRect(new Rectangle(50, 650, 200, 100))
                .setSignatureAppearance(appearance);

        return signerProperties;
    }

    private PdfPadesSigner createPdfPadesSigner(String srcFileName, OutputStream outputStream) throws IOException {
        return new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)), outputStream);
    }
}
