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
public class PdfFormFieldTextTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldTextTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldTextTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillFormWithAutosizeTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithAutosizeTest.pdf";
        String inPdf = sourceFolder + "fillFormWithAutosizeSource.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithAutosizeTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);
        Map<String, PdfFormField> fields = form.getFormFields();
        fields.get("First field").setValue("name name name ");
        fields.get("Second field").setValue("surname surname surname surname surname surname");
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void defaultAppearanceExtractionForNotMergedFieldsTest() throws IOException, InterruptedException {
        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "sourceDAExtractionTest.pdf"),
                new PdfWriter(destinationFolder + "defaultAppearanceExtractionTest.pdf"));
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, false);
        form.getField("First field").setValue("Your name");
        form.getField("Text1").setValue("Your surname");
        doc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destinationFolder + "defaultAppearanceExtractionTest.pdf",
                sourceFolder + "cmp_defaultAppearanceExtractionTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}