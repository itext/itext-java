package com.itextpdf.styledxmlparser.css.pseudo;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssPseudoElementUtilTest extends ExtendedITextTest {

    @Test
    public void createPseudoElementTagNameTest() {
        String beforePseudoElemName = CssPseudoElementUtil.createPseudoElementTagName("before");
        String expected = "pseudo-element::before";

        Assert.assertEquals(expected, beforePseudoElemName);
    }

    @Test
    public void hasBeforeAfterElementsNullScenarioTest() {
        Assert.assertFalse(CssPseudoElementUtil.hasBeforeAfterElements(null));
    }

    @Test
    public void hasBeforeAfterElementsInstanceOfTest() {
        Assert.assertFalse(CssPseudoElementUtil
                .hasBeforeAfterElements(new CssPseudoElementNode(null, "")));
    }

    @Test
    public void hasBeforeAfterElementsNodeNameTest() {
        Element element = new Element(Tag.valueOf("pseudo-element::"), "");
        IElementNode node = new JsoupElementNode(element);

        Assert.assertFalse(CssPseudoElementUtil.hasBeforeAfterElements(node));
    }

    @Test
    public void hasAfterElementTest() {
        Element element = new Element(Tag.valueOf("after"), "");
        IElementNode node = new JsoupElementNode(element);

        Assert.assertTrue(CssPseudoElementUtil.hasBeforeAfterElements(node));
    }

    @Test
    public void hasBeforeElementTest() {
        Element element = new Element(Tag.valueOf("before"), "");
        IElementNode node = new JsoupElementNode(element);

        Assert.assertTrue(CssPseudoElementUtil.hasBeforeAfterElements(node));
    }
}
