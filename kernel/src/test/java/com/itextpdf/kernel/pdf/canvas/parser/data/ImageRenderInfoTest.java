/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.*;

@Category(UnitTest.class)
public class ImageRenderInfoTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/parser/data/"
            + "ImageRenderInfoTest/";
    private final float EPS = 0.001f;

    @Test
    public void checkImageRenderInfoTest() throws MalformedURLException {
        String source_image = SOURCE_FOLDER + "simple.tif";

        PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(source_image));
        PdfStream imageStream = image.getPdfObject();
        Matrix matrix = new Matrix(2, 0.5f, 0, 2, 0.5f, 0);
        Stack<CanvasTag> tagHierarchy = new Stack<CanvasTag>();
        tagHierarchy.push(new CanvasTag(new PdfName("tag"), 2));
        ImageRenderInfo imageRenderInfo = new ImageRenderInfo(tagHierarchy, new TestGraphicsState(), matrix,
                imageStream, new PdfName("Im1"), new PdfDictionary(), true);

        Assert.assertTrue(imageRenderInfo.isInline());
        Assert.assertEquals(image.getWidth(), imageRenderInfo.getImage().getWidth(), EPS);
        Assert.assertEquals("/Im1", imageRenderInfo.getImageResourceName().toString());
        Assert.assertEquals(new com.itextpdf.kernel.geom.Vector(0.5f, 0, 1), imageRenderInfo.getStartPoint());
        Assert.assertEquals(matrix, imageRenderInfo.getImageCtm());
        Assert.assertEquals(4, imageRenderInfo.getArea(), EPS);
        Assert.assertEquals(0, imageRenderInfo.getColorSpaceDictionary().size());
        Assert.assertEquals(1, imageRenderInfo.getCanvasTagHierarchy().size());
        Assert.assertTrue(imageRenderInfo.hasMcid(2, true));
        Assert.assertTrue(imageRenderInfo.hasMcid(2));
        Assert.assertFalse(imageRenderInfo.hasMcid(1));
        Assert.assertEquals(2, imageRenderInfo.getMcid());
    }

    private class TestGraphicsState extends CanvasGraphicsState {
        protected TestGraphicsState() {
        }
    }
}
