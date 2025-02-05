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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.kernel.pdf.PdfArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Direct path item (see {@link ObjectPath}, which describes transition to the
 * {@link PdfArray} element which is now a currently comparing direct object.
 */
public final class ArrayPathItem extends LocalPathItem {
    private final int index;

    /**
     * Creates an instance of the {@link ArrayPathItem}.
     *
     * @param index the index which defines element of the {@link PdfArray} to which
     *              the transition was performed.
     */
    public ArrayPathItem(int index) {
        super();
        this.index = index;
    }

    @Override
    public String toString() {
        return "Array index: " + index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && index == ((ArrayPathItem) obj).index;
    }

    /**
     * The index which defines element of the {@link PdfArray} to which the transition was performed.
     * See {@link ArrayPathItem} for more info.
     *
     * @return the index which defines element of the array to which the transition was performed
     */
    public int getIndex() {
        return index;
    }

    @Override
    protected Node toXmlNode(Document document) {
        final Element element = document.createElement("arrayIndex");
        element.appendChild(document.createTextNode(String.valueOf(index)));
        return element;
    }
}
