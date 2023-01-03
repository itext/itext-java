/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
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

    @Test
    public void checkSignatureTest() {
        PdfDictionary dict = new PdfDictionary();
        pdfA1Checker.checkSignature(dict);
        Assert.assertTrue(pdfA1Checker.objectIsChecked(dict));
    }
}
