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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class PdfStampingTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStampingTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStampingTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void stamping1() throws IOException {
        String filename1 = destinationFolder + "stamping1_1.pdf";
        String filename2 = destinationFolder + "stamping1_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(destinationFolder + "stamping1_2.pdf");
        PdfDocument document = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary trailer = document.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfString creator = info.getAsString(PdfName.Creator);
        assertEquals("iText 7", creator.toString());
        byte[] bytes = document.getPage(1).getContentBytes();
        assertEquals("%Hello World\n", new String(bytes));
        String date = document.getDocumentInfo().getPdfObject().getAsString(PdfName.ModDate).getValue();
        Calendar cl = PdfDate.decode(date);
        double diff = DateTimeUtil.getUtcMillisFromEpoch(null) - DateTimeUtil.getUtcMillisFromEpoch(cl);
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        assertTrue(message, diff < 5000);
        document.close();
    }

    @Test
    public void stamping2() throws IOException {
        String filename1 = destinationFolder + "stamping2_1.pdf";
        String filename2 = destinationFolder + "stamping2_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(destinationFolder + "stamping2_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
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

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
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

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2));
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
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

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
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

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true)));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2));
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
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

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
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

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping9() throws IOException {
        String filename1 = destinationFolder + "stamping9_1.pdf";
        String filename2 = destinationFolder + "stamping9_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping10() throws IOException {
        String filename1 = destinationFolder + "stamping10_1.pdf";
        String filename2 = destinationFolder + "stamping10_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping11() throws IOException {
        String filename1 = destinationFolder + "stamping11_1.pdf";
        String filename2 = destinationFolder + "stamping11_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping12() throws IOException {
        String filename1 = destinationFolder + "stamping12_1.pdf";
        String filename2 = destinationFolder + "stamping12_2.pdf";
        int pageCount = 1010;

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2));

        int newPageCount = 10;
        for (int i = pageCount; i > newPageCount; i--) {
            pdfDoc2.removePage(i);
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfPage pdfPage = pdfDoc3.getPage(1);
        PdfDictionary root = pdfPage.getPdfObject().getAsDictionary(PdfName.Parent);
        assertEquals("PdfPages kids count", newPageCount, root.getAsArray(PdfName.Kids).size());
        assertEquals("Number of pages", newPageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping13() throws IOException {
        String filename1 = destinationFolder + "stamping13_1.pdf";
        String filename2 = destinationFolder + "stamping13_2.pdf";
        int pageCount = 1010;

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2));

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

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfArray rootKids = pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject().getAsArray(PdfName.Kids);
        assertEquals("Page root kids count", 2, rootKids.size());
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stamping14() throws IOException {
        String filename1 = sourceFolder + "20000PagesDocument.pdf";
        String filename2 = destinationFolder + "stamping14.pdf";

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2));

        for (int i = pdfDoc2.getNumberOfPages(); i > 3; i--) {
            pdfDoc2.removePage(i);
        }

        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        //NOTE: during page removing iText don't flatten page structure (we can end up with a lot of embedded pages dictionaries)
        assertEquals("Xref size", 42226, pdfDoc3.getXref().size());
        assertEquals("Number of pages", 3, pdfDoc3.getNumberOfPages());
        assertFalse("Rebuilt", reader3.hasRebuiltXref());
        assertFalse("Fixed", reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingStreamsCompression01() throws IOException {
        // by default, old streams should not be recompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression01.pdf";

        PdfReader reader = new PdfReader(filenameIn);
        PdfWriter writer = new PdfWriter(filenameOut);
        writer.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        doc.close();

        doc = new PdfDocument(new PdfReader(filenameOut));
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
        PdfWriter writer = new PdfWriter(filenameOut);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        stream.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        doc.close();

        doc = new PdfDocument(new PdfReader(filenameOut));
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

        PdfDocument doc = new PdfDocument(new PdfReader(filenameIn), new PdfWriter(filenameOut));
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        doc.close();

        doc = new PdfDocument(new PdfReader(filenameOut));
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

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2,
                new WriterProperties().setFullCompressionMode(false).addXmpMetadata());
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertNotNull("XmpMetadata not found", XMPMetaFactory.parseFromBuffer(pdfDoc3.getXmpMetadata()));
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingXmp2() throws IOException, XMPException {
        String filename1 = destinationFolder + "stampingXmp2_1.pdf";
        String filename2 = destinationFolder + "stampingXmp2_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2,
                new WriterProperties().setFullCompressionMode(true).addXmpMetadata());
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertNotNull("XmpMetadata not found", XMPMetaFactory.parseFromBuffer(pdfDoc3.getXmpMetadata()));
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend1() throws IOException {
        String filename1 = destinationFolder + "stampingAppend1_1.pdf";
        String filename2 = destinationFolder + "stampingAppend1_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2), new StampingProperties().useAppendMode());
        pdfDoc2.getDocumentInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary trailer = pdfDocument.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfString creator = info.getAsString(PdfName.Creator);
        assertEquals("iText 7", creator.toString());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%Hello World\n", new String(bytes));
        String date = pdfDocument.getDocumentInfo().getPdfObject().getAsString(PdfName.ModDate).getValue();
        Calendar cl = PdfDate.decode(date);
        double diff = DateTimeUtil.getUtcMillisFromEpoch(null) - DateTimeUtil.getUtcMillisFromEpoch(cl);
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        assertTrue(message, diff < 5000);
        pdfDocument.close();
    }

    @Test
    public void stampingAppend2() throws IOException {
        String filename1 = destinationFolder + "stampingAppend2_1.pdf";
        String filename2 = destinationFolder + "stampingAppend2_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2), new StampingProperties().useAppendMode());
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.setModified();
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
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

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2), new StampingProperties().useAppendMode());
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));

        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
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

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2), new StampingProperties().useAppendMode());
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }

        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend5() throws IOException {
        String filename1 = destinationFolder + "stampingAppend5_1.pdf";
        String filename2 = destinationFolder + "stampingAppend5_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(filename1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
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

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader(filename1), new PdfWriter(filename2), new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend9() throws IOException {
        String filename1 = destinationFolder + "stampingAppend9_1.pdf";
        String filename2 = destinationFolder + "stampingAppend9_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend10() throws IOException {
        String filename1 = destinationFolder + "stampingAppend10_1.pdf";
        String filename2 = destinationFolder + "stampingAppend10_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingAppend11() throws IOException {
        String filename1 = destinationFolder + "stampingAppend11_1.pdf";
        String filename2 = destinationFolder + "stampingAppend11_2.pdf";
        int pageCount = 10;

        PdfWriter writer1 = new PdfWriter(filename1, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(filename1);
        PdfWriter writer2 = new PdfWriter(filename2, new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(filename2);
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().getPageTree().getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        pdfDocument.close();
    }

    @Test
    public void stampingVersionTest01() throws IOException {
        // By default the version of the output file should be the same as the original one
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "hello_stamped01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(in), new PdfWriter(out));

        assertEquals(PdfVersion.PDF_1_4, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_1_4, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingVersionTest02() throws IOException {
        // There is a possibility to override version in stamping mode
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "hello_stamped02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(in), new PdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingAppendVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "stampingAppendVersionTest01.pdf";

        PdfReader reader = new PdfReader(in);
        PdfWriter writer = new PdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfDocument pdfDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingTestWithTaggedStructure() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename), new PdfWriter(destinationFolder + "stampingDocWithTaggedStructure.pdf"));
        pdfDoc.close();
    }

    @Test
    public void stampingTestWithFullCompression01() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "stampingTestWithFullCompression01.pdf";
        String cmpPdf = sourceFolder + "cmp_stampingTestWithFullCompression01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "fullCompressedDocument.pdf"),
                new PdfWriter(outPdf));
        pdfDoc.close();
        float result = new File(outPdf).length();
        float expected = new File(cmpPdf).length();
        float coef = Math.abs((expected - result) / expected);
        String compareRes = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder);
        assertTrue(coef < 0.01);
        assertNull(compareRes);
    }

    @Test
    public void stampingTestWithFullCompression02() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "fullCompressedDocument.pdf"),
                new PdfWriter(destinationFolder + "stampingTestWithFullCompression02.pdf",
                        new WriterProperties().setFullCompressionMode(false)));
        pdfDoc.close();
        float result = new File(destinationFolder + "stampingTestWithFullCompression02.pdf").length();
        float expected = new File(sourceFolder + "cmp_stampingTestWithFullCompression02.pdf").length();
        float coef = Math.abs((expected - result) / expected);
        assertTrue(coef < 0.01);
    }

    @Test
    //TODO: DEVSIX-2007
    public void stampingStreamNoEndingWhitespace01() throws IOException, InterruptedException {
        PdfDocument pdfDocInput = new PdfDocument(new PdfReader(sourceFolder + "stampingStreamNoEndingWhitespace01.pdf"));
        PdfDocument pdfDocOutput = new PdfDocument(new PdfWriter(destinationFolder + "stampingStreamNoEndingWhitespace01.pdf", new WriterProperties().setCompressionLevel(0)));

        pdfDocOutput.addEventHandler(PdfDocumentEvent.END_PAGE, new WatermarkEventHandler());

        pdfDocInput.copyPagesTo(1, pdfDocInput.getNumberOfPages(), pdfDocOutput);

        pdfDocInput.close();
        pdfDocOutput.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "stampingStreamNoEndingWhitespace01.pdf", sourceFolder + "cmp_stampingStreamNoEndingWhitespace01.pdf", destinationFolder, "diff_"));
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
        processor.processPageContent(pdfDoc.getPage(1)); // this fails with an NPE b/c the /F1 font isn't in the fonts dictionary
        Assert.assertTrue(strat.getResultantText().contains("TEXT TO STAMP"));

    }

    
    static void verifyPdfPagesCount(PdfObject root) {
        if (root.getType() == PdfObject.INDIRECT_REFERENCE)
            root = ((PdfIndirectReference) root).getRefersTo();
        PdfDictionary pages = (PdfDictionary) root;
        if (!pages.containsKey(PdfName.Kids)) return;
        PdfNumber count = pages.getAsNumber(PdfName.Count);
        if (count != null) {
            assertTrue("PdfPages with zero count", count.intValue() > 0);
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

    static class WatermarkEventHandler implements IEventHandler {

        @Override
        public void handleEvent(Event event) {
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
