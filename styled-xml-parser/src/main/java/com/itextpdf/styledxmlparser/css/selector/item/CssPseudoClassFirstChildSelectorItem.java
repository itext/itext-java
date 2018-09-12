package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import java.util.List;

class CssPseudoClassFirstChildSelectorItem extends CssPseudoClassChildSelectorItem {
    private static final CssPseudoClassFirstChildSelectorItem instance = new CssPseudoClassFirstChildSelectorItem();

    private CssPseudoClassFirstChildSelectorItem() {
        super(CommonCssConstants.FIRST_CHILD);
    }

    public static CssPseudoClassFirstChildSelectorItem getInstance() {
        return instance;
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        List<INode> children = getAllSiblings(node);
        return !children.isEmpty() && node.equals(children.get(0));
    }
}
