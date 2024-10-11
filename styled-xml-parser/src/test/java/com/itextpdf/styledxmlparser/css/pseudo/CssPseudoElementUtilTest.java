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
package com.itextpdf.styledxmlparser.css.pseudo;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class CssPseudoElementUtilTest extends ExtendedITextTest {

    @Test
    public void createPseudoElementTagNameTest() {
        String beforePseudoElemName = CssPseudoElementUtil.createPseudoElementTagName("before");
        String expected = "pseudo-element::before";

        Assertions.assertEquals(expected, beforePseudoElemName);
    }

    @Test
    public void hasBeforeAfterElementsNullScenarioTest() {
        Assertions.assertFalse(CssPseudoElementUtil.hasBeforeAfterElements(null));
    }

    @Test
    public void hasBeforeAfterElementsInstanceOfTest() {
        Assertions.assertFalse(CssPseudoElementUtil
                .hasBeforeAfterElements(new CssPseudoElementNode(null, "")));
    }

    @Test
    public void hasBeforeAfterElementsNodeNameTest() {
        Element element = new Element(Tag.valueOf("pseudo-element::"), "");
        IElementNode node = new JsoupElementNode(element);

        Assertions.assertFalse(CssPseudoElementUtil.hasBeforeAfterElements(node));
    }

    @Test
    public void hasAfterElementTest() {
        Element element = new Element(Tag.valueOf("after"), "");
        IElementNode node = new JsoupElementNode(element);

        Assertions.assertTrue(CssPseudoElementUtil.hasBeforeAfterElements(node));
    }

    @Test
    public void hasBeforeElementTest() {
        Element element = new Element(Tag.valueOf("before"), "");
        IElementNode node = new JsoupElementNode(element);

        Assertions.assertTrue(CssPseudoElementUtil.hasBeforeAfterElements(node));
    }
}
