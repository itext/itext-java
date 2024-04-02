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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUAFormulaTest extends ExtendedITextTest {


    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAFormulaTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private UaValidationTestFramework framework;

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void layoutTest01() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                return p;
            }
        });
        framework.assertBothFail("layout01");
    }

    @Test
    public void layoutTest02() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("Einstein smart boy formula");
                return p;
            }
        });
        framework.assertBothValid("layout02");
    }


    @Test
    public void layoutTest03() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("Einstein smart boy " + "formula");
                return p;
            }
        });
        framework.assertBothValid("layout03");
    }


    @Test
    public void layoutTest04() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("");
                return p;
            }
        });
        framework.assertBothFail("layout04");
    }

    @Test
    public void layoutTest05() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("");
                return p;
            }
        });
        framework.assertBothValid("layout05");
    }

    @Test
    public void layoutTest06() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("⫊").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("Some character that is not embeded in the font");
                return p;
            }
        });
        framework.assertBothFail("layout06",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "⫊"), false);
    }

    @Test
    public void layoutTest07() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("⫊").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("Alternate " + "description");
                return p;
            }
        });
        framework.assertBothFail("layout07",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "⫊"), false);
    }

    @Test
    public void layoutWithValidRole() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("e = mc^2").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole("BING");
                p.getAccessibilityProperties().setAlternateDescription("Alternate " + "description");
                return p;
            }
        });
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfStructTreeRoot tagStructureContext = pdfDocument.getStructTreeRoot();
            tagStructureContext.addRoleMapping("BING", StandardRoles.FORMULA);
        });
        framework.assertBothValid("layoutWithValidRole");
    }


    @Test
    public void layoutWithValidRoleButNoAlternateDescription() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("e = mc^2").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole("BING");
                return p;
            }
        });
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfStructTreeRoot tagStructureContext = pdfDocument.getStructTreeRoot();
            tagStructureContext.addRoleMapping("BING", StandardRoles.FORMULA);
        });
        framework.assertBothFail("layoutWithValidRoleButNoDescription");
    }

    @Test
    public void canvasTest01() throws IOException {
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont font = PdfFontFactory.createFont(FONT);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(document.getFirstPage());
        tagPointer.addTag(StandardRoles.FORMULA);
        canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12).showText("E=mc²")
                .endText().closeTag();
        Assert.assertThrows(PdfUAConformanceException.class, () -> {
            document.close();
        });

    }

    @Test
    public void canvasTest02() throws IOException {
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont font = PdfFontFactory.createFont(FONT);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(document.getFirstPage());
        tagPointer.addTag(StandardRoles.FORMULA);

        tagPointer.getProperties().setActualText("Einstein smart boy");
        canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12).showText("E=mc²")
                .endText().closeTag();
        AssertUtil.doesNotThrow(() -> {
            document.close();
        });
    }

    @Test
    public void canvasTest03() throws IOException {
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont font = PdfFontFactory.createFont(FONT);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(document.getFirstPage());
        tagPointer.addTag(StandardRoles.FORMULA);
        canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> canvas.showText("⫊"));
        Assert.assertEquals(
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "⫊"),
                e.getMessage());
    }

    private static PdfFont loadFont(String fontPath) {
        try {
            return PdfFontFactory.createFont(fontPath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
