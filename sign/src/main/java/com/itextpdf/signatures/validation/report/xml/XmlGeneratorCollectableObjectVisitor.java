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
package com.itextpdf.signatures.validation.report.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class XmlGeneratorCollectableObjectVisitor implements CollectableObjectVisitor {
    private final Document doc;
    private final Node parent;

    public XmlGeneratorCollectableObjectVisitor(Document doc, Node parent) {
        this.doc = doc;
        this.parent = parent;
    }

    @Override
    public void visit(CertificateWrapper certificateWrapper) {
        Node validationObject = createValidationObjectElement(certificateWrapper.getIdentifier(),
                "urn:etsi:019102:validationObject:certificate");
        Node representation = doc.createElement("ValidationObjectRepresentation");
        Node b64 = doc.createElement("base64");
        b64.setTextContent(certificateWrapper.getBase64ASN1Structure());
        representation.appendChild(b64);
        validationObject.appendChild(representation);
        parent.appendChild(validationObject);
    }

    @Override
    public void visit(POEValidationReport poeValidationReport) {
        // Will be completed later
    }

    private Node createValidationObjectElement(Identifier identifier, String typeName) {
        Element validationObject = doc.createElement("ValidationObject");
        validationObject.setAttribute("id", identifier.getId());
        Node objectType = doc.createElement("ObjectType");
        objectType.setTextContent(typeName);
        validationObject.appendChild(objectType);
        return validationObject;
    }
}
