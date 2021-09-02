package com.itextpdf.commons.actions.data;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CommonsProductDataTest extends ExtendedITextTest {
    @Test
    public void getInstanceTest() {
        ProductData commonsProductData = CommonsProductData.getInstance();

        Assert.assertEquals("Commons", commonsProductData.getPublicProductName());
        Assert.assertEquals("commons", commonsProductData.getProductName());
        Assert.assertEquals("7.2.0-SNAPSHOT", commonsProductData.getVersion());
        Assert.assertEquals(2000, commonsProductData.getSinceCopyrightYear());
        Assert.assertEquals(2021, commonsProductData.getToCopyrightYear());
    }
}
