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

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/lotl/XmlCertificateRetrieverTest/";


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