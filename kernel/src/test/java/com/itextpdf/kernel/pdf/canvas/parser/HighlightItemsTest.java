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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;


@Tag("IntegrationTest")
public class HighlightItemsTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/HighlightItemsTest/";
    private static final String outputPath = "./target/test/com/itextpdf/kernel/parser/HighlightItemsTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(outputPath);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(outputPath);
    }

    @Test
    public void highlightPage229() throws IOException, InterruptedException {
        String input = sourceFolder + "page229.pdf";
        String output = outputPath + "page229.pdf";
        String cmp = sourceFolder + "cmp_page229.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, false);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightCharactersPage229() throws IOException, InterruptedException {
        String input = sourceFolder + "page229.pdf";
        String output = outputPath + "page229_characters.pdf";
        String cmp = sourceFolder + "cmp_page229_characters.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightIsoTc171() throws IOException, InterruptedException {
        String input = sourceFolder + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        String output = outputPath + "SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        String cmp = sourceFolder + "cmp_ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, false);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightCharactersIsoTc171() throws IOException, InterruptedException {
        String input = sourceFolder + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        String output = outputPath + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda_characters.pdf";
        String cmp = sourceFolder + "cmp_ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda_characters.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightHeaderFooter() throws IOException, InterruptedException {
        String input = sourceFolder + "HeaderFooter.pdf";
        String output = outputPath + "HeaderFooter.pdf";
        String cmp = sourceFolder + "cmp_HeaderFooter.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, false);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightCharactersHeaderFooter() throws IOException, InterruptedException {
        String input = sourceFolder + "HeaderFooter.pdf";
        String output = outputPath + "HeaderFooter_characters.pdf";
        String cmp = sourceFolder + "cmp_HeaderFooter_characters.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightReferencePage2Test() throws IOException, InterruptedException {
        String input = sourceFolder + "reference_page2.pdf";
        String output = outputPath + "reference_page2_characters.pdf";
        String cmp = sourceFolder + "cmp_reference_page2_characters.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightReferencePage832Test() throws IOException, InterruptedException {
        String input = sourceFolder + "reference_page832.pdf";
        String output = outputPath + "reference_page832_characters.pdf";
        String cmp = sourceFolder + "cmp_reference_page832_characters.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightReferencePage604Test() throws IOException, InterruptedException {
        String input = sourceFolder + "reference_page604.pdf";
        String output = outputPath + "reference_page604_characters.pdf";
        String cmp = sourceFolder + "cmp_reference_page604_characters.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightNotDefTest() throws IOException, InterruptedException {
        String input = sourceFolder + "notdefWidth.pdf";
        String output = outputPath + "notdefWidth_highlighted.pdf";
        String cmp = sourceFolder + "cmp_notdefWidth.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, false);
        Assertions.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void fillStandardEncodingType1NoDescriptorTest() throws IOException, InterruptedException {
        String input = sourceFolder + "fillStandardEncodingType1NoDescriptorTest.pdf";
        String output = outputPath + "fillStandardEncodingType1NoDescriptorTest.pdf";
        String cmp = sourceFolder + "cmp_fillStandardEncodingType1NoDescriptorTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void fillStandardEncodingTrueTypeFontDescriptorTest() throws IOException, InterruptedException {
        String input = sourceFolder + "fillStandardEncodingTrueTypeFontDescriptorTest.pdf";
        String output = outputPath + "fillStandardEncodingTrueTypeFontDescriptorTest.pdf";
        String cmp = sourceFolder + "cmp_fillStandardEncodingTrueTypeFontDescriptorTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void fillStandardEncodingType1FontDescriptorTest() throws IOException, InterruptedException {
        String input = sourceFolder + "fillStandardEncodingType1FontDescriptorTest.pdf";
        String output = outputPath + "fillStandardEncodingType1FontDescriptorTest.pdf";
        String cmp = sourceFolder + "cmp_fillStandardEncodingType1FontDescriptorTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    // As the negative ascent is not covered by pdf specification in details,
    // we work with it as usual (which results with not very beautiful view).
    public void incorrectAscentFontDescriptorTest() throws IOException, InterruptedException {
        String input = sourceFolder + "incorrectAscentFontDescriptorTest.pdf";
        String output = outputPath + "incorrectAscentFontDescriptorTest.pdf";
        String cmp = sourceFolder + "cmp_incorrectAscentFontDescriptorTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void incorrectDescentFontDescriptorTest() throws IOException, InterruptedException {
        String input = sourceFolder + "incorrectDescentFontDescriptorTest.pdf";
        String output = outputPath + "incorrectDescentFontDescriptorTest.pdf";
        String cmp = sourceFolder + "cmp_incorrectDescentFontDescriptorTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void fontDictWidthArrayMissingWidthTest() throws IOException, InterruptedException {
        String input = sourceFolder + "fontDictWidthArrayMissingWidthTest.pdf";
        String output = outputPath + "fontDictWidthArrayMissingWidthTest.pdf";
        String cmp = sourceFolder + "cmp_fontDictWidthArrayMissingWidthTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void trueTypeCIDFontWithDWWithoutProperWidthGlyphTest() throws IOException, InterruptedException {
        String input = sourceFolder + "trueTypeCIDFontWithDWWithoutProperWidthGlyphTest.pdf";
        String output = outputPath + "trueTypeCIDFontWithDWWithoutProperWidthGlyphTest.pdf";
        String cmp = sourceFolder + "cmp_trueTypeCIDFontWithDWWithoutProperWidthGlyphTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void doubleMappingSimpleFontTest() throws IOException, InterruptedException {
        String input = sourceFolder + "doubleMappingSimpleFont.pdf";
        String output = outputPath + "doubleMappingSimpleFont.pdf";
        String cmp = sourceFolder + "cmp_doubleMappingSimpleFont.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, false);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    @Test
    public void doubleMappingSimpleFontTest2() throws IOException, InterruptedException {
        String input = sourceFolder + "doubleMappingSimpleFont2.pdf";
        String output = outputPath + "doubleMappingSimpleFont2.pdf";
        String cmp = sourceFolder + "cmp_doubleMappingSimpleFont2.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        parseAndHighlight(input, writer, true);
        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, outputPath));
    }

    private void parseAndHighlight(String input, PdfWriter writer, boolean singleCharacters) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), writer);

        MyEventListener myEventListener = singleCharacters ? new MyCharacterEventListener() : new MyEventListener();
        PdfDocumentContentParser parser = new PdfDocumentContentParser(pdfDocument);
        for (int pageNum = 1; pageNum <= pdfDocument.getNumberOfPages(); pageNum++) {
            parser.processContent(pageNum, myEventListener);
            List<Rectangle> rectangles = myEventListener.getRectangles();
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
            canvas.setLineWidth(0.5f);
            canvas.setStrokeColor(ColorConstants.RED);
            for (Rectangle rectangle : rectangles) {
                canvas.rectangle(rectangle);
                canvas.stroke();
            }
            myEventListener.clear();
        }

        pdfDocument.close();
    }

    static class MyEventListener implements IEventListener {
        private List<Rectangle> rectangles = new ArrayList<>();

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_TEXT) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                Vector startPoint = renderInfo.getDescentLine().getStartPoint();
                Vector endPoint = renderInfo.getAscentLine().getEndPoint();
                float x1 = Math.min(startPoint.get(0), endPoint.get(0));
                float x2 = Math.max(startPoint.get(0), endPoint.get(0));
                float y1 = Math.min(startPoint.get(1), endPoint.get(1));
                float y2 = Math.max(startPoint.get(1), endPoint.get(1));
                rectangles.add(new Rectangle(x1, y1, x2 - x1, y2 - y1));
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }

        public List<Rectangle> getRectangles() {
            return rectangles;
        }

        public void clear() {
            rectangles.clear();
        }
    }

    static class MyCharacterEventListener extends MyEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_TEXT) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                for (TextRenderInfo tri : renderInfo.getCharacterRenderInfos()) {
                    super.eventOccurred(tri, type);
                }
            }
        }
    }

}
