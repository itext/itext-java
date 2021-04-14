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
