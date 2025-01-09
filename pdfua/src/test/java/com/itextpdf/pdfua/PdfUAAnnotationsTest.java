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
package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfMediaClipData;
import com.itextpdf.kernel.pdf.action.PdfRendition;
import com.itextpdf.kernel.pdf.annot.Pdf3DAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfInkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPrinterMarkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfRedactAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTrapNetworkAnnotation;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;
import com.itextpdf.kernel.pdf.annot.da.StandardAnnotationFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfUAAnnotationsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAAnnotationsTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAAnnotationsTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void ua1LinkAnnotNoDirectChildOfAnnotTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Rectangle rect = new Rectangle(100, 650, 400, 100);
                PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
                annot.setContents("link annot");
                Link link = new Link("Link to iText", annot);

                Paragraph paragraph = new Paragraph();
                paragraph.setFont(loadFont());
                paragraph.add(link);

                return paragraph;
            }
        });
        framework.assertBothValid("ua1LinkAnnotNoDirectChildOfAnnotTest");
    }

    @Test
    public void ua1WidgetAnnotNoDirectChildOfAnnotTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(pdfDoc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20))
                    .setConformance(PdfConformance.PDF_UA_1)
                    .createCheckBox();

            checkBox.setAlternativeName("widget");

            acroForm.addField(checkBox);
        });
        framework.assertBothValid("ua1WidgetAnnotNoDirectChildOfAnnotTest");
    }

    @Test
    public void ua1WidgetAnnotNoDirectChildOfAnnotAutomaticConformanceLevelTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(pdfDoc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20))
                    .createCheckBox();

            checkBox.setAlternativeName("widget");

            acroForm.addField(checkBox);
        });
        framework.assertBothValid("ua1WidgetAnnotNoDirectChildOfAnnotAutomaticConformanceLevelTest");
    }

    @Test
    public void ua1PrinterMAnnotNoDirectChildOfAnnotTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "ua1PrinterMAnnotNoDirectChildOfAnnotTest.pdf";

        try (PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf))) {
            PdfPage pdfPage = pdfDoc.addNewPage();

            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas
                    .saveState()
                    .circle(265, 795, 5)
                    .setColor(ColorConstants.GREEN, true)
                    .fill()
                    .restoreState();
            canvas.release();

            PdfPrinterMarkAnnotation annot = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            // mark annotation as hidden, because in the scope of the test we check only that PrinterMark isn't enclosed by Annot tag
            annot.setFlag(PdfAnnotation.HIDDEN);

            pdfPage.addAnnotation(annot);
        }

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(outPdf))) {
            final IStructureNode docNode = pdfDoc.getStructTreeRoot().getKids().get(0);
            Assertions.assertEquals(PdfName.Document, docNode.getRole());
            Assertions.assertEquals(PdfName.PrinterMark, ((PdfObjRef) docNode.getKids().get(0)).getReferencedObject().get(PdfName.Subtype));
        }
    }

    @Test
    public void ua1FileAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, "file".getBytes(StandardCharsets.UTF_8),
                    "description", "file.txt", null, null, null);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            annot.setContents("Hello world");
            annot.getPdfObject().put(PdfName.Type, PdfName.Annot);

            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("ua1FileAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua1StampAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
            stamp.setStampName(PdfName.Approved);
            stamp.setContents("stamp contents");
            stamp.getPdfObject().put(PdfName.Type, PdfName.Annot);
            pdfPage.addAnnotation(stamp);
        });
        framework.assertBothValid("ua1StampAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua1ScreenAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("screen annotation");
            pdfPage.addAnnotation(screen);
        });
        framework.assertBothValid("ua1ScreenAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua1ScreenAnnotWithoutContentsAndAltTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(screen);
        });
        framework.assertBothFail("ua1ScreenWithoutContentsTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_OR_ALT_KEY, "Screen"));
    }

    @Test
    public void ua1PopupWithoutContentOrAltTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfPopupAnnotation popup = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            pdfPage.addAnnotation(popup);
        });
        framework.assertBothValid("ua1PopupWithoutContentOrAltTest");
    }

    @Test
    public void ua1StampAnnotWithAltTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "ua1StampAnnotWithAltTest.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfPage pdfPage = pdfDoc.addNewPage();
        PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
        stamp.setStampName(PdfName.Approved);
        stamp.getPdfObject().put(PdfName.Type, PdfName.Annot);

        pdfPage.addAnnotation(stamp);
        stamp.getPdfObject().put(PdfName.Alt, new PdfString("Alt description"));
        pdfPage.addAnnotation(stamp);
        AssertUtil.doesNotThrow(() -> {
            pdfDoc.close();
        });
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_ua1StampAnnotWithAltTest.pdf",
                DESTINATION_FOLDER, "diff_"));
        new VeraPdfValidator().validateFailure(outPdf); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void ua1ScreenAnnotWithAltTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "ua1ScreenAnnotWithAltTest.pdf";
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfPage pdfPage = pdfDoc.addNewPage();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
        pdfPage.addAnnotation(screen);
        screen.getPdfObject().put(PdfName.Alt, new PdfString("Alt description"));
        AssertUtil.doesNotThrow(() -> {
                    pdfDoc.close();
                });
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_ua1ScreenAnnotWithAltTest.pdf",
                DESTINATION_FOLDER, "diff_"));
        new VeraPdfValidator().validateFailure(outPdf); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void ua1InkAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfInkAnnotation ink = createInkAnnotation();
            pdfPage.addAnnotation(ink);
        });
        framework.assertBothValid("ua1InkAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua1RedactAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfRedactAnnotation redact = createRedactionAnnotation();
            pdfPage.addAnnotation(redact);
        });
        framework.assertBothValid("ua1RedactAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua13DAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Pdf3DAnnotation annot = create3DAnnotation();
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            // Check that second Annot tag won't be added in PdfPage#addAnnotation
            tagPointer.addTag(StandardRoles.ANNOT);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("ua13DAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua1RichAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.SECT);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("ua1RichAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void trapNetAnnotNotPermittedTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas
                    .saveState()
                    .circle(272, 795, 5)
                    .setColor(ColorConstants.GREEN, true)
                    .fill()
                    .restoreState();
            canvas.release();
            form.setProcessColorModel(PdfName.DeviceN);
            PdfTrapNetworkAnnotation annot = new PdfTrapNetworkAnnotation(PageSize.A4, form);
            annot.setContents("Some content");
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothFail("trapNetAnnotNotPermittedTest", PdfUAExceptionMessageConstants.ANNOT_TRAP_NET_IS_NOT_PERMITTED);
    }

    @Test
    public void invisibleTrapNetAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas
                    .saveState()
                    .circle(272, 795, 5)
                    .setColor(ColorConstants.GREEN, true)
                    .fill()
                    .restoreState();
            canvas.release();
            form.setProcessColorModel(PdfName.DeviceN);
            PdfTrapNetworkAnnotation annot = new PdfTrapNetworkAnnotation(PageSize.A4, form);
            annot.setContents("Some content");
            annot.setFlag(PdfAnnotation.HIDDEN);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("invisibleTrapNetAnnotTest");
    }

    @Test
    public void ua1SoundAnnotDirectChildOfAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfAnnotation annot = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), new PdfStream());
            annot.setContents("some content");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.PART);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("ua1SoundAnnotDirectChildOfAnnotTest");
    }

    @Test
    public void ua1PushBtnNestedWithinFormTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
            // The rest of the tests for widgets can be found in com.itextpdf.pdfua.checkers.PdfUAFormFieldsTest
            PdfFormField button = new PushButtonFormFieldBuilder(pdfDoc, "push button")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20))
                    .setConformance(PdfConformance.PDF_UA_1)
                    .setFont(loadFont())
                    .createPushButton();

            button.setAlternativeName("widget");
            acroForm.addField(button);
        });
        framework.assertBothValid("ua1PushBtnNestedWithinFormTest");
    }

    @Test
    public void linkAnnotNotDirectChildOfLinkTest1() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            page.addAnnotation(annot);
        });
        framework.assertBothFail("linkAnnotNotDirectChildOfLinkTest1", PdfUAExceptionMessageConstants.LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK);
    }

    @Test
    public void linkAnnotNotDirectChildOfLinkTest2() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");

            Document doc = new Document(pdfDoc);
            Paragraph p1 = new Paragraph("Text1");
            p1.setFont(loadFont());
            p1.getAccessibilityProperties().setRole(StandardRoles.LINK);


            Paragraph p2 = new Paragraph("Text");
            p2.setFont(loadFont());
            p2.getAccessibilityProperties().setRole(StandardRoles.H1);
            p2.setProperty(Property.LINK_ANNOTATION, annot);

            p1.add(p2);
            doc.add(p1);
        });
        framework.assertBothFail("linkAnnotNotDirectChildOfLinkTest2", PdfUAExceptionMessageConstants.LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK);
    }

    @Test
    public void linkAnnotNestedWithinLinkTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");

            Document doc = new Document(pdfDoc);
            Paragraph p2 = new Paragraph("Text");
            p2.setFont(loadFont());
            p2.getAccessibilityProperties().setRole(StandardRoles.LINK);
            p2.setProperty(Property.LINK_ANNOTATION, annot);

            doc.add(p2);
        });
        framework.assertBothValid("linkAnnotNestedWithinLinkTest");
    }

    @Test
    public void linkAnnotWithoutContentsTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));

            Document doc = new Document(pdfDoc);
            Paragraph p2 = new Paragraph("Text");
            p2.setFont(loadFont());
            p2.getAccessibilityProperties().setRole(StandardRoles.LINK);
            p2.setProperty(Property.LINK_ANNOTATION, annot);

            doc.add(p2);
            doc.getPdfDocument().getPage(1).getPdfObject().getAsArray(PdfName.Annots)
                    .getAsDictionary(0).put(PdfName.Alt, new PdfString("Alt description"));
        });
        framework.assertBothFail("linkAnnotNestedWithinLinkWithAnAlternateDescriptionTest");
    }

    @Test
    public void linkAnnotNotDirectChildOfLinkButHiddenTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            annot.setFlag(PdfAnnotation.HIDDEN);
            page.addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkButHiddenTest");
    }

    @Test
    public void linkAnnotNotDirectChildOfLinkButOutsideTest1() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            Rectangle rect = new Rectangle(10000, 65000, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            page.addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkButOutsideTest1");
    }

    @Test
    public void linkAnnotNotDirectChildOfLinkButOutsideTest2() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            page.setCropBox(new Rectangle(1000, 1000, 500, 500));

            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            page.addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkButOutsideTest2");
    }

    @Test
    public void screenAnnotationWithMediaDataTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).put(PdfName.Alt, new PdfArray());
            page.addAnnotation(screen);
        });
        framework.assertBothValid("screenAnnotationWithValidMediaDataTest");
    }

    @Test
    public void screenAnnotationAsAAWithMediaDataTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAdditionalAction(PdfName.E, action);
            screen.setContents("screen annotation");
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).put(PdfName.Alt, new PdfArray());
            page.addAnnotation(screen);
        });
        framework.assertBothValid("screenAnnotationWithValidMediaDataTest");
    }

    @Test
    public void screenAnnotationWithBEMediaDataTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "screenAnnotationWithBEMediaDataTest.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfPage page = pdfDoc.addNewPage();
        String file = "sample.wav";
        String mimeType = "audio/x-wav";
        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, file);
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfDictionary be = new PdfDictionary();
        PdfDictionary mediaClipData = new PdfMediaClipData(file, spec, mimeType).getPdfObject();
        mediaClipData.put(PdfName.Alt, new PdfArray());
        be.put(PdfName.C, mediaClipData);

        PdfDictionary rendition = new PdfDictionary();
        rendition.put(PdfName.S, PdfName.MR);
        rendition.put(PdfName.N, new PdfString(MessageFormatUtil.format("Rendition for {0}", file)));
        rendition.put(PdfName.BE, be);

        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Rendition).
                put(PdfName.OP, new PdfNumber(0)).put(PdfName.AN, screen.getPdfObject()).
                put(PdfName.R, new PdfRendition(rendition).getPdfObject());

        screen.setAction(action);
        screen.setContents("screen annotation");
        page.addAnnotation(screen);
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_screenAnnotationWithBEMediaDataTest.pdf",
                DESTINATION_FOLDER, "diff_"));
        //Verapdf throws runtime exception, so we don't do this check here.
    }

    @Test
    public void screenAnnotationWithMHMediaDataTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "screenAnnotationWithMHMediaDataTest.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfPage page = pdfDoc.addNewPage();
        String file = "sample.wav";
        String mimeType = "audio/x-wav";
        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, file);
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfDictionary mh = new PdfDictionary();
        PdfDictionary mediaClipData = new PdfMediaClipData(file, spec, mimeType).getPdfObject();
        mediaClipData.put(PdfName.Alt, new PdfArray());
        mh.put(PdfName.C, mediaClipData);

        PdfDictionary rendition = new PdfDictionary();
        rendition.put(PdfName.S, PdfName.MR);
        rendition.put(PdfName.N, new PdfString(MessageFormatUtil.format("Rendition for {0}", file)));
        rendition.put(PdfName.MH, mh);

        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Rendition).
                put(PdfName.OP, new PdfNumber(0)).put(PdfName.AN, screen.getPdfObject()).
                put(PdfName.R, new PdfRendition(rendition).getPdfObject());

        screen.setAction(action);
        screen.setContents("screen annotation");
        page.addAnnotation(screen);
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_screenAnnotationWithMHMediaDataTest.pdf",
                DESTINATION_FOLDER, "diff_"));
        //Verapdf throws runtime exception, so we don't do this check here.
    }

    @Test
    public void screenAnnotationWithMHWithoutAltMediaDataTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "screenAnnotationWithInvalidMHMediaDataTest.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfPage page = pdfDoc.addNewPage();
        String file = "sample.wav";
        String mimeType = "audio/x-wav";
        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, file);
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfDictionary mh = new PdfDictionary();
        PdfDictionary mediaClipData = new PdfMediaClipData(file, spec, mimeType).getPdfObject();
        mh.put(PdfName.C, mediaClipData);

        PdfDictionary rendition = new PdfDictionary();
        rendition.put(PdfName.S, PdfName.MR);
        rendition.put(PdfName.N, new PdfString(MessageFormatUtil.format("Rendition for {0}", file)));
        rendition.put(PdfName.MH, mh);

        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Rendition).
                put(PdfName.OP, new PdfNumber(0)).put(PdfName.AN, screen.getPdfObject()).
                put(PdfName.R, new PdfRendition(rendition).getPdfObject());

        screen.setAction(action);
        screen.setContents("screen annotation");
        page.addAnnotation(screen);

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            pdfDoc.close();
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP, e.getMessage());
        //Verapdf throws runtime exception, so we don't do this check here.
    }

    @Test
    public void screenAnnotationWithoutAltInMediaDataTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            page.addAnnotation(screen);
        });
        framework.assertBothFail("screenAnnotationWithMediaDataTest", PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP);
    }

    @Test
    public void screenAnnotationAsAAWithoutAltInMediaDataTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAdditionalAction(PdfName.E, action);
            screen.setContents("screen annotation");
            page.addAnnotation(screen);
        });
        framework.assertBothFail("screenAnnotationWithMediaDataTest", PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP);
    }

    @Test
    public void screenAnnotationWithoutCTInMediaDataTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).put(PdfName.Alt, new PdfArray());
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).remove(PdfName.CT);
            page.addAnnotation(screen);
        });
        framework.assertBothFail("screenAnnotationWithMediaDataTest", PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP);
    }

    @Test
    public void linkAnnotNotDirectChildOfLinkInvalidCropTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "linkAnnotNotDirectChildOfLinkInvalidCropTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page = pdfDoc.addNewPage();
        PdfArray array = new PdfArray();
        array.add(new PdfString("hey"));
        page.put(PdfName.CropBox, array);

        Rectangle rect = new Rectangle(10000, 6500, 400, 100);
        PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
        annot.setContents("link annot");
        page.addAnnotation(annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        // VeraPdf doesn't complain, but the document is invalid, so it is also accepted behaviour
        Assertions.assertEquals(PdfUAExceptionMessageConstants.LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK, e.getMessage());
    }

    @Test
    public void undefinedAnnotTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            PdfCustomAnnot annot = new PdfCustomAnnot(new Rectangle(100, 650, 400, 100));
            annot.setContents("Content of unique annot");
            page.addAnnotation(annot);
        });
        framework.assertBothValid("undefinedAnnotTest");
    }

    @Test
    public void tabsEntryAbsentInPageTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfPage.addAnnotation(annot);
            pdfPage.getPdfObject().remove(PdfName.Tabs);
        });
        framework.assertBothFail("tabsEntryAbsentInPageTest", PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S);
    }

    @Test
    public void tabsEntryNotSInPageTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfPage.addAnnotation(annot);
            pdfPage.setTabOrder(PdfName.O);
        });
        framework.assertBothFail("tabsEntryNotSInPageTest", PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S);
    }

    @Test
    public void invalidTabsEntryButAnnotInvisibleTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            annot.setFlag(PdfAnnotation.HIDDEN);
            pdfPage.addAnnotation(annot);
            pdfPage.setTabOrder(PdfName.O);
        });
        framework.assertBothFail("invalidTabsEntryButAnnotInvisibleTest", PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S);
    }

    @Test
    public void ua1PrinterMAnnotIsInLogicalStructureTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();

            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas
                    .saveState()
                    .circle(265, 795, 5)
                    .setColor(ColorConstants.GREEN, true)
                    .fill()
                    .restoreState();
            canvas.release();

            PdfPrinterMarkAnnotation annot = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            annot.setContents("link annot");
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothFail("ua1PrinterMAnnotIsInLogicalStructureTest",
                PdfUAExceptionMessageConstants.PRINTER_MARK_IS_NOT_PERMITTED);
    }

    @Test
    public void ua1PrinterMAnnotNotInTagStructureTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();

            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas
                    .saveState()
                    .circle(265, 795, 5)
                    .setColor(ColorConstants.GREEN, true)
                    .fill()
                    .restoreState();
            canvas.release();

            PdfPrinterMarkAnnotation annot = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            annot.setContents("link annot");
            // Put false as 3rd parameter to not tag annotation
            pdfPage.addAnnotation(-1, annot, false);


            PdfCustomAnnot annot2 = new PdfCustomAnnot(new Rectangle(100, 650, 400, 100));
            annot2.setContents("Content of unique annot");
            pdfPage.addAnnotation(annot2);


            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
            stamp.setStampName(PdfName.Approved);
            stamp.setContents("stamp contents");
            stamp.getPdfObject().put(PdfName.Type, PdfName.Annot);
            pdfPage.addAnnotation(stamp);
        });
        framework.assertBothValid("ua1PrinterMAnnotNotInTagStructureTest");
    }

    private PdfTextAnnotation createRichTextAnnotation() {
        PdfTextAnnotation annot = new PdfTextAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setContents("Rich media annot");
        PdfDictionary annotPdfObject = annot.getPdfObject();
        annotPdfObject.put(PdfName.Subtype, PdfName.RichMedia);
        return annot;
    }

    private PdfInkAnnotation createInkAnnotation() {
        float[] array1 = {100, 100, 100, 200, 200, 200, 300, 300};
        PdfArray firstPoint = new PdfArray(array1);

        PdfArray resultArray = new PdfArray();
        resultArray.add(firstPoint);

        PdfDictionary borderStyle = new PdfDictionary();
        borderStyle.put(PdfName.Type, PdfName.Border);
        borderStyle.put(PdfName.W, new PdfNumber(3));

        PdfInkAnnotation ink = new PdfInkAnnotation(new Rectangle(0, 0, 575, 842), resultArray);
        ink.setBorderStyle(borderStyle);
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        ink.setColor(colors);
        ink.setContents("ink annotation");

        return ink;
    }

    private Pdf3DAnnotation create3DAnnotation() {
        PdfStream stream3D = new PdfStream();
        stream3D.put(PdfName.Type, PdfName._3D);
        stream3D.put(PdfName.Subtype, new PdfName("U3D"));
        stream3D.setCompressionLevel(CompressionConstants.UNDEFINED_COMPRESSION);

        Pdf3DAnnotation annot = new Pdf3DAnnotation(new Rectangle(300, 300, 100, 50), stream3D);

        PdfDictionary dict3D = new PdfDictionary();
        dict3D.put(PdfName.Type, PdfName._3DView);
        dict3D.put(new PdfName("XN"), new PdfString("Default"));
        dict3D.put(new PdfName("IN"), new PdfString("Unnamed"));
        dict3D.put(new PdfName("MS"), PdfName.M);
        dict3D.put(new PdfName("C2W"),
                new PdfArray(new float[] {1, 0, 0, 0, 0, -1, 0, 1, 0, 3, -235, 28}));
        dict3D.put(PdfName.CO, new PdfNumber(235));

        annot.setDefaultInitialView(dict3D);
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setAppearance(PdfName.N, new PdfStream());
        annot.setContents("3D annot");
        return annot;
    }

    private PdfRedactAnnotation createRedactionAnnotation() {
        PdfRedactAnnotation redact = new PdfRedactAnnotation(new Rectangle(0, 0, 100, 50))
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(DeviceCmyk.MAGENTA)
                        .setFont(StandardAnnotationFont.CourierOblique)
                        .setFontSize(20))
                .setOverlayText(new PdfString("Redact CMYK courier-oblique"));

        redact.setContents("redact annotation");
        return redact;
    }

    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static class PdfCustomAnnot extends PdfAnnotation {

        protected PdfCustomAnnot(Rectangle rect) {
            super(rect);
        }

        @Override
        public PdfName getSubtype() {
            return new PdfName("CustomUniqueAnnot");
        }
    }
}
