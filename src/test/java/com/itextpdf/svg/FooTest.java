package com.itextpdf.svg;

import org.junit.Assert;
import org.junit.Test;

public class FooTest {

    @Test
    public void test() {
        Foo foo = new Foo();
        Assert.assertEquals(foo.toString(), "Hello, world");
    }

}
