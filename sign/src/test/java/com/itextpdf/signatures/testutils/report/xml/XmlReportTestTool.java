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
package com.itextpdf.signatures.testutils.report.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class XmlReportTestTool {

    private static final String XSDROOT = "./src/test/resources/com/itextpdf/signatures/validation/report/xml/";
    private final Document xml;
    private final XPath xPath;
    private final String report;

    public XmlReportTestTool(String report) throws ParserConfigurationException, IOException, SAXException {
        this.report = report;
        StringReader stringReader = new StringReader(report);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // Android-Conversion-Skip-Line (this feature is not supported in Android SDK)
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // Android-Conversion-Skip-Line (this feature is not supported in Android SDK)
        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        xml = db.parse(new InputSource(stringReader));

        xPath = XPathFactory.newInstance().newXPath();
        NamespaceContext nsContext = new ReportNamespaceContext(xml.getDocumentElement());
        xPath.setNamespaceContext(nsContext);
    }

    public Element getDocumentNode() {
        return xml.getDocumentElement();
    }

    public int countElements(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        return nodes.getLength();
    }

    public String getElementContent(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.STRING);
        return (String) result;
    }

    public NodeList executeXpathAsNodeList(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.NODESET);
        return (NodeList) result;
    }

    public Node executeXpathAsNode(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.NODE);
        return (Node) result;
    }

    public String executeXpathAsString(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.STRING);
        return (String) result;
    }

    public Double executeXpathAsNumber(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.NUMBER);
        return (Double) result;
    }

    public Boolean executeXpathAsBoolean(String xPathQuery) throws XPathExpressionException {
        Object result = xPath.compile(xPathQuery).evaluate(xml, XPathConstants.BOOLEAN);
        return (Boolean) result;
    }

    public String validateXMLSchema() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new Source[]{
                    new StreamSource(XSDROOT + "xml.xsd"),
                    new StreamSource(XSDROOT + "XMLSchema.xsd"),
                    new StreamSource(XSDROOT + "xmldsig-core-schema.xsd"),
                    new StreamSource(XSDROOT + "XAdES.xsd"),
                    new StreamSource(XSDROOT + "ts_119612v020201_201601xsd.xsd"),
                    new StreamSource(XSDROOT + "1910202xmlSchema.xsd"),
            });

            Validator validator = schema.newValidator();

            StringBuilder log = new StringBuilder();
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    writeMessage(exception, "warn");
                }

                private void writeMessage(SAXParseException exception, String severity) {
                    log.append("***\n");
                    log.append("\tPosition:").append(exception.getLineNumber()).append(':').append(exception.getColumnNumber()).append('\n');
                    log.append("\tSeverity:").append(severity).append('\n');
                    log.append("\tMessage :").append(exception.getMessage()).append('\n');
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    writeMessage(exception, "fatal error");
                }

                @Override
                public void error(SAXParseException exception) {
                    writeMessage(exception, "error");
                }
            });

            validator.validate(new StreamSource(new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8))));
            String message = log.toString();
            if (message.isEmpty()) {
                return null;
            }
            return message;
        } catch (IOException | SAXException e) {
            return e.getMessage();
        }
    }
}
