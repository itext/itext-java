package com.itextpdf.commons.actions;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AbstractProductITextEventTest extends ExtendedITextTest {
    @Test
    public void nullProductDataTest() {
        Exception exception =
                Assert.assertThrows(IllegalStateException.class, () -> new AbstractProductITextEvent(null) {});
        Assert.assertEquals("ProductData shouldn't be null.", exception.getMessage());
    }
}
