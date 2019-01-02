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
package com.itextpdf.styledxmlparser.css.pseudo;



import com.itextpdf.styledxmlparser.css.CssContextNode;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link IElementNode} implementation for pseudo elements.
 */
public class CssPseudoElementNode extends CssContextNode implements IElementNode, ICustomElementNode {
    
    /** The pseudo element name. */
    private String pseudoElementName;
    
    /** The pseudo element tag name. */
    private String pseudoElementTagName;

    /**
     * Creates a new {@link CssPseudoElementNode} instance.
     *
     * @param parentNode the parent node
     * @param pseudoElementName the pseudo element name
     */
    public CssPseudoElementNode(INode parentNode, String pseudoElementName) {
        super(parentNode);
        this.pseudoElementName = pseudoElementName;
        this.pseudoElementTagName = CssPseudoElementUtil.createPseudoElementTagName(pseudoElementName);
    }

    /**
     * Gets the pseudo element name.
     *
     * @return the pseudo element name
     */
    public String getPseudoElementName() {
        return pseudoElementName;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#name()
     */
    @Override
    public String name() {
        return pseudoElementTagName;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getAttributes()
     */
    @Override
    public IAttributes getAttributes() {
        return new AttributesStub();
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getAdditionalHtmlStyles()
     */
    @Override
    public List<Map<String, String>> getAdditionalHtmlStyles() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#addAdditionalHtmlStyles(java.util.Map)
     */
    @Override
    public void addAdditionalHtmlStyles(Map<String, String> styles) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getLang()
     */
    @Override
    public String getLang() {
        return null;
    }

    /**
     * A simple {@link IAttributes} implementation.
     */
    private static class AttributesStub implements IAttributes {
        
        /* (non-Javadoc)
         * @see com.itextpdf.styledxmlparser.html.node.IAttributes#getAttribute(java.lang.String)
         */
        @Override
        public String getAttribute(String key) {
            return null;
        }

        /* (non-Javadoc)
         * @see com.itextpdf.styledxmlparser.html.node.IAttributes#setAttribute(java.lang.String, java.lang.String)
         */
        @Override
        public void setAttribute(String key, String value) {
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see com.itextpdf.styledxmlparser.html.node.IAttributes#size()
         */
        @Override
        public int size() {
            return 0;
        }

        /* (non-Javadoc)
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<IAttribute> iterator() {
            return Collections.<IAttribute>emptyIterator();
        }
    }
}

