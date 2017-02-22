package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class PdfDestinationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDestinationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDestinationTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void destTest01() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleNoLinks.pdf";
        String outFile = destinationFolder + "destTest01.pdf";
        String cmpFile = sourceFolder + "cmp_destTest01.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outFile));
        PdfPage firstPage = document.getPage(1);
        
        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkExplicitDest.setAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(document.getPage(2))));
        firstPage.addAnnotation(linkExplicitDest);
        
        PdfLinkAnnotation linkStringDest = new PdfLinkAnnotation(new Rectangle(35, 760, 160, 15));
        PdfExplicitDestination destToPage3 = PdfExplicitDestination.createFit(document.getPage(3));
        String stringDest = "thirdPageDest";
        document.addNamedDestination(stringDest, destToPage3.getPdfObject());
        linkStringDest.setAction(PdfAction.createGoTo(new PdfStringDestination(stringDest)));
        firstPage.addAnnotation(linkStringDest);
        
        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest01() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest01.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest01.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 3), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest02() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest02.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest02.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest03() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest03.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest03.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest04() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest04.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest04.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 3), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest05() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest05.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest05.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 3, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }
}
