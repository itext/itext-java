/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.kernel.pdf.PdfDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Stack;

public final class TrailerPath extends ObjectPath {
    private final PdfDocument outDocument;
    private final PdfDocument cmpDocument;

    private static final String INITIAL_LINE = "Base cmp object: trailer. Base out object: trailer";

    public TrailerPath(PdfDocument cmpDoc, PdfDocument outDoc) {
        super();
        outDocument = outDoc;
        cmpDocument = cmpDoc;
    }

    public TrailerPath(TrailerPath trailerPath) {
        super();
        outDocument = trailerPath.getOutDocument();
        cmpDocument = trailerPath.getCmpDocument();
        path = trailerPath.getLocalPath();
    }


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
     * Creates an xml node that describes this {@link TrailerPath} instance.
     *
     * @param document xml document, to which this xml node will be added.
     * @return an xml node describing this {@link TrailerPath} instance.
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
