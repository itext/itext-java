package com.itextpdf.layout;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import com.itextpdf.test.ExtendedITextTest;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PreLayoutTest extends ExtendedITextTest{

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/layout/PreLayoutTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/layout/PreLayoutTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void preLayoutTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument, PageSize.Default, false);

        List<Text> pageNumberTexts = new ArrayList<>();
        List<IRenderer> pageNumberRenderers = new ArrayList<>();

        document.setProperty(Property.FONT, PdfFontFactory.createFont(FontConstants.HELVETICA));

        for (int i = 0; i < 200; i++) {
            document.add(new Paragraph("This is just junk text"));
            if (i % 10 == 0) {
                Text pageNumberText = new Text("Page #: {pageNumber}");
                IRenderer renderer = new TextRenderer(pageNumberText);
                pageNumberText.setNextRenderer(renderer);
                pageNumberRenderers.add(renderer);

                Paragraph pageNumberParagraph = new Paragraph().add(pageNumberText);
                pageNumberTexts.add(pageNumberText);
                document.add(pageNumberParagraph);
            }
        }

        for (IRenderer renderer : pageNumberRenderers) {
            String currentData = renderer.toString().replace("{pageNumber}", String.valueOf(renderer.getOccupiedArea().getPageNumber()));
            ((TextRenderer)renderer).setText(currentData);
            ((Text)renderer.getModelElement()).setNextRenderer(renderer);
        }

        document.relayout();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void preLayoutTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDoc, PageSize.Default, false);

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        class TwoColumnParagraphRenderer extends ParagraphRenderer {

            int oneColumnPage = -1;

            public TwoColumnParagraphRenderer(Paragraph modelElement) {
                super(modelElement);
            }

            public TwoColumnParagraphRenderer(Paragraph modelElement, int oneColumnPage) {
                this(modelElement);
                this.oneColumnPage = oneColumnPage;
            }

            @Override
            public List<Rectangle> initElementAreas(LayoutArea area) {
                List<Rectangle> areas = new ArrayList<Rectangle>();
                if (area.getPageNumber() != oneColumnPage) {
                    Rectangle firstArea = area.getBBox().clone();
                    Rectangle secondArea = area.getBBox().clone();
                    firstArea.setWidth(firstArea.getWidth() / 2 - 5);
                    secondArea.setX(secondArea.getX() + secondArea.getWidth() / 2 + 5);
                    secondArea.setWidth(firstArea.getWidth());
                    areas.add(firstArea);
                    areas.add(secondArea);
                } else {
                    areas.add(area.getBBox());
                }
                return areas;
            }

            @Override
            public ParagraphRenderer getNextRenderer() {
                return new TwoColumnParagraphRenderer((Paragraph) modelElement, oneColumnPage);
            }
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1200; i++) {
            text.append("A very long text is here...");
        }
        Paragraph twoColumnParagraph = new Paragraph();
        twoColumnParagraph.setNextRenderer(new TwoColumnParagraphRenderer(twoColumnParagraph));
        Text textElement = new Text(text.toString());
        twoColumnParagraph.add(textElement).setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
        document.add(twoColumnParagraph);

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        int paragraphLastPageNumber = -1;
        List<IRenderer> documentChildRenderers = document.getRenderer().getChildRenderers();
        for (int i = documentChildRenderers.size() - 1; i >= 0; i--) {
            if (documentChildRenderers.get(i).getModelElement() == twoColumnParagraph) {
                paragraphLastPageNumber = documentChildRenderers.get(i).getOccupiedArea().getPageNumber();
                break;
            }
        }

        twoColumnParagraph.setNextRenderer(new TwoColumnParagraphRenderer(twoColumnParagraph, paragraphLastPageNumber));
        document.relayout();

        //Close document. Drawing of content is happened on close
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
