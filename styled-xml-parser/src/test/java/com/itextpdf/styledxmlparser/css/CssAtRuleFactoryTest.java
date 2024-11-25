/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class CssAtRuleFactoryTest extends ExtendedITextTest {

    @Test
    public void testCreatingNestedRule() {
        CssNestedAtRule pageRule = CssAtRuleFactory.createNestedRule("page:first");
        Assertions.assertTrue(pageRule instanceof CssPageRule);
        Assertions.assertEquals(CssRuleName.PAGE, pageRule.getRuleName());
        Assertions.assertEquals(":first", pageRule.getRuleParameters());

        CssNestedAtRule rightBottomMarginRule = CssAtRuleFactory.createNestedRule("bottom-right");
        Assertions.assertTrue(rightBottomMarginRule instanceof CssMarginRule);
        Assertions.assertEquals(CssRuleName.BOTTOM_RIGHT, rightBottomMarginRule.getRuleName());

        CssNestedAtRule fontFaceRule = CssAtRuleFactory.createNestedRule("font-face");
        Assertions.assertTrue(fontFaceRule instanceof CssFontFaceRule);
        Assertions.assertEquals(CssRuleName.FONT_FACE, fontFaceRule.getRuleName());
    }

}
