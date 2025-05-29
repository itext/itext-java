package com.itextpdf.signatures;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

class XmlCertificateHandler extends DefaultHandler {

    private static final String CERTIFICATE_TAG = "X509Certificate";

    private static final String SIGNATURE_CERTIFICATE_TAG = "ds:X509Certificate";

    private boolean isReadingCertificate = false;

    private StringBuilder certificateByteBuilder;

    List<byte[]> certificateBytes = new ArrayList<>();

    XmlCertificateHandler() {
        //empty constructor
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (CERTIFICATE_TAG.equalsIgnoreCase(qName) || SIGNATURE_CERTIFICATE_TAG.equalsIgnoreCase(qName)) {
            isReadingCertificate = true;
            certificateByteBuilder = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (CERTIFICATE_TAG.equalsIgnoreCase(qName) || SIGNATURE_CERTIFICATE_TAG.equalsIgnoreCase(qName)) {
            certificateBytes.add(Base64.getDecoder().decode(certificateByteBuilder.toString()));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (isReadingCertificate) {
            certificateByteBuilder.append(ch, start, length);
        }
    }

    public List<byte[]> getCertificatesBytes() {
        return certificateBytes;
    }
}