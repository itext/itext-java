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


import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IAttributes;

import java.util.Iterator;

/**
 * Implementation of the {@link IAttributes} interface; wrapper for the JSoup {@link Attributes} class.
 */
public class JsoupAttributes implements IAttributes {

    /**
     * The JSoup {@link Attributes} instance.
     */
    private Attributes attributes;

    /**
     * Creates a new {@link JsoupAttributes} instance.
     *
     * @param attributes the attributes
     */
    public JsoupAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IAttributes#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) {
        return attributes.hasKey(key) ? attributes.get(key) : null;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IAttributes#setAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public void setAttribute(String key, String value) {
        if (attributes.hasKey(key)) {
            attributes.remove(key);
        }
        attributes.put(key, value);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IAttributes#size()
     */
    @Override
    public int size() {
        return attributes.size();
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<IAttribute> iterator() {
        return new AttributeIterator(attributes.iterator());
    }

    /**
     * Iterator to loop over {@link IAttribute} elements.
     */
    private static class AttributeIterator implements Iterator<IAttribute> {

        /**
         * The iterator.
         */
        private Iterator<Attribute> iterator;

        /**
         * Instantiates a new iterator.
         *
         * @param iterator the iterator
         */
        public AttributeIterator(Iterator<Attribute> iterator) {
            this.iterator = iterator;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public IAttribute next() {
            return new JsoupAttribute(iterator.next());
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
