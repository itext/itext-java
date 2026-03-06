/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.ComboBoxField;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BackgroundImage.Builder;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAFormFieldsTest extends ExtendedITextTest {

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAFormFieldTest/";

    private static final String DOG = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBox(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> new CheckBox("name"));
        framework.assertBothValid("testCheckBox");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxWithCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setPdfConformance(conformance);
            cb.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
            cb.setBackgroundColor(ColorConstants.YELLOW);
            return cb;
        });
        framework.assertBothValid("testCheckBoxWithCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxChecked(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setPdfConformance(conformance);
            cb.setChecked(true);
            return cb;
        });
        framework.assertBothValid("testCheckBoxChecked");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxCheckedAlternativeDescription(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setPdfConformance(conformance);
            cb.getAccessibilityProperties().setAlternateDescription("Yello");
            cb.setChecked(true);
            return cb;
        });
        framework.assertBothValid("testCheckBoxCheckedAlternativeDescription");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxCheckedCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setPdfConformance(conformance);
            cb.setChecked(true);
            cb.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            cb.setBackgroundColor(ColorConstants.GREEN);
            cb.setCheckBoxType(CheckBoxType.STAR);
            cb.setSize(20);
            return cb;
        });
        framework.assertBothValid("testCheckBoxCheckedCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
            checkBox.setPdfConformance(conformance);
            checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
            return checkBox;
        });
        framework.assertBothValid("testCheckBoxInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxInteractiveCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
            checkBox.setPdfConformance(conformance);
            checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
            checkBox.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            checkBox.setBackgroundColor(ColorConstants.GREEN);
            checkBox.setSize(20);
            checkBox.setCheckBoxType(CheckBoxType.SQUARE);
            return checkBox;
        });
        framework.assertBothValid("testCheckBoxInteractiveCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxInteractiveCustomAppearanceChecked(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
            checkBox.setPdfConformance(conformance);
            checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
            checkBox.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            checkBox.setBackgroundColor(ColorConstants.GREEN);
            checkBox.setSize(20);
            checkBox.setChecked(true);
            checkBox.setCheckBoxType(CheckBoxType.SQUARE);
            return checkBox;
        });
        framework.assertBothValid("checkBoxInteractiveCustomAppChecked");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButton(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> new Radio("name"));
        framework.assertBothValid("testRadioButton");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonChecked(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name");
            radio.setChecked(true);
            return radio;
        });
        framework.assertBothValid("testRadioButtonChecked");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name");
            radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            radio.setBackgroundColor(ColorConstants.GREEN);
            radio.setSize(20);
            return radio;
        });
        framework.assertBothValid("testRadioButtonCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearanceChecked(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name");
            radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            radio.setBackgroundColor(ColorConstants.GREEN);
            radio.setSize(20);
            radio.setChecked(true);
            return radio;
        });
        framework.assertBothValid("testRadioButtonCustomAppearanceChecked");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroup(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> new Radio("name", "group"));
        framework.addSuppliers(document -> new Radio("name2", "group"));
        framework.assertBothValid("testRadioButtonGroup");
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio r = new Radio("name", "group");
            r.setSize(20);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.setBackgroundColor(ColorConstants.GREEN);
            return r;
        });
        framework.addSuppliers(document -> {
            Radio r = new Radio("name2", "group");
            r.setSize(20);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.setBackgroundColor(ColorConstants.GREEN);
            return r;
        });
        framework.assertBothValid("testRadioButtonGroupCustom");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearanceChecked(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio r = new Radio("name", "group");
            r.setSize(20);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.setBackgroundColor(ColorConstants.GREEN);
            return r;
        });
        framework.addSuppliers(document -> {
            Radio r = new Radio("name2", "group");
            r.setSize(20);
            r.setChecked(true);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.setBackgroundColor(ColorConstants.GREEN);
            return r;
        });
        framework.assertBothValid("testRadioButtonGroupCustomAppearanceChecked");
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio r = new Radio("name", "group");
            r.setInteractive(true);
            r.getAccessibilityProperties().setAlternateDescription("Hello");
            return r;
        });
        framework.assertBothValid("testRadioButtonInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCheckedInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name", "group");
            radio.setInteractive(true);
            radio.setChecked(true);
            radio.getAccessibilityProperties().setAlternateDescription("Hello");
            return radio;
        });
        framework.assertBothValid("testRadioButtonCheckedInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name", "group");
            radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            radio.setBackgroundColor(ColorConstants.GREEN);
            radio.setSize(20);
            radio.setInteractive(true);
            radio.getAccessibilityProperties().setAlternateDescription("Hello");
            return radio;
        });
        framework.assertBothValid("testRadioButtonCustomAppearanceInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearanceCheckedInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name", "Group");
            radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            radio.setBackgroundColor(ColorConstants.GREEN);
            radio.setSize(20);
            radio.setChecked(true);
            radio.getAccessibilityProperties().setAlternateDescription("Hello");
            radio.setInteractive(true);
            return radio;
        });
        framework.assertBothValid("radioBtnCustomAppCheckedInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio r = new Radio("name", "group");
            r.setInteractive(true);
            r.getAccessibilityProperties().setAlternateDescription("Hello");
            return r;
        });
        framework.addSuppliers(document -> {
            Radio r = new Radio("name2", "group");
            r.setInteractive(true);
            r.getAccessibilityProperties().setAlternateDescription("Hello2");
            return r;
        });
        framework.assertBothValid("testRadioButtonGroupInteractive");
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio r = new Radio("name", "group");
            r.setSize(20);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.getAccessibilityProperties().setAlternateDescription("Hello");
            r.setBackgroundColor(ColorConstants.GREEN);
            r.setInteractive(true);
            return r;
        });
        framework.addSuppliers(document -> {
            Radio r = new Radio("name2", "group");
            r.setSize(20);
            r.setInteractive(true);
            r.getAccessibilityProperties().setAlternateDescription("Hello2");
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.setBackgroundColor(ColorConstants.GREEN);
            return r;
        });
        framework.assertBothValid("radioBtnCustomAppInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearanceCheckedInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio r = new Radio("name", "group");
            r.setSize(20);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.getAccessibilityProperties().setAlternateDescription("Hello");
            r.setBackgroundColor(ColorConstants.GREEN);
            r.setInteractive(true);
            return r;
        });
        framework.addSuppliers(document -> {
            Radio r = new Radio("name2", "group");
            r.setSize(20);
            r.setChecked(true);
            r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            r.getAccessibilityProperties().setAlternateDescription("Hello2");
            r.setInteractive(true);
            r.setBackgroundColor(ColorConstants.GREEN);
            return r;
        });
        framework.assertBothValid("radioBtnCustomAppGrCheckedInteractive");
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testButton(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setValue("Click me");
            b.setFont(getFont());
            return b;
        });
        framework.assertBothValid("testButton");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setValue("Click me");
            b.setFont(getFont());
            b.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            b.setBackgroundColor(ColorConstants.GREEN);
            return b;
        });
        framework.assertBothValid("testButtonCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonSingleLine(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setFont(getFont());
            b.setSingleLineValue("Click me?");
            return b;
        });
        framework.assertBothValid("testButtonSingleLine");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContent(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            Paragraph p = new Paragraph("Click me?").setFont(getFont())
                    .setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            b.add(p);
            return b;
        });
        framework.assertBothValid("testButtonCustomContent");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContentIsAlsoForm(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            CheckBox cb = new CheckBox("name2");
            cb.setChecked(true);
            b.add(cb);
            return b;
        });
        framework.assertBothValid("testButtonCustomContentIsAlsoForm");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setValue("Click me");
            b.setFont(getFont());

            b.setInteractive(true);
            b.getAccessibilityProperties().setAlternateDescription("Click me button");
            return b;
        });
        framework.assertBothValid("testButtonInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setValue("Click me");
            b.setFont(getFont());
            b.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            b.setInteractive(true);
            b.setBackgroundColor(ColorConstants.GREEN);

            b.getAccessibilityProperties().setAlternateDescription("Click me button");
            return b;
        });
        framework.assertBothValid("testButtonCustomAppearanceInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonSingleLineInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setFont(getFont());
            b.setSingleLineValue("Click me?");

            b.getAccessibilityProperties().setAlternateDescription("Click me button");
            b.setInteractive(true);
            return b;
        });
        framework.assertBothValid("testButtonSingleLineInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContentInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            Paragraph p = new Paragraph("Click me?").setFont(getFont())
                    .setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            b.add(p);
            b.setFont(getFont());
            b.getAccessibilityProperties().setAlternateDescription("Click me button");
            b.setInteractive(true);
            return b;
        });
        framework.assertBothValid("testButtonCustomContentInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContentIsAlsoFormInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setFont(getFont());
            CheckBox cb = new CheckBox("name2");
            cb.setChecked(true);
            cb.setInteractive(true);
            b.add(cb);
            b.setInteractive(true);
            b.getAccessibilityProperties().setAlternateDescription("Click me button");
            cb.getAccessibilityProperties().setAlternateDescription("Check me checkbox");
            return b;
        });
        framework.assertBothValid("testButtonCustomContentIsAlsoFormInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputField(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            return inputField;
        });
        framework.assertBothValid("testInputField");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithValue(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            inputField.setValue("Hello");
            return inputField;
        });
        framework.assertBothValid("testInputFieldWithValue");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            inputField.setBackgroundColor(ColorConstants.GREEN);
            inputField.setFont(getFont());
            return inputField;
        });
        framework.assertBothValid("testInputFieldWithCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndValue(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            inputField.setBackgroundColor(ColorConstants.GREEN);
            inputField.setFont(getFont());
            inputField.setValue("Hello");
            return inputField;
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndValue");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndPlaceHolder(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            inputField.setBackgroundColor(ColorConstants.GREEN);
            inputField.setFont(getFont());
            inputField.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
            return inputField;
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndPlaceHolder");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            inputField.setInteractive(true);
            inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return inputField;
        });
        framework.assertBothValid("testInputFieldInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithValueInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            inputField.setValue("Hello");
            inputField.setInteractive(true);
            inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return inputField;
        });
        framework.assertBothValid("testInputFieldWithValueInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            inputField.setBackgroundColor(ColorConstants.GREEN);
            inputField.setFont(getFont());
            inputField.setInteractive(true);
            inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return inputField;
        });
        framework.assertBothValid("inputFieldCustomAppInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndValueInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            inputField.setBackgroundColor(ColorConstants.GREEN);
            inputField.setFont(getFont());
            inputField.setValue("Hello");
            inputField.setInteractive(true);
            inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return inputField;
        });
        framework.assertBothValid("inputFieldCustomAppValueInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndPlaceHolderInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            inputField.setBackgroundColor(ColorConstants.GREEN);
            inputField.setFont(getFont());
            inputField.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
            inputField.setInteractive(true);
            inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return inputField;
        });
        framework.assertBothValid("inpFieldCustomAppPlaceholderInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextArea(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            return textArea;
        });
        framework.assertBothValid("testTextArea");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithValue(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            textArea.setValue("Hello");
            return textArea;
        });
        framework.assertBothValid("testTextAreaWithValue");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            textArea.setBackgroundColor(ColorConstants.GREEN);
            textArea.setFont(getFont());
            return textArea;
        });
        framework.assertBothValid("testTextAreaWithCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndValue(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            textArea.setBackgroundColor(ColorConstants.GREEN);
            textArea.setFont(getFont());
            textArea.setValue("Hello");
            return textArea;
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndValue");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndPlaceHolder(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            textArea.setBackgroundColor(ColorConstants.GREEN);
            textArea.setFont(getFont());
            textArea.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
            return textArea;
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndPlaceHolder");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            textArea.setInteractive(true);
            textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return textArea;
        });
        framework.assertBothValid("testTextAreaInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithValueInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            textArea.setValue("Hello");
            textArea.setInteractive(true);
            textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return textArea;
        });
        framework.assertBothValid("testTextAreaWithValueInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            textArea.setBackgroundColor(ColorConstants.GREEN);
            textArea.setFont(getFont());
            textArea.setInteractive(true);
            textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return textArea;
        });
        framework.assertBothValid("textAreaWithCustomAppearanceInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndValueInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            textArea.setBackgroundColor(ColorConstants.GREEN);
            textArea.setFont(getFont());
            textArea.setValue("Hello");
            textArea.setInteractive(true);
            textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return textArea;
        });
        framework.assertBothValid("textAreaCustomAppValueInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndPlaceHolderInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            textArea.setBackgroundColor(ColorConstants.GREEN);
            textArea.setFont(getFont());
            textArea.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
            textArea.setInteractive(true);
            textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
            return textArea;
        });
        framework.assertBothValid("textAreaCustomAppPlaceHolderInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBox(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setFont(getFont());
            list.addOption("value1");
            list.addOption("value2");
            return list;
        });
        framework.assertBothValid("testListBox");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.setFont(getFont());
            list.addOption("value1");
            list.addOption("value2");
            return list;
        });
        framework.assertBothValid("testListBoxCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearanceSelected(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.setFont(getFont());
            list.addOption("value1", true);
            list.addOption("value2");
            return list;
        });
        framework.assertBothValid("testListBoxCustomAppearanceSelected");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setFont(getFont());
            list.addOption("value1");
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.addOption("value2");
            list.setInteractive(true);
            return list;
        });
        framework.assertBothValid("testListBoxInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.setFont(getFont());
            list.setInteractive(true);
            list.addOption("value1");
            list.addOption("value2");
            return list;
        });
        framework.assertBothValid("testListBoxCustomAppearanceInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearanceSelectedInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.setFont(getFont());
            list.setInteractive(true);
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.addOption("value1", true);
            list.addOption("value2");
            return list;
        });
        framework.assertBothValid("listBoxCustomAppSelectedInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBox(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setFont(getFont());
            list.addOption(new SelectFieldItem("value1"));
            list.addOption(new SelectFieldItem("value2"));
            return list;
        });
        framework.assertBothValid("testComboBox");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.setFont(getFont());
            list.addOption(new SelectFieldItem("value1"));
            list.addOption(new SelectFieldItem("value2"));
            return list;
        });
        framework.assertBothValid("testComboBoxCustomAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearanceSelected(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.setFont(getFont());
            list.addOption(new SelectFieldItem("Value 1"), true);
            list.addOption(new SelectFieldItem("Value 1"), false);
            return list;
        });
        framework.assertBothValid("testComboBoxCustomAppearanceSelected");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setFont(getFont());
            list.addOption(new SelectFieldItem("Value 1"));
            list.addOption(new SelectFieldItem("Value 2"));
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.setInteractive(true);
            return list;
        });
        framework.assertBothValid("testComboBoxInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.setFont(getFont());
            list.setInteractive(true);
            list.addOption(new SelectFieldItem("Value 1"));
            list.addOption(new SelectFieldItem("Value 2"));
            return list;
        });
        framework.assertBothValid("comboBoxCustomAppearanceInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearanceSelectedInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setBackgroundColor(ColorConstants.GREEN);
            list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            list.setSize(200);
            list.setFont(getFont());
            list.setInteractive(true);
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.addOption(new SelectFieldItem("hello1"), true);
            list.addOption(new SelectFieldItem("hello1"), false);
            return list;
        });
        framework.assertBothValid("comboBoxCustomAppInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearance(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            appearance.setContent("Hello");
            return appearance;
        });
        framework.assertBothValid("testSignatureAppearance");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithSignedAppearanceText(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            SignedAppearanceText signedAppearanceText = new SignedAppearanceText();
            signedAppearanceText.setLocationLine("Location");
            signedAppearanceText.setSignedBy("Leelah");
            signedAppearanceText.setReasonLine("Cuz I can");
            appearance.setContent(signedAppearanceText);
            return appearance;
        });
        framework.assertBothValid("signatureAppearanceSignedAppearanceText");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithCustomContent(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            Div div = new Div();
            div.add(new Paragraph("Hello").setFont(getFont()));
            appearance.setContent(div);

            return appearance;
        });
        framework.assertBothValid("signatureAppearanceWithCustomContent");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithSignedAppearanceAndCustomAppearanceText(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            SignedAppearanceText signedAppearanceText = new SignedAppearanceText();
            signedAppearanceText.setLocationLine("Location");
            signedAppearanceText.setSignedBy("Leelah");
            signedAppearanceText.setReasonLine("Cuz I can");
            appearance.setContent(signedAppearanceText);
            appearance.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            appearance.setBackgroundColor(ColorConstants.GREEN);
            return appearance;
        });
        framework.assertBothValid("signAppSignedAppCustomAppText");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceInteractive(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            appearance.setContent("Hello");
            appearance.setInteractive(true);
            appearance.getAccessibilityProperties().setAlternateDescription("Hello");
            return appearance;
        });
        framework.assertBothValid("testSignatureAppearanceInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithSignedAppearanceTextInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            SignedAppearanceText signedAppearanceText = new SignedAppearanceText();
            signedAppearanceText.setLocationLine("Location");
            signedAppearanceText.setSignedBy("Leelah");
            signedAppearanceText.setReasonLine("Cuz I can");
            appearance.setContent(signedAppearanceText);
            appearance.setInteractive(true);
            appearance.getAccessibilityProperties().setAlternateDescription("Hello");
            return appearance;
        });
        framework.assertBothValid("signAppSignedTextInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithCustomContentInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            Div div = new Div();
            div.add(new Paragraph("Hello").setFont(getFont()));
            appearance.setContent(div);
            appearance.setInteractive(true);
            appearance.getAccessibilityProperties().setAlternateDescription("Hello");

            return appearance;
        });
        framework.assertBothValid("signedAppearanceTextInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignedAndCustomAppearanceTextInteractive(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            SignedAppearanceText signedAppearanceText = new SignedAppearanceText();
            signedAppearanceText.setLocationLine("Location");
            signedAppearanceText.setSignedBy("Leelah");
            signedAppearanceText.setReasonLine("Cuz I can");
            appearance.setContent(signedAppearanceText);
            appearance.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
            appearance.setBackgroundColor(ColorConstants.GREEN);
            appearance.setInteractive(true);
            appearance.getAccessibilityProperties().setAlternateDescription("Hello");
            return appearance;
        });
        framework.assertBothValid("signedCustomAppTextInteractive");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveCheckBoxNoAlternativeDescription(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setInteractive(true);
            return cb;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveCheckBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveCheckBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveRadioButtonNoAlternativeDescription(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name", "group");
            radio.setInteractive(true);
            return radio;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveRadioButtonNoAltDescr",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveRadioButtonNoAltDescr",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveButtonNoAlternativeDescription(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setInteractive(true);
            b.setFont(getFont());
            return b;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveButtonNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveButtonNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveInputFieldNoAlternativeDescription(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setInteractive(true);
            inputField.setFont(getFont());
            return inputField;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveInputFieldNoAltDescr",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveInputFieldNoAltDescr",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveTextAreaNoAlternativeDescription(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setInteractive(true);
            textArea.setFont(getFont());
            return textArea;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveTextAreaNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveTextAreaNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveListBoxNoAlternativeDescription(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setInteractive(true);
            list.setFont(getFont());
            return list;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveListBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveListBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveComboBoxNoAlternativeDescription(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setInteractive(true);
            list.setFont(getFont());
            return list;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveComboBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveComboBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveSignatureAppearanceNoAlternativeDescription(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setInteractive(true);
            appearance.setFont(getFont());
            return appearance;
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("interactiveSignAppearanceNoAltDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("interactiveSignAppearanceNoAltDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setPdfConformance(conformance);
            cb.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            cb.getAccessibilityProperties().setAlternateDescription("Hello");
            return cb;
        });
        framework.assertBothValid("testCheckBoxDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxArtifactRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setPdfConformance(conformance);
            cb.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            return cb;
        });
        framework.assertBothValid("testCheckBoxArtifactRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name1", "group");
            radio.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            radio.getAccessibilityProperties()
                    .setAlternateDescription("Radio " + "that " + "was " + "not " + "checked");
            return radio;
        });
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name2", "group");
            radio.setChecked(true);
            radio.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            radio.getAccessibilityProperties().setAlternateDescription("Radio that was not checked");
            return radio;
        });
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name3", "group");
            radio.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            return radio;
        });
        framework.assertBothValid("testRadioButtonDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonArtifactRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name1", "group");
            radio.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            radio.getAccessibilityProperties()
                    .setAlternateDescription("Radio that was not checked");
            return radio;
        });
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name2", "group");
            radio.setChecked(true);
            radio.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            radio.getAccessibilityProperties().setAlternateDescription("Radio that was not checked");
            return radio;
        });
        framework.addSuppliers(document -> {
            Radio radio = new Radio("name3", "group");
            radio.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            return radio;
        });
        framework.assertBothValid("testRadioButtonArtifactRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            b.setValue("Click me");
            b.getAccessibilityProperties().setAlternateDescription("Hello");
            b.setFont(getFont());
            return b;
        });
        framework.addSuppliers(document -> {
            Button b = new Button("name");
            b.setValue("Click me");
            b.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            b.setFont(getFont());
            return b;
        });
        framework.assertBothValid("testButtonDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            inputField.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            inputField.getAccessibilityProperties().setAlternateDescription("Hello");
            inputField.setValue("Hello");
            return inputField;
        });
        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            inputField.getAccessibilityProperties().setRole(StandardRoles.P);
            inputField.setValue("Hello");
            return inputField;
        });

        framework.addSuppliers(document -> {
            InputField inputField = new InputField("name");
            inputField.setFont(getFont());
            inputField.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            inputField.setValue("Hello");
            return inputField;
        });
        framework.assertBothValid("testInputFieldDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            textArea.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            textArea.getAccessibilityProperties().setAlternateDescription("Hello");
            return textArea;
        });
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            textArea.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            return textArea;
        });
        framework.addSuppliers(document -> {
            TextArea textArea = new TextArea("name");
            textArea.setFont(getFont());
            textArea.getAccessibilityProperties().setRole(StandardRoles.P);
            return textArea;
        });
        framework.assertBothValid("testTextAreaDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setFont(getFont());
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            list.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            return list;
        });
        framework.addSuppliers(document -> {
            ListBoxField list = new ListBoxField("name", 1, false);
            list.setFont(getFont());
            list.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            return list;
        });

        framework.assertBothValid("testListBoxDifferentRole");

    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setFont(getFont());
            list.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            list.addOption(new SelectFieldItem("value1"));
            list.addOption(new SelectFieldItem("value2"));
            list.getAccessibilityProperties().setAlternateDescription("Hello");
            return list;
        });

        framework.addSuppliers(document -> {
            ComboBoxField list = new ComboBoxField("name");
            list.setFont(getFont());
            list.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            return list;
        });
        framework.assertBothValid("testComboBoxDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceDifferentRole(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            appearance.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            appearance.setContent("Hello");
            appearance.getAccessibilityProperties().setAlternateDescription("Hello");
            return appearance;
        });

        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            appearance.setFont(getFont());
            appearance.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            appearance.setContent("Hello");
            return appearance;
        });
        framework.assertBothValid("testSignatureAppearanceDifferentRole");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextBuilderWithTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("testTextBuilderWithTu");
        } else {
            framework.assertBothFail("testTextBuilderWithTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextBuilderNoTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("testTextBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("testTextBuilderNoTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testChoiceBuilderWithTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfChoiceFormField field = new ChoiceFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createComboBox();
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("testChoiceBuilderWithTu");
        } else {
            framework.assertBothFail("testChoiceBuilderWithTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testChoiceBuilderNoTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfChoiceFormField field = new ChoiceFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createComboBox();
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("tesChoicetBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("tesChoicetBuilderNoTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonBuilderWithTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createPushButton();
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("testButtonBuilderWithTu");
        } else {
            framework.assertBothFail("testButtonBuilderWithTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonBuilderNoTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createPushButton();
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("testButtonBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("testButtonBuilderNoTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonBuilderNoTuNotVisible(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createPushButton();
            List<PdfFormAnnotation> annList = field.getChildFormAnnotations();
            annList.get(0).setVisibility(PdfFormAnnotation.HIDDEN);
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("testButtonBuilderNoTuNotVisible");
        } else {
            framework.assertBothFail("testButtonBuilderNoTuNotVisible",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonBuilderNoTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, "Radio");
            PdfButtonFormField radioGroup = builder.createRadioGroup();
            PdfFormAnnotation radioAnnotation = builder
                    .createRadioButton("AP", new Rectangle(100, 100, 100, 100));

            PdfFormAnnotation radioAnnotation2 = builder
                    .createRadioButton("AP2", new Rectangle(100, 200, 100, 100));

            radioGroup.addKid(radioAnnotation);
            radioGroup.addKid(radioAnnotation2);

            form.addField(radioGroup);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("testRadioButtonBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("testRadioButtonBuilderNoTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonBuilderWithTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, "Radio");
            PdfButtonFormField radioGroup = builder.createRadioGroup();
            PdfFormAnnotation radioAnnotation = builder
                    .createRadioButton("AP", new Rectangle(100, 100, 100, 100));

            PdfFormAnnotation radioAnnotation2 = builder
                    .createRadioButton("AP2", new Rectangle(100, 200, 100, 100));

            radioGroup.addKid(radioAnnotation);
            radioGroup.addKid(radioAnnotation2);

            radioGroup.setAlternativeName("Some radio group");

            form.addField(radioGroup);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("testRadioButtonBuilderWithTu");
        } else {
            framework.assertBothFail("testRadioButtonBuilderWithTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureBuilderWithTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfSignatureFormField field = new SignatureFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createSignature();
            field.setValue("some value");
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("testSignatureBuilderWithTu");
        } else {
            framework.assertBothFail("testSignatureBuilderWithTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureBuilderNoTu(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfSignatureFormField field = new SignatureFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createSignature();
            field.setValue("some value");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("testSignatureBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothFail("testSignatureBuilderNoTu",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testFormFieldWithAltEntry(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("FormFieldAltDescription");
        } else {
            framework.assertBothFail("FormFieldAltDescription",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testFormFieldWithContentsEntry(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            field.getFirstFormAnnotation().setAlternativeDescription("Some alt");
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("formFieldContentsDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        } else {
            framework.assertBothValid("formFieldContentsDescription");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testFormFieldAsStream(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfObject page = pdfDoc.addNewPage().getPdfObject();

            PdfStream streamObj = new PdfStream();
            streamObj.put(PdfName.Subtype, PdfName.Widget);
            streamObj.put(PdfName.T, new PdfString("hi"));
            streamObj.put(PdfName.TU, new PdfString("some text"));
            streamObj.put(PdfName.Contents, new PdfString("hello"));
            streamObj.put(PdfName.P, page);

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, streamObj);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.Form);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);
            PdfArray k = new PdfArray();
            k.add(objRef);
            parentDic.put(PdfName.K, k);

            if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
                pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(parentDic));
            } else {
                ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0)).addKid(new PdfStructElem(parentDic));
            }
        });

        framework.assertBothValid("FormFieldAsStream");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void severalWidgetKidsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfObject page = pdfDoc.addNewPage().getPdfObject();

            PdfStream streamObj = new PdfStream();
            streamObj.put(PdfName.Subtype, PdfName.Widget);
            streamObj.put(PdfName.T, new PdfString("hi"));
            streamObj.put(PdfName.TU, new PdfString("some text"));
            streamObj.put(PdfName.Contents, new PdfString("hello"));
            streamObj.put(PdfName.P, page);

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, streamObj);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.Form);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);

            PdfStructElem elem = new PdfStructElem(parentDic);
            elem.addKid(new PdfStructElem(objRef));
            elem.addKid(new PdfStructElem(objRef));
            elem.addKid(new PdfStructElem(objRef));

            if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
                pdfDoc.getStructTreeRoot().addKid(elem);
            } else {
                ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0)).addKid(elem);
            }
        });

        if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("severalWidgetKids", PdfUAExceptionMessageConstants
                    .FORM_STRUCT_ELEM_WITHOUT_ROLE_SHALL_CONTAIN_ONE_WIDGET);
        } else {
            framework.assertBothFail("severalWidgetKids",
                    PdfUAExceptionMessageConstants.FORM_STRUCT_ELEM_SHALL_CONTAIN_AT_MOST_ONE_WIDGET);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void severalWidgetKidsWithRoleTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfObject page = pdfDoc.addNewPage().getPdfObject();

            PdfStream streamObj = new PdfStream();
            streamObj.put(PdfName.Subtype, PdfName.Widget);
            streamObj.put(PdfName.T, new PdfString("hi"));
            streamObj.put(PdfName.TU, new PdfString("some text"));
            streamObj.put(PdfName.Contents, new PdfString("hello"));
            streamObj.put(PdfName.P, page);

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, streamObj);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.Form);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);

            PdfStructElem elem = new PdfStructElem(parentDic);
            elem.addKid(new PdfStructElem(objRef));
            elem.addKid(new PdfStructElem(objRef));
            elem.addKid(new PdfStructElem(objRef));

            PdfDictionary attributes = new PdfDictionary();
            attributes.put(PdfName.O, PdfStructTreeRoot.convertRoleToPdfName("PrintField"));
            attributes.put(PdfStructTreeRoot.convertRoleToPdfName("Role"), new PdfName("pb"));
            elem.setAttributes(attributes);

            if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_1) {
                pdfDoc.getStructTreeRoot().addKid(elem);
            } else {
                ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0)).addKid(elem);
            }
        });

        if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("severalWidgetKidsWithRole");
        } else {
            framework.assertBothFail("severalWidgetKidsWithRole",
                    PdfUAExceptionMessageConstants.FORM_STRUCT_ELEM_SHALL_CONTAIN_AT_MOST_ONE_WIDGET);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void widgetNeitherFormNorArtifactTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfDictionary page = pdfDoc.addNewPage().getPdfObject();

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.Contents, new PdfString("hello"));
            widget.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            widget.put(PdfName.P, page);
            widget.put(PdfName.StructParent, new PdfNumber(0));

            page.put(PdfName.Annots, new PdfArray(widget));

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, widget);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.P);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);
            parentDic.put(PdfName.K, objRef);

            ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0)).addKid(new PdfStructElem(parentDic));
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("widgetNeitherFormNorArtifact",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
        } else {
            // TODO DEVSIX-9580. VeraPDF claims the document to be valid, although it's not.
            //  We will need to update this test when veraPDF behavior is fixed and veraPDF version is updated.
            framework.assertOnlyITextFail("widgetNeitherFormNorArtifact",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void widgetNeitherFormNorArtifactInAcroformTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfDictionary page = pdfDoc.addNewPage().getPdfObject();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello").setFont(getFont()).createText();
            field.setValue("Some value");

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.Contents, new PdfString("hello"));
            widget.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            widget.put(PdfName.P, page);
            widget.put(PdfName.StructParent, new PdfNumber(0));
            widget.makeIndirect(pdfDoc);
            field.addKid(PdfFormCreator.createFormAnnotation(widget));
            form.addField(field);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(page, 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.P);
        });

        framework.assertBothFail("widgetNeitherFormNorArtifactInAcroform",
                PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void widgetIsArtifactInAcroformTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfDictionary page = pdfDoc.addNewPage().getPdfObject();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello").setFont(getFont()).createText();
            field.setValue("Some value");

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.Contents, new PdfString("hello"));
            widget.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            widget.put(PdfName.P, page);
            widget.put(PdfName.StructParent, new PdfNumber(0));
            widget.makeIndirect(pdfDoc);
            field.addKid(PdfFormCreator.createFormAnnotation(widget));
            form.addField(field);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(page, 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ARTIFACT);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothFail("widgetIsArtifactInAcroform",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
        } else {
            framework.assertBothValid("widgetIsArtifactInAcroform");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void widgetLabelNoContentsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfObject page = pdfDoc.addNewPage().getPdfObject();

            PdfStream streamObj = new PdfStream();
            streamObj.put(PdfName.Subtype, PdfName.Widget);
            streamObj.put(PdfName.T, new PdfString("hi"));
            streamObj.put(PdfName.TU, new PdfString("some text"));
            streamObj.put(PdfName.P, page);

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, streamObj);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.Form);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);
            parentDic.put(PdfName.K, objRef);

            PdfStructElem elem = new PdfStructElem(parentDic);
            elem.addKid(new PdfStructElem(pdfDoc, PdfName.Lbl));

            PdfDictionary attributes = new PdfDictionary();
            attributes.put(PdfName.O, PdfStructTreeRoot.convertRoleToPdfName("PrintField"));
            attributes.put(PdfStructTreeRoot.convertRoleToPdfName("Role"), new PdfName("pb"));
            elem.setAttributes(attributes);

            if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
                pdfDoc.getStructTreeRoot().addKid(elem);
            } else {
                ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0)).addKid(elem);
            }
        });

        framework.assertBothValid("widgetLabelNoContentsTest");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void additionalActionAndContentsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfObject page = pdfDoc.addNewPage().getPdfObject();

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.T, new PdfString("hi"));
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.Contents, new PdfString("hello"));
            widget.put(PdfName.AA, new PdfDictionary());
            widget.put(PdfName.P, page);

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, widget);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.Form);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);
            parentDic.put(PdfName.K, objRef);

            PdfStructElem elem = new PdfStructElem(parentDic);

            if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
                pdfDoc.getStructTreeRoot().addKid(elem);
            } else {
                ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0)).addKid(elem);
            }
        });

        framework.assertBothValid("additionalActionAndContents");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void additionalActionNoContentsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfPage page = pdfDoc.addNewPage();

            TagTreePointer p = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            p.addTag(StandardRoles.FORM);

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.T, new PdfString("hi"));
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.AA, new PdfDictionary());
            widget.put(PdfName.P, page.getPdfObject());

            page.addAnnotation(PdfAnnotation.makeAnnotation(widget));

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(page.getPdfObject(), 0);
            p = pdfDoc.getTagStructureContext().createPointerForStructElem((PdfStructElem) objRef.getParent());
            PdfDictionary attributes = new PdfDictionary();
            attributes.put(PdfName.O, PdfStructTreeRoot.convertRoleToPdfName("PrintField"));
            attributes.put(PdfStructTreeRoot.convertRoleToPdfName("Role"), new PdfName("pb"));
            p.getProperties().addAttributes(new PdfStructureAttributes(attributes));
            p.addTag(StandardRoles.LBL);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("additionalActionNoContents");
        } else {
            framework.assertBothFail("additionalActionNoContents",
                    PdfUAExceptionMessageConstants.WIDGET_WITH_AA_SHALL_PROVIDE_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void additionalActionNoContentsAcroformTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfDictionary page = pdfDoc.addNewPage().getPdfObject();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello").setFont(getFont()).createText();
            field.setValue("Some value");

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.AA, new PdfDictionary());
            widget.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            widget.put(PdfName.P, page);
            widget.put(PdfName.StructParent, new PdfNumber(0));
            widget.makeIndirect(pdfDoc);
            field.addKid(PdfFormCreator.createFormAnnotation(widget));
            field.setAlternativeName("Alt");
            form.addField(field);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(page, 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            PdfDictionary attributes = new PdfDictionary();
            attributes.put(PdfName.O, PdfStructTreeRoot.convertRoleToPdfName("PrintField"));
            attributes.put(PdfStructTreeRoot.convertRoleToPdfName("Role"), new PdfName("pb"));
            p.getProperties().addAttributes(new PdfStructureAttributes(attributes));
            p.addTag(StandardRoles.LBL);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("additionalActionNoContentsAcroform");
        } else {
            framework.assertBothFail("additionalActionNoContentsAcroform",
                    PdfUAExceptionMessageConstants.WIDGET_WITH_AA_SHALL_PROVIDE_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void noContentsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addAfterGenerationHook(pdfDoc -> {
            PdfPage page = pdfDoc.addNewPage();

            TagTreePointer p = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            p.addTag(StandardRoles.FORM);

            PdfDictionary widget = new PdfDictionary();
            widget.put(PdfName.Subtype, PdfName.Widget);
            widget.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            widget.put(PdfName.T, new PdfString("hi"));
            widget.put(PdfName.TU, new PdfString("some text"));
            widget.put(PdfName.P, page.getPdfObject());

            page.addAnnotation(PdfAnnotation.makeAnnotation(widget));
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("noContents");
        } else {
            framework.assertBothFail("noContents",
                    PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void textFieldRVAndVPositiveTest1(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();

            String value = "Red\rBlue\r";
            field.setValue(value);

            String richText = "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p style=\"color:#FF0000;\">Red&#13;</p>" +
                    "<p style=\"color:#1E487C;\">Blue&#13;</p></body>";
            field.setRichText(new PdfString(richText, PdfEncodings.PDF_DOC_ENCODING));

            field.getFirstFormAnnotation().setAlternativeDescription("alternate description");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        framework.assertBothValid("textFieldRVAndVPositiveTest1");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void textFieldRVAndVPositiveTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            field.setRichText(new PdfStream("<p>Some value</p>".getBytes(), CompressionConstants.NO_COMPRESSION));
            field.getFirstFormAnnotation().setAlternativeDescription("alternate description");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        framework.assertBothValid("textFieldRVAndVPositiveTest2");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void textFieldRVAndVPositiveTest3(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();

            String value = "\n\nThe following word\nis in bold.\n\n";
            field.setValue(value);

            String richText = "<field1>\n" +
                    "<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "<p>The following <span style=\"font-weight:bold\">word</span>\n" +
                    "is in bold.</p>\n" +
                    "</body>\n" +
                    "</field1>";
            field.setRichText(new PdfString(richText.getBytes(StandardCharsets.UTF_8)).setHexWriting(true));

            field.getFirstFormAnnotation().setAlternativeDescription("alternate description");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        framework.assertBothValid("textFieldRVAndVPositiveTest3");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void textFieldRVAndVNegativeTest1(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setRichText(new PdfString("<p>Some value</p>", PdfEncodings.UTF8));
            field.getFirstFormAnnotation().setAlternativeDescription("alternate description");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("textFieldRVAndVNegativeTest1");
        } else {
            framework.assertBothFail("textFieldRVAndVNegativeTest1",
                    PdfUAExceptionMessageConstants.TEXT_FIELD_V_AND_RV_SHALL_BE_TEXTUALLY_EQUIVALENT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void textFieldRVAndVNegativeTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            field.setRichText(new PdfStream("<p>Some different value</p>".getBytes(StandardCharsets.UTF_8),
                    CompressionConstants.NO_COMPRESSION));
            field.getFirstFormAnnotation().setAlternativeDescription("alternate description");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("textFieldRVAndVNegativeTest2");
        } else {
            framework.assertBothFail("textFieldRVAndVNegativeTest2",
                    PdfUAExceptionMessageConstants.TEXT_FIELD_V_AND_RV_SHALL_BE_TEXTUALLY_EQUIVALENT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void textFieldRVAndVNegativeTest3(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            field.setRichText(new PdfString("<p>Some different value</p>"));
            field.getFirstFormAnnotation().setAlternativeDescription("alternate description");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });

        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1)) {
            framework.assertBothValid("textFieldRVAndVNegativeTest3");
        } else {
            framework.assertBothFail("textFieldRVAndVNegativeTest3",
                    PdfUAExceptionMessageConstants.TEXT_FIELD_V_AND_RV_SHALL_BE_TEXTUALLY_EQUIVALENT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    // TODO DEVSIX-9023 Support "Signature fields" UA-2 rules
    public void signatureAppearanceWithImage(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            Div div = new Div();
            Image img;
            try {
                img = new Image(ImageDataFactory.create(DOG));
            } catch (MalformedURLException e) {
                throw new PdfException(e.getMessage());
            }
            div.add(img);
            appearance.setContent(div);
            appearance.setInteractive(true);
            appearance.setAlternativeDescription("Alternative Description");
            return appearance;
        });
        framework.assertBothValid("signatureAppearanceWithImage");
    }

    @ParameterizedTest
    @MethodSource("data")
    // TODO DEVSIX-9023 Support "Signature fields" UA-2 rules
    public void signatureAppearanceWithLineSeparator(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            Div div = new Div();
            LineSeparator line = new LineSeparator(new SolidLine(3));
            div.add(line);
            appearance.setContent(div);
            appearance.setInteractive(true);
            appearance.setAlternativeDescription("Alternative Description");
            return appearance;
        });
        framework.assertBothValid("signatureAppearanceLineSep");
    }

    @ParameterizedTest
    @MethodSource("data")
    // TODO DEVSIX-9023 Support "Signature fields" UA-2 rules
    public void signatureAppearanceBackgroundImage(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
            try {
                appearance.setFont(getFont());
                PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(DOG));
                BackgroundImage backgroundImage = new Builder().setImage(xObject).build();
                backgroundImage.getBackgroundSize().setBackgroundSizeToValues(UnitValue.createPointValue(100),
                        UnitValue.createPointValue(100));
                Div div = new Div();
                div.add(new Paragraph("Some text"));
                appearance.setContent(div).setFontSize(50)
                        .setBorder(new SolidBorder(ColorConstants.YELLOW, 10)).setHeight(200).setWidth(300);
                appearance.setBackgroundImage(backgroundImage);
                appearance.setAlternativeDescription("Alternative Description");
                appearance.setInteractive(true);
            } catch (MalformedURLException e) {
                throw new PdfException(e.getMessage());
            }
            return appearance;
        });
        framework.assertBothValid("signatureAppearanceBackgroundImage");
    }

    private PdfFont getFont() {
        try {
            return PdfFontFactory.createFont(FONT);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }
}
