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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.itextpdf.kernel.exceptions.PdfException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility methods for working with XML Documents in a safe way.
 */
public final class XmlUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private XmlUtils() {
        // Empty private constructor
    }

    /**
     * Writes the provided DOM {@link Document} to the given {@link OutputStream}.
     *
     * @param xmlReport the DOM document to serialize; must not be {@code null}
     * @param stream the output stream to write the XML to; the caller is
     *               responsible for opening and closing the stream
     *
     * @throws TransformerException when an error occurs during transformation
     */
    public static void writeXmlDocToStream(Document xmlReport, OutputStream stream) throws TransformerException {
        Transformer transformer = XmlProcessorCreator.createSafeTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
        DOMSource source = new DOMSource(xmlReport);
        StreamResult result = new StreamResult(stream);
        transformer.transform(source, result);
    }

    /**
     * Compares two XML documents provided as input streams for structural equality.
     * <p>
     * The method parses both input streams using a secure
     * {@link DocumentBuilder} (created via
     * {@link XmlProcessorCreator#createSafeDocumentBuilder(boolean, boolean)}),
     * normalizes the documents and removes empty text nodes before
     * delegating to {@link org.w3c.dom.Node#isEqualNode(Node)} to perform
     * the equality check.
     *
     * @param xml1 input stream of the first XML document; must be readable
     * @param xml2 input stream of the second XML document; must be readable
     *
     * @return {@code true} if the documents are structurally equal, {@code false}
     *         otherwise
     *
     * @throws SAXException when a parse error occurs
     * @throws IOException when an I/O error occurs while reading the streams
     */
    public static boolean compareXmls(InputStream xml1, InputStream xml2) throws SAXException, IOException {
        DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(true, true);

        Document doc1 = db.parse(xml1);
        doc1.normalizeDocument();
        normalizeTextNodes(doc1.getDocumentElement());

        Document doc2 = db.parse(xml2);
        doc2.normalizeDocument();
        normalizeTextNodes(doc2.getDocumentElement());

        return doc2.isEqualNode(doc1);
    }

    /**
     * Creates and returns a new empty DOM {@link Document} using a safe
     * {@link DocumentBuilder} from {@link XmlProcessorCreator}.
     *
     * @return a new empty {@link Document}
     */
    public static Document initNewXmlDocument() {
        DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
        return db.newDocument();
    }

    /**
     * Parses an XML document from the provided input stream and returns the resulting DOM {@link Document}.
     *
     * <p>
     * If parsing fails for any reason the method wraps the underlying
     * exception in a {@link PdfException}.
     *
     * @param inputStream the input stream containing XML content; must be readable
     *
     * @return the parsed {@link Document}
     *
     * @throws PdfException if parsing fails; the original exception is available
     *                      as the cause
     */
    public static Document initXmlDocument(InputStream inputStream) {
        try {
            DocumentBuilder db = XmlProcessorCreator.createSafeDocumentBuilder(false, false);
            return db.parse(inputStream);
        } catch (Exception e) {
            throw new PdfException(e.getMessage(), e);
        }
    }

    /**
     * Recursively removes text nodes that contain only whitespace from the
     * subtree rooted at {@code node}.
     *
     * This helper improves consistency when comparing documents by
     * eliminating insignificant text nodes that may differ between
     * serializations.
     *
     * @param node root of the subtree to normalize; may be an element node
     */
    private static void normalizeTextNodes(Node node) {
        NodeList children = node.getChildNodes();
        java.util.List<Node> toRemove = new java.util.ArrayList<>();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue();
                if (text != null && text.trim().isEmpty()) {
                    toRemove.add(child);
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                normalizeTextNodes(child);
            }
        }

        for (Node n : toRemove) {
            node.removeChild(n);
        }
    }
}
