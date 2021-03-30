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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern.Shading;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern.Tiling;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
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
public class PdfA1ImplementationLimitsCheckerTest extends ExtendedITextTest {
    private PdfA1Checker pdfA1Checker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B);
    private final static int MAX_ARRAY_CAPACITY = 8191;
    private final static int MAX_DICTIONARY_CAPACITY = 4095;

    @Before
    public void before() {
        pdfA1Checker.setFullCheckMode(true);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void validObjectsTest() {
        final int maxNameLength = pdfA1Checker.getMaxNameLength();
        final int maxStringLength = pdfA1Checker.getMaxStringLength();
        final int maxArrayCapacity = MAX_ARRAY_CAPACITY;
        final int maxDictionaryCapacity = MAX_DICTIONARY_CAPACITY;
        final long maxIntegerValue = pdfA1Checker.getMaxIntegerValue();
        final long minIntegerValue = pdfA1Checker.getMinIntegerValue();
        final double maxRealValue = pdfA1Checker.getMaxRealValue();

        Assert.assertEquals(65535, maxStringLength);
        Assert.assertEquals(127, maxNameLength);
        PdfString longString = PdfACheckerTestUtils.getLongString(maxStringLength);
        PdfName longName = PdfACheckerTestUtils.getLongName(maxNameLength);

        PdfArray longArray = PdfACheckerTestUtils.getLongArray(maxArrayCapacity);
        PdfDictionary longDictionary = PdfACheckerTestUtils.getLongDictionary(maxDictionaryCapacity);

        Assert.assertEquals(2147483647, maxIntegerValue);
        Assert.assertEquals(-2147483648, minIntegerValue);
        Assert.assertEquals(32767, maxRealValue, 0.001);

        PdfNumber largeInteger = new PdfNumber(maxIntegerValue);
        PdfNumber negativeInteger = new PdfNumber(minIntegerValue);
        PdfNumber largeReal = new PdfNumber(maxRealValue - 0.001);

        PdfObject[] largeObjects = {longName, longString, longArray, longDictionary,
                largeInteger, negativeInteger, largeReal};
        // No exceptions should not be thrown as all values match the
        // limitations provided in specification
        for (PdfObject largeObject: largeObjects) {
            pdfA1Checker.checkPdfObject(largeObject);
            checkInArray(largeObject);
            checkInDictionary(largeObject);
            checkInComplexStructure(largeObject);
            checkInContentStream(largeObject);
            checkInArrayInContentStream(largeObject);
            checkInDictionaryInContentStream(largeObject);
            checkInFormXObject(largeObject);
            checkInTilingPattern(largeObject);
            checkInType3Font(largeObject);
        }
    }

    @Test
    public void validStreamTest() {
        PdfStream longStream = PdfACheckerTestUtils.getStreamWithLongDictionary(MAX_DICTIONARY_CAPACITY);

        // No exceptions should not be thrown as the stream match the
        // limitations provided in specification
        pdfA1Checker.checkPdfObject(longStream);
    }

    @Test
    public void independentLongStringTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as provided String is longer then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(longString);
    }

    @Test
    public void independentLongNameTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_NAME_IS_TOO_LONG);

        PdfName longName = buildLongName();

        // An exception should be thrown as provided name is longer then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(longName);
    }

    @Test
    public void independentLargeIntegerTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.INTEGER_NUMBER_IS_OUT_OF_RANGE);

        PdfNumber largeNumber = new PdfNumber(pdfA1Checker.getMaxIntegerValue() + 1L);

        // An exception should be thrown as provided integer is larger then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(largeNumber);
    }

    @Test
    public void independentLargeNegativeIntegerTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.INTEGER_NUMBER_IS_OUT_OF_RANGE);

        PdfNumber largeNumber = new PdfNumber(pdfA1Checker.getMinIntegerValue() - 1L);

        // An exception should be thrown as provided integer is smaller then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(largeNumber);
    }

    @Test
    public void independentLargeRealTest() {

        PdfNumber largeNumber = new PdfNumber(pdfA1Checker.getMaxRealValue() + 1.0);

        // TODO DEVSIX-4182
        // An exception is not thrown as any number greater then 32767 is considered as Integer
        pdfA1Checker.checkPdfObject(largeNumber);
    }

    @Test
    public void independentLongArrayTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_ARRAY_CAPACITY_IS_EXCEEDED);

        PdfArray longArray = buildLongArray();

        // An exception should be thrown as provided array has more elements then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(longArray);
    }

    @Test
    public void independentLongDictionaryTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_DICTIONARY_CAPACITY_IS_EXCEEDED);

        PdfDictionary longDictionary = buildLongDictionary();

        // An exception should be thrown as provided dictionary has more entries
        // then it is allowed per specification
        pdfA1Checker.checkPdfObject(longDictionary);
    }

    @Test
    public void independentStreamWithLongDictionaryTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_DICTIONARY_CAPACITY_IS_EXCEEDED);

        PdfStream longStream = buildStreamWithLongDictionary();

        // An exception should be thrown as dictionary of the stream has more entries
        // then it is allowed per specification
        pdfA1Checker.checkPdfObject(longStream);
    }

    @Test
    public void longStringInDictionaryTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as dictionary contains value which is longer then
        // it is allowed per specification
        checkInDictionary(longString);
    }

    @Test
    public void longNameAsKeyInDictionaryTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_NAME_IS_TOO_LONG);

        PdfName longName = buildLongName();

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key1"), new PdfString("value1"));
        dict.put(new PdfName("Key2"), new PdfString("value2"));
        dict.put(longName, new PdfString("value3"));

        // An exception should be thrown as dictionary contains key which is longer then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(dict);
    }

    @Test
    public void longStringInArrayTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();
        // An exception should be thrown as one element is longer then
        // it is allowed per specification
        checkInArray(longString);
    }

    @Test
    public void longStringInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        checkInContentStream(longString);
    }

    @Test
    public void longNameInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_NAME_IS_TOO_LONG);

        PdfName longName = buildLongName();

        // An exception should be thrown as content stream has a name which
        // is longer then it is allowed per specification
        checkInContentStream(longName);
    }

    @Test
    public void largeIntegerInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.INTEGER_NUMBER_IS_OUT_OF_RANGE);

        PdfNumber largeNumber = new PdfNumber(pdfA1Checker.getMaxIntegerValue() + 1L);

        // An exception should be thrown as provided integer is larger then
        // it is allowed per specification
        checkInContentStream(largeNumber);
    }

    @Test
    public void largeNegativeIntegerInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.INTEGER_NUMBER_IS_OUT_OF_RANGE);

        PdfNumber largeNumber = new PdfNumber(pdfA1Checker.getMinIntegerValue() - 1L);

        // An exception should be thrown as provided integer is smaller then
        // it is allowed per specification
        checkInContentStream(largeNumber);
    }

    @Test
    public void largeRealInContentStreamTest() {

        PdfNumber largeNumber = new PdfNumber(pdfA1Checker.getMaxRealValue() + 1.0);

        // TODO DEVSIX-4182
        // An exception is not thrown as any number greater then 32767 is considered as Integer
        checkInContentStream(largeNumber);
    }

    @Test
    public void LongArrayInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_ARRAY_CAPACITY_IS_EXCEEDED);

        PdfArray longArray = buildLongArray();

        // An exception should be thrown as provided array has more elements then
        // it is allowed per specification
        checkInContentStream(longArray);
    }

    @Test
    public void longDictionaryInContentStream() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_DICTIONARY_CAPACITY_IS_EXCEEDED);

        PdfDictionary longDictionary = buildLongDictionary();

        // An exception should be thrown as provided dictionary has more entries
        // then it is allowed per specification
        checkInContentStream(longDictionary);
    }


    @Test
    public void contentStreamIsNotCheckedForNotModifiedObjectTest() {
        pdfA1Checker.setFullCheckMode(false);

        PdfString longString = buildLongString();
        PdfArray longArray = buildLongArray();
        PdfDictionary longDictionary = buildLongDictionary();

        // An exception should not be thrown as content stream considered as not modified
        // and won't be tested
        checkInContentStream(longString);
        checkInContentStream(longArray);
        checkInContentStream(longDictionary);
    }

    @Test
    public void indirectObjectIsNotCheckTest() {
        pdfA1Checker.setFullCheckMode(false);

        PdfStream longStream = buildStreamWithLongDictionary();

        // An exception should not be thrown as pdf stream is an indirect object
        // it is ignored during array / dictionary validation as it is expected
        // to be validated and flushed independently
        checkInArray(longStream);
    }

    @Test
    public void longStringInArrayInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        checkInArrayInContentStream(longString);
    }

    @Test
    public void longStringInDictionaryInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        checkInDictionaryInContentStream(longString);
    }

    @Test
    public void longNameAsKeyInDictionaryInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_NAME_IS_TOO_LONG);

        PdfName longName = buildLongName();

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key1"), new PdfString("value1"));
        dict.put(new PdfName("Key2"), new PdfString("value2"));
        dict.put(longName, new PdfString("value3"));

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        checkInContentStream(dict);
    }

    @Test
    public void longStringInComplexStructureTest() {

        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as there is a string element which
        // doesn't match the limitations provided in specification
        checkInComplexStructure(longString);
    }

    @Test
    public void LongArrayInComplexStructureTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_ARRAY_CAPACITY_IS_EXCEEDED);

        PdfArray longArray = buildLongArray();

        // An exception should be thrown as provided array has more elements then
        // it is allowed per specification
        checkInComplexStructure(longArray);
    }

    @Test
    public void longDictionaryInComplexStructureTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_DICTIONARY_CAPACITY_IS_EXCEEDED);

        PdfDictionary longDictionary = buildLongDictionary();

        // An exception should be thrown as provided dictionary has more entries
        // then it is allowed per specification
        checkInComplexStructure(longDictionary);
    }

    @Test
    public void longStringInPdfFormXObjectTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();

        // An exception should be thrown as form xobject content stream has a string which
        // is longer then it is allowed per specification
        checkInFormXObject(longString);
    }

    @Test
    public void longStringInTilingPatternTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();
        // An exception should be thrown as tiling pattern's content stream has a string which
        // is longer then it is allowed per specification
        checkInTilingPattern(longString);
    }

    @Test
    public void longStringInShadingPatternTest() {
        PdfString longString = buildLongString();

        // An exception should not be thrown as shading pattern doesn't have
        // content stream to validate
        checkInShadingPattern(longString);
    }

    @Test
    public void longStringInType3FontTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        PdfString longString = buildLongString();
        // An exception should be thrown as content stream of type3 font has a string which
        // is longer then it is allowed per specification
        checkInType3Font(longString);
    }

    @Test
    public void deviceNColorspaceWithMoreThan8Components() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.THE_NUMBER_OF_COLOR_COMPONENTS_IN_DEVICE_N_COLORSPACE_SHOULD_NOT_EXCEED);

        checkColorspace(buildDeviceNColorspace(10));
    }

    @Test
    public void deviceNColorspaceWith8Components() {
        checkColorspace(buildDeviceNColorspace(8));
    }

    @Test
    public void deviceNColorspaceWithLessThan8Components() {
        checkColorspace(buildDeviceNColorspace(2));
    }

    private PdfString buildLongString() {

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(65536, testLength);

        return PdfACheckerTestUtils.getLongString(testLength);
    }

    private PdfName buildLongName() {

        final int maxAllowedLength = pdfA1Checker.getMaxNameLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(128, testLength);

        return PdfACheckerTestUtils.getLongName(testLength);
    }

    private PdfArray buildLongArray() {

        final int testLength = MAX_ARRAY_CAPACITY + 1;

        return PdfACheckerTestUtils.getLongArray(testLength);
    }

    private PdfDictionary buildLongDictionary() {

        final int testLength = MAX_DICTIONARY_CAPACITY + 1;

        return PdfACheckerTestUtils.getLongDictionary(testLength);
    }

    private PdfStream buildStreamWithLongDictionary() {

        final int testLength = MAX_DICTIONARY_CAPACITY + 1;;

        return PdfACheckerTestUtils.getStreamWithLongDictionary(testLength);
    }

    private void checkInDictionary(PdfObject object) {

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key1"), new PdfString("value1"));
        dict.put(new PdfName("Key2"), new PdfString("value2"));
        dict.put(new PdfName("Key3"), object);

        pdfA1Checker.checkPdfObject(dict);

    }

    private void checkInArray(PdfObject object) {
        PdfArray array = new PdfArray();
        array.add(new PdfString("value1"));
        array.add(new PdfString("value2"));
        array.add(object);

        pdfA1Checker.checkPdfObject(array);
    }

    private void checkInContentStream(PdfObject object) {
        String byteContent =  PdfACheckerTestUtils.getStreamWithValue(object);

        byte[] newContent = byteContent.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        pdfA1Checker.checkContentStream(stream);
    }

    private void checkInArrayInContentStream(PdfObject object) {
        checkInContentStream(new PdfArray(object));
    }

    private void checkInDictionaryInContentStream(PdfObject object) {
        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("value"), object);
        checkInContentStream(dict);
    }

    private void checkInComplexStructure(PdfObject object) {

        PdfDictionary dict1 = new PdfDictionary();
        dict1.put(new PdfName("Key1"), new PdfString("value1"));
        dict1.put(new PdfName("Key2"), new PdfString("value2"));
        dict1.put(new PdfName("Key3"), object);

        PdfArray array = new PdfArray();
        array.add(new PdfString("value3"));
        array.add(new PdfString("value4"));
        array.add(dict1);

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key4"), new PdfString("value5"));
        dict.put(new PdfName("Key5"), new PdfString("value6"));
        dict.put(new PdfName("Key6"), array);

        pdfA1Checker.checkPdfObject(array);
    }

    private void checkInFormXObject(PdfObject object) {

        String newContentString = PdfACheckerTestUtils.getStreamWithValue(object);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);
        PdfXObject xobject = new PdfFormXObject(stream);

        pdfA1Checker.checkFormXObject(xobject.getPdfObject());
    }

    private void checkInTilingPattern(PdfObject object) {

        String newContentString = PdfACheckerTestUtils.getStreamWithValue(object);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfPattern pattern = new Tiling(200, 200);
        ((PdfStream) pattern.getPdfObject()).setData(newContent);

        Color color = new PatternColor(pattern);

        pdfA1Checker.checkColor(color, new PdfDictionary(), true, null);
    }

    private void checkInShadingPattern(PdfObject object) {

        String newContentString = PdfACheckerTestUtils.getStreamWithValue(object);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);
        PdfPattern pattern = new Shading(stream);

        pdfA1Checker.checkPdfObject(pattern.getPdfObject());
    }

    private void checkInType3Font(PdfObject object) {

        String newContentString = PdfACheckerTestUtils.getStreamWithValue(object);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);

        PdfFont font = PdfFontFactory.createType3Font(null, true);

        PdfDictionary charProcs = new PdfDictionary();
        charProcs.put(PdfName.A, new PdfStream(newContent));

        PdfDictionary dictionary = font.getPdfObject();
        dictionary.put(PdfName.Subtype, PdfName.Type3);
        dictionary.put(PdfName.CharProcs, charProcs);
        pdfA1Checker.checkFont(font);
    }

    private void checkColorspace(PdfColorSpace colorSpace) {
        PdfDictionary currentColorSpaces = new PdfDictionary();
        pdfA1Checker.checkColorSpace(colorSpace, currentColorSpaces, false, false);
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

        return new PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function);
    }
}
