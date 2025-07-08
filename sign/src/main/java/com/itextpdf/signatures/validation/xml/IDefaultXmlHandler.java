package com.itextpdf.signatures.validation.xml;

import java.util.HashMap;

public interface IDefaultXmlHandler {

    void startElement(String uri, String localName, String qName, HashMap<String, String> attributes);

    void endElement(String uri, String localName, String qName);

    void characters(char[] ch, int start, int length);


}
