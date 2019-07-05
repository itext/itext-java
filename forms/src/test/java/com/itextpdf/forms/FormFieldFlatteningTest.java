/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FormFieldFlatteningTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldFlatteningTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldFlatteningTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void getFieldsForFlatteningTest() throws IOException {
        String outPdfName = destinationFolder + "flattenedFormField.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "formFieldFile.pdf"), new PdfWriter(outPdfName));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);

        Assert.assertEquals(0, form.getFieldsForFlattening().size());

        form.partialFormFlattening("radioName");
        form.partialFormFlattening("Text1");

        PdfFormField radioNameField = form.getField("radioName");
        PdfFormField text1Field = form.getField("Text1");

        Assert.assertEquals(2, form.getFieldsForFlattening().size());
        Assert.assertTrue( form.getFieldsForFlattening().contains(radioNameField));
        Assert.assertTrue( form.getFieldsForFlattening().contains(text1Field));

        form.flattenFields();
        pdfDoc.close();

        PdfDocument outPdfDoc = new PdfDocument(new PdfReader(outPdfName));
        PdfAcroForm outPdfForm = PdfAcroForm.getAcroForm(outPdfDoc, false);

        Assert.assertEquals(2, outPdfForm.getFormFields().size());

        outPdfDoc.close();
    }

    @Test
    public void formFlatteningTest01() throws IOException, InterruptedException {
        String srcFilename = "formFlatteningSource.pdf";
        String filename = "formFlatteningTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void formFlatteningChoiceFieldTest01() throws IOException, InterruptedException {
        String srcFilename = "formFlatteningSourceChoiceField.pdf";
        String filename = "formFlatteningChoiceFieldTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void multiLineFormFieldClippingTest() throws IOException, InterruptedException {

        String src = sourceFolder + "multiLineFormFieldClippingTest.pdf";
        String dest = destinationFolder + "multiLineFormFieldClippingTest_flattened.pdf";
        String cmp = sourceFolder + "cmp_multiLineFormFieldClippingTest_flattened.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        form.getField("Text1").setValue("Tall letters: T I J L R E F");
        form.flattenFields();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    public void rotatedFieldAppearanceTest01() throws IOException, InterruptedException {
        String srcFilename = "src_rotatedFieldAppearanceTest01.pdf";
        String filename = "rotatedFieldAppearanceTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void rotatedFieldAppearanceTest02() throws IOException, InterruptedException {
        String srcFilename = "src_rotatedFieldAppearanceTest02.pdf";
        String filename = "rotatedFieldAppearanceTest02.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void degeneratedRectTest01() throws IOException, InterruptedException {
        String srcFilename = "src_degeneratedRectTest01.pdf";
        String filename = "degeneratedRectTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void degeneratedRectTest02() throws IOException, InterruptedException {
        String srcFilename = "src_degeneratedRectTest02.pdf";
        String filename = "degeneratedRectTest02.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    @Test
    public void scaledRectTest01() throws IOException, InterruptedException {
        String srcFilename = "src_scaledRectTest01.pdf";
        String filename = "scaledRectTest01.pdf";

        flattenFieldsAndCompare(srcFilename, filename);
    }

    private static void flattenFieldsAndCompare(String srcFile, String outFile) throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + srcFile);
        PdfWriter writer = new PdfWriter(destinationFolder + outFile);
        PdfDocument document = new PdfDocument(reader, writer);
        PdfAcroForm.getAcroForm(document, false).flattenFields();

        document.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destinationFolder + outFile, sourceFolder + "cmp_" + outFile, destinationFolder, "diff_");

        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void fieldsJustificationTest01() throws IOException, InterruptedException {
        fillTextFieldsThenFlattenThenCompare("fieldsJustificationTest01");
    }

    @Test
    public void fieldsJustificationTest02() throws IOException, InterruptedException {
        fillTextFieldsThenFlattenThenCompare("fieldsJustificationTest02");
    }

    private static void fillTextFieldsThenFlattenThenCompare(String testName) throws IOException, InterruptedException {
        String src = sourceFolder + "src_" + testName + ".pdf";
        String dest = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        for (PdfFormField field : form.getFormFields().values()) {
            if (field instanceof PdfTextFormField) {
                String newValue;
                if (field.isMultiline()) {
                    newValue = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
                } else {
                    newValue = "HELLO!";
                }

                Integer justification = field.getJustification();
                if (null == justification || 0 == (int) justification) {
                    field.setBackgroundColor(new DeviceRgb(255, 200, 200)); // reddish
                } else if (1 == (int) justification) {
                    field.setBackgroundColor(new DeviceRgb(200, 255, 200)); // greenish
                } else if (2 == (int) justification) {
                    field.setBackgroundColor(new DeviceRgb(200, 200, 255)); // blueish
                }

                field.setValue(newValue);
            }
        }
        form.flattenFields();
        doc.close();


        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 3)})
    //Logging is expected since there are duplicate field names
    //isReadOnly should be true after DEVSIX-2156
    public void flattenReadOnly() throws IOException{
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfReader reader = new PdfReader(sourceFolder + "readOnlyForm.pdf");
        PdfDocument pdfInnerDoc = new PdfDocument(reader);
        pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        pdfInnerDoc.close();
        reader = new PdfReader(sourceFolder + "readOnlyForm.pdf");
        pdfInnerDoc = new PdfDocument(reader);
        pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        pdfInnerDoc.close();
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);
        boolean isReadOnly = true;
        for (PdfFormField field : form.getFormFields().values()){
            isReadOnly = (isReadOnly && field.isReadOnly());
        }
        pdfDoc.close();
        Assert.assertFalse(isReadOnly);
    }

    @Test
    public void fieldsRegeneratePushButtonWithoutCaption() throws IOException, InterruptedException {
        fillTextFieldsThenFlattenThenCompare("pushbutton_without_caption");
    }

}
