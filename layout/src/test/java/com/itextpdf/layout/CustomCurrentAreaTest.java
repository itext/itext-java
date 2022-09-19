package com.itextpdf.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CustomCurrentAreaTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/CustomCurrentAreaTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/CustomCurrentAreaTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void longListItemTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "longListItemTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_longListItemTest.pdf";
        Rectangle customArea = new Rectangle(0, 15, 586, 723);
        try(PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));Document document = new Document(pdf)) {
            ClauseRenderer renderer = new ClauseRenderer(document, customArea);
            document.setRenderer(renderer);

            com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
            list.setListSymbol("1.");
            list.add(new ListItem(
                    "It is a long established fact that a reader will be distracted by the readable content of a page"
                            + " when looking at its layout."));
            document.add(new Table(1).addCell(new Cell().add(list)));
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private static class ClauseRenderer extends DocumentRenderer {
        protected Rectangle column;

        public ClauseRenderer(Document document, Rectangle rect) {
            super(document, false);
            this.column = rect;
        }

        @Override
        protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
            super.updateCurrentArea(overflowResult);
            return (currentArea = new RootLayoutArea(currentArea.getPageNumber(), column.clone()));
        }
    }
}
