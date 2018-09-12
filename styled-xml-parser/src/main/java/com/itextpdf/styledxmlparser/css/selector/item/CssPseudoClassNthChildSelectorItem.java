package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;

class CssPseudoClassNthChildSelectorItem extends CssPseudoClassNthSelectorItem {

    CssPseudoClassNthChildSelectorItem(String arguments) {
        super(CommonCssConstants.NTH_CHILD, arguments);
    }
}
