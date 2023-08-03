/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BmpTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openBmp1() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001.bmp");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openBmp2() throws IOException {
        // Test this a more specific entry point
        ImageData img = ImageDataFactory.createBmp(UrlUtil.toURL(sourceFolder + "WP_20140410_001_gray.bmp"), false);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openBmp3() throws IOException {
        String imageFileName = sourceFolder + "WP_20140410_001_monochrome.bmp";
        try (FileInputStream fis = new FileInputStream(imageFileName)) {
            byte[] imageBytes = StreamUtil.inputStreamToArray(fis);
            // Test this a more specific entry point
            ImageData img = ImageDataFactory.createBmp(imageBytes, false);
            Assert.assertEquals(2592, img.getWidth(), 0);
            Assert.assertEquals(1456, img.getHeight(), 0);
            Assert.assertEquals(1, img.getBpc());
        }
    }
}
