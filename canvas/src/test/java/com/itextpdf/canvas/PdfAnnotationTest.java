package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.*;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
                setColor(new float[]{1, 0, 0}));
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
                setColor(new float[]{1, 0, 0}));
        page.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 540, 300, 25)).
                setAction(PdfAction.createURI(document, "http://itextpdf.com/node")).
                setColor(new float[]{0, 1, 0}));
        page.addAnnotation(new PdfLinkAnnotation(document, new Rectangle(100, 490, 300, 25)).
                setAction(PdfAction.createURI(document, "http://itextpdf.com/salesfaq")).
                setColor(new float[]{0, 0, 1}));
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotation03.pdf", sourceFolder + "cmp_linkAnnotation03.pdf", destinationFolder, "diff_"));


        document = new PdfDocument(new PdfReader(new FileInputStream(destinationFolder + "linkAnnotation03.pdf")));
        page = document.getPage(1);
        Assert.assertEquals(3, page.getAnnotsSize());
        List<PdfAnnotation> annotations = page.getAnnotations();
        Assert.assertEquals(3, annotations.size());
        PdfLinkAnnotation link = (PdfLinkAnnotation) annotations.get(0);
        Assert.assertEquals(page, link.getPage());
        document.close();

    }

    @Test
    public void addTextAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "textAnnotation01.pdf")));

        PdfPage page = document.addNewPage();

        PdfTextAnnotation textannot = new PdfTextAnnotation(document, new Rectangle(100, 600, 50, 40)).setText(new PdfString("Text Annotation 01")).setContents(new PdfString("Some contents..."));
        PdfPopupAnnotation popupAnnot = new PdfPopupAnnotation(document, new Rectangle(150, 640, 200, 100)).setOpen(true);
        textannot.setPopup(popupAnnot);
        popupAnnot.setParent(textannot);
        page.addAnnotation(textannot);
        page.addAnnotation(popupAnnot);
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "textAnnotation01.pdf", sourceFolder + "cmp_textAnnotation01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void caretTest() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename = destinationFolder + "CaretAnnotation.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("This is a text")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(236, 750)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("This is an edited text")
                .endText()
                .restoreState();

        PdfCaretAnnotation caret = new PdfCaretAnnotation(pdfDoc1, new Rectangle(36, 745, 350, 20));
        caret.setSymbol(new PdfString("P"));

        PdfPopupAnnotation popup = new PdfPopupAnnotation(pdfDoc1, new Rectangle(36, 445, 100, 100));
        popup.setContents(new PdfString("Popup"));
        popup.setOpen(true);

        caret.setPopup(popup);

        page1.addAnnotation(caret);
        page1.addAnnotation(popup);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_CaretAnnotation.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void addFreeTextAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "freeTextAnnotation01.pdf")));

        PdfPage page = document.addNewPage();

        new PdfCanvas(page).beginText().setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Courier), 24).moveText(100, 600).showText("Annotated text").endText().release();
        PdfFreeTextAnnotation textannot = new PdfFreeTextAnnotation(document, new Rectangle(300, 700, 150, 20), "").
                setContents(new PdfString("FreeText annotation")).setColor(new float[]{1, 0, 0});
        textannot.setIntent(PdfName.FreeTextCallout);
        textannot.setCalloutLine(new float[]{120, 616, 180, 680, 300, 710}).setLineEndingStyle(PdfName.OpenArrow);
        page.addAnnotation(textannot);
        textannot.flush();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "freeTextAnnotation01.pdf", sourceFolder + "cmp_freeTextAnnotation01.pdf", destinationFolder, "diff_"));
    }


}
