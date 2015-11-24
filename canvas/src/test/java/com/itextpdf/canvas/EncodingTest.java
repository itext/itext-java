package com.itextpdf.canvas;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

@Category(IntegrationTest.class)
public class EncodingTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/EncodingTest/";
    static final public String outputFolder = "./target/test/com/itextpdf/canvas/EncodingTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(outputFolder).mkdirs();
    }

    @Test
    public void surrogatePairTest() throws IOException, InterruptedException {
        String fileName = "surrogatePairTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, sourceFolder + "DejaVuSans.ttf", PdfEncodings.IDENTITY_H);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\uD800\uDF1D").
                endText().
                restoreState();
        canvas.release();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void exoticCharsTimesRomanTest() throws IOException, InterruptedException {
        String fileName = "exoticCharsTimesRomanTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, FontConstants.TIMES_ROMAN, PdfEncodings.WINANSI);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\u0188").
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void exoticCharsTimesRomanWithDifferencesTest() throws IOException, InterruptedException {
        String fileName = "exoticCharsTimesRomanWithDifferencesTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, FontConstants.TIMES_ROMAN, "# simple 32 0020 0188");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\u0188").
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void exoticCharsCourierTest() throws IOException, InterruptedException {
        String fileName = "exoticCharsCourierTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, FontConstants.COURIER, PdfEncodings.WINANSI);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\u0188").
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void exoticCharsCourierWithDifferencesTest() throws IOException, InterruptedException {
        String fileName = "exoticCharsCourierWithDifferencesTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, FontConstants.COURIER, "# simple 32 0020 0188");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\u0188").
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void exoticCharsFreeSansTest() throws IOException, InterruptedException {
        String fileName = "exoticCharsFreeSansTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeSans.ttf", PdfEncodings.WINANSI);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\u0188").
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void exoticCharsFreeSansWithDifferencesTest() throws IOException, InterruptedException {
        String fileName = "exoticCharsFreeSansWithDifferencesTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeSans.ttf", "# simple 32 0020 0188");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("\u0188").
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolDefaultFontTest() throws IOException, InterruptedException {
        String fileName = "symbolDefaultFontTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, FontConstants.SYMBOL, PdfEncodings.WINANSI);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        String str = new String();
        for (int i = 32; i <= 100; i++) {
            str+= (char)i;
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText(str).
                endText();

        str = "";
        for (int i = 101; i <= 190; i++) {
            str+= (char)i;
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 12).
                showText(str).
                endText();
        str = "";
        for (int i = 191; i <= 254; i++) {
            str+= (char)i;
        }
        canvas.
                beginText().
                moveText(36, 766).
                showText(str).
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test@Ignore("DEVSIX-346")
    public void symbolFontWinansiTest() throws IOException, InterruptedException {
        String fileName = "symbolFontTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, sourceFolder + "Symbols1.ttf", PdfEncodings.WINANSI);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        String str = new String();
        for (int i = 32; i <= 100; i++) {
            str+= (char)i;
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText(str).
                endText();

        str = "";
        for (int i = 101; i <= 190; i++) {
            str+= (char)i;
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 12).
                showText(str).
                endText();
        str = "";
        for (int i = 191; i <= 254; i++) {
            str+= (char)i;
        }
        canvas.
                beginText().
                moveText(36, 766).
                showText(str).
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test@Ignore("DEVSIX-346")
    public void symbolFontIdentityHTest() throws IOException, InterruptedException {
        String fileName = "symbolFontTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFont.createFont(doc, sourceFolder + "Symbols1.ttf", PdfEncodings.IDENTITY_H);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        String str = new String();
        for (int i = 32; i <= 100; i++) {
            str+= (char)i;
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText(str).
                endText();

        str = "";
        for (int i = 101; i <= 190; i++) {
            str+= (char)i;
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 12).
                showText(str).
                endText();
        str = "";
        for (int i = 191; i <= 254; i++) {
            str+= (char)i;
        }
        canvas.
                beginText().
                moveText(36, 766).
                showText(str).
                endText().
                restoreState();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }
}
