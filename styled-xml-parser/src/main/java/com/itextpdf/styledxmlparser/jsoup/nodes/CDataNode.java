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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.UncheckedIOException;

import java.io.IOException;

/**
 * A Character Data node, to support CDATA sections.
 */
public class CDataNode extends TextNode {
    public CDataNode(String text) {
        super(text);
    }

    @Override
    public String nodeName() {
        return "#cdata";
    }

    /**
     * Get the unencoded, <b>non-normalized</b> text content of this CDataNode.
     * @return unencoded, non-normalized text
     */
    @Override
    public String text() {
        return getWholeText();
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<![CDATA[")
            .append(getWholeText());
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
        try {
            accum.append("]]>");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Object clone() {
        return (CDataNode) super.clone();
    }
}
