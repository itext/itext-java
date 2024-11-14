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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("IntegrationTest")
public class PdfPushButtonTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/PdfPushButtonTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/PdfPushButtonTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void setFontSizePushButtonWithDisplayTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pushButtonWithDisplay.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_pushButtonWithDisplay.pdf";
        try (PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf))) {
            doc.addNewPage();
            Rectangle rectangle = new Rectangle(150, 400, 400, 100);
            PdfButtonFormField button = new PushButtonFormFieldBuilder(doc, "button")
                    .setWidgetRectangle(rectangle).setPage(1).createPushButton();
            button.setFontSize(50);
            button.setValue("value", "some display text");
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(doc, true);
            acroForm.addField(button);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }
}
