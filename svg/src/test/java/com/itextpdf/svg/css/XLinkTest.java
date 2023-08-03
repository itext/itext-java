/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.impl.SvgStyleResolver;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;

@Category(UnitTest.class)
public class XLinkTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RESOLVE_IMAGE_URL))
    public void svgCssResolveMalformedXlinkTest() {
        Element jsoupImage = new Element(Tag.valueOf("image"), "");
        Attributes imageAttributes = jsoupImage.attributes();

        String value = "http://are::";
        imageAttributes.put(new Attribute("xlink:href", value));
        JsoupElementNode node = new JsoupElementNode(jsoupImage);

        SvgStyleResolver sr = new SvgStyleResolver(new SvgProcessorContext(new SvgConverterProperties()));
        Map<String, String> attr = sr.resolveStyles(node, new SvgCssContext());
        Assert.assertEquals(value, attr.get("xlink:href"));
    }

    @Test
    public void svgCssResolveDataXlinkTest() {
        Element jsoupImage = new Element(Tag.valueOf(SvgConstants.Tags.IMAGE), "");
        Attributes imageAttributes = jsoupImage.attributes();
        JsoupElementNode node = new JsoupElementNode(jsoupImage);

        String value1 = "data:image/png;base64,iVBORw0KGgoAAAANSU";
        imageAttributes.put(new Attribute("xlink:href", value1));

        SvgStyleResolver sr = new SvgStyleResolver(new SvgProcessorContext(new SvgConverterProperties()));
        Map<String, String> attr = sr.resolveStyles(node, new SvgCssContext());
        Assert.assertEquals(value1, attr.get("xlink:href"));

        String value2 = "data:...,.";
        imageAttributes.put(new Attribute("xlink:href", value2));

        sr = new SvgStyleResolver(new SvgProcessorContext(new SvgConverterProperties()));
        attr = sr.resolveStyles(node, new SvgCssContext());
        Assert.assertEquals(value2, attr.get("xlink:href"));

        String value3 = "dAtA:...,.";
        imageAttributes.put(new Attribute("xlink:href", value3));

        sr = new SvgStyleResolver(new SvgProcessorContext(new SvgConverterProperties()));
        attr = sr.resolveStyles(node, new SvgCssContext());
        Assert.assertEquals(value3, attr.get("xlink:href"));
    }
}
