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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.fields.borders.FormBorderFactory;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.ButtonRenderer;
import com.itextpdf.forms.form.renderer.InputFieldRenderer;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

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
    public void basicButtonTestWithFontDiffersOnParagraph() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicButtonWithFontDiffersOnParagraph.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicButtonWithFontDiffersOnParagraph.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formButton = new Button("form button");

            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            formButton.add(new Paragraph("form button"));
            formButton.add(new Paragraph("paragraph with yellow border inside button")
                    .setFont(PdfFontFactory.createFont(StandardFonts.COURIER))
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 1)));
            document.add(formButton);

            document.add(new Paragraph(""));

            Button flattenButton = new Button("flatten button");
            flattenButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenButton.add(new Paragraph("flatten button"));

            flattenButton.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            flattenButton.add(new Paragraph("paragraph with pink border inside button")
                    .setFont(PdfFontFactory.createFont(StandardFonts.COURIER))
                    .setBorder(new SolidBorder(ColorConstants.PINK, 1)));
            document.add(flattenButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void basicButtonTestWithFont() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicButtonWithFont.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicButtonWithFon.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formButton = new Button("form button");

            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            formButton.add(new Paragraph("form button"));
            formButton.add(new Paragraph("paragraph with yellow border inside button")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 1)));
            document.add(formButton);

            document.add(new Paragraph(""));

            Button flattenButton = new Button("flatten button");
            flattenButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenButton.add(new Paragraph("flatten button"));

            flattenButton.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
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
                    StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(imagePath)))))
                    .setWidth(98).setHeight(98));
            formButton.setFontColor(ColorConstants.BLUE);
            formButton.setBackgroundColor(ColorConstants.YELLOW);
            formButton.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            document.add(formButton);

            // Create push button using form field
            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);
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
    public void borderBoxesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderBoxes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderBoxes.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // BORDER_BOX
            Button interactiveButton = new Button("interactiveButton")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 10));
            interactiveButton.setWidth(200);
            interactiveButton.setInteractive(true);
            interactiveButton.setValue("interactive border box");
            interactiveButton.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(interactiveButton);

            // CONTENT_BOX
            Button interactiveButton2 = new Button("interactiveButton")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10));
            interactiveButton2.setWidth(200);
            interactiveButton2.setInteractive(true);
            interactiveButton2.setValue("interactive content box");
            interactiveButton2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(interactiveButton2);

            // BORDER_BOX
            Button flattenButton = new Button("flattenButton")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 10));
            flattenButton.setWidth(200);
            flattenButton.setInteractive(false);
            flattenButton.setValue("flatten border box");
            flattenButton.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(flattenButton);

            // CONTENT_BOX
            Button flattenButton2 = new Button("flattenButton")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10));
            flattenButton2.setWidth(200);
            flattenButton2.setInteractive(false);
            flattenButton2.setValue("flatten content box");
            flattenButton2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(flattenButton2);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void borderTypesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderTypes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderTypes.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // DASHED
            Button button = new Button("button")
                    .setBorder(new DashedBorder(ColorConstants.PINK, 10))
                    .setBackgroundColor(ColorConstants.YELLOW);
            button.setWidth(100);
            button.setInteractive(true);
            button.setValue("dashed");
            document.add(button);

            PdfDictionary bs = new PdfDictionary();
            // UNDERLINE
            bs.put(PdfName.S, PdfAnnotation.STYLE_UNDERLINE);
            Button button2 = new Button("button2")
                    .setBorder(FormBorderFactory.getBorder(bs, 10f, ColorConstants.YELLOW,
                            ColorConstants.ORANGE))
                    .setBackgroundColor(ColorConstants.PINK);
            button2.setSize(100);
            button2.setInteractive(true);
            button2.setValue("underline");
            document.add(button2);

            // INSET
            bs.put(PdfName.S, PdfAnnotation.STYLE_INSET);
            Button button3 = new Button("button3")
                    .setBorder(FormBorderFactory.getBorder(bs, 10f, ColorConstants.PINK,
                            ColorConstants.RED))
                    .setBackgroundColor(ColorConstants.YELLOW);
            button3.setSize(100);
            button3.setInteractive(true);
            button3.setValue("inset");
            document.add(button3);

            // BEVELLED
            bs.put(PdfName.S, PdfAnnotation.STYLE_BEVELED);
            Button button4 = new Button("button4")
                    .setBorder(FormBorderFactory.getBorder(bs, 10f, ColorConstants.YELLOW,
                            ColorConstants.ORANGE))
                    .setBackgroundColor(ColorConstants.PINK);
            button4.setSize(100);
            button4.setInteractive(true);
            button4.setValue("bevelled");
            document.add(button4);
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
