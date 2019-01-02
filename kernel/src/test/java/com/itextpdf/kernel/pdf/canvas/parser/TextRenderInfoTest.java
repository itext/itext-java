/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Category(IntegrationTest.class)
public class TextRenderInfoTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/TextRenderInfoTest/";

    public static final int FIRST_PAGE = 1;
    public static final int FIRST_ELEMENT_INDEX = 0;

    @Test
    public void testCharacterRenderInfos() throws Exception {
        PdfCanvasProcessor parser = new PdfCanvasProcessor(new CharacterPositionEventListener());
        parser.processPageContent(new PdfDocument(new PdfReader(sourceFolder + "simple_text.pdf")).getPage(FIRST_PAGE));
    }

    /**
     * Test introduced to exclude a bug related to a Unicode quirk for
     * Japanese. TextRenderInfo threw an AIOOBE for some characters.
     */
    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.COULD_NOT_FIND_GLYPH_WITH_CODE, count = 2)})
    public void testUnicodeEmptyString() throws Exception {
        StringBuilder sb = new StringBuilder();
        String inFile = "japanese_text.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + inFile));
        ITextExtractionStrategy start = new SimpleTextExtractionStrategy();

        sb.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(FIRST_PAGE), start));

        String result = sb.substring(0, sb.toString().indexOf("\n"));
        String origText =
                "\u76f4\u8fd1\u306e\u0053\uff06\u0050\u0035\u0030\u0030"
                        + "\u914d\u5f53\u8cb4\u65cf\u6307\u6570\u306e\u30d1\u30d5"
                        + "\u30a9\u30fc\u30de\u30f3\u30b9\u306f\u0053\uff06\u0050"
                        + "\u0035\u0030\u0030\u6307\u6570\u3092\u4e0a\u56de\u308b";
        Assert.assertEquals(origText, result);
    }

    @Test
    public void testType3FontWidth() throws Exception {
        String inFile = "type3font_text.pdf";
        LineSegment origLineSegment = new LineSegment(new Vector(20.3246f, 769.4974f, 1.0f), new Vector(151.22923f, 769.4974f, 1.0f));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + inFile));
        TextPositionEventListener renderListener = new TextPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(renderListener);

        processor.processPageContent(pdfDocument.getPage(FIRST_PAGE));

        Assert.assertEquals(renderListener.getLineSegments().get(FIRST_ELEMENT_INDEX).getStartPoint().get(FIRST_ELEMENT_INDEX),
                origLineSegment.getStartPoint().get(FIRST_ELEMENT_INDEX), 1 / 2f);

        Assert.assertEquals(renderListener.getLineSegments().get(FIRST_ELEMENT_INDEX).getEndPoint().get(FIRST_ELEMENT_INDEX),
                origLineSegment.getEndPoint().get(FIRST_ELEMENT_INDEX), 1 / 2f);
    }


    private static class TextPositionEventListener implements IEventListener {
        List<LineSegment> lineSegments = new ArrayList<>();

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                lineSegments.add(((TextRenderInfo) data).getBaseline());
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }

        public List<LineSegment> getLineSegments() {
            return lineSegments;
        }
    }

    private static class CharacterPositionEventListener implements ITextExtractionStrategy {

        @Override
        public String getResultantText() {
            return null;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                List<TextRenderInfo> subs = renderInfo.getCharacterRenderInfos();
                TextRenderInfo previousCharInfo = subs.get(0);

                for (int i = 1; i < subs.size(); i++) {
                    TextRenderInfo charInfo = subs.get(i);
                    Vector previousEndPoint = previousCharInfo.getBaseline().getEndPoint();
                    Vector currentStartPoint = charInfo.getBaseline().getStartPoint();
                    assertVectorsEqual(charInfo.getText(), previousEndPoint, currentStartPoint);
                    previousCharInfo = charInfo;
                }
            }
        }

        private void assertVectorsEqual(String message, Vector v1, Vector v2) {
            Assert.assertEquals(message, v1.get(0), v2.get(0), 1 / 72f);
            Assert.assertEquals(message, v1.get(1), v2.get(1), 1 / 72f);
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }
    }

}
