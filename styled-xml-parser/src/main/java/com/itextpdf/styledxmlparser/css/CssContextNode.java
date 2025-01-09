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
package com.itextpdf.styledxmlparser.css;


import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.IStylesContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The CSS context node.
 */
public abstract class CssContextNode implements INode, IStylesContainer {
    
    /** The child nodes. */
    private List<INode> childNodes = new ArrayList<>();
    
    /** The parent node. */
    private INode parentNode;
    
    /** The styles. */
    private Map<String, String> styles;

    /**
     * Creates a new {@link CssContextNode} instance.
     *
     * @param parentNode the parent node
     */
    public CssContextNode(INode parentNode) {
        this.parentNode = parentNode;
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
        childNodes.add(node);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.INode#parentNode()
     */
    @Override
    public INode parentNode() {
        return parentNode;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IStylesContainer#setStyles(java.util.Map)
     */
    @Override
    public void setStyles(Map<String, String> stringStringMap) {
        this.styles = stringStringMap;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IStylesContainer#getStyles()
     */
    @Override
    public Map<String, String> getStyles() {
        return this.styles;
    }
}
