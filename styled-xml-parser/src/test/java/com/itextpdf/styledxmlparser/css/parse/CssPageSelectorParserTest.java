/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.styledxmlparser.css.selector.item.CssPagePseudoClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPageTypeSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("UnitTest")
public class CssPageSelectorParserTest extends ExtendedITextTest {

    public static Object[] provideInvalidSelectorTestData() {
        return new Object[] {
                ":not(:first)",
                ":someselectorname",
                "customPageName :someselectorname",
                ":someselectorname customPageName",
                ":left :someselectorname",
                ":someselectorname :right",
                ":first :someselectorname :blank",
                ":invalidselector:first:blank",
                ":first :blank :invalidselector"
        };
    }

    public static Object[][] provideValidSelectorTestData() {
        return new Object[][] {
                {"", new ICssSelectorItem[]{}},
                {"    ", new ICssSelectorItem[]{}},
                {":first",
                        new ICssSelectorItem[]{new CssPagePseudoClassSelectorItem("first")}},
                {":right:left",
                        new ICssSelectorItem[]{new CssPagePseudoClassSelectorItem("right"),
                                new CssPagePseudoClassSelectorItem("left")}},
                {":first :right",
                        new ICssSelectorItem[]{new CssPagePseudoClassSelectorItem("first"),
                                new CssPagePseudoClassSelectorItem("right")}},
                {":blank    :first",
                        new ICssSelectorItem[]{new CssPagePseudoClassSelectorItem("blank"),
                                new CssPagePseudoClassSelectorItem("first")}},
                {":blank:right:first",
                        new ICssSelectorItem[]{new CssPagePseudoClassSelectorItem("blank"),
                                new CssPagePseudoClassSelectorItem("right"),
                                new CssPagePseudoClassSelectorItem("first")}},
                {"customPageName",
                        new ICssSelectorItem[]{new CssPageTypeSelectorItem("customPageName")}},
                {"somePageName:first",
                        new ICssSelectorItem[]{new CssPageTypeSelectorItem("somePageName"),
                                new CssPagePseudoClassSelectorItem("first")}},
                {"namedPageExample :first :blank",
                        new ICssSelectorItem[]{new CssPageTypeSelectorItem("namedPageExample"),
                                new CssPagePseudoClassSelectorItem("first"),
                                new CssPagePseudoClassSelectorItem("blank")}}
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSelectorTestData")
    public void invalidSelectorTest(String selector) {
        List<ICssSelectorItem> parsedSelector = CssPageSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssPageSelectorParser.NeverMatchSelectorItem);
    }

    @ParameterizedTest
    @MethodSource("provideValidSelectorTestData")
    public void validSelectorTest(String selector, ICssSelectorItem[] expectedParsedSelector) throws IOException {
        List<ICssSelectorItem> parsedSelector = CssPageSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(expectedParsedSelector.length, parsedSelector.size());
        for (int i = 0; i < parsedSelector.size(); i++) {
            Assertions.assertEquals(expectedParsedSelector[i].getClass(), parsedSelector.get(i).getClass());
            Assertions.assertEquals(expectedParsedSelector[i].toString(), parsedSelector.get(i).toString());
        }
    }
}