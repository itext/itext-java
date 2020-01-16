/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static com.itextpdf.test.ITextTest.createOrClearDestinationFolder;

@Category(IntegrationTest.class)
public class PdfChoiceFieldTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfChoiceFieldTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfChoiceFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void choiceFieldsWithUnicodeTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "choiceFieldsWithUnicodeTest.pdf";
        String cmpPdf = sourceFolder + "cmp_choiceFieldsWithUnicodeTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");
        font.setSubset(false);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        pdfDoc.addNewPage();

        // 规
        form.addField(PdfFormField.createComboBox(pdfDoc, new Rectangle(36, 666, 40, 80), "combo1", "\u89c4",
                new String[] {"\u89c4", "\u89c9"}, font, null).setBorderColor(ColorConstants.BLACK));
        // 觉
        form.addField(PdfFormField.createComboBox(pdfDoc, new Rectangle(136, 666, 40, 80), "combo2", "\u89c4",
                new String[] {"\u89c4", "\u89c9"}, font, null).setValue("\u89c9").setBorderColor(ColorConstants.BLACK));
        // 规
        form.addField(PdfFormField.createList(pdfDoc, new Rectangle(236, 666, 50, 80), "list1", "\u89c4",
                new String[] {"\u89c4", "\u89c9"}, font, null).setBorderColor(ColorConstants.BLACK));
        // 觉
        form.addField(PdfFormField.createList(pdfDoc, new Rectangle(336, 666, 50, 80), "list2", "\u89c4",
                new String[] {"\u89c4", "\u89c9"}, font, null).setValue("\u89c9").setBorderColor(ColorConstants.BLACK));

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
