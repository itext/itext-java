package com.itextpdf.svg.utils;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgCssUtilsTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    public void commaSplitValueTest() {
        String input = "a,b,c,d";
        List<String> expected = new ArrayList<>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        expected.add("d");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void whitespaceSplitValueTest() {
        String input = "1 2 3 4";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void newLineSplitValueTest() {
        String input = "1\n2\n3\n4";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tabSplitValueTest() {
        String input = "1\t2\t3\t4";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void mixedCommaWhitespaceSplitValueTest() {
        String input = "1,2 a,b";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("a");
        expected.add("b");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSplitValueTest() {
        List<String> actual = SvgCssUtils.splitValueList(null);

        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void emptySplitValueTest() {
        List<String> actual = SvgCssUtils.splitValueList("");

        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void normalConvertPtsToPxTest() {
        float[] input = new float[] { -1f, 0f, 1f };
        float[] expected = new float[] {-0.75f, 0f, 0.75f};

        for (int i = 0; i < input.length; i++) {
            float actual = SvgCssUtils.convertPtsToPx(input[i]);
            Assert.assertEquals(expected[i], actual, 0f);
        }
    }

    @Test
    public void convertFloatMaximumToPdfTest() {
        float expected = 2.5521175E38f;
        float actual = SvgCssUtils.convertPtsToPx(Float.MAX_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void convertFloatToStringTest() {
        String expected = "0.5";
        String actual = SvgCssUtils.convertFloatToString(0.5f);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void convertLongerFloatToStringTest() {
        String expected = "0.1234567";
        String actual = SvgCssUtils.convertFloatToString(0.1234567f);

        Assert.assertEquals(expected, actual);
    }

    @Ignore("TODO: Check autoport for failing float comparisons. Blocked by RND-882\n")
    @Test
    public void convertFloatMinimumToPdfTest() {
        float expected = 1.4E-45f;
        float actual = SvgCssUtils.convertPtsToPx(Float.MIN_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }
}