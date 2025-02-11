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

import com.itextpdf.styledxmlparser.css.page.PageMarginBoxContextNode;
import com.itextpdf.styledxmlparser.node.INode;

/**
 * {@link ICssSelector} implementation for CSS page margin box selectors.
 */
public class CssPageMarginBoxSelector implements ICssSelector {
    
    /** The page margin box name. */
    private String pageMarginBoxName;
    
    /** The page selector. */
    private ICssSelector pageSelector;

    /**
     * Creates a new {@link CssPageMarginBoxSelector} instance.
     *
     * @param pageMarginBoxName the page margin box name
     * @param pageSelector the page selector
     */
    public CssPageMarginBoxSelector(String pageMarginBoxName, ICssSelector pageSelector) {
        this.pageMarginBoxName = pageMarginBoxName;
        this.pageSelector = pageSelector;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.ICssSelector#calculateSpecificity()
     */
    @Override
    public int calculateSpecificity() {
        return pageSelector.calculateSpecificity();
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.ICssSelector#matches(com.itextpdf.styledxmlparser.html.node.INode)
     */
    @Override
    public boolean matches(INode node) {
        if (!(node instanceof PageMarginBoxContextNode)) {
            return false;
        }
        PageMarginBoxContextNode marginBoxNode = (PageMarginBoxContextNode) node;
        if (pageMarginBoxName.equals(marginBoxNode.getMarginBoxName())) {
            INode parent = node.parentNode();
            return pageSelector.matches(parent);   
        }
        return false;
    }
}
