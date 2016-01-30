package com.itextpdf.io;

import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AdobeGlyphListTest {

    @Test
    public void testGlyphListCount() {
        Assert.assertEquals(4200, AdobeGlyphList.getNameToUnicodeLength());
        Assert.assertEquals(3680, AdobeGlyphList.getUnicodeToNameLength());

    }
}
