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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfA2CanvasCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2CanvasCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void canvasCheckTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent)) {
            pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> {
                        for (int i = 0; i < 29; i++) {
                            canvas.saveState();
                        }
                    }
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.GRAPHICS_STATE_STACK_DEPTH_IS_GREATER_THAN_28, e.getMessage());
        }
    }

    @Test
    public void canvasCheckTest2() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_canvasCheckTest2.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfA2CanvasCheckTest/cmp_pdfA2b_canvasCheckTest2.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent)) {

            pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

            for (int i = 0; i < 28; i++) {
                canvas.saveState();
            }

            for (int i = 0; i < 28; i++) {
                canvas.restoreState();
            }
        }

        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }

    @Test
    public void canvasCheckTest3() throws IOException {
        PdfWriter writer = new PdfWriter(new java.io.ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent)) {
            pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setRenderingIntent(new PdfName("Test"))
            );
            Assertions.assertEquals(
                    PdfaExceptionMessageConstant.IF_SPECIFIED_RENDERING_SHALL_BE_ONE_OF_THE_FOLLOWING_RELATIVECOLORIMETRIC_ABSOLUTECOLORIMETRIC_PERCEPTUAL_OR_SATURATION,
                    e.getMessage());
        }
    }
}
