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

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Tag("UnitTest")
public class PagedChannelRandomAccessSourceTest  extends ExtendedITextTest {

    private final static String SOURCE_FILE = "./src/test/resources/com/itextpdf/io/source/RAF.txt";
    @Test
    public void readBytesFromFileTest() throws IOException {
        PagedChannelRandomAccessSource source;
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();
            source = new PagedChannelRandomAccessSource(channel);
            byte[] expected = new byte[] {72, 101, 108, 108, 111, 44, 32, 119, 111, 114, 108, 100, 33};
            byte[] result = new byte[13];
            source.get(0, result, 0, 13);
            Assertions.assertArrayEquals(expected, result);

            expected = new byte[] {111, 44, 32, 119, 111, 114, 108, 100};
            result = new byte[8];
            source.get(4, result, 0, 8);
            Assertions.assertArrayEquals(expected, result);
        }
    }

    @Test
    public void readIntFromFileTest() throws IOException {
        PagedChannelRandomAccessSource source;
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();
            source = new PagedChannelRandomAccessSource(channel, 10, 1);
            Assertions.assertEquals(13, source.length());
            Assertions.assertEquals(72, source.get(0));
            Assertions.assertEquals(44, source.get(5));
            Assertions.assertEquals(33, source.get(12));
            Assertions.assertEquals(100, source.get(11));
            Assertions.assertEquals(-1, source.get(13));
        }
    }

    @Test
    public void sourceReleaseAndOpenTest() throws IOException {
        PagedChannelRandomAccessSource source;
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();
            source = new PagedChannelRandomAccessSource(channel);
            IRandomAccessSource sourceToClose = new ByteBufferRandomAccessSource(ByteBuffer.allocate(10));
            //assert no exception is thrown
            source.sourceReleased(sourceToClose);
            source.sourceReleased(sourceToClose);
            source.sourceReleased(new ByteBufferRandomAccessSource(ByteBuffer.allocate(10)));
            source.sourceReleased(sourceToClose);

            MappedChannelRandomAccessSource sourceToOpen = new MappedChannelRandomAccessSource(channel, 0, channel.size());
            source.sourceInUse(sourceToOpen);
            source.close();
        }
    }
}
