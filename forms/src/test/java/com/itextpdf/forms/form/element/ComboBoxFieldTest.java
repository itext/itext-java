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

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ComboBoxFieldTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ComboBoxFieldTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ComboBoxFieldTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void emptyComboBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyComboBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptyComboBoxField.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten empty combo box field");
            flattenComboBoxField.setInteractive(false);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            document.add(flattenComboBoxField);

            ComboBoxField comboBoxWithBorder = new ComboBoxField("with boderder");
            comboBoxWithBorder.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
            document.add(comboBoxWithBorder);

            ComboBoxField comboBoxWithBackgroundColor = new ComboBoxField("with background color");
            comboBoxWithBackgroundColor.setBackgroundColor(ColorConstants.GREEN);
            comboBoxWithBackgroundColor.setInteractive(true);
            document.add(comboBoxWithBackgroundColor);

            ComboBoxField comboBoxWithBorderAndBackgroundColor = new ComboBoxField("with border");
            comboBoxWithBorderAndBackgroundColor.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
            comboBoxWithBorderAndBackgroundColor.setInteractive(true);
            document.add(comboBoxWithBorderAndBackgroundColor);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }


    @Test
    public void basicComboBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicComboBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicComboBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField formComboBoxField = new ComboBoxField("form combo box field");
            formComboBoxField.setInteractive(true);
            formComboBoxField.addOption(new SelectFieldItem("option 1"));
            formComboBoxField.addOption(new SelectFieldItem("option 2"));
            document.add(formComboBoxField);

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field");
            flattenComboBoxField.setInteractive(false);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            document.add(flattenComboBoxField);

            ComboBoxField formComboBoxFieldSelected = new ComboBoxField("form combo box field selected");
            formComboBoxFieldSelected.setInteractive(true);
            formComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
            formComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
            formComboBoxFieldSelected.setSelected("option 1");
            document.add(formComboBoxFieldSelected);

            ComboBoxField flattenComboBoxFieldSelected = new ComboBoxField("flatten combo box field selected");
            flattenComboBoxFieldSelected.setInteractive(false);
            flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxFieldSelected.setSelected("option 1");
            document.add(flattenComboBoxFieldSelected);

        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }


    @Test
    public void basicComboBoxFieldWithBordersTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicComboBoxBorderTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicComboBoxBorderTest.pdf";
        List<Border> borderList = new ArrayList<>();
        borderList.add(new SolidBorder(ColorConstants.RED, .7f));
        borderList.add(new SolidBorder(ColorConstants.GREEN, 1));
        borderList.add(new SolidBorder(ColorConstants.BLUE, 2));
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (int i = 0; i < borderList.size(); i++) {

                ComboBoxField formComboBoxField = new ComboBoxField("form combo box field" + i);
                formComboBoxField.setInteractive(true);

                SelectFieldItem option1 = new SelectFieldItem("option 1");
                formComboBoxField.addOption(option1);
                SelectFieldItem option2 = new SelectFieldItem("option 2");
                formComboBoxField.addOption(option2);
                formComboBoxField.setSelected(option1);

                formComboBoxField.setBorder(borderList.get(i));
                document.add(formComboBoxField);

                ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field" + i);
                flattenComboBoxField.setInteractive(false);
                SelectFieldItem option3 = new SelectFieldItem("option 1");
                flattenComboBoxField.addOption(option3);
                flattenComboBoxField.setSelected(option3);
                flattenComboBoxField.setBorder(borderList.get(i));
                SelectFieldItem option4 = new SelectFieldItem("option 2");
                flattenComboBoxField.addOption(option4);
                document.add(flattenComboBoxField);
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicComboBoxFieldWithBackgroundTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicComboBoxBackgroundTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicComboBoxBackgroundTest.pdf";
        List<Color> borderList = new ArrayList<>();
        borderList.add(ColorConstants.RED);
        borderList.add(ColorConstants.GREEN);
        borderList.add(ColorConstants.BLUE);
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (int i = 0; i < borderList.size(); i++) {

                ComboBoxField formComboBoxField = new ComboBoxField("form combo box field" + i);
                formComboBoxField.setInteractive(true);

                SelectFieldItem option1 = new SelectFieldItem("option 1");
                formComboBoxField.addOption(option1);
                SelectFieldItem option2 = new SelectFieldItem("option 2");
                formComboBoxField.addOption(option2);
                formComboBoxField.setSelected(option1);

                formComboBoxField.setBackgroundColor(borderList.get(i));
                document.add(formComboBoxField);

                ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field" + i);
                flattenComboBoxField.setInteractive(false);
                SelectFieldItem option3 = new SelectFieldItem("option 1");
                flattenComboBoxField.addOption(option3);
                flattenComboBoxField.setSelected(option3);
                flattenComboBoxField.setBackgroundColor(borderList.get(i));
                SelectFieldItem option4 = new SelectFieldItem("option 2");
                flattenComboBoxField.addOption(option4);
                document.add(flattenComboBoxField);
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithoutSelectionTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithoutSelection.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithoutSelection.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {

            ComboBoxField flattenComboBoxFieldWithFont = new ComboBoxField("flatten combo box field with font");
            flattenComboBoxFieldWithFont.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenComboBoxFieldWithFont.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxFieldWithFont.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxFieldWithFont.addOption(new SelectFieldItem("option 2"));
            document.add(flattenComboBoxFieldWithFont);

            ComboBoxField flattenComboBoxFieldWithPercentFont = new ComboBoxField(
                    "flatten combo box field with percent font");
            flattenComboBoxFieldWithPercentFont.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenComboBoxFieldWithPercentFont.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxFieldWithPercentFont.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxFieldWithPercentFont.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxFieldWithPercentFont.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(30));
            document.add(flattenComboBoxFieldWithPercentFont);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field with height");
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxField.setSelected("option 2");
            flattenComboBoxField.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            document.add(flattenComboBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithMinHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithMinHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithMinHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field with min height");
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxField.setSelected("option 2");
            flattenComboBoxField.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(100));
            document.add(flattenComboBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithMaxHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithMaxHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithMaxHeight.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field with max height");
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxField.setSelected("option 1");
            flattenComboBoxField.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(10));
            document.add(flattenComboBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldCannotFitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldCannotFit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldCannotFit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div();
            div.setWidth(UnitValue.createPointValue(400));
            div.setHeight(UnitValue.createPointValue(755));
            div.setBackgroundColor(ColorConstants.PINK);
            document.add(div);

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box cannot fit");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxField.setSelected("option 1");
            document.add(flattenComboBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithLangTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithLang.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithLang.pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box with lang");
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxField.setSelected("option 1");

            flattenComboBoxField.getAccessibilityProperties().setLanguage("random_lang");
            document.add(flattenComboBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void setFontSizeTest() throws IOException, InterruptedException {
        // test different font sizes
        String outPdf = DESTINATION_FOLDER + "comboBoxFontSizeTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFontSizeTest.pdf";
        Float[] fontSizes = {4F, 8F, 12F, 16F, 20F, 24F};

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (Float fontSize : fontSizes) {
                ComboBoxField formComboBoxFieldSelected = new ComboBoxField(
                        "form combo box field selected" + Math.round((float) fontSize));
                formComboBoxFieldSelected.setInteractive(true);
                formComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
                formComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
                formComboBoxFieldSelected.setFontSize((float) fontSize);
                formComboBoxFieldSelected.setSelected("option 1");
                document.add(formComboBoxFieldSelected);

                ComboBoxField flattenComboBoxFieldSelected = new ComboBoxField(
                        "flatten combo box field selected" + Math.round((float) fontSize));
                flattenComboBoxFieldSelected.setInteractive(false);
                flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
                flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
                flattenComboBoxFieldSelected.setFontSize((float) fontSize);
                flattenComboBoxFieldSelected.setSelected("option 1");
                document.add(flattenComboBoxFieldSelected);
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void nonSelectedInHtml2PdfSelectsFirstTest() throws IOException, InterruptedException {
        // test different font sizes
        String outPdf = DESTINATION_FOLDER + "nonSelectedInHtml2PdfSelectsFirst.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_nonSelectedInHtml2PdfSelectsFirst.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField formComboBoxFieldSelected = new ComboBoxField("form combo box field selected");
            formComboBoxFieldSelected.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            formComboBoxFieldSelected.setInteractive(true);
            formComboBoxFieldSelected.setWidth(150);
            formComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
            formComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
            document.add(formComboBoxFieldSelected);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void setFontColorTest() throws IOException, InterruptedException {
        // test different font sizes
        String outPdf = DESTINATION_FOLDER + "comboBoxFontColorTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFontColorTest.pdf";
        Color[] colors = {ColorConstants.GREEN, ColorConstants.RED, ColorConstants.BLUE, ColorConstants.YELLOW,
                ColorConstants.ORANGE, ColorConstants.PINK};

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            for (int i = 0; i < colors.length; i++) {
                Color color = colors[i];
                ComboBoxField formComboBoxFieldSelected = new ComboBoxField("form combo box field selected" + i);
                formComboBoxFieldSelected.setInteractive(true);
                formComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
                formComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
                formComboBoxFieldSelected.setFontColor(color);
                formComboBoxFieldSelected.setSelected("option 1");
                document.add(formComboBoxFieldSelected);

                ComboBoxField flattenComboBoxFieldSelected = new ComboBoxField("flatten combo box field selected" + i);
                flattenComboBoxFieldSelected.setInteractive(false);
                flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
                flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
                flattenComboBoxFieldSelected.setFontColor(color);
                flattenComboBoxFieldSelected.setSelected("option 1");
                document.add(flattenComboBoxFieldSelected);
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }


    @Test
    public void noneSelectedIsNullTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1"));
        comboBoxField.addOption(new SelectFieldItem("option 2"));

        Assertions.assertNull(comboBoxField.getSelectedOption());
    }

    @Test
    public void setSelectedByExportValueTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1"));
        comboBoxField.addOption(new SelectFieldItem("option 2"));
        comboBoxField.addOption(new SelectFieldItem("option 3"));

        comboBoxField.setSelected("option 1");
        Assertions.assertEquals("option 1", comboBoxField.getSelectedOption().getDisplayValue());
        Assertions.assertEquals("option 1", comboBoxField.getSelectedOption().getExportValue());

    }

    @Test
    public void setSelectedByDisplayValueTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1", "1"));
        comboBoxField.addOption(new SelectFieldItem("option 2", "2"));
        comboBoxField.addOption(new SelectFieldItem("option 3", "3"));

        comboBoxField.setSelected("1");
        Assertions.assertNull(comboBoxField.getSelectedOption());
    }

    @Test
    public void setSelectByDisplayValueTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1", "1"));
        comboBoxField.addOption(new SelectFieldItem("option 2", "2"));
        comboBoxField.addOption(new SelectFieldItem("option 3", "3"));

        comboBoxField.setSelected("option 1");
        Assertions.assertEquals("option 1", comboBoxField.getSelectedOption().getExportValue());
        Assertions.assertEquals("1", comboBoxField.getSelectedOption().getDisplayValue());
    }

    @Test
    public void setSelectedByIndexTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1"));
        comboBoxField.addOption(new SelectFieldItem("option 2"));
        comboBoxField.addOption(new SelectFieldItem("option 3"));

        comboBoxField.setSelected(1);
        Assertions.assertEquals("option 2", comboBoxField.getSelectedOption().getDisplayValue());
        Assertions.assertEquals("option 2", comboBoxField.getSelectedOption().getExportValue());
    }

    @Test
    public void setSelectedByIndexOutOfBoundsTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1"));
        comboBoxField.addOption(new SelectFieldItem("option 2"));
        comboBoxField.addOption(new SelectFieldItem("option 3"));

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> comboBoxField.setSelected(3));
    }

    @Test
    public void setSelectByIndexNegativeOutOfBoundsTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1"));
        comboBoxField.addOption(new SelectFieldItem("option 2"));
        comboBoxField.addOption(new SelectFieldItem("option 3"));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> comboBoxField.setSelected(-1));
    }

    @Test
    public void setBySelectFieldItem() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        SelectFieldItem option1 = new SelectFieldItem("option 1", "1");
        comboBoxField.addOption(option1);
        comboBoxField.addOption(new SelectFieldItem("option 2", "2"));
        comboBoxField.addOption(new SelectFieldItem("option 3", "3"));

        comboBoxField.setSelected(option1);
        Assertions.assertEquals("option 1", comboBoxField.getSelectedOption().getExportValue());
        Assertions.assertEquals("1", comboBoxField.getSelectedOption().getDisplayValue());
    }

    @Test
    public void setBySelectFieldItemNullTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1", "1"));
        comboBoxField.addOption(new SelectFieldItem("option 2", "2"));

        comboBoxField.setSelected((SelectFieldItem) null);
        Assertions.assertNull(comboBoxField.getSelectedOption());
    }

    @Test
    public void setBySelectFieldItemNotInOptionsTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1", "1"));
        comboBoxField.addOption(new SelectFieldItem("option 2", "2"));

        comboBoxField.setSelected(new SelectFieldItem("option 3", "3"));
        Assertions.assertNull(comboBoxField.getSelectedOption());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = FormsLogMessageConstants.DUPLICATE_EXPORT_VALUE, count = 1)
    })
    public void addingOptionsWithSameExportValuesLogsWarningTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1", "1"));
        comboBoxField.addOption(new SelectFieldItem("option 1", "2"));
        Assertions.assertEquals(2, comboBoxField.getOptions().size());
    }


    @Test
    public void addingWithDuplicateDisplayValueTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        comboBoxField.addOption(new SelectFieldItem("option 1", "1"));
        comboBoxField.addOption(new SelectFieldItem("option 2", "1"));
        Assertions.assertEquals(2, comboBoxField.getOptions().size());
    }

    @Test
    public void addingOptionWithNullExportValueTest() {
        ComboBoxField comboBoxField = new ComboBoxField("test");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> comboBoxField.addOption(new SelectFieldItem("option 1", (String) null)));
    }

    @Test
    public void basicComboBoxFieldTaggedTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicComboBoxFieldTagged.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicComboBoxFieldTagged.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().setTagged();
            ComboBoxField formComboBoxField = new ComboBoxField("form combo box field");
            formComboBoxField.setInteractive(true);
            formComboBoxField.addOption(new SelectFieldItem("option 1"));
            formComboBoxField.addOption(new SelectFieldItem("option 2"));
            document.add(formComboBoxField);

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field");
            flattenComboBoxField.setInteractive(false);
            flattenComboBoxField.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxField.addOption(new SelectFieldItem("option 2"));
            document.add(flattenComboBoxField);

            ComboBoxField formComboBoxFieldSelected = new ComboBoxField("form combo box field selected");
            formComboBoxFieldSelected.setInteractive(true);
            formComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
            formComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
            formComboBoxFieldSelected.setSelected("option 1");
            document.add(formComboBoxFieldSelected);

            ComboBoxField flattenComboBoxFieldSelected = new ComboBoxField("flatten combo box field selected");
            flattenComboBoxFieldSelected.setInteractive(false);
            flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 1"));
            flattenComboBoxFieldSelected.addOption(new SelectFieldItem("option 2"));
            flattenComboBoxFieldSelected.setSelected("option 1");
            document.add(flattenComboBoxFieldSelected);

        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }


}
