package com.itextpdf.styledxmlparser.css.resolve;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssInheritanceUnitTest {

    @Test
    public void isInheritablePositiveTest(){
        IStyleInheritance cssInheritance = new CssInheritance();
        Assert.assertTrue(cssInheritance.isInheritable(CommonCssConstants.FONT_SIZE));
    }

    @Test
    public void isInheritableNegativeTest(){
        IStyleInheritance cssInheritance = new CssInheritance();
        Assert.assertFalse(cssInheritance.isInheritable(CommonCssConstants.FOCUS));
    }
}
