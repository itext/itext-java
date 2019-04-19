package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

class CssPseudoClassDisabledSelectorItem extends CssPseudoClassSelectorItem {
    private static final CssPseudoClassDisabledSelectorItem instance = new CssPseudoClassDisabledSelectorItem();

    public static CssPseudoClassDisabledSelectorItem getInstance() {
        return instance;
    }

    private CssPseudoClassDisabledSelectorItem() {
        super(CommonCssConstants.DISABLED);
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        return null != ((IElementNode) node).getAttribute(CommonCssConstants.DISABLED);
    }
}
