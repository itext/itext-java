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
