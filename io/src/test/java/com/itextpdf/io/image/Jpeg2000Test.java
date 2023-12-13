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

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Jpeg2000Test extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openJpeg2000_1() throws java.io.IOException {
        try {
            // Test a more specific entry point
            ImageDataFactory.createJpeg2000(UrlUtil.toURL(sourceFolder + "WP_20140410_001.JP2"));
        } catch (IOException e) {
            Assert.assertEquals(IOException.UnsupportedBoxSizeEqEq0, e.getMessage());
        }
    }

    @Test
    public void openJpeg2000_2() throws java.io.IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001.JPC");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }
}
