package com.itextpdf.forms;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FormFieldFlatteningTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldFlatteningTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldFlatteningTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFlatteningTest01() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "formFlatteningSource.pdf";
        String filename = destinationFolder + "formFlatteningTest01.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)), new PdfWriter(new FileOutputStream(filename)));

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        form.flatFields();

        doc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFlatteningTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
