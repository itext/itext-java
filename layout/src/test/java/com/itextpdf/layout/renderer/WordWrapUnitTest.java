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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.TextSequenceWordWrapping.LastFittingChildRendererData;
import com.itextpdf.layout.renderer.TextSequenceWordWrapping.MinMaxWidthOfTextRendererSequenceHelper;
import com.itextpdf.layout.renderer.TextSequenceWordWrapping.SpecialScriptsContainingSequenceStatus;
import com.itextpdf.layout.renderer.TextSequenceWordWrapping.SpecialScriptsContainingTextRendererSequenceInfo;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class WordWrapUnitTest extends ExtendedITextTest {

    public static final String THAI_FONT = "./src/test/resources/com/itextpdf/layout/fonts/NotoSansThai-Regular.ttf";
    public static final String REGULAR_FONT = "./src/test/resources/com/itextpdf/layout/fonts/NotoSans-Regular.ttf";
    public static final String KHMER_FONT = "./src/test/resources/com/itextpdf/layout/fonts/KhmerOS.ttf";

    // หากอากาศดีในวันพรุ่งนี้เราจะไปปิกนิก - one sentence, multiple words.
    public static final String THAI_TEXT = "\u0E2B\u0E32\u0E01\u0E2D\u0E32\u0E01\u0E32\u0E28\u0E14\u0E35"
            + "\u0E43\u0E19\u0E27\u0E31\u0E19\u0E1E\u0E23\u0E38\u0E48\u0E07\u0E19\u0E35\u0E49"
            + "\u0E40\u0E23\u0E32\u0E08\u0E30\u0E44\u0E1B\u0E1B\u0E34\u0E01\u0E19\u0E34\u0E01";

    // อากาศ - one word
    public static final String THAI_WORD = "\u0E2D\u0E32\u0E01\u0E32\u0E28";

    @Test
    public void isTextRendererAndRequiresSpecialScriptPreLayoutProcessingTest() throws IOException {
        TextRenderer textRenderer = new TextRenderer(new Text(THAI_TEXT));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setText(THAI_TEXT);
        Assertions.assertTrue(TextSequenceWordWrapping.isTextRendererAndRequiresSpecialScriptPreLayoutProcessing(textRenderer));
    }

    @Test
    public void isTextRendererAndDoesNotRequireSpecialScriptPreLayoutProcessingTest() throws IOException {
        TextRenderer textRenderer = new TextRenderer(new Text(THAI_TEXT));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setText(THAI_TEXT);
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>());
        Assertions.assertFalse(TextSequenceWordWrapping.isTextRendererAndRequiresSpecialScriptPreLayoutProcessing(textRenderer));
    }

    @Test
    public void isNotTextRenderer() {
        TabRenderer tabRenderer = new TabRenderer(new Tab());
        Assertions.assertFalse(TextSequenceWordWrapping.isTextRendererAndRequiresSpecialScriptPreLayoutProcessing(tabRenderer));
    }

    @Test
    public void splitAndOverflowInheritSpecialScriptsWordBreakPoints() throws IOException {
        String nonSpecialScriptText = "Some non-special script";
        TextRenderer textRenderer = new TextRenderer(new Text(nonSpecialScriptText));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(REGULAR_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setText(nonSpecialScriptText);
        Assertions.assertNull(textRenderer.getSpecialScriptsWordBreakPoints());
        TextSequenceWordWrapping.isTextRendererAndRequiresSpecialScriptPreLayoutProcessing(textRenderer);
        Assertions.assertNotNull(textRenderer.getSpecialScriptsWordBreakPoints());
        Assertions.assertTrue(textRenderer.getSpecialScriptsWordBreakPoints().isEmpty());

        // layout is needed prior to calling #split() in order to fill TextRenderer fields required to be non-null
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        textRenderer.setParent(document.getRenderer());
        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(MinMaxWidthUtils.getInfWidth(), AbstractRenderer.INF));
        textRenderer.layout(new LayoutContext(layoutArea));

        TextRenderer[] splitRenderers = textRenderer.split(nonSpecialScriptText.length() / 2);
        for (TextRenderer split : splitRenderers) {
            Assertions.assertNotNull(split.getSpecialScriptsWordBreakPoints());
            Assertions.assertTrue(split.getSpecialScriptsWordBreakPoints().isEmpty());
        }
    }

    @Test
    public void noNeedToSplitTextRendererOnLineSplit() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        PdfFont pdfFont = PdfFontFactory.createFont(REGULAR_FONT, PdfEncodings.IDENTITY_H);

        // หากอากาศอากาศ - first 3 glyphs are an unbreakable placeholder in the first renderer so that text.start != 0;
        // the next 5 glyphs are an unbreakable part of first renderer that're supposed to fully fit on the first line,
        // the last 5 glyphs are an unbreakable part of the second renderer that could fit only partially, hence fully overflowed
        String thai = "\u0E2B\u0E32\u0E01" + THAI_WORD + THAI_WORD;
        TextRenderer textRendererFirst = new TextRenderer(new Text(""));
        textRendererFirst.setProperty(Property.FONT, pdfFont);
        textRendererFirst.setText(thai.substring(0, 8));
        textRendererFirst.text.setStart(3);
        textRendererFirst.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(3, 8)));
        textRendererFirst.setParent(document.getRenderer());
        float longestWordLength = textRendererFirst.getMinMaxWidth().getMaxWidth();

        TextRenderer textRendererSecond = new TextRenderer(new Text(thai.substring(8)));
        textRendererSecond.setProperty(Property.FONT, pdfFont);
        textRendererSecond.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));

        LayoutArea layoutArea = new LayoutArea(1,
                new Rectangle(longestWordLength * 1.5f, AbstractRenderer.INF));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRendererFirst);
        lineRenderer.addChild(textRendererSecond);

        LayoutResult result = lineRenderer.layout(new LayoutContext(layoutArea));
        Assertions.assertEquals(LayoutResult.PARTIAL, result.getStatus());
        IRenderer splitRenderer = result.getSplitRenderer();
        Assertions.assertNotNull(splitRenderer);
        List<IRenderer> splitChildren = splitRenderer.getChildRenderers();
        Assertions.assertNotNull(splitChildren);
        Assertions.assertEquals(1, splitChildren.size());

        IRenderer overflowRenderer = result.getOverflowRenderer();
        Assertions.assertNotNull(overflowRenderer);
        List<IRenderer> overflowChildren = overflowRenderer.getChildRenderers();
        Assertions.assertNotNull(overflowChildren);
        Assertions.assertEquals(1, overflowChildren.size());

        TextRenderer splitChild = (TextRenderer) splitChildren.get(0);
        TextRenderer overflowChild = (TextRenderer) overflowChildren.get(0);

        Assertions.assertEquals(splitChild.text, overflowChild.text);

    }

    @Test
    public void specialScriptPreLayoutProcessing() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        int thaiTextSplitPosition = THAI_TEXT.length() / 2;
        PdfFont font = PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H);

        TextRenderer textRendererFirstPart = new TextRenderer(new Text(THAI_TEXT.substring(0, thaiTextSplitPosition)));
        textRendererFirstPart.setProperty(Property.FONT, font);
        textRendererFirstPart.setText(THAI_TEXT.substring(0, thaiTextSplitPosition));

        TextRenderer textRendererSecondPart = new TextRenderer(new Text(THAI_TEXT.substring(thaiTextSplitPosition)));
        textRendererSecondPart.setProperty(Property.FONT, font);
        textRendererSecondPart.setText(THAI_TEXT.substring(thaiTextSplitPosition));

        TableRenderer floatingNonTextRenderer = new TableRenderer(new Table(3));
        floatingNonTextRenderer.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        TableRenderer regularNonTextRenderer = new TableRenderer(new Table(3));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRendererFirstPart);
        lineRenderer.addChild(floatingNonTextRenderer);
        lineRenderer.addChild(textRendererSecondPart);
        lineRenderer.addChild(regularNonTextRenderer);

        SpecialScriptsContainingTextRendererSequenceInfo info = TextSequenceWordWrapping
                .getSpecialScriptsContainingTextRendererSequenceInfo(lineRenderer, 0);
        int numberOfSequentialTextRenderers = info.numberOfSequentialTextRenderers;
        String sequentialTextContent = info.sequentialTextContent;
        List<Integer> indicesOfFloating = info.indicesOfFloating;

        Assertions.assertEquals(3, numberOfSequentialTextRenderers);
        Assertions.assertEquals(THAI_TEXT, sequentialTextContent);
        Assertions.assertEquals(1, indicesOfFloating.size());
        Assertions.assertEquals(1, (int) indicesOfFloating.get(0));

        List<Integer> possibleBreaks = new ArrayList<Integer>(Arrays.asList(3, 8, 10, 12, 15, 20, 23, 26, 28, 30, 36));
        TextSequenceWordWrapping.distributePossibleBreakPointsOverSequentialTextRenderers(
                lineRenderer, 0, numberOfSequentialTextRenderers, possibleBreaks, indicesOfFloating);

        List<Integer> possibleBreaksFirstPart = textRendererFirstPart.getSpecialScriptsWordBreakPoints();
        Assertions.assertNotNull(possibleBreaksFirstPart);
        List<Integer> possibleBreaksSecondPart = textRendererSecondPart.getSpecialScriptsWordBreakPoints();
        Assertions.assertNotNull(possibleBreaksSecondPart);

        int indexOfLastPossibleBreakInTheFirstRenderer = 4;

        List<Integer> expectedPossibleBreaksFirstPart = possibleBreaks
                .subList(0, indexOfLastPossibleBreakInTheFirstRenderer + 1);
        List<Integer> expectedPossibleBreaksSecondPart = possibleBreaks
                .subList(indexOfLastPossibleBreakInTheFirstRenderer + 1, possibleBreaks.size());

        Assertions.assertEquals(expectedPossibleBreaksFirstPart, possibleBreaksFirstPart);

        for (int i = 0; i < expectedPossibleBreaksSecondPart.size(); i++) {
            expectedPossibleBreaksSecondPart.set(i, expectedPossibleBreaksSecondPart.get(i) - thaiTextSplitPosition);
        }

        Assertions.assertEquals(expectedPossibleBreaksSecondPart, possibleBreaksSecondPart);
    }

    @Test
    public void specialScriptRendererFollowedByRegularTextRendererGetSequenceInfo() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer specialScriptRenderer = new TextRenderer(new Text(THAI_TEXT));
        specialScriptRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        specialScriptRenderer.setText(THAI_TEXT);

        TextRenderer nonSpecialScriptRenderer = new TextRenderer(new Text("non special"));
        nonSpecialScriptRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(REGULAR_FONT, PdfEncodings.IDENTITY_H));
        nonSpecialScriptRenderer.setText("non special");

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(specialScriptRenderer);
        lineRenderer.addChild(nonSpecialScriptRenderer);

        SpecialScriptsContainingTextRendererSequenceInfo info = TextSequenceWordWrapping
                .getSpecialScriptsContainingTextRendererSequenceInfo(lineRenderer, 0);
        Assertions.assertEquals(1, info.numberOfSequentialTextRenderers);
        Assertions.assertEquals(THAI_TEXT, info.sequentialTextContent);
        Assertions.assertTrue(info.indicesOfFloating.isEmpty());
    }

    @Test
    public void oneThaiWordSplitAcrossMultipleRenderersDistributePossibleBreakPoints() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());

        PdfFont font = PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H);

        for (int i = 0; i < THAI_WORD.length(); i++) {
            TextRenderer textRenderer = new TextRenderer(new Text(""));
            textRenderer.setProperty(Property.FONT, font);
            textRenderer.setText(new String(new char[] {THAI_WORD.charAt(i)}));
            lineRenderer.addChild(textRenderer);
        }

        List<Integer> possibleBreaks = new ArrayList<>(1);
        possibleBreaks.add(THAI_WORD.length());

        TextSequenceWordWrapping
                .distributePossibleBreakPointsOverSequentialTextRenderers(lineRenderer, 0, THAI_WORD.length(),
                        possibleBreaks, new ArrayList<Integer>());

        List<IRenderer> childRenderers = lineRenderer.getChildRenderers();
        for (int i = 0; i < THAI_WORD.length(); i++) {
            List<Integer> possibleBreaksPerRenderer = ((TextRenderer) childRenderers.get(i))
                    .getSpecialScriptsWordBreakPoints();
            Assertions.assertNotNull(possibleBreaksPerRenderer);
            Assertions.assertEquals(1, possibleBreaksPerRenderer.size());
            int breakPoint = possibleBreaksPerRenderer.get(0);
            if (i != THAI_WORD.length() - 1) {
                Assertions.assertEquals(-1, breakPoint);
            } else {
                Assertions.assertEquals(((TextRenderer) childRenderers.get(i)).length(), breakPoint);
            }
        }
    }

    @Test
    public void oneThaiWordSplitAcrossMultipleRenderersGetIndexAndLayoutResult() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());

        String twoWords = THAI_WORD + "\u0E14\u0E35";

        PdfFont font = PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H);
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();
        for (int i = 0; i < twoWords.length(); i++) {
            TextRenderer textRenderer = new TextRenderer(new Text(""));
            textRenderer.setProperty(Property.FONT, font);
            textRenderer.setText(new String(new char[]{twoWords.charAt(i)}));
            if (i == THAI_WORD.length() - 1 || i == twoWords.length() - 1) {
                textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(1)));
            } else {
                textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(-1)));
            }
            lineRenderer.addChild(textRenderer);

            LayoutArea layoutArea = new LayoutArea(1, new Rectangle(0, 0, i * 100, 100));
            if (i == twoWords.length() - 1) {
                specialScriptLayoutResults.put(i, new LayoutResult(LayoutResult.NOTHING, layoutArea, null, null));
            } else {
                specialScriptLayoutResults.put(i, new LayoutResult(LayoutResult.FULL, layoutArea, null, null));
            }
        }

        LastFittingChildRendererData lastFittingChildRendererData = TextSequenceWordWrapping
                .getIndexAndLayoutResultOfTheLastTextRendererContainingSpecialScripts(lineRenderer, THAI_WORD.length() + 1,
                        specialScriptLayoutResults, false, true);

        Assertions.assertEquals(5, lastFittingChildRendererData.childIndex);
        Assertions.assertEquals(LayoutResult.NOTHING, lastFittingChildRendererData.childLayoutResult.getStatus());
        Assertions.assertNull(lastFittingChildRendererData.childLayoutResult.getOccupiedArea());
    }

    @Test
    public void multipleFloatsFollowedByUnfittingThaiRenderer() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());

        PdfFont font = PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H);
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();
        int indexOfThaiRenderer = 3;
        for (int i = 0; i < indexOfThaiRenderer; i++) {
            TableRenderer tableRenderer = new TableRenderer(new Table(3));
            tableRenderer.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            lineRenderer.addChild(tableRenderer);
        }

        TextRenderer textRenderer = new TextRenderer(new Text(""));
        textRenderer.setProperty(Property.FONT, font);
        textRenderer.setText(THAI_WORD);
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(THAI_WORD.length())));
        lineRenderer.addChild(textRenderer);
        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(0, 0, 0, 100));
        specialScriptLayoutResults.put(indexOfThaiRenderer,
                new LayoutResult(LayoutResult.NOTHING, layoutArea, null, null));

        LastFittingChildRendererData lastFittingChildRendererData = TextSequenceWordWrapping
                .getIndexAndLayoutResultOfTheLastTextRendererContainingSpecialScripts(lineRenderer, indexOfThaiRenderer,
                        specialScriptLayoutResults, false, true);

        Assertions.assertEquals(indexOfThaiRenderer, lastFittingChildRendererData.childIndex);
        Assertions.assertEquals(LayoutResult.NOTHING, lastFittingChildRendererData.childLayoutResult.getStatus());
    }

    @Test
    public void trailingRightSideSpacesGetIndexAndLayoutResult() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());

        PdfFont font = PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H);
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();
        for (int i = 0; i < THAI_WORD.length(); i++) {
            TextRenderer textRenderer = new TextRenderer(new Text(""));
            textRenderer.setProperty(Property.FONT, font);
            if (i == THAI_WORD.length() - 1) {
                textRenderer.setText(new String(new char[] {THAI_WORD.charAt(i), ' ', ' ', ' '}));
                textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(4)));
            } else {
                textRenderer.setText(new String(new char[]{THAI_WORD.charAt(i)}));
                textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(-1)));
            }
            lineRenderer.addChild(textRenderer);

            LayoutArea layoutArea = new LayoutArea(1, new Rectangle(0, 0, i * 100, 100));
            if (i == THAI_WORD.length() - 1) {
                textRenderer.occupiedArea = layoutArea;
                TextRenderer[] split = textRenderer.split(1);
                specialScriptLayoutResults.put(i, new LayoutResult(LayoutResult.PARTIAL, layoutArea, split[0], split[1]));
            } else {
                specialScriptLayoutResults.put(i, new LayoutResult(LayoutResult.FULL, layoutArea, null, null));
            }
        }

        LastFittingChildRendererData lastFittingChildRendererData = TextSequenceWordWrapping
                .getIndexAndLayoutResultOfTheLastTextRendererContainingSpecialScripts(lineRenderer, THAI_WORD.length() - 1,
                        specialScriptLayoutResults, false, true);

        Assertions.assertEquals(THAI_WORD.length() - 1, lastFittingChildRendererData.childIndex);
        Assertions.assertEquals(specialScriptLayoutResults.get(THAI_WORD.length() - 1), lastFittingChildRendererData.childLayoutResult);
    }

    @Test
    public void minMaxWidthWithOneRenderer() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(""));
        textRenderer.setParent(document.getRenderer());
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setText(THAI_TEXT);
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(3, 8, 10, 12, 15, 20, 23, 26, 28, 30, 36)));

        MinMaxWidth minMaxWidth = textRenderer.getMinMaxWidth();

        Assertions.assertTrue(minMaxWidth.getMinWidth() < minMaxWidth.getMaxWidth());
    }

    @Test
    public void specialScriptsWordBreakPointsSplit() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(""));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setText(THAI_TEXT);
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(3, 8, 10, 12, 15, 20, 23, 26, 28, 30, 36)));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        MinMaxWidth minMaxWidth = lineRenderer.getMinMaxWidth();

        float width = minMaxWidth.getMinWidth() + minMaxWidth.getMaxWidth() / 2;
        LayoutResult layoutResult = lineRenderer
                .layout(new LayoutContext(new LayoutArea(1, new Rectangle(width, 500))));

        IRenderer lineSplitRenderer = layoutResult.getSplitRenderer();
        Assertions.assertNotNull(lineSplitRenderer);
        Assertions.assertNotNull(lineSplitRenderer.getChildRenderers());
        Assertions.assertTrue(lineSplitRenderer.getChildRenderers().get(0) instanceof TextRenderer);
        TextRenderer textSplitRenderer = (TextRenderer) lineSplitRenderer.getChildRenderers().get(0);
        Assertions.assertNotNull(textSplitRenderer.getSpecialScriptsWordBreakPoints());

        IRenderer lineOverflowRenderer = layoutResult.getOverflowRenderer();
        Assertions.assertNotNull(lineOverflowRenderer);
        Assertions.assertNotNull(lineOverflowRenderer.getChildRenderers());
        Assertions.assertTrue(lineOverflowRenderer.getChildRenderers().get(0) instanceof TextRenderer);
        TextRenderer textOverflowRenderer = (TextRenderer) lineOverflowRenderer.getChildRenderers().get(0);
        Assertions.assertNotNull(textOverflowRenderer.getSpecialScriptsWordBreakPoints());

        int textSplitRendererTextLength = textSplitRenderer.text.toString().length();
        for (int specialScriptsWordBreakPoint : textSplitRenderer.getSpecialScriptsWordBreakPoints()) {
            Assertions.assertTrue(specialScriptsWordBreakPoint <= textSplitRendererTextLength);
        }

        for (int specialScriptsWordBreakPoint : textOverflowRenderer.getSpecialScriptsWordBreakPoints()) {
            Assertions.assertTrue(specialScriptsWordBreakPoint > textSplitRendererTextLength
                    && specialScriptsWordBreakPoint <= textOverflowRenderer.text.size());
        }
    }

    @Test
    public void forcedSplitOnTooNarrowArea() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(THAI_WORD));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        float minWidth = lineRenderer.getMinMaxWidth().getMinWidth();

        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(minWidth / 2, 100));
        LayoutResult layoutResult = lineRenderer.layout(new LayoutContext(layoutArea));

        Assertions.assertEquals(LayoutResult.PARTIAL, layoutResult.getStatus());
    }

    @Test
    public void midWordSplitPartialLayoutResult() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(THAI_WORD));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        ArrayList<Integer> specialScriptsWordBreakPoints = new ArrayList<Integer>();
        specialScriptsWordBreakPoints.add(5);
        textRenderer.setSpecialScriptsWordBreakPoints(specialScriptsWordBreakPoints);

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        float minWidth = lineRenderer.getMinMaxWidth().getMinWidth();

        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(minWidth / 2, 100));
        LayoutResult layoutResult = lineRenderer.layout(new LayoutContext(layoutArea));

        Assertions.assertEquals(LayoutResult.PARTIAL, layoutResult.getStatus());
    }

    @Test
    public void multipleRenderers() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        document.setFont(PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));

        ArrayList<Integer> possibleBreaks = new ArrayList<Integer>(Arrays.asList(3, 8, 10, 12, 15, 20, 23, 26, 28, 30, 36));

        TextRenderer textRenderer = new TextRenderer(new Text(THAI_TEXT));
        textRenderer.setSpecialScriptsWordBreakPoints(possibleBreaks);

        LineRenderer lineRendererWithOneChild = new LineRenderer();
        lineRendererWithOneChild.setParent(document.getRenderer());
        lineRendererWithOneChild.addChild(textRenderer);

        float maxWidth = lineRendererWithOneChild.getMinMaxWidth().getMaxWidth();

        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(maxWidth / 2, 100));
        LayoutResult layoutResultSingleTextRenderer = lineRendererWithOneChild.layout(new LayoutContext(layoutArea));

        IRenderer splitRendererOneChild = layoutResultSingleTextRenderer.getSplitRenderer();
        Assertions.assertNotNull(splitRendererOneChild);
        Assertions.assertEquals(1, splitRendererOneChild.getChildRenderers().size());
        String splitTextOneChild = ((TextRenderer) splitRendererOneChild.getChildRenderers().get(0)).text.toString();

        IRenderer overflowRendererOneChild = layoutResultSingleTextRenderer.getOverflowRenderer();
        Assertions.assertNotNull(overflowRendererOneChild);
        Assertions.assertEquals(1, overflowRendererOneChild.getChildRenderers().size());
        String overflowTextOneChild = ((TextRenderer) overflowRendererOneChild.getChildRenderers().get(0)).text.toString();

        LineRenderer lineRendererMultipleChildren = new LineRenderer();
        lineRendererMultipleChildren.setParent(document.getRenderer());
        for (int i = 0; i < THAI_TEXT.length(); i++) {
            TextRenderer oneGlyphRenderer = new TextRenderer(new Text(new String(new char[]{THAI_TEXT.charAt(i)})));
            List<Integer> specialScriptsWordBreakPoints = new ArrayList<Integer>();
            if (possibleBreaks.contains(i)) {
                specialScriptsWordBreakPoints.add(i);
            }
            oneGlyphRenderer.setSpecialScriptsWordBreakPoints(specialScriptsWordBreakPoints);
            lineRendererMultipleChildren.addChild(oneGlyphRenderer);
        }

        LayoutResult layoutResultMultipleTextRenderers = lineRendererMultipleChildren.layout(new LayoutContext(layoutArea));

        IRenderer splitRendererMultipleChildren = layoutResultMultipleTextRenderers.getSplitRenderer();
        Assertions.assertNotNull(splitRendererMultipleChildren);
        Assertions.assertTrue(splitRendererMultipleChildren.getChildRenderers().size() > 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (IRenderer childRenderer : splitRendererMultipleChildren.getChildRenderers()) {
            stringBuilder.append(((TextRenderer) childRenderer).text.toString());
        }
        String splitTextMultipleChildren = stringBuilder.toString();

        IRenderer overflowRendererMultipleChildren = layoutResultMultipleTextRenderers.getOverflowRenderer();
        Assertions.assertNotNull(overflowRendererMultipleChildren);
        Assertions.assertTrue(overflowRendererMultipleChildren.getChildRenderers().size() > 0);
        stringBuilder.setLength(0);
        for (IRenderer childRenderer : overflowRendererMultipleChildren.getChildRenderers()) {
            stringBuilder.append(((TextRenderer) childRenderer).text.toString());
        }
        String overflowTextMultipleChildren = stringBuilder.toString();

        Assertions.assertEquals(splitTextOneChild, splitTextMultipleChildren);
        Assertions.assertEquals(overflowTextOneChild, overflowTextMultipleChildren);
    }

    @Test
    public void wordWrappingUnavailableWithNoCalligraph() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(THAI_TEXT));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        float maxWidth = lineRenderer.getMinMaxWidth().getMaxWidth();

        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(maxWidth / 2, 100));
        lineRenderer.layout(new LayoutContext(layoutArea));

        Assertions.assertNull(((TextRenderer) lineRenderer.getChildRenderers().get(0)).getSpecialScriptsWordBreakPoints());
    }

    @Test
    public void nothingLayoutResult() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(THAI_TEXT));
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(3, 8, 10, 12, 15, 20, 23, 26, 28, 30, 36)));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(MinMaxWidthUtils.getInfWidth(), 10000));
        Rectangle occupiedArea = lineRenderer.layout(new LayoutContext(layoutArea)).getOccupiedArea().getBBox();

        LayoutArea decreasedHeightLayoutArea = new LayoutArea(1,
                new Rectangle(occupiedArea.getWidth(), occupiedArea.getHeight() - 1));
        LayoutResult nothingExpected = lineRenderer.layout(new LayoutContext(decreasedHeightLayoutArea));

        Assertions.assertEquals(LayoutResult.NOTHING, nothingExpected.getStatus());
    }

    @Test
    public void resetTextSequenceLayoutResultsBecauseOfNonTextRenderer() {
        Map<Integer, LayoutResult> textRendererLayoutResults = new HashMap<Integer, LayoutResult>();
        TextLayoutResult res = new TextLayoutResult(LayoutResult.NOTHING,
                new LayoutArea(0, new Rectangle(0, 0, 10, 10)), null, null, null);

        textRendererLayoutResults.put(0,  res);

        TabRenderer tabRenderer = new TabRenderer(new Tab());

        MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper =
                new MinMaxWidthOfTextRendererSequenceHelper(0f, 0f, false);
        AbstractWidthHandler widthHandler = new MaxSumWidthHandler(new MinMaxWidth());

        TextSequenceWordWrapping.resetTextSequenceIfItEnded(textRendererLayoutResults, false, tabRenderer, 1,
                minMaxWidthOfTextRendererSequenceHelper, false, widthHandler);
        Assertions.assertTrue(textRendererLayoutResults.isEmpty());
    }

    @Test
    public void resetTextSequenceLayoutResultsBecauseOfFloatingRenderer() {
        Map<Integer, LayoutResult> textRendererLayoutResults = new HashMap<Integer, LayoutResult>();
        TextLayoutResult res = new TextLayoutResult(LayoutResult.NOTHING,
                new LayoutArea(0, new Rectangle(0, 0, 10, 10)), null, null, null);

        int childPosAlreadyAdded = 0;
        textRendererLayoutResults.put(childPosAlreadyAdded,  res);

        Text text = new Text("float");
        text.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        TextRenderer tabRenderer = new TextRenderer(text);

        MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper =
                new MinMaxWidthOfTextRendererSequenceHelper(0f, 0f, false);
        AbstractWidthHandler widthHandler = new MaxSumWidthHandler(new MinMaxWidth());

        int childPosDuringResetAttempt = 1;
        TextSequenceWordWrapping
                .resetTextSequenceIfItEnded(textRendererLayoutResults, false, tabRenderer, childPosDuringResetAttempt,
                minMaxWidthOfTextRendererSequenceHelper, true, widthHandler);
        Assertions.assertTrue(textRendererLayoutResults.isEmpty());
    }

    @Test
    public void updateSpecialScriptLayoutResultsNonTextRenderer() {
        Map<Integer, LayoutResult> textRendererLayoutResults = new HashMap<Integer, LayoutResult>();

        Tab tab = new Tab();
        TabRenderer tabRenderer = new TabRenderer(tab);

        int childPosNotToBeAdded = 1;
        TextSequenceWordWrapping
                .updateTextSequenceLayoutResults(textRendererLayoutResults, true, tabRenderer, childPosNotToBeAdded,
                new LayoutResult(LayoutResult.FULL, new LayoutArea(1, new Rectangle(10, 10)), null, null, null));
        Assertions.assertTrue(textRendererLayoutResults.isEmpty());
    }

    @Test
    public void resetSpecialScriptTextSequenceBecauseOfTextRendererWithNoSpecialScripts() {
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();
        LayoutResult res = new LayoutResult(LayoutResult.NOTHING,
                new LayoutArea(0, new Rectangle(0, 0, 10, 10)), null, null);
        specialScriptLayoutResults.put(0,  res);

        TextRenderer textRenderer = new TextRenderer(new Text("whatever"));

        MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper =
                new MinMaxWidthOfTextRendererSequenceHelper(0f, 0f, false);
        AbstractWidthHandler widthHandler = new MaxSumWidthHandler(new MinMaxWidth());

        TextSequenceWordWrapping.resetTextSequenceIfItEnded(specialScriptLayoutResults, true, textRenderer, 1,
                minMaxWidthOfTextRendererSequenceHelper, true, widthHandler);
        Assertions.assertTrue(specialScriptLayoutResults.isEmpty());
    }

    @Test
    public void updateSpecialScriptLayoutResultsTextRendererWithNoSpecialScripts() {
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();

        TextRenderer textRenderer = new TextRenderer(new Text("whatever"));
        LayoutResult res = new LayoutResult(LayoutResult.NOTHING,
                new LayoutArea(0, new Rectangle(0, 0, 10, 10)), null, null);

        TextSequenceWordWrapping.updateTextSequenceLayoutResults(specialScriptLayoutResults, true, textRenderer, 1, res);
        Assertions.assertTrue(specialScriptLayoutResults.isEmpty());
    }

    @Test
    public void notResetSpecialScriptTextSequenceBecauseOfTextRendererWithSpecialScripts() {
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();
        LayoutResult res = new LayoutResult(LayoutResult.NOTHING,
                new LayoutArea(0, new Rectangle(0, 0, 10, 10)), null, null);
        int firstKey = 0;
        specialScriptLayoutResults.put(firstKey,  res);

        TextRenderer textRenderer = new TextRenderer(new Text("whatever"));
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Collections.singletonList(-1)));

        MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper =
                new MinMaxWidthOfTextRendererSequenceHelper(0f, 0f, false);
        AbstractWidthHandler widthHandler = new MaxSumWidthHandler(new MinMaxWidth());

        int secondKey = firstKey + 1;
        TextSequenceWordWrapping.resetTextSequenceIfItEnded(specialScriptLayoutResults, true, textRenderer, secondKey,
                minMaxWidthOfTextRendererSequenceHelper, true, widthHandler);
        Assertions.assertEquals(1, specialScriptLayoutResults.size());
        Assertions.assertTrue(specialScriptLayoutResults.containsKey(firstKey));
    }

    @Test
    public void updateSpecialScriptLayoutResultsTextRendererWithSpecialScripts() {
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<Integer, LayoutResult>();
        LayoutResult res = new LayoutResult(LayoutResult.NOTHING,
                new LayoutArea(0, new Rectangle(0, 0, 10, 10)), null, null);
        int firstKey = 0;
        specialScriptLayoutResults.put(firstKey,  res);

        TextRenderer textRenderer = new TextRenderer(new Text("whatever"));
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Collections.singletonList(-1)));

        int secondKey = firstKey + 1;
        TextSequenceWordWrapping
                .updateTextSequenceLayoutResults(specialScriptLayoutResults, true, textRenderer, secondKey, res);
        Assertions.assertTrue(specialScriptLayoutResults.containsKey(firstKey));
        Assertions.assertTrue(specialScriptLayoutResults.containsKey(secondKey));
        Assertions.assertEquals(2, specialScriptLayoutResults.size());
    }

    @Test
    public void curWidthZeroDecrement() {
        int oldNewChildPos = 1;
        float decrement = TextSequenceWordWrapping
                .getCurWidthRelayoutedTextSequenceDecrement(oldNewChildPos, oldNewChildPos,
                new HashMap<Integer, LayoutResult>());
        Assertions.assertEquals(0.0f, decrement, 0.0001);
    }

    @Test
    public void curWidthLayoutResultNothing() {
        float widthOfNewNothingResult = 500;
        LayoutArea occupiedArea = new LayoutArea(1, new Rectangle(0, 0, widthOfNewNothingResult, 0));
        LayoutResult oldResult = new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);

        float simpleWidth = 200;
        LayoutResult simpleDecrement = new LayoutResult(LayoutResult.FULL,
                new LayoutArea(1, new Rectangle(0, 0, simpleWidth, 0)), null, null);
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<>();
        specialScriptLayoutResults.put(0, oldResult);
        // leave specialScriptLayoutResults.get(1) null, as if childRenderers.get(1) is floating
        specialScriptLayoutResults.put(2, simpleDecrement);
        float decrement = TextSequenceWordWrapping
                .getCurWidthRelayoutedTextSequenceDecrement(3, 0, specialScriptLayoutResults);
        Assertions.assertEquals(widthOfNewNothingResult + simpleWidth, decrement, 0.00001);
    }

    @Test
    public void curWidthLayoutResultPartial() {
        float widthOfNewPartialResult = 500;
        LayoutArea oldOccupiedArea = new LayoutArea(1, new Rectangle(0, 0, widthOfNewPartialResult, 0));
        LayoutResult oldResult = new LayoutResult(LayoutResult.FULL, oldOccupiedArea, null, null);

        float simpleWidth = 200;
        LayoutResult simpleDecrement = new LayoutResult(LayoutResult.FULL,
                new LayoutArea(1, new Rectangle(0, 0, simpleWidth, 0)), null, null);
        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<>();
        specialScriptLayoutResults.put(0, oldResult);
        // leave specialScriptLayoutResults.get(1) null, as if childRenderers.get(1) is floating
        specialScriptLayoutResults.put(2, simpleDecrement);
        float decrement = TextSequenceWordWrapping
                .getCurWidthRelayoutedTextSequenceDecrement(3, 0, specialScriptLayoutResults);
        Assertions.assertEquals(widthOfNewPartialResult + simpleWidth, decrement, 0.00001);
    }

    @Test
    public void possibleBreakWithinActualText() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());

        TextRenderer textRenderer = new TextRenderer(new Text(""));

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(new Glyph(629, 378, new char[]{'\u17c3'}));
        glyphs.add(new Glyph(578, 756, new char[]{'\u1790'}));
        glyphs.add(new Glyph(386, 0, new char[]{'\u17d2', '\u1784'}));
        glyphs.add(new Glyph(627, 378, new char[]{'\u17c1'}));
        glyphs.add(new Glyph(581, 756, new char[]{'\u1793'}));
        glyphs.add(new Glyph(633, 512, new char[]{'\u17c7'}));
        GlyphLine glyphLine = new GlyphLine(glyphs);
        glyphLine.setActualText(0, 3, "\u1790\u17d2\u1784\u17c3");
        glyphLine.setActualText(3, 6, "\u1793\u17c1\u17c7");

        textRenderer.setText(glyphLine, PdfFontFactory.createFont(KHMER_FONT, PdfEncodings.IDENTITY_H));

        lineRenderer.addChild(textRenderer);
        List<Integer> possibleBreakPoints = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        TextSequenceWordWrapping.distributePossibleBreakPointsOverSequentialTextRenderers(
                lineRenderer, 0, 1, possibleBreakPoints, new ArrayList<Integer>());
        List<Integer> distributed = ((TextRenderer) lineRenderer.getChildRenderers().get(0))
                .getSpecialScriptsWordBreakPoints();
        Assertions.assertEquals(new ArrayList<Integer>(Arrays.asList(3, 6)), distributed);
    }

    @Test
    public void trimFirstOnePossibleBreak() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        PdfFont pdfFont = PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H);

        // " อากาศ"
        String thai = "\u0020" + THAI_WORD;
        TextRenderer textRenderer = new TextRenderer(new Text(""));
        textRenderer.setProperty(Property.FONT, pdfFont);
        textRenderer.setText(thai);
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(1)));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        lineRenderer.trimFirst();
        TextRenderer childTextRenderer = (TextRenderer) lineRenderer.getChildRenderers().get(0);
        Assertions.assertNotNull(childTextRenderer.getSpecialScriptsWordBreakPoints());
        Assertions.assertEquals(1, childTextRenderer.getSpecialScriptsWordBreakPoints().size());
        Assertions.assertEquals(-1, (int) childTextRenderer.getSpecialScriptsWordBreakPoints().get(0));
    }

    @Test
    public void unfittingSequenceWithPrecedingTextRendererContainingNoSpecialScripts() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer thaiTextRenderer = new TextRenderer(new Text(""));
        thaiTextRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        thaiTextRenderer.setText(THAI_WORD);
        thaiTextRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));

        TextRenderer nonThaiTextRenderer = new TextRenderer(new Text("."));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(nonThaiTextRenderer);
        lineRenderer.addChild(thaiTextRenderer);

        SpecialScriptsContainingSequenceStatus status =
                TextSequenceWordWrapping.getSpecialScriptsContainingSequenceStatus(lineRenderer, 1);
        Assertions.assertEquals(TextSequenceWordWrapping.SpecialScriptsContainingSequenceStatus
                .MOVE_SEQUENCE_CONTAINING_SPECIAL_SCRIPTS_ON_NEXT_LINE, status);
    }

    @Test
    public void unfittingSequenceWithPrecedingInlineBlockRenderer() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer thaiTextRenderer = new TextRenderer(new Text(""));
        thaiTextRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        thaiTextRenderer.setText(THAI_WORD);
        thaiTextRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));

        TableRenderer inlineBlock = new TableRenderer(new Table(3));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(inlineBlock);
        lineRenderer.addChild(thaiTextRenderer);

        SpecialScriptsContainingSequenceStatus status =
                TextSequenceWordWrapping.getSpecialScriptsContainingSequenceStatus(lineRenderer, 1);
        Assertions.assertEquals(TextSequenceWordWrapping.SpecialScriptsContainingSequenceStatus
                .MOVE_SEQUENCE_CONTAINING_SPECIAL_SCRIPTS_ON_NEXT_LINE, status);
    }

    @Test
    public void unfittingSingleTextRendererContainingSpecialScripts() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer thaiTextRenderer = new TextRenderer(new Text(""));
        thaiTextRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        thaiTextRenderer.setText(THAI_WORD);
        thaiTextRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(thaiTextRenderer);

        SpecialScriptsContainingSequenceStatus status =
                TextSequenceWordWrapping.getSpecialScriptsContainingSequenceStatus(lineRenderer, 0);
        Assertions.assertEquals(TextSequenceWordWrapping.SpecialScriptsContainingSequenceStatus.FORCED_SPLIT, status);
    }

    @Test
    public void overflowXSingleWordSingleRenderer() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRenderer = new TextRenderer(new Text(""));
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRenderer.setText(THAI_WORD);
        textRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());
        lineRenderer.addChild(textRenderer);

        float minWidth = lineRenderer.getMinMaxWidth().getMinWidth();

        lineRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(minWidth / 2, 100));
        LayoutResult layoutResult = lineRenderer.layout(new LayoutContext(layoutArea));

        Assertions.assertEquals(LayoutResult.FULL, layoutResult.getStatus());
    }

    @Test
    public void overflowXSingleWordOneGlyphPerTextRenderer() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        TextRenderer textRendererForMinMaxWidth = new TextRenderer(new Text(THAI_WORD));
        textRendererForMinMaxWidth.setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
        textRendererForMinMaxWidth.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>(Arrays.asList(5)));
        textRendererForMinMaxWidth.setParent(document.getRenderer());
        float minWidth = textRendererForMinMaxWidth.getMinMaxWidth().getMinWidth();

        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(document.getRenderer());

        TextRenderer[] textRenderers = new TextRenderer[THAI_WORD.length()];
        for (int i = 0; i < textRenderers.length; i++) {
            textRenderers[i] = new TextRenderer(new Text(""));
            textRenderers[i].setProperty(Property.FONT, PdfFontFactory.createFont(THAI_FONT, PdfEncodings.IDENTITY_H));
            textRenderers[i].setText(new String(new char[] {THAI_WORD.charAt(i)}));
            textRenderers[i].setSpecialScriptsWordBreakPoints(
                    new ArrayList<Integer>(Arrays.asList(i + 1 != textRenderers.length ? -1 : 1)));
            lineRenderer.addChild(textRenderers[i]);
        }

        lineRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        LayoutArea layoutArea = new LayoutArea(1, new Rectangle(minWidth / 2, 100));
        LayoutResult layoutResult = lineRenderer.layout(new LayoutContext(layoutArea));

        Assertions.assertEquals(LayoutResult.FULL, layoutResult.getStatus());
    }
}
