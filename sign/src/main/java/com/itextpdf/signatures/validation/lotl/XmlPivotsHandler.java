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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.validation.lotl.xml.IDefaultXmlHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class XmlPivotsHandler implements IDefaultXmlHandler {
    private final List<String> pivots = new ArrayList<>();
    private boolean schemeInformationContext = false;
    private StringBuilder uriLink;

    public XmlPivotsHandler() {
        // Empty constructor.
    }

    @Override
    public void startElement(String uri, String localName, String qName, HashMap<String, String> attributes) {
        if (XmlTagConstants.SCHEME_INFORMATION_URI.equals(localName)) {
            schemeInformationContext = true;
        } else if (XmlTagConstants.URI.equals(localName)) {
            uriLink = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (XmlTagConstants.SCHEME_INFORMATION_URI.equals(localName)) {
            schemeInformationContext = false;
        } else if (XmlTagConstants.URI.equals(localName)) {
            String uriLinkString = uriLink.toString();
            if (isPivot(uriLinkString) || isOfficialJournal(uriLinkString)) {
                pivots.add(uriLinkString);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (schemeInformationContext) {
            uriLink.append(ch, start, length);
        }
    }

    public List<String> getPivots() {
        return new ArrayList<>(pivots);
    }

    static boolean isOfficialJournal(String uriLink) {
        return uriLink.contains("eur-lex.europa.eu");
    }

    private static boolean isPivot(String uriLink) {
        return uriLink.contains("eu-lotl-pivot");
    }
}
