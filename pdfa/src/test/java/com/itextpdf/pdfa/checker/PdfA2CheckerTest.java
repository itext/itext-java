/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfA2CheckerTest extends ExtendedITextTest {
    private PdfA2Checker pdfA2Checker = new PdfA2Checker(PdfAConformanceLevel.PDF_A_2B);

    @Test
    public void checkNameEntryShouldBeUniqueBetweenDefaultAndAdditionalConfigs() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES,
                e.getMessage());
    }

    @Test
    public void checkNameEntryShouldBeUniqueBetweenAdditionalConfigs() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES,
                e.getMessage());
    }

    @Test
    public void checkOCCDContainName() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName2"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY_SHALL_CONTAIN_NAME_ENTRY,
                e.getMessage());
    }

    @Test
    public void checkOrderArrayDoesNotContainRedundantReferences()  {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);


        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS, e.getMessage());
    }

    @Test
    public void checkOrderArrayContainsReferencesToAllOCGs() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);
        ocgs.add(orderItem1);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);

        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS, e.getMessage());
    }

    @Test
    public void checkOrderArrayAndOCGsMatch() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        PdfDictionary orderItem2 = new PdfDictionary();
        orderItem2.put(PdfName.Name, new PdfString("CustomName4"));
        ocgs.add(orderItem2);
        PdfDictionary orderItem3 = new PdfDictionary();
        orderItem3.put(PdfName.Name, new PdfString("CustomName5"));
        ocgs.add(orderItem3);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);

        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS, e.getMessage());
    }

    @Test
    public void checkAbsenceOfOptionalConfigEntryAllowed() {
        PdfDictionary ocProperties = new PdfDictionary();

        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));

        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        ocProperties.put(PdfName.OCGs, ocgs);
        ocProperties.put(PdfName.D, d);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        pdfA2Checker.checkCatalogValidEntries(catalog);

        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }

    @Test
    public void checkAbsenceOfOptionalOrderEntryAllowed() {
        PdfDictionary ocProperties = new PdfDictionary();

        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));

        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        PdfArray configs = new PdfArray();

        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));

        configs.add(config);

        ocProperties.put(PdfName.OCGs, ocgs);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        pdfA2Checker.checkCatalogValidEntries(catalog);


        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }

    @Test
    public void checkCatalogDictionaryWithoutAlternatePresentations() {
        PdfDictionary names = new PdfDictionary();
        names.put(PdfName.AlternatePresentations, new PdfDictionary());

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Names, names);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATEPRESENTATIONS_NAMES_ENTRY,
                e.getMessage());
    }

    @Test
    public void checkCatalogDictionaryWithoutRequirements() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Requirements, new PdfDictionary());

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_REQUIREMENTS_ENTRY, e.getMessage());
    }

    @Test
    public void deviceNColorspaceNoAttributesDictionary() {
        int numberOfComponents = 2;
        List<String> tmpArray = new ArrayList<String>(numberOfComponents);
        float[] transformArray = new float[numberOfComponents * 2];

        for (int i = 0; i < numberOfComponents; i++) {
            tmpArray.add("MyColor" + i + 1);
            transformArray[i * 2] = 0;
            transformArray[i * 2 + 1]  = 1;
        }
        com.itextpdf.kernel.pdf.function.PdfFunction.Type4 function = new com.itextpdf.kernel.pdf.function.PdfFunction.Type4
                (new PdfArray(transformArray), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0}".getBytes(StandardCharsets.ISO_8859_1));

        PdfDictionary currentColorSpaces = new PdfDictionary();

        //TODO DEVSIX-4203 should not cause an IndexOutOfBoundException.
        // Should throw PdfAConformanceException as Colorants dictionary always must be present
        // for Pdf/A-2
        Assert.assertThrows(RuntimeException.class,
                () -> pdfA2Checker.checkColorSpace(new PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function),
                        currentColorSpaces, true, false)
        );
    }
}
