/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SimpleImageCacheTest extends ExtendedITextTest {

    @Before
    public void before() {
        ImageXObjectStub.resetNumbering();
    }

    @Test
    public void simpleImageCacheTest01() {
        SimpleImageCache cache = new SimpleImageCache();
        String imgSrc = "src1.jpg";
        ImageXObjectStub imageData = new ImageXObjectStub();
        Assert.assertEquals(0, cache.size());
        cache.putImage(imgSrc, imageData);
        Assert.assertEquals(1, cache.size());
        Assert.assertEquals(imageData, cache.getImage(imgSrc));
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
        Assert.assertEquals(3, cache.size());

        cache.getImage(imgSrc[0]);
        cache.getImage(imgSrc[1]);
        cache.getImage(imgSrc[2]);

        cache.putImage(imgSrc[3], imgData[3]);
        Assert.assertEquals(4, cache.size());

        cache.putImage(imgSrc[4], imgData[4]);
        Assert.assertEquals(4, cache.size());
        Assert.assertNull(cache.getImage(imgSrc[3]));
        Assert.assertEquals(imgData[0], cache.getImage(imgSrc[0]));
        Assert.assertEquals(imgData[1], cache.getImage(imgSrc[1]));
        Assert.assertEquals(imgData[2], cache.getImage(imgSrc[2]));
        Assert.assertEquals(imgData[4], cache.getImage(imgSrc[4]));

        cache.getImage(imgSrc[0]);
        cache.getImage(imgSrc[1]);
        cache.getImage(imgSrc[2]);
        cache.getImage(imgSrc[4]);

        cache.putImage(imgSrc[5], imgData[5]);
        Assert.assertEquals(4, cache.size());
        Assert.assertNull(cache.getImage(imgSrc[4]));
        Assert.assertEquals(imgData[0], cache.getImage(imgSrc[0]));
        Assert.assertEquals(imgData[1], cache.getImage(imgSrc[1]));
        Assert.assertEquals(imgData[2], cache.getImage(imgSrc[2]));
        Assert.assertEquals(imgData[5], cache.getImage(imgSrc[5]));


        cache.putImage(imgSrc[3], imgData[3]);
        Assert.assertEquals(4, cache.size());
        Assert.assertEquals(imgData[3], cache.getImage(imgSrc[3]));
        Assert.assertNull(cache.getImage(imgSrc[5]));

        cache.putImage(imgSrc[5], imgData[5]);
        Assert.assertEquals(4, cache.size());
        Assert.assertEquals(imgData[5], cache.getImage(imgSrc[5]));
        Assert.assertNull(cache.getImage(imgSrc[3]));

        cache.putImage(imgSrc[3], imgData[3]);
        Assert.assertEquals(4, cache.size());
        Assert.assertEquals(imgData[3], cache.getImage(imgSrc[3]));
        Assert.assertNull(cache.getImage(imgSrc[5]));

        cache.putImage(imgSrc[5], imgData[5]);
        Assert.assertEquals(4, cache.size());
        Assert.assertEquals(imgData[5], cache.getImage(imgSrc[5]));
        Assert.assertEquals(imgData[3], cache.getImage(imgSrc[3]));


        Assert.assertEquals(imgData[1], cache.getImage(imgSrc[1]));
        Assert.assertEquals(imgData[2], cache.getImage(imgSrc[2]));
        Assert.assertNull(cache.getImage(imgSrc[0]));
        Assert.assertNull(cache.getImage(imgSrc[4]));

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
