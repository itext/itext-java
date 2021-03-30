/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import com.itextpdf.io.codec.Base64;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SignaturePolicyId;
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SignaturePolicyInfoTest extends ExtendedITextTest {
    private final static String POLICY_IDENTIFIER = "2.16.724.1.3.1.1.2.1.9";
    private final static String POLICY_HASH_BASE64 = "G7roucf600+f03r/o0bAOQ6WAs0=";
    private final static byte[] POLICY_HASH = Base64.decode(POLICY_HASH_BASE64);
    private final static String POLICY_DIGEST_ALGORITHM = "SHA-256";
    private final static String POLICY_URI = "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void checkConstructorTest() {
        SignaturePolicyInfo info = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);

        Assert.assertEquals(POLICY_IDENTIFIER, info.getPolicyIdentifier());
        Assert.assertArrayEquals(POLICY_HASH, info.getPolicyHash());
        Assert.assertEquals(POLICY_DIGEST_ALGORITHM, info.getPolicyDigestAlgorithm());
        Assert.assertEquals(POLICY_URI, info.getPolicyUri());
    }

    @Test
    public void checkConstructorWithEncodedHashTest() {
        SignaturePolicyInfo info = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH_BASE64,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);

        Assert.assertEquals(POLICY_IDENTIFIER, info.getPolicyIdentifier());
        Assert.assertArrayEquals(POLICY_HASH, info.getPolicyHash());
        Assert.assertEquals(POLICY_DIGEST_ALGORITHM, info.getPolicyDigestAlgorithm());
        Assert.assertEquals(POLICY_URI, info.getPolicyUri());
    }

    @Test
    public void nullIdentifierIsNotAllowedTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Policy identifier cannot be null");

        new SignaturePolicyInfo(null, POLICY_HASH,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);

    }

    @Test
    public void emptyIdentifierIsNotAllowedTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Policy identifier cannot be null");

        new SignaturePolicyInfo("", POLICY_HASH,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);

    }

    @Test
    public void nullPolicyHashIsNotAllowedTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Policy hash cannot be null");

        new SignaturePolicyInfo(POLICY_IDENTIFIER, (byte[]) null,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);
    }

    @Test
    public void nullEncodedPolicyHashIsNotAllowedTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Policy hash cannot be null");

        new SignaturePolicyInfo(POLICY_IDENTIFIER, (String) null,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);
    }

    @Test
    public void nullDigestAlgorithmIsNotAllowedTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Policy digest algorithm cannot be null");

        new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                null, POLICY_URI);
    }

    @Test
    public void emptyDigestAlgorithmIsNotAllowedTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Policy digest algorithm cannot be null");

        new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                "", POLICY_URI);
    }

    @Test
    public void toSignaturePolicyIdentifierTest() {
        SignaturePolicyIdentifier actual = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                POLICY_DIGEST_ALGORITHM, POLICY_URI).toSignaturePolicyIdentifier();

        DERIA5String deria5String = new DERIA5String(POLICY_URI);
        SigPolicyQualifierInfo sigPolicyQualifierInfo = new SigPolicyQualifierInfo(
                PKCSObjectIdentifiers.id_spq_ets_uri, deria5String);

        DEROctetString derOctetString = new DEROctetString(POLICY_HASH);
        String algId = DigestAlgorithms.getAllowedDigest(POLICY_DIGEST_ALGORITHM);
        ASN1ObjectIdentifier asn1ObjectIdentifier = new ASN1ObjectIdentifier(algId);
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(
                asn1ObjectIdentifier);
        OtherHashAlgAndValue otherHashAlgAndValue = new OtherHashAlgAndValue(algorithmIdentifier, derOctetString);
        ASN1ObjectIdentifier objectIdentifier = new ASN1ObjectIdentifier(POLICY_IDENTIFIER);
        ASN1ObjectIdentifier objectIdentifierInstance = ASN1ObjectIdentifier.getInstance(objectIdentifier);
        SignaturePolicyId signaturePolicyId = new SignaturePolicyId(objectIdentifierInstance, otherHashAlgAndValue,
                SignUtils.createSigPolicyQualifiers(sigPolicyQualifierInfo));

        SignaturePolicyIdentifier expected = new SignaturePolicyIdentifier(signaturePolicyId);

        Assert.assertEquals(expected.toASN1Primitive(), actual.toASN1Primitive());
    }

    @Test
    public void toSignaturePolicyIdentifierUnexpectedAlgorithmTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("Invalid policy hash algorithm");

        new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                "SHA-12345", POLICY_URI).toSignaturePolicyIdentifier();
    }
}
