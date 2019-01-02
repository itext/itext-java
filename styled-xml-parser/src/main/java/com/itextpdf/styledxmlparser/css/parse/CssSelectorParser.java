/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utilities class to parse a CSS selector.
 */
public final class CssSelectorParser {

    /**
     * Set of legacy pseudo elements (first-line, first-letter, before, after).
     */
    private static final Set<String> legacyPseudoElements = new HashSet<>();
    static {
        legacyPseudoElements.add("first-line");
        legacyPseudoElements.add("first-letter");
        legacyPseudoElements.add("before");
        legacyPseudoElements.add("after");
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
        CssSelectorParserMatch match = new CssSelectorParserMatch(selector, selectorPattern);
        boolean tagSelectorDescription = false;
        while (match.success()) {
            String selectorItem = match.getValue();
            char firstChar = selectorItem.charAt(0);
            switch (firstChar) {
                case '#':
                    match.next();
                    selectorItems.add(new CssIdSelectorItem(selectorItem.substring(1)));
                    break;
                case '.':
                    match.next();
                    selectorItems.add(new CssClassSelectorItem(selectorItem.substring(1)));
                    break;
                case '[':
                    match.next();
                    selectorItems.add(new CssAttributeSelectorItem(selectorItem));
                    break;
                case ':':
                    appendPseudoSelector(selectorItems, selectorItem, match);
                    break;
                case ' ':
                case '+':
                case '>':
                case '~':
                    match.next();
                    if (selectorItems.size() == 0) {
                        throw new IllegalArgumentException(MessageFormatUtil.format("Invalid token detected in the start of the selector string: {0}", firstChar));
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
                    match.next();
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
     * Resolves a pseudo selector, appends it to list and updates {@link CssSelectorParserMatch} in process.
     *
     * @param selectorItems list of items to which new selector will be added to
     * @param pseudoSelector the pseudo selector
     * @param match the corresponding {@link CssSelectorParserMatch} that will be updated.
     */
    private static void appendPseudoSelector(List<ICssSelectorItem> selectorItems, String pseudoSelector, CssSelectorParserMatch match) {
        pseudoSelector = pseudoSelector.toLowerCase();
        int start = match.getIndex() + pseudoSelector.length();
        String source = match.getSource();
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
                match.next(curr);
                pseudoSelector += source.substring(start, curr);
            } else {
                match.next();
            }
        } else {
            match.next();
        }
        /*
            This :: notation is introduced by the current document in order to establish a discrimination between
            pseudo-classes and pseudo-elements.
            For compatibility with existing style sheets, user agents must also accept the previous one-colon
            notation for pseudo-elements introduced in CSS levels 1 and 2 (namely, :first-line, :first-letter, :before and :after).
            This compatibility is not allowed for the new pseudo-elements introduced in this specification.
         */
        if (pseudoSelector.startsWith("::")) {
            selectorItems.add(new CssPseudoElementSelectorItem(pseudoSelector.substring(2)));
        } else if (pseudoSelector.startsWith(":") && legacyPseudoElements.contains(pseudoSelector.substring(1))) {
            selectorItems.add(new CssPseudoElementSelectorItem(pseudoSelector.substring(1)));
        } else {
            ICssSelectorItem pseudoClassSelectorItem = CssPseudoClassSelectorItem.create(pseudoSelector.substring(1));
            if (pseudoClassSelectorItem == null) {
                throw new IllegalArgumentException(MessageFormatUtil.format(LogMessageConstant.UNSUPPORTED_PSEUDO_CSS_SELECTOR, pseudoSelector));
            }
            selectorItems.add(pseudoClassSelectorItem);
        }
    }
}
