package com.itextpdf.io.font;

import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CFFFontTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/CFFFontTest/";

    @Test
    public void seekTest() throws IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory()
                .createBestSource(SOURCE_FOLDER + "NotoSansCJKjp-Bold.otf"));

        byte[] cff = new byte[16014190];
        try {
            raf.seek(283192);
            raf.readFully(cff);
        } finally {
            raf.close();
        }
        CFFFont cffFont = new CFFFont(cff);

        cffFont.seek(0);
        // Get int (bin 0000 0001 0000 0000  0000 0100 0000 0011)
        Assert.assertEquals(16778243, cffFont.getInt());
        cffFont.seek(0);
        // Gets the first short (bin 0000 0001 0000 0000)
        Assert.assertEquals(256, cffFont.getShort());
        cffFont.seek(2);
        // Gets the second short (bin 0000 0100 0000 0011)
        Assert.assertEquals(1027, cffFont.getShort());
    }

    @Test
    public void getPositionTest() throws IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory()
                .createBestSource(SOURCE_FOLDER + "NotoSansCJKjp-Bold.otf"));

        byte[] cff = new byte[16014190];
        try {
            raf.seek(283192);
            raf.readFully(cff);
        } finally {
            raf.close();
        }
        CFFFont cffFont = new CFFFont(cff);


        cffFont.seek(0);
        Assert.assertEquals(0, cffFont.getPosition());
        cffFont.seek(16);
        Assert.assertEquals(16, cffFont.getPosition());
    }
}
