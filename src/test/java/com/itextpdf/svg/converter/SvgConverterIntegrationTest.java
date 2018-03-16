package com.itextpdf.svg.converter;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class SvgConverterIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/converter/SvgConverterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/converter/SvgConverterTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void unusedXObjectIntegrationTest() throws IOException, InterruptedException {
        // This method tests that making an XObject does not, in itself, influence the document it's for.
        PdfDocument doc1 = new PdfDocument(new PdfWriter(destinationFolder + "unusedXObjectIntegrationTest1.pdf"));
        PdfDocument doc2 = new PdfDocument(new PdfWriter(destinationFolder + "unusedXObjectIntegrationTest2.pdf"));
        doc1.addNewPage();
        doc2.addNewPage();

        SvgConverter.convertToXObject("<svg width='100pt' height='100pt' />", doc1);

        doc1.close();
        doc2.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "unusedXObjectIntegrationTest1.pdf", destinationFolder + "unusedXObjectIntegrationTest2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void basicIntegrationTest() throws IOException, InterruptedException {
        String filename = "basicIntegrationTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        PdfFormXObject form = SvgConverter.convertToXObject("<svg width='100pt' height='100pt' />", doc);

        new PdfCanvas(doc.getPage(1)).addXObject(form, new Rectangle(100, 100, 100, 100));
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void nonExistingTagIntegrationTest() throws InterruptedException {
        junitExpectedException.expect(SvgProcessingException.class);
        String contents = "<svg width='100pt' height='100pt'> <nonExistingTag/> </svg>";
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();

        try {
            SvgConverter.convertToXObject(contents, doc);
        } finally {
            doc.close();
        }
    }
}
