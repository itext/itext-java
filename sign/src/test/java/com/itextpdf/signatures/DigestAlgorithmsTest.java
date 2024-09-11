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
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.security.Security;

@Tag("BouncyCastleUnitTest")
public class DigestAlgorithmsTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final boolean FIPS_MODE = "BCFIPS".equals(BOUNCY_CASTLE_FACTORY.getProviderName());

    @BeforeAll
    public static void before() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
    }

    @Test
    public void emptyStringOidGetDigestTest() {
        String oid = "";
        Assertions.assertEquals(oid, DigestAlgorithms.getDigest(oid));
    }

    @Test
    public void nonExistingOidGetDigestTest() {
        String oid = "non_existing_oid";
        Assertions.assertEquals(oid, DigestAlgorithms.getDigest(oid));
    }

    @Test
    public void emptyStringNameGetAllowedDigestTest() {
        Assertions.assertNull(DigestAlgorithms.getAllowedDigest(""));
    }

    @Test
    public void nonExistingNameGetAllowedDigestTest() {
        Assertions.assertNull(DigestAlgorithms.getAllowedDigest("non_existing_oid"));
    }

    @Test
    public void nullNameGetAllowedDigestTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DigestAlgorithms.getAllowedDigest(null));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = SignLogMessageConstant.ALGORITHM_NOT_FROM_SPEC, ignore = true)})
    @Test
    public void notAllowedOidGetDigestTest() {
        String name = "SM3";
        String oid = "1.2.156.10197.1.401";
        Assertions.assertEquals(FIPS_MODE ? oid : name, DigestAlgorithms.getDigest(oid));
    }
}
