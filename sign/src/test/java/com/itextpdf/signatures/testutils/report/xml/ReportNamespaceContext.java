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
package com.itextpdf.signatures.testutils.report.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class ReportNamespaceContext implements NamespaceContext {
    // Store the source document to search the namespaces.
    private final Node sourceNode;

    public ReportNamespaceContext(Node node) {
        sourceNode = node;
    }

    @Override
    // The lookup for the namespace URIs is delegated to the stored document.
    public String getNamespaceURI(String prefix) {
        if ("r".equals(prefix)) {
            return sourceNode.getNamespaceURI();
        } else {
            return lookupNamespaceURI(sourceNode, prefix);
        }
    }

    private String lookupNamespaceURI(Node node, String prefix) {
        if (node.lookupNamespaceURI(prefix) != null) {
            return node.lookupNamespaceURI(prefix);
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                return lookupNamespaceURI(childNode, prefix);
            }
        }
        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
