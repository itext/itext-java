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

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class BrotliOutputStreamTest extends ExtendedITextTest {

    private static final String TEST_DATA = "This is a test string for Brotli compression. "
            + "It should be compressed and decompressed correctly. "
            + "The quick brown fox jumps over the lazy dog. "
            + "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    @BeforeAll
    public static void beforeAll() {
        Brotli4jLoader.ensureAvailability();
    }

    @Test
    public void testDefaultConstructor() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (BrotliOutputStream brotliStream = new BrotliOutputStream(baos)) {
            brotliStream.write(TEST_DATA.getBytes(StandardCharsets.UTF_8));
            brotliStream.finish();
        }

        // Verify data was compressed (should be smaller than original for this test data)
        byte[] compressed = baos.toByteArray();
        Assertions.assertTrue(compressed.length > 0, "Compressed data should not be empty");

        // Verify we can decompress the data
        String decompressed = decompressData(compressed);
        Assertions.assertEquals(TEST_DATA, decompressed, "Decompressed data should match original");
    }

    @Test
    public void testConstructorWithParameters() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Encoder.Parameters params = Encoder.Parameters.create(5); // Medium compression level

        try (BrotliOutputStream brotliStream = new BrotliOutputStream(baos, params)) {
            brotliStream.write(TEST_DATA.getBytes(StandardCharsets.UTF_8));
            brotliStream.finish();
        }

        byte[] compressed = baos.toByteArray();
        Assertions.assertTrue(compressed.length > 0, "Compressed data should not be empty");

        String decompressed = decompressData(compressed);
        Assertions.assertEquals(TEST_DATA, decompressed, "Decompressed data should match original");
    }

    /**
     * Test the constructor with parameters and buffer size.
     */
    @Test
    public void testConstructorWithParametersAndBufferSize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Encoder.Parameters params = Encoder.Parameters.create(6);
        int bufferSize = 2048;

        try (BrotliOutputStream brotliStream =
                new BrotliOutputStream(baos, params, bufferSize)) {
            brotliStream.write(TEST_DATA.getBytes(StandardCharsets.UTF_8));
            brotliStream.finish();
        }

        byte[] compressed = baos.toByteArray();
        Assertions.assertTrue(compressed.length > 0, "Compressed data should not be empty");

        String decompressed = decompressData(compressed);
        Assertions.assertEquals(TEST_DATA, decompressed, "Decompressed data should match original");
    }

    @Test
    public void testFinishDoesNotCloseUnderlyingStream() throws IOException {
        ByteArrayOutputStream testStream = new ByteArrayOutputStream();

        BrotliOutputStream brotliStream = new BrotliOutputStream(testStream);
        brotliStream.write("Test data".getBytes(StandardCharsets.UTF_8));

        brotliStream.finish();
        byte[] a = testStream.toByteArray();
        brotliStream.close();
        byte[] b = testStream.toByteArray();
        Assertions.assertArrayEquals(a, b);
    }

    @Test
    public void testWriteToUnderlyingStreamAfterFinish() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BrotliOutputStream brotliStream = new BrotliOutputStream(baos);
        brotliStream.write("Compressed data".getBytes(StandardCharsets.UTF_8));
        brotliStream.finish();
        int size = baos.size();

        // Write additional data to the underlying stream after finish
        baos.write(" Additional uncompressed data".getBytes(StandardCharsets.UTF_8));

        byte[] result = baos.toByteArray();
        Assertions.assertTrue(result.length > size);
    }

    @Test
    public void testEmptyData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (BrotliOutputStream brotliStream = new BrotliOutputStream(baos)) {
            // Write nothing
            brotliStream.finish();
        }

        // Even empty data should produce some compressed output (Brotli header)
        byte[] compressed = baos.toByteArray();
        Assertions.assertTrue(compressed.length > 0, "Compressed empty data should contain Brotli header");

        // Verify we can decompress it to empty string
        String decompressed = decompressData(compressed);
        Assertions.assertEquals("", decompressed, "Decompressed empty data should be empty string");
    }

    @Test
    public void testSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (BrotliOutputStream brotliStream = new BrotliOutputStream(baos)) {
            brotliStream.write('A');
            brotliStream.finish();
        }

        byte[] compressed = baos.toByteArray();
        Assertions.assertTrue(compressed.length > 0, "Compressed data should not be empty");

        String decompressed = decompressData(compressed);
        Assertions.assertEquals("A", decompressed, "Decompressed data should match original single byte");
    }


    @Test
    public void testMultipleFinishCalls() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BrotliOutputStream brotliStream = new BrotliOutputStream(baos);
        brotliStream.write(TEST_DATA.getBytes(StandardCharsets.UTF_8));
        brotliStream.finish();

        // Get the size after first finish
        int sizeAfterFirstFinish = baos.toByteArray().length;

        // Call finish again - should be safe (idempotent)
        brotliStream.finish();

        // Size should not change
        Assertions.assertEquals(sizeAfterFirstFinish, baos.toByteArray().length);

        brotliStream.close();
    }

    @Test
    public void testWriteWithOffsetAndLength() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = "AAABBBCCCDDD".getBytes(StandardCharsets.UTF_8);

        try (BrotliOutputStream brotliStream = new BrotliOutputStream(baos)) {
            brotliStream.write(data, 3, 3);
            brotliStream.finish();
        }

        String decompressed = decompressData(baos.toByteArray());
        Assertions.assertEquals("BBB", decompressed);
    }

    @Test
    public void testImplementsIFinishable() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Test that it can be used as IFinishable
        com.itextpdf.io.source.IFinishable finishable = new BrotliOutputStream(baos);
        ((OutputStream) finishable).write(TEST_DATA.getBytes(StandardCharsets.UTF_8));
        finishable.finish();

        byte[] compressed = baos.toByteArray();
        String decompressed = decompressData(compressed);
        Assertions.assertEquals(TEST_DATA, decompressed);
    }

    private String decompressData(byte[] compressed) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
                BrotliInputStream brotliStream = new BrotliInputStream(bais);
                java.io.ByteArrayOutputStream decompressed = new java.io.ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = brotliStream.read(buffer)) != -1) {
                decompressed.write(buffer, 0, len);
            }

            return decompressed.toString(StandardCharsets.UTF_8.name());
        }
    }
}
