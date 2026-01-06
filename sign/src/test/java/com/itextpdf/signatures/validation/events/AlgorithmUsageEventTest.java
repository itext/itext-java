/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.events;

import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class AlgorithmUsageEventTest extends ExtendedITextTest {

    @Test
    public void creationTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("Name", "OID", "Location");
        Assertions.assertEquals("Name", sut.getName());
        Assertions.assertEquals("OID", sut.getOid());
        Assertions.assertEquals("Location", sut.getUsageLocation());
    }

    @Test
    public void isAllowedAccordingToAdESByOidNegativeTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("MD5", OID.MD5, "Location");
        Assertions.assertFalse(sut.isAllowedAccordingToAdES());
    }


    @Test
    public void isAllowedAccordingToAdESByNameNegativeTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("MD5",null, "Location");
        Assertions.assertFalse(sut.isAllowedAccordingToAdES());
    }

    @Test
    public void isAllowedAccordingToAdESByOidPositiveTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("SHA_256", OID.SHA_256, "Location");
        Assertions.assertTrue(sut.isAllowedAccordingToAdES());
    }

    @Test
    public void isAllowedAccordingToAdESByNamePositiveTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("SHA_256", null, "Location");
        Assertions.assertTrue(sut.isAllowedAccordingToAdES());
    }


    @Test
    public void isAllowedAccordingToEtsiTs119_312ByOidNegativeTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("SHA", "1.3.14.3.2.26", "Location");
        Assertions.assertFalse(sut.isAllowedAccordingToEtsiTs119_312());
    }
    @Test
    public void isAllowedAccordingToEtsiTs119_312ByNameNegativeTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("SHA", null, "Location");
        Assertions.assertFalse(sut.isAllowedAccordingToEtsiTs119_312());
    }


    @Test
    public void isAllowedAccordingToEtsiTs119_312ByOidPositiveTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("sha256WithRsaEncryption", "1.2.840.113549.1.1.11", "Location");
        Assertions.assertTrue(sut.isAllowedAccordingToEtsiTs119_312());
    }
    @Test
    public void isAllowedAccordingToEtsiTs119_312ByNamePositiveTest() {
        AlgorithmUsageEvent sut = new AlgorithmUsageEvent("sha256WithRsaEncryption", null, "Location");
        Assertions.assertTrue(sut.isAllowedAccordingToEtsiTs119_312());
    }
}
