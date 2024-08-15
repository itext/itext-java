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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.OverflowWrapPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class TextRendererTest extends RendererUnitTest {
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";

    private static final double EPS = 1e-5;

    @Test
    public void nextRendererTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("hello");
        text.setNextRenderer(new TextRenderer(text));

        IRenderer textRenderer1 = text.getRenderer().setParent(documentRenderer);
        IRenderer textRenderer2 = text.getRenderer().setParent(documentRenderer);

        LayoutArea area = new LayoutArea(1, new Rectangle(100, 100, 100, 100));
        LayoutContext layoutContext = new LayoutContext(area);

        doc.close();

        LayoutResult result1 = textRenderer1.layout(layoutContext);
        LayoutResult result2 = textRenderer2.layout(layoutContext);

        Assertions.assertEquals(result1.getOccupiedArea(), result2.getOccupiedArea());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT)
    })
    public void setTextException() {
        final String val = "other text";
        final String fontName = "Helvetica";
        TextRenderer rend = (TextRenderer) new Text("basic text").getRenderer();
        FontProvider fp = new FontProvider();
        fp.addFont(fontName);
        rend.setProperty(Property.FONT_PROVIDER, fp);
        rend.setProperty(Property.FONT, new String[] {fontName});
        rend.setText(val);
        Assertions.assertEquals(val, rend.getText().toString());
    }

    @Test
    public void setTextGlyphLineAndFontParamTest() throws IOException {
        TextRenderer renderer = new TextRenderer(new Text("Some text"));
        String text = "\t";
        PdfFont pdfFont = PdfFontFactory.createFont();
        GlyphLine glyphLine = new GlyphLine();

        for (int i = 0; i < text.length(); i++) {
            int codePoint = TextUtil.isSurrogatePair(text, i) ? TextUtil.convertToUtf32(text, i) : (int) text.charAt(i);

            Glyph glyph = pdfFont.getGlyph(codePoint);
            glyphLine.add(glyph);
        }

        renderer.setText(glyphLine, pdfFont);
        GlyphLine actualLine = renderer.getText();

        Assertions.assertFalse(actualLine == glyphLine);
        Glyph glyph = actualLine.get(0);
        Glyph space = pdfFont.getGlyph('\u0020');
        // Check that the glyph line has been processed using the replaceSpecialWhitespaceGlyphs method
        Assertions.assertEquals(space.getCode(), glyph.getCode());
        Assertions.assertEquals(space.getWidth(), glyph.getWidth());
    }

    @Test
    public void setTextGlyphLineAndPositionsParamTest() throws IOException {
        TextRenderer renderer = new TextRenderer(new Text("Some text"));
        String text = "\tsome";
        PdfFont pdfFont = PdfFontFactory.createFont();
        GlyphLine glyphLine = new GlyphLine();

        for (int i = 0; i < text.length(); i++) {
            int codePoint = TextUtil.isSurrogatePair(text, i) ? TextUtil.convertToUtf32(text, i) : (int) text.charAt(i);

            Glyph glyph = pdfFont.getGlyph(codePoint);
            glyphLine.add(glyph);
        }

        renderer.setText(new GlyphLine(), pdfFont);
        glyphLine.start = 1;
        glyphLine.end = 2;
        renderer.setText(glyphLine, pdfFont);
        GlyphLine actualLine = renderer.getText();

        Assertions.assertFalse(actualLine == glyphLine);
        Glyph glyph = actualLine.get(0);
        Glyph space = pdfFont.getGlyph('\u0020');
        // Check that the glyph line has been processed using the replaceSpecialWhitespaceGlyphs method
        Assertions.assertEquals(space.getCode(), glyph.getCode());
        Assertions.assertEquals(space.getWidth(), glyph.getWidth());
        Assertions.assertEquals(1, actualLine.start);
        Assertions.assertEquals(2, actualLine.end);
    }

    /**
     * This test assumes that absolute positioning for {@link Text} elements is
     * not supported. Adding this support is the subject of DEVSIX-1393.
     */
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT)
    })
    public void setFontAsText() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        Text txt = new Text("text");
        txt.setProperty(Property.POSITION, LayoutPosition.ABSOLUTE);
        txt.setProperty(Property.TOP, 5f);
        FontProvider fp = new FontProvider();
        fp.addFont("Helvetica");
        txt.setProperty(Property.FONT_PROVIDER, fp);
        txt.setFontFamily("Helvetica");
        doc.add(new Paragraph().add(txt));
        doc.close();
    }

    @Test
    public void getDescentTest() {
        Document doc = createDummyDocument();
        TextRenderer textRenderer = createLayoutedTextRenderer("hello", doc);
        textRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20f));
        Assertions.assertEquals(-2.980799674987793f, textRenderer.getDescent(), EPS);
    }

    @Test
    public void getOccupiedAreaBBoxTest() {
        Document doc = createDummyDocument();
        TextRenderer textRenderer = createLayoutedTextRenderer("hello", doc);
        textRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        Assertions.assertTrue(
                new Rectangle(0, 986.68f, 25.343998f, 13.32f).equalsWithEpsilon(textRenderer.getOccupiedAreaBBox()));
    }

    @Test
    public void getInnerAreaBBoxTest() {
        Document doc = createDummyDocument();
        TextRenderer textRenderer = createLayoutedTextRenderer("hello", doc);
        textRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        Assertions.assertTrue(new Rectangle(0, 986.68f, 5.343998f, -26.68f)
                .equalsWithEpsilon(textRenderer.getInnerAreaBBox()));
    }

    @Test
    public void resolveFirstPdfFontWithGlyphsAvailableOnlyInSecondaryFont() {
        // Test that in TextRenderer the #resolveFirstPdfFont method is overloaded in such way
        // that yielded font contains at least some of the glyphs for the text characters.

        Text text = new Text("\u043A\u0456\u0440\u044B\u043B\u0456\u0446\u0430"); // "кірыліца"

        // Puritan doesn't contain cyrillic symbols, while Noto Sans does.
        text.setFontFamily(Arrays.asList("Puritan 2.0", "Noto Sans"));

        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(FONTS_FOLDER + "Puritan2.otf");
        fontProvider.addFont(FONTS_FOLDER + "NotoSans-Regular.ttf");
        text.setProperty(Property.FONT_PROVIDER, fontProvider);

        TextRenderer renderer = (TextRenderer) new TextRenderer(text);
        PdfFont pdfFont = renderer.resolveFirstPdfFont();

        Assertions.assertEquals("NotoSans", pdfFont.getFontProgram().getFontNames().getFontName());
    }

    @Test
    public void myanmarCharacterBelongsToSpecificScripts() {
        // u1042 MYANMAR DIGIT TWO
        Assertions.assertTrue(TextRenderer.codePointIsOfSpecialScript(4162));
    }

    @Test
    public void thaiCharacterBelongsToSpecificScripts() {
        // u0E19 THAI CHARACTER NO NU
        Assertions.assertTrue(TextRenderer.codePointIsOfSpecialScript(3609));
    }

    @Test
    public void laoCharacterBelongsToSpecificScripts() {
        // u0EC8 LAO TONE MAI EK
        Assertions.assertTrue(TextRenderer.codePointIsOfSpecialScript(3784));
    }

    @Test
    public void khmerCharacterBelongsToSpecificScripts() {
        // u1789 KHMER LETTER NYO
        Assertions.assertTrue(TextRenderer.codePointIsOfSpecialScript(6025));
    }

    @Test
    public void cyrillicCharacterDoesntBelongToSpecificScripts() {
        // u0433 Cyrillic Small Letter U
        Assertions.assertFalse(TextRenderer.codePointIsOfSpecialScript(1091));
    }

    @Test
    public void overflowWrapAnywhereProperty() {
        Text text = new Text("wow");
        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(createDummyDocument().getRenderer());

        MinMaxWidth minMaxWidth = textRenderer.getMinMaxWidth();

        Assertions.assertTrue(minMaxWidth.getMinWidth() < minMaxWidth.getMaxWidth());
    }

    @Test
    public void overflowWrapBreakWordProperty() {
        Text text = new Text("wooow");

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        RootRenderer parentRenderer = createDummyDocument().getRenderer();
        textRenderer.setParent(parentRenderer);
        // overflow is set here to mock LineRenderer#layout behavior
        parentRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);

        float fullWordWidth = textRenderer.getMinMaxWidth().getMaxWidth();

        LayoutArea layoutArea = new LayoutArea(1,
                new Rectangle(fullWordWidth / 2, AbstractRenderer.INF));

        TextLayoutResult result = (TextLayoutResult) textRenderer.layout(new LayoutContext(layoutArea));
        Assertions.assertFalse(result.isWordHasBeenSplit());

        textRenderer.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.BREAK_WORD);
        result = (TextLayoutResult) textRenderer.layout(new LayoutContext(layoutArea));
        Assertions.assertTrue(result.isWordHasBeenSplit());
    }

    @Test
    public void overflowWrapAnywhereBoldSimulationMaxWidth() {
        Text text = new Text("wow");

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(createDummyDocument().getRenderer());

        float maxWidthNoOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);
        float maxWidthAndOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        Assertions.assertEquals(maxWidthAndOverflowWrap, maxWidthNoOverflowWrap, 0.0001);
    }

    @Test
    public void overflowWrapAnywhereItalicSimulationMaxWidth() {
        Text text = new Text("wow");
        text.simulateItalic();

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(createDummyDocument().getRenderer());

        float maxWidthNoOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);
        float maxWidthAndOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        Assertions.assertEquals(maxWidthAndOverflowWrap, maxWidthNoOverflowWrap, 0.0001);
    }

    @Test
    public void overflowWrapAnywhereBoldSimulationMinWidth() {
        Text text = new Text("wow");
        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(createDummyDocument().getRenderer());

        float minWidthNoBoldSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        text.simulateBold();
        float minWidthAndBoldSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        Assertions.assertTrue(minWidthAndBoldSimulation > minWidthNoBoldSimulation);
    }

    @Test
    public void overflowWrapAnywhereItalicSimulationMinWidth() {
        Text text = new Text("wow");
        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(createDummyDocument().getRenderer());

        float minWidthNoItalicSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        text.simulateItalic();
        float minWidthAndItalicSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        Assertions.assertTrue(minWidthAndItalicSimulation > minWidthNoItalicSimulation);
    }

    @Test
    public void floatingRightMinMaxWidth() throws IOException {
        String longestWord = "float:right";
        String wholeText = "text with " + longestWord;
        TextRenderer textRenderer = new TextRenderer(new Text(wholeText));
        textRenderer.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        textRenderer.setParent(createDummyDocument().getRenderer());

        PdfFont font = PdfFontFactory.createFont();
        int fontSize = 12;
        textRenderer.setProperty(Property.FONT, font);
        textRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(fontSize));

        float expectedMaxWidth = font.getWidth(wholeText, fontSize);
        float expectedMinWidth = font.getWidth(longestWord, fontSize);

        MinMaxWidth minMaxWidth = textRenderer.getMinMaxWidth();
        Assertions.assertEquals(expectedMinWidth, minMaxWidth.getMinWidth(), 0.01f);
        Assertions.assertEquals(expectedMaxWidth, minMaxWidth.getMaxWidth(), 0.01f);
    }
}
