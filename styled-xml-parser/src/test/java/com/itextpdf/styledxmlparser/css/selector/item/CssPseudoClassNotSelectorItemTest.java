package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class CssPseudoClassNotSelectorItemTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/styledxmlparser/css/selector/item/CssPseudoClassNotSelectorItemTest/";

    @Test
    public void cssPseudoClassNotSelectorItemWithSelectorsListTest() throws IOException {
        String filename = SOURCE_FOLDER + "cssPseudoClassNotSelectorItemTest.html";

        CssPseudoClassNotSelectorItem item = new CssPseudoClassNotSelectorItem(new CssSelector(
                "p > :not(strong, b.important)"));
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse(FileUtil.getInputStreamForFile(filename), "UTF-8");
        IElementNode body = new JsoupElementNode(((JsoupDocumentNode)documentNode).getDocument().getElementsByTag("body").get(0));

        Assertions.assertFalse(item.matches(documentNode));
        Assertions.assertTrue(item.matches(body));
        Assertions.assertFalse(item.matches(null));
    }
}
