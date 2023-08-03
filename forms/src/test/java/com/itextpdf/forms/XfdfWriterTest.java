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
package com.itextpdf.forms;

import com.itextpdf.forms.exceptions.XfdfException;
import com.itextpdf.forms.xfdf.AnnotObject;
import com.itextpdf.forms.xfdf.AnnotsObject;
import com.itextpdf.forms.xfdf.AttributeObject;
import com.itextpdf.forms.xfdf.XfdfObject;
import com.itextpdf.forms.xfdf.XfdfObjectFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class XfdfWriterTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/XfdfWriterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/XfdfWriterTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleFormWithOneFieldTest()
            throws IOException, TransformerException, ParserConfigurationException, SAXException {
        String pdfDocumentName = "simpleFormWithOneField.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + pdfDocumentName)));
        String xfdfFilename = destinationFolder + "simpleFormWithOneField.xfdf";

        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "simpleFormWithOneField.xfdf",
                sourceFolder + "cmp_simpleFormWithOneField.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    @Ignore("DEVSIX-7264: Investigate 3 failed forms tests from 7.3/develop on .NET")
    public void simpleFormWithMultipleFieldsTest()
            throws IOException, TransformerException, ParserConfigurationException, SAXException {
        String pdfDocumentName = "simpleFormWithMultipleFields.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + pdfDocumentName)));
        String xfdfFilename = destinationFolder + "simpleFormWithMultipleFields.xfdf";

        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDoc, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDoc.close();

        if (!new CompareTool().compareXmls(destinationFolder + "simpleFormWithMultipleFields.xfdf",
                sourceFolder + "cmp_simpleFormWithMultipleFields.xfdf"))
            Assert.fail("Xfdf files are not equal");

    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfValueRichText()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String pdfDocumentName = "xfdfValueRichText.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + pdfDocumentName)));
        String xfdfFilename = destinationFolder + "xfdfValueRichText.xfdf";

        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDoc, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDoc.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfValueRichText.xfdf",
                sourceFolder + "cmp_xfdfValueRichText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }


    @Test
    public void xfdfHierarchyFields()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfHierarchyFields.pdf")));
        String xfdfFilename = destinationFolder + "xfdfHierarchyFields.xfdf";
        String pdfDocumentName = "xfdfHierarchyFields.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfHierarchyFields.xfdf",
                sourceFolder + "cmp_xfdfHierarchyFields.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfFreeText() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfFreeText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfFreeText.xfdf";
        String pdfDocumentName = "xfdfFreeText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfFreeText.xfdf",
                sourceFolder + "cmp_xfdfFreeText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfHighlightedText()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfHighlightedText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfHighlightedText.xfdf";
        String pdfDocumentName = "xfdfHighlightedText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfHighlightedText.xfdf",
                sourceFolder + "cmp_xfdfHighlightedText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfUnderlineText()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfUnderlineText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfUnderlineText.xfdf";
        String pdfDocumentName = "xfdfUnderlineText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfUnderlineText.xfdf",
                sourceFolder + "cmp_xfdfUnderlineText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfPopupNewFlags()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfPopupNewFlags.pdf")));
        String xfdfFilename = destinationFolder + "xfdfPopupNewFlags.xfdf";
        String pdfDocumentName = "xfdfPopupNewFlags.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfPopupNewFlags.xfdf",
                sourceFolder + "cmp_xfdfPopupNewFlags.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfStrikeout() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfStrikeout.pdf")));
        String xfdfFilename = destinationFolder + "xfdfStrikeout.xfdf";
        String pdfDocumentName = "xfdfStrikeout.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfStrikeout.xfdf",
                sourceFolder + "cmp_xfdfStrikeout.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfSquigglyText()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfSquigglyText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfSquigglyText.xfdf";
        String pdfDocumentName = "xfdfSquigglyText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSquigglyText.xfdf",
                sourceFolder + "cmp_xfdfSquigglyText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLine() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLine.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLine.xfdf";
        String pdfDocumentName = "xfdfLine.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLine.xfdf",
                sourceFolder + "cmp_xfdfLine.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfCircle() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCircle.pdf")));
        String xfdfFilename = destinationFolder + "xfdfCircle.xfdf";
        String pdfDocumentName = "xfdfCircle.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCircle.xfdf",
                sourceFolder + "cmp_xfdfCircle.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfSquare() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSquare.pdf")));
        String xfdfFilename = destinationFolder + "xfdfSquare.xfdf";
        String pdfDocumentName = "xfdfSquare.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSquare.xfdf",
                sourceFolder + "cmp_xfdfSquare.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfSquareAndCircleInteriorColor()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfSquareAndCircleInteriorColor.pdf")));
        String xfdfFilename = destinationFolder + "xfdfSquareAndCircleInteriorColor.xfdf";
        String pdfDocumentName = "xfdfSquareAndCircleInteriorColor.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSquareAndCircleInteriorColor.xfdf",
                sourceFolder + "cmp_xfdfSquareAndCircleInteriorColor.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfCaret() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCaret.pdf")));
        String xfdfFilename = destinationFolder + "xfdfCaret.xfdf";
        String pdfDocumentName = "xfdfCaret.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCaret.xfdf",
                sourceFolder + "cmp_xfdfCaret.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support annots
    public void xfdfPolygon() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfPolygon.pdf")));
        String xfdfFilename = destinationFolder + "xfdfPolygon.xfdf";
        String pdfDocumentName = "xfdfPolygon.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfPolygon.xfdf",
                sourceFolder + "cmp_xfdfPolygon.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support annots
    public void xfdfPolyline() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfPolyline.pdf")));
        String xfdfFilename = destinationFolder + "xfdfPolyline.xfdf";
        String pdfDocumentName = "xfdfPolyline.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfPolyline.xfdf",
                sourceFolder + "cmp_xfdfPolyline.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfStamp() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfStamp.pdf")));
        String xfdfFilename = destinationFolder + "xfdfStamp.xfdf";
        String pdfDocumentName = "xfdfStamp.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfStamp.xfdf",
                sourceFolder + "cmp_xfdfStamp.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfStampWithAppearance()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfStampWithAppearance.pdf")));
        String xfdfFilename = destinationFolder + "xfdfStampWithAppearance.xfdf";
        String pdfDocumentName = "xfdfStampWithAppearance.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfStampWithAppearance.xfdf",
                sourceFolder + "cmp_xfdfStampWithAppearance.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support annots
    public void xfdfInk() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfInk.pdf")));
        String xfdfFilename = destinationFolder + "xfdfInk.xfdf";
        String pdfDocumentName = "xfdfInk.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfInk.xfdf",
                sourceFolder + "cmp_xfdfInk.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfFileAttachment()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfFileAttachment.pdf")));
        String xfdfFilename = destinationFolder + "xfdfFileAttachment.xfdf";
        String pdfDocumentName = "xfdfFileAttachment.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfFileAttachment.xfdf",
                sourceFolder + "cmp_xfdfFileAttachment.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfSound() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSound.pdf")));
        String xfdfFilename = destinationFolder + "xfdfSound.xfdf";
        String pdfDocumentName = "xfdfSound.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSound.xfdf",
                sourceFolder + "cmp_xfdfSound.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLink() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLink.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLink.xfdf";
        String pdfDocumentName = "xfdfLink.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLink.xfdf",
                sourceFolder + "cmp_xfdfLink.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkBorderStyle()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkBorderStyle.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkBorderStyle.xfdf";
        String pdfDocumentName = "xfdfLinkBorderStyle.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkBorderStyle.xfdf",
                sourceFolder + "cmp_xfdfLinkBorderStyle.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDest() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDest.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDest.xfdf";
        String pdfDocumentName = "xfdfLinkDest.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDest.xfdf",
                sourceFolder + "cmp_xfdfLinkDest.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFit() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFit.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFit.xfdf";
        String pdfDocumentName = "xfdfLinkDestFit.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFit.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFit.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFitB()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitB.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFitB.xfdf";
        String pdfDocumentName = "xfdfLinkDestFitB.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFitB.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFitB.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFitR()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitR.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFitR.xfdf";
        String pdfDocumentName = "xfdfLinkDestFitR.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFitR.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFitR.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFitH()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitH.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFitH.xfdf";
        String pdfDocumentName = "xfdfLinkDestFitH.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFitH.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFitH.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFitBH()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitBH.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFitBH.xfdf";
        String pdfDocumentName = "xfdfLinkDestFitBH.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFitBH.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFitBH.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFitBV()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitBV.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFitBV.xfdf";
        String pdfDocumentName = "xfdfLinkDestFitBV.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFitBV.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFitBV.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkDestFitV()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitV.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkDestFitV.xfdf";
        String pdfDocumentName = "xfdfLinkDestFitV.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkDestFitV.xfdf",
                sourceFolder + "cmp_xfdfLinkDestFitV.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfRedact() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfRedact.pdf")));
        String xfdfFilename = destinationFolder + "xfdfRedact.xfdf";
        String pdfDocumentName = "xfdfRedact.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfRedact.xfdf",
                sourceFolder + "cmp_xfdfRedact.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfProjection() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfProjection.pdf")));
        String xfdfFilename = destinationFolder + "xfdfProjection.xfdf";
        String pdfDocumentName = "xfdfProjection.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfProjection.xfdf",
                sourceFolder + "cmp_xfdfProjection.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfLinkAllParams()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkAllParams.pdf")));
        String xfdfFilename = destinationFolder + "xfdfLinkAllParams.xfdf";
        String pdfDocumentName = "xfdfLinkAllParams.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfLinkAllParams.xfdf",
                sourceFolder + "cmp_xfdfLinkAllParams.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support caret annontation
    public void xfdfReplaceText() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfReplaceText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfReplaceText.xfdf";
        String pdfDocumentName = "xfdfReplaceText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReplaceText.xfdf",
                sourceFolder + "cmp_xfdfReplaceText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfArrow() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfArrow.pdf")));
        String xfdfFilename = destinationFolder + "xfdfArrow.xfdf";
        String pdfDocumentName = "xfdfArrow.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfArrow.xfdf",
                sourceFolder + "cmp_xfdfArrow.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfCallout() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCallout.pdf")));
        String xfdfFilename = destinationFolder + "xfdfCallout.xfdf";
        String pdfDocumentName = "xfdfCallout.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCallout.xfdf",
                sourceFolder + "cmp_xfdfCallout.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support annots
    public void xfdfCloud() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCloud.pdf")));
        String xfdfFilename = destinationFolder + "xfdfCloud.xfdf";
        String pdfDocumentName = "xfdfCloud.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCloud.xfdf",
                sourceFolder + "cmp_xfdfCloud.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support annots
    public void xfdfCloudNested() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfCloudNested.pdf")));
        String xfdfFilename = destinationFolder + "xfdfCloudNested.xfdf";
        String pdfDocumentName = "xfdfCloudNested.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCloudNested.xfdf",
                sourceFolder + "cmp_xfdfCloudNested.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215 Support richtext
    public void xfdfTextBoxAllParams()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfTextBoxAllParams.pdf")));
        String xfdfFilename = destinationFolder + "xfdfTextBoxAllParams.xfdf";
        String pdfDocumentName = "xfdfTextBoxAllParams.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfTextBoxAllParams.xfdf",
                sourceFolder + "cmp_xfdfTextBoxAllParams.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfJavaScriptForms()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfJavaScriptForms.pdf")));
        String xfdfFilename = destinationFolder + "xfdfJavaScriptForms.xfdf";
        String pdfDocumentName = "xfdfJavaScriptForms.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfJavaScriptForms.xfdf",
                sourceFolder + "cmp_xfdfJavaScriptForms.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfAttrColor() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfAttrColor.pdf")));
        String xfdfFilename = destinationFolder + "xfdfAttrColor.xfdf";
        String pdfDocumentName = "xfdfAttrColor.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfAttrColor.xfdf",
                sourceFolder + "cmp_xfdfAttrColor.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfAttrFlagsOpacity()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfAttrFlagsOpacity.pdf")));
        String xfdfFilename = destinationFolder + "xfdfAttrFlagsOpacity.xfdf";
        String pdfDocumentName = "xfdfAttrFlagsOpacity.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfAttrFlagsOpacity.xfdf",
                sourceFolder + "cmp_xfdfAttrFlagsOpacity.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfAttrTitle() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfAttrTitle.pdf")));
        String xfdfFilename = destinationFolder + "xfdfAttrTitle.xfdf";
        String pdfDocumentName = "xfdfAttrTitle.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfAttrTitle.xfdf",
                sourceFolder + "cmp_xfdfAttrTitle.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfReferenceFor3DMeasurement()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DMeasurement.pdf")));
        String xfdfFilename = destinationFolder + "xfdfReferenceFor3DMeasurement.xfdf";
        String pdfDocumentName = "xfdfReferenceFor3DMeasurement.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReferenceFor3DMeasurement.xfdf",
                sourceFolder + "cmp_xfdfReferenceFor3DMeasurement.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfReferenceFor3DAngular()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DAngular.pdf")));
        String xfdfFilename = destinationFolder + "xfdfReferenceFor3DAngular.xfdf";
        String pdfDocumentName = "xfdfReferenceFor3DAngular.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReferenceFor3DAngular.xfdf",
                sourceFolder + "cmp_xfdfReferenceFor3DAngular.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfReferenceFor3DRadial()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DRadial.pdf")));
        String xfdfFilename = destinationFolder + "xfdfReferenceFor3DRadial.xfdf";
        String pdfDocumentName = "xfdfReferenceFor3DRadial.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReferenceFor3DRadial.xfdf",
                sourceFolder + "cmp_xfdfReferenceFor3DRadial.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-3215
    public void xfdfSubelementContents()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfSubelementContents.pdf")));
        String xfdfFilename = destinationFolder + "xfdfSubelementContents.xfdf";
        String pdfDocumentName = "xfdfSubelementContents.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSubelementContents.xfdf",
                sourceFolder + "cmp_xfdfSubelementContents.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //check when Redact annotation is implemented
    //TODO DEVSIX-3215
    public void xfdfSubelementOverlayAppearance()
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfSubelementOverlayAppearance.pdf")));
        String xfdfFilename = destinationFolder + "xfdfSubelementOverlayAppearance.xfdf";
        String pdfDocumentName = "xfdfSubelementOverlayAppearance.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSubelementOverlayAppearance.xfdf",
                sourceFolder + "cmp_xfdfSubelementOverlayAppearance.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //Widget annotation is not supported in xfdf 2014 spec version
    //TODO  DEVSIX-3215
    public void xfdfButton() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfButton.pdf")));

        String xfdfFilename = destinationFolder + "xfdfButton.xfdf";
        String pdfDocumentName = "xfdfButton.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfButton.xfdf",
                sourceFolder + "cmp_xfdfButton.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //Widget annotation is not supported in xfdf 2014 spec version
    //TODO DEVSIX-3215
    public void xfdfCheckBox() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfCheckBox.pdf")));
        String xfdfFilename = destinationFolder + "xfdfCheckBox.xfdf";
        String pdfDocumentName = "xfdfCheckBox.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCheckBox.xfdf",
                sourceFolder + "cmp_xfdfCheckBox.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfList() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfList.pdf")));
        String xfdfFilename = destinationFolder + "xfdfList.xfdf";
        String pdfDocumentName = "xfdfList.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfList.xfdf",
                sourceFolder + "cmp_xfdfList.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //Widget annotation is not supported in 2014 spec version
    //TODO DEVSIX-3215
    public void xfdfDropDown() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + "xfdfDropDown.pdf")));
        String xfdfFilename = destinationFolder + "xfdfDropDown.xfdf";
        String pdfDocumentName = "xfdfDropDown.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfDropDown.xfdf",
                sourceFolder + "cmp_xfdfDropDown.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    public void xfdfEmptyAttributeTest() {
        XfdfObject xfdfObject = new XfdfObject();

        AnnotsObject annots = new AnnotsObject();
        xfdfObject.setAnnots(annots);

        AnnotObject annot = new AnnotObject();
        annots.addAnnot(annot);

        String namePresent = "name1";
        String nameAbsent = null;
        String valuePresent = "value";
        String valueAbsent = null;

        Exception e = Assert.assertThrows(XfdfException.class,
                () -> annot.addAttribute(new AttributeObject(nameAbsent, valuePresent))
        );
        Assert.assertEquals(XfdfException.ATTRIBUTE_NAME_OR_VALUE_MISSING, e.getMessage());
        Exception e2 = Assert.assertThrows(XfdfException.class,
                () -> annot.addAttribute(new AttributeObject(namePresent, valueAbsent))
        );
        Assert.assertEquals(XfdfException.ATTRIBUTE_NAME_OR_VALUE_MISSING, e2.getMessage());
    }

    @Test
    //TODO DEVSIX-7600 update xfdf and src files after supporting all the annotation types mentioned in xfdf spec
    public void xfdfAnnotationAttributesTest() throws IOException, ParserConfigurationException, SAXException,
            TransformerException {
        String pdfDocumentName = "xfdfAnnotationAttributes.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + pdfDocumentName)))) {
            String xfdfFilename = destinationFolder + "xfdfAnnotationAttributes.xfdf";
            XfdfObjectFactory factory = new XfdfObjectFactory();
            XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
            xfdfObject.writeToFile(xfdfFilename);
        }
        if (!new CompareTool().compareXmls(destinationFolder + "xfdfAnnotationAttributes.xfdf",
                sourceFolder + "xfdfAnnotationAttributes.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO DEVSIX-7600 update xfdf and src files after supporting all the annotation types mentioned in xfdf spec
    public void xfdfOnlyRequiredAnnotationAttributesTest() throws IOException, ParserConfigurationException, SAXException,
            TransformerException {
        String pdfDocumentName = "xfdfOnlyRequiredAnnotationAttributes.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new FileInputStream(sourceFolder + pdfDocumentName)))) {
            String xfdfFilename = destinationFolder + "xfdfOnlyRequiredAnnotationAttributes.xfdf";
            XfdfObjectFactory factory = new XfdfObjectFactory();
            XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
            xfdfObject.writeToFile(xfdfFilename);
        }
        if (!new CompareTool().compareXmls(destinationFolder + "xfdfOnlyRequiredAnnotationAttributes.xfdf",
                sourceFolder + "xfdfOnlyRequiredAnnotationAttributes.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }
}
