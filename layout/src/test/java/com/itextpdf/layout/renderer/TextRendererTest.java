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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
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
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.OverflowWrapPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.RenderingMode;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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

        Assert.assertEquals(result1.getOccupiedArea(), result2.getOccupiedArea());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT)
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
        Assert.assertEquals(val, rend.getText().toString());
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

        Assert.assertFalse(actualLine == glyphLine);
        Glyph glyph = actualLine.get(0);
        Glyph space = pdfFont.getGlyph('\u0020');
        // Check that the glyph line has been processed using the replaceSpecialWhitespaceGlyphs method
        Assert.assertEquals(space.getCode(), glyph.getCode());
        Assert.assertEquals(space.getWidth(), glyph.getWidth());
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
        renderer.setText(glyphLine, 1, 2);
        GlyphLine actualLine = renderer.getText();

        Assert.assertFalse(actualLine == glyphLine);
        Glyph glyph = actualLine.get(0);
        Glyph space = pdfFont.getGlyph('\u0020');
        // Check that the glyph line has been processed using the replaceSpecialWhitespaceGlyphs method
        Assert.assertEquals(space.getCode(), glyph.getCode());
        Assert.assertEquals(space.getWidth(), glyph.getWidth());
        Assert.assertEquals(1, actualLine.start);
        Assert.assertEquals(2, actualLine.end);
    }

    /**
     * This test assumes that absolute positioning for {@link Text} elements is
     * not supported. Adding this support is the subject of DEVSIX-1393.
     */
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT)
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
        Document doc = createDocument();
        TextRenderer textRenderer = createLayoutedTextRenderer("hello", doc);
        textRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20f));
        Assert.assertEquals(-2.980799674987793f, textRenderer.getDescent(), EPS);
    }

    @Test
    public void getOccupiedAreaBBoxTest() {
        Document doc = createDocument();
        TextRenderer textRenderer = createLayoutedTextRenderer("hello", doc);
        textRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        Assert.assertTrue(
                new Rectangle(0, 986.68f, 25.343998f, 13.32f).equalsWithEpsilon(textRenderer.getOccupiedAreaBBox()));
    }

    @Test
    public void getInnerAreaBBoxTest() {
        Document doc = createDocument();
        TextRenderer textRenderer = createLayoutedTextRenderer("hello", doc);
        textRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20f));
        textRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        Assert.assertTrue(new Rectangle(0, 986.68f, 5.343998f, -26.68f)
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

        Assert.assertEquals("NotoSans", pdfFont.getFontProgram().getFontNames().getFontName());
    }

    @Test
    public void myanmarCharacterBelongsToSpecificScripts() {
        // u1042 MYANMAR DIGIT TWO
        Assert.assertTrue(TextRenderer.codePointIsOfSpecialScript(4162));
    }

    @Test
    public void thaiCharacterBelongsToSpecificScripts() {
        // u0E19 THAI CHARACTER NO NU
        Assert.assertTrue(TextRenderer.codePointIsOfSpecialScript(3609));
    }

    @Test
    public void laoCharacterBelongsToSpecificScripts() {
        // u0EC8 LAO TONE MAI EK
        Assert.assertTrue(TextRenderer.codePointIsOfSpecialScript(3784));
    }

    @Test
    public void khmerCharacterBelongsToSpecificScripts() {
        // u1789 KHMER LETTER NYO
        Assert.assertTrue(TextRenderer.codePointIsOfSpecialScript(6025));
    }

    @Test
    public void cyrillicCharacterDoesntBelongToSpecificScripts() {
        // u0433 Cyrillic Small Letter U
        Assert.assertFalse(TextRenderer.codePointIsOfSpecialScript(1091));
    }
    
    @Test
    public void overflowWrapAnywhereProperty() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("wow");
        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(documentRenderer);

        MinMaxWidth minMaxWidth = textRenderer.getMinMaxWidth();

        Assert.assertTrue(minMaxWidth.getMinWidth() < minMaxWidth.getMaxWidth());
    }

    @Test
    public void overflowWrapBreakWordProperty() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("wooow");

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(documentRenderer);
        // overflow is set here to mock LineRenderer#layout behavior
        documentRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);

        float fullWordWidth = textRenderer.getMinMaxWidth().getMaxWidth();

        LayoutArea layoutArea = new LayoutArea(1,
                new Rectangle(fullWordWidth / 2, AbstractRenderer.INF));

        TextLayoutResult result = (TextLayoutResult) textRenderer.layout(new LayoutContext(layoutArea));
        Assert.assertFalse(result.isWordHasBeenSplit());

        textRenderer.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.BREAK_WORD);
        result = (TextLayoutResult) textRenderer.layout(new LayoutContext(layoutArea));
        Assert.assertTrue(result.isWordHasBeenSplit());
    }

    @Test
    public void overflowWrapAnywhereBoldSimulationMaxWidth() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("wow");
        text.setBold();

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(documentRenderer);

        float maxWidthNoOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);
        float maxWidthAndOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        Assert.assertEquals(maxWidthAndOverflowWrap, maxWidthNoOverflowWrap, 0.0001);
    }

    @Test
    public void overflowWrapAnywhereItalicSimulationMaxWidth() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("wow");
        text.setItalic();

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(documentRenderer);

        float maxWidthNoOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);
        float maxWidthAndOverflowWrap = textRenderer.getMinMaxWidth().getMaxWidth();

        Assert.assertEquals(maxWidthAndOverflowWrap, maxWidthNoOverflowWrap, 0.0001);
    }

    @Test
    public void overflowWrapAnywhereBoldSimulationMinWidth() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("wow");
        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(documentRenderer);

        float minWidthNoBoldSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        text.setBold();
        float minWidthAndBoldSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        Assert.assertTrue(minWidthAndBoldSimulation > minWidthNoBoldSimulation);
    }

    @Test
    public void overflowWrapAnywhereItalicSimulationMinWidth() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("wow");
        text.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.ANYWHERE);

        TextRenderer textRenderer = (TextRenderer) text.getRenderer();
        textRenderer.setParent(documentRenderer);

        float minWidthNoItalicSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        text.setItalic();
        float minWidthAndItalicSimulation = textRenderer.getMinMaxWidth().getMinWidth();

        Assert.assertTrue(minWidthAndItalicSimulation > minWidthNoItalicSimulation);
    }
}
