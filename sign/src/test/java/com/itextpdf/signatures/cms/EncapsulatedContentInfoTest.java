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
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class EncapsulatedContentInfoTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String ENCODED_WITH_CONTENT_B64 = "MH0GCyqGSIb3DQEJEAEEoG4EbDBqAgEBBglghkgBhv1sBwEwMTANBglg" +
            "hkgBZQMEAgEFAAQgSbIIRqXY9+m1GfDgEnVFrQw//OObVVmEk4sLQ4uirygCEHHvE6CzVVvOraJrAlIXOO8YDzIwMjMxMDMxMDU1ODQ" +
            "5WgIEwrIa7w==";

    @Test
    public void testDeserializationWithoutContent() {
        IASN1EncodableVector v = FACTORY.createASN1EncodableVector();
        v.add(FACTORY.createASN1ObjectIdentifier(OID.PKCS7_DATA));
        IASN1Sequence testData = FACTORY.createDERSequence(v);
        EncapsulatedContentInfo sut = new EncapsulatedContentInfo(testData);
        Assertions.assertEquals(OID.PKCS7_DATA, sut.getContentType());
        Assertions.assertNull(sut.getContent());
    }

    @Test
    public void testDeserializationWithContent() throws IOException {
        IASN1Sequence testData = FACTORY.createASN1Sequence(Base64.decode(ENCODED_WITH_CONTENT_B64));
        EncapsulatedContentInfo sut = new EncapsulatedContentInfo(testData);
        Assertions.assertEquals("1.2.840.113549.1.9.16.1.4", sut.getContentType());
        Assertions.assertNotNull(sut.getContent());
    }

    @Test
    public void testCreation() {
        EncapsulatedContentInfo sut = new EncapsulatedContentInfo(OID.PKCS7_DATA);
        Assertions.assertEquals(OID.PKCS7_DATA, sut.getContentType());
        Assertions.assertNull(sut.getContent());
    }

    @Test
    public void testCreationWithContent() {
        EncapsulatedContentInfo sut = new EncapsulatedContentInfo(OID.PKCS7_DATA,
                FACTORY.createDEROctetString(new byte[20]));
        Assertions.assertEquals(OID.PKCS7_DATA, sut.getContentType());
        Assertions.assertNotNull(sut.getContent());
    }

}
