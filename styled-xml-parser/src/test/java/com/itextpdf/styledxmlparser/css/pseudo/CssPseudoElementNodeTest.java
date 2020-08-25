package com.itextpdf.styledxmlparser.css.pseudo;

import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CssPseudoElementNodeTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void getPseudoElementNameTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertEquals("after", pseudoElementNode.getPseudoElementName());
    }

    @Test
    public void getPseudoElementTagNameTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertEquals("pseudo-element::after", pseudoElementNode.name());
    }

    @Test
    public void getAttributeStringTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertNull(pseudoElementNode.getAttribute("after"));
    }

    @Test
    public void getAttributesTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertTrue(pseudoElementNode.getAttributes() instanceof IAttributes);
        Assert.assertFalse(pseudoElementNode.getAttributes() == pseudoElementNode.getAttributes());
    }

    @Test
    public void getAdditionalHtmlStylesTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertNull(pseudoElementNode.getAdditionalHtmlStyles());
    }

    @Test
    public void addAdditionalHtmlStylesTest() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Map<String, String> styles = new HashMap<>();
        styles.put("font-size", "12px");
        styles.put("color", "red");
        pseudoElementNode.addAdditionalHtmlStyles(styles);

        Assert.fail();
    }

    @Test
    public void getLangTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertNull(pseudoElementNode.getLang());
    }

    @Test
    public void attributesStubSetAttributeTest() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");
        pseudoElementNode.getAttributes().setAttribute("content", "iText");

        Assert.fail();
    }

    @Test
    public void attributesStubGetSizeTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertEquals(0, pseudoElementNode.getAttributes().size());
    }

    @Test
    public void attributesStubGetAttributeTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assert.assertNull(pseudoElementNode.getAttributes().getAttribute("after"));
    }

    @Test
    public void attributesStubIteratorTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");
        for (IAttribute attr : pseudoElementNode.getAttributes()) {
            Assert.fail("AttributesStub must return an empty iterator");
        }
    }
}
