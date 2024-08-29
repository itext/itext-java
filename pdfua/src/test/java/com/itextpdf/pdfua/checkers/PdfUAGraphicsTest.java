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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.checkers.utils.LayoutCheckUtil;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfUAGraphicsTest extends ExtendedITextTest {


    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAGraphicsTest/";

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAGraphicsTest/";

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

    @Test
    public void imageWithoutAlternativeDescription_ThrowsInLayout() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(DOG));
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(img);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void layoutCheckUtilTest() {
        AssertUtil.doesNotThrow(() -> new LayoutCheckUtil(null).checkRenderer(null));
    }

    @Test
    public void imageWithEmptyAlternativeDescription_ThrowsInLayout() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);

        Image img = new Image(ImageDataFactory.create(DOG));
        img.getAccessibilityProperties().setAlternateDescription("");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(img);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void imageCustomRole_Ok() throws IOException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Image img = null;
                try {
                    img = new Image(ImageDataFactory.create(DOG));
                } catch (MalformedURLException e) {
                    throw new RuntimeException();
                }
                img.getAccessibilityProperties().setRole("CustomImage");
                img.getAccessibilityProperties().setAlternateDescription("ff");
                return new Div().add(img);
            }
        });
        framework.assertBothValid("imageWithCustomRoleOk");
    }

    @Test
    public void imageCustomDoubleMapping_Ok() throws IOException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
            root.addRoleMapping("CustomImage2", "CustomImage");
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Image img = null;
                try {
                    img = new Image(ImageDataFactory.create(DOG));
                } catch (MalformedURLException e) {
                    throw new RuntimeException();
                }
                img.getAccessibilityProperties().setRole("CustomImage2");
                img.getAccessibilityProperties().setAlternateDescription("ff");
                return new Div().add(img);
            }
        });
        framework.assertBothValid("imageWithDoubleMapping");
    }

    @Test
    public void imageCustomRoleNoAlternateDescription_Throws() throws IOException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Image img = null;
                try {
                    img = new Image(ImageDataFactory.create(DOG));
                } catch (MalformedURLException e) {
                    throw new RuntimeException();
                }
                img.getAccessibilityProperties().setRole("CustomImage");
                return new Div().add(img);
            }
        });
        framework.assertBothFail("imageWithCustomRoleAndNoDescription");
    }

    @Test
    public void imageCustomDoubleMapping_Throws() throws IOException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("CustomImage", StandardRoles.FIGURE);
            root.addRoleMapping("CustomImage2", "CustomImage");
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Image img = null;
                try {
                    img = new Image(ImageDataFactory.create(DOG));
                } catch (MalformedURLException e) {
                    throw new RuntimeException();
                }
                img.getAccessibilityProperties().setRole("CustomImage2");
                return new Div().add(img);
            }
        });
        framework.assertBothFail("imageCustomDoubleMapping_Throws");
    }

    @Test
    public void imageWithValidAlternativeDescription_OK() throws IOException, InterruptedException {
        final String OUTPUT_FILE = DESTINATION_FOLDER + "imageWithValidAlternativeDescription_OK.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(OUTPUT_FILE,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(DOG));
        img.getAccessibilityProperties().setAlternateDescription("Alternative description");
        document.add(img);
        document.close();
        Assertions.assertNull(new VeraPdfValidator().validate(OUTPUT_FILE));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(new CompareTool().compareByContent(OUTPUT_FILE,
                SOURCE_FOLDER + "cmp_imageWithValidAlternativeDescription_OK.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void imageWithValidActualText_OK() throws IOException, InterruptedException {
        final String OUTPUT_FILE = DESTINATION_FOLDER + "imageWithValidActualText_OK.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(OUTPUT_FILE,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(DOG));
        img.getAccessibilityProperties().setActualText("Actual text");
        document.add(img);
        document.close();
        Assertions.assertNull(new VeraPdfValidator().validate(OUTPUT_FILE));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(
                new CompareTool().compareByContent(OUTPUT_FILE, SOURCE_FOLDER + "cmp_imageWithValidActualText_OK.pdf",
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void imageWithCaption_OK() throws IOException, InterruptedException {
        final String OUTPUT_FILE = DESTINATION_FOLDER + "imageWithCaption_OK.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(OUTPUT_FILE,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);

        Div imgWithCaption = new Div();
        imgWithCaption.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
        imgWithCaption.getAccessibilityProperties().setAlternateDescription("Alternative description");
        Image img = new Image(ImageDataFactory.create(DOG));
        img.setNeutralRole();
        Paragraph caption = new Paragraph("Caption");
        caption.setFont(PdfFontFactory.createFont(FONT));
        caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
        imgWithCaption.add(img);
        imgWithCaption.add(caption);



        document.add(imgWithCaption);
        document.close();
        Assertions.assertNull(new VeraPdfValidator().validate(OUTPUT_FILE)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(
                new CompareTool().compareByContent(OUTPUT_FILE, SOURCE_FOLDER + "cmp_imageWithCaption_OK.pdf",
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void imageWithCaptionWithoutAlternateDescription_Throws() throws IOException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);

        Div imgWithCaption = new Div();
        imgWithCaption.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
        Image img = new Image(ImageDataFactory.create(DOG));
        img.setNeutralRole();
        Paragraph caption = new Paragraph("Caption");
        caption.setFont(PdfFontFactory.createFont(FONT));
        caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
        imgWithCaption.add(img);
        imgWithCaption.add(caption);

        // will not throw in layout but will throw on close this is expected
        document.add(imgWithCaption);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.close();
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void imageWithoutActualText_ThrowsInLayout() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(DOG));
        img.getAccessibilityProperties().setActualText(null);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(img);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void imageWithEmptyActualText_ThrowsInLayout() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc);

        Image img = new Image(ImageDataFactory.create(DOG));
        img.getAccessibilityProperties().setActualText("");

        AssertUtil.doesNotThrow(() -> document.add(img));
    }

    @Test
    public void imageDirectlyOnCanvas_OK() throws IOException, InterruptedException {
        String OUTPUT_FILE = DESTINATION_FOLDER + "imageDirectlyOnCanvas_OK.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(OUTPUT_FILE,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
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

        pdfDoc.close();
        Assertions.assertNull(new VeraPdfValidator().validate(OUTPUT_FILE)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        new CompareTool().compareByContent(OUTPUT_FILE, SOURCE_FOLDER + "cmp_imageDirectlyOnCanvas_OK.pdf",
                DESTINATION_FOLDER, "diff_");
    }

    @Test
    public void imageDirectlyOnCanvasWithoutAlternateDescription_ThrowsOnClose()
            throws IOException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));

        TagTreePointer pointerForImage = new TagTreePointer(pdfDoc);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        pointerForImage.setPageForTagging(page);
        TagTreePointer tmp = pointerForImage.addTag(StandardRoles.FIGURE);
        canvas.openTag(tmp.getTagReference());
        canvas.addImageAt(ImageDataFactory.create(DOG), 200, 200, false);
        canvas.closeTag();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            pdfDoc.close();
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void imageDirectlyOnCanvasWithEmptyActualText_OK()
            throws IOException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));

        TagTreePointer pointerForImage = new TagTreePointer(pdfDoc);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        pointerForImage.setPageForTagging(page);
        TagTreePointer tmp = pointerForImage.addTag(StandardRoles.FIGURE);
        tmp.getProperties().setActualText("");
        canvas.openTag(tmp.getTagReference());
        canvas.addImageAt(ImageDataFactory.create(DOG), 200, 200, false);
        canvas.closeTag();
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
    }

    @Test
    public void testOverflowImage() throws IOException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Image img = new Image(ImageDataFactory.create(DOG));
        Document document = new Document(pdfDoc);
        document.add(new Div().setHeight(730).setBackgroundColor(ColorConstants.CYAN));

        Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(img);
        });

    }

    @Test
    public void testEmbeddedImageInTable() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Image img = new Image(ImageDataFactory.create(DOG));
        Document document = new Document(pdfDoc);
        Table table = new Table(2);
        for (int i = 0; i <= 20; i++) {
            table.addCell(new Paragraph("Cell " + i));
        }
        table.addCell(img);

        Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(table);
        });
    }

    @Test
    public void testEmbeddedImageInDiv() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Image img = new Image(ImageDataFactory.create(DOG));
        Document document = new Document(pdfDoc);
        Div div = new Div();
        div.add(img);
        Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(div);
        });
    }

    @Test
    public void testEmbeddedImageInParagraph() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Image img = new Image(ImageDataFactory.create(DOG));
        Document document = new Document(pdfDoc);
        Div div = new Div();
        div.add(img);
        Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            document.add(div);
        });
    }

}
