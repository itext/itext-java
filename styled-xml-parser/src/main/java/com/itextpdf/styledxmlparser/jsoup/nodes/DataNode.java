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

import java.io.IOException;

/**
 A data node, for contents of style, script tags etc, where contents should not show in text().
*/
public class DataNode extends LeafNode {

    /**
     Create a new DataNode.
     @param data data contents
     */
    public DataNode(String data) {
        value = data;
    }

    public String nodeName() {
        return "#data";
    }

    /**
     Get the data contents of this node. Will be unescaped and with original new lines, space etc.
     @return data
     */
    public String getWholeData() {
        return coreValue();
    }

    /**
     * Set the data contents of this node.
     * @param data unencoded data
     * @return this node, for chaining
     */
    public DataNode setWholeData(String data) {
        coreValue(data);
        return this;
    }

	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum.append(getWholeData()); // data is not escaped in return from data nodes, so " in script, style is plain
    }

	void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {}

    @Override
    public String toString() {
        return outerHtml();
    }

    @Override
    public Object clone() {
        return (DataNode) super.clone();
    }

    /**
     Create a new DataNode from HTML encoded data.
     @param encodedData encoded data
     @param baseUri base URI
     @return new DataNode
     @deprecated Unused, and will be removed in 1.15.1.
     */
    @Deprecated
    public static DataNode createFromEncoded(String encodedData, String baseUri) {
        String data = Entities.unescape(encodedData);
        return new DataNode(data);
    }
}
