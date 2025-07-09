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
package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.XmlProcessorCreator;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

final class XmlValidationUtils {
    static final String CERTIFICATE_NOT_FOUND =
            "\"X509Certificate\" object isn't found in the signature. Other certificate sources are not supported.";
    static final String KEY_INFO_NULL = "Key info in XML signature cannot be null.";

    private XmlValidationUtils() {
        // Private constructor so that the class instance cannot be instantiated.
    }

    public static boolean createXmlDocumentAndCheckValidity(InputStream xmlDocumentInputStream,
            CertificateSelector keySelector) throws IOException, SAXException, XMLSecurityException {
        Document xmlDocument = XmlProcessorCreator.createSafeDocumentBuilder(true, false)
                .parse(xmlDocumentInputStream);

        preProcessXmlDocument(xmlDocument);
        Node sigElement = xmlDocument.getElementsByTagName("ds:Signature").item(0);
        if (sigElement == null) {
            sigElement = xmlDocument.getElementsByTagName("dsig:Signature").item(0);
        }
        if (sigElement == null) {
            sigElement = xmlDocument.getElementsByTagName("Signature").item(0);
        }
        org.apache.xml.security.Init.init();
        XMLSignature signature = new XMLSignature((Element) sigElement, "", true,
                BouncyCastleFactoryCreator.getFactory().getProvider());

        if (signature.getKeyInfo() == null) {
            throw new PdfException(KEY_INFO_NULL);
        }
        X509Certificate cert = signature.getKeyInfo().getX509Certificate();
        if (cert == null) {
            throw new PdfException(CERTIFICATE_NOT_FOUND);
        }
        keySelector.setCertificate(cert);
        return signature.checkSignatureValue(cert);
    }

    private static void preProcessXmlDocument(Document xmlDocument) {
        Element rootElement = xmlDocument.getDocumentElement();
        setIdRecursively(rootElement);
    }

    private static void setIdRecursively(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attribute = attributes.item(i);
            if ("ID".equalsIgnoreCase(attribute.getLocalName())) {
                element.setIdAttribute(attribute.getNodeName(), true);
            }
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child instanceof Element) {
                setIdRecursively((Element) child);
            }
        }
    }
}
