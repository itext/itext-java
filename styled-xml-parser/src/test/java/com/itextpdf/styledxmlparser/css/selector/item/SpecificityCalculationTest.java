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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.selector.CssPageSelector;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SpecificityCalculationTest extends ExtendedITextTest {

    // https://www.smashingmagazine.com/2007/07/css-specificity-things-you-should-know/
    // https://specificity.keegan.st/

    @Test
    public void test01() {
        Assertions.assertEquals(0, getSpecificity("*"));
    }

    @Test
    public void test02() {
        Assertions.assertEquals(1, getSpecificity("li"));
    }

    @Test
    public void test03() {
        Assertions.assertEquals(2, getSpecificity("li:first-line"));
    }

    @Test
    public void test04() {
        Assertions.assertEquals(2, getSpecificity("ul li"));
    }

    @Test
    public void test05() {
        Assertions.assertEquals(3, getSpecificity("ul ol+li"));
    }

    @Test
    public void test06() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY, getSpecificity("h1 + *[rel=up]"));
    }

    @Test
    public void test07() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY * 3, getSpecificity("ul ol li.red"));
    }

    @Test
    public void test08() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2 + CssSpecificityConstants.ELEMENT_SPECIFICITY, getSpecificity("li.red.level"));
    }

    @Test
    public void test09() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(".sith"));
    }

    @Test
    public void test10() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getSpecificity("div p.sith"));
    }

    @Test
    public void test11() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY, getSpecificity("#sith"));
    }

    @Test
    public void test12() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getSpecificity("body #darkside .sith p"));
    }

    @Test
    public void test13() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2 + CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getSpecificity("li:first-child h2 .title"));
    }

    @Test
    public void test14() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY * 2 + CssSpecificityConstants.ELEMENT_SPECIFICITY, getSpecificity("#nav .selected > a:hover"));
    }

    @Test
    public void test15() {
        Assertions.assertEquals(2, getSpecificity("p:before"));
        Assertions.assertEquals(2, getSpecificity("p::before"));
    }

    @Test
    public void test16() {
        Assertions.assertEquals(2, getSpecificity("a::hover"));
    }

    @Test
    public void test17() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2, getSpecificity(".class_name:nth-child(3n + 1)"));
    }

    @Test
    public void test18() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2, getSpecificity(".class_name:nth-child(2n - 3)"));
    }

    @Test
    public void test19() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2, getSpecificity(".class_name:hover"));
    }

    @Test
    public void test20() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(":not(p)"));
    }

    @Test
    public void test21() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(":not(#id)"));
    }

    @Test
    public void test22() {
        Assertions.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(":not(.class_name)"));
    }

    @Test
    public void pageTest01() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY, getPageSelectorSpecificity("customPageName"));
    }

    @Test
    public void pageTest02() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY, getPageSelectorSpecificity("customPageName:first"));
    }

    @Test
    public void pageTest03() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY * 2, getPageSelectorSpecificity("customPageName:first:blank"));
    }

    @Test
    public void pageTest04() {
        Assertions.assertEquals(CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getPageSelectorSpecificity(":left:right"));
    }

    @Test
    public void pageTest05() {
        Assertions.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY, getPageSelectorSpecificity("left:blank"));
    }

    @Test
    public void pageTest06() {
        Assertions.assertEquals(CssSpecificityConstants.ELEMENT_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY, getPageSelectorSpecificity(":left:blank"));
    }

    private int getSpecificity(String selector) {
        return new CssSelector(selector).calculateSpecificity();
    }

    private int getPageSelectorSpecificity(String selector) {
        return new CssPageSelector(selector).calculateSpecificity();
    }

}
