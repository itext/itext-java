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
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TextAreaTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/TextAreaTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/TextAreaTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea formTextArea = new TextArea("form text area");
            formTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "form\ntext\narea");
            document.add(formTextArea);

            TextArea flattenTextArea = new TextArea("flatten text area");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext\narea");
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(
            messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 16))
    public void percentFontTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "percentFontTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_percentFontTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea formTextArea = new TextArea("form text area");
            formTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.FALSE);
            formTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "form\ntext\narea");
            formTextArea.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(10));
            document.add(formTextArea);

            TextArea flattenTextArea = new TextArea("flatten text area");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext\narea");
            formTextArea.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(10));
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void heightTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "heightTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_heightTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea flattenTextArea = new TextArea("flatten text area with height");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext area\nwith height");
            flattenTextArea.setProperty(Property.HEIGHT, new UnitValue(UnitValue.POINT, 100));
            flattenTextArea.setBorder(new SolidBorder(2f));
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void minHeightTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "minHeightTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_minHeightTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea flattenTextArea = new TextArea("flatten text area with height");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext area\nwith height");
            flattenTextArea.setProperty(Property.MIN_HEIGHT, new UnitValue(UnitValue.POINT, 100));
            flattenTextArea.setBorder(new SolidBorder(2f));
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void hugeMarginPaddingBorderTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "hugeMarginPaddingBorder.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_hugeMarginPaddingBorder.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea formTextArea = new TextArea("interactive text area with paddings");
            formTextArea.setInteractive(true);
            formTextArea.setValue("interactive\ntext area\nwith paddings");
            formTextArea.setBorder(new SolidBorder(20));
            formTextArea.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(20));
            formTextArea.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(20));
            document.add(formTextArea);

            TextArea flattenTextArea = new TextArea("flatten text area with paddings");
            flattenTextArea.setInteractive(false);
            flattenTextArea.setValue("flatten\ntext area\nwith paddings");
            flattenTextArea.setBorder(new SolidBorder(20));
            flattenTextArea.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(20));
            flattenTextArea.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(20));
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaDoesNotFitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaDoesNotFit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaDoesNotFit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div();
            div.setWidth(UnitValue.createPointValue(400));
            div.setHeight(UnitValue.createPointValue(730));
            div.setBackgroundColor(ColorConstants.PINK);
            document.add(div);

            TextArea textArea = new TextArea("text area");
            textArea.setInteractive(true);
            textArea.setProperty(FormProperty.FORM_FIELD_VALUE,
                    "some text to not\nbe able to fit in on the page\nmore text just text\nreally big height");
            textArea.setHeight(50);
            textArea.setBorder(new SolidBorder(2f));
            document.add(textArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWith0FontSizeDoesNotFitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWith0FontSizeDoesNotFit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWith0FontSizeDoesNotFit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.add(new Div().setBackgroundColor(ColorConstants.RED).setHeight(695));

            TextArea textArea = new TextArea("text area");
            textArea.setInteractive(true);
            textArea.setProperty(FormProperty.FORM_FIELD_VALUE,
                    "Font\n size \nof this\nText Area will \nbe approximated\nbased on the content");
            textArea.setBorder(new SolidBorder(1f));
            textArea.setFontSize(0);
            textArea.setHeight(75);
            document.add(textArea);

            document.add(new Div().setBackgroundColor(ColorConstants.RED).setHeight(695));

            TextArea flattenTextArea = new TextArea("text area");
            flattenTextArea.setInteractive(false);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE,
                    "Font\n size \nof this\nText Area will \nbe approximated\nbased on the content");
            flattenTextArea.setBorder(new SolidBorder(1f));
            flattenTextArea.setFontSize(0);
            flattenTextArea.setHeight(75);
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWith0FontSizeFitsTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWith0FontSizeFits.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWith0FontSizeFits.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea textArea = new TextArea("text area");
            textArea.setInteractive(true);
            textArea.setProperty(FormProperty.FORM_FIELD_VALUE,
                    "Font\n size \nof this\nText Area will \nbe approximated\nbased on the content");
            textArea.setBorder(new SolidBorder(1f));
            textArea.setFontSize(0);
            textArea.setHeight(75);
            document.add(textArea);

            TextArea flattenTextArea = new TextArea("text area");
            flattenTextArea.setInteractive(false);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE,
                    "Font\n size \nof this\nText Area will \nbe approximated\nbased on the content");
            flattenTextArea.setBorder(new SolidBorder(1f));
            flattenTextArea.setFontSize(0);
            flattenTextArea.setHeight(75);
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWith0FontSizeWithoutHeightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWith0FontSizeWithoutHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWith0FontSizeWithoutHeight.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea textArea = new TextArea("text area");
            textArea.setInteractive(true);
            textArea.setProperty(FormProperty.FORM_FIELD_VALUE, "Font\n size \nof this\nText Area will not "
                    + "\nbe approximated\nbased on the content\nbecause height is not set");
            textArea.setBorder(new SolidBorder(1f));
            textArea.setFontSize(0);
            document.add(textArea);

            TextArea flattenTextArea = new TextArea("text area");
            flattenTextArea.setInteractive(false);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "Font\n size \nof this\nText Area will not "
                    + "\nbe approximated\nbased on the content\nbecause height is not set");
            flattenTextArea.setBorder(new SolidBorder(1f));
            flattenTextArea.setFontSize(0);
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWithBorderLessThan1Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWithBorderLessThan1.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWithBorderLessThan1.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea textArea = new TextArea("text area");
            textArea.setInteractive(true);
            textArea.setProperty(FormProperty.FORM_FIELD_VALUE,
                    "Is border visible?\nAnd after clicking on the field?\nIt should be by the way");
            textArea.setBorder(new SolidBorder(0.5f));
            document.add(textArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWithJustificationTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWithJustification.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWithJustification.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea textArea = new TextArea("text area");
            textArea.setValue("text area with justification\nWords shall be in the center\nAre they?");
            textArea.setInteractive(true);
            textArea.setTextAlignment(TextAlignment.CENTER);
            document.add(textArea);

            TextArea flattenedTextArea = new TextArea("flattened text area");
            flattenedTextArea.setValue("text area with justification\nWords shall be in the center\nAre they?");
            flattenedTextArea.setInteractive(false);
            flattenedTextArea.setTextAlignment(TextAlignment.CENTER);
            document.add(flattenedTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWithCustomBorderTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWithCustomBorder.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWithCustomBorder.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea textArea = new TextArea("text area");
            textArea.setValue("text area with custom border\nBorder shall be orange, 10 points wide and dashed");
            textArea.setInteractive(true);
            textArea.setBorder(new DashedBorder(ColorConstants.ORANGE, 10));
            document.add(textArea);

            TextArea flattenedTextArea = new TextArea("flattened text area");
            flattenedTextArea.setValue(
                    "text area with custom border\nBorder shall be orange, 10 points wide and dashed");
            flattenedTextArea.setInteractive(false);
            flattenedTextArea.setBorder(new DashedBorder(ColorConstants.ORANGE, 10));
            document.add(flattenedTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void maxHeightTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "maxHeightTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_maxHeightTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea flattenTextArea = new TextArea("flatten text area with height");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext area\nwith height");
            flattenTextArea.setProperty(Property.MAX_HEIGHT, new UnitValue(UnitValue.POINT, 28));
            flattenTextArea.setBorder(new SolidBorder(2f));
            document.add(flattenTextArea);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void textAreaWithCustomLeadingTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textAreaWithCustomLeading.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_textAreaWithCustomLeading.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea textArea = new TextArea("text1").setBorder(new SolidBorder(ColorConstants.PINK, 1));
            textArea.setValue("text area with 1 used as the basis for the leading calculation");
            textArea.setInteractive(true);
            textArea.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 1));
            textArea.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(5));
            document.add(textArea);

            TextArea textArea2 = new TextArea("text2").setBorder(new SolidBorder(ColorConstants.YELLOW, 1));
            textArea2.setValue("text area with 3 used as the basis for the leading calculation");
            textArea2.setInteractive(true);
            textArea2.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 3));
            textArea2.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(5));
            document.add(textArea2);

            TextArea flattenedTextArea = new TextArea("text3").setBorder(new SolidBorder(ColorConstants.PINK, 1));
            flattenedTextArea.setValue("text area with 5 used as the basis for the leading calculation");
            flattenedTextArea.setInteractive(false);
            flattenedTextArea.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 5));
            flattenedTextArea.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(5));
            document.add(flattenedTextArea);

            TextArea flattenedTextArea2 = new TextArea("text4").setBorder(new SolidBorder(ColorConstants.YELLOW, 1));
            flattenedTextArea2.setValue("text area with 0.5 used as the basis for the leading calculation");
            flattenedTextArea2.setInteractive(false);
            flattenedTextArea2.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 0.5f));
            document.add(flattenedTextArea2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
