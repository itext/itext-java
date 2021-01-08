/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms.widget;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class AppearanceCharacteristicsTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/widget/AppearanceCharacteristicsTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/widget/AppearanceCharacteristicsTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFieldBordersTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "formFieldBorders.pdf";
        String cmpPdf = sourceFolder + "cmp_formFieldBorders.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);

            PdfFormField simpleField = PdfTextFormField.createText(doc,
                    new Rectangle(300, 300, 200, 100), "simpleField");
            simpleField.regenerateField();

            PdfFormField insetField = PdfTextFormField.createText(doc,
                    new Rectangle(50, 600, 200, 100), "insetField");
            insetField.getWidgets().get(0).setBorderStyle(PdfName.I);
            insetField.setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField underlineField = PdfTextFormField.createText(doc,
                    new Rectangle(300, 600, 200, 100), "underlineField");
            underlineField.getWidgets().get(0).setBorderStyle(PdfName.U);
            underlineField.setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField solidField = PdfTextFormField.createText(doc,
                    new Rectangle(50, 450, 200, 100), "solidField");
            solidField.getWidgets().get(0).setBorderStyle(PdfName.S);
            solidField.setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField dashField = PdfTextFormField.createText(doc,
                    new Rectangle(300, 450, 200, 100), "dashField");
            dashField.getWidgets().get(0).setBorderStyle(PdfName.D);
            dashField.setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            PdfFormField beveledField = PdfTextFormField.createText(doc,
                    new Rectangle(50, 300, 200, 100), "beveledField");
            beveledField.getWidgets().get(0).setBorderStyle(PdfName.B);
            beveledField.setBorderWidth(3f).setBorderColor(DeviceRgb.RED).regenerateField();

            form.addField(simpleField);
            form.addField(insetField);
            form.addField(underlineField);
            form.addField(solidField);
            form.addField(dashField);
            form.addField(beveledField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void beveledBorderWithBackgroundTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "beveledBorderWithBackground.pdf";
        String cmpPdf = sourceFolder + "cmp_beveledBorderWithBackground.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
            PdfFormField formField = PdfTextFormField.createText(doc,
                    new Rectangle(100, 600, 200, 100), "formField");
            formField.getWidgets().get(0).setBorderStyle(PdfName.B);
            formField.setBorderWidth(3f).setBackgroundColor(DeviceRgb.GREEN).setBorderColor(DeviceRgb.RED);
            formField.regenerateField();
            form.addField(formField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void dashedBorderWithBackgroundTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "dashedBorderWithBackground.pdf";
        String cmpPdf = sourceFolder + "cmp_dashedBorderWithBackground.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
            PdfFormField formField = PdfTextFormField.createText(doc,
                    new Rectangle(100, 600, 200, 100), "formField");
            formField.getWidgets().get(0).setBorderStyle(PdfName.D);
            formField.setBorderWidth(3f).setBorderColor(DeviceRgb.RED).setBackgroundColor(DeviceRgb.GREEN);
            formField.regenerateField();
            form.addField(formField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    // TODO DEVSIX-4809 text in form filed with borders must start after border
    public void textStartsAfterFieldBorderTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "textStartsAfterFieldBorderTest.pdf";
        String cmpPdf = sourceFolder + "cmp_textStartsAfterFieldBorderTest.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);

            PdfFormField insetFormField = PdfTextFormField.createText(doc,
                    new Rectangle(90, 600, 200, 100), "insetFormField");
            insetFormField.getWidgets().get(0).setBorderStyle(PdfName.I);
            insetFormField.setBorderWidth(15f).setBorderColor(DeviceRgb.RED)
                    .setValue("Text after border").regenerateField();

            PdfFormField solidFormField = PdfTextFormField.createText(doc,
                    new Rectangle(300, 600, 200, 100), "solidFormField");
            solidFormField.getWidgets().get(0).setBorderStyle(PdfName.S);
            solidFormField.setBorderWidth(15f).setBorderColor(DeviceRgb.RED)
                    .setValue("Text after border").regenerateField();

            PdfFormField underlineFormField = PdfTextFormField.createText(doc,
                    new Rectangle(90, 450, 200, 100), "underlineFormField");
            underlineFormField.getWidgets().get(0).setBorderStyle(PdfName.U);
            underlineFormField.setBorderWidth(15f).setBorderColor(DeviceRgb.RED)
                    .setValue("Text after border").regenerateField();

            PdfFormField simpleFormField = PdfTextFormField.createText(doc,
                    new Rectangle(300, 450, 200, 100), "formField1");
            simpleFormField.setBorderWidth(15f);
            simpleFormField.setValue("Text after border").regenerateField();

            form.addField(insetFormField);
            form.addField(solidFormField);
            form.addField(underlineFormField);
            form.addField(simpleFormField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void fillFormWithRotatedFieldAndPageTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithRotatedFieldAndPageTest.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithRotatedFieldAndPageTest.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "pdfWithRotatedField.pdf"),
                new PdfWriter(outPdf))) {

            PdfAcroForm form1 = PdfAcroForm.getAcroForm(doc, false);
            form1.getField("First field").setValue("We filled this field").setBorderColor(ColorConstants.BLACK);
        }

        String errorMessage = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void borderStyleInCreatedFormFieldsTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "borderStyleInCreatedFormFields.pdf";
        String cmpPdf = sourceFolder + "cmp_borderStyleInCreatedFormFields.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);

            PdfFormField formField1 = PdfTextFormField.createText(doc,
                    new Rectangle(100, 600, 100, 50), "firstField", "Hello, iText!");
            formField1.getWidgets().get(0).setBorderStyle(PdfAnnotation.STYLE_BEVELED);
            formField1.setBorderWidth(2).setBorderColor(ColorConstants.BLUE);

            PdfFormField formField2 = PdfTextFormField.createText(doc,
                    new Rectangle(100, 500, 100, 50), "secondField", "Hello, iText!");
            formField2.getWidgets().get(0).setBorderStyle(PdfAnnotation.STYLE_UNDERLINE);
            formField2.setBorderWidth(2).setBorderColor(ColorConstants.BLUE);

            PdfFormField formField3 = PdfTextFormField.createText(doc,
                    new Rectangle(100, 400, 100, 50), "thirdField", "Hello, iText!");
            formField3.getWidgets().get(0).setBorderStyle(PdfAnnotation.STYLE_INSET);
            formField3.setBorderWidth(2).setBorderColor(ColorConstants.BLUE);

            form.addField(formField1);
            form.addField(formField2);
            form.addField(formField3);
            form.flattenFields();
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void updatingBorderStyleInFormFieldsTest() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "borderStyleInCreatedFormFields.pdf";
        String outPdf = destinationFolder + "updatingBorderStyleInFormFields.pdf";
        String cmpPdf = sourceFolder + "cmp_updatingBorderStyleInFormFields.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(inputPdf), new PdfWriter(outPdf))) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(doc, false);

            Map<String, PdfFormField> fields = form.getFormFields();
            fields.get("firstField").setValue("New Value 1");
            fields.get("secondField").setValue("New Value 2");
            fields.get("thirdField").setValue("New Value 3");

            form.flattenFields();
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }
}
