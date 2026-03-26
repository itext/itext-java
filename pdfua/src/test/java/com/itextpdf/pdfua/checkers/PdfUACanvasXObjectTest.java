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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUACanvasXObjectTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUACanvasXObjectTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUACanvasXObjectTest/";

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
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)})
    public void copyPageAsFormXobjectWithTaggedPdf(PdfConformance conformance) throws IOException {
        final String inputPdf = SOURCE_FOLDER + "cmp_manualPdfUaCreation.pdf";

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            try {
                PdfDocument inputDoc = new PdfDocument(new PdfReader(inputPdf));
                PdfFormXObject xObject = inputDoc.getFirstPage().copyAsFormXObject(document);
                Image img = new Image(xObject);
                img.getAccessibilityProperties().setAlternateDescription("Some description");
                return new Div().add(img);
            } catch (IOException e) {
                throw new PdfException(e);
            }
        });
        if (framework.isPdf2Based(conformance)) {
            framework.assertBothValid("xobjectTesting");
        } else {
            framework.assertOnlyVeraPdfFail("xobjectTesting");
        }

    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)})
    public void copyPageAsFormXobjectWithUnTaggedContentButInvalidBecauseOfFont(PdfConformance conformance)
            throws IOException {

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);

        framework.addSuppliers( pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                Document document = new Document(dummyDoc);
                document.add(new Paragraph("Hello World!"));
                document.close();
                PdfFormXObject xObject = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDoc);

                Image img = new Image(xObject);
                img.getAccessibilityProperties().setAlternateDescription("Some description");
                return new Div().add(img);
            } catch (Exception e) {
                throw new PdfException(e);
            }
        });
        //itext should thrown an exception here but it does not.
        // because even if it's not tagged the inner content stream is not compliant as the font is not embeded
        framework.assertOnlyVeraPdfFail("copyPageAsFormXobjectWithUnTaggedPdf");
    }


    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)})
    @ParameterizedTest
    @MethodSource("data")
    public void copyPageAsFormWithUntaggedContentAndCorrectFont(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);

        framework.addSuppliers( pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                Document document = new Document(dummyDoc);

                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                        EmbeddingStrategy.PREFER_EMBEDDED);
                document.add(new Paragraph("Hello World!").setFont(font));
                document.close();

                PdfFormXObject xObject = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDoc);

                Image img = new Image(xObject);
                img.getAccessibilityProperties().setAlternateDescription("Some description");
                return new Div().add(img);
            } catch (Exception e) {
                throw new PdfException(e);
            }
        });
        framework.assertBothValid("copyPageAsFormWithUntaggedContentAndCorrectFont");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasWithUnTaggedContentButBadFont(PdfConformance conformance) throws IOException {

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);

        framework.addAfterGenerationHook(pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                Document document = new Document(dummyDoc);
                document.add(new Paragraph("Hello World!"));
                document.close();
                PdfFormXObject xObject = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDoc);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                canvas.beginMarkedContent(PdfName.Artifact);
                canvas.addXObject(xObject);
                canvas.endMarkedContent();
            } catch (Exception e) {
                throw new PdfException(e);
            }
        });

        framework.assertOnlyVeraPdfFail("manuallyAddToCanvasWithUnTaggedContentButBadFont");
    }


    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasCorrectFontAndUnTaggedContent(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                Document document = new Document(dummyDoc);
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                document.add(new Paragraph("Hello World!").setFont(font));
                document.close();

                PdfFormXObject xObject = null;
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDoc);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer()
                        .addTag(StandardRoles.DIV);
                tagPointer.setPageForTagging(pdfDoc.getPage(1));
                canvas.openTag(tagPointer.getTagReference());
                canvas.addXObject(xObject);
                canvas.closeTag();
            } catch (IOException e) {
                throw new PdfException(e);
            }
        });

        if (framework.isPdf2Based(conformance)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "Div", "CONTENT");
            framework.assertBothFail("addToCanvasCorrectFontUnTaggedContent", message);
        } else {
            framework.assertBothValid("addToCanvasCorrectFontUnTaggedContent");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactUnTaggedContent(PdfConformance conformance)
            throws IOException {
        //Now we are again adding untagged content with some artifacts and embedded font's so we should also be fine
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
            Document document = new Document(dummyDoc);
            try {
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);

                document.add(
                        new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                document.close();
                PdfFormXObject xObject = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDocument);

                PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
                TagTreePointer tagPointer = pdfDocument.getTagStructureContext().getAutoTaggingPointer()
                        .addTag(StandardRoles.DIV);
                tagPointer.setPageForTagging(pdfDocument.getPage(1));
                canvas.openTag(tagPointer.getTagReference());
                canvas.addXObject(xObject);
                canvas.closeTag();
            } catch (IOException e) {
                throw new PdfException(e);
            }
        });

        if (framework.isPdf2Based(conformance)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "Div", "CONTENT");
            framework.assertBothFail("addToCanvasCorrectFontArtifactUnTaggedContent", message);
        } else {
            framework.assertBothValid("addToCanvasCorrectFontArtifactUnTaggedContent");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactTaggedContent(PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);

        framework.addAfterGenerationHook(pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                dummyDoc.setTagged();
                Document document = new Document(dummyDoc);
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                document.add(new Paragraph("Hello World!").setFont(font)
                        .setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                document.close();

                PdfFormXObject xObject = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDoc);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                String tag = StandardRoles.ARTIFACT;
                if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_1) {
                    tag = StandardRoles.DIV;
                }

                TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(tag);
                tagPointer.setPageForTagging(pdfDoc.getPage(1));

                canvas.openTag(tagPointer.getTagReference());
                canvas.addXObject(xObject);
                canvas.closeTag();
            } catch (Exception e) {
                throw new PdfException(e);
            }
        });
        if (framework.isPdf2Based(conformance)) {
            framework.assertBothValid("manuallyCanvasCorrectFontAndArtifact");
        } else {
            framework.assertOnlyVeraPdfFail("manuallyCanvasCorrectFontAndArtifact");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactTaggedContentInsideArtifact(PdfConformance conformance)
            throws IOException {
        // We are adding tagged content to an artifact. Looks like Verapdf doesn't check xobject stream at all because
        // page content is marked as artifact. We think it's wrong though.
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                dummyDoc.setTagged();
                Document document = new Document(dummyDoc);
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                document.add(
                        new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                document.close();

                PdfFormXObject xObject = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDoc);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                canvas.openTag(new CanvasArtifact());
                canvas.addXObject(xObject);
                canvas.closeTag();

            } catch (IOException e) {
                throw new PdfException(e);
            }
        });
        framework.assertBothValid("manuallyAddToCanvasAndCorrectFontInsideArtifact");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContent(
            PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        dummyDoc.setTagged();
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addXObject(xObject);
        });

        framework.assertBothFail("untaggedAddXobject",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAtLocationAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContent(
            PdfConformance conformance) throws IOException {
        // We are adding untagged content, so we should throw an exception.
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        dummyDoc.setTagged();
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addXObjectAt(xObject, 200f, 200f);
        });

        framework.assertBothFail("untaggedAddXobjectAt",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAtLocationAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContenta(
            PdfConformance conformance) throws IOException {
        // We are adding untagged content, so we should throw an exception.
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(200, 200, 200, 200));
        });

        framework.assertBothFail("addXObjectFitted",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAtLocationAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContentab(
            PdfConformance conformance) throws IOException {
        // We are adding untagged content, so we should throw an exception.

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage()
                        .copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addXObjectWithTransformationMatrix(xObject, 1, 1, 1, 1, 1, 1);
        });

        framework.assertBothFail("addXObjectWithTransfoMatrix",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addImageObjectNotInline(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        // We are adding untagged content, so we should throw an exception.
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addImageAt(imd, 200, 200, false);
        });

        framework.assertBothFail("addIMageObjectNotInline",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addImageObjectInline(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        // We are adding untagged content, so we should throw an exception.
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addImageAt(imd, 200, 200, false);
        });

        framework.assertBothFail("addIMageObjectInline",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addImageTranformationMatrix(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        // We are adding untagged content, so we should throw an exception.
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addImageWithTransformationMatrix(imd, 1, 1, 1, 1, 1, 1, false);
        });

        framework.assertBothFail("addIMageObjectTransfo",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addImageFittedIntoRectangle(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        // We are adding untagged content, so we should throw an exception.
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            canvas.addImageFittedIntoRectangle(imd, new Rectangle(200, 200, 200, 200), false);
        });

        framework.assertBothFail("addImageFittedIntoRectangle",
                PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false);
    }
}
