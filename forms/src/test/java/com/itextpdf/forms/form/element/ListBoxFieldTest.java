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
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ListBoxFieldTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ListBoxFieldTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ListBoxFieldTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }
    
    @Test
    public void emptyListBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyListBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptyListBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField flattenListBoxField = new ListBoxField("flatten empty list box field", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            document.add(flattenListBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicListBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicListBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicListBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField formListBoxField = new ListBoxField("form list box field", 2, false);
            formListBoxField.setInteractive(true);
            formListBoxField.addOption("option 1", false);
            formListBoxField.addOption("option 2", true);
            document.add(formListBoxField);

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field", 2, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenListBoxField.addOption("option 1", false);
            flattenListBoxField.addOption("option 2", true);
            document.add(flattenListBoxField);

            Paragraph option3 = new Paragraph("option 3");
            option3.setProperty(FormProperty.FORM_FIELD_SELECTED, Boolean.TRUE);
            option3.setMargin(0);
            option3.setMultipliedLeading(2);

            ListBoxField flattenListBoxFieldWithMultipleSelection =
                    new ListBoxField("flatten list box field with multiple selection", 3, true);
            flattenListBoxFieldWithMultipleSelection.setInteractive(false);
            flattenListBoxFieldWithMultipleSelection.addOption("option 1", false);
            flattenListBoxFieldWithMultipleSelection.addOption("option 2", true);
            flattenListBoxFieldWithMultipleSelection.addOption(new SelectFieldItem("option 3", option3));
            document.add(flattenListBoxFieldWithMultipleSelection);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(
            messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 22))
    public void listBoxFieldWithFontSizeTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithFontSize.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithFontSize.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField formListBoxFieldWithFont = new ListBoxField("flatten list box field with font", 0, false);
            formListBoxFieldWithFont.setInteractive(true);
            formListBoxFieldWithFont.setBackgroundColor(ColorConstants.RED);
            formListBoxFieldWithFont.addOption("option 1");
            formListBoxFieldWithFont.addOption("option 2");
            formListBoxFieldWithFont.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
            formListBoxFieldWithFont.setFontSize(6);
            document.add(formListBoxFieldWithFont);

            document.add(new Paragraph("line break"));

            ListBoxField flattenListBoxFieldWithFont = new ListBoxField("flatten list box field with font", 0, false);
            flattenListBoxFieldWithFont.setInteractive(false);
            flattenListBoxFieldWithFont.setBackgroundColor(ColorConstants.RED);
            flattenListBoxFieldWithFont.addOption("option 1");
            flattenListBoxFieldWithFont.addOption("option 2");
            flattenListBoxFieldWithFont.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
            flattenListBoxFieldWithFont.setFontSize(6);
            document.add(flattenListBoxFieldWithFont);

            document.add(new Paragraph("line break"));

            ListBoxField flattenListBoxFieldWithPercentFont =
                    new ListBoxField("flatten list box field with percent font", 0, false);
            flattenListBoxFieldWithFont.setInteractive(false);
            flattenListBoxFieldWithPercentFont.setBackgroundColor(ColorConstants.RED);
            flattenListBoxFieldWithPercentFont.addOption("option 1");
            flattenListBoxFieldWithPercentFont.addOption("option 2");
            flattenListBoxFieldWithPercentFont.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
            flattenListBoxFieldWithPercentFont.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(6));
            document.add(flattenListBoxFieldWithPercentFont);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithMarginsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithMargins.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithMargins.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");
            option1.setMargin(4);

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, Boolean.TRUE);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");
            option2.setMargin(4);

            ListBoxField listBoxField = new ListBoxField("list box field with margins", 1, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption(new SelectFieldItem("option 1", option1));
            listBoxField.addOption(new SelectFieldItem("option 2", option2));
            document.add(listBoxField);

            document.add(new Paragraph("line break"));

            document.add(listBoxField);

            document.add(new Paragraph("line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("list box field with height", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption("option 1");
            listBoxField.addOption("option 2", true);
            listBoxField.setHeight(100);
            document.add(listBoxField);

            document.add(new Paragraph("line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithMinHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithMinHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithMinHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("list box field with height", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption("option 1");
            listBoxField.addOption("option 2", true);
            listBoxField.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(100));
            document.add(listBoxField);

            document.add(new Paragraph("line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2))
    public void listBoxFieldWithMaxHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithMaxHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithMaxHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("list box field with height", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption("option 1", false);
            listBoxField.addOption("option 2", true);
            listBoxField.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(25));
            document.add(listBoxField);

            document.add(new Paragraph("line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldCannotFitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldCannotFit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldCannotFit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div();
            div.setWidth(UnitValue.createPointValue(400));
            div.setHeight(UnitValue.createPointValue(740));
            div.setBackgroundColor(ColorConstants.PINK);
            document.add(div);
            
            ListBoxField listBoxField = new ListBoxField("list box field cannot fit", 0, false);
            listBoxField.setInteractive(true);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption("option 1", true);
            listBoxField.addOption("option 2");
            document.add(listBoxField);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldCannotFitByWidthTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldCannotFitByWidth.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldCannotFitByWidth.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, Boolean.TRUE);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField listBoxField = new ListBoxField("list box field cannot fit by width", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.setProperty(Property.WIDTH, UnitValue.createPointValue(600));
            listBoxField.setBorder(new SolidBorder(20));
            listBoxField.addOption(new SelectFieldItem("option 1", option1));
            listBoxField.addOption(new SelectFieldItem("option 2", option2));
            document.add(listBoxField);

            document.add(new Paragraph("Line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithLangTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithLang.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithLang.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().setTagged();
            ListBoxField listBoxField = new ListBoxField("list box field with lang", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption("option 1");
            listBoxField.addOption("option 2");
            listBoxField.getAccessibilityProperties().setLanguage("random_lang");
            document.add(listBoxField);

            document.add(new Paragraph("Line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void colorsBordersTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "colorsBorders.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_colorsBorders.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("coloured list box field with borders", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setBackgroundColor(ColorConstants.RED);
            listBoxField.addOption("option 1");
            listBoxField.addOption("option 2", true);
            listBoxField.setBorder(new DashedBorder(ColorConstants.BLUE, 3));
            listBoxField.setFontColor(ColorConstants.GREEN);
            document.add(listBoxField);

            document.add(new Paragraph("Line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void longListTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "longList.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_longList.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("long list box field", 4, true);
            listBoxField.setInteractive(false);
            listBoxField.addOption("option 1");
            listBoxField.addOption("option 2");
            listBoxField.addOption("option 3");
            listBoxField.addOption("option 4");
            listBoxField.addOption("option 5");
            listBoxField.addOption("option 6", true);
            listBoxField.addOption("option 7");
            listBoxField.addOption("option 8");
            listBoxField.addOption("option 9");
            listBoxField.addOption("very very very long long long option 10", true);
            listBoxField.addOption("option 11");

            document.add(listBoxField);

            document.add(new Paragraph("Line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void justificationTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "justification.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_justification.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("left box field", 0, false);
            listBoxField.setInteractive(false);
            listBoxField.setWidth(200);
            listBoxField.setTextAlignment(TextAlignment.LEFT);
            listBoxField.addOption("option 1");
            listBoxField.addOption("option 2", true);
            document.add(listBoxField);

            document.add(new Paragraph("Line break"));

            document.add(listBoxField.setInteractive(true));

            ListBoxField centerListBoxField = new ListBoxField("center box field", 0, false);
            centerListBoxField.setInteractive(false);
            centerListBoxField.setWidth(200);
            centerListBoxField.setTextAlignment(TextAlignment.CENTER);
            centerListBoxField.addOption("option 1");
            centerListBoxField.addOption("option 2", true);
            document.add(centerListBoxField);

            document.add(new Paragraph("Line break"));

            document.add(centerListBoxField.setInteractive(true));

            ListBoxField rightListBoxField = new ListBoxField("right box field", 0, false);
            rightListBoxField.setInteractive(false);
            rightListBoxField.setWidth(200);
            rightListBoxField.setTextAlignment(TextAlignment.RIGHT);
            rightListBoxField.addOption("option 1");
            rightListBoxField.addOption("option 2", true);
            document.add(rightListBoxField);

            document.add(new Paragraph("Line break"));

            document.add(rightListBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void exportValueTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "exportValue.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_exportValue.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField listBoxField = new ListBoxField("export value field", 0, true);
            listBoxField.setInteractive(false);
            listBoxField.setWidth(200);
            listBoxField.addOption(new SelectFieldItem("English"));
            listBoxField.addOption(new SelectFieldItem("German", "Deutch"), true);
            listBoxField.addOption(new SelectFieldItem("Italian", "Italiano"), true);
            document.add(listBoxField);

            document.add(new Paragraph("Line break"));

            document.add(listBoxField.setInteractive(true));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void invalidOptionsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "invalidOptions.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_invalidOptions.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            ListBoxField listBoxField = new ListBoxField("invalid", 0, true);
            listBoxField.setInteractive(true);
            listBoxField.setWidth(200);

            // Invalid options array here
            PdfArray option1 = new PdfArray();
            option1.add(new PdfString("English"));
            option1.add(new PdfString("English"));
            option1.add(new PdfString("English3"));
            PdfArray option2 = new PdfArray();
            option2.add(new PdfString("German"));
            option2.add(new PdfString("Deutch"));
            PdfArray option3 = new PdfArray();
            option3.add(new PdfString("Italian"));
            PdfArray options = new PdfArray();
            options.add(option1);
            options.add(option2);
            options.add(option3);
            options.add(new PdfArray());

            PdfChoiceFormField field = new ChoiceFormFieldBuilder(doc, "invalid")
                    .setWidgetRectangle(new Rectangle(100, 500, 100, 100))
                    .createList();
            field.setOptions(options);
            field.getFirstFormAnnotation().setFormFieldElement(listBoxField);

            PdfAcroForm.getAcroForm(doc, true).addField(field);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1))
    public void listBoxIsBiggerThanPage() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxIsBiggerThenPage.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxIsBiggerThenPage.pdf";
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf))) ;
        ListBoxField list = (ListBoxField) new ListBoxField("name", 200, false).setInteractive(true);
        list.setBackgroundColor(ColorConstants.RED);
        list.addOption("value1");
        list.addOption("value2");
        document.add(new Paragraph("s\no\nm\ne\nl\no\nn\ng\nt\ne\nx\nt\n"));
        document.add(list);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1))
    public void listBoxIsBiggerThanPageNonI() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxIsBiggerThenPageNonI.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxIsBiggerThenPageNonI.pdf";
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf))) ;
        ListBoxField list = (ListBoxField) new ListBoxField("name", 200, false);
        list.setBackgroundColor(ColorConstants.RED);
        list.addOption("value1");
        list.addOption("value2");
        document.add(new Paragraph("s\no\nm\ne\nl\no\nn\ng\nt\ne\nx\nt\n"));
        document.add(list);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void invalidOptionsExceptionTest() throws IOException, InterruptedException {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(doc, "invalid")
                    .setWidgetRectangle(new Rectangle(100, 500, 100, 100));

            PdfArray option1 = new PdfArray();
            option1.add(new PdfString("English"));
            option1.add(new PdfString("English"));
            option1.add(new PdfString("English3"));
            PdfArray options = new PdfArray();
            options.add(option1);
            Exception e = Assertions.assertThrows(IllegalArgumentException.class,  () -> builder.setOptions(options));
            Assertions.assertEquals(FormsExceptionMessageConstant.INNER_ARRAY_SHALL_HAVE_TWO_ELEMENTS, e.getMessage());
            options.clear();

            option1 = new PdfArray();
            option1.add(new PdfString("English"));
            option1.add(new PdfNumber(1));
            options.add(option1);
            e = Assertions.assertThrows(IllegalArgumentException.class,  () -> builder.setOptions(options));
            Assertions.assertEquals(FormsExceptionMessageConstant.OPTION_ELEMENT_MUST_BE_STRING_OR_ARRAY, e.getMessage());

            PdfArray options2 = new PdfArray();
            options2.add(new PdfNumber(1));
            e = Assertions.assertThrows(IllegalArgumentException.class,  () -> builder.setOptions(options2));
            Assertions.assertEquals(FormsExceptionMessageConstant.OPTION_ELEMENT_MUST_BE_STRING_OR_ARRAY, e.getMessage());
        }
    }
}
