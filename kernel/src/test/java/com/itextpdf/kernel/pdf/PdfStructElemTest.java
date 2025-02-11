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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfStructElemTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStructElemTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStructElemTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void structElemTest01() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest01.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page1));

        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(page1, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        page1.flush();
        page2.flush();

        document.close();

        compareResult("structElemTest01.pdf", "cmp_structElemTest01.pdf");
    }

    @Test
    public void structElemTest02() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest02.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new PdfName("Chunk"), PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        compareResult("structElemTest02.pdf", "cmp_structElemTest02.pdf");
    }

    @Test
    public void structElemTest03() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest03.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new PdfName("Chunk"), PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page1));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new PdfName("Chunk"), page1));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page1, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        span2 = paragraph.addKid(new PdfStructElem(document, new PdfName("Chunk"), page2));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page1.flush();
        page2.flush();

        document.close();

        document = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "structElemTest03.pdf"));
        Assertions.assertEquals(2, (int) document.getNextStructParentIndex());
        PdfPage page = document.getPage(1);
        Assertions.assertEquals(0, page.getStructParentIndex());
        Assertions.assertEquals(2, page.getNextMcid());
        document.close();
    }

    @Test
    public void structElemTest04() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new PdfName("Chunk"), PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();
        byte[] bytes = baos.toByteArray();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(bytes));
        writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest04.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        document = new PdfDocument(reader, writer);

        page = document.getPage(1);
        canvas = new PdfCanvas(page);

        PdfStructElem p = (PdfStructElem) document.getStructTreeRoot().getKids().get(0).getKids().get(0);

        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 490);

        //Inserting span between of 2 existing ones.
        span1 = p.addKid(1, new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("text1");
        canvas.closeTag();

        //Inserting span at the end.
        span1 = p.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("text2");
        canvas.closeTag();

        canvas.endText();

        canvas.release();
        page.flush();

        document.close();

        compareResult("structElemTest04.pdf", "cmp_structElemTest04.pdf");
    }

    @Test
    public void structElemTest05() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest05.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Click ");
        canvas.closeTag();

        PdfStructElem link = paragraph.addKid(new PdfStructElem(document, PdfName.Link, page));
        canvas.openTag(new CanvasTag(link.addKid(new PdfMcrNumber(page, link))));
        canvas.setFillColorRgb(0, 0, 1).showText("here");
        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(80, 508, 40, 18));
        linkAnnotation.setColor(new float[] {0, 0, 1}).setBorder(new PdfArray(new float[] {0, 0, 1}));
        page.addAnnotation(-1, linkAnnotation, false);
        link.addKid(new PdfObjRef(linkAnnotation, link, document.getNextStructParentIndex()));
        canvas.closeTag();

        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.setFillColorRgb(0, 0, 0);
        canvas.showText(" to visit iText site.");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        document.close();

        compareResult("structElemTest05.pdf", "cmp_structElemTest05.pdf");
    }

    @Test
    public void structElemTest06() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest06.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1)))
                .addProperty(PdfName.Lang, new PdfString("en-US"))
                .addProperty(PdfName.ActualText, new PdfString("The actual text is: Text with property list")));
        canvas.showText("Text with property list");
        canvas.closeTag();

        canvas.endText();
        canvas.release();

        document.close();

        compareResult("structElemTest06.pdf", "cmp_structElemTest06.pdf");
    }

    @Test
    @LogMessages(messages =
    @LogMessage(messageTemplate = IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, count = 5))
    public void structElemTest07() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest07.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1999/xhtml");
        span1.setNamespace(namespace);
        span1.addRef(span2);
        span1.setPhoneticAlphabet(PdfName.ipa);
        span1.setPhoneme(new PdfString("Heeeelllloooooo"));
        namespace.addNamespaceRoleMapping(StandardRoles.SPAN, StandardRoles.SPAN);
        document.getStructTreeRoot().addNamespace(namespace);

        page.flush();

        document.close();

        compareResult("structElemTest07.pdf", "cmp_structElemTest07.pdf");
    }

    @Test
    public void structElemTest08() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest08.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfStructTreeRoot doc = document.getStructTreeRoot();

        PdfPage firstPage = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(firstPage);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, firstPage));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(firstPage, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(firstPage, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage secondPage = document.addNewPage();

        // on flushing, the Document tag is not added
        firstPage.flush();
        secondPage.flush();

        document.close();

        compareResult("structElemTest08.pdf", "cmp_structElemTest08.pdf");
    }

    @Test
    public void structElemTest09() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structElemTest09.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "88th_Academy_Awards_mult_roots.pdf"),
                writer);

        document.removePage(1);
        document.close();

        compareResult("structElemTest09.pdf", "cmp_structElemTest09.pdf");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest01() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest01.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        ArrayList<Integer> pagesToCopy = new ArrayList<Integer>();
        pagesToCopy.add(3);
        pagesToCopy.add(4);
        pagesToCopy.add(10);
        pagesToCopy.add(11);
        source.copyPagesTo(pagesToCopy, destination);
        source.copyPagesTo(50, 52, destination);

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest01.pdf", "cmp_structTreeCopyingTest01.pdf");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest02() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest02.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        source.copyPagesTo(6, source.getNumberOfPages(), destination);
        source.copyPagesTo(1, 5, destination);

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest02.pdf", "cmp_structTreeCopyingTest02.pdf");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest03() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest03.pdf"));
        destination.initializeOutlines();

        source.copyPagesTo(6, source.getNumberOfPages(), destination);
        source.copyPagesTo(1, 5, destination);

        destination.close();
        source.close();

        // we don't compare tag structures, because resultant document is not tagged
        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "structTreeCopyingTest03.pdf",
                        sourceFolder + "cmp_structTreeCopyingTest03.pdf",
                        destinationFolder, "diff_copying_03_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest04() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest04.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        for (int i = 1; i <= source.getNumberOfPages(); i++) {
            source.copyPagesTo(i, i, destination);
        }

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest04.pdf", "cmp_structTreeCopyingTest04.pdf");
    }

    @Test
    public void structTreeCopyingTest05() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest05.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.copyPagesTo(1, 1, document, 2);

        PdfDocument document2 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"));
        document2.copyPagesTo(1, 3, document, 4);

        document.close();
        document1.close();
        document2.close();

        compareResult("structTreeCopyingTest05.pdf", "cmp_structTreeCopyingTest05.pdf");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest06() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest06.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        source.copyPagesTo(1, source.getNumberOfPages(), destination);

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest06.pdf", "cmp_structTreeCopyingTest06.pdf");
    }

    @Test
    public void structTreeCopyingTest07() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "quick-brown-fox.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest07.pdf");
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page1));

        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(page1, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfDocument document1 = new PdfDocument(reader);
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document);

        document.close();
        document1.close();

        compareResult("structTreeCopyingTest07.pdf", "cmp_structTreeCopyingTest07.pdf");
    }

    @Test
    public void structTreeCopyingTest08() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest08.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document, 2);

        document.close();
        document1.close();

        compareResult("structTreeCopyingTest08.pdf", "cmp_structTreeCopyingTest08.pdf");
    }

    @Test
    public void structTreeCopyingTest09() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest09.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document, 2);
        document1.copyPagesTo(1, 1, document, 4);

        document.close();
        document1.close();

        compareResult("structTreeCopyingTest09.pdf", "cmp_structTreeCopyingTest09.pdf");
    }

    @Test
    public void structTreeCopyingTest10() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "88th_Academy_Awards.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest10.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 3, document, 2);

        PdfDocument document2 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document2.initializeOutlines();
        document2.copyPagesTo(1, 1, document, 4);

        document.close();
        document1.close();
        document2.close();

        compareResult("structTreeCopyingTest10.pdf", "cmp_structTreeCopyingTest10.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.ROLE_MAPPING_FROM_SOURCE_IS_NOT_COPIED_ALREADY_EXIST))
    public void structTreeCopyingTest11() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "88th_Academy_Awards.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "structTreeCopyingTest11.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox_mapping_mod.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document, 2);

        PdfDocument document2 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document2.initializeOutlines();
        document2.copyPagesTo(1, 1, document, 4);

        document.close();
        document1.close();
        document2.close();

        compareResult("structTreeCopyingTest11.pdf", "cmp_structTreeCopyingTest11.pdf");
    }

    @Test
    public void structTreeCopyingToPartiallyFlushedDocumentTest() throws Exception {
        String outFile = "structTreeCopyingToPartiallyFlushedDocumentTest.pdf";

        PdfDocument resultDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + outFile));
        resultDoc.setTagged();

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.copyPagesTo(1, 1, resultDoc);
        resultDoc.flushCopiedObjects(document1);
        document1.close();

        PdfDocument document2 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            document2.copyPagesTo(1, 1, resultDoc);
        });
        // TODO DEVSIX-7005 after exception is gone add assertion for the resulting document
        Assertions.assertEquals(
                KernelExceptionMessageConstant.TAG_STRUCTURE_COPYING_FAILED_IT_MIGHT_BE_CORRUPTED_IN_ONE_OF_THE_DOCUMENTS,
                e.getMessage()
        );
        document2.close();

        resultDoc.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.ENCOUNTERED_INVALID_MCR)
    })
    public void corruptedTagStructureTest01() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "invalidMcr.pdf"));
        assertTrue(document.isTagged());
        document.close();
    }

    @Test
    public void corruptedTagStructureTest02() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "directStructElem01.pdf"));
        assertTrue(document.isTagged());
        document.close();
    }

    @Test
    public void corruptedTagStructureTest03() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "directStructElem02.pdf");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(reader, writer);
        assertTrue(document.isTagged());
        document.close();

        document = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        assertTrue(document.isTagged());
        document.close();
    }

    @Test
    public void corruptedTagStructureTest04() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "directStructElem03.pdf"));
        assertTrue(document.isTagged());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument docToCopyTo = new PdfDocument(new PdfWriter(baos));
        docToCopyTo.setTagged();
        document.copyPagesTo(1, 1, docToCopyTo);
        document.close();

        docToCopyTo.close();

        document = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        Assertions.assertTrue(document.isTagged());
        document.close();
    }

    private void compareResult(String outFileName, String cmpFileName)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String outPdf = destinationFolder + outFileName;
        String cmpPdf = sourceFolder + cmpFileName;

        String contentDifferences = compareTool.compareByContent(outPdf,
                cmpPdf, destinationFolder);
        String taggedStructureDifferences = compareTool.compareTagStructures(outPdf, cmpPdf);

        String errorMessage = "";
        errorMessage += taggedStructureDifferences == null ? "" : taggedStructureDifferences + "\n";
        errorMessage += contentDifferences == null ? "" : contentDifferences;
        if (errorMessage.length() > 0) {
            fail(errorMessage);
        }
    }
}
