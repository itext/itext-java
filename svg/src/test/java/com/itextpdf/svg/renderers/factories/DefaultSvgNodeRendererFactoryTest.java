/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.svg.renderers.factories;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class DefaultSvgNodeRendererFactoryTest extends ExtendedITextTest {

    @Test
    public void createSvgNodeRenderer() {
        ISvgNodeRendererFactory nodeRendererFactory = new DefaultSvgNodeRendererFactory();

        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> nodeRendererFactory.createSvgNodeRendererForTag(null, null)
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TAG_PARAMETER_NULL, e.getMessage());
    }

    @Test
    public void maskRendererDoesNotHaveParentTest() {
        ISvgNodeRendererFactory nodeRendererFactory = new DefaultSvgNodeRendererFactory();
        ISvgNodeRenderer parent = nodeRendererFactory.createSvgNodeRendererForTag(
                createElementNode(SvgConstants.Tags.G), null);

        ISvgNodeRenderer mask = nodeRendererFactory.createSvgNodeRendererForTag(
                createElementNode(SvgConstants.Tags.MASK), parent);

        Assertions.assertNull(mask.getParent());
    }

    @Test
    public void symbolRendererDoesNotHaveParentTest() {
        ISvgNodeRendererFactory nodeRendererFactory = new DefaultSvgNodeRendererFactory();
        ISvgNodeRenderer parent = nodeRendererFactory.createSvgNodeRendererForTag(
                createElementNode(SvgConstants.Tags.G), null);

        ISvgNodeRenderer symbol = nodeRendererFactory.createSvgNodeRendererForTag(
                createElementNode(SvgConstants.Tags.SYMBOL), parent);

        Assertions.assertNull(symbol.getParent());
    }

    @Test
    public void drawableRendererHasParentTest() {
        ISvgNodeRendererFactory nodeRendererFactory = new DefaultSvgNodeRendererFactory();
        ISvgNodeRenderer parent = nodeRendererFactory.createSvgNodeRendererForTag(
                createElementNode(SvgConstants.Tags.G), null);

        ISvgNodeRenderer rectangle = nodeRendererFactory.createSvgNodeRendererForTag(
                createElementNode(SvgConstants.Tags.RECT), parent);

        Assertions.assertSame(parent, rectangle.getParent());
    }

    private static IElementNode createElementNode(String tagName) {
        return new JsoupElementNode(new Element(Tag.valueOf(tagName), ""));
    }
}
