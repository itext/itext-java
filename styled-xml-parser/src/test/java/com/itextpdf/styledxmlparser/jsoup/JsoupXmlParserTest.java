/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.styledxmlparser.node.IXmlDeclarationNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class JsoupXmlParserTest extends ExtendedITextTest {
    @Test
    public void testXmlDeclarationAndComment() throws IOException {
        String xml = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                "<!-- just declaration and comment -->";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        IDocumentNode node = new JsoupXmlParser().parse(stream, "UTF-8");
        Assertions.assertEquals(2, node.childNodes().size());
        Assertions.assertTrue(node.childNodes().get(0) instanceof IXmlDeclarationNode);
        Assertions.assertTrue(node.childNodes().get(1) instanceof ITextNode);
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
