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
package com.itextpdf.styledxmlparser;


import com.itextpdf.styledxmlparser.node.IDocumentNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the XML parsing operations that accept XML and return a document node.
 */
public interface IXmlParser {

    /**
     * Parses XML provided as an {@code InputStream} and an encoding.
     *
     * @param XmlStream the Xml stream
     * @param charset the character set. If {@code null} then parser should detect encoding from stream.
     * @return a document node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    IDocumentNode parse(InputStream XmlStream, String charset) throws IOException;

    /**
     * Parses XML provided as a {@code String}.
     *
     * @param Xml the Xml string
     * @return a document node
     */
    IDocumentNode parse(String Xml);

}
