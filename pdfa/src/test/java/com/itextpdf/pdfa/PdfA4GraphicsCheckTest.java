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
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.IccBased;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
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
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfTransparencyGroup;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Canvas;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfA4GraphicsCheckTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String CMP_FOLDER = SOURCE_FOLDER + "cmp/PdfA4GraphicsCheckTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4GraphicsCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void validHalftoneTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));
            colourantHalftone.put(PdfName.TransferFunction, PdfName.Identity);

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(new PdfName("Green"), colourantHalftone);
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void validHalftoneType1Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone1.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone1.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(1));
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void validHalftoneTest2() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone2.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone2.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(new PdfName("Green"), colourantHalftone);
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        //this pdf is invalid according to pdf 2.0 so we don't use verapdf check here
        //Table 128 — Entries in a Type 1 halftone dictionary
        //TransferFunction - This entry shall be present if the dictionary is a component of a Type 5 halftone
        // (see 10.6.5.6, "Type 5 halftones") and represents either a nonprimary or nonstandard primary colour component
        // (see 10.5, "Transfer functions").
    }

    @Test
    public void validHalftoneTest3() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone3.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone3.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(PdfName.Cyan, colourantHalftone);
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        //this pdf is invalid according to pdf 2.0 so we don't use verapdf check here
        //Table 128 — Entries in a Type 1 halftone dictionary
        //TransferFunction - This entry shall be present if the dictionary is a component of a Type 5 halftone
        // (see 10.6.5.6, "Type 5 halftones") and represents either a nonprimary or nonstandard primary colour component
        // (see 10.5, "Transfer functions").
    }

    @Test
    public void invalidHalftoneTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(1));
            halftone.put(PdfName.TransferFunction, new PdfDictionary());

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(halftone))
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5,
                    e.getMessage());
        }
    }

    @Test
    public void invalidHalftoneTest2() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(PdfName.TransferFunction, new PdfDictionary());
            halftone.put(PdfName.Magenta, new PdfDictionary());
            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(halftone))
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5,
                    e.getMessage());
        }
    }

    @Test
    public void invalidHalftoneTest3() throws IOException {
        testWithColourant(PdfName.Cyan);
    }

    @Test
    public void invalidHalftoneTest4() throws IOException {
        testWithColourant(PdfName.Magenta);
    }

    @Test
    public void invalidHalftoneTest5() throws IOException {
        testWithColourant(PdfName.Yellow);
    }

    @Test
    public void invalidHalftoneTest6() throws IOException {
        testWithColourant(PdfName.Black);
    }

    @Test
    public void colorCheckTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {

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
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest2.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null)) {

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
                    SOURCE_FOLDER + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
            canvas.setFontAndSize(font, 12);
            canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();
            canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();
        }

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest3() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> doc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_SHALL_ONLY_BE_USED_IF_CURRENT_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest4() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest4.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest4.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

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
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_SHALL_ONLY_BE_USED_IF_CURRENT_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest5() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest5.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest5.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = doc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

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

        // Here we use RGB and CMYK at the same time. And only page output intent is taken into account not both.
        // So it throws on device RGB color.
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest6() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                SOURCE_FOLDER + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.CLIP);
        canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_SHALL_ONLY_BE_USED_IF_CURRENT_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest7() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest7.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest7.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = doc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                SOURCE_FOLDER + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.CLIP);
        canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        // Here we use RGB and CMYK at the same time. And only page output intent is taken into account not both.
        // So it throws on device RGB color.
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest8() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest8.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest8.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                SOURCE_FOLDER + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.INVISIBLE);
        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEVICECMYK_SHALL_ONLY_BE_USED_IF_CURRENT_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT,
                e.getMessage());
    }

    @Test
    public void colorCheckTest9() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest9.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest9.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = doc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(
                SOURCE_FOLDER + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.INVISIBLE);
        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest10() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest10.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest10.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // Add page blending colorspace
        PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
        PdfArray transparencyArray = new PdfArray(PdfName.ICCBased);
        transparencyArray.add(PdfCieBasedCs.IccBased.getIccProfileStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        transparencyGroup.setColorSpace(transparencyArray);
        page.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setStrokeColor(DeviceCmyk.MAGENTA).circle(250, 300, 50).stroke();

        pdfDoc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest11() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest11.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest11.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // Add page blending colorspace
        PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
        PdfArray transparencyArray = new PdfArray(PdfName.ICCBased);
        transparencyArray.add(PdfCieBasedCs.IccBased.getIccProfileStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        transparencyGroup.setColorSpace(transparencyArray);
        page.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        // Add annotation
        PdfAnnotation annot = new PdfCircleAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        // Draw annotation
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        Canvas annotCanvas = new Canvas(xObject, pdfDoc);
        annotCanvas.getPdfCanvas().setStrokeColor(DeviceCmyk.MAGENTA);
        annotCanvas.getPdfCanvas().circle(50, 50, 40).stroke();

        xObject.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        // Add appearance stream
        annot.setAppearance(PdfName.N, xObject.getPdfObject());
        page.addAnnotation(annot);

        pdfDoc.close();

        // Here we have blending colorspaces set on page and xobject level but verapdf still asserts
        // That's very weird
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorCheckTest12() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckTest12.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckTest12.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // Add page blending colorspace
        PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
        PdfArray transparencyArray = new PdfArray(PdfName.ICCBased);
        transparencyArray.add(PdfCieBasedCs.IccBased.getIccProfileStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        transparencyGroup.setColorSpace(transparencyArray);
        page.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setStrokeColor(DeviceRgb.BLUE).circle(250, 300, 50).stroke();

        pdfDoc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void defaultTextColorCheckTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "defaultColorCheck.pdf";

        PdfDocument pdfDocument = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
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
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.DEVICEGRAY_SHALL_ONLY_BE_USED_IF_CURRENT_PDFA_OUTPUT_INTENT_OR_DEFAULTGRAY_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void defaultTextColorCheckWithPageOutputIntentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "defaultTextColorCheckWithPageOutputIntent.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_defaultTextColorCheckWithPageOutputIntent.pdf";

        PdfDocument pdfDocument = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page = pdfDocument.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState();
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("some text")
                .endText()
                .restoreState();

        pdfDocument.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void defaultTextColorCheckForInvisibleTextTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_defaultColorCheckInvisibleText.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_defaultColorCheckInvisibleText.pdf";

        PdfDocument pdfDocument = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
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
        String outPdf = DESTINATION_FOLDER + "defaultColorCheck.pdf";

        PdfDocument pdfDocument = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);
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

        // We set fill color but don't set stroke, so the exception should be thrown
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.DEVICEGRAY_SHALL_ONLY_BE_USED_IF_CURRENT_PDFA_OUTPUT_INTENT_OR_DEFAULTGRAY_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void colorCheckWithDuplicatedCmykColorspaceTest1() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicatedCmykColorspace1.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicatedCmykColorspace1.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

        // Create color
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased magenta = new IccBased(stream, new float[]{0f, 1f, 0f, 0f});

        canvas.setStrokeColor(magenta).circle(250, 300, 50).stroke();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE),
                e.getMessage());
    }

    @Test
    public void colorCheckWithDuplicatedCmykColorspaceTest2() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicatedCmykColorspace2.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicatedCmykColorspace2.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();
        // Add page blending colorspace
        PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
        PdfArray transparencyArray = new PdfArray(PdfName.ICCBased);
        transparencyArray.add(PdfCieBasedCs.IccBased.getIccProfileStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        transparencyGroup.setColorSpace(transparencyArray);
        page.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        PdfCanvas canvas = new PdfCanvas(page);

        // Create color
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased magenta = new IccBased(stream, new float[]{0f, 1f, 0f, 0f});

        canvas.setStrokeColor(magenta).circle(250, 300, 50).stroke();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
              Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE),
                    e.getMessage());
    }

    @Test
    public void colorCheckWithDuplicatedCmykColorspaceTest3() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicatedCmykColorspace3.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicatedCmykColorspace3.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();
        // Add page blending colorspace
        PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
        PdfArray transparencyArray = new PdfArray(PdfName.ICCBased);
        transparencyArray.add(PdfCieBasedCs.IccBased.getIccProfileStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        transparencyGroup.setColorSpace(transparencyArray);
        page.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        // Create color
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased magenta = new IccBased(stream, new float[]{0f, 1f, 0f, 0f});

        // Add annotation
        PdfAnnotation annot = new PdfCircleAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        // Draw annotation
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        Canvas annotCanvas = new Canvas(xObject, pdfDoc);
        annotCanvas.getPdfCanvas().setStrokeColor(magenta);
        annotCanvas.getPdfCanvas().circle(50, 50, 40).stroke();

        // Add appearance stream
        annot.setAppearance(PdfName.N, xObject.getPdfObject());
        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
              Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE),
                    e.getMessage());
    }

    @Test
    public void colorCheckWithDuplicatedCmykColorspaceTest4() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicatedCmykColorspace4.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicatedCmykColorspace4.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        // Create color
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased magenta = new IccBased(stream, new float[]{0f, 1f, 0f, 0f});

        canvas.setStrokeColor(magenta).circle(250, 300, 50).stroke();

        // Add annotation
        PdfAnnotation annot = new PdfCircleAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        // Draw annotation
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        Canvas annotCanvas = new Canvas(xObject, pdfDoc);
        annotCanvas.getPdfCanvas().setStrokeColor(magenta);
        annotCanvas.getPdfCanvas().circle(50, 50, 40).stroke();

        // Add stream blending colorspace
        PdfTransparencyGroup transparencyGroup = new PdfTransparencyGroup();
        PdfArray transparencyArray = new PdfArray(PdfName.ICCBased);
        transparencyArray.add(PdfCieBasedCs.IccBased.getIccProfileStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        transparencyGroup.setColorSpace(transparencyArray);
        xObject.getPdfObject().put(PdfName.Group, transparencyGroup.getPdfObject());

        // Add appearance stream
        annot.setAppearance(PdfName.N, xObject.getPdfObject());
        page.addAnnotation(annot);

        // Verapdf doesn't assert for such file however the expectation is that it's not a valid pdfa4
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE),
                e.getMessage());
    }

    @Test
    public void colorCheckWithDuplicatedRgbColorspaceTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicatedRgbColorspace.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicatedRgbColorspace.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        // Create color
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        IccBased green = new IccBased(stream, new float[]{0f, 1f, 0f});

        canvas.setStrokeColor(green).circle(250, 300, 50).stroke();

        pdfDoc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckWithDuplicatedRgbAndCmykColorspaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicatedRgbAndCmykColorspace.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicatedRgbAndCmykColorspace.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

        // Create colors
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        IccBased green = new IccBased(stream, new float[]{0f, 1f, 0f});
        stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased magenta = new IccBased(stream, new float[]{0f, 1f, 0f, 0f});

        canvas.setStrokeColor(green).setFillColor(magenta).circle(250, 300, 50).fillStroke();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE),
                e.getMessage());
    }

    @Test
    public void colorCheckWithDuplicated2CmykColorspacesTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colorCheckWithDuplicated2CmykColorspaces.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colorCheckWithDuplicated2CmykColorspaces.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent);

        PdfPage page = pdfDoc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

        // Create colors
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased cayan = new IccBased(stream, new float[]{1f, 0f, 0f, 0f});
        stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ISOcoated_v2_300_bas.icc");
        IccBased magenta = new IccBased(stream, new float[]{0f, 1f, 0f, 0f});

        canvas.setStrokeColor(cayan).setFillColor(magenta).circle(250, 300, 50).fillStroke();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE),
                e.getMessage());
    }

    @Test
    public void colourSpaceTest01() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colourSpaceTest01.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colourSpaceTest01.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colourSpaceTest02() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colourSpaceTest02.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colourSpaceTest02.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colourSpaceTest03() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_colourSpaceTest03.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_colourSpaceTest03.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void imageFailureTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "imageFailure.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_imageFailure.pdf";

        PdfDocument pdfDoc = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // This should suppress transparency issue
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.saveState();
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"),
                new Rectangle(0, 0, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2), false);
        canvas.restoreState();

        // But devicergb should still be not allowed
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void imageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_image.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_image.pdf";

        PdfDocument pdfDoc = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // This should suppress transparency and device RGB
        page.addOutputIntent(new PdfOutputIntent("Custom", "",
                "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.saveState();
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"),
                new Rectangle(0, 0, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2), false);
        canvas.restoreState();

        pdfDoc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void imageJpeg20002ColorChannelsTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_jpeg2000.pdf";

        PdfDocument pdfDoc = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // This should suppress transparency and device RGB
        page.addOutputIntent(new PdfOutputIntent("Custom", "",
                "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.saveState();
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(SOURCE_FOLDER + "jpeg2000/bee2colorchannels.jp2"),
                new Rectangle(0, 0, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2), false);
        canvas.restoreState();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfDoc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_NUMBER_OF_COLOUR_CHANNELS_IN_THE_JPEG2000_DATA_SHALL_BE_1_3_OR_4, e.getMessage());
    }

    @Test
    public void imageJpeg2000Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_jpeg2000.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_jpeg2000.pdf";

        PdfDocument pdfDoc = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);

        PdfPage page = pdfDoc.addNewPage();
        // This should suppress transparency and device RGB
        page.addOutputIntent(new PdfOutputIntent("Custom", "",
                "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.saveState();
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(SOURCE_FOLDER + "jpeg2000/bee.jp2"),
                new Rectangle(0, 0, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2), false);
        canvas.restoreState();

        pdfDoc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4AnnotationsNoOutputIntentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AnnotationsNoOutputIntent.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AnnotationsNoOutputIntent.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);
        PdfPage page = pdfDoc.addNewPage();

        PdfAnnotation annot = new PdfCircleAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);

        // Draw annotation
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        Canvas canvas = new Canvas(xObject, pdfDoc);
        canvas.getPdfCanvas().setFillColor(DeviceRgb.RED);
        canvas.getPdfCanvas().circle(50, 50, 40).stroke();

        annot.setAppearance(PdfName.N, xObject.getPdfObject());
        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void pdfA4AnnotationsWrongPageOutputIntentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AnnotationsWrongPageOutputIntent.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AnnotationsWrongPageOutputIntent.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);
        PdfPage page = pdfDoc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc")));

        PdfAnnotation annot = new PdfCircleAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setContents("Circle");
        annot.setColor(DeviceRgb.BLUE);

        // Draw annotation
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        Canvas canvas = new Canvas(xObject, pdfDoc);
        canvas.getPdfCanvas().setStrokeColor(DeviceRgb.BLUE);
        canvas.getPdfCanvas().circle(50, 50, 40).stroke();

        annot.setAppearance(PdfName.N, xObject.getPdfObject());
        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT),
                e.getMessage());
    }

    @Test
    public void pdfA4AnnotationsCorrectPageOutputIntentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AnnotationsCorrectPageOutputIntent.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AnnotationsCorrectPageOutputIntent.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);
        PdfPage page = pdfDoc.addNewPage();
        page.addOutputIntent(new PdfOutputIntent("Custom", "",
                "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));

        PdfAnnotation annot = new PdfCircleAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setContents("Circle");
        annot.setColor(DeviceRgb.BLUE);

        // Draw annotation
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        Canvas canvas = new Canvas(xObject, pdfDoc);
        canvas.getPdfCanvas().setStrokeColor(DeviceRgb.BLUE);
        canvas.getPdfCanvas().circle(50, 50, 40).stroke();

        annot.setAppearance(PdfName.N, xObject.getPdfObject());
        page.addAnnotation(annot);

        pdfDoc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void destOutputIntentProfileNotAllowedTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4DestOutputIntentProfileNotAllowed.pdf";
        String isoFilePath = SOURCE_FOLDER + "ISOcoated_v2_300_bas.icc";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        byte[] bytes = Files.readAllBytes(Paths.get(isoFilePath));
        byte[] manipulatedBytes = new String(bytes, StandardCharsets.US_ASCII).replace("prtr", "not_def").getBytes(StandardCharsets.US_ASCII);

        PdfOutputIntent pdfOutputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(isoFilePath));

        pdfOutputIntent.getPdfObject().put(PdfName.DestOutputProfile, new PdfStream(manipulatedBytes));
        pdfDoc.addOutputIntent(pdfOutputIntent);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfDoc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.PROFILE_STREAM_OF_OUTPUTINTENT_SHALL_BE_OUTPUT_PROFILE_PRTR_OR_MONITOR_PROFILE_MNTR, e.getMessage());
    }

    @Test
    public void destOutputIntentProfileNotAllowedInPageTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4DestOutputIntentProfileNotAllowedInPage.pdf";
        String isoFilePath = SOURCE_FOLDER + "ISOcoated_v2_300_bas.icc";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);
        PdfPage page = pdfDoc.addNewPage();

        byte[] bytes = Files.readAllBytes(Paths.get(isoFilePath));
        byte[] manipulatedBytes = new String(bytes, StandardCharsets.US_ASCII).replace("prtr", "not_def").getBytes(StandardCharsets.US_ASCII);

        PdfOutputIntent pdfOutputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(isoFilePath));

        pdfOutputIntent.getPdfObject().put(PdfName.DestOutputProfile, new PdfStream(manipulatedBytes));
        page.addOutputIntent(pdfOutputIntent);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfDoc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.PROFILE_STREAM_OF_OUTPUTINTENT_SHALL_BE_OUTPUT_PROFILE_PRTR_OR_MONITOR_PROFILE_MNTR, e.getMessage());
    }

    @Test
    public void destOutputIntentColorSpaceNotAllowedTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4DestOutputIntentProfileNotAllowed.pdf";
        String isoFilePath = SOURCE_FOLDER + "ISOcoated_v2_300_bas.icc";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        byte[] bytes = Files.readAllBytes(Paths.get(isoFilePath));
        byte[] manipulatedBytes = new String(bytes, StandardCharsets.US_ASCII).replace("CMYK", "not_def").getBytes(StandardCharsets.US_ASCII);
        PdfOutputIntent pdfOutputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(isoFilePath));

        pdfOutputIntent.getPdfObject().put(PdfName.DestOutputProfile, new PdfStream(manipulatedBytes));
        pdfDoc.addOutputIntent(pdfOutputIntent);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfDoc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.OUTPUT_INTENT_COLOR_SPACE_SHALL_BE_EITHER_GRAY_RGB_OR_CMYK, e.getMessage());

    }

    @Test
    public void destOutputIntentColorSpaceNotAllowedInPageTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4DestOutputIntentProfileNotAllowedInPage.pdf";
        String isoFilePath = SOURCE_FOLDER + "ISOcoated_v2_300_bas.icc";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);
        PdfPage page = pdfDoc.addNewPage();

        byte[] bytes = Files.readAllBytes(Paths.get(isoFilePath));
        byte[] manipulatedBytes = new String(bytes, StandardCharsets.US_ASCII).replace("CMYK", "not_def").getBytes(StandardCharsets.US_ASCII);
        PdfOutputIntent pdfOutputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "cmyk",
                FileUtil.getInputStreamForFile(isoFilePath));

        pdfOutputIntent.getPdfObject().put(PdfName.DestOutputProfile, new PdfStream(manipulatedBytes));
        page.addOutputIntent(pdfOutputIntent);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfDoc.close()
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.OUTPUT_INTENT_COLOR_SPACE_SHALL_BE_EITHER_GRAY_RGB_OR_CMYK, e.getMessage());
    }



    @Test
    public void destOutputIntentRefNotAllowedTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "PdfWithOutputIntentProfileRef.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument pdfADocument = new PdfADocument(writer, conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        PdfPage page = pdfADocument.addNewPage();

        PdfDictionary catalog = pdfADocument.getCatalog().getPdfObject();
        PdfArray outputIntents = catalog.getAsArray(PdfName.OutputIntents);
        PdfDictionary outputIntent = outputIntents.getAsDictionary(0);
        outputIntent.put(new PdfName("DestOutputProfileRef"), new PdfDictionary());
        outputIntents.add(outputIntent);
        catalog.put(PdfName.OutputIntents, outputIntents);

        Exception exc = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfADocument.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.OUTPUTINTENT_SHALL_NOT_CONTAIN_DESTOUTPUTPROFILEREF_KEY,
                exc.getMessage());
    }


    private void testWithColourant(PdfName color) throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));
            colourantHalftone.put(PdfName.TransferFunction, PdfName.Identity);

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(color, colourantHalftone);
            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(halftone))
            );
            Assertions.assertEquals(
                    PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5,
                    e.getMessage());
        }
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
