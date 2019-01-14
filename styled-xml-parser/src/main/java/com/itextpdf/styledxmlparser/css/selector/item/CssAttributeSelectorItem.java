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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.io.util.MessageFormatUtil;
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
