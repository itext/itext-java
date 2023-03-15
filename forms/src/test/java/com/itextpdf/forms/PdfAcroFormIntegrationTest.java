/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class PdfAcroFormIntegrationTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/PdfAcroFormIntegrationTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/PdfAcroFormIntegrationTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void orphanedNamelessFormFieldTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "orphanedFormField.pdf"))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Assert.assertEquals(3, form.getDirectFormFields().size());
        }
    }

    @Test
    public void mergeMergedFieldsWithTheSameNamesTest() throws IOException, InterruptedException {
        String srcFileName = SOURCE_FOLDER + "fieldMergedWithWidget.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_mergeMergedFieldsWithTheSameNames.pdf";
        String outFileName = DESTINATION_FOLDER + "mergeMergedFieldsWithTheSameNames.pdf";

        try (PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(sourceDoc, true);

            Assert.assertEquals(1, acroForm.getFields().size());
            Assert.assertNull(acroForm.getField("Field").getKids());

            PdfFormField field = acroForm.copyField("Field");
            field.getPdfObject().put(PdfName.Rect, new PdfArray(new Rectangle(210, 490, 150, 22)));
            acroForm.addField(field);

            Assert.assertEquals(1, acroForm.getFields().size());
            Assert.assertEquals(2, acroForm.getField("Field").getKids().size());
        }

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.CANNOT_MERGE_FORMFIELDS))
    public void allowAddingFieldsWithTheSameNamesButDifferentValuesTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_fieldsWithTheSameNamesButDifferentValues.pdf";
        String outFileName = DESTINATION_FOLDER + "fieldsWithTheSameNamesButDifferentValues.pdf";
        try (PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFileName))) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root").createText();
            PdfFormField firstField = new TextFormFieldBuilder(outputDoc, "field")
                    .createText().setValue("first");
            PdfFormField secondField = new TextFormFieldBuilder(outputDoc, "field")
                    .createText().setValue("second");

            acroForm.addField(root);

            root.addKid(firstField);
            root.addKid(secondField, false);

            Assert.assertEquals(1, acroForm.getFields().size());
            Assert.assertEquals(2, root.getKids().size());
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void processFieldsWithTheSameNamesButDifferentValuesInReadingModeTest() throws IOException {
        String srcFileName = SOURCE_FOLDER + "cmp_fieldsWithTheSameNamesButDifferentValues.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(srcFileName))) {
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
            Assert.assertEquals(1, acroForm.getFields().size());

            PdfFormField root = acroForm.getField("root");
            Assert.assertEquals(2, root.getKids().size());

            root.getChildField("field").setValue("field");
            PdfAcroForm.getAcroForm(document, true);
            // Check that fields weren't merged
            Assert.assertEquals(2, root.getKids().size());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.CANNOT_MERGE_FORMFIELDS))
    public void processFieldsWithTheSameNamesInWritingModeTest() throws IOException {
        String srcFileName = SOURCE_FOLDER + "cmp_fieldsWithTheSameNamesButDifferentValues.pdf";
        String outFileName = DESTINATION_FOLDER + "processFieldsWithTheSameNamesInWritingMode.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
            Assert.assertEquals(1, acroForm.getFields().size());

            PdfFormField root = acroForm.getField("root");
            Assert.assertEquals(2, root.getKids().size());

            root.getChildField("field").setValue("field");
            PdfAcroForm.getAcroForm(document, true);
            // Check that fields were merged
            Assert.assertEquals(1, root.getKids().size());
        }
    }
}
