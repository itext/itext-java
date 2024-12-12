/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
            case CommonCssConstants.NTH_LAST_CHILD:
                return new CssPseudoClassNthLastChildSelectorItem(arguments);
            case CommonCssConstants.NTH_OF_TYPE:
                return new CssPseudoClassNthOfTypeSelectorItem(arguments);
            case CommonCssConstants.NTH_LAST_OF_TYPE:
                return new CssPseudoClassNthLastOfTypeSelectorItem(arguments);
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
            case CommonCssConstants.DISABLED:
                return CssPseudoClassDisabledSelectorItem.getInstance();
            //Still unsupported, should be addressed in DEVSIX-1440
            //case CommonCssConstants.CHECKED:
            //case CommonCssConstants.ENABLED:
            //case CommonCssConstants.IN_RANGE:
            //case CommonCssConstants.INVALID:
            //case CommonCssConstants.LANG:
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
