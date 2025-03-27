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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
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
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAFormFieldsTest extends ExtendedITextTest {

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUATest/PdfUAFormFieldTest/";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @BeforeEach
    public void setUp() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBox(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                return new CheckBox("name");
            }
        });
        framework.assertBothValid("testCheckBox", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxWithCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformance(PdfConformance.PDF_UA_1);
                cb.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
                cb.setBackgroundColor(ColorConstants.YELLOW);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxWithCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxChecked(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformance(PdfConformance.PDF_UA_1);
                cb.setChecked(true);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBox", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxCheckedAlternativeDescription(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformance(PdfConformance.PDF_UA_1);
                cb.getAccessibilityProperties().setAlternateDescription("Yello");
                cb.setChecked(true);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxCheckedAlternativeDescription", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxCheckedCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformance(PdfConformance.PDF_UA_1);
                cb.setChecked(true);
                cb.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                cb.setBackgroundColor(ColorConstants.GREEN);
                cb.setCheckBoxType(CheckBoxType.STAR);
                cb.setSize(20);
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxCheckedCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxInteractive(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
                checkBox.setPdfConformance(PdfConformance.PDF_UA_1);
                checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
                return checkBox;
            }
        });
        framework.assertBothValid("testCheckBoxInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxInteractiveCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
                checkBox.setPdfConformance(PdfConformance.PDF_UA_1);
                checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
                checkBox.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                checkBox.setBackgroundColor(ColorConstants.GREEN);
                checkBox.setSize(20);
                checkBox.setCheckBoxType(CheckBoxType.SQUARE);
                return checkBox;
            }
        });
        framework.assertBothValid("testCheckBoxInteractiveCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxInteractiveCustomAppearanceChecked(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox checkBox = (CheckBox) new CheckBox("name").setInteractive(true);
                checkBox.setPdfConformance(PdfConformance.PDF_UA_1);
                checkBox.getAccessibilityProperties().setAlternateDescription("Alternative description");
                checkBox.setBorder(new SolidBorder(ColorConstants.CYAN, 2));
                checkBox.setBackgroundColor(ColorConstants.GREEN);
                checkBox.setSize(20);
                checkBox.setChecked(true);
                checkBox.setCheckBoxType(CheckBoxType.SQUARE);
                return checkBox;
            }
        });
        framework.assertBothValid("checkBoxInteractiveCustomAppChecked", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButton(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                return new Radio("name");
            }
        });
        framework.assertBothValid("testRadioButton", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonChecked(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name");
                radio.setChecked(true);
                return radio;
            }
        });
        framework.assertBothValid("testRadioButtonChecked", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearanceChecked(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonCustomAppearanceChecked", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroup(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonGroup", pdfUAConformance);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonGroupCustom", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearanceChecked(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonGroupCustomAppearanceChecked", pdfUAConformance);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonInteractive(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio r = new Radio("name", "group");
                r.setInteractive(true);
                r.getAccessibilityProperties().setAlternateDescription("Hello");
                return r;
            }
        });
        framework.assertBothValid("testRadioButtonInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCheckedInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonChecked", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonCustomAppearanceCheckedInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("radioBtnCustomAppCheckedInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonGroupInteractive", pdfUAConformance);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonGroupInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonGroupCustomAppearanceCheckedInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("radioBtnCustomAppCheckedInteractive", pdfUAConformance);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void testButton(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setValue("Click me");
                b.setFont(getFont());
                return b;
            }
        });
        framework.assertBothValid("testButton", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonSingleLine(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setFont(getFont());
                b.setSingleLineValue("Click me?");
                return b;
            }
        });
        framework.assertBothValid("testButtonSingleLine", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContent(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonSingleLine", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContentIsAlsoForm(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonCustomContentIsAlsoForm", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonCustomAppearanceInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonSingleLineInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonSingleLineInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContentInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonSingleLineInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonCustomContentIsAlsoFormInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonSingleLineInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputField(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                return inputField;
            }
        });
        framework.assertBothValid("testInputField", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithValue(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setFont(getFont());
                inputField.setValue("Hello");
                return inputField;
            }
        });
        framework.assertBothValid("testInputFieldWithValue", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testInputFieldWithCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndValue(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndValue", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndPlaceHolder(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testInputFieldWithCustomAppearanceAndValue", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testInputFieldInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithValueInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testInputFieldWithValueInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("inputFieldCustomAppInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndValueInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("inputFieldCustomAppValueInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldWithCustomAppearanceAndPlaceHolderInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("inpFieldCustomAppPlaceholderInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextArea(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                return textArea;
            }
        });
        framework.assertBothValid("testTextArea", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithValue(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setFont(getFont());
                textArea.setValue("Hello");
                return textArea;
            }
        });
        framework.assertBothValid("testTextAreaWithValue", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testTextAreaWithCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndValue(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndValue", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndPlaceHolder(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testTextAreaWithCustomAppearanceAndValue", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testTextAreaInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithValueInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testTextAreaWithValueInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("textAreaWithCustomAppearanceInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndValueInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("textAreaCustomAppValueInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaWithCustomAppearanceAndPlaceHolderInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("textAreaCustomAppPlaceHolderInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBox(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testListBox", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testListBoxCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearanceSelected(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testListBoxCustomAppearanceSelected", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testListBoxInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testListBoxCustomAppearanceInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxCustomAppearanceSelectedInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("listBoxCustomAppSelectedInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBox(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testComboBox", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearance(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testComboBoxCustomAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearanceSelected(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testListBoxCustomAppearanceSelected", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testComboBoxInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("comboBoxCustomAppearanceInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxCustomAppearanceSelectedInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("comboBoxCustomAppInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearance(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setFont(getFont());
                appearance.setContent("Hello");
                return appearance;
            }
        });
        framework.assertBothValid("testSignatureAppearance", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithSignedAppearanceText(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("signatureAppearanceSignedAppearanceText", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithCustomContent(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("signatureAppearanceSignedAppearanceText", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithSignedAppearanceAndCustomAppearanceText(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("signAppSignedAppCustomAppText", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceInteractive(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testSignatureAppearanceInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithSignedAppearanceTextInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("signAppSignedTextInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceWithCustomContentInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("signedAppearanceTextInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignedAndCustomAppearanceTextInteractive(PdfUAConformance pdfUAConformance)
            throws IOException {
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
        framework.assertBothValid("signedCustomAppTextInteractive", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveCheckBoxNoAlternativeDescription(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setInteractive(true);
                return cb;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveCheckBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("interactiveCheckBoxNoAlternativeDescription",
                    MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "ZapfDingbats"),
                    pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveRadioButtonNoAlternativeDescription(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Radio radio = new Radio("name", "group");
                radio.setInteractive(true);
                return radio;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveRadioButtonNoAltDescr",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveRadioButtonNoAltDescr", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveButtonNoAlternativeDescription(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Button b = new Button("name");
                b.setInteractive(true);
                b.setFont(getFont());
                return b;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveButtonNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveButtonNoAlternativeDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveInputFieldNoAlternativeDescription(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                InputField inputField = new InputField("name");
                inputField.setInteractive(true);
                inputField.setFont(getFont());
                return inputField;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveInputFieldNoAltDescr",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveInputFieldNoAltDescr", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveTextAreaNoAlternativeDescription(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                TextArea textArea = new TextArea("name");
                textArea.setInteractive(true);
                textArea.setFont(getFont());
                return textArea;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveTextAreaNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveTextAreaNoAlternativeDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveListBoxNoAlternativeDescription(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ListBoxField list = new ListBoxField("name", 1, false);
                list.setInteractive(true);
                list.setFont(getFont());
                return list;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveListBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveListBoxNoAlternativeDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveComboBoxNoAlternativeDescription(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                ComboBoxField list = new ComboBoxField("name");
                list.setInteractive(true);
                list.setFont(getFont());
                return list;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveComboBoxNoAlternativeDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveComboBoxNoAlternativeDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInteractiveSignatureAppearanceNoAlternativeDescription(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                SignatureFieldAppearance appearance = new SignatureFieldAppearance("name");
                appearance.setInteractive(true);
                appearance.setFont(getFont());
                return appearance;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("interactiveSignAppearanceNoAltDescription",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("interactiveSignAppearanceNoAltDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformance(PdfConformance.PDF_UA_1);
                cb.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
                cb.getAccessibilityProperties().setAlternateDescription("Hello");
                return cb;
            }
        });
        framework.assertBothValid("testCheckBoxDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCheckBoxArtifactDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setPdfConformance(PdfConformance.PDF_UA_1);
                cb.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                return cb;
            }
        });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testCheckBoxArtifactRoleua1", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            //TODO DEVSIX-8974 Tagging formfield as artifact will put the inner content into bad places in tagstructure
            framework.assertBothFail("testCheckBoxArtifactRoleua2", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testRadioButtonDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testButtonDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInputFieldDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testInputFieldDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextAreaDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testTextAreaDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testListBoxDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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

        framework.assertBothValid("testListBoxDifferentRole", pdfUAConformance);

    }

    @ParameterizedTest
    @MethodSource("data")
    public void testComboBoxDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testComboBoxDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureAppearanceDifferentRole(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("testSignatureAppearanceDifferentRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextBuilderWithTu(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testTextBuilderWithTu", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testTextBuilderWithTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testTextBuilderNoTu(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createText();
            field.setValue("Some value");
            form.addField(field);
        });


        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("testTextBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION,
                    pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testTextBuilderNoTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testChoiceBuilderWithTu(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfChoiceFormField field = new ChoiceFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createComboBox();
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testChoiceBuilderWithTu", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testChoiceBuilderWithTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testChoiceBuilderNoTu(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfChoiceFormField field = new ChoiceFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createComboBox();
            form.addField(field);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("tesChoicetBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION,
                    pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("tesChoicetBuilderNoTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonBuilderWithTu(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createPushButton();
            field.setAlternativeName("Some tu entry value");
            form.addField(field);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testButtonBuilderWithTu", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testButtonBuilderWithTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonBuilderNoTu(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfButtonFormField field = new PushButtonFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createPushButton();
            form.addField(field);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("testButtonBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION,
                    pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testButtonBuilderNoTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testButtonBuilderNoTuNotVisible(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testButtonBuilderNoTuNotVisible", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testButtonBuilderNoTuNotVisible", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonBuilderNoTu(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("testRadioButtonBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testRadioButtonBuilderNoTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRadioButtonBuilderWithTu(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testRadioButtonBuilderWithTu", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testRadioButtonBuilderWithTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureBuilderWithTu(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("testSignatureBuilderWithTu", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testSignatureBuilderWithTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSignatureBuilderNoTu(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            PdfSignatureFormField field = new SignatureFormFieldBuilder(pdfDoc, "hello")
                    .setWidgetRectangle(new Rectangle(100, 100, 100, 100))
                    .setFont(getFont())
                    .createSignature();
            field.setValue("some value");
            form.addField(field);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("testSignatureBuilderNoTu",
                    PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("testSignatureBuilderNoTu", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testFormFieldWithAltEntry(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("FormFieldAltDescription", pdfUAConformance);
            // TODO DEVSIX-8242 PDF/UA-2 checks
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyVeraPdfFail("FormFieldAltDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testFormFieldAsStream(PdfUAConformance pdfUAConformance) throws IOException {
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

            pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(parentDic));
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("FormFieldAsStream", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "StructTreeRoot", "Form");
            framework.assertBothFail("FormFieldAsStream", message, pdfUAConformance);
        }
    }

    private PdfFont getFont() {
        try {
            return PdfFontFactory.createFont(FONT);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
