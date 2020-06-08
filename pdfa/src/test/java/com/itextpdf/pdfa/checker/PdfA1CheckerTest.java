/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern.Shading;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern.Tiling;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfA1CheckerTest extends ExtendedITextTest {

    private PdfA1Checker pdfA1Checker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B);

    @Before
    public void before() {
        pdfA1Checker.setFullCheckMode(true);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();
    @Test
    public void checkCatalogDictionaryWithoutAAEntry() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.AA, new PdfDictionary());

        pdfA1Checker.checkCatalogValidEntries(catalog);
    }

    @Test
    public void checkCatalogDictionaryWithoutOCPropertiesEntry() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_OCPROPERTIES_KEY);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, new PdfDictionary());

        pdfA1Checker.checkCatalogValidEntries(catalog);
    }

    @Test
    public void checkCatalogDictionaryWithoutEmbeddedFiles() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.A_NAME_DICTIONARY_SHALL_NOT_CONTAIN_THE_EMBEDDED_FILES_KEY);

        PdfDictionary names = new PdfDictionary();
        names.put(PdfName.EmbeddedFiles, new PdfDictionary());

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Names, names);

        pdfA1Checker.checkCatalogValidEntries(catalog);
    }

    @Test
    public void checkValidCatalog() {
        pdfA1Checker.checkCatalogValidEntries(new PdfDictionary());

        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }

    @Test
    public void independentLongStringTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);
        PdfString longString = new PdfString(PdfACheckerTestUtils.getLongString(testLength));

        // An exception should be thrown as provided String is longer then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(longString);

    }

    @Test
    public void independentNormalStringTest() {
        final int testLength = pdfA1Checker.getMaxStringLength();

        Assert.assertEquals(testLength, 65535);
        PdfString longString = new PdfString(PdfACheckerTestUtils.getLongString(testLength));

        // An exception should not be thrown as provided String matches
        // the limitations provided in specification
        pdfA1Checker.checkPdfObject(longString);

    }

    @Test
    public void longStringInDictionaryTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key1"), new PdfString("value1"));
        dict.put(new PdfName("Key2"), new PdfString("value2"));
        dict.put(new PdfName("Key3"), new PdfString(PdfACheckerTestUtils.getLongString(testLength)));

        // An exception should be thrown as value for 'key3' is longer then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(dict);

    }

    @Test
    public void normalStringInDictionaryTest() {
        final int testLength = pdfA1Checker.getMaxStringLength();

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key1"), new PdfString("value1"));
        dict.put(new PdfName("Key2"), new PdfString("value2"));
        dict.put(new PdfName("Key3"), new PdfString(PdfACheckerTestUtils.getLongString(testLength)));

        // An exception should not be thrown as all values match the
        // limitations provided in specification
        pdfA1Checker.checkPdfObject(dict);

    }

    @Test
    public void longStringInArrayTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        PdfArray array = new PdfArray();
        array.add(new PdfString("value1"));
        array.add(new PdfString("value2"));
        array.add(new PdfString(PdfACheckerTestUtils.getLongString(testLength)));

        // An exception should be thrown as 3rd element is longer then
        // it is allowed per specification
        pdfA1Checker.checkPdfObject(array);
    }

    @Test
    public void normalStringInArrayTest() {
        final int testLength = pdfA1Checker.getMaxStringLength();

        Assert.assertEquals(testLength, 65535);

        PdfArray array = new PdfArray();
        array.add(new PdfString("value1"));
        array.add(new PdfString("value2"));
        array.add(new PdfString(PdfACheckerTestUtils.getLongString(testLength)));

        // An exception should not be thrown as all elements match the
        // limitations provided in specification
        pdfA1Checker.checkPdfObject(array);

    }

    @Test
    public void longStringInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        pdfA1Checker.checkContentStream(stream);
    }

    @Test
    public void contentStreamIsNotCheckedForNotModifiedObjectTest() {
        pdfA1Checker.setFullCheckMode(false);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should not be thrown as content stream considered as not modified
        // and won't be tested
        pdfA1Checker.checkContentStream(stream);
    }

    @Test
    public void normalStringInContentStreamTest() {
        final int testLength = pdfA1Checker.getMaxStringLength();

        Assert.assertEquals(testLength, 65535);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should be thrown as  all strings inside content stream
        // are not longer then it is allowed per specification
        pdfA1Checker.checkContentStream(stream);
    }

    @Test
    public void longStringInArrayInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongStringInArray(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        pdfA1Checker.checkContentStream(stream);
    }

    @Test
    public void longStringInDictionaryInContentStreamTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongStringInDictionary(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);

        // An exception should be thrown as content stream has a string which
        // is longer then it is allowed per specification
        pdfA1Checker.checkContentStream(stream);
    }

    @Test
    public void longStringInComplexStructureTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        PdfDictionary dict1 = new PdfDictionary();
        dict1.put(new PdfName("Key1"), new PdfString("value1"));
        dict1.put(new PdfName("Key2"), new PdfString("value2"));
        dict1.put(new PdfName("Key3"), new PdfString(PdfACheckerTestUtils.getLongString(testLength)));

        PdfArray array = new PdfArray();
        array.add(new PdfString("value3"));
        array.add(new PdfString("value4"));
        array.add(dict1);

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("Key4"), new PdfString("value5"));
        dict.put(new PdfName("Key5"), new PdfString("value6"));
        dict.put(new PdfName("Key6"), array);

        // An exception should be thrown as there is a string element which
        // doesn't match the limitations provided in specification
        pdfA1Checker.checkPdfObject(array);
    }

    @Test
    public void longStringInPdfFormXObjectTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);
        PdfXObject xobject = new PdfFormXObject(stream);

        // An exception should be thrown as form xobject content stream has a string which
        // is longer then it is allowed per specification
        pdfA1Checker.checkFormXObject(xobject.getPdfObject());
    }

    @Test
    public void longStringInTilingPatternTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfPattern pattern = new Tiling(200, 200);
        ((PdfStream) pattern.getPdfObject()).setData(newContent);

        Color color = new PatternColor(pattern);
        // An exception should be thrown as tiling pattern's content stream has a string which
        // is longer then it is allowed per specification
        pdfA1Checker.checkColor(color, new PdfDictionary(), true, null);
    }

    @Test
    public void longStringInShadingPatternTest() {
        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        PdfStream stream = new PdfStream(newContent);
        PdfPattern pattern = new Shading(stream);

        // An exception should not be thrown as shading pattern doesn't have
        // content stream to validate
        pdfA1Checker.checkPdfObject(pattern.getPdfObject());
    }

    @Test
    public void longStringInType3FontTest() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);

        final int maxAllowedLength = pdfA1Checker.getMaxStringLength();
        final int testLength = maxAllowedLength + 1;

        Assert.assertEquals(testLength, 65536);

        String newContentString = PdfACheckerTestUtils.getStreamWithLongString(testLength);
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);

        PdfFont font = PdfFontFactory.createType3Font(null, true);

        PdfDictionary charProcs = new PdfDictionary();
        charProcs.put(PdfName.A, new PdfStream(newContent));

        PdfDictionary dictionary = font.getPdfObject();
        dictionary.put(PdfName.Subtype, PdfName.Type3);
        dictionary.put(PdfName.CharProcs, charProcs);
        // An exception should be thrown as content stream of type3 font has a string which
        // is longer then it is allowed per specification
        pdfA1Checker.checkFont(font);
    }
}
