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
