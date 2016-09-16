package com.itextpdf.forms.xfa;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class XFAFormTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/xfa/XFAFormTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/xfa/XFAFormTest/";
    public static final String XML = sourceFolder + "xfa.xml";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createEmptyXFAFormTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createEmptyXFAFormTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_createEmptyXFAFormTest01.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outFileName));
        XfaForm xfa = new XfaForm(doc);
        XfaForm.setXfaForm(xfa, doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void createEmptyXFAFormTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createEmptyXFAFormTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_createEmptyXFAFormTest02.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outFileName));
        XfaForm xfa = new XfaForm();
        XfaForm.setXfaForm(xfa, doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void createXFAFormTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createXFAFormTest.pdf";
        String cmpFileName = sourceFolder + "cmp_createXFAFormTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outFileName));
        XfaForm xfa = new XfaForm(new FileInputStream(XML));
        xfa.write(doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


}
