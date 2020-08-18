package com.itextpdf.layout;

import com.itextpdf.layout.property.BackgroundRepeat;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BackgroundRepeatUnitTest extends ExtendedITextTest {

    @Test
    public void constructorTest() {
        final BackgroundRepeat backgroundRepeat = new BackgroundRepeat(true, false);
        Assert.assertTrue(backgroundRepeat.isRepeatX());
        Assert.assertFalse(backgroundRepeat.isRepeatY());
    }
}
