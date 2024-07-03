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
package com.itextpdf.styledxmlparser.jsoup;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@org.junit.jupiter.api.Tag("UnitTest")
public class JsoupXmlParserTest extends ExtendedITextTest {
    @Test
    public void testXmlDeclarationAndComment() throws IOException {
        String xml = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                "<!-- just declaration and comment -->";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        IDocumentNode node = new JsoupXmlParser().parse(stream, "UTF-8");
        // only text (whitespace) child node shall be fetched.
        Assertions.assertEquals(1, node.childNodes().size());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.ERROR_ADDING_CHILD_NODE),
    })
    public void testMessageAddingChild() {
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(null);
        Assertions.assertEquals(0, root.childNodes().size());
    }
}
