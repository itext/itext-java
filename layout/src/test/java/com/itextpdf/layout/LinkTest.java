package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
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

        PdfArray array = new PdfArray();
        array.add(doc.getPdfDocument().getPage(1).getPdfObject());
        array.add(PdfName.XYZ);
        array.add(new PdfNumber(36));
        array.add(new PdfNumber(100));
        array.add(new PdfNumber(1));

        PdfDestination dest = PdfDestination.makeDestination(array);

        PdfAction action = PdfAction.createGoTo(dest);

        Link link = new Link("TestLink", action);
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
        link.setBorder(new SolidBorder(Color.ORANGE, 5));
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
    public void testCreateLocalLinkInRotatedCell () throws IOException , InterruptedException {
        String outFileName = destinationFolder + "linkInRotatedCell.pdf" ;
        String cmpFileName = sourceFolder + "cmp_linkInRotatedCell.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName)) ;
        Document document = new Document(pdfDocument);
        Table table = new Table(2 );

        Link chunk = new Link("Click here" , PdfAction.createURI("http://itextpdf.com/"));
        table.addCell( new Cell().add(new Paragraph().add(chunk)).setRotationAngle(Math. PI / 2 ));

        chunk = new Link("Click here 2" , PdfAction.createURI ("http://itextpdf.com/" ));
        table.addCell( new Paragraph().add(chunk));

        document.add(table) ;
        document.close() ;

        Assert. assertNull( new CompareTool().compareByContent(outFileName, cmpFileName , destinationFolder , "diff")) ;
    }

    @Test
    public void rotatedLinkAtFixedPosition() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLinkAtFixedPosition.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLinkAtFixedPosition.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);

        Link link = new Link("TestLink", action);
        doc.add(new Paragraph(link).setRotationAngle(Math.PI / 4).setFixedPosition(300, 623, 100));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rotatedLinkInnerRotation() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLinkInnerRotation.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLinkInnerRotation.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);

        Link link = new Link("TestLink", action);
        Paragraph p = new Paragraph(link).setRotationAngle(Math.PI / 4).setBackgroundColor(Color.RED);
        Div div = new Div().add(p).setRotationAngle(Math.PI / 3).setBackgroundColor(Color.BLUE);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
