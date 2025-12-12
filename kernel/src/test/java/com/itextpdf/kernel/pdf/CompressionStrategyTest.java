/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class CompressionStrategyTest extends ExtendedITextTest {
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

    private static void doStrategyTest(IStreamCompressionStrategy strategy) throws IOException {
        long writePlainTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream plainPdfBytes = new ByteArrayOutputStream();
        writeTestDocument(plainPdfBytes, null, CompressionConstants.NO_COMPRESSION);
        long plainSize = plainPdfBytes.size();
        writePlainTime = SystemUtil.currentTimeMillis() - writePlainTime;
        System.out.println(
                "Generated PDF size without compression: "
                        + plainSize + " bytes in " + writePlainTime + "ms"
        );

        long writeCompressedTime = SystemUtil.currentTimeMillis();
        ByteArrayOutputStream compressedPdfBytes = new ByteArrayOutputStream();
        writeTestDocument(compressedPdfBytes, strategy, CompressionConstants.DEFAULT_COMPRESSION);
        long compressedSize = compressedPdfBytes.size();
        writeCompressedTime = SystemUtil.currentTimeMillis() - writeCompressedTime;
        System.out.println(
                "Generated PDF with `" + strategy.getFilterName() + "` compression: "
                        + compressedSize + " bytes in " + writeCompressedTime + "ms"
        );

        System.out.println("Compression ratio: " + ((double) compressedSize / plainSize));

        PdfDocument plainDoc = new PdfDocument(new PdfReader(
                new ByteArrayInputStream(compressedPdfBytes.toByteArray())
        ));
        PdfDocument compressedDoc = new PdfDocument(new PdfReader(
                new ByteArrayInputStream(plainPdfBytes.toByteArray())
        ));

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
            return -1;
        }
        return obj.getType();
    }
}
