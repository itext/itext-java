package com.itextpdf.styledxmlparser.css.resolve;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssPropertyMergerUnitTest {

    @Test
    public void mergeTextDecorationSimpleTest(){
        String firstValue="underline";
        String secondValue="strikethrough bold";

        String expected="underline strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationNormalizeFirstTest(){
        String firstValue="   underline  ";
        String secondValue="strikethrough bold";

        String expected="underline strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);

    }

    @Test
    public void mergeTextDecorationNormalizeSecondTest(){
        String firstValue="underline";
        String secondValue="strikethrough     bold   ";

        String expected="underline strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationFirstNullTest(){
        String firstValue=null;
        String secondValue="strikethrough bold";

        String expected="strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationSecondNullTest(){
        String firstValue="underline";
        String secondValue=null;

        String expected="underline";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationBothNullTest(){
        String firstValue=null;
        String secondValue=null;

        String expected=null;
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }


    @Test
    public void mergeTextDecorationEmpyInputsTest(){
        String firstValue="";
        String secondValue="";

        String expected="none";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationSecondInputContainsNoneTest(){
        String firstValue="underline";
        String secondValue="none strikethrough";

        String expected="underline";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationFirstInputNoneTest(){
        String firstValue="underline none";
        String secondValue="strikethrough";

        String expected="strikethrough";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationBothInputsNoneTest(){
        String firstValue="underline none";
        String secondValue="strikethrough none";

        String expected="none";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assert.assertEquals(expected,actual);
    }

}
