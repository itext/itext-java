package com.itextpdf.core.pdf.canvas;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfOutputStream;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfCaretAnnotation;
import com.itextpdf.core.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.core.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.core.pdf.annot.PdfFixedPrint;
import com.itextpdf.core.pdf.annot.PdfFreeTextAnnotation;
import com.itextpdf.core.pdf.annot.PdfInkAnnotation;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.core.pdf.annot.PdfPrinterMarkAnnotation;
import com.itextpdf.core.pdf.annot.PdfRedactAnnotation;
import com.itextpdf.core.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.core.pdf.annot.PdfSoundAnnotation;
import com.itextpdf.core.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.core.pdf.annot.PdfStampAnnotation;
import com.itextpdf.core.pdf.annot.PdfTextAnnotation;
import com.itextpdf.core.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.core.pdf.annot.PdfTrapNetworkAnnotation;
import com.itextpdf.core.pdf.annot.PdfWatermarkAnnotation;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAnnotationTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfAnnotationTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/PdfAnnotationTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void addLinkAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "linkAnnotation01.pdf")));

        PdfPage page1 = document.addNewPage();
        PdfPage page2 = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 1");
        canvas.moveText(0, -30);
        canvas.showText("Link to page 2. Click here!");
        canvas.endText();
        canvas.release();
        page1.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 260, 25)).setDestination(PdfExplicitDestination.createFit(page2)).setBorder(new PdfArray(new float[]{0, 0, 1})));
        page1.flush();

        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.COURIER_BOLD), 14);
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
        canvas.setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.endText();
        canvas.release();

        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com")).
                setBorder(new PdfArray(new float[]{0, 0, 1})).
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
        canvas.setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.moveText(0, -50);
        canvas.showText("Click here to go to itextpdf blog.");
        canvas.moveText(0, -50);
        canvas.showText("Click here to go to itextpdf FAQ.");
        canvas.endText();
        canvas.release();
        int[] borders = {0, 0, 1};
        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com")).
                setBorder(new PdfArray(borders)).
                setColor(new PdfArray(new float[]{1, 0, 0})));
        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 540, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com/node")).
                setBorder(new PdfArray(borders)).
                setColor(new PdfArray(new float[]{0, 1, 0})));
        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 490, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com/salesfaq")).
                setBorder(new PdfArray(borders)).
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

        PdfTextAnnotation textannot = new PdfTextAnnotation(new Rectangle(100, 600, 50, 40)).setText(new PdfString("Text Annotation 01")).setContents(new PdfString("Some contents..."));
        PdfPopupAnnotation popupAnnot = new PdfPopupAnnotation(new Rectangle(150, 640, 200, 100)).setOpen(true);
        textannot.setPopup(popupAnnot);
        popupAnnot.setParent(textannot);
        page.addAnnotation(textannot);
        page.addAnnotation(popupAnnot);
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "textAnnotation01.pdf", sourceFolder + "cmp_textAnnotation01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void caretTest() throws IOException,  InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("This is a text")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(236, 750)
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("This is an edited text")
                .endText()
                .restoreState();

        PdfCaretAnnotation caret = new PdfCaretAnnotation(new Rectangle(36, 745, 350, 20));
        caret.setSymbol(new PdfString("P"));

        PdfPopupAnnotation popup = new PdfPopupAnnotation(new Rectangle(36, 445, 100, 100));
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

        new PdfCanvas(page).beginText().setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.COURIER), 24).moveText(100, 600).showText("Annotated text").endText().release();
        PdfFreeTextAnnotation textannot = new PdfFreeTextAnnotation(new Rectangle(300, 700, 150, 20), "").
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

        PdfSquareAnnotation square = new PdfSquareAnnotation(new Rectangle(100, 700, 100, 100)).setInteriorColor(new float[]{1, 0, 0}).setColor(new float[]{0, 1, 0}).setContents("RED Square");
        page.addAnnotation(square);
        PdfCircleAnnotation circle = new PdfCircleAnnotation(new Rectangle(300, 700, 100, 100)).setInteriorColor(new float[]{0, 1, 0}).setColor(new float[]{0, 0, 1}).setContents(new PdfString("GREEN Circle"));
        page.addAnnotation(circle);
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "squareAndCircleAnnotations01.pdf", sourceFolder + "cmp_squareAndCircleAnnotations01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void fileAttachmentTest() throws IOException,  InterruptedException {
        String filename = destinationFolder + "fileAttachmentAnnotation.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc1, sourceFolder + "sample.wav", null, "sample.wav", null, null, true);

        PdfFileAttachmentAnnotation fileAttach = new PdfFileAttachmentAnnotation(new Rectangle(100, 100), spec);
        fileAttach.setIconName(PdfName.Paperclip);
        page1.addAnnotation(fileAttach);

        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_fileAttachmentAnnotation.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void rubberStampTest() throws  IOException, InterruptedException{
        String filename =  destinationFolder + "rubberStampAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
        stamp.setStampName(PdfName.Approved);
        PdfStampAnnotation stamp1 = new PdfStampAnnotation(new Rectangle(0, 50, 100, 50));
        stamp1.setStampName(PdfName.AsIs);
        PdfStampAnnotation stamp2 = new PdfStampAnnotation(new Rectangle(0, 100, 100, 50));
        stamp2.setStampName(PdfName.Confidential);
        PdfStampAnnotation stamp3 = new PdfStampAnnotation(new Rectangle(0, 150, 100, 50));
        stamp3.setStampName(PdfName.Departmental);
        PdfStampAnnotation stamp4 = new PdfStampAnnotation(new Rectangle(0, 200, 100, 50));
        stamp4.setStampName(PdfName.Draft);
        PdfStampAnnotation stamp5 = new PdfStampAnnotation(new Rectangle(0, 250, 100, 50));
        stamp5.setStampName(PdfName.Experimental);
        PdfStampAnnotation stamp6 = new PdfStampAnnotation(new Rectangle(0, 300, 100, 50));
        stamp6.setStampName(PdfName.Expired);
        PdfStampAnnotation stamp7 = new PdfStampAnnotation(new Rectangle(0, 350, 100, 50));
        stamp7.setStampName(PdfName.Final);
        PdfStampAnnotation stamp8 = new PdfStampAnnotation(new Rectangle(0, 400, 100, 50));
        stamp8.setStampName(PdfName.ForComment);
        PdfStampAnnotation stamp9 = new PdfStampAnnotation(new Rectangle(0, 450, 100, 50));
        stamp9.setStampName(PdfName.ForPublicRelease);
        PdfStampAnnotation stamp10 = new PdfStampAnnotation(new Rectangle(0, 500, 100, 50));
        stamp10.setStampName(PdfName.NotApproved);
        PdfStampAnnotation stamp11 = new PdfStampAnnotation(new Rectangle(0, 550, 100, 50));
        stamp11.setStampName(PdfName.NotForPublicRelease);
        PdfStampAnnotation stamp12 = new PdfStampAnnotation(new Rectangle(0, 600, 100, 50));
        stamp12.setStampName(PdfName.Sold);
        PdfStampAnnotation stamp13 = new PdfStampAnnotation(new Rectangle(0, 650, 100, 50));
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
    public void rubberStampWrongStampTest() throws  IOException, InterruptedException{
        String filename =  destinationFolder + "rubberStampAnnotation02.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));

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
    public void inkTest() throws IOException,  InterruptedException {
        String filename = destinationFolder + "inkAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        float[] array1 = {100, 100, 100, 200, 200, 200, 300, 300};
        PdfArray firstPoint = new PdfArray(array1);

        PdfArray resultArray = new PdfArray();
        resultArray.add(firstPoint);

        PdfDictionary borderStyle = new PdfDictionary();
        borderStyle.put(PdfName.Type, PdfName.Border);
        borderStyle.put(PdfName.W, new PdfNumber(3));

        PdfInkAnnotation ink = new PdfInkAnnotation(new Rectangle(0, 0, 575, 842), resultArray);
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
    public void textMarkupTest01() throws IOException,  InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Underline!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createUnderline(PageSize.A4, points);
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
    public void textMarkupTest02() throws IOException,  InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Highlight!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createHighLight(PageSize.A4, points);
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
    public void textMarkupTest03() throws IOException,  InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Squiggly!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createSquiggly(PageSize.A4, points);
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
    public void textMarkupTest04() throws IOException,  InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Strikeout!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createStrikeout(PageSize.A4, points);
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
    public void printerMarkText() throws IOException, InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("This is Printer Mark annotation:")
                .endText()
                .restoreState();
        PdfFormXObject form = new PdfFormXObject(PageSize.A4);

        PdfCanvas canvas = new PdfCanvas(form, pdfDoc1);
        canvas
                .saveState()
                .circle(265, 795, 5)
                .setColor(Color.GREEN, true)
                .fill()
                .restoreState();
        canvas.release();

        PdfPrinterMarkAnnotation printer = new PdfPrinterMarkAnnotation(PageSize.A4, form);

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
    public void trapNetworkText() throws IOException, InterruptedException {
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
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("This is Trap Network annotation:")
                .endText()
                .restoreState();

        PdfFormXObject form = new PdfFormXObject(PageSize.A4);
        PdfCanvas canvas = new PdfCanvas(form, pdfDoc1);
        canvas
                .saveState()
                .circle(272, 795, 5)
                .setColor(Color.GREEN, true)
                .fill()
                .restoreState();
        canvas.release();

        form.setProcessColorModel(PdfName.DeviceN);
        PdfTrapNetworkAnnotation trap = new PdfTrapNetworkAnnotation(PageSize.A4, form);

        page.addAnnotation(trap);
        page.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_trapNetworkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestAif() throws  IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation02.pdf";
        String audioFile = sourceFolder + "sample.aif";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        String string = "";
        for (int i = 0; i < 4; i++) {
            string = string + (char) is.read();
        }
        if (string.equals("RIFF")) {
            is = new FileInputStream(audioFile);
            is.read();
        } else {
            is = new FileInputStream(audioFile);
        }

        PdfStream sound1 = new PdfStream(pdfDoc1, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), sound1);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestAiff() throws  IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation03.pdf";
        String audioFile = sourceFolder + "sample.aiff";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        String string = "";
        for (int i = 0; i < 4; i++) {
            string = string + (char) is.read();
        }
        if (string.equals("RIFF")) {
            is = new FileInputStream(audioFile);
            is.read();
        } else {
            is = new FileInputStream(audioFile);
        }

        PdfStream sound1 = new PdfStream(pdfDoc1, is);
        sound1.put(PdfName.R, new PdfNumber(44100));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), sound1);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestSnd() throws  IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation04.pdf";
        String audioFile = sourceFolder + "sample.snd";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        InputStream is = new FileInputStream(audioFile);

        PdfSoundAnnotation sound = new PdfSoundAnnotation(pdfDoc1, new Rectangle(100, 100, 100, 100), is, 44100, PdfName.Signed, 2, 16);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestWav() throws  IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation01.pdf";
        String audioFile = sourceFolder + "sample.wav";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        PdfSoundAnnotation sound = new PdfSoundAnnotation(pdfDoc1, new Rectangle(100, 100, 100, 100), is, 48000, PdfName.Signed, 2, 16);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestWav01() throws  IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation05.pdf";
        String audioFile = sourceFolder + "sample.wav";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        String header = "";
        for (int i = 0; i < 4; i++) {
            header = header + (char) is.read();
        }
        if (header.equals("RIFF")) {
            is = new FileInputStream(audioFile);
            is.read();
        } else {
            is = new FileInputStream(audioFile);
        }

        PdfStream soundStream = new PdfStream(pdfDoc1, is);

        soundStream.put(PdfName.R, new PdfNumber(48000));
        soundStream.put(PdfName.E, PdfName.Signed);
        soundStream.put(PdfName.B, new PdfNumber(16));
        soundStream.put(PdfName.C, new PdfNumber(2));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), soundStream);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation05.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenTestExternalWavFile() throws IOException,  InterruptedException {
        String filename = destinationFolder + "screenAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc1, "c:\\morph\\itext6\\itextpdf\\canvas\\src\\test\\resources\\com\\itextpdf\\canvas\\PdfAnnotationTest\\" + "sample.wav", true);

        PdfAction action = PdfAction.createRendition(pdfDoc1, sourceFolder+"sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc1.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation01.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile01() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation02.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc1, sourceFolder + "sample.wav", null, "sample.wav", null, null, true);

        PdfAction action = PdfAction.createRendition(pdfDoc1, sourceFolder+"sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc1.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation02.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile02() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation03.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc1, new FileInputStream(sourceFolder + "sample.wav"), null, "sample.wav", null, null, true);

        PdfAction action = PdfAction.createRendition(pdfDoc1, sourceFolder+"sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc1.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation03.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile03() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation04.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        InputStream is = new FileInputStream(sourceFolder + "sample.wav");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();

        while (reads != -1){
            baos.write(reads);
            reads = is.read();
        }

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc1, baos.toByteArray(), null, "sample.wav", null, null, null, true);

        PdfAction action = PdfAction.createRendition(pdfDoc1, sourceFolder+"sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc1.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation04.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void waterMarkTest() throws IOException,  InterruptedException {
        String filename = destinationFolder + "waterMarkAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        PdfWatermarkAnnotation watermark = new PdfWatermarkAnnotation(new Rectangle(400, 400, 200, 200));

        float[] arr = {1, 0, 0, 1, 0, 0};

        PdfFixedPrint fixedPrint = new PdfFixedPrint(pdfDoc1);
        fixedPrint.setMatrix(arr);
        fixedPrint.setHorizontalTranslation(0.5f);
        fixedPrint.setVerticalTranslation(0);

        watermark.setFixedPrint(fixedPrint);

        PdfFormXObject form = new PdfFormXObject(new Rectangle(200, 200));

        PdfCanvas canvas = new PdfCanvas(form, pdfDoc1);
        canvas
                .saveState()
                .circle(100, 100, 50)
                .setColor(Color.BLACK, true)
                .fill()
                .restoreState();
        canvas.release();

        watermark.setNormalAppearance(form.getPdfObject());
        watermark.setFlags(PdfAnnotation.Print);

        page1.addAnnotation(watermark);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_watermarkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void redactionTest() throws IOException,  InterruptedException {
        String filename = destinationFolder + "redactionAnnotation01.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();

        float[] rgb = { 0, 0, 0};
        float[] rgb1 = { 1, 0, 0};
        PdfRedactAnnotation redact = new PdfRedactAnnotation(new Rectangle(180, 531, 120, 49));

        PdfFormXObject formD = new PdfFormXObject(new Rectangle(180, 531, 120, 49));
        PdfCanvas canvasD = new PdfCanvas(formD, pdfDoc1);
        canvasD
                .setFillColorGray(0)
                .rectangle(180, 531, 120, 48)
                .fill();
        redact.setDownAppearance(formD.getPdfObject());

        PdfFormXObject formN = new PdfFormXObject(new Rectangle(179, 530, 122, 51));
        PdfCanvas canvasN = new PdfCanvas(formN, pdfDoc1);
        canvasN
                .setColor(Color.RED, true)
                .setLineWidth(1.5f)
                .setLineCapStyle(PdfCanvasConstants.LineCapStyle.PROJECTING_SQUARE)
                .rectangle(180, 531, 120, 48)
                .stroke()
                .rectangle(181, 532,118, 47)
                .closePath();
        redact.setNormalAppearance(formN.getPdfObject());

        PdfFormXObject formR = new PdfFormXObject(new Rectangle(180, 531, 120, 49));
        PdfCanvas canvasR = new PdfCanvas(formR, pdfDoc1);
        canvasR
                .saveState()
                .rectangle(180, 531, 120, 48)
                .fill()
                .restoreState()
                .release();
        redact.setRolloverAppearance(formR.getPdfObject());

        PdfFormXObject formRO = new PdfFormXObject(new Rectangle(180, 531, 120, 49));
        PdfCanvas canvasRO = new PdfCanvas(formRO, pdfDoc1);
        canvasRO
                .saveState()
                .rectangle(180, 531, 120, 48)
                .fill()
                .restoreState()
                .release();

        redact.setRedactRolloverAppearance(formRO.getPdfObject());

        redact.put(PdfName.OC, new PdfArray(rgb1));

        redact.setColor(rgb1);
        redact.setInteriorColor(rgb);
        page1.addAnnotation(redact);
        page1.flush();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_redactionAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
