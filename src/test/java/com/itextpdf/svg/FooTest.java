package com.itextpdf.svg;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FooTest {

    @Test
    public void test() {
        Foo foo = new Foo();
        assertEquals(foo.toString(), "Hello, world");
    }

}
