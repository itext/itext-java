package com.itextpdf.model;

import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.draw.DashedLine;
import com.itextpdf.canvas.draw.DottedLine;
import com.itextpdf.canvas.draw.Drawable;
import com.itextpdf.canvas.draw.SolidLine;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Tab;
import com.itextpdf.model.element.TabStop;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Category(IntegrationTest.class)
public class TabsTest extends ExtendedITextTest{
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/TabTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/TabTest/";

    static final private String text0 = "The Po\u017Eega Valley is a geographic microregion\tof Croatia, located in central" +
            " Slavonia, enveloped by the Slavonian mountains. It consists of\tsouthern slopes of 984-metre (3,228 ft)" +
            " Psunj, 953-metre (3,127 ft) Papuk, and 792-metre (2,598 ft) The Krndija\tmountains, the northern slopes of " +
            "618-metre (2,028 ft) Po\u017Ee\u0161ka Gora and 461-metre\t(1,512 ft) the Dilj hills,\tand\tlowland is surrounded  by the " +
            "mountains and\thills, and occupying the eastern paaart\tof the Po\u017Eega-Slavonia County.";

    static final private String text1 = "Sarehole Mill, Hall Green, Birmingham\t\"Inspired\" 1896\u20131900 (i. e. lived nearby)\t15 August 2002\tBirmingham Civic Society and The Tolkien Society\n" +
            "1 Duchess Place, Ladywood, Birmingham\tLived near here 1902\u20131910\tUnknown\tBirmingham Civic Society\n" +
            "4 Highfield Road, Edgbaston, Birmingham\tLived here 1910\u20131911\tUnknown\tBirmingham Civic Society and The Tolkien Society\n" +
            "Plough and Harrow, Hagley Road, Birmingham\tStayed here June 1916\tJune 1997\tThe Tolkien Society\n" +
            "2 Darnley Road, West Park, Leeds\tFirst academic appointment, Leeds\t1 October 2012\tThe Tolkien Society and the Leeds Civic Trust\n" +
            "20 Northmoor Road, North Oxford\tLived here 1930\u20131947\t3 December 2002\tOxfordshire Blue Plaques Board\n" +
            "Hotel Miramar, East Overcliff Drive, Bournemouth\tStayed here regularly from the 1950s until 1972\t10 June 1992 by Priscilla Tolkien\tBorough of Bournemouth";

    static final private String text2 = "space anchor:\t222222222222222222222222222222222222222222222222 03\tslash anchor:\t2024\\12\tdot anchor:\t20421.32\n" +
            "space anchor:\t2012 203\tslash anchor:\t2024\\2\tdot anchor:\t20421.333452\n" +
            "space anchor:\t201212 0423\tslash anchor:\t2067867824\\67867812\tdot anchor:\t21.32131232\n" +
            "space anchor:\t2123123012 03\tslash anchor:\t202131224\\12\tdot anchor:\t202.32323232323232323223223223223232323232323232323232\n" +
            "space anchor:\t2012 0213133\tslash anchor:\t2024\\21312312\tdot anchor:\t131.292\n";

    @BeforeClass
    static public void beforeClass() {
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
        addTabbedTextToParagraph(p, text0, new Float[0], null, null, null);
        doc.add(p);

        float left = doc.getPdfDocument().getDefaultPageSize().getLeftMargin();
        float right = doc.getPdfDocument().getDefaultPageSize().getRightMargin();float pageWidth = doc.getPdfDocument().getDefaultPageSize().getWidth();
        Float[] defaultStopPositions = {0f, 50f, 100f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 500f, pageWidth - left - right};
        drawTabStopsPositions(Arrays.asList(defaultStopPositions), doc, 1, 0, 120);

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
        Float[] positions1 = {tabInterval * 2, tabInterval * 4, tabInterval * 5};
        Property.TabAlignment[] alignments1 = {Property.TabAlignment.LEFT, Property.TabAlignment.LEFT, Property.TabAlignment.LEFT};
        Drawable[] leaders1 = {null, null, null};
        Character[] anchors1 = {null, null, null};

        Paragraph p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text1, positions1, alignments1, leaders1, anchors1);
        doc.add(p);
        doc.add(new Paragraph("\n"));

