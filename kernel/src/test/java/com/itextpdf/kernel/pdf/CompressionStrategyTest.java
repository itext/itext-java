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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.IFinishable;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Tag("IntegrationTest")
public class CompressionStrategyTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/CompressionStrategyTest/";

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/kernel/pdf/CompressionStrategyTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    private static final String testString = "Some test string for testing compression strategy should be big enough";

    private static final byte[] TEST_CONTENT_STREAM_DATA = ("q\n"
            + "0 0 0 RG\n"
            + "10 w\n"
            + "1 j\n"
            + "2 J\n"
            + "100 100 m\n"
            + "300 100 l\n"
            + "200 300 l\n"
            + "s\n"
            + "1 0 0 RG\n"
            + "200 50 m\n"
            + "200 350 l\n"
            + "S\n"
            + "Q\n"
    ).getBytes(StandardCharsets.US_ASCII);

    public static Iterable<Object[]> compressionStrategiesArguments() {
        return Arrays.asList(
                new Object[]{
                        new ASCII85CompressionStrategy(),
                        "ASCII85"
                },
                new Object[]{
                        new ASCIIHexCompressionStrategy(),
                        "ASCIIHex"
                },
                new Object[]{
                        new RunLengthCompressionStrategy(),
                        "RunLength"
                }
        );
    }

    public static Iterable<Object[]> twoCompressionStrategies() {
        return Arrays.asList(
                new Object[]{
                        new RunLengthCompressionStrategy(),
                        new ASCII85CompressionStrategy(),
                        "Run length on ASCII85"
                },
                new Object[]{
                        new ASCII85CompressionStrategy(),
                        new ASCIIHexCompressionStrategy(),
                        "ASCII85 on ASCIIHex"
                },
                new Object[]{
                        new ASCIIHexCompressionStrategy(),
                        new RunLengthCompressionStrategy(),
                        "ASCIIHex on RunLength"
                }
        );
    }

    @Test
    public void ascii85DecodeTest() throws IOException {
        doStrategyTest(new ASCII85CompressionStrategy());
    }

    @Test
    public void asciiHexDecodeTest() throws IOException {
        doStrategyTest(new ASCIIHexCompressionStrategy());
    }

    @Test
    public void flateDecodeTest() throws IOException {
        doStrategyTest(new FlateCompressionStrategy());
    }

    @Test
    public void runLengthDecodeTest() throws IOException {
        doStrategyTest(new RunLengthCompressionStrategy());
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("compressionStrategiesArguments")
    public void addStreamCompressionStampingModeTest(IStreamCompressionStrategy strategy, String compressionName) throws IOException {
        String resultPath = DESTINATION_FOLDER + "stamped" + compressionName + "Streams.pdf";
        StampingProperties props = new StampingProperties();
        props.registerDependency(IStreamCompressionStrategy.class, strategy);
        int streamCount = 3;
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + compressionName + "ContentStream.pdf"),
                new PdfWriter(resultPath), props)) {
            PdfPage page = pdfDoc.addNewPage();


            PdfStream stream = new PdfStream(testString.getBytes(StandardCharsets.UTF_8), CompressionConstants.BEST_COMPRESSION);
            stream.makeIndirect(pdfDoc);
            PdfArray contents = new PdfArray();
            contents.add(page.getPdfObject().get(PdfName.Contents));

            for (int i = 0; i < streamCount; i++) {
                contents.add(stream.getIndirectReference());
            }
            page.getPdfObject().put(PdfName.Contents, contents);
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(resultPath), props)) {
            PdfPage page = pdfDoc.getPage(2);
            Assertions.assertEquals(streamCount + 1, page.getContentStreamCount());

            for (int i = 1; i < page.getContentStreamCount(); i++) {
                PdfObject filterObject = page.getContentStream(i).get(PdfName.Filter);
                Assertions.assertEquals(strategy.getFilterName(), filterObject);
                Assertions.assertArrayEquals(page.getContentStream(i).getBytes(),
                        testString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }


    @ParameterizedTest(name = "{2}")
    @MethodSource("twoCompressionStrategies")
    public void twoFiltersInSingleStreamTest(IStreamCompressionStrategy firstStrategy, IStreamCompressionStrategy secondStrategy, String testName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StampingProperties props = new StampingProperties();
        props.registerDependency(IStreamCompressionStrategy.class, firstStrategy);

        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfWriter(baos), props)) {
            PdfPage page = pdfDoc.addNewPage();

            PdfCanvas canvas = new PdfCanvas(page);
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
            canvas.moveText(50, 700);
            canvas.showText(testString);
            canvas.endText();
            canvas.release();

            PdfStream contentStream = page.getContentStream(0);
            contentStream.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            OutputStream zip = secondStrategy.createNewOutputStream(byteArrayStream, contentStream);
            ((ByteArrayOutputStream) contentStream.getOutputStream().getOutputStream()).writeTo(zip);
            ((IFinishable) zip).finish();

            contentStream.setData(byteArrayStream.toByteArray());
            PdfArray filters = new PdfArray();
            filters.add(secondStrategy.getFilterName());
            contentStream.put(PdfName.Filter, filters);
        }

        try (PdfDocument readDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {

            PdfStream stream = readDoc.getFirstPage().getContentStream(0);

            PdfArray filters = stream.getAsArray(PdfName.Filter);
            Assertions.assertEquals(firstStrategy.getFilterName(), filters.getAsName(0));
            Assertions.assertEquals(secondStrategy.getFilterName(), filters.getAsName(1));

            byte[] decoded = stream.getBytes();

            String decodedText = new String(decoded, StandardCharsets.UTF_8);
            Assertions.assertTrue(decodedText.contains(testString));
        }

    }

    private static void doStrategyTest(IStreamCompressionStrategy strategy) throws IOException {
        long writePlainTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream plainPdfBytes = new ByteArrayOutputStream();
        writeTestDocument(plainPdfBytes, null, CompressionConstants.NO_COMPRESSION);
        byte[] bytes = plainPdfBytes.toByteArray();
        long plainSize = bytes.length;
        writePlainTime = SystemUtil.currentTimeMillis() - writePlainTime;
        System.out.println(
                "Generated PDF size without compression: "
                        + plainSize + " bytes in " + writePlainTime + "ms"
        );

        long writeCompressedTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream compressedPdfBytes = new ByteArrayOutputStream();
        writeTestDocument(compressedPdfBytes, strategy, CompressionConstants.DEFAULT_COMPRESSION);
        byte[] compressedBytes = compressedPdfBytes.toByteArray();
        long compressedSize = compressedBytes.length;
        writeCompressedTime = SystemUtil.currentTimeMillis() - writeCompressedTime;
        System.out.println(
                "Generated PDF with `" + strategy.getFilterName() + "` compression: "
                        + compressedSize + " bytes in " + writeCompressedTime + "ms"
        );

        System.out.println("Compression ratio: " + ((double) compressedSize / plainSize));

        PdfDocument plainDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(compressedBytes)));
        PdfDocument compressedDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));

        int numberOfPdfObjects = plainDoc.getNumberOfPdfObjects();
        Assertions.assertEquals(
                numberOfPdfObjects, compressedDoc.getNumberOfPdfObjects(),
                "Number of PDF objects should be the same in both documents"
        );

        for (int objNum = 1; objNum <= numberOfPdfObjects; ++objNum) {
            PdfObject plainObj = plainDoc.getPdfObject(objNum);
            PdfObject compressedObj = compressedDoc.getPdfObject(objNum);
            Assertions.assertEquals(
                    getType(plainObj), getType(compressedObj),
                    "PDF object type should be identical for object number " + objNum
            );
            if ((plainObj instanceof PdfStream) && (compressedObj instanceof PdfStream)) {
                byte[] plainStreamBytes = ((PdfStream) plainObj).getBytes();
                byte[] compressedStreamBytes = ((PdfStream) compressedObj).getBytes();
                Assertions.assertArrayEquals(
                        plainStreamBytes, compressedStreamBytes,
                        "PDF stream bytes should be identical for object number " + objNum
                );
            }
        }
    }

    private static void writeTestDocument(
            ByteArrayOutputStream os,
            IStreamCompressionStrategy strategy,
            int compressionLevel
    ) {
        DocumentProperties docProps = new DocumentProperties();
        if (strategy != null) {
            docProps.registerDependency(IStreamCompressionStrategy.class, strategy);
        }
        WriterProperties writerProps = new WriterProperties()
                .setCompressionLevel(compressionLevel);
        try (PdfDocument doc = new PdfDocument(new PdfWriter(os, writerProps), docProps)) {
            PdfPage page = doc.addNewPage();
            page.getFirstContentStream().setData(TEST_CONTENT_STREAM_DATA);
        }
    }

    private static byte getType(PdfObject obj) {
        if (obj == null) {
            return (byte) 0;
        }
        return obj.getType();
    }
}
