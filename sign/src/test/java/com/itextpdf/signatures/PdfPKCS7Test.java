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
package com.itextpdf.signatures;

import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class PdfPKCS7Test extends PdfPKCS7BasicTest {

    private static final double EPS = 0.001;

    @Test
    // PdfPKCS7 is created here the same way it's done in PdfSigner#signDetached,
    // only the hash algorithm is altered
    public void unknownHashAlgorithmTest() {
        String hashAlgorithm = "";
        Exception e = Assert.assertThrows(PdfException.class,
                () -> new PdfPKCS7(null, chain, hashAlgorithm, null,
                        new BouncyCastleDigest(), false));
        Assert.assertEquals(
                MessageFormatUtil.format(SignExceptionMessageConstant.UNKNOWN_HASH_ALGORITHM, hashAlgorithm),
                e.getMessage());
    }

    @Test
    public void simpleCreationTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(null, chain, hashAlgorithm, null, false);

        String expectedOid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);
        Assert.assertEquals(expectedOid, pkcs7.getDigestAlgorithmOid());
        Assert.assertEquals(chain[0], pkcs7.getSigningCertificate());
        Assert.assertArrayEquals(chain, pkcs7.getCertificates());
        Assert.assertNull(pkcs7.getSignatureMechanismOid());

        // test default fields
        Assert.assertEquals(1, pkcs7.getVersion());
        Assert.assertEquals(1, pkcs7.getSigningInfoVersion());
    }

    @Test
    public void simpleCreationWithPrivateKeyTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(pk, chain, hashAlgorithm, null, new BouncyCastleDigest(), false);

        String expectedOid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);
        Assert.assertEquals(expectedOid, pkcs7.getDigestAlgorithmOid());
        Assert.assertEquals(chain[0], pkcs7.getSigningCertificate());
        Assert.assertArrayEquals(chain, pkcs7.getCertificates());
        Assert.assertEquals(SecurityIDs.ID_RSA_WITH_SHA256, pkcs7.getSignatureMechanismOid());
    }

    @Test
    public void notAvailableSignatureTest() {
        String hashAlgorithm = "GOST3411";
        // Throws different exceptions on .net and java, bc/bcfips
        Assert.assertThrows(Exception.class,
                () -> new PdfPKCS7(pk, chain, hashAlgorithm, null, new BouncyCastleDigest(), false));
    }

    @Test
    public void reasonSetGetTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        Assert.assertNull(pkcs7.getReason());

        String testReason = "testReason";
        pkcs7.setReason(testReason);
        Assert.assertEquals(testReason, pkcs7.getReason());
    }

    @Test
    public void locationSetGetTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        Assert.assertNull(pkcs7.getLocation());

        String testLocation = "testLocation";
        pkcs7.setLocation(testLocation);
        Assert.assertEquals(testLocation, pkcs7.getLocation());
    }

    @Test
    public void signNameSetGetTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        Assert.assertNull(pkcs7.getSignName());

        String testSignName = "testSignName";
        pkcs7.setSignName(testSignName);
        Assert.assertEquals(testSignName, pkcs7.getSignName());
    }

    @Test
    public void signDateSetGetTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        Assert.assertEquals(TimestampConstants.UNDEFINED_TIMESTAMP_DATE, pkcs7.getSignDate());

        Calendar testSignDate = DateTimeUtil.getCurrentTimeCalendar();
        pkcs7.setSignDate(testSignDate);
        Assert.assertEquals(testSignDate, pkcs7.getSignDate());
    }

    @Test
    public void ocspGetTest() throws IOException, ParseException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "ltvEnabledSingleSignatureTest01.pdf"));
        SignatureUtil sigUtil = new SignatureUtil(outDocument);
        PdfPKCS7 pkcs7 = sigUtil.readSignatureData("Signature1");

        Assert.assertNull(pkcs7.getCRLs());
        // it's tested here that ocsp and time stamp token were found while
        // constructing PdfPKCS7 instance
        ITSTInfo timeStampTokenInfo = pkcs7.getTimeStampTokenInfo();
        Assert.assertNotNull(timeStampTokenInfo);

        // The number corresponds to 3 September, 2021 13:32:33.
        double expectedMillis = (double) 1630675953000L;
        Assert.assertEquals(
                TimeTestUtil.getFullDaysMillis(expectedMillis),
                TimeTestUtil.getFullDaysMillis(DateTimeUtil.getUtcMillisFromEpoch(
                        DateTimeUtil.getCalendar(timeStampTokenInfo.getGenTime()))),
                EPS);
        Assert.assertEquals(
                TimeTestUtil.getFullDaysMillis(expectedMillis),
                TimeTestUtil.getFullDaysMillis(DateTimeUtil.getUtcMillisFromEpoch(
                        DateTimeUtil.getCalendar(pkcs7.getOcsp().getProducedAtDate()))),
                EPS);
    }

    @Test
    public void verifyTimestampImprintSimpleSignatureTest() throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "simpleSignature.pdf"));
        PdfPKCS7 pkcs7 = new SignatureUtil(outDocument).readSignatureData("Signature1");
        Assert.assertFalse(pkcs7.verifyTimestampImprint());
    }

    @Test
    public void verifyTimestampImprintTimeStampSignatureTest() throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timeStampSignature.pdf"));
        PdfPKCS7 pkcs7 = new SignatureUtil(outDocument).readSignatureData("timestampSig1");
        Assert.assertFalse(pkcs7.verifyTimestampImprint());
    }

    @Test
    public void verifyTimestampImprintEmbeddedTimeStampSignatureTest() throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "embeddedTimeStampSignature.pdf"));
        PdfPKCS7 pkcs7 = new SignatureUtil(outDocument).readSignatureData("Signature1");
        Assert.assertTrue(pkcs7.verifyTimestampImprint());
    }

    @Test
    public void verifyTimestampImprintCorruptedTimeStampSignatureTest() throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "embeddedTimeStampCorruptedSignature.pdf"));
        PdfPKCS7 pkcs7 = new SignatureUtil(outDocument).readSignatureData("Signature1");
        Assert.assertTrue(pkcs7.verifyTimestampImprint());
    }

    @Test
    public void findCrlIsNotNullTest() throws IOException, CRLException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "singleSignatureNotEmptyCRL.pdf"));
        SignatureUtil sigUtil = new SignatureUtil(outDocument);
        PdfPKCS7 pkcs7 = sigUtil.readSignatureData("Signature1");
        List<X509CRL> crls = pkcs7.getCRLs().stream().map(crl -> (X509CRL) crl).collect(Collectors.toList());
        Assert.assertEquals(2, crls.size());
        Assert.assertArrayEquals(crls.get(0).getEncoded(),
                Files.readAllBytes(Paths.get(SOURCE_FOLDER, "firstCrl.bin")));
        Assert.assertArrayEquals(crls.get(1).getEncoded(),
                Files.readAllBytes(Paths.get(SOURCE_FOLDER, "secondCrl.bin")));
    }

    @Test
    public void findCrlNullSequenceNoExceptionTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        pkcs7.findCRL(null);
        Assert.assertTrue(pkcs7.getCRLs().isEmpty());
    }

    @Test
    public void isRevocationValidWithInvalidOcspTest() throws IOException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "signatureWithInvalidOcspTest.pdf"));
        SignatureUtil sigUtil = new SignatureUtil(outDocument);
        PdfPKCS7 pkcs7 = sigUtil.readSignatureData("Signature1");
        Assert.assertFalse(pkcs7.isRevocationValid());
    }

    @Test
    public void isRevocationValidWithValidOcspTest() throws IOException {
        PdfDocument outDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "signatureWithValidOcspTest.pdf"));
        SignatureUtil sigUtil = new SignatureUtil(outDocument);
        PdfPKCS7 pkcs7 = sigUtil.readSignatureData("Signature1");
        Assert.assertTrue(pkcs7.isRevocationValid());
    }

    @Test
    public void isRevocationValidOcspResponseIsNullTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        pkcs7.basicResp = null;
        Assert.assertFalse(pkcs7.isRevocationValid());
    }

    @Test
    public void isRevocationValidLackOfSignCertsTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, IOException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        pkcs7.basicResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(
                BOUNCY_CASTLE_FACTORY.createASN1InputStream(
                        Files.readAllBytes(Paths.get(SOURCE_FOLDER, "simpleOCSPResponse.bin"))).readObject());
        pkcs7.signCerts = Collections.singleton(chain[0]);
        Assert.assertFalse(pkcs7.isRevocationValid());
    }

    @Test
    public void isRevocationValidExceptionDuringValidationTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, IOException {
        PdfPKCS7 pkcs7 = createSimplePdfPKCS7();
        pkcs7.basicResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(
                BOUNCY_CASTLE_FACTORY.createASN1InputStream(
                        Files.readAllBytes(Paths.get(SOURCE_FOLDER, "simpleOCSPResponse.bin"))).readObject());
        pkcs7.signCerts = Arrays.asList(new Certificate[] {null, null});
        Assert.assertFalse(pkcs7.isRevocationValid());
    }

    @Test
    public void getEncodedPkcs1Test()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, IOException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(pk, chain, hashAlgorithm, null, new BouncyCastleDigest(), true);
        byte[] bytes = pkcs7.getEncodedPKCS1();
        byte[] cmpBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "cmpBytesPkcs1.txt"));
        IASN1OctetString outOctetString = BOUNCY_CASTLE_FACTORY.createASN1OctetString(bytes);
        IASN1OctetString cmpOctetString = BOUNCY_CASTLE_FACTORY.createASN1OctetString(cmpBytes);
        Assert.assertEquals(outOctetString, cmpOctetString);
    }

    @Test
    public void getEncodedPkcs1NullPrivateKeyTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(null, chain, hashAlgorithm, null, new BouncyCastleDigest(), true);
        Exception exception = Assert.assertThrows(PdfException.class, () -> pkcs7.getEncodedPKCS1());
        Assert.assertEquals(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION, exception.getMessage());
    }

    @Test
    public void getEncodedPkcs7UnknownExceptionTest()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(pk, chain, hashAlgorithm, null, new BouncyCastleDigest(), true);
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(chain), pk);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> pkcs7.getEncodedPKCS7(null, CryptoStandard.CMS, testTsa, null, null));
        Assert.assertEquals(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION, exception.getMessage());
    }

    @Test
    public void getEncodedPkcs7Test()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, IOException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(pk, chain, hashAlgorithm, null, new BouncyCastleDigest(), true);
        byte[] bytes = pkcs7.getEncodedPKCS7();
        byte[] cmpBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "cmpBytesPkcs7.txt"));
        IASN1Primitive outStream = BOUNCY_CASTLE_FACTORY.createASN1Primitive(bytes);
        IASN1Primitive cmpStream = BOUNCY_CASTLE_FACTORY.createASN1Primitive(cmpBytes);
        Assert.assertEquals("SHA256withRSA", pkcs7.getSignatureMechanismName());
        Assert.assertEquals(outStream, cmpStream);
    }

    @Test
    public void getEncodedPkcs7WithRevocationInfoTest() throws NoSuchAlgorithmException, InvalidKeyException,
            NoSuchProviderException, IOException, CertificateException, CRLException {
        String hashAlgorithm = DigestAlgorithms.SHA256;
        PdfPKCS7 pkcs7 = new PdfPKCS7(pk, chain, hashAlgorithm, null, new BouncyCastleDigest(), true);
        pkcs7.getSignedDataCRLs().add(SignTestPortUtil.parseCrlFromStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "firstCrl.bin")));
        pkcs7.getSignedDataOcsps().add(BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(BOUNCY_CASTLE_FACTORY.createASN1InputStream(
                        Files.readAllBytes(Paths.get(SOURCE_FOLDER, "simpleOCSPResponse.bin"))).readObject()));
        byte[] bytes = pkcs7.getEncodedPKCS7();
        byte[] cmpBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "cmpBytesPkcs7WithRevInfo.txt"));
        Assert.assertEquals("SHA256withRSA", pkcs7.getSignatureMechanismName());
        Assert.assertEquals(serializedAsString(bytes), serializedAsString(cmpBytes));
    }

    @Test
    public void verifyEd448SignatureTest() throws IOException, GeneralSecurityException {
        // SHAKE256 is not available in BCFIPS
        if ("BCFIPS".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            Assert.assertThrows(PdfException.class,
                    () -> verifyIsoExtensionExample("Ed448", "sample-ed448-shake256.pdf"));
        } else {
            verifyIsoExtensionExample("Ed448", "sample-ed448-shake256.pdf");
        }
    }

    @Test
    public void verifyNistECDSASha2SignatureTest() throws IOException, GeneralSecurityException {
        verifyIsoExtensionExample("SHA256withECDSA", "sample-nistp256-sha256.pdf");
    }

    @Test
    public void verifyBrainpoolSha2SignatureTest() throws IOException, GeneralSecurityException {
        verifyIsoExtensionExample("SHA384withECDSA", "sample-brainpoolP384r1-sha384.pdf");
    }

    // PdfPKCS7 is created here the same way it's done in PdfSigner#signDetached
    private static PdfPKCS7 createSimplePdfPKCS7()
            throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        return new PdfPKCS7(null, chain, DigestAlgorithms.SHA256, null,
                new BouncyCastleDigest(), false);
    }

    private String serializedAsString(byte[] serialized) throws IOException {
        IASN1InputStream is = BOUNCY_CASTLE_FACTORY.createASN1InputStream(serialized);
        IASN1Primitive obj1 = is.readObject();
        return BOUNCY_CASTLE_FACTORY.createASN1Dump().dumpAsString(obj1, true).replace("\r\n", "\n");
    }
}
