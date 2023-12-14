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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfA1CheckerTest extends ExtendedITextTest {

    private PdfA1Checker pdfA1Checker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B);

    @Before
    public void before() {
        pdfA1Checker.setFullCheckMode(true);
    }

    @Test
    public void checkCatalogDictionaryWithoutAAEntry() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.AA, new PdfDictionary());

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY, e.getMessage());
    }

    @Test
    public void checkCatalogDictionaryWithoutOCPropertiesEntry() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, new PdfDictionary());

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_OCPROPERTIES_KEY, e.getMessage());
    }

    @Test
    public void checkCatalogDictionaryWithoutEmbeddedFiles() {
        PdfDictionary names = new PdfDictionary();
        names.put(PdfName.EmbeddedFiles, new PdfDictionary());

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Names, names);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkCatalogValidEntries(catalog)
        );
        Assert.assertEquals(PdfAConformanceException.A_NAME_DICTIONARY_SHALL_NOT_CONTAIN_THE_EMBEDDED_FILES_KEY, e.getMessage());
    }

    @Test
    public void checkValidCatalog() {
        pdfA1Checker.checkCatalogValidEntries(new PdfDictionary());

        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }
}
