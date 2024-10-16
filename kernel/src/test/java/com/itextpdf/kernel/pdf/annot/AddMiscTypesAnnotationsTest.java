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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfTarget;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;
import com.itextpdf.kernel.pdf.annot.da.ExtendedAnnotationFont;
import com.itextpdf.kernel.pdf.annot.da.StandardAnnotationFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.pdf.navigation.PdfNamedDestination;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class AddMiscTypesAnnotationsTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/kernel/pdf/annot/AddMiscTypesAnnotationsTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/kernel/pdf/annot/AddMiscTypesAnnotationsTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }
    
    @Test
    public void addTextAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "textAnnotation01.pdf"));

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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "textAnnotation01.pdf", SOURCE_FOLDER
                        + "cmp_textAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addTextAnnotInTagged14PdfTest() throws Exception {
        String outPdf = DESTINATION_FOLDER + "addTextAnnotInTagged14PdfTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_addTextAnnotInTagged14PdfTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(
                outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_4)))) {
            pdfDoc.setTagged();

            PdfPage page = pdfDoc.addNewPage();

            PdfTextAnnotation annot = new PdfTextAnnotation(new Rectangle(100, 600, 50, 40));
            annot.setText(new PdfString("Text Annotation 01")).setContents(new PdfString("Some contents..."));
            page.addAnnotation(annot);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void caretTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "caretAnnotation.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_CaretAnnotation.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void addFreeTextAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "freeTextAnnotation01.pdf"));

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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "freeTextAnnotation01.pdf", SOURCE_FOLDER
                        + "cmp_freeTextAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addSquareAndCircleAnnotations01() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "squareAndCircleAnnotations01.pdf"));

        PdfPage page = document.addNewPage();

        PdfSquareAnnotation square = new PdfSquareAnnotation(new Rectangle(100, 700, 100, 100));
        square.setInteriorColor(new float[]{1, 0, 0}).setColor(new float[]{0, 1, 0}).setContents("RED Square");
        page.addAnnotation(square);
        PdfCircleAnnotation circle = new PdfCircleAnnotation(new Rectangle(300, 700, 100, 100));
        circle.setInteriorColor(new float[]{0, 1, 0}).setColor(new float[]{0, 0, 1}).setContents(new PdfString("GREEN Circle"));
        page.addAnnotation(circle);
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "squareAndCircleAnnotations01.pdf", SOURCE_FOLDER
                        + "cmp_squareAndCircleAnnotations01.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void fileAttachmentTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "fileAttachmentAnnotation.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfPage page1 = pdfDoc.addNewPage();

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, SOURCE_FOLDER + "sample.wav", null, "sample.wav", null, null);

        PdfFileAttachmentAnnotation fileAttach = new PdfFileAttachmentAnnotation(new Rectangle(100, 100), spec);
        fileAttach.setIconName(PdfName.Paperclip);
        page1.addAnnotation(fileAttach);

        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_fileAttachmentAnnotation.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void fileAttachmentTargetTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "fileAttachmentTargetTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, SOURCE_FOLDER + "sample.pdf", null, "embedded_doc.pdf", null, null);
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
        linkAnnotation.setAction(PdfAction.createGoToE(new PdfStringFS("Some fake destination"),
                new PdfNamedDestination("prime"), true, target));
        pdfDoc.getFirstPage().addAnnotation(linkAnnotation);

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_fileAttachmentTargetTest.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.EMBEDDED_GO_TO_DESTINATION_NOT_SPECIFIED)})
    public void noFileAttachmentTargetTest() throws IOException, InterruptedException {
        String fileName = "noFileAttachmentTargetTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + fileName));
        pdfDoc.addNewPage();

        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(400, 500, 50, 50));
        linkAnnotation.setAction(PdfAction.createGoToE(null, true, null));
        pdfDoc.getFirstPage().addAnnotation(linkAnnotation);

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(DESTINATION_FOLDER + fileName,
                SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    /**
     * see DEVSIX-1539
     */
    @Test
    public void fileAttachmentAppendModeTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "fileAttachmentAppendModeTest.pdf";
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

        PdfDocument finalDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())), CompareTool.createTestPdfWriter(fileName), new StampingProperties().useAppendMode());
        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(finalDoc, "Some test".getBytes(), null, "test.txt", null);
        finalDoc.addFileAttachment("some_test", spec);
        finalDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_fileAttachmentAppendModeTest.pdf",
                DESTINATION_FOLDER, "diff_"));
    }


    @Test
    public void rubberStampTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "rubberStampAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_rubberStampAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void rubberStampWrongStampTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "rubberStampAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));

        stamp.setStampName(PdfName.StrikeOut);

        page1.addAnnotation(stamp);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_rubberStampAnnotation02.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.assertNull(errorMessage);
        }
    }

    @Test
    public void inkTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "inkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_inkAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.assertNull(errorMessage);
        }
    }

    @Test
    public void printerMarkText() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "printerMarkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));
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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_printerMarkAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void trapNetworkText() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "trapNetworkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_trapNetworkAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void waterMarkTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "watermarkAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_watermarkAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void redactionTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "redactionAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

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
        String errorMessage = compareTool.compareByContent(filename, SOURCE_FOLDER + "cmp_redactionAnnotation01.pdf",
                DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void defaultAppearanceTest() throws IOException, InterruptedException {
        String name = "defaultAppearance";
        String inPath = SOURCE_FOLDER + "in_" + name + ".pdf";
        String outPath = DESTINATION_FOLDER + name + ".pdf";
        String cmpPath = SOURCE_FOLDER + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inPath), CompareTool.createTestPdfWriter(outPath));
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
        Assertions.assertNull(new CompareTool().compareByContent(outPath, cmpPath, DESTINATION_FOLDER, diff));
    }

    @Test
    public void make3dAnnotationTest() throws IOException {
        String filename = SOURCE_FOLDER + "3d_annotation.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfPage page1 = pdfDoc.getPage(1);
        List<PdfAnnotation> annots = page1.getAnnotations();

        Assertions.assertTrue(annots.get(0) instanceof Pdf3DAnnotation);
    }

    @Test
    public void add3dAnnotationTest() throws IOException, InterruptedException {

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "add3DAnnotation01.pdf"));
        Rectangle rect = new Rectangle(100, 400, 400, 400);

        PdfStream stream3D = new PdfStream(pdfDoc, FileUtil.getInputStreamForFile(SOURCE_FOLDER + "teapot.u3d"));
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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "add3DAnnotation01.pdf",
                SOURCE_FOLDER + "cmp_add3DAnnotation01.pdf", DESTINATION_FOLDER, "diff_"));

    }

}
