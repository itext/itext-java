/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CssStyleAttributeParseTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/styledxmlparser/css/parse/CssStyleAttributeParseTest/";

    @Test
    public void styleAttributeParseTest() throws IOException {
        String fileName = SOURCE_FOLDER + "cssStyleAttributeParse.html";

        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode document = parser.parse(FileUtil.getInputStreamForFile(fileName), "UTF-8");

        List<String> styleDeclarations = new ArrayList<>();

        List<String> expectStyleDeclarations = new ArrayList<>();
        expectStyleDeclarations.add("display:none;");
        expectStyleDeclarations.add("position:relative;");
        expectStyleDeclarations.add("display:none");
        expectStyleDeclarations.add("text-align:center;");
        expectStyleDeclarations.add("white-space:nowrap;");
        expectStyleDeclarations
                .add("float:right; clear:right; width:22.0em; margin:0 0 1.0em 1.0em; background:#f9f9f9;"
                        + " border:1px solid #aaa; padding:0.2em; border-spacing:0.4em 0; text-align:center;"
                        + " line-height:1.4em; font-size:88%;");
        expectStyleDeclarations.add("padding:0.2em 0.4em 0.2em; font-size:145%; line-height:1.15em; font-weight:bold;"
                + " display:block; margin-bottom:0.25em;");

        parseStyleAttrForSubtree(document, styleDeclarations);

        Assertions.assertEquals(styleDeclarations.size(), expectStyleDeclarations.size());

        for (int i = 0; i < expectStyleDeclarations.size(); i++) {
            Assertions.assertEquals(expectStyleDeclarations.get(i), styleDeclarations.get(i));
        }
    }

    private void parseOwnStyleAttr(IElementNode element, List<String> styleDeclarations) {
        IAttributes attributes = element.getAttributes();
        String styleAttr = attributes.getAttribute("style");

        if (styleAttr != null && styleAttr.length() > 0) {
            styleDeclarations.add(styleAttr);
        }
    }

    private void parseStyleAttrForSubtree(INode node, List<String> styleDeclarations) {
        if (node instanceof IElementNode) {
            parseOwnStyleAttr((IElementNode) node, styleDeclarations);
        }
        for (INode child : node.childNodes()) {
            parseStyleAttrForSubtree(child, styleDeclarations);
        }
    }
}

