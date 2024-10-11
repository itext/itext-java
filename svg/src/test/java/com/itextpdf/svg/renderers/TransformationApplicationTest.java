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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class TransformationApplicationTest extends ExtendedITextTest {

    @Test
    public void normalDrawTest() {
        byte[] expected = "1 0 0 1 7.5 0 cm\n0 0 0 rg\nf\n".getBytes(StandardCharsets.UTF_8);

        ISvgNodeRenderer nodeRenderer = new AbstractSvgNodeRenderer() {

            @Override
            public ISvgNodeRenderer createDeepCopy() {
                return null;
            }

            @Override
            public Rectangle getObjectBoundingBox(SvgDrawContext context) {
                return null;
            }

            @Override
            protected void doDraw(SvgDrawContext context) {
                // do nothing
            }
        };

        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(SvgConstants.Attributes.TRANSFORM, "translate(10)");
        nodeRenderer.setAttributesAndStyles(attributeMap);

        SvgDrawContext context = new SvgDrawContext(null, null);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        context.pushCanvas(canvas);

        nodeRenderer.draw(context);

        byte[] actual = canvas.getContentStream().getBytes(true);

        Assertions.assertArrayEquals(expected, actual);
    }
}
