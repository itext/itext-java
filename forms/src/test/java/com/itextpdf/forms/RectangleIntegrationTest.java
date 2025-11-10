/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static com.itextpdf.test.ITextTest.createOrClearDestinationFolder;


@Tag("IntegrationTest")
public class RectangleIntegrationTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/RectangleTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/RectangleTest/";

    @BeforeAll
    public static void initDestinationFolder() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void createPdfWithSignatureFields() throws IOException, InterruptedException {

        String outPdf = DESTINATION_FOLDER + "RectangleTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_RectangleTest.pdf";

        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + "RectangleTest.pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        for (int i = 0; i <= 3; i++) {
            int rotation = 90 * i;
            PdfPage page = pdfDoc.addNewPage();
            page.setRotation(rotation);

            float x = 20, y = 500, width = 100, height = 50, spacing = 50;

            for (int j = 1; j <= 3; j++) {
                Rectangle rect = new Rectangle(x, y, width, height);
                String fieldName = "page" + i + "_Signature" + j;

                PdfFormField signatureField = new SignatureFormFieldBuilder(pdfDoc, fieldName)
                        .setPage(page)
                        .setWidgetRectangle(rect)
                        .createSignature();

                form.addField(signatureField);
                x += width + spacing;
            }
        }

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }
}
