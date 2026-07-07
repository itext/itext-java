/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.IPdfFunction;
import com.itextpdf.kernel.pdf.function.PdfType2Function;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


@Tag("IntegrationTest")
public class PdfAColorTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfAColorTest/";

    public static Object[] pdfAConformanceLevels() {
        return new Object[] {
                PdfAConformance.PDF_A_2B,
                PdfAConformance.PDF_A_3B,
                PdfAConformance.PDF_A_4
        };
    }


    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @ParameterizedTest
    @MethodSource("pdfAConformanceLevels")
    public void validate2SeparationColorsCreatesValidDocument(PdfAConformance conformance) throws java.io.IOException {
        String outfile = DESTINATION_FOLDER + "2validSeparationColors_" + conformance.getPart() + ".pdf";
        PdfWriter writer = new PdfWriter(outfile);
        PdfDocument pdfADoc = new PdfADocument(writer, conformance, createOutputIntent());
        PdfColorSpace alternateSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceRGB);

        // Separation 1: "Pantone Green"
        PdfDictionary funcDict1 = new PdfDictionary();
        funcDict1.put(PdfName.FunctionType, new PdfNumber(2));
        funcDict1.put(PdfName.Domain, new PdfArray(new float[] {0, 1}));
        funcDict1.put(PdfName.C0, new PdfArray(new float[] {0, 0, 0}));
        funcDict1.put(PdfName.C1, new PdfArray(new float[] {0, 1, 0}));
        funcDict1.put(PdfName.N, new PdfNumber(1));
        IPdfFunction transform1 = new PdfType2Function(funcDict1);
        PdfSpecialCs.Separation green = new PdfSpecialCs.Separation("Pantone Green", alternateSpace, transform1);
        Color colorGreen = new Separation(green, 1);

        // Separation 2: "Pantone Red"
        PdfDictionary funcDict2 = new PdfDictionary();
        funcDict2.put(PdfName.FunctionType, new PdfNumber(2));
        funcDict2.put(PdfName.Domain, new PdfArray(new float[] {0, 1}));
        funcDict2.put(PdfName.C0, new PdfArray(new float[] {0, 0, 0}));
        funcDict2.put(PdfName.C1, new PdfArray(new float[] {1, 0, 0}));
        funcDict2.put(PdfName.N, new PdfNumber(1));
        IPdfFunction transform2 = new PdfType2Function(funcDict2);
        PdfSpecialCs.Separation red = new PdfSpecialCs.Separation("Pantone Red", alternateSpace, transform2);
        Color colorRed = new Separation(red, 1);

        PdfPage page = pdfADoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setFillColor(colorGreen);
        canvas.rectangle(new Rectangle(50, 400, 200, 100));
        canvas.fill();

        canvas.setFillColor(colorRed);
        canvas.rectangle(new Rectangle(300, 400, 200, 100));
        canvas.fill();
        pdfADoc.close();

        Assertions.assertNull(new VeraPdfValidator().validate(outfile));
    }

    @ParameterizedTest
    @MethodSource("pdfAConformanceLevels")
    public void validate2OfTheSameColorThrow(PdfAConformance conformance) throws java.io.IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfADoc = new PdfADocument(writer, conformance, createOutputIntent());
        PdfColorSpace alternateSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceRGB);

        // Separation 1: "Pantone Green"
        PdfDictionary funcDict1 = new PdfDictionary();
        funcDict1.put(PdfName.FunctionType, new PdfNumber(2));
        funcDict1.put(PdfName.Domain, new PdfArray(new float[] {0, 1}));
        funcDict1.put(PdfName.C0, new PdfArray(new float[] {0, 0, 0}));
        funcDict1.put(PdfName.C1, new PdfArray(new float[] {0, 1, 0}));
        funcDict1.put(PdfName.N, new PdfNumber(1));
        IPdfFunction transform1 = new PdfType2Function(funcDict1);
        PdfSpecialCs.Separation green = new PdfSpecialCs.Separation("Green", alternateSpace, transform1);
        Color colorGreen = new Separation(green, 1);

        // Separation 2: "Pantone Red"
        PdfDictionary funcDict2 = new PdfDictionary();
        funcDict2.put(PdfName.FunctionType, new PdfNumber(2));
        funcDict2.put(PdfName.Domain, new PdfArray(new float[] {0, 1}));
        funcDict2.put(PdfName.C0, new PdfArray(new float[] {0, 0, 0}));
        funcDict2.put(PdfName.C1, new PdfArray(new float[] {1, 0, 0}));
        funcDict2.put(PdfName.N, new PdfNumber(1));
        IPdfFunction transform2 = new PdfType2Function(funcDict2);
        PdfSpecialCs.Separation green2 = new PdfSpecialCs.Separation("Green", alternateSpace, transform2);
        Color colorRed = new Separation(green2, 1);

        PdfPage page = pdfADoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setFillColor(colorGreen);
        canvas.rectangle(new Rectangle(50, 400, 200, 100));
        canvas.fill();

        // TODO(DEVSIX-1672) in fact need to check if objects content is equal. ISO 19005-2, 6.2.4.4
        // This test should stop  throwing an exception as the acutal content is the same
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            canvas.setFillColor(colorRed);
        });
        Assertions.assertEquals(
                PdfaExceptionMessageConstant.TINT_TRANSFORM_AND_ALTERNATE_SPACE_SHALL_BE_THE_SAME_FOR_THE_ALL_SEPARATION_CS_WITH_THE_SAME_NAME,
                e.getMessage());
    }


    @ParameterizedTest
    @MethodSource("pdfAConformanceLevels")
    public void validate2OfSameNameDifferentValuesShouldFail(PdfAConformance conformance) throws java.io.IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfADoc = new PdfADocument(writer, conformance, createOutputIntent());
        PdfColorSpace alternateSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceRGB);

        // Separation 1: "Pantone Green"
        PdfDictionary funcDict1 = new PdfDictionary();
        funcDict1.put(PdfName.FunctionType, new PdfNumber(2));
        funcDict1.put(PdfName.Domain, new PdfArray(new float[] {0, 1}));
        funcDict1.put(PdfName.C0, new PdfArray(new float[] {0, 0, 0}));
        funcDict1.put(PdfName.C1, new PdfArray(new float[] {0, 1, 0}));
        funcDict1.put(PdfName.N, new PdfNumber(1));
        IPdfFunction transform1 = new PdfType2Function(funcDict1);
        PdfSpecialCs.Separation green = new PdfSpecialCs.Separation("Green", alternateSpace, transform1);
        Color colorGreen = new Separation(green, 1);

        // Separation 2: "Pantone Red"
        PdfDictionary funcDict2 = new PdfDictionary();
        funcDict2.put(PdfName.FunctionType, new PdfNumber(2));
        funcDict2.put(PdfName.Domain, new PdfArray(new float[] {0, 1}));
        funcDict2.put(PdfName.C0, new PdfArray(new float[] {0, 1, 0}));
        funcDict2.put(PdfName.C1, new PdfArray(new float[] {1, 0, 0}));
        funcDict2.put(PdfName.N, new PdfNumber(1));
        IPdfFunction transform2 = new PdfType2Function(funcDict2);
        PdfSpecialCs.Separation green2 = new PdfSpecialCs.Separation("Green", alternateSpace, transform2);
        Color colorRed = new Separation(green2, 1);

        PdfPage page = pdfADoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setFillColor(colorGreen);
        canvas.rectangle(new Rectangle(50, 400, 200, 100));
        canvas.fill();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            canvas.setFillColor(colorRed);
        });
        Assertions.assertEquals(
                PdfaExceptionMessageConstant.TINT_TRANSFORM_AND_ALTERNATE_SPACE_SHALL_BE_THE_SAME_FOR_THE_ALL_SEPARATION_CS_WITH_THE_SAME_NAME,
                e.getMessage());
    }


    private PdfOutputIntent createOutputIntent() throws java.io.IOException {
        return new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm"));
    }
}
