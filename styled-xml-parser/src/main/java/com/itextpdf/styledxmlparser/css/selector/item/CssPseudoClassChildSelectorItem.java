/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
