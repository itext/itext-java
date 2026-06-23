package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class FootnotesTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/FootnotesTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private static final String PART1_1 = "Tiger! Tiger! burning bright\n"
            + "In the forests of the night,\n"
            + "What immortal";
    private static final String PART1_2 = " hand or eye\n"
            + "Could frame thy fearful symmetry?\n\n";

    private static final String PART2_1 = "In what distant deeps";

    private static final String PART2_2 = " or skies\n"
            + "Burnt the fire of thine eyes?\n"
            + "On what wings dare he aspire?\n"
            + "What the hand dare seize the fire?";

    private static final String PART3 = "And what shoulder, and what art,\n"
            + "Could twist the sinews of thy heart?\n"
            + "And when thy heart began to beat,\n"
            + "What dread hand? and what dread feet?";

    private static final String NOTE1 = "immortal (adjective): never dying";
    private static final String NOTE2 = "deeps (noun): seas";


    private static final String IMG1 = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";
    private static final String IMG2 = "./src/test/resources/com/itextpdf/pdfua/img/FOX.bmp";

    public static List<PdfConformance> conformances() {
        return UaValidationTestFramework.getConformanceList(true);
    }

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void testFootnoteTagging(PdfConformance conformance) throws IOException {
        PdfConformance pdfConformance = conformance;
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {

                Document document = new Document(pdfDoc);
                PdfFont font = null;
                font = PdfFontFactory.createFont(FONT,
                        "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
                document.setFont(font);

                Div div1 = new Div();

                Footnote footnote = new Footnote(NOTE1);
                footnote.setBackgroundColor(ColorConstants.CYAN);
                FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
                Footnote footnote2 = new Footnote(new Paragraph(NOTE2).setMargin(0));
                footnote2.setBackgroundColor(ColorConstants.ORANGE);
                FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);

                Paragraph p = new Paragraph(PART1_1);
                p.add(anchor);
                p.add(PART1_2);
                div1.add(p);
                p = new Paragraph(PART2_1);
                p.add(PART2_1);
                p.add(anchor2);
                p.add(PART2_2);
                div1.add(p);
                div1.add(new Paragraph(PART3));

                div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
                document.add(div1);
            } catch (IOException e) {
                throw new RuntimeException("Error creating test document", e);
            }
        });
        framework.assertBothValid("footnotes");
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void testFootnoteTaggingImages(PdfConformance conformance) throws IOException {
        PdfConformance pdfConformance = conformance;
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {

                Document document = new Document(pdfDoc);
                PdfFont font = null;
                font = PdfFontFactory.createFont(FONT,
                        "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
                document.setFont(font);

                Div div1 = new Div();

                Footnote footnote = new Footnote(NOTE1);
                footnote.setBackgroundColor(ColorConstants.CYAN);
                Image anchorImage = new Image(ImageDataFactory.create(IMG1));
                anchorImage.setWidth(10);
                anchorImage.getAccessibilityProperties().setAlternateDescription("dog");
                FootnoteAnchor anchor = new FootnoteAnchor(anchorImage, footnote);
                Footnote footnote2 = new Footnote(new Paragraph(NOTE2).setMargin(0));
                footnote2.setBackgroundColor(ColorConstants.ORANGE);
                anchorImage = new Image(ImageDataFactory.create(IMG2));
                anchorImage.setWidth(10);
                anchorImage.getAccessibilityProperties().setAlternateDescription("fox");
                FootnoteAnchor anchor2 = new FootnoteAnchor(anchorImage, footnote2);

                Paragraph p = new Paragraph(PART1_1);
                p.add(anchor);
                p.add(PART1_2);
                div1.add(p);
                p = new Paragraph(PART2_1);
                p.add(PART2_1);
                p.add(anchor2);
                p.add(PART2_2);
                div1.add(p);
                div1.add(new Paragraph(PART3));

                div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
                document.add(div1);
            } catch (IOException e) {
                throw new RuntimeException("Error creating test document", e);
            }
        });
        framework.assertBothValid("UA2IMG");
    }


    @ParameterizedTest
    @MethodSource("conformances")
    public void testFootnoteTableTagging(PdfConformance conformance) throws IOException {
        PdfConformance pdfConformance = conformance;
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {

                Document document = new Document(pdfDoc);
                PdfFont font = null;
                font = PdfFontFactory.createFont(FONT,
                        "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
                document.setFont(font);

                Footnote footnote = new Footnote("Footnote text");
                footnote.setBackgroundColor(ColorConstants.PINK);
                FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
                Footnote footnote2 = new Footnote("Footnote text 2");
                footnote2.setBackgroundColor(ColorConstants.YELLOW);
                FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);
                Footnote footnote3 = new Footnote("Footnote text 3");
                footnote.setBackgroundColor(ColorConstants.PINK);
                FootnoteAnchor anchor3 = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote3);
                Footnote footnote4 = new Footnote("Footnote text 4");
                footnote2.setBackgroundColor(ColorConstants.YELLOW);
                FootnoteAnchor anchor4 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote4);

                Table table = new Table(4);
                for (int i = 0; i < 120; ++i) {
                    Paragraph paragraph = new Paragraph("Cell " + i);
                    if (i == 1) {
                        paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                    }
                    if (i == 5) {
                        paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                    }

                    if (i == 119) {
                        paragraph.add(anchor4).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                    }
                    if (i == 100) {
                        paragraph.add(anchor3).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                    }
                    if (i < 4) {
                        table.addHeaderCell(
                                new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                    } else if (i > 115) {
                        table.addFooterCell(
                                new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.BLUE, 2)));
                    } else {
                        table.addCell(paragraph);
                    }
                }
                document.add(table);
            } catch (IOException e) {
                throw new RuntimeException("Error creating test document", e);
            }
         });
        framework.assertBothValid("footnotesTables");
    }
}
