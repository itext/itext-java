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
package com.itextpdf.io.util;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in the future.
 */
public final class XmlUtil {

    private XmlUtil() {
    }

    /**
     * Creates default document builder factory.
     *
     * @return document builder factory implementation
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        return new DocumentBuilderFactoryImpl();
    }

    /**
     * Creates default SAX parser factory.
     *
     * @return SAX parser factory implementation
     */
    public static SAXParserFactory createSAXParserFactory() {
        return new SAXParserFactoryImpl();
    }

    /**
     * This method creates a new empty Xml document.
     *
     * @return a new Xml document
     * @throws ParserConfigurationException if an error occurs while creating the document
     */
    public static Document initNewXmlDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    /**
     * This method creates new Xml document from input stream.
     *
     * @param inputStream to parse
     *
     * @return parsed Xml document
     *
     * @throws ParserConfigurationException if an error occurs while creating the document
     * @throws SAXException if any parse errors occur
     * @throws IOException if any IO errors occur
     */
    public static Document initXmlDocument(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
    }

}
