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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfConformanceTest extends ExtendedITextTest {

    @Test
    public void constructorWithBothAAndUaConformance() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_2A, PdfUAConformance.PDF_UA_1);
        Assertions.assertTrue(conformance.isPdfA());
        Assertions.assertTrue(conformance.isPdfUA());
        Assertions.assertFalse(conformance.isWtpdf());
        Assertions.assertEquals(PdfAConformance.PDF_A_2A, conformance.getAConformance());
        Assertions.assertEquals(PdfUAConformance.PDF_UA_1, conformance.getUAConformance());
        Assertions.assertTrue(conformance.getWtpdfConformances().isEmpty());
    }

    @Test
    public void constructorWithAConformanceAndNullUa() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_3B, null);
        Assertions.assertTrue(conformance.isPdfA());
        Assertions.assertFalse(conformance.isPdfUA());
        Assertions.assertEquals(PdfAConformance.PDF_A_3B, conformance.getAConformance());
        Assertions.assertNull(conformance.getUAConformance());
    }

    @Test
    public void constructorWithNullAAndUaConformance() {
        PdfConformance conformance = new PdfConformance(null, PdfUAConformance.PDF_UA_2);
        Assertions.assertFalse(conformance.isPdfA());
        Assertions.assertTrue(conformance.isPdfUA());
        Assertions.assertNull(conformance.getAConformance());
        Assertions.assertEquals(PdfUAConformance.PDF_UA_2, conformance.getUAConformance());
    }

    @Test
    public void constructorWithBothNull() {
        PdfAConformance nullA = null;
        PdfUAConformance nullUa = null;
        PdfConformance conformance = new PdfConformance(nullA, nullUa);
        Assertions.assertFalse(conformance.isPdfA());
        Assertions.assertFalse(conformance.isPdfUA());
        Assertions.assertFalse(conformance.isWtpdf());
        Assertions.assertNull(conformance.getAConformance());
        Assertions.assertNull(conformance.getUAConformance());
    }

    @Test
    public void constructorWithAAndUaSetsWtpdfToNull() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2);
        Assertions.assertFalse(conformance.isWtpdf());
    }

    @Test
    public void toStringNoConformance() {
        PdfConformance conformance = new PdfConformance();
        Assertions.assertEquals("Conformance:", conformance.toString());
    }

    @Test
    public void toStringWithPdfAConformance() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_2A);
        Assertions.assertEquals("Conformance: A-2A", conformance.toString());
    }

    @Test
    public void toStringWithPdfUAConformance() {
        PdfConformance conformance = new PdfConformance(PdfUAConformance.PDF_UA_1);
        Assertions.assertEquals("Conformance: UA-1", conformance.toString());
    }

    @Test
    public void toStringWithPdfUA2Conformance() {
        PdfConformance conformance = new PdfConformance(PdfUAConformance.PDF_UA_2);
        Assertions.assertEquals("Conformance: UA-2", conformance.toString());
    }

    @Test
    public void toStringWithWtpdfConformance() {
        PdfConformance conformance = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        Assertions.assertEquals("Conformance: WTPDF-FOR_ACCESSIBILITY", conformance.toString());
    }

    @Test
    public void toStringWithWtpdfReuseConformance() {
        PdfConformance conformance = new PdfConformance(WellTaggedPdfConformance.FOR_REUSE);
        Assertions.assertEquals("Conformance: WTPDF-FOR_REUSE", conformance.toString());
    }

    @Test
    public void toStringWithPdfAAndUaConformance() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_1B, PdfUAConformance.PDF_UA_1);
        Assertions.assertEquals("Conformance: A-1B UA-1", conformance.toString());
    }

    @Test
    public void toStringWithAllThreeConformances() {
        PdfConformance conformance = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        Assertions.assertEquals("Conformance: A-4 UA-2 WTPDF-FOR_ACCESSIBILITY", conformance.toString());
    }

    @Test
    public void toStringWithPdfA4NullLevel() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_4);
        Assertions.assertEquals("Conformance: A-4", conformance.toString());
    }

    @Test
    public void getAConformancePart1LevelA() {
        Assertions.assertEquals(PdfAConformance.PDF_A_1A, PdfConformance.getAConformance("1", "A"));
    }

    @Test
    public void getAConformancePart1LevelB() {
        Assertions.assertEquals(PdfAConformance.PDF_A_1B, PdfConformance.getAConformance("1", "B"));
    }

    @Test
    public void getAConformancePart1LevelALowerCase() {
        Assertions.assertEquals(PdfAConformance.PDF_A_1A, PdfConformance.getAConformance("1", "a"));
    }

    @Test
    public void getAConformancePart2LevelA() {
        Assertions.assertEquals(PdfAConformance.PDF_A_2A, PdfConformance.getAConformance("2", "A"));
    }

    @Test
    public void getAConformancePart2LevelB() {
        Assertions.assertEquals(PdfAConformance.PDF_A_2B, PdfConformance.getAConformance("2", "B"));
    }

    @Test
    public void getAConformancePart2LevelU() {
        Assertions.assertEquals(PdfAConformance.PDF_A_2U, PdfConformance.getAConformance("2", "U"));
    }

    @Test
    public void getAConformancePart3LevelA() {
        Assertions.assertEquals(PdfAConformance.PDF_A_3A, PdfConformance.getAConformance("3", "A"));
    }

    @Test
    public void getAConformancePart3LevelB() {
        Assertions.assertEquals(PdfAConformance.PDF_A_3B, PdfConformance.getAConformance("3", "B"));
    }

    @Test
    public void getAConformancePart3LevelU() {
        Assertions.assertEquals(PdfAConformance.PDF_A_3U, PdfConformance.getAConformance("3", "U"));
    }

    @Test
    public void getAConformancePart4NoLevel() {
        Assertions.assertEquals(PdfAConformance.PDF_A_4, PdfConformance.getAConformance("4", null));
    }

    @Test
    public void getAConformancePart4LevelE() {
        Assertions.assertEquals(PdfAConformance.PDF_A_4E, PdfConformance.getAConformance("4", "E"));
    }

    @Test
    public void getAConformancePart4LevelF() {
        Assertions.assertEquals(PdfAConformance.PDF_A_4F, PdfConformance.getAConformance("4", "F"));
    }

    @Test
    public void getAConformancePart4UnknownLevelReturnsPdfA4() {
        Assertions.assertEquals(PdfAConformance.PDF_A_4, PdfConformance.getAConformance("4", "Z"));
    }

    @Test
    public void getAConformanceInvalidPartReturnsNull() {
        Assertions.assertNull(PdfConformance.getAConformance("5", "A"));
    }

    @Test
    public void getAConformancePart1InvalidLevelReturnsNull() {
        Assertions.assertNull(PdfConformance.getAConformance("1", "U"));
    }

    @Test
    public void getAConformancePart2InvalidLevelReturnsNull() {
        Assertions.assertNull(PdfConformance.getAConformance("2", "E"));
    }

    @Test
    public void getAConformancePart3InvalidLevelReturnsNull() {
        Assertions.assertNull(PdfConformance.getAConformance("3", "F"));
    }


    @Test
    public void getAConformanceInstanceReturnsCorrectValue() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_3A);
        Assertions.assertEquals(PdfAConformance.PDF_A_3A, conformance.getAConformance());
    }

    @Test
    public void getAConformanceInstanceReturnsNullWhenNotSet() {
        PdfConformance conformance = new PdfConformance(PdfUAConformance.PDF_UA_1);
        Assertions.assertNull(conformance.getAConformance());
    }

    @Test
    public void getAConformanceInstanceReturnsNullForEmptyConformance() {
        PdfConformance conformance = new PdfConformance();
        Assertions.assertNull(conformance.getAConformance());
    }


    @Test
    public void hashCodeEqualForSameConformance() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_2A, PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_2A, PdfUAConformance.PDF_UA_1);
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeDifferentForDifferentConformance() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_1A);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_2A);
        Assertions.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeEqualForBothEmpty() {
        PdfConformance c1 = new PdfConformance();
        PdfConformance c2 = new PdfConformance();
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeConsistentWithEquals() {
        PdfConformance c1 = new PdfConformance(PdfUAConformance.PDF_UA_2);
        PdfConformance c2 = new PdfConformance(PdfUAConformance.PDF_UA_2);
        Assertions.assertEquals(c1, c2);
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeDifferentForUaVsA() {
        PdfConformance c1 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_1A);
        Assertions.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeWithWtpdfConformance() {
        PdfConformance c1 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        PdfConformance c2 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeDifferentWtpdfValues() {
        PdfConformance c1 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        PdfConformance c2 = new PdfConformance(WellTaggedPdfConformance.FOR_REUSE);
        Assertions.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeWithAllThreeConformances() {
        PdfConformance c1 = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_REUSE);
        PdfConformance c2 = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_REUSE);
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCodeStaticConstantsAreConsistent() {
        Assertions.assertEquals(PdfConformance.PDF_UA_1.hashCode(), new PdfConformance(PdfUAConformance.PDF_UA_1).hashCode());
        Assertions.assertEquals(PdfConformance.PDF_A_1A.hashCode(), new PdfConformance(PdfAConformance.PDF_A_1A).hashCode());
    }

    @Test
    public void equalsSameReference() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_2A, PdfUAConformance.PDF_UA_1);
        Assertions.assertEquals(conformance, conformance);
    }

    @Test
    public void equalsNull() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_2A);
        Assertions.assertNotEquals(null, conformance);
    }

    @Test
    public void equalsDifferentClass() {
        PdfConformance conformance = new PdfConformance(PdfAConformance.PDF_A_2A);
        Assertions.assertNotEquals("not a conformance", conformance);
    }

    @Test
    public void equalsSameAConformance() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_3B);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_3B);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsDifferentAConformance() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_1A);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_2A);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsSameUaConformance() {
        PdfConformance c1 = new PdfConformance(PdfUAConformance.PDF_UA_2);
        PdfConformance c2 = new PdfConformance(PdfUAConformance.PDF_UA_2);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsDifferentUaConformance() {
        PdfConformance c1 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(PdfUAConformance.PDF_UA_2);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsAConformanceVsUaConformance() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_1A);
        PdfConformance c2 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsBothEmpty() {
        PdfConformance c1 = new PdfConformance();
        PdfConformance c2 = new PdfConformance();
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsEmptyVsNonEmpty() {
        PdfConformance c1 = new PdfConformance();
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_1A);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsSameWtpdfConformance() {
        PdfConformance c1 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        PdfConformance c2 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsDifferentWtpdfConformance() {
        PdfConformance c1 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        PdfConformance c2 = new PdfConformance(WellTaggedPdfConformance.FOR_REUSE);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsWtpdfVsNoWtpdf() {
        PdfConformance c1 = new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        PdfConformance c2 = new PdfConformance();
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsSameAAndUaConformance() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsSameADifferentUa() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsDifferentASameUa() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_2A, PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_3A, PdfUAConformance.PDF_UA_1);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsSameAllThreeConformances() {
        PdfConformance c1 = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_REUSE);
        PdfConformance c2 = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_REUSE);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsDifferentWtpdfSameAAndUa() {
        PdfConformance c1 = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        PdfConformance c2 = new PdfConformance(
                PdfAConformance.PDF_A_4, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_REUSE);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    public void equalsIsSymmetric() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_2B);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_2B);
        Assertions.assertEquals(c1, c2);
        Assertions.assertEquals(c2, c1);
    }

    @Test
    public void equalsIsTransitive() {
        PdfConformance c1 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        PdfConformance c3 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        Assertions.assertEquals(c1, c2);
        Assertions.assertEquals(c2, c3);
        Assertions.assertEquals(c1, c3);
    }

    @Test
    public void equalsStaticConstantsMatchNewInstances() {
        Assertions.assertEquals(PdfConformance.PDF_A_1A, new PdfConformance(PdfAConformance.PDF_A_1A));
        Assertions.assertEquals(PdfConformance.PDF_UA_2, new PdfConformance(PdfUAConformance.PDF_UA_2));
    }

    @Test
    public void equalsWithAConformanceVsAWithNullUa() {
        PdfConformance c1 = new PdfConformance(PdfAConformance.PDF_A_2A);
        PdfConformance c2 = new PdfConformance(PdfAConformance.PDF_A_2A, null);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void equalsWithUaConformanceVsNullAWithUa() {
        PdfConformance c1 = new PdfConformance(PdfUAConformance.PDF_UA_1);
        PdfConformance c2 = new PdfConformance(null, PdfUAConformance.PDF_UA_1);
        Assertions.assertEquals(c1, c2);
    }
}
