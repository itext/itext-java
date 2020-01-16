/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.forms;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfAcroFormTest extends ExtendedITextTest {

    @Test
    public void setSignatureFlagsTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        acroForm.setSignatureFlags(65);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject sigFlags = acroForm.getPdfObject().get(PdfName.SigFlags);
        outputDoc.close();

        Assert.assertEquals(new PdfNumber(65), sigFlags);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setCalculationOrderTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        PdfArray calculationOrderArray = new PdfArray(new int[] {1, 0});
        acroForm.setCalculationOrder(calculationOrderArray);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject calculationOrder = acroForm.getPdfObject().get(PdfName.CO);
        outputDoc.close();

        Assert.assertEquals(calculationOrderArray, calculationOrder);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setDefaultAppearanceTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        acroForm.setDefaultAppearance("default appearance");

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject calculationOrder = acroForm.getPdfObject().get(PdfName.DA);
        outputDoc.close();

        Assert.assertEquals(new PdfString("default appearance"), calculationOrder);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setDefaultJustificationTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        acroForm.setDefaultJustification(14);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject defaultJustification = acroForm.getPdfObject().get(PdfName.Q);
        outputDoc.close();

        Assert.assertEquals(new PdfNumber(14), defaultJustification);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setDefaultResourcesTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);

        PdfDictionary dictionary = new PdfDictionary();
        PdfAcroForm.getAcroForm(outputDoc, true).setDefaultResources(dictionary);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject defaultResourcesDict = acroForm.getPdfObject().get(PdfName.DR);
        outputDoc.close();

        Assert.assertEquals(dictionary, defaultResourcesDict);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setNeedAppearancesTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        acroForm.setNeedAppearances(false);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject needAppearance = acroForm.getPdfObject().get(PdfName.NeedAppearances);

        outputDoc.close();

        Assert.assertEquals(new PdfBoolean(false), needAppearance);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "NeedAppearances has been deprecated in PDF 2.0. Appearance streams are required in PDF 2.0."))
    public void setNeedAppearancesInPdf2Test() {
        PdfDocument outputDoc = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(
                        PdfVersion.PDF_2_0)));
        outputDoc.addNewPage();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        acroForm.setNeedAppearances(false);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject needAppearance = acroForm.getPdfObject().get(PdfName.NeedAppearances);

        outputDoc.close();

        Assert.assertNull(needAppearance);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setGenerateAppearanceTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        acroForm.setNeedAppearances(false);
        acroForm.setGenerateAppearance(true);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        boolean isGenerateAppearance = acroForm.isGenerateAppearance();
        Object needAppearances = acroForm.getPdfObject().get(PdfName.NeedAppearances);
        outputDoc.close();

        Assert.assertNull(needAppearances);
        Assert.assertTrue(isGenerateAppearance);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setXFAResourcePdfArrayTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        PdfArray array = new PdfArray();
        acroForm.setXFAResource(array);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject xfaObject = acroForm.getPdfObject().get(PdfName.XFA);
        outputDoc.close();

        Assert.assertEquals(array, xfaObject);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setXFAResourcePdfStreamTest() {
        PdfDocument outputDoc = createDocument();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
        PdfStream stream = new PdfStream();
        acroForm.setXFAResource(stream);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject xfaObject = acroForm.getPdfObject().get(PdfName.XFA);
        outputDoc.close();

        Assert.assertEquals(stream, xfaObject);
        Assert.assertTrue(isModified);
        Assert.assertTrue(isReleaseForbidden);
    }

    private static PdfDocument createDocument() {
        PdfDocument outputDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        outputDoc.addNewPage();
        return outputDoc;
    }
}
