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
package com.itextpdf.kernel.pdf.canvas;

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
import com.itextpdf.kernel.pdf.PdfArray;
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
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfCanvasColorTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasColorTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasColorTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void colorTest01() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "colorTest01.pdf"));
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest01.pdf", sourceFolder + "cmp_colorTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest02() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "colorTest02.pdf");
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest02.pdf", sourceFolder + "cmp_colorTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest03() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "colorTest03.pdf");
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest03.pdf", sourceFolder + "cmp_colorTest03.pdf", destinationFolder, "diff_"));

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
        FileInputStream streamGray = new FileInputStream(sourceFolder + "BlackWhite.icc");
        FileInputStream streamRgb = new FileInputStream(sourceFolder + "CIERGB.icc");
        FileInputStream streamCmyk = new FileInputStream(sourceFolder + "USWebUncoated.icc");
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
        writer = new PdfWriter(destinationFolder + "colorTest04.pdf");
        PdfDocument newDocument = new PdfDocument(writer);
        newDocument.addPage(document.getPage(1).copyTo(newDocument));
        newDocument.close();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest04.pdf", sourceFolder + "cmp_colorTest04.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest05() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "colorTest05.pdf"));
        PdfPage page = document.addNewPage();
        FileInputStream streamGray = new FileInputStream(sourceFolder + "BlackWhite.icc");
        FileInputStream streamRgb = new FileInputStream(sourceFolder + "CIERGB.icc");
        FileInputStream streamCmyk = new FileInputStream(sourceFolder + "USWebUncoated.icc");
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest05.pdf", sourceFolder + "cmp_colorTest05.pdf", destinationFolder, "diff_"));
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

        PdfWriter writer = new PdfWriter(destinationFolder + "colorTest06.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfSpecialCs.Indexed indexed = new PdfSpecialCs.Indexed(com.itextpdf.kernel.pdf.PdfName.DeviceRGB, 255, new PdfString(new String(bytes, "UTF-8")));
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Indexed(indexed, 85)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Indexed(indexed, 127)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Indexed(indexed, 170)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest06.pdf", sourceFolder + "cmp_colorTest06.pdf", destinationFolder, "diff_"));
    }


    @Test
    public void colorTest07() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "colorTest07.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        com.itextpdf.kernel.pdf.function.PdfFunction.Type4 function = new com.itextpdf.kernel.pdf.function.PdfFunction.Type4(new PdfArray(new float[]{0, 1}), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0 0}".getBytes(StandardCharsets.ISO_8859_1));
        PdfSpecialCs.Separation separation = new PdfSpecialCs.Separation("MyRed", new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Separation(separation, 0.25f)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.5f)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.75f)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest07.pdf", sourceFolder + "cmp_colorTest07.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest08() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "colorTest08.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        com.itextpdf.kernel.pdf.function.PdfFunction.Type4 function = new com.itextpdf.kernel.pdf.function.PdfFunction.Type4(new PdfArray(new float[]{0, 1, 0, 1}), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0}".getBytes(StandardCharsets.ISO_8859_1));

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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest08.pdf", sourceFolder + "cmp_colorTest08.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void setColorsSameColorSpaces() throws IOException, InterruptedException {
        setColorSameColorSpacesTest("setColorsSameColorSpaces.pdf", false);
    }

    @Test
    public void setColorsSameColorSpacesPattern() throws IOException, InterruptedException {
        setColorSameColorSpacesTest("setColorsSameColorSpacesPattern.pdf", true);
    }

    private void setColorSameColorSpacesTest(String pdfName, boolean pattern) throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_" + pdfName;
        String destFile = destinationFolder + pdfName;

        PdfDocument document = new PdfDocument(new PdfWriter(destFile));

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

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void patternColorColoredAxialPatternTest() throws Exception {
        String name = "patternColorColoredAxialPatternTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + name);
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name, sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void patternColorColoredRadialPatternTest() throws Exception {
        String name = "patternColorColoredRadialPatternTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + name);
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name, sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void patternColorUncoloredCircleRgbTest() throws Exception {
        String name = "patternColorUncoloredCircleRgbTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + name);
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name, sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void patternColorUncoloredLineGrayTest() throws Exception {
        String name = "patternColorUncoloredLineGrayTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + name);
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name, sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void patternColorColoredSetTwiceTest() throws Exception {
        String name = "patternColorColoredSetTwiceTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + name);
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
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name, sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void patternColorUncoloredSetTwiceTest() throws Exception {
        String name = "patternColorUncoloredSetTwiceTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + name);
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

        // this case will be removed when deprecated method is removed
        patternColorLine.setPattern(circle);
        canvas.setFillColor(patternColorLine);
        canvas.rectangle(360, 696, 126, 126).fill();

        byte[] pageContentStreamBytes = canvas.getContentStream().getBytes();

        canvas.release();
        document.close();

        String contentStreamString = new String(pageContentStreamBytes, StandardCharsets.US_ASCII);
        int p1Count = countSubstringOccurrences(contentStreamString, "/P1 scn");
        int p2Count = countSubstringOccurrences(contentStreamString, "/P2 scn");
        Assert.assertEquals(3, p1Count);
        Assert.assertEquals(2, p2Count);
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name, sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void patternColorUncoloredPatternCsUnitTest() {
        junitExpectedException.expect(IllegalArgumentException.class);

        PdfDocument doc = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));

        PdfPattern.Tiling circle = new PdfPattern.Tiling(15, 15, 10, 20, false);
        new PdfPatternCanvas(circle, doc).circle(7.5f, 7.5f, 2.5f).fill().release();

        new PatternColor(circle, new PdfSpecialCs.Pattern(), new float[0]);
    }

    @Test
    public void patternColorUncoloredPatternColorUnitTest() {
        junitExpectedException.expect(IllegalArgumentException.class);

        PdfDocument doc = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));

        PdfPattern.Tiling circle = new PdfPattern.Tiling(15, 15, 10, 20, false);
        new PdfPatternCanvas(circle, doc).circle(7.5f, 7.5f, 2.5f).fill().release();

        PatternColor redCirclePattern = new PatternColor(circle, ColorConstants.RED);
        new PatternColor(circle, redCirclePattern);
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
