package com.itextpdf.io.source;

import org.junit.Assert;
import org.junit.Test;

public class WriteStringsTest {

    @Test
    public void writeStringTest() {
        String str = "SomeString";
        byte[] content = OutputStream.getIsoBytes(str);
        Assert.assertArrayEquals(str.getBytes(), content);
    }

    @Test
    public void writeNameTest() {
        String str = "SomeName";
        byte[] content = OutputStream.getIsoBytes((byte) '/', str);
        Assert.assertArrayEquals(("/"+str).getBytes(), content);
    }

    @Test
    public void writePdfStringTest() {
        String str = "Some PdfString";
        byte[] content = OutputStream.getIsoBytes((byte) '(', str, (byte) ')');
        Assert.assertArrayEquals(("(" + str + ")").getBytes(), content);
    }
}
