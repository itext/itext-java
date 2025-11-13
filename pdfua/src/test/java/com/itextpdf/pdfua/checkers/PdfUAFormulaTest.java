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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAFormulaTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAFormulaTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    
    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest01(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                return p;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("layout01", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("layout01", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest02(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("Einstein smart boy formula");
                return p;
            }
        });
        framework.assertBothValid("layout02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest03(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("Einstein smart boy " + "formula");
                return p;
            }
        });
        framework.assertBothValid("layout03", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest04(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("");
                return p;
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("layout04", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("layout04", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest05(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("");
                return p;
            }
        });
        framework.assertBothValid("layout05", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest06(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("⫊").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("Some character that is not embeded in the font");
                return p;
            }
        });
        framework.assertBothFail("layout06",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "⫊"),
                false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutTest07(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("⫊").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("Alternate " + "description");
                return p;
            }
        });
        framework.assertBothFail("layout07",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "⫊"),
                false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutWithValidRole(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("e = mc^2").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole("BING");
                p.getAccessibilityProperties().setAlternateDescription("Alternate " + "description");
                return p;
            }
        });
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("BING", StandardRoles.FORMULA);
            }

            PdfStructTreeRoot tagStructureContext = pdfDocument.getStructTreeRoot();
            tagStructureContext.addRoleMapping("BING", StandardRoles.FORMULA);
        });
        framework.assertBothValid("layoutWithValidRole", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void layoutWithValidRoleButNoAlternateDescription(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("e = mc^2").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole("BING");
                return p;
            }
        });
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("BING", StandardRoles.FORMULA);
            }

            PdfStructTreeRoot tagStructureContext = pdfDocument.getStructTreeRoot();
            tagStructureContext.addRoleMapping("BING", StandardRoles.FORMULA);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("layoutWithValidRoleButNoDescription", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("layoutWithValidRoleButNoDescription", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void canvasTest01(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addBeforeGenerationHook(pdfDoc -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            PdfFont font = loadFont(FONT);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            tagPointer.setPageForTagging(pdfDoc.getFirstPage());
            tagPointer.addTag(StandardRoles.FORMULA);
            canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12).showText("E=mc²")
                    .endText().closeTag();
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("canvasTest01", PdfUAExceptionMessageConstants.FORMULA_SHALL_HAVE_ALT, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("canvasTest01", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void canvasTest02(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addBeforeGenerationHook(pdfDoc -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            PdfFont font = loadFont(FONT);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            tagPointer.setPageForTagging(pdfDoc.getFirstPage());
            tagPointer.addTag(StandardRoles.FORMULA);

            tagPointer.getProperties().setActualText("Einstein smart boy");
            canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12).showText("E=mc²")
                    .endText().closeTag();
        });
        framework.assertBothValid("canvasTest02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void canvasTest03(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addBeforeGenerationHook(pdfDoc -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            PdfFont font = loadFont(FONT);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            tagPointer.setPageForTagging(pdfDoc.getFirstPage());
            tagPointer.addTag(StandardRoles.FORMULA);
            tagPointer.getProperties().setAlternateDescription("Alt descr");
            canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12);
            canvas.showText("⫊");
        });
        framework.assertBothFail("canvasTest03",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "⫊"),
                false, pdfUAConformance);
    }

    @Test
    public void mathStructureElementInvalidUA2Test() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setNamespace(new PdfNamespace(StandardNamespaces.MATH_ML));
                p.getAccessibilityProperties().setRole("math");
                return p;
            }
        });

        framework.assertBothFail("mathStructureElementInvalidUA2Test",
                PdfUAExceptionMessageConstants.MATH_NOT_CHILD_OF_FORMULA, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void mathStructureElementValidUA2Test() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addAfterGenerationHook(pdfDocument -> {
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            PdfFont font = loadFont(FONT);

            TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
            tagPointer.setPageForTagging(pdfDocument.getFirstPage());
            tagPointer.addTag(StandardRoles.FORMULA);
            tagPointer.setNamespaceForNewTags(new PdfNamespace(StandardNamespaces.MATH_ML));
            tagPointer.addTag("math");
            canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12)
                    .showText("E=mc²")
                    .endText().closeTag();
        });

        framework.assertBothValid("mathStructureElementValidUA2Test", PdfUAConformance.PDF_UA_2);
    }

    private static PdfFont loadFont(String fontPath) {
        try {
            return PdfFontFactory.createFont(fontPath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
