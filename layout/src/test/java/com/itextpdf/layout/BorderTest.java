package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.DottedBorder;
import com.itextpdf.layout.border.DoubleBorder;
import com.itextpdf.layout.border.GrooveBorder;
import com.itextpdf.layout.border.InsetBorder;
import com.itextpdf.layout.border.OutsetBorder;
import com.itextpdf.layout.border.RidgeBorder;
import com.itextpdf.layout.border.RoundDotsBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;

@Category(IntegrationTest.class)
public class BorderTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/BorderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/BorderTest/";
    public static final String cmpPrefix = "cmp_";

    String fileName;
    String outFileName;
    String cmpFileName;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleBordersTest() throws IOException, InterruptedException {
        fileName = "simpleBordersTest.pdf";
        Document doc = createDocument();

        List list = new List();

        ListItem solidBorderItem = new ListItem("solid");
        solidBorderItem.setBorder(new SolidBorder(Color.RED, 6)).setMarginBottom(5);
        solidBorderItem.setBorderTop(new SolidBorder(Color.BLUE, 10));
        list.add(solidBorderItem);

        ListItem doubleBorderItem = new ListItem("double");
        doubleBorderItem.setBorder(new DoubleBorder(Color.RED, 10)).setMarginBottom(5);
        doubleBorderItem.setBorderRight(new DoubleBorder(Color.BLUE, 6));
        list.add(doubleBorderItem);

        ListItem dashedBorderItem = new ListItem("dashed");
        dashedBorderItem.setBorder(new DashedBorder(Color.GRAY, 2)).setMarginBottom(5);
        dashedBorderItem.setBorderBottom(new DashedBorder(Color.BLACK, 4));
        list.add(dashedBorderItem);

        ListItem dottedBorderItem = new ListItem("dotted");
        dottedBorderItem.setBorder(new DottedBorder(Color.BLACK, 3)).setMarginBottom(5);
        dottedBorderItem.setBorderLeft(new DottedBorder(Color.GRAY, 6));
        list.add(dottedBorderItem);

        ListItem roundDotsBorderItem = new ListItem("round dots");
        roundDotsBorderItem.setBorder(new RoundDotsBorder(Color.LIGHT_GRAY, 3)).setMarginBottom(5);
        roundDotsBorderItem.setBorderLeft(new RoundDotsBorder(Color.BLUE, 5));
        list.add(roundDotsBorderItem);

        doc.add(list);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void incompleteTableTest01() throws IOException, InterruptedException {
        fileName = "incompleteTableTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(2);
        table.setBorder(new SolidBorder(Color.GREEN, 5));
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add("One");
        table.addCell(cell);
        // row 1 and 2, cell 2
        cell = new Cell(2, 1).add("Two");
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add("Three");
        table.addCell(cell);

        // row 3, cell 1
        cell = new Cell().add("Four");
        table.addCell(cell);


        doc.add(table);


        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void simpleBorderTest02() throws IOException, InterruptedException {
        fileName = "simpleBorderTest02.pdf";
        Document doc = createDocument();

        Table table = new Table(1);
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add("One");
        cell.setBorderTop(new SolidBorder(20));
        cell.setBorderBottom(new SolidBorder(20));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add("Two");
        cell.setBorderTop(new SolidBorder(30));
        cell.setBorderBottom(new SolidBorder(40));

        table.addCell(cell);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void simpleBorderTest03() throws IOException, InterruptedException {
        fileName = "simpleBorderTest03.pdf";
        Document doc = createDocument();

        Table table = new Table(2);
        table.addCell(new Cell().add("1"));
        table.addCell(new Cell(2, 1).add("2"));
        table.addCell(new Cell().add("3"));
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Ignore("DEVSIX-796")
    @Test
    public void simpleBorderTest04() throws IOException, InterruptedException {
        fileName = "simpleBorderTest04.pdf";
        Document doc = createDocument();
        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";
        String textHelloWorld =
                "Hello World\n" +
                        "Hello World\n" +
                        "Hello World\n" +
                        "Hello World\n" +
                        "Hello World\n";

        Table table = new Table(2);
        table.setBorder(new SolidBorder(Color.RED, 2f));
        table.addCell(new Cell(2, 1).add(new Paragraph(textHelloWorld)));
        for (int i = 0; i < 2; i++) {
            table.addCell(new Cell().add(new Paragraph(textByron)));
        }
        table.addCell(new Cell(1, 2).add(textByron));
        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borders3DTest() throws IOException, InterruptedException {
        fileName = "borders3DTest.pdf";
        Document doc = createDocument();

        List list = new List();

        ListItem grooveBorderItem = new ListItem("groove");
        grooveBorderItem.setBorder(new GrooveBorder(2)).setMarginBottom(5).setWidth(100);
        list.add(grooveBorderItem);

        ListItem ridgeBorderItem = new ListItem("ridge");
        ridgeBorderItem.setBorder(new RidgeBorder(2)).setMarginBottom(5).setWidth(100);
        list.add(ridgeBorderItem);

        ListItem insetBorderItem = new ListItem("inset");
        insetBorderItem.setBorder(new InsetBorder(1)).setMarginBottom(5).setWidth(100);
        list.add(insetBorderItem);

        ListItem outsetBorderItem = new ListItem("outset");
        outsetBorderItem.setBorder(new OutsetBorder(1)).setMarginBottom(5).setWidth(100);
        list.add(outsetBorderItem);

        doc.add(list);

        Paragraph emptyParagraph = new Paragraph("\n");
        doc.add(emptyParagraph);


        DeviceRgb blueRgb = new DeviceRgb(0, 0, 200);
        DeviceRgb greenRgb = new DeviceRgb(0, 255, 0);
        DeviceCmyk magentaCmyk = new DeviceCmyk(0, 100, 0, 0);
        DeviceCmyk yellowCmyk = new DeviceCmyk(0, 0, 100, 0);

        list = new List();

        grooveBorderItem = new ListItem("groove");
        grooveBorderItem.setBorder(new GrooveBorder(blueRgb, 2)).setMarginBottom(5).setWidth(100);
        list.add(grooveBorderItem);

        ridgeBorderItem = new ListItem("ridge");
        ridgeBorderItem.setBorder(new RidgeBorder(greenRgb, 2)).setMarginBottom(5).setWidth(100);
        list.add(ridgeBorderItem);

        insetBorderItem = new ListItem("inset");
        insetBorderItem.setBorder(new InsetBorder(magentaCmyk, 1)).setMarginBottom(5).setWidth(100);
        list.add(insetBorderItem);

        outsetBorderItem = new ListItem("outset");
        outsetBorderItem.setBorder(new OutsetBorder(yellowCmyk, 1)).setMarginBottom(5).setWidth(100);
        list.add(outsetBorderItem);

        doc.add(list);

        emptyParagraph = new Paragraph("\n");
        doc.add(emptyParagraph);


        list = new List();

        grooveBorderItem = new ListItem("groove");
        grooveBorderItem.setBorder(new GrooveBorder(yellowCmyk, 8)).setMarginBottom(5);
        list.add(grooveBorderItem);

        ridgeBorderItem = new ListItem("ridge");
        ridgeBorderItem.setBorder(new RidgeBorder(magentaCmyk, 8)).setMarginBottom(5);
        list.add(ridgeBorderItem);

        insetBorderItem = new ListItem("inset");
        insetBorderItem.setBorder(new InsetBorder(greenRgb, 8)).setMarginBottom(5);
        list.add(insetBorderItem);

        outsetBorderItem = new ListItem("outset");
        outsetBorderItem.setBorder(new OutsetBorder(blueRgb, 8)).setMarginBottom(5);
        list.add(outsetBorderItem);

        doc.add(list);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderSidesTest() throws IOException, InterruptedException {
        fileName = "borderSidesTest.pdf";
        Document doc = createDocument();

        String text =
                "<p class=\"none\"  >No border.</p>\n" +
                        "<p class=\"dotted\">A dotted border.</p>\n" +
                        "<p class=\"dashed\">A dashed border.</p>\n" +
                        "<p class=\"solid\" >A solid border.</p>\n" +
                        "<p class=\"double\">A double border.</p>\n" +
                        "<p class=\"groove\">A groove border.</p>\n" +
                        "<p class=\"ridge\" >A ridge border.</p>\n" +
                        "<p class=\"inset\" >An inset border.</p>\n" +
                        "<p class=\"outset\">An outset border.</p>\n" +
                        "<p class=\"hidden\">A hidden border.</p>";
        Paragraph p = new Paragraph(text);

        p.setBorderTop(new SolidBorder(DeviceCmyk.MAGENTA, 4));
        p.setBorderRight(new DoubleBorder(DeviceRgb.RED, 6));
        p.setBorderBottom(new RoundDotsBorder(DeviceCmyk.CYAN, 2));
        p.setBorderLeft(new DashedBorder(DeviceGray.BLACK, 3));

        doc.add(p);

        doc.add(new Paragraph(text).setBorderTop(new SolidBorder(DeviceCmyk.MAGENTA, 8)));
        doc.add(new Paragraph(text).setBorderRight(new DoubleBorder(DeviceRgb.RED, 4)));
        doc.add(new Paragraph(text).setBorderBottom(new RoundDotsBorder(DeviceCmyk.CYAN, 3)));
        doc.add(new Paragraph(text).setBorderLeft(new DashedBorder(DeviceGray.BLACK, 5)));
        doc.add(new Paragraph(text).setBorder(new DottedBorder(DeviceGray.BLACK, 1)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderBoxTest() throws IOException, InterruptedException {
        fileName = "borderBoxTest.pdf";
        Document doc = createDocument();

        String textBefore = "At the mid-oceanic ridges, two tectonic plates diverge from one another as new oceanic crust is formed by the cooling and " +
                "solidifying of hot molten rock. Because the crust is very thin at these ridges due to the pull of the tectonic plates, the release of " +
                "pressure leads to adiabatic expansion and the partial melting of the mantle, causing volcanism and creating new oceanic crust. Most divergent " +
                "plate boundaries are at the bottom of the oceans; therefore, most volcanic activity is submarine, forming new seafloor. Black smokers (also " +
                "known as deep sea vents) are an example of this kind of volcanic activity. Where the mid-oceanic ridge is above sea-level, volcanic islands are " +
                "formed, for example, Iceland.";

        String text = "Earth's volcanoes occur because its crust is broken into 17 major, rigid tectonic plates that float on a hotter," +
                " softer layer in its mantle. Therefore, on Earth, volcanoes are generally found where tectonic plates are diverging or converging. " +
                "For example, a mid-oceanic ridge, such as the Mid-Atlantic Ridge, has volcanoes caused by divergent tectonic plates pulling apart;" +
                " the Pacific Ring of Fire has volcanoes caused by convergent tectonic plates coming together. Volcanoes can also form where there is " +
                "stretching and thinning of the crust's interior plates, e.g., in the East African Rift and the Wells Gray-Clearwater volcanic field and " +
                "Rio Grande Rift in North America. This type of volcanism falls under the umbrella of \"plate hypothesis\" volcanism. Volcanism away " +
                "from plate boundaries has also been explained as mantle plumes. These so-called \"hotspots\", for example Hawaii, are postulated to arise " +
                "from upwelling diapirs with magma from the core-mantle boundary, 3,000 km deep in the Earth. Volcanoes are usually not created where two " +
                "tectonic plates slide past one another.";

        String textAfter = "Subduction zones are places where two plates, usually an oceanic plate and a continental plate, collide. In this case, the oceanic " +
                "plate subducts, or submerges under the continental plate forming a deep ocean trench just offshore. In a process called flux melting, water released" +
                " from the subducting plate lowers the melting temperature of the overlying mantle wedge, creating magma. This magma tends to be very viscous due to " +
                "its high silica content, so often does not reach the surface and cools at depth. When it does reach the surface, a volcano is formed. Typical examples" +
                " of this kind of volcano are Mount Etna and the volcanoes in the Pacific Ring of Fire.";

        doc.add(new Paragraph(textBefore).setMargins(25, 60, 70, 80));

        Paragraph p = new Paragraph(text).setBackgroundColor(Color.GRAY);
        p.setMargins(25, 60, 70, 80);
        p.setBorderLeft(new DoubleBorder(DeviceRgb.RED, 25));
        p.setBorder(new DoubleBorder(DeviceRgb.BLACK, 6));
        doc.add(p);

        doc.add(new Paragraph(textAfter).setBorder(new DottedBorder(Color.BLACK, 3)).setBorderRight(new DottedBorder(Color.BLACK, 12)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void noVerticalBorderTest() throws IOException, InterruptedException {
        fileName = "noVerticalBorderTest.pdf";
        Document doc = createDocument();

        Table mainTable = new Table(1);
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(Color.BLACK, 0.5f));
        cell.add("TESCHTINK");
        mainTable.addCell(cell);
        doc.add(mainTable);
        doc.close();

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest01() throws IOException, InterruptedException {
        fileName = "wideBorderTest01.pdf";
        Document doc = createDocument();

        doc.add(new Paragraph("ROWS SHOULD BE THE SAME"));

        Table table = new Table(new float[]{1, 3});
        table.setWidthPercent(50);
        Cell cell;
        // row 21, cell 1
        cell = new Cell().add("BORDERS");
        table.addCell(cell);
        // row 1, cell 2
        cell = new Cell().add("ONE");
        cell.setBorderLeft(new SolidBorder(Color.RED, 16f));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add("BORDERS");
        table.addCell(cell);
        // row 2, cell 2
        cell = new Cell().add("TWO");
        cell.setBorderLeft(new SolidBorder(Color.RED, 16f));
        table.addCell(cell);

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest02() throws IOException, InterruptedException {
        fileName = "wideBorderTest02.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc  = new Document(pdfDocument, new PageSize(842,842));

        Table table = new Table(3);
        table.setBorder(new SolidBorder(Color.GREEN, 91f));
        Cell cell;

        cell = new Cell(1, 2).add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 70f));
        table.addCell(cell);

        cell = new Cell(2, 1).add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 70f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 70f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.BLUE, 20f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);


        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell(1, 2).add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);


        cell = new Cell(2, 1).add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);


        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);


        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell(1, 2).add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 45f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 40f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 35f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.BLUE, 5f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 45f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 64f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 102f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 11f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 12f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 44f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 27f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 16f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 59));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 20f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 20f));
        table.addCell(cell);
        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 20f));

        table.addCell(cell);

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest03() throws IOException, InterruptedException {
        fileName = "wideBorderTest03.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc  = new Document(pdfDocument, new PageSize(842, 400));

        Table table = new Table(2);
        table.setBorder(new SolidBorder(Color.GREEN, 90f));
        Cell cell;

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.BLUE, 20f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 120f));
        table.addCell(cell);


        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add("Borders shouldn't be layouted outside the layout area.");
        cell.setBorder(new SolidBorder(Color.RED, 50f));
        table.addCell(cell);

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void infiniteLoopTest01() throws IOException, InterruptedException {
        fileName = "infiniteLoopTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(new float[]{1, 3});
        table.setWidthPercent(50);
        Cell cell;

        // row 1, cell 1
        cell = new Cell().add("1ORD");
        cell.setBorderLeft(new SolidBorder(Color.BLUE, 5));
        table.addCell(cell);
        // row 1, cell 2
        cell = new Cell().add("ONE");
        cell.setBorderLeft(new SolidBorder(Color.RED, 100f));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add("2ORD");
        cell.setBorderTop(new SolidBorder(Color.YELLOW, 100f));
        table.addCell(cell);
        // row 2, cell 2
        cell = new Cell().add("TWO");
        cell.setBorderLeft(new SolidBorder(Color.RED, 0.5f));
        table.addCell(cell);


        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest01() throws IOException, InterruptedException {
        fileName = "splitCellsTest01.pdf";
        Document doc = createDocument();

        String longText = "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.";
        Table table = new Table(2);
        table.setBorderTop(new DottedBorder(Color.MAGENTA, 3f));
        table.setBorderRight(new DottedBorder(Color.RED, 3f));
        table.setBorderBottom(new DottedBorder(Color.BLUE, 3f));
        table.setBorderLeft(new DottedBorder(Color.GRAY, 3f));

        Cell cell;
        cell = new Cell().add("Some text");
        cell.setBorderRight(new SolidBorder(Color.RED, 2f));
        table.addCell(cell);
        cell = new Cell().add("Some text");
        cell.setBorderLeft(new SolidBorder(Color.GREEN, 4f));
        table.addCell(cell);
        cell = new Cell().add(longText);
        cell.setBorderBottom(new SolidBorder(Color.RED, 5f));
        table.addCell(cell);

        cell = new Cell().add("Hello");
        cell.setBorderBottom(new SolidBorder(Color.BLUE, 5f));
        table.addCell(cell);

        cell = new Cell().add("Some text.");
        cell.setBorderTop(new SolidBorder(Color.GREEN, 6f));
        table.addCell(cell);

        cell = new Cell().add("World");
        cell.setBorderTop(new SolidBorder(Color.YELLOW, 6f));
        table.addCell(cell);

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest02() throws IOException, InterruptedException {
        fileName = "splitCellsTest02.pdf";
        Document doc = createDocument();

        String text = "And it's Arsenal, \n" +
                "Arsenal FC, \n" +
                "We're by far the greatest team, \n" +
                "The world has ever seen.... \n";

        Table table = new Table(2);

        Cell cell;
        for (int i = 0; i < 38; i++) {
            cell = new Cell().add(text);
            cell.setBorder(new SolidBorder(Color.RED, 2f));
            cell.setBorderBottom(Border.NO_BORDER);
            table.addCell(cell);
        }
        doc.add(table);
        doc.add(new AreaBreak());

        table.setBorder(new SolidBorder(Color.YELLOW, 3));
        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderTest01() throws IOException, InterruptedException {
        fileName = "tableWithHeaderTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(2);
        table.setBorder(new SolidBorder(Color.YELLOW, 30));
        Cell cell;

        cell = new Cell().add("Header with narrow border").setBorder(new SolidBorder(Color.GREEN, 0.5f));
        table.addCell(cell);
        cell = new Cell().add("Header with wide border").setBorder(new SolidBorder(Color.GREEN, 65f));
        table.addCell(cell);

        cell = new Cell().add("Hello").setBorder(new SolidBorder(Color.MAGENTA, 5f));
        table.addCell(cell);
        cell = new Cell().add("World").setBorder(new SolidBorder(Color.MAGENTA, 5f));
        table.addCell(cell);

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    public void forcedPlacementTest01() throws IOException, InterruptedException {
        fileName = "forcedPlacementTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(1);
        table.setWidth(10);
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add("1ORD");
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add("2ORD");
        cell.setBorderTop(new SolidBorder(Color.YELLOW, 100f));
        table.addCell(cell);

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void noHorizontalBorderTest() throws IOException, InterruptedException {
        fileName = "noHorizontalBorderTest.pdf";
        Document doc = createDocument();

        Table mainTable = new Table(1);
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderRight(new SolidBorder(Color.BLACK, 0.5f));
        cell.add("TESCHTINK");
        mainTable.addCell(cell);
        doc.add(mainTable);
        doc.close();

        closeDocumentAndCompareOutputs(doc);
    }

    private Document createDocument() throws FileNotFoundException {
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        return new Document(pdfDocument);
    }

    private void closeDocumentAndCompareOutputs(Document document) throws IOException, InterruptedException {
        document.close();
        String compareResult = new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff");
        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }
}
