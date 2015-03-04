package com.itextpdf.canvas;

import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

public class PdfAnnotationTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfAnnotationTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfAnnotationTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void addLinkAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "linkAnnotation01.pdf")));

        PdfPage page1 = document.addNewPage();
        PdfPage page2 = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.CourierBold), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 1");
        canvas.moveText(0, -30);
        canvas.showText("Link to page 2. Click here!");
        canvas.endText();
        canvas.release();
        page1.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 560, 260, 25)).setDestination(PdfExplicitDestination.createFit(page2)));
        page1.flush();

        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.CourierBold), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 2");
        canvas.endText();
        canvas.release();
        page2.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotation01.pdf", sourceFolder + "cmp_linkAnnotation01.pdf", destinationFolder, "diff_"));

    }

    @Test
    public void addLinkAnnotation02() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "linkAnnotation02.pdf")));

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.CourierBold), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.endText();
        canvas.release();
        page.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 590, 300, 25)).
                setAction(PdfAction.createURI(document, "http://itextpdf.com")).
                setColor(new PdfArray(new float[]{1, 0, 0})));
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotation02.pdf", sourceFolder + "cmp_linkAnnotation02.pdf", destinationFolder, "diff_"));

    }


}
