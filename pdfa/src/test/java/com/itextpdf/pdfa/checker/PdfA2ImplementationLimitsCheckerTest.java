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
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfA2ImplementationLimitsCheckerTest extends ExtendedITextTest {
    private PdfA2Checker pdfA2Checker;

    @BeforeEach
    public void before() {
        pdfA2Checker = new PdfA2Checker(PdfAConformance.PDF_A_2B);
        pdfA2Checker.setFullCheckMode(true);
    }

    @Test
    public void independentLongStringTest() {
        final int maxAllowedLength = pdfA2Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assertions.assertEquals(testLength, 32768);
        PdfString longString = PdfACheckerTestUtils.getLongString(testLength);

        // An exception should be thrown as provided String is longer then
        // it is allowed per specification
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfObject(longString)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.PDF_STRING_IS_TOO_LONG, e.getMessage());
    }

    @Test
    public void longStringInContentStreamTest() {
        pdfA2Checker.setFullCheckMode(true);

        final int maxAllowedLength = pdfA2Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assertions.assertEquals(testLength, 32768);

        PdfString longString = PdfACheckerTestUtils.getLongString(testLength);
        String newContentString = PdfACheckerTestUtils.getStreamWithValue(longString);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkContentStream(stream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.PDF_STRING_IS_TOO_LONG, e.getMessage());
    }

    @Test
    public void arrayCapacityHasNoLimitsTest() {

        PdfArray longArray = PdfACheckerTestUtils.getLongArray(999999);

        // An exception should not be thrown as there is no limits for capacity of an array
        // in PDFA 2
        pdfA2Checker.checkPdfObject(longArray);
    }

    @Test
    public void dictionaryCapacityHasNoLimitsTest() {
        // Using 9999 dictionary pairs which is more than pdfA1 4095 limit (see PDF/A 4.3.2 Limits)
        PdfDictionary longDictionary = PdfACheckerTestUtils.getLongDictionary(9999);

        // An exception should not be thrown as there is no limits for capacity of a dictionary
        // in PDFA 2
        pdfA2Checker.checkPdfObject(longDictionary);

        // Using 9999 dictionary pairs which is more than pdfA1 4095 limit (see PDF/A 4.3.2 Limits)
        PdfStream longStream = PdfACheckerTestUtils.getStreamWithLongDictionary(9999);

        // An exception should not be thrown as there is no limits for capacity of a dictionary
        // and stream in PDFA 2
        pdfA2Checker.checkPdfObject(longStream);

    }

    @Test
    public void independentLargeRealTest() {
        PdfNumber largeNumber = new PdfNumber(pdfA2Checker.getMaxRealValue());

        // TODO DEVSIX-4182
        // An exception is thrown as any number greater then 32767 is considered as Integer
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfObject(largeNumber)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.INTEGER_NUMBER_IS_OUT_OF_RANGE, e.getMessage());
    }

    @Test
    public void deviceNColorspaceWithMoreThan32Components() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> checkColorspace(buildDeviceNColorspace(34))
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_NUMBER_OF_COLOR_COMPONENTS_IN_DEVICE_N_COLORSPACE_SHOULD_NOT_EXCEED, e.getMessage());
    }

    @Test
    public void deviceNColorspaceWithLessThan32Components() {
        checkColorspace(buildDeviceNColorspace(16));
    }

    @Test
    public void deviceNColorspaceWith32Components() {
        checkColorspace(buildDeviceNColorspace(32));
    }

    private void checkColorspace(PdfColorSpace colorSpace) {
        PdfDictionary currentColorSpaces = new PdfDictionary();
        pdfA2Checker.checkColorSpace(colorSpace, null, currentColorSpaces, true, false);
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

        //TODO DEVSIX-4205 Replace with a constructor with 4 parameters or use a setter for attributes dictionary
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
