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
package com.itextpdf.io.source;

import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ByteBufferRandomAccessSourceTest extends ExtendedITextTest {

    private final static String SOURCE_FILE = "./src/test/resources/com/itextpdf/io/source/RAF.txt";

    @Test
    public void heapByteBufferTest() {
        IRandomAccessSource source = new ByteBufferRandomAccessSource(ByteBuffer.allocate(10));
        AssertUtil.doesNotThrow(source::close);
    }

    @Test
    public void nullByteBufferTest() {
        IRandomAccessSource source = new ByteBufferRandomAccessSource(null);
        AssertUtil.doesNotThrow(source::close);
    }

    @Test
    public void disableUnmappingTest() throws IOException {
        ByteBufferRandomAccessSource.disableByteBufferMemoryUnmapping();
        IRandomAccessSource source;
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();

            source = new ByteBufferRandomAccessSource(
                    channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
        }
        AssertUtil.doesNotThrow(() -> source.get(0));

        source.close();
        AssertUtil.doesNotThrow(() -> source.get(0));

        ByteBufferRandomAccessSource.enableByteBufferMemoryUnmapping();
    }

    @Test
    public void readIntFromFile() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();

            IRandomAccessSource source = new ByteBufferRandomAccessSource(
                    channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));

            Assertions.assertEquals(13, source.length());
            Assertions.assertEquals(72, source.get(0));
            Assertions.assertEquals(44, source.get(5));
            Assertions.assertEquals(33, source.get(12));
            Assertions.assertEquals(100, source.get(11));
            Assertions.assertEquals(-1, source.get(13));

            long position = Integer.MAX_VALUE + 1L;
            Assertions.assertThrows(IllegalArgumentException.class, () -> source.get(position));
        }
    }

    @Test
    public void readBytesFromFile() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();

            IRandomAccessSource source = new ByteBufferRandomAccessSource(
                    channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));

            byte[] expected = new byte[] {72, 101, 108, 108, 111, 44, 32, 119, 111, 114, 108, 100, 33};
            byte[] result = new byte[13];
            source.get(0, result, 0, 13);
            Assertions.assertArrayEquals(expected, result);

            expected = new byte[] {111, 44, 32, 119, 111, 114, 108, 100};
            result = new byte[8];
            source.get(4, result, 0, 8);
            Assertions.assertArrayEquals(expected, result);

            long position = Integer.MAX_VALUE + 1L;
            Assertions.assertThrows(IllegalArgumentException.class, () -> source.get(position, new byte[6], 2, 4));
        }
    }

    @Test
    public void readFileWithMultipleThreadsTest() throws InterruptedException, ExecutionException, IOException {

        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();

            IRandomAccessSource source = new ByteBufferRandomAccessSource(
                    channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));

            ReadFileTask task1 = new ReadFileTask(source);
            ReadFileTask task2 = new ReadFileTask(source);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<List<Integer>> future1 = executor.submit(task1);
            Future<List<Integer>> future2 = executor.submit(task2);

            List<Integer> expected = Arrays.asList(13,
                    72, 101, 108, 108, 111, 44, 32, 119, 111, 114, 108, 100, 33,
                    33, 100, 108, 114, 111, 119, 32, 44, 111, 108, 108, 101, 72);

            List<Integer> result1 = future1.get();
            List<Integer> result2 = future2.get();

            Assertions.assertEquals(expected, result1);
            Assertions.assertEquals(expected, result2);
        }
    }

    private static class ReadFileTask implements Callable<List<Integer>> {

        final private IRandomAccessSource source;

        ReadFileTask(IRandomAccessSource source) {
            this.source = source;
        }

        public List<Integer> call() throws IOException {
            List<Integer> result = new ArrayList<>();

            result.add((int) source.length());
            for (long position = 0; position < source.length(); ++position) {
                result.add(source.get(position));
            }
            for (long position = source.length() - 1; position >= 0; --position) {
                result.add(source.get(position));
            }

            return result;
        }
    }
}
