package com.itextpdf.forms.form.element;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
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
public class ComboBoxFieldTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ComboBoxFieldTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ComboBoxFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void emptyComboBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyComboBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptyComboBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten empty combo box field");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.ACROFORM_NOT_SUPPORTED_FOR_SELECT))
    public void basicComboBoxFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicComboBoxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicComboBoxField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            ComboBoxField formComboBoxField = new ComboBoxField("form combo box field");
            formComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");
            formComboBoxField.addOption(option1);
            
            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");
            formComboBoxField.addOption(option2);
            document.add(formComboBoxField);

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.addOption(option1);
            flattenComboBoxField.addOption(option2);
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }


    @Test
    @LogMessages(messages = @LogMessage(
            messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 11))
    public void comboBoxFieldWithoutSelectionTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithoutSelection.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithoutSelection.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ComboBoxField flattenComboBoxFieldWithFont = new ComboBoxField("flatten combo box field with font");
            flattenComboBoxFieldWithFont.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxFieldWithFont.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxFieldWithFont.addOption(option1);
            flattenComboBoxFieldWithFont.addOption(option2);
            document.add(flattenComboBoxFieldWithFont);

            ComboBoxField flattenComboBoxFieldWithPercentFont =
                    new ComboBoxField("flatten combo box field with percent font");
            flattenComboBoxFieldWithPercentFont.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxFieldWithPercentFont.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxFieldWithPercentFont.addOption(option1);
            flattenComboBoxFieldWithPercentFont.addOption(option2);
            flattenComboBoxFieldWithPercentFont.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(10));
            document.add(flattenComboBoxFieldWithPercentFont);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field with height");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(option1);
            flattenComboBoxField.addOption(option2);
            flattenComboBoxField.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithMinHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithMinHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithMinHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field with min height");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(option1);
            flattenComboBoxField.addOption(option2);
            flattenComboBoxField.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(100));
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithMaxHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithMaxHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithMaxHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box field with max height");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(option1);
            flattenComboBoxField.addOption(option2);
            flattenComboBoxField.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(10));
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
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
            
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box cannot fit");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(option1);
            flattenComboBoxField.addOption(option2);
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void comboBoxFieldWithLangTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "comboBoxFieldWithLang.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_comboBoxFieldWithLang.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Paragraph option1 = new Paragraph("option 1");
            option1.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
            option1.setProperty(FormProperty.FORM_FIELD_LABEL, "option 1");

            Paragraph option2 = new Paragraph("option 2");
            option2.setProperty(FormProperty.FORM_FIELD_LABEL, "option 2");

            ComboBoxField flattenComboBoxField = new ComboBoxField("flatten combo box with lang");
            flattenComboBoxField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenComboBoxField.setBackgroundColor(ColorConstants.RED);
            flattenComboBoxField.addOption(option1);
            flattenComboBoxField.addOption(option2);
            flattenComboBoxField.setProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE, "random_lang");
            document.add(flattenComboBoxField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
