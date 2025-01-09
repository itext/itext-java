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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.utils.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.util.Collection;

/**
 * This class will convert a {@link PadesValidationReport} to its xml representation.
 */
public class XmlReportGenerator {

    public static final String DOC_NS = "http://uri.etsi.org/19102/v1.2.1#";
    public static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XS_NS = "http://www.w3.org/2001/XMLSchema";
    private final XmlReportOptions options;

    /**
     * Instantiate a new instance of XmlReportGenerator.
     *
     * @param options the conversion options to use
     */
    public XmlReportGenerator(XmlReportOptions options) {
        this.options = options;
    }

    /**
     * Generate xlm representation of a {@link PadesValidationReport}.
     *
     * @param report the report to transform
     * @param writer the writer instance to write the resulting xml to
     *
     * @throws TransformerException if an unrecoverable error occurs during the course of the transformation
     * @throws ParserConfigurationException if the DocumentBuilderFactory cannot support a configured feature
     */
    public void generate(PadesValidationReport report, Writer writer) throws TransformerException,
            ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        // To be compliant, completely disable DOCTYPE declaration:
        docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // Android-Conversion-Skip-Line (this feature is not supported in Android SDK)
        docFactory.setExpandEntityReferences(false);
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("ValidationReport");
        // Register namespaces in root node.
        root.setAttribute("xmlns", DOC_NS);
        root.setAttribute("xmlns:ds", DS_NS);
        root.setAttribute("xmlns:xsi", XSI_NS);
        root.setAttribute("xmlns:xs", XS_NS);
        doc.appendChild(root);
        for (SignatureValidationReport signatureValidation : report.getSignatureValidationReports()) {
            addSignatureReport(doc, root, signatureValidation);
        }

        Node signatureValidationObjects = root.appendChild(doc.createElementNS(DOC_NS, "SignatureValidationObjects"));
        XmlGeneratorCollectableObjectVisitor visitor = new XmlGeneratorCollectableObjectVisitor(doc,
                signatureValidationObjects);
        for (CollectableObject object : report.getValidationObjects()) {
            object.accept(visitor);
        }

        // Convert to pretty printed xml.
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", ""); // Android-Conversion-Skip-Line (this attribute is not supported in Android SDK)
        tf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", ""); // Android-Conversion-Skip-Line (this attribute is not supported in Android SDK)
        tf.setAttribute("indent-number", 2); // Android-Conversion-Skip-Line (this attribute is not supported in Android SDK)
        Transformer trans = tf.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(new DOMSource(doc), new StreamResult(writer));
    }

    private static void addSignatureReport(Document doc, Element root, SignatureValidationReport signatureValidation) {
        Node sigValNode = root.appendChild(doc.createElementNS(DOC_NS, "SignatureValidationReport"));
        // Create elements with one of the defined namespaces. For the default namespace, there is no namespace prefix.
        // For other namespaces, the correct namespace prefix must be added.
        Element signatureIdentifier = doc.createElementNS(DOC_NS, "SignatureIdentifier");
        signatureIdentifier.setAttribute("id", signatureValidation.getSignatureIdentifier().getIdentifier().getId());
        sigValNode.appendChild(signatureIdentifier);
        Node digestAlgAndValue = signatureIdentifier.appendChild(doc.createElementNS(DOC_NS, "DigestAlgAndValue"));
        // Use setAttributeNS only when the attribute has a different namespace as the element.
        ((Element) digestAlgAndValue.appendChild(doc.createElementNS(DS_NS, "ds:DigestMethod"))).setAttributeNS("",
                "Algorithm",
                signatureValidation.getSignatureIdentifier().getDigestMethodAlgorithm());
        digestAlgAndValue.appendChild(doc.createElementNS(DS_NS, "ds:DigestValue")).setTextContent(
                signatureValidation.getSignatureIdentifier().getDigestValue());
        signatureIdentifier.appendChild(doc.createElementNS(DS_NS, "ds:SignatureValue")).setTextContent(
                signatureValidation.getSignatureIdentifier().getBase64SignatureValue());
        signatureIdentifier.appendChild(doc.createElementNS(DOC_NS, "HashOnly")).setTextContent(String.valueOf(
                signatureValidation.getSignatureIdentifier().isHashOnly()));
        signatureIdentifier.appendChild(doc.createElementNS(DOC_NS, "DocHashOnly")).setTextContent(String.valueOf(
                signatureValidation.getSignatureIdentifier().isDocHashOnly()));

        Node status = doc.createElement("SignatureValidationStatus");
        Node mainIndication = doc.createElement("MainIndication");
        mainIndication.setTextContent(signatureValidation.getSignatureValidationStatus().getMainIndicationAsString());
        status.appendChild(mainIndication);
        sigValNode.appendChild(status);
        String subIndication = signatureValidation.getSignatureValidationStatus().getSubIndicationAsString();
        if (subIndication != null) {
            Node subIndicationNode = doc.createElement("SubIndication");
            subIndicationNode.setTextContent(subIndication);
            status.appendChild(subIndicationNode);
        }
        Collection<Pair<String, String>> messages = signatureValidation.getSignatureValidationStatus().getMessages();
        if (!messages.isEmpty()) {
            Node associatedValidationReportData = doc.createElement("AssociatedValidationReportData");
            status.appendChild(associatedValidationReportData);
            Node additionalValidationReportData = doc.createElement("AdditionalValidationReportData");
            associatedValidationReportData.appendChild(additionalValidationReportData);
            for (Pair<String, String> message : messages) {
                Node reportData = doc.createElement("ReportData");
                Node type = doc.createElement("Type");
                type.setTextContent(message.getValue());
                reportData.appendChild(type);
                Element value = doc.createElement("Value");
                value.setAttribute("xsi:type", "xs:string");
                value.setTextContent(message.getKey());
                reportData.appendChild(value);
                additionalValidationReportData.appendChild(reportData);
            }
        }
    }
}
