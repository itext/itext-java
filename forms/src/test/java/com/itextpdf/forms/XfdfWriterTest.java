/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.forms;

import com.itextpdf.forms.xfdf.AnnotObject;
import com.itextpdf.forms.xfdf.AnnotsObject;
import com.itextpdf.forms.xfdf.AttributeObject;
import com.itextpdf.forms.xfdf.XfdfConstants;
import com.itextpdf.forms.xfdf.XfdfException;
import com.itextpdf.forms.xfdf.XfdfObject;
import com.itextpdf.forms.xfdf.XfdfObjectFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class XfdfWriterTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/XfdfWriterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/XfdfWriterTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();


    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    public void simpleDocWithoutFormTest() throws IOException {
        String fileName = "simpleDocWithoutForm.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + fileName));

        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDoc, fileName);
    }

    @Test
    public void simpleFormWithOneFieldTest() throws IOException, TransformerException, ParserConfigurationException, SAXException {
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

    //@Test
    public void simpleFormWithMultipleFieldsTest() throws IOException, TransformerException, ParserConfigurationException, SAXException {
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

//    @Test
//    public void simpleDocSquareCircleAnnotationsTest() throws IOException {
//        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "simpleDocSquareCircleAnnotations.pdf"));
//
//        XfdfWriter writer = new XfdfWriter();
//        XfdfObject xfdfObject = writer.generateAnnotationsXfdfObjectFromPdfDocument(pdfDoc);
//
//        //TODO add support for button?
//        //TODO add support for radioButton - the same as form hierarchy fields?
//        //TODO use xfdfwriter to create xfdf file and compare it with the one made by acrobat
//
//        /*Assert.assertTrue(fields.size() == 6);
//        Assert.assertTrue(field.getFieldName().toUnicodeString().equals("Text1"));
//        Assert.assertTrue(field.getValue().toString().equals("TestField"));*/
//    }

//    @Test
//    public void generateCircleAndSquareAnnotationsPdf() throws FileNotFoundException {
//        PdfDocument document = new PdfDocument(new PdfWriter(new FileOutputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations.pdf")));
//
//        PdfPage page = document.addNewPage();
//
//        PdfSquareAnnotation square = new PdfSquareAnnotation(new Rectangle(100, 700, 100, 100));
//        square.setInteriorColor(new float[]{1, 0, 0}).setColor(new float[]{0, 1, 0}).setContents("RED Square");
//        page.addAnnotation(square);
//        PdfCircleAnnotation circle = new PdfCircleAnnotation(new Rectangle(300, 700, 100, 100));
//        circle.setInteriorColor(new float[]{0, 1, 0}).setColor(new float[]{0, 0, 1}).setContents(new PdfString("GREEN Circle"));
//        page.addAnnotation(circle);
//        page.flush();
//
//        document.close();
//    }

//    @Test
//    public void generateCircleAndSquareAnnotationsXfdf() throws IOException{
//        PdfDocument document = new PdfDocument(new PdfReader(new FileInputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations.pdf")),
//                new PdfWriter(new FileOutputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations1.pdf")));
//
///*
//        PdfPage page = document.getPage(0);
//        List<PdfAnnotation> annotationList = page.getAnnotations();
//
//
//*/
//        PdfPage page = document.addNewPage();
//
//        PdfSquareAnnotation square = new PdfSquareAnnotation(new Rectangle(100, 700, 100, 100));
//        square.setInteriorColor(new float[]{1, 0, 0}).setColor(new float[]{0, 1, 0}).setContents("RED Square");
//        page.addAnnotation(square);
//        PdfCircleAnnotation circle = new PdfCircleAnnotation(new Rectangle(300, 700, 100, 100));
//        circle.setInteriorColor(new float[]{0, 1, 0}).setColor(new float[]{0, 0, 1}).setContents(new PdfString("GREEN Circle"));
//        page.addAnnotation(circle);
//        page.flush();
//
//        XfdfWriter writer = new XfdfWriter();
//        XfdfObject xfdfObject = writer.generateAnnotationsXfdfObjectFromPdfDocument(document);
//        document.close();
//    }
//
//    @Test
//    public void generateSimpleTextAnnotationsPdf() throws IOException{
//        PdfDocument document = new PdfDocument(new PdfReader(new FileInputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations.pdf")),
//                new PdfWriter(new FileOutputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations1.pdf")));
//
//        PdfPage page = document.getPage(0);
//        List<PdfAnnotation> annotationList = page.getAnnotations();
//
//        XfdfWriter writer = new XfdfWriter();
//        XfdfObject xfdfObject = writer.generateAnnotationsXfdfObjectFromPdfDocument(document);
//        document.close();
//    }
//
//    @Test
//    public void generateSimpleTextAnnotationsXfdf() throws IOException{
//        PdfDocument document = new PdfDocument(new PdfReader(new FileInputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations.pdf")),
//                new PdfWriter(new FileOutputStream("C:\\Users\\User\\itext7\\java\\itextcore\\forms\\target\\test\\com\\itextpdf\\forms\\PdfFormFieldTest\\squareandcircleannotations1.pdf")));
//
//        PdfPage page = document.getPage(0);
//        List<PdfAnnotation> annotationList = page.getAnnotations();
//
//        XfdfWriter writer = new XfdfWriter();
//        XfdfObject xfdfObject = writer.generateAnnotationsXfdfObjectFromPdfDocument(document);
//        document.close();
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfPdfRichText() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfValueRichText.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfValueRichText.xfdf";
//        XfdfWriter writer = new XfdfWriter(xfdfFilename);
//        writer.write(pdfDocument, "xfdfValueRichText.pdf");
//
//        pdfDocument.close();
//
//        if(!new CompareTool().compareXmls(destinationFolder + "xfdfValueRichText.xfdf",
//                sourceFolder + "cmp_xfdfValueRichText.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }


    @Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfHierarchyFields() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfHierarchyFields.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfFreeText() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfFreeText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfFreeText.xfdf";
        String pdfDocumentName = "xfdfFreeText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

        if(!new CompareTool().compareXmls(destinationFolder + "xfdfFreeText.xfdf",
                sourceFolder + "cmp_xfdfFreeText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }

    @Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfHighlightedText() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfHighlightedText.pdf")));
        String xfdfFilename = destinationFolder + "xfdfHighlightedText.xfdf";
        String pdfDocumentName = "xfdfHighlightedText.pdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
        xfdfObject.writeToFile(xfdfFilename);

        pdfDocument.close();

//        XfdfObjectUtils.preprocessXfdf(destinationFolder, "xfdfHighlightedText.xfdf",
//                sourceFolder,"cmp_xfdfHighlightedText.xfdf", "contents", "contents-richtext");
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfHighlightedText_preprocessed.xfdf",
//                sourceFolder + "cmp_xfdfHighlightedText_preprocessed.xfdf"))
//            Assert.fail("Xfdf files are not equal");

        if (!new CompareTool().compareXmls(destinationFolder + "xfdfHighlightedText.xfdf",
                sourceFolder + "cmp_xfdfHighlightedText.xfdf"))
            Assert.fail("Xfdf files are not equal");
    }
    @Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfUnderlineText() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfUnderlineText.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfPopupNewFlags() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfPopupNewFlags.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfStrikeout() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfStrikeout.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfSquigglyText() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSquigglyText.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix.
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
    //TODO some tags and attributes are missed. Check after fix.
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
    //TODO some tags and attributes are missed. Check after fix.
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
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfSquareAndCircleInteriorColor() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSquareAndCircleInteriorColor.pdf")));
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

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfCaret() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCaret.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfCaret.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//       XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfCaret.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCaret.xfdf",
//                sourceFolder + "cmp_xfdfCaret.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfPolygon() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfPolygon.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfPolygon.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfPolygon.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfPolygon.xfdf",
//                sourceFolder + "cmp_xfdfPolygon.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfPolyline() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfPolyline.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfPolyline.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfPolyline.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfPolyline.xfdf",
//                sourceFolder + "cmp_xfdfPolyline.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    @Test
    //TODO some tags and attributes are missed. Check after fix.
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
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfStampWithAppearance() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfStampWithAppearance.pdf")));
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

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfInk() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfInk.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfInk.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfInk.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfInk.xfdf",
//                sourceFolder + "cmp_xfdfInk.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfFileAttachment() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfFileAttachment.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfFileAttachment.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfFileAttachment.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfFileAttachment.xfdf",
//                sourceFolder + "cmp_xfdfFileAttachment.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfSound() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSound.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfSound.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfSound.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSound.xfdf",
//                sourceFolder + "cmp_xfdfSound.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    @Test
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkBorderStyle() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkBorderStyle.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDest() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDest.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFit() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFit.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFitB() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitB.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFitR() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitR.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFitH() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitH.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFitBH() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitBH.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFitBV() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitBV.pdf")));
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
    //TODO some tags and attributes are missed. Check after fix. Replace cmp file.
    public void xfdfLinkDestFitV() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkDestFitV.pdf")));
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

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfRedact() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfRedact.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfRedact.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfRedact.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfRedact.xfdf",
//                sourceFolder + "cmp_xfdfRedact.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO Null pointer exception. Check after fix.
//    public void xfdfProjection() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfProjection.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfProjection.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfProjection.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfProjection.xfdf",
//                sourceFolder + "cmp_xfdfProjection.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    @Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfLinkAllParams() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfLinkAllParams.pdf")));
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

//    @Test
//    //TODO check after caret annotation is implemented
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfReplaceText() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReplaceText.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfReplaceText.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//       XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfReplaceText.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReplaceText.xfdf",
//                sourceFolder + "cmp_xfdfReplaceText.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    //@Test
    //TODO some tags and attributes are missed. Check after fix.
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

    //@Test
    //TODO some tags and attributes are missed. Check after fix.
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

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfCloud() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCloud.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfCloud.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfCloud.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCloud.xfdf",
//                sourceFolder + "cmp_xfdfCloud.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfCloudNested() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCloudNested.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfCloudNested.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfCloudNested.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCloudNested.xfdf",
//                sourceFolder + "cmp_xfdfCloudNested.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    //@Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfTextBoxAllParams() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfTextBoxAllParams.pdf")));
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

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfJavaScriptForms() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfJavaScriptForms.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfJavaScriptForms.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfJavaScriptForms.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfJavaScriptForms.xfdf",
//                sourceFolder + "cmp_xfdfJavaScriptForms.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //Widget annotation is not supported in 2014 spec version
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfFormsFieldParams() throws IOException, ParserConfigurationException, SAXException, TransformerException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfFormsFieldParams.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfFormsFieldParams.xfdf";
//        String pdfDocumentName = "xfdfFormsFieldParams.pdf";
//        XfdfObjectFactory factory = new XfdfObjectFactory();
//        XfdfObject xfdfObject = factory.createXfdfObject(pdfDocument, pdfDocumentName);
//        XfdfWriter writer = new XfdfWriter(xfdfFilename);
//        writer.write(xfdfObject);
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfFormsFieldParams.xfdf",
//                sourceFolder + "cmp_xfdfFormsFieldParams.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    //@Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfAttrColor() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAttrColor.pdf")));
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

    //@Test
    //TODO Null pointer exception. Check after fix.
    public void xfdfAttrFlagsOpacity() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAttrFlagsOpacity.pdf")));
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

    //@Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfAttrTitle() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAttrTitle.pdf")));
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

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfReferenceFor3DMeasurement() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DMeasurement.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfReferenceFor3DMeasurement.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//       XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfReferenceFor3DMeasurement.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReferenceFor3DMeasurement.xfdf",
//                sourceFolder + "cmp_xfdfReferenceFor3DMeasurement.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfReferenceFor3DAngular() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DAngular.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfReferenceFor3DAngular.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
 //       XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfReferenceFor3DAngular.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReferenceFor3DAngular.xfdf",
//                sourceFolder + "cmp_xfdfReferenceFor3DAngular.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }
//
//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfReferenceFor3DRadial() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DRadial.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfReferenceFor3DRadial.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfReferenceFor3DRadial.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfReferenceFor3DRadial.xfdf",
//                sourceFolder + "cmp_xfdfReferenceFor3DRadial.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    //@Test
    //TODO some tags and attributes are missed. Check after fix.
    public void xfdfSubelementContents() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSubelementContents.pdf")));
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

//    @Test
//    //check when Redact annotation is implemented
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfSubelementOverlayAppearance() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSubelementOverlayAppearance.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfSubelementOverlayAppearance.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfSubelementOverlayAppearance.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfSubelementOverlayAppearance.xfdf",
//                sourceFolder + "cmp_xfdfSubelementOverlayAppearance.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //Widget annotation is not supported in xfdf 2014 spec version
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfButton() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfButton.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfButton.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfButton.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfButton.xfdf",
//                sourceFolder + "cmp_xfdfButton.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //Widget annotation is not supported in xfdf 2014 spec version
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfCheckBox() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCheckBox.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfCheckBox.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//       XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfCheckBox.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfCheckBox.xfdf",
//                sourceFolder + "cmp_xfdfCheckBox.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

//    @Test
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfList() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfList.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfList.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfList.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfList.xfdf",
//                sourceFolder + "cmp_xfdfList.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }
//
//    @Test
//    //Widget annotation is not supported in 2014 spec version
//    //TODO some tags and attributes are missed. Check after fix.
//    public void xfdfDropDown() throws IOException, ParserConfigurationException, SAXException {
//        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfDropDown.pdf")));
//        String xfdfFilename = destinationFolder + "xfdfDropDown.xfdf";
//         OutputStream out = new FileOutputStream(xfdfFilename);
//        XfdfWriter writer = new XfdfWriter(out);
//        writer.write(pdfDocument, "xfdfDropDown.pdf");
//
//        pdfDocument.close();
//
//        if (!new CompareTool().compareXmls(destinationFolder + "xfdfDropDown.xfdf",
//                sourceFolder + "cmp_xfdfDropDown.xfdf"))
//            Assert.fail("Xfdf files are not equal");
//    }

    @Test
    public void xfdfEmptyAttributeTst() {

        junitExpectedException.expect(XfdfException.class);
        junitExpectedException.expectMessage(XfdfConstants.ATTRIBUTE_NAME_OR_VALUE_MISSING);

        XfdfObject xfdfObject = new XfdfObject();

        AnnotsObject annots = new AnnotsObject();
        xfdfObject.setAnnots(annots);

        AnnotObject annot = new AnnotObject();
        annots.addAnnot(annot);

        String namePresent = "name1";
        String nameAbsent = null;
        String valuePresent = "value";
        String valueAbsent = null;

        annot.addAttribute(new AttributeObject(nameAbsent, valuePresent));
        annot.addAttribute(new AttributeObject(namePresent, valueAbsent));

    }

}
