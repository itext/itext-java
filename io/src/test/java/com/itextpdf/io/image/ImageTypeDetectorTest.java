/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class ImageTypeDetectorTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/ImageTypeDetectorTest/";
    private static final String IMAGE_NAME = "image";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        junitExpectedException.expect(com.itextpdf.io.IOException.class);

        ImageTypeDetector.detectImageType(UrlUtil.toURL("not existing path"));

        Assert.fail("This line is not expected to be triggered: "
                + "an exception should have been thrown");
    }

    @Test
    public void testStreamUnknown() throws FileNotFoundException {
        testStream(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".txt"), ImageType.NONE);
    }

    @Test
    public void testStreamGif() throws FileNotFoundException {
        testStream(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".gif"), ImageType.GIF);
    }

    @Test
    public void testStreamJpeg() throws FileNotFoundException {
        testStream(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".jpg"), ImageType.JPEG);
    }

    @Test
    public void testStreamTiff() throws FileNotFoundException {
        testStream(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".tiff"), ImageType.TIFF);
    }

    @Test
    public void testStreamWmf() throws FileNotFoundException {
        testStream(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".wmf"), ImageType.WMF);
    }

    @Test
    public void testStreamClosed() throws IOException {
        // A common exception is expected instead of com.itextpdf.io.IOException, because in .NET
        // the thrown exception is different
        junitExpectedException.expect(Exception.class);

        InputStream stream = new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".wmf");
        stream.close();
        ImageTypeDetector.detectImageType(stream);

        Assert.fail("This line is not expected to be triggered: "
                + "an exception should have been thrown");
    }

    @Test
    public void testBytesUnknown() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".txt")),
                ImageType.NONE);
    }

    @Test
    public void testBytesGif() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".gif")),
                ImageType.GIF);
    }

    @Test
    public void testBytesJpeg() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".jpg")),
                ImageType.JPEG);
    }

    @Test
    public void testBytesTiff() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".tiff")),
                ImageType.TIFF);
    }

    @Test
    public void testBytesWmf() throws IOException {
        testBytes(StreamUtil.inputStreamToArray(new FileInputStream(SOURCE_FOLDER + IMAGE_NAME + ".wmf")),
                ImageType.WMF);
    }

    private static void testURL(URL location, ImageType expectedType) {
        Assert.assertEquals(expectedType, ImageTypeDetector.detectImageType(location));
    }

    private static void testStream(InputStream stream, ImageType expectedType) {
        Assert.assertEquals(expectedType, ImageTypeDetector.detectImageType(stream));
    }

    private static void testBytes(byte[] bytes, ImageType expectedType) {
        Assert.assertEquals(expectedType, ImageTypeDetector.detectImageType(bytes));
    }
}
