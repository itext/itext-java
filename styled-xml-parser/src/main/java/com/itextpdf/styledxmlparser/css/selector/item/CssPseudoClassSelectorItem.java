/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.styledxmlparser.css.parse.CssSelectorParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * Creates a new {@link CssPseudoClassSelectorItem} instance.
     *
     * @param pseudoClass the pseudo class name
     * @param arguments the arguments of the selector
     */
    protected CssPseudoClassSelectorItem(String pseudoClass, String arguments) {
        this.pseudoClass = pseudoClass;
        this.arguments = arguments;
    }

    /**
     * Creates a new instance of {@link CssPseudoClassSelectorItem} from
     * passed string which contains selector name and its arguments.
     *
     * @param fullSelectorString the full selector string
     *
     * @return the {@link CssPseudoClassSelectorItem} or {@code null}
     */
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

    /**
     * Creates a new instance of {@link CssPseudoClassSelectorItem} from
     * passed selector class name and its arguments.
     *
     * @param pseudoClass the pseudo class name
     * @param arguments the arguments of the selector
     *
     * @return the {@link CssPseudoClassSelectorItem} or {@code null}
     */
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
            case CommonCssConstants.HAS:
                return createHasSelectorItem(arguments);
            case CommonCssConstants.NOT:
                return CssPseudoClassNotSelectorItem.createNotSelectorItem(arguments);
            case CommonCssConstants.IS:
                return CssPseudoClassIsSelectorItem.createIsSelectorItem(arguments);
            case CommonCssConstants.WHERE:
                return CssPseudoClassWhereSelectorItem.createWhereSelectorItem(arguments);
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

    /**
     * Parses a selector list. Whether parsing is supposed to be forgiving can be configured.
     *
     * @param arguments selector list as written inside parentheses
     * @param forgiving {@code true} if parsing is supposed to be forgiving, {@code false} otherwise
     *
     * @return list of valid selectors (possibly empty), or null if arguments are syntactically incorrect
     */
    static List<ICssSelector> parseSelectorListWithoutPseudoElements(String arguments, boolean forgiving) {
        if (arguments == null || arguments.trim().isEmpty()) {
            // selector list with empty argument is invalid.
            return null;
        }

        List<String> parts = CssSelectorParser.splitByTopLevelComma(arguments);
        if (parts.isEmpty()) {
            return null;
        }

        List<ICssSelector> selectors = new ArrayList<>();
        for (String rawPart : parts) {
            String part = rawPart == null ? "" : rawPart.trim();
            if (part.isEmpty()) {
                // Empty entries like :is(.a,,.b) are invalid selectors in the list.
                if (forgiving) {
                    continue;
                } else {
                    return null;
                }
            }

            try {
                CssSelector sel = new CssSelector(CssSelectorParser.parseSelectorItems(part, false));
                if (containsPseudoElement(Collections.<ICssSelector>singletonList(sel))) {
                    if (!forgiving) {
                        return null;
                    }
                } else {
                    selectors.add(sel);
                }
            } catch (IllegalArgumentException ex) {
                // Invalid/unsupported selector in the list.
                if (!forgiving) {
                    return null;
                }
            }
        }

        return selectors;
    }

    static boolean containsPseudoElement(List<ICssSelector> selectors) {
        for (ICssSelector sel : selectors) {
            if (sel instanceof CssSelector) {
                for (ICssSelectorItem item : ((CssSelector) sel).getSelectorItems()) {
                    if (item instanceof CssPseudoElementSelectorItem) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static CssPseudoClassHasSelectorItem createHasSelectorItem(String arguments) {
        List<ICssSelector> hasSelectors = CssSelectorParser.parseCommaSeparatedSelectors(arguments);
        for (ICssSelector hasSelector : hasSelectors) {
            if (hasSelector instanceof CssSelector) {
                for (ICssSelectorItem item : ((CssSelector) hasSelector).getSelectorItems()) {
                    // Pseudo-elements are restricted as they don't make sense in :has() context.
                    if (item instanceof CssPseudoElementSelectorItem || item instanceof CssPseudoClassHasSelectorItem) {
                        return null;
                    }
                }
            }
        }
        return new CssPseudoClassHasSelectorItem(hasSelectors, arguments);
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

    /**
     * Gets the selector pseudo class name.
     *
     * @return the pseudo class name
     */
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
