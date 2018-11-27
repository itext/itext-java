package com.itextpdf.forms;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class FlatteningTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FlatteningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FlatteningTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void formFlatteningTestWithAPWithoutSubtype() throws IOException, InterruptedException {
        String filename = "job_application_filled";
        String src = sourceFolder + filename + ".pdf";
        String dest = destinationFolder + filename + "_flattened.pdf";
        String cmp = sourceFolder + "cmp_" + filename + "_flattened.pdf";
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        PdfAcroForm.getAcroForm(doc, false).flattenFields();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

}
