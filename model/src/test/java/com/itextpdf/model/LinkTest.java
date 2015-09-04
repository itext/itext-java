package com.itextpdf.model;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.model.element.AreaBreak;
import com.itextpdf.model.element.Link;
import com.itextpdf.model.element.Paragraph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class LinkTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/LinkTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/LinkTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void linkTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder+"linkTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_linkTest01.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        PdfAction action = PdfAction.createURI(pdfDoc, "http://itextpdf.com/", false);

        Link link = new Link("TestLink", action);
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void linkTest02() throws IOException, InterruptedException {

        String outFileName = destinationFolder+"linkTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_linkTest02.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        doc.add(new AreaBreak()).add(new AreaBreak());

        PdfArray array = new PdfArray();
        array.add(doc.getPdfDocument().getPage(1).getPdfObject());
        array.add(PdfName.XYZ);
        array.add(new PdfNumber(36));
        array.add(new PdfNumber(100));
        array.add(new PdfNumber(1));

        PdfDestination dest = PdfDestination.makeDestination(array);

        PdfAction action = PdfAction.createGoTo(pdfDoc, dest);

        Link link = new Link("TestLink", action);
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
