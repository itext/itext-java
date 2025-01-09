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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class Jpeg2000Test extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openJpeg2000_1() throws java.io.IOException {
        try {
            // Test a more specific entry point
            ImageDataFactory.createJpeg2000(UrlUtil.toURL(sourceFolder + "bee.jp2"));
        } catch (IOException e) {
            Assertions.assertEquals(IoExceptionMessageConstant.UNSUPPORTED_BOX_SIZE_EQ_EQ_0, e.getMessage());
        }
    }

    @Test
    public void openJpeg2000_2() throws java.io.IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "bee.jpc");
        Assertions.assertEquals(640, img.getWidth(), 0);
        Assertions.assertEquals(800, img.getHeight(), 0);
        Assertions.assertEquals(7, img.getBpc());
    }
}
