/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.commons.utils.ExperimentalFeatures;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.form.renderer.checkboximpl.HtmlCheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.ICheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfACheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfCheckBoxRenderingStrategy;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CheckBoxTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/form/element/CheckBoxTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/form/element/CheckBoxTest/";

    private boolean experimental = false;

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void beforeTest() {
        experimental = ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING;
        ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING = true;
    }

    @After
    public void afterTest() {
        ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING = experimental;
    }

    @Test
    public void renderingModeDefaultValueTest() {
        CheckBox checkBoxPdf = new CheckBox("test");
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBoxPdf.getRenderer();
        Assert.assertEquals(RenderingMode.DEFAULT_LAYOUT_MODE, rendererPdf.getRenderingMode());
    }

    @Test
    public void setRenderingModeReturnsToDefaultMode() {
        CheckBox checkBoxPdf = new CheckBox("test");
        checkBoxPdf.setProperty(Property.RENDERING_MODE, null);
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBoxPdf.getRenderer();
        Assert.assertEquals(RenderingMode.DEFAULT_LAYOUT_MODE, rendererPdf.getRenderingMode());

        CheckBox checkBoxHtml = new CheckBox("test");
        checkBoxHtml.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        checkBoxHtml.setProperty(Property.RENDERING_MODE, null);
        CheckBoxRenderer rendererHtml = (CheckBoxRenderer) checkBoxHtml.getRenderer();
        Assert.assertEquals(RenderingMode.DEFAULT_LAYOUT_MODE, rendererHtml.getRenderingMode());
    }

    @Test
    public void setRenderingModeTest() {
        CheckBox checkBoxPdf = new CheckBox("test");
        checkBoxPdf.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBoxPdf.getRenderer();
        Assert.assertEquals(RenderingMode.HTML_MODE, rendererPdf.getRenderingMode());
    }

    @Test
    public void checkBoxSetCheckedTest() {
        CheckBox checkBox = new CheckBox("test");
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        Assert.assertFalse(renderer.isBoxChecked());
        checkBox.setChecked(true);
        Assert.assertTrue(renderer.isBoxChecked());
        checkBox.setChecked(false);
        Assert.assertFalse(renderer.isBoxChecked());
    }

    @Test
    public void createCheckBoxFactoryDefaultPdfTest() {
        CheckBox checkBox = new CheckBox("test");
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assert.assertTrue(strategy instanceof PdfCheckBoxRenderingStrategy);
    }

    @Test
    public void createCheckBoxFactoryPdfATest() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assert.assertTrue(strategy instanceof PdfACheckBoxRenderingStrategy);
    }

    @Test
    public void createCheckBoxFactoryHtmlTest() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assert.assertTrue(strategy instanceof HtmlCheckBoxRenderingStrategy);
    }

    @Test
    public void createCheckBoxFactoryHtmlWithPdfATest() {
        CheckBox checkBox = new CheckBox("test");
        checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
        CheckBoxRenderer renderer = (CheckBoxRenderer) checkBox.getRenderer();
        ICheckBoxRenderingStrategy strategy = renderer.createCheckBoxRenderStrategy();
        Assert.assertTrue(strategy instanceof HtmlCheckBoxRenderingStrategy);
    }

    @Test
    public void isPdfATest() {
        CheckBox checkBox = new CheckBox("test");

        CheckBoxRenderer rendererPdf2 = (CheckBoxRenderer) checkBox.getRenderer();
        Assert.assertFalse(rendererPdf2.isPdfA());

        checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
        CheckBoxRenderer rendererPdf = (CheckBoxRenderer) checkBox.getRenderer();
        Assert.assertTrue(rendererPdf.isPdfA());

        checkBox.setPdfAConformanceLevel(null);
        CheckBoxRenderer rendererPdf1 = (CheckBoxRenderer) checkBox.getRenderer();
        Assert.assertFalse(rendererPdf1.isPdfA());
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

        Assert.assertEquals(height, heightUnitValue.getValue(), 0);
        Assert.assertEquals(height, widthUnitValue.getValue(), 0);
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
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicCheckBoxDrawingTestPdfAMode() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicCheckBoxPdfA.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicCheckBoxPdfA.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            //should be invisble because there is no default border
            CheckBox checkBoxUnset = new CheckBox("test").setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
            document.add(checkBoxUnset);

            CheckBox checkBoxset = new CheckBox("test").setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
            checkBoxset.setChecked(true);
            document.add(checkBoxset);

            CheckBox checkBoxUnsetInteractive = new CheckBox("test1").setPdfAConformanceLevel(
                    PdfAConformanceLevel.PDF_A_1B);
            checkBoxUnsetInteractive.setInteractive(true);
            document.add(checkBoxUnsetInteractive);

            CheckBox checkBoxsetInteractive = new CheckBox("test2").setPdfAConformanceLevel(
                    PdfAConformanceLevel.PDF_A_1B);
            checkBoxsetInteractive.setInteractive(true);
            checkBoxsetInteractive.setChecked(true);
            document.add(checkBoxsetInteractive);
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
            }, 0);
            generateCheckBoxes(document, checkBox -> {
                checkBox.setSize(-1);
            }, 1);
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void checkBoxSetCheckTypes() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setCheckType.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_setCheckType.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (CheckBoxType enumConstant : EnumUtil.getAllValuesOfEnum(CheckBoxType.class)) {
                generateCheckBoxes(document, checkBox -> {
                    checkBox.setCheckBoxType(enumConstant);
                }, 0);
                generateCheckBoxes(document, checkBox -> {
                    checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
                    checkBox.setCheckBoxType(enumConstant);
                }, 0);
            }
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void setPdfAConformanceLevel() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "checkBox_setConformanceLevel.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_checkBox_setConformanceLevel.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (CheckBoxType enumConstant : EnumUtil.getAllValuesOfEnum(CheckBoxType.class)) {
                generateCheckBoxes(document, checkBox -> {
                    checkBox.setSize(20);
                    checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_3B);
                    checkBox.setCheckBoxType(enumConstant);
                }, 0);
            }
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    private static void generateCheckBoxesForAllRenderingModes(Document document, Consumer<CheckBox> alterFunction) {
        document.add(new Paragraph("Normal rendering mode"));
        int ctr = 0;
        generateCheckBoxes(document, (checkBox) -> {
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);
            alterFunction.accept(checkBox);
        }, ctr++);

        document.add(new Paragraph("Pdfa rendering mode"));
        generateCheckBoxes(document, (checkBox) -> {
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);
            checkBox.setPdfAConformanceLevel(PdfAConformanceLevel.PDF_A_1B);
            alterFunction.accept(checkBox);
        }, ctr++);

        document.add(new Paragraph("Html rendering mode"));
        generateCheckBoxes(document, (checkBox) -> {
            checkBox.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            alterFunction.accept(checkBox);
        }, ctr++);
    }

    private static List<CheckBox> generateCheckBoxes(Document document, Consumer<CheckBox> alterFunction, int i) {
        List<CheckBox> checkBoxList = new ArrayList<>();

        CheckBox formCheckbox = new CheckBox("checkbox_interactive_off_" + i);
        formCheckbox.setInteractive(true);
        checkBoxList.add(formCheckbox);

        CheckBox flattenCheckbox = new CheckBox("checkbox_flatten_off_" + i);
        checkBoxList.add(flattenCheckbox);

        CheckBox formCheckboxChecked = new CheckBox("checkbox_interactive_checked_" + i);
        formCheckboxChecked.setInteractive(true);
        formCheckboxChecked.setChecked(true);
        checkBoxList.add(formCheckboxChecked);

        CheckBox flattenCheckboxChecked = new CheckBox("checkbox_flatten_checked_" + i);
        flattenCheckboxChecked.setChecked(true);
        checkBoxList.add(flattenCheckboxChecked);

        for (CheckBox checkBox : checkBoxList) {
            alterFunction.accept(checkBox);
            document.add(checkBox);
        }

        return checkBoxList;
    }
}
