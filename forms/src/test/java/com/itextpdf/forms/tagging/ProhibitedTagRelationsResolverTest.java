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
package com.itextpdf.forms.tagging;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.ComboBoxField;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BackgroundBox;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.tagging.ProhibitedTagRelationsResolver;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ProhibitedTagRelationsResolverTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/forms" +
            "/ResolveProhibitedRelationsRuleTest/";

    public static final String FONT_LOCATION = "./src/test/resources/com/itextpdf/forms/fonts/NotoSans-Regular.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void forms001() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testf001.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));


        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);


        InputField inputField = new InputField("name");
        inputField.setValue("Hello world!");
        document.add(inputField);


        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void forms001Interactive() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testf001Interactive.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));

        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);


        InputField inputField = new InputField("name");
        inputField.setValue("Hello world!");
        inputField.setInteractive(true);
        inputField.getAccessibilityProperties().setAlternateDescription("This is an input field");
        document.add(inputField);


        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
        PdfDictionary f = form.getField("name").getPdfObject();
        f.put(PdfName.Contents, new PdfString("Hello world!"));

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }


    @Test
    public void forms002() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testf002.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));

        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);


        ComboBoxField comboBoxField = new ComboBoxField("name");
        comboBoxField.addOption(new SelectFieldItem("Hello world!"));
        comboBoxField.addOption(new SelectFieldItem("Hello world1!"));
        comboBoxField.addOption(new SelectFieldItem("Hello world2!"));

        document.add(comboBoxField);

        ListBoxField listBoxField = new ListBoxField("name1", 4, true);
        listBoxField.addOption(new SelectFieldItem("Hello world!"));
        listBoxField.addOption(new SelectFieldItem("Hello world1!"));
        listBoxField.addOption(new SelectFieldItem("Hello world2!"));

        document.add(listBoxField);

        ListBoxField listBoxField1 = new ListBoxField("name2", 4, false);
        listBoxField1.addOption(new SelectFieldItem("Hello world!"));
        listBoxField1.addOption(new SelectFieldItem("Hello world1!"));
        listBoxField1.addOption(new SelectFieldItem("Hello world2!"));

        document.add(listBoxField1);


        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void forms003() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testf003.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));


        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        generate0(document);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void forms005() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testf005.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        TextArea textArea = new TextArea("name");
        textArea.setValue("Hello world!");

        document.add(textArea);

        TextArea textArea1 = new TextArea("name1");
        textArea1.setPlaceholder(new Paragraph("Hello world!"));

        document.add(textArea1);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }


    @Test
    public void forms004() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testf004.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));


        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        CheckBox checkBox = new CheckBox("name");
        checkBox.setChecked(true);
        document.add(checkBox);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void formsButton() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testButton.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));


        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        Button button = new Button("name");

        Paragraph h1 = new Paragraph("Header 1");
        h1.getAccessibilityProperties().setRole(StandardRoles.H1);
        Paragraph paragraph = new Paragraph("Hello World");
        h1.add(paragraph);
        button.add(h1);

        document.add(button);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }



    public void generate0(Document parent) {
        Paragraph paragraph1 = new Paragraph();
        paragraph1.setMargins(0.000000F, 0.000000F, 0.000000F, 0.000000F);
        paragraph1.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
        paragraph1.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph1.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        paragraph1.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        this.generate1(paragraph1);
        parent.add(paragraph1);
    }


    public void generate1(Paragraph parent) {
        Radio radio1 = new Radio("A");
        radio1.setFontSize(9.999975F);
        radio1.setProperty(Property.BACKGROUND,
                new Background(new DeviceRgb(1.000000F, 1.000000F, 1.000000F), 1.000000F, BackgroundBox.BORDER_BOX));
        radio1.setProperty(Property.BORDER_BOTTOM,
                new SolidBorder(new DeviceRgb(0.752941F, 0.752941F, 0.752941F), 1.000000F, 1.000000F));
        radio1.setProperty(Property.BORDER_LEFT,
                new SolidBorder(new DeviceRgb(0.752941F, 0.752941F, 0.752941F), 1.000000F, 1.000000F));
        radio1.setProperty(Property.BORDER_RIGHT,
                new SolidBorder(new DeviceRgb(0.752941F, 0.752941F, 0.752941F), 1.000000F, 1.000000F));
        radio1.setProperty(Property.BORDER_TOP,
                new SolidBorder(new DeviceRgb(0.752941F, 0.752941F, 0.752941F), 1.000000F, 1.000000F));
        radio1.setProperty(Property.FIRST_LINE_INDENT, 0.000000F);
        radio1.setProperty(Property.HYPHENATION, null);
        radio1.setProperty(Property.LEADING, null);
        radio1.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
        radio1.setProperty(Property.MARGIN_BOTTOM, new UnitValue(UnitValue.POINT, 2.250000F));
        radio1.setProperty(Property.MARGIN_LEFT, new UnitValue(UnitValue.POINT, 2.499994F));
        radio1.setProperty(FormProperty.FORM_FIELD_RADIO_GROUP_NAME, "A");
        radio1.setProperty(Property.MARGIN_RIGHT, new UnitValue(UnitValue.POINT, 2.499994F));
        radio1.setProperty(FormProperty.FORM_FIELD_RADIO_BORDER_CIRCLE, Boolean.TRUE);
        radio1.setProperty(Property.MARGIN_TOP, new UnitValue(UnitValue.POINT, 2.250000F));
        radio1.setProperty(Property.PADDING_BOTTOM, new UnitValue(UnitValue.POINT, 0.000000F));
        radio1.setProperty(FormProperty.FORM_CONFORMANCE_LEVEL, null);
        radio1.setProperty(Property.PADDING_LEFT, new UnitValue(UnitValue.POINT, 0.000000F));
        radio1.setProperty(Property.PADDING_RIGHT, new UnitValue(UnitValue.POINT, 0.000000F));
        radio1.setProperty(Property.PADDING_TOP, new UnitValue(UnitValue.POINT, 0.000000F));
        radio1.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        radio1.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        radio1.setProperty(Property.BORDER_TOP_LEFT_RADIUS, null);
        radio1.setProperty(Property.BORDER_TOP_RIGHT_RADIUS, null);
        radio1.setProperty(Property.BORDER_BOTTOM_RIGHT_RADIUS, null);
        radio1.setProperty(Property.BORDER_BOTTOM_LEFT_RADIUS, null);
        radio1.setProperty(Property.NO_SOFT_WRAP_INLINE, Boolean.FALSE);
        parent.add(radio1);
    }


    private static void convertToUa2(PdfDocument pdfDocument) throws XMPException, IOException {
        // We can't depend on ua module in layout module so we need to do some low level operations
        // to convert the to ua2
        pdfDocument.getDiContainer()
                .register(ProhibitedTagRelationsResolver.class, new ProhibitedTagRelationsResolver(pdfDocument));
        byte[] bytes = Files.readAllBytes(Paths.get(
                "./src/test/resources/com/itextpdf/forms/ua/simplePdfUA2.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        pdfDocument.setXmpMetadata(xmpMeta);
        pdfDocument.setTagged();
        pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("PdfUA2 Title");
    }
}
