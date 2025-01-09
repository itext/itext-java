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
package com.itextpdf.io.image;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class TiffTest extends ExtendedITextTest {

    private static final double DELTA = 1e-5;
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/TiffTest/";

    @Test
    public void openTiff1() throws IOException {
        byte[] imageBytes = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "WP_20140410_001.tif"));
        // Test a more specific entry point
        ImageData img = ImageDataFactory.createTiff(imageBytes, false, 1, false);
        Assertions.assertEquals(2592, img.getWidth(), 0);
        Assertions.assertEquals(1456, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff2() throws IOException {
        // Test a more specific entry point
        String sourceFile = SOURCE_FOLDER + "WP_20140410_001_gray.tiff";

        createTiff(sourceFile, 8, 2592D, 1456D);
    }

    @Test
    public void openTiff3() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_monochrome.tiff");

        Assertions.assertEquals(2592, img.getWidth(), 0);
        Assertions.assertEquals(1456, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff4() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_negate.tiff");

        Assertions.assertEquals(2592, img.getWidth(), 0);
        Assertions.assertEquals(1456, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff5() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_year1900.tiff");

        Assertions.assertEquals(2592, img.getWidth(), 0);
        Assertions.assertEquals(1456, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff6() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_year1980.tiff");

        Assertions.assertEquals(2592, img.getWidth(), 0);
        Assertions.assertEquals(1456, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
    }

    @Test
    public void getStringDataFromTiff() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER, "img_cmyk.tif"));
        TIFFDirectory dir = new TIFFDirectory(new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(bytes)), 0);
        String[] stringArray = new String[] {"iText? 7.1.7-SNAPSHOT ?2000-2019 iText Group NV (AGPL-version)\u0000"};

        Assertions.assertArrayEquals(stringArray, dir.getField(305).getAsStrings());
    }

    @Test
    public void group3CompressionCreateTiffImageTest() throws MalformedURLException {
        String sourceFile = SOURCE_FOLDER + "group3CompressionImage.tif";
        createTiff(sourceFile, 1, 1024D, 768D);
    }

    @Test
    public void group3CompressionBECreateTiffImageTest() throws MalformedURLException {
        String sourceFile = SOURCE_FOLDER + "group3CompressionImageBE.tif";
        createTiff(sourceFile, 1, 1024D, 768D);
    }


    @Test
    public void group3Compression2DCreateTiffImageTest() throws MalformedURLException {
        String sourceFile = SOURCE_FOLDER + "group3CompressionImage2d.tif";
        createTiff(sourceFile, 1, 1024D, 768D);
    }


    @Test
    public void group3CompressionEolErrorCreateTiffImageTest() throws MalformedURLException {
        String sourceFile = SOURCE_FOLDER + "group3CompressionImageWithEolError.tif";

        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> createTiff(sourceFile, 1, 1024D, 768D));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }


    @Test
    public void group3CompressionCreateImageDataTest() throws MalformedURLException {
        String sourceFile = SOURCE_FOLDER + "group3CompressionImage.tif";
        ImageData img = ImageDataFactory.create(UrlUtil.toURL(SOURCE_FOLDER + "group3CompressionImage.tif"));
        Assertions.assertEquals(1024, img.getWidth(), 0);
        Assertions.assertEquals(768, img.getHeight(), 0);
        Assertions.assertEquals(1, img.getBpc());
    }

    @Test
    public void group4CompressionTiffImageTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "group4CompressionImage.tif";

        createTiff(sourceFile, 1, 1024D, 768D);
    }

    @Test
    public void adobeDeflateCompression1BitMinIsBlackTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "adobeDeflateCompression1BitMinIsBlack.tif";

        createTiff(sourceFile, 1, 1024D, 768D);
    }

    @Test
    public void adobeDeflateCompression1BitMinIsWhiteTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "adobeDeflateCompression1BitMinIsWhite.tif";

        createTiff(sourceFile, 1, 1024D, 768D);
    }

    @Test
    public void adobeDeflateCompression8BitMinIsBlackTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "adobeDeflateCompression8BitMinIsBlack.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void adobeDeflateCompression8BitMinIsWhiteTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "adobeDeflateCompression8BitMinIsWhite.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void adobeDeflateCompression8BitRgbTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "adobeDeflateCompression8BitRgb.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsBlackCreateTiffTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.createTiff(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsBlack.tif"),
                        false, 1, false));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsBlackCreateImageTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.create(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsBlack.tif")));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsWhiteCreateTiffTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.createTiff(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsWhite.tif"),
                        false, 1, false));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsWhiteCreateImageTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.create(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsWhite.tif")));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateCompression16BitRgbCreateTiffTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.createTiff(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitRgb.tif"),
                        false, 1, false));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateCompression16BitRgbCreateImageTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.create(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitRgb.tif")));

        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.CANNOT_READ_TIFF_IMAGE), e.getMessage());
    }

    @Test
    public void ccittRleCompressionTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "ccittRleCompression.tif";

        createTiff(sourceFile, 1, 1024D, 768D);
    }

    @Test
    public void deflateCompression8BitRgbTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "deflateCompression8BitRgb.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void deflateCompression8BitPaletteTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "deflateCompression8BitPalette.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void jpegCompression8BitYcbcrTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "jpegCompression8BitYcbcr.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void oldJpegCompression8BitYcbcrTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "oldJpegCompression8BitYcbcr.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void lzwCompression8BitRgbTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "lzwCompression8BitRgb.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void lzwCompression8BitPaletteTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "lzwCompression8BitPalette.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void packbitsCompression8BitMinIsBlackTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "packbitsCompression8BitMinIsBlack.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    @Test
    public void packbitsCompression8BitMinIsWhiteTest() throws IOException {
        String sourceFile = SOURCE_FOLDER + "packbitsCompression8BitMinIsWhite.tif";

        createTiff(sourceFile, 8, 1024D, 768D);
    }

    private static void createTiff (String sourceFile, int bpc, double width, double height)
            throws MalformedURLException {
        ImageData img = ImageDataFactory.createTiff(UrlUtil.toURL(sourceFile),
                false, 1, false);

        Assertions.assertEquals(bpc, img.getBpc(), DELTA);
        Assertions.assertEquals(width, img.getWidth(), DELTA);
        Assertions.assertEquals(height, img.getHeight(), DELTA);
    }
}
