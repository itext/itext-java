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
package com.itextpdf.styledxmlparser.css.page;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PageMarginBoxContextNodeTest extends ExtendedITextTest {

    @Test
    public void defaultBehaviourTest() {
        String marginBoxName = "someName";
        PageMarginBoxContextNode pageMarginBoxContextNode
                = new PageMarginBoxContextNode(new PageContextNode(), marginBoxName);

        Assertions.assertEquals(marginBoxName, pageMarginBoxContextNode.getMarginBoxName());
        Assertions.assertEquals(PageMarginBoxContextNode.PAGE_MARGIN_BOX_TAG, pageMarginBoxContextNode.name());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> pageMarginBoxContextNode.getLang());
        Assertions.assertNull(pageMarginBoxContextNode.getAdditionalHtmlStyles());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> pageMarginBoxContextNode.addAdditionalHtmlStyles(new HashMap<>()));

        IAttributes attributes = pageMarginBoxContextNode.getAttributes();
        Assertions.assertNotNull(attributes);
        Assertions.assertEquals(0, attributes.size());

        String someKey = "someKey";
        String someValue = "someValue";
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> attributes.setAttribute(someKey, someValue));
        Assertions.assertNull(attributes.getAttribute(someKey));
        Assertions.assertNull(pageMarginBoxContextNode.getAttribute(someKey));

        Assertions.assertNull(pageMarginBoxContextNode.getContainingBlockForMarginBox());
        Rectangle someRectangle = new Rectangle(100, 100);
        pageMarginBoxContextNode.setContainingBlockForMarginBox(someRectangle);
        Assertions.assertEquals(someRectangle, pageMarginBoxContextNode.getContainingBlockForMarginBox());

        Assertions.assertNull(pageMarginBoxContextNode.getPageMarginBoxRectangle());
        Rectangle someRectangle2 = new Rectangle(200, 200);
        pageMarginBoxContextNode.setPageMarginBoxRectangle(someRectangle2);
        Assertions.assertEquals(someRectangle2, pageMarginBoxContextNode.getPageMarginBoxRectangle());

    }

    @Test
    public void parentNotPageTest() {
        // Create some invalid node
        PageContextNode pageContextNode = new PageContextNode();
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(pageContextNode, "test");

        // Pass this mode to the constructor
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PageMarginBoxContextNode(pseudoElementNode, "test"));
        Assertions.assertEquals(
                "Page-margin-box context node shall have a page context node as parent.",
                e.getMessage());

    }
}
