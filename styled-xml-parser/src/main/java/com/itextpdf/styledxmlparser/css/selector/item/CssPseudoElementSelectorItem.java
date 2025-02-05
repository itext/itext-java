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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode;
import com.itextpdf.styledxmlparser.node.INode;


/**
 * {@link ICssSelectorItem} implementation for pseudo element selectors.
 */
public class CssPseudoElementSelectorItem implements com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem {

    /** The pseudo element name. */
    private String pseudoElementName;

    /**
     * Creates a new {@link CssPseudoElementSelectorItem} instance.
     *
     * @param pseudoElementName the pseudo element name
     */
    public CssPseudoElementSelectorItem(String pseudoElementName) {
        this.pseudoElementName = pseudoElementName;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#getSpecificity()
     */
    @Override
    public int getSpecificity() {
        return CssSpecificityConstants.ELEMENT_SPECIFICITY;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#matches(com.itextpdf.styledxmlparser.html.node.INode)
     */
    @Override
    public boolean matches(INode node) {
        return node instanceof CssPseudoElementNode && ((CssPseudoElementNode) node).getPseudoElementName().equals(pseudoElementName);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "::" + pseudoElementName;
    }
}
