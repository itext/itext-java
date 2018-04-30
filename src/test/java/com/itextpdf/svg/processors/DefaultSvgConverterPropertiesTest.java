package com.itextpdf.svg.processors;

import com.itextpdf.svg.processors.impl.DefaultSvgConverterProperties;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultSvgConverterPropertiesTest {

    @Test
    public void getCharsetNameRegressionTest() {
        String expected = StandardCharsets.UTF_8.name();
        String actual = new DefaultSvgConverterProperties().getCharset();

        Assert.assertEquals(expected, actual);
    }
}