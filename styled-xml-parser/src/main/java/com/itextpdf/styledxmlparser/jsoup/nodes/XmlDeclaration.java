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

import com.itextpdf.styledxmlparser.jsoup.helper.Validate;

import java.io.IOException;

/**
 An XML Declaration.

 @author Jonathan Hedley, jonathan@hedley.net */
public class XmlDeclaration extends Node {
    private final String name;
    private final boolean isProcessingInstruction; // <! if true, <? if false, declaration (and last data char should be ?)

    /**
     Create a new XML declaration
     @param name of declaration
     @param baseUri base uri
     @param isProcessingInstruction is processing instruction
     */
    public XmlDeclaration(String name, String baseUri, boolean isProcessingInstruction) {
        super(baseUri);
        Validate.notNull(name);
        this.name = name;
        this.isProcessingInstruction = isProcessingInstruction;
    }

    public String nodeName() {
        return "#declaration";
    }


    /**
     * Get the name of this declaration.
     * @return name of this declaration.
     */
    public String name() {
        return name;
    }

    /**
     Get the unencoded XML declaration.
     @return XML declaration
     */
    public String getWholeDeclaration() {
        return attributes.html().trim(); // attr html starts with a " "
    }

	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<")
            .append(isProcessingInstruction ? "!" : "?")
            .append(name);
        attributes.html(accum, out);
        accum
            .append(isProcessingInstruction ? "!" : "?")
            .append(">");
    }

	void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {}

    @Override
    public String toString() {
        return outerHtml();
    }
}
