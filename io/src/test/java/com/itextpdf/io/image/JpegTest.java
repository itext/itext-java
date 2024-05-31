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
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class JpegTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openJpeg1() throws IOException {
        try (InputStream fis = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "WP_20140410_001.jpg")) {
            // Test this a more specific entry point
            ImageData img = ImageDataFactory.createJpeg(StreamUtil.inputStreamToArray(fis));
            Assert.assertEquals(2592, img.getWidth(), 0);
            Assert.assertEquals(1456, img.getHeight(), 0);
            Assert.assertEquals(8, img.getBpc());
        }
    }

    @Test
    public void openJpeg2() throws IOException {
        // Test this a more specific entry point
        ImageData img = ImageDataFactory.createJpeg(UrlUtil.toURL(SOURCE_FOLDER + "WP_20140410_001_gray.jpg"));
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg3() throws IOException {
        try (InputStream fis = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "WP_20140410_001_monochrome.jpg")) {
            // Test this a more specific entry point
            ImageData img = ImageDataFactory.create(StreamUtil.inputStreamToArray(fis));
            Assert.assertEquals(2592, img.getWidth(), 0);
            Assert.assertEquals(1456, img.getHeight(), 0);
            Assert.assertEquals(8, img.getBpc());
        }
    }

    @Test
    public void openJpeg4() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_negate.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg5() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_year1900.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg6() throws IOException {
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "WP_20140410_001_year1980.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }
}
