/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.PdfException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Implementation of {@link IXmlParserFactory} for creating safe xml parser objects.
 * Creates parsers with configuration to prevent XML bombs and XXE attacks.
 */
public class DefaultSafeXmlParserFactory implements IXmlParserFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSafeXmlParserFactory.class);

    /**
     * Feature for disallowing DOCTYPE declaration.
     *
     * <p>
     * Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
     */
    private final static String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * If you can't disable DOCTYPE declarations, then at least disable external entities.
     * Must be used with the {@link DefaultSafeXmlParserFactory#EXTERNAL_PARAMETER_ENTITIES}, otherwise has no effect.
     *
     * <p>
     * Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
     * Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
     * JDK7+ - http://xml.org/sax/features/external-general-entities
     */
    private final static String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

    /**
     * Must be used with the {@link DefaultSafeXmlParserFactory#EXTERNAL_GENERAL_ENTITIES}, otherwise has no effect.
     *
     * <p>
     * Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
     * Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
     * JDK7+ - http://xml.org/sax/features/external-parameter-entities
     */
    private final static String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    /**
     * Disable external DTDs.
     */
    private final static String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    /**
     * Creates instance of {@link DefaultSafeXmlParserFactory}.
     */
    public DefaultSafeXmlParserFactory() {
        // empty constructor
    }

    @Override
    public DocumentBuilder createDocumentBuilderInstance(boolean namespaceAware, boolean ignoringComments) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        configureSafeDocumentBuilderFactory(factory);
        factory.setNamespaceAware(namespaceAware);
        factory.setIgnoringComments(ignoringComments);
        DocumentBuilder db;
        try {
            db = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new PdfException(e.getMessage(), e);
        }
        db.setEntityResolver(new SafeEmptyEntityResolver());
        return db;
    }

    @Override
    public XMLReader createXMLReaderInstance(boolean namespaceAware, boolean validating) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        factory.setValidating(validating);
        configureSafeSAXParserFactory(factory);
        XMLReader xmlReader;
        try {
            SAXParser saxParser = factory.newSAXParser();
            xmlReader = saxParser.getXMLReader();
        } catch (ParserConfigurationException | SAXException e) {
            throw new PdfException(e.getMessage(), e);
        }
        xmlReader.setEntityResolver(new SafeEmptyEntityResolver());
        return xmlReader;
    }

    private void configureSafeDocumentBuilderFactory(DocumentBuilderFactory factory) {
        tryToSetFeature(factory, DISALLOW_DOCTYPE_DECL, true);
        tryToSetFeature(factory, EXTERNAL_GENERAL_ENTITIES, false);
        tryToSetFeature(factory, EXTERNAL_PARAMETER_ENTITIES, false);
        tryToSetFeature(factory, LOAD_EXTERNAL_DTD, false);
        // recommendations from Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
    }

    private void configureSafeSAXParserFactory(SAXParserFactory factory) {
        tryToSetFeature(factory, DISALLOW_DOCTYPE_DECL, true);
        tryToSetFeature(factory, EXTERNAL_GENERAL_ENTITIES, false);
        tryToSetFeature(factory, EXTERNAL_PARAMETER_ENTITIES, false);
        tryToSetFeature(factory, LOAD_EXTERNAL_DTD, false);
        // recommendations from Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
        factory.setXIncludeAware(false);
    }

    private void tryToSetFeature(DocumentBuilderFactory factory, String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        } catch (ParserConfigurationException e) {
            LOGGER.info(MessageFormatUtil
                    .format(KernelLogMessageConstant.FEATURE_IS_NOT_SUPPORTED, e.getMessage(), feature));
        }
    }

    private void tryToSetFeature(SAXParserFactory factory, String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
            LOGGER.info(MessageFormatUtil
                    .format(KernelLogMessageConstant.FEATURE_IS_NOT_SUPPORTED, e.getMessage(), feature));
        }
    }

    // Prevents XXE attacks
    private static class SafeEmptyEntityResolver implements EntityResolver {
        public SafeEmptyEntityResolver() {
            // empty constructor
        }

        public InputSource resolveEntity(String publicId, String systemId) {
            throw new PdfException(PdfException.ExternalEntityElementFoundInXml);
        }
    }
}
