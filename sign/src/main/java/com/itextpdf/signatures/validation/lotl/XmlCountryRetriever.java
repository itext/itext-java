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
import com.itextpdf.signatures.validation.lotl.xml.XmlSaxProcessor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to retrieve the list of countries and their corresponding TSL (Trusted List) locations
 * from an XML file.
 */
final class XmlCountryRetriever {
    /**
     * Default constructor for XmlCountryRetriever.
     * Creates an instance of {@code XmlCountryRetriever}.
     */
    public XmlCountryRetriever() {
        // Empty constructor
    }

    /**
     * This method reads an XML file containing country-specific TSL locations and returns a list of
     * {@code CountrySpecificLotl} objects.
     *
     * @param data the InputStream of the XML data containing country-specific TSL locations
     *
     * @return a list of CountrySpecificLotl objects, each containing the scheme territory and TSL location.
     */
    public List<CountrySpecificLotl> getAllCountriesLotlFilesLocation(InputStream data,
            LotlFetchingProperties lotlFetchingProperties) {
        TSLLocationExtractor tslLocationExtractor = new TSLLocationExtractor();
        new XmlSaxProcessor().process(data, tslLocationExtractor);

        List<CountrySpecificLotl> countrySpecificLotls = tslLocationExtractor.tslLocations;

        // Ignored country specific Lotl files which were not requested.
        return countrySpecificLotls.stream().filter(countrySpecificLotl ->
                        lotlFetchingProperties.shouldProcessCountry(countrySpecificLotl.getSchemeTerritory()))
                .collect(Collectors.toList());

    }

    private static final class TSLLocationExtractor implements IDefaultXmlHandler {
        private static final String MIME_TYPE_ETSI_TSL = "application/vnd.etsi.tsl+xml";
        final List<CountrySpecificLotl> tslLocations = new ArrayList<>();
        String parsingState = null;
        String schemeTerritory = null;
        String tslLocation = null;
        String mimeType = null;

        TSLLocationExtractor() {
            //Empty constructor
        }

        @Override
        public void startElement(String uri, String localName, String qName, HashMap<String, String> attributes) {
            parsingState = localName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (XmlTagConstants.OTHER_TSL_POINTER.equals(localName)) {
                CountrySpecificLotl data = new CountrySpecificLotl(schemeTerritory, tslLocation, mimeType);
                if (isXmlLink(data)) {
                    tslLocations.add(data);
                }
                resetState();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (parsingState == null) {
                return;
            }
            String value = new String(ch, start, length).trim();
            if (value.isEmpty()) {
                return;
            }
            switch (parsingState) {
                case XmlTagConstants.SCHEME_TERRITORY:
                    schemeTerritory = value;
                    break;
                case XmlTagConstants.TSLLOCATION:
                    tslLocation = value;
                    break;
                case XmlTagConstants.MIME_TYPE:
                    mimeType = value;
                    break;
                default:
            }
        }

        private void resetState() {
            schemeTerritory = null;
            tslLocation = null;
            parsingState = null;
            mimeType = null;
        }

        private static boolean isXmlLink(CountrySpecificLotl data) {
            return MIME_TYPE_ETSI_TSL.equals(data.getMimeType());
        }
    }
}
