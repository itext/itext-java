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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.XmlUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
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
        DocumentBuilderFactory factory = createDocumentBuilderFactory();
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
        SAXParserFactory factory = createSAXParserFactory();
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

    @Override
    public Transformer createTransformerInstance() {
        TransformerFactory factory = TransformerFactory.newInstance();
        configureSafeTransformerFactory(factory);
        Transformer transformer;
        try {
            transformer = factory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new PdfException(e.getMessage(), e);
        }
        return transformer;
    }

    /**
     * Creates a document builder factory implementation.
     *
     * @return result of {@link DocumentBuilderFactory#newInstance()} call
     */
    protected DocumentBuilderFactory createDocumentBuilderFactory() {
        return XmlUtil.getDocumentBuilderFactory();
    }

    /**
     * Creates a SAX parser factory implementation.
     *
     * @return result of {@link SAXParserFactory#newInstance()} call
     */
    protected SAXParserFactory createSAXParserFactory() {
        return XmlUtil.createSAXParserFactory();
    }

    /**
     * Configures document builder factory to make it secure against xml attacks.
     *
     * @param factory {@link DocumentBuilderFactory} instance to be configured
     */
    protected void configureSafeDocumentBuilderFactory(DocumentBuilderFactory factory) {
        tryToSetFeature(factory, DISALLOW_DOCTYPE_DECL, true);
        tryToSetFeature(factory, EXTERNAL_GENERAL_ENTITIES, false);
        tryToSetFeature(factory, EXTERNAL_PARAMETER_ENTITIES, false);
        tryToSetFeature(factory, LOAD_EXTERNAL_DTD, false);
        // recommendations from Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
    }

    /**
     * Configures SAX parser factory to make it secure against xml attacks.
     *
     * @param factory {@link SAXParserFactory} instance to be configured
     */
    protected void configureSafeSAXParserFactory(SAXParserFactory factory) {
        tryToSetFeature(factory, DISALLOW_DOCTYPE_DECL, true);
        tryToSetFeature(factory, EXTERNAL_GENERAL_ENTITIES, false);
        tryToSetFeature(factory, EXTERNAL_PARAMETER_ENTITIES, false);
        tryToSetFeature(factory, LOAD_EXTERNAL_DTD, false);
        // recommendations from Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
        factory.setXIncludeAware(false);
    }

    /**
     * Configures transformer factory to make it secure against xml attacks.
     *
     * @param factory {@link TransformerFactory} instance to be configured
     */
    protected void configureSafeTransformerFactory(TransformerFactory factory) {
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Android-Conversion-Skip-Line (android XMLConstants doesn't have such property)
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, ""); // Android-Conversion-Skip-Line (android XMLConstants doesn't have such property)
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
            throw new PdfException(KernelExceptionMessageConstant.EXTERNAL_ENTITY_ELEMENT_FOUND_IN_XML);
        }
    }
}
