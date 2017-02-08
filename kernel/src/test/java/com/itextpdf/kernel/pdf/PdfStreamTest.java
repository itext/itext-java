package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PdfStreamTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStreamTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStreamTest/";

    @Before
    public void before() {
        createOrClearDestinationFolder(destinationFolder);
    }


    @Test
    public void streamAppendDataOnJustCopiedWithCompression() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "pageWithContent.pdf";
        String cmpFile = sourceFolder + "cmp_streamAppendDataOnJustCopiedWithCompression.pdf";
        String destFile = destinationFolder + "streamAppendDataOnJustCopiedWithCompression.pdf";

        PdfDocument srcDocument = new PdfDocument(new PdfReader(srcFile));
        PdfDocument document = new PdfDocument(new PdfWriter(destFile));
        srcDocument.copyPagesTo(1, 1, document);
        srcDocument.close();

        String newContentString = "BT\n" +
                "/F1 36 Tf\n" +
                "50 700 Td\n" +
                "(new content here!) Tj\n" +
                "ET";
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        document.getPage(1).getLastContentStream().setData(newContent, true);

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }
}
