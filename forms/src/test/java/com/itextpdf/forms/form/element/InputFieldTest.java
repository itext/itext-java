/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class InputFieldTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/InputFieldTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/InputFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicInputFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicInputField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicInputField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputField formInputField = new InputField("form input field");
            formInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "form input field");
            document.add(formInputField);

            InputField flattenInputField = new InputField("flatten input field");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input field");
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void noValueInputFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "noValueInputField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_noValueInputField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputField flattenInputField = new InputField("no value input field");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, null);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, null);
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
    
    @Test
    public void inputFieldDoesNotFitTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputFieldDoesNotFit.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputFieldDoesNotFit.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Div div = new Div();
            div.setWidth(UnitValue.createPointValue(400));
            div.setHeight(UnitValue.createPointValue(752));
            div.setBackgroundColor(ColorConstants.PINK);
            document.add(div);
            
            InputField flattenInputField = new InputField("input field does not fit");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "input field does not fit");
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void inputFieldWithLangTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputFieldWithLang.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputFieldWithLang.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().setTagged();
            InputField flattenInputField = new InputField("input field with lang");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "input field with lang");
            flattenInputField.setProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE, "random_lang");
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void inputFieldWithNullLangTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputFieldWithNullLang.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputFieldWithNullLang.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().setTagged();
            InputField flattenInputField = new InputField("input field with null lang");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "input field with null lang");
            flattenInputField.setProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE, null);
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void inputFieldWithPasswordTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "inputFieldWithPassword.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_inputFieldWithPassword.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputField formInputField = new InputField("form input field with password");
            formInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "form input field with password");
            formInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            formInputField.setProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG, true);
            document.add(formInputField);
            
            InputField flattenInputField = new InputField("flatten input field with password");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input field with password");
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            flattenInputField.setProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG, true);
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
    
    @Test
    public void heightInputFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "heightInputField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_heightInputField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputField flattenInputField = new InputField("flatten input field with height");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input field with height");
            flattenInputField.setProperty(Property.HEIGHT, new UnitValue(UnitValue.POINT, 100));
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void minHeightInputFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "minHeightInputField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_minHeightInputField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputField flattenInputField = new InputField("flatten input field with height");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input field with height");
            flattenInputField.setProperty(Property.MIN_HEIGHT, new UnitValue(UnitValue.POINT, 100));
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void maxHeightInputFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "maxHeightInputField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_maxHeightInputField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputField flattenInputField = new InputField("flatten input field with height");
            flattenInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input field with height");
            flattenInputField.setProperty(Property.MAX_HEIGHT, new UnitValue(UnitValue.POINT, 10));
            flattenInputField.setProperty(Property.BORDER, new SolidBorder(2f));
            document.add(flattenInputField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
