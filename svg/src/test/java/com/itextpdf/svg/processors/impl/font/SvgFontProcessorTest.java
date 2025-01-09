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
package com.itextpdf.svg.processors.impl.font;

import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode;
import com.itextpdf.svg.css.impl.SvgStyleResolver;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")

public class SvgFontProcessorTest extends ExtendedITextTest {
    @Test
    public void addFontFaceFontsTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\n" +
                "\t@font-face{\n" +
                "\t\tfont-family:Courier;\n" +
                "\t\tsrc:local(Courier);\n" +
                "\t}\n" +
                "  ");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        ICssResolver cssResolver = new SvgStyleResolver(jSoupStyle, context);
        SvgFontProcessor svgFontProcessor = new SvgFontProcessor(context);
        svgFontProcessor.addFontFaceFonts(cssResolver);
        FontInfo info = (FontInfo) context.getTempFonts().getFonts().toArray()[0];
        Assertions.assertEquals("Courier", info.getFontName());
    }
}
