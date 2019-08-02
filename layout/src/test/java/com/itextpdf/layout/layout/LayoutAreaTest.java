package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class LayoutAreaTest extends ExtendedITextTest {

    @Test
    public void cloneTest() {
        RootLayoutArea originalRootLayoutArea = new RootLayoutArea(1, new Rectangle(5, 10, 15, 20));
        originalRootLayoutArea.emptyArea = false;
        LayoutArea cloneAsLayoutArea = ((LayoutArea) originalRootLayoutArea).clone();
        RootLayoutArea cloneAsRootLayoutArea = (RootLayoutArea) originalRootLayoutArea.clone();

        Assert.assertTrue((originalRootLayoutArea).getBBox() != cloneAsLayoutArea.getBBox());

        Assert.assertEquals(RootLayoutArea.class, cloneAsRootLayoutArea.getClass());

        Assert.assertEquals(RootLayoutArea.class, cloneAsLayoutArea.getClass());
        Assert.assertFalse(((RootLayoutArea) cloneAsLayoutArea).isEmptyArea());
    }


}
