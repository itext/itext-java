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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.PdfType4Function;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Tag("UnitTest")
public class PdfA4ImplementationLimitsTest extends ExtendedITextTest {
    private PdfA4Checker pdfA4Checker = new PdfA4Checker(PdfAConformance.PDF_A_4);

    @BeforeEach
    public void before() {
        pdfA4Checker.setFullCheckMode(true);
    }

    @Test
    public void independentLongStringTest() {
        final int maxAllowedLength = new PdfA2Checker(PdfAConformance.PDF_A_2B).getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        PdfString longString = PdfACheckerTestUtils.getLongString(testLength);

        //An exception should not be thrown because pdf/a-4 spec allows any length strings
        pdfA4Checker.checkPdfObject(longString);
        Assertions.assertEquals(testLength, longString.toString().length());
    }

    @Test
    public void longStringInContentStreamTest() {
        final int maxAllowedLength = new PdfA2Checker(PdfAConformance.PDF_A_2B).getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        PdfString longString = PdfACheckerTestUtils.getLongString(testLength);
        String newContentString = PdfACheckerTestUtils.getStreamWithValue(longString);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);
        //An exception should not be thrown because pdf/a-4 spec allows any length strings
        pdfA4Checker.checkContentStream(stream);
        Assertions.assertEquals(testLength, longString.toString().length());
    }

    @Test
    public void independentLargeRealTest() {
        PdfNumber largeNumber = new PdfNumber(new PdfA2Checker(PdfAConformance.PDF_A_2B).getMaxRealValue());
        // An exception shall not be thrown pdf/a-4 has no number limits
        pdfA4Checker.checkPdfObject(largeNumber);
        Assertions.assertEquals(Float.MAX_VALUE, largeNumber.floatValue(), 0.001f);
    }

    @Test
    public void deviceNColorspaceWithMoreThan32Components() {
        //exception shall not be thrown as pdf/a-4 supports any number of deviceN components
        PdfDictionary currentColorSpaces = new PdfDictionary();
        pdfA4Checker.checkColorSpace(buildDeviceNColorspace(40), null, currentColorSpaces, true, false);
    }

    @Test
    public void longPdfNameTest() {
        //exception shall not be thrown as pdf/a-4 supports greater than 127 characters pdf names
        pdfA4Checker.checkPdfObject(PdfACheckerTestUtils.getLongName(200));
    }

    private PdfColorSpace buildDeviceNColorspace(int numberOfComponents) {
        List<String> tmpArray = new ArrayList<String>(numberOfComponents);
        float[] transformArray = new float[numberOfComponents * 2];

        for (int i = 0; i < numberOfComponents; i++) {
            tmpArray.add("MyColor" + i + 1);
            transformArray[i * 2] = 0;
            transformArray[i * 2 + 1]  = 1;
        }
        PdfType4Function function = new PdfType4Function(transformArray, new float[]{0, 1, 0, 1, 0, 1},
                "{0}".getBytes(StandardCharsets.ISO_8859_1));

        PdfArray deviceNAsArray = ((PdfArray)(new  PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function)).getPdfObject());
        PdfDictionary attributes = new PdfDictionary();
        PdfDictionary colourants = new PdfDictionary();
        String colourantName = "colourantTest";
        colourants.put(new PdfName(colourantName), new PdfSpecialCs.DeviceN(((PdfArray)(new  PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function)).getPdfObject())).getPdfObject());
        attributes.put(PdfName.Colorants, colourants);
        deviceNAsArray.add(attributes);
        return new PdfSpecialCs.DeviceN(deviceNAsArray);
    }
}
