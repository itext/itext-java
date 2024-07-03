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
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;

@Tag("IntegrationTest")
public class InputButtonTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/InputButtonTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/InputButtonTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicInputButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicInputButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicInputButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formInputButton = new Button("form input button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setSingleLineValue("form input button");
            document.add(formInputButton);

            document.add(new Paragraph(""));

            Button flattenInputButton = new Button("flatten input button");
            flattenInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenInputButton.setSingleLineValue("flatten input button");
            document.add(flattenInputButton);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void customizedInputButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "customizedInputButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_customizedInputButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formInputButton = new Button("form input button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setSingleLineValue("form input button");
            formInputButton.setFontColor(ColorConstants.BLUE);
            formInputButton.setBackgroundColor(ColorConstants.YELLOW);
            formInputButton.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(formInputButton);

            document.add(new Paragraph(""));

            Button flattenInputButton = new Button("flatten input button");
            flattenInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenInputButton.setSingleLineValue("flatten input button");
            flattenInputButton.setFontColor(ColorConstants.BLUE);
            flattenInputButton.setBackgroundColor(ColorConstants.YELLOW);
            flattenInputButton.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(flattenInputButton);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void addInputButtonInTwoWaysTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "addInputButtonInTwoWays.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_addInputButtonInTwoWays.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // Create push button using html element
            Button formInputButton = new Button("button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setSingleLineValue("html input button");
            formInputButton.setFontColor(ColorConstants.BLUE);
            formInputButton.setBackgroundColor(ColorConstants.YELLOW);
            formInputButton.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(formInputButton);

            // Create push button using form field
            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);
            PdfButtonFormField button = new PushButtonFormFieldBuilder(document.getPdfDocument(), "push")
                    .setWidgetRectangle(new Rectangle(36, 700, 94, 40))
                    .setCaption("form input button").createPushButton();
            button.setFontSizeAutoScale().setColor(ColorConstants.RED);
            button.getFirstFormAnnotation()
                    .setBorderWidth(5).setBorderColor(ColorConstants.MAGENTA).setBackgroundColor(ColorConstants.PINK)
                    .setVisibility(PdfFormAnnotation.VISIBLE);
            form.addField(button);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void inputButtonIsSplitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputButtonIsSplit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputButtonIsSplit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formInputButton = new Button("button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setProperty(Property.WIDTH, UnitValue.createPointValue(280));
            formInputButton.setProperty(Property.HEIGHT, UnitValue.createPointValue(30));
            formInputButton.setSingleLineValue("text with default font size longer than button width won't be split");
            document.add(formInputButton);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2),
            @LogMessage(messageTemplate = FormsLogMessageConstants.INPUT_FIELD_DOES_NOT_FIT, count = 2)
    })
    public void inputButtonIsForcedSplitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputButtonIsForcedSplit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputButtonIsForcedSplit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formInputButton = new Button("button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setProperty(Property.WIDTH, UnitValue.createPointValue(280));
            formInputButton.setProperty(Property.HEIGHT, UnitValue.createPointValue(30));
            formInputButton.setSingleLineValue("text with line break\n which will be split");
            document.add(formInputButton);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void inputButtonWithPaddingsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputButtonWithPaddings.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputButtonWithPaddings.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formInputButton = new Button("button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(15));
            formInputButton.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(15));
            formInputButton.setFontSize(50);
            formInputButton.setSingleLineValue("Caption");
            document.add(formInputButton);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void inputButtonWithMarginsPaddingsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputButtonWithMarginsPaddings.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputButtonWithMarginsPaddings.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div().setBackgroundColor(ColorConstants.PINK);
            Button formInputButton = new Button("button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formInputButton.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(20));
            formInputButton.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(20));
            formInputButton.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 20));
            formInputButton.setFontSize(20);
            formInputButton.setSingleLineValue("Caption");
            div.add(formInputButton);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
