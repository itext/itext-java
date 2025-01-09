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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Tag("UnitTest")
public class CssPseudoClassDisabledSelectorItemTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/selector/item/CssPseudoClassDisabledSelectorItemTest/";

    @Test
    public void testDisabledSelector() throws IOException {
        String filename = sourceFolder + "disabled.html";

        CssPseudoClassDisabledSelectorItem item = CssPseudoClassDisabledSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse(FileUtil.getInputStreamForFile(filename), "UTF-8");
        IElementNode disabledInput = new JsoupElementNode(((JsoupDocumentNode)documentNode).getDocument().getElementsByTag("input").first());
        IElementNode enabledInput = new JsoupElementNode(((JsoupDocumentNode)documentNode).getDocument().getElementsByTag("input").get(1));

        Assertions.assertFalse(item.matches(documentNode));
        Assertions.assertTrue(item.matches(disabledInput));
        Assertions.assertFalse(item.matches(enabledInput));
        Assertions.assertFalse(item.matches(null));
    }
}
