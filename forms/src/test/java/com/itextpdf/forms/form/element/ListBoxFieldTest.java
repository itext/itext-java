package com.itextpdf.forms.form.element;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ListBoxFieldTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ListBoxFieldTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ListBoxFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }
    
    @Test
    public void emptyListBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyListBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptyListBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField flattenListBoxField = new ListBoxField("flatten empty list box field", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.ACROFORM_NOT_SUPPORTED_FOR_SELECT))
    public void basicListBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicListBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicListBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ListBoxField formListBoxField = new ListBoxField("form list box field", 2, false);
            formListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);

            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");
            formListBoxField.addOption(option1);

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");
            formListBoxField.addOption(option2);
            
            document.add(formListBoxField);

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field", 2, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            document.add(flattenListBoxField);

            Paragraph option3 = new Paragraph("option 3");
            option3.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option3.setProperty(FormProperty.FORM_FIELD_LABEL, "option 3");

            ListBoxField flattenListBoxFieldWithMultipleSelection =
                    new ListBoxField("flatten list box field with multiple selection", 3, true);
            flattenListBoxFieldWithMultipleSelection.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxFieldWithMultipleSelection.addOption(option1);
            flattenListBoxFieldWithMultipleSelection.addOption(option2);
            flattenListBoxFieldWithMultipleSelection.addOption(option3);
            document.add(flattenListBoxFieldWithMultipleSelection);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(
            messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 22))
    public void listBoxFieldWithoutSelectionTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithoutSelection.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithoutSelection.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxFieldWithFont = new ListBoxField("flatten list box field with font", 0, false);
            flattenListBoxFieldWithFont.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxFieldWithFont.setBackgroundColor(ColorConstants.RED);
            flattenListBoxFieldWithFont.addOption(option1);
            flattenListBoxFieldWithFont.addOption(option2);
            document.add(flattenListBoxFieldWithFont);

            ListBoxField flattenListBoxFieldWithPercentFont =
                    new ListBoxField("flatten list box field with percent font", 0, false);
            flattenListBoxFieldWithPercentFont.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxFieldWithPercentFont.setBackgroundColor(ColorConstants.RED);
            flattenListBoxFieldWithPercentFont.addOption(option1);
            flattenListBoxFieldWithPercentFont.addOption(option2);
            flattenListBoxFieldWithPercentFont.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(10));
            document.add(flattenListBoxFieldWithPercentFont);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithOverflowTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithOverflow.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithOverflow.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field with overflow", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            flattenListBoxField.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");
            
            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field with height", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            flattenListBoxField.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithMinHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithMinHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithMinHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field with min height", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            flattenListBoxField.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(100));
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT))
    public void listBoxFieldWithMaxHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithMaxHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithMaxHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field with max height", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            flattenListBoxField.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(40));
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
            
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field cannot fit", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldCannotFitByWidthTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldCannotFitByWidth.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldCannotFitByWidth.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field cannot fit by width", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.setProperty(Property.WIDTH, UnitValue.createPointValue(600));
            flattenListBoxField.setBorder(new SolidBorder(20));
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void listBoxFieldWithLangTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "listBoxFieldWithLang.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_listBoxFieldWithLang.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ListBoxField flattenListBoxField = new ListBoxField("flatten list box field with lang", 0, false);
            flattenListBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenListBoxField.setBackgroundColor(ColorConstants.RED);
            flattenListBoxField.addOption(option1);
            flattenListBoxField.addOption(option2);
            flattenListBoxField.setProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE, "random_lang");
            document.add(flattenListBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
