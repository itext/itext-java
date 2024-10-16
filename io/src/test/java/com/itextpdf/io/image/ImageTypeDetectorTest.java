/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ImageTypeDetectorTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/ImageTypeDetectorTest/";
    private static final String IMAGE_NAME = "image";

    @Test
    public void testUrlUnknown() throws MalformedURLException {
        testURL(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".txt"), ImageType.NONE);
    }

    @Test
    public void testUrlGif() throws MalformedURLException {
        testURL(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".gif"), ImageType.GIF);
    }

    @Test
    public void testUrlJpeg() throws MalformedURLException {
        testURL(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".jpg"), ImageType.JPEG);
    }

    @Test
    public void testUrlTiff() throws MalformedURLException {
        testURL(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".tiff"), ImageType.TIFF);
    }

    @Test
    public void testUrlWmf() throws MalformedURLException {
        testURL(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".wmf"), ImageType.WMF);
    }

    @Test
    public void testNullUrl() throws MalformedURLException {
        URL url = UrlUtil.toURL("not existing path");

        Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> ImageTypeDetector.detectImageType(url)
        );
    }

    @Test
    public void testStreamUnknown() throws IOException {
        testStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".txt"), ImageType.NONE);
    }

    @Test
    public void testStreamGif() throws IOException {
        testStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".gif"), ImageType.GIF);
    }

    @Test
    public void testStreamJpeg() throws IOException {
        testStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".jpg"), ImageType.JPEG);
    }

    @Test
    public void testStreamTiff() throws IOException {
        testStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".tiff"), ImageType.TIFF);
    }

    @Test
    public void testStreamWmf() throws IOException {
        testStream(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".wmf"), ImageType.WMF);
    }

    @Test
    public void testStreamClosed() throws IOException {
        InputStream stream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".wmf");
        stream.close();

        // A common exception is expected instead of com.itextpdf.io.exceptions.IOException, because in .NET
        // the thrown exception is different
        Assertions.assertThrows(Exception.class, () -> ImageTypeDetector.detectImageType(stream));
    }

    @Test
    public void testBytesUnknown() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".txt")),
                ImageType.NONE);
    }

    @Test
    public void testBytesGif() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".gif")),
                ImageType.GIF);
    }

    @Test
    public void testBytesJpeg() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".jpg")),
                ImageType.JPEG);
    }

    @Test
    public void testBytesTiff() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".tiff")),
                ImageType.TIFF);
    }

    @Test
    public void testBytesWmf() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + IMAGE_NAME + ".wmf")),
                ImageType.WMF);
    }

    private static void testURL(URL location, ImageType expectedType) {
        Assertions.assertEquals(expectedType, ImageTypeDetector.detectImageType(location));
    }

    private static void testStream(InputStream stream, ImageType expectedType) {
        Assertions.assertEquals(expectedType, ImageTypeDetector.detectImageType(stream));
    }

    private static void testBytes(byte[] bytes, ImageType expectedType) {
        Assertions.assertEquals(expectedType, ImageTypeDetector.detectImageType(bytes));
    }
}
