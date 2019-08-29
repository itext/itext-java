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
package com.itextpdf.io.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TiffTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openTiff1() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001.tif");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff2() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_gray.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff3() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_monochrome.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff4() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_negate.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff5() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_year1900.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff6() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_year1980.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void getStringDataFromTiff() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(sourceFolder, "img_cmyk.tif"));
        TIFFDirectory dir = new TIFFDirectory(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(bytes)), 0);
        String[] stringArray = new String[] {"iText? 7.1.7-SNAPSHOT ?2000-2019 iText Group NV (AGPL-version)\u0000"};
        Assert.assertArrayEquals(stringArray, dir.getField(305).getAsStrings());
    }
}
