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

import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfChoiceFieldTest extends ExtendedITextTest{

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

    @Test
    public void choiceFieldsSetValueTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "choiceFieldsWithUnnecessaryIEntries.pdf";
        String outPdf = destinationFolder + "choiceFieldsSetValueTest.pdf";
        String cmpPdf = sourceFolder + "cmp_choiceFieldsSetValueTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, false);
        form.getField("First").setValue("First");
        form.getField("Second").setValue("Second");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff01_"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY, count = 2)})
    public void multiSelectByValueTest() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "multiSelectByValueTest.pdf"));
        document.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
        PdfFormField choice = PdfFormField.createList(document, new Rectangle(336, 666, 50, 80), "choice", "two",
                new String[] {"one", "two", "three", "four"}, null, null).setBorderColor(ColorConstants.BLACK);
        ((PdfChoiceFormField) choice).setMultiSelect(true);

        ((PdfChoiceFormField) choice).setListSelected(new String[] {"one", "three", "eins", "drei", null});

        form.addField(choice);
        document.close();
        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "multiSelectByValueTest.pdf", sourceFolder + "cmp_multiSelectByValueTest.pdf",
                        destinationFolder, "diff01_"));
    }

    @Test
    public void multiSelectByIndexOutOfBoundsTest() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "multiSelectTest.pdf"), new PdfWriter(destinationFolder + "multiSelectByIndexOutOfBoundsTest.pdf"));
        PdfAcroForm form = PdfAcroForm.getAcroForm(document, false);
        PdfChoiceFormField field = (PdfChoiceFormField) form.getField("choice");
        field.setListSelected(new int[] {5});
        document.close();
        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "multiSelectByIndexOutOfBoundsTest.pdf", sourceFolder + "cmp_multiSelectByIndexOutOfBoundsTest.pdf",
                        destinationFolder, "diff01_"));
    }
}
