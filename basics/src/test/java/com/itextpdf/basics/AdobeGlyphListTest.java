package com.itextpdf.basics;

import com.itextpdf.basics.font.AdobeGlyphList;
import org.junit.Assert;
import org.junit.Test;

public class AdobeGlyphListTest {
    @Test
    public void testGlyphListCount() {
        Assert.assertEquals(4200, AdobeGlyphList.getNameToUnicodeLength());
        Assert.assertEquals(3680, AdobeGlyphList.getUnicodeToNameLength());

    }
}
