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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.form.renderer.checkboximpl.HtmlCheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.ICheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfACheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfCheckBoxRenderingStrategy;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CheckBoxTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/form/element/CheckBoxTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/form/element/CheckBoxTest/";
    private int counter = 0;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void before() {
        counter = 0;
    }


    @Test
    public void renderingModeDefaultValueTest() {
        CheckBox checkBoxPdf = new CheckBox("test");
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBoxPdf.getRenderer();
        Assertions.assertEquals(RenderingMode.DEFAULT_LAYOUT_MODE, rendererPdf.getRenderingMode());
    }

    @Test
    public void setRenderingModeReturnsToDefaultMode() {
        CheckBox checkBoxPdf = new CheckBox("test");
        checkBoxPdf.setProperty(Property.RENDERING_MODE, null);
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBoxPdf.getRenderer();
        Assertions.assertEquals(RenderingMode.DEFAULT_LAYOUT_MODE, rendererPdf.getRenderingMode());

        CheckBox checkBoxHtml = new CheckBox("test");
        checkBoxHtml.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        checkBoxHtml.setProperty(Property.RENDERING_MODE, null);
        CheckBoxRenderer rendererHtml = (CheckBoxRenderer) checkBoxHtml.getRenderer();
        Assertions.assertEquals(RenderingMode.DEFAULT_LAYOUT_MODE, rendererHtml.getRenderingMode());
    }

    @Test
    public void setRenderingModeTest() {
        CheckBox checkBoxPdf = new CheckBox("test");
        checkBoxPdf.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBoxPdf.getRenderer();
        Assertions.assertEquals(RenderingMode.HTML_MODE, rendererPdf.getRenderingMode());
    }

    @Test
    public void checkBoxSetCheckedTest() {
        CheckBox checkBox = new CheckBox("test");
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        Assertions.assertFalse(renderer.isBoxChecked());
        checkBox.setChecked(true);
        Assertions.assertTrue(renderer.isBoxChecked());
        checkBox.setChecked(false);
        Assertions.assertFalse(renderer.isBoxChecked());
    }

    @Test
    public void createCheckBoxFactoryDefaultPdfTest() {
        CheckBox checkBox = new CheckBox("test");
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assertions.assertTrue(strategy instanceof PdfCheckBoxRenderingStrategy);
    }

    @Test
    public void createCheckBoxFactoryPdfATest() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assertions.assertTrue(strategy instanceof PdfACheckBoxRenderingStrategy);
    }

    @Test
    public void createCheckBoxFactoryHtmlTest() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assertions.assertTrue(strategy instanceof HtmlCheckBoxRenderingStrategy);
    }

    @Test
    public void createCheckBoxFactoryHtmlWithPdfATest() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        checkBox.setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assertions.assertTrue(strategy instanceof HtmlCheckBoxRenderingStrategy);
    }

    @Test
    public void checkBoxInHtmlModeKeeps1on1RatioAndTakesMaxValue() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        final int height = 100;
        final int width = 50;
        checkBox.setProperty(Property.HEIGHT, UnitValue.createPointValue(height));
        checkBox.setProperty(Property.WIDTH, UnitValue.createPointValue(width));

        ParagraphRenderer renderer =
                (ParagraphRenderer) ((CheckBoxRenderer) checkBox.getRenderer()).createFlatRenderer();
        UnitValue heightUnitValue = renderer.getPropertyAsUnitValue(Property.HEIGHT);
        UnitValue widthUnitValue = renderer.getPropertyAsUnitValue(Property.WIDTH);

        Assertions.assertEquals(height, heightUnitValue.getValue(), 0);
        Assertions.assertEquals(height, widthUnitValue.getValue(), 0);
    }

    @Test
    public void basicCheckBoxDrawingTestHtmlMode() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicCheckBoxHtml.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicCheckBoxHtml.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            CheckBox checkBoxUnset = new CheckBox("test");
            checkBoxUnset.setBorder(new SolidBorder(ColorConstants.RED, 1));
            checkBoxUnset.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            document.add(checkBoxUnset);

            CheckBox checkBox = new CheckBox("test1");
            checkBox.setInteractive(true);
            checkBox.setBorder(new SolidBorder(ColorConstants.RED, 1));
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            document.add(checkBox);

            CheckBox checkBoxset = new CheckBox("test2");
            checkBoxset.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            checkBoxset.setBorder(new SolidBorder(ColorConstants.RED, 1));
            checkBoxset.setChecked(true);
            document.add(checkBoxset);

            CheckBox checkBoxsetAndInteractive = new CheckBox("test3");
            checkBoxsetAndInteractive.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            checkBoxsetAndInteractive.setBorder(new SolidBorder(ColorConstants.RED, 1));
            checkBoxsetAndInteractive.setChecked(true);
            checkBoxsetAndInteractive.setInteractive(true);
            document.add(checkBoxsetAndInteractive);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicCheckBoxDrawingTestPdfMode() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicCheckBoxPdf.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicCheckBoxPdf.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            //should be invisble because there is no default border
            CheckBox checkBoxUnset = new CheckBox("test");
            checkBoxUnset.setBorder(new SolidBorder(ColorConstants.RED, 1));
            document.add(checkBoxUnset);

            CheckBox checkBoxset = new CheckBox("test0");
            checkBoxset.setChecked(true);
            checkBoxset.setBorder(new SolidBorder(ColorConstants.RED, 1));
            document.add(checkBoxset);

            CheckBox checkBoxUnsetInteractive = new CheckBox("test1");
            checkBoxUnsetInteractive.setInteractive(true);
            checkBoxUnsetInteractive.setBorder(new SolidBorder(ColorConstants.RED, 1));
            document.add(checkBoxUnsetInteractive);

            CheckBox checkBoxsetInteractive = new CheckBox("test2");
            checkBoxsetInteractive.setInteractive(true);
            checkBoxsetInteractive.setChecked(true);
            checkBoxsetInteractive.setBorder(new SolidBorder(ColorConstants.RED, 1));
            document.add(checkBoxsetInteractive);

        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicCheckBoxDrawingTestPdfAMode() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicCheckBoxPdfA.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicCheckBoxPdfA.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            //should be invisble because there is no default border
            CheckBox checkBoxUnset = new CheckBox("test").setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
            document.add(checkBoxUnset);

            CheckBox checkBoxset = new CheckBox("test").setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
            checkBoxset.setChecked(true);
            document.add(checkBoxset);

            CheckBox checkBoxUnsetInteractive = new CheckBox("test1").setPdfConformanceLevel(
                    PdfAConformanceLevel.PDF_A_1B);
            checkBoxUnsetInteractive.setInteractive(true);
            document.add(checkBoxUnsetInteractive);

            CheckBox checkBoxsetInteractive = new CheckBox("test2").setPdfConformanceLevel(
                    PdfAConformanceLevel.PDF_A_1B);
            checkBoxsetInteractive.setInteractive(true);
            checkBoxsetInteractive.setChecked(true);
            document.add(checkBoxsetInteractive);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicCheckBoxSetSize() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBoxSetSize.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBoxSetSize.pdf";
        final int scaleFactor = 5;
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (int i = 1; i < 5; i++) {
                final int size = i * i * scaleFactor;
                generateCheckBoxesForAllRenderingModes(document, checkBox -> {
                    checkBox.setSize(size);
                });
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.INVALID_VALUE_FALLBACK_TO_DEFAULT,
            count = 8))
    public void basicCheckBoxSetSizeNegativeValueFallsBackToDefaultValue() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBoxSetSizeBadSize.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBoxSetSizeBadSize.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            generateCheckBoxes(document, checkBox -> {
                checkBox.setSize(0);
            });
            generateCheckBoxes(document, checkBox -> {
                checkBox.setSize(-1);
            });
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicCheckBoxSetBorderTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setBorder.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_setBorder.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            generateCheckBoxesForAllRenderingModes(document, checkBox -> {
                checkBox.setBorder(new SolidBorder(ColorConstants.GREEN, .5f));
            });
            generateCheckBoxesForAllRenderingModes(document, checkBox -> {
                checkBox.setBorder(new SolidBorder(ColorConstants.YELLOW, 1));
            });

            generateCheckBoxesForAllRenderingModes(document, checkBox -> {
                checkBox.setBorder(new SolidBorder(ColorConstants.BLUE, 3));
            });
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicCheckBoxSetBackgroundTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setBackground.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_setBackground.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            generateCheckBoxesForAllRenderingModes(document, checkBox -> {
                checkBox.setBackgroundColor(ColorConstants.MAGENTA);
            });
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void setBorderTestSmallDefaultsToMinValue() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setSmallBorder.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_setSmallBorder.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            final Float[] i = {.1f};
            document.add(new Paragraph("Non interactive"));
            for (int j = 0; j < 30; j++) {
                i[0] += .05f;
                CheckBox checkBox = new CheckBox("test" + j);
                checkBox.setBorder(new SolidBorder(ColorConstants.GREEN, (float) i[0]));
                document.add(checkBox);
            }
            final Float[] k = {.1f};
            document.add(new Paragraph("Interactive"));
            for (int j = 0; j < 30; j++) {
                k[0] += .05f;
                CheckBox checkBox = new CheckBox("test" + j);
                checkBox.setInteractive(true);
                checkBox.setBorder(new SolidBorder(ColorConstants.RED, (float) k[0]));
                document.add(checkBox);
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void checkBoxSetCheckTypes() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setCheckType.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_setCheckType.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (CheckBoxType enumConstant : EnumUtil.getAllValuesOfEnum(CheckBoxType.class)) {
                generateCheckBoxes(document, checkBox -> {
                    checkBox.setCheckBoxType(enumConstant);
                });
                generateCheckBoxes(document, checkBox -> {
                    checkBox.setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
                    checkBox.setCheckBoxType(enumConstant);
                });
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }


    @Test
    public void setPdfAConformanceLevel() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setConformanceLevel.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_setConformanceLevel.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (CheckBoxType enumConstant : EnumUtil.getAllValuesOfEnum(CheckBoxType.class)) {
                generateCheckBoxes(document, checkBox -> {
                    checkBox.setSize(20);
                    checkBox.setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_3B);
                    checkBox.setCheckBoxType(enumConstant);
                });
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void removingFormFieldsLeavesNoVisualTrace() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_removeFormField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_removeFormField.pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (Document document = new Document(new PdfDocument(new PdfWriter(baos)))) {
            generateCheckBoxesForAllRenderingModes(document, checkBox -> {
                checkBox.setBorder(new SolidBorder(ColorConstants.GREEN, 1f));
                checkBox.setBackgroundColor(ColorConstants.CYAN);
            });
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(bais), new PdfWriter(outPdf));
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, true);
        for (Entry<String, PdfFormField> entry : acroForm.getAllFormFields().entrySet()) {
            String key = entry.getKey();
            acroForm.removeField(key);
        }
        pdfDocument.close();
        // all non flattend acroform fields should be removed
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void checkBoxWithMarginsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBoxWithMargins.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBoxWithMargins.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div().setBackgroundColor(ColorConstants.PINK);
            CheckBox checkBox = new CheckBox("check");
            checkBox.setInteractive(true);
            checkBox.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(20));
            checkBox.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));
            checkBox.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(20));
            checkBox.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(20));
            checkBox.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 20))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setSize(100)
                    .setChecked(true);
            div.add(checkBox);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void borderBoxesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderBoxes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderBoxes.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // BORDER_BOX
            CheckBox interactiveCheckBox1 = new CheckBox("checkBox1")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 10))
                    .setSize(50)
                    .setChecked(false);
            interactiveCheckBox1.setInteractive(true);
            interactiveCheckBox1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(interactiveCheckBox1);

            // CONTENT_BOX
            CheckBox interactiveCheckBox2 = new CheckBox("checkBox2")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10))
                    .setSize(50)
                    .setChecked(true);
            interactiveCheckBox2.setInteractive(true);
            interactiveCheckBox2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(interactiveCheckBox2);

            // BORDER_BOX
            CheckBox flattenCheckBox1 = new CheckBox("checkBox3")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 10))
                    .setSize(50)
                    .setChecked(true);
            flattenCheckBox1.setInteractive(false);
            flattenCheckBox1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(flattenCheckBox1);

            // CONTENT_BOX
            CheckBox flattenCheckBox2 = new CheckBox("checkBox4")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10))
                    .setSize(50)
                    .setChecked(false);
            flattenCheckBox2.setInteractive(false);
            flattenCheckBox2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(flattenCheckBox2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void addFieldWithTwoWidgetsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "fieldWithTwoWidgets.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_fieldWithTwoWidgets.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // Create checkboxes using html element.
            CheckBox checkBox1 = new CheckBox("checkbox");
            checkBox1.setInteractive(true);
            checkBox1.setSize(100);
            checkBox1.setBackgroundColor(ColorConstants.YELLOW);
            checkBox1.setBorder(new SolidBorder(ColorConstants.PINK, 5));
            checkBox1.setChecked(true);
            document.add(checkBox1);

            // Add break to the end of the page.
            document.add(new AreaBreak());

            // Note that fields with the same fully qualified field name shall have the same
            // field type, value, and default value.
            CheckBox checkBox2 = new CheckBox("checkbox");
            checkBox2.setInteractive(true);
            checkBox2.setSize(200);
            checkBox2.setBackgroundColor(ColorConstants.PINK);
            checkBox2.setBorder(new SolidBorder(ColorConstants.YELLOW, 10));
            checkBox2.setChecked(true);
            document.add(checkBox2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void multiPageCheckboxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "multiPageCheckboxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_multiPageCheckBoxField.pdf";

        try (PdfDocument document = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
            for (int i = 0; i < 10; i++) {
                document.addNewPage();
                Rectangle rect = new Rectangle(210, 490, 150, 22);
                final PdfFormField inputField = new CheckBoxFormFieldBuilder(document, "fing").setWidgetRectangle(rect)
                        .createCheckBox();
                inputField.setValue("1");
                PdfPage page = document.getPage(i + 1);
                form.addField(inputField, page);
                if (i > 2) {
                    page.flush();
                }
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    private void generateCheckBoxesForAllRenderingModes(Document document, Consumer<CheckBox> alterFunction) {
        document.add(new Paragraph("Normal rendering mode"));
        generateCheckBoxes(document, (checkBox) -> {
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);
            alterFunction.accept(checkBox);
        });
        document.add(new Paragraph("Pdfa rendering mode"));
        generateCheckBoxes(document, (checkBox) -> {
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);
            checkBox.setPdfConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
            alterFunction.accept(checkBox);
        });

        document.add(new Paragraph("Html rendering mode"));
        generateCheckBoxes(document, (checkBox) -> {
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            alterFunction.accept(checkBox);
        });

    }

    @Test
    public void basicCheckBoxTagged() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicCheckboxTagged.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicCheckboxTagged.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().setTagged();
            generateCheckBoxes(document, checkBox -> {
            });
            generateCheckBoxes(document, checkBox -> {
            });
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void setBordersTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBoxSetBorders.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBoxSetBorders.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            final Float[] i = {.1f, .2f, .3f, .4f};
            document.add(new Paragraph("Test different borders"));
            for (int j = 0; j < 30; j++) {
                i[0] += .03f;
                i[1] += .05f;
                i[2] += .07f;
                i[3] += .09f;
                CheckBox checkBox = new CheckBox("test" + j);
                checkBox.setChecked(true);
                checkBox.setSize(20);
                if (j % 2 == 0) {
                    checkBox.setBorderRight(new SolidBorder(ColorConstants.GREEN, (float) i[0]));
                }
                if (j % 3 == 0) {
                    checkBox.setBorderLeft(new SolidBorder(ColorConstants.PINK, (float) i[1]));
                }
                if (j % 5 == 0 || j == 1 || j == 13 || j == 19 || j == 29) {
                    checkBox.setBorderTop(new SolidBorder(ColorConstants.CYAN, (float) i[2]));
                }
                if (j % 7 == 0 || j == 11 || j == 17 || j == 23) {
                    checkBox.setBorderBottom(new SolidBorder(ColorConstants.YELLOW, (float) i[3]));
                }
                document.add(checkBox);
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    private List<CheckBox> generateCheckBoxes(Document document, Consumer<CheckBox> alterFunction) {
        List<CheckBox> checkBoxList = new ArrayList<>();

        CheckBox formCheckbox = new CheckBox("checkbox_interactive_off_" + this.counter);
        formCheckbox.setInteractive(true);
        checkBoxList.add(formCheckbox);
        this.counter++;

        CheckBox flattenCheckbox = new CheckBox("checkbox_flatten_off_" + this.counter);
        checkBoxList.add(flattenCheckbox);
        this.counter++;

        CheckBox formCheckboxChecked = new CheckBox("checkbox_interactive_checked_" + this.counter);
        formCheckboxChecked.setInteractive(true);
        formCheckboxChecked.setChecked(true);
        checkBoxList.add(formCheckboxChecked);
        this.counter++;

        CheckBox flattenCheckboxChecked = new CheckBox("checkbox_flatten_checked_" + this.counter);
        flattenCheckboxChecked.setChecked(true);
        checkBoxList.add(flattenCheckboxChecked);
        this.counter++;

        for (CheckBox checkBox : checkBoxList) {
            alterFunction.accept(checkBox);
            document.add(checkBox);
        }

        return checkBoxList;
    }
}
