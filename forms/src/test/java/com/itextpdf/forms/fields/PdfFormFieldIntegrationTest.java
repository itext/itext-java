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
package com.itextpdf.forms.fields;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfFormFieldIntegrationTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/fields/PdfFormFieldIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/fields/PdfFormFieldIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void addFormXObjectToPushButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "addFormXObjectToPushButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_addFormXObjectToPushButton.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "addFormXObjectToPushButton.pdf"),
                new PdfWriter(outPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getPage(2);
        PdfFormXObject xObject = page.copyAsFormXObject(pdfDoc);
        ((PdfButtonFormField) form.getField("pushButton")).setImageAsForm(xObject);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }
}
