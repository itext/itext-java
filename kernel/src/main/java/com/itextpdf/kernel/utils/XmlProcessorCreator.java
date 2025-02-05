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
 * Utility class for creating XML processors.
 */
public final class XmlProcessorCreator {

    private static IXmlParserFactory xmlParserFactory;

    static {
        xmlParserFactory = new DefaultSafeXmlParserFactory();
    }

    private XmlProcessorCreator() {
    }

    /**
     * Specifies an {@link IXmlParserFactory} implementation that will be used to create the xml
     * parsers in the {@link XmlProcessorCreator}. Pass {@link DefaultSafeXmlParserFactory} to use default safe
     * factory that should prevent XML attacks like XML bombs and XXE attacks. This will definitely
     * throw an exception if the XXE object is present in the XML. Also it is configured to throw
     * an exception even in case of any DTD in XML file, but this option depends on the parser
     * implementation on your system, it may not work if your parser implementation
     * does not support the corresponding functionality. In this case declare your own
     * {@link IXmlParserFactory} implementation and pass it to this method.
     *
     * @param factory factory to be used to create xml parsers. If the passed factory is {@code null},
     *                the {@link DefaultSafeXmlParserFactory} will be used.
     */
    public static void setXmlParserFactory(IXmlParserFactory factory) {
        if (factory == null) {
            xmlParserFactory = new DefaultSafeXmlParserFactory();
        } else {
            xmlParserFactory = factory;
        }
    }

    /**
     * Creates {@link DocumentBuilder} instance.
     * The default implementation is configured to prevent
     * possible XML attacks (see {@link DefaultSafeXmlParserFactory}).
     * But you can use {@link XmlProcessorCreator#setXmlParserFactory} to set your specific
     * factory for creating xml parsers.
     *
     * @param namespaceAware   specifies whether the parser should be namespace aware
     * @param ignoringComments specifies whether the parser should ignore comments
     *
     * @return safe {@link DocumentBuilder} instance
     */
    public static DocumentBuilder createSafeDocumentBuilder(boolean namespaceAware, boolean ignoringComments) {
        return xmlParserFactory.createDocumentBuilderInstance(namespaceAware, ignoringComments);
    }

    /**
     * Creates {@link XMLReader} instance.
     * The default implementation is configured to prevent
     * possible XML attacks (see {@link DefaultSafeXmlParserFactory}).
     * But you can use {@link XmlProcessorCreator#setXmlParserFactory} to set your specific
     * factory for creating xml parsers.
     *
     * @param namespaceAware specifies whether the parser should be namespace aware
     * @param validating     specifies whether the parser should validate documents as they are parsed
     *
     * @return safe {@link XMLReader} instance
     */
    public static XMLReader createSafeXMLReader(boolean namespaceAware, boolean validating) {
        return xmlParserFactory.createXMLReaderInstance(namespaceAware, validating);
    }

    /**
     * Creates {@link Transformer} instance.
     * The default implementation is configured to prevent
     * possible XML attacks (see {@link DefaultSafeXmlParserFactory}).
     * But you can use {@link XmlProcessorCreator#setXmlParserFactory} to set your specific
     * factory for creating xml parsers.
     *
     * @return safe {@link Transformer} instance
     */
    public static Transformer createSafeTransformer() {
        return xmlParserFactory.createTransformerInstance();
    }
}
