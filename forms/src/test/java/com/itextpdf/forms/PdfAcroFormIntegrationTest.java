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

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;

@Tag("IntegrationTest")
public class PdfAcroFormIntegrationTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/PdfAcroFormIntegrationTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/PdfAcroFormIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void orphanedNamelessFormFieldTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "orphanedFormField.pdf"))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            Assertions.assertEquals(3, form.getRootFormFields().size());
        }
    }

    @Test
    public void formWithSameFieldReferencesTest() throws IOException, InterruptedException {
        String srcFileName = SOURCE_FOLDER + "formWithSameFieldReferences.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_formWithSameFieldReferences.pdf";
        String outFileName = DESTINATION_FOLDER + "formWithSameFieldReferences.pdf";

        try (PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(sourceDoc, true);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertNull(acroForm.getField("Field").getKids());
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void mergeMergedFieldsWithTheSameNamesTest() throws IOException, InterruptedException {
        String srcFileName = SOURCE_FOLDER + "fieldMergedWithWidget.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_mergeMergedFieldsWithTheSameNames.pdf";
        String outFileName = DESTINATION_FOLDER + "mergeMergedFieldsWithTheSameNames.pdf";

        try (PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(sourceDoc, true);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertNull(acroForm.getField("Field").getKids());

            PdfFormField field = acroForm.copyField("Field");
            field.getPdfObject().put(PdfName.Rect, new PdfArray(new Rectangle(210, 490, 150, 22)));
            acroForm.addField(field);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(2, acroForm.getField("Field").getKids().size());
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.CANNOT_MERGE_FORMFIELDS))
    public void allowAddingFieldsWithTheSameNamesButDifferentValuesTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_fieldsWithTheSameNamesButDifferentValues.pdf";
        String outFileName = DESTINATION_FOLDER + "fieldsWithTheSameNamesButDifferentValues.pdf";
        try (PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFileName))) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root").createText();
            PdfFormField firstField = new TextFormFieldBuilder(outputDoc, "field")
                    .createText().setValue("first");
            PdfFormField secondField = new TextFormFieldBuilder(outputDoc, "field")
                    .createText().setValue("second");

            acroForm.addField(root);

            root.addKid(firstField);
            root.addKid(secondField, false);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(2, root.getKids().size());
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void processFieldsWithTheSameNamesButDifferentValuesInReadingModeTest() throws IOException {
        String srcFileName = SOURCE_FOLDER + "cmp_fieldsWithTheSameNamesButDifferentValues.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(srcFileName))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
            Assertions.assertEquals(1, acroForm.getFields().size());

            PdfFormField root = acroForm.getField("root");
            Assertions.assertEquals(2, root.getKids().size());

            root.getChildField("field").setValue("field");
            PdfFormCreator.getAcroForm(document, true);
            // Check that fields weren't merged
            Assertions.assertEquals(2, root.getKids().size());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.CANNOT_MERGE_FORMFIELDS))
    public void processFieldsWithTheSameNamesInWritingModeTest() throws IOException {
        String srcFileName = SOURCE_FOLDER + "cmp_fieldsWithTheSameNamesButDifferentValues.pdf";
        String outFileName = DESTINATION_FOLDER + "processFieldsWithTheSameNamesInWritingMode.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
            Assertions.assertEquals(1, acroForm.getFields().size());

            PdfFormField root = acroForm.getField("root");
            Assertions.assertEquals(2, root.getKids().size());

            root.getChildField("field").setValue("field");
            PdfFormCreator.getAcroForm(document, true);
            // Check that fields were merged
            Assertions.assertEquals(1, root.getKids().size());
        }
    }

    @Test
    public void disableFieldRegenerationTest() throws IOException, InterruptedException {
        String srcFileName = SOURCE_FOLDER + "borderBoxes.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_disableFieldRegeneration.pdf";
        String cmpFileName2 = SOURCE_FOLDER + "cmp_disableFieldRegenerationUpdated.pdf";
        String outFileName = DESTINATION_FOLDER + "disableFieldRegeneration.pdf";
        String outFileName2 = DESTINATION_FOLDER + "disableFieldRegenerationUpdated.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
            acroForm.disableRegenerationForAllFields();
            for (PdfFormField field : acroForm.getRootFormFields().values()) {
                field.setColor(new DeviceRgb(51, 0, 102));
                field.getFirstFormAnnotation().setBackgroundColor(new DeviceRgb(229, 204, 255))
                        .setBorderColor(new DeviceRgb(51, 0, 102)).setBorderWidth(5);
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
        try (PdfDocument document = new PdfDocument(new PdfReader(cmpFileName), new PdfWriter(outFileName2))) {
            PdfFormCreator.getAcroForm(document, true).enableRegenerationForAllFields();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName2, cmpFileName2, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void enableFieldRegenerationTest() throws IOException, InterruptedException {
        String srcFileName = SOURCE_FOLDER + "cmp_disableFieldRegeneration.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_enableFieldRegeneration.pdf";
        String outFileName = DESTINATION_FOLDER + "enableFieldRegeneration.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
            acroForm.disableRegenerationForAllFields();
            for (PdfFormField field : acroForm.getRootFormFields().values()) {
                field.setColor(ColorConstants.DARK_GRAY);
                field.getFirstFormAnnotation().setBackgroundColor(new DeviceRgb(255, 255, 204))
                        .setBorderColor(new DeviceRgb(204, 229, 255)).setBorderWidth(10);
            }
            acroForm.enableRegenerationForAllFields();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }
}
