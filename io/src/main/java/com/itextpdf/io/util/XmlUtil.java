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
package com.itextpdf.io.util;

// Android-Conversion-Skip-Line (Directly use xerces library to unify behavior with vanilla java (where xerces is implemented into JRE))
// Android-Conversion-Replace import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
// Android-Conversion-Replace import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

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
        // Android-Conversion-Skip-Line (Directly use xerces library to unify behavior with vanilla java (where xerces is implemented into JRE))
        return DocumentBuilderFactory.newInstance(); // Android-Conversion-Replace return new DocumentBuilderFactoryImpl();
    }

    /**
     * Creates default SAX parser factory.
     *
     * @return SAX parser factory implementation
     */
    public static SAXParserFactory createSAXParserFactory() {
        // Android-Conversion-Skip-Line (Directly use xerces library to unify behavior with vanilla java (where xerces is implemented into JRE))
        return SAXParserFactory.newInstance(); // Android-Conversion-Replace return new SAXParserFactoryImpl();
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

}
