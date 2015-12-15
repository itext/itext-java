package com.itextpdf.signatures;


import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


//TODO: add some validation of results in future
@Category(IntegrationTest.class)
public class SigningTest {

    public static final String sourceFolder = "src/test/resources/signingTest/";
    public static final String destinationFolder = "target/test/signingTest/";
    public static final String keystorePath = "src/test/resources/ks";
    public static final char[] password = "password".toCharArray();

    private BouncyCastleProvider provider;
    private Certificate[] chain;
    private PrivateKey pk;

    @Before
    public void init() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), password);
        String alias = ks.aliases().nextElement();
        pk = (PrivateKey) ks.getKey(alias, password);
        chain = ks.getCertificateChain(alias);

        new File(destinationFolder).mkdirs();
    }

    @Test
    public void simpleSigningTest() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "simpleDocument.pdf";
        String dest = destinationFolder + "simpleSignature.pdf";

        int x = 36;
        int y = 648;
        int w = 200;
        int h = 100;
        Rectangle rect = new Rectangle(x, y, w, h);

        String fieldName =  "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                MakeSignature.CryptoStandard.CADES, "Test 1", "TestCity", rect, false);
    }

    @Test
    public void signingIntoExistingFieldTest01() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "emptySignature01.pdf"; //field is merged with widget and has /P key
        String dest = destinationFolder + "filledSignatureFields01.pdf";

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                MakeSignature.CryptoStandard.CADES, "Test 1", "TestCity", null, false);
    }

    @Test
    public void signingIntoExistingFieldTest02() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "emptySignature02.pdf"; //field is merged with widget and widget doesn't have /P key
        String dest = destinationFolder + "filledSignatureFields02.pdf";

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                MakeSignature.CryptoStandard.CADES, "Test 1", "TestCity", null, false);
    }

    @Test
    public void signingIntoExistingReuseAppearanceTest() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "emptySigWithAppearance.pdf";
        String dest = destinationFolder + "filledSignatureReuseAppearanceFields.pdf";

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                MakeSignature.CryptoStandard.CADES, "Test 1", "TestCity", null, true);
    }

    protected void sign(String src, String name, String dest,
                     Certificate[] chain, PrivateKey pk,
                     String digestAlgorithm, String provider, MakeSignature.CryptoStandard subfilter,
                     String reason, String location, Rectangle rectangle, boolean setReuseAppearance)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new PdfWriter(dest), false);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);


        appearance.setReuseAppearance(setReuseAppearance);

        if (rectangle != null) {
            appearance.setPageRect(rectangle);
        }

        signer.setFieldName(name);
        // Creating the signature
        ExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        ExternalDigest digest = new BouncyCastleDigest();
        MakeSignature.signDetached(signer, digest, pks, chain, null, null, null, 0, subfilter);
    }
}
