package com.itextpdf.svg.utils;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgTextUtilTest {

    //Trim leading tests
    @Test
    public void trimLeadingTest(){
        String toTrim = "\t \t   to trim  \t";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "to trim  \t";
        Assert.assertEquals(expected,actual);
    }
    @Test
    public void trimLeadingEmptyTest(){
        String toTrim = "";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimLeadingNoLeadingTest(){
        String toTrim = "to Test  ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "to Test  ";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimLeadingSingleWhiteSpaceTest(){
        String toTrim = " to Test  ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "to Test  ";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimLeadingNonBreakingSpaceTest(){
        String toTrim = "\u00A0to Test  ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "\u00A0to Test  ";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimLeadingOnlyWhitespaceTest(){
        String toTrim = "\t\t\t   \t\t\t";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimLeadingLineBreakTest(){
        String toTrim = " \n Test ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "\n Test ";
        Assert.assertEquals(expected,actual);
    }

    //Trim trailing tests
    @Test
    public void trimTrailingTest(){
        String toTrim = "\t \t   to trim  \t";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "\t \t   to trim";
        Assert.assertEquals(expected,actual);
    }
    @Test
    public void trimTrailingEmptyTest(){
        String toTrim = "";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimTrailingNoTrailingTest(){
        String toTrim = "   to Test";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "   to Test";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimTrailingSingleWhiteSpaceTest(){
        String toTrim = " to Test ";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = " to Test";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimTrailingNonBreakingSpaceTest(){
        String toTrim = " to Test\u00A0";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = " to Test\u00A0";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimTrailingOnlyWhitespaceTest(){
        String toTrim = "\t\t\t   \t\t\t";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void trimTrailingLineBreakTest(){
        String toTrim = " to trim \n";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = " to trim \n";
        Assert.assertEquals(expected,actual);
    }
    
    

}
