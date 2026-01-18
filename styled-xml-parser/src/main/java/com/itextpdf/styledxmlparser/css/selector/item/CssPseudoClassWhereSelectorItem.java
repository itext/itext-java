package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;

import java.util.List;

class CssPseudoClassWhereSelectorItem extends CssPseudoClassForgivingSelectorListSelectorItem {

    CssPseudoClassWhereSelectorItem(List<ICssSelector> selectorList, String argumentsString) {
        super(CommonCssConstants.WHERE, selectorList, argumentsString);
    }

    @Override
    public int getSpecificity() {
        // Per Selectors Level 4: :where() always contributes 0 specificity.
        return 0;
    }

    public static CssPseudoClassWhereSelectorItem createWhereSelectorItem(String arguments) {
        List<ICssSelector> selectors = parseForgivingSelectorListWithoutPseudoElements(arguments);
        if (selectors == null) {
            return null;
        }
        return new CssPseudoClassWhereSelectorItem(selectors, arguments);
    }
}

