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
package com.itextpdf.kernel.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

final class XmlUtils {
    public static void writeXmlDocToStream(Document xmlReport, OutputStream stream) throws TransformerException {
        Transformer transformer = XmlProcessorCreator.createSafeTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
        DOMSource source = new DOMSource(xmlReport);
        StreamResult result = new StreamResult(stream);
        transformer.transform(source, result);
    }

    public static boolean compareXmls(InputStream xml1, InputStream xml2)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(true, true);

        Document doc1 = db.parse(xml1);
        doc1.normalizeDocument();

        Document doc2 = db.parse(xml2);
        doc2.normalizeDocument();

        return doc2.isEqualNode(doc1);
    }

    public static Document initNewXmlDocument() throws ParserConfigurationException {
        DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
        return db.newDocument();
    }
}
