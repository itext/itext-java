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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceN;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.function.PdfType0Function;
import com.itextpdf.kernel.pdf.function.PdfType2Function;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfA2GraphicsCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2GraphicsCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2GraphicsCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void colorCheckTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent)) {

            float[] whitePoint = {0.9505f, 1f, 1.089f};
            float[] gamma = {2.2f, 2.2f, 2.2f};
            float[] matrix = {0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f};
            PdfCieBasedCs.CalRgb calRgb = new PdfCieBasedCs.CalRgb(whitePoint, null, gamma, matrix);

            PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

            canvas.getResources().setDefaultCmyk(calRgb);

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f))
            );
            Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.COLOR_SPACE_0_SHALL_HAVE_1_COMPONENTS,
                    PdfName.DefaultCMYK.getValue(), 4), e.getMessage());
        }
    }

    @Test
    public void colorCheckTest2() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_colorCheckTest2.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, null)) {

            float[] whitePoint = {0.9505f, 1f, 1.089f};
            float[] gamma = {2.2f, 2.2f, 2.2f};
            float[] matrix = {0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f};
            PdfCieBasedCs.CalRgb calRgb = new PdfCieBasedCs.CalRgb(whitePoint, null, gamma, matrix);

            PdfCieBasedCs.CalGray calGray = new PdfCieBasedCs.CalGray(whitePoint, null, 2.2f);

            PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

            canvas.getResources().setDefaultRgb(calRgb);
            canvas.getResources().setDefaultGray(calGray);

            String shortText = "text";

            PdfFont font = PdfFontFactory.createFont(
                    sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
            canvas.setFontAndSize(font, 12);
            canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();
            canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();
        }

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest3() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> doc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest4() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_colorCheckTest4.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest4.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.setFillColor(ColorConstants.BLUE);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        canvas.setFillColor(DeviceGray.BLACK);
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest5() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.CLIP);
        canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest6() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_colorCheckTest6.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest6.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.setFillColor(ColorConstants.RED);
        canvas.beginText().showText(shortText).endText();

        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest7() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_colorCheckTest7.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest7.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.INVISIBLE);
        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void defaultTextColorCheckTest() throws IOException {
        String outPdf = destinationFolder + "defaultColorCheck.pdf";

        PdfDocument pdfDocument = new PdfADocument(new PdfWriter(outPdf), PdfAConformance.PDF_A_2B, null);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState();
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("some text")
                .endText()
                .restoreState();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.IF_DEVICE_RGB_CMYK_GRAY_USED_IN_FILE_THAT_FILE_SHALL_CONTAIN_PDFA_OUTPUTINTENT_OR_DEFAULT_RGB_CMYK_GRAY_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void defaultTextColorCheckForInvisibleTextTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "defaultColorCheckInvisibleText.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_defaultColorCheckInvisibleText.pdf";

        PdfDocument pdfDocument = new PdfADocument(new PdfWriter(outPdf), PdfAConformance.PDF_A_2B, null);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState();
        canvas.beginText()
                .setTextRenderingMode(TextRenderingMode.INVISIBLE)
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("some text")
                .endText()
                .restoreState();

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void defaultStrokeColorCheckTest() throws IOException {
        String outPdf = destinationFolder + "defaultColorCheck.pdf";

        PdfDocument pdfDocument = new PdfADocument(new PdfWriter(outPdf), PdfAConformance.PDF_A_2B, null);
        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState();
        float[] whitePoint = {0.9505f, 1f, 1.089f};
        float[] gamma = {2.2f, 2.2f, 2.2f};
        float[] matrix = {0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f};
        PdfCieBasedCs.CalRgb calRgb = new PdfCieBasedCs.CalRgb(whitePoint, null, gamma, matrix);
        canvas.getResources().setDefaultRgb(calRgb);
        canvas.setFillColor(ColorConstants.BLUE);
        canvas.moveTo(pdfDocument.getDefaultPageSize().getLeft(), pdfDocument.getDefaultPageSize().getBottom());
        canvas.lineTo(pdfDocument.getDefaultPageSize().getRight(), pdfDocument.getDefaultPageSize().getBottom());
        canvas.lineTo(pdfDocument.getDefaultPageSize().getRight(), pdfDocument.getDefaultPageSize().getTop());
        canvas.stroke();

        // We set fill color but stroked so the exception should be thrown
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.IF_DEVICE_RGB_CMYK_GRAY_USED_IN_FILE_THAT_FILE_SHALL_CONTAIN_PDFA_OUTPUTINTENT_OR_DEFAULT_RGB_CMYK_GRAY_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void egsCheckTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> canvas.setExtGState(new PdfExtGState().put(PdfName.HTP, new PdfName("Test")))
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_HTP_KEY, e.getMessage());
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void egsCheckTest2() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent)) {

            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

            PdfDictionary dict = new PdfDictionary();
            dict.put(PdfName.HalftoneType, new PdfNumber(5));
            dict.put(PdfName.HalftoneName, new PdfName("Test"));

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(dict))
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.HALFTONES_SHALL_NOT_CONTAIN_HALFTONENAME, e.getMessage());
        }
    }

    @Test
    public void imageCheckTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.addImageAt(ImageDataFactory.create(sourceFolder + "jpeg2000/p0_01.j2k"), 300, 300, false);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.ONLY_JPX_BASELINE_SET_OF_FEATURES_SHALL_BE_USED, e.getMessage());
    }

    @Test
    public void imageCheckTest2() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.addImageAt(ImageDataFactory.create(sourceFolder + "jpeg2000/file5.jp2"), 300, 300, false);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.EXACTLY_ONE_COLOUR_SPACE_SPECIFICATION_SHALL_HAVE_THE_VALUE_0X01_IN_THE_APPROX_FIELD,
                e.getMessage());
    }

    @Test
    public void imageCheckTest3() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.addImageAt(ImageDataFactory.create(sourceFolder + "jpeg2000/file7.jp2"), 300, 300, false);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.EXACTLY_ONE_COLOUR_SPACE_SPECIFICATION_SHALL_HAVE_THE_VALUE_0X01_IN_THE_APPROX_FIELD,
                e.getMessage());
    }


    /**
     * NOTE: resultant file of this test fails acrobat's preflight check,
     * but it seems to me that preflight fails to check jpeg2000 file.
     * This file also fails check on http://www.pdf-tools.com/pdf/validate-pdfa-online.aspx,
     * but there it's stated that "The key ColorSpace is required but missing" but according to spec, jpeg2000 images
     * can omit ColorSpace entry if color space is defined implicitly in the image itself.
     */
    @Test
    public void imageCheckTest4() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_imageCheckTest4.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_imageCheckTest4.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas;

        for (int i = 1; i < 5; ++i) {
            canvas = new PdfCanvas(doc.addNewPage());
            canvas.addImageAt(ImageDataFactory.create(MessageFormatUtil.format(sourceFolder + "jpeg2000/file{0}.jp2", String.valueOf(i))), 300, 300, false);
        }
        canvas = new PdfCanvas(doc.addNewPage());
        canvas.addImageAt(ImageDataFactory.create(sourceFolder + "jpeg2000/file6.jp2"), 300, 300, false);
        for (int i = 8; i < 10; ++i) {
            canvas = new PdfCanvas(doc.addNewPage());
            canvas.addImageAt(ImageDataFactory.create(MessageFormatUtil.format(sourceFolder + "jpeg2000/file{0}.jp2", String.valueOf(i))), 300, 300, false);
        }

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void transparencyCheckTest1() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, null);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.saveState();
        canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Darken));
        canvas.rectangle(100, 100, 100, 100);
        canvas.fill();
        canvas.restoreState();

        canvas.saveState();
        canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Lighten));
        canvas.rectangle(200, 200, 100, 100);
        canvas.fill();
        canvas.restoreState();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE,
                e.getMessage());
    }

    @Test
    public void transparencyCheckTest2() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_transparencyCheckTest2.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_transparencyCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.saveState();
        canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Darken));
        canvas.rectangle(100, 100, 100, 100);
        canvas.fill();
        canvas.restoreState();

        canvas.saveState();
        canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Lighten));
        canvas.rectangle(200, 200, 100, 100);
        canvas.fill();
        canvas.restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void transparencyCheckTest3() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, outputIntent)) {

            PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

            canvas.saveState();
            canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Darken));
            canvas.rectangle(100, 100, 100, 100);
            canvas.fill();
            canvas.restoreState();

            canvas.saveState();
            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setBlendMode(new PdfName("UnknownBlendMode")))
            );
            Assertions.assertEquals(
                    PdfaExceptionMessageConstant.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY,
                    e.getMessage());
        }
    }

    @Test
    public void colourSpaceTest01() throws IOException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a stream
        byte[] samples = {0x00,0x00,0x00,0x01,0x01,0x01};
        float[] domain = new float[]{0,1};
        float[] range  = new float[]{0,1,0,1,0,1};
        int[] size = new int[]{2};
        int bitsPerSample = 8;

        PdfType0Function type0 = new PdfType0Function(domain, size, range, 1, bitsPerSample, samples);
        PdfColorSpace separationColourSpace = new PdfSpecialCs.Separation("separationTestFunction0",
                alternateSpace, type0);
        //Add to document
        page.getResources().addColorSpace(separationColourSpace);

        doc.close();
    }

    @Test
    public void colourSpaceTest02() throws IOException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a dictionary
        float[] domain = new float[]{0,1};
        float[] range = new float[]{0,1,0,1,0,1};
        float[] C0 = new float[]{0,0,0};
        float[] C1 = new float[]{1,1,1};
        int n = 1;

        PdfType2Function type2 = new PdfType2Function(domain, range, C0, C1, n);
        PdfColorSpace separationColourSpace = new PdfSpecialCs.Separation("separationTestFunction2",
                alternateSpace, type2);
        //Add to document
        page.getResources().addColorSpace(separationColourSpace);
        doc.close();
    }

    @Test
    public void colourSpaceTest03() throws IOException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a dictionary
        float[] domain = new float[]{0,1};
        float[] range  = new float[]{0,1,0,1,0,1};
        float[] C0 = new float[]{0,0,0};
        float[] C1 = new float[]{1,1,1};
        int n = 1;

        PdfType2Function type2 = new PdfType2Function(domain, range, C0, C1, n);

        PdfCanvas canvas = new PdfCanvas(page);
        String separationName = "separationTest";
        canvas.setColor(new Separation(separationName, alternateSpace, type2, 0.5f), true);

        PdfDictionary attributes = new PdfDictionary();
        PdfDictionary colorantsDict = new PdfDictionary();
        colorantsDict.put(new PdfName(separationName), new PdfSpecialCs.Separation(separationName, alternateSpace,type2).getPdfObject());
        attributes.put(PdfName.Colorants, colorantsDict);
        DeviceN deviceN = new DeviceN(new PdfSpecialCs.NChannel(Collections.singletonList(separationName), alternateSpace, type2, attributes), new float[]{0.5f});
        canvas.setColor(deviceN, true);

        doc.close();
    }

    @Test
    public void colourSpaceWithoutColourantsTest() throws IOException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a dictionary
        float[] domain = new float[]{0,1};
        float[] range  = new float[]{0,1,0,1,0,1};
        float[] C0 = new float[]{0,0,0};
        float[] C1 = new float[]{1,1,1};
        int n = 1;

        PdfType2Function type2 = new PdfType2Function(domain, range, C0, C1, n);

        PdfCanvas canvas = new PdfCanvas(page);
        String separationName = "separationTest";
        canvas.setColor(new Separation(separationName, alternateSpace, type2, 0.5f), true);

        PdfDictionary attributes = new PdfDictionary();
        PdfDictionary colorantsDict = new PdfDictionary();
        colorantsDict.put(new PdfName(separationName), new PdfSpecialCs.Separation(separationName, alternateSpace,type2).getPdfObject());
        DeviceN deviceN = new DeviceN(new PdfSpecialCs.NChannel(Collections.singletonList(separationName), alternateSpace, type2, attributes), new float[]{0.5f});
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () ->         canvas.setColor(deviceN, true));
        Assertions.assertEquals(PdfaExceptionMessageConstant.COLORANTS_DICTIONARY_SHALL_NOT_BE_EMPTY_IN_DEVICE_N_COLORSPACE, e.getMessage());
        doc.close();
    }

    @Test
    public void colourSpaceWithoutAttributesTest() throws IOException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a dictionary
        float[] domain = new float[]{0,1};
        float[] range  = new float[]{0,1,0,1,0,1};
        float[] C0 = new float[]{0,0,0};
        float[] C1 = new float[]{1,1,1};
        int n = 1;

        PdfType2Function type2 = new PdfType2Function(domain, range, C0, C1, n);

        PdfCanvas canvas = new PdfCanvas(page);
        String separationName = "separationTest";
        canvas.setColor(new Separation(separationName, alternateSpace, type2, 0.5f), true);

        PdfDictionary colorantsDict = new PdfDictionary();
        colorantsDict.put(new PdfName(separationName), new PdfSpecialCs.Separation(separationName, alternateSpace,type2).getPdfObject());
        DeviceN deviceN = new DeviceN(new PdfSpecialCs.DeviceN(Collections.singletonList(separationName), alternateSpace, type2), new float[]{0.5f});
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () ->         canvas.setColor(deviceN, true));
        Assertions.assertEquals(PdfaExceptionMessageConstant.COLORANTS_DICTIONARY_SHALL_NOT_BE_EMPTY_IN_DEVICE_N_COLORSPACE, e.getMessage());
        doc.close();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
