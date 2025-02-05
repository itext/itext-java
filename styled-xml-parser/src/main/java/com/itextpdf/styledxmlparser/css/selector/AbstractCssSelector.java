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


import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;

import java.util.Collections;
import java.util.List;

/**
 * Abstract superclass for CSS Selectors.
 */
public abstract class AbstractCssSelector implements ICssSelector {
    
    /** The selector items. */
    protected List<ICssSelectorItem> selectorItems;

    /**
     * Creates a new {@link AbstractCssSelector} instance.
     *
     * @param selectorItems the selector items
     */
    public AbstractCssSelector(List<ICssSelectorItem> selectorItems) {
        this.selectorItems = selectorItems;
    }

    public List<ICssSelectorItem> getSelectorItems() {
        return Collections.unmodifiableList(selectorItems);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.ICssSelector#calculateSpecificity()
     */
    @Override
    public int calculateSpecificity() {
        int specificity = 0;
        for (ICssSelectorItem item : selectorItems) {
            specificity += item.getSpecificity();
        }
        return specificity;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ICssSelectorItem item : selectorItems) {
            sb.append(item.toString());
        }
        return sb.toString();
    }
}
