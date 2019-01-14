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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class PdfOutlineTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfOutlineTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfOutlineTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void createSimpleDocWithOutlines() throws IOException, InterruptedException {
        String filename = "simpleDocWithOutlines.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);

        PdfPage firstPage = pdfDoc.addNewPage();
        PdfPage secondPage = pdfDoc.addNewPage();

        PdfOutline rootOutline = pdfDoc.getOutlines(false);
        PdfOutline firstOutline = rootOutline.addOutline("First Page");
        PdfOutline secondOutline = rootOutline.addOutline("Second Page");
        firstOutline.addDestination(PdfExplicitDestination.createFit(firstPage));
        secondOutline.addDestination(PdfExplicitDestination.createFit(secondPage));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void outlinesTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));
        PdfOutline outlines = pdfDoc.getOutlines(false);
        List<PdfOutline> children = outlines.getAllChildren().get(0).getAllChildren();

        Assert.assertEquals(outlines.getTitle(), "Outlines");
        Assert.assertEquals(children.size(), 13);
        Assert.assertTrue(children.get(0).getDestination() instanceof PdfStringDestination);
    }

    @Test
    public void outlinesWithPagesTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));
        PdfPage page = pdfDoc.getPage(52);
        List<PdfOutline> pageOutlines = page.getOutlines(true);
        try {
            Assert.assertEquals(3, pageOutlines.size());
            Assert.assertTrue(pageOutlines.get(0).getTitle().equals("Safari"));
            Assert.assertEquals(pageOutlines.get(0).getAllChildren().size(), 4);
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void addOutlinesToDocumentTest() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "iphone_user_guide.pdf");
        String filename = "addOutlinesToDocumentTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void readOutlinesFromDocumentTest() throws IOException, InterruptedException {
        String filename = sourceFolder + "addOutlinesResult.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfOutline outlines = pdfDoc.getOutlines(false);
        try {
            Assert.assertEquals(3, outlines.getAllChildren().size());
            Assert.assertEquals("firstPageChild", outlines.getAllChildren().get(1).getAllChildren().get(0).getTitle());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE, count = 36))
    // TODO DEVSIX-1583: destinations are not removed along with page
    public void removePageWithOutlinesTest() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = "removePageWithOutlinesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"), new PdfWriter(destinationFolder + filename));
        // TODO this causes log message errors! it's because of destinations pointing to removed page (freed reference, replaced by PdfNull)
        pdfDoc.removePage(102);

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String diffContent = compareTool.compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_");
        String diffTags = compareTool.compareTagStructures(destinationFolder + filename, sourceFolder + "cmp_" + filename);
        if (diffContent != null || diffTags != null) {
            diffContent = diffContent != null ? diffContent : "";
            diffTags = diffTags != null ? diffTags : "";
            Assert.fail(diffContent + diffTags);
        }
    }

    @Test
    public void readRemovedPageWithOutlinesTest() throws IOException {
        // TODO DEVSIX-1583: src document is taken from the previous removePageWithOutlinesTest test, however it contains numerous destination objects which contain PdfNull instead of page reference
        String filename = sourceFolder + "removePagesWithOutlinesResult.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfPage page = pdfDoc.getPage(102);
        List<PdfOutline> pageOutlines = page.getOutlines(false);
        try {
            Assert.assertEquals(4, pageOutlines.size());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void updateOutlineTitle() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "iphone_user_guide.pdf");
        String filename = "updateOutlineTitle.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        outlines.getAllChildren().get(0).getAllChildren().get(1).setTitle("New Title");

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void readOutlineTitle() throws IOException {
        String filename = sourceFolder + "updateOutlineTitleResult.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfOutline outlines = pdfDoc.getOutlines(false);
        PdfOutline outline = outlines.getAllChildren().get(0).getAllChildren().get(1);
        try {
            Assert.assertEquals("New Title", outline.getTitle());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void addOutlineInNotOutlineMode() throws IOException, InterruptedException {
        String filename = "addOutlineInNotOutlineMode.pdf";
        PdfReader reader = new PdfReader(sourceFolder + "iphone_user_guide.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void readOutlineAddedInNotOutlineMode() throws IOException {
        String filename = sourceFolder + "addOutlinesWithoutOutlineModeResult.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        List<PdfOutline> pageOutlines = pdfDoc.getPage(102).getOutlines(true);
        try {
            Assert.assertEquals(5, pageOutlines.size());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void createDocWithOutlines() throws IOException, InterruptedException {
        String filename = sourceFolder + "documentWithOutlines.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfOutline outlines = pdfDoc.getOutlines(false);
        try {
            Assert.assertEquals(2, outlines.getAllChildren().size());
            Assert.assertEquals("First Page", outlines.getAllChildren().get(0).getTitle());
        } finally {
            pdfDoc.close();
        }
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void copyPagesWithOutlines() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "iphone_user_guide.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "copyPagesWithOutlines01.pdf");

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

        Assert.assertEquals(6, pdfDoc1.getNumberOfPages());
        Assert.assertEquals(4, pdfDoc1.getOutlines(false).getAllChildren().get(0).getAllChildren().size());
        pdfDoc1.close();
    }

    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException {
        String filename = destinationFolder + "outlinesWithNamedDestinations01.pdf";

        PdfReader reader = new PdfReader(sourceFolder + "iphone_user_guide.pdf");
        PdfWriter writer = new PdfWriter(filename);

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

        assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_outlinesWithNamedDestinations01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void addOutlinesWithNamedDestinations02() throws IOException, InterruptedException {
        String filename = destinationFolder + "outlinesWithNamedDestinations02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
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

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_outlinesWithNamedDestinations02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void outlineStackOverflowTest01() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "outlineStackOverflowTest01.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);

        try {
            pdfDoc.getOutlines(true);
        } catch (StackOverflowError e) {
            Assert.fail("StackOverflow thrown when reading document with a large number of outlines.");
        }
    }

    @Test
    public void outlineTypeNull() throws IOException, InterruptedException {
        String filename = "outlineTypeNull";
        String outputFile = destinationFolder + filename + ".pdf";
        PdfReader reader = new PdfReader(sourceFolder + filename + ".pdf");
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputFile));
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.removePage(3);
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outputFile, sourceFolder + "cmp_" + filename + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void removeAllOutlinesTest() throws IOException, InterruptedException {
        String filename = "iphone_user_guide_removeAllOutlinesTest.pdf";
        String input = sourceFolder + "iphone_user_guide.pdf";
        String output = destinationFolder + "cmp_" + filename;
        String cmp = sourceFolder + "cmp_" + filename;
        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        pdfDocument.getOutlines(true).removeOutline();
        pdfDocument.close();


        Assert.assertNull(new CompareTool().compareByContent(output, cmp, destinationFolder, "diff_"));
    }
}
