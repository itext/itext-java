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
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.regex.Pattern;

/**
 * {@link ICssSelectorItem} implementation for attribute selectors.
 */
public class CssAttributeSelectorItem implements ICssSelectorItem {

    /** The property. */
    private String property;

    /** The match symbol. */
    private char matchSymbol = (char)0;

    /** The value. */
    private String value = null;

    /**
     * Creates a new {@link CssAttributeSelectorItem} instance.
     *
     * @param attrSelector the attribute
     */
    public CssAttributeSelectorItem(String attrSelector) {
        int indexOfEqual = attrSelector.indexOf('=');
        if (indexOfEqual == -1) {
            property = attrSelector.substring(1, attrSelector.length() - 1);
        } else {
            if (attrSelector.charAt(indexOfEqual + 1) == '"' || attrSelector.charAt(indexOfEqual + 1) == '\'') {
                value = attrSelector.substring(indexOfEqual + 2, attrSelector.length() - 2);
            } else {
                value = attrSelector.substring(indexOfEqual + 1, attrSelector.length() - 1);
            }
            matchSymbol = attrSelector.charAt(indexOfEqual - 1);
            if ("~^$*|".indexOf(matchSymbol) == -1) {
                matchSymbol = 0;
                property = attrSelector.substring(1, indexOfEqual);
            } else {
                property = attrSelector.substring(1, indexOfEqual - 1);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem#getSpecificity()
     */
    @Override
    public int getSpecificity() {
        return CssSpecificityConstants.CLASS_SPECIFICITY;
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
        String attributeValue = element.getAttribute(property);
        if (attributeValue == null) {
            return false;
        }
        if (value == null) {
            return true;
        } else {
            switch (matchSymbol) {
                case (char)0:
                    return value.equals(attributeValue);
                case '|':
                    return value.length() > 0 && attributeValue.startsWith(value) && (attributeValue.length() == value.length() || attributeValue.charAt(value.length()) == '-');
                case '^':
                    return value.length() > 0 && attributeValue.startsWith(value);
                case '$':
                    return value.length() > 0 && attributeValue.endsWith(value);
                case '~':
                    String pattern = MessageFormatUtil.format("(^{0}\\s+)|(\\s+{1}\\s+)|(\\s+{2}$)", value, value, value);
                    return Pattern.compile(pattern).matcher(attributeValue).matches();
                case '*':
                    return value.length() > 0 && attributeValue.contains(value);
                default:
                    return false;
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (value == null) {
            return MessageFormatUtil.format("[{0}]", property);
        } else {
            return MessageFormatUtil.format("[{0}{1}=\"{2}\"]", property, matchSymbol == 0 ? "" : String.valueOf(matchSymbol), value);
        }
    }
}
