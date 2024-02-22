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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfUAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUAFormFieldsTest extends ExtendedITextTest {

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATest/PdfUAFormFieldTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAFormFieldTest/";


    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testCheckBox() throws FileNotFoundException {
        String dest = DESTINATION_FOLDER + "checkBoxLayout.pdf";
        Document document = createDocument(dest);
        CheckBox checkBox = new CheckBox("name");
        document.add(checkBox);
        document.close();
    }


    @Test
    public void testCheckBoxInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "checkBoxLayoutI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_checkBoxLayoutI.pdf";
        Document document = createDocument(dest);
        CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
        checkBox.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        document.add(checkBox);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testRadioButton() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "radioButtonLayout.pdf";
        String cmp = SOURCE_FOLDER + "cmp_radioButtonLayout.pdf";
        Document document = createDocument(dest);
        Radio radioButton = new Radio("name");
        document.add(radioButton);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testRadioButtonInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "radioButtonLayoutI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_radioButtonLayoutI.pdf";
        Document document = createDocument(dest);
        Radio radioButton = (Radio) new Radio("name", "empty").setInteractive(true);
        document.add(radioButton);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testRadioButtonGroup() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "radioButtonGroup.pdf";
        String cmp = SOURCE_FOLDER + "cmp_radioButtonGroup.pdf";
        Document document = createDocument(dest);
        Radio radioButton = new Radio("name", "group");
        Radio radioButton2 = new Radio("name2", "group");
        document.add(radioButton);
        document.add(radioButton2);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testRadioButtonGroupInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "radioButtonGroupInteractiveI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_radioButtonGroupInteractiveI.pdf";
        Document document = createDocument(dest);
        Radio radioButton = (Radio) new Radio("name", "group").setInteractive(true);
        Radio radioButton2 = (Radio) new Radio("name2", "group").setInteractive(true);
        document.add(radioButton);
        document.add(radioButton2);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testButton() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "buttonLayout.pdf";
        String cmp = SOURCE_FOLDER + "cmp_buttonLayout.pdf";
        Document document = createDocument(dest);
        Button button = new Button("name");
        button.setFont(PdfFontFactory.createFont(FONT));
        button.setValue("Click me");
        document.add(button);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testButtonInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "buttonLayoutI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_buttonLayoutI.pdf";
        Document document = createDocument(dest);
        Button button = (Button) new Button("name").setInteractive(true);
        button.setFont(PdfFontFactory.createFont(FONT));
        button.setValue("Click me");
        document.add(button);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testInputField() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "inputFieldLayout.pdf";
        String cmp = SOURCE_FOLDER + "cmp_inputFieldLayout.pdf";
        Document document = createDocument(dest);
        InputField text = new InputField("name");
        document.add(text);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testInputFieldInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "inputFieldLayoutI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_inputFieldLayoutI.pdf";
        Document document = createDocument(dest);
        PdfFont font = PdfFontFactory.createFont(FONT);
        InputField text = (InputField) new InputField("name").setFont(font).setInteractive(true);
        document.add(text);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testTextArea() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "textAreaLayout.pdf";
        String cmp = SOURCE_FOLDER + "cmp_textAreaLayout.pdf";
        Document document = createDocument(dest);
        TextArea text = new TextArea("name");
        document.add(text);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testTextAreaInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "textAreaLayoutI.pdf";
        String cmp = DESTINATION_FOLDER + "textAreaLayoutI.pdf";
        Document document = createDocument(dest);
        PdfFont font = PdfFontFactory.createFont(FONT);
        TextArea text = (TextArea) new TextArea("name").setFont(font).setInteractive(true);
        document.add(text);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testListBox() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "listBoxLayout.pdf";
        String cmp = DESTINATION_FOLDER + "cmp_listBoxLayout.pdf";
        Document document = createDocument(dest);
        ListBoxField list = new ListBoxField("name", 1, false);
        list.addOption("value1");
        list.addOption("value2");
        document.add(list);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testListBoxInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "listBoxLayoutI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_listBoxLayoutI.pdf";
        Document document = createDocument(dest);
        PdfFont font = PdfFontFactory.createFont(FONT);
        ListBoxField list = (ListBoxField) new ListBoxField("name", 1, false).setFont(font).setInteractive(true);
        list.addOption("value1");
        list.addOption("value2");
        document.add(list);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    @Ignore("DEVSIX-8128")
    public void testSelectField() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "selectFieldLayout.pdf";
        String cmp = SOURCE_FOLDER + "cmp_selectFieldLayout.pdf";
        Document document = createDocument(dest);
        ListBoxField list = new ListBoxField("name", 1, false);
        list.addOption("value1");
        list.addOption("value2");
        document.add(list);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void testSelectFieldInteractive() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "selectFieldLayoutI.pdf";
        String cmp = SOURCE_FOLDER + "cmp_selectFieldLayoutI.pdf";
        Document document = createDocument(dest);
        PdfFont font = PdfFontFactory.createFont(FONT);
        ListBoxField list = (ListBoxField) new ListBoxField("name", 1, false).setFont(font).setInteractive(true);
        list.addOption("value1");
        list.addOption("value2");
        document.add(list);
        document.close();
        assertPdf(dest, cmp);
    }

    @Test
    public void checkBoxBuilderTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "checkBoxBuilderTest.pdf";
        String cmp = SOURCE_FOLDER + "cmp_checkBoxBuilderTest.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        CheckBoxFormFieldBuilder builder = new CheckBoxFormFieldBuilder(document, "chk");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        builder.setCheckType(CheckBoxType.CHECK);
        builder.setWidgetRectangle(new Rectangle(200, 200, 20, 20));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        acroForm.addField(builder.createCheckBox().setValue("Yes"));
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160   Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    @Test
    public void radioBuilderTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "radioBuilder.pdf";
        String cmp = SOURCE_FOLDER + "cmp_radioBuilder.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(document, "radio");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        PdfFormField group = builder.createRadioGroup().setValue("bing");
        group.addKid(builder.createRadioButton("bing", new Rectangle(200, 200, 20, 20)));
        group.addKid(builder.createRadioButton("bong", new Rectangle(230, 200, 20, 20)));
        acroForm.addField(group);
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160 Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    @Test
    public void inputTextFieldTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "inputTextBuilder.pdf";
        String cmp = SOURCE_FOLDER + "cmp_inputTextBuilder.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        TextFormFieldBuilder builder = new TextFormFieldBuilder(document, "txt");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        builder.setWidgetRectangle(new Rectangle(200, 200, 100, 20));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        PdfFormField f = builder.setFont(PdfFontFactory.createFont(FONT)).createText();
        f.setValue("Hello from text box");
        acroForm.addField(f);
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160  Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    @Test
    public void inputAreaFieldTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "inputAreaBuilder.pdf";
        String cmp = SOURCE_FOLDER + "cmp_inputAreaBuilder.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        TextFormFieldBuilder builder = new TextFormFieldBuilder(document, "txt");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        builder.setWidgetRectangle(new Rectangle(200, 200, 100, 200));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        PdfFormField f = builder.setFont(PdfFontFactory.createFont(FONT)).createMultilineText();
        f.setValue("Hello from text box");
        acroForm.addField(f);
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160  Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    @Test
    public void listBoxFieldTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "listBoxBuilder.pdf";
        String cmp = SOURCE_FOLDER + "cmp_ListboxBuilder.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(document, "txt");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        builder.setWidgetRectangle(new Rectangle(200, 200, 100, 200));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        builder.setOptions(new String[] {"opt 1", "opt 2", "opt 3"});
        PdfFormField f = builder.setFont(PdfFontFactory.createFont(FONT)).createList();
        f.setValue("opt 2");
        acroForm.addField(f);
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160  Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    @Test
    public void comboBoxFieldTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "comboboxBuilder.pdf";
        String cmp = SOURCE_FOLDER + "cmp_comboboxBuilder.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(document, "txt");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        builder.setWidgetRectangle(new Rectangle(200, 200, 100, 200));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        builder.setOptions(new String[] {"opt 1", "opt 2", "opt 3"});
        PdfFormField f = builder.setFont(PdfFontFactory.createFont(FONT)).createComboBox();
        f.setValue("opt 2");
        acroForm.addField(f);
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160  Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    @Test
    public void buttonTest() throws IOException, InterruptedException {
        String dest = DESTINATION_FOLDER + "buttonBuilder.pdf";
        String cmp = SOURCE_FOLDER + "cmp_buttonBuilder.pdf";
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));

        PushButtonFormFieldBuilder builder = new PushButtonFormFieldBuilder(document, "txt");
        builder.setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
        builder.setWidgetRectangle(new Rectangle(200, 200, 100, 200));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        PdfFormField f = builder.setFont(PdfFontFactory.createFont(FONT)).createPushButton();
        f.setValue("Click me");
        acroForm.addField(f);
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));
        // TODO-DEVSIX-8160  formfield TU entry Assert.assertNull(new VeraPdfValidator().validate(dest));
    }

    private static Document createDocument(String dest) throws FileNotFoundException {
        PdfDocument doc = new PdfUATestPdfDocument(new PdfWriter(dest, PdfUATestPdfDocument.createWriterProperties()));
        return new Document(doc);
    }

    private static void assertPdf(String dest, String cmp) throws IOException, InterruptedException {
        // TODO-DEVSIX-8160 formfield TU entry Assert.assertNull(new VeraPdfValidator().validate(dest)); //
        //  Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, DESTINATION_FOLDER, "diff_"));

    }

}
