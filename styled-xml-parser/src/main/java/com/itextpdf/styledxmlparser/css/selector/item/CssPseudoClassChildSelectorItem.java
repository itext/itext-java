package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CssPseudoClassChildSelectorItem extends CssPseudoClassSelectorItem {

    /**
     * Creates a new {@link CssPseudoClassSelectorItem} instance.
     *
     * @param pseudoClass the pseudo class name
     */
    CssPseudoClassChildSelectorItem(String pseudoClass) {
        super(pseudoClass);
    }

    CssPseudoClassChildSelectorItem(String pseudoClass, String arguments) {
        super(pseudoClass, arguments);
    }

    /**
     * Gets the all the siblings of a child node.
     *
     * @param node the child node
     * @return the sibling nodes
     */
    List<INode> getAllSiblings(INode node) {
        INode parentElement = node.parentNode();
        if (parentElement != null) {
            List<INode> childrenUnmodifiable = parentElement.childNodes();
            List<INode> children = new ArrayList<INode>(childrenUnmodifiable.size());
            for (INode iNode : childrenUnmodifiable) {
                if (iNode instanceof IElementNode)
                    children.add(iNode);
            }
            return children;
        }
        return Collections.<INode>emptyList();
    }

    /**
     * Gets all siblings of a child node with the type of a child node.
     *
     * @param node the child node
     * @return the sibling nodes with the type of a child node
     */
    List<INode> getAllSiblingsOfNodeType(INode node) {
        INode parentElement = node.parentNode();
        if (parentElement != null) {
            List<INode> childrenUnmodifiable = parentElement.childNodes();
            List<INode> children = new ArrayList<INode>(childrenUnmodifiable.size());
            for (INode iNode : childrenUnmodifiable) {
                if (iNode instanceof IElementNode && ((IElementNode) iNode).name().equals(((IElementNode) node).name()))
                    children.add(iNode);
            }
            return children;
        }
        return Collections.<INode>emptyList();
    }
}
