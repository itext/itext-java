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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.ButtonRenderer;
import com.itextpdf.forms.form.renderer.InputFieldRenderer;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ButtonTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ButtonTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ButtonTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formButton = new Button("form button");
            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.add(new Paragraph("form button"));
            formButton.add(new Paragraph("paragraph with yellow border inside button")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 1)));
            document.add(formButton);

            document.add(new Paragraph(""));

            Button flattenButton = new Button("flatten button");
            flattenButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenButton.add(new Paragraph("flatten button"));
            flattenButton.add(new Paragraph("paragraph with pink border inside button")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 1)));
            document.add(flattenButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void customizedButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "customizedButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_customizedButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formButton = new Button("form button");
            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.setValue("form button");
            formButton.setFontColor(ColorConstants.BLUE);
            formButton.setBackgroundColor(ColorConstants.YELLOW);
            formButton.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(formButton);

            document.add(new Paragraph(""));

            Button flattenButton = new Button("flatten  button");
            flattenButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenButton.setValue("flatten button");
            flattenButton.setFontColor(ColorConstants.BLUE);
            flattenButton.setBackgroundColor(ColorConstants.YELLOW);
            flattenButton.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(flattenButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void buttonVerticalAlignmentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "buttonVerticalAlignment.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_buttonVerticalAlignment.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formButton = new Button("form button");
            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.setValue("capture on bottom");
            formButton.setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.BOTTOM);
            formButton.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            document.add(formButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void addButtonInTwoWaysTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "addButtonInTwoWays.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_addButtonInTwoWays.pdf";
        String imagePath = SOURCE_FOLDER + "Desert.jpg";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // Create push button using html element
            Button formButton = new Button("button");
            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.setProperty(Property.WIDTH, UnitValue.createPointValue(100));
            formButton.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            formButton.add(new Image(new PdfImageXObject(ImageDataFactory.create(
                    StreamUtil.inputStreamToArray(new FileInputStream(imagePath)))))
                    .setWidth(98).setHeight(98));
            formButton.setFontColor(ColorConstants.BLUE);
            formButton.setBackgroundColor(ColorConstants.YELLOW);
            formButton.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            document.add(formButton);

            // Create push button using form field
            PdfAcroForm form = PdfAcroForm.getAcroForm(document.getPdfDocument(), true);
            PdfButtonFormField button = new PushButtonFormFieldBuilder(document.getPdfDocument(), "push")
                    .setWidgetRectangle(new Rectangle(36, 600, 100, 100))
                    .createPushButton();
            button.setImage(imagePath);
            button.getFirstFormAnnotation()
                    .setBorderWidth(1).setBorderColor(ColorConstants.MAGENTA).setBackgroundColor(ColorConstants.PINK)
                    .setVisibility(PdfFormAnnotation.VISIBLE);
            form.addField(button);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void isFlattenTest() {
        Button button = new Button("button");
        button.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
        ButtonRenderer buttonRenderer = new ButtonRenderer(button);
        Assert.assertFalse(buttonRenderer.isFlatten());
        button.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
        Assert.assertTrue(buttonRenderer.isFlatten());
        InputField inputField = new InputField("input");
        inputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
        button.add(inputField);
        buttonRenderer = (ButtonRenderer) button.createRendererSubTree();
        Assert.assertTrue(((InputFieldRenderer)buttonRenderer.getChildRenderers().get(0)
                .setParent(buttonRenderer)).isFlatten());
    }
}
