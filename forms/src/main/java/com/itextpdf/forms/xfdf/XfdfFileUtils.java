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
package com.itextpdf.forms.xfdf;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.XmlProcessorCreator;

import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

final class XfdfFileUtils {

    private XfdfFileUtils() {
    }

    /**
     * Creates a new xml-styled document for writing xfdf info.
     *
     * @throws ParserConfigurationException in case of failure to create a new document.
     */
    static Document createNewXfdfDocument() {
        try {
            DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
            return db.newDocument();
        } catch (Exception e) {
            throw new PdfException(e.getMessage(), e);
        }
    }

    /**
     * Creates a new xfdf document based on given input stream.
     *
     * @param inputStream containing xfdf info.
     */
    static Document createXfdfDocumentFromStream(InputStream inputStream) {
        try {
            DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
            return db.parse(inputStream);
        } catch (Exception e) {
            throw new PdfException(e.getMessage(), e);
        }
    }

    /**
     * Saves the info from XML-styled {@link Document} to {@link OutputStream}.
     *
     * @param document     input {@link Document} that contains XFDF info
     * @param outputStream the output stream
     */
    static void saveXfdfDocumentToFile(Document document, OutputStream outputStream)
            throws TransformerException {
        Transformer transformer = XmlProcessorCreator.createSafeTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(outputStream);
        transformer.transform(domSource, streamResult);
    }
}
