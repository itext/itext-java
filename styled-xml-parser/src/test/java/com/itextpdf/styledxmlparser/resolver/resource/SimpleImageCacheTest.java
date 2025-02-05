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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SimpleImageCacheTest extends ExtendedITextTest {

    @BeforeEach
    public void before() {
        ImageXObjectStub.resetNumbering();
    }

    @Test
    public void simpleImageCacheTest01() {
        SimpleImageCache cache = new SimpleImageCache();
        String imgSrc = "src1.jpg";
        ImageXObjectStub imageData = new ImageXObjectStub();
        Assertions.assertEquals(0, cache.size());
        cache.putImage(imgSrc, imageData);
        Assertions.assertEquals(1, cache.size());
        Assertions.assertEquals(imageData, cache.getImage(imgSrc));
    }

    @Test
    public void simpleImageCacheTest02() {
        String[] imgSrc = {"src0.jpg", "src1.jpg", "src2.jpg", "src3.jpg", "src4.jpg", "src5.jpg"};
        ImageXObjectStub[] imgData = {new ImageXObjectStub(), new ImageXObjectStub(), new ImageXObjectStub(), new ImageXObjectStub(),
                new ImageXObjectStub(), new ImageXObjectStub()};

        SimpleImageCache cache = new SimpleImageCache(4);

        // imgs frequency is increased on getImage call
        cache.getImage(imgSrc[1]);
        cache.getImage(imgSrc[2]);

        cache.putImage(imgSrc[0], imgData[0]);
        cache.putImage(imgSrc[1], imgData[1]);
        cache.putImage(imgSrc[2], imgData[2]);
        Assertions.assertEquals(3, cache.size());

        cache.getImage(imgSrc[0]);
        cache.getImage(imgSrc[1]);
        cache.getImage(imgSrc[2]);

        cache.putImage(imgSrc[3], imgData[3]);
        Assertions.assertEquals(4, cache.size());

        cache.putImage(imgSrc[4], imgData[4]);
        Assertions.assertEquals(4, cache.size());
        Assertions.assertNull(cache.getImage(imgSrc[3]));
        Assertions.assertEquals(imgData[0], cache.getImage(imgSrc[0]));
        Assertions.assertEquals(imgData[1], cache.getImage(imgSrc[1]));
        Assertions.assertEquals(imgData[2], cache.getImage(imgSrc[2]));
        Assertions.assertEquals(imgData[4], cache.getImage(imgSrc[4]));

        cache.getImage(imgSrc[0]);
        cache.getImage(imgSrc[1]);
        cache.getImage(imgSrc[2]);
        cache.getImage(imgSrc[4]);

        cache.putImage(imgSrc[5], imgData[5]);
        Assertions.assertEquals(4, cache.size());
        Assertions.assertNull(cache.getImage(imgSrc[4]));
        Assertions.assertEquals(imgData[0], cache.getImage(imgSrc[0]));
        Assertions.assertEquals(imgData[1], cache.getImage(imgSrc[1]));
        Assertions.assertEquals(imgData[2], cache.getImage(imgSrc[2]));
        Assertions.assertEquals(imgData[5], cache.getImage(imgSrc[5]));


        cache.putImage(imgSrc[3], imgData[3]);
        Assertions.assertEquals(4, cache.size());
        Assertions.assertEquals(imgData[3], cache.getImage(imgSrc[3]));
        Assertions.assertNull(cache.getImage(imgSrc[5]));

        cache.putImage(imgSrc[5], imgData[5]);
        Assertions.assertEquals(4, cache.size());
        Assertions.assertEquals(imgData[5], cache.getImage(imgSrc[5]));
        Assertions.assertNull(cache.getImage(imgSrc[3]));

        cache.putImage(imgSrc[3], imgData[3]);
        Assertions.assertEquals(4, cache.size());
        Assertions.assertEquals(imgData[3], cache.getImage(imgSrc[3]));
        Assertions.assertNull(cache.getImage(imgSrc[5]));

        cache.putImage(imgSrc[5], imgData[5]);
        Assertions.assertEquals(4, cache.size());
        Assertions.assertEquals(imgData[5], cache.getImage(imgSrc[5]));
        Assertions.assertEquals(imgData[3], cache.getImage(imgSrc[3]));


        Assertions.assertEquals(imgData[1], cache.getImage(imgSrc[1]));
        Assertions.assertEquals(imgData[2], cache.getImage(imgSrc[2]));
        Assertions.assertNull(cache.getImage(imgSrc[0]));
        Assertions.assertNull(cache.getImage(imgSrc[4]));

    }

    @Test
    public void orderRemovingFromCacheTest() {
        SimpleImageCache cache = new SimpleImageCache(10);
        for (int j = 0; j <= 9; j++) {
            cache.putImage("src" + j + ".jpg", new ImageXObjectStub());
        }

        for (int i = 0; i <= 9; i++) {
            cache.putImage("src" + i + 10 + ".jpg", new ImageXObjectStub());

            Assertions.assertNull(cache.getImage("src" + i + ".jpg"));
        }
    }

    private static class ImageXObjectStub extends PdfImageXObject {
        private static int totalNum = 0;
        private int num = 0;

        ImageXObjectStub() {
            super(new PdfStream());
            num = totalNum++;
        }

        public static void resetNumbering() {
            totalNum = 0;
        }

        @Override
        public String toString() {
            return "ImageXObjectStub_" + String.valueOf(num);
        }
    }
}
