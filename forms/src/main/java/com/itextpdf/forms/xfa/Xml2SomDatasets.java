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
package com.itextpdf.forms.xfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Node;

/**
 * Processes the datasets section in the XFA form.
 */
class Xml2SomDatasets extends Xml2Som {
    /**
     * Creates a new instance from the datasets node. This expects
     * not the datasets but the data node that comes below.
     *
     * @param n the datasets node
     */
    public Xml2SomDatasets(Node n) {
        order = new ArrayList<>();
        name2Node = new HashMap<>();
        stack = new Stack<>();
        anform = 0;
        inverseSearch = new HashMap<>();
        processDatasetsInternal(n);
    }

    /**
     * Inserts a new <CODE>Node</CODE> that will match the short name.
     *
     * @param n         the datasets top <CODE>Node</CODE>
     * @param shortName the short name
     * @return the new <CODE>Node</CODE> of the inserted name
     */
    public Node insertNode(Node n, String shortName) {
        Stack<String> localStack = splitParts(shortName);
        org.w3c.dom.Document doc = n.getOwnerDocument();
        Node n2 = null;
        n = n.getFirstChild();
        while (n.getNodeType() != Node.ELEMENT_NODE)
            n = n.getNextSibling();
        for (int k = 0; k < localStack.size(); ++k) {
            String part = localStack.get(k);
            int idx = part.lastIndexOf('[');
            String name = part.substring(0, idx);
            idx = Integer.parseInt(part.substring(idx + 1, part.length() - 1));
            int found = -1;
            for (n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() == Node.ELEMENT_NODE) {
                    String s = escapeSom(n2.getLocalName());
                    if (s.equals(name)) {
                        ++found;
                        if (found == idx)
                            break;
                    }
                }
            }
            for (; found < idx; ++found) {
                n2 = doc.createElementNS(null, name);
                n2 = n.appendChild(n2);
                Node attr = doc.createAttributeNS(XfaForm.XFA_DATA_SCHEMA, "dataNode");
                attr.setNodeValue("dataGroup");
                n2.getAttributes().setNamedItemNS(attr);
            }
            n = n2;
        }
        inverseSearchAdd(inverseSearch, localStack, shortName);
        name2Node.put(shortName, n2);
        order.add(shortName);
        return n2;
    }

    private static boolean hasChildren(Node n) {
        Node dataNodeN = n.getAttributes().getNamedItemNS(XfaForm.XFA_DATA_SCHEMA, "dataNode");
        if (dataNodeN != null) {
            String dataNode = dataNodeN.getNodeValue();
            if ("dataGroup".equals(dataNode))
                return true;
            else if ("dataValue".equals(dataNode))
                return false;
        }
        if (!n.hasChildNodes())
            return false;
        Node n2 = n.getFirstChild();
        while (n2 != null) {
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
            n2 = n2.getNextSibling();
        }
        return false;
    }

    private void processDatasetsInternal(Node n) {
        if (n != null) {
            Map<String, Integer> ss = new HashMap<>();
            Node n2 = n.getFirstChild();
            while (n2 != null) {
                if (n2.getNodeType() == Node.ELEMENT_NODE) {
                    String s = escapeSom(n2.getLocalName());
                    Integer i = ss.get(s);
                    if (i == null)
                        i = 0;
                    else
                        i = i + 1;
                    ss.put(s, i);
                    stack.push(String.format("%s[%s]", s, i.toString()));
                    if (hasChildren(n2)) {
                        processDatasetsInternal(n2);
                    }
                    String unstack = printStack();
                    order.add(unstack);
                    inverseSearchAdd(unstack);
                    name2Node.put(unstack, n2);
                    stack.pop();
                }
                n2 = n2.getNextSibling();
            }
        }
    }
}
