package com.itextpdf.signatures.validation;

import com.itextpdf.signatures.validation.xml.IDefaultXmlHandler;
import com.itextpdf.signatures.validation.xml.XmlSaxProcessor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * @return a list of CountrySpecificLotl objects, each containing the scheme territory and TSL location.
     */
    public List<CountrySpecificLotl> getAllCountriesLotlFilesLocation(InputStream data) {
        TSLLocationExtractor tslLocationExtractor = new TSLLocationExtractor();
        new XmlSaxProcessor().process(data, tslLocationExtractor);
        return tslLocationExtractor.tslLocations;
    }

    /**
     * This class represents a country-specific TSL (Trusted List) location.
     * It contains the scheme territory and the TSL location URL.
     */
    public static final class CountrySpecificLotl {

        private final String schemeTerritory;
        private final String tslLocation;
        private final String mimeType;


        CountrySpecificLotl(String schemeTerritory, String tslLocation, String mimeType) {
            this.schemeTerritory = schemeTerritory;
            this.tslLocation = tslLocation;
            this.mimeType = mimeType;
        }

        /**
         * Returns the scheme territory of this country-specific TSL.
         *
         * @return The scheme territory
         */
        public String getSchemeTerritory() {
            return schemeTerritory;
        }

        /**
         * Returns the TSL location URL of this country-specific TSL.
         *
         * @return The TSL location URL
         */
        public String getTslLocation() {
            return tslLocation;
        }

        /**
         * Returns the MIME type of the TSL location.
         *
         * @return The MIME type of the TSL location
         */
        public String getMimeType() {
            return mimeType;
        }

        @Override
        public String toString() {
            return "CountrySpecificLotl{" + "schemeTerritory='" +
                    schemeTerritory + '\'' + ", tslLocation='" + tslLocation + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    '}';
        }
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

        private static boolean isXmlLink(CountrySpecificLotl data) {
            return MIME_TYPE_ETSI_TSL.equals(data.getMimeType());
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
    }
}
