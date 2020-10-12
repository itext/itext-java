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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.LineHeight;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.RenderingMode;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.LineRenderer.LineSplitIntoGlyphsData;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class LineRendererUnitTest extends RendererUnitTest {

    private static final double EPS = 1e-5;

    @Test
    public void adjustChildPositionsAfterReorderingSimpleTest01() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummyImage = createLayoutedImageRenderer(100, 100, dummyDocument);
        Assert.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummyImage, dummy2), 10);
        Assert.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(37.3359985, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(137.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 4)})
    public void adjustChildPositionsAfterReorderingTestWithPercentMargins01() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        dummy1.setProperty(Property.MARGIN_LEFT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.MARGIN_RIGHT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.PADDING_LEFT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.PADDING_RIGHT, UnitValue.createPercentValue(10));
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        Assert.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummy2), 10);
        Assert.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        // If margins and paddings are specified in percents, we treat them as point values for now
        Assert.assertEquals(77.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    public void adjustChildPositionsAfterReorderingTestWithFloats01() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummyImage = createLayoutedImageRenderer(100, 100, dummyDocument);
        dummyImage.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        Assert.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummyImage, dummy2), 10);
        Assert.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        // Floating renderer is not repositioned
        Assert.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(37.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.INLINE_BLOCK_ELEMENT_WILL_BE_CLIPPED)})
    public void inlineBlockWithBigMinWidth01() {
        Document dummyDocument = createDocument();
        LineRenderer lineRenderer = (LineRenderer) new LineRenderer().setParent(dummyDocument.getRenderer());
        Div div = new Div().setMinWidth(2000).setHeight(100);
        DivRenderer inlineBlockRenderer = (DivRenderer) div.createRendererSubTree();
        lineRenderer.addChild(inlineBlockRenderer);
        LayoutResult result = lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));
        // In case there is an inline block child with large min-width, the inline block child will be force placed (not layouted properly)
        Assert.assertEquals(LayoutResult.FULL, result.getStatus());
        Assert.assertEquals(0, result.getOccupiedArea().getBBox().getHeight(), EPS);
        Assert.assertEquals(true, inlineBlockRenderer.getPropertyAsBoolean(Property.FORCED_PLACEMENT));
    }

    @Test
    public void adjustChildrenYLineTextChildHtmlModeTest() {
        Document document = createDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 100, 200, 200));
        lineRenderer.maxAscent = 100;

        TextRenderer childTextRenderer = new TextRenderer(new Text("Hello"));
        childTextRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        childTextRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 50, 200, 200));
        childTextRenderer.yLineOffset = 100;
        childTextRenderer.setProperty(Property.TEXT_RISE, 0f);

        lineRenderer.addChild(childTextRenderer);
        lineRenderer.adjustChildrenYLine();

        Assert.assertEquals(100f, lineRenderer.getOccupiedAreaBBox().getBottom(), EPS);
        Assert.assertEquals(100f, childTextRenderer.getOccupiedAreaBBox().getBottom(), EPS);
    }

    @Test
    public void adjustChildrenYLineImageChildHtmlModeTest() {
        Document document = createDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.occupiedArea = new LayoutArea(1, new Rectangle(50, 50, 200, 200));
        lineRenderer.maxAscent = 100;

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(200, 200));
        Image img = new Image(xObject);
        ImageRenderer childImageRenderer = new ImageRenderer(img);
        childImageRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        childImageRenderer.occupiedArea = new LayoutArea(1, new Rectangle(50, 50, 200, 200));

        lineRenderer.addChild(childImageRenderer);

        lineRenderer.adjustChildrenYLine();

        Assert.assertEquals(50f, lineRenderer.getOccupiedAreaBBox().getBottom(), EPS);
        Assert.assertEquals(150.0, childImageRenderer.getOccupiedAreaBBox().getBottom(), EPS);
    }

    @Test
    public void hasChildRendererInHtmlModeTest() {
        LineRenderer lineRenderer = new LineRenderer();

        TextRenderer textRenderer1 = new TextRenderer(new Text("text1"));

        TextRenderer textRenderer2 = new TextRenderer(new Text("text2"));
        textRenderer2.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        lineRenderer.addChild(textRenderer1);
        lineRenderer.addChild(textRenderer2);

        Assert.assertTrue(lineRenderer.hasChildRendererInHtmlMode());
    }

    @Test
    public void childRendererInDefaultModeTest() {
        LineRenderer lineRenderer = new LineRenderer();

        TextRenderer textRenderer1 = new TextRenderer(new Text("text1"));

        TextRenderer textRenderer2 = new TextRenderer(new Text("text2"));
        textRenderer2.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);

        lineRenderer.addChild(textRenderer1);
        lineRenderer.addChild(textRenderer2);

        Assert.assertFalse(lineRenderer.hasChildRendererInHtmlMode());
    }

    @Test
    public void hasChildRendererInHtmlModeNoChildrenTest() {
        LineRenderer lineRenderer = new LineRenderer();
        Assert.assertFalse(lineRenderer.hasChildRendererInHtmlMode());
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightAndNoChildrenTest() {
        Document document = createDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        lineRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());

        lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));

        Assert.assertEquals(0f, lineRenderer.maxAscent, 0f);
        Assert.assertEquals(0f, lineRenderer.maxDescent, 0f);
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightAndChildrenInDefaultModeTest() {
        Document document = createDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        lineRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createFixedValue(50));

        TextRenderer textRenderer1 = new TextRenderer(new Text("text"));
        textRenderer1.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);

        TextRenderer textRenderer2 = new TextRenderer(new Text("text"));
        textRenderer2.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);

        lineRenderer.addChild(textRenderer1);
        lineRenderer.addChild(textRenderer2);

        lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));

        Assert.assertEquals(10.3392f, lineRenderer.maxAscent, EPS);
        Assert.assertEquals(-2.98079f, lineRenderer.maxDescent, EPS);
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightAndChildInHtmlModeTest() {
        Document document = createDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        lineRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createFixedValue(50));

        TextRenderer textRenderer1 = new TextRenderer(new Text("text"));
        textRenderer1.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        TextRenderer textRenderer2 = new TextRenderer(new Text("text"));

        lineRenderer.addChild(textRenderer1);
        lineRenderer.addChild(textRenderer2);

        lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));

        Assert.assertEquals(28.67920f, lineRenderer.maxAscent, EPS);
        Assert.assertEquals(-21.32080f, lineRenderer.maxDescent, EPS);
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightPropertyNotSet() throws IOException {
        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(createDocument().getRenderer());
        lineRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        // Set fonts with different ascent/descent to line and text
        lineRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.HELVETICA));

        TextRenderer textRenderer = new TextRenderer(new Text("text"));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.COURIER));

        lineRenderer.addChild(textRenderer);
        LayoutResult layoutResLineHeightNotSet = lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));

        lineRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());
        LayoutResult layoutResLineHeightNormal = lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));

        Rectangle bboxLineHeightNotSet = layoutResLineHeightNotSet.getOccupiedArea().getBBox();
        Rectangle bboxLineHeightNormal = layoutResLineHeightNormal.getOccupiedArea().getBBox();
        Assert.assertTrue(bboxLineHeightNotSet.equalsWithEpsilon(bboxLineHeightNormal));
    }

    @Test
    public void minMaxWidthEqualsActualMarginsBordersPaddings() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        Text ranText = new Text("ran");
        ranText.setProperty(Property.MARGIN_LEFT, new UnitValue(UnitValue.POINT, 8f));

        ranText.setProperty(Property.MARGIN_RIGHT, new UnitValue(UnitValue.POINT, 10f));
        ranText.setProperty(Property.BORDER_RIGHT, new SolidBorder(3));
        ranText.setProperty(Property.PADDING_RIGHT, new UnitValue(UnitValue.POINT, 13f));

        TextRenderer ran = new TextRenderer(ranText);

        Text domText = new Text("dom");
        domText.setProperty(Property.MARGIN_LEFT, new UnitValue(UnitValue.POINT, 17f));
        domText.setProperty(Property.BORDER_LEFT, new SolidBorder(4));
        domText.setProperty(Property.PADDING_LEFT, new UnitValue(UnitValue.POINT, 12f));

        domText.setProperty(Property.MARGIN_RIGHT, new UnitValue(UnitValue.POINT, 2f));

        TextRenderer dom = new TextRenderer(domText);

        LayoutArea layoutArea = new LayoutArea(1,
                new Rectangle(AbstractRenderer.INF, AbstractRenderer.INF));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(ran);
        lineRenderer.addChild(dom);

        float countedMinWidth = lineRenderer.getMinMaxWidth().getMinWidth();
        LayoutResult result = lineRenderer.layout(new LayoutContext(layoutArea));

        Assert.assertEquals(result.getOccupiedArea().getBBox().getWidth(), countedMinWidth, 0.0001);
    }

    @Test
    public void splitLineIntoGlyphsSimpleTest() {
        Document dummyDocument = createDocument();
        TextRenderer dummy1 = createLayoutedTextRenderer("hello", dummyDocument);
        TextRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        TextRenderer dummy3 = createLayoutedTextRenderer("!!!", dummyDocument);
        IRenderer dummyImage1 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage2 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage3 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage4 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage5 = createLayoutedImageRenderer(100, 100, dummyDocument);

        LineRenderer toSplit = new LineRenderer();
        toSplit.addChildRenderer(dummyImage1);
        toSplit.addChildRenderer(dummyImage2);
        toSplit.addChildRenderer(dummy1);
        toSplit.addChildRenderer(dummyImage3);
        toSplit.addChildRenderer(dummy2);
        toSplit.addChildRenderer(dummy3);
        toSplit.addChildRenderer(dummyImage4);
        toSplit.addChildRenderer(dummyImage5);


        LineSplitIntoGlyphsData splitIntoGlyphsData = LineRenderer.splitLineIntoGlyphs(toSplit);
        Assert.assertEquals(Arrays.asList(dummyImage1, dummyImage2), splitIntoGlyphsData.getStarterNonTextRenderers());
        Assert.assertEquals(Arrays.asList(dummyImage3), splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assert.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assert.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy2));
        Assert.assertEquals(Arrays.asList(dummyImage4, dummyImage5), splitIntoGlyphsData.getInsertAfterAndRemove(dummy3));
        Assert.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy3));
        Assert.assertEquals(13, splitIntoGlyphsData.getLineGlyphs().size());
    }

    @Test
    public void splitLineIntoGlyphsWithLineBreakTest() {
        Document dummyDocument = createDocument();
        TextRenderer dummy1 = createLayoutedTextRenderer("hello", dummyDocument);
        TextRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        dummy2.line.set(2, new Glyph('\n', 0, '\n'));
        TextRenderer dummy3 = createLayoutedTextRenderer("!!!", dummyDocument);
        IRenderer dummyImage1 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage2 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage3 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage4 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage5 = createLayoutedImageRenderer(100, 100, dummyDocument);

        LineRenderer toSplit = new LineRenderer();
        toSplit.addChildRenderer(dummyImage1);
        toSplit.addChildRenderer(dummyImage2);
        toSplit.addChildRenderer(dummy1);
        toSplit.addChildRenderer(dummyImage3);
        toSplit.addChildRenderer(dummy2);
        toSplit.addChildRenderer(dummy3);
        toSplit.addChildRenderer(dummyImage4);
        toSplit.addChildRenderer(dummyImage5);


        LineSplitIntoGlyphsData splitIntoGlyphsData = LineRenderer.splitLineIntoGlyphs(toSplit);
        Assert.assertEquals(Arrays.asList(dummyImage1, dummyImage2), splitIntoGlyphsData.getStarterNonTextRenderers());
        Assert.assertEquals(Arrays.asList(dummyImage3), splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assert.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assert.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy2));
        Assert.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy3));
        Assert.assertEquals(7, splitIntoGlyphsData.getLineGlyphs().size());
    }

    @Test
    public void reorderSimpleTest() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummy3 = createLayoutedTextRenderer("!!!", dummyDocument);
        IRenderer dummyImage1 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage2 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage3 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage4 = createLayoutedImageRenderer(100, 100, dummyDocument);
        IRenderer dummyImage5 = createLayoutedImageRenderer(100, 100, dummyDocument);

        LineRenderer toSplit = new LineRenderer();
        toSplit.addChildRenderer(dummyImage1);
        toSplit.addChildRenderer(dummyImage2);
        toSplit.addChildRenderer(dummy1);
        toSplit.addChildRenderer(dummyImage3);
        toSplit.addChildRenderer(dummy2);
        toSplit.addChildRenderer(dummy3);
        toSplit.addChildRenderer(dummyImage4);
        toSplit.addChildRenderer(dummyImage5);


        LineSplitIntoGlyphsData splitIntoGlyphsData = LineRenderer.splitLineIntoGlyphs(toSplit);

        LineRenderer.reorder(toSplit, splitIntoGlyphsData, new int[]{0, 1, 4, 3, 2, 6, 5, 8, 7, 10, 9, 11, 12});
        // validate that all non text renderers are in place and all text renderers contains
        // the right revers ranges
        List<IRenderer> childRenderers = toSplit.getChildRenderers();
        Assert.assertEquals(8, childRenderers.size());
        Assert.assertSame(dummyImage1, childRenderers.get(0));
        Assert.assertSame(dummyImage2, childRenderers.get(1));
        List<int[]> firstReverseRanges = ((TextRenderer) childRenderers.get(2)).getReversedRanges();
        Assert.assertEquals(1, firstReverseRanges.size());
        Assert.assertArrayEquals(new int[]{2, 4}, firstReverseRanges.get(0));
        Assert.assertSame(dummyImage3, childRenderers.get(3));
        List<int[]> secondReverseRanges = ((TextRenderer) childRenderers.get(4)).getReversedRanges();
        Assert.assertEquals(2, secondReverseRanges.size());
        Assert.assertArrayEquals(new int[]{0, 1}, secondReverseRanges.get(0));
        Assert.assertArrayEquals(new int[]{2, 3}, secondReverseRanges.get(1));
        List<int[]> thirdReverseRanges = ((TextRenderer) childRenderers.get(5)).getReversedRanges();
        Assert.assertNull(thirdReverseRanges);
        Assert.assertSame(dummyImage4, childRenderers.get(6));
        Assert.assertSame(dummyImage5, childRenderers.get(7));
    }
}
