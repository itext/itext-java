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
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


@Tag("UnitTest")
public class PdfUACanvasTest extends ExtendedITextTest {
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/pdfua/font/";

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUACanvasTest/";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextContentIsNotTagged(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(getPdfFont(), 10)
                    .showText("Hello World!");

        });

        framework.assertBothFail("textContentIsNotTagged",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextNoContentIsNotTagged(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(getPdfFont(), 10)
                    .endText();

        });
        framework.assertBothValid("textNoContentIsNotTagged", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextContentIsCorrectlyTaggedAsContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage page1 = pdfDoc.addNewPage();
            PdfFont font = getPdfFont();
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
        });
        framework.assertBothValid("01_005_TextContentIsCorrectlyTaggedAsContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextContentIsNotInTagTree(PdfUAConformance pdfUAConformance) throws IOException {
       framework.addBeforeGenerationHook(pdfDoc -> {
           PdfFont font = getPdfFont();

           PdfPage page1 = pdfDoc.addNewPage();
           PdfCanvas canvas = new PdfCanvas(page1);

           canvas
                   .openTag(new CanvasTag(PdfName.P))
                   .saveState()
                   .beginText()
                   .setFontAndSize(font, 12)
                   .moveText(200, 200);
           canvas.showText("Hello World!");
       });
        framework.assertBothFail("01_005_TextArtifactIsNotInTagTree",
                PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextArtifactIsNotInTagTree(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage page1 = pdfDoc.addNewPage();
            PdfFont font = getPdfFont();
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
        });
        framework.assertBothValid("01_005_TextArtifactIsNotInTagTree", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextContentWithMCIDButNotInTagTree(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            canvas
                    .openTag(new CanvasTag(PdfName.P, 99))
                    .saveState()
                    .beginText()
                    .setFontAndSize(font, 12)
                    .moveText(200, 200);

            canvas.showText("Hello World!");
        });
        framework.assertBothFail("textContentWithMCIDButNotInTagTree",
                PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT, false,
                pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextGlyphLineContentIsTaggedButNotInTagTree(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            GlyphLine glyphLine = font.createGlyphLine("Hello World!");
            canvas.saveState()
                    .openTag(new CanvasTag(PdfName.H1))
                    .setFontAndSize(font, 12)
                    .beginText()
                    .moveText(200, 200)
                    .setColor(ColorConstants.RED, true);
            canvas.showText(glyphLine);
        });
        framework.assertBothFail("textGlyphLineContentIsTaggedButNotInTagTree",
                PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextGlyphLineInBadStructure(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage()) {

                @Override
                public PdfCanvas openTag(CanvasTag tag) {
                    // Disable the checkIsoConformance call check by simulating generating not tagged content
                    // same as in annotations of form fields.
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
            canvas.showText(glyphLine);
        });
        framework.assertBothFail("textGlyphLineInBadStructure", PdfUAExceptionMessageConstants.REAL_CONTENT_INSIDE_ARTIFACT_OR_VICE_VERSA, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextGlyphLineContentIsArtifact(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();
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
        });
        framework.assertBothValid("01_005_TextGlyphLineContentIsArtifact", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_TextGlyphLineContentIsContentCorrect(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();
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
        });
        framework.assertBothValid("01_005_TextGlyphLineContentIsContentCorrect", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_allowPureBmcInArtifact(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();
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
        });
        framework.assertBothValid("01_005_allowPureBmcInArtifact", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_allowNestedPureBmcInArtifact(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();
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
        });
        framework.assertBothValid("01_005_allowNestedPureBmcInArtifact", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_LineContentThatIsContentIsNotTagged(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200).fill();
        });

        framework.assertBothFail("lineContentThatIsContentIsNotTagged",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_LineContentThatIsContentIsNotTagged_noContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200);
        });
        framework.assertBothValid("lineContentThatIsContentIsNotTagged_noContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            canvas.openTag(new CanvasTag(PdfName.P))
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200).fill();
        });

        framework.assertBothFail("lineContentThatIsContentIsTaggedButIsNotAnArtifact",
                PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_LineContentThatIsContentIsTaggedButIsNotAnArtifact_no_drawing(
            PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            canvas.openTag(new CanvasTag(PdfName.P))
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.lineTo(200, 200);
            canvas.lineTo(300, 200);

        });
        framework.assertBothValid("lineContentThatIsContentIsTaggedButIsNotAnArtifactNoDrawing", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_LineContentThatIsMarkedAsArtifact(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);
            canvas
                    .openTag(tagPointer.getTagReference())
                    .saveState()
                    .setStrokeColor(ColorConstants.MAGENTA)
                    .moveTo(300, 300)
                    .lineTo(400, 350)
                    .stroke()
                    .restoreState()
                    .closeTag();
        });
        framework.assertBothValid("01_005_LineContentThatIsMarkedAsArtifact", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleNotMarked(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.fill();
        });

        framework.assertBothFail("checkPoint_01_005_RectangleNotMarked",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleNoContent(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
        });
        framework.assertBothValid("checkPoint_01_005_RectangleNoContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleClip(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.clip();
        });
        framework.assertBothValid("checkPoint_01_005_RectangleClip", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleClosePathStroke(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.closePathStroke();
        });

        framework.assertBothFail("checkPoint_01_005_RectangleClosePathStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_Rectangle_EOFIllStroke(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.closePathEoFillStroke();
        });

        framework.assertBothFail("checkPoint_01_005_Rectangle_ClosPathEOFIllStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_Rectangle_FillStroke(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.fillStroke();
        });

        framework.assertBothFail("checkPoint_01_005_Rectangle_FillStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_Rectangle_eoFill(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.eoFill();
        });

        framework.assertBothFail("checkPoint_01_005_Rectangle_eoFill",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_Rectangle_eoFillStroke(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setColor(ColorConstants.RED, true)
                    .setLineWidth(2);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
            canvas.eoFillStroke();
        });

        framework.assertBothFail("checkPoint_01_005_Rectangle_eoFillStroke",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleMarkedArtifact(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.Artifact))
                    .setFillColor(ColorConstants.RED)
                    .rectangle(new Rectangle(200, 200, 100, 100))
                    .fill()
                    .closeTag()
                    .restoreState();
        });
        framework.assertBothValid("01_005_RectangleMarkedArtifact", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleMarkedContentWithoutMcid(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.P))
                    .setFillColor(ColorConstants.RED);
            canvas.rectangle(new Rectangle(200, 200, 100, 100)).fill();
        });

        framework.assertBothFail("rectangleMarkedContentWithoutMcid",
                PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleMarkedContentWithoutMcid_NoContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas
                    .saveState()
                    .openTag(new CanvasTag(PdfName.P))
                    .setFillColor(ColorConstants.RED);
            canvas.rectangle(new Rectangle(200, 200, 100, 100));
        });

        framework.assertBothValid("checkPoint_01_005_RectangleMarkedContentWithoutMcid_NoContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RectangleMarkedContent(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);

            canvas
                    .saveState()
                    .openTag(tagPointer.getTagReference())
                    .setFillColor(ColorConstants.RED)
                    .rectangle(new Rectangle(200, 200, 100, 100))
                    .fill()
                    .closeTag()
                    .restoreState();
        });
        framework.assertBothValid("01_005_RectangleMarkedContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_004_bezierMarkedAsContent(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("01_004_bezierCurveShouldBeTagged", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("01_004_bezierCurveShouldBeTagged", MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED,
                    "Div", "CONTENT"), pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_004_bezierMarkedAsArtifact(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        });
        framework.assertBothValid("01_004_bezierMarkedAsArtifact", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_004_bezierCurveInvalidMCID(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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

        framework.assertBothFail("checkPoint_01_004_bezierCurveInvalidMCID", PdfUAExceptionMessageConstants
                .CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_004_bezierCurveInvalidMCID_NoContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        framework.assertBothValid("checkPoint_01_004_bezierCurveInvalidMCID_NoContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_005_RandomOperationsWithoutActuallyAddingContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            canvas
                    .setColor(ColorConstants.RED, true)
                    .setLineCapStyle(1)
                    .setTextMatrix(20, 2)
                    .setLineWidth(2);
        });

        framework.assertBothValid("01_005_RandomOperationsWithoutActuallyAddingContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_003_ContentMarkedAsArtifactsPresentInsideTaggedContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

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
            canvas.openTag(new CanvasTag(PdfName.Artifact));

        });
        framework.assertBothFail("contentMarkedAsArtifactsInsideTaggedContent",
                PdfUAExceptionMessageConstants.ARTIFACT_CANT_BE_INSIDE_REAL_CONTENT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_validRoleAddedInsideMarkedContent(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            // Have to use low level tagging, otherwise it throws error earlier.
            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem paragraph = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.P, page1)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
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
        });

        framework.assertBothValid("validRoleAddedInsideMarkedContent", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_validRoleAddedInsideMarkedContentMultiple(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            // Have to use low level tagging, otherwise it throws error earlier.
            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem paragraph = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.P, page1)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
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
        });

        framework.assertBothValid("validRoleAddedInsideMarkedContentMultiple", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_validRoleAddedInsideMarkedContentMCR_IN_MCR(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem paragraph = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.P, page1)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));
            PdfStructElem paragraph2 = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.P, page1)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.P, page1));

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
        });

        framework.assertBothValid("validRoleAddedInsideMarkedContentMCR_IN_MCR", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_01_004_TaggedContentShouldNotBeInsideArtifact(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

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
            canvas.openTag(tagPointer.getTagReference());
        });
        framework.assertBothFail("taggedContentShouldNotBeInsideArtifact",
                PdfUAExceptionMessageConstants.REAL_CONTENT_CANT_BE_INSIDE_ARTIFACT, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_31_009_FontIsNotEmbedded(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(StandardFonts.COURIER);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            tagPointer.setPageForTagging(pdfDoc.getFirstPage());
            tagPointer.addTag(StandardRoles.P);
            canvas.beginText()
                    .openTag(tagPointer.getTagReference())
                    .setFontAndSize(font, 12)
                    .showText("Please crash on close, tyvm")
                    .endText()
                    .closeTag();
        });

        framework.assertBothFail("31_009_FontIsNotEmbedded", MessageFormatUtil.format(
                PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "Courier"), false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_19_003_iDEntryInNoteTagIsNotPresent(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = getPdfFont();

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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("invalidNoteTag02", PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY,
                    pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("invalidNoteTag02", PdfUAExceptionMessageConstants.DOCUMENT_USES_NOTE_TAG, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint_19_003_validNoteTagIsPresent(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfFont font = getPdfFont();
            PdfPage page1 = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            pdfDocument.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem paragraph = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDocument.getStructTreeRoot().addKid(new PdfStructElem(pdfDocument, PdfName.P, page1)) :
                    ((PdfStructElem) pdfDocument.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDocument, PdfName.P, page1));
            PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));
            PdfStructElem note = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDocument.getStructTreeRoot().addKid(new PdfStructElem(pdfDocument, PdfName.Note, page1)) :
                    ((PdfStructElem) pdfDocument.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDocument, PdfName.Note, page1));
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("validNoteTagPresent", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("invalidNoteTag02", PdfUAExceptionMessageConstants.DOCUMENT_USES_NOTE_TAG, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void usingCharacterWithoutUnicodeMappingTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, " "),
                false, pdfUAConformance);
    }

    private static PdfFont getPdfFont() {
        PdfFont font = null;
        try {
            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return font;
    }
}
