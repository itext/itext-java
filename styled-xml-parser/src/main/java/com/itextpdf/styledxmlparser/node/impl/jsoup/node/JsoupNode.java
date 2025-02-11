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
package com.itextpdf.styledxmlparser.node.impl.jsoup.node;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.node.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the {@link INode} interface; wrapper for the JSoup {@link Node} class.
 */
public class JsoupNode implements INode {

    /** The JSoup node instance. */
    private Node node;

    /** The child nodes. */
    private List<INode> childNodes = new ArrayList<>();
    
    /** The parent node. */
    INode parentNode;

    /**
     * Creates a new {@link JsoupNode} instance.
     *
     * @param node the node
     */
    public JsoupNode(Node node) {
        this.node = node;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.INode#childNodes()
     */
    @Override
    public List<INode> childNodes() {
        return Collections.unmodifiableList(childNodes);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.INode#addChild(com.itextpdf.styledxmlparser.html.node.INode)
     */
    @Override
    public void addChild(INode node) {
        if (node instanceof JsoupNode) {
            childNodes.add(node);
            ((JsoupNode) node).parentNode = this;
        } else {
            Logger logger = LoggerFactory.getLogger(JsoupNode.class);
            logger.error(StyledXmlParserLogMessageConstant.ERROR_ADDING_CHILD_NODE);
        }
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.INode#parentNode()
     */
    @Override
    public INode parentNode() {
        return parentNode;
    }
}
