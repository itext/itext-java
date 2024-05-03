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
import com.itextpdf.forms.fields.*;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.ComboBoxField;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUAFormFieldsTest extends ExtendedITextTest {

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATest/PdfUAFormFieldTest/";

    private UaValidationTestFramework framework;

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void setUp() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER, false);
    }

    @Test
    public void testCheckBox() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                return new CheckBox("name");
            }
        });
        framework.assertBothValid("testCheckBox.pdf");
    }

    @Test
    public void testCheckBoxWithCustomAppearance() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
                cb.setBackgroundColor(ColorConstants.YELLOW);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxWithCustomAppearance.pdf");
    }

    @Test
    public void testCheckBoxChecked() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.setChecked(true);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBox");
    }

    @Test
    public void testCheckBoxCheckedAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.getAccessibilityProperties().setAlternateDescription("Yello");
                cb.setChecked(true);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxCheckedAlternativeDescription");
    }

    @Test
    public void testCheckBoxCheckedCustomAppearance() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.setChecked(true);
                cb.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                cb.setBackgroundColor(ColorConstants.GREEN);
                cb.setCheckBoxType(CheckBoxType.STAR);
                cb.setSize(20);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxCheckedCustomAppearance");
    }

    @Test
    public void testCheckBoxInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
                checkBox.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
                return checkBox;
            }
        });
        framework.assertBothValid("testCheckBoxInteractive");
    }

    @Test
    public void testCheckBoxInteractiveCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
                checkBox.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
                checkBox.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                checkBox.setBackgroundColor(ColorConstants.GREEN);
                checkBox.setSize(20);
                checkBox.setCheckBoxType(CheckBoxType.SQUARE);
                return checkBox;
            }
        });
        framework.assertBothValid("testCheckBoxInteractiveCustomAppearance");
    }

    @Test
    public void testCheckBoxInteractiveCustomAppearanceChecked() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
                checkBox.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
                checkBox.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                checkBox.setBackgroundColor(ColorConstants.GREEN);
                checkBox.setSize(20);
                checkBox.setChecked(true);
                checkBox.setCheckBoxType(CheckBoxType.SQUARE);
                return checkBox;
            }
        });
        framework.assertBothValid("testCheckBoxInteractiveCustomAppearanceChecked");
    }

    @Test
    public void testRadioButton() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                return new Radio("name");
            }
        });
        framework.assertBothValid("testRadioButton");
    }

    @Test
    public void testRadioButtonChecked() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name");
                radio.setChecked(true);
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonChecked");
    }

    @Test
    public void testRadioButtonCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name");
                radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                radio.setBackgroundColor(ColorConstants.GREEN);
                radio.setSize(20);
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonCustomAppearance");
    }

    @Test
    public void testRadioButtonCustomAppearanceChecked() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name");
                radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                radio.setBackgroundColor(ColorConstants.GREEN);
                radio.setSize(20);
                radio.setChecked(true);
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonCustomAppearanceChecked");
    }

    @Test
    public void testRadioButtonGroup() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                return new Radio("name", "group");
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                return new Radio("name2", "group");
            }
        });
        framework.assertBothValid("testRadioButtonGroup");
    }


    @Test
    public void testRadioButtonGroupCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setSize(20);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.setBackgroundColor(ColorConstants.GREEN);
                return r;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name2", "group");
                r.setSize(20);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.setBackgroundColor(ColorConstants.GREEN);
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonGroup");
    }

    @Test
    public void testRadioButtonGroupCustomAppearanceChecked() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setSize(20);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.setBackgroundColor(ColorConstants.GREEN);
                return r;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name2", "group");
                r.setSize(20);
                r.setChecked(true);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.setBackgroundColor(ColorConstants.GREEN);
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonGroupCustomAppearanceChecked");
    }


    @Test
    public void testRadioButtonInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setInteractive(true);
                r.getAccessibilityProperties().setAlternateDescription("Hello");
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonInteractive");
    }

    @Test
    public void testRadioButtonCheckedInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.setInteractive(true);
                radio.setChecked(true);
                radio.getAccessibilityProperties().setAlternateDescription("Hello");
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonChecked");
    }

    @Test
    public void testRadioButtonCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                radio.setBackgroundColor(ColorConstants.GREEN);
                radio.setSize(20);
                radio.setInteractive(true);
                radio.getAccessibilityProperties().setAlternateDescription("Hello");
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonCustomAppearance");
    }

    @Test
    public void testRadioButtonCustomAppearanceCheckedInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "Group");
                radio.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                radio.setBackgroundColor(ColorConstants.GREEN);
                radio.setSize(20);
                radio.setChecked(true);
                radio.getAccessibilityProperties().setAlternateDescription("Hello");
                radio.setInteractive(true);
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonCustomAppearanceCheckedInteractive");
    }

    @Test
    public void testRadioButtonGroupInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setInteractive(true);
                r.getAccessibilityProperties().setAlternateDescription("Hello");
                return r;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name2", "group");
                r.setInteractive(true);
                r.getAccessibilityProperties().setAlternateDescription("Hello2");
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonGroupInteractive");
    }


    @Test
    public void testRadioButtonGroupCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setSize(20);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.getAccessibilityProperties().setAlternateDescription("Hello");
                r.setBackgroundColor(ColorConstants.GREEN);
                r.setInteractive(true);
                return r;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name2", "group");
                r.setSize(20);
                r.setInteractive(true);
                r.getAccessibilityProperties().setAlternateDescription("Hello2");
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.setBackgroundColor(ColorConstants.GREEN);
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonGroupInteractive");
    }

    @Test
    public void testRadioButtonGroupCustomAppearanceCheckedInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setSize(20);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.getAccessibilityProperties().setAlternateDescription("Hello");
                r.setBackgroundColor(ColorConstants.GREEN);
                r.setInteractive(true);
                return r;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name2", "group");
                r.setSize(20);
                r.setChecked(true);
                r.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                r.getAccessibilityProperties().setAlternateDescription("Hello2");
                r.setInteractive(true);
                r.setBackgroundColor(ColorConstants.GREEN);
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonGroupCustomAppearanceCheckedInteractive");
    }


    @Test
    public void testButton() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setValue("Click me");
                b.setFont(getFont());
                return b;
            }
        });
        framework.assertBothValid("testButton");
    }

    @Test
    public void testButtonCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setValue("Click me");
                b.setFont(getFont());
                b.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                b.setBackgroundColor(ColorConstants.GREEN);
                return b;
            }
        });
        framework.assertBothValid("testButtonCustomAppearance");
    }

    @Test
    public void testButtonSingleLine() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setFont(getFont());
                b.setSingleLineValue("Click me?");
                return b;
            }
        });
        framework.assertBothValid("testButtonSingleLine");
    }

    @Test
    public void testButtonCustomContent() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                Paragraph p = new Paragraph("Click me?").setFont(getFont())
                        .setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                b.add(p);
                return b;
            }
        });
        framework.assertBothValid("testButtonSingleLine");
    }

    @Test
    public void testButtonCustomContentIsAlsoForm() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                CheckBox cb = new CheckBox("name2");
                cb.setChecked(true);
                b.add(cb);
                return b;
            }
        });
        framework.assertBothValid("testButtonSingleLine");
    }

    @Test
    public void testButtonInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setValue("Click me");
                b.setFont(getFont());

                b.setInteractive(true);
                b.getAccessibilityProperties().setAlternateDescription("Click me button");
                return b;
            }
        });
        framework.assertBothValid("testButtonInteractive");
    }

    @Test
    public void testButtonCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setValue("Click me");
                b.setFont(getFont());
                b.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                b.setInteractive(true);
                b.setBackgroundColor(ColorConstants.GREEN);

                b.getAccessibilityProperties().setAlternateDescription("Click me button");
                return b;
            }
        });
        framework.assertBothValid("testButtonCustomAppearanceInteractive");
    }

    @Test
    public void testButtonSingleLineInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setFont(getFont());
                b.setSingleLineValue("Click me?");

                b.getAccessibilityProperties().setAlternateDescription("Click me button");
                b.setInteractive(true);
                return b;
            }
        });
        framework.assertBothValid("testButtonSingleLineInteractive");
    }

    @Test
    public void testButtonCustomContentInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                Paragraph p = new Paragraph("Click me?").setFont(getFont())
                        .setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                b.add(p);
                b.setFont(getFont());
                b.getAccessibilityProperties().setAlternateDescription("Click me button");
                b.setInteractive(true);
                return b;
            }
        });
        framework.assertBothValid("testButtonSingleLineInteractive");
    }

    @Test
    public void testButtonCustomContentIsAlsoFormInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testButtonSingleLineInteractive");
    }

    @Test
    public void testInputField() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                return inputField;
            }
        });
        framework.assertBothValid("testInputField");
    }

    @Test
    public void testInputFieldWithValue() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.setValue("Hello");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithValue");
    }

    @Test
    public void testInputFieldWithCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                inputField.setBackgroundColor(ColorConstants.GREEN);
                inputField.setFont(getFont());
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithCustomAppearance");
    }

    @Test
    public void testInputFieldWithCustomAppearanceAndValue() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                inputField.setBackgroundColor(ColorConstants.GREEN);
                inputField.setFont(getFont());
                inputField.setValue("Hello");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndValue");
    }

    @Test
    public void testInputFieldWithCustomAppearanceAndPlaceHolder() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                inputField.setBackgroundColor(ColorConstants.GREEN);
                inputField.setFont(getFont());
                inputField.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndValue");
    }

    @Test
    public void testInputFieldInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.setInteractive(true);
                inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldInteractive");
    }

    @Test
    public void testInputFieldWithValueInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.setValue("Hello");
                inputField.setInteractive(true);
                inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithValueInteractive");
    }

    @Test
    public void testInputFieldWithCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                inputField.setBackgroundColor(ColorConstants.GREEN);
                inputField.setFont(getFont());
                inputField.setInteractive(true);
                inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceInteractive");
    }

    @Test
    public void testInputFieldWithCustomAppearanceAndValueInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                inputField.setBackgroundColor(ColorConstants.GREEN);
                inputField.setFont(getFont());
                inputField.setValue("Hello");
                inputField.setInteractive(true);
                inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndValueInteractive");
    }

    @Test
    public void testInputFieldWithCustomAppearanceAndPlaceHolderInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                inputField.setBackgroundColor(ColorConstants.GREEN);
                inputField.setFont(getFont());
                inputField.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
                inputField.setInteractive(true);
                inputField.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndPlaceHolderInteractive");
    }

    @Test
    public void testTextArea() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                return textArea;
            }
        });
        framework.assertBothValid("testTextArea");
    }

    @Test
    public void testTextAreaWithValue() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.setValue("Hello");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithValue");
    }

    @Test
    public void testTextAreaWithCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                textArea.setBackgroundColor(ColorConstants.GREEN);
                textArea.setFont(getFont());
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithCustomAppearance");
    }

    @Test
    public void testTextAreaWithCustomAppearanceAndValue() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                textArea.setBackgroundColor(ColorConstants.GREEN);
                textArea.setFont(getFont());
                textArea.setValue("Hello");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndValue");
    }

    @Test
    public void testTextAreaWithCustomAppearanceAndPlaceHolder() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                textArea.setBackgroundColor(ColorConstants.GREEN);
                textArea.setFont(getFont());
                textArea.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndValue");
    }

    @Test
    public void testTextAreaInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.setInteractive(true);
                textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaInteractive");
    }

    @Test
    public void testTextAreaWithValueInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.setValue("Hello");
                textArea.setInteractive(true);
                textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithValueInteractive");
    }

    @Test
    public void testTextAreaWithCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                textArea.setBackgroundColor(ColorConstants.GREEN);
                textArea.setFont(getFont());
                textArea.setInteractive(true);
                textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceInteractive");
    }

    @Test
    public void testTextAreaWithCustomAppearanceAndValueInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                textArea.setBackgroundColor(ColorConstants.GREEN);
                textArea.setFont(getFont());
                textArea.setValue("Hello");
                textArea.setInteractive(true);
                textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndValueInteractive");
    }

    @Test
    public void testTextAreaWithCustomAppearanceAndPlaceHolderInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                textArea.setBackgroundColor(ColorConstants.GREEN);
                textArea.setFont(getFont());
                textArea.setPlaceholder(new Paragraph("Placeholder").setFont(getFont()));
                textArea.setInteractive(true);
                textArea.getAccessibilityProperties().setAlternateDescription("Name of the cat");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndPlaceHolderInteractive");
    }

    @Test
    public void testListBox() throws IOException, InterruptedException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setFont(getFont());
                list.addOption("value1");
                list.addOption("value2");
                return list;
            }
        });
        framework.assertBothValid("testListBox");
    }

    @Test
    public void testListBoxCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setBackgroundColor(ColorConstants.GREEN);
                list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                list.setSize(200);
                list.setFont(getFont());
                list.addOption("value1");
                list.addOption("value2");
                return list;
            }
        });
        framework.assertBothValid("testListBoxCustomAppearance");
    }

    @Test
    public void testListBoxCustomAppearanceSelected() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setBackgroundColor(ColorConstants.GREEN);
                list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                list.setSize(200);
                list.setFont(getFont());
                list.addOption("value1", true);
                list.addOption("value2");
                return list;
            }
        });
        framework.assertBothValid("testListBoxCustomAppearanceSelected");
    }

    @Test
    public void testListBoxInteractive() throws IOException, InterruptedException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setFont(getFont());
                list.addOption("value1");
                list.getAccessibilityProperties().setAlternateDescription("Hello");
                list.addOption("value2");
                list.setInteractive(true);
                return list;
            }
        });
        framework.assertBothValid("testListBoxInteractive");
    }

    @Test
    public void testListBoxCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testListBoxCustomAppearanceInteractive");
    }

    @Test
    public void testListBoxCustomAppearanceSelectedInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testListBoxCustomAppearanceSelectedInteractive");
    }

    @Test
    public void testComboBox() throws IOException, InterruptedException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setFont(getFont());
                list.addOption(new SelectFieldItem("value1"));
                list.addOption(new SelectFieldItem("value2"));
                return list;
            }
        });
        framework.assertBothValid("testComboBox");
    }

    @Test
    public void testComboBoxCustomAppearance() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setBackgroundColor(ColorConstants.GREEN);
                list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                list.setSize(200);
                list.setFont(getFont());
                list.addOption(new SelectFieldItem("value1"));
                list.addOption(new SelectFieldItem("value2"));
                return list;
            }
        });
        framework.assertBothValid("testComboBoxCustomAppearance");
    }

    @Test
    public void testComboBoxCustomAppearanceSelected() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setBackgroundColor(ColorConstants.GREEN);
                list.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                list.setSize(200);
                list.setFont(getFont());
                list.addOption(new SelectFieldItem("Value 1"), true);
                list.addOption(new SelectFieldItem("Value 1"), false);
                return list;
            }
        });
        framework.assertBothValid("testListBoxCustomAppearanceSelected");
    }

    @Test
    public void testComboBoxInteractive() throws IOException, InterruptedException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setFont(getFont());
                list.addOption(new SelectFieldItem("Value 1"));
                list.addOption(new SelectFieldItem("Value 2"));
                list.getAccessibilityProperties().setAlternateDescription("Hello");
                list.setInteractive(true);
                return list;
            }
        });
        framework.assertBothValid("testComboBoxInteractive");
    }

    @Test
    public void testComboBoxCustomAppearanceInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testComboBoxCustomAppearanceInteractive");
    }

    @Test
    public void testComboBoxCustomAppearanceSelectedInteractive() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testComboBoxCustomAppearanceSelectedInteractive");
    }

    @Test
    public void testSignatureAppearance() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                appearance.setContent("Hello");
                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearance");
    }

    @Test
    public void testSignatureAppearanceWithSignedAppearanceText() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                SignedAppearanceText signedAppearanceText = new SignedAppearanceText();
                signedAppearanceText.setLocationLine("Location");
                signedAppearanceText.setSignedBy("Leelah");
                signedAppearanceText.setReasonLine("Cuz I can");
                appearance.setContent(signedAppearanceText);
                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearanceWithSignedAppearanceText");
    }

    @Test
    public void testSignatureAppearanceWithCustomContent() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                Div div = new Div();
                div.add(new Paragraph("Hello").setFont(getFont()));
                appearance.setContent(div);

                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearanceWithSignedAppearanceText");
    }

    @Test
    public void testSignatureAppearanceWithSignedAppearanceAndCustomAppearanceText() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testSignatureAppearanceWithSignedAppearanceAndCustomAppearanceText");
    }

    @Test
    public void testSignatureAppearanceInteractive() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                appearance.setContent("Hello");
                appearance.setInteractive(true);
                appearance.getAccessibilityProperties().setAlternateDescription("Hello");
                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearanceInteractive");
    }

    @Test
    public void testSignatureAppearanceWithSignedAppearanceTextInteractive() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });

        framework.assertBothValid("testSignatureAppearanceWithSignedAppearanceTextInteractive");
    }

    @Test
    public void testSignatureAppearanceWithCustomContentInteractive() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                Div div = new Div();
                div.add(new Paragraph("Hello").setFont(getFont()));
                appearance.setContent(div);
                appearance.setInteractive(true);
                appearance.getAccessibilityProperties().setAlternateDescription("Hello");

                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearanceWithSignedAppearanceTextInteractive");
    }

    @Test
    public void testSignedAndCustomAppearanceTextInteractive()
            throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("testSignedAndCustomAppearanceTextInteractive");
    }

    @Test
    public void testInteractiveCheckBoxNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.setInteractive(true);
                return cb;
            }
        });
        framework.assertBothFail("testInteractiveCheckBoxNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveRadioButtonNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.setInteractive(true);
                return radio;
            }
        });
        framework.assertBothFail("testInteractiveRadioButtonNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveButtonNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setInteractive(true);
                b.setFont(getFont());
                return b;
            }
        });
        framework.assertBothFail("testInteractiveButtonNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveInputFieldNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setInteractive(true);
                inputField.setFont(getFont());
                return inputField;
            }
        });
        framework.assertBothFail("testInteractiveInputFieldNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveTextAreaNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setInteractive(true);
                textArea.setFont(getFont());
                return textArea;
            }
        });
        framework.assertBothFail("testInteractiveTextAreaNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveListBoxNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setInteractive(true);
                list.setFont(getFont());
                return list;
            }
        });
        framework.assertBothFail("testInteractiveListBoxNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveComboBoxNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setInteractive(true);
                list.setFont(getFont());
                return list;
            }
        });
        framework.assertBothFail("testInteractiveComboBoxNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testInteractiveSignatureAppearanceNoAlternativeDescription() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setInteractive(true);
                appearance.setFont(getFont());
                return appearance;
            }
        });
        framework.assertBothFail("testInteractiveSignatureAppearanceNoAlternativeDescription",
                PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testCheckBoxDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                cb.getAccessibilityProperties().setAlternateDescription("Hello");
                return cb;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformanceLevel(PdfUAConformanceLevel.PDFUA_1);
                cb.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxDifferentRole");
    }

    @Test
    public void testRadioButtonDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                radio.getAccessibilityProperties()
                        .setAlternateDescription("Radio " + "that " + "was " + "not " + "checked");
                return radio;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.setChecked(true);
                radio.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                radio.getAccessibilityProperties().setAlternateDescription("Radio that was not checked");
                return radio;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonDifferentRole");
    }

    @Test
    public void testButtonDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                b.setValue("Click me");
                b.getAccessibilityProperties().setAlternateDescription("Hello");
                b.setFont(getFont());
                return b;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setValue("Click me");
                b.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                b.setFont(getFont());
                return b;
            }
        });
        framework.assertBothValid("testButtonDifferentRole");
    }

    @Test
    public void testInputFieldDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                inputField.getAccessibilityProperties().setAlternateDescription("Hello");
                inputField.setValue("Hello");
                return inputField;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.getAccessibilityProperties().setRole(StandardRoles.P);
                inputField.setValue("Hello");
                return inputField;
            }
        });

        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                inputField.setValue("Hello");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldDifferentRole");
    }

    @Test
    public void testTextAreaDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                textArea.getAccessibilityProperties().setAlternateDescription("Hello");
                return textArea;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                return textArea;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.getAccessibilityProperties().setRole(StandardRoles.P);
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaDifferentRole");

    }

    @Test
    public void testListBoxDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setFont(getFont());
                list.getAccessibilityProperties().setAlternateDescription("Hello");
                list.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                return list;
            }
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setFont(getFont());
                list.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                return list;
            }
        });

        framework.assertBothValid("testListBoxDifferentRole");

    }

    @Test
    public void testComboBoxDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setFont(getFont());
                list.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                list.addOption(new SelectFieldItem("value1"));
                list.addOption(new SelectFieldItem("value2"));
                list.getAccessibilityProperties().setAlternateDescription("Hello");
                return list;
            }
        });

        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setFont(getFont());
                list.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                return list;
            }
        });
        framework.assertBothValid("testComboBoxDifferentRole");
    }

    @Test
    public void testSignatureAppearanceDifferentRole() throws FileNotFoundException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                appearance.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                appearance.setContent("Hello");
                appearance.getAccessibilityProperties().setAlternateDescription("Hello");
                return appearance;
            }
        });

        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                appearance.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                appearance.setContent("Hello");
                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearanceDifferentRole");
    }

    @Test
    public void testTextBuilderWithTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createText();
            field.setValue("Some value");
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });
        framework.assertBothValid("testTextBuilderWithTu");
    }

    @Test
    public void testTextBuilderNoTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createText();
            field.setValue("Some value");
            form.addField(field);
        });
        framework.assertBothFail("testTextBuilderNoTu", PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testChoiceBuilderWithTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfChoiceFormField field = new ChoiceFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createComboBox();
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });
        framework.assertBothValid("testChoiceBuilderWithTu");
    }

    @Test
    public void testChoiceBuilderNoTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfChoiceFormField field = new ChoiceFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createComboBox();
            form.addField(field);
        });
        framework.assertBothFail("tesChoicetBuilderNoTu", PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testButtonBuilderWithTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createPushButton();
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });
        framework.assertBothValid("testButtonBuilderWithTu");
    }

    @Test
    public void testButtonBuilderNoTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createPushButton();
            form.addField(field);
        });
        framework.assertBothFail("testButtonBuilderNoTu", PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testButtonBuilderNoTuNotVisible() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createPushButton();
            List<PdfFormAnnotation> annList = field.getChildFormAnnotations();
            annList.get(0).setVisibility(PdfFormAnnotation.HIDDEN);
            form.addField(field);
        });
        framework.assertBothValid("testButtonBuilderNoTuNotVisible");
    }

    @Test
    public void testRadioButtonBuilderNoTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
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
        framework.assertBothFail("testRadioButtonBuilderNoTu", PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testRadioButtonBuilderWithTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
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
        framework.assertBothValid("testRadioButtonBuilderWithTu");
    }

    @Test
    public void testSignatureBuilderWithTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfSignatureFormField field = new SignatureFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createSignature();
            field.setValue("some value");
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });
        framework.assertBothValid("testSignatureBuilderWithTu");
    }

    @Test
    public void testSignatureBuilderNoTu() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDoc) ->{
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfSignatureFormField field = new SignatureFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createSignature();
            field.setValue("some value");
            form.addField(field);
        });
        framework.assertBothFail("testSignatureBuilderNoTu", PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
    }

    @Test
    public void testFormFieldWithAltEntry() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc,"hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .setGenericConformanceLevel(PdfUAConformanceLevel.PDFUA_1)
                    .createText();
            field.setValue("Some value");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(
                    new DefaultAccessibilityProperties(StandardRoles.FORM)
                            .setAlternateDescription("alternate description"));
            form.addField(field);
        });
        framework.assertBothValid("FormFieldAltDescription");
    }

    @Test
    public void testFormFieldAsStream() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfObject page = pdfDoc.addNewPage().getPdfObject();

            PdfStream streamObj = new PdfStream();
            streamObj.put(PdfName.Subtype, PdfName.Widget);
            streamObj.put(PdfName.T, new PdfString("hi"));
            streamObj.put(PdfName.TU, new PdfString("some text"));
            streamObj.put(PdfName.P,  page);

            PdfDictionary objRef = new PdfDictionary();
            objRef.put(PdfName.Obj, streamObj);
            objRef.put(PdfName.Type, PdfName.OBJR);

            PdfDictionary parentDic = new PdfDictionary();
            parentDic.put(PdfName.P, pdfDoc.getStructTreeRoot().getPdfObject());
            parentDic.put(PdfName.S, PdfName.Form);
            parentDic.put(PdfName.Type, PdfName.StructElem);
            parentDic.put(PdfName.Pg, page);
            parentDic.put(PdfName.K, objRef);

            pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(parentDic));
        });
        framework.assertBothValid("FormFieldAsStream");
    }



    private PdfFont getFont() {
        try {
            return PdfFontFactory.createFont(FONT);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
