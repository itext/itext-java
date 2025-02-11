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
import com.itextpdf.styledxmlparser.node.IAttribute;

/**
 * Implementation of the {@link IAttribute} interface; wrapper for the JSoup {@link Attribute} class.
 */
public class JsoupAttribute implements IAttribute {

    /**
     * The JSoup {@link Attribute} instance.
     */
    private Attribute attribute;

    /**
     * Creates a new {@link JsoupAttribute} instance.
     *
     * @param attribute the attribute
     */
    public JsoupAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IAttribute#getKey()
     */
    @Override
    public String getKey() {
        return attribute.getKey();
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IAttribute#getValue()
     */
    @Override
    public String getValue() {
        return attribute.getValue();
    }
}
