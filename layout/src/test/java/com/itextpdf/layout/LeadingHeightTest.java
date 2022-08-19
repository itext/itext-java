package com.itextpdf.layout;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LeadingHeightTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/LeadingHeightTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/LeadingHeightTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2)})
    @Test
    public void clippedHeightParagraphTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "leadingTestHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_leadingTestHeight.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        Document doc = new Document(pdfDoc, new PageSize(700, 700));
        // This is how table looks like if no height property is set
        addTable(doc, 504, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", -1);
        // Here we set value from pre layout as height. We expect that this table shall be equal to the previous one
        addTable(doc, 360, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", 0);
        // Here we set 100 as height. We expect that this will be enough and all text will be placed
        addTable(doc, 216, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", 100);
        // Here we set 100 as height. We expect that this will be enough to place 3 lines
        addTable(doc, 216, "RETIREMENT PLANNING: BECAUSE ***SOME TEST TEXT IS PLACED*** YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", 100);
        // Here we set value from pre layout minus 0.5f as height. We expect that this table shall not be equal to the previous one
        addTable(doc, 50, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", -2);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void pageHeightParagraphTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pageHeightParagraphTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_pageHeightParagraphTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        //176 = 104 + 36 + 36 (page margins)
        Document doc = new Document(pdfDoc, new PageSize(700, 176));
        Paragraph ph = new Paragraph();
        Text txt = new Text("RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.");
        txt.setFontSize(32f);
        ph.add(txt);
        ph.setFixedLeading(32f);
        ph.setPaddingTop(0f);
        ph.setPaddingBottom(0f);
        ph.setWidth(585f);

        doc.add(ph);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void pageHeightParagraphWithWithWorkaroundTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pageHeightParagraphWorkAroundTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_pageHeightParagraphWorkAroundTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        //176 = 104 + 36 + 36 (page margins)
        Document doc = new Document(pdfDoc, new PageSize(700, 176));
        Paragraph ph = new Paragraph();
        Text txt = new Text("RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.");
        txt.setFontSize(32f);
        ph.add(txt);
        ph.setFixedLeading(32f);
        ph.setPaddingTop(0f);
        ph.setPaddingBottom(0f);
        ph.setWidth(585f);


        Paragraph ph2 = new Paragraph();
        ph2.setHeight(104);
        ph2.setMargin(0);
        ph2.setPadding(0);
        ph2.add(ph);
        ph2.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);;
        doc.add(ph2);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    public void addTable(Document doc, int y, String text, int heightParam)
    {
        float width = 585f;
        float fontSize = 32f;

        Table table = new Table(1);
        table.setWidth(width);
        table.setFixedLayout();

        Paragraph ph = new Paragraph();
        Text txt = new Text(text);
        txt.setFontSize(fontSize);
        ph.add(txt);
        ph.setFixedLeading(fontSize);

        Cell cell = new Cell();
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(0f);
        cell.add(ph);
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setBorder(null);

        table.addCell(cell);

        // find out how tall the cell is we just added
        LayoutResult result = table.createRendererSubTree()
                .setParent(doc.getRenderer())
                .layout(
                        new LayoutContext(
                                new LayoutArea(
                                        1,
                                        new Rectangle(0, 0, width, 10000.0F)
                                )
                        )
                );

        String heightStr = "Natural";
        if (heightParam == -2) {
            float rowHeight = result.getOccupiedArea().getBBox().getHeight();
            cell.setHeight(rowHeight - 0.5f);
            heightStr = "Calculated " + (rowHeight - 0.5f);
        }
        if (heightParam == 0)
        {
            float rowHeight = result.getOccupiedArea().getBBox().getHeight();
            cell.setHeight(rowHeight + 1f);
            heightStr = "Calculated " + rowHeight;
        }
        else if (heightParam > 0)
        {
            cell.setHeight(heightParam);
            heightStr = "Explicit " + heightParam;
        }

        table.setFixedPosition((float) 36, (float) y, width);

        doc.add(table);

        Table t2 = new Table(1);
        t2.setWidth(width);
        t2.setFixedLayout();
        Cell c2 = new Cell();
        c2.setTextAlignment(TextAlignment.CENTER);
        c2.setWidth(width);
        c2.setBorder(Border.NO_BORDER);
        c2.add(new Paragraph("Row Height: " + heightStr));
        t2.addCell(c2);
        t2.setFixedPosition((float) 36, (float) y-18, width);
        doc.add(t2);
    }
}
