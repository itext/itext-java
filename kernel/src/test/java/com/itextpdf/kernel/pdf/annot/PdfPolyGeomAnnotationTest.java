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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.List;

@Tag("UnitTest")
public class PdfPolyGeomAnnotationTest extends ExtendedITextTest {
    private static final float FLOAT_EPSILON_COMPARISON = 1E-6f;

    @Test
    public void createPolygonTest() {
        Rectangle rect = new Rectangle(10, 10);
        float[] vertices = new float[]{1, 1, 1, 1};
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = PdfPolyGeomAnnotation.createPolygon(rect, vertices);

        Assertions.assertTrue(pdfPolyGeomAnnotation.getRectangle().toRectangle().equalsWithEpsilon(rect),
                "Rectangles are not equal");
        Assertions.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void createPolylineTest() {
        Rectangle rect = new Rectangle(10, 10);
        float[] vertices = new float[]{1, 1, 1, 1};
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = PdfPolyGeomAnnotation.createPolyLine(rect, vertices);

        Assertions.assertTrue(pdfPolyGeomAnnotation.getRectangle().toRectangle().equalsWithEpsilon(rect), "Rectangles are not equal");
        Assertions.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetVerticesFloatArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        float[] vertices = new float[]{1, 1, 1, 1};
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assertions.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED)})
    public void setAndGetVerticesFloatArrayLogMessageTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        float[] vertices = new float[]{1, 1, 1, 1};
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assertions.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetVerticesPdfArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfArray vertices = new PdfArray(new float[]{1, 1, 1, 1});
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assertions.assertArrayEquals(vertices.toFloatArray(), pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED)})
    public void setAndGetVerticesPdfArrayLogMessageTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        PdfArray vertices = new PdfArray(new float[]{1, 1, 1, 1});
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assertions.assertArrayEquals(vertices.toFloatArray(), pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLineEndingStylesTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        PdfArray lineEndingStyles = new PdfArray(new float[]{1, 2});
        pdfPolyGeomAnnotation.setLineEndingStyles(lineEndingStyles);

        Assertions.assertArrayEquals(lineEndingStyles.toFloatArray(),
                pdfPolyGeomAnnotation.getLineEndingStyles().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetMeasureTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        PdfDictionary measure = new PdfDictionary();
        measure.put(PdfName.Subtype, new PdfString(""));
        pdfPolyGeomAnnotation.setMeasure(measure);

        Assertions.assertEquals(measure, pdfPolyGeomAnnotation.getMeasure());
    }

    @Test
    public void setAndGetPathTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        List<PdfObject> arrays = new ArrayList<>();
        arrays.add(new PdfArray(new float[]{10, 10}));
        PdfArray path = new PdfArray(arrays);
        pdfPolyGeomAnnotation.setPath(path);

        Assertions.assertEquals(path.toString(), pdfPolyGeomAnnotation.getPath().toString());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.IF_PATH_IS_SET_VERTICES_SHALL_NOT_BE_PRESENT)})
    public void setAndGetPathLogMessageTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());
        pdfPolyGeomAnnotation.setVertices(new float[]{1, 1, 1, 1});

        List<PdfObject> arrays = new ArrayList<>();
        arrays.add(new PdfArray(new float[]{10, 10}));
        PdfArray path = new PdfArray(arrays);
        pdfPolyGeomAnnotation.setPath(path);

        Assertions.assertEquals(path, pdfPolyGeomAnnotation.getPath());
    }

    @Test
    public void setAndGetBorderStylePdfDictTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfDictionary style = new PdfDictionary();
        style.put(PdfName.Width, new PdfNumber(1));
        pdfPolyGeomAnnotation.setBorderStyle(style);

        Assertions.assertEquals(style, pdfPolyGeomAnnotation.getBorderStyle());
    }

    @Test
    public void setAndGetBorderStylePdfNameTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        pdfPolyGeomAnnotation.setBorderStyle(PdfName.D);

        Assertions.assertEquals(PdfName.D, pdfPolyGeomAnnotation.getBorderStyle().getAsName(PdfName.S));
    }

    @Test
    public void setAndGetDashPatternTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfArray array = new PdfArray(new float[]{1, 2});
        pdfPolyGeomAnnotation.setDashPattern(array);

        Assertions.assertEquals(array, pdfPolyGeomAnnotation.getBorderStyle().getAsArray(PdfName.D));
    }

    @Test
    public void setAndGetBorderEffectTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfDictionary dict = new PdfDictionary();
        pdfPolyGeomAnnotation.setBorderEffect(dict);

        Assertions.assertEquals(dict, pdfPolyGeomAnnotation.getBorderEffect());
    }

    @Test
    public void setAndGetInteriorColorPdfArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        PdfArray array = new PdfArray(colorValues);
        pdfPolyGeomAnnotation.setInteriorColor(array);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assertions.assertEquals(expectedColor, pdfPolyGeomAnnotation.getInteriorColor());
    }

    @Test
    public void setAndGetInteriorColorFloatArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        pdfPolyGeomAnnotation.setInteriorColor(colorValues);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assertions.assertEquals(expectedColor, pdfPolyGeomAnnotation.getInteriorColor());
    }
}
