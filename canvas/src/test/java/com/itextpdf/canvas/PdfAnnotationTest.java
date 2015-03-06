package com.itextpdf.canvas;

import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

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

    @Test
    public void addAndGetLinkAnnotations() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "linkAnnotation03.pdf")));

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.CourierBold), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.moveText(0, -50);
        canvas.showText("Click here to go to itextpdf blog.");
        canvas.moveText(0, -50);
        canvas.showText("Click here to go to itextpdf FAQ.");
        canvas.endText();
        canvas.release();
        page.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 590, 300, 25)).
                setAction(PdfAction.createURI(document, "http://itextpdf.com")).
                setColor(new PdfArray(new float[]{1, 0, 0})));
        page.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 540, 300, 25)).
                setAction(PdfAction.createURI(document, "http://itextpdf.com/node")).
                setColor(new PdfArray(new float[]{0, 1, 0})));
        page.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 490, 300, 25)).
                setAction(PdfAction.createURI(document, "http://itextpdf.com/salesfaq")).
                setColor(new PdfArray(new float[]{0, 0, 1})));
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotation03.pdf", sourceFolder + "cmp_linkAnnotation03.pdf", destinationFolder, "diff_"));


        document = new PdfDocument(new PdfReader(new FileInputStream(destinationFolder + "linkAnnotation03.pdf")));
        page = document.getPage(1);
        Assert.assertEquals(3, page.getAnnotsSize());
        List<PdfAnnotation> annotations = page.getAnnotations();
        Assert.assertEquals(3, annotations.size());
        PdfLinkAnnotation link = (PdfLinkAnnotation)annotations.get(0);
        Assert.assertEquals(page, link.getPage());
        document.close();

    }


}
