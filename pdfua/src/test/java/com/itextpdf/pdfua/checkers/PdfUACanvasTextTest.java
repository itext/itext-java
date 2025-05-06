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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUACanvasTextTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUACanvasTextTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/iTextFreeSansWithE001Glyph.ttf";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void setUp() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER, false);
    }

    public static List<String> textRepresentation() {
        return Arrays.asList("text", "array", "glyphs");
    }

    @Test
    public void puaValueInLayoutTest() throws IOException {
        String filename = "puaValueInLayoutTest";
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph paragraph = new Paragraph("hello_" + "\uE001");
                paragraph.setFont(loadFont());
                return paragraph;
            }
        });
        framework.assertBothFail(filename, PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("textRepresentation")
    public void puaValueWithoutAttributesTest(String textRepresentation) throws IOException {
        String filename = "puaValueWithoutAttributesTest_" + textRepresentation;
        framework.addBeforeGenerationHook(document -> {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer = document.getTagStructureContext().getAutoTaggingPointer();
            pointer.addTag(StandardRoles.P);
            pointer.setPageForTagging(document.getFirstPage());
            canvas.beginText();
            PdfFont font = loadFont();
            canvas.setFontAndSize(font, 24);
            canvas.openTag(pointer.getTagReference());
            addPuaTextToCanvas(canvas, textRepresentation, font);
            canvas.closeTag();
            canvas.endText();
        });
        framework.assertBothFail(filename, PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("textRepresentation")
    public void puaValueWithAltOnTagTest(String textRepresentation) throws IOException {
        String filename = "puaValueWithAltOnTagTest_" + textRepresentation;
        framework.addBeforeGenerationHook(document -> {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer = document.getTagStructureContext().getAutoTaggingPointer();
            pointer.addTag(StandardRoles.P);
            pointer.setPageForTagging(document.getFirstPage());
            pointer.applyProperties(new DefaultAccessibilityProperties(StandardRoles.P).setAlternateDescription("alt description"));
            canvas.beginText();
            PdfFont font = loadFont();
            canvas.setFontAndSize(font, 24);
            CanvasTag canvasTag = new CanvasTag(pointer.getTagReference().getRole(), pointer.getTagReference().createNextMcid());
            canvas.openTag(canvasTag);
            addPuaTextToCanvas(canvas, textRepresentation, font);
            canvas.closeTag();
            canvas.endText();
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("textRepresentation")
    public void puaValueWithActualTextOnTagTest(String textRepresentation) throws IOException {
        String filename = "puaValueWithActualTextOnTagTest_" + textRepresentation;
        framework.addBeforeGenerationHook(document -> {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer = document.getTagStructureContext().getAutoTaggingPointer();
            pointer.addTag(StandardRoles.P);
            pointer.setPageForTagging(document.getFirstPage());
            pointer.applyProperties(new DefaultAccessibilityProperties(StandardRoles.P).setActualText("alt description"));
            canvas.beginText();
            PdfFont font = loadFont();
            canvas.setFontAndSize(font, 24);
            CanvasTag canvasTag = new CanvasTag(pointer.getTagReference().getRole(), pointer.getTagReference().createNextMcid());
            canvas.openTag(canvasTag);
            addPuaTextToCanvas(canvas, textRepresentation, font);
            canvas.closeTag();
            canvas.endText();
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("textRepresentation")
    public void puaValueWithAltOnCanvasTest(String textRepresentation) throws IOException {
        String filename = "puaValueWithAltOnCanvasTest_" + textRepresentation;
        framework.addBeforeGenerationHook(document -> {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer = document.getTagStructureContext().getAutoTaggingPointer();
            pointer.addTag(StandardRoles.P);
            pointer.setPageForTagging(document.getFirstPage());
            canvas.beginText();
            PdfFont font = loadFont();
            canvas.setFontAndSize(font, 24);
            CanvasTag canvasTag = new CanvasTag(pointer.getTagReference().getRole(), pointer.getTagReference().createNextMcid());
            canvasTag.addProperty(PdfName.Alt, new PdfString("alt description"));
            canvas.openTag(canvasTag);
            addPuaTextToCanvas(canvas, textRepresentation, font);
            canvas.closeTag();
            canvas.endText();
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("textRepresentation")
    public void puaValueWithActualTextOnCanvasTest(String textRepresentation) throws IOException {
        String filename = "puaValueWithActualTextOnCanvasTest_" + textRepresentation;
        framework.addBeforeGenerationHook(document -> {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer = document.getTagStructureContext().getAutoTaggingPointer();
            pointer.addTag(StandardRoles.P);
            pointer.setPageForTagging(document.getFirstPage());
            canvas.beginText();
            PdfFont font = loadFont();
            canvas.setFontAndSize(font, 24);
            CanvasTag canvasTag = new CanvasTag(pointer.getTagReference().getRole(), pointer.getTagReference().createNextMcid());
            canvasTag.addProperty(PdfName.ActualText, new PdfString("alt description"));
            canvas.openTag(canvasTag);
            addPuaTextToCanvas(canvas, textRepresentation, font);
            canvas.closeTag();
            canvas.endText();
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("textRepresentation")
    public void puaValueOnTwoPagesTest(String textRepresentation) throws IOException {
        String filename = "puaValueOnTwoPagesTest_" + textRepresentation;
        framework.addBeforeGenerationHook(document -> {
            // Text on page 1 contains PUA and alt, which is valid.
            PdfCanvas canvasOnPageOne = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer1 = document.getTagStructureContext().getAutoTaggingPointer();
            pointer1.addTag(StandardRoles.P);
            pointer1.setPageForTagging(document.getFirstPage());
            pointer1.applyProperties(new DefaultAccessibilityProperties(StandardRoles.P).setAlternateDescription("alt description"));
            canvasOnPageOne.beginText();
            PdfFont font = loadFont();
            canvasOnPageOne.setFontAndSize(font, 24);
            CanvasTag canvasTag = new CanvasTag(pointer1.getTagReference().getRole(), pointer1.getTagReference().createNextMcid());
            canvasOnPageOne.openTag(canvasTag);
            addPuaTextToCanvas(canvasOnPageOne, textRepresentation, font);
            canvasOnPageOne.closeTag();
            canvasOnPageOne.endText();
            pointer1.moveToParent();

            // Text on page two contains PUA, but doesn't contain alt, which is invalid.
            PdfCanvas canvasOnPageTwo = new PdfCanvas(document.addNewPage());
            TagTreePointer pointer2 = document.getTagStructureContext().getAutoTaggingPointer();
            pointer2.addTag(StandardRoles.P);
            pointer2.setPageForTagging(document.getPage(2));
            canvasOnPageTwo.beginText();
            canvasOnPageTwo.setFontAndSize(font, 24);
            canvasOnPageTwo.openTag(pointer2.getTagReference());
            addPuaTextToCanvas(canvasOnPageTwo, textRepresentation, font);
            canvasOnPageTwo.closeTag();
            canvasOnPageTwo.endText();
        });
        framework.assertBothFail(filename, PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT, PdfUAConformance.PDF_UA_2);
    }

    private void addPuaTextToCanvas(PdfCanvas canvas, String textRepresentation, PdfFont font) {
        String stringWithPua = "hello_" + "\uE001";
        switch (textRepresentation) {
            case "text":
                canvas.showText(stringWithPua);
                break;
            case "array":
                PdfArray array = new PdfArray();
                array.add(new PdfString(font.convertToBytes(stringWithPua)));
                canvas.showText(array);
                break;
            case "glyphs":
                GlyphLine glyphLine = font.createGlyphLine(stringWithPua);
                canvas.showText(glyphLine);
                break;
        }
    }

    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
