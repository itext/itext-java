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
package com.itextpdf.kernel.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XmlUtilsNormalizeTextNodesTest extends ExtendedITextTest {

    private static InputStream stream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void compareXmlsSameStructureDifferentWhitespace() throws Exception {
        String pretty =
                "<root>\n" +
                        "  <a>1</a>\n" +
                        "  <b>2</b>\n" +
                        "</root>";

        String compact =
                "<root><a>1</a><b>2</b></root>";

        Assertions.assertTrue(XmlUtils.compareXmls(stream(pretty), stream(compact)));
    }

    @Test
    public void compareXmlsMixedContentDifferentFormatting() throws Exception {
        String xml1 =
                "<Title>Text\n" +
                        "  <Link>link</Link>\n" +
                        "</Title>";

        String xml2 =
                "<Title>Text<Link>link</Link></Title>";

        Assertions.assertFalse(XmlUtils.compareXmls(stream(xml1), stream(xml2)));
    }

    @Test
    public void compareXmlsDifferentTextContent() throws Exception {
        Assertions.assertFalse(XmlUtils.compareXmls(stream("<root><a>1</a></root>"), stream("<root><a>2</a></root>")));
    }

    @Test
    public void compareXmlsEmptyElementsWithAttributes() throws Exception {
        String xml1 = "<root><a x=\"1\"/></root>";
        String xml2 = "<root>\n  <a x=\"1\" />\n</root>";

        Assertions.assertTrue(XmlUtils.compareXmls(stream(xml1), stream(xml2)));
    }

    @Test
    public void emptyElementWithAttributesIsNotRemoved() throws Exception {
        String xmlWithWhitespace =
                "<root>\n" +
                        "  <a x=\"1\">   \n   </a>\n" +
                        "</root>";

        String xmlExpected =
                "<root><a x=\"1\"/></root>";

        Assertions.assertTrue(XmlUtils.compareXmls(stream(xmlWithWhitespace), stream(xmlExpected)));
    }
}
