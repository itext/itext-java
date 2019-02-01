/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Category(IntegrationTest.class)
public class PdfFormFieldTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFieldTest01() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);

        Map<String, PdfFormField> fields = form.getFormFields();
        PdfFormField field = fields.get("Text1");

        Assert.assertTrue(fields.size() == 6);
        Assert.assertTrue(field.getFieldName().toUnicodeString().equals("Text1"));
        Assert.assertTrue(field.getValue().toString().equals("TestField"));
    }

    @Test
    public void formFieldTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Rectangle rect = new Rectangle(210, 490, 150, 22);
        PdfTextFormField field = PdfFormField.createText(pdfDoc, rect, "fieldName", "some value");
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formFieldTest03() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest03.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"), new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);

        PdfTextFormField field = PdfFormField.createText(pdfDoc, rect, "TestField", "some value");

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formFieldTest04() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest04.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"), new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);

        PdfTextFormField field = PdfFormField.createText(pdfDoc, rect, "TestField", "some value in courier font", PdfFontFactory.createFont(StandardFonts.COURIER), 10);

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void unicodeFormFieldTest() throws IOException {
        String filename = sourceFolder + "unicodeFormFieldFile.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> formFields = form.getFormFields();
        String fieldName = "\u5E10\u53F71"; // 帐号1: account number 1
        Assert.assertEquals(fieldName, formFields.keySet().toArray(new String[1])[0]);
    }

    @Test
    public void unicodeFormFieldTest2() throws IOException {
        String filename = sourceFolder + "unicodeFormFieldFile.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        String fieldName = "\u5E10\u53F71"; // 帐号1: account number 1
        Assert.assertNotNull(form.getField(fieldName));
    }

    @Test
    public void choiceFieldTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "choiceFieldTest01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(210, 490, 150, 20);

        String[] options = new String[]{"First Item", "Second Item", "Third Item", "Fourth Item"};
        PdfChoiceFormField choice = PdfFormField.createComboBox(pdfDoc, rect, "TestField", "First Item", options);

        form.addField(choice);

        Rectangle rect1 = new Rectangle(210, 250, 150, 90);

        PdfChoiceFormField choice1 = PdfFormField.createList(pdfDoc, rect1, "TestField1", "Second Item", options);
        choice1.setMultiSelect(true);
        form.addField(choice1);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_choiceFieldTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void buttonFieldTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "buttonFieldTest01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(36, 700, 20, 20);
        Rectangle rect1 = new Rectangle(36, 680, 20, 20);

        PdfButtonFormField group = PdfFormField.createRadioGroup(pdfDoc, "TestGroup", "1");

        PdfFormField.createRadioButton(pdfDoc, rect, group, "1");
        PdfFormField.createRadioButton(pdfDoc, rect1, group, "2");

        form.addField(group);

        PdfButtonFormField pushButton = PdfFormField.createPushButton(pdfDoc, new Rectangle(36, 650, 40, 20), "push", "Capcha");
        PdfButtonFormField checkBox = PdfFormField.createCheckBox(pdfDoc, new Rectangle(36, 560, 20, 20), "TestCheck", "1");

        form.addField(pushButton);
        form.addField(checkBox);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_buttonFieldTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void defaultRadiobuttonFieldTest() throws IOException, InterruptedException {
        String file = "defaultRadiobuttonFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect1 = new Rectangle(36, 700, 20, 20);
        Rectangle rect2 = new Rectangle(36, 680, 20, 20);

        PdfButtonFormField group = PdfFormField.createRadioGroup(pdfDoc, "TestGroup", "1");

        PdfFormField.createRadioButton(pdfDoc, rect1, group, "1");
        PdfFormField.createRadioButton(pdfDoc, rect2, group, "2");

        form.addField(group);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedRadiobuttonFieldTest() throws IOException, InterruptedException {
        String file = "customizedRadiobuttonFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect1 = new Rectangle(36, 700, 20, 20);
        Rectangle rect2 = new Rectangle(36, 680, 20, 20);

        PdfButtonFormField group2 = PdfFormField.createRadioGroup(pdfDoc, "TestGroup2", "1");

        PdfFormField.createRadioButton(pdfDoc, rect1, group2, "1")
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormField.VISIBLE);


        PdfFormField.createRadioButton(pdfDoc, rect2, group2, "2")
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormField.VISIBLE);

        form.addField(group2);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    @Test
    public void customizedRadiobuttonWithGroupRegeneratingFieldTest() throws IOException, InterruptedException {
        String file = "customizedRadiobuttonWithGroupRegeneratingFieldTest.pdf";

        String filename = destinationFolder + file;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect1 = new Rectangle(36, 700, 20, 20);
        Rectangle rect2 = new Rectangle(36, 680, 20, 20);

        PdfButtonFormField group2 = PdfFormField.createRadioGroup(pdfDoc, "TestGroup2", "1");

        PdfFormField.createRadioButton(pdfDoc, rect1, group2, "1")
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormField.VISIBLE);


        PdfFormField.createRadioButton(pdfDoc, rect2, group2, "2")
                .setBorderWidth(2).setBorderColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVisibility(PdfFormField.VISIBLE);

        group2.regenerateField();
        form.addField(group2);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }


    @Test
    public void buttonFieldTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "buttonFieldTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "buttonFieldTest02_input.pdf"), new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        ((PdfButtonFormField) form.getField("push")).setImage(sourceFolder + "Desert.jpg");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_buttonFieldTest02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void realFontSizeRegenerateAppearanceTest() throws IOException, InterruptedException {
        String sourceFilename = sourceFolder + "defaultAppearanceRealFontSize.pdf";
        String destFilename = destinationFolder + "realFontSizeRegenerateAppearance.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFilename), new PdfWriter(destFilename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        form.getField("fieldName").regenerateField();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destFilename, sourceFolder + "cmp_realFontSizeRegenerateAppearance.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void addFieldWithKidsTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfFormField root = PdfFormField.createEmptyField(pdfDoc);
        root.setFieldName("root");

        PdfFormField child = PdfFormField.createEmptyField(pdfDoc);
        child.setFieldName("child");
        root.addKid(child);

        PdfTextFormField text1 = PdfFormField.createText(pdfDoc, new Rectangle(100, 700, 200, 20), "text1", "test");
        child.addKid(text1);

        form.addField(root);

        Assert.assertEquals(3, form.getFormFields().size());
    }

    @Test
    public void fillFormWithDefaultResources() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithDefaultResources.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithDefaultResources.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "formWithDefaultResources.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);


        Map<String, PdfFormField> fields = form.getFormFields();
        PdfFormField field = fields.get("Text1");

        field.setValue("New value size must be 8");
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void fillFormTwiceWithoutResources() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithoutResources.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithoutResources.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "formWithoutResources.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);


        Map<String, PdfFormField> fields = form.getFormFields();
        PdfFormField field = fields.get("Text1");

        field.setValue("New value size must be 8").setFontSize(8);
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void autoScaleFontSizeInFormFields() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "autoScaleFontSizeInFormFields.pdf";
        String cmpPdf = sourceFolder + "cmp_autoScaleFontSizeInFormFields.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfFormField field = PdfFormField.createText(pdfDoc, new Rectangle(36, 786, 80, 20), "name", "TestValueAndALittleMore");
        form.addField(field.setFontSizeAutoScale());

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.NO_FIELDS_IN_ACROFORM)})
    public void acroFieldDictionaryNoFields() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "acroFieldDictionaryNoFields.pdf";
        String cmpPdf = sourceFolder + "cmp_acroFieldDictionaryNoFields.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "acroFieldDictionaryNoFields.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void regenerateAppearance() throws IOException, InterruptedException {
        String input = "regenerateAppearance.pdf";
        String output = "regenerateAppearance.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + input),
                new PdfWriter(destinationFolder + output),
                new StampingProperties().useAppendMode());
        PdfAcroForm acro = PdfAcroForm.getAcroForm(document, false);
        int i = 1;
        for (Map.Entry<String, PdfFormField> entry : acro.getFormFields().entrySet()) {
            if (entry.getKey().contains("field")) {
                PdfFormField field = entry.getValue();
                field.setValue("test" + i++, false);
            }
        }
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + output,
                sourceFolder + "cmp_" + output, destinationFolder, "diff"));
    }

    @Test
    public void regenerateAppearance2() throws IOException, InterruptedException {
        String input = "regenerateAppearance2.pdf";
        String output = "regenerateAppearance2.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + input),
                new PdfWriter(destinationFolder + output),
                new StampingProperties().useAppendMode());
        PdfAcroForm acro = PdfAcroForm.getAcroForm(document, false);
        acro.setNeedAppearances(true);
        PdfFormField field = acro.getField("number");
        field.setValue("20150044DR");
        document.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + output,
                sourceFolder + "cmp_" + output, destinationFolder, "diff"));
    }

    @Test
    public void multilineTextFieldWithAlignmentTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "multilineTextFieldWithAlignment.pdf";
        String cmpPdf = sourceFolder + "cmp_multilineTextFieldWithAlignment.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(210, 600, 150, 100);
        PdfTextFormField field = PdfFormField.createMultilineText(pdfDoc, rect, "fieldName", "some value\nsecond line\nthird");
        field.setJustification(PdfTextFormField.ALIGN_RIGHT);
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void flushedPagesTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "flushedPagesTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        pdfDoc.addNewPage().flush();
        pdfDoc.addNewPage().flush();
        pdfDoc.addNewPage();

        PdfTextFormField field = PdfFormField.createText(pdfDoc, new Rectangle(100, 100, 300, 20), "name", "");
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_flushedPagesTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void fillFormWithDefaultResourcesUpdateFont() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithDefaultResourcesUpdateFont.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithDefaultResourcesUpdateFont.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "formWithDefaultResources.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);


        Map<String, PdfFormField> fields = form.getFormFields();
        PdfFormField field = fields.get("Text1");

        // TODO DEVSIX-2016: the font in /DR of AcroForm dict is not updated, even though /DA field is updated.
        field.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
        field.setValue("New value size must be 8, but with different font.");

        new Canvas(new PdfCanvas(pdfDoc.getFirstPage()), pdfDoc, new Rectangle(30, 500, 500, 200))
                .add(new Paragraph("The text font after modification it via PDF viewer (e.g. Acrobat) shall be preserved."));

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formRegenerateWithInvalidDefaultAppearance01() throws IOException, InterruptedException {
        String testName = "formRegenerateWithInvalidDefaultAppearance01";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_"+ testName + ".pdf";
        String srcPdf = sourceFolder + "invalidDA.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(srcPdf);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> fields = form.getFormFields();
        fields.get("Text1").setValue("New field value");
        fields.get("Text2").setValue("New field value");
        fields.get("Text3").setValue("New field value");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    //DEVSIX-2393
    //TODO change cmp file after fix
    public void multilineFormFieldNewLineTest() throws IOException, InterruptedException {
        String testName = "multilineFormFieldNewLineTest";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_"+ testName + ".pdf";
        String srcPdf = sourceFolder + testName + ".pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(srcPdf);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> fields = form.getFormFields();
        fields.get("BEMERKUNGEN").setValue("First line\n\n\nFourth line");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void fillFormWithSameEmptyObjsForAppearance() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithSameEmptyObjsForAppearance.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithSameEmptyObjsForAppearance.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "fillFormWithSameEmptyObjsForAppearance.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");
        acroForm.getField("text_2").setValue("Text 2!");
        acroForm.getField("text.3").setValue("Text 3!");
        acroForm.getField("text.4").setValue("Text 4!");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void dashedBorderApearanceTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "dashedBorderApearanceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_dashedBorderApearanceTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfTextFormField[] fields = new PdfTextFormField[3];
        String[] names = new String[] {"fieldNoPattern", "fieldEmptyPattern", "fieldSingleEntryPattern"};
        float y = 830;
        PdfDictionary borderDict = new PdfDictionary();
        borderDict.put(PdfName.S, PdfName.D);
        PdfArray patternArray = new PdfArray();
        for (int i = 0; i < 3; i++) {
            if (i == 2)
                patternArray.add(new PdfNumber(10));
            if (i > 0)
                borderDict.put(PdfName.D, patternArray);
            fields[i] = PdfTextFormField.createText(pdfDoc, new Rectangle(10, y -= 70, 200, 50), names[i], names[i]);
            acroForm.addField(fields[i]);
            fields[i].setBorderStyle(borderDict);
            fields[i].setBorderWidth(3);
            fields[i].setBorderColor(ColorConstants.CYAN);
            fields[i].setBackgroundColor(ColorConstants.MAGENTA);
        }

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.COMB_FLAG_MAY_BE_SET_ONLY_IF_MAXLEN_IS_PRESENT, count = 2)})
    public void noMaxLenWithSetCombFlagTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "noMaxLenWithSetCombFlagTest.pdf";
        String cmpPdf = sourceFolder + "cmp_noMaxLenWithSetCombFlagTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfTextFormField textField = PdfFormField.createText(pdfDoc, new Rectangle(100, 500, 200, 200), "text");
        textField.setComb(true);

        // The line below should throw an exception, because the Comb flag may be set only if the MaxLen entry is present in the text field dictionary
        textField.setValue("12345678");

        textField.setMaxLen(1);

        form.addField(textField);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }


    @Test
    public void maxLenWithSetCombFlagAppearanceTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "maxLenFields.pdf";
        String outPdf = destinationFolder + "maxLenWithSetCombFlagAppearanceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_maxLenWithSetCombFlagAppearanceTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);

        form.getField("text1").setValue("123");
        form.getField("text2").setJustification(1).setValue("123");
        form.getField("text3").setJustification(2).setValue("123");
        form.getField("text4").setValue("12345678");
        form.getField("text5").setValue("123456789101112131415161718");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

}
