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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfPagesTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfPagesTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfPagesTest/";
    private static final PdfName PageNum = new PdfName("PageNum");

    @BeforeAll
    public static void setup() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }
    
    @Test
    public void hugeNumberOfPagesWithOnePageTest() throws IOException {
         PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hugeNumberOfPagesWithOnePage.pdf"),
                 new PdfWriter(new ByteArrayOutputStream()));
         PdfPage page = new PdfPage(pdfDoc, pdfDoc.getDefaultPageSize());
         AssertUtil.doesNotThrow(() -> pdfDoc.addPage(1, page));
    }

    @Test
    public void countDontCorrespondToRealTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "countDontCorrespondToReal.pdf"),
                new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = new PdfPage(pdfDoc, pdfDoc.getDefaultPageSize());
        AssertUtil.doesNotThrow(() -> pdfDoc.addPage(1, page));

        // we don't expect that Count will be different from real number of pages
        Assertions.assertThrows(NullPointerException.class, () -> pdfDoc.close());
    }

    @Test
    public void simplePagesTest() throws IOException {
        String filename = "simplePagesTest.pdf";
        int pageCount = 111;

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));

        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(i + 1));
            page.flush();
        }
        pdfDoc.close();
        verifyPagesOrder(DESTINATION_FOLDER + filename, pageCount);
    }

    @Test
    public void reversePagesTest() throws IOException {
        String filename = "reversePagesTest.pdf";
        int pageCount = 111;

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));

        for (int i = pageCount; i > 0; i--) {
            PdfPage page = new PdfPage(pdfDoc, pdfDoc.getDefaultPageSize());
            pdfDoc.addPage(1, page);
            page.getPdfObject().put(PageNum, new PdfNumber(i));
            page.flush();
        }
        pdfDoc.close();

        verifyPagesOrder(DESTINATION_FOLDER + filename, pageCount);
    }

    @Test
    public void reversePagesTest2() throws Exception {
        String filename = "1000PagesDocument_reversed.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "1000PagesDocument.pdf"),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        int n = pdfDoc.getNumberOfPages();
        for (int i = n - 1; i > 0; --i) {
            pdfDoc.movePage(i, n + 1);
        }
        pdfDoc.close();
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER,
                        "diff"));
    }

    @Test
    public void randomObjectPagesTest() throws IOException {
        String filename = "randomObjectPagesTest.pdf";
        int pageCount = 10000;
        int[] indexes = new int[pageCount];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i + 1;
        }

        Random rnd = new Random();
        for (int i = indexes.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = indexes[index];
            indexes[index] = indexes[i];
            indexes[i] = a;
        }

        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        PdfPage[] pages = new PdfPage[pageCount];

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = document.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(indexes[i]));
            //page.flush();
            pages[indexes[i] - 1] = page;
        }

        int testPageXref = document.getPage(1000).getPdfObject().getIndirectReference().getObjNumber();
        document.movePage(1000, 1000);
        Assertions.assertEquals(testPageXref, document.getPage(1000).getPdfObject().getIndirectReference().getObjNumber());

        for (int i = 0; i < pages.length; i++) {
            Assertions.assertTrue(document.movePage(pages[i], i + 1), "Move page");
        }
        document.close();

        verifyPagesOrder(DESTINATION_FOLDER + filename, pageCount);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-8114 Fix randomNumberPagesTest test)
    public void randomNumberPagesTest() throws IOException {
        String filename = "randomNumberPagesTest.pdf";
        int pageCount = 1000;
        int[] indexes = new int[pageCount];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i + 1;
        }

        Random rnd = new Random();
        for (int i = indexes.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = indexes[index];
            indexes[index] = indexes[i];
            indexes[i] = a;
        }

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(indexes[i]));
        }

        for (int i = 1; i < pageCount; i++) {
            for (int j = i + 1; j <= pageCount; j++) {
                int j_page = pdfDoc.getPage(j).getPdfObject().getAsNumber(PageNum).intValue();
                int i_page = pdfDoc.getPage(i).getPdfObject().getAsNumber(PageNum).intValue();
                if (j_page < i_page) {
                    pdfDoc.movePage(i, j);
                    pdfDoc.movePage(j, i);
                }
            }
            Assertions.assertTrue(verifyIntegrity(pdfDoc.getCatalog().getPageTree()) == -1);
        }
        pdfDoc.close();

        verifyPagesOrder(DESTINATION_FOLDER + filename, pageCount);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED)
    })
    public void insertFlushedPageTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.removePage(page);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc.addPage(1, page)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_ADDED_OR_INSERTED, e.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED)
    })
    public void addFlushedPageTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.removePage(page);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc.addPage(page)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_ADDED_OR_INSERTED, e.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED, count = 2)
    })
    public void removeFlushedPage() throws IOException {
        String filename = "removeFlushedPage.pdf";
        int pageCount = 10;

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));

        PdfPage removedPage = pdfDoc.addNewPage();
        int removedPageObjectNumber = removedPage.getPdfObject().getIndirectReference().getObjNumber();
        removedPage.flush();
        pdfDoc.removePage(removedPage);

        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(i + 1));
            page.flush();
        }

        Assertions.assertTrue(pdfDoc.removePage(pdfDoc.getPage(pageCount)), "Remove last page");
        Assertions.assertFalse(pdfDoc.getXref().get(removedPageObjectNumber).checkState(PdfObject.FREE), "Free reference");

        pdfDoc.close();
        verifyPagesOrder(DESTINATION_FOLDER + filename, pageCount - 1);
    }

    @Test
    public void removeFlushedPageFromTaggedDocument() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.setTagged();
            pdfDocument.addNewPage();
            pdfDocument.getPage(1).flush();

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> pdfDocument.removePage(1)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_REMOVED, e.getMessage());
        }
    }

    @Test
    public void removeFlushedPageFromDocumentWithAcroForm() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.getCatalog().put(PdfName.AcroForm, new PdfDictionary());
            pdfDocument.addNewPage();
            pdfDocument.getPage(1).flush();

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> pdfDocument.removePage(1)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_REMOVED, e.getMessage());
        }
    }

    @Test
    public void testInheritedResources() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "simpleInheritedResources.pdf"));
        PdfPage page = pdfDocument.getPage(1);
        PdfDictionary dict = page.getResources().getResource(PdfName.ExtGState);
        Assertions.assertEquals(2, dict.size());
        PdfExtGState gState = new PdfExtGState((PdfDictionary) dict.get(new PdfName("Gs1")));
        Assertions.assertEquals(10, gState.getLineWidth().intValue());
    }

    @Test
    public void readFormXObjectsWithCircularReferencesInResources() throws IOException {

        // given input file contains circular reference in resources of form xobjects
        // (form xobjects are nested inside each other)
        String input = SOURCE_FOLDER + "circularReferencesInResources.pdf";

        PdfReader reader1 = new PdfReader(input);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfPage page = inputPdfDoc1.getPage(1);
        PdfResources resources = page.getResources();
        List<PdfFormXObject> formXObjects = new ArrayList<>();

        // We just try to work with resources in arbitrary way and make sure that circular reference
        // doesn't block it. However it is expected that PdfResources doesn't try to "look in deep"
        // and recursively resolves resources, so this test should never meet any issues.
        for (PdfName xObjName : resources.getResourceNames(PdfName.XObject)) {
            PdfFormXObject form = resources.getForm(xObjName);
            if (form != null) {
                formXObjects.add(form);
            }
        }

        // ensure resources XObject entry is read correctly
        Assertions.assertEquals(2, formXObjects.size());
    }

    @Test
    public void testInheritedResourcesUpdate() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "simpleInheritedResources.pdf"),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "updateInheritedResources.pdf")
                        .setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfName newGsName = pdfDoc.getPage(1).getResources().addExtGState(new PdfExtGState().setLineWidth(30));
        int gsCount = pdfDoc.getPage(1).getResources().getResource(PdfName.ExtGState).size();
        pdfDoc.close();
        String compareResult = new CompareTool().compareByContent(
                DESTINATION_FOLDER + "updateInheritedResources.pdf",
                SOURCE_FOLDER + "cmp_" + "updateInheritedResources.pdf",
                DESTINATION_FOLDER, "diff");

        Assertions.assertEquals(3, gsCount);
        Assertions.assertEquals("Gs3", newGsName.getValue());
        Assertions.assertNull(compareResult);
    }

    @Test
    //TODO: DEVSIX-1643 Inherited resources aren't copied on page reordering
    public void reorderInheritedResourcesTest() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "inheritedFontResources.pdf"),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "reorderInheritedFontResources.pdf")
        );
        pdfDoc.movePage(1, pdfDoc.getNumberOfPages() + 1);
        pdfDoc.removePage(1);
        pdfDoc.close();
        String compareResult = new CompareTool().compareByContent(
                DESTINATION_FOLDER + "reorderInheritedFontResources.pdf",
                SOURCE_FOLDER + "cmp_reorderInheritedFontResources.pdf",
                DESTINATION_FOLDER, "diff_reorderInheritedFontResources_");
        Assertions.assertNull(compareResult);
    }

    @Test
    public void getPageByDictionary() throws IOException {
        String filename = SOURCE_FOLDER + "1000PagesDocument.pdf";
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfObject[] pageDictionaries = new PdfObject[] {
                pdfDoc.getPdfObject(4),
                pdfDoc.getPdfObject(255),
                pdfDoc.getPdfObject(512),
                pdfDoc.getPdfObject(1023),
                pdfDoc.getPdfObject(2049),
                pdfDoc.getPdfObject(3100)
        };

        for (PdfObject pageObject : pageDictionaries) {
            PdfDictionary pageDictionary = (PdfDictionary) pageObject;
            Assertions.assertEquals(PdfName.Page, pageDictionary.get(PdfName.Type));
            PdfPage page = pdfDoc.getPage(pageDictionary);
            Assertions.assertEquals(pageDictionary, page.getPdfObject());
        }
        pdfDoc.close();
    }

    @Test
    public void removePageWithFormFieldsTest() throws IOException, InterruptedException {
        String testName = "docWithFieldsRemovePage.pdf";
        String outPdf = DESTINATION_FOLDER + testName;
        String sourceFile = SOURCE_FOLDER + "docWithFields.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFile), CompareTool.createTestPdfWriter(outPdf))) {
            pdfDoc.removePage(1);
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_" + testName, DESTINATION_FOLDER));
    }

    @Test
    public void getPageSizeWithInheritedMediaBox() throws IOException {
        double eps = 0.0000001;
        String filename = SOURCE_FOLDER + "inheritedMediaBox.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        Assertions.assertEquals(0, pdfDoc.getPage(1).getPageSize().getLeft(), eps);
        Assertions.assertEquals(0, pdfDoc.getPage(1).getPageSize().getBottom(), eps);
        Assertions.assertEquals(595, pdfDoc.getPage(1).getPageSize().getRight(), eps);
        Assertions.assertEquals(842, pdfDoc.getPage(1).getPageSize().getTop(), eps);

        pdfDoc.close();
    }

    @Test
    public void pageThumbnailTest() throws Exception {
        String filename = "pageThumbnail.pdf";
        String imageSrc = "icon.jpg";
        PdfDocument pdfDoc = new PdfDocument(
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfPage page = pdfDoc.addNewPage()
                .setThumbnailImage(new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + imageSrc)));
        new PdfCanvas(page).setFillColor(ColorConstants.RED).rectangle(100, 100, 400, 400).fill();
        pdfDoc.close();
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER,
                        "diff"));
    }

    @Test
    public void rotationPagesRotationTest() throws IOException {
        String filename = "singlePageDocumentWithRotation.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + filename));
        PdfPage page = pdfDoc.getPage(1);
        Assertions.assertEquals(90, page.getRotation(), "Inherited value is invalid");
    }

    @Test
    public void pageTreeCleanupParentRefTest() throws IOException {
        String src = SOURCE_FOLDER + "CatalogWithPageAndPagesEntries.pdf";
        String dest = DESTINATION_FOLDER + "CatalogWithPageAndPagesEntries_opened.pdf";
        PdfReader reader = new PdfReader(src);
        PdfWriter writer = CompareTool.createTestPdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();

        Assertions.assertTrue(testPageTreeParentsValid(src) && testPageTreeParentsValid(dest));
    }

    @Test
    public void pdfNumberInPageContentArrayTest() throws IOException {
        String src = SOURCE_FOLDER + "pdfNumberInPageContentArray.pdf";
        String dest = DESTINATION_FOLDER + "pdfNumberInPageContentArray_saved.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), CompareTool.createTestPdfWriter(dest));
        pdfDoc.close();

        // test is mainly to ensure document is successfully opened-and-closed without exceptions

        pdfDoc = new PdfDocument(CompareTool.createOutputReader(dest));
        PdfObject pageDictWithInvalidContents = pdfDoc.getPdfObject(10);
        PdfArray invalidContentsArray = ((PdfDictionary) pageDictWithInvalidContents).getAsArray(PdfName.Contents);
        Assertions.assertEquals(5, invalidContentsArray.size());

        Assertions.assertFalse(invalidContentsArray.get(0).isStream());
        Assertions.assertFalse(invalidContentsArray.get(1).isStream());
        Assertions.assertFalse(invalidContentsArray.get(2).isStream());
        Assertions.assertFalse(invalidContentsArray.get(3).isStream());
        Assertions.assertTrue(invalidContentsArray.get(4).isStream());
    }

    private boolean testPageTreeParentsValid(String src) throws com.itextpdf.io.exceptions.IOException, java.io.IOException {
        boolean valid = true;
        PdfReader reader = CompareTool.createOutputReader(src);
        PdfDocument pdfDocument = new PdfDocument(reader);
        PdfDictionary page_root = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages);
        for (int x = 1; x < pdfDocument.getNumberOfPdfObjects(); x++) {
            PdfObject obj = pdfDocument.getPdfObject(x);
            if (obj != null && obj.isDictionary() && ((PdfDictionary) obj).getAsName(PdfName.Type) != null
                    && ((PdfDictionary) obj).getAsName(PdfName.Type).equals(PdfName.Pages)) {
                if (obj != page_root) {
                    PdfDictionary parent = ((PdfDictionary) obj).getAsDictionary(PdfName.Parent);
                    if (parent == null) {
                        System.out.println(obj);
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    @Test
    public void testExcessiveXrefEntriesForCopyXObject() throws IOException {
        PdfDocument inputPdf = new PdfDocument(new PdfReader(SOURCE_FOLDER + "input500.pdf"));
        PdfDocument outputPdf = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "output500.pdf"));

        float scaleX = 595f / 612f;
        float scaleY = 842f / 792f;

        for (int i = 1; i <= inputPdf.getNumberOfPages(); ++i) {
            PdfPage sourcePage = inputPdf.getPage(i);
            PdfFormXObject pageCopy = sourcePage.copyAsFormXObject(outputPdf);
            PdfPage page = outputPdf.addNewPage(PageSize.A4);
            PdfCanvas outputCanvas = new PdfCanvas(page);
            outputCanvas.addXObjectWithTransformationMatrix(pageCopy, scaleX, 0, 0, scaleY, 0, 0);
            page.flush();
        }

        outputPdf.close();
        inputPdf.close();

        Assertions.assertNotNull(outputPdf.getXref());
        Assertions.assertEquals(500, outputPdf.getXref().size() - inputPdf.getXref().size());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.WRONG_MEDIABOX_SIZE_TOO_MANY_ARGUMENTS, count = 1),
    })
    public void pageGetMediaBoxTooManyArgumentsTest() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "helloWorldMediaboxTooManyArguments.pdf");
        Rectangle expected = new Rectangle(0, 0, 375, 300);

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage pageOne = pdfDoc.getPage(1);
        Rectangle actual = pageOne.getPageSize();

        Assertions.assertTrue(expected.equalsWithEpsilon(actual));

    }

    @Test
    public void closeDocumentWithRecursivePagesNodeReferencesThrowsExTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "recursivePagesNodeReference.pdf");
             PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        ) {
            PdfDocument pdfDocument = new PdfDocument(reader, writer);
            Exception e = Assertions.assertThrows(PdfException.class, () -> pdfDocument.close());
            Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE, 2), e.getMessage());
        }
    }

    @Test
    public void getPageWithRecursivePagesNodeReferenceInAppendModeThrowExTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "recursivePagesNodeReference.pdf");
             PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
             PdfDocument pdfDocument = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
        ) {
            Assertions.assertEquals(2, pdfDocument.getNumberOfPages());
            Assertions.assertNotNull(pdfDocument.getPage(1));
            Exception e = Assertions.assertThrows(PdfException.class, () -> pdfDocument.getPage(2));
            Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE, 2), e.getMessage());
        }
    }

    @Test
    public void closeDocumentWithRecursivePagesNodeInAppendModeDoesNotThrowsTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "recursivePagesNodeReference.pdf");
             PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
             PdfDocument pdfDocument = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
        ) {
            AssertUtil.doesNotThrow(() -> pdfDocument.close());
        }
    }

    @Test
    public void pageGetMediaBoxNotEnoughArgumentsTest() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "helloWorldMediaboxNotEnoughArguments.pdf");

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage pageOne = pdfDoc.getPage(1);

        Exception e = Assertions.assertThrows(PdfException.class, () -> pageOne.getPageSize());
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.WRONG_MEDIA_BOX_SIZE_TOO_FEW_ARGUMENTS, 3), e.getMessage());
    }

    @Test
    public void insertIntermediateParentTest() throws IOException {
        String filename = "insertIntermediateParentTest.pdf";
        PdfReader reader = new PdfReader(SOURCE_FOLDER + filename);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        PdfPage page = pdfDoc.getFirstPage();

        PdfPages pdfPages = new PdfPages(page.parentPages.getFrom(), pdfDoc, page.parentPages);
        page.parentPages.getKids().set(0, pdfPages.getPdfObject());
        page.parentPages.decrementCount();
        pdfPages.addPage(page.getPdfObject());

        pdfDoc.close();

        Assertions.assertTrue(page.getPdfObject().isModified());
    }

    @Test
    public void verifyPagesAreNotReadOnOpenTest() throws IOException {
        String srcFile = SOURCE_FOLDER + "taggedOnePage.pdf";
        CustomPdfReader reader = new CustomPdfReader(srcFile);
        PdfDocument document = new PdfDocument(reader);
        document.close();
        Assertions.assertFalse(reader.pagesAreRead);
    }

    @Test
    public void copyAnnotationWithoutSubtypeTest() throws IOException {
        try (
                ByteArrayOutputStream baos = createSourceDocumentWithEmptyAnnotation(new ByteArrayOutputStream());
                PdfDocument documentToMerge = new PdfDocument(
                        new PdfReader(
                                new RandomAccessSourceFactory().createSource(baos.toByteArray()),
                                new ReaderProperties()));
                ByteArrayOutputStream resultantBaos = new ByteArrayOutputStream();
                PdfDocument resultantDocument = new PdfDocument(new PdfWriter(resultantBaos))) {

            // We do expect that the following line will not throw any NPE
            PdfPage copiedPage = documentToMerge.getPage(1).copyTo(resultantDocument);
            Assertions.assertEquals(1, copiedPage.getAnnotations().size());
            Assertions.assertNull(copiedPage.getAnnotations().get(0).getSubtype());

            resultantDocument.addPage(copiedPage);
        }
    }

    @Test
    public void readPagesInBlocksTest() throws IOException {
        String srcFile = SOURCE_FOLDER + "docWithBalancedPageTree.pdf";
        int maxAmountOfPagesReadAtATime = 0;
        CustomPdfReader reader = new CustomPdfReader(srcFile);
        PdfDocument document = new PdfDocument(reader);
        for (int page = 1; page <= document.getNumberOfPages(); page++) {
            document.getPage(page);
            if (reader.numOfPagesRead > maxAmountOfPagesReadAtATime) {
                maxAmountOfPagesReadAtATime = reader.numOfPagesRead;
            }
            reader.numOfPagesRead = 0;
        }

        Assertions.assertEquals(111, document.getNumberOfPages());
        Assertions.assertEquals(10, maxAmountOfPagesReadAtATime);

        document.close();
    }

    @Test
    public void readSinglePageTest() throws IOException {
        String srcFile = SOURCE_FOLDER + "allPagesAreLeaves.pdf";
        CustomPdfReader reader = new CustomPdfReader(srcFile);
        reader.setMemorySavingMode(true);
        PdfDocument document = new PdfDocument(reader);
        int amountOfPages = document.getNumberOfPages();

        PdfPages pdfPages = document.catalog.getPageTree().getRoot();
        PdfArray pageIndRefArray = ((PdfDictionary) pdfPages.getPdfObject()).getAsArray(PdfName.Kids);

        document.getPage(amountOfPages);
        Assertions.assertEquals(1, getAmountOfReadPages(pageIndRefArray));

        document.getPage(amountOfPages / 2);
        Assertions.assertEquals(2, getAmountOfReadPages(pageIndRefArray));

        document.getPage(1);
        Assertions.assertEquals(3, getAmountOfReadPages(pageIndRefArray));

        document.close();
    }

    @Test
    public void implicitPagesTreeRebuildingTest() throws IOException, InterruptedException {
        String inFileName = SOURCE_FOLDER + "implicitPagesTreeRebuilding.pdf";
        String outFileName = DESTINATION_FOLDER + "implicitPagesTreeRebuilding.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_implicitPagesTreeRebuilding.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName), CompareTool.createTestPdfWriter(outFileName));
        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName,cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE)})
    public void brokenPageTreeWithExcessiveLastPageTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "brokenPageTreeNullLast.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(4);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE)})
    public void brokenPageTreeWithExcessiveMiddlePageTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "brokenPageTreeNullMiddle.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(3);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE, count = 7)})
    public void brokenPageTreeWithExcessiveMultipleNegativePagesTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "brokenPageTreeNullMultipleSequence.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(2, 3, 4, 6, 7, 8, 9);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE, count = 2)})
    public void brokenPageTreeWithExcessiveRangeNegativePagesTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "brokenPageTreeNullRangeNegative.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(2, 4);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }
    
    @Test
    public void testPageTreeGenerationWhenFirstPdfPagesHasOnePageOnly() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        int totalPageCount = PdfPagesTree.DEFAULT_LEAF_SIZE + 4;
        for (int i = 0; i < totalPageCount; i++) {
            pdfDocument.addNewPage();
        }
        Assertions.assertEquals(2, pdfDocument.getCatalog().getPageTree().getParents().size());
        Assertions.assertEquals(PdfPagesTree.DEFAULT_LEAF_SIZE,
                pdfDocument.getCatalog().getPageTree().getParents().get(0).getCount());

        // Leave only one page in the first pages tree
        for (int i = PdfPagesTree.DEFAULT_LEAF_SIZE - 1; i >= 1; i--) {
            pdfDocument.removePage(i);
        }
        Assertions.assertEquals(2, pdfDocument.getCatalog().getPageTree().getParents().size());
        Assertions.assertEquals(1,
                pdfDocument.getCatalog().getPageTree().getParents().get(0).getCount());

        // TODO DEVSIX-5575 remove expected exception and add proper assertions
        Assertions.assertThrows(NullPointerException.class, () -> pdfDocument.close());
    }

    private static void findAndAssertNullPages(PdfDocument pdfDocument, Set<Integer> nullPages) {
        for (Integer nullPage : nullPages) {
            int pageNum = (int)nullPage;
            Exception  exception = Assertions.assertThrows(PdfException.class,()-> pdfDocument.getPage(pageNum));
            Assertions.assertEquals(exception.getMessage() , MessageFormatUtil.format(
                    IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE, pageNum));
        }
    }

    private static int getAmountOfReadPages(PdfArray pageIndRefArray) {
        int amountOfLoadedPages = 0;
        for (int i = 0; i < pageIndRefArray.size(); i++) {
            if (((PdfIndirectReference) pageIndRefArray.get(i, false)).refersTo != null) {
                amountOfLoadedPages++;
            }
        }
        return amountOfLoadedPages;
    }

    private static void verifyPagesOrder(String filename, int numOfPages) throws IOException {
        PdfReader reader = CompareTool.createOutputReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(Boolean.FALSE, reader.hasRebuiltXref(), "Rebuilt");

        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assertions.assertNotNull(page);
            PdfNumber number = page.getAsNumber(PageNum);
            Assertions.assertEquals(i, number.intValue(), "Page number");
        }

        Assertions.assertEquals(numOfPages, pdfDocument.getNumberOfPages(), "Number of pages");
        pdfDocument.close();
    }

    private static int verifyIntegrity(PdfPagesTree pagesTree) {
        List<PdfPages> parents = pagesTree.getParents();
        int from = 0;
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i).getFrom() != from) {
                return i;
            }
            from = parents.get(i).getFrom() + parents.get(i).getCount();
        }
        return -1;
    }

    private static ByteArrayOutputStream createSourceDocumentWithEmptyAnnotation(ByteArrayOutputStream baos) {
        try (PdfDocument sourceDocument = new PdfDocument(new PdfWriter(baos))) {
            PdfPage page = sourceDocument.addNewPage();
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(new PdfDictionary());
            page.addAnnotation(annotation);
            return baos;
        }
    }

    private class CustomPdfReader extends PdfReader {

        public boolean pagesAreRead = false;

        public int numOfPagesRead = 0;

        public CustomPdfReader(String filename) throws IOException {
            super(filename);
        }

        @Override
        protected PdfObject readObject(PdfIndirectReference reference) {
            PdfObject toReturn = super.readObject(reference);
            if (toReturn instanceof PdfDictionary
                    && PdfName.Page.equals(((PdfDictionary) toReturn).get(PdfName.Type))) {
                numOfPagesRead++;
                pagesAreRead = true;
            }
            return toReturn;
        }
    }

}
