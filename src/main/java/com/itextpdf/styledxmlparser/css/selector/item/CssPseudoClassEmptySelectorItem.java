package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.ITextNode;

class CssPseudoClassEmptySelectorItem extends CssPseudoClassSelectorItem {
    private static final CssPseudoClassEmptySelectorItem instance = new CssPseudoClassEmptySelectorItem();

    private CssPseudoClassEmptySelectorItem() {
        super(CommonCssConstants.EMPTY);
    }

    public static CssPseudoClassEmptySelectorItem getInstance() {
        return instance;
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        if (node.childNodes().isEmpty()) {
            return true;
        }
        for (INode childNode : node.childNodes()) {
            if (!(childNode instanceof ITextNode) || !((ITextNode) childNode).wholeText().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
