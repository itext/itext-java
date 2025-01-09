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
package com.itextpdf.nativeimage;

import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.SvgCssContext;
import com.itextpdf.svg.css.impl.SvgStyleResolver;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SvgTest {

    @Test
    void defaultCss() {
        ICssResolver styleResolver = new SvgStyleResolver(new SvgProcessorContext(new SvgConverterProperties()));
        Element svg = new Element(Tag.valueOf("svg"), "");
        INode svgNode = new JsoupElementNode(svg);
        Map<String, String> resolvedStyles = styleResolver.resolveStyles(svgNode, new SvgCssContext());

        Assertions.assertEquals("1", resolvedStyles.get(SvgConstants.Attributes.STROKE_OPACITY));
        Assertions.assertEquals("1px", resolvedStyles.get(SvgConstants.Attributes.STROKE_WIDTH));
        Assertions.assertEquals(SvgConstants.Values.NONE, resolvedStyles.get(SvgConstants.Attributes.STROKE));
        Assertions.assertEquals(SvgConstants.Values.BUTT, resolvedStyles.get(SvgConstants.Attributes.STROKE_LINECAP));
        Assertions.assertEquals("0", resolvedStyles.get(SvgConstants.Attributes.STROKE_DASHOFFSET));
        Assertions.assertEquals(SvgConstants.Values.NONE, resolvedStyles.get(SvgConstants.Attributes.STROKE_DASHARRAY));
        Assertions.assertEquals("4", resolvedStyles.get(SvgConstants.Attributes.STROKE_MITERLIMIT));
        Assertions.assertEquals("black", resolvedStyles.get(SvgConstants.Attributes.FILL));
        Assertions.assertEquals(SvgConstants.Values.FILL_RULE_NONZERO, resolvedStyles.get(SvgConstants.Attributes.FILL_RULE));
        Assertions.assertEquals("1", resolvedStyles.get(SvgConstants.Attributes.FILL_OPACITY));
        Assertions.assertEquals("helvetica", resolvedStyles.get(SvgConstants.Attributes.FONT_FAMILY));
        Assertions.assertEquals("9pt", resolvedStyles.get(SvgConstants.Attributes.FONT_SIZE));
    }
}