        //right alignments
        Float[] positions2 = {tabInterval * 3, tabInterval * 4, tabInterval * 6};
        Property.TabAlignment[] alignments2 = {Property.TabAlignment.RIGHT, Property.TabAlignment.RIGHT, Property.TabAlignment.RIGHT};
        Drawable[] leaders2 = {null, null, null};
        Character[] anchors2 = {null, null, null};

        p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text1, positions2, alignments2, leaders2, anchors2);
        doc.add(p);
        doc.add(new Paragraph("\n"));

        //center alignments
        Float[] positions3 = {tabInterval * 3, tabInterval * 4, tabInterval * 6};
        Property.TabAlignment[] alignments3 = {Property.TabAlignment.CENTER, Property.TabAlignment.CENTER, Property.TabAlignment.CENTER};
        Drawable[] leaders3 = {null, null, null};
        Character[] anchors3 = {null, null, null};

        p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text1, positions3, alignments3, leaders3, anchors3);
        doc.add(p);


        drawTabStopsPositions(Arrays.asList(positions1), doc, 1, 0, 120);
        drawTabStopsPositions(Arrays.asList(positions2), doc, 1, 125, 95);
        drawTabStopsPositions(Arrays.asList(positions3), doc, 1, 235, 95);

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

        Float[] positions1 = {tabInterval * 2, tabInterval * 3, tabInterval * 4, tabInterval * 5, tabInterval * 6};
        Property.TabAlignment[] alignments1 = {Property.TabAlignment.ANCHOR, Property.TabAlignment.CENTER, Property.TabAlignment.ANCHOR,
                                                Property.TabAlignment.RIGHT, Property.TabAlignment.ANCHOR};
        Drawable[] leaders1 = {new DottedLine(), null, new DashedLine(), null, new SolidLine()};
        Character[] anchors1 = {' ', null, '\\', null, '.'};

        Paragraph p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text2, positions1, alignments1, leaders1, anchors1);
        doc.add(p);

        drawTabStopsPositions(Arrays.asList(positions1), doc, 1, 0, 120);

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

        Float[] positions = {tabInterval * 2, tabInterval * 4, tabInterval * 6};
        Property.TabAlignment[] alignments = {Property.TabAlignment.RIGHT, Property.TabAlignment.CENTER, Property.TabAlignment.CENTER};
//        Drawable[] leaders = {null, null, null};
        Drawable[] leaders = {new DottedLine(), new DashedLine(), new SolidLine()};

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

        drawTabStopsPositions(Arrays.asList(positions), doc, 1, 0, 120);

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
        p.addTabStops(new TabStop(1000, Property.TabAlignment.LEFT, new DashedLine()));
        p.add("text").add(new Tab()).add("some interesting text after left-tabstop");
        doc.add(p);

        p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("right tab stop out of page bounds:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(1000, Property.TabAlignment.RIGHT, new DashedLine()));
        p.add("text").add(new Tab()).add("some interesting text after right-tabstop");
        doc.add(p);

        //text out of page bounds
        p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("text out of page bounds after left tab stop:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(450, Property.TabAlignment.LEFT, new DashedLine()));
        p.add("text").add(new Tab()).add("some interesting text after left-tabstop\n");
        p.add("text").add(new Tab()).add("someinterestingtextafterleft-tabstop");
        doc.add(p);

        p = new Paragraph();
        p.setFontColor(Color.GREEN);
        p.add("text out of page bounds after right tab stop:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(450, Property.TabAlignment.RIGHT, new DashedLine()));
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

    private void drawTabStopsPositions(java.util.List<Float> positions, Document doc, int pageNum, int yStart, int dy) {
        PdfCanvas canvas = new PdfCanvas(doc.getPdfDocument().getPage(pageNum));
        float left = doc.getPdfDocument().getDefaultPageSize().getLeftMargin();
        float h = doc.getPdfDocument().getPage(pageNum).getCropBox().getHeight() - yStart;

        canvas.saveState();
        canvas.setLineDash(4, 2);
        canvas.setLineWidth(0.5f);
        canvas.setLineDash(4, 2);
        for (Float f : positions) {
            canvas.moveTo(left + f, h);
            canvas.lineTo(left + f, h - dy);
        }

        canvas.stroke();
        canvas.restoreState();
        canvas.release();
    }

    private void addTabbedTextToParagraph(Paragraph p, String text, Float[] positions, Property.TabAlignment[] alignments,
                                          Drawable[] tabLeadings, Character[] tabAnchorCharacters){
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
