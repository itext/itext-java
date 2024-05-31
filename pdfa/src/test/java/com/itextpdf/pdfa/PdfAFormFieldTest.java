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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.NonTerminalFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.ComboBoxField;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAFormFieldTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfAFormFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pdfAButtonFieldTest() throws Exception {
        PdfDocument pdf;
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        String file = "pdfAButtonField.pdf";
        String filename = DESTINATION_FOLDER + file;
        pdf = new PdfADocument(
                new PdfWriter(FileUtil.getFileOutputStream(filename)),
                PdfAConformanceLevel.PDF_A_1B,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB ICC preference", is));

        PageSize pageSize = PageSize.LETTER;
        Document doc = new Document(pdf, pageSize);
        PdfFontFactory.register(SOURCE_FOLDER + "FreeSans.ttf", SOURCE_FOLDER + "FreeSans.ttf");
        PdfFont font = PdfFontFactory.createFont(
                SOURCE_FOLDER + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);

        PdfButtonFormField group = new RadioFormFieldBuilder(pdf, "group")
                .setGenericConformanceLevel(PdfAConformanceLevel.PDF_A_1B).createRadioGroup();
        group.setValue("");
        group.setReadOnly(true);

        Paragraph p = new Paragraph();
        Text t = new Text("supported");

        t.setFont(font);

        p.add(t);

        Image ph = new Image(new PdfFormXObject(new Rectangle(10, 10)));
        Paragraph pc = new Paragraph().add(ph);
        PdfAButtonFieldTestRenderer r = new PdfAButtonFieldTestRenderer(pc, group, "v1");

        pc.setNextRenderer(r);

        p.add(pc);

        Paragraph pc1 = new Paragraph().add(ph);
        PdfAButtonFieldTestRenderer r1 = new PdfAButtonFieldTestRenderer(pc, group, "v2");
        pc1.setNextRenderer(r1);

        Paragraph p2 = new Paragraph();
        Text t2 = new Text("supported 2");
        t2.setFont(font);

        p2.add(t2).add(pc1);

        doc.add(p);
        doc.add(p2);
        //set generateAppearance param to false to retain custom appearance
        group.setValue("v1", false);
        PdfFormCreator.getAcroForm(pdf, true).addField(group);

        pdf.close();
        Assert.assertNull(
                new CompareTool().compareByContent(filename, SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_" + file,
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void pdfA1DocWithPdfA1ButtonFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ButtonField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ButtonField.pdf";

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;

        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField emptyField = new NonTerminalFormFieldBuilder(pdfDoc, "empty")
                .setGenericConformanceLevel(conformanceLevel).createNonTerminalFormField();
        emptyField.addKid(new PushButtonFormFieldBuilder(pdfDoc, "button")
                .setWidgetRectangle(new Rectangle(36, 756, 20, 20)).setGenericConformanceLevel(conformanceLevel)
                .createPushButton().setFieldFlags(PdfAnnotation.PRINT)
                .setFieldName("button").setValue("hello"));
        form.addField(emptyField);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA1DocWithPdfA1CheckBoxFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1CheckBoxField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1CheckBoxField.pdf";

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;

        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        form.addField(new CheckBoxFormFieldBuilder(pdfDoc, "checkBox").setWidgetRectangle(new Rectangle(36, 726, 20, 20))
                .setCheckType(CheckBoxType.STAR).setGenericConformanceLevel(conformanceLevel)
                .createCheckBox().setValue("1"));
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY)})
    public void pdfA1DocWithPdfA1ChoiceFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ChoiceField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ChoiceField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfArray options = new PdfArray();
        options.add(new PdfString("Name"));
        options.add(new PdfString("Surname"));
        PdfFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, "choice").setWidgetRectangle(new Rectangle(36, 696, 100, 70))
                .setOptions(options).setGenericConformanceLevel(conformanceLevel)
                .createList().setValue("1", true);
        choiceFormField.setFont(fontFreeSans);
        form.addField(choiceFormField);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA1DocWithPdfA1ComboBoxFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ComboBoxField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ComboBoxField.pdf";

        PdfFont fontCJK = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSansCJKtc-Light.otf",
                        PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, "combo")
                .setWidgetRectangle(new Rectangle(156, 616, 70, 70)).setOptions(new String[]{"用", "规", "表"})
                .setGenericConformanceLevel(conformanceLevel).createComboBox()
                .setValue("用");
        choiceFormField.setFont(fontCJK);
        form.addField(choiceFormField);
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.MULTIPLE_VALUES_ON_A_NON_MULTISELECT_FIELD)})
    public void pdfA1DocWithPdfA1ListFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ListField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ListField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfChoiceFormField f = new ChoiceFormFieldBuilder(pdfDoc, "list")
                .setWidgetRectangle(new Rectangle(86, 556, 50, 200)).setOptions(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
                .setGenericConformanceLevel(conformanceLevel).createList();
        f.setValue("9").setFont(fontFreeSans);
        f.setValue("4");
        f.setTopIndex(2);
        f.setListSelected(new String[] {"3", "5"});
        form.addField(f);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA1DocWithPdfA1PushButtonFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1PushButtonField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1PushButtonField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField pushButtonFormField = new PushButtonFormFieldBuilder(pdfDoc, "push button").setWidgetRectangle(new Rectangle(36, 526, 100, 20))
                .setCaption("Push").setGenericConformanceLevel(conformanceLevel)
                .createPushButton();
        pushButtonFormField.setFont(fontFreeSans).setFontSize(12);
        form.addField(pushButtonFormField);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA1DocWithPdfA1RadioButtonFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1RadioButtonField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1RadioButtonField.pdf";

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        String pdfFormFieldName = "radio group";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, pdfFormFieldName).setGenericConformanceLevel(conformanceLevel);
        PdfButtonFormField radioGroup = builder.setGenericConformanceLevel(conformanceLevel)
                .createRadioGroup();
        radioGroup.setValue("");
        PdfFormAnnotation radio1 = builder
                .createRadioButton("1",new Rectangle(36, 496, 20, 20))
                .setBorderWidth(2).setBorderColor(ColorConstants.ORANGE);
        PdfFormAnnotation radio2 = builder
                .createRadioButton("2",new Rectangle(66, 496, 20, 20))
                .setBorderWidth(2).setBorderColor(ColorConstants.ORANGE);

        radioGroup.addKid(radio1);
        radioGroup.addKid(radio2);

        form.addField(radioGroup);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA1DocWithPdfA1TextFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1TextField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1TextField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        fontFreeSans.setSubset(false);

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField textFormField = new TextFormFieldBuilder(pdfDoc, "text").setWidgetRectangle(new Rectangle(36, 466, 90, 20))
                .setGenericConformanceLevel(conformanceLevel).createText().setValue("textField").setValue("iText");
        textFormField.setFont(fontFreeSans).setFontSize(12);
        form.addField(textFormField);
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA1DocWithPdfA1SignatureFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1SignatureField";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1SignatureField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        fontFreeSans.setSubset(false);

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField signFormField = new SignatureFormFieldBuilder(pdfDoc, "signature")
                .setGenericConformanceLevel(conformanceLevel).createSignature();
        signFormField.setFont(fontFreeSans).setFontSize(20);
        form.addField(signFormField);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void mergePdfADocWithFormTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "pdfADocWithTextFormField.pdf";
        String mergedDocFileName = DESTINATION_FOLDER + "mergedPdfADoc.pdf";

        try (InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
                PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), PdfAConformanceLevel.PDF_A_1B,
                        new PdfOutputIntent("Custom", "",
                                "http://www.color.org", "sRGB ICC preference", is));
                Document doc = new Document(pdfDoc)) {

            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf", PdfEncodings.WINANSI);

            doc.add(new Paragraph(new Text("Some text").setFont(font).setFontSize(10)));

            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            PdfFormField field = new TextFormFieldBuilder(pdfDoc, "text").setWidgetRectangle(new Rectangle(150, 100, 100, 20))
                    .setGenericConformanceLevel(PdfAConformanceLevel.PDF_A_1B).createText()
                    .setValue("textField").setFieldName("text");
            field.setFont(font).setFontSize(10);
            field.getFirstFormAnnotation().setPage(1);
            form.addField(field, pdfDoc.getPage(1));
        }

        Assert.assertNull(new VeraPdfValidator().validate(fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

        PdfADocument pdfDocToMerge;
        try (InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
                PdfDocument newDoc = new PdfDocument(new PdfReader(fileName))) {
            pdfDocToMerge = new PdfADocument(new PdfWriter(mergedDocFileName).setSmartMode(true),
                    PdfAConformanceLevel.PDF_A_1B,
                    new PdfOutputIntent("Custom", "",
                            "http://www.color.org", "sRGB ICC preference", is));

            newDoc.copyPagesTo(1, newDoc.getNumberOfPages(), pdfDocToMerge, new PdfPageFormCopier());
        }

        pdfDocToMerge.close();
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_mergePdfADocWithForm.pdf";
        Assert.assertNull(new VeraPdfValidator().validate(mergedDocFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(mergedDocFileName, cmp, DESTINATION_FOLDER, "diff_"));

    }


    @Test
    public void testComboBoxNoFont() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testComboBoxNoFont.pdf";
        makePdfDocument(outPdf, null, (document -> {
            ComboBoxField comboBoxField = new ComboBoxField("combobox");
            comboBoxField.setWidth(200);
            comboBoxField.setInteractive(true);
            comboBoxField.addOption(new SelectFieldItem("item1"));
            comboBoxField.addOption(new SelectFieldItem("item2"));
            comboBoxField.addOption(new SelectFieldItem("item3"));
            Assert.assertThrows(IllegalStateException.class, () -> {
                document.add(comboBoxField);
            });
        }));
    }

    @Test
    public void testButtonNoFont() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testButtonNoFont.pdf";
        makePdfDocument(outPdf, null, (document -> {
            Button button = new Button("button");
            button.setValue("Hello there");
            button.setInteractive(true);
            Assert.assertThrows(IllegalStateException.class, () -> {
                document.add(button);
            });
        }));
    }

    @Test
    public void testTextFieldNoFont() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testTextFieldNoFont.pdf";
            makePdfDocument(outPdf, null, (document -> {
                InputField inputField = new InputField("inputfield");
                inputField.setValue("Hello there");
                inputField.setInteractive(true);

                Assert.assertThrows(IllegalStateException.class, () -> {
                    document.add(inputField);
                });
            }));
    }


    @Test
    public void testCheckboxWithPDFA() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testCheckboxNonPdfa.pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testCheckboxNonPdfa.pdf";
        makePdfDocument(outPdf, cmp, (doc) -> {
            CheckBox checkBox = new CheckBox("CheckBox");
            checkBox.setChecked(true);
            checkBox.setInteractive(true);
            checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1A);
            doc.add(checkBox);
        });
    }


    @Test
    public void testMultipleCombinationsFontOnFieldSeparate() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testMultipleCombinations.pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testMultipleCombinations.pdf";
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        makePdfDocument(outPdf, cmp, document -> {
            for (Supplier<IFormField> formFieldSupplier : generateFormFields()) {
                IFormField formField = formFieldSupplier.get();
                formField.setProperty(Property.FONT, font);
                formField.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLACK, 1));
                formField.setInteractive(true);
                document.add(formField);
            }
        });
    }


    @Test
    public void testMultipleCombinationsWriteAndReload() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testMultipleCombinationsWriteAndLoad1.pdf";
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        makePdfDocument(outPdf, null, (document -> {
            for (Supplier<IFormField> formFieldSupplier : generateFormFields()) {
                IFormField formField = formFieldSupplier.get();
                formField.setProperty(Property.FONT, font);
                formField.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLACK, 1));
                formField.setInteractive(true);
                document.add(formField);
            }
        }));
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testMultipleCombinationsWriteAndLoad.pdf";
        String outPdf2 = DESTINATION_FOLDER + "testMultipleCombinationsWriteAndLoad2.pdf";
        PdfADocument newDoc = new PdfADocument(new PdfReader(outPdf), new PdfWriter(outPdf2));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(newDoc, false);
        for (Entry<String, PdfFormField> stringPdfFormFieldEntry : acroForm.getAllFormFields().entrySet()) {
            stringPdfFormFieldEntry.getValue().setValue("item1");
        }
        newDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outPdf2, cmp, DESTINATION_FOLDER));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf2)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void testMultipleCombinationsOnDocument() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testMultipleCombinationsOnDocument.pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testMultipleCombinationsOnDocument.pdf";
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        makePdfDocument(outPdf, cmp, (document -> {
            document.setFont(font);
            for (Supplier<IFormField> formFieldSupplier : generateFormFields()) {
                IFormField formField = formFieldSupplier.get();
                formField.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLACK, 1));
                formField.setProperty(Property.FONT, font);
                formField.setInteractive(true);
                document.add(formField);
            }
        }));
    }

    @Test
    public void testMultipleCombinationsFontOnFieldSeparateNonInteractive() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testMultipleCombinationsNonInteractive.pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testMultipleCombinationsNonInteractive.pdf";
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        makePdfDocument(outPdf, cmp, (document -> {
            for (Supplier<IFormField> formFieldSupplier : generateFormFields()) {
                IFormField formField = formFieldSupplier.get();
                formField.setProperty(Property.FONT, font);
                formField.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLACK, 1));
                formField.setInteractive(false);
                document.add(formField);
            }
        }));
    }

    @Test
    public void testMultipleCombinationsOnDocumentNonInteractive() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "testMultipleCombinationsOnDocumentNonInteractive.pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testMultipleCombinationsOnDocumentNonInteractive.pdf";
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        makePdfDocument(outPdf, cmp, (document -> {
            document.setFont(font);
            for (Supplier<IFormField> formFieldSupplier : generateFormFields()) {
                IFormField formField = formFieldSupplier.get();
                formField.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLACK, 1));
                formField.setProperty(Property.FONT, font);
                formField.setInteractive(false);
                document.add(formField);
            }
        }));
    }

    @Test
    public void testCopyPagesDoesntEmbedHelveticaFont() throws IOException, InterruptedException {
        String simplePdf = DESTINATION_FOLDER + "simplePdfAWithFormfield.pdf";
        String outPdf = DESTINATION_FOLDER + "testCopyPagesDoesntEmbedHelveticaFont.pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_testCopyPagesDoesntEmbedHelveticaFont.pdf";

        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfWriter writer = new PdfWriter(simplePdf, new WriterProperties()
                .setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));

        Document document = new Document(doc);
        document.add(new InputField("inputfield1").setFont(font).setInteractive(true).setValue("Hello there"));
        document.add(new Paragraph("Hello there paragraph").setFont(font));
        doc.close();

        PdfWriter writer2 = new PdfWriter(outPdf,
                new WriterProperties()
                        .setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc2 = new PdfADocument(writer2, PdfAConformanceLevel.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));

        PdfDocument docToCopy = new PdfDocument(new PdfReader(simplePdf));
        docToCopy.copyPagesTo(1, 1, doc2, new PdfPageFormCopier());
        docToCopy.close();
        doc2.close();

        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmp, DESTINATION_FOLDER, "diff_"));

    }

    @Test
    public void pdfASignatureFieldWithTextAndFontTest() throws IOException, InterruptedException {
        String name = "pdfASignatureFieldTestWithText";
        String fileName = DESTINATION_FOLDER + name + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp/PdfAFormFieldTest/cmp_" + name + ".pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

        makePdfDocument(fileName, cmp, (pdfDoc) -> {
            SignatureFieldAppearance signatureFieldAppearance = new SignatureFieldAppearance("Signature1");
            signatureFieldAppearance.setContent(new SignedAppearanceText().setLocationLine("HEEELLLLLO"));
            signatureFieldAppearance.setInteractive(true);
            signatureFieldAppearance.setFont(fontFreeSans);
            pdfDoc.add(signatureFieldAppearance);
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc.getPdfDocument(), true);
            SignatureFormFieldBuilder signatureFormFieldBuilder = new SignatureFormFieldBuilder(pdfDoc.getPdfDocument(),
                    "Signature2");

            SignatureFieldAppearance signatureFieldAppearance2 = new SignatureFieldAppearance("Signature2");
            signatureFieldAppearance2.setContent(new SignedAppearanceText().setLocationLine("Byeeee"));
            signatureFieldAppearance2.setInteractive(true);

            PdfSignatureFormField signatureFormField = signatureFormFieldBuilder.setWidgetRectangle(
                            new Rectangle(200, 200, 40, 40))
                    .setFont(fontFreeSans)
                    .setGenericConformanceLevel(PdfAConformanceLevel.PDF_A_4)
                    .createSignature();
            signatureFormField.getFirstFormAnnotation().setFormFieldElement(signatureFieldAppearance2);
            form.addField(signatureFormField);

        });

    }

    private void makePdfDocument(String outPdf, String cmp, Consumer<Document> consumer)
            throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(outPdf,
                new WriterProperties()
                        .setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));

        Document document = new Document(doc);
        consumer.accept(document);
        doc.close();
        if (cmp == null) {
            return;
        }
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmp, DESTINATION_FOLDER));
    }

    private List<Supplier<IFormField>> generateFormFields() {
        List<Supplier<IFormField>> inputs = new ArrayList<>();

        inputs.add(() -> {
                    CheckBox checkBox = new CheckBox("CheckBox");
                    checkBox.setChecked(true);
                    checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_4);
                    return checkBox;
        });
        inputs.add(() -> {
                    CheckBox checkBox = new CheckBox("CheckBox1");
                    checkBox.setChecked(false);
                    checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_4);
                    return checkBox;
        });
        inputs.add(() -> {
                    InputField inputField = new InputField("inputfield1");
                    return inputField;
        });
        inputs.add(() -> {
                    InputField inputField = new InputField("inputfield2");
                    inputField.setValue("Hello there");
                    return inputField;
        });
        inputs.add(() -> {
                    Radio radio = new Radio("Radio1", "group1");
                    radio.setChecked(true);
                    return radio;
        });
        inputs.add(() -> {
                    Radio radio = new Radio("Radio2", "group1");
                    radio.setChecked(false);
                    return radio;
        });
        inputs.add(() -> {
                    ComboBoxField comboBoxField = new ComboBoxField("combobox1");
                    comboBoxField.setWidth(200);
                    comboBoxField.addOption(new SelectFieldItem("item1"));
                    comboBoxField.addOption(new SelectFieldItem("item2"));
                    comboBoxField.addOption(new SelectFieldItem("item3"));
                    return comboBoxField;
        });
        inputs.add(() -> {
                    ComboBoxField comboBoxField = new ComboBoxField("combobox2");
                    comboBoxField.setWidth(200);
                    comboBoxField.addOption(new SelectFieldItem("item1"));
                    comboBoxField.addOption(new SelectFieldItem("item2"));
                    comboBoxField.addOption(new SelectFieldItem("item3"));
                    comboBoxField.setSelected(0);
                    return comboBoxField;
        });
        inputs.add(() -> {
                    TextArea textArea = new TextArea("textarea1");
                    textArea.setValue("Hello there");
                    textArea.setHeight(100);
                    textArea.setWidth(300);
                    return textArea;
        });
        inputs.add(() -> {
                    TextArea textArea = new TextArea("textarea2");
                    textArea.setHeight(100);
                    textArea.setWidth(300);
                    return textArea;
                }
        );
        inputs.add(() -> {
                    Button btn = new Button("button1");
                    btn.setValue("Hello button");
                    return btn;
        });
        return inputs;
    }

    static class PdfAButtonFieldTestRenderer extends ParagraphRenderer {
        private PdfButtonFormField _group;
        private String _value;

        public PdfAButtonFieldTestRenderer(Paragraph para, PdfButtonFormField group, String value) {
            super(para);
            _group = group;
            _value = value;
        }

        @Override
        public void draw(DrawContext context) {
            int pageNumber = getOccupiedArea().getPageNumber();
            Rectangle bbox = getInnerAreaBBox();
            PdfDocument pdf = context.getDocument();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdf, true);
            PdfFormAnnotation chk = new RadioFormFieldBuilder(pdf, "")
                    .setGenericConformanceLevel(PdfAConformanceLevel.PDF_A_1B).createRadioButton(_value, bbox);
            _group.addKid(chk);
            chk.setPage(pageNumber);

            chk.setVisibility(PdfFormAnnotation.VISIBLE);
            chk.setBorderColor(ColorConstants.BLACK);
            chk.setBackgroundColor(ColorConstants.WHITE);
            _group.setReadOnly(true);

            PdfFormXObject appearance = new PdfFormXObject(bbox);
            PdfCanvas canvas = new PdfCanvas(appearance, pdf);

            canvas.saveState()
                    .moveTo(bbox.getLeft(), bbox.getBottom())
                    .lineTo(bbox.getRight(), bbox.getBottom())
                    .lineTo(bbox.getRight(), bbox.getTop())
                    .lineTo(bbox.getLeft(), bbox.getTop())
                    .lineTo(bbox.getLeft(), bbox.getBottom())
                    .setLineWidth(1f)
                    .stroke()
                    .restoreState();

            //form.addFieldAppearanceToPage(chk, pdf.getPage(pageNumber));
            //appearance stream was set, while AS has kept as is, i.e. in Off state.
            chk.setAppearance(PdfName.N, "v1".equals(_value) ? _value : "Off", appearance.getPdfObject());
        }

        @Override
        public IRenderer getNextRenderer() {
            return new PdfAButtonFieldTestRenderer((Paragraph) modelElement, _group, _value);
        }
    }

}
