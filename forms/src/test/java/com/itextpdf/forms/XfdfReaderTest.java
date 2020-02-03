/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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

import com.itextpdf.forms.xfdf.XfdfObject;
import com.itextpdf.forms.xfdf.XfdfObjectFactory;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@Category(IntegrationTest.class)
public class XfdfReaderTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/XfdfReaderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/XfdfReaderTest/";


    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfNoFields() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfNoFields.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfNoFields.pdf")));
        String xfdfFilename = sourceFolder + "xfdfNoFields.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfNoFields.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfNoFields.pdf",
                sourceFolder + "cmp_xfdfNoFields.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_NO_F_OBJECT_TO_COMPARE))
    public void xfdfNoFieldsNoFAttributes() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfNoFieldsNoFAttributes.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfNoFieldsNoFAttributes.pdf")));
        String xfdfFilename = sourceFolder + "xfdfNoFieldsNoFAttributes.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfNoFieldsNoFAttributes.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfNoFieldsNoFAttributes.pdf",
                sourceFolder + "cmp_xfdfNoFieldsNoFAttributes.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfNoFieldsNoIdsAttributes() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfNoFieldsNoIdsAttributes.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfNoFieldsNoIdsAttributes.pdf")));
        String xfdfFilename = sourceFolder + "xfdfNoFieldsNoIdsAttributes.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfNoFieldsNoIdsAttributes.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfNoFieldsNoIdsAttributes.pdf",
                sourceFolder + "cmp_xfdfNoFieldsNoIdsAttributes.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfWithFieldsWithValue() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfWithFieldsWithValue.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfWithFieldsWithValue.pdf")));
        String xfdfFilename = sourceFolder + "xfdfWithFieldsWithValue.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfWithFieldsWithValue.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfWithFieldsWithValue.pdf",
                sourceFolder + "cmp_xfdfWithFieldsWithValue.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_NO_SUCH_FIELD_IN_PDF_DOCUMENT)
    })
    //TODO DEVSIX-3215 Support annots
    public void xfdfValueRichText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfValueRichText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfValueRichText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfValueRichText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfValueRichText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfValueRichText.pdf",
                sourceFolder + "cmp_xfdfValueRichText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_NO_SUCH_FIELD_IN_PDF_DOCUMENT, count = 3)
    })
    public void xfdfHierarchyFieldsTest() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "hierarchy_fields.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "hierarchy_fields.pdf")));
        String xfdfFilename = sourceFolder + "hierarchy_fields.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "hierarchy_fields.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "hierarchy_fields.pdf",
                sourceFolder + "cmp_hierarchy_fields.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_NO_SUCH_FIELD_IN_PDF_DOCUMENT, count = 3)
    })
    public void xfdfWithFieldsWithValueParentAndChild() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfWithFieldsWithValueParentAndChild.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfWithFieldsWithValueParentAndChild.pdf")));
        String xfdfFilename = sourceFolder + "xfdfWithFieldsWithValueParentAndChild.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfWithFieldsWithValueParentAndChild.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfWithFieldsWithValueParentAndChild.pdf",
                sourceFolder + "cmp_xfdfWithFieldsWithValueParentAndChild.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE)})
    public void xfdfAnnotationHighlightedText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationHighlightedText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationHighlightedText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationHighlightedText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, "xfdfAnnotationHighlightedText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationHighlightedText.pdf",
                sourceFolder + "cmp_xfdfAnnotationHighlightedText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationUnderlineText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationUnderlineText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationUnderlineText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationUnderlineText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationUnderlineText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationUnderlineText.pdf",
                sourceFolder + "cmp_xfdfAnnotationUnderlineText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationUnderlineTextRectWithTwoCoords() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationUnderlineTextRectWithTwoCoords.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationUnderlineTextRectWithTwoCoords.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationUnderlineTextRectWithTwoCoords.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationUnderlineTextRectWithTwoCoords.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationUnderlineTextRectWithTwoCoords.pdf",
                sourceFolder + "cmp_xfdfAnnotationUnderlineTextRectWithTwoCoords.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationUnderlinePopupAllFlags() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationUnderlinePopupAllFlags.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationUnderlinePopupAllFlags.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationUnderlinePopupAllFlags.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationUnderlinePopupAllFlags.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationUnderlinePopupAllFlags.pdf",
                sourceFolder + "cmp_xfdfAnnotationUnderlinePopupAllFlags.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationText.pdf",
                sourceFolder + "cmp_xfdfAnnotationText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationStrikeout() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationStrikeout.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationStrikeout.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationStrikeout.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationStrikeout.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationStrikeout.pdf",
                sourceFolder + "cmp_xfdfAnnotationStrikeout.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationSquigglyText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationSquigglyText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationSquigglyText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationSquigglyText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationSquigglyText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationSquigglyText.pdf",
                sourceFolder + "cmp_xfdfAnnotationSquigglyText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE, count = 2),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_ANNOTATION_IS_NOT_SUPPORTED)
    })
    public void xfdfAnnotationLine() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationLine.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationLine.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationLine.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationLine.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationLine.pdf",
                sourceFolder + "cmp_xfdfAnnotationLine.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationCircle() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationCircle.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationCircle.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationCircle.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationCircle.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationCircle.pdf",
                sourceFolder + "cmp_xfdfAnnotationCircle.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationSquare() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationSquare.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationSquare.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationSquare.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationSquare.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationSquare.pdf",
                sourceFolder + "cmp_xfdfAnnotationSquare.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationCaret() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationCaret.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationCaret.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationCaret.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationCaret.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationCaret.pdf",
                sourceFolder + "cmp_xfdfAnnotationCaret.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationPolygon() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationPolygon.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationPolygon.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationPolygon.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationPolygon.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationPolygon.pdf",
                sourceFolder + "cmp_xfdfAnnotationPolygon.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationPolyline() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationPolyline.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationPolyline.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationPolyline.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationPolyline.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationPolyline.pdf",
                sourceFolder + "cmp_xfdfAnnotationPolyline.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationStamp() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationStamp.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationStamp.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationStamp.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationStamp.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationStamp.pdf",
                sourceFolder + "cmp_xfdfAnnotationStamp.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationInk() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationInk.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationInk.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationInk.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationInk.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationInk.pdf",
                sourceFolder + "cmp_xfdfAnnotationInk.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationFreeText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationFreeText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationFreeText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationFreeText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationFreeText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationFreeText.pdf",
                sourceFolder + "cmp_xfdfAnnotationFreeText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationFileAttachment() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationFileAttachment.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationFileAttachment.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationFileAttachment.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationFileAttachment.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationFileAttachment.pdf",
                sourceFolder + "cmp_xfdfAnnotationFileAttachment.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationSound() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationSound.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationSound.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationSound.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationSound.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationSound.pdf",
                sourceFolder + "cmp_xfdfAnnotationSound.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationLink() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationLink.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationLink.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationLink.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationLink.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationLink.pdf",
                sourceFolder + "cmp_xfdfAnnotationLink.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationRedact() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationRedact.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationRedact.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationRedact.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationRedact.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationRedact.pdf",
                sourceFolder + "cmp_xfdfAnnotationRedact.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationProjection() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationProjection.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationProjection.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationProjection.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationProjection.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationProjection.pdf",
                sourceFolder + "cmp_xfdfAnnotationProjection.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationLinkAllParams() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationLinkAllParams.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationLinkAllParams.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationLinkAllParams.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationLinkAllParams.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationLinkAllParams.pdf",
                sourceFolder + "cmp_xfdfAnnotationLinkAllParams.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE)
    })
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationReplaceText() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationReplaceText.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationReplaceText.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationReplaceText.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationReplaceText.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationReplaceText.pdf",
                sourceFolder + "cmp_xfdfAnnotationReplaceText.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE, count = 5),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_ANNOTATION_IS_NOT_SUPPORTED)
    })
    public void xfdfAnnotationArrow() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationArrow.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationArrow.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationArrow.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationArrow.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationArrow.pdf",
                sourceFolder + "cmp_xfdfAnnotationArrow.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationCallout() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationCallout.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationCallout.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationCallout.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationCallout.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationCallout.pdf",
                sourceFolder + "cmp_xfdfAnnotationCallout.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE, count = 3)
    })
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationCloud() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationCloud.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationCloud.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationCloud.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationCloud.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationCloud.pdf",
                sourceFolder + "cmp_xfdfAnnotationCloud.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE, count = 3)
    })
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationCloudNested() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationCloudNested.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationCloudNested.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationCloudNested.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationCloudNested.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationCloudNested.pdf",
                sourceFolder + "cmp_xfdfAnnotationCloudNested.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationTextBoxAllParams() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationTextBoxAllParams.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationTextBoxAllParams.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationTextBoxAllParams.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationTextBoxAllParams.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationTextBoxAllParams.pdf",
                sourceFolder + "cmp_xfdfAnnotationTextBoxAllParams.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfJavaScriptForms() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfJavaScriptForms.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfJavaScriptForms.pdf")));
        String xfdfFilename = sourceFolder + "xfdfJavaScriptForms.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfJavaScriptForms.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfJavaScriptForms.pdf",
                sourceFolder + "cmp_xfdfJavaScriptForms.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfFormsFieldParams() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfFormsFieldParams.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfFormsFieldParams.pdf")));
        String xfdfFilename = sourceFolder + "xfdfFormsFieldParams.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfFormsFieldParams.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfFormsFieldParams.pdf",
                sourceFolder + "cmp_xfdfFormsFieldParams.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationAttrColor() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationAttrColor.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationAttrColor.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationAttrColor.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationAttrColor.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationAttrColor.pdf",
                sourceFolder + "cmp_xfdfAnnotationAttrColor.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    public void xfdfAnnotationAttrFlagsOpacity() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationAttrFlagsOpacity.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationAttrFlagsOpacity.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationAttrFlagsOpacity.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationAttrFlagsOpacity.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationAttrFlagsOpacity.pdf",
                sourceFolder + "cmp_xfdfAnnotationAttrFlagsOpacity.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT),
            @LogMessage(messageTemplate = LogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE, count = 4)
    })
    //TODO DEVSIX-3215 Support annots
    public void xfdfAnnotationAttrTitle() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfAnnotationAttrTitle.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfAnnotationAttrTitle.pdf")));
        String xfdfFilename = sourceFolder + "xfdfAnnotationAttrTitle.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfAnnotationAttrTitle.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfAnnotationAttrTitle.pdf",
                sourceFolder + "cmp_xfdfAnnotationAttrTitle.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfReferenceFor3DMeasurement() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DMeasurement.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfReferenceFor3DMeasurement.pdf")));
        String xfdfFilename = sourceFolder + "xfdfReferenceFor3DMeasurement.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfReferenceFor3DMeasurement.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfReferenceFor3DMeasurement.pdf",
                sourceFolder + "cmp_xfdfReferenceFor3DMeasurement.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfReferenceFor3DAngular() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DAngular.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfReferenceFor3DAngular.pdf")));
        String xfdfFilename = sourceFolder + "xfdfReferenceFor3DAngular.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfReferenceFor3DAngular.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfReferenceFor3DAngular.pdf",
                sourceFolder + "cmp_xfdfReferenceFor3DAngular.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfReferenceFor3DRadial() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfReferenceFor3DRadial.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfReferenceFor3DRadial.pdf")));
        String xfdfFilename = sourceFolder + "xfdfReferenceFor3DRadial.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfReferenceFor3DRadial.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfReferenceFor3DRadial.pdf",
                sourceFolder + "cmp_xfdfReferenceFor3DRadial.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215
    public void xfdfSubelementContents() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSubelementContents.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfSubelementContents.pdf")));
        String xfdfFilename = sourceFolder + "xfdfSubelementContents.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfSubelementContents.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfSubelementContents.pdf",
                sourceFolder + "cmp_xfdfSubelementContents.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support annots
    public void xfdfSubelementOverlayAppearance() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfSubelementOverlayAppearance.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfSubelementOverlayAppearance.pdf")));
        String xfdfFilename = sourceFolder + "xfdfSubelementOverlayAppearance.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfSubelementOverlayAppearance.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfSubelementOverlayAppearance.pdf",
                sourceFolder + "cmp_xfdfSubelementOverlayAppearance.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215
    public void xfdfButton() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfButton.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfButton.pdf")));
        String xfdfFilename = sourceFolder + "xfdfButton.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfButton.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfButton.pdf",
                sourceFolder + "cmp_xfdfButton.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215
    public void xfdfCheckBox() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfCheckBox.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfCheckBox.pdf")));
        String xfdfFilename = sourceFolder + "xfdfCheckBox.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfCheckBox.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfCheckBox.pdf",
                sourceFolder + "cmp_xfdfCheckBox.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215
    public void xfdfList() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfList.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfList.pdf")));
        String xfdfFilename = sourceFolder + "xfdfList.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfList.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfList.pdf",
                sourceFolder + "cmp_xfdfList.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT))
    //TODO DEVSIX-3215 Support richtext
    public void xfdfDropDown() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "xfdfDropDown.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "xfdfDropDown.pdf")));
        String xfdfFilename = sourceFolder + "xfdfDropDown.xfdf";
        XfdfObjectFactory factory = new XfdfObjectFactory();
        XfdfObject xfdfObject = factory.createXfdfObject(new FileInputStream(xfdfFilename));
        xfdfObject.mergeToPdf(pdfDocument, sourceFolder + "xfdfDropDown.pdf");
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "xfdfDropDown.pdf",
                sourceFolder + "cmp_xfdfDropDown.pdf", destinationFolder, "diff_"));
    }

}
