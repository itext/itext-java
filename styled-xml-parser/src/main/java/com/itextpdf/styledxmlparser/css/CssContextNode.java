/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.
    
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS
    
    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/
    
    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.
    
    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.
    
    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.
    
    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
