package com.itextpdf.styledxmlparser.css.selector.item;


import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

class CssPseudoClassRootSelectorItem extends CssPseudoClassSelectorItem {
    private static final CssPseudoClassRootSelectorItem instance = new CssPseudoClassRootSelectorItem();

    private CssPseudoClassRootSelectorItem() {
        super(CommonCssConstants.ROOT);
    }

    public static CssPseudoClassRootSelectorItem getInstance() {
        return instance;
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        return node.parentNode() instanceof IDocumentNode;
    }
}
