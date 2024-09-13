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
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfUALayoutTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUALayoutTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUALayoutTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private UaValidationTestFramework framework;

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void simpleParagraphTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "simpleParagraphTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_simpleParagraphTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Simple layout PDF/UA-1 test").setFont(font));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void simpleParagraphWithUnderlineTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "simpleParagraphUnderlinesTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_simpleParagraphWithUnderlineTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Simple layout PDF/UA-1 with underline test").setFont(font).setUnderline());
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void simpleBorderTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "simpleBorderTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_simpleBorderTest.pdf";

        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(outPdf))) {

            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            canvas.openTag(new CanvasTag(PdfName.Artifact));
            new DottedBorder(DeviceRgb.GREEN, 5).draw(canvas, new Rectangle(350, 700, 100, 100));
            canvas.closeTag();

        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void simpleTableTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "simpleTableTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_simpleTableTest.pdf";

        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        Document doc = new Document(pdfDoc);

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        Table table = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1").setFont(font)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2").setFont(font)));
        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void addNoteWithoutIdTest() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("note");
                PdfFont font = null;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(StandardRoles.NOTE);
                return note;
            }
        });
        framework.assertBothFail("noteWithoutID",
                PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 2)})
    public void addTwoNotesWithSameIdTest() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("note 1");
                PdfFont font = null;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(StandardRoles.NOTE);
                note.getAccessibilityProperties().setStructureElementIdString("123");
                return note;
            }
        },
        new UaValidationTestFramework.Generator<IBlockElement>() {
             @Override
             public IBlockElement generate() {
                 Paragraph note = new Paragraph("note 2");
                 PdfFont font = null;
                 try {
                      font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                 } catch (IOException e) {
                     throw new RuntimeException(e.getMessage());
                 }
                 note.setFont(font);
                 note.getAccessibilityProperties().setRole(StandardRoles.NOTE);
                 note.getAccessibilityProperties().setStructureElementIdString("123");
                 return note;
             }
                });
        framework.assertBothFail("twoNotesWithSameId",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.NON_UNIQUE_ID_ENTRY_IN_STRUCT_TREE_ROOT, "123"),
                false);
    }

    @Test
    public void addNoteWithValidIdTest() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("note");
                PdfFont font = null;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(StandardRoles.NOTE);
                note.getAccessibilityProperties().setStructureElementIdString("123");
                return note;
            }
        });
        framework.assertBothValid("noteWithValidID");
    }

    @Test
    public void addTwoNotesWithDifferentIdTest() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
                                   @Override
                                   public IBlockElement generate() {
                                       Paragraph note = new Paragraph("note 1");
                                       PdfFont font = null;
                                       try {
                                           font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                                       } catch (IOException e) {
                                           throw new RuntimeException(e.getMessage());
                                       }
                                       note.setFont(font);
                                       note.getAccessibilityProperties().setRole(StandardRoles.NOTE);
                                       note.getAccessibilityProperties().setStructureElementIdString("123");
                                       return note;
                                   }
                               },
                new UaValidationTestFramework.Generator<IBlockElement>() {
                    @Override
                    public IBlockElement generate() {
                        Paragraph note = new Paragraph("note 2");
                        PdfFont font = null;
                        try {
                            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        note.setFont(font);
                        note.getAccessibilityProperties().setRole(StandardRoles.NOTE);
                        note.getAccessibilityProperties().setStructureElementIdString("234");
                        return note;
                    }
                });
        framework.assertBothValid("twoNotesWithDifferentId");
    }

}
