package com.itextpdf.signatures.validation;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@Tag("IntegrationTest")
class XmlCountryRetrieverTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation" +
            "/XmlCertificateRetrieverTest/";


    @Test
    public void readLotlCertificatesTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "eu-lotl.xml";
        XmlCountryRetriever xmlCountryRetriever = new XmlCountryRetriever();
        List<XmlCountryRetriever.CountrySpecificLotl> otherCountryList =
                xmlCountryRetriever.getAllCountriesLotlFilesLocation(
                        Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(32, otherCountryList.size());

        for (XmlCountryRetriever.CountrySpecificLotl countrySpecificLotl : otherCountryList) {
            Assertions.assertNotNull(countrySpecificLotl.getSchemeTerritory(),
                    "Scheme territory should not be null for country: " +
                            countrySpecificLotl.getTslLocation());
            Assertions.assertNotNull(countrySpecificLotl.getTslLocation(),
                    "TSL location should not be null for country: " +
                            countrySpecificLotl.getSchemeTerritory());
        }
    }

}