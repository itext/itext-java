/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.SecurityIDs;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
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

@Category(BouncyCastleUnitTest.class)
public class CMSContainerTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static final byte[] EXPECTEDRESULT_1 = Base64.decode(CMSTestHelper.EXPECTED_RESULT_CMS_CONTAINER_TEST);

    private X509Certificate[] chain;
    private X509Certificate signCert;

    private byte[] testCrlResponse;

    @Before
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
            NoSuchProviderException {
        CMSContainer sut = new CMSContainer();
        sut.addCertificates((X509Certificate[]) chain);

        SignerInfo si = new SignerInfo();
        si.setSigningCertificate(signCert);
        ArrayList<byte[]> fakeOcspREsponses = new ArrayList<>();
        fakeOcspREsponses.add(new byte[250]);
        si.setMessageDigest(new byte[256]);
        si.setOcspResponses(fakeOcspREsponses);
        si.setCrlResponses(Collections.singletonList(testCrlResponse));
        si.setDigestAlgorithm(new AlgorithmIdentifier(SecurityIDs.ID_SHA512));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, SecurityIDs.ID_SHA512);
        si.setSignature(new byte[256]);
        sut.setSignerInfo(si);

        byte[] serRes = sut.serialize();
        Assert.assertEquals(serializedAsString(EXPECTEDRESULT_1), serializedAsString(serRes));
    }

    @Test
    public void testGetSizeEstimation() throws CertificateEncodingException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException {
        CMSContainer sut = new CMSContainer();
        sut.addCertificates((X509Certificate[]) chain);

        SignerInfo si = new SignerInfo();
        si.setSigningCertificate(signCert);
        ArrayList<byte[]> fakeOcspREsponses = new ArrayList<>();
        fakeOcspREsponses.add(new byte[250]);
        si.setMessageDigest(new byte[256]);
        si.setOcspResponses(fakeOcspREsponses);
        si.setCrlResponses(Collections.singletonList(testCrlResponse));
        si.setDigestAlgorithm(new AlgorithmIdentifier(SecurityIDs.ID_SHA512));
        si.setSigningCertificateAndAddToSignedAttributes(signCert, SecurityIDs.ID_SHA512);
        si.setSignature(new byte[256]);
        sut.setSignerInfo(si);

        long size = sut.getSizeEstimation();

        Assert.assertEquals(4825, size);
    }


    @Test
    public void testDeserialisation() throws CertificateException, IOException {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_CASE1);
        CMSContainer sd = new CMSContainer(rawData);
        Assert.assertEquals("2.16.840.1.101.3.4.2.1", sd.getDigestAlgorithm().getAlgorithmOid());
        Assert.assertEquals("1.2.840.113549.1.7.1", sd.getEncapContentInfo().getContentType());
        Assert.assertEquals(3, sd.getCertificates().size());
        Assert.assertTrue(sd.getCertificates().stream()
                .anyMatch(c -> "140282000747862710817410059465802198354".equals(c.getSerialNumber().toString())));
        Assert.assertTrue(sd.getCertificates().stream()
                .anyMatch(c -> "151118660848720701053205649823964411794".equals(c.getSerialNumber().toString())));
        Assert.assertTrue(sd.getCertificates().stream()
                .anyMatch(c -> "8380897714609953925".equals(c.getSerialNumber().toString())));
        Assert.assertEquals("8380897714609953925",
                sd.getSignerInfo().getSigningCertificate().getSerialNumber().toString());
    }

    @Test
    public void testDeserialisationWithRevocationData() throws CertificateException, IOException {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_CASE2);
        CMSContainer sd = new CMSContainer(rawData);
        Assert.assertEquals("2.16.840.1.101.3.4.2.1", sd.getDigestAlgorithm().getAlgorithmOid());
        Assert.assertEquals("1.2.840.113549.1.7.1", sd.getEncapContentInfo().getContentType());
        Assert.assertEquals(3, sd.getCertificates().size());
        Assert.assertTrue(sd.getCertificates().stream()
                .anyMatch(c -> "3081".equals(c.getSerialNumber().toString())));
        Assert.assertTrue(sd.getCertificates().stream()
                .anyMatch(c -> "2776".equals(c.getSerialNumber().toString())));
        Assert.assertTrue(sd.getCertificates().stream()
                .anyMatch(c -> "1".equals(c.getSerialNumber().toString())));
        Assert.assertEquals("3081",
                sd.getSignerInfo().getSigningCertificate().getSerialNumber().toString());
    }

    @Test
    public void testMultipleDigestAlgorithms() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_2DIGEST_ALGOS);
        Exception e = Assert.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assert.assertEquals(SignExceptionMessageConstant.CMS_ONLY_ONE_SIGNER_ALLOWED, e.getMessage());
    }


    @Test
    public void testMultipleSignerInfos() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_2SIGNERS);
        Exception e = Assert.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assert.assertEquals(SignExceptionMessageConstant.CMS_ONLY_ONE_SIGNER_ALLOWED, e.getMessage());
    }

    @Test
    public void testCertificatesMissing() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_MISSING_CERTIFICATES);
        Exception e = Assert.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assert.assertEquals(SignExceptionMessageConstant.CMS_MISSING_CERTIFICATES, e.getMessage());
    }

    @Test
    public void testCertificatesEmpty() {
        byte[] rawData = Base64.decode(CMSTestHelper.SERIALIZED_B64_EMPTY_CERTIFICATES);
        Exception e = Assert.assertThrows(PdfException.class, () -> {
            CMSContainer sd = new CMSContainer(rawData);
        });
        Assert.assertEquals(SignExceptionMessageConstant.CMS_MISSING_CERTIFICATES, e.getMessage());
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