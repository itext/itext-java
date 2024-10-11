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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;

@Tag("IntegrationTest")
public class PdfOutlineTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfOutlineTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfOutlineTest/";


    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }
    
    @Test
    public void createSimpleDocWithOutlines() throws IOException, InterruptedException {
        String filename = "simpleDocWithOutlines.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);

        PdfPage firstPage = pdfDoc.addNewPage();
        PdfPage secondPage = pdfDoc.addNewPage();

        PdfOutline rootOutline = pdfDoc.getOutlines(false);
        PdfOutline firstOutline = rootOutline.addOutline("First Page");
        PdfOutline secondOutline = rootOutline.addOutline("Second Page");
        firstOutline.addDestination(PdfExplicitDestination.createFit(firstPage));
        secondOutline.addDestination(PdfExplicitDestination.createFit(secondPage));

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }
    
    @Test
    public void outlinesTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"));
        PdfOutline outlines = pdfDoc.getOutlines(false);
        List<PdfOutline> children = outlines.getAllChildren().get(0).getAllChildren();

        Assertions.assertEquals(outlines.getTitle(), "Outlines");
        Assertions.assertEquals(children.size(), 13);
        Assertions.assertTrue(children.get(0).getDestination() instanceof PdfStringDestination);
    }

    @Test
    public void outlinesWithPagesTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"));
        PdfPage page = pdfDoc.getPage(52);
        List<PdfOutline> pageOutlines = page.getOutlines(true);
        try {
            Assertions.assertEquals(3, pageOutlines.size());
            Assertions.assertTrue(pageOutlines.get(0).getTitle().equals("Safari"));
            Assertions.assertEquals(pageOutlines.get(0).getAllChildren().size(), 4);
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void addOutlinesToDocumentTest() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        String filename = "addOutlinesToDocumentTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.setTagged();

        PdfOutline outlines = pdfDoc.getOutlines(false);

        PdfOutline firstPage = outlines.addOutline("firstPage");
        PdfOutline firstPageChild = firstPage.addOutline("firstPageChild");
        PdfOutline secondPage = outlines.addOutline("secondPage");
        PdfOutline secondPageChild = secondPage.addOutline("secondPageChild");
        firstPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        firstPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        secondPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));
        secondPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));
        outlines.getAllChildren().get(0).getAllChildren().get(1).addOutline("testOutline", 1).addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(102)));

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void readOutlinesFromDocumentTest() throws IOException {
        String filename = SOURCE_FOLDER + "addOutlinesResult.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfOutline outlines = pdfDoc.getOutlines(false);
        try {
            Assertions.assertEquals(3, outlines.getAllChildren().size());
            Assertions.assertEquals("firstPageChild", outlines.getAllChildren().get(1).getAllChildren().get(0).getTitle());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE, count = 36))
    // TODO DEVSIX-1643: destinations are not removed along with page
    public void removePageWithOutlinesTest() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = "removePageWithOutlinesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"), CompareTool.createTestPdfWriter(
                DESTINATION_FOLDER + filename));
        // TODO DEVSIX-1643 (this causes log message errors. It's because of destinations pointing to removed page (freed reference, replaced by PdfNull))
        pdfDoc.removePage(102);

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String diffContent = compareTool.compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_");
        String diffTags = compareTool.compareTagStructures(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename);
        if (diffContent != null || diffTags != null) {
            diffContent = diffContent != null ? diffContent : "";
            diffTags = diffTags != null ? diffTags : "";
            Assertions.fail(diffContent + diffTags);
        }
    }

    @Test
    public void readRemovedPageWithOutlinesTest() throws IOException {
        // TODO DEVSIX-1643: src document is taken from the previous removePageWithOutlinesTest test, however it contains numerous destination objects which contain PdfNull instead of page reference
        String filename = SOURCE_FOLDER + "removePagesWithOutlinesResult.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfPage page = pdfDoc.getPage(102);
        List<PdfOutline> pageOutlines = page.getOutlines(false);
        try {
            Assertions.assertEquals(4, pageOutlines.size());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void updateOutlineTitle() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        String filename = "updateOutlineTitle.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        outlines.getAllChildren().get(0).getAllChildren().get(1).setTitle("New Title");

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void getOutlinesInvalidParentLink() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "outlinesInvalidParentLink.pdf");
        String filename = "updateOutlineTitleInvalidParentLink.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfOutline outlines = pdfDoc.getOutlines(true);
        PdfOutline firstOutline = outlines.getAllChildren().get(0);
        PdfOutline secondOutline = outlines.getAllChildren().get(1);
        try {
            Assertions.assertEquals(2, outlines.getAllChildren().size());
            Assertions.assertEquals("First Page", firstOutline.getTitle());
            Assertions.assertEquals(outlines, firstOutline.getParent());
            Assertions.assertEquals("Second Page", secondOutline.getTitle());
            Assertions.assertEquals(outlines, secondOutline.getParent());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void readOutlineTitle() throws IOException {
        String filename = SOURCE_FOLDER + "updateOutlineTitleResult.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfOutline outlines = pdfDoc.getOutlines(false);
        PdfOutline outline = outlines.getAllChildren().get(0).getAllChildren().get(1);
        try {
            Assertions.assertEquals("New Title", outline.getTitle());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void addOutlineInNotOutlineMode() throws IOException, InterruptedException {
        String filename = "addOutlineInNotOutlineMode.pdf";
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = new PdfOutline(pdfDoc);

        PdfOutline firstPage = outlines.addOutline("firstPage");
        PdfOutline firstPageChild = firstPage.addOutline("firstPageChild");
        PdfOutline secondPage = outlines.addOutline("secondPage");
        PdfOutline secondPageChild = secondPage.addOutline("secondPageChild");
        firstPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        firstPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        secondPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));
        secondPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void readOutlineAddedInNotOutlineMode() throws IOException {
        String filename = SOURCE_FOLDER + "addOutlinesWithoutOutlineModeResult.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        List<PdfOutline> pageOutlines = pdfDoc.getPage(102).getOutlines(true);
        try {
            Assertions.assertEquals(5, pageOutlines.size());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void createDocWithOutlines() throws IOException {
        String filename = SOURCE_FOLDER + "documentWithOutlines.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfOutline outlines = pdfDoc.getOutlines(false);
        try {
            Assertions.assertEquals(2, outlines.getAllChildren().size());
            Assertions.assertEquals("First Page", outlines.getAllChildren().get(0).getTitle());
        } finally {
            pdfDoc.close();
        }
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void copyPagesWithOutlines() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "copyPagesWithOutlines01.pdf");

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(writer);

        List<Integer> pages = new ArrayList<>();
        pages.add(1);
        pages.add(2);
        pages.add(3);
        pages.add(5);
        pages.add(52);
        pages.add(102);
        pdfDoc1.initializeOutlines();
        pdfDoc.copyPagesTo(pages, pdfDoc1);
        pdfDoc.close();

        Assertions.assertEquals(6, pdfDoc1.getNumberOfPages());
        Assertions.assertEquals(4, pdfDoc1.getOutlines(false).getAllChildren().get(0).getAllChildren().size());
        pdfDoc1.close();
    }

    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "outlinesWithNamedDestinations01.pdf";

        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(filename);

        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfArray array1 = new PdfArray();
        array1.add(pdfDoc.getPage(2).getPdfObject());
        array1.add(PdfName.XYZ);
        array1.add(new PdfNumber(36));
        array1.add(new PdfNumber(806));
        array1.add(new PdfNumber(0));

        PdfArray array2 = new PdfArray();
        array2.add(pdfDoc.getPage(3).getPdfObject());
        array2.add(PdfName.XYZ);
        array2.add(new PdfNumber(36));
        array2.add(new PdfNumber(806));
        array2.add(new PdfNumber(1.25));

        PdfArray array3 = new PdfArray();
        array3.add(pdfDoc.getPage(4).getPdfObject());
        array3.add(PdfName.XYZ);
        array3.add(new PdfNumber(36));
        array3.add(new PdfNumber(806));
        array3.add(new PdfNumber(1));

        pdfDoc.addNamedDestination("test1", array2);
        pdfDoc.addNamedDestination("test2", array3);
        pdfDoc.addNamedDestination("test3", array1);

        PdfOutline root = pdfDoc.getOutlines(false);

        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("test1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("test2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("test3")));
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, SOURCE_FOLDER + "cmp_outlinesWithNamedDestinations01.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addOutlinesWithNamedDestinations02() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "outlinesWithNamedDestinations02.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        PdfArray array1 = new PdfArray();
        array1.add(pdfDoc.addNewPage().getPdfObject());
        array1.add(PdfName.XYZ);
        array1.add(new PdfNumber(36));
        array1.add(new PdfNumber(806));
        array1.add(new PdfNumber(0));

        PdfArray array2 = new PdfArray();
        array2.add(pdfDoc.addNewPage().getPdfObject());
        array2.add(PdfName.XYZ);
        array2.add(new PdfNumber(36));
        array2.add(new PdfNumber(806));
        array2.add(new PdfNumber(1.25));

        PdfArray array3 = new PdfArray();
        array3.add(pdfDoc.addNewPage().getPdfObject());
        array3.add(PdfName.XYZ);
        array3.add(new PdfNumber(36));
        array3.add(new PdfNumber(806));
        array3.add(new PdfNumber(1));

        pdfDoc.addNamedDestination("page1", array2);
        pdfDoc.addNamedDestination("page2", array3);
        pdfDoc.addNamedDestination("page3", array1);

        PdfOutline root = pdfDoc.getOutlines(false);
        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("page1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("page2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("page3")));
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, SOURCE_FOLDER + "cmp_outlinesWithNamedDestinations02.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void outlineStackOverflowTest01() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "outlineStackOverflowTest01.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);

        try {
            pdfDoc.getOutlines(true);
        } catch (StackOverflowError e) {
            Assertions.fail("StackOverflow thrown when reading document with a large number of outlines.");
        }
    }

    @Test
    public void outlineTypeNull() throws IOException, InterruptedException {
        String filename = "outlineTypeNull";
        String outputFile = DESTINATION_FOLDER + filename + ".pdf";
        PdfReader reader = new PdfReader(SOURCE_FOLDER + filename + ".pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFile);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.removePage(3);
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outputFile, SOURCE_FOLDER + "cmp_" + filename + ".pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void removeAllOutlinesTest() throws IOException, InterruptedException {
        String filename = "iphone_user_guide_removeAllOutlinesTest.pdf";
        String input = SOURCE_FOLDER + "iphone_user_guide.pdf";
        String output = DESTINATION_FOLDER + "cmp_" + filename;
        String cmp = SOURCE_FOLDER + "cmp_" + filename;
        PdfReader reader = new PdfReader(input);
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        pdfDocument.getOutlines(true).removeOutline();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void removeOneOutlineTest() throws IOException, InterruptedException {
        String filename = "removeOneOutline.pdf";
        String input = SOURCE_FOLDER + "outlineTree.pdf";
        String output = DESTINATION_FOLDER + "cmp_" + filename;
        String cmp = SOURCE_FOLDER + "cmp_" + filename;
        PdfReader reader = new PdfReader(input);
        PdfWriter writer = CompareTool.createTestPdfWriter(output);
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        PdfOutline root = pdfDocument.getOutlines(true);
        PdfOutline toRemove = root.getAllChildren().get(2);
        toRemove.removeOutline();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void testReinitializingOutlines() throws IOException {
        String input = SOURCE_FOLDER + "outlineTree.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input));
        PdfOutline root = pdfDocument.getOutlines(false);
        Assertions.assertEquals(4, root.getAllChildren().size());
        pdfDocument.getCatalog().getPdfObject().remove(PdfName.Outlines);
        root = pdfDocument.getOutlines(true);
        Assertions.assertNull(root);
        pdfDocument.close();
    }

    @Test
    public void removePageInDocWithSimpleOutlineTreeStructTest() throws IOException, InterruptedException {
        String input = SOURCE_FOLDER + "simpleOutlineTreeStructure.pdf";
        String output = DESTINATION_FOLDER + "simpleOutlineTreeStructure.pdf";
        String cmp = SOURCE_FOLDER + "cmp_simpleOutlineTreeStructure.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), CompareTool.createTestPdfWriter(output));
        pdfDocument.removePage(2);
        Assertions.assertEquals(2, pdfDocument.getNumberOfPages());

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void removePageInDocWithComplexOutlineTreeStructTest() throws IOException, InterruptedException {
        String input = SOURCE_FOLDER + "complexOutlineTreeStructure.pdf";
        String output = DESTINATION_FOLDER + "complexOutlineTreeStructure.pdf";
        String cmp = SOURCE_FOLDER + "cmp_complexOutlineTreeStructure.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), CompareTool.createTestPdfWriter(output));
        pdfDocument.removePage(2);
        Assertions.assertEquals(2, pdfDocument.getNumberOfPages());

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void constructOutlinesNoParentTest() throws IOException {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.addNewPage();

            PdfDictionary first = new PdfDictionary();
            first.makeIndirect(pdfDocument);

            PdfDictionary outlineDictionary = new PdfDictionary();
            outlineDictionary.put(PdfName.First, first);
            outlineDictionary.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));
            first.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));

            AssertUtil.doesNotThrow(() -> pdfDocument.getCatalog()
                    .constructOutlines(outlineDictionary, new EmptyNameTree()));
        }
    }

    @Test
    public void constructOutlinesNoTitleTest() throws IOException {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.addNewPage();

            PdfDictionary first = new PdfDictionary();
            first.makeIndirect(pdfDocument);

            PdfDictionary outlineDictionary = new PdfDictionary();
            outlineDictionary.makeIndirect(pdfDocument);

            outlineDictionary.put(PdfName.First, first);
            first.put(PdfName.Parent, outlineDictionary);

            Exception exception = Assertions.assertThrows(
                    PdfException.class,
                    () -> pdfDocument.getCatalog()
                            .constructOutlines(outlineDictionary, new EmptyNameTree())
            );
            Assertions.assertEquals(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.CORRUPTED_OUTLINE_NO_TITLE_ENTRY,
                            first.indirectReference),
                    exception.getMessage());
        }
    }

    @Test
    public void checkParentOfOutlinesTest() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.getCatalog().setPageMode(PdfName.UseOutlines);

            PdfPage firstPage = pdfDocument.addNewPage();

            PdfOutline rootOutline = pdfDocument.getOutlines(false);
            PdfOutline firstOutline = rootOutline.addOutline("First outline");
            PdfOutline firstSubOutline = firstOutline.addOutline("First suboutline");
            PdfOutline secondSubOutline = firstOutline.addOutline("Second suboutline");
            PdfOutline secondOutline = rootOutline.addOutline("SecondOutline");

            firstOutline.addDestination(PdfExplicitDestination.createFit(firstPage));

            PdfOutline resultedRoot = pdfDocument.getOutlines(true);
            Assertions.assertEquals(2, resultedRoot.getAllChildren().size());
            Assertions.assertEquals(resultedRoot, resultedRoot.getAllChildren().get(0).getParent());
            Assertions.assertEquals(resultedRoot, resultedRoot.getAllChildren().get(1).getParent());

            PdfOutline resultedFirstOutline = resultedRoot.getAllChildren().get(0);
            Assertions.assertEquals(2, resultedFirstOutline.getAllChildren().size());
            Assertions.assertEquals(resultedFirstOutline, resultedFirstOutline.getAllChildren().get(0).getParent());
            Assertions.assertEquals(resultedFirstOutline, resultedFirstOutline.getAllChildren().get(1).getParent());
        }
    }

    @Test
    public void checkNestedOutlinesParentTest() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.getCatalog().setPageMode(PdfName.UseOutlines);

            PdfPage firstPage = pdfDocument.addNewPage();
            PdfOutline rootOutline = pdfDocument.getOutlines(false);
            PdfOutline firstOutline = rootOutline.addOutline("First outline");
            PdfOutline secondOutline = firstOutline.addOutline("Second outline");
            PdfOutline thirdOutline = secondOutline.addOutline("Third outline");

            firstOutline.addDestination(PdfExplicitDestination.createFit(firstPage));

            PdfOutline resultedRoot = pdfDocument.getOutlines(true);
            Assertions.assertEquals(1, resultedRoot.getAllChildren().size());
            Assertions.assertEquals(resultedRoot, resultedRoot.getAllChildren().get(0).getParent());

            PdfOutline resultedFirstOutline = resultedRoot.getAllChildren().get(0);
            Assertions.assertEquals(1, resultedFirstOutline.getAllChildren().size());
            Assertions.assertEquals(resultedFirstOutline, resultedFirstOutline.getAllChildren().get(0).getParent());

            PdfOutline resultedSecondOutline = resultedFirstOutline.getAllChildren().get(0);
            Assertions.assertEquals(1, resultedSecondOutline.getAllChildren().size());
            Assertions.assertEquals(resultedSecondOutline, resultedSecondOutline.getAllChildren().get(0).getParent());
        }
    }

    @Test
    public void setOutlinePropertiesTest() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {

            PdfPage firstPage = pdfDocument.addNewPage();

            PdfOutline rootOutline = pdfDocument.getOutlines(true);
            PdfOutline outline = rootOutline.addOutline("Outline");

            Assertions.assertTrue(outline.isOpen());
            Assertions.assertNull(outline.getStyle());
            Assertions.assertNull(outline.getColor());

            outline.getContent().put(PdfName.C, new PdfArray(ColorConstants.BLACK.getColorValue()));
            outline.getContent().put(PdfName.F, new PdfNumber(2));
            outline.getContent().put(PdfName.Count, new PdfNumber(4));

            Assertions.assertTrue(outline.isOpen());
            Assertions.assertEquals(new Integer(2), outline.getStyle());
            Assertions.assertEquals(ColorConstants.BLACK, outline.getColor());

            outline.getContent().put(PdfName.Count, new PdfNumber(0));
            Assertions.assertTrue(outline.isOpen());

            outline.getContent().put(PdfName.Count, new PdfNumber(-5));
            Assertions.assertFalse(outline.isOpen());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            KernelLogMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP))
    public void checkPossibleInfiniteLoopWithSameNextAndPrevLinkTest() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {

            pdfDocument.addNewPage();

            PdfDictionary first = new PdfDictionary();
            first.makeIndirect(pdfDocument);
            PdfDictionary second = new PdfDictionary();
            second.makeIndirect(pdfDocument);

            PdfDictionary outlineDictionary = new PdfDictionary();
            outlineDictionary.makeIndirect(pdfDocument);

            outlineDictionary.put(PdfName.First, first);
            outlineDictionary.put(PdfName.Last, second);
            first.put(PdfName.Parent, outlineDictionary);
            second.put(PdfName.Parent, outlineDictionary);
            first.put(PdfName.Next, second);
            first.put(PdfName.Prev, second);
            second.put(PdfName.Next, first);
            second.put(PdfName.Prev, first);
            outlineDictionary.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));
            first.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));
            second.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));

            AssertUtil.doesNotThrow(() -> pdfDocument.getCatalog()
                    .constructOutlines(outlineDictionary, new EmptyNameTree()));
            PdfOutline resultedOutline = pdfDocument.getOutlines(false);
            Assertions.assertEquals(2, resultedOutline.getAllChildren().size());
            Assertions.assertEquals(resultedOutline.getAllChildren().get(1).getParent(),
                    resultedOutline.getAllChildren().get(0).getParent());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            KernelLogMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP))
    public void checkPossibleInfiniteLoopWithSameFirstAndLastLinkTest() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {

            pdfDocument.addNewPage();

            PdfDictionary first = new PdfDictionary();
            first.makeIndirect(pdfDocument);

            PdfDictionary outlineDictionary = new PdfDictionary();
            outlineDictionary.makeIndirect(pdfDocument);

            outlineDictionary.put(PdfName.First, first);
            first.put(PdfName.Parent, outlineDictionary);
            first.put(PdfName.First, outlineDictionary);
            first.put(PdfName.Last, outlineDictionary);
            outlineDictionary.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));
            first.put(PdfName.Title, new PdfString("title", PdfEncodings.UNICODE_BIG));

            AssertUtil.doesNotThrow(() -> pdfDocument.getCatalog()
                    .constructOutlines(outlineDictionary, new EmptyNameTree()));
            PdfOutline resultedOutline = pdfDocument.getOutlines(false);
            Assertions.assertEquals(1, resultedOutline.getAllChildren().size());
            Assertions.assertEquals(resultedOutline,
                    resultedOutline.getAllChildren().get(0).getParent());
        }
    }

    @Test
    public void outlineNoParentLinkInConservativeModeTest() throws IOException {
        try (
                PdfDocument pdfDocument = new PdfDocument(
                        new PdfReader(SOURCE_FOLDER + "outlinesNoParentLink.pdf"))) {
            pdfDocument.getReader().setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(PdfException.class, () -> pdfDocument.getOutlines(true));

            //Hardcode indirectReference, cause there is no option to get this outline due to #getOutlines method
            // will be thrown an exception.
            Assertions.assertEquals(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.CORRUPTED_OUTLINE_NO_PARENT_ENTRY, "9 0 R"),
                    exception.getMessage());
        }
    }

    @Test
    public void outlineHasInfiniteLoopInConservativeModeTest() throws IOException {
        try (
                PdfDocument pdfDocument = new PdfDocument(
                        new PdfReader(SOURCE_FOLDER + "outlinesHaveInfiniteLoop.pdf"))) {
            pdfDocument.getReader().setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(PdfException.class, () -> pdfDocument.getOutlines(true));

            //Hardcode indirectReference, cause there is no option to get this outline due to #getOutlines method
            // will be thrown an exception.
            Assertions.assertEquals(
                    MessageFormatUtil.format(
                            KernelExceptionMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP,
                            "<</Dest [4 0 R /Fit ] /Next 10 0 R /Parent <<>> /Prev 10 0 R /Title First Page >>"),
                    exception.getMessage());
        }
    }

    @Test
    public void createOutlinesWithDifferentVariantsOfChildrenTest() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.getCatalog().setPageMode(PdfName.UseOutlines);

            PdfPage firstPage = pdfDocument.addNewPage();

            PdfOutline a = pdfDocument.getOutlines(false);
            PdfOutline b = a.addOutline("B");
            PdfOutline e = b.addOutline("E");
            PdfOutline f = e.addOutline("F");
            PdfOutline d = b.addOutline("D");
            PdfOutline c = a.addOutline("C");
            PdfOutline g = f.addOutline("G");
            PdfOutline h = f.addOutline("H");

            a.addDestination(PdfExplicitDestination.createFit(firstPage));

            PdfOutline resultedA = pdfDocument.getOutlines(true);

            // Asserting children of root outline.
            Assertions.assertEquals(2, resultedA.getAllChildren().size());
            Assertions.assertEquals(resultedA, resultedA.getAllChildren().get(0).getParent());
            Assertions.assertEquals(resultedA, resultedA.getAllChildren().get(1).getParent());
            Assertions.assertTrue(resultedA.getAllChildren().get(1).getAllChildren().isEmpty());
            Assertions.assertEquals(2, resultedA.getAllChildren().get(0).getAllChildren().size());

            //Asserting children of B outline after reconstructing.
            PdfOutline resultedB = resultedA.getAllChildren().get(0);
            Assertions.assertEquals(resultedB, resultedB.getAllChildren().get(0).getParent());
            Assertions.assertEquals(resultedB, resultedB.getAllChildren().get(1).getParent());
            Assertions.assertTrue(resultedB.getAllChildren().get(1).getAllChildren().isEmpty());
            Assertions.assertEquals(1, resultedB.getAllChildren().get(0).getAllChildren().size());

            //Asserting children of E outline after reconstructing.
            PdfOutline resultedE = resultedB.getAllChildren().get(0);
            Assertions.assertEquals(resultedE, resultedE.getAllChildren().get(0).getParent());
            Assertions.assertEquals(2, resultedE.getAllChildren().get(0).getAllChildren().size());

            //Asserting children of F outline after reconstructing.
            PdfOutline resultedF = resultedE.getAllChildren().get(0);
            Assertions.assertEquals(resultedF, resultedF.getAllChildren().get(0).getParent());
            Assertions.assertEquals(resultedF, resultedF.getAllChildren().get(1).getParent());
            Assertions.assertTrue(resultedF.getAllChildren().get(0).getAllChildren().isEmpty());
            Assertions.assertTrue(resultedF.getAllChildren().get(1).getAllChildren().isEmpty());
        }
    }

    @Test
    public void createOutlinesWithActionsTest() throws IOException, InterruptedException {
        String filename = "createOutlinesWithActions.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename))) {
            pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);

            PdfPage firstPage = pdfDoc.addNewPage();
            PdfPage secondPage = pdfDoc.addNewPage();

            PdfOutline rootOutline = pdfDoc.getOutlines(false);
            PdfOutline firstOutline = rootOutline.addOutline("First Page");
            PdfOutline secondOutline = rootOutline.addOutline("Second Page");

            PdfDestination page1Dest = PdfExplicitDestination.createFit(firstPage);
            PdfAction page1Action = PdfAction.createGoTo(page1Dest);
            firstOutline.addAction(page1Action);
            Assertions.assertEquals(page1Dest.getPdfObject(), firstOutline.getDestination().getPdfObject());

            PdfAction page2Action = PdfAction.createGoTo(PdfExplicitDestination.createFit(secondPage));
            secondOutline.addAction(page2Action);
        }

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void createOutlinesWithURIActionTest() throws IOException, InterruptedException {
        String filename = "createOutlinesWithURIAction.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename))) {
            pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);

            PdfOutline rootOutline = pdfDoc.getOutlines(false);
            PdfOutline firstOutline = rootOutline.addOutline("First Page");

            // The test was created to improve the coverage but
            // Apparently it works!
            PdfAction action1 = PdfAction.createURI("https://example.com");
            firstOutline.addAction(action1);
            Assertions.assertNull(firstOutline.getDestination());
        }

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    private static final class EmptyNameTree implements IPdfNameTreeAccess {
        @Override
        public PdfObject getEntry(PdfString key) {
            return null;
        }

        @Override
        public PdfObject getEntry(String key) {
            return null;
        }

        @Override
        public Set<PdfString> getKeys() {
            return Collections.<PdfString>emptySet();
        }
    }
}
