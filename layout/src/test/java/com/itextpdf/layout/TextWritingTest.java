/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class TextWritingTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TextWritingTest/";
    public static final String destinationFolder = TestUtil.getOutputPath() + "/layout/TextWritingTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void textRiseTest01() throws IOException, InterruptedException {
        // CountryChunks example
        String outFileName = destinationFolder + "textRiseTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textRiseTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        for (int i = 0; i < 10; i++) {
            Paragraph p = new Paragraph().add("country").add(" ");
            Text id = new Text("id").
                    setTextRise(6).
                    setFont(font).
                    setFontSize(6).
                    setFontColor(ColorConstants.WHITE).
                    setBackgroundColor(ColorConstants.BLACK, 0, 0, 0, 0);
            p.add(id);
            document.add(p);
        }

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textRenderingModeTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textRenderingModeTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textRenderingModeTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Text text1 = new Text("This is a fill and stroke text").
                setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE).
                setStrokeColor(ColorConstants.RED).
                setStrokeWidth(0.1f);
        document.add(new Paragraph().add(text1));

        Text text2 = new Text("This is a stroke-only text").
                setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE).
                setStrokeColor(ColorConstants.GREEN).
                setStrokeWidth(0.3f);
        document.add(new Paragraph(text2));

        Text text3 = new Text("This is a colorful text").
                setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE).
                setStrokeColor(ColorConstants.BLUE).
                setStrokeWidth(0.3f).
                setFontColor(ColorConstants.GREEN).
                setFontSize(20);
        document.add(new Paragraph(text3));

        Text text4 = new Text("This is a stroke with dashes text").
                setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE).
                setStrokeColor(ColorConstants.BLUE).
                setStrokeWidth(0.5f).
                setFontColor(ColorConstants.PINK).
                setFontSize(20);
        text4.setDashPattern(new float[]{0.5f, 1f}, 0f);
        document.add(new Paragraph(text4));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textStrokeTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textStrokeTest.pdf";
        String cmpFileName = sourceFolder + "cmp_textStrokeTest.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument)) {
            Text text1 = new Text("Red stroke text via color setter")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE)
                    .setStrokeColor(ColorConstants.RED)
                    .setStrokeWidth(0.1f);
            document.add(new Paragraph().add(text1));

            Text text2 = new Text("Red transparent stroke text via setter with 2 parameters")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE)
                    .setStrokeColor(ColorConstants.RED, 0.5f)
                    .setStrokeWidth(0.1f);
            document.add(new Paragraph().add(text2));

            Text text3 = new Text("Red transparent stroke text via transparent color setter")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE)
                    .setStrokeColor(new TransparentColor(ColorConstants.RED, 0.5f))
                    .setStrokeWidth(0.1f);
            document.add(new Paragraph().add(text3));

            Text text4 = new Text("Red transparent stroke text via transparent color property")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE)
                    .setStrokeWidth(0.1f);
            text4.setProperty(Property.STROKE_COLOR, new TransparentColor(ColorConstants.RED, 0.5f));
            document.add(new Paragraph().add(text4));

            Text text5 = new Text("Red transparent stroke text via color property")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE)
                    .setStrokeWidth(0.1f);
            text5.setProperty(Property.STROKE_COLOR, ColorConstants.RED);
            document.add(new Paragraph().add(text5));
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textFillStrokeTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textFillStrokeTest.pdf";
        String cmpFileName = sourceFolder + "cmp_textFillStrokeTest.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Text text1 = new Text("Pink text with null stroke color (so font color is used)")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE)
                    .setFontColor(ColorConstants.PINK)
                    .setStrokeColor((Color) null)
                    .setStrokeWidth(2)
                    .setFontSize(50);
            document.add(new Paragraph().add(text1));

            Text text2 = new Text("Pink text with red half-transparent stroke")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE)
                    .setFontColor(ColorConstants.PINK)
                    .setStrokeColor(ColorConstants.RED, 0.5f)
                    .setStrokeWidth(2)
                    .setFontSize(50);
            document.add(new Paragraph().add(text2));

            Text text3 = new Text("Pink text with fully transparent stroke")
                    .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE)
                    .setFontColor(ColorConstants.PINK)
                    .setStrokeColor(new TransparentColor(ColorConstants.RED, 0f))
                    .setStrokeWidth(2)
                    .setFontSize(50);
            document.add(new Paragraph().add(text3));
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void leadingTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "leadingTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_leadingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p1 = new Paragraph("first, leading of 150")
                .setFixedLeading(150);
        document.add(p1);

        Paragraph p2 = new Paragraph("second, leading of 500")
                .setFixedLeading(500);
        document.add(p2);

        Paragraph p3 = new Paragraph();
        p3.add(new Text("third, leading of 20"))
                .setFixedLeading(20);
        document.add(p3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void leadingTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "leadingTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_leadingTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p1 = new Paragraph().add(new Text("Abdgsdfds ffs f dds").setFontSize(60)).add(new Text("fsd f dsf ds fds f ds").setFontSize(22))
                .setMultipliedLeading(1);
        document.add(p1);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void firstLineIndentTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "firstLineIndentTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_firstLineIndentTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.setProperty(Property.FIRST_LINE_INDENT, 25);

        document.add(new Paragraph("Portable Document Format (PDF) is a file format used to present documents in a manner " +
                "independent of application software, hardware, and operating systems.[2] Each PDF file encapsulates a complete " +
                "description of a fixed-layout flat document, including the text, fonts, graphics, and other information needed to " +
                "display it. In 1991, Adobe Systems co-founder John Warnock outlined a system called \"Camelot\"[3] that evolved into PDF."));

        document.add(new Paragraph("While Adobe Systems made the PDF specification available free of charge in 1993, PDF was a proprietary format, controlled " +
                "by Adobe, until it was officially released as an open standard on July 1, 2008, and published by the International Organization for" +
                " Standardization as ISO 32000-1:2008,[4][5] at which time control of the specification passed to an ISO Committee of volunteer industry " +
                "experts. In 2008, Adobe published a Public Patent License to ISO 32000-1 granting royalty-free rights for all patents owned by Adobe that " +
                "are necessary to make, use, sell and distribute PDF compliant implementations.[6] However, there are still some proprietary technologies" +
                " with published specification defined only by Adobe, such as Adobe XML Forms Architecture, and JavaScript for Acrobat, which are referenced " +
                "by ISO 32000-1 as normative and indispensable for the application of ISO 32000-1 specification.[7][8][9][10][11] The ISO committee is actively" +
                " standardizing many of these as part of ISO 32000-2.").
                setFirstLineIndent(50));


        document.add(new AreaBreak());
        document.add(new Paragraph("During Harry's fourth year of school (detailed in Harry Potter and the Goblet of Fire), Harry is unwillingly entered as a participant in the Triwizard Tournament, a dangerous contest where Harry must compete against a witch and a wizard \"champion\" from visiting schools as well as another Hogwarts student, causing Harry's friends to distance themselves from him.[18] Harry is guided through the tournament by their new Defence Against the Dark Arts professor, Alastor \"Mad-Eye\" Moody, who turns out to be an impostor\u2014one of Voldemort's supporters named Barty Crouch, Jr. in disguise. The point at which the mystery is unravelled marks the series' shift from foreboding and uncertainty into open conflict. Voldemort's plan to have Crouch use the tournament to bring Harry to Voldemort succeeds. Although Harry manages to escape, Cedric Diggory, the other Hogwarts champion in the tournament, is killed by Peter Pettigrew and Voldemort re-enters the wizarding world with a physical body.\n" +
                "\n" +
                "In the fifth book, Harry Potter and the Order of the Phoenix, Harry must confront the newly resurfaced Voldemort. In response to Voldemort's reappearance, Dumbledore re-activates the Order of the Phoenix, a secret society which works from Sirius Black's dark family home to defeat Voldemort's minions and protect Voldemort's targets, especially Harry. Despite Harry's description of Voldemort's recent activities, the Ministry of Magic and many others in the magical world refuse to believe that Voldemort has returned.[19] In an attempt to counter and eventually discredit Dumbledore, who along with Harry is the most prominent voice in the wizarding world attempting to warn of Voldemort's return, the Ministry appoints Dolores Umbridge as the High Inquisitor of Hogwarts and the new Defence Against the Dark Arts teacher. She transforms the school into a dictatorial regime and refuses to allow the students to learn ways to defend themselves against dark magic.[19]\n" +
                "\n" +
                "With Ron and Hermione's suggestion, Harry reluctantly forms \"Dumbledore's Army,\" a secret study group aimed to teach his classmates the higher-level skills of Defence Against the Dark Arts that he has learned from his previous encounters with Dark wizards. An important prophecy concerning Harry and Lord Voldemort is revealed,[20] and Harry discovers that he and Voldemort have a painful connection, allowing Harry to view some of Voldemort's actions telepathically. In the novel's climax, Harry and his friends face off against Voldemort's Death Eaters at the Ministry of Magic. Although the timely arrival of members of the Order of the Phoenix saves the children's lives, Sirius Black is killed in the conflict.\n" +
                "\n" +
                "In the sixth book, Harry Potter and the Half-Blood Prince, Voldemort begins waging open warfare. Harry and his friends are relatively protected from that danger at Hogwarts. They are subject to all the difficulties of adolescence\u2014Harry eventually begins dating Ginny, Ron establishes a strong infatuation with fellow Hogwarts student Lavender Brown, and Hermione starts recognising her loving feelings for Ron. Near the beginning of the novel, lacking his own book, Harry is given an old potions textbook filled with many annotations and recommendations signed by a mysterious writer; \"the Half-Blood Prince.\" This book is a source of scholastic success and great recognition from their new potions master, Horace Slughorn, but because of the potency of the spells that are written in it, becomes a source of concern. Harry takes private lessons with Dumbledore, who shows him various memories concerning the early life of Voldemort in a device called a Pensieve. These reveal that in order to preserve his life, Voldemort has split his soul into pieces, creating a series of horcruxes - evil enchanted items hidden in various locations, one of which was the diary destroyed in the second book.[21] Harry's snobbish adversary, Draco Malfoy, attempts to attack Dumbledore, and the book culminates in the killing of Dumbledore by Professor Snape, the titular Half-Blood Prince.\n" +
                "\n" +
                "Harry Potter and the Deathly Hallows, the last book in the series, begins directly after the events of the sixth book. Lord Voldemort has completed his ascension to power and gains control of the Ministry of Magic. Harry, Ron and Hermione drop out of school so that they can find and destroy Voldemort's remaining horcruxes. To ensure their own safety as well as that of their family and friends, they are forced to isolate themselves. As they search for the horcruxes, the trio learns details about Dumbledore's past, as well as Snape's true motives\u2014he had worked on Dumbledore's behalf since the murder of Harry's mother.\n" +
                "\n" +
                "The book culminates in the Battle of Hogwarts. Harry, Ron and Hermione, in conjunction with members of the Order of the Phoenix and many of the teachers and students, defend Hogwarts from Voldemort, his Death Eaters, and various dangerous magical creatures. Several major characters are killed in the first wave of the battle, including Remus Lupin and Fred Weasley. After learning that he himself is a horcrux, Harry surrenders himself to Voldemort in the Forbidden Forest, who casts a killing curse (Avada Kedavra) at him. The defenders of Hogwarts do not surrender after learning of Harry's presumed death and continue to fight on. Harry awakens and faces Voldemort, whose horcruxes have all been destroyed. In the final battle, Voldemort's killing curse rebounds off Harry's defensive spell (Expelliarmus) killing Voldemort.\n" +
                "\n" +
                "An epilogue describes the lives of the surviving characters and the effects of Voldemort's death on the wizarding world."));


        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void charSpacingTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "charSpacingTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_charSpacingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Paragraph p = new Paragraph().setFont(font);
        p.add("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n");
        p.setCharacterSpacing(4);
        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void wordSpacingTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSpacingTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSpacingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Paragraph p = new Paragraph().setFont(font);
        p.add("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device. ");
        p.add(new Text("You can collapse parts of the document and focus.").setBackgroundColor(ColorConstants.GREEN));
        p.setWordSpacing(15);
        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fontStyleSimulationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fontStyleSimulationTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_fontStyleSimulationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("I'm underlined").setUnderline());
        document.add(new Paragraph("I'm strikethrough").setLineThrough());
        document.add(new Paragraph(new Text("I'm a bold simulation font").setBackgroundColor(ColorConstants.GREEN)).simulateBold());
        document.add(new Paragraph(new Text("I'm an italic simulation font").setBackgroundColor(ColorConstants.GREEN)).simulateItalic());
        document.add(new Paragraph(new Text("I'm a super bold italic underlined linethrough piece of text and no one can be better than me, even if " +
                "such a long description will cause me to occupy two lines").setBackgroundColor(ColorConstants.GREEN))
                .simulateItalic().simulateBold().setUnderline().setLineThrough());

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void bigWordTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "bigWordTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_bigWordTest01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph();
        p.setWidth(150);
        p.setBackgroundColor(ColorConstants.RED);
        p.add("Hello ho ho ho ");
        p.add("LongWordThatDoNotFitInALine");
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.add(p);

        p = new Paragraph();
        p.setWidth(150);
        p.setBackgroundColor(ColorConstants.RED);
        p.add("LongWordThatDoNotFitInALine World");
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.add(p);

        p = new Paragraph();
        p.setWidth(150);
        p.setBackgroundColor(ColorConstants.RED);
        p.add("World LongWordThatDoNotFitInALine");
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.add(p);

        p = new Paragraph();
        p.setWidth(150);
        p.setBackgroundColor(ColorConstants.RED);
        p.add("World ");
        p.add("LongWordThatDoNotFitInALine");
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.add(p);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void underlineTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "underline.pdf";
        String cmpFileName = sourceFolder + "cmp_underline.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("Text");
        p.setUnderline(1, 0);
        p.setUnderline(1, 5);
        p.setUnderline(1, 10);

        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void strokedUnderlineTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "strokedUnderline.pdf";
        String cmpFileName = sourceFolder + "cmp_strokedUnderline.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph("Yellow text with pink stroked dashed underline.")
                    .setFontSize(45).setFontColor(ColorConstants.YELLOW);
            Underline underline = new Underline(null, 0, 0.1f, 0, -0.1f, PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeWidth(2).setStrokeColor(new TransparentColor(ColorConstants.PINK, 0.5f))
                    .setDashPattern(new float[]{5, 5, 10, 5}, 5);
            p.setUnderline(underline);

            TransparentColor strokeColor = new TransparentColor(ColorConstants.GREEN, 0.5f);
            Paragraph p2 = new Paragraph("Text with line-through and default underline.")
                    .setFontSize(50).setStrokeWidth(1).setFontColor(ColorConstants.DARK_GRAY)
                    .setStrokeColor(strokeColor);
            Underline underline2 = new Underline(ColorConstants.DARK_GRAY, 0, 0.1f, 0, 0.3f,
                    PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeWidth(1).setStrokeColor(strokeColor);
            p2.setUnderline(underline2);
            p2.setUnderline();

            Paragraph p3 = new Paragraph("Text with transparent color and default overline.").setFontSize(50)
                    .setFontColor(new TransparentColor(ColorConstants.BLUE, 0));
            Underline underline3 = new Underline(null, 0, 0.1f, 0, 0.9f, PdfCanvasConstants.LineCapStyle.BUTT);
            p3.setUnderline(underline3);
            p3.setBackgroundColor(ColorConstants.PINK);

            Paragraph p4 = new Paragraph("Text with null font color and default overline.").setFontSize(50)
                    .setFontColor((TransparentColor) null);
            p4.setUnderline(underline3);

            document.add(p);
            document.add(p2);
            document.add(p3);
            document.add(p4);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lineThroughTest() throws IOException, InterruptedException {
        //TODO: update after DEVSIX-2623 fix
        String outFileName = destinationFolder + "lineThrough.pdf";
        String cmpFileName = sourceFolder + "cmp_lineThrough.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        Text textUp = new Text("textRise10f_with_lineThrough");
        textUp.setTextRise(10f);
        textUp.setLineThrough();
        Text textDown = new Text("textRise-10f_with_lineThrough");
        textDown.setTextRise(-10f);
        textDown.setLineThrough();
        Paragraph n = new Paragraph("baseline");
        n.add(textUp).add(textDown);
        document.add(n);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void textInitializationWithNullValueThrowsException() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> new Text(null));
        Assertions.assertEquals(LayoutExceptionMessageConstant.TEXT_CONTENT_CANNOT_BE_NULL, e.getMessage());
    }

    @Test
    // TODO: update cmp file after fixing DEVSIX-4604
    public void leadingAndFloatInTextTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "leadingAndFloatInText.pdf";
        String cmpFileName = sourceFolder + "cmp_leadingAndFloatInText.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().setFixedLeading(30).setBorder(new SolidBorder(ColorConstants.RED, 2));
        p.add("First text");

        Text text = new Text("Second text with float ");
        text.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        p.add(text);

        document.add(p);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void textWrappingEpsilonTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textWrappingEpsilon.pdf";
        String cmpFileName = sourceFolder + "cmp_textWrappingEpsilon.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Play with margins to make AbstractRenderer.EPS important for wrapping behavior
        document.setLeftMargin(250.0F);
        document.setRightMargin(238.727F);
        pdfDoc.setDefaultPageSize(PageSize.LETTER);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "../fonts/Open_Sans/OpenSans-Regular.ttf");

        String text1 = "First line of some text ";
        String text2 = "Second line of some text";

        Text text = new Text(text1);
        text.setFont(font);
        text.setFontSize(9);
        Paragraph paragraph = new Paragraph();
        paragraph.add(text);

        text = new Text(text2);
        text.setFont(font);
        text.setFontSize(9);
        paragraph.add(text);

        paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        document.add(paragraph);
        document.close();
        writer.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }
}
