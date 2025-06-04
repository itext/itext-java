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