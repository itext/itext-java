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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfFormFieldMultilineTextTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldMultilineTextTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldMultilineTextTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
    }


    @Test
    public void multilineFormFieldTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "multilineFormFieldTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfTextFormField name = new TextFormFieldBuilder(pdfDoc, "fieldName")
                .setWidgetRectangle(new Rectangle(150, 600, 277, 44)).createMultilineText();
        name.setValue("").setFont(null).setFontSize(0);
        name.setScroll(false);
        name.getFirstFormAnnotation().setBorderColor(ColorConstants.GRAY);
        String itextLicence = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        name.setValue(itextLicence);
        form.addField(name);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_multilineFormFieldTest.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void multilineTextFieldWithAlignmentTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "multilineTextFieldWithAlignment.pdf";
        String cmpPdf = sourceFolder + "cmp_multilineTextFieldWithAlignment.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(210, 600, 150, 100);
        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "fieldName")
                .setWidgetRectangle(rect).createMultilineText();
        field.setValue("some value\nsecond line\nthird");
        field.setJustification(TextAlignment.RIGHT);
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void multilineFormFieldNewLineTest() throws IOException, InterruptedException {
        String testName = "multilineFormFieldNewLineTest";
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";
        String srcPdf = sourceFolder + testName + ".pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(srcPdf);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> fields = form.getAllFormFields();
        fields.get("BEMERKUNGEN").setValue("First line\n\n\nFourth line");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void multilineFormFieldNewLineFontType3Test() throws IOException, InterruptedException {
        String testName = "multilineFormFieldNewLineFontType3Test";

        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";
        String srcPdf = sourceFolder + testName + ".pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(srcPdf);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField info = (PdfTextFormField) form.getField("info");
        info.setValue("A\n\nE");

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void notFittingByHeightTest() throws IOException, InterruptedException {
        String filename = "notFittingByHeightTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        for (int i = 15; i <= 50; i += 15) {
            PdfFormField[] fields = new PdfFormField[]{
                    new TextFormFieldBuilder(pdfDoc, "multi " + i)
                            .setWidgetRectangle(new Rectangle(100, 800 - i * 4, 150, i)).createMultilineText().setValue("MULTI"),
                    new TextFormFieldBuilder(pdfDoc, "single " + i).setWidgetRectangle(new Rectangle(300, 800 - i * 4, 150, i))
                            .createText().setValue("SINGLE")};
            for (PdfFormField field : fields) {
                field.setFontSize(40);
                field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
                form.addField(field);
            }
        }
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void borderWidthIndentMultilineTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "borderWidthIndentMultilineTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "multi")
                .setWidgetRectangle(new Rectangle(100, 500, 400, 300)).createMultilineText();
        field.setValue("Does this text overlap the border? Well it shouldn't!");
        field.setFontSize(30);
        field.getFirstFormAnnotation().setBorderColor(ColorConstants.RED);
        field.getFirstFormAnnotation().setBorderWidth(50);
        form.addField(field);

        PdfTextFormField field2 = new TextFormFieldBuilder(pdfDoc, "multiAuto")
                .setWidgetRectangle(new Rectangle(100, 400, 400, 50)).createMultilineText();
        field2.setValue("Does this autosize text overlap the border? Well it shouldn't! Does it fit accurately though?");
        field2.setFontSize(0);
        field2.getFirstFormAnnotation().setBorderColor(ColorConstants.RED);
        field2.getFirstFormAnnotation().setBorderWidth(20);
        form.addField(field2);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_borderWidthIndentMultilineTest.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void formFieldFilledWithStringTest() throws IOException, InterruptedException {
        String value = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "formFieldWithStringTest.pdf"));

        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "NotoSansCJKtc-Light.otf", PdfEncodings.IDENTITY_H);

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfFormField form = new TextFormFieldBuilder(pdfDoc, "field")
                .setWidgetRectangle(new Rectangle(59, 715, 127, 69)).createMultilineText().setValue("");
        form.setFont(font).setFontSize(10f);
        form.getFirstFormAnnotation().setBorderWidth(2).setBorderColor(ColorConstants.BLACK);
        form.setValue(value);

        acroForm.addField(form);
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "formFieldWithStringTest.pdf",
                sourceFolder + "cmp_formFieldWithStringTest.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void multilineTextFieldLeadingSpacesAreNotTrimmedTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "multilineTextFieldLeadingSpacesAreNotTrimmed.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.addNewPage();

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 300, 200);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "TestField")
                .setWidgetRectangle(rect).createMultilineText();
        field.setValue("        value\n      with\n    leading\n    space");

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_multilineTextFieldLeadingSpacesAreNotTrimmed.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void multilineTextFieldRedundantSpacesAreTrimmedTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "multilineTextFieldRedundantSpacesAreTrimmedTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.addNewPage();

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 90, 200);

        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "TestField")
                .setWidgetRectangle(rect).createMultilineText();
        field.setValue("before spaces           after spaces");

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_multilineTextFieldRedundantSpacesAreTrimmedTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }
}
