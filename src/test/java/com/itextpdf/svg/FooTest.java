package com.itextpdf.svg;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FooTest {

    @Test
    public void test() {
        Foo foo = new Foo();
        assertThat(foo.toString(), equalTo("Hello, world"));
    }

}
