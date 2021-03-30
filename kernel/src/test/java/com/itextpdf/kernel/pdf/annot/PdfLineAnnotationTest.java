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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfLineAnnotationTest extends ExtendedITextTest {
    private static final float FLOAT_EPSILON_COMPARISON = 1E-6f;

    @Test
    public void getLineTest() {
        float[] lineArray = new float[] {1f, 1f, 1f, 1f};
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new Rectangle(10, 10), lineArray);

        Assert.assertArrayEquals(lineArray, pdfLineAnnotation.getLine().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetBorderStylePdfDictTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Width, new PdfNumber(1));
        pdfLineAnnotation.setBorderStyle(dict);

        Assert.assertEquals(dict, pdfLineAnnotation.getBorderStyle());
    }

    @Test
    public void setAndGetBorderStylePdfNameTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        pdfLineAnnotation.setBorderStyle(PdfName.D);

        Assert.assertEquals(PdfName.D, pdfLineAnnotation.getBorderStyle().getAsName(PdfName.S));
    }

    @Test
    public void setDashPatternTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfArray array = new PdfArray(new float[]{1, 2});
        pdfLineAnnotation.setDashPattern(array);

        Assert.assertArrayEquals(array.toFloatArray(),
                pdfLineAnnotation.getBorderStyle().getAsArray(PdfName.D).toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLineEndingStylesTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfArray lineEndingStyles = new PdfArray(new float[]{1, 2});
        pdfLineAnnotation.setLineEndingStyles(lineEndingStyles);

        Assert.assertArrayEquals(lineEndingStyles.toFloatArray(),
                pdfLineAnnotation.getLineEndingStyles().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetInteriorColorPdfArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        PdfArray array = new PdfArray(colorValues);
        pdfLineAnnotation.setInteriorColor(array);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assert.assertEquals(expectedColor, pdfLineAnnotation.getInteriorColor());
    }

    @Test
    public void setAndGetInteriorColorFloatArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        pdfLineAnnotation.setInteriorColor(colorValues);

        Color expectedColor = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Assert.assertEquals(expectedColor, pdfLineAnnotation.getInteriorColor());
    }

    @Test
    public void setAndGetLeaderLineLengthTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float length = 1f;

        pdfLineAnnotation.setLeaderLineLength(length);

        Assert.assertEquals(length, pdfLineAnnotation.getLeaderLineLength(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getLeaderLineLengthNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assert.assertEquals(0, pdfLineAnnotation.getLeaderLineLength(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLeaderLineExtensionTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float length = 1f;

        pdfLineAnnotation.setLeaderLineExtension(length);

        Assert.assertEquals(length, pdfLineAnnotation.getLeaderLineExtension(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLeaderLineExtensionNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assert.assertEquals(0, pdfLineAnnotation.getLeaderLineExtension(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetLeaderLineOffsetTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float length = 1f;

        pdfLineAnnotation.setLeaderLineOffset(length);

        Assert.assertEquals(length, pdfLineAnnotation.getLeaderLineOffset(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getLeaderLineOffsetNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assert.assertEquals(0, pdfLineAnnotation.getLeaderLineOffset(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetContentsAsCaptionTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        boolean contentsAsCaption = true;

        pdfLineAnnotation.setContentsAsCaption(contentsAsCaption);

        Assert.assertEquals(contentsAsCaption, pdfLineAnnotation.getContentsAsCaption());
    }

    @Test
    public void getContentsAsCaptionNullTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        Assert.assertFalse(pdfLineAnnotation.getContentsAsCaption());
    }

    @Test
    public void setAndGetCaptionPositionTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        pdfLineAnnotation.setCaptionPosition(PdfName.Inline);

        Assert.assertEquals(PdfName.Inline, pdfLineAnnotation.getCaptionPosition());
    }

    @Test
    public void setAndGetMeasureTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());

        PdfDictionary measure = new PdfDictionary();
        measure.put(PdfName.Subtype, new PdfString(""));
        pdfLineAnnotation.setMeasure(measure);

        Assert.assertEquals(measure, pdfLineAnnotation.getMeasure());
    }

    @Test
    public void setAndGetCaptionOffsetPdfArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        PdfArray offset = new PdfArray(new float[] {1, 1});

        pdfLineAnnotation.setCaptionOffset(offset);

        Assert.assertArrayEquals(offset.toFloatArray(), pdfLineAnnotation.getCaptionOffset().toFloatArray(),
                FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setAndGetCaptionOffsetFloatArrayTest() {
        PdfLineAnnotation pdfLineAnnotation = new PdfLineAnnotation(new PdfDictionary());
        float[] offset = new float[] {1, 1};

        pdfLineAnnotation.setCaptionOffset(offset);

        Assert.assertArrayEquals(offset, pdfLineAnnotation.getCaptionOffset().toFloatArray(), FLOAT_EPSILON_COMPARISON);
    }
}
