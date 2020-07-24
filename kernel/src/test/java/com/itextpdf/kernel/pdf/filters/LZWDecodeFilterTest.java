package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class LZWDecodeFilterTest extends ExtendedITextTest {

    @Test
    public void decodingTestStatic() {
        byte[] bytes = {(byte) 0x80, 0x0B, 0x60, 0x50, 0x22, 0x0C, 0x0C, (byte) 0x85, 0x01};
        String expectedResult = "-----A---B";

        String decoded = new String(LZWDecodeFilter.LZWDecode(bytes));
        Assert.assertEquals(expectedResult, decoded);
    }

    @Test
    public void decodingTestNonStatic() {
        byte[] bytes = {(byte) 0x80, 0x0B, 0x60, 0x50, 0x22, 0x0C, 0x0C, (byte) 0x85, 0x01};
        String expectedResult = "-----A---B";

        LZWDecodeFilter filter = new LZWDecodeFilter();
        String decoded = new String(filter.decode(bytes,null, new PdfDictionary(),
                new PdfDictionary()));
        Assert.assertEquals(expectedResult, decoded);
    }

}
