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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("IntegrationTest")
public class PdfCopyTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfCopyTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfCopyTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = IoLogMessageConstant.MAKE_COPY_OF_CATALOG_DICTIONARY_IS_FORBIDDEN)
    })
    public void copySignedDocuments() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(new PdfReader(sourceFolder + "hello_signed.pdf"));

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copySignedDocuments.pdf"));
        pdfDoc1.copyPagesTo(1, 1, pdfDoc2);
        pdfDoc2.close();
        pdfDoc1.close();

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copySignedDocuments.pdf"));

        PdfDictionary sig = (PdfDictionary) pdfDocument.getPdfObject(13);
        PdfDictionary sigRef = sig.getAsArray(PdfName.Reference).getAsDictionary(0);
        Assertions.assertTrue(PdfName.SigRef.equals(sigRef.getAsName(PdfName.Type)));
        Assertions.assertTrue(sigRef.get(PdfName.Data).isNull());
    }

    @Test
    public void copying1() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copying1_1.pdf"));
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc1.getCatalog().put(new PdfName("a"), new PdfName("b").makeIndirect(pdfDoc1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copying1_1.pdf"));

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copying1_2.pdf"));
        pdfDoc2.addNewPage();
        pdfDoc2.getDocumentInfo().getPdfObject()
                .put(new PdfName("a"), pdfDoc1.getCatalog().getPdfObject().get(new PdfName("a")).copyTo(pdfDoc2));
        pdfDoc2.close();
        pdfDoc1.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copying1_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary trailer = pdfDocument.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfName b = info.getAsName(new PdfName("a"));
        assertEquals("/b", b.toString());
        pdfDocument.close();
    }

    @Test
    public void copying2() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copying2_1.pdf"));
        for (int i = 0; i < 10; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            page1.getContentStream(0).getOutputStream()
                    .write(ByteUtils.getIsoBytes("%page " + String.valueOf(i + 1) + "\n"));
            page1.flush();
        }
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copying2_1.pdf"));

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copying2_2.pdf"));
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                pdfDoc2.addPage(pdfDoc1.getPage(i + 1).copyTo(pdfDoc2));
            }
        }
        pdfDoc2.close();
        pdfDoc1.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copying2_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        for (int i = 0; i < 5; i++) {
            byte[] bytes = pdfDocument.getPage(i + 1).getContentBytes();
            assertEquals("%page " + String.valueOf(i * 2 + 1) + "\n", new String(bytes));
        }
        pdfDocument.close();

    }

    @Test
    public void copying3() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copying3_1.pdf"));

        PdfDictionary helloWorld = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        PdfDictionary helloWorld1 = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        helloWorld.put(new PdfName("HelloWrld"), helloWorld);
        helloWorld.put(new PdfName("HelloWrld1"), helloWorld1);
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.getPdfObject().put(new PdfName("HelloWorldClone"), (PdfObject) helloWorld.clone());

        pdfDoc.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copying3_1.pdf");
        pdfDoc = new PdfDocument(reader);
        assertFalse(reader.hasRebuiltXref(), "Rebuilt");

        PdfDictionary dic0 = pdfDoc.getPage(1).getPdfObject().getAsDictionary(new PdfName("HelloWorld"));
        assertEquals(4, dic0.getIndirectReference().getObjNumber());
        assertEquals(0, dic0.getIndirectReference().getGenNumber());

        PdfDictionary dic1 = pdfDoc.getPage(1).getPdfObject().getAsDictionary(new PdfName("HelloWorldClone"));
        assertEquals(8, dic1.getIndirectReference().getObjNumber());
        assertEquals(0, dic1.getIndirectReference().getGenNumber());

        PdfString str0 = dic0.getAsString(new PdfName("Hello"));
        PdfString str1 = dic1.getAsString(new PdfName("Hello"));
        assertEquals(str0.getValue(), str1.getValue());
        assertEquals(str0.getValue(), "World");

        PdfDictionary dic01 = dic0.getAsDictionary(new PdfName("HelloWrld"));
        PdfDictionary dic11 = dic1.getAsDictionary(new PdfName("HelloWrld"));
        assertEquals(dic01.getIndirectReference().getObjNumber(), dic11.getIndirectReference().getObjNumber());
        assertEquals(dic01.getIndirectReference().getGenNumber(), dic11.getIndirectReference().getGenNumber());
        assertEquals(dic01.getIndirectReference().getObjNumber(), 4);
        assertEquals(dic01.getIndirectReference().getGenNumber(), 0);

        PdfDictionary dic02 = dic0.getAsDictionary(new PdfName("HelloWrld1"));
        PdfDictionary dic12 = dic1.getAsDictionary(new PdfName("HelloWrld1"));
        assertEquals(dic02.getIndirectReference().getObjNumber(), dic12.getIndirectReference().getObjNumber());
        assertEquals(dic02.getIndirectReference().getGenNumber(), dic12.getIndirectReference().getGenNumber());
        assertEquals(dic12.getIndirectReference().getObjNumber(), 5);
        assertEquals(dic12.getIndirectReference().getGenNumber(), 0);

        pdfDoc.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void copyDocumentsWithFormFieldsTest() throws IOException, InterruptedException {
        String filename = sourceFolder + "fieldsOn2-sPage.pdf";

        PdfDocument sourceDoc = new PdfDocument(new PdfReader(filename));
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyDocumentsWithFormFields.pdf"));

        sourceDoc.initializeOutlines();
        sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), pdfDoc);

        sourceDoc.close();
        pdfDoc.close();

        assertNull(new CompareTool().compareByContent(destinationFolder + "copyDocumentsWithFormFields.pdf",
                sourceFolder + "cmp_copyDocumentsWithFormFields.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copySamePageWithAnnotationsSeveralTimes() throws IOException, InterruptedException {
        String filename = sourceFolder + "rotated_annotation.pdf";

        PdfDocument sourceDoc = new PdfDocument(new PdfReader(filename));
        PdfDocument pdfDoc = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + "copySamePageWithAnnotationsSeveralTimes.pdf"));

        sourceDoc.initializeOutlines();
        sourceDoc.copyPagesTo(Arrays.asList(1, 1, 1), pdfDoc);

        sourceDoc.close();
        pdfDoc.close();

        assertNull(new CompareTool().compareByContent(destinationFolder + "copySamePageWithAnnotationsSeveralTimes.pdf",
                sourceFolder + "cmp_copySamePageWithAnnotationsSeveralTimes.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyIndirectInheritablePageEntriesTest01() throws IOException, InterruptedException {
        String src = sourceFolder + "indirectPageProps.pdf";
        String filename = "copyIndirectInheritablePageEntriesTest01.pdf";
        String dest = destinationFolder + filename;
        String cmp = sourceFolder + "cmp_" + filename;
        PdfDocument outputDoc = new PdfDocument(CompareTool.createTestPdfWriter(dest));

        PdfDocument sourceDoc = new PdfDocument(new PdfReader(src));
        sourceDoc.copyPagesTo(1, 1, outputDoc);
        sourceDoc.close();

        outputDoc.close();

        assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    public void copyPageNoRotationToDocWithRotationInKidsPageTest() throws IOException, InterruptedException {
        String src = sourceFolder + "srcFileWithSetRotation.pdf";
        String dest = destinationFolder + "copyPageNoRotationToDocWithRotationInKidsPage.pdf";
        String cmp = sourceFolder + "cmp_copyPageNoRotationToDocWithRotationInKidsPage.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), CompareTool.createTestPdfWriter(dest));
        PdfDocument sourceDoc = new PdfDocument(new PdfReader(sourceFolder + "noRotationProp.pdf"));
        sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), pdfDoc);

        sourceDoc.close();

        pdfDoc.close();

        assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder));
    }

    @Test
    //TODO: update cmp-files when DEVSIX-3635 will be fixed
    public void copyPageNoRotationToDocWithRotationInPagesDictTest() throws IOException, InterruptedException {
        String src = sourceFolder + "indirectPageProps.pdf";
        String dest = destinationFolder + "copyPageNoRotationToDocWithRotationInPagesDict.pdf";
        String cmp = sourceFolder + "cmp_copyPageNoRotationToDocWithRotationInPagesDict.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), CompareTool.createTestPdfWriter(dest));
        PdfDocument sourceDoc = new PdfDocument(new PdfReader(sourceFolder + "noRotationProp.pdf"));
        sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), pdfDoc);

        sourceDoc.close();

        pdfDoc.close();

        assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder));
    }

    @Test
    public void copyPageWithRotationInPageToDocWithRotationInPagesDictTest() throws IOException, InterruptedException {
        String src = sourceFolder + "indirectPageProps.pdf";
        String dest = destinationFolder + "copyPageWithRotationInPageToDocWithRotationInPagesDict.pdf";
        String cmp = sourceFolder + "cmp_copyPageWithRotationInPageToDocWithRotationInPagesDict.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), CompareTool.createTestPdfWriter(dest));
        PdfDocument sourceDoc = new PdfDocument(
                new PdfReader(sourceFolder + "srcFileCopyPageWithSetRotationValueInKids.pdf"));
        sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), pdfDoc);

        sourceDoc.close();

        pdfDoc.close();

        assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder));
    }

    @Test
    public void copySelfContainedObject() throws IOException {
        ByteArrayOutputStream inputBytes = new ByteArrayOutputStream();
        PdfDocument prepInputDoc = new PdfDocument(new PdfWriter(inputBytes));
        PdfDictionary selfContainedDict = new PdfDictionary();
        PdfName randDictName = PdfName.Sound;
        PdfName randEntry1 = PdfName.R;
        PdfName randEntry2 = PdfName.S;
        selfContainedDict.put(randEntry1, selfContainedDict);
        selfContainedDict.put(randEntry2, selfContainedDict);
        prepInputDoc.addNewPage().put(randDictName, selfContainedDict.makeIndirect(prepInputDoc));
        prepInputDoc.close();

        PdfDocument srcDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(inputBytes.toByteArray())));
        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copySelfContainedObject.pdf"));

        srcDoc.copyPagesTo(1, 1, destDoc);

        PdfDictionary destPageObj = destDoc.getFirstPage().getPdfObject();
        PdfDictionary destSelfContainedDict = destPageObj.getAsDictionary(randDictName);
        PdfDictionary destSelfContainedDictR = destSelfContainedDict.getAsDictionary(randEntry1);
        PdfDictionary destSelfContainedDictS = destSelfContainedDict.getAsDictionary(randEntry2);

        Assertions.assertEquals(destSelfContainedDict.getIndirectReference(),
                destSelfContainedDictR.getIndirectReference());
        Assertions.assertEquals(destSelfContainedDict.getIndirectReference(),
                destSelfContainedDictS.getIndirectReference());

        destDoc.close();
        srcDoc.close();
    }

    @Test
    public void copyDifferentRangesOfPagesWithBookmarksTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "copyDifferentRangesOfPagesWithBookmarksTest.pdf";
        String cmpFileName = sourceFolder + "cmp_copyDifferentRangesOfPagesWithBookmarksTest.pdf";
        PdfDocument targetPdf = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
        targetPdf.initializeOutlines();

        PdfDocument sourcePdf = new PdfDocument(new PdfReader(sourceFolder + "sameDocWithBookmarksPdf.pdf"));
        sourcePdf.initializeOutlines();

        int sourcePdfLength = sourcePdf.getNumberOfPages();
        int sourcePdfOutlines = sourcePdf.getOutlines(false).getAllChildren().size();

        sourcePdf.copyPagesTo(3, sourcePdfLength, targetPdf);
        sourcePdf.copyPagesTo(1, 2, targetPdf);

        int targetOutlines = targetPdf.getOutlines(false).getAllChildren().size();

        Assertions.assertEquals(sourcePdfOutlines, targetOutlines);

        sourcePdf.close();
        targetPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-577. Update cmp
    public void copyPagesLinkAnnotationTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "copyPagesLinkAnnotationTest.pdf";
        String cmpFileName = sourceFolder + "cmp_copyPagesLinkAnnotationTest.pdf";
        PdfDocument targetPdf = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));

        PdfDocument linkAnotPdf = new PdfDocument(new PdfReader(sourceFolder + "pdfLinkAnnotationTest.pdf"));

        int linkPdfLength = linkAnotPdf.getNumberOfPages();

        linkAnotPdf.copyPagesTo(3, linkPdfLength, targetPdf);
        linkAnotPdf.copyPagesTo(1, 2, targetPdf);

        List<PdfAnnotation> annotations = getPdfAnnotations(targetPdf);
        Assertions.assertEquals(0,  annotations.size(), "The number of merged annotations are not the same.");

        linkAnotPdf.close();
        targetPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void objRefAsStreamCopyTest() throws IOException, InterruptedException {
        String pdf = sourceFolder + "objRefAsStream.pdf";
        String outPdf = destinationFolder + "objRefAsStreamCopy.pdf";
        String cmpPdf = sourceFolder + "cmp_objRefAsStreamCopy.pdf";

        PdfDocument pdfFile = new PdfDocument(new PdfReader(pdf));
        PdfDocument copiedFile = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        copiedFile.setTagged();
        pdfFile.copyPagesTo(1, 1, copiedFile);

        pdfFile.close();
        copiedFile.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    public void copyDocWithFullDDictionary() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "copyDocWithDDictionary.pdf";
        String cmpFileName = sourceFolder + "cmp_copyDocWithDDictionary.pdf";

        PdfDocument inPdf = new PdfDocument(new PdfReader(sourceFolder + "DocWithDDictionary.pdf"));
        PdfDocument outPdf = new PdfDocument(new PdfWriter(outFileName));

        inPdf.copyPagesTo(1, 1, outPdf);

        inPdf.close();
        outPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    private List<PdfAnnotation> getPdfAnnotations(PdfDocument pdfDoc) {
        int number = pdfDoc.getNumberOfPages();
        ArrayList<PdfAnnotation> annotations = new ArrayList<>();

        for(int i = 1; i <= number; i++){
            annotations.addAll(pdfDoc.getPage(i).getAnnotations());
        }

        return annotations;
    }
}
