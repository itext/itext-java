/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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

import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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

            Assert.assertEquals(13, source.length());
            Assert.assertEquals(72, source.get(0));
            Assert.assertEquals(44, source.get(5));
            Assert.assertEquals(33, source.get(12));
            Assert.assertEquals(100, source.get(11));
            Assert.assertEquals(-1, source.get(13));

            long position = Integer.MAX_VALUE + 1L;
            Assert.assertThrows(IllegalArgumentException.class, () -> source.get(position));
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
            Assert.assertArrayEquals(expected, result);

            expected = new byte[] {111, 44, 32, 119, 111, 114, 108, 100};
            result = new byte[8];
            source.get(4, result, 0, 8);
            Assert.assertArrayEquals(expected, result);

            long position = Integer.MAX_VALUE + 1L;
            Assert.assertThrows(IllegalArgumentException.class, () -> source.get(position, new byte[6], 2, 4));
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

            Assert.assertEquals(expected, result1);
            Assert.assertEquals(expected, result2);
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
