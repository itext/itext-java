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
import com.itextpdf.io.source.ByteArrayOutputStream;
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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Set;

@Category(IntegrationTest.class)
public class PdfCanvasProcessorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfCanvasProcessorTest/";

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void contentStreamProcessorTest() throws IOException {

        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "yaxiststar.pdf"), new PdfWriter(new ByteArrayOutputStream()));

        for (int i = 1; i <= document.getNumberOfPages(); ++i) {
            PdfPage page = document.getPage(i);

            PdfCanvasProcessor processor = new PdfCanvasProcessor(new IEventListener() {
                public void eventOccurred(IEventData data, EventType type) {
                    switch (type) {
                        case BEGIN_TEXT:
                            System.out.println("-------- BEGIN TEXT CALLED ---------");
                            System.out.println("------------------------------------");
                            break;

                        case RENDER_TEXT:
                            System.out.println("-------- RENDER TEXT CALLED --------");

                            TextRenderInfo renderInfo = (TextRenderInfo) data;
                            System.out.println("String: " + renderInfo.getPdfString());

                            System.out.println("------------------------------------");
                            break;

                        case END_TEXT:
                            System.out.println("-------- END TEXT CALLED -----------");
                            System.out.println("------------------------------------");
                            break;

                        case RENDER_IMAGE:
                            System.out.println("-------- RENDER IMAGE CALLED---------");

                            ImageRenderInfo renderInfo1 = (ImageRenderInfo) data;
                            System.out.println("Image: " + renderInfo1.getImage().getPdfObject());

                            System.out.println("------------------------------------");
                            break;

                        case RENDER_PATH:
                            System.out.println("-------- RENDER PATH CALLED --------");

                            PathRenderInfo renderinfo2 = (PathRenderInfo) data;
                            System.out.println("Path: " + renderinfo2.getPath());

                            System.out.println("------------------------------------");
                            break;

                        case CLIP_PATH_CHANGED:
                            System.out.println("-------- CLIPPING PATH CALLED-------");

                            ClippingPathInfo renderinfo3 = (ClippingPathInfo) data;
                            System.out.println("Clipping path: " + renderinfo3.getClippingPath());

                            System.out.println("------------------------------------");
                            break;
                    }
                }

                public Set<EventType> getSupportedEvents() {
                    return null;
                }
            });

            processor.processPageContent(page);

        }
    }

    @Test
    public void testClosingEmptyPath() throws IOException {
        String fileName = "closingEmptyPath.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + fileName));
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new NoOpEventListener());
        // Assert than no exception is thrown when an empty path is handled
        processor.processPageContent(document.getPage(1));
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

}
