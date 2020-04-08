/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.LineHeight;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.RenderingMode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class LineHeightHelperUnitTest extends ExtendedITextTest {

    private static final double EPS = 1e-5;
    private static final String FONTS = "./src/test/resources/com/itextpdf/layout/fonts/";
    private static final String OPEN_SANS_FONTS = FONTS + "Open_Sans/";

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsCourierTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        float[] fontAscenderDescender = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(629.0f * TextRenderer.TYPO_ASCENDER_SCALE_COEFF, fontAscenderDescender[0], EPS);
        Assert.assertEquals(-157.0f * TextRenderer.TYPO_ASCENDER_SCALE_COEFF, fontAscenderDescender[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsTimesTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        float[] fontAscenderDescender = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(683.0f * TextRenderer.TYPO_ASCENDER_SCALE_COEFF, fontAscenderDescender[0], EPS);
        Assert.assertEquals(-217.0f * TextRenderer.TYPO_ASCENDER_SCALE_COEFF, fontAscenderDescender[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsHelveticaTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        float[] fontAscenderDescender = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(718.0f * TextRenderer.TYPO_ASCENDER_SCALE_COEFF, fontAscenderDescender[0], EPS);
        Assert.assertEquals(-207.0f * TextRenderer.TYPO_ASCENDER_SCALE_COEFF, fontAscenderDescender[1], EPS);
    }

    @Test
    public void getFontAscenderDescenderNormalizedTextRendererTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setParent(document.getRenderer());
        float[] ascenderDescender = LineHeightHelper.getFontAscenderDescenderNormalized(textRenderer);
        Assert.assertEquals(10.33920f, ascenderDescender[0], EPS);
        Assert.assertEquals(-2.9808f, ascenderDescender[1], EPS);
    }

    @Test
    public void calculateLineHeightTextRendererNullTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(13.79999f, lineHeight, EPS);
    }

    @Test
    public void calculateLineHeightTextRendererNormalTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(13.79999f, lineHeight, EPS);
    }

    @Test
    public void calculateLineHeightTextRendererPointNegativeTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createFixedValue(-10));
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(13.79999f, lineHeight, EPS);
    }

    @Test
    public void calculateLineHeightTextRendererNormalNegativeTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(13.79999f, lineHeight, EPS);
    }

    @Test
    public void calculateLineHeighttTextRendererPointZeroTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createFixedValue(0));
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(0f, lineHeight, EPS);
    }


    @Test
    public void calculateLineHeighttTextRendererNormalZeroTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(13.79999f, lineHeight, EPS);
    }

    @Test
    public void calculateLineHeighttTextRendererPointTest() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createFixedValue(200));
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(200, lineHeight, EPS);
    }

    @Test
    public void calculateLineHeightTextRendererNormalAscenderDescenderSumForNotoSansFontTest() throws IOException {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfFont font = PdfFontFactory.createFont(FONTS + "NotoSans-Regular.ttf");
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setProperty(Property.FONT, font);
        textRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());
        textRenderer.setParent(document.getRenderer());
        float lineHeight = LineHeightHelper.calculateLineHeight(textRenderer);
        Assert.assertEquals(16.31999f, lineHeight, EPS);
    }

    @Test
    public void getActualAscenderDescenderTextRenderer() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        TextRenderer textRenderer = new TextRenderer(new Text("Hello"));
        textRenderer.setParent(document.getRenderer());
        float[] ascenderDescender = LineHeightHelper.getActualAscenderDescender(textRenderer);
        Assert.assertEquals(10.57919f, ascenderDescender[0], EPS);
        Assert.assertEquals(-3.22079f, ascenderDescender[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsNotoEmojiFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "NotoEmoji-Regular.ttf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(1068.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-292.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsNotoSansFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "NotoSans-Regular.ttf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(1068.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-292.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsNotoColorEmojiFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "NotoColorEmoji.ttf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        System.out.println(ascenderDescenderFromFontMetrics[0]);
        System.out.println(ascenderDescenderFromFontMetrics[1]);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsNotoSansCJKscRegularFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "NotoSansCJKsc-Regular.otf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(1160.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-320.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsPuritan2FontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "Puritan2.otf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(860.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-232.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsNotoSansCJKjpBoldFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "NotoSansCJKjp-Bold.otf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(1160.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-320.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsFreeSansFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONTS + "FreeSans.ttf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(800.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-200.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }

    @Test
    public void calculateFontAscenderDescenderFromFontMetricsOpenSansRegularFontTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(OPEN_SANS_FONTS + "OpenSans-Regular.ttf");
        float[] ascenderDescenderFromFontMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        Assert.assertEquals(1068.0f, ascenderDescenderFromFontMetrics[0], EPS);
        Assert.assertEquals(-292.0f, ascenderDescenderFromFontMetrics[1], EPS);
    }
}
