package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DIContainerTest extends ExtendedITextTest {

    @Test
    public void testGetRegisteredInstance() {
        DIContainer di = new DIContainer();
        di.register(String.class, "hello");
        Assert.assertEquals("hello", di.getInstance(String.class));
    }

    @Test
    public void testRegisterDefaultInstance() {
        DIContainer.registerDefault(String.class, () -> "hello");
        DIContainer di = new DIContainer();
        Assert.assertEquals("hello", di.getInstance(String.class));
    }

}