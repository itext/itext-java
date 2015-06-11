package com.itextpdf.model;

import com.itextpdf.canvas.color.DeviceRgb;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AlignmentTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/AlignmentTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/AlignmentTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }


    @Test
    public void justifyAlignmentTest01() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        for (int i = 0; i < 21; i++) {
            paragraph.add(new Text ("Hello World! Hello People! " +
                    "Hello Sky! Hello Sun! Hello Moon! Hello Stars!").setBackgroundColor(DeviceRgb.Red));
        }
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAlignmentTest02() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        paragraph.add(new Text("Hello World!")).add(new Text(" ")).add(new Text("Hello People! ")).add("End");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAlignmentForcedNewlinesTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentForcedNewlinesTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentForcedNewlinesTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        paragraph.add("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAllTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "justifyAllTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAllTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED_ALL);
        paragraph.add("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
