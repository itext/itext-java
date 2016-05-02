package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileOutputStream;
import java.text.MessageFormat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfActionTest  extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfActionTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfActionTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void actionTest01() throws Exception {
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "actionTest01.pdf"));
        PdfDocument document = createDocument(writer, true);

        document.getCatalog().setOpenAction(PdfAction.createURI("http://itextpdf.com/"));

        document.close();

        System.out.println(MessageFormat.format("Please open document {0} and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest01.pdf", "http://itextpdf.com"));
    }

    @Test
    public void actionTest02() throws Exception {

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "actionTest02.pdf"));
        PdfDocument document = createDocument(writer, false);

        document.getPage(2).setAdditionalAction(PdfName.O, PdfAction.createURI("http://itextpdf.com/"));

        document.close();

        System.out.println(MessageFormat.format("Please open document {0} at page 2 and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest02.pdf", "http://itextpdf.com"));

    }

    private PdfDocument createDocument(PdfWriter writer, boolean flushPages) {
        PdfDocument document = new PdfDocument(writer);
        PdfPage p1 = document.addNewPage();
        PdfStream str1 = p1.getFirstContentStream();
        str1.getOutputStream().writeString("1 0 0 rg 100 600 100 100 re f\n");
        if (flushPages)
            p1.flush();
        PdfPage p2 = document.addNewPage();
        PdfStream str2 = p2.getFirstContentStream();
        str2.getOutputStream().writeString("0 1 0 rg 100 600 100 100 re f\n");
        if (flushPages)
            p2.flush();
        PdfPage p3 = document.addNewPage();
        PdfStream str3 = p3.getFirstContentStream();
        str3.getOutputStream().writeString("0 0 1 rg 100 600 100 100 re f\n");
        if (flushPages)
            p3.flush();
        return document;
    }

}
