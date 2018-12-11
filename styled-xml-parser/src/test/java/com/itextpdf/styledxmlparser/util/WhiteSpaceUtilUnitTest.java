package com.itextpdf.styledxmlparser.util;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class WhiteSpaceUtilUnitTest {

    @Test
    public void collapseConsecutiveWhiteSpacesTest() {
        String toCollapse = "A   B";
        String actual = WhiteSpaceUtil.collapseConsecutiveSpaces(toCollapse);
        String expected = "A B";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void collapseConsecutiveWhiteSpacesTrailingWhiteSpaceTest() {
        String toCollapse = "A   B   ";
        String actual = WhiteSpaceUtil.collapseConsecutiveSpaces(toCollapse);
        String expected = "A B ";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void collapseConsecutiveWhiteSpacesPrecedingWhiteSpaceTest() {
        String toCollapse = "   A B";
        String actual = WhiteSpaceUtil.collapseConsecutiveSpaces(toCollapse);
        String expected = " A B";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void collapseConsecutiveWhiteSpacesPrecedingAndTrailingWhiteSpaceTest() {
        String toCollapse = "   A   B   ";
        String actual = WhiteSpaceUtil.collapseConsecutiveSpaces(toCollapse);
        String expected = " A B ";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void collapseConsecutiveWhiteSpacesNewLineWhiteSpaceTest() {
        String toCollapse = "\n   A B  \n";
        String actual = WhiteSpaceUtil.collapseConsecutiveSpaces(toCollapse);
        String expected = " A B ";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void collapseConsecutiveWhiteSpacesTabWhiteSpaceTest() {
        String toCollapse = "\t  A B  \t";
        String actual = WhiteSpaceUtil.collapseConsecutiveSpaces(toCollapse);
        String expected = " A B ";
        Assert.assertEquals(expected, actual);
    }
}
