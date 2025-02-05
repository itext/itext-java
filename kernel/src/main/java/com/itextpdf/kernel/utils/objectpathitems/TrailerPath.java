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

import com.itextpdf.kernel.pdf.PdfDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Stack;

/**
 * Direct path item (see {@link ObjectPath}), which describes transition to the
 * trailer entry which value is now a currently comparing direct object.
 */
public final class TrailerPath extends ObjectPath {
    private final PdfDocument outDocument;
    private final PdfDocument cmpDocument;

    private static final String INITIAL_LINE = "Base cmp object: trailer. Base out object: trailer";

    /**
     * Creates new {@link TrailerPath} instance with corresponding base objects in two documents.
     *
     * @param cmpDoc base object in the cmp document
     * @param outDoc base object in the out document
     */
    public TrailerPath(PdfDocument cmpDoc, PdfDocument outDoc) {
        super();
        outDocument = outDoc;
        cmpDocument = cmpDoc;
    }

    /**
     * Creates new {@link TrailerPath} instance from another {@link TrailerPath} object, passed as argument.
     *
     * @param trailerPath {@link TrailerPath} to create new instance from
     */
    public TrailerPath(TrailerPath trailerPath) {
        super();
        outDocument = trailerPath.getOutDocument();
        cmpDocument = trailerPath.getCmpDocument();
        path = trailerPath.getLocalPath();
    }

    /**
     * Creates new {@link TrailerPath} instance with corresponding base objects in two documents.
     *
     * @param cmpDoc base object in the cmp document
     * @param outDoc base object in the out document
     * @param path   local path that denotes sequence of the path items
     *               from base object to the comparing direct object
     */
    public TrailerPath(PdfDocument cmpDoc, PdfDocument outDoc, Stack<LocalPathItem> path) {
        super();
        this.outDocument = outDoc;
        this.cmpDocument = cmpDoc;
        this.path = path;
    }

    /**
     * Method returns current out {@link PdfDocument} object.
     *
     * @return current out {@link PdfDocument} object.
     */
    public PdfDocument getOutDocument() {
        return outDocument;
    }

    /**
     * Method returns current cmp {@link PdfDocument} object.
     *
     * @return current cmp {@link PdfDocument} object.
     */
    public PdfDocument getCmpDocument() {
        return cmpDocument;
    }

    /**
     * Creates xml node that describes this {@link TrailerPath} instance.
     *
     * @param document xml document, to which this xml node will be added.
     * @return xml node describing this {@link TrailerPath} instance.
     */
    @Override
    public Node toXmlNode(Document document) {
        final Element element = document.createElement("path");
        final Element baseNode = document.createElement("base");
        baseNode.setAttribute("cmp", "trailer");
        baseNode.setAttribute("out", "trailer");
        element.appendChild(baseNode);
        for (final LocalPathItem pathItem : path) {
            element.appendChild(pathItem.toXmlNode(document));
        }
        return element;
    }

    /**
     * Method returns a string representation of this {@link TrailerPath} instance.
     *
     * @return a string representation of this {@link TrailerPath} instance.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(INITIAL_LINE.length());
        sb.append(INITIAL_LINE);
        for (final LocalPathItem pathItem : path) {
            sb.append('\n');
            sb.append(pathItem.toString());
        }
        return sb.toString();
    }

    /**
     * Method returns a hash code of this {@link TrailerPath} instance.
     *
     * @return a int hash code of this {@link TrailerPath} instance.
     */
    @Override
    public int hashCode() {
        int hashCode = outDocument.hashCode() * 31 + cmpDocument.hashCode();
        for (final LocalPathItem pathItem : path) {
            hashCode *= 31;
            hashCode += pathItem.hashCode();
        }
        return hashCode;
    }

    /**
     * Method returns true if this {@link TrailerPath} instance equals to the passed object.
     *
     * @return true - if this {@link TrailerPath} instance equals to the passed object.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj.getClass() == getClass()
                && outDocument.equals(((TrailerPath) obj).outDocument)
                && cmpDocument.equals(((TrailerPath) obj).cmpDocument)
                && path.equals(((ObjectPath) obj).path);
    }
}
