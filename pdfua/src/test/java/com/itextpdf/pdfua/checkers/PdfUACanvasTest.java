/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfUACanvasTest extends ExtendedITextTest {
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUACanvasTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUACanvasTest/";

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkPoint_01_005_TextContentIsNotTagged() throws IOException {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText("Hello World!");
        });
        Assert.assertEquals(
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextContentIsCorrectlyTaggedAsContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextContentIsCorrectlyTaggedAsContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(page1)
                .addTag(StandardRoles.P);

        canvas
                .openTag(tagPointer.getTagReference())
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .restoreState()
                .closeTag();
        pdfDoc.close();
        Assert.assertNull(
                new CompareTool().compareByContent(outPdf,
                        SOURCE_FOLDER + "cmp_01_005_TextContentIsCorrectlyTaggedAsContent.pdf",
                        DESTINATION_FOLDER,
                        "diff_")
        );
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_TextContentIsNotInTagTree() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextContentIsNotInTagTree.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        canvas
                .openTag(new CanvasTag(PdfName.P))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText("Hello World!");
        });
        Assert.assertEquals(PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextArtifactIsNotInTagTree() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextArtifactIsNotInTagTree.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        canvas
                .openTag(new CanvasTag(PdfName.Artifact))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .restoreState()
                .closeTag();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                        SOURCE_FOLDER + "cmp_01_005_TextArtifactIsNotInTagTree.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_TextContentWithMCIDButNotInTagTree() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextContentWithMCIDButNotInTagTree.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        canvas
                .openTag(new CanvasTag(PdfName.P, 99))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200);

        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText("Hello World!");
        });
        Assert.assertEquals(PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineContentIsTaggedButNotInTagTree() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextGlyphLineContentIsTagged.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        GlyphLine glyphLine = font.createGlyphLine("Hello World!");
        canvas.saveState()
                .openTag(new CanvasTag(PdfName.H1))
                .setFontAndSize(font, 12)
                .beginText()
                .moveText(200, 200)
                .setColor(ColorConstants.RED, true);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText(glyphLine);
        });
        Assert.assertEquals(PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineContentIsArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextGlyphLineContentIsArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        GlyphLine glyphLine = font.createGlyphLine("Hello World!");
        canvas.saveState()
                .openTag(new CanvasTag(PdfName.Artifact))
                .setFontAndSize(font, 12)
                .beginText()
                .moveText(200, 200)
                .setColor(ColorConstants.RED, true)
                .showText(glyphLine)
                .endText()
                .closeTag()
                .restoreState();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_TextGlyphLineContentIsArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineContentIsContentCorrect() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextGlyphLineContentIsContentCorrect.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        GlyphLine glyphLine = font.createGlyphLine("Hello World!");

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(pdfDoc.getFirstPage())
                .addTag(StandardRoles.H1);

        canvas.saveState()
                .openTag(tagPointer.getTagReference())
                .setFontAndSize(font, 12)
                .beginText()
                .moveText(200, 200)
                .setColor(ColorConstants.RED, true)
                .showText(glyphLine)
                .endText()
                .closeTag()
                .restoreState();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_TextGlyphLineContentIsContentCorrect.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_allowPureBmcInArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_allowPureBmcInArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        GlyphLine glyphLine = font.createGlyphLine("Hello World!");
        canvas.saveState()
                .openTag(new CanvasTag(PdfName.Artifact))
                .setFontAndSize(font, 12)
                .beginMarkedContent(PdfName.P)
                .beginText()
                .moveText(200, 200)
                .setColor(ColorConstants.RED, true)
                .showText(glyphLine)
                .endMarkedContent()
                .endText()
                .closeTag()
                .restoreState();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_allowPureBmcInArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_allowNestedPureBmcInArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_allowNestedPureBmcInArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        GlyphLine glyphLine = font.createGlyphLine("Hello World!");
        canvas.saveState()
                .openTag(new CanvasTag(PdfName.Artifact))
                .setFontAndSize(font, 12)
                .beginMarkedContent(PdfName.P)
                .openTag(new CanvasTag(PdfName.Artifact))
                .beginText()
                .moveText(200, 200)
                .setColor(ColorConstants.RED, true)
                .showText(glyphLine)
                .closeTag()
                .endMarkedContent()
                .endText()
                .closeTag()
                .restoreState();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_allowNestedPureBmcInArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsContentIsNotTagged() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setColor(ColorConstants.RED, true)
                .setLineWidth(2);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.lineTo(200, 200);
        });
        Assert.assertEquals(
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.openTag(new CanvasTag(PdfName.H1))
                .setColor(ColorConstants.RED, true)
                .setLineWidth(2);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.lineTo(200, 200);
        });
        Assert.assertEquals(PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsMarkedAsArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_LineContentThatIsMarkedAsArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(pdfDoc.getFirstPage())
                .addTag(StandardRoles.H);
        canvas
                .openTag(tagPointer.getTagReference())
                .saveState()
                .setStrokeColor(ColorConstants.MAGENTA)
                .moveTo(300, 300)
                .lineTo(400, 350)
                .stroke()
                .restoreState()
                .closeTag();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_LineContentThatIsMarkedAsArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_RectangleNotMarked() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setColor(ColorConstants.RED, true)
                .setLineWidth(2);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
        });
        Assert.assertEquals(
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_RectangleMarkedArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas
                .saveState()
                .openTag(new CanvasTag(PdfName.Artifact))
                .setFillColor(ColorConstants.RED)
                .rectangle(new Rectangle(200, 200, 100, 100))
                .fill()
                .closeTag()
                .restoreState();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_RectangleMarkedArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedContentWithoutMcid() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_005_RectangleMarkedContentWithoutMcid.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas
                .saveState()
                .openTag(new CanvasTag(PdfName.Art))
                .setFillColor(ColorConstants.RED);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
        });
        Assert.assertEquals(PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_RectangleMarkedContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(pdfDoc.getFirstPage())
                .addTag(StandardRoles.H);

        canvas
                .saveState()
                .openTag(tagPointer.getTagReference())
                .setFillColor(ColorConstants.RED)
                .rectangle(new Rectangle(200, 200, 100, 100))
                .fill()
                .closeTag()
                .restoreState();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_RectangleMarkedContent.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_004_bezierMarkedAsContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_004_bezierCurveShouldBeTagged.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(pdfDoc.getFirstPage())
                .addTag(StandardRoles.DIV);

        canvas
                .saveState()
                .openTag(tagPointer.getTagReference())
                .setColor(ColorConstants.RED, true)
                .setLineWidth(5)
                .setStrokeColor(ColorConstants.RED)
                .arc(400, 400, 500, 500, 30, 50)
                .stroke()
                .closeTag()
                .restoreState();

        pdfDoc.close();
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_004_bezierCurveShouldBeTagged.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void checkPoint_01_004_bezierMarkedAsArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_004_bezierMarkedAsArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas
                .saveState()
                .openTag(new CanvasTag(PdfName.Artifact))
                .setColor(ColorConstants.RED, true)
                .setLineWidth(5)
                .setStrokeColor(ColorConstants.RED)
                .arc(400, 400, 500, 500, 30, 50)
                .stroke()
                .closeTag()
                .restoreState();

        pdfDoc.close();
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_004_bezierMarkedAsArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void checkPoint_01_004_bezierCurveInvalidMCID() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_004_bezierCurveInvalidMCID.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas
                .saveState()
                .openTag(new CanvasTag(PdfName.P, 420))
                .setColor(ColorConstants.RED, true)
                .setLineWidth(5)
                .setStrokeColor(ColorConstants.RED);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.arc(400, 400, 500, 500, 30, 50);
        });
        Assert.assertEquals(PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_RandomOperationsWithoutActuallyAddingContent()
            throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_RandomOperationsWithoutActuallyAddingContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas
                .setColor(ColorConstants.RED, true)
                .setLineCapStyle(1)
                .setTextMatrix(20, 2)
                .setLineWidth(2);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_RandomOperationsWithoutActuallyAddingContent.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_003_ContentMarkedAsArtifactsPresentInsideTaggedContent() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_003_ContentMarkedAsArtifactsPresentInsideTaggedContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(page1)
                .addTag(StandardRoles.P);

        canvas
                .openTag(tagPointer.getTagReference())
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText();
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.openTag(new CanvasTag(PdfName.Artifact));
        });
        Assert.assertEquals(
                PdfUAExceptionMessageConstants.ARTIFACT_CANT_BE_INSIDE_REAL_CONTENT,
                e.getMessage());
    }

    @Test
    public void checkPoint_validRoleAddedInsideMarkedContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "validRoleAddedInsideMarkedContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        //Have to use low level tagging otherwise it throws error earlier
        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
        PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));

        canvas
                .openTag(new CanvasTag(mcr))
                .saveState()
                .beginMarkedContent(PdfName.P)
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .endMarkedContent()
                .restoreState()
                .closeTag();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                        SOURCE_FOLDER + "cmp_validRoleAddedInsideMarkedContent.pdf",
                DESTINATION_FOLDER, "diff_")
        );
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_validRoleAddedInsideMarkedContentMultiple() throws IOException, InterruptedException {

        String outPdf = DESTINATION_FOLDER + "validRoleAddedInsideMarkedContentMultiple.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        //Have to use low level tagging otherwise it throws error earlier
        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
        PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));

        canvas
                .openTag(new CanvasTag(mcr))
                .saveState()
                .beginMarkedContent(PdfName.P)
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .endMarkedContent()
                .beginMarkedContent(PdfName.H1)
                .beginText()
                .showText("Hello but nested")
                .endText()
                .endMarkedContent()
                .restoreState()
                .closeTag();
        pdfDoc.close();

        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_validRoleAddedInsideMarkedContentMultiple.pdf",
                DESTINATION_FOLDER, "diff_")
        );
    }

    @Test
    public void checkPoint_validRoleAddedInsideMarkedContentMCR_IN_MCR() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "validRoleAddedInsideMarkedContentMCR_IN_MCR.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
        PdfStructElem paragraph2 = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));

        PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));
        PdfMcr mcr1 = paragraph2.addKid(new PdfMcrNumber(page1, paragraph2));

        canvas
                .openTag(new CanvasTag(mcr))
                .saveState()
                .beginMarkedContent(PdfName.P)
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .endMarkedContent()
                .openTag(new CanvasTag(mcr1))
                .beginMarkedContent(PdfName.H1)
                .beginText()
                .showText("Hello but nested")
                .endText()
                .endMarkedContent()
                .closeTag()
                .restoreState()
                .closeTag();
        pdfDoc.close();

        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_validRoleAddedInsideMarkedContentMCR_IN_MCR.pdf",
                DESTINATION_FOLDER, "diff_")
        );

    }

    @Test
    public void checkPoint_01_004_TaggedContentShouldNotBeInsideArtifact() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_004_TaggedContentShouldNotBeInsideArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(page1)
                .addTag(StandardRoles.P);

        canvas
                .openTag(new CanvasTag(PdfName.Artifact))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText();
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.openTag(tagPointer.getTagReference());
        });
        Assert.assertEquals(
                PdfUAExceptionMessageConstants.REAL_CONTENT_CANT_BE_INSIDE_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_31_009_FontIsNotEmbedded() throws IOException {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
        TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
        tagPointer.setPageForTagging(pdfDoc.getFirstPage());
        tagPointer.addTag(StandardRoles.P);
        canvas.beginText()
                .openTag(tagPointer.getTagReference())
                .setFontAndSize(font, 12)
                .showText("Please crash on close, tyvm")
                .endText()
                .closeTag();
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> {
            pdfDoc.close();
        });
        Assert.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "Courier"),
                e.getMessage());
    }
}
