package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.parse.CssSelectorParser;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.List;

class CssPseudoClassNotSelectorItem extends CssPseudoClassSelectorItem {
    private ICssSelector argumentsSelector;

    CssPseudoClassNotSelectorItem(ICssSelector argumentsSelector) {
        super(CommonCssConstants.NOT, argumentsSelector.toString());
        this.argumentsSelector = argumentsSelector;
    }

    public List<ICssSelectorItem> getArgumentsSelector() {
        return CssSelectorParser.parseSelectorItems(arguments);
    }

    @Override
    public boolean matches(INode node) {
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
        return !argumentsSelector.matches(node);
    }
}
