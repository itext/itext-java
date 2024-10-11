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

import com.itextpdf.forms.fields.AbstractPdfFormField;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.NonTerminalFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfFormFieldTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    // The first message for the case when the FormField is null,
    // the second message when the FormField is an indirect reference to null.
    @LogMessages(messages = {@LogMessage(messageTemplate = FormsLogMessageConstants.CANNOT_CREATE_FORMFIELD, count = 2)})
    public void nullFormFieldTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "nullFormField.pdf"));
        PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.close();
    }

    @Test
    public void formFieldTest01() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);

        Map<String, PdfFormField> fields = form.getAllFormFields();
        PdfFormField field = fields.get("Text1");

        Assertions.assertEquals(4, fields.size());
        Assertions.assertEquals("Text1", field.getFieldName().toUnicodeString());
        Assertions.assertEquals("TestField", field.getValue().toString());
    }

    @Test
    public void formFieldTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        Rectangle rect = new Rectangle(210, 490, 150, 22);
        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "fieldName")
                .setWidgetRectangle(rect).createText();
        field.setValue("some value");
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void formFieldTest03() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest03.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"), new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "TestField")
                .setWidgetRectangle(rect).createText();
        field.setValue("some value");

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void formFieldTest04() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest04.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"), new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "TestField").setWidgetRectangle(rect).createText();
        field.setValue("some value in courier font").setFont(PdfFontFactory.createFont(StandardFonts.COURIER)).setFontSize(10);

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }
    
    @Test
    public void formFieldWithFloatBorderTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldWithFloatBorder.pdf";
        String cmpFilename = sourceFolder + "cmp_formFieldWithFloatBorder.pdf";

        // In this test it's important to open the document in the acrobat and make sure that border width
        // does not change after clicking on the field. Acrobat doesn't support float border width therefore we round it
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(filename))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, true);

            PdfTextFormField textFormField = new TextFormFieldBuilder(pdfDocument, "text field")
                    .setWidgetRectangle(new Rectangle(100, 600, 100, 100)).createText();
            textFormField.setValue("text field value");
            textFormField.getFirstFormAnnotation().setBorderWidth(5.25f);
            textFormField.getFirstFormAnnotation().setBorderColor(ColorConstants.RED);

            form.addField(textFormField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void textFieldLeadingSpacesAreNotTrimmedTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "textFieldLeadingSpacesAreNotTrimmed.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.addNewPage();

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 300, 22);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "TestField")
                .setWidgetRectangle(rect).createText();
        field.setValue("        value with leading space");

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename,
                sourceFolder + "cmp_textFieldLeadingSpacesAreNotTrimmed.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void unicodeFormFieldTest() throws IOException {
        String filename = sourceFolder + "unicodeFormFieldFile.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> formFields = form.getAllFormFields();
        // 帐号1: account number 1
        String fieldName = "\u5E10\u53F71";
        Assertions.assertEquals(fieldName, formFields.keySet().toArray(new String[1])[0]);
    }

    @Test
    public void unicodeFormFieldTest2() throws IOException {
        String filename = sourceFolder + "unicodeFormFieldFile.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        // 帐号1: account number 1
        String fieldName = "\u5E10\u53F71";
        Assertions.assertNotNull(form.getField(fieldName));
    }

    @Test
    public void textFieldValueInStreamTest() throws IOException {
        String filename = sourceFolder + "textFieldValueInStream.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        String fieldValue = form.getField("fieldName").getValueAsString();
        // Trailing newline is not trimmed which seems to match Acrobat's behavior on copy-paste
        Assertions.assertEquals("some value\n", fieldValue);
    }

    @Test
    public void choiceFieldTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "choiceFieldTest01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(210, 490, 150, 20);

        String[] options = new String[]{"First Item", "Second Item", "Third Item", "Fourth Item"};
        PdfChoiceFormField choice = new ChoiceFormFieldBuilder(pdfDoc, "TestField")
                .setWidgetRectangle(rect).setOptions(options).createComboBox();
        choice.setValue("First Item", true);

        form.addField(choice);

        Rectangle rect1 = new Rectangle(210, 250, 150, 90);

        PdfChoiceFormField choice1 = new ChoiceFormFieldBuilder(pdfDoc, "TestField1")
                .setWidgetRectangle(rect1).setOptions(options).createList();
        choice1.setValue("Second Item", true);
        choice1.setMultiSelect(true);
        form.addField(choice1);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_choiceFieldTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void buttonFieldTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "buttonFieldTest01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(36, 700, 20, 20);
        Rectangle rect1 = new Rectangle(36, 680, 20, 20);


        String formFieldName = "TestGroup";
        RadioFormFieldBuilder builder =    new RadioFormFieldBuilder(pdfDoc, formFieldName);
        PdfButtonFormField group = builder.createRadioGroup();
        group.setValue("1", true);

        PdfFormAnnotation radio1 = builder.createRadioButton("1", rect);
        PdfFormAnnotation radio2 = builder.createRadioButton("2", rect1);

        group.addKid(radio1);
        group.addKid(radio2);

        form.addField(group);

        PdfButtonFormField pushButton = new PushButtonFormFieldBuilder(pdfDoc, "push")
                .setWidgetRectangle(new Rectangle(36, 650, 40, 20)).setCaption("Capcha").createPushButton();
        PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(pdfDoc, "TestCheck")
                .setWidgetRectangle(new Rectangle(36, 560, 20, 20)).createCheckBox();
        checkBox.setValue("1", true);

        form.addField(pushButton);
        form.addField(checkBox);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_buttonFieldTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }

    }

    @Test
    public void defaultRadiobuttonFieldTest() throws IOException, InterruptedException {
        String file = "defaultRadiobuttonFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Rectangle rect1 = new Rectangle(36, 700, 20, 20);
        Rectangle rect2 = new Rectangle(36, 680, 20, 20);

        String formFieldName = "TestGroup";
        RadioFormFieldBuilder builder =new RadioFormFieldBuilder(pdfDoc, formFieldName);
        PdfButtonFormField group = builder.createRadioGroup();
        group.setValue("1", true);

        group.addKid(builder.createRadioButton("1",rect1));
        group.addKid(builder.createRadioButton("2", rect2));

        form.addField(group);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedRadiobuttonFieldTest() throws IOException, InterruptedException {
        String file = "customizedRadiobuttonFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Rectangle rect1 = new Rectangle(36, 700, 20, 20);
        Rectangle rect2 = new Rectangle(36, 680, 20, 20);

        String formFieldName2 = "TestGroup2";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, formFieldName2);
        PdfButtonFormField group2 =builder.createRadioGroup();
        group2.setValue("1", true);

        PdfFormAnnotation radio1 =builder
                .createRadioButton("1", rect1)
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);
        group2.addKid(radio1);

        PdfFormAnnotation radio2 = new RadioFormFieldBuilder(pdfDoc, formFieldName2)
                .createRadioButton("2",rect2)
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);
        group2.addKid(radio2);

        form.addField(group2);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedRadiobuttonWithGroupRegeneratingFieldTest() throws IOException, InterruptedException {
        String file = "customizedRadiobuttonWithGroupRegeneratingFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Rectangle rect1 = new Rectangle(36, 700, 20, 20);
        Rectangle rect2 = new Rectangle(36, 680, 20, 20);

        String formFieldName2 = "TestGroup2";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, formFieldName2);
        PdfButtonFormField group2 = builder.createRadioGroup();
        group2.setValue("1", true);

        PdfFormAnnotation radio1 = builder
                .createRadioButton("1", rect1)
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);

        PdfFormAnnotation radio2 = builder
                .createRadioButton("2", rect2)
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);

        group2.addKid(radio1);
        group2.addKid(radio2);

        group2.regenerateField();
        form.addField(group2);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedPushButtonFieldTest() throws IOException, InterruptedException {
        String file = "customizedPushButtonFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        String itext = "itextpdf";

        PdfButtonFormField button = new PushButtonFormFieldBuilder(pdfDoc, itext)
                .setWidgetRectangle(new Rectangle(36, 500, 200, 200)).setCaption(itext)
                .createPushButton();
        button.setFontSize(0);
        button.setValue(itext);

        button.getFirstFormAnnotation()
                .setBorderWidth(10).setBorderColor(ColorConstants.GREEN).setBackgroundColor(ColorConstants.GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);

        form.addField(button);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedPushButtonField2Test() throws IOException, InterruptedException {
        String file = "customizedPushButtonField2Test.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        String itext = "itextpdf";

        PdfButtonFormField button = new PushButtonFormFieldBuilder(pdfDoc, itext)
                .setWidgetRectangle(new Rectangle(36, 500, 300, 110)).setCaption(itext)
                .createPushButton();
        button.setFontSize(0);
        button.setValue(itext);

        button.getFirstFormAnnotation()
                .setBorderWidth(10).setBorderColor(ColorConstants.GREEN).setBackgroundColor(ColorConstants.GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);

        form.addField(button);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedPushButtonField3Test() throws IOException, InterruptedException {
        String file = "customizedPushButtonField3Test.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        String text = "toolongtext";

        PdfButtonFormField button = new PushButtonFormFieldBuilder(pdfDoc, text)
                .setWidgetRectangle(new Rectangle(36, 500, 160, 300)).setCaption(text)
                .createPushButton();
        button.setFontSize(40);
        button.setValue(text);

        button.getFirstFormAnnotation()
                .setBorderWidth(10).setBorderColor(ColorConstants.GREEN).setBackgroundColor(ColorConstants.GRAY)
                .setVisibility(PdfFormAnnotation.VISIBLE);

        form.addField(button);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void buttonFieldTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "buttonFieldTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "buttonFieldTest02_input.pdf"), new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        ((PdfButtonFormField) form.getField("push")).setImage(sourceFolder + "Desert.jpg");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_buttonFieldTest02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void realFontSizeRegenerateAppearanceTest() throws IOException, InterruptedException {
        String sourceFilename = sourceFolder + "defaultAppearanceRealFontSize.pdf";
        String destFilename = destinationFolder + "realFontSizeRegenerateAppearance.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFilename), new PdfWriter(destFilename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        form.getField("fieldName").regenerateField();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destFilename, sourceFolder + "cmp_realFontSizeRegenerateAppearance.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void addFieldWithKidsTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfFormField root = new NonTerminalFormFieldBuilder(pdfDoc, "root").createNonTerminalFormField();

        PdfFormField child = new NonTerminalFormFieldBuilder(pdfDoc, "child").createNonTerminalFormField();
        root.addKid(child);

        PdfTextFormField text1 = new TextFormFieldBuilder(pdfDoc, "text1")
                .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
        text1.setValue("test");
        child.addKid(text1);

        form.addField(root);

        Assertions.assertEquals(3, form.getAllFormFields().size());
    }

    @Test
    public void fillFormWithDefaultResources() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithDefaultResources.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithDefaultResources.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "formWithDefaultResources.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);


        Map<String, PdfFormField> fields = form.getAllFormFields();
        PdfFormField field = fields.get("Text1");

        field.setValue("New value size must be 8");
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void fillFormTwiceWithoutResources() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithoutResources.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithoutResources.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "formWithoutResources.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);


        Map<String, PdfFormField> fields = form.getAllFormFields();
        PdfFormField field = fields.get("Text1");

        field.setValue("New value size must be 8").setFontSize(8);
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void autoScaleFontSizeInFormFields() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "autoScaleFontSizeInFormFields.pdf";
        String cmpPdf = sourceFolder + "cmp_autoScaleFontSizeInFormFields.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfFormField field = new TextFormFieldBuilder(pdfDoc, "name").setWidgetRectangle(new Rectangle(36, 786, 80, 20))
                .createText().setValue("TestValueAndALittleMore");
        field.setFontSizeAutoScale();
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = FormsLogMessageConstants.NO_FIELDS_IN_ACROFORM)})
    public void acroFieldDictionaryNoFields() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "acroFieldDictionaryNoFields.pdf";
        String cmpPdf = sourceFolder + "cmp_acroFieldDictionaryNoFields.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "acroFieldDictionaryNoFields.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void regenerateAppearance() throws IOException, InterruptedException {
        String input = "regenerateAppearance.pdf";
        String output = "regenerateAppearance.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + input),
                new PdfWriter(destinationFolder + output),
                new StampingProperties().useAppendMode());
        PdfAcroForm acro = PdfFormCreator.getAcroForm(document, false);
        int i = 1;
        for (Map.Entry<String, PdfFormField> entry : acro.getAllFormFields().entrySet()) {
            if (entry.getKey().contains("field")) {
                PdfFormField field = entry.getValue();
                field.setValue("test" + i++, false);
            }
        }
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + output,
                sourceFolder + "cmp_" + output, destinationFolder, "diff"));
    }

    @Test
    public void regenerateAppearance2() throws IOException, InterruptedException {
        String input = "regenerateAppearance2.pdf";
        String output = "regenerateAppearance2.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + input),
                new PdfWriter(destinationFolder + output),
                new StampingProperties().useAppendMode());
        PdfAcroForm acro = PdfFormCreator.getAcroForm(document, false);
        acro.setNeedAppearances(true);
        PdfFormField field = acro.getField("number");
        field.setValue("20150044DR");
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + output,
                sourceFolder + "cmp_" + output, destinationFolder, "diff"));
    }

    @Test
    public void flushedPagesTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "flushedPagesTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        pdfDoc.addNewPage().flush();
        pdfDoc.addNewPage().flush();
        pdfDoc.addNewPage();

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "name")
                .setWidgetRectangle(new Rectangle(100, 100, 300, 20)).createText();
        field.setValue("");
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_flushedPagesTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void fillFormWithDefaultResourcesUpdateFont() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithDefaultResourcesUpdateFont.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithDefaultResourcesUpdateFont.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "formWithDefaultResources.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);


        Map<String, PdfFormField> fields = form.getAllFormFields();
        PdfFormField field = fields.get("Text1");

        field.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
        field.setValue("New value size must be 8, but with different font.");

        new Canvas(new PdfCanvas(pdfDoc.getFirstPage()), new Rectangle(30, 500, 500, 200))
                .add(new Paragraph("The text font after modification it via PDF viewer (e.g. Acrobat) shall be preserved."));

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void formRegenerateWithInvalidDefaultAppearance01() throws IOException, InterruptedException {
        String testName = "formRegenerateWithInvalidDefaultAppearance01";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";
        String srcPdf = sourceFolder + "invalidDA.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(srcPdf);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> fields = form.getAllFormFields();
        fields.get("Text1").setValue("New field value");
        fields.get("Text2").setValue("New field value");
        fields.get("Text3").setValue("New field value");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    //Create a document with formfields and paragraphs in both fonts, and fill them before closing the document
    public void fillFieldWithHebrewCase1() throws IOException, InterruptedException {
        String testName = "fillFieldWithHebrewCase1";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont hebrew = PdfFontFactory.createFont(sourceFolder + "OpenSansHebrew-Regular.ttf",
                PdfEncodings.IDENTITY_H);
        hebrew.setSubset(false);
        PdfFont sileot = PdfFontFactory.createFont(sourceFolder + "SILEOT.ttf", PdfEncodings.IDENTITY_H);
        sileot.setSubset(false);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        String text = "שלום וברכה";
        createAcroForm(pdfDoc, form, hebrew, text, 0);
        createAcroForm(pdfDoc, form, sileot, text, 3);

        addParagraph(document, text, hebrew);
        addParagraph(document, text, sileot);

        pdfDoc.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff" + testName + "_"));
    }

    @Test
    //Create a document with formfields and paragraphs in both fonts, and fill them after closing and reopening the document
    public void fillFieldWithHebrewCase2() throws IOException, InterruptedException {
        String testName = "fillFieldWithHebrewCase2";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont hebrew = PdfFontFactory.createFont(sourceFolder + "OpenSansHebrew-Regular.ttf",
                PdfEncodings.IDENTITY_H);
        hebrew.setSubset(false);
        PdfFont sileot = PdfFontFactory.createFont(sourceFolder + "SILEOT.ttf", PdfEncodings.IDENTITY_H);
        sileot.setSubset(false);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        createAcroForm(pdfDoc, form, hebrew, null, 0);
        createAcroForm(pdfDoc, form, sileot, null, 3);

        String text = "שלום וברכה";
        addParagraph(document, text, hebrew);
        addParagraph(document, text, sileot);

        pdfDoc.close();

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())),
                new PdfWriter(outPdf));
        fillAcroForm(pdfDocument, text);
        pdfDocument.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff" + testName + "_"));
    }

    @Test
    //Create a document with formfields in both fonts, and fill them before closing the document
    public void fillFieldWithHebrewCase3() throws IOException, InterruptedException {
        String testName = "fillFieldWithHebrewCase3";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont hebrew = PdfFontFactory.createFont(sourceFolder + "OpenSansHebrew-Regular.ttf",
                PdfEncodings.IDENTITY_H);
        hebrew.setSubset(false);
        PdfFont sileot = PdfFontFactory.createFont(sourceFolder + "SILEOT.ttf", PdfEncodings.IDENTITY_H);
        sileot.setSubset(false);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        String text = "שלום וברכה";
        createAcroForm(pdfDoc, form, hebrew, text, 0);
        createAcroForm(pdfDoc, form, sileot, text, 3);

        pdfDoc.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff" + testName + "_"));
    }

    @Test
    //Create a document with formfields in both fonts, and fill them after closing and reopening the document
    public void fillFieldWithHebrewCase4() throws IOException, InterruptedException {
        String testName = "fillFieldWithHebrewCase4";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont hebrew = PdfFontFactory.createFont(sourceFolder + "OpenSansHebrew-Regular.ttf",
                PdfEncodings.IDENTITY_H);
        hebrew.setSubset(false);
        PdfFont sileot = PdfFontFactory.createFont(sourceFolder + "SILEOT.ttf", PdfEncodings.IDENTITY_H);
        sileot.setSubset(false);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        createAcroForm(pdfDoc, form, hebrew, null, 0);
        createAcroForm(pdfDoc, form, sileot, null, 3);

        pdfDoc.close();

        String text = "שלום וברכה";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())),
                new PdfWriter(outPdf));
        fillAcroForm(pdfDocument, text);
        pdfDocument.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff" + testName + "_"));
    }

    @Test
    public void fillFormWithSameEmptyObjsForAppearance() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithSameEmptyObjsForAppearance.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithSameEmptyObjsForAppearance.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "fillFormWithSameEmptyObjsForAppearance.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");
        acroForm.getField("text_2").setValue("Text 2!");
        acroForm.getField("text.3").setValue("Text 3!");
        acroForm.getField("text.4").setValue("Text 4!");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void dashedBorderAppearanceTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "dashedBorderAppearanceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_dashedBorderAppearanceTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField[] fields = new PdfTextFormField[3];
        String[] names = new String[]{"fieldNoPattern", "fieldEmptyPattern", "fieldSingleEntryPattern"};
        float y = 830;
        PdfDictionary borderDict = new PdfDictionary();
        borderDict.put(PdfName.S, PdfName.D);
        PdfArray patternArray = new PdfArray();
        for (int i = 0; i < 3; i++) {
            if (i == 2)
                patternArray.add(new PdfNumber(10));
            if (i > 0)
                borderDict.put(PdfName.D, patternArray);
            fields[i] = new TextFormFieldBuilder(pdfDoc, names[i])
                    .setWidgetRectangle(new Rectangle(10, y -= 70, 200, 50)).createText();
            fields[i].setValue(names[i]);
            acroForm.addField(fields[i]);
            fields[i].getFirstFormAnnotation().setBorderStyle(borderDict);
            fields[i].getFirstFormAnnotation().setBorderWidth(3);
            fields[i].getFirstFormAnnotation().setBorderColor(ColorConstants.CYAN);
            fields[i].getFirstFormAnnotation().setBackgroundColor(ColorConstants.MAGENTA);
        }

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.COMB_FLAG_MAY_BE_SET_ONLY_IF_MAXLEN_IS_PRESENT)})
    public void noMaxLenWithSetCombFlagTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "noMaxLenWithSetCombFlagTest.pdf";
        String cmpPdf = sourceFolder + "cmp_noMaxLenWithSetCombFlagTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfTextFormField textField = new TextFormFieldBuilder(pdfDoc, "text")
                .setWidgetRectangle(new Rectangle(100, 500, 200, 200)).createText();
        textField.setComb(true);

        // The line below should throw an exception, because the Comb flag may be set only if the MaxLen entry is present in the text field dictionary
        textField.setValue("12345678");

        textField.setMaxLen(1);

        form.addField(textField);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void maxLenWithSetCombFlagAppearanceTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "maxLenFields.pdf";
        String outPdf = destinationFolder + "maxLenWithSetCombFlagAppearanceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_maxLenWithSetCombFlagAppearanceTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);

        form.getField("text1").setValue("123");
        form.getField("text2").setJustification(TextAlignment.CENTER).setValue("123");
        form.getField("text3").setJustification(TextAlignment.RIGHT).setValue("123");
        form.getField("text4").setValue("12345678");
        form.getField("text5").setValue("123456789101112131415161718");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void preserveFontPropsTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "preserveFontPropsTest.pdf";
        String outPdf = destinationFolder + "preserveFontPropsTest.pdf";
        String cmpPdf = sourceFolder + "cmp_preserveFontPropsTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        PdfFormField field1 = form.getField("emptyField");
        field1.setValue("Do fields on the left look the same?", field1.getFont(), field1.getFontSize());
        PdfFormField field2 = form.getField("emptyField2");
        field2.setValue("Do fields on the right look the same?", field2.getFont(), field2.getFontSize());
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void fontAutoSizeButtonFieldTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fontAutoSizeButtonFieldTest.pdf";
        String cmpPdf = sourceFolder + "cmp_fontAutoSizeButtonFieldTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        String itext = "itextpdf";

        PdfButtonFormField button = new PushButtonFormFieldBuilder(pdfDoc, itext)
                .setWidgetRectangle(new Rectangle(36, 500, 200, 200)).setCaption(itext).createPushButton();
        button.setFontSize(0);
        button.getFirstFormAnnotation().setBackgroundColor(ColorConstants.GRAY);
        button.setValue(itext);
        button.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.VISIBLE_BUT_DOES_NOT_PRINT);
        form.addField(button);

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void maxLenInheritanceTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "maxLenInheritanceTest.pdf";
        String outPdf = destinationFolder + "maxLenInheritanceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_maxLenInheritanceTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        form.getField("text").setValue("iText!");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void maxLenDeepInheritanceTest() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "maxLenDeepInheritanceTest.pdf";
        String destFilename = destinationFolder + "maxLenDeepInheritanceTest.pdf";
        String cmpFilename = sourceFolder + "cmp_maxLenDeepInheritanceTest.pdf";

        PdfDocument destDoc = new PdfDocument(new PdfReader(srcFilename), new PdfWriter(destFilename));

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);
        acroForm.getField("text.1.").setColor(ColorConstants.RED);

        destDoc.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(destFilename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void maxLenColoredTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "maxLenColoredTest.pdf";
        String outPdf = destinationFolder + "maxLenColoredTest.pdf";
        String cmpPdf = sourceFolder + "cmp_maxLenColoredTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        form.getField("magenta").setColor(ColorConstants.MAGENTA);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.COMB_FLAG_MAY_BE_SET_ONLY_IF_MAXLEN_IS_PRESENT, count = 2)})
    public void regenerateMaxLenCombTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "regenerateMaxLenCombTest.pdf";
        String outPdf = destinationFolder + "regenerateMaxLenCombTest.pdf";
        String cmpPdf = sourceFolder + "cmp_regenerateMaxLenCombTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        for (int i = 0; i < 12; i++) {
            PdfTextFormField field = (PdfTextFormField) form.getField("field " + i);
            if (i < 8)
                field.setMaxLen(i < 4 ? 7 : 0);
            if (i % 6 > 1)
                field.setFieldFlag(PdfTextFormField.FF_COMB, i % 2 == 0);

        }
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void wrapPrecedingContentOnFlattenTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "wrapPrecedingContentOnFlattenTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFillColor(ColorConstants.MAGENTA);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField[] fields = new PdfTextFormField[4];
        for (int i = 0; i < 4; i++) {
            fields[i] = new TextFormFieldBuilder(pdfDoc, "black" + i)
                    .setWidgetRectangle(new Rectangle(90, 700 - i * 100, 150, 22)).createText();
            fields[i].setValue("black");
        }
        form.addField(fields[0]);
        form.addField(fields[1]);
        Document doc = new Document(pdfDoc);
        doc.add(new AreaBreak());
        canvas = new PdfCanvas(pdfDoc.getPage(2));
        canvas.setFillColor(ColorConstants.CYAN);
        form.addField(fields[2]);
        form.addField(fields[3], pdfDoc.getFirstPage());
        form.flattenFields();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_wrapPrecedingContentOnFlattenTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.MULTIPLE_VALUES_ON_A_NON_MULTISELECT_FIELD)})
    public void pdfWithDifferentFieldsTest() throws IOException, InterruptedException {
        String fileName = destinationFolder + "pdfWithDifferentFieldsTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileName));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.addNewPage();
        PdfFormField emptyField = new NonTerminalFormFieldBuilder(pdfDoc, "empty").createNonTerminalFormField();
        form.addField(emptyField);
        PdfArray options = new PdfArray();
        options.add(new PdfString("1"));
        options.add(new PdfString("2"));
        form.addField(new ChoiceFormFieldBuilder(pdfDoc, "choice")
                .setWidgetRectangle(new Rectangle(36, 696, 20, 20)).setOptions(options).createList().setValue("1", true));
        // combo
        form.addField(new ChoiceFormFieldBuilder(pdfDoc, "list")
                .setWidgetRectangle(new Rectangle(36, 666, 20, 20)).setOptions(new String[]{"1", "2", "3"})
                .createComboBox().setValue("1", true));
        // list
        PdfChoiceFormField f = new ChoiceFormFieldBuilder(pdfDoc, "combo")
                .setWidgetRectangle(new Rectangle(36, 556, 50, 100)).setOptions(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}).createList();
        f.disableFieldRegeneration();
        f.setValue("9", true);
        f.setValue("4");
        f.setTopIndex(2);
        f.setListSelected(new String[]{"3", "5"});
        f.setMultiSelect(true);
        f.enableFieldRegeneration();
        form.addField(f);
        // push button
        form.addField(new PushButtonFormFieldBuilder(pdfDoc, "push button")
                .setWidgetRectangle(new Rectangle(36, 526, 80, 20)).setCaption("push").createPushButton());

        // radio button
        String formFieldName = "radio group";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, formFieldName);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        radioGroup.setValue("1", true);

        PdfFormAnnotation radio1 = builder
                .createRadioButton("1", new Rectangle(36, 496, 20, 20));
        radioGroup.addKid(radio1);

        PdfFormAnnotation radio2 =builder
                .createRadioButton( "2", new Rectangle(66, 496, 20, 20));
        radioGroup.addKid(radio2);
        form.addField(radioGroup);
        // signature
        PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").createSignature().setValue("Signature");
        signField.setFontSize(20);
        form.addField(signField);
        // text
        form.addField(new TextFormFieldBuilder(pdfDoc, "text").setWidgetRectangle(new Rectangle(36, 466, 80, 20))
                .createText().setValue("text").setValue("la la land"));

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(fileName, sourceFolder + "cmp_pdfWithDifferentFieldsTest.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void textFieldWithWideUnicodeRange() throws IOException, InterruptedException {
        String filename = "textFieldWithWideUnicodeRange.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        pdfDoc.addNewPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        form.addField(new TextFormFieldBuilder(pdfDoc, "text_helvetica").setWidgetRectangle(
                new Rectangle(36, 400, 100, 40))
                .createText().setValue("Helvetica"));

        PdfFont noto = PdfFontFactory.createFont(sourceFolder + "NotoSans-Regular.ttf", PdfEncodings.IDENTITY_H);
        noto.setSubset(false);
        String value = "aAáÁàÀăĂắẮằẰẵẴẳẲâÂấẤầẦẫẪǎǍåÅǻǺäÄǟǞãÃą" +
                "ĄāĀảẢạẠặẶẬæÆǽǼbBḃḂcCćĆčČċĊçÇdDd̂D̂ďĎḋḊḑḐđĐðÐeE" +
                "éÉèÈĕĔêÊếẾềỀễỄěĚëËẽẼėĖęĘēĒẻẺẹẸệỆəƏfFḟḞgGǵǴğĞ" +
                "ǧǦġĠģĢḡḠǥǤhHȟȞḧḦħĦḥḤiIíÍìÌĭĬîÎǐǏïÏĩĨİįĮīĪỉỈị" +
                "ỊıjJĵĴǰJ̌kKḱḰǩǨķĶlLĺĹl̂L̂ľĽļĻłŁŀĿmMm̂M̂ṁṀnNńŃn̂N̂ňŇ" +
                "ñÑṅṄņŅŋŊoOóÓòÒŏŎôÔốỐồỒỗỖǒǑöÖȫȪőŐõÕȯȮȱȰøØǿǾǫǪ" +
                "ǭǬōŌỏỎơƠớỚờỜọỌộỘœŒpPṗṖqQĸrRŕŔřŘŗŖsSśŚšŠṡṠşŞṣ" +
                "ṢșȘßẞtTťŤṫṪţŢțȚŧŦuUúÚùÙûÛǔǓůŮüÜűŰũŨųŲūŪủỦưƯứ" +
                "ỨừỪữỮửỬựỰụỤvVwWẃẂẁẀŵŴẅẄxXẍẌyYýÝỳỲŷŶÿŸỹỸẏẎȳȲỷỶ" +
                "ỵỴzZźŹẑẐžŽżŻẓẒʒƷǯǮþÞŉ";
        PdfFormField textField = new TextFormFieldBuilder(pdfDoc, "text").setWidgetRectangle(
                new Rectangle(36, 500, 400, 300))
                .createMultilineText().setValue(value);
        textField.setFont(noto).setFontSize(12);

        form.addField(textField);

        pdfDoc.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename,
                        destinationFolder, "diff_"));
    }

    @Test
    public void testMakeField() {
        Assertions.assertNull(PdfFormField.makeFormField(new PdfNumber(1), null));
        Assertions.assertNull(PdfFormField.makeFormField(new PdfArray(), null));
    }

    @Test
    public void testDaInAppendMode() throws IOException {
        String testName = "testDaInAppendMode.pdf";

        String srcPdf = sourceFolder + testName;
        ByteArrayOutputStream outPdf = new ByteArrayOutputStream();

        int objectNumber;
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf),
                new StampingProperties().useAppendMode())) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
            PdfFormField field = form.getField("magenta");
            field.setFontSize(35);
            field.updateDefaultAppearance();
            objectNumber = field.getPdfObject().getIndirectReference().getObjNumber();
        }

        PdfString da;
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(outPdf.toByteArray())))) {
            da = ((PdfDictionary) pdfDoc.getPdfObject(objectNumber)).getAsString(PdfName.DA);
        }

        Assertions.assertEquals("/F1 35 Tf 1 0 1 rg", da.toString());
    }

    @Test
    public void setPageNewField() throws IOException {
        String filename = destinationFolder + "setPageNewField.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.addNewPage();
        pdfDoc.addNewPage();
        pdfDoc.addNewPage();

        String fieldName = "field1";
        int pageNum = 2;
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField field1 = new TextFormFieldBuilder(pdfDoc, fieldName)
                .setWidgetRectangle(new Rectangle(90, 700, 150, 22)).createText();
        field1.setValue("new field");
        field1.getFirstFormAnnotation().setPage(pageNum);
        form.addField(field1);

        pdfDoc.close();

        // -------------------------------------------
        printOutputPdfNameAndDir(filename);
        PdfDocument resPdf = new PdfDocument(new PdfReader(filename));
        PdfArray fieldsArr = resPdf.getCatalog().getPdfObject()
                .getAsDictionary(PdfName.AcroForm).getAsArray(PdfName.Fields);
        Assertions.assertEquals(1, fieldsArr.size());

        PdfDictionary field = fieldsArr.getAsDictionary(0);
        PdfDictionary fieldP = field.getAsDictionary(PdfName.P);
        Assertions.assertEquals(resPdf.getPage(2).getPdfObject(), fieldP);

        Assertions.assertNull(resPdf.getPage(1).getPdfObject().getAsArray(PdfName.Annots));

        PdfArray secondPageAnnots = resPdf.getPage(2).getPdfObject().getAsArray(PdfName.Annots);
        Assertions.assertEquals(1, secondPageAnnots.size());
        Assertions.assertEquals(field, secondPageAnnots.get(0));

        Assertions.assertNull(resPdf.getPage(3).getPdfObject().getAsArray(PdfName.Annots));
    }

    private void createAcroForm(PdfDocument pdfDoc, PdfAcroForm form, PdfFont font, String text, int offSet) {
        for (int x = offSet; x < (offSet + 3); x++) {
            Rectangle rect = new Rectangle(100 + (30 * x), 100 + (100 * x), 55, 30);
            PdfFormField field = new TextFormFieldBuilder(pdfDoc, "f-" + x).setWidgetRectangle(rect)
                    .createText();
            field.setValue("").setJustification(TextAlignment.RIGHT).setFont(font).setFontSize(12.0f);
            if (text != null) {
                field.setValue(text);
            }
            form.addField(field);
        }
    }

    private void addParagraph(Document document, String text, PdfFont font) {
        document.add(new Paragraph("Hello world ").add(text).setFont(font));
    }

    private void fillAcroForm(PdfDocument pdfDocument, String text) {
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, false);
        for (PdfFormField field : acroForm.getAllFormFields().values()) {
            field.setValue(text);
        }
    }

    @Test
    public void setFont2Ways() throws IOException, InterruptedException {
        String filename = destinationFolder + "setFont3Ways.pdf";
        String cmpFilename = sourceFolder + "cmp_setFont3Ways.pdf";
        String testString = "Don't cry over spilt milk";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, true);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "SILEOT.ttf", PdfEncodings.IDENTITY_H);

        Rectangle rect1 = new Rectangle(10, 700, 200, 25);
        Rectangle rect2 = new Rectangle(30, 600, 200, 25);

        PdfButtonFormField pushButton1 = new PushButtonFormFieldBuilder(pdfDocument, "Name1")
                .setWidgetRectangle(rect1).setCaption(testString).createPushButton();
        pushButton1.setFont(font).setFontSize(12);
        form.addField(pushButton1);

        PdfButtonFormField pushButton2 = new PushButtonFormFieldBuilder(pdfDocument, "Name2")
                .setWidgetRectangle(rect2).setCaption(testString).createPushButton();
        pushButton2.setFontAndSize(font, 12f);
        form.addField(pushButton2);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    // Acrobat removes /NeedAppearances flag when document is opened and suggests to resave the document at once.
    @LogMessages(messages = {@LogMessage(messageTemplate = FormsLogMessageConstants.INPUT_FIELD_DOES_NOT_FIT)})
    public void appendModeAppearance() throws IOException, InterruptedException {
        String inputFile = "appendModeAppearance.pdf";
        String outputFile = "appendModeAppearance.pdf";

        String line1 = "ABC";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + inputFile),
                new PdfWriter(destinationFolder + outputFile),
                new StampingProperties().useAppendMode());
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, false);
        form.setNeedAppearances(true);

        PdfFormField field;
        for (Map.Entry<String, PdfFormField> entry : form.getAllFormFields().entrySet()) {
            field = entry.getValue();
            field.setValue(line1);
        }

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + outputFile,
                sourceFolder + "cmp_" + outputFile, destinationFolder, "diff_"));
    }

    @Test
    public void fillUnmergedTextFormField() throws IOException, InterruptedException {
        String file = sourceFolder + "fillUnmergedTextFormField.pdf";
        String outfile = destinationFolder + "fillUnmergedTextFormField.pdf";
        String text = "John";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(file), new PdfWriter(outfile));
        fillAcroForm(pdfDocument, text);
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "fillUnmergedTextFormField.pdf",
                sourceFolder + "cmp_" + "fillUnmergedTextFormField.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void choiceFieldAutoSize01Test() throws IOException, InterruptedException {
        String filename = destinationFolder + "choiceFieldAutoSize01Test.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        String[] options = new String[]{"First Item", "Second Item", "Third Item", "Fourth Item"};

        PdfFormField[] fields = new PdfFormField[]{
                new ChoiceFormFieldBuilder(pdfDoc, "TestField")
                        .setWidgetRectangle(new Rectangle(110, 750, 150, 20)).setOptions(options)
                        .createComboBox().setValue("First Item"),
                new ChoiceFormFieldBuilder(pdfDoc, "TestField1")
                        .setWidgetRectangle(new Rectangle(310, 650, 150, 90)).setOptions(options)
                        .createList().setValue("Second Item")};

        for (PdfFormField field : fields) {
            field.setFontSize(0);
            field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
            form.addField(field);
        }

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_choiceFieldAutoSize01Test.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void choiceFieldAutoSize02Test() throws IOException, InterruptedException {
        String filename = destinationFolder + "choiceFieldAutoSize02Test.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfArray options = new PdfArray();
        options.add(new PdfString("First Item", PdfEncodings.UNICODE_BIG));
        options.add(new PdfString("Second Item", PdfEncodings.UNICODE_BIG));
        options.add(new PdfString("Third Item", PdfEncodings.UNICODE_BIG));

        form.addField(new ChoiceFormFieldBuilder(pdfDoc, "TestField").setWidgetRectangle(new Rectangle(110, 750, 150, 20))
                .setOptions(options).createComboBox().setValue("First Item", true));
        form.addField(new ChoiceFormFieldBuilder(pdfDoc, "TestField1").setWidgetRectangle(new Rectangle(310, 650, 150, 90))
                .setOptions(options).createList().setValue("Second Item", true));

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_choiceFieldAutoSize02Test.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void borderWidthIndentSingleLineTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "borderWidthIndentSingleLineTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "single")
                .setWidgetRectangle(new Rectangle(50, 700, 500, 120)).createText();
        field.setValue("Does this text overlap the border?");
        field.setFontSize(20);
        field.getFirstFormAnnotation().setBorderColor(ColorConstants.RED);
        field.getFirstFormAnnotation().setBorderWidth(50);
        form.addField(field);

        PdfTextFormField field2 = new TextFormFieldBuilder(pdfDoc, "singleAuto")
                .setWidgetRectangle(new Rectangle(50, 600, 500, 80)).createText();
        field2.setValue("Does this autosize text overlap the border? Well it shouldn't! Does it fit accurately though?");
        field2.setFontSize(0);
        field2.getFirstFormAnnotation().setBorderColor(ColorConstants.RED);
        field2.getFirstFormAnnotation().setBorderWidth(20);
        form.addField(field2);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_borderWidthIndentSingleLineTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET, count = 3))
    public void releaseAcroformTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "formFieldFile.pdf";
        String outPureStamping = destinationFolder + "formFieldFileStamping.pdf";
        String outStampingRelease = destinationFolder + "formFieldFileStampingRelease.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outPureStamping));
        PdfFormCreator.getAcroForm(doc, false);
        // We open/close document to make sure that the results of release logic and simple overwriting coincide.
        doc.close();

        try (PdfDocument stamperRelease = new PdfDocument(new PdfReader(srcFile),
                new PdfWriter(outStampingRelease))) {

            PdfAcroForm form = PdfFormCreator.getAcroForm(stamperRelease, false);
            form.release();
        }

        Assertions.assertNull(new CompareTool().compareByContent(outStampingRelease, outPureStamping, destinationFolder));
    }

    @Test
    public void addChildToFormFieldTest() throws InterruptedException, IOException {
        String outPdf = destinationFolder + "addChildToFormFieldTest.pdf";
        String cmpPdf = sourceFolder + "cmp_addChildToFormFieldTest.pdf";
        try (PdfDocument outputDoc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            acroForm.addField(field);
            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText().setValue("root");
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText().setValue("child");
            root.addKid(child);

            acroForm.addField(root);
            Assertions.assertEquals(2, acroForm.fields.size());
            PdfArray fieldKids = root.getKids();
            Assertions.assertEquals(2, fieldKids.size());
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD))
    public void duplicateFormTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "duplicateFormTest.pdf";
        String inPdf = sourceFolder + "duplicateFormTestSource.pdf";
        String cmpPdf = sourceFolder + "cmp_duplicateFormTest.pdf";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inPdf), new PdfWriter(byteArrayOutputStream));
        PdfDocument pdfInnerDoc = new PdfDocument(new PdfReader(inPdf));
        pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDocument, new PdfPageFormCopier());
        pdfInnerDoc.close();
        pdfDocument.close();

        pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                new PdfWriter(outPdf));
        PdfAcroForm pdfAcroForm = PdfFormCreator.getAcroForm(pdfDocument, false);
        pdfAcroForm.getField("checkbox").setValue("Off");
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void getValueTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "getValueTest.pdf";
        String cmpPdf = sourceFolder + "cmp_getValueTest.pdf";
        String srcPdf = sourceFolder + "getValueTest.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, false);
            for (AbstractPdfFormField field : acroForm.getAllFormFieldsAndAnnotations()) {
                if (field instanceof PdfFormField && "child".equals(field.getPdfObject().get(PdfName.V).toString())) {
                    // Child has value "root" still because it doesn't contain T entry
                    Assertions.assertEquals("root", ((PdfFormField) field).getValue().toString());
                }
                field.regenerateField();
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY))
    public void setValueWithDisplayTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "setValueWithDisplayTest.pdf";
        String cmpPdf = sourceFolder + "cmp_setValueWithDisplayTest.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField textField = new TextFormFieldBuilder(doc, "text")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            textField.setValue("some text", "*****");
            textField.setColor(ColorConstants.BLUE);
            acroForm.addField(textField);

            PdfFormField textField2 = new TextFormFieldBuilder(doc, "text2")
                    .setWidgetRectangle(new Rectangle(100, 650, 100, 20)).createText();
            textField2.setValue("some text", "*****");
            textField2.setColor(ColorConstants.BLUE);
            textField2.setValue("new value");
            acroForm.addField(textField2);

            PdfFormField textField3 = new TextFormFieldBuilder(doc, "text3")
                    .setWidgetRectangle(new Rectangle(250, 650, 100, 20)).createText();
            textField3.setValue("some text", null);
            acroForm.addField(textField3);

            PdfFormField textField4 = new TextFormFieldBuilder(doc, "text4")
                    .setWidgetRectangle(new Rectangle(400, 650, 100, 20)).createText();
            textField4.setValue("some other text", "");
            textField4.getFirstFormAnnotation().setBorderColor(ColorConstants.LIGHT_GRAY);
            acroForm.addField(textField4);

            PdfButtonFormField pushButtonField = new PushButtonFormFieldBuilder(doc, "button")
                    .setWidgetRectangle(new Rectangle(36, 600, 200, 20)).setCaption("Click").createPushButton();
            pushButtonField.setValue("Some button text", "*****");
            pushButtonField.setColor(ColorConstants.BLUE);
            acroForm.addField(pushButtonField);

            String[] options = new String[]{"First Item", "Second Item", "Third Item", "Fourth Item"};
            PdfChoiceFormField choiceField = new ChoiceFormFieldBuilder(doc, "choice")
                    .setWidgetRectangle(new Rectangle(36, 550, 200, 20)).setOptions(options).createComboBox();
            choiceField.setValue("First Item", "display value");
            choiceField.setColor(ColorConstants.BLUE);
            acroForm.addField(choiceField);

            RadioFormFieldBuilder builder = new RadioFormFieldBuilder(doc, "group");
            PdfButtonFormField radioGroupField = builder.createRadioGroup();
            PdfFormAnnotation radio = builder.createRadioButton("1", new Rectangle(36, 500, 20, 20));
            radioGroupField.addKid(radio);
            radioGroupField.setValue("1", "display value");
            acroForm.addField(radioGroupField);

            PdfButtonFormField checkBoxField = new CheckBoxFormFieldBuilder(doc, "check")
                    .setWidgetRectangle(new Rectangle(36, 450, 20, 20)).createCheckBox();
            checkBoxField.setValue("1", "display value");
            acroForm.addField(checkBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.FIELD_VALUE_CANNOT_BE_NULL, count = 2))
    public void setNullValueTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "setNullValueTest.pdf";
        String cmpPdf = sourceFolder + "cmp_setNullValueTest.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField textField = new TextFormFieldBuilder(doc, "text")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            textField.setValue(null);
            textField.setValue(null, "*****");
            acroForm.addField(textField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void getSigFlagsTest() {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
            form.setSignatureFlag(1);
            Assertions.assertEquals(1, form.getSignatureFlags());
        }
    }

    @Test
    public void disableRegenerationForTheRootFieldTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "disableRegenerationForTheRootField.pdf";
        String cmpPdf = sourceFolder + "cmp_regenerationEnabled.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);
            CustomButtonFormField root = new CustomButtonFormField(document.getPdfDocument(), "root");
            CustomButtonFormField parent = new CustomButtonFormField(document.getPdfDocument(), "parent");
            CustomButtonFormField child = new CustomButtonFormField(new PdfWidgetAnnotation(
                    new Rectangle(200, 550, 150, 100)), document.getPdfDocument(), "child");
            parent.addKid(child);
            root.addKid(parent);

            // Disable all fields regeneration
            root.disableFieldRegeneration();
            child.getFirstFormAnnotation().setCaption("regenerated button")
                    .setBorderWidth(3).setBorderColor(ColorConstants.DARK_GRAY).setBackgroundColor(ColorConstants.PINK)
                    .setVisibility(PdfFormAnnotation.VISIBLE);

            Assertions.assertEquals(0, root.getCounter());
            Assertions.assertEquals(0, parent.getCounter());
            Assertions.assertEquals(0, child.getCounter());

            root.enableFieldRegeneration();

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(1, parent.getCounter());
            Assertions.assertEquals(1, child.getCounter());

            // Disable only root field regeneration
            root.disableCurrentFieldRegeneration();
            root.regenerateField();

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(2, parent.getCounter());
            Assertions.assertEquals(2, child.getCounter());

            root.enableCurrentFieldRegeneration();

            Assertions.assertEquals(2, root.getCounter());
            Assertions.assertEquals(3, parent.getCounter());
            Assertions.assertEquals(3, child.getCounter());

            form.addField(root);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void disableRegenerationForTheMiddleFieldTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "disableRegenerationForTheMiddleField.pdf";
        String cmpPdf = sourceFolder + "cmp_regenerationEnabled.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);
            CustomButtonFormField root = new CustomButtonFormField(document.getPdfDocument(), "root");
            CustomButtonFormField parent = new CustomButtonFormField(document.getPdfDocument(), "parent");
            CustomButtonFormField child = new CustomButtonFormField(new PdfWidgetAnnotation(
                    new Rectangle(200, 550, 150, 100)), document.getPdfDocument(), "child");
            parent.addKid(child);
            root.addKid(parent);

            // Disable parent field level regeneration
            parent.disableFieldRegeneration();
            child.getFirstFormAnnotation().setCaption("regenerated button")
                    .setBorderWidth(3).setBorderColor(ColorConstants.DARK_GRAY).setBackgroundColor(ColorConstants.PINK)
                    .setVisibility(PdfFormAnnotation.VISIBLE);
            root.regenerateField();

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(0, parent.getCounter());
            Assertions.assertEquals(0, child.getCounter());

            parent.enableFieldRegeneration();

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(1, parent.getCounter());
            Assertions.assertEquals(1, child.getCounter());

            // Disable only parent field regeneration
            parent.disableCurrentFieldRegeneration();
            root.regenerateField();

            Assertions.assertEquals(2, root.getCounter());
            Assertions.assertEquals(1, parent.getCounter());
            Assertions.assertEquals(2, child.getCounter());

            parent.enableCurrentFieldRegeneration();

            Assertions.assertEquals(2, root.getCounter());
            Assertions.assertEquals(2, parent.getCounter());
            Assertions.assertEquals(3, child.getCounter());

            form.addField(root);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void disableChildRegenerationTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "disableChildRegeneration.pdf";
        String cmpPdf = sourceFolder + "cmp_regenerationEnabled.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);
            CustomButtonFormField root = new CustomButtonFormField(document.getPdfDocument(), "root");
            CustomButtonFormField parent = new CustomButtonFormField(document.getPdfDocument(), "parent");
            CustomButtonFormField child = new CustomButtonFormField(new PdfWidgetAnnotation(
                    new Rectangle(200, 550, 150, 100)), document.getPdfDocument(), "child");
            parent.addKid(child);
            root.addKid(parent);

            // Disable child field regeneration
            child.disableFieldRegeneration();
            child.getFirstFormAnnotation().setBorderWidth(10).setBorderColor(ColorConstants.PINK)
                    .setBackgroundColor(ColorConstants.BLUE);
            root.regenerateField();

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(1, parent.getCounter());
            Assertions.assertEquals(0, child.getCounter());

            child.enableFieldRegeneration();

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(1, parent.getCounter());
            Assertions.assertEquals(1, child.getCounter());

            // Disable only child field regeneration (so widget should be regenerated)
            child.disableCurrentFieldRegeneration();

            child.getFirstFormAnnotation().setCaption("regenerated button")
                    .setBorderWidth(3).setBorderColor(ColorConstants.DARK_GRAY).setBackgroundColor(ColorConstants.PINK)
                    .setVisibility(PdfFormAnnotation.VISIBLE);

            Assertions.assertEquals(1, root.getCounter());
            Assertions.assertEquals(1, parent.getCounter());
            Assertions.assertEquals(1, child.getCounter());

            form.addField(root);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void signatureLayersTest() throws IOException, InterruptedException {
        String fileName = destinationFolder + "signatureLayersTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileName));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.addNewPage();

        PdfSignatureFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature")
                .setWidgetRectangle(new Rectangle(36, 436, 100, 100)).createSignature();

        PdfFormXObject layer0 = new PdfFormXObject(new Rectangle(0, 0, 100, 100));

        // Draw pink rectangle with blue border
        new PdfCanvas(layer0, pdfDoc)
                .saveState()
                .setFillColor(ColorConstants.PINK)
                .setStrokeColor(ColorConstants.BLUE)
                .rectangle(0, 0, 100, 100)
                .fillStroke()
                .restoreState();

        PdfFormXObject layer2 = new PdfFormXObject(new Rectangle(0, 0, 100, 100));

        // Draw yellow circle with gray border
        new PdfCanvas(layer2, pdfDoc)
                .saveState()
                .setFillColor(ColorConstants.YELLOW)
                .setStrokeColor(ColorConstants.DARK_GRAY)
                .circle(50, 50, 50)
                .fillStroke()
                .restoreState();

        signField.setBackgroundLayer(layer0).setSignatureAppearanceLayer(layer2);
        form.addField(signField);
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(fileName, sourceFolder + "cmp_signatureLayersTest.pdf",
                destinationFolder, "diff_"));
    }

    @Test
    public void pdfWithSignatureFieldTest() throws IOException, InterruptedException {
        String fileName = destinationFolder + "pdfWithSignatureFieldTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileName));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.addNewPage();

        PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature")
                .setWidgetRectangle(new Rectangle(100, 600, 400, 150))
                .createSignature();
        signField.getPdfObject().put(PdfName.Name, new PdfName("test name"));
        signField.getPdfObject().put(PdfName.Reason, new PdfString("test reason"));
        signField.getPdfObject().put(PdfName.Location, new PdfString("test location"));
        signField.getPdfObject().put(PdfName.ContactInfo, new PdfString("test contact"));
        signField.getFirstFormAnnotation()
                .setBackgroundColor(ColorConstants.PINK)
                .setColor(ColorConstants.WHITE);
        form.addField(signField);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(fileName,
                sourceFolder + "cmp_pdfWithSignatureFieldTest.pdf", destinationFolder, "diff_"));
    }


    @Test
    public void pdfWithSignatureAndFontInBuilderFieldTest() throws IOException, InterruptedException {
        String fileName = destinationFolder + "pdfWithSignatureAndFontInBuilderFieldTestFieldTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileName));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.addNewPage();

        PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature")
                .setWidgetRectangle(new Rectangle(100, 600, 400, 150))
                .setFont(PdfFontFactory.createFont(StandardFonts.COURIER))
                .createSignature();
        signField.getPdfObject().put(PdfName.Name, new PdfName("test name"));
        signField.getPdfObject().put(PdfName.Reason, new PdfString("test reason"));
        signField.getPdfObject().put(PdfName.Location, new PdfString("test location"));
        signField.getPdfObject().put(PdfName.ContactInfo, new PdfString("test contact"));
        signField.getFirstFormAnnotation()
                .setBackgroundColor(ColorConstants.PINK)
                .setColor(ColorConstants.WHITE);
        form.addField(signField);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(fileName,
                sourceFolder + "cmp_pdfWithSignatureAndFontInBuilderFieldTest.pdf", destinationFolder,
                "diff_"));
    }

    static class CustomButtonFormField extends PdfButtonFormField {
        private int counter = 0;

        CustomButtonFormField(PdfDocument pdfDocument, String formFieldName) {
            super(pdfDocument);
            setPushButton(true);
            setFieldName(formFieldName);
        }

        CustomButtonFormField(PdfWidgetAnnotation annotation, PdfDocument pdfDocument, String formFieldName) {
            super(annotation, pdfDocument);
            setPushButton(true);
            setFieldName(formFieldName);
        }

        public int getCounter() {
            return counter;
        }

        @Override
        public boolean regenerateField() {
            boolean isRegenerated = super.regenerateField();
            if (isRegenerated) {
                counter++;
            }
            return isRegenerated;
        }
    }
}
