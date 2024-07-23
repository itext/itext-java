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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.CalGray;
import com.itextpdf.kernel.colors.CalRgb;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceN;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.IccBased;
import com.itextpdf.kernel.colors.Indexed;
import com.itextpdf.kernel.colors.Lab;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfShading;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.PdfType4Function;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfCanvasColorTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasColorTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasColorTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterClass
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void colorTest01() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest01.pdf"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setFillColor(ColorConstants.RED).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(ColorConstants.GREEN).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(ColorConstants.BLUE).rectangle(250, 500, 50, 50).fill();
        canvas.setLineWidth(5);
        canvas.setStrokeColor(DeviceCmyk.CYAN).rectangle(50, 400, 50, 50).stroke();
        canvas.setStrokeColor(DeviceCmyk.MAGENTA).rectangle(150, 400, 50, 50).stroke();
        canvas.setStrokeColor(DeviceCmyk.YELLOW).rectangle(250, 400, 50, 50).stroke();
        canvas.setStrokeColor(DeviceCmyk.BLACK).rectangle(350, 400, 50, 50).stroke();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest01.pdf",
                SOURCE_FOLDER + "cmp_colorTest01.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest02() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest02.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfDeviceCs.Rgb rgb = new PdfDeviceCs.Rgb();
        Color red = Color.makeColor(rgb, new float[]{1, 0, 0});
        Color green = Color.makeColor(rgb, new float[]{0, 1, 0});
        Color blue = Color.makeColor(rgb, new float[]{0, 0, 1});
        PdfDeviceCs.Cmyk cmyk = new PdfDeviceCs.Cmyk();
        Color cyan = Color.makeColor(cmyk, new float[]{1, 0, 0, 0});
        Color magenta = Color.makeColor(cmyk, new float[]{0, 1, 0, 0});
        Color yellow = Color.makeColor(cmyk, new float[]{0, 0, 1, 0});
        Color black = Color.makeColor(cmyk, new float[]{0, 0, 0, 1});

        canvas.setFillColor(red).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(green).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(blue).rectangle(250, 500, 50, 50).fill();
        canvas.setLineWidth(5);
        canvas.setStrokeColor(cyan).rectangle(50, 400, 50, 50).stroke();
        canvas.setStrokeColor(magenta).rectangle(150, 400, 50, 50).stroke();
        canvas.setStrokeColor(yellow).rectangle(250, 400, 50, 50).stroke();
        canvas.setStrokeColor(black).rectangle(350, 400, 50, 50).stroke();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest02.pdf",
                SOURCE_FOLDER + "cmp_colorTest02.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest03() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest03.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        CalGray calGray1 = new CalGray(new float[]{0.9505f, 1.0000f, 1.0890f}, 0.5f);
        canvas.setFillColor(calGray1).rectangle(50, 500, 50, 50).fill();
        CalGray calGray2 = new CalGray(new float[]{0.9505f, 1.0000f, 1.0890f}, null, 2.222f, 0.5f);
        canvas.setFillColor(calGray2).rectangle(150, 500, 50, 50).fill();

        CalRgb calRgb = new CalRgb(
                new float[]{0.9505f, 1.0000f, 1.0890f},
                null,
                new float[]{1.8000f, 1.8000f, 1.8000f},
                new float[]{0.4497f, 0.2446f, 0.0252f, 0.3163f, 0.6720f, 0.1412f, 0.1845f, 0.0833f, 0.9227f},
                new float[]{1f, 0.5f, 0f});
        canvas.setFillColor(calRgb).rectangle(50, 400, 50, 50).fill();

        Lab lab1 = new Lab(new float[]{0.9505f, 1.0000f, 1.0890f}, null, new float[]{-128, 127, -128, 127}, new float[]{1f, 0.5f, 0f});
        canvas.setFillColor(lab1).rectangle(50, 300, 50, 50).fill();
        Lab lab2 = new Lab((PdfCieBasedCs.Lab) lab1.getColorSpace(), new float[]{0f, 0.5f, 0f});
        canvas.setFillColor(lab2).rectangle(150, 300, 50, 50).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest03.pdf",
                SOURCE_FOLDER + "cmp_colorTest03.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest04() throws Exception {
        //Create document with 3 colored rectangles in memory.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        InputStream streamGray = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "BlackWhite.icc");
        InputStream streamRgb = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "CIERGB.icc");
        InputStream streamCmyk = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        IccBased gray = new IccBased(streamGray, new float[]{0.5f});
        IccBased rgb = new IccBased(streamRgb, new float[]{1.0f, 0.5f, 0f});
        IccBased cmyk = new IccBased(streamCmyk, new float[]{1.0f, 0.5f, 0f, 0f});
        canvas.setFillColor(gray).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(rgb).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(cmyk).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        //Copies page from created document to new document.
        //This is not strictly necessary for ICC-based colors paces test, but this is an additional test for copy functionality.
        byte[] bytes = baos.toByteArray();
        com.itextpdf.kernel.pdf.PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(new ByteArrayInputStream(bytes));
        document = new PdfDocument(reader);
        writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest04.pdf");
        PdfDocument newDocument = new PdfDocument(writer);
        newDocument.addPage(document.getPage(1).copyTo(newDocument));
        newDocument.close();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest04.pdf",
                SOURCE_FOLDER + "cmp_colorTest04.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest05() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest05.pdf"));
        PdfPage page = document.addNewPage();
        InputStream streamGray = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "BlackWhite.icc");
        InputStream streamRgb = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "CIERGB.icc");
        InputStream streamCmyk = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "USWebUncoated.icc");
        PdfCieBasedCs.IccBased gray = (PdfCieBasedCs.IccBased) new IccBased(streamGray).getColorSpace();
        PdfCieBasedCs.IccBased rgb = (PdfCieBasedCs.IccBased) new IccBased(streamRgb).getColorSpace();
        PdfCieBasedCs.IccBased cmyk = (PdfCieBasedCs.IccBased) new IccBased(streamCmyk).getColorSpace();
        PdfResources resources = page.getResources();
        resources.setDefaultGray(gray);
        resources.setDefaultRgb(rgb);
        resources.setDefaultCmyk(cmyk);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColorGray(0.5f).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColorRgb(1.0f, 0.5f, 0f).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColorCmyk(1.0f, 0.5f, 0f, 0f).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest05.pdf",
                SOURCE_FOLDER + "cmp_colorTest05.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest06() throws Exception {
        byte[] bytes = new byte[256 * 3];
        int k = 0;
        for (int i = 0; i < 256; i++) {
            bytes[k++] = (byte) i;
            bytes[k++] = (byte) i;
            bytes[k++] = (byte) i;
        }

        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest06.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfSpecialCs.Indexed indexed = new PdfSpecialCs.Indexed(com.itextpdf.kernel.pdf.PdfName.DeviceRGB, 255, new PdfString(new String(bytes,
                StandardCharsets.UTF_8)));
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Indexed(indexed, 85)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Indexed(indexed, 127)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Indexed(indexed, 170)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest06.pdf",
                SOURCE_FOLDER + "cmp_colorTest06.pdf", DESTINATION_FOLDER, "diff_"));
    }
    
    @Test
    public void colorTest07Depr() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest07.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfType4Function function = new PdfType4Function(new float[]{0, 1}, new float[]{0, 1, 0, 1, 0, 1},
                "{0 0}".getBytes(StandardCharsets.ISO_8859_1));
        PdfSpecialCs.Separation separation = new PdfSpecialCs.Separation("MyRed", new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Separation(separation, 0.25f)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.5f)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.75f)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest07.pdf",
                SOURCE_FOLDER + "cmp_colorTest07.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest07() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest07.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        //com.itextpdf.kernel.pdf.function.PdfFunction.Type4 function = new com.itextpdf.kernel.pdf.function.PdfFunction.Type4(new PdfArray(new float[]{0, 1}), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0 0}".getBytes(StandardCharsets.ISO_8859_1));
        PdfType4Function function = new PdfType4Function(new double[]{0, 1}, new double[]{0, 1, 0, 1, 0, 1}, "{0 0}".getBytes(StandardCharsets.ISO_8859_1));
        PdfSpecialCs.Separation separation = new PdfSpecialCs.Separation("MyRed", new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Separation(separation, 0.25f)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.5f)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.75f)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest07.pdf",
                SOURCE_FOLDER + "cmp_colorTest07.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest08Depr() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest08.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfType4Function function = new PdfType4Function(new float[]{0, 1, 0, 1}, new float[]{0, 1, 0, 1, 0, 1},
                "{0}".getBytes(StandardCharsets.ISO_8859_1));

        ArrayList<String> tmpArray = new ArrayList<String>(2);
        tmpArray.add("MyRed");
        tmpArray.add("MyGreen");
        PdfSpecialCs.DeviceN deviceN = new PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new DeviceN(deviceN, new float[]{0, 0})).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new DeviceN(deviceN, new float[]{0, 1})).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new DeviceN(deviceN, new float[]{1, 0})).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest08.pdf",
                SOURCE_FOLDER + "cmp_colorTest08.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void colorTest08() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "colorTest08.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfType4Function function = new PdfType4Function(new double[]{0, 1, 0, 1}, new double[]{0, 1, 0, 1, 0, 1}, "{0}".getBytes(StandardCharsets.ISO_8859_1));

        ArrayList<String> tmpArray = new ArrayList<String>(2);
        tmpArray.add("MyRed");
        tmpArray.add("MyGreen");
        PdfSpecialCs.DeviceN deviceN = new PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new DeviceN(deviceN, new float[]{0, 0})).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new DeviceN(deviceN, new float[]{0, 1})).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new DeviceN(deviceN, new float[]{1, 0})).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "colorTest08.pdf",
                SOURCE_FOLDER + "cmp_colorTest08.pdf", DESTINATION_FOLDER, "diff_"));
    }


    @Test
    public void setColorsSameColorSpaces() throws IOException, InterruptedException {
        setColorSameColorSpacesTest("setColorsSameColorSpaces.pdf", false);
    }

    @Test
    public void setColorsSameColorSpacesPattern() throws IOException, InterruptedException {
        setColorSameColorSpacesTest("setColorsSameColorSpacesPattern.pdf", true);
    }

    @Test
    public void makePatternColorTest() throws IOException, InterruptedException {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "makePatternColorTest.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfSpecialCs.Pattern pattern = new PdfSpecialCs.UncoloredTilingPattern(new PdfDeviceCs.Rgb());
        Color greenPattern = Color.makeColor(pattern, new float[]{0, 1,0});

        PdfPattern.Tiling circle = new PdfPattern.Tiling(10, 10, 12, 12, false);
        new PdfPatternCanvas(circle, document).circle(5f, 5f, 5f).fill().release();
        canvas.setColor(greenPattern.getColorSpace(), greenPattern.getColorValue(), circle, true).rectangle(50, 600, 50, 50).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "makePatternColorTest.pdf",
                SOURCE_FOLDER + "cmp_makePatternColorTest.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void patternColorColoredAxialPatternTest() throws Exception {
        String name = "patternColorColoredAxialPatternTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + name);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        PdfShading axial = new PdfShading.Axial(
                new PdfDeviceCs.Rgb(),
                36, 716, new float[]{1, .784f, 0},
                396, 788, new float[]{0, 0, 1},
                new boolean[] {true, true}
                );

        canvas.setFillColor(new PatternColor(new PdfPattern.Shading(axial)));
        canvas.rectangle(30, 300, 400, 400).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + name,
                SOURCE_FOLDER + "cmp_" + name, DESTINATION_FOLDER));
    }

    @Test
    public void patternColorColoredRadialPatternTest() throws Exception {
        String name = "patternColorColoredRadialPatternTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + name);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        PdfShading radial = new PdfShading.Radial(
                new PdfDeviceCs.Rgb(),
                200, 700, 50, new float[] {1, 0.968f, 0.58f},
                300, 700, 100, new float[] {0.968f, 0.541f, 0.42f}
                );

        canvas.setFillColor(new PatternColor(new PdfPattern.Shading(radial)));
        canvas.rectangle(30, 300, 400, 400).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + name,
                SOURCE_FOLDER + "cmp_" + name, DESTINATION_FOLDER));
    }

    @Test
    public void patternColorUncoloredCircleRgbTest() throws Exception {
        String name = "patternColorUncoloredCircleRgbTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + name);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        PdfPattern.Tiling circle = new PdfPattern.Tiling(15, 15, 10, 20, false);
        new PdfPatternCanvas(circle, document).circle(7.5f, 7.5f, 2.5f).fill().release();

        PdfSpecialCs.UncoloredTilingPattern uncoloredRgbCs
                = new PdfSpecialCs.UncoloredTilingPattern(new PdfDeviceCs.Rgb());

        float[] green = {0, 1, 0};

        canvas.setFillColor(new PatternColor(circle, uncoloredRgbCs, green));
        canvas.rectangle(30, 300, 400, 400).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + name,
                SOURCE_FOLDER + "cmp_" + name, DESTINATION_FOLDER));
    }

    @Test
    public void patternColorUncoloredLineGrayTest() throws Exception {
        String name = "patternColorUncoloredLineGrayTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + name);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        PdfPattern.Tiling line = new PdfPattern.Tiling(5, 10, false);
        new PdfPatternCanvas(line, document).setLineWidth(1).moveTo(3, -1).lineTo(3, 11).stroke().release();

        canvas.setFillColor(new PatternColor(line, new DeviceGray()));
        canvas.rectangle(30, 300, 400, 400).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + name,
                SOURCE_FOLDER + "cmp_" + name, DESTINATION_FOLDER));
    }

    @Test
    public void patternColorColoredSetTwiceTest() throws Exception {
        String name = "patternColorColoredSetTwiceTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + name);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);


        PdfPattern.Tiling square = new PdfPattern.Tiling(15, 15);
        new PdfPatternCanvas(square, document).setFillColor(new DeviceRgb(0xFF, 0xFF, 0x00))
                .setStrokeColor(new DeviceRgb(0xFF, 0x00, 0x00))
                .rectangle(5, 5, 5, 5)
                .fillStroke()
                .release();

        PdfPattern.Tiling ellipse = new PdfPattern.Tiling(15, 10, 20, 25);
        new PdfPatternCanvas(ellipse, document)
                .setFillColor(new DeviceRgb(0xFF, 0xFF, 0x00))
                .setStrokeColor(new DeviceRgb(0xFF, 0x00, 0x00))
                .ellipse(2, 2, 13, 8)
                .fillStroke()
                .release();


        canvas.setFillColor(new PatternColor(square));
        canvas.rectangle(36, 696, 126, 126).fill();
        canvas.setFillColor(new PatternColor(square));
        canvas.rectangle(180, 696, 126, 126).fill();
        canvas.setFillColor(new PatternColor(ellipse));
        canvas.rectangle(360, 696, 126, 126).fill();

        byte[] pageContentStreamBytes = canvas.getContentStream().getBytes();

        canvas.release();
        document.close();

        String contentStreamString = new String(pageContentStreamBytes, StandardCharsets.US_ASCII);
        int p1Count = countSubstringOccurrences(contentStreamString, "/P1 scn");
        int p2Count = countSubstringOccurrences(contentStreamString, "/P2 scn");
        Assert.assertEquals(1, p1Count);
        Assert.assertEquals(1, p2Count);
        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + name,
                SOURCE_FOLDER + "cmp_" + name, DESTINATION_FOLDER));
    }

    @Test
    public void patternColorUncoloredSetTwiceTest() throws Exception {
        String name = "patternColorUncoloredSetTwiceTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + name);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        PdfPattern.Tiling circle = new PdfPattern.Tiling(15, 15, 10, 20, false);
        new PdfPatternCanvas(circle, document).circle(7.5f, 7.5f, 2.5f).fill().release();

        PdfPattern.Tiling line = new PdfPattern.Tiling(5, 10, false);
        new PdfPatternCanvas(line, document).setLineWidth(1).moveTo(3, -1).lineTo(3, 11).stroke().release();

        PatternColor patternColorCircle = new PatternColor(circle, ColorConstants.RED);

        float[] cyan = {1, 0, 0, 0};
        float[] magenta = {0, 1, 0, 0};
        PdfSpecialCs.UncoloredTilingPattern uncoloredTilingCmykCs = new PdfSpecialCs.UncoloredTilingPattern(new PdfDeviceCs.Cmyk());
        PatternColor patternColorLine = new PatternColor(line, uncoloredTilingCmykCs, magenta);

        canvas.setFillColor(patternColorCircle);
        canvas.rectangle(36, 696, 126, 126).fill();
        canvas.setFillColor(patternColorCircle);
        canvas.rectangle(180, 696, 126, 126).fill();

        canvas.setFillColor(patternColorLine);
        canvas.rectangle(36, 576, 126, 126).fill();

        patternColorLine.setColorValue(cyan);
        canvas.setFillColor(patternColorLine);
        canvas.rectangle(180, 576, 126, 126).fill();

        byte[] pageContentStreamBytes = canvas.getContentStream().getBytes();

        canvas.release();
        document.close();

        String contentStreamString = new String(pageContentStreamBytes, StandardCharsets.US_ASCII);
        int p1Count = countSubstringOccurrences(contentStreamString, "/P1 scn");
        int p2Count = countSubstringOccurrences(contentStreamString, "/P2 scn");
        Assert.assertEquals(2, p1Count);
        Assert.assertEquals(2, p2Count);
        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + name,
                SOURCE_FOLDER + "cmp_" + name, DESTINATION_FOLDER));
    }

    @Test
    public void patternColorUncoloredPatternCsUnitTest() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));

        PdfPattern.Tiling circle = new PdfPattern.Tiling(15, 15, 10, 20, false);
        new PdfPatternCanvas(circle, doc).circle(7.5f, 7.5f, 2.5f).fill().release();

        Assert.assertThrows(IllegalArgumentException.class,
                () -> new PatternColor(circle, new PdfSpecialCs.Pattern(), new float[0])
        );
    }

    @Test
    public void patternColorUncoloredPatternColorUnitTest() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));

        PdfPattern.Tiling circle = new PdfPattern.Tiling(15, 15, 10, 20, false);
        new PdfPatternCanvas(circle, doc).circle(7.5f, 7.5f, 2.5f).fill().release();

        PatternColor redCirclePattern = new PatternColor(circle, ColorConstants.RED);

        Assert.assertThrows(IllegalArgumentException.class, () -> new PatternColor(circle, redCirclePattern));
    }

    private void setColorSameColorSpacesTest(String pdfName, boolean pattern) throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_" + pdfName;
        String destFile = DESTINATION_FOLDER + pdfName;

        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destFile));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfColorSpace space = pattern ? new PdfSpecialCs.Pattern() : PdfColorSpace.makeColorSpace(PdfName.DeviceRGB);
        float[] colorValue1 = pattern ? null : new float[]{1.0f, 0.6f, 0.7f};
        float[] colorValue2 = pattern ? null : new float[]{0.1f, 0.9f, 0.9f};

        PdfPattern pattern1 = pattern? new PdfPattern.Shading(new PdfShading.Axial(new PdfDeviceCs.Rgb(), 45, 750, ColorConstants.PINK.getColorValue(),
                100, 760, ColorConstants.MAGENTA.getColorValue())) : null;
        PdfPattern pattern2 = pattern ? new PdfPattern.Shading(new PdfShading.Axial(new PdfDeviceCs.Rgb(), 45, 690, ColorConstants.BLUE.getColorValue(),
                100, 710, ColorConstants.CYAN.getColorValue())) : null;

        canvas.setColor(space, colorValue1, pattern1, true);
        canvas.saveState();
        canvas.beginText()
                .moveText(50, 750)
                .setFontAndSize(PdfFontFactory.createFont(), 16)
                .showText("pinkish")
                .endText();
        canvas.saveState()
                .beginText()
                .setColor(space, colorValue2, pattern2, true)
                .moveText(50, 720)
                .setFontAndSize(PdfFontFactory.createFont(), 16)
                .showText("bluish")
                .endText()
                .restoreState();
        canvas.restoreState();
        canvas.saveState()
                .beginText()
                .moveText(50, 690)
                .setColor(space, colorValue2, pattern2, true)
                .setFontAndSize(PdfFontFactory.createFont(), 16)
                .showText("bluish")
                .endText()
                .restoreState();

        canvas.release();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, DESTINATION_FOLDER, "diff_"));
    }

    private static int countSubstringOccurrences(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){
            lastIndex = str.indexOf(findStr, lastIndex);

            if(lastIndex != -1){
                ++count;
                lastIndex += findStr.length();
            }
        }
        return count;
    }
}
