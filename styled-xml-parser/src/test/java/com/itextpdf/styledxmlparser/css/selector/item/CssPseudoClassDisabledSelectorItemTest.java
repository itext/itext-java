package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Category(UnitTest.class)
public class CssPseudoClassDisabledSelectorItemTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/selector/item/CssPseudoClassDisabledSelectorItemTest/";

    @Test
    public void testDisabledSelector() throws IOException {
        String filename = sourceFolder + "disabled.html";

        CssPseudoClassDisabledSelectorItem item = CssPseudoClassDisabledSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse(new FileInputStream(filename), "UTF-8");
        IElementNode disabledInput = new JsoupElementNode(((JsoupDocumentNode)documentNode).getDocument().getElementsByTag("input").first());
        IElementNode enabledInput = new JsoupElementNode(((JsoupDocumentNode)documentNode).getDocument().getElementsByTag("input").get(1));

        Assert.assertFalse(item.matches(documentNode));
        Assert.assertTrue(item.matches(disabledInput));
        Assert.assertFalse(item.matches(enabledInput));
        Assert.assertFalse(item.matches(null));
    }
}
