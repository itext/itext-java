package com.itextpdf.io.source;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Category(UnitTest.class)
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
            Assert.assertArrayEquals(expected, result);

            expected = new byte[] {111, 44, 32, 119, 111, 114, 108, 100};
            result = new byte[8];
            source.get(4, result, 0, 8);
            Assert.assertArrayEquals(expected, result);
        }
    }

    @Test
    public void readIntFromFileTest() throws IOException {
        PagedChannelRandomAccessSource source;
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_FILE, "r")) {
            FileChannel channel = raf.getChannel();
            source = new PagedChannelRandomAccessSource(channel, 10, 1);
            Assert.assertEquals(13, source.length());
            Assert.assertEquals(72, source.get(0));
            Assert.assertEquals(44, source.get(5));
            Assert.assertEquals(33, source.get(12));
            Assert.assertEquals(100, source.get(11));
            Assert.assertEquals(-1, source.get(13));
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
