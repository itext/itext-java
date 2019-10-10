package com.itextpdf.styledxmlparser.css;

import com.itextpdf.styledxmlparser.css.page.CssMarginRule;
import com.itextpdf.styledxmlparser.css.page.CssPageRule;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssNestedAtRuleFactoryTest extends ExtendedITextTest {

    @Test
    public void testCreatingNestedRule() {
        CssNestedAtRule pageRule = CssNestedAtRuleFactory.createNestedRule("page:first");
        Assert.assertTrue(pageRule instanceof CssPageRule);
        Assert.assertEquals(CssRuleName.PAGE, pageRule.getRuleName());
        Assert.assertEquals(":first", pageRule.getRuleParameters());

        CssNestedAtRule rightBottomMarginRule = CssNestedAtRuleFactory.createNestedRule("bottom-right");
        Assert.assertTrue(rightBottomMarginRule instanceof CssMarginRule);
        Assert.assertEquals(CssRuleName.BOTTOM_RIGHT, rightBottomMarginRule.getRuleName());

        CssNestedAtRule fontFaceRule = CssNestedAtRuleFactory.createNestedRule("font-face");
        Assert.assertTrue(fontFaceRule instanceof CssFontFaceRule);
        Assert.assertEquals(CssRuleName.FONT_FACE, fontFaceRule.getRuleName());
    }

}
