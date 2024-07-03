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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.CompareTool.CompareResult;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;

@Tag("IntegrationTest")
public class ParentTreeTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ParentTreeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ParentTreeTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void test01() throws IOException {
        String outFile = destinationFolder + "parentTreeTest01.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest01.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

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

        document.close();
        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void stampingFormXObjectInnerContentTaggedTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "alreadyTaggedFormXObjectInnerContent.pdf";
        String outPdf = destinationFolder + "stampingFormXObjectInnerContentTaggedTest.pdf";
        String cmpPdf = sourceFolder + "cmp_stampingFormXObjectInnerContentTaggedTest.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        taggedPdf.setTagged();
        taggedPdf.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    public void severalXObjectsOnOnePageTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "severalXObjectsOnOnePageTest.pdf";
        String outPdf = destinationFolder + "severalXObjectsOnOnePageTest.pdf";
        String cmpPdf = sourceFolder + "cmp_severalXObjectsOnOnePageTest.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        taggedPdf.setTagged();
        taggedPdf.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    public void earlyFlushXObjectTaggedTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "earlyFlushXObjectTaggedTest.pdf";
        String outPdf = destinationFolder + "earlyFlushXObjectTaggedTest.pdf";
        String cmpPdf = sourceFolder + "cmp_earlyFlushXObjectTaggedTest.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        PdfDictionary resource = taggedPdf.getFirstPage().getResources().getResource(PdfName.XObject);
        resource.get(new PdfName("Fm1")).flush();

