package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import java.util.List;

class CssPseudoClassFirstOfTypeSelectorItem extends CssPseudoClassChildSelectorItem {
    private static final CssPseudoClassFirstOfTypeSelectorItem instance = new CssPseudoClassFirstOfTypeSelectorItem();

    private CssPseudoClassFirstOfTypeSelectorItem() {
        super(CommonCssConstants.FIRST_OF_TYPE);
    }

    public static CssPseudoClassFirstOfTypeSelectorItem getInstance() {
        return instance;
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        List<INode> children = getAllSiblingsOfNodeType(node);
        return !children.isEmpty() && node.equals(children.get(0));
    }
}
