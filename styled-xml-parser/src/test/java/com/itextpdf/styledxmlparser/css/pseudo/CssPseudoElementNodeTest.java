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
package com.itextpdf.styledxmlparser.css.pseudo;

import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssPseudoElementNodeTest extends ExtendedITextTest {

    @Test
    public void getPseudoElementNameTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertEquals("after", pseudoElementNode.getPseudoElementName());
    }

    @Test
    public void getPseudoElementTagNameTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertEquals("pseudo-element::after", pseudoElementNode.name());
    }

    @Test
    public void getAttributeStringTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertNull(pseudoElementNode.getAttribute("after"));
    }

    @Test
    public void getAttributesTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertTrue(pseudoElementNode.getAttributes() instanceof IAttributes);
        Assertions.assertFalse(pseudoElementNode.getAttributes() == pseudoElementNode.getAttributes());
    }

    @Test
    public void getAdditionalHtmlStylesTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertNull(pseudoElementNode.getAdditionalHtmlStyles());
    }

    @Test
    public void addAdditionalHtmlStylesTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Map<String, String> styles = new HashMap<>();
        styles.put("font-size", "12px");
        styles.put("color", "red");

        Assertions.assertThrows(UnsupportedOperationException.class, () -> pseudoElementNode.addAdditionalHtmlStyles(styles));
    }

    @Test
    public void getLangTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertNull(pseudoElementNode.getLang());
    }

    @Test
    public void attributesStubSetAttributeTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");
        IAttributes attributes = pseudoElementNode.getAttributes();

        Assertions.assertThrows(UnsupportedOperationException.class, () -> attributes.setAttribute("content", "iText"));
    }

    @Test
    public void attributesStubGetSizeTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertEquals(0, pseudoElementNode.getAttributes().size());
    }

    @Test
    public void attributesStubGetAttributeTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");

        Assertions.assertNull(pseudoElementNode.getAttributes().getAttribute("after"));
    }

    @Test
    public void attributesStubIteratorTest() {
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(null, "after");
        for (IAttribute attr : pseudoElementNode.getAttributes()) {
            Assertions.fail("AttributesStub must return an empty iterator");
        }
    }
}
