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

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FormFieldFlatteningTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldFlatteningTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldFlatteningTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void getFieldsForFlatteningTest() throws IOException {
        String outPdfName = destinationFolder + "flattenedFormField.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"),
                new PdfWriter(outPdfName));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);

        Assertions.assertEquals(0, form.getFieldsForFlattening().size());

        form.partialFormFlattening("radioName");
        form.partialFormFlattening("Text1");

        PdfFormField radioNameField = form.getField("radioName");
        PdfFormField text1Field = form.getField("Text1");

        Assertions.assertEquals(2, form.getFieldsForFlattening().size());
        Assertions.assertTrue(form.getFieldsForFlattening().contains(radioNameField));
        Assertions.assertTrue(form.getFieldsForFlattening().contains(text1Field));

        form.flattenFields();
        pdfDoc.close();

        PdfDocument outPdfDoc = new PdfDocument(new PdfReader(outPdfName));
        PdfAcroForm outPdfForm = PdfFormCreator.getAcroForm(outPdfDoc, false);

        Assertions.assertEquals(2, outPdfForm.getAllFormFields().size());

        outPdfDoc.close();
    }

    @Test
    public void formFlatteningTest01() throws IOException, InterruptedException {
        String srcFilename = "formFlatteningSource.pdf";
        String filename = "formFlatteningTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void formFlatteningChoiceFieldTest01() throws IOException, InterruptedException {
        String srcFilename = "formFlatteningSourceChoiceField.pdf";
        String filename = "formFlatteningChoiceFieldTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void multiLineFormFieldClippingTest() throws IOException, InterruptedException {
        String src = sourceFolder + "multiLineFormFieldClippingTest.pdf";
        String dest = destinationFolder + "multiLineFormFieldClippingTest_flattened.pdf";
        String cmp = sourceFolder + "cmp_multiLineFormFieldClippingTest_flattened.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        form.getField("Text1").setValue("Tall letters: T I J L R E F");
        form.flattenFields();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    public void rotatedFieldAppearanceTest01() throws IOException, InterruptedException {
        String srcFilename = "src_rotatedFieldAppearanceTest01.pdf";
        String filename = "rotatedFieldAppearanceTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void rotatedFieldAppearanceTest02() throws IOException, InterruptedException {
        String srcFilename = "src_rotatedFieldAppearanceTest02.pdf";
        String filename = "rotatedFieldAppearanceTest02.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void degeneratedRectTest01() throws IOException, InterruptedException {
        String srcFilename = "src_degeneratedRectTest01.pdf";
        String filename = "degeneratedRectTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void degeneratedRectTest02() throws IOException, InterruptedException {
        String srcFilename = "src_degeneratedRectTest02.pdf";
        String filename = "degeneratedRectTest02.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void scaledRectTest01() throws IOException, InterruptedException {
        String srcFilename = "src_scaledRectTest01.pdf";
        String filename = "scaledRectTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    private static void flattenFieldsAndCompare(String srcFile, String outFile)
            throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + srcFile);
        PdfWriter writer = new PdfWriter(destinationFolder + outFile);
        PdfDocument document = new PdfDocument(reader, writer);
        PdfFormCreator.getAcroForm(document, false).flattenFields();

        document.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(destinationFolder + outFile, sourceFolder + "cmp_" + outFile, destinationFolder,
                        "diff_");

        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void fieldsJustificationTest01() throws IOException, InterruptedException {
        fillTextFieldsThenFlattenThenCompare("fieldsJustificationTest01");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = FormsLogMessageConstants.ANNOTATION_IN_ACROFORM_DICTIONARY, count = 2)
    })
    public void fieldsJustificationTest02() throws IOException, InterruptedException {
        fillTextFieldsThenFlattenThenCompare("fieldsJustificationTest02");
    }

    private static void fillTextFieldsThenFlattenThenCompare(String testName) throws IOException, InterruptedException {
        String src = sourceFolder + "src_" + testName + ".pdf";
        String dest = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        for (PdfFormField field : form.getAllFormFields().values()) {
            if (field instanceof PdfTextFormField) {
                String newValue;
                if (field.isMultiline()) {
                    newValue = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
                    field.setFontSize(0);
                } else {
                    newValue = "HELLO!";
                }

                TextAlignment justification = field.getJustification();
                if (null == justification || justification == TextAlignment.LEFT) {
                    // reddish
                    for(PdfFormAnnotation annot: field.getChildFormAnnotations()) {
                        annot.setBackgroundColor(new DeviceRgb(255, 200, 200));
                    }
                } else if (justification == TextAlignment.CENTER) {
                    // greenish
                    for(PdfFormAnnotation annot: field.getChildFormAnnotations()) {
                        annot.setBackgroundColor(new DeviceRgb(200, 255, 200));
                    }
                } else if (justification == TextAlignment.RIGHT) {
                    // blueish
                    for(PdfFormAnnotation annot: field.getChildFormAnnotations()) {
                        annot.setBackgroundColor(new DeviceRgb(200, 200, 255));
                    }
                }
                field.setValue(newValue);
            }
        }
        form.flattenFields();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 4)})
    //Logging is expected since there are duplicate field names
    public void flattenReadOnly() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfReader reader = new PdfReader(sourceFolder + "readOnlyForm.pdf");
        PdfDocument pdfInnerDoc = new PdfDocument(reader);
        pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        pdfInnerDoc.close();
        reader = new PdfReader(sourceFolder + "readOnlyForm.pdf");
        pdfInnerDoc = new PdfDocument(reader);
        pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        pdfInnerDoc.close();
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        boolean isReadOnly = true;
        for (PdfFormField field : form.getAllFormFields().values()) {
            isReadOnly = (isReadOnly && field.isReadOnly());
        }
        pdfDoc.close();
        Assertions.assertTrue(isReadOnly);
    }

    @Test
    public void fieldsRegeneratePushButtonWithoutCaption() throws IOException, InterruptedException {
        fillTextFieldsThenFlattenThenCompare("pushbutton_without_caption");
    }
}
