package com.itextpdf.kernel.geom;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PageSizeUnitTest extends ExtendedITextTest {

    @Test
    public void constructFromRectangleTest() {
        Rectangle rectangle = new Rectangle(0, 0, 100, 200);
        PageSize pageSize = new PageSize(rectangle);
        Assert.assertEquals(rectangle.x, pageSize.x, 1e-5);
        Assert.assertEquals(rectangle.y, pageSize.y, 1e-5);
        Assert.assertEquals(rectangle.width, pageSize.width, 1e-5);
        Assert.assertEquals(rectangle.height, pageSize.height, 1e-5);
    }
}
