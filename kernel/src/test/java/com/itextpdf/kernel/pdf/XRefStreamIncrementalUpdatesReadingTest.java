package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XRefStreamIncrementalUpdatesReadingTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/XrefStreamIncrementalUpdatesReadingTest/";

    @Test
    public void freeRefReusingInAppendModeTest() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader
                (sourceFolder + "freeRefReusingInAppendMode.pdf"));

        PdfArray array = (PdfArray)  document.getCatalog().getPdfObject()
                .get(new PdfName("CustomKey"));

        Assert.assertTrue(array instanceof PdfArray);
        Assert.assertEquals(0, array.size());
    }
}