        taggedPdf.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    public void identicalMcidIdInOneStreamTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "identicalMcidIdInOneStreamTest.pdf";
        String outPdf = destinationFolder + "identicalMcidIdInOneStreamTest.pdf";
        String cmpPdf = sourceFolder + "cmp_identicalMcidIdInOneStreamTest.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        taggedPdf.setTagged();
        taggedPdf.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    public void copyPageWithFormXObjectTaggedTest() throws IOException, InterruptedException {
        String cmpPdf = sourceFolder + "cmp_copyPageWithFormXobjectTaggedTest.pdf";
        String outDoc = destinationFolder + "copyPageWithFormXobjectTaggedTest.pdf";
        PdfDocument srcPdf = new PdfDocument(new PdfReader(sourceFolder + "copyFromFile.pdf"));
        PdfDocument outPdf = new PdfDocument(new PdfReader(sourceFolder + "copyToFile.pdf"), CompareTool.createTestPdfWriter(outDoc));

        outPdf.setTagged();
        srcPdf.copyPagesTo(1, 1, outPdf);

        srcPdf.close();
        outPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outDoc, cmpPdf, destinationFolder));
    }

    @Test
    public void removePageWithFormXObjectTaggedTest() throws IOException, InterruptedException {
        String cmpPdf = sourceFolder + "cmp_removePageWithFormXobjectTaggedTest.pdf";
        String outDoc = destinationFolder + "removePageWithFormXobjectTaggedTest.pdf";

        PdfDocument outPdf = new PdfDocument(new PdfReader(sourceFolder + "forRemovePage.pdf"), CompareTool.createTestPdfWriter(outDoc));

        outPdf.setTagged();
        outPdf.removePage(1);

        outPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outDoc, cmpPdf, destinationFolder));
    }

    @Test
    public void test02() throws IOException {
        String outFile = destinationFolder + "parentTreeTest02.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest02.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage firstPage = document.addNewPage();

        PdfPage secondPage = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(secondPage);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, secondPage));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(secondPage, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(secondPage, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test03() throws IOException {
        String outFile = destinationFolder + "parentTreeTest03.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest03.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage firstPage = document.addNewPage();

        for (int i = 0; i < 51; i++) {
            PdfPage anotherPage = document.addNewPage();
            PdfCanvas canvas = new PdfCanvas(anotherPage);
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
            canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
            PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
            PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, anotherPage));
            canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(anotherPage, span1))));
            canvas.showText("Hello ");
            canvas.closeTag();
            canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(anotherPage, span1))));
            canvas.showText("World");
            canvas.closeTag();
            canvas.endText();
            canvas.release();
        }
        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test04() throws IOException {
        String outFile = destinationFolder + "parentTreeTest04.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest04.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        for (int i = 0; i < 51; i++) {
            PdfPage anotherPage = document.addNewPage();
            PdfCanvas canvas = new PdfCanvas(anotherPage);
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
            canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
            PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
            PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, anotherPage));
            canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(anotherPage, span1))));
            canvas.showText("Hello ");
            canvas.closeTag();
            canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(anotherPage, span1))));
            canvas.showText("World");
            canvas.closeTag();
            canvas.endText();
            canvas.release();
        }
        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test05() throws IOException {
        String outFile = destinationFolder + "parentTreeTest05.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest05.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
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

        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test06() throws IOException {
        String outFile = destinationFolder + "parentTreeTest06.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest06.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

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
        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        secondPage.addAnnotation(linkExplicitDest);

        document.close();
        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void objRefAsStreamTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "objRefAsStream.pdf";
        String outPdf = destinationFolder + "objRefAsStream.pdf";
        String cmpPdf = sourceFolder + "cmp_objRefAsStream.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        taggedPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED)
    })
    public void objRefAsInvalidType() throws IOException {
        String pdf = sourceFolder + "objRefAsInvalidType.pdf";
        PdfDocument doc = new PdfDocument(new PdfReader(pdf));
        Assertions.assertNull(doc.getStructTreeRoot());
    }

    @Test
    public void unregisterObjRefAsStreamTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "objRefAsStream.pdf";
        String outPdf = destinationFolder + "objRefAsStreamUnregisterMcr.pdf";
        String cmpPdf = sourceFolder + "cmp_objRefAsStreamUnregisterMcr.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));

        PdfStructElem elem = (PdfStructElem) taggedPdf.getStructTreeRoot().getKids().get(0).getKids().get(0);
        elem.removeKid(0);
        taggedPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED, count = 4)
    })
    public void allObjRefDontHaveStructParentTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "allObjRefDontHaveStructParent.pdf";
        String outPdf = destinationFolder + "allObjRefDontHaveStructParent.pdf";
        String cmpPdf = sourceFolder + "cmp_allObjRefDontHaveStructParent.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), new PdfWriter(outPdf));
        taggedPdf.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.XOBJECT_STRUCT_PARENT_INDEX_MISSED_AND_RECREATED)
    })
    public void xObjDoesntHaveStructParentTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "xObjDoesntHaveStructParentTest.pdf";
        String outPdf = destinationFolder + "xObjDoesntHaveStructParentTest.pdf";
        String cmpPdf = sourceFolder + "cmp_xObjDoesntHaveStructParentTest.pdf";

        PdfDocument taggedPdf = new PdfDocument(new PdfReader(pdf), new PdfWriter(outPdf));
        taggedPdf.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED)
    })
    public void objRefNoStructParentNoModificationTest() throws IOException {
        String pdf = sourceFolder + "objRefNoStructParent.pdf";
        String outPdf = destinationFolder + "objRefNoStructParentNoModification.pdf";

        PdfReader reader = new PdfReader(pdf).setStrictnessLevel(PdfReader.StrictnessLevel.CONSERVATIVE);
        PdfDocument doc = new PdfDocument(reader, CompareTool.createTestPdfWriter(outPdf));
        PdfArray nums =  doc.getCatalog().getPdfObject().getAsDictionary(PdfName.StructTreeRoot)
                .getAsDictionary(PdfName.ParentTree).getAsArray(PdfName.Nums);

        assertNull(getStructParentEntry(nums.get(3)));
        assertNull(getStructParentEntry(nums.get(5)));
        assertNull(getStructParentEntry(nums.get(7)));
        assertNull(getStructParentEntry(nums.get(9)));
        doc.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED, count = 4)
    })
    public void objRefNoStructParentModificationTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "objRefNoStructParent.pdf";
        String outPdf = destinationFolder + "objRefNoStructParentModification.pdf";
        String cmpPdf = sourceFolder + "cmp_objRefNoStructParentModification.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED)
    })
    public void xObjNoStructParentNoModificationTest() throws IOException {
        String pdf = sourceFolder + "xObjNoStructParent.pdf";
        String outPdf = destinationFolder + "xObjNoStructParentNoModification.pdf";

        PdfReader reader = new PdfReader(pdf).setStrictnessLevel(PdfReader.StrictnessLevel.CONSERVATIVE);
        PdfDocument doc = new PdfDocument(reader, new PdfWriter(outPdf));
        PdfObject obj =  doc.getCatalog().getPdfObject().getAsDictionary(PdfName.StructTreeRoot)
                .getAsDictionary(PdfName.ParentTree).getAsArray(PdfName.Nums).get(1);
        PdfStream xObj = ((PdfDictionary) ((PdfArray) obj).get(0)).getAsDictionary(PdfName.K).getAsStream(PdfName.Stm);

        assertNull(xObj.get(PdfName.StructParent));
        doc.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.XOBJECT_STRUCT_PARENT_INDEX_MISSED_AND_RECREATED)
    })
    public void xObjNoStructParentModificationTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "xObjNoStructParent.pdf";
        String outPdf = destinationFolder + "xObjNoStructParentModification.pdf";
        String cmpPdf = sourceFolder + "cmp_xObjNoStructParentModification.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(pdf), CompareTool.createTestPdfWriter(outPdf));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED)
    })
    public void objRefNoStructParentNoReaderTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "objRefNoStructParentNoReader.pdf";
        String cmpPdf = sourceFolder + "cmp_objRefNoStructParentNoReader.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        pdfDoc.setTagged();

        PdfPage page = pdfDoc.addNewPage();

        PdfDictionary mcrDic = new PdfDictionary();
        mcrDic.put(PdfName.Pg, page.getPdfObject());
        mcrDic.put(PdfName.MCID, new PdfNumber(0));
        mcrDic.put(PdfName.Obj, new PdfDictionary());

        PdfDictionary elemDic = new PdfDictionary();
        elemDic.put(PdfName.P,pdfDoc.getStructTreeRoot().getPdfObject());

        PdfStructElem elem = new PdfStructElem(elemDic);
        elem.makeIndirect(pdfDoc);

        PdfMcr mcr = new PdfObjRef(mcrDic, elem);
        elem.addKid(0,mcr);

        pdfDoc.getStructTreeRoot().addKid(elem);
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING)
    })
    public void copyPageWithMultipleDocumentTagsTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(sourceFolder + "pdfWithMultipleDocumentTags.pdf"),
                new PdfWriter(new ByteArrayOutputStream()));

        AssertUtil.doesNotThrow(() -> pdfDoc.getTagStructureContext().normalizeDocumentRootTag());
    }

    private PdfObject getStructParentEntry(PdfObject obj){
        return ((PdfDictionary) obj).getAsDictionary(PdfName.K).getAsDictionary(PdfName.Obj).get(PdfName.StructParent);
    }

    private boolean checkParentTree(String outFileName, String cmpFileName) throws IOException {
        PdfReader outReader = CompareTool.createOutputReader(outFileName);
        PdfDocument outDocument = new PdfDocument(outReader);
        PdfReader cmpReader = CompareTool.createOutputReader(cmpFileName);
        PdfDocument cmpDocument = new PdfDocument(cmpReader);
        CompareResult result = new CompareTool().compareByCatalog(outDocument, cmpDocument);
        if (!result.isOk()) {
            System.out.println(result.getReport());
        }
        return result.isOk();
    }
}
