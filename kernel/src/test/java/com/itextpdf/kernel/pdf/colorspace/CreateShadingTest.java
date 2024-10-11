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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Cmyk;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Gray;
import com.itextpdf.kernel.pdf.colorspace.shading.PdfAxialShading;
import com.itextpdf.kernel.pdf.colorspace.shading.AbstractPdfShading;
import com.itextpdf.kernel.pdf.colorspace.shading.PdfRadialShading;
import com.itextpdf.kernel.pdf.colorspace.shading.ShadingType;
import com.itextpdf.kernel.pdf.function.AbstractPdfFunction;
import com.itextpdf.kernel.pdf.function.PdfType2Function;
import com.itextpdf.kernel.pdf.function.PdfType3Function;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.CompareTool.CompareResult;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CreateShadingTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/colorspace/CreateShadingTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/colorspace/CreateShadingTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void createAxialShadingWithStitchingFunctionTest() throws IOException {
        String testName = "createAxialShadingWithStitchingFunctionTest";
        String outName = destinationFolder + testName + ".pdf";
        String cmpName = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outName));
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage());

        int x0 = 40;
        int y0 = 500;
        int x1 = 80;
        int y1 = 400;
        PdfArray shadingVector = new PdfArray(new int[] {x0, y0, x1, y1});

        PdfType3Function stitchingFunction = createStitchingCmykShadingFunction();

        PdfAxialShading axialShading = new PdfAxialShading(new Cmyk(), shadingVector, stitchingFunction);

        pdfCanvas.paintShading(axialShading);
        pdfDocument.close();

        assertShadingDictionaryResult(outName, cmpName, "Sh1");
    }

    @Test
    public void modifyAxialShadingTest() throws IOException {
        String testName = "modifyAxialShadingTest";
        String outName = destinationFolder + testName + ".pdf";
        String cmpName = sourceFolder + "cmp_" + testName + ".pdf";
        String input = sourceFolder + "axialShading.pdf";

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(input), CompareTool.createTestPdfWriter(outName),
                new StampingProperties().useAppendMode());

        PdfResources resources = pdfDocument.getPage(1).getResources();
        for (PdfName resName : resources.getResourceNames()) {
            AbstractPdfShading shading = resources.getShading(resName);
            if (shading != null && shading.getShadingType() == ShadingType.AXIAL) {
                PdfAxialShading axialShading = (PdfAxialShading) shading;

                // "cut" shading and extend colors
                axialShading.setDomain(0.1f, 0.8f);
                axialShading.setExtend(true, true);
            }
        }

        pdfDocument.close();

        assertShadingDictionaryResult(outName, cmpName, "Sh1");
    }

    @Test
    public void createSimpleRadialShadingTest() throws IOException {
        String testName = "createSimpleRadialShadingTest";
        String outName = destinationFolder + testName + ".pdf";
        String cmpName = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outName));
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage());

        int x0 = 100;
        int y0 = 500;
        int r0 = 25;
        int x1 = x0;
        int y1 = y0;
        int r1 = 50;

        PdfRadialShading radialShading = new PdfRadialShading(
                new Gray(),
                x0, y0, r0, new float[] {0.9f},
                x1, y1, r1, new float[] {0.2f},
                new boolean[]{false, false}
                );

        pdfCanvas.paintShading(radialShading);
        pdfDocument.close();

        assertShadingDictionaryResult(outName, cmpName, "Sh1");
    }

    @Test
    public void createRadialShadingWithStitchingFunctionTest() throws IOException {
        String testName = "createRadialShadingWithStitchingFunctionTest";
        String outName = destinationFolder + testName + ".pdf";
        String cmpName = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outName));
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage());

        int x0 = 40;
        int y0 = 500;
        int r0 = 25;
        int x1 = 380;
        int y1 = 400;
        int r1 = 50;
        PdfArray shadingVector = new PdfArray(new int[] {x0, y0, r0, x1, y1, r1});

        PdfType3Function stitchingFunction = createStitchingCmykShadingFunction();

        PdfRadialShading radialShading = new PdfRadialShading(new Cmyk(), shadingVector, stitchingFunction);

        pdfCanvas.paintShading(radialShading);
        pdfDocument.close();

        assertShadingDictionaryResult(outName, cmpName, "Sh1");
    }

    @Test
    public void modifyRadialShadingTest() throws IOException {
        String testName = "modifyRadialAxialShadingTest";
        String outName = destinationFolder + testName + ".pdf";
        String cmpName = sourceFolder + "cmp_" + testName + ".pdf";
        String input = sourceFolder + "radialShading.pdf";

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(input), CompareTool.createTestPdfWriter(outName),
                new StampingProperties().useAppendMode());

        PdfResources resources = pdfDocument.getPage(1).getResources();
        for (PdfName resName : resources.getResourceNames()) {
            AbstractPdfShading shading = resources.getShading(resName);
            if (shading != null && shading.getShadingType() == ShadingType.RADIAL) {
                PdfRadialShading radialShading = (PdfRadialShading) shading;

                // "cut" shading and extend colors
                radialShading.setDomain(0.1f, 0.8f);
                radialShading.setExtend(true, true);
            }
        }

        pdfDocument.close();

        assertShadingDictionaryResult(outName, cmpName, "Sh1");
    }

    private static PdfType3Function createStitchingCmykShadingFunction() {
        float[] domain0to1 = new float[] {0, 1};
        float[] range0to1For4n = new float[] {0, 1, 0, 1, 0, 1, 0, 1};

        float[] cmykColor0 = {0.2f, 0.4f, 0f, 0f};
        float[] cmykColor1 = {0.2f, 1f, 0f, 0f};
        PdfType2Function function0 = new PdfType2Function(domain0to1, null, cmykColor0, cmykColor1, 1);
        PdfType2Function function1 = new PdfType2Function(domain0to1, null, cmykColor1, cmykColor0, 1);

        float[] boundForTwoFunctionsSubdomains = new float[] {0.5f};
        float[] encodeStitchingSubdomainToNthFunctionDomain = new float[] {0, 1, 0, 1};

        return new PdfType3Function(domain0to1, range0to1For4n,
                new ArrayList<AbstractPdfFunction<? extends PdfDictionary>>(Arrays.asList(function0, function1)),
                boundForTwoFunctionsSubdomains, encodeStitchingSubdomainToNthFunctionDomain);
    }

    private static void assertShadingDictionaryResult(String outName, String cmpName, String shadingResourceName) throws IOException {
        printOutCmpPdfNameAndDir(outName, cmpName);

        PdfDocument outPdf = new PdfDocument(CompareTool.createOutputReader(outName));
        PdfDocument cmpPdf = new PdfDocument(CompareTool.createOutputReader(cmpName));

        PdfName resName = new PdfName(shadingResourceName);
        PdfObject outShDictionary = outPdf.getPage(1).getResources().getResourceObject(PdfName.Shading, resName);
        PdfObject cmpShDictionary = cmpPdf.getPage(1).getResources().getResourceObject(PdfName.Shading, resName);

        Assertions.assertTrue(outShDictionary.isDictionary());

        CompareResult compareResult = new CompareTool()
                .compareDictionariesStructure((PdfDictionary) outShDictionary, (PdfDictionary) cmpShDictionary);
        Assertions.assertNull(compareResult);

        outPdf.close();
        cmpPdf.close();
    }
}
