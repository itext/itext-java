package com.itextpdf.io.util;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.regex.Pattern;

/**
 * At the moment there is no StringUtil class in Java, but there is one in C# and we are testing
 */
@Category(UnitTest.class)
public class StringUtilTest {

    @Test
    public void patternSplitTest01() {
        // Pattern.split in Java works differently compared to Regex.Split in C#
        // In C#, empty strings are possible at the beginning of the resultant array for non-capturing groups in split regex
        // Thus, in C# we use a separate utility for splitting to align the implementation with Java
        // This test verifies that the resultant behavior is the same
        Pattern pattern = Pattern.compile("(?=[ab])");
        String source = "a01aa78ab89b";
        String[] expected = new String[] {"a01", "a", "a78", "a", "b89", "b"};
        String[] result = pattern.split(source);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void patternSplitTest02() {
        Pattern pattern = Pattern.compile("(?=[ab])");
        String source = "";
        String[] expected = new String[] {""};
        String[] result = pattern.split(source);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void stringSplitTest01() {
        String source = "a01aa78ab89b";
        String[] expected = new String[] {"a01", "a", "a78", "a", "b89", "b"};
        String[] result = source.split("(?=[ab])");
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void stringSplitTest02() {
        String source = "";
        String[] expected = new String[] {""};
        String[] result = source.split("(?=[ab])");
        Assert.assertArrayEquals(expected, result);
    }

}
