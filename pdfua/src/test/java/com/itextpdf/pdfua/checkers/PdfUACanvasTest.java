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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
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
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua
// validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfUACanvasTest extends ExtendedITextTest {
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/pdfua/font/";

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUACanvasTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUACanvasTest/";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void checkPoint_01_005_TextContentIsNotTagged() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(getFont(), 10)
                    .showText("Hello World!");

        });
        framework.assertBothFail("checkPoint_01_005_TextContentIsNotTagged",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_TextNoContentIsNotTagged() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(getFont(), 10)
                    .endText();

        });
        framework.assertBothValid("checkPoint_01_005_TextNoContentIsNotTagged");
    }


    @Test
    public void checkPoint_01_005_TextContentIsCorrectlyTaggedAsContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextContentIsCorrectlyTaggedAsContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
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
        Assertions.assertNull(
                new CompareTool().compareByContent(outPdf,
                        SOURCE_FOLDER + "cmp_01_005_TextContentIsCorrectlyTaggedAsContent.pdf",
                        DESTINATION_FOLDER,
                        "diff_")
        );
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_TextContentIsNotInTagTree() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextContentIsNotInTagTree.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        canvas
                .openTag(new CanvasTag(PdfName.P))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText("Hello World!");
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextArtifactIsNotInTagTree() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextArtifactIsNotInTagTree.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_TextArtifactIsNotInTagTree.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_TextContentWithMCIDButNotInTagTree() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextContentWithMCIDButNotInTagTree.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        canvas
                .openTag(new CanvasTag(PdfName.P, 99))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200);

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText("Hello World!");
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineContentIsTaggedButNotInTagTree() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextGlyphLineContentIsTagged.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        GlyphLine glyphLine = font.createGlyphLine("Hello World!");
        canvas.saveState()
                .openTag(new CanvasTag(PdfName.H1))
                .setFontAndSize(font, 12)
                .beginText()
                .moveText(200, 200)
                .setColor(ColorConstants.RED, true);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText(glyphLine);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineInBadStructure() throws IOException {
        String outPdf = DESTINATION_FOLDER + "checkPoint_01_005_TextGlyphLineInBadStructure.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage()) {

            @Override
            public PdfCanvas openTag(CanvasTag tag) {
                // disable the checkIsoConformance call check by simulating  generating not tagged content
                // same as in annotations of formfields.
                setDrawingOnPage(false);
                super.openTag(tag);
                setDrawingOnPage(true);
                return this;
            }
        };

        GlyphLine glyphLine = font.createGlyphLine("Hello World!");

        TagTreePointer pointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
        pointer.addTag(StandardRoles.DIV);
        pointer.setPageForTagging(pdfDoc.getFirstPage());
        canvas.saveState();
        canvas.openTag(pointer.getTagReference());
        canvas.openTag(new CanvasArtifact());
        pointer.addTag(StandardRoles.P);
        canvas.openTag(pointer.getTagReference());
        canvas.setFontAndSize(font, 12);
        canvas.beginText();
        canvas.moveText(200, 200);
        canvas.setColor(ColorConstants.RED, true);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText(glyphLine);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.REAL_CONTENT_INSIDE_ARTIFACT_OR_VICE_VERSA,
                e.getMessage());
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineContentIsArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextGlyphLineContentIsArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_TextGlyphLineContentIsArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_TextGlyphLineContentIsContentCorrect() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_TextGlyphLineContentIsContentCorrect.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_TextGlyphLineContentIsContentCorrect.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_allowPureBmcInArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_allowPureBmcInArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_allowPureBmcInArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_allowNestedPureBmcInArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_allowNestedPureBmcInArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_allowNestedPureBmcInArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsContentIsNotTagged() throws IOException {

        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200).fill();
        });
        framework.assertBothFail("checkPoint_01_005_LineContentThatIsContentIsNotTagged",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsContentIsNotTagged_noContent() throws IOException {

        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200);
        });
        framework.assertBothValid("checkPoint_01_005_LineContentThatIsContentIsNotTagged_noContent");
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact() throws IOException {

        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            canvas.openTag(new CanvasTag(PdfName.P))
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200).fill();
        });

        framework.assertBothFail("checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact",
                PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT, false);
    }


    @Test
    public void checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact_no_drawing() throws IOException {

        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            canvas.openTag(new CanvasTag(PdfName.P))
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200);
            canvas.lineTo(300, 200);

        });
        framework.assertBothValid("checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact_no_drawing");
    }

    @Test
    public void checkPoint_01_005_LineContentThatIsMarkedAsArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_LineContentThatIsMarkedAsArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_LineContentThatIsMarkedAsArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_RectangleNotMarked() throws IOException {

        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.fill();
        });
        framework.assertBothFail("checkPoint_01_005_RectangleNotMarked",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }


    @Test
    public void checkPoint_01_005_RectangleNoContent() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
        });
        framework.assertBothValid("checkPoint_01_005_RectangleNoContent");
    }


    @Test
    public void checkPoint_01_005_RectangleClip() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.clip();
        });
        framework.assertBothValid("checkPoint_01_005_RectangleNoContent");
    }

    @Test
    public void checkPoint_01_005_RectangleClosePathStroke() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.closePathStroke();
        });

        framework.assertBothFail("checkPoint_01_005_RectangleClosePathStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_Rectangle_EOFIllStroke() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.closePathEoFillStroke();
        });
        framework.assertBothFail("checkPoint_01_005_Rectangle_ClosPathEOFIllStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_Rectangle_FillStroke() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.fillStroke();
        });
        framework.assertBothFail("checkPoint_01_005_Rectangle_FillStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_Rectangle_eoFill() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.eoFill();
        });
        framework.assertBothFail("checkPoint_01_005_Rectangle_eoFill",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_Rectangle_eoFillStroke() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.eoFillStroke();
        });
        framework.assertBothFail("checkPoint_01_005_Rectangle_eoFillStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_RectangleMarkedArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_RectangleMarkedArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedContentWithoutMcid() throws IOException {

        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.P))
                    .setFillColor(ColorConstants.RED);
            canvas.rectangle(new Rectangle(200, 200, 100, 100)).fill();
        });

        framework.assertBothFail("checkPoint_01_005_RectangleMarkedContentWithoutMcid",
                PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT, false);
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedContentWithoutMcid_NoContent() throws IOException {

        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.P))
                    .setFillColor(ColorConstants.RED);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
        });
        framework.assertBothValid("checkPoint_01_005_RectangleMarkedContentWithoutMcid_NoContent");
    }

    @Test
    public void checkPoint_01_005_RectangleMarkedContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_RectangleMarkedContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_RectangleMarkedContent.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_004_bezierMarkedAsContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_004_bezierCurveShouldBeTagged.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_004_bezierCurveShouldBeTagged.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void checkPoint_01_004_bezierMarkedAsArtifact() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_004_bezierMarkedAsArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_004_bezierMarkedAsArtifact.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void checkPoint_01_004_bezierCurveInvalidMCID() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.P, 420))
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(5)
                    .moveTo(20, 20)
                    .lineTo(300, 300)
                    .setStrokeColor(ColorConstants.RED)
                    .fill();
        });
        framework.assertBothFail("checkPoint_01_004_bezierCurveInvalidMCID",
                PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT, false);
    }

    @Test
    public void checkPoint_01_004_bezierCurveInvalidMCID_NoContent() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.P, 420))
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(5)
                    .moveTo(20, 20)
                    .lineTo(300, 300)
                    .setStrokeColor(ColorConstants.RED);
        });
        framework.assertBothValid("checkPoint_01_004_bezierCurveInvalidMCID_NoContent");
    }

    @Test
    public void checkPoint_01_005_RandomOperationsWithoutActuallyAddingContent()
            throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "01_005_RandomOperationsWithoutActuallyAddingContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas
                .setColor(ColorConstants.RED, true)
                .setLineCapStyle(1)
                .setTextMatrix(20, 2)
                .setLineWidth(2);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_01_005_RandomOperationsWithoutActuallyAddingContent.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_01_003_ContentMarkedAsArtifactsPresentInsideTaggedContent() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_003_ContentMarkedAsArtifactsPresentInsideTaggedContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.openTag(new CanvasTag(PdfName.Artifact));
        });
        Assertions.assertEquals(
                PdfUAExceptionMessageConstants.ARTIFACT_CANT_BE_INSIDE_REAL_CONTENT,
                e.getMessage());
    }

    @Test
    public void checkPoint_validRoleAddedInsideMarkedContent() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "validRoleAddedInsideMarkedContent.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_validRoleAddedInsideMarkedContent.pdf",
                DESTINATION_FOLDER, "diff_")
        );
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void checkPoint_validRoleAddedInsideMarkedContentMultiple() throws IOException, InterruptedException {

        String outPdf = DESTINATION_FOLDER + "validRoleAddedInsideMarkedContentMultiple.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_validRoleAddedInsideMarkedContentMultiple.pdf",
                DESTINATION_FOLDER, "diff_")
        );
    }

    @Test
    public void checkPoint_validRoleAddedInsideMarkedContentMCR_IN_MCR() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "validRoleAddedInsideMarkedContentMCR_IN_MCR.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_validRoleAddedInsideMarkedContentMCR_IN_MCR.pdf",
                DESTINATION_FOLDER, "diff_")
        );

    }

    @Test
    public void checkPoint_01_004_TaggedContentShouldNotBeInsideArtifact() throws IOException {
        String outPdf = DESTINATION_FOLDER + "01_004_TaggedContentShouldNotBeInsideArtifact.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
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
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.openTag(tagPointer.getTagReference());
        });
        Assertions.assertEquals(
                PdfUAExceptionMessageConstants.REAL_CONTENT_CANT_BE_INSIDE_ARTIFACT,
                e.getMessage());
    }

    @Test
    public void checkPoint_31_009_FontIsNotEmbedded() throws IOException {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream()));
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
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            pdfDoc.close();
        });
        Assertions.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "Courier"),
                e.getMessage());
    }

    @Test
    public void checkPoint_19_003_iDEntryInNoteTagIsNotPresent() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }

            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
            PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
            PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));
            doc.addKid(new PdfStructElem(pdfDoc, PdfName.Note, page1));

            canvas
                    .openTag(new CanvasTag(mcr))
                    .saveState()
                    .beginText()
                    .setFontAndSize(font, 12)
                    .moveText(200, 200)
                    .showText("Hello World!")
                    .endText()
                    .restoreState()
                    .closeTag();
        });
        framework.assertBothFail("invalidNoteTag02", PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY);
    }

    @Test
    public void checkPoint_19_003_validNoteTagIsPresent() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            PdfPage page1 = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            PdfStructElem doc = pdfDocument.getStructTreeRoot().addKid(new PdfStructElem(pdfDocument, PdfName.Document));
            PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDocument, PdfName.P, page1));
            PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));
            PdfStructElem note = doc.addKid(new PdfStructElem(pdfDocument, PdfName.Note, page1));
            note.put(PdfName.ID, new PdfString("1"));


            canvas.openTag(new CanvasTag(mcr))
                    .saveState()
                    .beginText()
                    .setFontAndSize(font, 12)
                    .moveText(200, 200)
                    .showText("Hello World!")
                    .endText()
                    .restoreState()
                    .closeTag();
        });
        framework.assertBothValid("validNoteTagPresent");

        String outPdf = DESTINATION_FOLDER + "layout_validNoteTagPresent.pdf";
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_validNoteTagPresent.pdf",
                DESTINATION_FOLDER, "diff_")
        );
    }

    @Test
    public void usingCharacterWithoutUnicodeMappingTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(
                        FontProgramFactory.createType1Font(FONT_FOLDER + "cmr10.afm", FONT_FOLDER + "cmr10.pfb"),
                        FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }

            final PdfPage page = pdfDoc.addNewPage();
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(page)
                    .addTag(StandardRoles.P);

            new PdfCanvas(page)
                    .openTag(tagPointer.getTagReference())
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(font, 72)
                    // space symbol isn't defined in the font
                    .showText("Hello world")
                    .endText()
                    .restoreState()
                    .closeTag();
        });
        framework.assertBothFail("usingCharacterWithoutUnicodeMappingTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, " "), false);
    }

    private PdfFont getFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
