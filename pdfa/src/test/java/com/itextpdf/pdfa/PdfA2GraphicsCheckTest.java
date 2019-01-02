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
package com.itextpdf.pdfa;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceN;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.function.PdfFunction;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collections;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA2GraphicsCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2GraphicsCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2GraphicsCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void colorCheckTest1() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(PdfAConformanceException.COLOR_SPACE_0_SHALL_HAVE_1_COMPONENTS, PdfName.DefaultCMYK.getValue(), 4));

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        float[] whitePoint = {0.9505f, 1f, 1.089f};
        float[] gamma = {2.2f, 2.2f, 2.2f};
        float[] matrix = {0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f};
        PdfCieBasedCs.CalRgb calRgb = new PdfCieBasedCs.CalRgb(whitePoint, null, gamma, matrix);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.getResources().setDefaultCmyk(calRgb);

        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        doc.close();
    }

    @Test
    public void colorCheckTest2() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_colorCheckTest2.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, null);

        float[] whitePoint = {0.9505f, 1f, 1.089f};
        float[] gamma = {2.2f, 2.2f, 2.2f};
        float[] matrix = {0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f};
        PdfCieBasedCs.CalRgb calRgb = new PdfCieBasedCs.CalRgb(whitePoint, null, gamma, matrix);

        PdfCieBasedCs.CalGray calGray = new PdfCieBasedCs.CalGray(whitePoint, null, 2.2f);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.getResources().setDefaultRgb(calRgb);
        canvas.getResources().setDefaultGray(calGray);

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", true);
        canvas.setFontAndSize(font, 12);
        canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest3() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        doc.close();
    }

    @Test
    public void colorCheckTest4() throws IOException, InterruptedException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);

        String outPdf = destinationFolder + "pdfA2b_colorCheckTest4.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest4.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

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

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest5() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", true);
        canvas.setFontAndSize(font, 12);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.CLIP);
        canvas.setFillColor(ColorConstants.RED).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f)).beginText().showText(shortText).endText();

        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        doc.close();
    }

    @Test
    public void colorCheckTest6() throws IOException, InterruptedException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);

        String outPdf = destinationFolder + "pdfA2b_colorCheckTest6.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest6.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", true);
        canvas.setFontAndSize(font, 12);
        canvas.setStrokeColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.setFillColor(ColorConstants.RED);
        canvas.beginText().showText(shortText).endText();

        canvas.setFillColor(DeviceGray.GRAY).beginText().showText(shortText).endText();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void colorCheckTest7() throws IOException, InterruptedException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);

        String outPdf = destinationFolder + "pdfA2b_colorCheckTest7.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest7.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        String shortText = "text";

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", true);
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
    public void egsCheckTest1() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_HTP_KEY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().put(PdfName.HTP, new PdfName("Test")));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void egsCheckTest2() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.HALFTONES_SHALL_NOT_CONTAIN_HALFTONENAME);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.HalftoneType, new PdfNumber(5));
        dict.put(PdfName.HalftoneName, new PdfName("Test"));



        canvas.setExtGState(new PdfExtGState().setHalftone(dict));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void imageCheckTest1() throws FileNotFoundException, MalformedURLException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ONLY_JPX_BASELINE_SET_OF_FEATURES_SHALL_BE_USED);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.addImage(ImageDataFactory.create(sourceFolder + "jpeg2000/p0_01.j2k"), 300, 300, false);

        doc.close();
    }

    @Test
    public void imageCheckTest2() throws FileNotFoundException, MalformedURLException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.EXACTLY_ONE_COLOUR_SPACE_SPECIFICATION_SHALL_HAVE_THE_VALUE_0X01_IN_THE_APPROX_FIELD);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.addImage(ImageDataFactory.create(sourceFolder + "jpeg2000/file5.jp2"), 300, 300, false);

        doc.close();
    }
    @Test
    public void imageCheckTest3() throws FileNotFoundException, MalformedURLException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.EXACTLY_ONE_COLOUR_SPACE_SPECIFICATION_SHALL_HAVE_THE_VALUE_0X01_IN_THE_APPROX_FIELD);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.addImage(ImageDataFactory.create(sourceFolder + "jpeg2000/file7.jp2"), 300, 300, false);


        doc.close();
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
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfCanvas canvas;

        for (int i = 1; i < 5; ++i) {
            canvas = new PdfCanvas(doc.addNewPage());
            canvas.addImage(ImageDataFactory.create(MessageFormatUtil.format(sourceFolder + "jpeg2000/file{0}.jp2", String.valueOf(i))), 300, 300, false);
        }
        canvas = new PdfCanvas(doc.addNewPage());
        canvas.addImage(ImageDataFactory.create(sourceFolder + "jpeg2000/file6.jp2"), 300, 300, false);
        for (int i = 8; i < 10; ++i) {
            canvas = new PdfCanvas(doc.addNewPage());
            canvas.addImage(ImageDataFactory.create(MessageFormatUtil.format(sourceFolder + "jpeg2000/file{0}.jp2", String.valueOf(i))), 300, 300, false);
        }

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void transparencyCheckTest1() {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.IF_THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_TRANSPARENCY_IS_FORBIDDEN);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, null);

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
    }

    @Test
    public void transparencyCheckTest2() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_transparencyCheckTest2.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_transparencyCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

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
    public void transparencyCheckTest3() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        canvas.saveState();
        canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Darken));
        canvas.rectangle(100, 100, 100, 100);
        canvas.fill();
        canvas.restoreState();

        canvas.saveState();
        canvas.setExtGState(new PdfExtGState().setBlendMode(new PdfName("UnknownBlendMode")));
        canvas.rectangle(200, 200, 100, 100);
        canvas.fill();
        canvas.restoreState();

        doc.close();
    }

    @Test
    public void colourSpaceTest01() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a stream
        byte[] samples = {0x00,0x00,0x00,0x01,0x01,0x01};
        PdfArray domain = new PdfArray(new float[]{0,1});
        PdfArray range  =new PdfArray(new float[]{0,1,0,1,0,1});
        PdfArray size = new PdfArray(new float[]{2});
        PdfNumber bitsPerSample = new PdfNumber(8);

        PdfFunction.Type0 type0 = new PdfFunction.Type0(domain,range,size,bitsPerSample,samples);
        PdfColorSpace separationColourSpace = new PdfSpecialCs.Separation("separationTestFunction0",alternateSpace,type0);
        //Add to document
        page.getResources().addColorSpace(separationColourSpace);

        doc.close();
    }

    @Test
    public void colourSpaceTest02() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a dictionary
        PdfArray domain = new PdfArray(new float[]{0,1});
        PdfArray range  =new PdfArray(new float[]{0,1,0,1,0,1});
        PdfArray C0 = new PdfArray(new float[]{0,0,0});
        PdfArray C1 = new PdfArray(new float[]{1,1,1});
        PdfNumber n = new PdfNumber(1);

        PdfFunction.Type2 type2 = new PdfFunction.Type2(domain,range,C0,C1,n);
        PdfColorSpace separationColourSpace = new PdfSpecialCs.Separation("separationTestFunction2",alternateSpace,type2);
        //Add to document
        page.getResources().addColorSpace(separationColourSpace);
        doc.close();
    }

    @Test
    public void colourSpaceTest03() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfColorSpace alternateSpace= new PdfDeviceCs.Rgb();
        //Tint transformation function is a dictionary
        PdfArray domain = new PdfArray(new float[]{0,1});
        PdfArray range  =new PdfArray(new float[]{0,1,0,1,0,1});
        PdfArray C0 = new PdfArray(new float[]{0,0,0});
        PdfArray C1 = new PdfArray(new float[]{1,1,1});
        PdfNumber n = new PdfNumber(1);

        PdfFunction.Type2 type2 = new PdfFunction.Type2(domain,range,C0,C1,n);

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

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
