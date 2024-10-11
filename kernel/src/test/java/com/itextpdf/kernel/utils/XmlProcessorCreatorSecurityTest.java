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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

@Tag("UnitTest")
public class XmlProcessorCreatorSecurityTest extends ExtendedITextTest {

    private static final String XML_WITHOUT_DTD = "<?xml version=\"1.0\"?>\n"
            + "<employees>\n"
            + "  <employee>Artem B</employee>\n"
            + "  <employee>Nikita K</employee>\n"
            + "</employees>";

    private static final String XML_WITH_DTD = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE employees [\n"
            + "  <!ELEMENT employees (employee)*>\n"
            + "  <!ELEMENT employee (#PCDATA)>\n"
            + "]>\n"
            + "<employees>\n"
            + "  <employee>Artem B</employee>\n"
            + "  <employee>Nikita K</employee>\n"
            + "</employees>";

    private static final String XML_WITH_EMPTY_DTD = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE>\n"
            + "<employees>\n"
            + "  <employee>Artem B</employee>\n"
            + "  <employee>Nikita K</employee>\n"
            + "</employees>";

    private static final String XML_WITH_INTERNAL_ENTITY = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE employees [\n"
            + "  <!ELEMENT employees (employee)*>\n"
            + "  <!ELEMENT employee (#PCDATA)>\n"
            + "  <!ENTITY companyname \"Company\">\n"
            + "]>\n"
            + "<employees>\n"
            + "  <employee>Artem B &companyname;</employee>\n"
            + "  <employee>Nikita K &companyname;</employee>\n"
            + "</employees>";

    private static final String XML_WITH_XXE = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE employees [\n"
            + "  <!ELEMENT employees (employee)*>\n"
            + "  <!ELEMENT employee (#PCDATA)>\n"
            + "  <!ENTITY xxe SYSTEM \"{0}\">"
            + "]>\n"
            + "<employees>\n"
            + "  <employee>Artem B &xxe;</employee>\n"
            + "  <employee>Nikita K &xxe;</employee>\n"
            + "</employees>";

    private final static String DTD_EXCEPTION_MESSAGE = ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage();

    @BeforeEach
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(null);
    }

    @Test
    public void xmlWithoutDtd() throws ParserConfigurationException, IOException, SAXException {
        Document document;
        DocumentBuilder documentBuilder = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
        try (InputStream inputStream = new ByteArrayInputStream(XML_WITHOUT_DTD.getBytes(StandardCharsets.UTF_8))) {
            document = documentBuilder.parse(inputStream);
        }
        Assertions.assertNotNull(document);
    }

    @Test
    public void xmlWithXXECustomFactory() throws ParserConfigurationException, IOException, SAXException {
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        DocumentBuilder documentBuilder = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
        try (InputStream inputStream = new ByteArrayInputStream(XML_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> documentBuilder.parse(inputStream)
            );
            Assertions.assertEquals("Test message", e.getMessage());
        }
    }

    @Test
    public void xmlWithDtd() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_DTD, false, false);
    }

    @Test
    public void xmlWithEmptyDtdDtd() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_EMPTY_DTD, false, false);
    }

    @Test
    public void xmlWithInternalEntity() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_INTERNAL_ENTITY, false, false);
    }

    @Test
    public void xmlWithXXE() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_XXE, false, false);
    }

    @Test
    public void xmlWithXXEFlags() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_XXE, true, true);
    }

    @Test
    public void xmlWithXXEFlags2() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_XXE, true, false);
    }

    @Test
    public void xmlWithXXEFlags3() throws ParserConfigurationException, IOException, SAXException {
        createSafeDocumentBuilderTest(XML_WITH_XXE, false, true);
    }

    @Test
    public void xmlWithXxeXMLReader() throws ParserConfigurationException, IOException, SAXException {
        createSafeXMLReaderTest(true, true);
    }

    @Test
    public void xmlWithXxeXMLReaderFlags() throws ParserConfigurationException, IOException, SAXException {
        createSafeXMLReaderTest(false, false);
    }

    @Test
    public void xmlWithXxeXMLReaderFlags2() throws ParserConfigurationException, IOException, SAXException {
        createSafeXMLReaderTest(true, false);
    }

    @Test
    public void xmlWithXxeXMLReaderFlags3() throws ParserConfigurationException, IOException, SAXException {
        createSafeXMLReaderTest(false, true);
    }

    @Test
    public void setXmlParserFactoryNull() throws IOException, SAXException {
        XmlProcessorCreator.setXmlParserFactory(null);
        createSafeDocumentBuilderTest(XML_WITH_XXE, false, false);
    }

    private void createSafeXMLReaderTest(boolean nameSpace, boolean validating)
            throws IOException, SAXException {
        XMLReader reader = XmlProcessorCreator.createSafeXMLReader(nameSpace, validating);
        try (InputStream inputStream = new ByteArrayInputStream(
                XmlProcessorCreatorSecurityTest.XML_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            InputSource inputSource = new InputSource(inputStream);
            Exception e = Assertions.assertThrows(SAXParseException.class,
                    () -> reader.parse(inputSource)
            );
            Assertions.assertEquals(XmlProcessorCreatorSecurityTest.DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    private void createSafeDocumentBuilderTest(String xml, boolean nameSpace, boolean comments)
            throws IOException, SAXException {
        DocumentBuilder documentBuilder = XmlProcessorCreator.createSafeDocumentBuilder(nameSpace, comments);
        try (InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            Exception e = Assertions.assertThrows(SAXParseException.class,
                    () -> documentBuilder.parse(inputStream)
            );
            Assertions.assertEquals(XmlProcessorCreatorSecurityTest.DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }
}
