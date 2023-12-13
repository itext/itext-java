/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.parser.util.InlineImageParsingUtils.InlineImageParseException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class InlineImageParsingUtilsTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void iccBasedCsTest() {
        PdfName colorSpace = PdfName.ICCBased;

        PdfDictionary dictionary = new PdfDictionary();
        PdfArray array = new PdfArray();
        array.add(colorSpace);
        PdfStream stream = new PdfStream();
        stream.put(PdfName.N, new PdfNumber(4));
        array.add(stream);
        dictionary.put(colorSpace, array);

        Assert.assertEquals(4, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
    }

    @Test
    public void indexedCsTest() {
        PdfName colorSpace = PdfName.Indexed;

        PdfDictionary dictionary = new PdfDictionary();
        PdfArray array = new PdfArray();
        array.add(colorSpace);
        dictionary.put(colorSpace, array);

        Assert.assertEquals(1, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
    }

    @Test
    public void csInDictAsNameTest() {
        PdfName colorSpace = PdfName.ICCBased;

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(colorSpace, PdfName.DeviceCMYK);

        Assert.assertEquals(4, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
    }

    @Test
    public void csInDictAsNameNullTest() {
        PdfName colorSpace = PdfName.ICCBased;

        PdfDictionary dictionary = new PdfDictionary();

        junitExpectedException.expect(InlineImageParseException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(PdfException.UnexpectedColorSpace1, "/ICCBased"));
        InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary);
    }

    @Test
    public void notSupportedCsWithCsDictionaryTest() {
        PdfName colorSpace = PdfName.ICCBased;

        PdfDictionary dictionary = new PdfDictionary();
        PdfArray array = new PdfArray();
        array.add(PdfName.Pattern);
        PdfStream stream = new PdfStream();
        stream.put(PdfName.N, new PdfNumber(4));
        array.add(stream);
        dictionary.put(colorSpace, array);

        junitExpectedException.expect(InlineImageParseException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(PdfException.UnexpectedColorSpace1, "/ICCBased"));
        InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary);
    }

    @Test
    public void nullCsTest() {
        Assert.assertEquals(1, InlineImageParsingUtils.getComponentsPerPixel(null, null));
    }

    @Test
    public void deviceGrayCsTest() {
        PdfName colorSpace = PdfName.DeviceGray;
        Assert.assertEquals(1, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, null));
    }

    @Test
    public void deviceRGBCsTest() {
        PdfName colorSpace = PdfName.DeviceRGB;
        Assert.assertEquals(3, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, null));
    }

    @Test
    public void deviceCMYKCsTest() {
        PdfName colorSpace = PdfName.DeviceCMYK;
        Assert.assertEquals(4, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, null));
    }
}
