/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DocumentSerializationTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/DocumentSerializationTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/DocumentSerializationTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    private static final String DEBUG_BYTES_METHOD_NAME = "getDebugBytes";
    private static final String SERIALIZED_BYTES_METHOD_NAME = "getSerializedBytes";
    private static Method getDebugBytesMethod;
    private static Method getSerializedBytesMethod;

    static {
        try {
            getDebugBytesMethod = PdfWriter.class.getDeclaredMethod(DEBUG_BYTES_METHOD_NAME);
            getDebugBytesMethod.setAccessible(true);
            getSerializedBytesMethod = PdfDocument.class.getDeclaredMethod(SERIALIZED_BYTES_METHOD_NAME);
            getSerializedBytesMethod.setAccessible(true);
        } catch (NoSuchMethodException ignored) {
        }
    }

    @Test
    public void cloneDocumentTest01() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(true, false, false);
    }

    @Test
    public void cloneDocumentTest02() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(false, false, false);
    }

    @Test
    public void cloneDocumentTest03() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(true, true, false);
    }

    @Test
    public void cloneDocumentTest04() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(false, true, false);
    }

    @Test
    public void cloneDocumentTest05() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(true, false, true);
    }

    @Test
    public void cloneDocumentTest06() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(false, false, true);
    }

    @Test
    public void cloneDocumentTest07() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(true, true, true);
    }

    @Test
    public void cloneDocumentTest08() throws IOException, InterruptedException,
            InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        cloneDocumentTest(false, true, true);
    }

    @Test
    public void cloneDocumentNameTreeTest() throws IOException, InvocationTargetException, IllegalAccessException {
        PdfDocument pdf = new PdfDocument(new PdfReader(SOURCE_FOLDER + "cmp_Listing_06_19_FillDataSheet.pdf"),
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().useDebugMode()));
        pdf.getCatalog().getNameTree(PdfName.Dests);
        assertSerializable(pdf);
    }

    @Test
    public void cloneDocumentEventTest() throws IOException, InvocationTargetException, IllegalAccessException {
        PdfDocument pdf = new PdfDocument(new PdfReader(SOURCE_FOLDER + "cmp_Listing_06_19_FillDataSheet.pdf"),
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().useDebugMode()));

        final float x = 300;
        final float y = 25;

        final PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        IEventHandler event = new IEventHandler() {
            public void handleEvent(Event event) {
                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfDocument pdf = docEvent.getDocument();
                PdfPage page = docEvent.getPage();
                int pageNumber = pdf.getPageNumber(page);

                PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
                pdfCanvas
                        .beginText()
                        .setFontAndSize(font, 12)
                        .moveText(x, y)
                        .showText("Page ")
                        .showText(String.valueOf(pageNumber))
                        .showText(" of")
                        .endText();
                pdfCanvas.release();
            }
        };

        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, event);
        for (int i = 0; i < 10; i++) {
            pdf.addNewPage();
        }
        
        assertSerializable(pdf);
    }

    @Test
    public void deserializeNoVersionInfoTest() throws IOException, ReflectiveOperationException {
        String outFileName = DESTINATION_FOLDER + "deserializeNoVersionInfo.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties().useDebugMode()));
        pdfDocument.addNewPage();
        Field versionInfo = pdfDocument.getClass().getDeclaredField("versionInfo");
        versionInfo.setAccessible(true);
        versionInfo.set(pdfDocument, null);

        byte[] serializedPdfDocument;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(pdfDocument);
        serializedPdfDocument = baos.toByteArray();
        oos.close();

        PdfDocument deserializedPdfDocument = createDocumentFromSerializedBytes(serializedPdfDocument);
        deserializedPdfDocument.close();
        Assert.assertNotNull(deserializedPdfDocument);
    }

    private void cloneDocumentTest(boolean compreResults, boolean useStamperMode, boolean tagged) throws IOException,
            InterruptedException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        String outFileName = DESTINATION_FOLDER + "out.pdf";
        String copyFileName = DESTINATION_FOLDER + "copy.pdf";
        String tmpFileName = DESTINATION_FOLDER + "tmp.pdf";

        PdfDocument pdf = new PdfDocument(
                new PdfWriter(useStamperMode ? tmpFileName : outFileName, new WriterProperties().useDebugMode()));
        if (tagged) {
            pdf.setTagged();
        }

        PageSize ps = PageSize.A4;
        PdfPage page = pdf.addNewPage(ps);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.moveTo(10, 10).lineTo(50, 50);
        canvas.lineTo(100, 50).stroke();

        if (useStamperMode) {
            pdf.close();
            pdf = new PdfDocument(new PdfReader(tmpFileName),
                    new PdfWriter(outFileName, new WriterProperties().useDebugMode()));
        }

        pdf.addNewPage();

        PdfDocument doc = createDocumentFromSerializedBytes((byte[]) getSerializedBytesMethod.invoke(pdf));
        PdfWriter writer = doc.getWriter();
        writer.setCloseStream(true);

        doc.setCloseWriter(false);
        doc.close();
        byte[] documentCopyBytes = (byte[]) getDebugBytesMethod.invoke(writer);
        writer.close();

        pdf.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(documentCopyBytes);
        PdfReader reader = new PdfReader(bais);
        reader.setCloseStream(false);

        PdfDocument tempDoc = new PdfDocument(reader, new PdfWriter(copyFileName));
        tempDoc.close();

        if (compreResults) {
            Assert.assertNull(
                    new CompareTool().compareByContent(outFileName, copyFileName, DESTINATION_FOLDER));
        }
    }

    private PdfDocument createDocumentFromSerializedBytes(byte[] bytes) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        PdfDocument doc = null;
        try {
            doc = (PdfDocument) new ObjectInputStream(bais).readObject();
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

    private void assertSerializable(PdfDocument document) throws InvocationTargetException, IllegalAccessException {
        byte[] bytes = (byte[]) getSerializedBytesMethod.invoke(document);
        document.close();
        Assert.assertNotNull(bytes);
    }
}
