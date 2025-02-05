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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.exceptions.StyledXmlParserExceptionMessage;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.selector.item.CssAttributeSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssIdSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoElementSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssSeparatorSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssTagSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities class to parse a CSS selector.
 */
public final class CssSelectorParser {

    /**
     * Set of legacy pseudo elements (first-line, first-letter, before, after).
     */
    private static final Set<String> LEGACY_PSEUDO_ELEMENTS;
    static {
        // HashSet is required in order to autoport correctly in .Net
        HashSet<String> tempSet = new HashSet<>();
        tempSet.add("first-line");
        tempSet.add("first-letter");
        tempSet.add("before");
        tempSet.add("after");
        LEGACY_PSEUDO_ELEMENTS = Collections.unmodifiableSet(tempSet);
    }

    /**
     * The pattern string for selectors.
     */
    private static final String SELECTOR_PATTERN_STR =
            "(\\*)|([_a-zA-Z][\\w-]*)|(\\.[_a-zA-Z][\\w-]*)|(#[_a-z][\\w-]*)|(\\[[_a-zA-Z][\\w-]*(([~^$*|])?=((\"[^\"]+\")|([^\"]+)|('[^']+')|(\"\")|('')))?\\])|(::?[a-zA-Z-]*)|( )|(\\+)|(>)|(~)";

    /**
     * The pattern for selectors.
     */
    private static final Pattern selectorPattern = Pattern.compile(SELECTOR_PATTERN_STR);

    /**
     * Creates a new {@link CssSelectorParser} instance.
     */
    private CssSelectorParser() {
    }

    /**
     * Parses the selector items.
     *
     * @param selector the selectors in the form of a {@link String}
     * @return the resulting list of {@link ICssSelectorItem}
     */
    public static List<ICssSelectorItem> parseSelectorItems(String selector) {
        List<ICssSelectorItem> selectorItems = new ArrayList<>();
        Matcher match = selectorPattern.matcher(selector);
        boolean tagSelectorDescription = false;
        while (match.find()) {
            String selectorItem = match.group(0);
            char firstChar = selectorItem.charAt(0);
            switch (firstChar) {
                case '#':
                    selectorItems.add(new CssIdSelectorItem(selectorItem.substring(1)));
                    break;
                case '.':
                    selectorItems.add(new CssClassSelectorItem(selectorItem.substring(1)));
                    break;
                case '[':
                    selectorItems.add(new CssAttributeSelectorItem(selectorItem));
                    break;
                case ':':
                    appendPseudoSelector(selectorItems, selectorItem, match, selector);
                    break;
                case ' ':
                case '+':
                case '>':
                case '~':
                    if (selectorItems.size() == 0) {
                        throw new IllegalArgumentException(MessageFormatUtil.format(
                                StyledXmlParserExceptionMessage.INVALID_TOKEN_AT_THE_BEGINNING_OF_SELECTOR,
                                firstChar));
                    }
                    ICssSelectorItem lastItem = selectorItems.get(selectorItems.size() - 1);
                    CssSeparatorSelectorItem curItem = new CssSeparatorSelectorItem(firstChar);
                    if (lastItem instanceof CssSeparatorSelectorItem) {
                        if (curItem.getSeparator() == ' ') {
                            break;
                        } else if (((CssSeparatorSelectorItem) lastItem).getSeparator() == ' ') {
                            selectorItems.set(selectorItems.size() - 1, curItem);
                        } else {
                            throw new IllegalArgumentException(MessageFormatUtil.format("Invalid selector description. Two consequent characters occurred: {0}, {1}", ((CssSeparatorSelectorItem) lastItem).getSeparator(), curItem.getSeparator()));
                        }
                    } else {
                        selectorItems.add(curItem);
                        tagSelectorDescription = false;
                    }
                    break;
                default: //and case '*':
                    if (tagSelectorDescription) {
                        throw new IllegalStateException("Invalid selector string");
                    }
                    tagSelectorDescription = true;
                    selectorItems.add(new CssTagSelectorItem(selectorItem));
                    break;
            }
        }

        if (selectorItems.size() == 0) {
            throw new IllegalArgumentException("Selector declaration is invalid");
        }

        return selectorItems;
    }

    /**
     * Resolves a pseudo selector and appends it to list.
     *
     * @param selectorItems list of items to which new selector will be added to
     * @param pseudoSelector the pseudo selector
     * @param match the corresponding {@link Matcher}.
     * @param source is the original source
     */
    private static void appendPseudoSelector(List<ICssSelectorItem> selectorItems, String pseudoSelector,
            Matcher match, String source) {
        pseudoSelector = pseudoSelector.toLowerCase();
        pseudoSelector = handleBracketsOfPseudoSelector(pseudoSelector, match, source);

        /*
            This :: notation is introduced by the current document in order to establish a discrimination between
            pseudo-classes and pseudo-elements.
            For compatibility with existing style sheets, user agents must also accept the previous one-colon
            notation for pseudo-elements introduced in CSS levels 1 and 2 (namely, :first-line, :first-letter, :before and :after).
            This compatibility is not allowed for the new pseudo-elements introduced in this specification.
         */
        if (pseudoSelector.startsWith("::")) {
            selectorItems.add(new CssPseudoElementSelectorItem(pseudoSelector.substring(2)));
        } else if (pseudoSelector.startsWith(":") && LEGACY_PSEUDO_ELEMENTS.contains(pseudoSelector.substring(1))) {
            selectorItems.add(new CssPseudoElementSelectorItem(pseudoSelector.substring(1)));
        } else {
            ICssSelectorItem pseudoClassSelectorItem = CssPseudoClassSelectorItem.create(pseudoSelector.substring(1));
            if (pseudoClassSelectorItem == null) {
                throw new IllegalArgumentException(
                        MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNSUPPORTED_PSEUDO_CSS_SELECTOR,
                                pseudoSelector));
            }
            selectorItems.add(pseudoClassSelectorItem);
        }
    }

    /**
     * Resolves a pseudo selector if it contains brackets. Updates internal state of
     * {@link Matcher} if necessary.
     *
     * @param pseudoSelector the pseudo selector
     * @param match the corresponding {@link Matcher}.
     * @param source is the original source
     */
    private static String handleBracketsOfPseudoSelector(String pseudoSelector, Matcher match, String source) {
        int start = match.start() + pseudoSelector.length();
        if (start < source.length() && source.charAt(start) == '(') {
            int bracketDepth = 1;
            int curr = start + 1;
            while(bracketDepth > 0 && curr < source.length()) {
                if (source.charAt(curr) == '(') {
                    ++bracketDepth;
                } else if (source.charAt(curr) == ')') {
                    --bracketDepth;
                } else if (source.charAt(curr) == '"' || source.charAt(curr) == '\'') {
                    curr = CssUtils.findNextUnescapedChar(source, source.charAt(curr), curr + 1);
                }
                ++curr;
            }
            if (bracketDepth == 0) {
                match.region(curr, source.length());
                pseudoSelector += source.substring(start, curr);
            }
        }
        return pseudoSelector;
    }
}
