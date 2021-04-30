/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfEncryptionUnitTest extends ExtendedITextTest {
    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentCorrectEntryTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assert.assertTrue(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectEffTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.Identity);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assert.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectStmFTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assert.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectStrFTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.StdCF);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assert.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectCfTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.DefaultCryptFilter, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assert.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }
}
