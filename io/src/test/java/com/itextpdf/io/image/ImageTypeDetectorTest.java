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

import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ImageTypeDetectorTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/ImageTypeDetectorTest/";
    private static final String IMAGE_NAME = "image";

    @Test
    public void testUnknown() throws MalformedURLException {
        test(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".txt"), ImageType.NONE);
    }

    @Test
    public void testGif() throws MalformedURLException {
        test(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".gif"), ImageType.GIF);
    }

    @Test
    public void testJpeg() throws MalformedURLException {
        test(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".jpg"), ImageType.JPEG);
    }

    @Test
    public void testTiff() throws MalformedURLException {
        test(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".tiff"), ImageType.TIFF);
    }

    @Test
    public void testWmf() throws MalformedURLException {
        test(UrlUtil.toURL(SOURCE_FOLDER + IMAGE_NAME + ".wmf"), ImageType.WMF);
    }

    private void test(URL location, ImageType expectedType) {
        Assert.assertEquals(expectedType, ImageTypeDetector.detectImageType(location));
    }

}
