package com.itextpdf.core.pdf;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.core.pdf.navigation.PdfStringDestination;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.text.DocumentException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfOutlineTest extends ExtendedITextTest{

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfOutlineTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfOutlineTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void outlinesTest() throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder+"iphone_user_guide.pdf"));

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfOutline outlines = pdfDoc.getOutlines(false);
        List<PdfOutline> children = outlines.getAllChildren().get(0).getAllChildren();

        Assert.assertEquals(outlines.getTitle(), "Outlines");
        Assert.assertEquals(children.size(), 13);
        Assert.assertTrue(children.get(0).getDestination() instanceof PdfStringDestination);
    }

    @Test
    public void outlinesWithPagesTest() throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder+"iphone_user_guide.pdf"));

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(52);
        List<PdfOutline> pageOutlines = page.getOutlines(true);

        Assert.assertEquals(3, pageOutlines.size());
        Assert.assertTrue(pageOutlines.get(0).getTitle().equals("Safari"));
        Assert.assertEquals(pageOutlines.get(0).getAllChildren().size(), 4);
    }

    @Before
    public void setupAddOutlinesToDocumentTest() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        FileOutputStream fos = new FileOutputStream(destinationFolder+"addOutlinesResult.pdf");
        PdfWriter writer = new PdfWriter(fos);
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
    }
    @Test
    public void addOutlinesToDocumentTest() throws IOException, InterruptedException, DocumentException {
        String filename = destinationFolder+"addOutlinesResult.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        Assert.assertEquals(3, outlines.getAllChildren().size());
        Assert.assertEquals("firstPageChild", outlines.getAllChildren().get(1).getAllChildren().get(0).getTitle());
    }

    @Before
    public void setupRemovePageWithOutlinesTest() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        FileOutputStream fos = new FileOutputStream(destinationFolder+"removePagesWithOutlinesResult.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        pdfDoc.removePage(102);

        pdfDoc.close();
    }

    @Test
    public void removePageWithOutlinesTest() throws IOException {
        String filename = destinationFolder + "removePagesWithOutlinesResult.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfPage page = pdfDoc.getPage(102);
        List<PdfOutline> pageOutlines =  page.getOutlines(false);
        Assert.assertEquals(4, pageOutlines.size());
    }

    @Before
    public void setupUpdateOutlineTitle() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";
        PdfReader reader = new PdfReader(new FileInputStream(filename));
        FileOutputStream fos = new FileOutputStream(destinationFolder+"updateOutlineTitleResult.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        outlines.getAllChildren().get(0).getAllChildren().get(1).setTitle("New Title");

        pdfDoc.close();
    }

    @Test
    public void updateOutlineTitle() throws IOException {
        String filename = destinationFolder + "updateOutlineTitleResult.pdf";
        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        PdfOutline outline = outlines.getAllChildren().get(0).getAllChildren().get(1);

        Assert.assertEquals("New Title", outline.getTitle());
    }

    @Before
    public void setupAddOutlineInNotOutlineMode() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        FileOutputStream fos = new FileOutputStream(destinationFolder+"addOutlinesWithoutOutlineModeResult.pdf");
        PdfWriter writer = new PdfWriter(fos);
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
    }

    @Test
    public void addOutlineInNotOutlineMode() throws IOException {
        String filename = destinationFolder + "addOutlinesWithoutOutlineModeResult.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfOutline outlines = pdfDoc.getOutlines(false);

        List<PdfOutline> pageOutlines = pdfDoc.getPage(102).getOutlines(true);
        Assert.assertEquals(5, pageOutlines.size());
    }

    @Before
    public void setupCreateDocWithOutlines() throws IOException, DocumentException, InterruptedException {

        FileOutputStream fos = new FileOutputStream(destinationFolder+"documentWithOutlines.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);

        PdfPage firstPage = pdfDoc.addNewPage();
        PdfPage secondPage = pdfDoc.addNewPage();

        PdfOutline outlines = pdfDoc.getOutlines(false);

        PdfOutline rootOutline = new PdfOutline(pdfDoc);
        PdfOutline firstOutline = rootOutline.addOutline("First Page");
        PdfOutline secondOutline = rootOutline.addOutline("Second Page");
        firstOutline.addDestination(PdfExplicitDestination.createFit(firstPage));
        secondOutline.addDestination(PdfExplicitDestination.createFit(secondPage));

        pdfDoc.close();
    }

    @Test
    public void createDocWithOutlines() throws IOException, DocumentException, InterruptedException {

        String filename = destinationFolder + "documentWithOutlines.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfOutline outlines = pdfDoc.getOutlines(false);

        Assert.assertEquals(2, outlines.getAllChildren().size());
        Assert.assertEquals("First Page", outlines.getAllChildren().get(0).getTitle());
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void copyPagesWithOutlines() throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder+"iphone_user_guide.pdf"));
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder+"copyPagesWithOutlines01.pdf"));

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(writer);

        Set<Integer> pages = new TreeSet<Integer>();
        pages.add(1);
        pages.add(2);
        pages.add(3);
        pages.add(5);
        pages.add(52);
        pages.add(102);
        PdfOutline outlines = pdfDoc.getOutlines(false);
        pdfDoc.copyPages(pages, pdfDoc1);
        pdfDoc.close();

        Assert.assertEquals(6, pdfDoc1.getNumberOfPages());
        Assert.assertEquals(4, pdfDoc1.getOutlines(false).getAllChildren().get(0).getAllChildren().size());
        pdfDoc1.close();
    }

    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException, DocumentException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder+"iphone_user_guide.pdf"));
        String filename = destinationFolder + "outlinesWithNamedDestinations01.pdf";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);

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

        pdfDoc.addNewName(new PdfString("test1"), array2);
        pdfDoc.addNewName(new PdfString("test2"), array3);
        pdfDoc.addNewName(new PdfString("test3"), array1);

        PdfOutline root = pdfDoc.getOutlines(false);
        if (root == null)
            root = new PdfOutline(pdfDoc);

        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("test1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("test2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("test3")));
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_outlinesWithNamedDestinations01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void addOutlinesWithNamedDestinations02() throws IOException, InterruptedException, DocumentException {
        String filename = destinationFolder + "outlinesWithNamedDestinations02.pdf";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);

        PdfDocument pdfDoc = new PdfDocument(writer);
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

        pdfDoc.addNewName(new PdfString("page1"), array2);
        pdfDoc.addNewName(new PdfString("page2"), array3);
        pdfDoc.addNewName(new PdfString("page3"), array1);

        PdfOutline root = pdfDoc.getOutlines(false);
        if (root == null)
            root = new PdfOutline(pdfDoc);
        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("page1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("page2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("page3")));
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_outlinesWithNamedDestinations02.pdf", destinationFolder, "diff_"));
    }
}
