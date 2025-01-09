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
package com.itextpdf.forms.widget;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class AppearanceCharacteristicsTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/widget/AppearanceCharacteristicsTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/widget/AppearanceCharacteristicsTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFieldBordersTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "formFieldBorders.pdf";
        String cmpPdf = sourceFolder + "cmp_formFieldBorders.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);

            PdfFormField simpleField = new TextFormFieldBuilder(doc, "simpleField")
                    .setWidgetRectangle(new Rectangle(300, 300, 200, 100)).createText();
            simpleField.regenerateField();

            PdfFormField insetField = new TextFormFieldBuilder(doc, "insetField")
                    .setWidgetRectangle(new Rectangle(50, 600, 200, 100)).createText();
            insetField.getWidgets().get(0).setBorderStyle(PdfName.I);
            insetField.getFirstFormAnnotation().setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField underlineField = new TextFormFieldBuilder(doc, "underlineField")
                    .setWidgetRectangle(new Rectangle(300, 600, 200, 100)).createText();
            underlineField.getWidgets().get(0).setBorderStyle(PdfName.U);
            underlineField.getFirstFormAnnotation().setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField solidField = new TextFormFieldBuilder(doc, "solidField")
                    .setWidgetRectangle(new Rectangle(50, 450, 200, 100)).createText();
            solidField.getWidgets().get(0).setBorderStyle(PdfName.S);
            solidField.getFirstFormAnnotation().setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField dashField = new TextFormFieldBuilder(doc, "dashField")
                    .setWidgetRectangle(new Rectangle(300, 450, 200, 100)).createText();
            dashField.getWidgets().get(0).setBorderStyle(PdfName.D);
            dashField.getFirstFormAnnotation().setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField beveledField = new TextFormFieldBuilder(doc, "beveledField")
                    .setWidgetRectangle(new Rectangle(50, 300, 200, 100)).createText();
            beveledField.getWidgets().get(0).setBorderStyle(PdfName.B);
            beveledField.getFirstFormAnnotation().setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            form.addField(simpleField);
            form.addField(insetField);
            form.addField(underlineField);
            form.addField(solidField);
            form.addField(dashField);
            form.addField(beveledField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void beveledBorderWithBackgroundTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "beveledBorderWithBackground.pdf";
        String cmpPdf = sourceFolder + "cmp_beveledBorderWithBackground.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField formField = new TextFormFieldBuilder(doc, "formField")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 100)).createText();
            formField.getWidgets().get(0).setBorderStyle(PdfName.B);
            formField.getFirstFormAnnotation().setBorderWidth(3f).setBackgroundColor(DeviceRgb.GREEN).setBorderColor(DeviceRgb.RED);
            formField.regenerateField();
            form.addField(formField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void dashedBorderWithBackgroundTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "dashedBorderWithBackground.pdf";
        String cmpPdf = sourceFolder + "cmp_dashedBorderWithBackground.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField formField = new TextFormFieldBuilder(doc, "formField")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 100)).createText();
            formField.getWidgets().get(0).setBorderStyle(PdfName.D);
            formField.getFirstFormAnnotation().setBorderWidth(3f).setBorderColor(DeviceRgb.RED).setBackgroundColor(DeviceRgb.GREEN);
            formField.regenerateField();
            form.addField(formField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void textStartsAfterFieldBorderTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "textStartsAfterFieldBorderTest.pdf";
        String cmpPdf = sourceFolder + "cmp_textStartsAfterFieldBorderTest.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);

            PdfFormField insetFormField = new TextFormFieldBuilder(doc, "insetFormField")
                    .setWidgetRectangle(new Rectangle(90, 600, 200, 100)).createText();
            insetFormField.getWidgets().get(0).setBorderStyle(PdfName.I);
            insetFormField.getFirstFormAnnotation().setBorderWidth(15f).setBorderColor(DeviceRgb.RED);
            insetFormField.setValue("Text after border").regenerateField();

            PdfFormField solidFormField = new TextFormFieldBuilder(doc, "solidFormField")
                    .setWidgetRectangle(new Rectangle(300, 600, 200, 100)).createText();
            solidFormField.getWidgets().get(0).setBorderStyle(PdfName.S);
            solidFormField.getFirstFormAnnotation().setBorderWidth(15f).setBorderColor(DeviceRgb.RED);
            solidFormField.setValue("Text after border").regenerateField();

            PdfFormField underlineFormField = new TextFormFieldBuilder(doc, "underlineFormField")
                    .setWidgetRectangle(new Rectangle(90, 450, 200, 100)).createText();
            underlineFormField.getWidgets().get(0).setBorderStyle(PdfName.U);
            underlineFormField.getFirstFormAnnotation().setBorderWidth(15f).setBorderColor(DeviceRgb.RED);
            underlineFormField.setValue("Text after border").regenerateField();

            PdfFormField simpleFormField = new TextFormFieldBuilder(doc, "formField1")
                    .setWidgetRectangle(new Rectangle(300, 450, 200, 100)).createText();
            simpleFormField.getFirstFormAnnotation().setBorderWidth(15f).setBorderColor(DeviceRgb.RED);
            simpleFormField.setValue("Text after border").regenerateField();

            form.addField(insetFormField);
            form.addField(solidFormField);
            form.addField(underlineFormField);
            form.addField(simpleFormField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void fillFormWithRotatedFieldAndPageTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithRotatedFieldAndPageTest.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithRotatedFieldAndPageTest.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "pdfWithRotatedField.pdf"),
                new PdfWriter(outPdf))) {

            PdfAcroForm form1 = PdfFormCreator.getAcroForm(doc, false);
            form1.getField("First field").setValue("We filled this field").getFirstFormAnnotation()
                    .setBorderColor(ColorConstants.BLACK);
        }

        String errorMessage = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void borderStyleInCreatedFormFieldsTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "borderStyleInCreatedFormFields.pdf";
        String cmpPdf = sourceFolder + "cmp_borderStyleInCreatedFormFields.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);

            PdfFormField formField1 = new TextFormFieldBuilder(doc, "firstField").setWidgetRectangle(new Rectangle(100, 600, 100, 50))
                    .createText().setValue("Hello, iText!");
            formField1.getWidgets().get(0).setBorderStyle(PdfAnnotation.STYLE_BEVELED);
            formField1.getFirstFormAnnotation().setBorderWidth(2).setBorderColor(ColorConstants.BLUE);

            PdfFormField formField2 = new TextFormFieldBuilder(doc, "secondField").setWidgetRectangle(new Rectangle(100, 500, 100, 50))
                    .createText().setValue("Hello, iText!");
            formField2.getWidgets().get(0).setBorderStyle(PdfAnnotation.STYLE_UNDERLINE);
            formField2.getFirstFormAnnotation().setBorderWidth(2).setBorderColor(ColorConstants.BLUE);

            PdfFormField formField3 = new TextFormFieldBuilder(doc, "thirdField").setWidgetRectangle(new Rectangle(100, 400, 100, 50))
                    .createText().setValue("Hello, iText!");
            formField3.getWidgets().get(0).setBorderStyle(PdfAnnotation.STYLE_INSET);
            formField3.getFirstFormAnnotation().setBorderWidth(2).setBorderColor(ColorConstants.BLUE);

            form.addField(formField1);
            form.addField(formField2);
            form.addField(formField3);
            form.flattenFields();
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void updatingBorderStyleInFormFieldsTest() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "borderStyleInCreatedFormFields.pdf";
        String outPdf = destinationFolder + "updatingBorderStyleInFormFields.pdf";
        String cmpPdf = sourceFolder + "cmp_updatingBorderStyleInFormFields.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(inputPdf), new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, false);

            Map<String, PdfFormField> fields = form.getAllFormFields();
            fields.get("firstField").setValue("New Value 1");
            fields.get("secondField").setValue("New Value 2");
            fields.get("thirdField").setValue("New Value 3");

            form.flattenFields();
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }
}
