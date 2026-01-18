package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;

import java.util.List;

class CssPseudoClassIsSelectorItem extends CssPseudoClassForgivingSelectorListSelectorItem {

    CssPseudoClassIsSelectorItem(List<ICssSelector> selectorList, String argumentsString) {
        super(CommonCssConstants.IS, selectorList, argumentsString);
    }

    @Override
    public int getSpecificity() {
        int max = 0;
        for (ICssSelector sel : selectorList) {
            if (sel != null) {
                max = Math.max(max, sel.calculateSpecificity());
            }
        }
        return max;
    }

    public static CssPseudoClassIsSelectorItem createIsSelectorItem(String arguments) {
        List<ICssSelector> selectors = parseForgivingSelectorListWithoutPseudoElements(arguments);
        if (selectors == null) {
            return null;
        }
        return new CssPseudoClassIsSelectorItem(selectors, arguments);
    }
}

