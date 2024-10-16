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
package com.itextpdf.signatures.cms;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureMechanisms;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CMSContainerTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/cms/CMSContainerTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private X509Certificate[] chain;
    private X509Certificate signCert;

    private byte[] testCrlResponse;

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        Security.addProvider(FACTORY.getProvider());
        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsaWithChain.pem");
        chain = new X509Certificate[certChain.length];
        for (int i = 0; i < certChain.length; i++) {
            chain[i] = (X509Certificate) certChain[i];
        }
        signCert = chain[0];
        PrivateKey caPrivateKey =
                PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsaWithChain.pem", PASSWORD);
        TestCrlBuilder testCrlBuilder = new TestCrlBuilder(signCert, caPrivateKey);
        testCrlBuilder.addCrlEntry(signCert, FACTORY.createCRLReason().getKeyCompromise());
        testCrlResponse = testCrlBuilder.makeCrl();
    }

    @Test
    public void testSerialize() throws CertificateEncodingException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException, CRLException {
        CMSContainer sut = new CMSContainer();
        sut.addCertificates((X509Certificate[]) chain);

        SignerInfo si = new SignerInfo();
        si.setSigningCertificate(signCert);
        ArrayList<byte[]> fakeOcspREsponses = new ArrayList<>();
        fakeOcspREsponses.add(new byte[250]);
        si.setMessageDigest(new byte[256]);
        si.setOcspResponses(fakeOcspREsponses);
        si.setCrlResponses(Collections.singletonList(testCrlResponse));
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, OID.SHA_512);
        si.setSignatureAlgorithm(new AlgorithmIdentifier(
                SignatureMechanisms.getSignatureMechanismOid("RSA", DigestAlgorithms.SHA512)));
        si.setSignature(new byte[256]);
        sut.setSignerInfo(si);

        byte[] serRes = sut.serialize();
        Assertions.assertEquals(serializedAsString(Base64.decode(CMSTestHelper.EXPECTED_RESULT_CMS_CONTAINER_TEST)),
                serializedAsString(serRes));
    }

    @Test
    public void testSerializationWithRevocationData() throws CertificateException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException, CRLException {
        CMSContainer sut = new CMSContainer();
        sut.addCertificates((X509Certificate[]) chain);
        sut.addCrl(SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(testCrlResponse)));
        sut.addOcsp(FACTORY.createBasicOCSPResponse(FACTORY.createASN1InputStream(
                Files.readAllBytes(Paths.get(SOURCE_FOLDER, "simpleOCSPResponse.bin"))).readObject()));

        SignerInfo si = new SignerInfo();
        si.setSigningCertificate(signCert);
        si.setMessageDigest(new byte[256]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, OID.SHA_512);
        si.setSignatureAlgorithm(new AlgorithmIdentifier(
                SignatureMechanisms.getSignatureMechanismOid("RSA", DigestAlgorithms.SHA512)));
        si.setSignature(new byte[256]);
        sut.setSignerInfo(si);

        byte[] serRes = sut.serialize();

        Assertions.assertEquals(serializedAsString(Base64.decode(CMSTestHelper.CMS_CONTAINER_WITH_OCSP_AND_CRL)),
                serializedAsString(serRes));
    }

    @Test
    public void testGetSizeEstimation() throws CertificateEncodingException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException, CRLException {
        CMSContainer sut = new CMSContainer();
        sut.addCertificates((X509Certificate[]) chain);

        SignerInfo si = new SignerInfo();
        si.setSigningCertificate(signCert);
        ArrayList<byte[]> fakeOcspREsponses = new ArrayList<>();
        fakeOcspREsponses.add(new byte[250]);
        si.setMessageDigest(new byte[256]);
        si.setOcspResponses(fakeOcspREsponses);
        si.setCrlResponses(Collections.singletonList(testCrlResponse));
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.setSignatureAlgorithm(new AlgorithmIdentifier(
                SignatureMechanisms.getSignatureMechanismOid("RSA", DigestAlgorithms.SHA512)));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, OID.SHA_512);
        si.setSignature(new byte[256]);
        sut.setSignerInfo(si);

        long size = sut.getSizeEstimation();

        Assertions.assertEquals(4821, size);
    }

    @Test
    public void testDeserialization() throws CertificateException, IOException, CRLException {
        byte[] rawData = Base64.decode(CMSTestHelper.EXPECTED_RESULT_CMS_CONTAINER_TEST);
        CMSContainer sd = new CMSContainer(rawData);
        Assertions.assertEquals("2.16.840.1.101.3.4.2.3", sd.getDigestAlgorithm().getAlgorithmOid());
        Assertions.assertEquals("1.2.840.113549.1.7.1", sd.getEncapContentInfo().getContentType());
        Assertions.assertEquals(3, sd.getCertificates().size());
        Assertions.assertEquals(0, sd.getCrls().size());
        Assertions.assertEquals(0, sd.getOcsps().size());
        for (X509Certificate certificate : chain) {
            Assertions.assertTrue(sd.getCertificates().stream()
                    .anyMatch(c -> certificate.getSerialNumber().toString().equals(c.getSerialNumber().toString())));
        }
        Assertions.assertEquals(chain[0].getSerialNumber().toString(),
                sd.getSignerInfo().getSigningCertificate().getSerialNumber().toString());
    }

    @Test
    public void testDeserializationWithRevocationData() throws CertificateException, IOException, CRLException {
        byte[] rawData = Base64.decode(CMSTestHelper.CMS_CONTAINER_WITH_OCSP_AND_CRL);
        CMSContainer sd = new CMSContainer(rawData);
        Assertions.assertEquals("2.16.840.1.101.3.4.2.3", sd.getDigestAlgorithm().getAlgorithmOid());
        Assertions.assertEquals("1.2.840.113549.1.7.1", sd.getEncapContentInfo().getContentType());
        Assertions.assertEquals(3, sd.getCertificates().size());
        Assertions.assertEquals(1, sd.getCrls().size());
        Assertions.assertEquals(1, sd.getOcsps().size());
        for (X509Certificate certificate : chain) {
            Assertions.assertTrue(sd.getCertificates().stream()
                    .anyMatch(c -> certificate.getSerialNumber().toString().equals(c.getSerialNumber().toString())));
        }
        Assertions.assertEquals(chain[0].getSerialNumber().toString(),
                sd.getSignerInfo().getSigningCertificate().getSerialNumber().toString());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.UNABLE_TO_PARSE_REV_INFO))
    public void testDeserializationWithIncorrectRevocationData() throws CertificateException, IOException, CRLException {
        byte[] rawData = Base64.decode(CMSTestHelper.CMS_CONTAINER_WITH_INCORRECT_REV_INFO);
        CMSContainer sd = new CMSContainer(rawData);
        Assertions.assertEquals(1, sd.getCrls().size());
        Assertions.assertEquals(1, sd.getOcsps().size());
        Assertions.assertEquals(1, sd.otherRevocationInfo.size());
    }

    @Test
    public void createPkcs7WithRevocationInfoTest() {
        PdfPKCS7 pkcs7 = new PdfPKCS7(Base64.decode(CMSTestHelper.CMS_CONTAINER_WITH_OCSP_AND_CRL),
                PdfName.Adbe_pkcs7_detached, FACTORY.getProviderName());
        Assertions.assertEquals(1, pkcs7.getSignedDataCRLs().size());
        Assertions.assertEquals(1, pkcs7.getSignedDataOcsps().size());
    }

    @Test
    public void testMultipleDigestAlgorithms() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_2DIGEST_ALGOS);
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_ONLY_ONE_SIGNER_ALLOWED, e.getMessage());
    }


    @Test
    public void testMultipleSignerInfos() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_2SIGNERS);
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_ONLY_ONE_SIGNER_ALLOWED, e.getMessage());
    }

    @Test
    public void testCertificatesMissing() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_MISSING_CERTIFICATES);
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_MISSING_CERTIFICATES, e.getMessage());
    }

    @Test
    public void testCertificatesEmpty() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_EMPTY_CERTIFICATES);
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_MISSING_CERTIFICATES, e.getMessage());
    }

    private String toUnixStringEnding(String in) {
        return in.replace("\r\n", "\n");
    }

    private String serializedAsString(byte[] serialized) throws IOException {
        IASN1InputStream is = FACTORY.createASN1InputStream(serialized);
        IASN1Primitive obj1 = is.readObject();
        return toUnixStringEnding(FACTORY.createASN1Dump().dumpAsString(obj1, true));
    }
}
