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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class SvgTagSvgNodeRendererUnitTest extends ExtendedITextTest {

    @Test
    public void calculateNestedViewportSameAsParentTest() {
        Rectangle expected = new Rectangle(0, 0, 600, 600);

        SvgDrawContext context = new SvgDrawContext(null, null);

        PdfDocument document = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);
        context.addViewPort(expected);

        SvgTagSvgNodeRenderer parent = new SvgTagSvgNodeRenderer();
        parent.setAttributesAndStyles(new HashMap<>());
        SvgTagSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();
        renderer.setParent(parent);

        Rectangle actual = renderer.calculateViewPort(context);
        Assertions.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void equalsOtherObjectNegativeTest() {
        SvgTagSvgNodeRenderer one = new SvgTagSvgNodeRenderer();
        CircleSvgNodeRenderer two = new CircleSvgNodeRenderer();
        Assertions.assertFalse(one.equals(two));
    }

    @Test
    public void noObjectBoundingBoxTest() {
        SvgTagSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();
        Assertions.assertNull(renderer.getObjectBoundingBox(null));
    }
}
