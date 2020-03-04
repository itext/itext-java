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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.Element;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Category(IntegrationTest.class)
public class PdfPagesTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfPagesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfPagesTest/";
    static final PdfName PageNum = new PdfName("PageNum");
    static final PdfName PageNum5 = new PdfName("PageNum");
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void simplePagesTest() throws IOException {
        String filename = "simplePagesTest.pdf";
        int pageCount = 111;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));

        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(i + 1));
            page.flush();
        }
        pdfDoc.close();
        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void reversePagesTest() throws IOException {
        String filename = "reversePagesTest.pdf";
        int pageCount = 111;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));

        for (int i = pageCount; i > 0; i--) {
            PdfPage page = new PdfPage(pdfDoc, pdfDoc.getDefaultPageSize());
            pdfDoc.addPage(1, page);
            page.getPdfObject().put(PageNum, new PdfNumber(i));
            page.flush();
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void reversePagesTest2() throws Exception {
        String filename = "1000PagesDocument_reversed.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "1000PagesDocument.pdf"),
                new PdfWriter(destinationFolder + filename));
        int n = pdfDoc.getNumberOfPages();
        for (int i = n - 1; i > 0; --i) {
            pdfDoc.movePage(i, n + 1);
        }
        pdfDoc.close();
        new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff");
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

        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));
        PdfPage[] pages = new PdfPage[pageCount];

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = document.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(indexes[i]));
            //page.flush();
            pages[indexes[i] - 1] = page;
        }

        int testPageXref = document.getPage(1000).getPdfObject().getIndirectReference().getObjNumber();
        document.movePage(1000, 1000);
        Assert.assertEquals(testPageXref, document.getPage(1000).getPdfObject().getIndirectReference().getObjNumber());

        for (int i = 0; i < pages.length; i++) {
            Assert.assertTrue("Move page", document.movePage(pages[i], i + 1));
        }
        document.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void randomNumberPagesTest() throws IOException {
        String filename = "randomNumberPagesTest.pdf";
        int pageCount = 3000;
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

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));

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
            Assert.assertTrue(verifyIntegrity(pdfDoc.getCatalog().getPageTree()) == -1);
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED)
    })
    public void insertFlushedPageTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        boolean error = false;
        try {
            page.flush();
            pdfDoc.removePage(page);
            pdfDoc.addPage(1, page);
            pdfDoc.close();
        } catch (PdfException e) {
            if (PdfException.FlushedPageCannotBeAddedOrInserted.equals(e.getMessage())) {
                error = true;
            }
        }

        Assert.assertTrue(error);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED)
    })
    public void addFlushedPageTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        boolean error = false;
        try {
            page.flush();
            pdfDoc.removePage(page);
            pdfDoc.addPage(page);
            pdfDoc.close();
        } catch (PdfException e) {
            if (PdfException.FlushedPageCannotBeAddedOrInserted.equals(e.getMessage())) {
                error = true;
            }
        }

        Assert.assertTrue(error);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED, count = 2)
    })
    public void removeFlushedPage() throws IOException {
        String filename = "removeFlushedPage.pdf";
        int pageCount = 10;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage removedPage = pdfDoc.addNewPage();
        int removedPageObjectNumber = removedPage.getPdfObject().getIndirectReference().getObjNumber();
        removedPage.flush();
        pdfDoc.removePage(removedPage);

        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(i + 1));
            page.flush();
        }

        Assert.assertTrue("Remove last page", pdfDoc.removePage(pdfDoc.getPage(pageCount)));
        Assert.assertFalse("Free reference", pdfDoc.getXref().get(removedPageObjectNumber).checkState(PdfObject.FREE));

        pdfDoc.close();
        verifyPagesOrder(destinationFolder + filename, pageCount - 1);
    }

    void verifyPagesOrder(String filename, int numOfPages) throws IOException {
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());

        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertNotNull(page);
            PdfNumber number = page.getAsNumber(PageNum5);
            Assert.assertEquals("Page number", i, number.intValue());
        }

        Assert.assertEquals("Number of pages", numOfPages, pdfDocument.getNumberOfPages());
        pdfDocument.close();
    }

    int verifyIntegrity(PdfPagesTree pagesTree) {
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

    @Test
    public void testInheritedResources() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleInheritedResources.pdf"));
        PdfPage page = pdfDocument.getPage(1);
        PdfDictionary dict = page.getResources().getResource(PdfName.ExtGState);
        Assert.assertEquals(2, dict.size());
        PdfExtGState gState = new PdfExtGState((PdfDictionary) dict.get(new PdfName("Gs1")));
        Assert.assertEquals(10, gState.getLineWidth().intValue());
    }

    @Test
    public void readFormXObjectsWithCircularReferencesInResources() throws IOException {

        // given input file contains circular reference in resources of form xobjects
        // (form xobjects are nested inside each other)
        String input = sourceFolder + "circularReferencesInResources.pdf";

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
        Assert.assertEquals(2, formXObjects.size());
    }

    @Test
    public void testInheritedResourcesUpdate() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(sourceFolder + "simpleInheritedResources.pdf"),
                new PdfWriter(destinationFolder + "updateInheritedResources.pdf")
                        .setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfName newGsName = pdfDoc.getPage(1).getResources().addExtGState(new PdfExtGState().setLineWidth(30));
        int gsCount = pdfDoc.getPage(1).getResources().getResource(PdfName.ExtGState).size();
        pdfDoc.close();
        String compareResult = new CompareTool().compareByContent(
                destinationFolder + "updateInheritedResources.pdf",
                sourceFolder + "cmp_" + "updateInheritedResources.pdf",
                destinationFolder, "diff");

        Assert.assertEquals(3, gsCount);
        Assert.assertEquals("Gs3", newGsName.getValue());
        Assert.assertNull(compareResult);
    }

    @Test
    //TODO: DEVSIX-1643 Inherited resources aren't copied on page reordering
    public void reorderInheritedResourcesTest() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(sourceFolder + "inheritedFontResources.pdf"),
                new PdfWriter(destinationFolder + "reorderInheritedFontResources.pdf")
        );
        pdfDoc.movePage(1, pdfDoc.getNumberOfPages() + 1);
        pdfDoc.removePage(1);
        pdfDoc.close();
        String compareResult = new CompareTool().compareByContent(
                destinationFolder + "reorderInheritedFontResources.pdf",
                sourceFolder + "cmp_reorderInheritedFontResources.pdf",
                destinationFolder, "diff_reorderInheritedFontResources_");
        Assert.assertNull(compareResult);
    }

    @Test
    public void getPageByDictionary() throws IOException {
        String filename = sourceFolder + "1000PagesDocument.pdf";
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
            Assert.assertEquals(PdfName.Page, pageDictionary.get(PdfName.Type));
            PdfPage page = pdfDoc.getPage(pageDictionary);
            Assert.assertEquals(pageDictionary, page.getPdfObject());
        }
        pdfDoc.close();
    }

    @Test
    public void removePageWithFormFieldsTest() throws IOException {
        String filename = sourceFolder + "docWithFields.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        pdfDoc.removePage(1);

        PdfArray fields = pdfDoc.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm)
                .getAsArray(PdfName.Fields);
        PdfDictionary field = (PdfDictionary) fields.get(0);
        PdfDictionary kid = (PdfDictionary) field.getAsArray(PdfName.Kids).get(0);
        Assert.assertEquals(6, kid.keySet().size());
        Assert.assertEquals(3, fields.size());

        pdfDoc.close();
    }

    @Test
    public void getPageSizeWithInheritedMediaBox() throws IOException {
        double eps = 0.0000001;
        String filename = sourceFolder + "inheritedMediaBox.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        Assert.assertEquals(0, pdfDoc.getPage(1).getPageSize().getLeft(), eps);
        Assert.assertEquals(0, pdfDoc.getPage(1).getPageSize().getBottom(), eps);
        Assert.assertEquals(595, pdfDoc.getPage(1).getPageSize().getRight(), eps);
        Assert.assertEquals(842, pdfDoc.getPage(1).getPageSize().getTop(), eps);

        pdfDoc.close();
    }

    @Test
    public void pageThumbnailTest() throws Exception {
        String filename = "pageThumbnail.pdf";
        String imageSrc = "icon.jpg";
        PdfDocument pdfDoc = new PdfDocument(
                new PdfWriter(destinationFolder + filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfPage page = pdfDoc.addNewPage()
                .setThumbnailImage(new PdfImageXObject(ImageDataFactory.create(sourceFolder + imageSrc)));
        new PdfCanvas(page).setFillColor(ColorConstants.RED).rectangle(100, 100, 400, 400).fill();
        pdfDoc.close();
        new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff");
    }

    @Test
    public void rotationPagesRotationTest() throws IOException {
        String filename = "singlePageDocumentWithRotation.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + filename));
        PdfPage page = pdfDoc.getPage(1);
        Assert.assertEquals("Inherited value is invalid", 90, page.getRotation());
    }

    @Test
    public void pageTreeCleanupParentRefTest() throws IOException {
        String src = sourceFolder + "CatalogWithPageAndPagesEntries.pdf";
        String dest = destinationFolder + "CatalogWithPageAndPagesEntries_opened.pdf";
        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();

        Assert.assertTrue(testPageTreeParentsValid(src) && testPageTreeParentsValid(dest));
    }

    @Test
    public void pdfNumberInPageContentArrayTest() throws IOException {
        String src = sourceFolder + "pdfNumberInPageContentArray.pdf";
        String dest = destinationFolder + "pdfNumberInPageContentArray_saved.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        pdfDoc.close();

        // test is mainly to ensure document is successfully opened-and-closed without exceptions

        pdfDoc = new PdfDocument(new PdfReader(dest));
        PdfObject pageDictWithInvalidContents = pdfDoc.getPdfObject(10);
        PdfArray invalidContentsArray = ((PdfDictionary) pageDictWithInvalidContents).getAsArray(PdfName.Contents);
        Assert.assertEquals(5, invalidContentsArray.size());

        Assert.assertFalse(invalidContentsArray.get(0).isStream());
        Assert.assertFalse(invalidContentsArray.get(1).isStream());
        Assert.assertFalse(invalidContentsArray.get(2).isStream());
        Assert.assertFalse(invalidContentsArray.get(3).isStream());
        Assert.assertTrue(invalidContentsArray.get(4).isStream());
    }

    private boolean testPageTreeParentsValid(String src) throws com.itextpdf.io.IOException, java.io.IOException {
        boolean valid = true;
        PdfReader reader = new PdfReader(src);
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
        PdfDocument inputPdf = new PdfDocument(new PdfReader(sourceFolder + "input500.pdf"));
        PdfDocument outputPdf = new PdfDocument(new PdfWriter(destinationFolder + "output500.pdf"));

        float scaleX = 595f / 612f;
        float scaleY = 842f / 792f;

        for (int i = 1; i <= inputPdf.getNumberOfPages(); ++i) {
            PdfPage sourcePage = inputPdf.getPage(i);
            PdfFormXObject pageCopy = sourcePage.copyAsFormXObject(outputPdf);
            PdfPage page = outputPdf.addNewPage(PageSize.A4);
            PdfCanvas outputCanvas = new PdfCanvas(page);
            outputCanvas.addXObject(pageCopy, scaleX, 0, 0, scaleY, 0, 0);
            page.flush();
        }

        outputPdf.close();
        inputPdf.close();

        Assert.assertNotNull(outputPdf.getXref());
        Assert.assertEquals(500, outputPdf.getXref().size() - inputPdf.getXref().size());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.WRONG_MEDIABOX_SIZE_TOO_MANY_ARGUMENTS, count = 1),
    })
    public void pageGetMediaBoxTooManyArgumentsTest() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "helloWorldMediaboxTooManyArguments.pdf");
        Rectangle expected = new Rectangle(0, 0, 375, 300);

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage pageOne = pdfDoc.getPage(1);
        Rectangle actual = pageOne.getPageSize();

        Assert.assertTrue(expected.equalsWithEpsilon(actual));

    }

    @Test
    public void pageGetMediaBoxNotEnoughArgumentsTest() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(MessageFormatUtil.format(PdfException.WRONGMEDIABOXSIZETOOFEWARGUMENTS, 3));

        PdfReader reader = new PdfReader(sourceFolder + "helloWorldMediaboxNotEnoughArguments.pdf");

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage pageOne = pdfDoc.getPage(1);
        Rectangle actual = pageOne.getPageSize();

        Assert.fail("Exception was not thrown");
    }

    @Test
    public void insertIntermediateParentTest() throws IOException {
        String filename = "insertIntermediateParentTest.pdf";
        PdfReader reader = new PdfReader(sourceFolder + filename);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        PdfPage page = pdfDoc.getFirstPage();

        PdfPages pdfPages = new PdfPages(page.parentPages.getFrom(), pdfDoc, page.parentPages);
        page.parentPages.getKids().set(0, pdfPages.getPdfObject());
        page.parentPages.decrementCount();
        pdfPages.addPage(page.getPdfObject());

        pdfDoc.close();

        Assert.assertTrue(page.getPdfObject().isModified());
    }

    @Test
    public void verifyPagesAreNotReadOnOpenTest() throws IOException {
        String srcFile = sourceFolder + "taggedOnePage.pdf";
        CustomPdfReader reader = new CustomPdfReader(srcFile);
        PdfDocument document = new PdfDocument(reader);
        document.close();
        Assert.assertFalse(reader.pagesAreRead);
    }

    @Test
    public void readPagesInBlocksTest() throws IOException {
        String srcFile = sourceFolder + "docWithBalancedPageTree.pdf";
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

        Assert.assertEquals(111, document.getNumberOfPages());
        Assert.assertEquals(10, maxAmountOfPagesReadAtATime);

        document.close();
    }

    @Test
    public void readSinglePageTest() throws IOException {
        String srcFile = sourceFolder + "allPagesAreLeaves.pdf";
        CustomPdfReader reader = new CustomPdfReader(srcFile);
        reader.setMemorySavingMode(true);
        PdfDocument document = new PdfDocument(reader);
        int amountOfPages = document.getNumberOfPages();

        PdfPages pdfPages = document.catalog.getPageTree().getRoot();
        PdfArray pageIndRefArray = ((PdfDictionary) pdfPages.getPdfObject()).getAsArray(PdfName.Kids);

        document.getPage(amountOfPages);
        Assert.assertEquals(1, getAmountOfReadPages(pageIndRefArray));

        document.getPage(amountOfPages / 2);
        Assert.assertEquals(2, getAmountOfReadPages(pageIndRefArray));

        document.getPage(1);
        Assert.assertEquals(3, getAmountOfReadPages(pageIndRefArray));

        document.close();
    }

    @Test
    public void implicitPagesTreeRebuildingTest() throws IOException, InterruptedException {
        String inFileName = sourceFolder + "implicitPagesTreeRebuilding.pdf";
        String outFileName = destinationFolder + "implicitPagesTreeRebuilding.pdf";
        String cmpFileName = sourceFolder + "cmp_implicitPagesTreeRebuilding.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName), new PdfWriter(outFileName));
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName,cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE)})
    public void brokenPageTreeWithExcessiveLastPageTest() throws IOException {
        String inFileName = sourceFolder + "brokenPageTreeNullLast.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(4);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE)})
    public void brokenPageTreeWithExcessiveMiddlePageTest() throws IOException {
        String inFileName = sourceFolder + "brokenPageTreeNullMiddle.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(3);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE, count = 7)})
    public void brokenPageTreeWithExcessiveMultipleNegativePagesTest() throws IOException {
        String inFileName = sourceFolder + "brokenPageTreeNullMultipleSequence.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(2, 3, 4, 6, 7, 8, 9);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE, count = 2)})
    public void brokenPageTreeWithExcessiveRangeNegativePagesTest() throws IOException {
        String inFileName = sourceFolder + "brokenPageTreeNullRangeNegative.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        List<Integer> pages = Arrays.asList(2, 4);
        Set<Integer> nullPages = new HashSet<>(pages);

        findAndAssertNullPages(pdfDocument, nullPages);
    }

    private static void findAndAssertNullPages(PdfDocument pdfDocument, Set<Integer> nullPages) {
            for (Integer e : nullPages) {
                Assert.assertNull(pdfDocument.getPage((int) e));
            }
    }

    private int getAmountOfReadPages(PdfArray pageIndRefArray) {
        int amountOfLoadedPages = 0;
        for (int i = 0; i < pageIndRefArray.size(); i++) {
            if (((PdfIndirectReference) pageIndRefArray.get(i, false)).refersTo != null) {
                amountOfLoadedPages++;
            }
        }
        return amountOfLoadedPages;
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
