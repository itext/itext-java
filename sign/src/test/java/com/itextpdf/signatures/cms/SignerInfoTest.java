/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class SignerInfoTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    private static final IASN1Dump DUMP = FACTORY.createASN1Dump();

    private static final byte[] MESSAGE_DIGEST =
            CMSTestHelper.MESSAGE_DIGEST_STRING.getBytes(StandardCharsets.UTF_8);
    private static final byte[] EXPECTEDRESULT_1 = Base64.decode(CMSTestHelper.EXPECTEDRESULT_1);
    private static final byte[] EXPECTEDRESULT_2 = Base64.decode(CMSTestHelper.EXPECTEDRESULT_2);
    private static final byte[] EXPECTEDRESULT_3 = Base64.decode(CMSTestHelper.EXPECTEDRESULT_3);
    private static final byte[] EXPECTEDRESULT_4 = Base64.decode(CMSTestHelper.EXPECTEDRESULT_4);
    private static final byte[] EXPECTEDRESULT_5 = Base64.decode(CMSTestHelper.EXPECTEDRESULT_5);

    private static final List<X509Certificate> chain = new ArrayList<>();

    static {
        Certificate[] certChain = new Certificate[0];
        try {
            certChain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsaWithChain.pem");
        } catch (Exception e) {
            // Ignore.
        }
        for (Certificate cert : certChain) {
            chain.add((X509Certificate) cert);
        }
    }

    private X509Certificate signCert;
    private List<byte[]> testCrlResponse;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        signCert = chain.get(0);
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsaWithChain.pem", PASSWORD);
        TestCrlBuilder testCrlBuilder = new TestCrlBuilder(signCert, caPrivateKey);
        testCrlBuilder.addCrlEntry(signCert, FACTORY.createCRLReason().getKeyCompromise());
        testCrlResponse = Collections.singletonList(testCrlBuilder.makeCrl());
    }

    @Test
    public void testSignedAttributesReadonlyModeActivatedByGettingSerializedData() throws IOException, CertificateEncodingException {
        SignerInfo si = new SignerInfo();
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSA));
        si.setSigningCertificate(signCert);
        ArrayList<byte[]> fakeOcspREsponses = new ArrayList<>();
        fakeOcspREsponses.add(Base64.decode(CMSTestHelper.BASE64_OCSP_RESPONSE));
        si.setMessageDigest(new byte[1024]);
        si.setOcspResponses(fakeOcspREsponses);
        si.setCrlResponses(testCrlResponse);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.serializeSignedAttributes();

        Assertions.assertThrows(IllegalStateException.class, () -> si.setSerializedSignedAttributes(new byte[1235]));
        Assertions.assertThrows(IllegalStateException.class,
                () -> si.setCrlResponses(testCrlResponse));
        Assertions.assertThrows(IllegalStateException.class, () -> si.setOcspResponses(fakeOcspREsponses));
        Assertions.assertThrows(IllegalStateException.class, () -> si.setMessageDigest(new byte[1024]));

        CmsAttribute attribute = new CmsAttribute("", FACTORY.createASN1Integer(1));
        Assertions.assertThrows(IllegalStateException.class,
                () -> si.addSignedAttribute(attribute));
        Assertions.assertThrows(IllegalStateException.class, () ->
                si.addSignerCertificateToSignedAttributes(signCert, OID.SHA_512));
    }

    @Test
    public void testGetSerializedBasicSignedAttributes() throws IOException, CertificateEncodingException {
        SignerInfo si = new SignerInfo();
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificate(signCert);
        si.setMessageDigest(MESSAGE_DIGEST);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        byte[] serRes = si.serializeSignedAttributes();
        Assertions.assertEquals(serializedAsString(EXPECTEDRESULT_1), serializedAsString(serRes));
    }

    @Test
    public void testGetSerializedExtendedSignedAttributes() throws IOException, CertificateEncodingException {
        SignerInfo si = new SignerInfo();
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificate(signCert);
        ArrayList<byte[]> fakeOcspREsponses = new ArrayList<>();
        fakeOcspREsponses.add(Base64.decode(CMSTestHelper.BASE64_OCSP_RESPONSE));
        si.setOcspResponses(fakeOcspREsponses);
        si.setCrlResponses(testCrlResponse);
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        byte[] serRes = si.serializeSignedAttributes();
        Assertions.assertEquals(serializedAsString(EXPECTEDRESULT_2), serializedAsString(serRes));
    }

    @Test
    public void testGetSerializedExtendedSignedAttributesCrlOnly() throws IOException, CertificateEncodingException {
        SignerInfo si = new SignerInfo();
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificate(signCert);
        si.setCrlResponses(testCrlResponse);
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        byte[] serRes = si.serializeSignedAttributes();
        Assertions.assertEquals(serializedAsString(EXPECTEDRESULT_5), serializedAsString(serRes));
    }

    @Test
    public void testAddSignedAttribute() {
        SignerInfo si = new SignerInfo();
        Assertions.assertFalse(si.getSignedAttributes().stream().anyMatch(a ->
                Objects.equals(a.getType(), OID.SIGNING_TIME)));
        CmsAttribute attrib = new CmsAttribute(OID.SIGNING_TIME, FACTORY.createNullASN1Set());
        si.addSignedAttribute(attrib);
        Assertions.assertTrue(si.getSignedAttributes().stream().anyMatch(a ->
                Objects.equals(a.getType(), OID.SIGNING_TIME)));
    }

    @Test
    public void testAddUnsignedAttribute() {
        SignerInfo si = new SignerInfo();
        CmsAttribute attrib = new CmsAttribute(OID.SIGNING_TIME, FACTORY.createNullASN1Set());
        si.addUnSignedAttribute(attrib);
        Assertions.assertEquals(OID.SIGNING_TIME,
                SignTestPortUtil.<CmsAttribute>getFirstElement(si.getUnSignedAttributes()).getType());
    }

    @Test
    public void testGetSerializedSignedAttributesWithCertificateId() throws CertificateEncodingException,
            NoSuchAlgorithmException, NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificate(signCert);
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.addSignerCertificateToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        byte[] serRes = si.serializeSignedAttributes();
        Assertions.assertEquals(serializedAsString(EXPECTEDRESULT_3), serializedAsString(serRes));
    }

    @Test
    public void testGetSerializedSignedAttributesWithCertificateIdTroughCertSetter()
            throws CertificateEncodingException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        byte[] serRes = si.serializeSignedAttributes();
        Assertions.assertEquals(serializedAsString(EXPECTEDRESULT_3), serializedAsString(serRes));
    }

    @Test
    public void testGetAsDerSequence() throws CertificateEncodingException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();

        si.addUnSignedAttribute(new CmsAttribute(OID.SIGNING_TIME, FACTORY.
                createDERSet(FACTORY.createASN1Integer(123456))));

        si.setSigningCertificateAndAddToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.setSignature(new byte[512]);
        IDERSequence res = si.getAsDerSequence();
        Assertions.assertEquals(serializedAsString(EXPECTEDRESULT_4),
                serializedAsString(res.getEncoded()));
    }

    @Test
    public void testEstimatedSizeWithSignature() throws CertificateEncodingException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();

        si.addUnSignedAttribute(new CmsAttribute(OID.SIGNING_TIME, FACTORY.
                createDERSet(FACTORY.createASN1Integer(123456))));

        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.setSignature(new byte[512]);

        long res = si.getEstimatedSize();

        Assertions.assertEquals(1973, res);
    }

    @Test
    public void testSignedAttributesSerializationRoundTrip() throws CertificateEncodingException,
            NoSuchAlgorithmException, IOException, NoSuchProviderException {
        SignerInfo si = new SignerInfo();

        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));
        si.setSignature(new byte[512]);
        byte[] serialized = si.serializeSignedAttributes();

        SignerInfo si2 = new SignerInfo();
        si2.setSerializedSignedAttributes(serialized);

        Assertions.assertEquals(si.getSignedAttributes().size(), si2.getSignedAttributes().size());
        for (CmsAttribute attribute : si.getSignedAttributes()) {
            Assertions.assertTrue(si2.getSignedAttributes().stream().anyMatch(
                            a -> a.getType().equals(attribute.getType()) && a.getValue().equals(attribute.getValue())),
                    MessageFormatUtil.format("Expected to find an attribute with id {0} and value {1}",
                            attribute.getType(), attribute.getValue().toString()));
        }
    }

    @Test
    public void testEstimatedSizeEstimatedSignature() throws CertificateEncodingException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();

        si.addUnSignedAttribute(new CmsAttribute(OID.SIGNING_TIME, FACTORY.
                createDERSet(FACTORY.createASN1Integer(123456))));
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));

        long res = si.getEstimatedSize();

        Assertions.assertEquals(2485, res);
    }

    @Test
    public void testSerializeAndDeserializeSignerInfo() throws CertificateEncodingException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();

        si.addUnSignedAttribute(new CmsAttribute(OID.SIGNING_TIME, FACTORY.
                createDERSet(FACTORY.createASN1Integer(123456))));
        si.setSignatureAlgorithm(new AlgorithmIdentifier(OID.RSASSA_PSS));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, "2.16.840.1.101.3.4.2.3");
        si.setMessageDigest(new byte[1024]);
        si.setDigestAlgorithm(new AlgorithmIdentifier(OID.SHA_512));

        IDERSequence encoded = si.getAsDerSequence(false);

        SignerInfo si2 = new SignerInfo(encoded, Collections.singletonList(signCert));

        Assertions.assertEquals(si.getSignedAttributes().size(), si2.getSignedAttributes().size());
    }


    @Test
    public void testSerializeAndDeserializeSignedAttributes() throws CertificateEncodingException,
            NoSuchAlgorithmException, NoSuchProviderException, IOException {
        SignerInfo si = new SignerInfo();

        si.addSignerCertificateToSignedAttributes(signCert, OID.SHA_256);
        si.setMessageDigest(new byte[20]);

        byte[] attribs = si.serializeSignedAttributes();

        SignerInfo si2 = new SignerInfo();
        si2.setSerializedSignedAttributes(attribs);

        Assertions.assertEquals(si.getSignedAttributes().size(), si2.getSignedAttributes().size());
    }

    @Test
    public void testDeserializationMissingSignedAttributes() throws IOException {
        IASN1Encodable testData = FACTORY.createASN1Primitive(
                Base64.decode(CMSTestHelper.B64_ENCODED_NO_SIGNED_ATTRIBS));
        SignerInfo si = new SignerInfo(testData, chain);
        Assertions.assertEquals(0, si.getSignedAttributes().size());
    }

    @Test
    public void testMissingSignerCertificate() throws IOException {
        IASN1Encodable testData = FACTORY.createASN1Primitive(
                Base64.decode(CMSTestHelper.B64_ENCODED_NO_SIGNED_ATTRIBS));
        Exception e = Assertions.assertThrows(PdfException.class, () ->
                new SignerInfo(testData, chain.subList(1, chain.size() - 1)));
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_CERTIFICATE_NOT_FOUND, e.getMessage());
    }

    @Test
    public void testSidWithSubjectKeyIdentifier() throws IOException {
        IASN1Encodable testData = FACTORY.createASN1Primitive(
                Base64.decode(CMSTestHelper.B64_ENCODED_SUBJECTKEY_IDENTIFIER));
        SignerInfo si = new SignerInfo(testData, chain);
        Assertions.assertEquals(signCert.getSerialNumber(), si.getSigningCertificate().getSerialNumber());
    }

    @Test
    public void testMissingCertificateWithSubjectKeyIdentifier() throws IOException {
        IASN1Encodable testData = FACTORY.createASN1Primitive(
                Base64.decode(CMSTestHelper.B64_ENCODED_SUBJECTKEY_IDENTIFIER));
        Exception e = Assertions.assertThrows(PdfException.class, () ->
                new SignerInfo(testData, chain.subList(1, chain.size() - 1)));
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_CERTIFICATE_NOT_FOUND, e.getMessage());
    }

    @Test
    public void testInvalidStructure() {
        IASN1EncodableVector v = FACTORY.createASN1EncodableVector();
        v.add(FACTORY.createASN1ObjectIdentifier("1.2.840.113549.1.7.2"));
        //should be tagged with 0
        v.add(FACTORY.createDERSequence(FACTORY.createASN1EncodableVector()));
        IASN1Encodable testData = FACTORY.createASN1Sequence(v);
        Exception e = Assertions.assertThrows(PdfException.class, () ->
                new SignerInfo(testData, chain.subList(1, chain.size() - 1)));
        Assertions.assertEquals(SignExceptionMessageConstant.CMS_INVALID_CONTAINER_STRUCTURE, e.getMessage());
    }

    private String toUnixStringEnding(String in) {
        return in.replace("\r\n", "\n");
    }

    private String serializedAsString(byte[] serialized) throws IOException {
        IASN1InputStream is = FACTORY.createASN1InputStream(serialized);
        IASN1Primitive obj1 = is.readObject();
        return toUnixStringEnding(DUMP.dumpAsString(obj1, true));
    }
}
