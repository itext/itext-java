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

import com.itextpdf.styledxmlparser.jsoup.parser.ParseSettings;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;

import java.io.IOException;

/**
 A comment node.
*/
public class Comment extends LeafNode {
    /**
     Create a new comment node.
     @param data The contents of the comment
     */
    public Comment(String data) {
        value = data;
    }

    public String nodeName() {
        return "#comment";
    }

    /**
     Get the contents of the comment.
     @return comment content
     */
    public String getData() {
        return coreValue();
    }

    public Comment setData(String data) {
        coreValue(data);
        return this;
    }

	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (out.prettyPrint() && ((siblingIndex() == 0 && parentNode instanceof Element && ((Element) parentNode).tag().formatAsBlock()) || (out.outline() )))
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

    @Override
    public Object clone() {
        return (Comment) super.clone();
    }

    /**
     * Check if this comment looks like an XML Declaration.
     * @return true if it looks like, maybe, it's an XML Declaration.
     */
    public boolean isXmlDeclaration() {
        String data = getData();
        return isXmlDeclarationData(data);
    }

    private static boolean isXmlDeclarationData(String data) {
        return (data.length() > 1 && (data.startsWith("!") || data.startsWith("?")));
    }

    /**
     * Attempt to cast this comment to an XML Declaration node.
     * @return an XML declaration if it could be parsed as one, null otherwise.
     */
    public XmlDeclaration asXmlDeclaration() {
        String data = getData();

        XmlDeclaration decl = null;
        String declContent = data.substring(1, data.length() - 1);
        // make sure this bogus comment is not immediately followed by another, treat as comment if so
        if (isXmlDeclarationData(declContent))
            return null;

        String fragment = "<" + declContent + ">";
        // use the HTML parser not XML, so we don't get into a recursive XML Declaration on contrived data
        Document doc = Parser.htmlParser().settings(ParseSettings.preserveCase).parseInput(fragment, baseUri());
        if (doc.body().children().size() > 0) {
            Element el = doc.body().child(0);
            decl = new XmlDeclaration(NodeUtils.parser(doc).settings().normalizeTag(el.tagName()), data.startsWith("!"));
            decl.attributes().addAll(el.attributes());
        }
        return decl;
    }
}
