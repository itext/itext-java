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

import com.itextpdf.kernel.exceptions.PdfException;

import java.nio.charset.StandardCharsets;

import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

@Tag("UnitTest")
public class XmlUtilsTest extends ExtendedITextTest {

    private static final String XML_WITH_XXE = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE r [ <!ENTITY xxe SYSTEM \"xxe-data.txt\"> ]>\n"
            + "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p>&xxe;</p></body>";

    @BeforeEach
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(null);
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

        Assertions.assertTrue(XmlUtils.compareXmls(getStream(pretty), getStream(compact)));
    }

    @Test
    public void compareXmlsMixedContentDifferentFormatting() throws Exception {
        String xml1 =
                "<Title>Text\n" +
                        "  <Link>link</Link>\n" +
                        "</Title>";

        String xml2 =
                "<Title>Text<Link>link</Link></Title>";

        Assertions.assertFalse(XmlUtils.compareXmls(getStream(xml1), getStream(xml2)));
    }

    @Test
    public void compareXmlsDifferentTextContent() throws Exception {
        Assertions.assertFalse(XmlUtils.compareXmls(getStream("<root><a>1</a></root>"), getStream("<root><a>2</a></root>")));
    }

    @Test
    public void compareXmlsEmptyElementsWithAttributes() throws Exception {
        String xml1 = "<root><a x=\"1\"/></root>";
        String xml2 = "<root>\n  <a x=\"1\" />\n</root>";

        Assertions.assertTrue(XmlUtils.compareXmls(getStream(xml1), getStream(xml2)));
    }

    @Test
    public void emptyElementWithAttributesIsNotRemoved() throws Exception {
        String xmlWithWhitespace =
                "<root>\n" +
                        "  <a x=\"1\">   \n   </a>\n" +
                        "</root>";

        String xmlExpected =
                "<root><a x=\"1\"/></root>";

        Assertions.assertTrue(XmlUtils.compareXmls(getStream(xmlWithWhitespace), getStream(xmlExpected)));
    }

    @Test
    public void safeXmlDocumentTest() {
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> XmlUtils.initXmlDocument(getStream(XML_WITH_XXE)));
        Assertions.assertEquals(ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage(), e.getMessage());
    }

    @Test
    public void initNewXmlDocumentTest() {
        Document doc = XmlUtils.initNewXmlDocument();
        Assertions.assertNotNull(doc);
    }

    private static InputStream getStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
}
