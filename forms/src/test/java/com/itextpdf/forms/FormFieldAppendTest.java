package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.File;

@Category(IntegrationTest.class)
public class FormFieldAppendTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldAppendTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldAppendTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFillingAppend_form_empty_Test() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "Form_Empty.pdf";
        String temp = destinationFolder + "temp_empty.pdf";
        String filename = destinationFolder + "formFillingAppend_form_empty.pdf";
        StampingProperties props = new StampingProperties();
        props.useAppendMode();

        PdfDocument doc = new PdfDocument(new PdfReader(srcFilename), new PdfWriter(temp), props);

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        for (PdfFormField field : form.getFormFields().values()) {
            field.setValue("Test");
        }

        doc.close();

        flatten(temp, filename);

        File toDelete = new File(temp);
        toDelete.delete();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFillingAppend_form_empty.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formFillingAppend_form_filled_Test() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "Form_Empty.pdf";
        String temp = destinationFolder + "temp_filled.pdf";
        String filename = destinationFolder + "formFillingAppend_form_filled.pdf";
        StampingProperties props = new StampingProperties();
        props.useAppendMode();

        PdfDocument doc = new PdfDocument(new PdfReader(srcFilename), new PdfWriter(temp), props);

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        for (PdfFormField field : form.getFormFields().values()) {
            field.setValue("Different");
        }

        doc.close();

        flatten(temp, filename);

        new File(temp).delete();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFillingAppend_form_filled.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    private void flatten(String src, String dest) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        form.flattenFields();
        doc.close();
    }
}
