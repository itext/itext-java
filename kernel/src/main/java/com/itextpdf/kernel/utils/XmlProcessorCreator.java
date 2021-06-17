/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.utils;

import javax.xml.parsers.DocumentBuilder;
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
}
