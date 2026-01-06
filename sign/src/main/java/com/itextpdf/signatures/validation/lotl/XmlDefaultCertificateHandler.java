/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.CertificateUtil;

import java.security.cert.Certificate;
import java.util.HashMap;

class XmlDefaultCertificateHandler extends AbstractXmlCertificateHandler {
    private StringBuilder information;

    XmlDefaultCertificateHandler() {
        //empty constructor
    }

    @Override
    public void startElement(String uri, String localName, String qName, HashMap<String, String> attributes) {
        if (XmlTagConstants.X509CERTIFICATE.equals(localName)) {
            information = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (XmlTagConstants.X509CERTIFICATE.equals(localName)) {
            Certificate certificate = CertificateUtil.createCertificateFromEncodedData(
                    removeWhitespacesAndBreakLines(information.toString()));
            serviceContextList.add(new SimpleServiceContext(certificate));
        }
        information = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (information != null) {
            information.append(ch, start, length);
        }
    }

    private static String removeWhitespacesAndBreakLines(String data) {
        return data.replace(" ", "").replace("\n", "");
    }
}
