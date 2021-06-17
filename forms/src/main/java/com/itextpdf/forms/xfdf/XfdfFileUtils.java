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
package com.itextpdf.forms.xfdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.utils.XmlProcessorCreator;

import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(outputStream);
        transformer.transform(domSource, streamResult);
    }
}
