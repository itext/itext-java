package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfWinTest extends ExtendedITextTest {

    @Test
    public void checkDictionaryConstructorTest() {
        final String somePath = "C:\\some\\path\\some-app.exe";
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.F, new PdfString(somePath));

        PdfWin win = new PdfWin(dict);
        PdfDictionary pdfWinObj = win.getPdfObject();

        Assert.assertEquals(1, pdfWinObj.size());
        Assert.assertEquals(somePath, pdfWinObj.getAsString(PdfName.F).toString());
    }

    @Test
    public void checkSingleParamConstructorTest() {
        final String somePath = "C:\\some\\path\\some-app.exe";

        PdfWin win = new PdfWin(new PdfString(somePath));
        PdfDictionary pdfWinObj = win.getPdfObject();

        Assert.assertEquals(1, pdfWinObj.size());
        Assert.assertEquals(somePath, pdfWinObj.getAsString(PdfName.F).toString());
    }

    @Test
    public void checkMultipleParamConstructorTest() {
        final String somePath = "C:\\some\\path\\some-app.exe";
        final String defaultDirectory = "C:\\temp";
        final String operation = "open";
        final String parameter = "param";

        PdfWin win = new PdfWin(new PdfString(somePath), new PdfString(defaultDirectory),
                new PdfString(operation), new PdfString(parameter));

        PdfDictionary pdfWinObj = win.getPdfObject();

        Assert.assertEquals(4, pdfWinObj.size());
        Assert.assertEquals(somePath, pdfWinObj.getAsString(PdfName.F).toString());
        Assert.assertEquals(defaultDirectory, pdfWinObj.getAsString(PdfName.D).toString());
        Assert.assertEquals(operation, pdfWinObj.getAsString(PdfName.O).toString());
        Assert.assertEquals(parameter, pdfWinObj.getAsString(PdfName.P).toString());
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        final String somePath = "C:\\some\\path\\some-app.exe";

        PdfWin win = new PdfWin(new PdfString(somePath));

        Assert.assertFalse(win.isWrappedObjectMustBeIndirect());
    }
}
