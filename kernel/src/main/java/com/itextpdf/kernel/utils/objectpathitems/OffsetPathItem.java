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

import com.itextpdf.kernel.pdf.PdfStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Direct path item (see {@link ObjectPath}, which describes transition to the
 * specific position in {@link PdfStream}.
 */
public final class OffsetPathItem extends LocalPathItem {
    private final int offset;

    /**
     * Creates an instance of the {@link OffsetPathItem}.
     *
     * @param offset bytes offset to the specific position in {@link PdfStream}.
     */
    public OffsetPathItem(int offset) {
        super();
        this.offset = offset;
    }

    /**
     * The bytes offset of the stream which defines specific position in the {@link PdfStream}, to which transition
     * was performed.
     *
     * @return an integer defining bytes offset to the specific position in stream.
     */
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "Offset: " + offset;
    }

    @Override
    public int hashCode() {
        return offset;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && offset == ((OffsetPathItem) obj).offset;
    }

    @Override
    protected Node toXmlNode(Document document) {
        final Element element = document.createElement("offset");
        element.appendChild(document.createTextNode(String.valueOf(offset)));
        return element;
    }
}
