/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.image;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TiffTest extends ExtendedITextTest {

    private static final double DELTA = 1e-5;
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/TiffTest/";

    @Test
    public void openTiff1() throws IOException {
        byte[] imageBytes = StreamUtil.inputStreamToArray(new FileInputStream(SOURCE_FOLDER + "WP_20140410_001.tif"));
        // Test a more specific entry point
        ImageData img = ImageDataFactory.createTiff(imageBytes, false, 1, false);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
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

        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff4() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_negate.tiff");

        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff5() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_year1900.tiff");

        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff6() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_year1980.tiff");

        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void getStringDataFromTiff() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER, "img_cmyk.tif"));
        TIFFDirectory dir = new TIFFDirectory(new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(bytes)), 0);
        String[] stringArray = new String[] {"iText? 7.1.7-SNAPSHOT ?2000-2019 iText Group NV (AGPL-version)\u0000"};

        Assert.assertArrayEquals(stringArray, dir.getField(305).getAsStrings());
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

        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> createTiff(sourceFile, 1, 1024D, 768D));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
    }


    @Test
    public void group3CompressionCreateImageDataTest() throws MalformedURLException {
        String sourceFile = SOURCE_FOLDER + "group3CompressionImage.tif";
        ImageData img = ImageDataFactory.create(UrlUtil.toURL(SOURCE_FOLDER + "group3CompressionImage.tif"));
        Assert.assertEquals(1024, img.getWidth(), 0);
        Assert.assertEquals(768, img.getHeight(), 0);
        Assert.assertEquals(1, img.getBpc());
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
        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.createTiff(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsBlack.tif"),
                        false, 1, false));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsBlackCreateImageTest() {
        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.create(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsBlack.tif")));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsWhiteCreateTiffTest() {
        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.createTiff(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsWhite.tif"),
                        false, 1, false));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateComp16BitMinIsWhiteCreateImageTest() {
        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.create(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitMinIsWhite.tif")));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateCompression16BitRgbCreateTiffTest() {
        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.createTiff(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitRgb.tif"),
                        false, 1, false));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
    }

    @Test
    // TODO: DEVSIX-5791 (update test when support for adobeDeflate compression tiff image will be realized)
    public void adobeDeflateCompression16BitRgbCreateImageTest() {
        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageDataFactory.create(UrlUtil.toURL(
                        SOURCE_FOLDER + "adobeDeflateCompression16BitRgb.tif")));

        Assert.assertEquals(MessageFormatUtil.format(
                com.itextpdf.io.exceptions.IOException.CannotReadTiffImage), e.getMessage());
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

        Assert.assertEquals(bpc, img.getBpc(), DELTA);
        Assert.assertEquals(width, img.getWidth(), DELTA);
        Assert.assertEquals(height, img.getHeight(), DELTA);
    }
}
