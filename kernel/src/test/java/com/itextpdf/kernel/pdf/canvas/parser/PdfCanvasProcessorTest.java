/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.data.ClippingPathInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Set;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfCanvasProcessorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfCanvasProcessorTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void contentStreamProcessorTest() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "tableWithImageAndText.pdf"), new PdfWriter(new ByteArrayOutputStream()));

        StringBuilder pageEventsLog = new StringBuilder();
        for (int i = 1; i <= document.getNumberOfPages(); ++i) {
            PdfPage page = document.getPage(i);

            PdfCanvasProcessor processor = new PdfCanvasProcessor(new RecordEveryHighLevelEventListener(pageEventsLog));

            processor.processPageContent(page);

        }

        byte[] logBytes = Files.readAllBytes(Paths.get(sourceFolder + "contentStreamProcessorTest_events_log.dat"));
        String expectedPageEventsLog = new String(logBytes, StandardCharsets.UTF_8);

        Assert.assertEquals(expectedPageEventsLog, pageEventsLog.toString());
    }

    @Test
    public void testClosingEmptyPath() throws IOException {
        String fileName = "closingEmptyPath.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + fileName));
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new NoOpEventListener());
        // Assert than no exception is thrown when an empty path is handled
        processor.processPageContent(document.getPage(1));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FAILED_TO_PROCESS_A_TRANSFORMATION_MATRIX, count = 1))
    public void testNoninvertibleMatrix() throws IOException {
        String fileName = "noninvertibleMatrix.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + fileName));

        LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(strategy);
        PdfPage page = pdfDocument.getFirstPage();
        processor.processPageContent(page);

        String resultantText = strategy.getResultantText();
        pdfDocument.close();

        Assert.assertEquals("Hello World!\nHello World!\nHello World!\nHello World! Hello World! Hello World!", resultantText);
    }

    @Test
    @Ignore("DEVSIX-3608: this test currently throws StackOverflowError, which cannot be caught in .NET")
    public void parseCircularReferencesInResourcesTest() throws IOException {
        junitExpectedException.expect(StackOverflowError.class);

        String fileName = "circularReferencesInResources.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + fileName));

        PdfCanvasProcessor processor = new PdfCanvasProcessor(new NoOpEventListener());
        PdfPage page = pdfDocument.getFirstPage();

        processor.processPageContent(page);

        pdfDocument.close();
    }

    private static class NoOpEventListener implements IEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null;
        }
    }

    private static class RecordEveryHighLevelEventListener implements IEventListener {
        private static final String END_EVENT_OCCURRENCE = "------------------------------------";
        private StringBuilder sb;

        RecordEveryHighLevelEventListener(StringBuilder outStream) {
            this.sb = outStream;
        }

        public void eventOccurred(IEventData data, EventType type) {
            switch (type) {
                case BEGIN_TEXT:
                    sb.append("-------- BEGIN TEXT ---------").append("\n");
                    sb.append(END_EVENT_OCCURRENCE).append("\n");
                    break;

                case RENDER_TEXT:
                    sb.append("-------- RENDER TEXT --------").append("\n");

                    TextRenderInfo renderInfo = (TextRenderInfo) data;
                    sb.append("String: ").append(renderInfo.getPdfString().toUnicodeString()).append("\n");

                    sb.append(END_EVENT_OCCURRENCE).append("\n");
                    break;

                case END_TEXT:
                    sb.append("-------- END TEXT -----------").append("\n");
                    sb.append(END_EVENT_OCCURRENCE).append("\n");
                    break;

                case RENDER_IMAGE:
                    sb.append("-------- RENDER IMAGE ---------").append("\n");

                    ImageRenderInfo imageRenderInfo = (ImageRenderInfo) data;
                    sb.append("Image: ").append(imageRenderInfo.getImageResourceName()).append("\n");

                    sb.append(END_EVENT_OCCURRENCE).append("\n");
                    break;

                case RENDER_PATH:
                    sb.append("-------- RENDER PATH --------").append("\n");

                    PathRenderInfo pathRenderInfo = (PathRenderInfo) data;
                    sb.append("Operation type: ").append(pathRenderInfo.getOperation()).append("\n");
                    sb.append("Num of subpaths: ").append(pathRenderInfo.getPath().getSubpaths().size()).append("\n");

                    sb.append(END_EVENT_OCCURRENCE).append("\n");
                    break;

                case CLIP_PATH_CHANGED:
                    sb.append("-------- CLIPPING PATH ------").append("\n");

                    ClippingPathInfo clippingPathRenderInfo = (ClippingPathInfo) data;
                    sb.append("Num of subpaths: ").append(clippingPathRenderInfo.getClippingPath().getSubpaths().size())
                            .append("\n");

                    sb.append(END_EVENT_OCCURRENCE).append("\n");
                    break;
            }
        }

        public Set<EventType> getSupportedEvents() {
            return null;
        }
    }
}
