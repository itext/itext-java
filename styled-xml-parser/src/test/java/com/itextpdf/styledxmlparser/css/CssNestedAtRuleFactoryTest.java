/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
