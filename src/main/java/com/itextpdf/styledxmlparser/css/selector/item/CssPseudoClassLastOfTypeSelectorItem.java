package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import java.util.List;

class CssPseudoClassLastOfTypeSelectorItem extends CssPseudoClassChildSelectorItem {
    private static final CssPseudoClassLastOfTypeSelectorItem instance = new CssPseudoClassLastOfTypeSelectorItem();

    private CssPseudoClassLastOfTypeSelectorItem() {
        super(CommonCssConstants.LAST_OF_TYPE);
    }

    public static CssPseudoClassLastOfTypeSelectorItem getInstance() {
        return instance;
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        List<INode> children = getAllSiblingsOfNodeType(node);
        return !children.isEmpty() && node.equals(children.get(children.size() - 1));
    }
}
