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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
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
public class RadioTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/RadioTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/RadioTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicRadioTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicRadio.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicRadio.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio formRadio1 = createRadioButton("form radio button 1", "form radio group", null, null, true, false);
            document.add(formRadio1);

            Radio formRadio2 = createRadioButton("form radio button 2", "form radio group", null, null, false, false);
            document.add(formRadio2);

            Radio flattenRadio1 = createRadioButton("flatten radio button 1", "flatten radio group", null, null, true,
                    true);
            document.add(flattenRadio1);

            Radio flattenRadio2 = createRadioButton("flatten radio button 2", "flatten radio group", null, null, false,
                    true);
            document.add(flattenRadio2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicRadioTaggedTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicRadioTagged.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicRadioTagged.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().setTagged();
            Radio formRadio1 = createRadioButton("form radio button 1", "form radio group", null, null, true, false);
            document.add(formRadio1);

            Radio formRadio2 = createRadioButton("form radio button 2", "form radio group", null, null, false, false);
            document.add(formRadio2);

            Radio flattenRadio1 = createRadioButton("flatten radio button 1", "flatten radio group", null, null, true,
                    true);
            document.add(flattenRadio1);

            Radio flattenRadio2 = createRadioButton("flatten radio button 2", "flatten radio group", null, null, false,
                    true);
            document.add(flattenRadio2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void emptyNameTest() {
        try (Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            Radio formRadio = createRadioButton("radio button 1", null, null, null, true, false);

            Exception e = Assertions.assertThrows(PdfException.class, () -> document.add(formRadio));
            Assertions.assertEquals(FormsExceptionMessageConstant.EMPTY_RADIO_GROUP_NAME, e.getMessage());
        }
    }

    @Test
    public void emptyValueTest() {
        try (Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            Radio formRadio = createRadioButton("", "radioGroup", null, null, true, false);

            Exception e = Assertions.assertThrows(PdfException.class, () -> document.add(formRadio));
            Assertions.assertEquals(FormsExceptionMessageConstant.APEARANCE_NAME_MUST_BE_PROVIDED, e.getMessage());
        }
    }

    @Test
    public void mergeWithExistingFieldTest() throws IOException, InterruptedException {
        String srcPdf = SOURCE_FOLDER + "src_mergeWithExistingField.pdf";
        String outPdf = DESTINATION_FOLDER + "mergeWithExistingField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_mergeWithExistingField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf)))) {
            Radio formRadio1 = createRadioButton("radio1", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 1), null, false, false);
            document.add(formRadio1);

            Radio formRadio2 = createRadioButton("radio2", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 1), null, false, false);
            document.add(formRadio2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void borderBackgroundTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderBackground.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderBackground.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio formRadio1 = createRadioButton("formRadio1", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 1), ColorConstants.GREEN, true, false);
            document.add(formRadio1);

            Radio formRadio2 = createRadioButton("formRadio2", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, false, false);
            document.add(formRadio2);

            Radio formRadio3 = createRadioButton("formRadio3", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 6), ColorConstants.GREEN, false, false);
            document.add(formRadio3);

            Radio formRadio4 = createRadioButton("formRadio4", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 6), ColorConstants.GREEN, false, false);
            formRadio4.setSize(20);
            document.add(formRadio4);

            Radio formRadio5 = createRadioButton("formRadio5", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 6), ColorConstants.GREEN, false, false);
            formRadio5.setSize(20);
            document.add(formRadio5);

            Radio flattenRadio1 = createRadioButton("flattenRadio1", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 1), ColorConstants.GREEN, true, true);
            flattenRadio1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(flattenRadio1);

            Radio flattenRadio2 = createRadioButton("flattenRadio2", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, false, true);
            document.add(flattenRadio2);

            Radio flattenRadio3 = createRadioButton("flattenRadio3", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 6), ColorConstants.GREEN, false, true);
            document.add(flattenRadio3);

            Radio flattenRadio4 = createRadioButton("flattenRadio4", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 6), ColorConstants.GREEN, false, true);
            flattenRadio4.setSize(20);
            document.add(flattenRadio4);

            Radio flattenRadio5 = createRadioButton("flattenRadio5", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 6), ColorConstants.GREEN, false, true);
            flattenRadio5.setSize(20);
            document.add(flattenRadio5);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void borderBoxesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderBoxes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderBoxes.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // BORDER_BOX
            Radio formRadio1 = createRadioButton("formRadio1", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, true, false);
            formRadio1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(formRadio1);

            // CONTENT_BOX
            Radio formRadio2 = createRadioButton("formRadio2", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, false, false);
            formRadio2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(formRadio2);

            // BORDER_BOX
            Radio flattenRadio1 = createRadioButton("flattenRadio1", "flatten radio group",
                    new SolidBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, true, true);
            flattenRadio1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(flattenRadio1);

            // CONTENT_BOX
            Radio flattenRadio2 = createRadioButton("flattenRadio2", "flatten radio group",
                    new SolidBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, false, true);
            flattenRadio2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(flattenRadio2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void dottedBorderTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "dottedBorder.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_dottedBorder.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio formRadio1 = createRadioButton("formRadio1", "form radio group",
                    new DottedBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, true, false);
            formRadio1.setSize(20);
            document.add(formRadio1);

            Radio formRadio2 = createRadioButton("formRadio2", "form radio group",
                    new DottedBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, false, false);
            formRadio2.setSize(20).setProperty(FormProperty.FORM_FIELD_RADIO_BORDER_CIRCLE, Boolean.FALSE);
            document.add(formRadio2);

            Radio flattenRadio1 = createRadioButton("flattenRadio1", "flatten radio group",
                    new DottedBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, true, true);
            flattenRadio1.setSize(20);
            document.add(flattenRadio1);

            Radio flattenRadio2 = createRadioButton("flattenRadio2", "flatten radio group",
                    new DottedBorder(ColorConstants.BLUE, 3), ColorConstants.GREEN, false, true);
            flattenRadio2.setSize(20).setProperty(FormProperty.FORM_FIELD_RADIO_BORDER_CIRCLE, Boolean.FALSE);
            document.add(flattenRadio2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    // This is the test for TODO: DEVSIX-7425 - Border radius 50% doesn't draw rounded borders
    // showing different drawing of circles
    @Test
    public void formFieldRadioBorderCircleTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "formFieldRadioBorderCircle.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_formFieldRadioBorderCircle.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio flattenRadio1 = createRadioButton("flattenRadio1", "flatten radio group",
                    new SolidBorder(ColorConstants.LIGHT_GRAY, 1), ColorConstants.GREEN, false, true);
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_RADIO_BORDER_CIRCLE, Boolean.TRUE);
            document.add(flattenRadio1);

            Radio flattenRadio2 = createRadioButton("flattenRadio2", "flatten radio group",
                    new SolidBorder(ColorConstants.LIGHT_GRAY, 1), ColorConstants.GREEN, false, true);
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_RADIO_BORDER_CIRCLE, Boolean.FALSE);
            document.add(flattenRadio2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA),
            @LogMessage(messageTemplate = FormsLogMessageConstants.INPUT_FIELD_DOES_NOT_FIT)})
    public void bigRadioButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "bigRadioButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_bigRadioButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio flattenRadio1 = createRadioButton("flattenRadio1", "form radio group",
                    new SolidBorder(ColorConstants.BLUE, 1), ColorConstants.GREEN, true, true);
            flattenRadio1.setSize(825f);
            document.add(flattenRadio1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void radioWithMarginsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "radioWithMargins.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_radioWithMargins.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div().setBackgroundColor(ColorConstants.PINK);
            Radio radio = createRadioButton("radio", "form radio group",
                    new SolidBorder(ColorConstants.DARK_GRAY, 20), ColorConstants.WHITE, true, false);
            radio.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(20));
            radio.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));
            radio.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(20));
            radio.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(20));
            radio.setSize(100);
            div.add(radio);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void radioWithPaddingsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "radioWithPaddings.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_radioWithPaddings.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div().setBackgroundColor(ColorConstants.PINK);
            Radio radio = createRadioButton("radio", "form radio group",
                    new SolidBorder(ColorConstants.DARK_GRAY, 20), ColorConstants.WHITE, true, false);
            radio.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(20));
            radio.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20));
            radio.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(20));
            radio.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20));

            // Paddings are always 0 for radio buttons
            Assertions.assertEquals(radio.<UnitValue>getProperty(Property.PADDING_BOTTOM), UnitValue.createPointValue(0));
            Assertions.assertEquals(radio.<UnitValue>getProperty(Property.PADDING_TOP), UnitValue.createPointValue(0));
            Assertions.assertEquals(radio.<UnitValue>getProperty(Property.PADDING_LEFT), UnitValue.createPointValue(0));
            Assertions.assertEquals(radio.<UnitValue>getProperty(Property.PADDING_RIGHT), UnitValue.createPointValue(0));

            radio.setSize(100);
            div.add(radio);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void multiPageRadioFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "multiPageCheckboxField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_multiPageCheckBoxField.pdf";

        try (PdfDocument document = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
            for (int i = 0; i < 10; i++) {
                document.addNewPage();
                Rectangle rect = new Rectangle(210, 490, 150, 22);
                final PdfFormField group = new RadioFormFieldBuilder(document, "fing").createRadioGroup();
                final PdfFormAnnotation radio = new RadioFormFieldBuilder(document, "fing")
                        .setWidgetRectangle(rect)
                        .createRadioButton("bing bong", rect);
                PdfPage page = document.getPage(i + 1);
                group.addKid(radio);
                form.addField(group, page);
                if (i > 2) {
                    page.flush();
                }
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    static private Radio createRadioButton(String name, String groupName, Border border, Color backgroundColor,
            boolean checked, boolean flatten) {
        Radio radio = new Radio(name, groupName);
        radio.setBorder(border);
        radio.setBackgroundColor(backgroundColor);
        radio.setInteractive(!flatten);
        radio.setChecked(checked);

        return radio;
    }
}
