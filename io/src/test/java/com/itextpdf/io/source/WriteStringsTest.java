package com.itextpdf.io.source;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class WriteStringsTest {

    @Test
    public void writeStringTest() {
        String str = "SomeString";
        byte[] content = ByteUtils.getIsoBytes(str);
        Assert.assertArrayEquals(str.getBytes(), content);
    }

    @Test
    public void writeNameTest() {
        String str = "SomeName";
        byte[] content = ByteUtils.getIsoBytes((byte) '/', str);
        Assert.assertArrayEquals(("/" + str).getBytes(), content);
    }

    @Test
    public void writePdfStringTest() {
        String str = "Some PdfString";
        byte[] content = ByteUtils.getIsoBytes((byte) '(', str, (byte) ')');
        Assert.assertArrayEquals(("(" + str + ")").getBytes(), content);
    }
}
