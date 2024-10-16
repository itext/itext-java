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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.shading.PdfAxialShading;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfShadingDocumentTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/colorspace/PdfShadingDocumentTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void axialDocumentTest() throws IOException {
        String dest = DESTINATION_FOLDER + "axialDoc.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest))) {
            PdfPage pdfPage = pdfDocument.addNewPage();

            PdfCanvas canvas = new PdfCanvas(pdfPage);

            int x = 36;
            int y = 400;
            int side = 500;

            float[] green = new float[] {0, 255, 0};
            float[] blue = new float[] {0, 0, 255};

            PdfAxialShading axial = new PdfAxialShading(new PdfDeviceCs.Rgb(), x, y, green,
                    x + side, y, blue);
            PdfPattern.Shading shading = new PdfPattern.Shading(axial);

            canvas.setFillColorShading(shading);
            canvas.moveTo(x, y);
            canvas.lineTo(x + side, y);
            canvas.lineTo(x + (side / 2), (float) (y + (side * Math.sin(Math.PI / 3))));
            canvas.closePathFillStroke();

            PdfDictionary pdfObject = pdfDocument.getPage(1).getResources().getPdfObject();

            Assertions.assertTrue(pdfObject.containsKey(PdfName.Pattern));
        }
    }
}
