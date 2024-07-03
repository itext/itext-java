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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.LineHeight;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.LineRenderer.LineSplitIntoGlyphsData;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class LineRendererUnitTest extends RendererUnitTest {

    private static final double EPS = 1e-5;

    @Test
    public void adjustChildPositionsAfterReorderingSimpleTest01() {
        Document dummyDocument = createDummyDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummyImage = createLayoutedImageRenderer(100, 100, dummyDocument);
        Assertions.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummyImage, dummy2), 10);
        Assertions.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(37.3359985, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(137.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 4)})
    public void adjustChildPositionsAfterReorderingTestWithPercentMargins01() {
        Document dummyDocument = createDummyDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        dummy1.setProperty(Property.MARGIN_LEFT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.MARGIN_RIGHT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.PADDING_LEFT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.PADDING_RIGHT, UnitValue.createPercentValue(10));
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        Assertions.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummy2), 10);
        Assertions.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        // If margins and paddings are specified in percents, we treat them as point values for now
        Assertions.assertEquals(77.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    public void adjustChildPositionsAfterReorderingTestWithFloats01() {
        Document dummyDocument = createDummyDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummyImage = createLayoutedImageRenderer(100, 100, dummyDocument);
        dummyImage.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        Assertions.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummyImage, dummy2), 10);
        Assertions.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        // Floating renderer is not repositioned
        Assertions.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        Assertions.assertEquals(37.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.INLINE_BLOCK_ELEMENT_WILL_BE_CLIPPED)})
    public void inlineBlockWithBigMinWidth01() {
        Document dummyDocument = createDummyDocument();
        LineRenderer lineRenderer = (LineRenderer) new LineRenderer().setParent(dummyDocument.getRenderer());
        Div div = new Div().setMinWidth(2000).setHeight(100);
        DivRenderer inlineBlockRenderer = (DivRenderer) div.createRendererSubTree();
        lineRenderer.addChild(inlineBlockRenderer);
        LayoutResult result = lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));
        // In case there is an inline block child with large min-width, the inline block child will be force placed (not layouted properly)
        Assertions.assertEquals(LayoutResult.FULL, result.getStatus());
        Assertions.assertEquals(0, result.getOccupiedArea().getBBox().getHeight(), EPS);
        Assertions.assertEquals(true, inlineBlockRenderer.getPropertyAsBoolean(Property.FORCED_PLACEMENT));
    }

    @Test
    public void adjustChildrenYLineTextChildHtmlModeTest() {
        Document document = createDummyDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        lineRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 100, 200, 200));
        lineRenderer.maxAscent = 150;
        lineRenderer.maxDescent = -50;

        TextRenderer childTextRenderer = new TextRenderer(new Text("Hello"));
        childTextRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        childTextRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 50, 200, 200));
        childTextRenderer.yLineOffset = 150;
        childTextRenderer.setProperty(Property.TEXT_RISE, 0f);

        lineRenderer.addChild(childTextRenderer);
        lineRenderer.adjustChildrenYLine();

        Assertions.assertEquals(100f, lineRenderer.getOccupiedAreaBBox().getBottom(), EPS);
        Assertions.assertEquals(100f, childTextRenderer.getOccupiedAreaBBox().getBottom(), EPS);
    }

    @Test
    public void adjustChildrenYLineImageChildHtmlModeTest() {
        Document document = createDummyDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.occupiedArea = new LayoutArea(1, new Rectangle(50, 50, 200, 200));
        lineRenderer.maxAscent = 150;
        lineRenderer.maxDescent = -50;

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(200, 200));
        Image img = new Image(xObject);
        ImageRenderer childImageRenderer = new ImageRenderer(img);
        childImageRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        childImageRenderer.occupiedArea = new LayoutArea(1, new Rectangle(50, 50, 200, 200));

        lineRenderer.addChild(childImageRenderer);

        lineRenderer.adjustChildrenYLine();

        Assertions.assertEquals(50f, lineRenderer.getOccupiedAreaBBox().getBottom(), EPS);
        //image should be on the baseline top 250 - maxAscent 150 = 100
        Assertions.assertEquals(100.0, childImageRenderer.getOccupiedAreaBBox().getBottom(), EPS);
    }

    @Test
    public void hasChildRendererInHtmlModeTest() {
        LineRenderer lineRenderer = new LineRenderer();

        TextRenderer textRenderer1 = new TextRenderer(new Text("text1"));

        TextRenderer textRenderer2 = new TextRenderer(new Text("text2"));
        textRenderer2.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        lineRenderer.addChild(textRenderer1);
        lineRenderer.addChild(textRenderer2);

        Assertions.assertTrue(lineRenderer.hasChildRendererInHtmlMode());
    }

    @Test
    public void childRendererInDefaultModeTest() {
        LineRenderer lineRenderer = new LineRenderer();

        TextRenderer textRenderer1 = new TextRenderer(new Text("text1"));

        TextRenderer textRenderer2 = new TextRenderer(new Text("text2"));
        textRenderer2.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);

        lineRenderer.addChild(textRenderer1);
        lineRenderer.addChild(textRenderer2);

        Assertions.assertFalse(lineRenderer.hasChildRendererInHtmlMode());
    }

    @Test
    public void hasChildRendererInHtmlModeNoChildrenTest() {
        LineRenderer lineRenderer = new LineRenderer();
        Assertions.assertFalse(lineRenderer.hasChildRendererInHtmlMode());
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightAndNoChildrenTest() {
        Document document = createDummyDocument();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        lineRenderer.setProperty(Property.LINE_HEIGHT, LineHeight.createNormalValue());

        lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));

        Assertions.assertEquals(0f, lineRenderer.maxAscent, 0f);
        Assertions.assertEquals(0f, lineRenderer.maxDescent, 0f);
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightAndChildrenInDefaultModeTest() {
        Document document = createDummyDocument();

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

        Assertions.assertEquals(10.3392f, lineRenderer.maxAscent, EPS);
        Assertions.assertEquals(-2.98079f, lineRenderer.maxDescent, EPS);
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightAndChildInHtmlModeTest() {
        Document document = createDummyDocument();

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

        Assertions.assertEquals(28.67920f, lineRenderer.maxAscent, EPS);
        Assertions.assertEquals(-21.32080f, lineRenderer.maxDescent, EPS);
    }

    @Test
    public void lineRendererLayoutInHtmlModeWithLineHeightPropertyNotSet() throws IOException {
        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(createDummyDocument().getRenderer());
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
        Assertions.assertTrue(bboxLineHeightNotSet.equalsWithEpsilon(bboxLineHeightNormal));
    }

    @Test
    public void minMaxWidthEqualsActualMarginsBordersPaddings() {
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
        lineRenderer.setParent(createDummyDocument().getRenderer());
        lineRenderer.addChild(ran);
        lineRenderer.addChild(dom);

        float countedMinWidth = lineRenderer.getMinMaxWidth().getMinWidth();
        LayoutResult result = lineRenderer.layout(new LayoutContext(layoutArea));

        Assertions.assertEquals(result.getOccupiedArea().getBBox().getWidth(), countedMinWidth, 0.0001);
    }

    @Test
    public void splitLineIntoGlyphsSimpleTest() {
        Document dummyDocument = createDummyDocument();
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
        Assertions.assertEquals(Arrays.asList(dummyImage1, dummyImage2), splitIntoGlyphsData.getStarterNonTextRenderers());
        Assertions.assertEquals(Arrays.asList(dummyImage3), splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assertions.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assertions.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy2));
        Assertions.assertEquals(Arrays.asList(dummyImage4, dummyImage5), splitIntoGlyphsData.getInsertAfterAndRemove(dummy3));
        Assertions.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy3));
        Assertions.assertEquals(13, splitIntoGlyphsData.getLineGlyphs().size());
    }

    @Test
    public void splitLineIntoGlyphsWithLineBreakTest() {
        Document dummyDocument = createDummyDocument();
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
        Assertions.assertEquals(Arrays.asList(dummyImage1, dummyImage2), splitIntoGlyphsData.getStarterNonTextRenderers());
        Assertions.assertEquals(Arrays.asList(dummyImage3), splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assertions.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy1));
        Assertions.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy2));
        Assertions.assertNull(splitIntoGlyphsData.getInsertAfterAndRemove(dummy3));
        Assertions.assertEquals(7, splitIntoGlyphsData.getLineGlyphs().size());
    }

    @Test
    public void reorderSimpleTest() {
        Document dummyDocument = createDummyDocument();
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
        Assertions.assertEquals(8, childRenderers.size());
        Assertions.assertSame(dummyImage1, childRenderers.get(0));
        Assertions.assertSame(dummyImage2, childRenderers.get(1));
        List<int[]> firstReverseRanges = ((TextRenderer) childRenderers.get(2)).getReversedRanges();
        Assertions.assertEquals(1, firstReverseRanges.size());
        Assertions.assertArrayEquals(new int[]{2, 4}, firstReverseRanges.get(0));
        Assertions.assertSame(dummyImage3, childRenderers.get(3));
        List<int[]> secondReverseRanges = ((TextRenderer) childRenderers.get(4)).getReversedRanges();
        Assertions.assertEquals(2, secondReverseRanges.size());
        Assertions.assertArrayEquals(new int[]{0, 1}, secondReverseRanges.get(0));
        Assertions.assertArrayEquals(new int[]{2, 3}, secondReverseRanges.get(1));
        List<int[]> thirdReverseRanges = ((TextRenderer) childRenderers.get(5)).getReversedRanges();
        Assertions.assertNull(thirdReverseRanges);
        Assertions.assertSame(dummyImage4, childRenderers.get(6));
        Assertions.assertSame(dummyImage5, childRenderers.get(7));
    }
}
