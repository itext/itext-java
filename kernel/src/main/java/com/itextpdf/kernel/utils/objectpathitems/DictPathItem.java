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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Direct path item (see {@link ObjectPath}, which describes transition to the
 * {@link PdfDictionary} entry which value is now a currently comparing direct object.
 */
public final class DictPathItem extends LocalPathItem {
    private final PdfName key;

    /**
     * Creates an instance of the {@link DictPathItem}.
     *
     * @param key the key which defines to which entry of the {@link PdfDictionary}
     *            the transition was performed.
     */
    public DictPathItem(PdfName key) {
        super();
        this.key = key;
    }

    @Override
    public String toString() {
        return "Dict key: " + key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && key.equals(((DictPathItem) obj).key);
    }

    /**
     * The key which defines to which entry of the {@link PdfDictionary} the transition was performed.
     * See {@link DictPathItem} for more info.
     *
     * @return a {@link PdfName} which is the key which defines
     * to which entry of the dictionary the transition was performed.
     */
    public PdfName getKey() {
        return key;
    }

    @Override
    protected Node toXmlNode(Document document) {
        final Element element = document.createElement("dictKey");
        element.appendChild(document.createTextNode(key.toString()));
        return element;
    }
}
