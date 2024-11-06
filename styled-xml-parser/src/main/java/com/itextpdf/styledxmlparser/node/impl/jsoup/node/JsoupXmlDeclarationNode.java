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
package com.itextpdf.styledxmlparser.node.impl.jsoup.node;

import com.itextpdf.styledxmlparser.jsoup.nodes.XmlDeclaration;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.styledxmlparser.node.IXmlDeclarationNode;

/**
 * Implementation of the {@link IXmlDeclarationNode} interface; wrapper for the JSoup {@link XmlDeclaration} class.
 */
public class JsoupXmlDeclarationNode extends JsoupNode implements IXmlDeclarationNode {
    private final IAttributes attributes;
    private final XmlDeclaration xmlDeclaration;

    /**
     * Creates a new {@link JsoupXmlDeclarationNode} instance.
     *
     * @param xmlDeclaration the xml declaration node
     */
    public JsoupXmlDeclarationNode(XmlDeclaration xmlDeclaration) {
        super(xmlDeclaration);
        this.attributes = new JsoupAttributes(xmlDeclaration.attributes());
        this.xmlDeclaration = xmlDeclaration;
    }

    @Override
    public IAttributes getAttributes() {
        return attributes;
    }

    @Override
    public String getAttribute(String key) {
        return attributes.getAttribute(key);
    }

    @Override
    public String name() {
        return xmlDeclaration.name();
    }
}
