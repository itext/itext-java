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

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
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
public class TextAreaTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/TextAreaTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/TextAreaTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea formTextArea = new TextArea("form text area");
            formTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "form\ntext\narea");
            document.add(formTextArea);

            TextArea flattenTextArea = new TextArea("flatten text area");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext\narea");
            document.add(flattenTextArea);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(
            messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 16))
    public void percentFontTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "percentFontTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_percentFontTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea formTextArea = new TextArea("form text area");
            formTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "form\ntext\narea");
            formTextArea.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(10));
            document.add(formTextArea);

            TextArea flattenTextArea = new TextArea("flatten text area");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext\narea");
            formTextArea.setProperty(Property.FONT_SIZE, UnitValue.createPercentValue(10));
            document.add(flattenTextArea);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void heightTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "heightTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_heightTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea flattenTextArea = new TextArea("flatten text area with height");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext area\nwith height");
            flattenTextArea.setProperty(Property.HEIGHT, new UnitValue(UnitValue.POINT, 100));
            flattenTextArea.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenTextArea);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void minHeightTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "minHeightTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_minHeightTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea flattenTextArea = new TextArea("flatten text area with height");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext area\nwith height");
            flattenTextArea.setProperty(Property.MIN_HEIGHT, new UnitValue(UnitValue.POINT, 100));
            flattenTextArea.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenTextArea);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void maxHeightTextAreaTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "maxHeightTextArea.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_maxHeightTextArea.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            TextArea flattenTextArea = new TextArea("flatten text area with height");
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten\ntext area\nwith height");
            flattenTextArea.setProperty(Property.MAX_HEIGHT, new UnitValue(UnitValue.POINT, 28));
            flattenTextArea.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenTextArea);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
