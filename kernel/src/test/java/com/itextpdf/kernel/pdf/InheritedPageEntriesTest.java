package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static com.itextpdf.test.ITextTest.createOrClearDestinationFolder;

@Category(IntegrationTest.class)
public class InheritedPageEntriesTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/InheritedPageEntriesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/InheritedPageEntriesTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    //TODO: update cmp-files when DEVSIX-3635 will be fixed
    public void addNewPageToDocumentWithInheritedPageRotationTest() throws InterruptedException, IOException {
        String inputFileName = sourceFolder + "srcFileTestRotationInheritance.pdf";
        String outputFileName = destinationFolder + "addNewPageToDocumentWithInheritedPageRotation.pdf";
        String cmpFileName = sourceFolder + "cmp_addNewPageToDocumentWithInheritedPageRotation.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName), new PdfWriter(outputFileName));

        PdfPage page = outFile.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Hello Helvetica!")
                .endText()
                .saveState();

        outFile.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, destinationFolder));
    }
}