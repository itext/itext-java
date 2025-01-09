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


import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.node.IDocumentNode;

/**
 * Implementation of the {@link IDocumentNode} interface; wrapper for the JSoup {@link Document} class.
 */
public class JsoupDocumentNode extends JsoupElementNode implements IDocumentNode {

    /** The JSoup document instance. */
    private Document document;

    /**
     * Creates a new {@link JsoupDocumentNode} instance.
     *
     * @param document the document
     */
    public JsoupDocumentNode(Document document) {
        super(document);
        this.document = document;
    }

    /**
     * Gets the JSoup document.
     *
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

}
