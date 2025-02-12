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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


@Tag("IntegrationTest")
public class PdfUACanvasXObjectTest extends ExtendedITextTest {


    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUACanvasXObjectTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUACanvasXObjectTest/";

    private static final String DOG = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

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
        return Arrays.asList(PdfUAConformance.PDF_UA_1, PdfUAConformance.PDF_UA_2);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)})
    public void copyPageAsFormXobjectWithTaggedPdf() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "xobjectTesting.pdf";
        String inputPdf = SOURCE_FOLDER + "cmp_manualPdfUaCreation.pdf";
        String cmpFIle = SOURCE_FOLDER + "cmp_xobjectTesting.pdf";
        PdfUATestPdfDocument doc = new PdfUATestPdfDocument(new PdfWriter(outPdf));

        PdfDocument inputDoc = new PdfDocument(new PdfReader(inputPdf));
        PdfFormXObject xObject = inputDoc.getFirstPage().copyAsFormXObject(doc);

        Document document = new Document(doc);

        Image img = new Image(xObject);
        img.getAccessibilityProperties().setAlternateDescription("Some description");
        document.add(img);
        document.close();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpFIle, DESTINATION_FOLDER, "diff_"));
        VeraPdfValidator validator = new VeraPdfValidator();  // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        // We expect verapdf to fail because we are embedding tagged content which contains artifacts
        Assertions.assertNotNull("We expect vera pdf to fail, because we are embedding tagged content which contains artifacts into a tagged item", validator.validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)})
    public void copyPageAsFormXobjectWithUnTaggedContentButInvalidBecauseOfFont() throws IOException, InterruptedException {
        //itext should thrown an exception here but it does not.
        // because even if it's not tagged the inner content stream is not compliant as the font is not embeded
        String outputPdf = DESTINATION_FOLDER + "copyPageAsFormXobjectWithUnTaggedPdf.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_copyPageAsFormXobjectWithUnTaggedPdf.pdf";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);
        document.add(new Paragraph("Hello World!"));
        document.close();

        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outputPdf));

        PdfFormXObject xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDoc);

        Image img = new Image(xObject);
        img.getAccessibilityProperties().setAlternateDescription("Some description");
        Document doc = new Document(pdfDoc);
        doc.add(img);
        doc.close();
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputPdf, cmpFile, DESTINATION_FOLDER, "diff_"));
        VeraPdfValidator validator = new VeraPdfValidator(); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNotNull("Fails are expected because the content inside the xobject isn't valid because of not embedded font, and iText doesn't parse the content streams", validator.validate(outputPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

    }


    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)})
    @Test
    public void copyPageAsFormWithUntaggedContentAndCorrectFont() throws IOException, InterruptedException {

        String outputPdf = DESTINATION_FOLDER + "copyPageAsFormWithCorrectFontXobjectWithUnTaggedPdf.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_copyPageAsFormWithCorrectFontXobjectWithUnTaggedPdf.pdf";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font));
        document.close();

        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outputPdf));

        PdfFormXObject xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDoc);

        Image img = new Image(xObject);
        img.getAccessibilityProperties().setAlternateDescription("Some description");
        Document doc = new Document(pdfDoc);
        doc.add(img);
        doc.close();
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputPdf, cmpFile, DESTINATION_FOLDER, "diff_"));
        VeraPdfValidator validator = new VeraPdfValidator(); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNull(validator.validate(outputPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

    }

    @Test
    public void manuallyAddToCanvasWithUnTaggedContentButBadFont() throws IOException, InterruptedException {
        String outputPdf = DESTINATION_FOLDER + "manuallyAddToCanvasWithUnTaggedPdf.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_manuallyAddToCanvasWithUnTaggedPdf.pdf";


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);
        document.add(new Paragraph("Hello World!"));
        document.close();

        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outputPdf));

        PdfFormXObject xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDoc);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginMarkedContent(PdfName.Artifact);
        canvas.addXObject(xObject);
        canvas.endMarkedContent();
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputPdf, cmpFile, DESTINATION_FOLDER, "diff_"));
        VeraPdfValidator validator = new VeraPdfValidator(); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNotNull("Content of the xobject is not valid causing it to be an non compliant", validator.validate(outputPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasCorrectFontAndUnTaggedContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                Document document = new Document(dummyDoc);
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                document.add(new Paragraph("Hello World!").setFont(font));
                document.close();

                PdfFormXObject xObject = null;
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDoc);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.DIV);
                tagPointer.setPageForTagging(pdfDoc.getPage(1));
                canvas.openTag(tagPointer.getTagReference());
                canvas.addXObject(xObject);
                canvas.closeTag();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("manuallyAddToCanvasCorrectFontAndUnTaggedContent", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("manuallyAddToCanvasCorrectFontAndUnTaggedContent", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactUnTaggedContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        //Now we are again adding untagged content with some artifacts and embedded font's so we should also be fine
        framework.addBeforeGenerationHook(pdfDocument -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
            Document document = new Document(dummyDoc);
            try {
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);

                document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                document.close();
                PdfFormXObject xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDocument);

                PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
                TagTreePointer tagPointer = pdfDocument.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.DIV);
                tagPointer.setPageForTagging(pdfDocument.getPage(1));
                canvas.openTag(tagPointer.getTagReference());
                canvas.addXObject(xObject);
                canvas.closeTag();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("manuallyAddToCanvasCorrectFontArtifactUnTaggedContent", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("manuallyAddToCanvasCorrectFontArtifactUnTaggedContent", pdfUAConformance);
        }
    }

    @Test
    public void manuallyAddToCanvasAndCorrectFontAndArtifactTaggedContent() throws IOException, InterruptedException {
        String outputPdf = DESTINATION_FOLDER + "manuallyAddToCanvasWithUnAndCorrectFontAndArtifactUnPdf.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_manuallyAddToCanvasWithUnAndCorrectFontUnAndArtifactPdf.pdf";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        dummyDoc.setTagged();
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();

        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outputPdf));

        PdfFormXObject xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDoc);


        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.DIV);
        tagPointer.setPageForTagging(pdfDoc.getPage(1));

        canvas.openTag(tagPointer.getTagReference());
        canvas.addXObject(xObject);
        canvas.closeTag();
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputPdf, cmpFile, DESTINATION_FOLDER, "diff_"));
        VeraPdfValidator validator = new VeraPdfValidator(); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNotNull("The content is non compliant because it contains both artifacts, and real content", validator.validate(outputPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactTaggedContentInsideArtifact(PdfUAConformance pdfUAConformance)
            throws IOException {
        // We are adding tagged content to an artifact. Looks like Verapdf doesn't check xobject stream at all because
        // page content is marked as artifact. We think it's wrong though.
        framework.addBeforeGenerationHook(pdfDoc -> {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
                dummyDoc.setTagged();
                Document document = new Document(dummyDoc);
                PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                document.close();

                PdfFormXObject xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDoc);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                canvas.openTag(new CanvasArtifact());
                canvas.addXObject(xObject);
                canvas.closeTag();

            } catch (IOException e) {
                throw new RuntimeException();
            }
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("manuallyAddToCanvasAndCorrectFontInsideArtifact", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("manuallyAddToCanvasAndCorrectFontInsideArtifact", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        dummyDoc.setTagged();
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addXObject(xObject);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("untaggedAddXobject",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("untaggedAddXobject", pdfUAConformance);
        }
    }


    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAtLocationAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContent(PdfUAConformance pdfUAConformance)
            throws IOException {
        //We are adding untagged content we should throw an exception
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        dummyDoc.setTagged();
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addXObjectAt(xObject, 200f, 200f);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("untaggedAddXobjectAt",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("untaggedAddXobjectAt", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAtLocationAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContenta(PdfUAConformance pdfUAConformance)
            throws IOException {
        //We are adding untagged content we should throw an exception
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(200, 200, 200, 200));
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addXObjectFitted",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("addXObjectFitted", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void manuallyAddToCanvasAtLocationAndCorrectFontAndArtifactTaggedContentInsideUntaggedPageContentab(PdfUAConformance pdfUAConformance)
            throws IOException {
        //We are adding untagged content we should throw an exception

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(os));
        Document document = new Document(dummyDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        document.add(new Paragraph("Hello World!").setFont(font).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
        document.close();
        dummyDoc.close();

        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            PdfFormXObject xObject = null;
            try {
                xObject = new PdfDocument(new PdfReader(new ByteArrayInputStream(os.toByteArray()))).getFirstPage().copyAsFormXObject(pdfDocument);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addXObjectWithTransformationMatrix(xObject, 1, 1, 1, 1, 1, 1);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addXObjectWithTransfoMatrix",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("addXObjectWithTransfoMatrix", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addImageObjectNotInline(PdfUAConformance pdfUAConformance) throws IOException {
        //We are adding untagged content we should throw an exception
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addImageAt(imd, 200, 200, false);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addIMageObjectNotInline",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("addIMageObjectNotInline", pdfUAConformance);
        }
    }


    @ParameterizedTest
    @MethodSource("data")
    public void addImageObjectInline(PdfUAConformance pdfUAConformance) throws IOException {
        //We are adding untagged content we should throw an exception
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addImageAt(imd, 200, 200, false);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addIMageObjectInline",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("addIMageObjectInline", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addImageTranformationMatrix(PdfUAConformance pdfUAConformance) throws IOException {
        //We are adding untagged content we should throw an exception
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addImageWithTransformationMatrix(imd, 1, 1, 1, 1, 1, 1, false);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addIMageObjectTransfo",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("addIMageObjectTransfo", pdfUAConformance);
        }
    }


    @ParameterizedTest
    @MethodSource("data")
    public void addImageFittedIntoRectangle(PdfUAConformance pdfUAConformance) throws IOException {
        //We are adding untagged content we should throw an exception
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
            ImageData imd = null;
            try {
                imd = ImageDataFactory.create(DOG);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            canvas.addImageFittedIntoRectangle(imd, new Rectangle(200, 200, 200, 200), false);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addImageFittedIntoRectangle",
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING, false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertVeraPdfFail("addImageFittedIntoRectangle", pdfUAConformance);
        }
    }
}
