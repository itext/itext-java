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

public class XmlSaxProcessor {

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
