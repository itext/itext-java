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
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SecurityIDs;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class IsoSignatureExtensionsRoundtripTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/signatures/sign/IsoSignatureExtensionsRoundtripTests/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/signatures/sign/IsoSignatureExtensionsRoundtripTests/";
    private static final char[] SAMPLE_KEY_PASSPHRASE = "secret".toCharArray();
    private static final String SOURCE_FILE = SOURCE_FOLDER + "helloWorldDoc.pdf";
    private static final String SIGNATURE_FIELD = "Signature";

    @BeforeAll
    public static void before() {
        Assumptions.assumeFalse(BOUNCY_CASTLE_FACTORY.isInApprovedOnlyMode());
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void testEd25519() throws Exception {
        doRoundTrip("ed25519", DigestAlgorithms.SHA512, EdECObjectIdentifiers.id_Ed25519);
    }

    @Test
    public void testEd448() throws Exception {
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            doRoundTrip("ed448", DigestAlgorithms.SHAKE256, EdECObjectIdentifiers.id_Ed448);
        } else {
            // SHAKE256 is currently not supported in BCFIPS
            Exception e = Assertions.assertThrows(NoSuchAlgorithmException.class, () ->
                    doRoundTrip("ed448", DigestAlgorithms.SHAKE256, EdECObjectIdentifiers.id_Ed448));
        }
    }

    @Test
    public void testBrainpoolP384r1WithSha384() throws Exception {
        doRoundTrip("brainpoolP384r1", DigestAlgorithms.SHA384, X9ObjectIdentifiers.ecdsa_with_SHA384);
    }
    
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.ALGORITHM_NOT_FROM_SPEC, count = 3), ignore = true)
    public void testPlainBrainpoolP384r1WithSha384() throws Exception {
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            doRoundTrip("plainBrainpoolP384r1", DigestAlgorithms.SHA384, "PLAIN-ECDSA",
                    BSIObjectIdentifiers.ecdsa_plain_SHA384);
        } else {
            // PLAIN_ECDSA is currently not supported in BCFIPS
            Assertions.assertThrows(PdfException.class,
                    () -> doRoundTrip("plainBrainpoolP384r1", DigestAlgorithms.SHA384,
                            "PLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_SHA384));
        }
    }
    
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.ALGORITHM_NOT_FROM_SPEC, count = 3), ignore = true)
    public void testCvcBrainpoolP384r1WithSha384() throws Exception {
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            doRoundTrip("cvcBrainpoolP384r1", DigestAlgorithms.SHA384, "CVC-ECDSA",
                    EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
        } else {
            // CVC_ECDSA is currently not supported in BCFIPS
            Assertions.assertThrows(PdfException.class,
                    () -> doRoundTrip("cvcBrainpoolP384r1", DigestAlgorithms.SHA384,
                            "CVC-ECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_384));
        }
    }

    @Test
    public void testBrainpoolP384r1WithSha3_384() throws Exception {
        doRoundTrip("brainpoolP384r1", DigestAlgorithms.SHA3_384, NISTObjectIdentifiers.id_ecdsa_with_sha3_384);
    }

    @Test
    public void testNistP256WithSha3_256() throws Exception {
        doRoundTrip("nistp256", DigestAlgorithms.SHA3_256, NISTObjectIdentifiers.id_ecdsa_with_sha3_256);
    }

    @Test
    public void testRsaWithSha3_512() throws Exception {
        // For now we use a generic OID, but NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512 would
        // be more appropriate
        doRoundTrip("rsa", DigestAlgorithms.SHA3_512, new ASN1ObjectIdentifier(SecurityIDs.ID_RSA_WITH_SHA3_512));
    }

    @Test
    public void testRsaSsaPssWithSha3_256() throws Exception {
        doRoundTrip("rsa", DigestAlgorithms.SHA3_256, "RSASSA-PSS", new ASN1ObjectIdentifier(SecurityIDs.ID_RSASSA_PSS));
    }

    @Test
    public void testRsaWithSha3_256() throws Exception {
        doRoundTrip("dsa", DigestAlgorithms.SHA3_256, NISTObjectIdentifiers.id_dsa_with_sha3_256);
    }

    @Test
    public void testEd25519ForceSha512WhenSigning() {
        Exception e = Assertions.assertThrows(PdfException.class, () ->
            doSign("ed25519", DigestAlgorithms.SHA1, null, new ByteArrayOutputStream())
        );
        Assertions.assertEquals(
                "Ed25519 requires the document to be digested using SHA-512, not SHA1", e.getMessage()
        );
    }

    @Test
    public void testEd448ForceShake256WhenSigning() {
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    doSign("ed448", DigestAlgorithms.SHA1, null, new ByteArrayOutputStream())
            );
            Assertions.assertEquals(
                    "Ed448 requires the document to be digested using 512-bit SHAKE256, not SHA1", e.getMessage()
            );
        } else {
            // SHAKE256 is currently not supported in BCFIPS
            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    doSign("ed448", DigestAlgorithms.SHA1, null, new ByteArrayOutputStream()));
        }
    }

    @Test
    public void testEd25519ForceSha512WhenValidating() {
        // file contains an Ed25519 signature where the document digest is computed using SHA-1
        String referenceFile = Paths.get(SOURCE_FOLDER, "bad-digest-ed25519.pdf").toString();
        Exception e = Assertions.assertThrows(PdfException.class, () -> doVerify(referenceFile, null));
        Assertions.assertEquals(
                "Ed25519 requires the document to be digested using SHA-512, not SHA1",
                e.getCause().getCause().getMessage()
        );
    }

    @Test
    public void testEd448ForceShake256WhenValidating() {
        // file contains an Ed448 signature where the document digest is computed using SHA-1
        String referenceFile = Paths.get(SOURCE_FOLDER, "bad-digest-ed448.pdf").toString();
        Exception e = Assertions.assertThrows(PdfException.class, () -> doVerify(referenceFile, null));
        Assertions.assertEquals(
                "Ed448 requires the document to be digested using 512-bit SHAKE256, not SHA1",
                e.getCause().getCause().getMessage()
        );
    }

    @Test
    public void testRsaWithSha3ExtensionDeclarations() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doSign("rsa", DigestAlgorithms.SHA3_256, null, baos);
        checkIsoExtensions(baos.toByteArray(), Collections.singleton(32001));
    }

    @Test
    public void testEd25519ExtensionDeclarations() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doSign("ed25519", DigestAlgorithms.SHA512, null, baos);
        checkIsoExtensions(baos.toByteArray(), Collections.singleton(32002));
    }

    @Test
    public void testEd448ExtensionDeclarations() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            doSign("ed448", DigestAlgorithms.SHAKE256, null, baos);
            checkIsoExtensions(baos.toByteArray(), Arrays.asList(32001, 32002));
        } else {
            // SHAKE256 is currently not supported in BCFIPS
            Exception e = Assertions.assertThrows(NoSuchAlgorithmException.class, () ->
                    doSign("ed448", DigestAlgorithms.SHAKE256, null, baos));
        }
    }

    @Test
    public void testIsoExtensionsWithMultipleSignatures() throws Exception {
        String keySample1 = "rsa";
        String keySample2 = "ed25519";
        Path sourceFolder = Paths.get(SOURCE_FOLDER);

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        Certificate root = readCertificate(sourceFolder.resolve("ca.crt"));
        Certificate signerCert1 = readCertificate(sourceFolder.resolve(keySample1 + ".crt"));
        Certificate signerCert2 = readCertificate(sourceFolder.resolve(keySample2 + ".crt"));
        Certificate[] signChain1 = new Certificate[] {signerCert1, root};
        Certificate[] signChain2 = new Certificate[] {signerCert2, root};

        try(InputStream in1 = FileUtil.getInputStreamForFile(SOURCE_FILE)) {
            PrivateKey signPrivateKey = readUnencryptedPrivateKey(sourceFolder.resolve(keySample1 + ".key.pem"));
            IExternalSignature pks = new PrivateKeySignature(
                    signPrivateKey, DigestAlgorithms.SHA3_256, BOUNCY_CASTLE_FACTORY.getProviderName()
            );

            PdfSigner signer = new PdfSigner(new PdfReader(in1), baos1, new StampingProperties());
            signer.setFieldName("Signature1");
            signer.signDetached(
                    new BouncyCastleDigest(), pks, signChain1, null, null, null, 0,
                    PdfSigner.CryptoStandard.CMS);
        }

        try(InputStream in2 = new ByteArrayInputStream(baos1.toByteArray())) {
            PrivateKey signPrivateKey = readUnencryptedPrivateKey(sourceFolder.resolve(keySample2 + ".key.pem"));
            IExternalSignature pks = new PrivateKeySignature(
                    signPrivateKey, DigestAlgorithms.SHA512, BOUNCY_CASTLE_FACTORY.getProviderName()
            );

            PdfSigner signer = new PdfSigner(new PdfReader(in2), baos2, new StampingProperties());
            signer.setFieldName("Signature2");
            signer.signDetached(
                    new BouncyCastleDigest(), pks, signChain2, null, null, null, 0,
                    PdfSigner.CryptoStandard.CMS);
        }

        checkIsoExtensions(baos2.toByteArray(), Arrays.asList(32001, 32002));
    }

    private void doRoundTrip(String keySampleName, String digestAlgo, String signatureAlgo, ASN1ObjectIdentifier expectedSigAlgoIdentifier) throws GeneralSecurityException, IOException {
        String outFile = Paths.get(DESTINATION_FOLDER, keySampleName + "-" + digestAlgo + ".pdf").toString();
        doSign(keySampleName, digestAlgo, signatureAlgo, outFile);
        doVerify(outFile, expectedSigAlgoIdentifier);
    }

    private void doRoundTrip(String keySampleName, String digestAlgo, ASN1ObjectIdentifier expectedSigAlgoIdentifier) throws GeneralSecurityException, IOException {
        doRoundTrip(keySampleName, digestAlgo, null, expectedSigAlgoIdentifier);
    }

    private void doSign(String keySampleName, String digestAlgo, String signatureAlgo, String outFile)
            throws IOException, GeneralSecurityException {
        // write to a file for easier inspection when debugging
        try (OutputStream fos = FileUtil.getFileOutputStream(outFile)) {
            doSign(keySampleName, digestAlgo, signatureAlgo, fos);
        }
    }

    private void doSign(String keySampleName, String digestAlgo, String signatureAlgo, OutputStream os)
            throws IOException, GeneralSecurityException {
        Certificate root = readCertificate(Paths.get(SOURCE_FOLDER, "ca.crt"));
        Certificate signerCert = readCertificate(Paths.get(SOURCE_FOLDER, keySampleName + ".crt"));
        Certificate[] signChain = new Certificate[] {signerCert, root};
        PrivateKey signPrivateKey = readUnencryptedPrivateKey(Paths.get(SOURCE_FOLDER, keySampleName + ".key.pem"));
        // The default provider doesn't necessarily distinguish between different types of EdDSA keys
        // and accessing that information requires APIs that are not available in older JDKs we still support.
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, digestAlgo, signatureAlgo, BOUNCY_CASTLE_FACTORY.getProviderName(), null);

        PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), os, new StampingProperties());
        signer.setFieldName(SIGNATURE_FIELD);
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName())
                .setContent("Approval test signature.\nCreated by iText.");
        signer
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);

        signer.signDetached(
                new BouncyCastleDigest(), pks, signChain, null, null, null, 0,
                PdfSigner.CryptoStandard.CMS);
    }

    private void doVerify(String fileName, ASN1ObjectIdentifier expectedSigAlgoIdentifier)
            throws IOException, GeneralSecurityException {
        try (PdfReader r = new PdfReader(fileName); PdfDocument pdfDoc = new PdfDocument(r)) {
            SignatureUtil u = new SignatureUtil(pdfDoc);
            PdfPKCS7 data = u.readSignatureData(SIGNATURE_FIELD, BOUNCY_CASTLE_FACTORY.getProviderName());
            Assertions.assertTrue(data.verifySignatureIntegrityAndAuthenticity());
            if (expectedSigAlgoIdentifier != null) {
                ASN1ObjectIdentifier oid = new ASN1ObjectIdentifier(data.getSignatureMechanismOid());
                Assertions.assertEquals(expectedSigAlgoIdentifier, oid);
            }
        }
    }

    private void checkIsoExtensions(byte[] fileData, Collection<Integer> expectedLevels)
            throws IOException {
        try (PdfReader r = new PdfReader(new ByteArrayInputStream(fileData));
             PdfDocument pdfDoc = new PdfDocument(r)) {
            PdfArray isoExtensions = pdfDoc
                    .getCatalog()
                    .getPdfObject()
                    .getAsDictionary(PdfName.Extensions)
                    .getAsArray(PdfName.ISO_);
            Assertions.assertEquals(expectedLevels.size(), isoExtensions.size());

            Set<Integer> actualLevels = new HashSet<>();
            for (int i = 0; i < isoExtensions.size(); i++) {
                PdfDictionary extDict = isoExtensions.getAsDictionary(i);
                actualLevels.add(extDict.getAsNumber(PdfName.ExtensionLevel).intValue());
            }

            Set<Integer> expectedLevelSet = new HashSet<>(expectedLevels);
            Assertions.assertEquals(expectedLevelSet, actualLevels);
        }
    }

    private Certificate readCertificate(Path path) throws IOException, GeneralSecurityException {
        byte[] content = Files.readAllBytes(path);
        IX509CertificateHolder certHolder = BOUNCY_CASTLE_FACTORY.createX509CertificateHolder(content);
        return BOUNCY_CASTLE_FACTORY.createJcaX509CertificateConverter().getCertificate(certHolder);
    }

    private PrivateKey readUnencryptedPrivateKey(Path path) throws GeneralSecurityException {
        try {
            return PemFileHelper.readFirstKey(path.toString(), SAMPLE_KEY_PASSPHRASE);
        } catch (Exception e) {
            throw new KeyException(e);
        }
    }
}
