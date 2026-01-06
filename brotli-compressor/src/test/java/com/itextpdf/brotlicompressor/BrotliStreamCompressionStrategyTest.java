/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.brotlicompressor;

import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.IFinishable;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.FlateCompressionStrategy;
import com.itextpdf.kernel.pdf.IStreamCompressionStrategy;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.filters.BrotliFilter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class BrotliStreamCompressionStrategyTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/brotli-compressor"
            + "/BrotliStreamCompressionStrategyTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/brotli-compressor/BrotliStreamCompressionStrategyTest/";

    private static final String testString = "The quick brown fox jumps over the lazy dog. "
            + "And accented words like naïve, façade, and piñata help validate Unicode handling";
    private static final int streamCount = 10;

    @BeforeAll
    public static void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void generateSimplePdfTest() throws IOException, InterruptedException {
        String fileName = "simpleBrotli.pdf";
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
        }), fileName);

    }

    @Test
    public void basicBrotliContentStreamTest() throws IOException, InterruptedException {
        String fileName = "simpleBrotliContentStream.pdf";
        runTest((pdfDocument -> {
            PdfPage page = pdfDocument.addNewPage();
            PdfStream pdfStream = new PdfStream(testString.getBytes(StandardCharsets.UTF_8),
                    CompressionConstants.BEST_COMPRESSION);
            pdfStream.makeIndirect(pdfDocument);
            PdfArray contents = new PdfArray();
            contents.add(pdfStream.getIndirectReference());
            page.getPdfObject().put(PdfName.Contents, contents);
        }), fileName);
    }

    @Test
    public void addBrotliStreamsToFlateStampingModeTest() throws IOException {
        String resultPath = DESTINATION_FOLDER + "stampedBrotliStreams.pdf";
        StampingProperties props = new StampingProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "flateBase.pdf"),
                new PdfWriter(resultPath), props)) {
            PdfPage page = pdfDoc.addNewPage();
            PdfStream brotliStream = createBrotliContentStream(pdfDoc,
                    testString.getBytes(StandardCharsets.UTF_8));

            PdfArray contents = createContentArrayIfNotFound(page);

            for (int i = 0; i < streamCount; i++) {
                contents.add(brotliStream.getIndirectReference());
            }
            page.getPdfObject().put(PdfName.Contents, contents);
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(resultPath), props)) {
            PdfPage page = pdfDoc.getFirstPage();

            Assertions.assertEquals(1, page.getContentStreamCount());
            PdfObject filterObject = page.getFirstContentStream().get(PdfName.Filter);
            Assertions.assertEquals(PdfName.FlateDecode, filterObject);

            page = pdfDoc.getPage(2);
            Assertions.assertEquals(streamCount + 1, page.getContentStreamCount());

            for (int i = 1; i < page.getContentStreamCount(); i++) {
                filterObject = page.getContentStream(i).get(PdfName.Filter);
                Assertions.assertEquals(PdfName.BrotliDecode, filterObject);
                Assertions.assertArrayEquals(page.getContentStream(i).getBytes(),
                        testString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void addBrotliStreamsStampingModeTest() throws IOException {
        String resultPath = DESTINATION_FOLDER + "stampedBrotliStreams2.pdf";
        StampingProperties props = new StampingProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "brotliBase.pdf"),
                new PdfWriter(resultPath), props)) {
            PdfPage page = pdfDoc.addNewPage();

            PdfStream brotliStream = createBrotliContentStream(pdfDoc,
                    testString.getBytes(StandardCharsets.UTF_8));
            PdfArray contents = createContentArrayIfNotFound(page);

            for (int i = 0; i < streamCount; i++) {
                contents.add(brotliStream.getIndirectReference());
            }
            page.getPdfObject().put(PdfName.Contents, contents);
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(resultPath), props)) {
            PdfPage page = pdfDoc.getPage(2);
            Assertions.assertEquals(streamCount + 1, page.getContentStreamCount());

            for (int i = 1; i < page.getContentStreamCount(); i++) {
                PdfObject filterObject = page.getContentStream(i).get(PdfName.Filter);
                Assertions.assertEquals(PdfName.BrotliDecode, filterObject);
                Assertions.assertArrayEquals(page.getContentStream(i).getBytes(),
                        testString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void replaceFlateContentStreamWithBrotliTest() throws IOException {
        String resultPath = DESTINATION_FOLDER + "replacedContentStream.pdf";
        StampingProperties props = new StampingProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "flateBase.pdf"),
                new PdfWriter(resultPath), props)) {
            PdfPage page = pdfDoc.getFirstPage();

            PdfStream brotliContent = createBrotliContentStream(pdfDoc,
                    "Overwritten content".getBytes(StandardCharsets.UTF_8));

            page.getPdfObject().put(PdfName.Contents, brotliContent.getIndirectReference());
            page.setModified();
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(resultPath), props)) {
            PdfPage page = pdfDoc.getFirstPage();

            Assertions.assertEquals(1, page.getContentStreamCount());
            PdfObject filterObject = page.getFirstContentStream().get(PdfName.Filter);
            Assertions.assertEquals(PdfName.BrotliDecode, filterObject);
        }
    }

    @Test
    public void readAndDecodeStreamsTest() throws IOException {
        String sourcePdf = SOURCE_FOLDER + "mixedStreamFiltersDocument.pdf";
        StampingProperties props = new StampingProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourcePdf))) {
            int numberOfPages = pdfDoc.getNumberOfPages();

            for (int i = 2; i <= numberOfPages; i++) {
                PdfStream contentStream = pdfDoc.getPage(i).getFirstContentStream();
                Assertions.assertArrayEquals(contentStream.getBytes(), testString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void singleContentStreamWithBrotliAndFlateTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DocumentProperties props = createBrotliProperties();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos), props)) {
            PdfPage page = pdfDoc.addNewPage();

            PdfCanvas canvas = new PdfCanvas(page);
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
            canvas.moveText(50, 700);
            canvas.showText(testString);
            canvas.endText();
            canvas.release();

            // Flate encode upfront
            PdfStream contentStream = page.getContentStream(0);
            contentStream.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            OutputStream zip = new FlateCompressionStrategy().createNewOutputStream(byteArrayStream, contentStream);
            ((ByteArrayOutputStream) contentStream.getOutputStream().getOutputStream()).writeTo(zip);
            ((IFinishable) zip).finish();

            contentStream.setData(byteArrayStream.toByteArray());
            PdfArray filters = new PdfArray();
            filters.add(PdfName.FlateDecode);
            contentStream.put(PdfName.Filter, filters);
        }

        try (PdfDocument readDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {

            PdfStream stream = readDoc.getFirstPage().getContentStream(0);

            PdfArray filters = stream.getAsArray(PdfName.Filter);
            Assertions.assertEquals(PdfName.BrotliDecode, filters.getAsName(0));
            Assertions.assertEquals(PdfName.FlateDecode, filters.getAsName(1));

            byte[] decoded = stream.getBytes();

            String decodedText = new String(decoded, StandardCharsets.UTF_8);
            Assertions.assertTrue(decodedText.contains("The quick brown fox jumps over the lazy dog. "));
        }
    }

    @Test
    @Disabled("DEVSIX-9595: This is a starting point test with brotli dictionaries."
            + " Dictionary is used for decompression but not for compression")
    public void brotliStreamWithDDictionaryTest() throws IOException {
        String resultPath = DESTINATION_FOLDER + "brotli_with_decodeParams_full.pdf";
        byte[] dictionaryBytes = "custom dictionary for Brotli".getBytes(StandardCharsets.UTF_8);
        byte[] contentBytes = "The quick brown fox jumps over the lazy dog.".getBytes(StandardCharsets.UTF_8);

        DocumentProperties props = new DocumentProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(resultPath), props)) {
            PdfPage page = pdfDoc.addNewPage();

            PdfStream dictStream = new PdfStream(dictionaryBytes);
            dictStream.makeIndirect(pdfDoc);

            PdfDictionary decodeParms = new PdfDictionary();
            decodeParms.put(PdfName.D, dictStream.getIndirectReference());

            PdfStream brotliStream = new PdfStream(contentBytes, CompressionConstants.BEST_COMPRESSION);
            brotliStream.makeIndirect(pdfDoc);

            // Brotli encode upfront
            brotliStream.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            OutputStream zip = new FlateCompressionStrategy().createNewOutputStream(byteArrayStream, brotliStream);
            ((ByteArrayOutputStream) brotliStream.getOutputStream().getOutputStream()).writeTo(zip);
            ((IFinishable) zip).finish();

            brotliStream.setData(byteArrayStream.toByteArray());
            brotliStream.put(PdfName.Filter, PdfName.BrotliDecode);
            brotliStream.put(PdfName.DecodeParms, decodeParms);

            page.getPdfObject().put(PdfName.Contents, brotliStream.getIndirectReference());
            page.setModified();
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(resultPath))) {
            PdfPage page = pdfDoc.getFirstPage();
            PdfStream stream = page.getFirstContentStream();

            PdfDictionary decodeParams = stream.getAsDictionary(PdfName.DecodeParms);

            BrotliFilter filter = new BrotliFilter();
            byte[] decoded = filter.decode(stream.getBytes(false), PdfName.BrotliDecode, decodeParams, stream);

            Assertions.assertArrayEquals(contentBytes, decoded,
                    "Decoded content should match original bytes");
        }
    }

    @Test
    public void addEmbeddedFileAndCompareTest() throws IOException, InterruptedException {
        String fileName = "simpleEmbeddedFile.pdf";
        runTest(pdfDoc ->
                addEmbeddedFile(pdfDoc, "example.txt",
                        "Hello from Brotli embedded file".getBytes(StandardCharsets.UTF_8)), fileName);
    }

    @Test
    public void addEmbeddedJsonFileWithBrotliTest() throws Exception {
        String fileName = "simpleJsonEmbedded.pdf";
        runTest(pdfDoc -> {
            String json = "{\"message\": \"Hello Brotli JSON\", \"id\": 42}";
            byte[] fileBytes = json.getBytes(StandardCharsets.UTF_8);
            addEmbeddedFile(pdfDoc, "JSON File", "data.json", fileBytes);
        }, fileName);
    }

    @Test
    public void addMultipleEmbeddedFilesWithBrotliTest() throws Exception {
        String fileName = "simpleBrotliEmbedded.pdf";
        runTest(pdfDoc -> {
            for (int i = 1; i <= 3; i++) {
                addEmbeddedFile(pdfDoc, "file" + i + ".txt",
                        ("File " + i).getBytes(StandardCharsets.UTF_8));
            }
        }, fileName);
    }

    @Test
    public void addEmbeddedUnicodeFileWithBrotliTest() throws Exception {
        String fileName = "simpleEmbeddedUnicode.pdf";
        runTest(pdfDoc -> {
            String unicode = "こんにちは世界 — Hello World 🌍";
            byte[] bytes = unicode.getBytes(StandardCharsets.UTF_16LE);
            addEmbeddedFile(pdfDoc, "Unicode Text", "unicode.txt", bytes);
        }, fileName);
    }

    @Test
    public void addRealEmbeddedFileAndCompareTest() throws IOException, InterruptedException {
        String fileName = "realEmbeddedFile.pdf";
        byte[] fileBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "hello.txt"));
        runTest(pdfDocument -> {
            addEmbeddedFile(pdfDocument, "Brotli Embedded File", "hello.txt", fileBytes);
        }, fileName);
    }

    @Test
    public void addEmbeddedFileAndBrotliContentStreamTest() throws Exception {
        String fileName = "embeddedFileAndContentStream.pdf";
        runTest(pdfDoc -> {
            byte[] fileBytes = "Embedded + Brotli content".getBytes(StandardCharsets.UTF_8);

            addEmbeddedFile(pdfDoc, "Brotli File", "embedded.txt", fileBytes);

            PdfPage page = pdfDoc.addNewPage();

            PdfStream brotliContent = createBrotliContentStream(pdfDoc,
                    "Hello from Brotli page stream".getBytes(StandardCharsets.UTF_8));

            PdfArray arr = new PdfArray();
            arr.add(brotliContent.getIndirectReference());
            page.getPdfObject().put(PdfName.Contents, arr);
        }, fileName);
    }

    @Test
    public void addEmbeddedBrotliFileTest() throws IOException {
        String dest = DESTINATION_FOLDER + "embedded_brotli.pdf";

        DocumentProperties props = createBrotliProperties();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest), props)) {
            byte[] fileBytes = "Hello from Brotli embedded file".getBytes(StandardCharsets.UTF_8);
            addEmbeddedFile(pdfDoc, "Brotli Embedded File", "example.txt", fileBytes);
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(dest))) {
            Map<String, PdfStream> embedded = getEmbeddedFiles(pdfDoc);

            for (Map.Entry<String, PdfStream> e : embedded.entrySet()) {
                String name = e.getKey();
                PdfStream stream = e.getValue();

                byte[] decoded = stream.getBytes();

                Assertions.assertEquals("example.txt", name);
                Assertions.assertEquals(PdfName.BrotliDecode, stream.getAsName(PdfName.Filter));
                Assertions.assertEquals(31, decoded.length);
            }
        }
    }

    @Test
    public void addFlateImageToBrotliPdfTest() throws IOException, InterruptedException {
        String fileName = "imgBrotli.pdf";
        byte[] imageBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "apryse.png"));

        runTest((pdfDocument -> {
            ImageData imageData = ImageDataFactory.create(imageBytes);

            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            canvas.addImageAt(imageData, 50, 700, false);
            canvas.release();
        }), fileName);
    }

    @Test
    public void addMultipleBrotliCompressedImagesAndDecodeTest() throws IOException {
        String[] images = {
                "apryse.png",
                "itext.png",
                "bulb.gif",
                "bee.jp2",
                "simple.bmp"
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DocumentProperties props = createBrotliProperties();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos), props)) {
            for (int i = 0; i < images.length; i++) {
                PdfPage page = pdfDoc.addNewPage();

                byte[] imgBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + images[i]));

                addBrotliImageXObject(pdfDoc, page, "Im1", imgBytes);
            }
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {
            for (int j = 1; j <= pdfDoc.getNumberOfPages(); j++) {
                PdfPage page = pdfDoc.getPage(j);

                byte[] original = Files.readAllBytes(Paths.get(SOURCE_FOLDER + images[j - 1]));
                byte[] decoded = readAndDecodeBrotliImage(page, new PdfName("Im1"));
                Assertions.assertArrayEquals(original, decoded);
            }
        }
    }

    @Test
    public void brotliInlineImagesProhibitedTest() throws IOException, InterruptedException {
        String fileName = "inlineImg.pdf";
        ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + "bulb.gif");
        runTest(pdfDoc -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.addImageFittedIntoRectangle(imageData, new Rectangle(36, 460, 100, 14.16f), true);
        }, fileName);
    }

    @Test
    public void comparePdfStreamsTest() throws IOException {
        // Create PDF with Brotli compression

        ByteArrayOutputStream brotliBaos = new ByteArrayOutputStream();
        DocumentProperties props = createBrotliProperties();
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

    private void runTest(Consumer<PdfDocument> testRunner, String brotliPdfPath)
            throws IOException, InterruptedException {
        long startTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DocumentProperties props = createBrotliProperties();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos), props);
        testRunner.accept(pdfDoc);
        long length = baos.toByteArray().length;
        pdfDoc.close();
        long endTime = SystemUtil.currentTimeMillis();

        long startFlateTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream flateBaos = new ByteArrayOutputStream();
        PdfDocument flatePdfDoc = new PdfDocument(new PdfWriter(flateBaos));
        testRunner.accept(flatePdfDoc);
        long flateLength = flateBaos.toByteArray().length;
        flatePdfDoc.close();

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

        for (int i = 1; i <= numberOfPdfObjects; i++) {
            PdfObject obj = readDoc.getPdfObject(i);
            PdfObject flateObj = flateReadDoc.getPdfObject(i);
            if (obj instanceof PdfStream && flateObj instanceof PdfStream) {
                byte[] brotliBytes = ((PdfStream) obj).getBytes();
                byte[] flateBytes = ((PdfStream) flateObj).getBytes();
                Assertions.assertArrayEquals(flateBytes, brotliBytes,
                        "PDF stream bytes should be identical for object number " + i);
            }
        }

        //compare PDF
        if (brotliPdfPath != null) {
            String destPath = DESTINATION_FOLDER + brotliPdfPath;
            String cmpPath = SOURCE_FOLDER + "cmp_" + brotliPdfPath;
            Files.write(Paths.get(destPath), baos.toByteArray());

            CompareTool compareTool = new CompareTool();
            String compareResult = compareTool.compareByContent(
                    destPath,
                    cmpPath,
                    DESTINATION_FOLDER,
                    "diff_"
            );
            Assertions.assertNull(compareResult, "Brotli PDF does not match reference PDF: " + compareResult);
        }

    }

    private PdfStream createBrotliContentStream(PdfDocument pdfDoc, byte[] data) {
        PdfStream stream = new PdfStream(data, CompressionConstants.BEST_COMPRESSION);
        stream.makeIndirect(pdfDoc);
        return stream;
    }

    private void addEmbeddedFile(
            PdfDocument pdfDoc,
            String displayName,
            String fileName,
            byte[] bytes
    ) {
        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(
                pdfDoc,
                bytes,
                displayName,
                fileName,
                null,
                null
        );
        pdfDoc.addFileAttachment(fileName, spec);
    }

    private void addBrotliImageXObject(PdfDocument pdfDoc, PdfPage page, String name, byte[] imgBytes) {
        PdfStream imgStream = new PdfStream(imgBytes, CompressionConstants.BEST_COMPRESSION);
        imgStream.makeIndirect(pdfDoc);

        PdfDictionary resources = page.getResources().getPdfObject();
        PdfDictionary xObjects = resources.getAsDictionary(PdfName.XObject);
        if (xObjects == null) {
            xObjects = new PdfDictionary();
            resources.put(PdfName.XObject, xObjects);
        }

        xObjects.put(new PdfName(name), imgStream);
    }

    private byte[] readAndDecodeBrotliImage(PdfPage page, PdfName imageName) {
        PdfDictionary xObjects = page.getResources()
                .getPdfObject()
                .getAsDictionary(PdfName.XObject);

        PdfStream stream = xObjects.getAsStream(imageName);
        return stream.getBytes();
    }

    private void addEmbeddedFile(PdfDocument pdfDoc, String name, byte[] bytes) {
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDoc, bytes, name, name, null, null);
        pdfDoc.addFileAttachment(name, fs);
    }

    private DocumentProperties createBrotliProperties() {
        DocumentProperties props = new DocumentProperties();
        props.registerDependency(IStreamCompressionStrategy.class, new BrotliStreamCompressionStrategy());
        return props;
    }

    private PdfArray createContentArrayIfNotFound(PdfPage page) {
        PdfArray contents;
        PdfObject existingContents = page.getPdfObject().get(PdfName.Contents);
        if (existingContents instanceof PdfArray) {
            contents = (PdfArray) existingContents;
        } else if (existingContents != null) {
            contents = new PdfArray();
            contents.add(existingContents);
        } else {
            contents = new PdfArray();
        }
        return contents;
    }

    private static Map<String, PdfStream> getEmbeddedFiles(PdfDocument pdfDoc) {
        Map<String, PdfStream> result = new HashMap<>();

        PdfDictionary catalog = pdfDoc.getCatalog().getPdfObject();
        PdfDictionary names = catalog.getAsDictionary(PdfName.Names);
        if (names == null) {
            return result;
        }

        PdfDictionary embeddedFiles = names.getAsDictionary(PdfName.EmbeddedFiles);
        if (embeddedFiles == null) {
            return result;
        }

        PdfArray nameArray = embeddedFiles.getAsArray(PdfName.Names);
        if (nameArray == null || nameArray.isEmpty()) {
            return result;
        }

        for (int i = 0; i < nameArray.size(); i += 2) {
            PdfString fileName = nameArray.getAsString(i);
            PdfDictionary fileSpec = nameArray.getAsDictionary(i + 1);

            if (fileName == null || fileSpec == null) {
                continue;
            }

            PdfDictionary efDict = fileSpec.getAsDictionary(PdfName.EF);
            if (efDict == null) {
                continue;
            }

            PdfStream stream = efDict.getAsStream(PdfName.UF);
            if (stream == null) {
                stream = efDict.getAsStream(PdfName.F);
            }
            if (stream == null) {
                continue;
            }

            result.put(fileName.toString(), stream);
        }

        return result;
    }
}
