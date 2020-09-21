/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
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
    public void fillFormWithRotatedFieldAndPageTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithRotatedFieldAndPageTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "pdfWithRotatedField.pdf"),
                new PdfWriter(outPdf));
        PdfAcroForm form1 = PdfAcroForm.getAcroForm(doc, false);
        form1.getField("First field").setValue("We filled this field").setBorderColor(ColorConstants.BLACK);
        doc.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(outPdf, sourceFolder + "cmp_fillFormWithRotatedFieldAndPageTest.pdf",
                        destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    //TODO: update cmp file after fixing DEVSIX-836
    public void borderStyleInCreatedFormFieldsTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "borderStyleInCreatedFormFields.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outPdf));

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

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                sourceFolder + "cmp_borderStyleInCreatedFormFields.pdf", destinationFolder));
    }

    @Test
    //TODO: update cmp file after fixing DEVSIX-836
    public void updatingBorderStyleInFormFieldsTest() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "borderStyleInCreatedFormFields.pdf";
        String outPdf = destinationFolder + "updatingBorderStyleInFormFields.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(inputPdf), new PdfWriter(outPdf));

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, false);

        Map<String, PdfFormField> fields = form.getFormFields();
        fields.get("firstField").setValue("New Value 1");
        fields.get("secondField").setValue("New Value 2");
        fields.get("thirdField").setValue("New Value 3");
        
        form.flattenFields();

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                sourceFolder + "cmp_updatingBorderStyleInFormFields.pdf", destinationFolder));
    }
}
