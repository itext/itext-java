/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.styledxmlparser.css.selector.item.CssPagePseudoClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPageTypeSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities class to parse CSS page selectors.
 */
public final class CssPageSelectorParser {
    
    /** The pattern string for page selectors. */
    private static final String PAGE_SELECTOR_PATTERN_STR =
            "(^-?[_a-zA-Z][\\w-]*)|(:(?i)(left|right|first|blank))";

    /** The pattern for page selectors. */
    private static final Pattern selectorPattern = Pattern.compile(PAGE_SELECTOR_PATTERN_STR);

    /**
     * Parses the selector items into a list of {@link ICssSelectorItem} instances.
     *
     * @param selectorItemsStr the selector items in the form of a {@link String}
     * @return the resulting list of {@link ICssSelectorItem} instances
     */
    public static List<ICssSelectorItem> parseSelectorItems(String selectorItemsStr) {
        List<ICssSelectorItem> selectorItems = new ArrayList<>();
        Matcher itemMatcher = selectorPattern.matcher(selectorItemsStr);
        while (itemMatcher.find()) {
            String selectorItem = itemMatcher.group(0);
            if (selectorItem.charAt(0) == ':') {
                selectorItems.add(new CssPagePseudoClassSelectorItem(selectorItem.substring(1).toLowerCase()));
            } else {
                selectorItems.add(new CssPageTypeSelectorItem(selectorItem));
            }
        }
        return selectorItems;
    }
}
