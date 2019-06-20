package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SmartModeTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/SmartModeTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/SmartModeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void smartModeSameResourcesCopyingAndFlushing() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingAndFlushing.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(new PdfReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assert.assertTrue(page1ResFontObj.equals(page2ResFontObj));
        Assert.assertTrue(page1ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing() throws IOException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };
        boolean exceptionCaught = false;

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        int lastPageNum = 1;
        PdfFont font = PdfFontFactory.createFont();
        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            int i;
            for (i = lastPageNum; i <= outputDoc.getNumberOfPages(); ++i) {
                PdfCanvas canvas;
                try {
                    canvas = new PdfCanvas(outputDoc.getPage(i));
                } catch (NullPointerException expected) {
                    // Smart mode makes it possible to share objects coming from different source documents.
                    // Flushing one object documents might make it impossible to modify further copied objects.
                    Assert.assertEquals(2, i);
                    exceptionCaught = true;
                    break;
                }
                canvas.beginText().moveText(36, 36).setFontAndSize(font, 12).showText("Page " + i).endText();
            }
            lastPageNum = i;

            if (exceptionCaught) {
                break;
            }

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        if (!exceptionCaught) {
            Assert.fail();
        }
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        int lastPageNum = 1;
        PdfFont font = PdfFontFactory.createFont();
        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            for (int i = 1; i <= sourceDoc.getNumberOfPages(); ++i) {
                PdfDictionary srcRes = sourceDoc.getPage(i).getPdfObject().getAsDictionary(PdfName.Resources);

                // Ensures that objects copied to the output document are fresh,
                // i.e. are not reused from already copied objects cache.
                boolean ensureObjectIsFresh = true;
                // it's crucial to copy first inner objects and then the container object!
                for (PdfObject v : srcRes.values()) {
                    if (v.getIndirectReference() != null) {
                        // We are not interested in returned copied objects instances, they will be picked up by
                        // general copying mechanism from copied objects cache by default.
                        v.copyTo(outputDoc, ensureObjectIsFresh);
                    }
                }
                if (srcRes.getIndirectReference() != null) {
                    srcRes.copyTo(outputDoc, ensureObjectIsFresh);
                }
            }
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            int i;
            for (i = lastPageNum; i <= outputDoc.getNumberOfPages(); ++i) {
                PdfPage page = outputDoc.getPage(i);
                PdfCanvas canvas = new PdfCanvas(page);
                canvas.beginText().moveText(36, 36).setFontAndSize(font, 12).showText("Page " + i).endText();
            }
            lastPageNum = i;

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(new PdfReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assert.assertFalse(page1ResFontObj.equals(page2ResFontObj));
        Assert.assertFalse(page1ResFontObj.equals(page3ResFontObj));
        Assert.assertFalse(page2ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }
}
