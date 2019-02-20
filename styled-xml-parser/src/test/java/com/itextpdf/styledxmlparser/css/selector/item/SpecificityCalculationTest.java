/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.selector.CssPageSelector;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SpecificityCalculationTest extends ExtendedITextTest {

    // https://www.smashingmagazine.com/2007/07/css-specificity-things-you-should-know/
    // https://specificity.keegan.st/

    @Test
    public void test01() {
        Assert.assertEquals(0, getSpecificity("*"));
    }

    @Test
    public void test02() {
        Assert.assertEquals(1, getSpecificity("li"));
    }

    @Test
    public void test03() {
        Assert.assertEquals(2, getSpecificity("li:first-line"));
    }

    @Test
    public void test04() {
        Assert.assertEquals(2, getSpecificity("ul li"));
    }

    @Test
    public void test05() {
        Assert.assertEquals(3, getSpecificity("ul ol+li"));
    }

    @Test
    public void test06() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY, getSpecificity("h1 + *[rel=up]"));
    }

    @Test
    public void test07() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY * 3, getSpecificity("ul ol li.red"));
    }

    @Test
    public void test08() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2 + CssSpecificityConstants.ELEMENT_SPECIFICITY, getSpecificity("li.red.level"));
    }

    @Test
    public void test09() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(".sith"));
    }

    @Test
    public void test10() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getSpecificity("div p.sith"));
    }

    @Test
    public void test11() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY, getSpecificity("#sith"));
    }

    @Test
    public void test12() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY + CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getSpecificity("body #darkside .sith p"));
    }

    @Test
    public void test13() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2 + CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getSpecificity("li:first-child h2 .title"));
    }

    @Test
    public void test14() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY * 2 + CssSpecificityConstants.ELEMENT_SPECIFICITY, getSpecificity("#nav .selected > a:hover"));
    }

    @Test
    public void test15() {
        Assert.assertEquals(2, getSpecificity("p:before"));
        Assert.assertEquals(2, getSpecificity("p::before"));
    }

    @Test
    public void test16() {
        Assert.assertEquals(2, getSpecificity("a::hover"));
    }

    @Test
    public void test17() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2, getSpecificity(".class_name:nth-child(3n + 1)"));
    }

    @Test
    public void test18() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2, getSpecificity(".class_name:nth-child(2n - 3)"));
    }

    @Test
    public void test19() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY * 2, getSpecificity(".class_name:hover"));
    }

    @Test
    public void test20() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(":not(p)"));
    }

    @Test
    public void test21() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(":not(#id)"));
    }

    @Test
    public void test22() {
        Assert.assertEquals(CssSpecificityConstants.CLASS_SPECIFICITY, getSpecificity(":not(.class_name)"));
    }

    @Test
    public void pageTest01() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY, getPageSelectorSpecificity("customPageName"));
    }

    @Test
    public void pageTest02() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY, getPageSelectorSpecificity("customPageName:first"));
    }

    @Test
    public void pageTest03() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY * 2, getPageSelectorSpecificity("customPageName:first:blank"));
    }

    @Test
    public void pageTest04() {
        Assert.assertEquals(CssSpecificityConstants.ELEMENT_SPECIFICITY * 2, getPageSelectorSpecificity(":left:right"));
    }

    @Test
    public void pageTest05() {
        Assert.assertEquals(CssSpecificityConstants.ID_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY, getPageSelectorSpecificity("left:blank"));
    }

    @Test
    public void pageTest06() {
        Assert.assertEquals(CssSpecificityConstants.ELEMENT_SPECIFICITY + CssSpecificityConstants.CLASS_SPECIFICITY, getPageSelectorSpecificity(":left:blank"));
    }

    private int getSpecificity(String selector) {
        return new CssSelector(selector).calculateSpecificity();
    }

    private int getPageSelectorSpecificity(String selector) {
        return new CssPageSelector(selector).calculateSpecificity();
    }

}
