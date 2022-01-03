/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PageMarginBoxContextNodeTest extends ExtendedITextTest {

    @Test
    public void defaultBehaviourTest() {
        String marginBoxName = "someName";
        PageMarginBoxContextNode pageMarginBoxContextNode
                = new PageMarginBoxContextNode(new PageContextNode(), marginBoxName);

        Assert.assertEquals(marginBoxName, pageMarginBoxContextNode.getMarginBoxName());
        Assert.assertEquals(PageMarginBoxContextNode.PAGE_MARGIN_BOX_TAG, pageMarginBoxContextNode.name());

        Assert.assertThrows(UnsupportedOperationException.class, () -> pageMarginBoxContextNode.getLang());
        Assert.assertNull(pageMarginBoxContextNode.getAdditionalHtmlStyles());
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> pageMarginBoxContextNode.addAdditionalHtmlStyles(new HashMap<>()));

        IAttributes attributes = pageMarginBoxContextNode.getAttributes();
        Assert.assertNotNull(attributes);
        Assert.assertEquals(0, attributes.size());

        String someKey = "someKey";
        String someValue = "someValue";
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> attributes.setAttribute(someKey, someValue));
        Assert.assertNull(attributes.getAttribute(someKey));
        Assert.assertNull(pageMarginBoxContextNode.getAttribute(someKey));

        Assert.assertNull(pageMarginBoxContextNode.getContainingBlockForMarginBox());
        Rectangle someRectangle = new Rectangle(100, 100);
        pageMarginBoxContextNode.setContainingBlockForMarginBox(someRectangle);
        Assert.assertEquals(someRectangle, pageMarginBoxContextNode.getContainingBlockForMarginBox());

        Assert.assertNull(pageMarginBoxContextNode.getPageMarginBoxRectangle());
        Rectangle someRectangle2 = new Rectangle(200, 200);
        pageMarginBoxContextNode.setPageMarginBoxRectangle(someRectangle2);
        Assert.assertEquals(someRectangle2, pageMarginBoxContextNode.getPageMarginBoxRectangle());

    }

    @Test
    public void parentNotPageTest() {
        // Create some invalid node
        PageContextNode pageContextNode = new PageContextNode();
        CssPseudoElementNode pseudoElementNode = new CssPseudoElementNode(pageContextNode, "test");

        // Pass this mode to the constructor
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> new PageMarginBoxContextNode(pseudoElementNode, "test"));
        Assert.assertEquals(
                "Page-margin-box context node shall have a page context node as parent.",
                e.getMessage());

    }
}
