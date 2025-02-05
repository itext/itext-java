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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfLineAnnotationTest extends ExtendedITextTest {
    private static final float FLOAT_EPSILON_COMPARISON = 1E-6f;

    @Test
    public void getLineTest() {
        float[] lineArray = new float[] {1f, 1f, 1f, 1f};
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new Rectangle(10, 10), lineArray);

        Assertions.assertArrayEquals(lineArray, pdfLineAnnotation.getLine().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetBorderStylePdfDictTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Width, new PdfNumber(1));
        pdfLineAnnotation.setBorderStyle(dict);

        Assertions.assertEquals(dict, pdfLineAnnotation.getBorderStyle());
    }

    @Test
    public void setAndGetBorderStylePdfNameTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        pdfLineAnnotation.setBorderStyle(PdfName.D);

        Assertions.assertEquals(PdfName.D, pdfLineAnnotation.getBorderStyle().getAsName(PdfName.S));
    }

    @Test
    public void setDashPatternTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfArray array = new PdfArray(new float[]{1, 2});
        pdfLineAnnotation.setDashPattern(array);

        Assertions.assertArrayEquals(array.toFloatArray(),
                pdfLineAnnotation.getBorderStyle().getAsArray(PdfName.D).toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLineEndingStylesTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfArray lineEndingStyles = new PdfArray(new float[]{1, 2});
        pdfLineAnnotation.setLineEndingStyles(lineEndingStyles);

        Assertions.assertArrayEquals(lineEndingStyles.toFloatArray(),
                pdfLineAnnotation.getLineEndingStyles().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetInteriorColorPdfArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        PdfArray array = new PdfArray(colorValues);
        pdfLineAnnotation.setInteriorColor(array);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assertions.assertEquals(expectedColor, pdfLineAnnotation.getInteriorColor());
    }

    @Test
    public void setAndGetInteriorColorFloatArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        pdfLineAnnotation.setInteriorColor(colorValues);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assertions.assertEquals(expectedColor, pdfLineAnnotation.getInteriorColor());
    }

    @Test
    public void setAndGetLeaderLineLengthTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float length = 1f;

        pdfLineAnnotation.setLeaderLineLength(length);

        Assertions.assertEquals(length, pdfLineAnnotation.getLeaderLineLength(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getLeaderLineLengthNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assertions.assertEquals(0, pdfLineAnnotation.getLeaderLineLength(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLeaderLineExtensionTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float length = 1f;

        pdfLineAnnotation.setLeaderLineExtension(length);

        Assertions.assertEquals(length, pdfLineAnnotation.getLeaderLineExtension(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLeaderLineExtensionNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assertions.assertEquals(0, pdfLineAnnotation.getLeaderLineExtension(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLeaderLineOffsetTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float length = 1f;

        pdfLineAnnotation.setLeaderLineOffset(length);

        Assertions.assertEquals(length, pdfLineAnnotation.getLeaderLineOffset(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getLeaderLineOffsetNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assertions.assertEquals(0, pdfLineAnnotation.getLeaderLineOffset(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetContentsAsCaptionTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        boolean contentsAsCaption = true;

        pdfLineAnnotation.setContentsAsCaption(contentsAsCaption);

        Assertions.assertEquals(contentsAsCaption, pdfLineAnnotation.getContentsAsCaption());
    }

    @Test
    public void getContentsAsCaptionNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assertions.assertFalse(pdfLineAnnotation.getContentsAsCaption());
    }

    @Test
    public void setAndGetCaptionPositionTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        pdfLineAnnotation.setCaptionPosition(PdfName.Inline);

        Assertions.assertEquals(PdfName.Inline, pdfLineAnnotation.getCaptionPosition());
    }

    @Test
    public void setAndGetMeasureTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfDictionary measure = new PdfDictionary();
        measure.put(PdfName.Subtype, new PdfString(""));
        pdfLineAnnotation.setMeasure(measure);

        Assertions.assertEquals(measure, pdfLineAnnotation.getMeasure());
    }

    @Test
    public void setAndGetCaptionOffsetPdfArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        PdfArray offset = new PdfArray(new float[] {1, 1});

        pdfLineAnnotation.setCaptionOffset(offset);

        Assertions.assertArrayEquals(offset.toFloatArray(), pdfLineAnnotation.getCaptionOffset().toFloatArray(),
                FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetCaptionOffsetFloatArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float[] offset = new float[] {1, 1};

        pdfLineAnnotation.setCaptionOffset(offset);

        Assertions.assertArrayEquals(offset, pdfLineAnnotation.getCaptionOffset().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }
}
