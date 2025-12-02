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