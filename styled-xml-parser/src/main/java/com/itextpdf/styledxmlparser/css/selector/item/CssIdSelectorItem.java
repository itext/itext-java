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

import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

/**
 * {@link ICssSelectorItem} implementation for id selectors.
 */
public class CssIdSelectorItem implements ICssSelectorItem {

    /** The id. */
    private String id;

    /**
     * Creates a new {@link CssIdSelectorItem} instance.
     *
     * @param id the id
     */
    public CssIdSelectorItem(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#getSpecificity()
     */
    @Override
    public int getSpecificity() {
        return CssSpecificityConstants.ID_SPECIFICITY;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#matches(com.itextpdf.styledxmlparser.html.node.INode)
     */
    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        IElementNode element = (IElementNode) node;
        return id.equals(element.getAttribute(CommonAttributeConstants.ID));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#" + id;
    }
}
