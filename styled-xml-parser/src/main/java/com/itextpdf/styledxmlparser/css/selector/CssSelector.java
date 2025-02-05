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
package com.itextpdf.styledxmlparser.css.selector;


import com.itextpdf.styledxmlparser.css.parse.CssSelectorParser;
import com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoElementSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssSeparatorSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.List;

/**
 * {@link ICssSelector} implementation for CSS selectors.
 */
public class CssSelector extends AbstractCssSelector {

    /**
     * Creates a new {@link CssSelector} instance.
     *
     * @param selectorItems the selector items
     */
    public CssSelector(List<ICssSelectorItem> selectorItems) {
        super(selectorItems);
    }

    /**
     * Creates a new {@link CssSelector} instance.
     *
     * @param selector the selector
     */
    public CssSelector(String selector) {
        this(CssSelectorParser.parseSelectorItems(selector));
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.ICssSelector#matches(com.itextpdf.styledxmlparser.html.node.INode)
     */
    public boolean matches(INode element) {
        return matches(element, selectorItems.size() - 1);
    }

    /**
     * Checks if a node matches the selector.
     *
     * @param element the node
     * @param lastSelectorItemInd the index of the last selector
     * @return true, if there's a match
     */
    private boolean matches(INode element, int lastSelectorItemInd) {
        if (!(element instanceof IElementNode)) {
            return false;
        }
        if (lastSelectorItemInd < 0) {
            return true;
        }
        boolean isPseudoElement = element instanceof CssPseudoElementNode;
        for (int i = lastSelectorItemInd; i >= 0; i--) {
            if (isPseudoElement && selectorItems.get(lastSelectorItemInd) instanceof CssPseudoElementSelectorItem && i < lastSelectorItemInd) {
                // Pseudo element selector item shall be at the end of the selector string
                // and be single pseudo element selector item in it. All other selector items are checked against
                // pseudo element node parent.
                element = element.parentNode();
                isPseudoElement = false;
            }
            ICssSelectorItem currentItem = selectorItems.get(i);
            if (currentItem instanceof CssSeparatorSelectorItem) {
                char separator = ((CssSeparatorSelectorItem) currentItem).getSeparator();
                switch (separator) {
                    case '>':
                        return matches(element.parentNode(), i - 1);
                    case ' ': {
                        INode parent = element.parentNode();
                        while (parent != null) {
                            boolean parentMatches = matches(parent, i - 1);
                            if (parentMatches) {
                                return true;
                            } else {
                                parent = parent.parentNode();
                            }
                        }
                        return false;
                    }
                    case '~': {
                        INode parent = element.parentNode();
                        if (parent != null) {
                            int indexOfElement = parent.childNodes().indexOf(element);
                            for (int j = indexOfElement - 1; j >= 0; j--) {
                                if (matches(parent.childNodes().get(j), i - 1)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                    case '+': {
                        INode parent = element.parentNode();
                        if (parent != null) {
                            int indexOfElement = parent.childNodes().indexOf(element);
                            INode previousElement = null;
                            for (int j = indexOfElement - 1; j >= 0; j--)
                                if (parent.childNodes().get(j) instanceof IElementNode) {
                                    previousElement = parent.childNodes().get(j);
                                    break;
                                }
                            if (previousElement != null)
                                return indexOfElement > 0 && matches(previousElement, i - 1);
                        }
                        return false;
                    }
                    default:
                        return false;
                }
            } else {
                if (!currentItem.matches(element)) {
                    return false;
                }
            }
        }

        return true;
    }

}
