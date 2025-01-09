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

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return pseudoElementTagName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAttributes getAttributes() {
        return new AttributesStub();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttribute(String key) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, String>> getAdditionalHtmlStyles() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAdditionalHtmlStyles(Map<String, String> styles) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLang() {
        return null;
    }

    /**
     * A simple {@link IAttributes} implementation.
     */
    private static class AttributesStub implements IAttributes {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAttribute(String key) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setAttribute(String key, String value) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<IAttribute> iterator() {
            return Collections.<IAttribute>emptyIterator();
        }
    }
}

