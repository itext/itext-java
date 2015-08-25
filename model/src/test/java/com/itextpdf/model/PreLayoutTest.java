package com.itextpdf.model;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.ParagraphRenderer;
import com.itextpdf.model.renderer.TextRenderer;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreLayoutTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/PreLayoutTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/PreLayoutTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void preLayoutTest01() throws IOException, InterruptedException, DocumentException {
        String outFileName = destinationFolder + "preLayoutTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument, PageSize.Default, false);

        List<Text> pageNumberTexts = new ArrayList<>();
        List<IRenderer> pageNumberRenderers = new ArrayList<>();

        document.setProperty(Property.FONT, new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA, "")));

        for (int i = 0; i < 200; i++) {
            document.add(new Paragraph("This is just junk text"));
            if (i % 10 == 0) {
                Text pageNumberText = new Text("Page #: {pageNumber}");

                IRenderer renderer = new TextRenderer(pageNumberText, pageNumberText.getText()) {
                    @Override
                    public void draw(PdfDocument document, PdfCanvas canvas) {
                        line = line.replace("{pageNumber}", String.valueOf(occupiedArea.getPageNumber()));
                        super.draw(document, canvas);
                    }
                };
                pageNumberText.setNextRenderer(renderer);
                pageNumberRenderers.add(renderer);

                Paragraph pageNumberParagraph = new Paragraph().add(pageNumberText);
                pageNumberTexts.add(pageNumberText);
                document.add(pageNumberParagraph);
            }
        }

        for (IRenderer renderer : pageNumberRenderers) {
            String currentData = ((TextRenderer)renderer).getText().replace("{pageNumber}", String.valueOf(renderer.getOccupiedArea().getPageNumber()));
            ((TextRenderer)renderer).setText(currentData);
        }

        // No need in relayout. Flush(draw) is done on close.
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void preLayoutTest02() throws IOException, InterruptedException, DocumentException {
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
            protected ParagraphRenderer createSplitRenderer() {
                return new TwoColumnParagraphRenderer((Paragraph) modelElement, oneColumnPage);
            }

            @Override
            protected ParagraphRenderer createOverflowRenderer() {
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
        twoColumnParagraph.add(textElement).setFont(new PdfType1Font(pdfDoc, new Type1Font(FontConstants.HELVETICA, "")));
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
