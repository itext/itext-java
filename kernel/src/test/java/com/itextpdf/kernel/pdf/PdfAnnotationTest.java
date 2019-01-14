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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfTarget;
import com.itextpdf.kernel.pdf.annot.Pdf3DAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCaretAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFixedPrint;
import com.itextpdf.kernel.pdf.annot.PdfFreeTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfInkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPrinterMarkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfRedactAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTrapNetworkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWatermarkAnnotation;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;
import com.itextpdf.kernel.pdf.annot.da.ExtendedAnnotationFont;
import com.itextpdf.kernel.pdf.annot.da.StandardAnnotationFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfNamedDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Category(IntegrationTest.class)
public class PdfAnnotationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfAnnotationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfAnnotationTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void addLinkAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "linkAnnotation01.pdf"));

        PdfPage page1 = document.addNewPage();
        PdfPage page2 = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
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
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
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
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "linkAnnotation02.pdf"));

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
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
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "linkAnnotation03.pdf"));

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
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


        document = new PdfDocument(new PdfReader(destinationFolder + "linkAnnotation03.pdf"));
        page = document.getPage(1);
        Assert.assertEquals(3, page.getAnnotsSize());
        List<PdfAnnotation> annotations = page.getAnnotations();
        Assert.assertEquals(3, annotations.size());
        PdfLinkAnnotation link = (PdfLinkAnnotation) annotations.get(0);
        Assert.assertEquals(page, link.getPage());
        document.close();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.DESTINATION_NOT_PERMITTED_WHEN_ACTION_IS_SET)})
    public void linkAnnotationActionDestinationTest() throws IOException, InterruptedException {
        String fileName = "linkAnnotationActionDestinationTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + fileName)));
        PdfArray array = new PdfArray();
        array.add(pdfDocument.addNewPage().getPdfObject());
        array.add(PdfName.XYZ);
        array.add(new PdfNumber(36));
        array.add(new PdfNumber(100));
        array.add(new PdfNumber(1));

        PdfDestination dest = PdfDestination.makeDestination(array);

        PdfLinkAnnotation link = new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0));
        link.setAction(PdfAction.createGoTo("abc"));
        link.setDestination(dest);

        pdfDocument.getPage(1).addAnnotation(link);

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void addTextAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "textAnnotation01.pdf"));

        PdfPage page = document.addNewPage();

        PdfTextAnnotation textannot = new PdfTextAnnotation(new Rectangle(100, 600, 50, 40));
        textannot.setText(new PdfString("Text Annotation 01")).setContents(new PdfString("Some contents..."));
        PdfPopupAnnotation popupAnnot = new PdfPopupAnnotation(new Rectangle(150, 640, 200, 100));
        popupAnnot.setOpen(true);
        textannot.setPopup(popupAnnot);
        popupAnnot.setParent(textannot);
        page.addAnnotation(textannot);
        page.addAnnotation(popupAnnot);
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "textAnnotation01.pdf", sourceFolder + "cmp_textAnnotation01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void caretTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "caretAnnotation.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("This is a text")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(236, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_CaretAnnotation.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void addFreeTextAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "freeTextAnnotation01.pdf"));

        PdfPage page = document.addNewPage();

        new PdfCanvas(page).beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24).moveText(100, 600).showText("Annotated text").endText().release();
        PdfFreeTextAnnotation textannot = new PdfFreeTextAnnotation(new Rectangle(300, 700, 150, 20), new PdfString("FreeText annotation"));
        textannot.setDefaultAppearance(new AnnotationDefaultAppearance().setFont(StandardAnnotationFont.TimesRoman));
        textannot.setColor(new float[]{1, 0, 0});
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
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "squareAndCircleAnnotations01.pdf"));

        PdfPage page = document.addNewPage();

        PdfSquareAnnotation square = new PdfSquareAnnotation(new Rectangle(100, 700, 100, 100));
        square.setInteriorColor(new float[]{1, 0, 0}).setColor(new float[]{0, 1, 0}).setContents("RED Square");
        page.addAnnotation(square);
        PdfCircleAnnotation circle = new PdfCircleAnnotation(new Rectangle(300, 700, 100, 100));
        circle.setInteriorColor(new float[]{0, 1, 0}).setColor(new float[]{0, 0, 1}).setContents(new PdfString("GREEN Circle"));
        page.addAnnotation(circle);
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "squareAndCircleAnnotations01.pdf", sourceFolder + "cmp_squareAndCircleAnnotations01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void fileAttachmentTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "fileAttachmentAnnotation.pdf";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfPage page1 = pdfDoc.addNewPage();

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, sourceFolder + "sample.wav", null, "sample.wav", null, null);

        PdfFileAttachmentAnnotation fileAttach = new PdfFileAttachmentAnnotation(new Rectangle(100, 100), spec);
        fileAttach.setIconName(PdfName.Paperclip);
        page1.addAnnotation(fileAttach);

        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_fileAttachmentAnnotation.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void fileAttachmentTargetTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "fileAttachmentTargetTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, sourceFolder + "sample.pdf", null, "embedded_doc.pdf", null, null);
        PdfFileAttachmentAnnotation fileAttachmentAnnotation = new PdfFileAttachmentAnnotation(new Rectangle(300, 500, 50, 50), spec);
        fileAttachmentAnnotation.setName(new PdfString("FileAttachmentAnnotation1"));
        pdfDoc.addNewPage();
        pdfDoc.addNewPage().addAnnotation(fileAttachmentAnnotation);

        PdfArray array = new PdfArray();
        array.add(pdfDoc.getPage(2).getPdfObject());
        array.add(PdfName.XYZ);
        array.add(new PdfNumber(pdfDoc.getPage(2).getPageSize().getLeft()));
        array.add(new PdfNumber(pdfDoc.getPage(2).getPageSize().getTop()));
        array.add(new PdfNumber(1));
        pdfDoc.addNamedDestination("FileAttachmentDestination1", array);

        PdfTarget target = PdfTarget.createChildTarget();
        target.getPdfObject().put(PdfName.P, new PdfString("FileAttachmentDestination1"));
        target.getPdfObject().put(PdfName.A, fileAttachmentAnnotation.getName());

        // just test functionality to get annotation /* DEVSIX-1503 */
        target.getAnnotation(pdfDoc);

        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(400, 500, 50, 50));
        linkAnnotation.setColor(ColorConstants.RED);
        linkAnnotation.setAction(PdfAction.createGoToE(new PdfNamedDestination("prime"), true, target));
        pdfDoc.getFirstPage().addAnnotation(linkAnnotation);

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_fileAttachmentTargetTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    /**
     * see DEVSIX-1539
     */
    @Test
    public void fileAttachmentAppendModeTest() throws IOException, InterruptedException {
        String fileName = destinationFolder + "fileAttachmentAppendModeTest.pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument inputDoc = new PdfDocument(new PdfWriter(baos));
        PdfPage page1 = inputDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("This is a text")
                .endText()
                .restoreState();
        inputDoc.close();

        PdfDocument finalDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())), new PdfWriter(fileName), new StampingProperties().useAppendMode());
        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(finalDoc, "Some test".getBytes(), null, "test.txt", null);
        finalDoc.addFileAttachment("some_test", spec);
        finalDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, sourceFolder + "cmp_fileAttachmentAppendModeTest.pdf", destinationFolder, "diff_"));
    }


    @Test
    public void rubberStampTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "rubberStampAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();
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

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_rubberStampAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void rubberStampWrongStampTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "rubberStampAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));

        stamp.setStampName(PdfName.StrikeOut);

        page1.addAnnotation(stamp);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_rubberStampAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void inkTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "inkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_inkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest03() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest04() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void printerMarkText() throws IOException, InterruptedException {
        String filename = destinationFolder + "printerMarkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvasText = new PdfCanvas(page1);
        canvasText
                .saveState()
                .beginText()
                .moveText(36, 790)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("This is Printer Mark annotation:")
                .endText()
                .restoreState();
        PdfFormXObject form = new PdfFormXObject(PageSize.A4);

        PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
        canvas
                .saveState()
                .circle(265, 795, 5)
                .setColor(ColorConstants.GREEN, true)
                .fill()
                .restoreState();
        canvas.release();

        PdfPrinterMarkAnnotation printer = new PdfPrinterMarkAnnotation(PageSize.A4, form);

        page1.addAnnotation(printer);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_printerMarkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void trapNetworkText() throws IOException, InterruptedException {
        String filename = destinationFolder + "trapNetworkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvasText = new PdfCanvas(page);

        canvasText
                .saveState()
                .beginText()
                .moveText(36, 790)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("This is Trap Network annotation:")
                .endText()
                .restoreState();

        PdfFormXObject form = new PdfFormXObject(PageSize.A4);
        PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
        canvas
                .saveState()
                .circle(272, 795, 5)
                .setColor(ColorConstants.GREEN, true)
                .fill()
                .restoreState();
        canvas.release();

        form.setProcessColorModel(PdfName.DeviceN);
        PdfTrapNetworkAnnotation trap = new PdfTrapNetworkAnnotation(PageSize.A4, form);

        page.addAnnotation(trap);
        page.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_trapNetworkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestAif() throws IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation02.pdf";
        String audioFile = sourceFolder + "sample.aif";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

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

        PdfStream sound1 = new PdfStream(pdfDoc, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), sound1);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestAiff() throws IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation03.pdf";
        String audioFile = sourceFolder + "sample.aiff";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

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

        PdfStream sound1 = new PdfStream(pdfDoc, is);
        sound1.put(PdfName.R, new PdfNumber(44100));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), sound1);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestSnd() throws IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation04.pdf";
        String audioFile = sourceFolder + "sample.snd";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);

        PdfSoundAnnotation sound = new PdfSoundAnnotation(pdfDoc, new Rectangle(100, 100, 100, 100), is, 44100, PdfName.Signed, 2, 16);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestWav() throws IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation01.pdf";
        String audioFile = sourceFolder + "sample.wav";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        PdfSoundAnnotation sound = new PdfSoundAnnotation(pdfDoc, new Rectangle(100, 100, 100, 100), is, 48000, PdfName.Signed, 2, 16);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestWav01() throws IOException, InterruptedException, UnsupportedAudioFileException {
        String filename = destinationFolder + "soundAnnotation05.pdf";
        String audioFile = sourceFolder + "sample.wav";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

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

        PdfStream soundStream = new PdfStream(pdfDoc, is);

        soundStream.put(PdfName.R, new PdfNumber(48000));
        soundStream.put(PdfName.E, PdfName.Signed);
        soundStream.put(PdfName.B, new PdfNumber(16));
        soundStream.put(PdfName.C, new PdfNumber(2));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), soundStream);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation05.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenTestExternalWavFile() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        FileOutputStream fos = new FileOutputStream(destinationFolder + "sample.wav");
        FileInputStream fis = new FileInputStream(sourceFolder + "sample.wav");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        fis.close();
        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");

        PdfAction action = PdfAction.createRendition("sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenTestEmbeddedWavFile01() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, sourceFolder + "sample.wav", null, "sample.wav", null, null);

        PdfAction action = PdfAction.createRendition(sourceFolder + "sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation02.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile02() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, new FileInputStream(sourceFolder + "sample.wav"), null, "sample.wav", null, null);

        PdfAction action = PdfAction.createRendition(sourceFolder + "sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation03.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile03() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        InputStream is = new FileInputStream(sourceFolder + "sample.wav");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();

        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, baos.toByteArray(), null, "sample.wav", null, null, null);

        PdfAction action = PdfAction.createRendition(sourceFolder + "sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation04.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void waterMarkTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "watermarkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfWatermarkAnnotation watermark = new PdfWatermarkAnnotation(new Rectangle(400, 400, 200, 200));

        float[] arr = {1, 0, 0, 1, 0, 0};

        PdfFixedPrint fixedPrint = new PdfFixedPrint();
        fixedPrint.setMatrix(arr);
        fixedPrint.setHorizontalTranslation(0.5f);
        fixedPrint.setVerticalTranslation(0);

        watermark.setFixedPrint(fixedPrint);

        PdfFormXObject form = new PdfFormXObject(new Rectangle(200, 200));

        PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
        canvas
                .saveState()
                .circle(100, 100, 50)
                .setColor(ColorConstants.BLACK, true)
                .fill()
                .restoreState();
        canvas.release();

        watermark.setNormalAppearance(form.getPdfObject());
        watermark.setFlags(PdfAnnotation.PRINT);

        page1.addAnnotation(watermark);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_watermarkAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void redactionTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "redactionAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        float[] rgb = {0, 0, 0};
        float[] rgb1 = {1, 0, 0};
        PdfRedactAnnotation redact = new PdfRedactAnnotation(new Rectangle(180, 531, 120, 49));

        PdfFormXObject formD = new PdfFormXObject(new Rectangle(180, 531, 120, 49));
        PdfCanvas canvasD = new PdfCanvas(formD, pdfDoc);
        canvasD
                .setFillColorGray(0)
                .rectangle(180, 531, 120, 48)
                .fill();
        redact.setDownAppearance(formD.getPdfObject());

        PdfFormXObject formN = new PdfFormXObject(new Rectangle(179, 530, 122, 51));
        PdfCanvas canvasN = new PdfCanvas(formN, pdfDoc);
        canvasN
                .setColor(ColorConstants.RED, true)
                .setLineWidth(1.5f)
                .setLineCapStyle(PdfCanvasConstants.LineCapStyle.PROJECTING_SQUARE)
                .rectangle(180, 531, 120, 48)
                .stroke()
                .rectangle(181, 532, 118, 47)
                .closePath();
        redact.setNormalAppearance(formN.getPdfObject());

        PdfFormXObject formR = new PdfFormXObject(new Rectangle(180, 531, 120, 49));
        PdfCanvas canvasR = new PdfCanvas(formR, pdfDoc);
        canvasR
                .saveState()
                .rectangle(180, 531, 120, 48)
                .fill()
                .restoreState()
                .release();
        redact.setRolloverAppearance(formR.getPdfObject());

        PdfFormXObject formRO = new PdfFormXObject(new Rectangle(180, 531, 120, 49));
        PdfCanvas canvasRO = new PdfCanvas(formRO, pdfDoc);
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
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_redactionAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void defaultAppearanceTest() throws IOException, InterruptedException {
        String name = "defaultAppearance";
        String inPath = sourceFolder + "in_" + name + ".pdf";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = sourceFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inPath), new PdfWriter(outPath));
        PdfPage page = pdfDoc.getPage(1);

        Rectangle rect = new Rectangle(20, 700, 250, 50);
        page.addAnnotation(new PdfRedactAnnotation(rect)
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(new DeviceRgb(1.0f, 0, 0))
                        .setFont(StandardAnnotationFont.TimesBold)
                        .setFontSize(20))
                .setOverlayText(new PdfString("Redact RGB times-bold"))
        );
        rect.moveDown(80);
        page.addAnnotation(new PdfRedactAnnotation(rect)
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(DeviceCmyk.MAGENTA)
                        .setFont(StandardAnnotationFont.CourierOblique)
                        .setFontSize(20))
                .setOverlayText(new PdfString("Redact CMYK courier-oblique"))

        );
        rect.moveDown(80);
        page.addAnnotation(new PdfRedactAnnotation(rect)
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(DeviceGray.GRAY)
                        .setFont(ExtendedAnnotationFont.HeiseiMinW3)
                        .setFontSize(20))
                .setOverlayText(new PdfString("Redact Gray HeiseiMinW3"))
        );

        rect.moveUp(160).moveRight(260);
        page.addAnnotation(new PdfFreeTextAnnotation(rect, new PdfString("FreeText RGB times-bold"))
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(new DeviceRgb(1.0f, 0, 0))
                        .setFont(StandardAnnotationFont.TimesBold)
                        .setFontSize(20))
                .setColor(ColorConstants.WHITE)
        );
        rect.moveDown(80);
        page.addAnnotation(new PdfFreeTextAnnotation(rect, new PdfString("FreeText CMYK courier-oblique"))
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(DeviceCmyk.MAGENTA)
                        .setFont(StandardAnnotationFont.CourierOblique)
                        .setFontSize(20))
                .setColor(ColorConstants.WHITE)
        );
        rect.moveDown(80);
        page.addAnnotation(new PdfFreeTextAnnotation(rect, new PdfString("FreeText Gray HeiseiMinW3"))
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(DeviceGray.GRAY)
                        .setFont(ExtendedAnnotationFont.HeiseiMinW3)
                        .setFontSize(20))
                .setColor(ColorConstants.WHITE)
        );

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void make3dAnnotationTest() throws IOException {
        String filename = sourceFolder + "3d_annotation.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfPage page1 = pdfDoc.getPage(1);
        List<PdfAnnotation> annots = page1.getAnnotations();

        Assert.assertTrue(annots.get(0) instanceof Pdf3DAnnotation);
    }

    @Test
    public void add3dAnnotationTest() throws IOException, InterruptedException {

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "add3DAnnotation01.pdf"));
        Rectangle rect = new Rectangle(100, 400, 400, 400);

        PdfStream stream3D = new PdfStream(pdfDoc, new FileInputStream(sourceFolder + "teapot.u3d"));
        stream3D.put(PdfName.Type, new PdfName("3D"));
        stream3D.put(PdfName.Subtype, new PdfName("U3D"));
        stream3D.setCompressionLevel(CompressionConstants.UNDEFINED_COMPRESSION);
        stream3D.flush();

        PdfDictionary dict3D = new PdfDictionary();
        dict3D.put(PdfName.Type, new PdfName("3DView"));
        dict3D.put(new PdfName("XN"), new PdfString("Default"));
        dict3D.put(new PdfName("IN"), new PdfString("Unnamed"));
        dict3D.put(new PdfName("MS"), PdfName.M);
        dict3D.put(new PdfName("C2W"),
                new PdfArray(new float[]{1, 0, 0, 0, 0, -1, 0, 1, 0, 3, -235, 28}));
        dict3D.put(PdfName.CO, new PdfNumber(235));

        Pdf3DAnnotation annot = new Pdf3DAnnotation(rect, stream3D);
        annot.setContents(new PdfString("3D Model"));
        annot.setDefaultInitialView(dict3D);
        pdfDoc.addNewPage().addAnnotation(annot);
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "add3DAnnotation01.pdf",
                sourceFolder + "cmp_add3DAnnotation01.pdf", destinationFolder, "diff_"));

    }

}
