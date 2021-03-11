/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

@Category(UnitTest.class)
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

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(new DefaultSafeXmlParserFactory());
    }

    @Test
    public void xmlWithoutDtd() throws ParserConfigurationException, IOException, SAXException {
        Document document;
        DocumentBuilder documentBuilder = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
        try (InputStream inputStream = new ByteArrayInputStream(XML_WITHOUT_DTD.getBytes(StandardCharsets.UTF_8))) {
            document = documentBuilder.parse(inputStream);
        }
        Assert.assertNotNull(document);
    }

    @Test
    public void xmlWithXXECustomFactory() throws ParserConfigurationException, IOException, SAXException {
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        DocumentBuilder documentBuilder = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
        try (InputStream inputStream = new ByteArrayInputStream(XML_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage("Test message");
            documentBuilder.parse(inputStream);
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
            junitExpectedException.expect(SAXParseException.class);
            junitExpectedException.expectMessage(XmlProcessorCreatorSecurityTest.DTD_EXCEPTION_MESSAGE);
            reader.parse(inputSource);
        }
    }

    private void createSafeDocumentBuilderTest(String xml, boolean nameSpace, boolean comments)
            throws IOException, SAXException {
        DocumentBuilder documentBuilder = XmlProcessorCreator.createSafeDocumentBuilder(nameSpace, comments);
        try (InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            junitExpectedException.expect(SAXParseException.class);
            junitExpectedException.expectMessage(XmlProcessorCreatorSecurityTest.DTD_EXCEPTION_MESSAGE);
            documentBuilder.parse(inputStream);
        }
    }
}
