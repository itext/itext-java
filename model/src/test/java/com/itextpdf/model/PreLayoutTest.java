package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PreLayoutTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/PreLayoutTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/PreLayoutTest/";

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

}
