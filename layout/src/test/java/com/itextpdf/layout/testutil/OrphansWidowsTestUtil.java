package com.itextpdf.layout.testutil;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.io.FileNotFoundException;

public class OrphansWidowsTestUtil {

    public static String paraText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
            + "tempor incididunt ut labore et dolore magna aliqua. Nulla at volutpat diam ut "
            + "venenatis tellus in. Orci porta non pulvinar neque laoreet suspendisse interdum "
            + "consectetur. Ipsum dolor sit amet consectetur adipiscing. Id porta nibh venenatis"
            + " cras sed felis eget velit. Sapien nec sagittis aliquam malesuada. Cras sed felis"
            + " eget velit aliquet sagittis. Leo a diam sollicitudin tempor id eu nisl nunc."
            + " Faucibus a pellentesque sit amet porttitor eget dolor morbi. Nisl vel pretium"
            + " lectus quam id leo in vitae. Vehicula ipsum a arcu cursus vitae. Tincidunt praesent"
            + " semper feugiat nibh sed pulvinar proin gravida hendrerit. Nisl vel pretium lectus"
            + " quam id leo in vitae turpis. Quis hendrerit dolor magna eget est lorem. Diam sit"
            + " amet nisl suscipit adipiscing bibendum est ultricies. Ultricies mi eget mauris pharetra."
            + " Etiam dignissim diam quis enim. Felis bibendum ut tristique et egestas quis.";

    public static void produceOrphansWidowsTestCase(String outPdf, int linesLeft, boolean orphans, Paragraph testPara, boolean marginTestCase) throws FileNotFoundException {
        Document doc = new Document(new PdfDocument(new PdfWriter(outPdf)));

        PageSize pageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A5.getHeight());
        doc.getPdfDocument().setDefaultPageSize(pageSize);
        Rectangle[] columns = initUniformColumns(pageSize, 2);
        doc.setRenderer(new ColumnDocumentRenderer(doc, columns));

        testPara.setMargins(marginTestCase ? 30 : 0, 0, 0, 0)
                .setBackgroundColor(new DeviceRgb(232, 232, 232))
                .setBorder(new SolidBorder(1));
        testPara.add(paraText);

        float linesHeight = calculateHeightForLinesNum(doc, testPara, columns[0].getWidth(), linesLeft, orphans);
        float linesSpaceEps = 5;

        String descriptionIntro = "Test " + (orphans ? "orphans" : "widows") + ". ";
        String descriptionBeg = "This block height is adjusted in such way as to leave ";
        String descriptionEnd = " line(s) on area break. Configuration of orphans/widows is identified by the file name. " +
                "Reference example without orphans/widows control can be found on the next page.";
        float adjustmentHeight = columns[0].getHeight() - linesHeight - linesSpaceEps;
        doc.add(new Paragraph()
                .add(new Text(descriptionIntro).setFontColor(ColorConstants.RED))
                .add(new Text(descriptionBeg).setFontSize(8))
                .add(new Text(String.valueOf(linesLeft)).setFontColor(ColorConstants.RED))
                .add(new Text(descriptionEnd).setFontSize(8))
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));
        if (!marginTestCase) {
            doc.add(testPara);
        } else {
            Div div = new Div().add(testPara).setMarginTop(15);
            div.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            doc.add(div);
        }

        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        doc.add(new Paragraph("Reference example without orphans/widows control.")
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));

        if (!marginTestCase) {
            doc.add(new Paragraph(paraText).setMargin(0)
                    .setBackgroundColor(new DeviceRgb(232, 232, 232))
                    .setBorder(new SolidBorder(1)));
        } else {
            Div div = new Div().add(new Paragraph(paraText).setMargins(30, 0, 0, 0)
                    .setBackgroundColor(new DeviceRgb(232, 232, 232))
                    .setBorder(new SolidBorder(1)))
                    .setMarginTop(15);
            div.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            doc.add(div);
        }
        doc.close();
    }

    public static float calculateHeightForLinesNum(Document doc, Paragraph p, float width, float linesNum, boolean orphans) {
        ParagraphRenderer renderer = (ParagraphRenderer) p.createRendererSubTree().setParent(doc.getRenderer());
        LayoutResult layoutRes = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(width, 100000))));
        float lineHeight = layoutRes.getOccupiedArea().getBBox().getHeight() / renderer.getLines().size();
        float height = lineHeight * linesNum;
        if (orphans) {
            return height;
        } else {
            return layoutRes.getOccupiedArea().getBBox().getHeight() - height;
        }
    }

    private static Rectangle[] initUniformColumns(PageSize pageSize, int columnsNum) {
        Rectangle[] columns = new Rectangle[columnsNum];
        float columnWidth = (pageSize.getWidth() - 72) / columnsNum;
        for (int i = 0; i < columnsNum; ++i) {
            columns[i] = new Rectangle(36 + i * columnWidth, 36, columnWidth - 36, pageSize.getHeight() - 72);
        }
        return columns;
    }
}
