package com.itextpdf.signatures.sign;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.esf.SignaturePolicyId;
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PadesSigTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PadesSigTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PadesSigTest/";

    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void padesRsaSigTest01() throws IOException, GeneralSecurityException, TSPException, OperatorCreationException {
        signApproval(certsSrc + "signCertRsa01.p12", destinationFolder + "padesRsaSigTest01.pdf");
    }

    @Test
    @Ignore("For some reason signatures created with the given cert (either by iText or acrobat) are considered invalid")
    public void padesDsaSigTest01() throws IOException, GeneralSecurityException, TSPException, OperatorCreationException {
        signApproval(certsSrc + "signCertDsa01.p12", destinationFolder + "padesDsaSigTest01.pdf");
    }

    @Test
    public void padesEccSigTest01() throws IOException, GeneralSecurityException, TSPException, OperatorCreationException {
        signApproval(certsSrc + "signCertEcc01.p12", destinationFolder + "padesEccSigTest01.pdf");
    }

    @Test
    public void padesEpesProfileTest01() throws IOException, GeneralSecurityException {

        String notExistingSignaturePolicyOid = "2.16.724.631.3.1.124.2.29.9";
        ASN1ObjectIdentifier asn1PolicyOid = DERObjectIdentifier.getInstance(new DERObjectIdentifier(notExistingSignaturePolicyOid));
        AlgorithmIdentifier hashAlg = new AlgorithmIdentifier(new ASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest("SHA1")));

        // indicate that the policy hash value is not known; see ETSI TS 101 733 V2.2.1, 5.8.1
        byte[] zeroSigPolicyHash = {0};
        DEROctetString hash = new DEROctetString(zeroSigPolicyHash);

        SignaturePolicyId signaturePolicyId = new SignaturePolicyId(asn1PolicyOid, new OtherHashAlgAndValue(hashAlg, hash));
        SignaturePolicyIdentifier sigPolicyIdentifier = new SignaturePolicyIdentifier(signaturePolicyId);

        signApproval(certsSrc + "signCertRsa01.p12", destinationFolder + "padesEpesProfileTest01.pdf", sigPolicyIdentifier);
    }

    private void signApproval(String signCertFileName, String outFileName) throws IOException, GeneralSecurityException {
        signApproval(signCertFileName, outFileName, null);
    }

    private void signApproval(String signCertFileName, String outFileName, SignaturePolicyIdentifier sigPolicyInfo) throws IOException, GeneralSecurityException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        Certificate[] signChain = Pkcs12FileHelper.readFirstChain(signCertFileName, password);
        PrivateKey signPrivateKey = Pkcs12FileHelper.readFirstKey(signCertFileName, password, password);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);


        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(outFileName), false);
        signer.setFieldName("Signature1");
        signer.getSignatureAppearance()
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setLayer2Text("Approval test signature.\nCreated by iText7.");

        if (sigPolicyInfo == null) {
            signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
        } else {
            signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, 0, PdfSigner.CryptoStandard.CADES, sigPolicyInfo);
        }
    }
}
