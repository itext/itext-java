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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class PdfAPageTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAPageTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED)})
    public void checkThatFlushingPreventedWhenAddingElementToDocument() throws IOException {
        // Expected log message that page flushing was not performed
        String outPdf = destinationFolder + "checkThatFlushingPreventedWhenAddingElementToDocument.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformance.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        Document document = new Document(pdfDoc, PageSize.A4);
        for (int i = 1; i < pageCount; i++) {
            // Adding a area break causes a new page to be added and an attempt to flush the page will occur,
            // but flushing these pages will be prevented due to a condition added to the PdfAPage#flush method
            document.add(new AreaBreak());
        }

        // Before closing document have 3 pages, but no one call of end page event
        Assertions.assertEquals(pageCount, document.getPdfDocument().getNumberOfPages());
        Assertions.assertEquals(0, eventHandler.getCounter());

        document.close();

        // During the closing event was called on each document page
        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED)})
    public void checkThatFlushingPreventedWithFalseFlushResourcesContentStreams() throws IOException {
        // Expected log message that page flushing was not performed
        String outPdf = destinationFolder + "checkThatFlushingPreventedWithFalseFlushResourcesContentStreams.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformance.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            pdfDoc.addNewPage().flush(false);
        }

        Assertions.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assertions.assertEquals(0, eventHandler.getCounter());

        pdfDoc.close();

        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkFlushingWhenPdfDocumentIsClosing() throws IOException {
        String outPdf = destinationFolder + "checkFlushingWhenPdfDocumentIsClosing.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformance.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            pdfDoc.addNewPage();
        }

        Assertions.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assertions.assertEquals(0, eventHandler.getCounter());

        pdfDoc.close();

        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkFlushingWithTrueFlushResourcesContentStreams() throws IOException {
        String outPdf = destinationFolder + "checkFlushingWithTrueFlushResourcesContentStreams.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformance.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            pdfDoc.addNewPage().flush(true);
        }

        Assertions.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        pdfDoc.close();

        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkFlushingOfCheckedPage() throws IOException {
        String outPdf = destinationFolder + "checkFlushingOfCheckedPage.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformance.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.checker.checkSinglePage(page);
            page.flush(false);
        }

        Assertions.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        pdfDoc.close();

        Assertions.assertEquals(pageCount, eventHandler.getCounter());

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    static class EndPageEventHandler extends AbstractPdfDocumentEventHandler {
        private int counter = 0;

        EndPageEventHandler() {
        }

        public int getCounter() {
            return counter;
        }

        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            counter++;
        }
    }
}
