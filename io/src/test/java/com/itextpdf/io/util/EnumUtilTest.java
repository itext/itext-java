package com.itextpdf.io.util;

import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class EnumUtilTest {
    @Test
    public void testEnumUtilSameAmount() {
        Assert.assertEquals(3, EnumUtil.getAllValuesOfEnum(TestEnum1.class).size());
    }

    @Test
    public void testEnumUtilSameValues() {
        List<TestEnum1> list = EnumUtil.getAllValuesOfEnum(TestEnum1.class);
        Assert.assertTrue(list.contains(TestEnum1.A));
        Assert.assertTrue(list.contains(TestEnum1.B));
        Assert.assertTrue(list.contains(TestEnum1.C));
        Assert.assertEquals(TestEnum1.A, list.get(0));
        Assert.assertEquals(TestEnum1.B, list.get(1));
        Assert.assertEquals(TestEnum1.C, list.get(2));
    }
}

enum TestEnum1 {
    A, B, C
}
