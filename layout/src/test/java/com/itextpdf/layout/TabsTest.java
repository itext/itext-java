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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Category(IntegrationTest.class)
public class TabsTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TabsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TabsTest/";

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
            "space anchor:\t2012 0213133\tslash anchor:\t2024\\21312312\tdot anchor:\t131.292";

    // private static final String text3 = "\t0\n\t11#2.35\n\t813.2134#558914423\n\t3.37761#098\n\t#.715\n\t972#5844.18167\n\t";
    private static final String text3 = "\t0\n\t11#2.35\n\t813.2134#558914423\n\t3.37761#098\n\t#.715\n\t972#5844.18167\n\t65#1094.6177##1128\n\t65.7#463\n\t68750.25121\n\t393#19.6#418#31\n\t7#811";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void chunkEndsAfterOrBeforeTabPosition() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "chunkEndsAfterOrBeforeTabPosition.pdf";
        String cmpFileName = sourceFolder + "cmp_chunkEndsAfterOrBeforeTabPosition.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textBeforeTab = "a";
        String textAfterTab = "tab stop's position = ";

        Paragraph paragraph;
        for (int i = 0; i < 20; i++) {
            paragraph = new Paragraph();
            paragraph.add(new Text(textBeforeTab));
            TabStop[] tabStop = new TabStop[1];
            tabStop[0] = new TabStop(i);
            paragraph.addTabStops(tabStop);
            paragraph.add(new Tab());
            paragraph.add(new Text(textAfterTab));
            paragraph.add(Integer.toString(i));
            doc.add(paragraph);
        }

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void defaultTabsTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "defaultTabTest.pdf";
        String cmpFileName = sourceFolder + "cmp_defaultTabTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
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
    public void anchorTabStopsTest01() throws IOException, InterruptedException {
        String fileName = "anchorTabStopsTest01.pdf";
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
    public void anchorTabStopsTest02() throws IOException, InterruptedException {
        String fileName = "anchorTabStopsTest02.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;

        Document doc = initDocument(outFileName, true);

        float tabInterval = doc.getPdfDocument().getDefaultPageSize().getWidth() / 2;

        float[] positions1 = {tabInterval};
        TabAlignment[] alignments1 = {TabAlignment.ANCHOR};

        ILineDrawer[] leaders1 = {new DottedLine()};
        Character[] anchors1 = {'.'};

        Paragraph p = new Paragraph();
        p.setFontSize(8);

        addTabbedTextToParagraph(p, text3, positions1, alignments1, leaders1, anchors1);
        doc.add(p);

        drawTabStopsPositions(positions1, doc, 1, 0, 200);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + outFileName));
    }

    @Test
    public void tablesAndTabInsideOfParagraph() throws IOException, InterruptedException {
        String testName = "tablesAndTabInsideOfParagraph.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        Document doc = initDocument(outFileName, false);


        Table leftTable = new Table(1);
        for(int x=0; x<3; x++){
            leftTable.addCell("Table 1, Line " + (x + 1));
        }
        Table rightTable = new Table(1);
        for(int x=0; x<3; x++){
            rightTable.addCell("Table 2, Line " + (x + 1));
        }

        Paragraph p = new Paragraph().add(leftTable);
        p.add(new Tab());
        p.addTabStops(new TabStop(300, TabAlignment.LEFT));
        p.add(rightTable);
        doc.add(new Paragraph("TabAlignment: LEFT"));
        doc.add(p);

        p = new Paragraph().add(leftTable);
        p.add(new Tab());
        p.addTabStops(new TabStop(300, TabAlignment.CENTER));
        p.add(rightTable);
        doc.add(new Paragraph("TabAlignment: CENTER"));
        doc.add(p);

        p = new Paragraph().add(leftTable);
        p.add(new Tab());
        p.addTabStops(new TabStop(300, TabAlignment.RIGHT));
        p.add(rightTable);
        doc.add(new Paragraph("TabAlignment: RIGHT"));
        doc.add(p);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);


        //tabstops out of page bounds
        Paragraph p = new Paragraph();
        p.setFontColor(ColorConstants.GREEN);
        p.add("left tab stop out of page bounds:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(1000, TabAlignment.LEFT, new DashedLine(.5f)));
        p.add("text").add(new Tab()).add("some interesting text after left-tabstop");
        doc.add(p);

        p = new Paragraph();
        p.setFontColor(ColorConstants.GREEN);
        p.add("right tab stop out of page bounds:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(1000, TabAlignment.RIGHT, new DashedLine(.5f)));
        p.add("text").add(new Tab()).add("some interesting text after right-tabstop");
        doc.add(p);

        //text out of page bounds
        p = new Paragraph();
        p.setFontColor(ColorConstants.GREEN);
        p.add("text out of page bounds after left tab stop:");
        doc.add(p);

        p = new Paragraph();
        p.addTabStops(new TabStop(450, TabAlignment.LEFT, new DashedLine(.5f)));
        p.add("text").add(new Tab()).add("some interesting text after left-tabstop\n");
        p.add("text").add(new Tab()).add("someinterestingtextafterleft-tabstop");
        doc.add(p);

        p = new Paragraph();
        p.setFontColor(ColorConstants.GREEN);
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

    @Test
    public void tabsInParagraphTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tabsInParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_tabsInParagraphTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        float tabWidth = pdfDoc.getDefaultPageSize().getWidth() - doc.getLeftMargin() - doc.getRightMargin();

        Paragraph p = new Paragraph();
        p
                .addTabStops(new TabStop(tabWidth, TabAlignment.RIGHT))
                .add("There is a right-aligned tab after me. And then three chunks of text.")
                .add(new Tab())
                .add("Text1")
                .add("Text2")
                .add("Text3");
        doc.add(p);

        p = new Paragraph();
        p
                .addTabStops(new TabStop(tabWidth, TabAlignment.RIGHT))
                .add("There is a right-aligned tab after me. And then three chunks of text.")
                .add(new Tab())
                .add("Text1")
                .add("Tex\nt2")
                .add("Text3");
        doc.add(p);

        p = new Paragraph();
        p
                .addTabStops(new TabStop(tabWidth, TabAlignment.RIGHT))
                .add("There is a right-aligned tab after me. And then three chunks of text.")
                .add(new Tab())
                .add("Long Long Long Long Long Long Long Text1")
                .add("Tex\nt2")
                .add("Text3");
        doc.add(p);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createJpeg(UrlUtil.toURL(sourceFolder + "Desert.jpg")));
        Image image = new Image(xObject, 100);

        p = new Paragraph();
        p
                .addTabStops(new TabStop(tabWidth, TabAlignment.RIGHT))
                .add("There is a right-aligned tab after me. And then texts and an image.")
                .add(new Tab())
                .add("Text1")
                .add(image)
                .add("Text3");
        doc.add(p);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tabsAnchorSemicolonTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tabsAnchorSemicolonTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_tabsAnchorSemicolonTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);

        float w = document.getPageEffectiveArea(PageSize.A4).getWidth();
        Paragraph p = new Paragraph();
        List<TabStop> tabstops = new ArrayList<TabStop>();
        tabstops.add(new TabStop(w / 2, TabAlignment.RIGHT));
        tabstops.add(new TabStop(w / 2 + 1f, TabAlignment.LEFT));
        p.addTabStops(tabstops);
        p.add(new Tab()).add("Test:").add(new Tab()).add("Answer");
        document.add(p);
        p = new Paragraph();
        p.addTabStops(tabstops);
        p.add(new Tab()).add("Test245454:").add(new Tab()).add("Answer2");
        document.add(p);

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tabsAnchorSemicolonTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tabsAnchorSemicolonTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_tabsAnchorSemicolonTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);

        float w = document.getPageEffectiveArea(PageSize.A4).getWidth();
        Paragraph p = new Paragraph();
        p.setProperty(Property.TAB_DEFAULT, 0.01f);
        List<TabStop> tabstops = new ArrayList<TabStop>();
        tabstops.add(new TabStop(w / 2, TabAlignment.RIGHT));
        p.addTabStops(tabstops);
        p.add(new Tab()).add("Test:").add(new Tab()).add("Answer");
        document.add(p);
        p = new Paragraph();
        p.setProperty(Property.TAB_DEFAULT, 0.01f);
        p.addTabStops(tabstops);
        p.add(new Tab()).add("Test245454:").add(new Tab()).add("Answer2");
        document.add(p);

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tabsAnchorSemicolonTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tabsAnchorSemicolonTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_tabsAnchorSemicolonTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);

        float w = document.getPageEffectiveArea(PageSize.A4).getWidth();
        Paragraph p = new Paragraph();
        TabStop tabStop = new TabStop(w / 2, TabAlignment.ANCHOR);
        tabStop.setTabAnchor(':');
        p.addTabStops(tabStop);
        p.add(new Tab()).add("Test:Answer");
        document.add(p);

        p = new Paragraph();
        p.addTabStops(tabStop);
        p.add(new Tab()).add("Test245454:Answer2");
        document.add(p);

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fillParagraphWithTabsDifferently() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fillParagraphWithTabsDifferently.pdf";
        String cmpFileName = sourceFolder + "cmp_fillParagraphWithTabsDifferently.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("a\tb"));
        doc.add(new Paragraph().add("a").add("\t").add("b"));
        doc.add(new Paragraph().add(new Text("a")).add(new Text("\t")).add(new Text("b")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private Document initDocument(String outFileName) throws FileNotFoundException {
        return initDocument(outFileName, false);
    }

    private Document initDocument(String outFileName, boolean tagged) throws FileNotFoundException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        if (tagged) {
            pdfDoc.setTagged();
        }
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
                for (String piece : chunk.split("#")) {
                    if (!piece.isEmpty()) {
                        p.add(piece);
                    }
                }
                p.add(new Tab());
            }
            p.add("\n");
        }
    }
}
