/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

@Tag("UnitTest")
public class VerticalLineThroughTest extends ExtendedITextTest {
    private static final float EPS = 0.001f;

    public static Collection<Object[]> parameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        for (float fontSize : new float[]{12, 24, 48}) {
            for (float textRise : new float[]{-6, 0, 6}) {
                parameters.add(new Object[]{fontSize, textRise});
            }
        }
        return parameters;
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void defaultLineThroughPositionTest(float fontSize, float textRise) {
        Text text = new Text("ABC").setFontSize(fontSize).setTextRise(textRise).setLineThrough();
        assertDecorationPosition(text, fontSize, 0.5f);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void customUnderlinePositionIsPreservedTest(float fontSize, float textRise) {
        Text text = new Text("ABC").setFontSize(fontSize).setTextRise(textRise)
                .setUnderline(null, .75f, 0, 0, 7 / 24f, LineCapStyle.BUTT);
        assertDecorationPosition(text, fontSize, 7 / 24f);
    }

    @Test
    public void verticalPositionDoesNotChangeHorizontalPositionTest() {
        Underline underline = new Underline(null, 1, 0, 2, .25f, LineCapStyle.BUTT);
        Assertions.assertEquals(8, underline.getXPosition(24), EPS);
        Assertions.assertSame(underline, underline.setXPosition(3, .5f));
        Assertions.assertEquals(15, underline.getXPosition(24), EPS);
        Assertions.assertEquals(8, underline.getYPosition(24), EPS);
        Assertions.assertEquals(.25f, underline.getYPositionMul(), EPS);
    }

    private static void assertDecorationPosition(Text text, float fontSize, float expectedMultiplier) {
        try (Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            LineRenderer line = new LineRenderer();
            line.setParent(document.getRenderer());
            line.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            line.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            line.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            line.addChild(new Text("Base").setFontSize(fontSize).createRendererSubTree());
            TextRenderer renderer = (TextRenderer) text.createRendererSubTree();
            line.addChild(renderer);
            Assertions.assertEquals(LayoutResult.FULL, line.layout(new LayoutContext(
                    new LayoutArea(1, new Rectangle(100, 100, 400, 400)))).getStatus());
            RecordingCanvas canvas = new RecordingCanvas(document.getPdfDocument());
            Underline underline = text.<Underline>getProperty(Property.UNDERLINE);
            renderer.drawSingleUnderline(underline, new TransparentColor(ColorConstants.BLACK), canvas, fontSize, 0);
            Rectangle actual = canvas.rectangle;
            Rectangle inner = renderer.getInnerAreaBBox();
            Rectangle occupied = renderer.getOccupiedAreaBBox();
            Assertions.assertEquals(occupied.getX() + occupied.getWidth() * expectedMultiplier,
                    actual.getX() + actual.getWidth() / 2, EPS);
            Assertions.assertEquals(inner.getY(), actual.getY(), EPS);
            Assertions.assertEquals(inner.getHeight(), actual.getHeight(), EPS);
            Assertions.assertEquals(.75f, actual.getWidth(), EPS);
        }
    }

    private static class RecordingCanvas extends PdfCanvas {
        Rectangle rectangle;

        RecordingCanvas(PdfDocument document) {
            super(document.addNewPage());
        }

        @Override
        public PdfCanvas rectangle(Rectangle rectangle) {
            this.rectangle = rectangle.clone();
            return super.rectangle(rectangle);
        }
    }
}
