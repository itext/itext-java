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

import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.*;

@Tag("UnitTest")
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

        Assertions.assertTrue(imageRenderInfo.isInline());
        Assertions.assertEquals(image.getWidth(), imageRenderInfo.getImage().getWidth(), EPS);
        Assertions.assertEquals("/Im1", imageRenderInfo.getImageResourceName().toString());
        Assertions.assertEquals(new com.itextpdf.kernel.geom.Vector(0.5f, 0, 1), imageRenderInfo.getStartPoint());
        Assertions.assertEquals(matrix, imageRenderInfo.getImageCtm());
        Assertions.assertEquals(4, imageRenderInfo.getArea(), EPS);
        Assertions.assertEquals(0, imageRenderInfo.getColorSpaceDictionary().size());
        Assertions.assertEquals(1, imageRenderInfo.getCanvasTagHierarchy().size());
        Assertions.assertTrue(imageRenderInfo.hasMcid(2, true));
        Assertions.assertTrue(imageRenderInfo.hasMcid(2));
        Assertions.assertFalse(imageRenderInfo.hasMcid(1));
        Assertions.assertEquals(2, imageRenderInfo.getMcid());
    }

    private class TestGraphicsState extends CanvasGraphicsState {
        protected TestGraphicsState() {
        }
    }
}
