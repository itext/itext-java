package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.renderer.BlockRenderer;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TextRenderer;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
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
    @Ignore
    public void preLayoutTest01() throws FileNotFoundException, PdfException {
        String outFileName = destinationFolder + "preLayoutTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        List<Text> pageNumberTexts = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            document.add(new Paragraph("This is just junk text"));
            if (i % 10 == 0) {
                Text pageNumberText = new Text("Page #: {pageNumber}");
                Paragraph pageNumberParagraph = new Paragraph().add(pageNumberText);
                pageNumberTexts.add(pageNumberText);
                document.add(pageNumberParagraph);
            }
        }

//        document.layout();
//        for (Paragraph p : pageNumberTexts) {
//            List<IRenderer> renderers = p.getRenderers();
//            for (IRenderer renderer : renderers) {
//                String currentData = ((TextRenderer)renderer).getData().getText().replace("{pageNumber}", renderer.getOccupiedArea().getPageNumber());
//                ((TextRenderer)renderer).getData().setText(currentData);
//            }
//        }
//        document.relayout();
//        document.draw();

        document.close();
    }

    @Test
    public void preLayoutTest02() throws IOException, PdfException {
        String outFileName = destinationFolder + "preLayoutTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDoc);

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        class TwoColumnParagraphRenderer extends BlockRenderer {

            public TwoColumnParagraphRenderer(Paragraph modelElement) {
                super(modelElement);
            }

            @Override
            public List<LayoutArea> initElementAreas(LayoutContext context) {
                LayoutArea area = context.getArea();
                List<LayoutArea> areas = new ArrayList<LayoutArea>();
                LayoutArea firstArea = area.clone();
                LayoutArea secondArea = area.clone();
                firstArea.getBBox().setWidth(firstArea.getBBox().getWidth() / 2);
                secondArea.getBBox().setX(secondArea.getBBox().getX() + secondArea.getBBox().getWidth() / 2);
                secondArea.getBBox().setWidth(firstArea.getBBox().getWidth());
                areas.add(firstArea);
                areas.add(secondArea);
                return areas;
            }

            @Override
            protected BlockRenderer createSplitRenderer() {
                return new TwoColumnParagraphRenderer((Paragraph) modelElement);
            }

            @Override
            protected BlockRenderer createOverflowRenderer() {
                return new TwoColumnParagraphRenderer((Paragraph) modelElement);
            }
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            text.append("A very long text is here...");
        }
        Paragraph twoColumnParagraph = new Paragraph();
        twoColumnParagraph.setRenderer(new TwoColumnParagraphRenderer(twoColumnParagraph));
        Text textElement = new Text(text.toString());
        twoColumnParagraph.add(textElement);
        document.add(twoColumnParagraph.setFont(new PdfType1Font(pdfDoc, new Type1Font(FontConstants.HELVETICA, ""))));

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        List<IRenderer> documentChildRenderers = document.getRenderer().getChildRenderers();
        for (int i = documentChildRenderers.size() - 1; i >= 0; i--)
            if (documentChildRenderers.get(i).getModelElement() == twoColumnParagraph) {
                TwoColumnParagraphRenderer renderer = (TwoColumnParagraphRenderer) documentChildRenderers.get(i);
                if (renderer.getChildRenderers().size() == 2) {
                    int len = ((TextRenderer)renderer.getChildRenderers().get(1)).getText().length();
                    textElement.setText(textElement.getText().substring(0, textElement.getText().length() - len));
                }
                break;
            }

        document.relayout();

        //Close document. Drawing of content is happened on close
        document.close();
    }

}
