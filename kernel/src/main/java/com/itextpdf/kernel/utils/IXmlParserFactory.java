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
package com.itextpdf.kernel.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import org.xml.sax.XMLReader;

/**
 * The interface in which methods for creating xml parsers are declared.
 */
public interface IXmlParserFactory {
    /**
     * Creates the instance of the {@link DocumentBuilder}.
     *
     * @param namespaceAware   specifies whether the parser should be namespace aware
     * @param ignoringComments specifies whether the parser should ignore comments
     *
     * @return instance of the {@link DocumentBuilder}
     */
    DocumentBuilder createDocumentBuilderInstance(boolean namespaceAware, boolean ignoringComments);

    /**
     * Creates the instance of the {@link XMLReader}.
     *
     * @param namespaceAware specifies whether the parser should be namespace aware
     * @param validating     specifies whether the parser should validate documents as they are parsed
     *
     * @return instance of the {@link XMLReader}
     */
    XMLReader createXMLReaderInstance(boolean namespaceAware, boolean validating);

    /**
     * Creates the instance of the {@link Transformer}.
     *
     * @return instance of the {@link Transformer}
     */
    Transformer createTransformerInstance();
}
