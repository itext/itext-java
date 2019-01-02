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
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class LinkTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LinkTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LinkTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void linkTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_linkTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);

        Link link = new Link("TestLink", action);
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void linkTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_linkTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.add(new AreaBreak()).add(new AreaBreak());

        PdfDestination dest = PdfExplicitDestination.createXYZ(pdfDoc.getPage(1), 36, 100, 1);
        PdfAction action = PdfAction.createGoTo(dest);

        Link link = new Link("TestLink", action);
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ACTION_WAS_SET_TO_LINK_ANNOTATION_WITH_DESTINATION)})
    public void linkTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_linkTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfArray array = new PdfArray();
        array.add(doc.getPdfDocument().addNewPage().getPdfObject());
        array.add(PdfName.XYZ);
        array.add(new PdfNumber(36));
        array.add(new PdfNumber(100));
        array.add(new PdfNumber(1));

        PdfDestination dest = PdfDestination.makeDestination(array);

        Link link = new Link("TestLink", dest);
        link.setAction(PdfAction.createURI("http://itextpdf.com/", false));
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderedLinkTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderedLinkTest.pdf";
        String cmpFileName = sourceFolder + "cmp_borderedLinkTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        Link link = new Link("Link with orange border", PdfAction.createURI("http://itextpdf.com"));
        link.setBorder(new SolidBorder(ColorConstants.ORANGE, 5));
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * <a href="http://stackoverflow.com/questions/34408764/create-local-link-in-rotated-pdfpcell-in-itextsharp">
     * Stack overflow: Create local link in rotated PdfPCell in iTextSharp
     * </a>
     * <p>
     * This is the equivalent Java code for iText 7 of the C# code for iTextSharp 5
     * in the question.
     * </p>
     * Author: mkl.
     */
    @Test
    public void testCreateLocalLinkInRotatedCell() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkInRotatedCell.pdf";
        String cmpFileName = sourceFolder + "cmp_linkInRotatedCell.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));

        Link chunk = new Link("Click here", PdfAction.createURI("http://itextpdf.com/"));
        table.addCell(new Cell().add(new Paragraph().add(chunk)).setRotationAngle(Math.PI / 2));

        chunk = new Link("Click here 2", PdfAction.createURI("http://itextpdf.com/"));
        table.addCell(new Paragraph().add(chunk));

        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rotatedLinkAtFixedPosition() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLinkAtFixedPosition.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLinkAtFixedPosition.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);

        Link link = new Link("TestLink", action);
        doc.add(new Paragraph(link).setMargin(0).setRotationAngle(Math.PI / 4).setFixedPosition(300, 623, 100));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void rotatedLinkInnerRotation() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLinkInnerRotation.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLinkInnerRotation.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);

        Link link = new Link("TestLink", action);
        Paragraph p = new Paragraph(link).setRotationAngle(Math.PI / 4).setBackgroundColor(ColorConstants.RED);
        Div div = new Div().add(p).setRotationAngle(Math.PI / 3).setBackgroundColor(ColorConstants.BLUE);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void simpleMarginsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "simpleMarginsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_simpleMarginsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);
        Link link = new Link("TestLink", action);
        link.setBorder(new SolidBorder(ColorConstants.BLUE, 20));
        link.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(50));
        link.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(50));

        doc.add(new Paragraph(link).setBorder(new SolidBorder(10)));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multiLineLinkTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multiLineLinkTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_multiLineLinkTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);
        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
                "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
                "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate " +
                "velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                "culpa qui officia deserunt mollit anim id est laborum.";
        Link link = new Link(text, action);
        doc.add(new Paragraph(link));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableHeaderLinkTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableHeaderLinkTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_tableHeaderLinkTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);
        int numCols = 3;
        int numRows = 24;
        Table table = new Table(numCols);
        for (int x = 0; x < numCols; x++) {
            Cell headerCell = new Cell();
            String cellContent = "Header cell\n" + (x + 1);
            Link link = new Link(cellContent, action);
            link.setFontColor(ColorConstants.BLUE);
            headerCell.add(new Paragraph().add(link));
            table.addHeaderCell(headerCell);
        }

        for (int x = 0; x < numRows; x++) {
            table.addCell(new Cell().setHeight(100f).add(new Paragraph("Content cell " + (x + 1))));
        }
        doc.add(table);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

}
