/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.checkers.utils.LayoutCheckUtil;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAGraphicsTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAGraphicsTest/";

    private static final String DOG = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithoutAlternativeDescription_ThrowsInLayout(PdfConformance pdfConformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            Image img = loadImage();
            document.add(img);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("imageNoAltDescription");
        } else {
            framework.assertBothFail("imageNoAltDescription", PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @Test
    public void layoutCheckUtilTest() {
        AssertUtil.doesNotThrow(() -> new LayoutCheckUtil(null).checkRenderer(null));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithEmptyAlternativeDescription_ThrowsInLayout(PdfConformance pdfConformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);

            Image img = loadImage();
            img.getAccessibilityProperties().setAlternateDescription("");
            document.add(img);
        });

        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("imageWithEmptyAltDescription");
        } else if (pdfConformance.getUAConformance() == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("imageWithEmptyAltDescription",
                    PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        } else if (pdfConformance.getUAConformance() == PdfUAConformance.PDF_UA_2) {
            framework.assertOnlyITextFail("imageWithEmptyAltDescription",
                    PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageCustomRole_Ok(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (framework.isPdf2Based(pdfConformance)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("CustomImage", StandardRoles.FIGURE);
            }
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
        });
        framework.addSuppliers( document -> {
            Image img = loadImage();
            img.getAccessibilityProperties().setRole("CustomImage");
            img.getAccessibilityProperties().setAlternateDescription("ff");
            return new Div().add(img);
        });
        framework.assertBothValid("imageWithCustomRoleOk");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageCustomDoubleMapping_Ok(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (framework.isPdf2Based(pdfConformance)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("CustomImage", StandardRoles.FIGURE);
                namespace.addNamespaceRoleMapping("CustomImage2", "CustomImage");
            }
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
            root.addRoleMapping("CustomImage2", "CustomImage");
        });
        framework.addSuppliers( document -> {
            Image img = loadImage();
            img.getAccessibilityProperties().setRole("CustomImage2");
            img.getAccessibilityProperties().setAlternateDescription("ff");
            return new Div().add(img);
        });
        framework.assertBothValid("imageWithDoubleMapping");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageCustomRoleNoAlternateDescription_Throws(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (framework.isPdf2Based(pdfConformance)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("CustomImage", StandardRoles.FIGURE);
            }
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
        });
        framework.addSuppliers( document -> {
            Image img = loadImage();
            img.getAccessibilityProperties().setRole("CustomImage");
            return new Div().add(img);
        });

        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("imageWithCustomRoleAndNoDescription");
        } else {
            framework.assertBothFail("imageWithCustomRoleAndNoDescription");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageCustomDoubleMapping_Throws(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (framework.isPdf2Based(pdfConformance)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("CustomImage", StandardRoles.FIGURE);
                namespace.addNamespaceRoleMapping("CustomImage2", "CustomImage");
            }
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
            root.addRoleMapping("CustomImage2", "CustomImage");
        });
        framework.addSuppliers( document -> {
            Image img = loadImage();
            img.getAccessibilityProperties().setRole("CustomImage2");
            return new Div().add(img);
        });

        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("imageCustomDoubleMapping_Throws");
        } else {
            framework.assertBothFail("imageCustomDoubleMapping_Throws");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithValidAlternativeDescription_OK(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            Image img = loadImage();
            img.getAccessibilityProperties().setAlternateDescription("Alternative description");
            document.add(img);
        });
        framework.assertBothValid("imageWithValidAltDescr");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithValidActualText_OK(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            Image img = loadImage();
            img.getAccessibilityProperties().setActualText("Actual text");
            document.add(img);
        });
        framework.assertBothValid("imageWithValidActualText");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithCaption_OK(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);

            Div imgWithCaption = new Div();
            imgWithCaption.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            imgWithCaption.getAccessibilityProperties().setAlternateDescription("Alternative description");
            Image img = loadImage();
            img.setNeutralRole();
            Paragraph caption = new Paragraph("Caption");
            try {
                caption.setFont(PdfFontFactory.createFont(FONT, EmbeddingStrategy.FORCE_EMBEDDED));
            } catch (IOException e) {
                throw new PdfException(e.getMessage());
            }
            caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
            imgWithCaption.add(img);
            imgWithCaption.add(caption);

            document.add(imgWithCaption);
        });
        framework.assertBothValid("imageWithCaption_OK");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithCaptionWithoutAlternateDescription_Throws(PdfConformance pdfConformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);

            Div imgWithCaption = new Div();
            imgWithCaption.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            Image img = loadImage();
            img.setNeutralRole();
            Paragraph caption = new Paragraph("Caption");
            try {
                caption.setFont(PdfFontFactory.createFont(FONT));
            } catch (IOException e) {
                throw new PdfException(e.getMessage());
            }
            caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
            imgWithCaption.add(img);
            imgWithCaption.add(caption);

            // will not throw in layout but will throw on close this is expected
            document.add(imgWithCaption);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("imageWithCaptionWithoutAltDescr");
        } else {
            framework.assertBothFail("imageWithCaptionWithoutAltDescr",
                    PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithoutActualText_ThrowsInLayout(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            Image img = loadImage();
            img.getAccessibilityProperties().setActualText(null);
            document.add(img);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("imageWithoutActualText");
        } else {
            framework.assertBothFail("imageWithoutActualText", PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageWithEmptyActualText_ThrowsInLayout(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);

            Image img = loadImage();
            img.getAccessibilityProperties().setActualText("");
            document.add(img);

        });
        framework.assertBothValid("imageWithEmptyActualText");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageDirectlyOnCanvas_OK(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {
                Document document = new Document(pdfDoc);

                Image img = new Image(ImageDataFactory.create(DOG));
                img.getAccessibilityProperties().setAlternateDescription("Hello");
                document.add(img);
                Image img2 = new Image(ImageDataFactory.create(DOG));
                img2.getAccessibilityProperties().setActualText("Some actual text on layout img");
                document.add(img2);

                TagTreePointer pointerForImage = new TagTreePointer(pdfDoc);
                PdfPage page = pdfDoc.addNewPage();
                PdfCanvas canvas = new PdfCanvas(page);
                pointerForImage.setPageForTagging(page);
                TagTreePointer tmp = pointerForImage.addTag(StandardRoles.FIGURE);
                tmp.getProperties().setActualText("Some text");

                canvas.openTag(tmp.getTagReference());
                canvas.addImageAt(ImageDataFactory.create(DOG), 400, 400, false);
                canvas.closeTag();

                TagTreePointer ttp = pointerForImage.addTag(StandardRoles.FIGURE);
                ttp.getProperties().setAlternateDescription("Alternate description");
                canvas.openTag(ttp.getTagReference());
                canvas.addImageAt(ImageDataFactory.create(DOG), 200, 200, false);
                canvas.closeTag();
            } catch (MalformedURLException e) {
                throw new PdfException(e.getMessage());
            }
        });
        framework.assertBothValid("imageDirectlyOnCanvas");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageDirectlyOnCanvasWithoutAlternateDescription_ThrowsOnClose(PdfConformance pdfConformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            TagTreePointer pointerForImage = new TagTreePointer(pdfDoc);
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            pointerForImage.setPageForTagging(page);
            TagTreePointer tmp = pointerForImage.addTag(StandardRoles.FIGURE);
            canvas.openTag(tmp.getTagReference());
            try {
                canvas.addImageAt(ImageDataFactory.create(DOG), 200, 200, false);
            } catch (MalformedURLException e) {
                throw new PdfException(e.getMessage());
            }
            canvas.closeTag();
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("canvasWithoutAltDescr");
        } else {
            framework.assertBothFail("canvasWithoutAltDescr", PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void imageDirectlyOnCanvasWithEmptyActualText_OK(PdfConformance pdfConformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            TagTreePointer pointerForImage = new TagTreePointer(pdfDoc);
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            pointerForImage.setPageForTagging(page);
            TagTreePointer tmp = pointerForImage.addTag(StandardRoles.FIGURE);
            tmp.getProperties().setActualText("");
            canvas.openTag(tmp.getTagReference());
            try {
                canvas.addImageAt(ImageDataFactory.create(DOG), 200, 200, false);
            } catch (MalformedURLException e) {
                throw new PdfException(e.getMessage());
            }
            canvas.closeTag();
        });

        framework.assertBothValid("imageOnCanvasEmptyActualText");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testOverflowImage(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Image img = loadImage();
            Document document = new Document(pdfDoc);
            document.add(new Div().setHeight(730).setBackgroundColor(ColorConstants.CYAN));
            document.add(img);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("overflowImage");
        } else {
            framework.assertBothFail("overflowImage");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testEmbeddedImageInTable(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Image img = loadImage();
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONT, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e.getMessage());
            }
            Document document = new Document(pdfDoc);
            Table table = new Table(2);
            for (int i = 0; i <= 20; i++) {
                table.addCell(new Paragraph("Cell " + i).setFont(font));
            }
            table.addCell(img);
            document.add(table);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("embeddedImageInTable");
        } else {
            framework.assertBothFail("embeddedImageInTable", PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testEmbeddedImageInDiv(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Image img = loadImage();
            Document document = new Document(pdfDoc);
            Div div = new Div();
            div.add(img);
            document.add(div);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("embeddedImageInDiv");
        } else {
            framework.assertBothFail("embeddedImageInDiv", PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testEmbeddedImageInParagraph(PdfConformance pdfConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, pdfConformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Image img = loadImage();
            Document document = new Document(pdfDoc);
            Paragraph paragraph = new Paragraph();
            paragraph.add(img);
            document.add(paragraph);
        });
        if (pdfConformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("embeddedImageInParagraph");
        } else {
            framework.assertBothFail("embeddedImageInParagraph", PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    private static Image loadImage() {
        try {
            return new Image(ImageDataFactory.create(DOG));
        } catch (MalformedURLException e) {
            throw new PdfException(e.getMessage());
        }
    }
}
