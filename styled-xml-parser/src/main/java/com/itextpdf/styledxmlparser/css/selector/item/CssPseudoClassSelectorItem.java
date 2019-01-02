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

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.node.INode;

/**
 * {@link ICssSelectorItem} implementation for pseudo class selectors.
 */
public abstract class CssPseudoClassSelectorItem implements ICssSelectorItem {

    /**
     * The arguments.
     */
    protected String arguments;
    /**
     * The pseudo class.
     */
    private String pseudoClass;

    /**
     * Creates a new {@link CssPseudoClassSelectorItem} instance.
     *
     * @param pseudoClass the pseudo class name
     */
    protected CssPseudoClassSelectorItem(String pseudoClass) {
        this(pseudoClass, "");
    }

    protected CssPseudoClassSelectorItem(String pseudoClass, String arguments) {
        this.pseudoClass = pseudoClass;
        this.arguments = arguments;
    }

    public static CssPseudoClassSelectorItem create(String fullSelectorString) {
        int indexOfParentheses = fullSelectorString.indexOf('(');
        String pseudoClass;
        String arguments;
        if (indexOfParentheses == -1) {
            pseudoClass = fullSelectorString;
            arguments = "";
        } else {
            pseudoClass = fullSelectorString.substring(0, indexOfParentheses);
            arguments = fullSelectorString.substring(indexOfParentheses + 1, fullSelectorString.length() - 1).trim();
        }
        return create(pseudoClass, arguments);
    }

    public static CssPseudoClassSelectorItem create(String pseudoClass, String arguments) {
        switch (pseudoClass) {
            case CommonCssConstants.EMPTY:
                return CssPseudoClassEmptySelectorItem.getInstance();
            case CommonCssConstants.FIRST_CHILD:
                return CssPseudoClassFirstChildSelectorItem.getInstance();
            case CommonCssConstants.FIRST_OF_TYPE:
                return CssPseudoClassFirstOfTypeSelectorItem.getInstance();
            case CommonCssConstants.LAST_CHILD:
                return CssPseudoClassLastChildSelectorItem.getInstance();
            case CommonCssConstants.LAST_OF_TYPE:
                return CssPseudoClassLastOfTypeSelectorItem.getInstance();
            case CommonCssConstants.NTH_CHILD:
                return new CssPseudoClassNthChildSelectorItem(arguments);
            case CommonCssConstants.NTH_OF_TYPE:
                return new CssPseudoClassNthOfTypeSelectorItem(arguments);
            case CommonCssConstants.NOT:
                CssSelector selector = new CssSelector(arguments);
                for (ICssSelectorItem item : selector.getSelectorItems()) {
                    if (item instanceof CssPseudoClassNotSelectorItem || item instanceof CssPseudoElementSelectorItem) {
                        return null;
                    }
                }
                return new CssPseudoClassNotSelectorItem(selector);
            case CommonCssConstants.ROOT:
                return CssPseudoClassRootSelectorItem.getInstance();
            case CommonCssConstants.LINK:
                return new AlwaysApplySelectorItem(pseudoClass, arguments);
            case CommonCssConstants.ACTIVE:
            case CommonCssConstants.FOCUS:
            case CommonCssConstants.HOVER:
            case CommonCssConstants.TARGET:
            case CommonCssConstants.VISITED:
                return new AlwaysNotApplySelectorItem(pseudoClass, arguments);
            //Still unsupported, should be addressed in DEVSIX-1440
            //case CommonCssConstants.CHECKED:
            //case CommonCssConstants.DISABLED:
            //case CommonCssConstants.ENABLED:
            //case CommonCssConstants.IN_RANGE:
            //case CommonCssConstants.INVALID:
            //case CommonCssConstants.LANG:
            //case CommonCssConstants.NTH_LAST_CHILD:
            //case CommonCssConstants.NTH_LAST_OF_TYPE:
            //case CommonCssConstants.ONLY_OF_TYPE:
            //case CommonCssConstants.ONLY_CHILD:
            //case CommonCssConstants.OPTIONAL:
            //case CommonCssConstants.OUT_OF_RANGE:
            //case CommonCssConstants.READ_ONLY:
            //case CommonCssConstants.READ_WRITE:
            //case CommonCssConstants.REQUIRED:
            //case CommonCssConstants.VALID:
            default:
                return null;
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
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ":" + pseudoClass + (!arguments.isEmpty() ? "(" + arguments + ")" : "");
    }

    public String getPseudoClass() {
        return pseudoClass;
    }

    private static class AlwaysApplySelectorItem extends CssPseudoClassSelectorItem {
        AlwaysApplySelectorItem(String pseudoClass, String arguments) {
            super(pseudoClass, arguments);
        }

        @Override
        public boolean matches(INode node) {
            return true;
        }
    }

    private static class AlwaysNotApplySelectorItem extends CssPseudoClassSelectorItem {
        AlwaysNotApplySelectorItem(String pseudoClass, String arguments) {
            super(pseudoClass, arguments);
        }

        @Override
        public boolean matches(INode node) {
            return false;
        }
    }
}
