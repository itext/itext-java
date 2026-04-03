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

package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.parse.CssSelectorParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for pseudo-classes that accept a forgiving selector list (e.g. :is(), :where()).
 */
abstract class CssPseudoClassForgivingSelectorListSelectorItem extends CssPseudoClassSelectorItem {
    protected final List<ICssSelector> selectorList;

    protected CssPseudoClassForgivingSelectorListSelectorItem(String pseudoClass, List<ICssSelector> selectorList,
                                                              String argumentsString) {
        super(pseudoClass, argumentsString);
        this.selectorList = selectorList;
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        for (ICssSelector sel : selectorList) {
            if (sel != null && sel.matches(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a forgiving selector list for :is() / :where().
     * <p>
     * Per Selectors Level 4, :is() and :where() accept a forgiving selector list:
     * invalid selectors are ignored rather than invalidating the whole pseudo-class.
     *
     * @param arguments selector list as written inside parentheses
     * @return list of valid selectors (possibly empty), or null if arguments are syntactically missing
     */
    static List<ICssSelector> parseForgivingSelectorListWithoutPseudoElements(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            // :is() / :where() with empty arguments is invalid.
            return null;
        }

        List<String> parts = CssSelectorParser.splitByTopLevelComma(arguments);
        if (parts.isEmpty()) {
            return null;
        }

        List<ICssSelector> selectors = new ArrayList<>();
        for (String rawPart : parts) {
            String part = rawPart == null ? "" : rawPart.trim();
            if (part.isEmpty()) {
                // Empty entries like :is(.a,,.b) are invalid selectors in the list; ignore (forgiving).
                continue;
            }

            try {
                CssSelector sel = new CssSelector(CssSelectorParser.parseSelectorItems(part, false));
                if (!containsPseudoElement(Collections.singletonList(sel))) {
                    selectors.add(sel);
                }
            } catch (IllegalArgumentException ex) {
                // Invalid/unsupported selector in the list; ignore (forgiving).
            }
        }

        return selectors;
    }

    static boolean containsPseudoElement(List<ICssSelector> selectors) {
        for (ICssSelector sel : selectors) {
            if (sel instanceof CssSelector) {
                for (ICssSelectorItem item : ((CssSelector) sel).getSelectorItems()) {
                    if (item instanceof CssPseudoElementSelectorItem) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}