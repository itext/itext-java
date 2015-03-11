package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.*;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import java.io.*;

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
        String filename =  destinationFolder + "caretAnnotation.pdf";

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

    @Test
    public void addSquareAndCircleAnnotations01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "squareAndCircleAnnotations01.pdf")));

        PdfPage page = document.addNewPage();

        PdfSquareAnnotation square = new PdfSquareAnnotation(document, new Rectangle(100, 700, 100, 100)).setInteriorColor(new float[]{1, 0, 0}).setColor(new float[]{0, 1, 0}).setContents("Red Square");
        page.addAnnotation(square);
        PdfCircleAnnotation circle = new PdfCircleAnnotation(document, new Rectangle(300, 700, 100, 100)).setInteriorColor(new float[]{0, 1, 0}).setColor(new float[]{0, 0, 1}).setContents(new PdfString("Green Circle"));
        page.addAnnotation(circle);
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "squareAndCircleAnnotations01.pdf", sourceFolder + "cmp_squareAndCircleAnnotations01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void fileAttachmentTest() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename = destinationFolder + "fileAttachmentAnnotation.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfStream stream = new PdfStream(pdfDoc1, new FileInputStream(sourceFolder+"sample.wav"));
        stream.put(PdfName.Type, PdfName.EmbeddedFile);

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString("sample.wav"));
        dict.put(PdfName.UF, new PdfString("sample.wav"));

        PdfDictionary EF = new PdfDictionary();
        EF.put(PdfName.F, stream);
        EF.put(PdfName.UF, stream);
        dict.put(PdfName.EF, EF);
        PdfFileSpec spec = new PdfFileSpec(dict, pdfDoc1);

        PdfFileAttachmentAnnotation fileAttach = new PdfFileAttachmentAnnotation(pdfDoc1, new Rectangle(100, 100), spec);
        fileAttach.setIconName(PdfName.Paperclip);
        page1.addAnnotation(fileAttach);

        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_fileAttachmentAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void rubberStampTest() throws DocumentException, IOException, PdfException, InterruptedException{
        String filename =  destinationFolder + "rubberStampAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 0, 100, 50));
        stamp.setStampName(PdfName.Approved);
        PdfStampAnnotation stamp1 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 50, 100, 50));
        stamp1.setStampName(PdfName.AsIs);
        PdfStampAnnotation stamp2 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 100, 100, 50));
        stamp2.setStampName(PdfName.Confidential);
        PdfStampAnnotation stamp3 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 150, 100, 50));
        stamp3.setStampName(PdfName.Departmental);
        PdfStampAnnotation stamp4 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 200, 100, 50));
        stamp4.setStampName(PdfName.Draft);
        PdfStampAnnotation stamp5 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 250, 100, 50));
        stamp5.setStampName(PdfName.Experimental);
        PdfStampAnnotation stamp6 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 300, 100, 50));
        stamp6.setStampName(PdfName.Expired);
        PdfStampAnnotation stamp7 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 350, 100, 50));
        stamp7.setStampName(PdfName.Final);
        PdfStampAnnotation stamp8 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 400, 100, 50));
        stamp8.setStampName(PdfName.ForComment);
        PdfStampAnnotation stamp9 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 450, 100, 50));
        stamp9.setStampName(PdfName.ForPublicRelease);
        PdfStampAnnotation stamp10 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 500, 100, 50));
        stamp10.setStampName(PdfName.NotApproved);
        PdfStampAnnotation stamp11 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 550, 100, 50));
        stamp11.setStampName(PdfName.NotForPublicRelease);
        PdfStampAnnotation stamp12 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 600, 100, 50));
        stamp12.setStampName(PdfName.Sold);
        PdfStampAnnotation stamp13 = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 650, 100, 50));
        stamp13.setStampName(PdfName.TopSecret);
        page1.addAnnotation(stamp);
        page1.addAnnotation(stamp1);
        page1.addAnnotation(stamp2);
        page1.addAnnotation(stamp3);
        page1.addAnnotation(stamp4);
        page1.addAnnotation(stamp5);
        page1.addAnnotation(stamp6);
        page1.addAnnotation(stamp7);
        page1.addAnnotation(stamp8);
        page1.addAnnotation(stamp9);
        page1.addAnnotation(stamp10);
        page1.addAnnotation(stamp11);
        page1.addAnnotation(stamp12);
        page1.addAnnotation(stamp13);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_rubberStampAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void rubberStampWrongStampTest() throws DocumentException, IOException, PdfException, InterruptedException{
        String filename =  destinationFolder + "rubberStampAnnotation02.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(pdfDoc1, new Rectangle(0, 0, 100, 50));

        stamp.setStampName(PdfName.StrikeOut);

        page1.addAnnotation(stamp);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_rubberStampAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void inkTest() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename = destinationFolder + "inkAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        float[] array1 = {100, 100, 100, 200, 200, 200, 300, 300};
        PdfArray firstPoint = new PdfArray(array1);

        PdfArray resultArray = new PdfArray(firstPoint);

        PdfDictionary borderStyle = new PdfDictionary();
        borderStyle.put(PdfName.Type, PdfName.Border);
        borderStyle.put(PdfName.W, new PdfNumber(3));

        PdfInkAnnotation ink = new PdfInkAnnotation(pdfDoc1, new Rectangle(0, 0, 575, 842), resultArray);
        ink.setBorderStyle(borderStyle);
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        ink.setColor(colors);
        page1.addAnnotation(ink);

        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_inkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest01() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename =  destinationFolder + "textMarkupAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("Underline!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createUnderline(pdfDoc1, PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest02() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename =  destinationFolder + "textMarkupAnnotation02.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("Highlight!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createHighLight(pdfDoc1, PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest03() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename =  destinationFolder + "textMarkupAnnotation03.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("Squiggly!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createSquiggly(pdfDoc1, PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest04() throws IOException, PdfException, DocumentException, InterruptedException {
        String filename =  destinationFolder + "textMarkupAnnotation04.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("Strikeout!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createStrikeout(pdfDoc1, PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void printerMarkText() throws PdfException, IOException, InterruptedException, DocumentException {
        String filename =  destinationFolder + "printerMarkAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvasText = new PdfCanvas(page1);
        canvasText
                .saveState()
                .beginText()
                .moveText(36, 790)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("This is Printer Mark annotation:")
                .endText()
                .restoreState();
        PdfFormXObject form = new PdfFormXObject(pdfDoc1, PageSize.A4);

        PdfCanvas canvas = new PdfCanvas(form);
        canvas
                .saveState()
                .circle(265, 795, 5)
                .setColor(Color.Green, true)
                .fill()
                .restoreState();
        canvas.release();

        PdfPrinterMarkAnnotation printer = new PdfPrinterMarkAnnotation(pdfDoc1, PageSize.A4, form);

        page1.addAnnotation(printer);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_printerMarkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void trapNetworkText() throws PdfException, IOException, InterruptedException, DocumentException {
        String filename = destinationFolder + "trapNetworkAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page = pdfDoc1.addNewPage();

        PdfCanvas canvasText = new PdfCanvas(page);

        canvasText
        .saveState()
                .beginText()
                .moveText(36, 790)
                .setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Helvetica), 16)
                .showText("This is Trap Network annotation:")
                .endText()
                .restoreState();

        PdfFormXObject form = new PdfFormXObject(pdfDoc1, PageSize.A4);
        PdfCanvas canvas = new PdfCanvas(form);
        canvas
                .saveState()
                .circle(272, 795, 5)
                .setColor(Color.Green, true)
                .fill()
                .restoreState();
        canvas.release();

        form.setProcessColorModel(PdfName.DeviceN);
        PdfTrapNetworkAnnotation trap = new PdfTrapNetworkAnnotation(pdfDoc1, PageSize.A4, form);
        Calendar calendar = new GregorianCalendar();
        calendar.set(2014, Calendar.APRIL, 30, 0, 0, 0);
        //calendar.set(date);
        trap.setLastModified(new PdfDate(calendar));

        page.addAnnotation(trap);
        page.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_trapNetworkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
