package com.itextpdf.brotlicompressor;

import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.IStreamCompressionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class BrotliStreamCompressionStrategyTest extends ExtendedITextTest {


    @Test
    public void generateSimplePdf() throws IOException {
        runTest((pdfDocument -> {
            Document layoutDoc = new Document(pdfDocument);
            Table table = new Table(3);
            for (int i = 0; i < 3000; i++) {
                table.addCell("Cell " + (i + 1) + ", 1");
                table.addCell("Cell " + (i + 1) + ", 2");
                table.addCell("Cell " + (i + 1) + ", 3");
            }
            layoutDoc.add(table);
            layoutDoc.close();
        }));

    }

    @Test
    public void generateSimplePdfHighCompression() throws IOException {
        runTest((pdfDocument -> {
            pdfDocument.getWriter().setCompressionLevel(9);
            Document layoutDoc = new Document(pdfDocument);
            Table table = new Table(3);
            for (int i = 0; i < 300; i++) {
                table.addCell("Cell " + (i + 1) + ", 1");
                table.addCell("Cell " + (i + 1) + ", 2");
                table.addCell("Cell " + (i + 1) + ", 3");
            }
            layoutDoc.add(table);
            layoutDoc.close();
        }));
    }

    @Test
    public void generateSimplePdfLowCompression() throws IOException {
        runTest((pdfDocument -> {
            pdfDocument.getWriter().setCompressionLevel(1);
            Document layoutDoc = new Document(pdfDocument);
            Table table = new Table(3);
            for (int i = 0; i < 3000; i++) {
                table.addCell("Cell " + (i + 1) + ", 1");
                table.addCell("Cell " + (i + 1) + ", 2");
                table.addCell("Cell " + (i + 1) + ", 3");
            }
            layoutDoc.add(table);
            layoutDoc.close();
        }));
    }


    @Test
    public void comparePdfStreamsTest() throws IOException {
        // Create PDF with Brotli compression

        ByteArrayOutputStream brotliBaos = new ByteArrayOutputStream();
        DocumentProperties props = new DocumentProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());
        PdfDocument brotliPdfDoc = new PdfDocument(new PdfWriter(brotliBaos), props);

        Document brotliLayoutDoc = new Document(brotliPdfDoc);
        Table brotliTable = new Table(3);
        for (int i = 0; i < 1000; i++) {
            brotliTable.addCell("Cell " + (i + 1) + ", Column 1");
            brotliTable.addCell("Cell " + (i + 1) + ", Column 2");
            brotliTable.addCell("Cell " + (i + 1) + ", Column 3");
        }
        brotliLayoutDoc.add(brotliTable);
        brotliLayoutDoc.close();

        long brotliSize = brotliBaos.toByteArray().length;

        // Create PDF with Flate compression
        ByteArrayOutputStream flateBaos = new ByteArrayOutputStream();
        PdfDocument flatePdfDoc = new PdfDocument(new PdfWriter(flateBaos));

        Document flateLayoutDoc = new Document(flatePdfDoc);
        Table flateTable = new Table(3);
        for (int i = 0; i < 1000; i++) {
            flateTable.addCell("Cell " + (i + 1) + ", Column 1");
            flateTable.addCell("Cell " + (i + 1) + ", Column 2");
            flateTable.addCell("Cell " + (i + 1) + ", Column 3");
        }
        flateLayoutDoc.add(flateTable);
        flateLayoutDoc.close();

        long flateSize = flateBaos.toByteArray().length;

        // Verify both PDFs were created successfully
        Assertions.assertTrue(brotliSize > 0, "Brotli compressed PDF should not be empty");
        Assertions.assertTrue(flateSize > 0, "Flate compressed PDF should not be empty");

        // Verify both PDFs can be read back
        PdfDocument brotliReadDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(brotliBaos.toByteArray())));
        Assertions.assertEquals(30, brotliReadDoc.getNumberOfPages(),
                "Brotli PDF should have 30 pages");

        PdfDocument flateReadDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(flateBaos.toByteArray())));
        Assertions.assertEquals(30, flateReadDoc.getNumberOfPages(),
                "Flate PDF should have 30 pages");

        //loop over each page and compare the content streams
        for (int i = 1; i <= brotliReadDoc.getNumberOfPages(); i++) {
            PdfStream brotliContentStream = brotliReadDoc.getPage(i).getContentStream(0);
            PdfStream flateContentStream = flateReadDoc.getPage(i).getContentStream(0);

            byte[] brotliBytes = brotliContentStream.getBytes();
            byte[] flateBytes = flateContentStream.getBytes();

            Assertions.assertArrayEquals(flateBytes, brotliBytes,
                    "Content streams of page " + i + " should be identical between Brotli and Flate PDFs");
        }

        brotliReadDoc.close();
        flateReadDoc.close();
    }

    private void runTest(Consumer<PdfDocument> testRunner) throws IOException {
        long startTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DocumentProperties props = new DocumentProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos), props);
        testRunner.accept(pdfDoc);
        long length = baos.toByteArray().length;
        long endTime = SystemUtil.currentTimeMillis();

        long startFlateTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream flateBaos = new ByteArrayOutputStream();
        PdfDocument flatePdfDoc = new PdfDocument(new PdfWriter(flateBaos));
        testRunner.accept(flatePdfDoc);
        long flateLength = flateBaos.toByteArray().length;

        System.out.println(
                "Generated PDF size with Brotli compression: " + length + " bytes" + " in " + (endTime - startTime)
                        + " ms");
        System.out.println("Generated PDF size with Flate  compression: " + flateLength + " bytes" + " in " + (
                SystemUtil.currentTimeMillis() - startFlateTime) + " ms");

        double ratio = (double) flateLength / length;
        System.out.println("Compression ratio (Flate / Brotli): " + ratio);

        PdfDocument readDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        PdfDocument flateReadDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(flateBaos.toByteArray())));

        int numberOfPdfObjects = readDoc.getNumberOfPdfObjects();
        int numberOfFlatePdfObjects = flateReadDoc.getNumberOfPdfObjects();
        Assertions.assertEquals(numberOfFlatePdfObjects, numberOfPdfObjects,
                "Number of PDF objects should be the same in both documents");

        for (int i = 1; i < numberOfPdfObjects; i++) {
            PdfObject obj = readDoc.getPdfObject(i);
            PdfObject flateObj = flateReadDoc.getPdfObject(i);

            if (obj instanceof PdfStream && flateObj instanceof PdfStream) {
                byte[] brotliBytes = ((PdfStream) obj).getBytes();
                byte[] flateBytes = ((PdfStream) flateObj).getBytes();
                Assertions.assertArrayEquals(flateBytes, brotliBytes,
                        "PDF stream bytes should be identical for object number " + i);
            }
        }
    }
}