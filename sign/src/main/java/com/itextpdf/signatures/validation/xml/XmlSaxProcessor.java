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
package com.itextpdf.signatures.validation.xml;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * This class provides an autoport independent SAX processor for XML documents.
 */
public class XmlSaxProcessor {

    /**
     * Processes the XML document from the given input stream using the provided handler.
     *
     * @param inputStream the input stream containing the XML document
     * @param handler     the handler to process the XML elements
     */
    public void process(InputStream inputStream, IDefaultXmlHandler handler) {
        XMLReader reader = XmlProcessorCreator.createSafeXMLReader(true, false);
        reader.setContentHandler(new HandlerProxy(handler));
        try {
            reader.parse(new InputSource(inputStream));
        } catch (IOException | SAXException e) {
            throw new PdfException(e);
        }
    }


    private static final class HandlerProxy extends DefaultHandler {
        private final IDefaultXmlHandler handler;

        private HandlerProxy(IDefaultXmlHandler handler) {
            this.handler = handler;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            HashMap<String, String> attributesMap = new HashMap<>();
            for (int i = 0; i < attributes.getLength(); i++) {
                attributesMap.put(attributes.getQName(i), attributes.getValue(i));
            }
            handler.startElement(uri, localName, qName, attributesMap);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            handler.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            handler.characters(ch, start, length);
        }
    }
}
