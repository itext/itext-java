/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
 A comment node.

 @author Jonathan Hedley, jonathan@hedley.net */
public class Comment extends Node {
    private static final String COMMENT_KEY = "comment";

    /**
     Create a new comment node.
     @param data The contents of the comment
     @param baseUri base URI
     */
    public Comment(String data, String baseUri) {
        super(baseUri);
        attributes.put(COMMENT_KEY, data);
    }

    public String nodeName() {
        return "#comment";
    }

    /**
     Get the contents of the comment.
     @return comment content
     */
    public String getData() {
        return attributes.get(COMMENT_KEY);
    }

	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (out.prettyPrint())
            indent(accum, depth, out);
        accum
                .append("<!--")
                .append(getData())
                .append("-->");
    }

	void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {}

    @Override
    public String toString() {
        return outerHtml();
    }
}
