/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.LogMessageConstant;
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
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTest.class)
public class PdfPolyGeomAnnotationTest extends ExtendedITextTest {
    private static final float FLOAT_EPSILON_COMPARISON = 1E-6f;

    @Test
    public void createPolygonTest() {
        Rectangle rect = new Rectangle(10, 10);
        float[] vertices = new float[]{1, 1, 1, 1};
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = PdfPolyGeomAnnotation.createPolygon(rect, vertices);

        Assert.assertTrue("Rectangles are not equal",
                pdfPolyGeomAnnotation.getRectangle().toRectangle().equalsWithEpsilon(rect));
        Assert.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void createPolylineTest() {
        Rectangle rect = new Rectangle(10, 10);
        float[] vertices = new float[]{1, 1, 1, 1};
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = PdfPolyGeomAnnotation.createPolyLine(rect, vertices);

        Assert.assertTrue("Rectangles are not equal",
                pdfPolyGeomAnnotation.getRectangle().toRectangle().equalsWithEpsilon(rect));
        Assert.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetVerticesFloatArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        float[] vertices = new float[]{1, 1, 1, 1};
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assert.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED)})
    public void setAndGetVerticesFloatArrayLogMessageTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        float[] vertices = new float[]{1, 1, 1, 1};
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assert.assertArrayEquals(vertices, pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetVerticesPdfArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfArray vertices = new PdfArray(new float[]{1, 1, 1, 1});
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assert.assertArrayEquals(vertices.toFloatArray(), pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED)})
    public void setAndGetVerticesPdfArrayLogMessageTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        PdfArray vertices = new PdfArray(new float[]{1, 1, 1, 1});
        pdfPolyGeomAnnotation.setVertices(vertices);

        Assert.assertArrayEquals(vertices.toFloatArray(), pdfPolyGeomAnnotation.getVertices().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLineEndingStylesTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Path, new PdfString(""));
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(dict);

        PdfArray lineEndingStyles = new PdfArray(new float[]{1, 2});
        pdfPolyGeomAnnotation.setLineEndingStyles(lineEndingStyles);

        Assert.assertArrayEquals(lineEndingStyles.toFloatArray(),
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

        Assert.assertEquals(measure, pdfPolyGeomAnnotation.getMeasure());
    }

    @Test
    public void setAndGetPathTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        List<PdfObject> arrays = new ArrayList<>();
        arrays.add(new PdfArray(new float[]{10, 10}));
        PdfArray path = new PdfArray(arrays);
        pdfPolyGeomAnnotation.setPath(path);

        Assert.assertEquals(path.toString(), pdfPolyGeomAnnotation.getPath().toString());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.IF_PATH_IS_SET_VERTICES_SHALL_NOT_BE_PRESENT)})
    public void setAndGetPathLogMessageTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());
        pdfPolyGeomAnnotation.setVertices(new float[]{1, 1, 1, 1});

        List<PdfObject> arrays = new ArrayList<>();
        arrays.add(new PdfArray(new float[]{10, 10}));
        PdfArray path = new PdfArray(arrays);
        pdfPolyGeomAnnotation.setPath(path);

        Assert.assertEquals(path, pdfPolyGeomAnnotation.getPath());
    }

    @Test
    public void setAndGetBorderStylePdfDictTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfDictionary style = new PdfDictionary();
        style.put(PdfName.Width, new PdfNumber(1));
        pdfPolyGeomAnnotation.setBorderStyle(style);

        Assert.assertEquals(style, pdfPolyGeomAnnotation.getBorderStyle());
    }

    @Test
    public void setAndGetBorderStylePdfNameTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        pdfPolyGeomAnnotation.setBorderStyle(PdfName.D);

        Assert.assertEquals(PdfName.D, pdfPolyGeomAnnotation.getBorderStyle().getAsName(PdfName.S));
    }

    @Test
    public void setAndGetDashPatternTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfArray array = new PdfArray(new float[]{1, 2});
        pdfPolyGeomAnnotation.setDashPattern(array);

        Assert.assertEquals(array, pdfPolyGeomAnnotation.getBorderStyle().getAsArray(PdfName.D));
    }

    @Test
    public void setAndGetBorderEffectTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());

        PdfDictionary dict = new PdfDictionary();
        pdfPolyGeomAnnotation.setBorderEffect(dict);

        Assert.assertEquals(dict, pdfPolyGeomAnnotation.getBorderEffect());
    }

    @Test
    public void setAndGetInteriorColorPdfArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        PdfArray array = new PdfArray(colorValues);
        pdfPolyGeomAnnotation.setInteriorColor(array);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assert.assertEquals(expectedColor, pdfPolyGeomAnnotation.getInteriorColor());
    }

    @Test
    public void setAndGetInteriorColorFloatArrayTest() {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = new PdfPolygonAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        pdfPolyGeomAnnotation.setInteriorColor(colorValues);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assert.assertEquals(expectedColor, pdfPolyGeomAnnotation.getInteriorColor());
    }
}
