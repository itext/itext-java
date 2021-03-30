/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.CMapEncoding;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfType0FontTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/font/PdfType0FontTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void trueTypeFontAndCmapConstructorTest() throws IOException {
        TrueTypeFont ttf = new TrueTypeFont(sourceFolder + "NotoSerif-Regular_v1.7.ttf");

        PdfType0Font type0Font = new PdfType0Font(ttf, PdfEncodings.IDENTITY_H);

        CMapEncoding cmap = type0Font.getCmap();

        Assert.assertNotNull(cmap);
        Assert.assertTrue(cmap.isDirect());
        Assert.assertFalse(cmap.hasUniMap());
        Assert.assertNull(cmap.getUniMapName());
        Assert.assertEquals("Adobe", cmap.getRegistry());
        Assert.assertEquals("Identity", cmap.getOrdering());
        Assert.assertEquals(0, cmap.getSupplement());
        Assert.assertEquals(PdfEncodings.IDENTITY_H, cmap.getCmapName());
    }

    @Test
    public void unsupportedCmapTest() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.OnlyIdentityCMapsSupportsWithTrueType);

        TrueTypeFont ttf = new TrueTypeFont(sourceFolder + "NotoSerif-Regular_v1.7.ttf");
        PdfType0Font type0Font = new PdfType0Font(ttf, PdfEncodings.WINANSI);
    }

    @Test
    public void dictionaryConstructorTest() throws IOException {
        String filePath = sourceFolder + "documentWithType0Noto.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filePath));

        PdfDictionary fontDict = pdfDocument.getPage(1).getResources()
                .getResource(PdfName.Font).getAsDictionary(new PdfName("F1"));

        PdfType0Font type0Font = new PdfType0Font(fontDict);

        CMapEncoding cmap = type0Font.getCmap();

        Assert.assertNotNull(cmap);
        Assert.assertTrue(cmap.isDirect());
        Assert.assertFalse(cmap.hasUniMap());
        Assert.assertNull(cmap.getUniMapName());
        Assert.assertEquals("Adobe", cmap.getRegistry());
        Assert.assertEquals("Identity", cmap.getOrdering());
        Assert.assertEquals(0, cmap.getSupplement());
        Assert.assertEquals(PdfEncodings.IDENTITY_H, cmap.getCmapName());
    }
}
