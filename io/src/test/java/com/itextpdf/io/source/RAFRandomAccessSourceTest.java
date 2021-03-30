/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.io.util.FileUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class RAFRandomAccessSourceTest extends ExtendedITextTest {
    private final static String SOURCE_FILE = "./src/test/resources/com/itextpdf/io/source/RAF.txt";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private final byte[] content = "Hello, world!".getBytes();

    @Test
    public void getByIndexTest() throws IOException {
        File file = new File(SOURCE_FILE);

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            for (int i = 0; i < content.length; i++) {
                Assert.assertEquals(content[i], source.get(i));
            }
        }
    }

    @Test
    public void getByIndexOutOfBoundsTest() throws IOException {
        File file = new File(SOURCE_FILE);
        int indexOutOfBounds = content.length;

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            Assert.assertNotEquals(-1, source.get(indexOutOfBounds - 1));
            Assert.assertEquals(-1, source.get(indexOutOfBounds));
        }
    }

    @Test
    public void getArrayByIndexesTest() throws IOException {
        File file = new File(SOURCE_FILE);
        final int beginIndex = 7;
        final int length = 5;

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            byte[] dest = new byte[24];

            int read = source.get(beginIndex, dest, 0, length);

            Assert.assertEquals(length, read);
            for (int i = 0; i < length; i++) {
                Assert.assertEquals(content[beginIndex + i], dest[i]);
            }
        }
    }

    @Test
    public void getArrayByIndexesNotEnoughBytesTest() throws IOException {
        File file = new File(SOURCE_FILE);
        final int beginIndex = 7;
        final int length = 24;
        final int expectedLength = 6;

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            byte[] dest = new byte[24];

            int read = source.get(beginIndex, dest, 0, length);
            Assert.assertEquals(expectedLength, read);
            for (int i = 0; i < expectedLength; i++) {
                Assert.assertEquals(content[beginIndex + i], dest[i]);
            }
        }
    }

    @Test
    public void getArrayByIndexesWithOffsetTest() throws IOException {
        File file = new File(SOURCE_FILE);
        final int beginIndex = 7;
        final int length = 5;
        final int offset = 2;

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            byte[] dest = new byte[24];

            int read = source.get(beginIndex, dest, offset, length);

            Assert.assertEquals(length, read);
            for (int i = 0; i < length; i++) {
                Assert.assertEquals(content[beginIndex + i], dest[offset + i]);
            }
        }
    }

    @Test
    public void getArrayByIndexesOutOfBounds() throws IOException {
        File file = new File(SOURCE_FILE);
        final int beginIndex = content.length;
        final int length = 5;

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            byte[] dest = new byte[24];

            int read = source.get(beginIndex, dest, 0, length);

            Assert.assertEquals(-1, read);
            for (int i = 0; i < dest.length; i++) {
                Assert.assertEquals(0, dest[i]);
            }
        }
    }
}
