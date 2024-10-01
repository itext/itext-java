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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
public class PdfStampingTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStampingTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStampingTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void stamping1() throws IOException {
        String filename1 = destinationFolder + "stamping1_1.pdf";
        String filename2 = destinationFolder + "stamping1_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setCreator("iText").setTitle("Empty iText Document");
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "stamping1_2.pdf");
        PdfDocument document = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary trailer = document.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfString creator = info.getAsString(PdfName.Creator);
        assertEquals("iText", creator.toString());
        byte[] bytes = document.getPage(1).getContentBytes();
        assertEquals("%Hello World\n", new String(bytes));
        String date = document.getDocumentInfo().getPdfObject().getAsString(PdfName.ModDate).getValue();
        Calendar cl = PdfDate.decode(date);
        double diff = DateTimeUtil.getUtcMillisFromEpoch(null) - DateTimeUtil.getUtcMillisFromEpoch(cl);
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        assertTrue(diff < 5000, message);
        document.close();
    }

    @Test
    public void stamping2() throws IOException {
        String filename1 = destinationFolder + "stamping2_1.pdf";
        String filename2 = destinationFolder + "stamping2_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "stamping2_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping3() throws IOException {
        String filename1 = destinationFolder + "stamping3_1.pdf";
        String filename2 = destinationFolder + "stamping3_2.pdf";

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        pdfDocument.close();
    }

    @Test
    public void stamping4() throws IOException {
        String filename1 = destinationFolder + "stamping4_1.pdf";
        String filename2 = destinationFolder + "stamping4_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2));
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        assertEquals(pageCount, pdfDocument.getNumberOfPages(), "Page count");
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping5() throws IOException {
        String filename1 = destinationFolder + "stamping5_1.pdf";
        String filename2 = destinationFolder + "stamping5_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        assertEquals(pageCount, pdfDocument.getNumberOfPages(), "Page count");
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping6() throws IOException {
        String filename1 = destinationFolder + "stamping6_1.pdf";
        String filename2 = destinationFolder + "stamping6_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true)));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2));
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        pdfDocument.close();
    }

    @Test
    public void stamping7() throws IOException {
        String filename1 = destinationFolder + "stamping7_1.pdf";
        String filename2 = destinationFolder + "stamping7_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        pdfDocument.close();
    }

    @Test
    public void stamping8() throws IOException {
        String filename1 = destinationFolder + "stamping8_1.pdf";
        String filename2 = destinationFolder + "stamping8_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stamping9() throws IOException {
        String filename1 = destinationFolder + "stamping9_1.pdf";
        String filename2 = destinationFolder + "stamping9_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stamping10() throws IOException {
        String filename1 = destinationFolder + "stamping10_1.pdf";
        String filename2 = destinationFolder + "stamping10_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stamping11() throws IOException {
        String filename1 = destinationFolder + "stamping11_1.pdf";
        String filename2 = destinationFolder + "stamping11_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stamping12() throws IOException {
        String filename1 = destinationFolder + "stamping12_1.pdf";
        String filename2 = destinationFolder + "stamping12_2.pdf";
        int pageCount = 1010;

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2));

        int newPageCount = 10;
        for (int i = pageCount; i > newPageCount; i--) {
            pdfDoc2.removePage(i);
        }
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfPage pdfPage = pdfDoc3.getPage(1);
        PdfDictionary root = pdfPage.getPdfObject().getAsDictionary(PdfName.Parent);
        assertEquals(newPageCount, root.getAsArray(PdfName.Kids).size(), "PdfPages kids count");
        assertEquals(newPageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stamping13() throws IOException {
        String filename1 = destinationFolder + "stamping13_1.pdf";
        String filename2 = destinationFolder + "stamping13_2.pdf";
        int pageCount = 1010;

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2));

        for (int i = pageCount; i > 1; i--) {
            pdfDoc2.removePage(i);
        }
        pdfDoc2.removePage(1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc2.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfArray rootKids = pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject().getAsArray(PdfName.Kids);
        assertEquals(2, rootKids.size(), "Page root kids count");
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stamping14() throws IOException {
        String filename1 = sourceFolder + "20000PagesDocument.pdf";
        String filename2 = destinationFolder + "stamping14.pdf";

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), CompareTool.createTestPdfWriter(filename2));

        for (int i = pdfDoc2.getNumberOfPages(); i > 3; i--) {
            pdfDoc2.removePage(i);
        }

        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        //NOTE: during page removing iText don't flatten page structure (we can end up with a lot of embedded pages dictionaries)
        assertEquals(42226, pdfDoc3.getXref().size(), "Xref size");
        assertEquals(3, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stampingStreamsCompression01() throws IOException {
        // by default, old streams should not be recompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression01.pdf";

        PdfReader reader = new PdfReader(filenameIn);
        PdfWriter writer = CompareTool.createTestPdfWriter(filenameOut);
        writer.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        doc.close();

        doc = new PdfDocument(CompareTool.createOutputReader(filenameOut));
        stream = (PdfStream) doc.getPdfObject(6);
        int lengthAfter = stream.getLength();

        assertEquals(5731884, lengthBefore);
        float expected = 5731884;
        float coef = Math.abs((expected - lengthAfter) / expected);
        assertTrue(coef < 0.01);
    }

    @Test
    public void stampingStreamsCompression02() throws IOException {
        // if user specified, stream may be uncompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression02.pdf";

        PdfReader reader = new PdfReader(filenameIn);
        PdfWriter writer = CompareTool.createTestPdfWriter(filenameOut);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        stream.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        doc.close();

        doc = new PdfDocument(CompareTool.createOutputReader(filenameOut));
        stream = (PdfStream) doc.getPdfObject(6);
        int lengthAfter = stream.getLength();

        assertEquals(5731884, lengthBefore);
        float expected = 11321910;
        float coef = Math.abs((expected - lengthAfter) / expected);
        assertTrue(coef < 0.01);
    }

    @Test
    public void stampingStreamsCompression03() throws IOException {
        // if user specified, stream may be recompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression03.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(filenameIn), CompareTool.createTestPdfWriter(filenameOut));
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        doc.close();

        doc = new PdfDocument(CompareTool.createOutputReader(filenameOut));
        stream = (PdfStream) doc.getPdfObject(6);
        int lengthAfter = stream.getLength();

        assertEquals(5731884, lengthBefore);
        float expected = 5729270;
        float coef = Math.abs((expected - lengthAfter) / expected);
        assertTrue(coef < 0.01);
    }

    @Test
    public void stampingXmp1() throws IOException, XMPException {
        String filename1 = destinationFolder + "stampingXmp1_1.pdf";
        String filename2 = destinationFolder + "stampingXmp1_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2,
                new WriterProperties().setFullCompressionMode(false).addXmpMetadata());
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertNotNull(pdfDoc3.getXmpMetadata(), "XmpMetadata not found");
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stampingXmp2() throws IOException, XMPException {
        String filename1 = destinationFolder + "stampingXmp2_1.pdf";
        String filename2 = destinationFolder + "stampingXmp2_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2,
                new WriterProperties().setFullCompressionMode(true).addXmpMetadata());
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertNotNull(pdfDoc3.getXmpMetadata(), "XmpMetadata not found");
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend1() throws IOException {
        String filename1 = destinationFolder + "stampingAppend1_1.pdf";
        String filename2 = destinationFolder + "stampingAppend1_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2), new StampingProperties().useAppendMode());
        pdfDoc2.getDocumentInfo().setCreator("iText").setTitle("Empty iText Document");
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary trailer = pdfDocument.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfString creator = info.getAsString(PdfName.Creator);
        assertEquals("iText", creator.toString());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%Hello World\n", new String(bytes));
        String date = pdfDocument.getDocumentInfo().getPdfObject().getAsString(PdfName.ModDate).getValue();
        Calendar cl = PdfDate.decode(date);
        double diff = DateTimeUtil.getUtcMillisFromEpoch(null) - DateTimeUtil.getUtcMillisFromEpoch(cl);
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        assertTrue(diff < 5000, message);
        pdfDocument.close();
    }

    @Test
    public void stampingAppend2() throws IOException {
        String filename1 = destinationFolder + "stampingAppend2_1.pdf";
        String filename2 = destinationFolder + "stampingAppend2_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2), new StampingProperties().useAppendMode());
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.setModified();
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        pdfDocument.close();
    }

    @Test
    public void stampingAppend3() throws IOException {
        String filename1 = destinationFolder + "stampingAppend3_1.pdf";
        String filename2 = destinationFolder + "stampingAppend3_2.pdf";

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2), new StampingProperties().useAppendMode());
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));

        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        pdfDocument.close();
    }

    @Test
    public void stampingAppend4() throws IOException {
        String filename1 = destinationFolder + "stampingAppend4_1.pdf";
        String filename2 = destinationFolder + "stampingAppend4_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2), new StampingProperties().useAppendMode());
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }

        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        assertEquals(pageCount, pdfDocument.getNumberOfPages(), "Page count");
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_TABLE_INCONSISTENCY)})
    public void stampingAppend5() throws IOException {
        String filename1 = destinationFolder + "stampingAppend5_1.pdf";
        String filename2 = destinationFolder + "stampingAppend5_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        assertEquals(pageCount, pdfDocument.getNumberOfPages(), "Page count");
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend8() throws IOException {
        String filename1 = destinationFolder + "stampingAppend8_1.pdf";
        String filename2 = destinationFolder + "stampingAppend8_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createOutputReader(filename1), CompareTool.createTestPdfWriter(filename2), new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_TABLE_INCONSISTENCY)})
    public void stampingAppend9() throws IOException {
        String filename1 = destinationFolder + "stampingAppend9_1.pdf";
        String filename2 = destinationFolder + "stampingAppend9_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_STREAM_INCONSISTENCY)})
    public void stampingAppend10() throws IOException {
        String filename1 = destinationFolder + "stampingAppend10_1.pdf";
        String filename2 = destinationFolder + "stampingAppend10_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend11() throws IOException {
        String filename1 = destinationFolder + "stampingAppend11_1.pdf";
        String filename2 = destinationFolder + "stampingAppend11_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = CompareTool.createTestPdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = CompareTool.createOutputReader(filename1);
        PdfWriter writer2 = CompareTool.createTestPdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals(pageCount, pdfDoc3.getNumberOfPages(), "Number of pages");
        assertFalse(reader3.hasRebuiltXref(), "Rebuilt");
        assertFalse(reader3.hasFixedXref(), "Fixed");
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = CompareTool.createOutputReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes), "Page content at page " + i);
        }
        pdfDocument.close();
    }

    @Test
    public void stampingVersionTest01() throws IOException {
        // By default the version of the output file should be the same as the original one
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "hello_stamped01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(in), CompareTool.createTestPdfWriter(out));

        assertEquals(PdfVersion.PDF_1_4, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(CompareTool.createOutputReader(out));
        assertEquals(PdfVersion.PDF_1_4, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingVersionTest02() throws IOException {
        // There is a possibility to override version in stamping mode
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "hello_stamped02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(in), CompareTool.createTestPdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(CompareTool.createOutputReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingAppendVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "stampingAppendVersionTest01.pdf";

        PdfReader reader = new PdfReader(in);
        PdfWriter writer = CompareTool.createTestPdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfDocument pdfDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(CompareTool.createOutputReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingTestWithTaggedStructure() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename), CompareTool.createTestPdfWriter(destinationFolder + "stampingDocWithTaggedStructure.pdf"));
        pdfDoc.close();
    }

    @Test
    public void stampingTestWithFullCompression01() throws IOException, InterruptedException {
        String compressedOutPdf = destinationFolder + "stampingTestWithFullCompression01Compressed.pdf";
        String decompressedOutPdf = destinationFolder + "stampingTestWithFullCompression01Decompressed.pdf";
        
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "fullCompressedDocument.pdf"),
                new PdfWriter(compressedOutPdf));
        pdfDoc.close();
        float compressedLength = new File(compressedOutPdf).length();

        pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "fullCompressedDocument.pdf"),
                new PdfWriter(decompressedOutPdf, new WriterProperties().setFullCompressionMode(false)));
        pdfDoc.close();
        float decompressedLength = new File(decompressedOutPdf).length();

        float coef = compressedLength / decompressedLength;
        String compareRes = new CompareTool().compareByContent(compressedOutPdf, decompressedOutPdf, destinationFolder);
        assertTrue(coef < 0.7);
        assertNull(compareRes);
    }

    @Test
    //TODO: DEVSIX-2007
    public void stampingStreamNoEndingWhitespace01() throws IOException, InterruptedException {
        PdfDocument pdfDocInput = new PdfDocument(new PdfReader(sourceFolder + "stampingStreamNoEndingWhitespace01.pdf"));
        PdfDocument pdfDocOutput = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "stampingStreamNoEndingWhitespace01.pdf", new WriterProperties().setCompressionLevel(0)));

        pdfDocOutput.addEventHandler(PdfDocumentEvent.END_PAGE, new WatermarkEventHandler());

        pdfDocInput.copyPagesTo(1, pdfDocInput.getNumberOfPages(), pdfDocOutput);

        pdfDocInput.close();
        pdfDocOutput.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "stampingStreamNoEndingWhitespace01.pdf", sourceFolder + "cmp_stampingStreamNoEndingWhitespace01.pdf", destinationFolder, "diff_"));
    }

    @Test
    // with some PDFs, when adding content to an existing PDF in append mode, the resource dictionary didn't get written as a new version
    public void stampingInAppendModeCreatesNewResourceDictionary() throws Exception {
        StampingProperties stampProps = new StampingProperties();
        stampProps.useAppendMode();
        stampProps.preserveEncryption();

        PdfFont font = PdfFontFactory.createFont();

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "hello-d.pdf"), new PdfWriter(resultStream), stampProps);
        PdfPage page = pdfDoc.getPage(1);
        PdfCanvas canvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setFontAndSize(font, 42);
        canvas.setTextMatrix(1, 0, 0, -1, 100, 100);
        canvas.showText("TEXT TO STAMP");
        canvas.endText();
        pdfDoc.close();

        // parse text
        pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(resultStream.toByteArray())));
        LocationTextExtractionStrategy strat = new LocationTextExtractionStrategy();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(strat);
        // this fails with an NPE b/c the /F1 font isn't in the fonts dictionary
        processor.processPageContent(pdfDoc.getPage(1));
        Assertions.assertTrue(strat.getResultantText().contains("TEXT TO STAMP"));

    }

    
    static void verifyPdfPagesCount(PdfObject root) {
        if (root.getType() == PdfObject.INDIRECT_REFERENCE)
            root = ((PdfIndirectReference) root).getRefersTo();
        PdfDictionary pages = (PdfDictionary) root;
        if (!pages.containsKey(PdfName.Kids)) return;
        PdfNumber count = pages.getAsNumber(PdfName.Count);
        if (count != null) {
            assertTrue(count.intValue() > 0, "PdfPages with zero count");
        }
        PdfObject kids = pages.get(PdfName.Kids);
        if (kids.getType() == PdfObject.ARRAY) {
            for (PdfObject kid : (PdfArray) kids) {
                verifyPdfPagesCount(kid);
            }
        } else {
            verifyPdfPagesCount(kids);
        }
    }

    static class WatermarkEventHandler extends AbstractPdfDocumentEventHandler {

        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            PdfDocumentEvent pdfEvent = (PdfDocumentEvent) event;
            PdfPage page = pdfEvent.getPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            try {
                pdfCanvas.beginText()
                        .setFontAndSize(PdfFontFactory.createFont(), 12.0f)
                        .showText("Text")
                        .endText();
            } catch (IOException e) {
            }
        }
    }
}
