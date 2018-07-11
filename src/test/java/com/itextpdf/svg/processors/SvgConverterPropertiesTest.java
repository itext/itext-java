package com.itextpdf.svg.processors;

import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.nio.charset.StandardCharsets;

@Category(UnitTest.class)
public class SvgConverterPropertiesTest {

    @Test
    public void getCharsetNameRegressionTest() {
        String expected = StandardCharsets.UTF_8.name();
        String actual = new SvgConverterProperties().getCharset();
        Assert.assertEquals(expected, actual);
    }
}