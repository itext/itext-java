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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfA2ImplementationLimitsCheckerTest extends ExtendedITextTest {
    private PdfA2Checker pdfA2Checker = new PdfA2Checker(PdfAConformanceLevel.PDF_A_2B);

    @Before
    public void before() {
        pdfA2Checker.setFullCheckMode(true);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void independentLongStringTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA2Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 32768);
        PdfString longString = PdfACheckerTestUtils.getLongString(testLength);

        // An exception should be thrown as provided String is longer then
        // it is allowed per specification
        pdfA2Checker.checkPdfObject(longString);
    }

    @Test
    public void longStringInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        pdfA2Checker.setFullCheckMode(true);

        final int maxAllowedLength = pdfA2Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 32768);

        PdfString longString = PdfACheckerTestUtils.getLongString(testLength);
        String newContentString = PdfACheckerTestUtils.getStreamWithValue(longString);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        pdfA2Checker.checkContentStream(stream);
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

        PdfDictionary longDictionary = PdfACheckerTestUtils.getLongDictionary(999999);

        // An exception should not be thrown as there is no limits for capacity of a dictionary
        // in PDFA 2
        pdfA2Checker.checkPdfObject(longDictionary);

        PdfStream longStream = PdfACheckerTestUtils.getStreamWithLongDictionary(999999);

        // An exception should not be thrown as there is no limits for capacity of a dictionary
        // and stream in PDFA 2
        pdfA2Checker.checkPdfObject(longStream);

    }

    @Test
    public void independentLargeRealTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.INTEGER_NUMBER_IS_OUT_OF_RANGE);

        PdfNumber largeNumber = new PdfNumber(pdfA2Checker.getMaxRealValue());

        // TODO DEVSIX-4182
        // An exception is thrown as any number greater then 32767 is considered as Integer
        pdfA2Checker.checkPdfObject(largeNumber);
    }

    @Test
    public void deviceNColorspaceWithMoreThan32Components() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.THE_NUMBER_OF_COLOR_COMPONENTS_IN_DEVICE_N_COLORSPACE_SHOULD_NOT_EXCEED);

        checkColorspace(buildDeviceNColorspace(34));

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
        pdfA2Checker.checkColorSpace(colorSpace, currentColorSpaces, true, false);
    }

    private PdfColorSpace buildDeviceNColorspace(int numberOfComponents) {
        List<String> tmpArray = new ArrayList<String>(numberOfComponents);
        float[] transformArray = new float[numberOfComponents * 2];

        for (int i = 0; i < numberOfComponents; i++) {
            tmpArray.add("MyColor" + i + 1);
            transformArray[i * 2] = 0;
            transformArray[i * 2 + 1]  = 1;
        }
        com.itextpdf.kernel.pdf.function.PdfFunction.Type4 function = new com.itextpdf.kernel.pdf.function.PdfFunction.Type4
                (new PdfArray(transformArray), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0}".getBytes(StandardCharsets.ISO_8859_1));

        //TODO DEVSIX-4205 Replace with a constructor with 4 parameters or use a setter for attributes dictionary
        PdfArray deviceNAsArray = ((PdfArray)(new  PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function)).getPdfObject());
        PdfDictionary attributes = new PdfDictionary();
        deviceNAsArray.add(attributes);
        return new PdfSpecialCs.DeviceN(deviceNAsArray);
    }
}
