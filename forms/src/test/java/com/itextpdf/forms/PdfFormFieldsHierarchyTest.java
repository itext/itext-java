package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
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
public class PdfFormFieldsHierarchyTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldsHierarchyTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldsHierarchyTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillingFormWithKidsTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "formWithKids.pdf";
        String cmpPdf = sourceFolder + "cmp_fillingFormWithKidsTest.pdf";
        String outPdf = destinationFolder + "fillingFormWithKidsTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

        Map<String, PdfFormField> formFields = acroForm.getFormFields();

        for (String key : formFields.keySet()) {
            PdfFormField field = acroForm.getField(key);
            field.setValue(key);
        }

        pdfDocument.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}