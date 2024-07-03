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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class SignaturePolicyInfoTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final static String POLICY_IDENTIFIER = "2.16.724.1.3.1.1.2.1.9";
    private final static String POLICY_HASH_BASE64 = "G7roucf600+f03r/o0bAOQ6WAs0=";
    private final static byte[] POLICY_HASH = Base64.decode(POLICY_HASH_BASE64);
    private final static String POLICY_DIGEST_ALGORITHM = "SHA-256";
    private final static String POLICY_URI = "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf";

    @Test
    public void checkConstructorTest() {
        SignaturePolicyInfo info = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);

        Assertions.assertEquals(POLICY_IDENTIFIER, info.getPolicyIdentifier());
        Assertions.assertArrayEquals(POLICY_HASH, info.getPolicyHash());
        Assertions.assertEquals(POLICY_DIGEST_ALGORITHM, info.getPolicyDigestAlgorithm());
        Assertions.assertEquals(POLICY_URI, info.getPolicyUri());
    }

    @Test
    public void checkConstructorWithEncodedHashTest() {
        SignaturePolicyInfo info = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH_BASE64,
                POLICY_DIGEST_ALGORITHM, POLICY_URI);

        Assertions.assertEquals(POLICY_IDENTIFIER, info.getPolicyIdentifier());
        Assertions.assertArrayEquals(POLICY_HASH, info.getPolicyHash());
        Assertions.assertEquals(POLICY_DIGEST_ALGORITHM, info.getPolicyDigestAlgorithm());
        Assertions.assertEquals(POLICY_URI, info.getPolicyUri());
    }

    @Test
    public void nullIdentifierIsNotAllowedTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SignaturePolicyInfo(null, POLICY_HASH, POLICY_DIGEST_ALGORITHM, POLICY_URI)
        );
        Assertions.assertEquals("Policy identifier cannot be null", e.getMessage());
    }

    @Test
    public void emptyIdentifierIsNotAllowedTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SignaturePolicyInfo("", POLICY_HASH, POLICY_DIGEST_ALGORITHM, POLICY_URI)
        );
        Assertions.assertEquals("Policy identifier cannot be null", e.getMessage());
    }

    @Test
    public void nullPolicyHashIsNotAllowedTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SignaturePolicyInfo(POLICY_IDENTIFIER, (byte[]) null, POLICY_DIGEST_ALGORITHM, POLICY_URI)
        );
        Assertions.assertEquals("Policy hash cannot be null", e.getMessage());
    }

    @Test
    public void nullEncodedPolicyHashIsNotAllowedTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SignaturePolicyInfo(POLICY_IDENTIFIER, (String) null, POLICY_DIGEST_ALGORITHM, POLICY_URI)
        );
        Assertions.assertEquals("Policy hash cannot be null", e.getMessage());
    }

    @Test
    public void nullDigestAlgorithmIsNotAllowedTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH, null, POLICY_URI)
        );
        Assertions.assertEquals("Policy digest algorithm cannot be null", e.getMessage());
    }

    @Test
    public void emptyDigestAlgorithmIsNotAllowedTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH, "", POLICY_URI)
        );
        Assertions.assertEquals("Policy digest algorithm cannot be null", e.getMessage());
    }

    @Test
    public void toSignaturePolicyIdentifierTest() {
        ISignaturePolicyIdentifier actual = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH,
                POLICY_DIGEST_ALGORITHM, POLICY_URI).toSignaturePolicyIdentifier();

        IDERIA5String deria5String = BOUNCY_CASTLE_FACTORY.createDERIA5String(POLICY_URI);
        ISigPolicyQualifierInfo sigPolicyQualifierInfo = BOUNCY_CASTLE_FACTORY.createSigPolicyQualifierInfo(
                BOUNCY_CASTLE_FACTORY.createPKCSObjectIdentifiers().getIdSpqEtsUri(), deria5String);

        IDEROctetString derOctetString = BOUNCY_CASTLE_FACTORY.createDEROctetString(POLICY_HASH);
        String algId = DigestAlgorithms.getAllowedDigest(POLICY_DIGEST_ALGORITHM);
        IASN1ObjectIdentifier asn1ObjectIdentifier = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(algId);
        IAlgorithmIdentifier algorithmIdentifier = BOUNCY_CASTLE_FACTORY.createAlgorithmIdentifier(
                asn1ObjectIdentifier);
        IOtherHashAlgAndValue otherHashAlgAndValue = BOUNCY_CASTLE_FACTORY.createOtherHashAlgAndValue(
                algorithmIdentifier, derOctetString);
        IASN1ObjectIdentifier objectIdentifier = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(POLICY_IDENTIFIER);
        IASN1ObjectIdentifier objectIdentifierInstance = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                objectIdentifier);
        ISignaturePolicyId signaturePolicyId = BOUNCY_CASTLE_FACTORY.createSignaturePolicyId(objectIdentifierInstance,
                otherHashAlgAndValue, sigPolicyQualifierInfo);

        ISignaturePolicyIdentifier expected = BOUNCY_CASTLE_FACTORY.createSignaturePolicyIdentifier(signaturePolicyId);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toSignaturePolicyIdentifierUnexpectedAlgorithmTest() {
        SignaturePolicyInfo info = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH, "SHA-12345", POLICY_URI);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> info.toSignaturePolicyIdentifier()
        );
        Assertions.assertEquals("Invalid policy hash algorithm", e.getMessage());
    }
}
