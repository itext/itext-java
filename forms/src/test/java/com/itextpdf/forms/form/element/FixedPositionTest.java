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


import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.ElementPropertyContainer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FixedPositionTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/FixedPositionTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/FixedPositionTest/";
    public static final String IMG_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/SignatureFieldAppearanceTest/";

    @BeforeAll
    public static void setUp() {
        createDestinationFolder(DESTINATION_FOLDER);
    }


    @Test
    public void nonInteractive() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "ni_setFixedPosition.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_ni_setFixedPosition.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument);

            int left = 100;
            int bottom = 700;
            int width = 150;

            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                new DummyContainer()
                        .applyFixedPosition(left, bottom, width)
                        .applyToElement(field);

                bottom -= 75;
                document.add(field);
            }

            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void nonInteractiveOnSpecificPage() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "ni_setFixedPositionOnPage.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_ni_setFixedPositionOnPage.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument);

            int left = 100;
            int bottom = 700;
            int width = 150;
            int page = 1;
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                new DummyContainer()
                        .applyFixedPosition(++page, left, bottom, width)
                        .applyToElement(field);

                document.add(field);
            }

            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT))
    public void interactive() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "interactive_fixed_pos.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_interactive_fixed_pos.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument);
            int left = 100;
            int bottom = 700;
            int width = 150;
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);
                new DummyContainer()
                        .applyFixedPosition(left, bottom, width)
                        .applyToElement(field);
                document.add(field);
                bottom -= 75;
            }

            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT))
    public void interactiveOnPage() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "interactive_fixed_pos_on_page.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_interactive_fixed_pos_on_page.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument);
            int left = 100;
            int bottom = 700;
            int width = 150;
            int page = 1;
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);

                new DummyContainer()
                        .applyFixedPosition(++page, left, bottom, width)
                        .applyToElement(field);

                document.add(field);
            }

            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT))
    public void interactiveWidthOutOfBounds() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "interactiveOutOfBounds.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_interactiveOutOfBounds.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument);
            int left = 100;
            int bottom = 10000;
            int width = 100;
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);
                new DummyContainer()
                        .applyFixedPosition(left, bottom, width)
                        .applyToElement(field);
                document.add(field);
                bottom -= 75;
            }
            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2))
    public void interactiveMarginLeft() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "interactiveMarginLeft.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_interactiveMarginLeft.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument);
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();

                new DummyContainer().applyToElement(field);
                UnitValue marginUV = UnitValue.createPointValue(200);
                field.setProperty(Property.MARGIN_LEFT, marginUV);
                document.add(field);
            }
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);
                new DummyContainer().applyToElement(field);

                UnitValue marginUV = UnitValue.createPointValue(200);
                field.setProperty(Property.MARGIN_LEFT, marginUV);

                document.add(field);
            }
            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2))
    public void interactiveMarginTop() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "marginTop.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_marginTop.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument, PageSize.A4, false);

            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                new DummyContainer().applyToElement(field);
                UnitValue marginUV = UnitValue.createPointValue(50);
                field.setProperty(Property.MARGIN_TOP, marginUV);
                document.add(field);
            }
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);
                new DummyContainer().applyToElement(field);

                UnitValue marginUV = UnitValue.createPointValue(50);
                field.setProperty(Property.MARGIN_TOP, marginUV);

                document.add(field);
            }
            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2))
    public void width() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "width.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_width.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument, PageSize.A4, false);

            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                new DummyContainer().applyToElement(field);
                field.setProperty(Property.WIDTH, UnitValue.createPointValue(400));
                document.add(field);
            }
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);

                new DummyContainer().applyToElement(field);
                field.setProperty(Property.WIDTH, UnitValue.createPointValue(400));
                document.add(field);
            }
            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2))
    public void padding() throws IOException, InterruptedException {
        final String outputFileName = DESTINATION_FOLDER + "padding.pdf";
        final String cmpFileName = SOURCE_FOLDER + "cmp_padding.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputFileName))) {
            Document document = new Document(pdfDocument, PageSize.A4, false);

            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                new DummyContainer().applyToElement(field);
                field.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(50));
                document.add(field);
            }
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            for (Supplier<IFormField> iFormFieldSupplier : getDataToTest()) {
                IFormField field = iFormFieldSupplier.get();
                field.setInteractive(true);
                new DummyContainer().applyToElement(field);
                field.setProperty(Property.LEFT, UnitValue.createPointValue(20));
                document.add(field);
            }
            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER,
                "diff"));
    }

    private static List<Supplier<IFormField>> getDataToTest() throws MalformedURLException {
        List<Supplier<IFormField>> data = new ArrayList<>();
        data.add(() -> {
            InputField inputField = new InputField("inputField");
            inputField.setValue("value some text");
            return inputField;
        });

        data.add(() -> {
            TextArea textArea = new TextArea("textArea");
            textArea.setValue("value some text\nsome more text");
            return textArea;
        });

        data.add(() -> {
            Radio radio = new Radio("radioButton", "group");
            radio.setChecked(true);
            return radio;
        });

        data.add(() -> {
            ComboBoxField field = new ComboBoxField("comboBox");
            field.addOption(new SelectFieldItem("option1"));
            field.addOption(new SelectFieldItem("option2"));
            field.setSelected("option1");
            return field;
        });

        data.add(() -> {
            ListBoxField field2 = new ListBoxField("listBox", 4, false);
            field2.addOption(new SelectFieldItem("option1"));
            field2.addOption(new SelectFieldItem("option2"));
            field2.setValue("option1");
            return field2;
        });

        data.add(() -> {
            SignatureFieldAppearance app = new SignatureFieldAppearance("signatureField1");
            app.setContent(new SignedAppearanceText().setSignedBy("signer\nname").setLocationLine("location")
                    .setReasonLine("reason"));
            return app;
        });

        ImageData image = ImageDataFactory.create(IMG_FOLDER + "1.png");
        data.add(() -> {
            SignatureFieldAppearance app = new SignatureFieldAppearance("signatureField2");
            app.setContent(image);
            return app;
        });

        data.add(() -> {
            SignatureFieldAppearance app = new SignatureFieldAppearance("signatureField3");
            app.setContent("signature with image and description test\n" +
                    "signature with image and description test\nsignature with image and description test", image);
            return app;
        });

        data.add(() -> {
            SignatureFieldAppearance app = new SignatureFieldAppearance("signatureField4");
            app.setContent("signer", new SignedAppearanceText().setSignedBy("signer").setLocationLine("location")
                    .setReasonLine("reason"));
            return app;
        });

        data.add(() -> {
            SignatureFieldAppearance app = new SignatureFieldAppearance("signatureField5");
            app.setContent(new Div().add(new Paragraph("signature with div element test\n" +
                    "signature with div element test\nsignature with div element test")));
            return app;
        });

        data.add(() -> {
            Button button = new Button("button");
            button.setValue("Click me");
            return button;
        });

        data.add(() -> {
            CheckBox cb = new CheckBox("checkBox");
            cb.setSize(20);
            cb.setChecked(true);
            return cb;
        });
        return data;
    }

    public static class DummyContainer extends ElementPropertyContainer<IPropertyContainer> {

         public DummyContainer(){
             setBackgroundColor(ColorConstants.RED);
             setBorder(new SolidBorder(ColorConstants.BLUE, 2));
         }

        public DummyContainer applyFixedPosition(int left, int bottom, int width) {
            setFixedPosition(left, bottom, width);
            return this;
        }

        public DummyContainer applyFixedPosition(int pageNumber, int left, int bottom, int width) {
            setFixedPosition(pageNumber, left, bottom, width);
            return this;
        }


        public void applyToElement(IPropertyContainer propertyContainer) {
            for (Integer i : this.properties.keySet()) {
                Object value = this.properties.<Object>get((int)i);
                propertyContainer.setProperty((int) i, value);
            }

        }
    }


}

