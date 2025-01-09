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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.node.INode;

/**
 * {@link ICssSelectorItem} implementation for separator selectors.
 */
public class CssSeparatorSelectorItem implements ICssSelectorItem {

    /** The separator character. */
    private char separator;

    /**
     * Creates a new {@link CssSeparatorSelectorItem} instance.
     *
     * @param separator the separator character
     */
    public CssSeparatorSelectorItem(char separator) {
        this.separator = separator;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#getSpecificity()
     */
    @Override
    public int getSpecificity() {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#matches(com.itextpdf.styledxmlparser.html.node.INode)
     */
    @Override
    public boolean matches(INode node) {
        throw new IllegalStateException("Separator item is not supposed to be matched against an element");
    }

    /**
     * Gets the separator character.
     *
     * @return the separator character
     */
    public char getSeparator() {
        return separator;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return separator == ' ' ? " " : MessageFormatUtil.format(" {0} ", separator);
    }
}
