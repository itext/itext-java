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
package com.itextpdf.layout;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
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
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LinkTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LinkTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LinkTest/";

    private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam nec condimentum odio. Duis sed ipsum semper, imperdiet risus sit amet, pellentesque leo. Proin eget libero quis orci sagittis efficitur et a justo. Phasellus ac ipsum id lacus fermentum malesuada. Morbi vulputate ultricies ligula a pretium. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Etiam eget leo maximus velit placerat condimentum. Nulla in fermentum ex, in fermentum risus. Phasellus gravida ante sit amet magna porta fermentum. Nunc nec urna quis enim facilisis scelerisque. Praesent risus est, efficitur eget quam nec, dignissim mollis nunc. Mauris in sodales nulla.\n"
            + "Sed sodales pharetra sapien, eget tristique magna fringilla at. Quisque ligula eros, auctor sit amet varius a, tincidunt non mauris. Sed diam mi, dignissim id magna accumsan, viverra scelerisque risus. Etiam blandit condimentum quam non bibendum. Sed vehicula justo quis lectus consequat, sit amet tempor sem mollis. Sed turpis nibh, luctus in arcu mattis, consequat laoreet est. Integer tempor, ante a gravida efficitur, velit libero dapibus nibh, et scelerisque diam nulla a orci. Vestibulum eleifend rutrum elit, sed pellentesque arcu lacinia nec. Nam semper, velit eget rhoncus efficitur, odio libero molestie mi, ut eleifend libero purus ut ex. Quisque hendrerit vehicula hendrerit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam quis elit eu dolor pellentesque viverra non eget purus. Nam nisi erat, efficitur sed malesuada ut, ornare sit amet risus. Nunc eu vestibulum turpis.\n"
            + "Duis ultricies et dui nec pharetra. Cras sagittis felis risus, vel vulputate diam blandit non. Vestibulum sed neque quis massa rutrum luctus. Nulla vitae leo ornare, elementum dolor sit amet, fringilla enim. Vestibulum efficitur, diam in molestie tincidunt, tellus purus ultricies nisl, ut bibendum purus augue et mi. Mauris eget leo aliquam metus egestas dapibus eget sit amet risus. Cras eget felis porttitor, ornare est congue, venenatis ipsum. Suspendisse accumsan eget elit efficitur malesuada. Quisque porttitor efficitur lorem in placerat. Nunc sit amet mattis ante. Vestibulum eget quam et ex tempus iaculis. Duis pharetra posuere erat, vitae imperdiet ipsum lacinia in. Aenean nunc quam, consectetur vel nibh sit amet, sollicitudin porta purus.\n"
            + "Curabitur non nunc in libero pretium dictum rutrum at lorem. Suspendisse nec magna id libero bibendum porta. Nullam urna tellus, ornare nec massa quis, fringilla fermentum leo. Vestibulum ac velit pulvinar ex feugiat varius vel eu nunc. Mauris vitae purus porttitor, sagittis elit eu, volutpat quam. Nunc mattis pretium arcu, vitae pellentesque mauris tincidunt vitae. Proin congue sem eget commodo pulvinar. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer eu augue tortor. Vestibulum porta enim eget neque semper scelerisque. Nulla et enim ac nulla luctus viverra sed nec risus. Aliquam blandit, lorem non consectetur auctor, ex ipsum blandit ipsum, ut faucibus orci sem non odio. Nulla ut condimentum ante. Proin dignissim risus vitae arcu tristique, ac ultricies lacus lobortis. Aliquam sodales orci justo, vitae imperdiet elit volutpat id. Nullam vitae interdum erat.\n"
            + "Donec fringilla sapien sed neque finibus, non luctus justo lobortis. Praesent commodo pellentesque ligula, vel fringilla odio commodo id. Nam ultrices justo a dignissim congue. Nullam imperdiet sem eget placerat aliquam. Suspendisse non faucibus libero. Aenean purus arcu, auctor vitae tincidunt in, tincidunt at ante. Pellentesque euismod, velit vel vulputate faucibus, dolor erat consectetur sapien, ut elementum dui turpis nec lacus. In hac habitasse platea dictumst. Aenean vel elit ultrices, varius mi quis, congue erat."
            + "Curabitur sit amet nunc porttitor, congue elit vestibulum, vestibulum sapien. Fusce ut arcu consequat, scelerisque sapien vitae, dignissim ligula. Duis gravida mollis volutpat. Maecenas condimentum pulvinar urna in cursus. Nulla ornare est non tellus elementum auctor. Mauris ornare, elit non ornare lobortis, risus augue consectetur orci, ac efficitur ex nunc nec leo. Aenean dictum mattis magna vitae bibendum.";

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
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.ACTION_WAS_SET_TO_LINK_ANNOTATION_WITH_DESTINATION)})
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
     * This is the equivalent Java code for iText of the C# code for iTextSharp 5
     * in the question.
     * <p>
     * @author mkl
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
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
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

    @Test
    public void linkWithCustomRectangleTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkWithCustomRectangleTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_linkWithCustomRectangleTest01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        String text = "Hello World";

        PdfAction action = PdfAction.createURI("http://itextpdf.com");

        PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(1, 1)).setAction(action);

        Link linkByAnnotation = new Link(text, annotation);
        doc.add(new Paragraph(linkByAnnotation));

        annotation.setRectangle(new PdfArray(new Rectangle(100, 100, 20, 20)));
        Link linkByChangedAnnotation = new Link(text, annotation);
        doc.add(new Paragraph(linkByChangedAnnotation));

        Link linkByAction = new Link(text, action);
        doc.add(new Paragraph(linkByAction));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void splitLinkTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "splitLinkTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_splitLinkTest01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        PdfAction action = PdfAction.createURI("http://itextpdf.com");
        PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(1, 1)).setAction(action);
        Link linkByAnnotation = new Link(LONG_TEXT, annotation);


        doc.add(new Div().setHeight(700).setBackgroundColor(ColorConstants.RED));

        // This paragraph is so long that it will be present on the first, second and third pages
        doc.add(new Paragraph(linkByAnnotation));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void linkAnnotationOnDivSplitTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkAnnotationOnDivSplitTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_linkAnnotationOnDivSplitTest01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        PdfAction action = PdfAction.createURI("http://itextpdf.com");
        PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(1, 1)).setAction(action);

        Div div = new Div()
                .setHeight(2000)
                .setBackgroundColor(ColorConstants.RED);

        div.setProperty(Property.LINK_ANNOTATION, annotation);

        doc.add(div);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void linkActionOnDivSplitTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linkActionOnDivSplitTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_linkActionOnDivSplitTest01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        PdfAction action = PdfAction.createURI("http://itextpdf.com");

        Div div = new Div()
                .setHeight(2000)
                .setBackgroundColor(ColorConstants.RED);

        div.setAction(action);

        doc.add(div);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

}
