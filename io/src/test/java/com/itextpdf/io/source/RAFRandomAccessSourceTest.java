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
package com.itextpdf.io.source;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RAFRandomAccessSourceTest extends ExtendedITextTest {
    private final static String SOURCE_FILE = "./src/test/resources/com/itextpdf/io/source/RAF.txt";

    private final byte[] content = "Hello, world!".getBytes();

    @Test
    public void getByIndexTest() throws IOException {
        File file = new File(SOURCE_FILE);

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            for (int i = 0; i < content.length; i++) {
                Assertions.assertEquals(content[i], source.get(i));
            }
        }
    }

    @Test
    public void getByIndexOutOfBoundsTest() throws IOException {
        File file = new File(SOURCE_FILE);
        int indexOutOfBounds = content.length;

        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {
            RAFRandomAccessSource source = new RAFRandomAccessSource(raf);
            Assertions.assertNotEquals(-1, source.get(indexOutOfBounds - 1));
            Assertions.assertEquals(-1, source.get(indexOutOfBounds));
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

            Assertions.assertEquals(length, read);
            for (int i = 0; i < length; i++) {
                Assertions.assertEquals(content[beginIndex + i], dest[i]);
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
            Assertions.assertEquals(expectedLength, read);
            for (int i = 0; i < expectedLength; i++) {
                Assertions.assertEquals(content[beginIndex + i], dest[i]);
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

            Assertions.assertEquals(length, read);
            for (int i = 0; i < length; i++) {
                Assertions.assertEquals(content[beginIndex + i], dest[offset + i]);
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

            Assertions.assertEquals(-1, read);
            for (int i = 0; i < dest.length; i++) {
                Assertions.assertEquals(0, dest[i]);
            }
        }
    }
}
