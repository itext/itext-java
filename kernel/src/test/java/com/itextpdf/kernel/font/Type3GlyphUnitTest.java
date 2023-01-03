/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Type3GlyphUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/font/Type3GlyphUnitTest/";

    @Test
    public void addImageWithoutMaskTest() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Type3Glyph glyph = new Type3Glyph(new PdfStream(), pdfDoc);
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "imageTest.png");
        Exception e = Assert.assertThrows(PdfException.class,
                () -> glyph.addImageWithTransformationMatrix(img, 100, 0, 0, 100, 0, 0, false));
        Assert.assertEquals("Not colorized type3 fonts accept only mask images.", e.getMessage());
    }

    @Test
    public void addInlineImageMaskTest() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Type3Glyph glyph = new Type3Glyph(new PdfStream(), pdfDoc);
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "imageTest.png");
        img.makeMask();
        Assert.assertNull(glyph.addImageWithTransformationMatrix(img, 100, 0, 0, 100, 0, 0, true));
    }

    @Test
    //TODO DEVSIX-5764 Display message error for non-inline images in type 3 glyph
    public void addImageMaskAsNotInlineTest() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Type3Glyph glyph = new Type3Glyph(new PdfStream(), pdfDoc);
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "imageTest.png");
        img.makeMask();
        Assert.assertThrows(NullPointerException.class,
                () -> glyph.addImageWithTransformationMatrix(img, 100, 0, 0, 100, 0, 0, false));
    }
}
