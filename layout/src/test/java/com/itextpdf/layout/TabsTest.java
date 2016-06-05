package com.itextpdf.layout;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TabsTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TabTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TabTest/";

    private static final String text0 = "The Po\u017Eega Valley is a geographic microregion\tof Croatia, located in central" +
            " Slavonia, enveloped by the Slavonian mountains. It consists of\tsouthern slopes of 984-metre (3,228 ft)" +
            " Psunj, 953-metre (3,127 ft) Papuk, and 792-metre (2,598 ft) The Krndija\tmountains, the northern slopes of " +
            "618-metre (2,028 ft) Po\u017Ee\u0161ka Gora and 461-metre\t(1,512 ft) the Dilj hills,\tand\tlowland is surrounded  by the " +
            "mountains and\thills, and occupying the eastern paaart\tof the Po\u017Eega-Slavonia County.";

    private static final String text1 = "Sarehole Mill, Hall Green, Birmingham\t\"Inspired\" 1896\u20131900 (i. e. lived nearby)\t15 August 2002\tBirmingham Civic Society and The Tolkien Society\n" +
            "1 Duchess Place, Ladywood, Birmingham\tLived near here 1902\u20131910\tUnknown\tBirmingham Civic Society\n" +
            "4 Highfield Road, Edgbaston, Birmingham\tLived here 1910\u20131911\tUnknown\tBirmingham Civic Society and The Tolkien Society\n" +
            "Plough and Harrow, Hagley Road, Birmingham\tStayed here June 1916\tJune 1997\tThe Tolkien Society\n" +
            "2 Darnley Road, West Park, Leeds\tFirst academic appointment, Leeds\t1 October 2012\tThe Tolkien Society and the Leeds Civic Trust\n" +
            "20 Northmoor Road, North Oxford\tLived here 1930\u20131947\t3 December 2002\tOxfordshire Blue Plaques Board\n" +
            "Hotel Miramar, East Overcliff Drive, Bournemouth\tStayed here regularly from the 1950s until 1972\t10 June 1992 by Priscilla Tolkien\tBorough of Bournemouth";

    private static final String text2 = "space anchor:\t222222222222222222222222222222222222222222222222 03\tslash anchor:\t2024\\12\tdot anchor:\t20421.32\n" +
            "space anchor:\t2012 203\tslash anchor:\t2024\\2\tdot anchor:\t20421.333452\n" +
            "space anchor:\t201212 0423\tslash anchor:\t2067867824\\67867812\tdot anchor:\t21.32131232\n" +
            "space anchor:\t2123123012 03\tslash anchor:\t202131224\\12\tdot anchor:\t202.32323232323232323223223223223232323232323232323232\n" +
            "space anchor:\t2012 0213133\tslash anchor:\t2024\\21312312\tdot anchor:\t131.292\n";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void defaultTabsTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "defaultTabTest.pdf";
        String cmpFileName = sourceFolder + "cmp_defaultTabTest.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph();
        addTabbedTextToParagraph(p, text0, new float[0], null, null, null);
        doc.add(p);

        float left = doc.getLeftMargin();
        float right = doc.getRightMargin();
        float pageWidth = doc.getPdfDocument().getDefaultPageSize().getWidth();
        float[] defaultStopPositions = {0f, 50f, 100f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 500f, pageWidth - left - right};
        drawTabStopsPositions(defaultStopPositions, doc, 1, 0, 120);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void simpleTabStopsTest() throws IOException, InterruptedException {
        String fileName = "simpleTabStopsTest.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;

        Document doc = initDocument(outFileName);

        float tabInterval = doc.getPdfDocument().getDefaultPageSize().getWidth() / 8;

        //left alignments
        float[] positions1 = {tabInterval * 2, tabInterval * 4, tabInterval * 5};
        TabAlignment[] alignments1 = {TabAlignment.LEFT, TabAlignment.LEFT, TabAlignment.LEFT};
        ILineDrawer[] leaders1 = {null, null, null};
        Character[] anchors1 = {null, null, null};

        Paragraph p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text1, positions1, alignments1, leaders1, anchors1);
        doc.add(p);
        doc.add(new Paragraph("\n"));

        //right alignments
        float[] positions2 = {tabInterval * 3, tabInterval * 4, tabInterval * 6};
        TabAlignment[] alignments2 = {TabAlignment.RIGHT, TabAlignment.RIGHT, TabAlignment.RIGHT};
        ILineDrawer[] leaders2 = {null, null, null};
        Character[] anchors2 = {null, null, null};

        p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text1, positions2, alignments2, leaders2, anchors2);
        doc.add(p);
        doc.add(new Paragraph("\n"));

        //center alignments
        float[] positions3 = {tabInterval * 3, tabInterval * 4, tabInterval * 6};
        TabAlignment[] alignments3 = {TabAlignment.CENTER, TabAlignment.CENTER, TabAlignment.CENTER};
        ILineDrawer[] leaders3 = {null, null, null};
        Character[] anchors3 = {null, null, null};

        p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text1, positions3, alignments3, leaders3, anchors3);
        doc.add(p);


        drawTabStopsPositions(positions1, doc, 1, 0, 120);
        drawTabStopsPositions(positions2, doc, 1, 125, 95);
        drawTabStopsPositions(positions3, doc, 1, 235, 95);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void anchorTabStopsTest() throws IOException, InterruptedException {
        String fileName = "anchorTabStopsTest.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;

        Document doc = initDocument(outFileName);

        float tabInterval = doc.getPdfDocument().getDefaultPageSize().getWidth() / 8;

        float[] positions1 = {tabInterval * 2, tabInterval * 3, tabInterval * 4, tabInterval * 5, tabInterval * 6};
        TabAlignment[] alignments1 = {TabAlignment.ANCHOR, TabAlignment.CENTER, TabAlignment.ANCHOR,
                TabAlignment.RIGHT, TabAlignment.ANCHOR};
        ILineDrawer[] leaders1 = {new DottedLine(), null, new DashedLine(.5f), null, new SolidLine(.5f)};
        Character[] anchors1 = {' ', null, '\\', null, '.'};

        Paragraph p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text2, positions1, alignments1, leaders1, anchors1);
        doc.add(p);

        drawTabStopsPositions(positions1, doc, 1, 0, 120);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + outFileName));
    }

    @Test
    public void severalTabsInRowTest() throws IOException, InterruptedException {
        String fileName = "severalTabsInRowTest.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;

        Document doc = initDocument(outFileName);

        float tabInterval = doc.getPdfDocument().getDefaultPageSize().getWidth() / 8;

        float[] positions = {tabInterval * 2, tabInterval * 4, tabInterval * 6};
        TabAlignment[] alignments = {TabAlignment.RIGHT, TabAlignment.CENTER, TabAlignment.CENTER};
//        Drawable[] leaders = {null, null, null};
        ILineDrawer[] leaders = {new DottedLine(), new DashedLine(.5f), new SolidLine(.5f)};

        Paragraph p = new Paragraph();
        p.setFontSize(8);

        java.util.List<TabStop> tabStops = new ArrayList<>();
        for (int i = 0; i < positions.length; ++i) {
            TabStop tabStop = new TabStop(positions[i], alignments[i], leaders[i]);
            tabStops.add(tabStop);
        }
        p.addTabStops(tabStops);

        p.add(new Tab()).add("ttttttttttttttttttttttttttttttttttttttttttttt").add(new Tab()).add(new Tab()).add("ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt\n");
        p.add(new Tab()).add(new Tab()).add(new Tab()).add("ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt\n");
        p.add(new Tab()).add(new Tab()).add("ttttttttttttttttttttttttttttttttttttttttttttt").add(new Tab()).add("ttttttttttttttttttttttttttttttttttttttttttt");

        doc.add(p);

        drawTabStopsPositions(positions, doc, 1, 0, 120);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + outFileName));
    }

    @Test
    public void outOfPageBoundsTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "outOfPageBoundsTest.pdf";
        String cmpFileName = sourceFolder + "cmp_outOfPageBoundsTest.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);


        //tabstops out of page bounds
        Paragraph p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("left tab stop out of page bounds:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(1000, TabAlignment.LEFT, new DashedLine(.5f)));
        p.add("text").add(new Tab()).add("some interesting text after left-tabstop");
        doc.add(p);

        p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("right tab stop out of page bounds:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(1000, TabAlignment.RIGHT, new DashedLine(.5f)));
        p.add("text").add(new Tab()).add("some interesting text after right-tabstop");
        doc.add(p);

        //text out of page bounds
        p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("text out of page bounds after left tab stop:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(450, TabAlignment.LEFT, new DashedLine(.5f)));
        p.add("text").add(new Tab()).add("some interesting text after left-tabstop\n");
        p.add("text").add(new Tab()).add("someinterestingtextafterleft-tabstop");
        doc.add(p);

        p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("text out of page bounds after right tab stop:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(450, TabAlignment.RIGHT, new DashedLine(.5f)));
        p.add("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeext")
                .add(new Tab()).add("some interesting text after right-tabstop\n");
        p.add("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeext")
                .add(new Tab()).add("someinterestingtextafterright-tabstop\n");
        p.add("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeext")
                .add(new Tab()).add("word.");
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private Document initDocument(String outFileName) throws FileNotFoundException {
        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
        return new Document(pdfDoc);
    }

    private void drawTabStopsPositions(float[] positions, Document doc, int pageNum, int yStart, int dy) {
        PdfCanvas canvas = new PdfCanvas(doc.getPdfDocument().getPage(pageNum));
        float left = doc.getLeftMargin();
        float h = doc.getPdfDocument().getPage(pageNum).getCropBox().getHeight() - yStart;

        canvas.saveState();
        canvas.setLineDash(4, 2);
        canvas.setLineWidth(0.5f);
        canvas.setLineDash(4, 2);
        for (float f : positions) {
            canvas.moveTo(left + f, h);
            canvas.lineTo(left + f, h - dy);
        }

        canvas.stroke();
        canvas.restoreState();
        canvas.release();
    }

    private void addTabbedTextToParagraph(Paragraph p, String text, float[] positions, TabAlignment[] alignments,
                                          ILineDrawer[] tabLeadings, Character[] tabAnchorCharacters) {
        java.util.List<TabStop> tabStops = new ArrayList<>();
        for (int i = 0; i < positions.length; ++i) {
            TabStop tabStop = new TabStop(positions[i], alignments[i], tabLeadings[i]);
            tabStop.setTabAnchor(tabAnchorCharacters[i]);
            tabStops.add(tabStop);
        }
        p.addTabStops(tabStops);

        for (String line : text.split("\n")) {
            for (String chunk : line.split("\t")) {
                p.add(chunk).add(new Tab());
            }
            p.add("\n");
        }
    }
}
