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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAAnnotationsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAAnnotationsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAAnnotationsTest/";
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

    public static List<PdfUAConformance> data() {
        return Arrays.asList(PdfUAConformance.PDF_UA_1, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNotDirectChildOfAnnotLayoutTest(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("linkAnnotNotDirectChildOfAnnotLayoutTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNotDirectChildOfAnnotKernelTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            pdfDoc.addNewPage();
            pdfDoc.getPage(1).addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfAnnotKernelTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void widgetAnnotNoDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(pdfDoc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20))
                    .createCheckBox();

            checkBox.setAlternativeName("widget");

            acroForm.addField(checkBox);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("widgetAnnotNoDirectChildOfAnnotTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 PDF/UA-2 checks
            framework.assertVeraPdfFail("widgetAnnotNoDirectChildOfAnnotTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void widgetAnnotNoDirectChildOfAnnotAutomaticConformanceLevelTest(PdfUAConformance pdfUAConformance)
            throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(pdfDoc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20))
                    .createCheckBox();

            checkBox.setAlternativeName("widget");

            acroForm.addField(checkBox);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("widgetAnnotNoDirectChildAutoConformanceLvl", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 PDF/UA-2 checks
            framework.assertVeraPdfFail("widgetAnnotNoDirectChildAutoConformanceLvl", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void printerMAnnotNoDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
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
            // mark annotation as hidden, because in the scope of the test we check only that PrinterMark isn't enclosed by Annot tag
            annot.setFlag(PdfAnnotation.HIDDEN);

            pdfPage.addAnnotation(annot);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("printerMAnnotNoDirectChildOfAnnotTest", pdfUAConformance);
            String layoutPdf = "layout_printerMAnnotNoDirectChildOfAnnotTest" + "_UA_" + pdfUAConformance.getPart() + ".pdf";
            try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(DESTINATION_FOLDER + layoutPdf))) {
                final IStructureNode docNode = pdfDoc.getStructTreeRoot().getKids().get(0);
                Assertions.assertEquals(PdfName.Document, docNode.getRole());
                Assertions.assertEquals(PdfName.PrinterMark, ((PdfObjRef) docNode.getKids().get(0)).getReferencedObject().get(PdfName.Subtype));
            }
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("printerMAnnotNoDirectChildOfAnnotTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void fileAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("fileAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void stampAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
            stamp.setStampName(PdfName.Approved);
            stamp.setContents("stamp contents");
            stamp.getPdfObject().put(PdfName.Type, PdfName.Annot);
            pdfPage.addAnnotation(stamp);
        });
        framework.assertBothValid("stampAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("screen annotation");
            pdfPage.addAnnotation(screen);
        });
        framework.assertBothValid("screenAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotWithoutContentsAndAltTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(screen);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("screenAnnotWithoutContentsAndAltTest", MessageFormatUtil.format(
                            PdfUAExceptionMessageConstants.ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_OR_ALT_KEY, "Screen"),
                    pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("screenAnnotWithoutContentsAndAltTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void popupWithoutContentOrAltTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfPopupAnnotation popup = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            pdfPage.addAnnotation(popup);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("popupWithoutContentOrAltTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("popupWithoutContentOrAltTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void stampAnnotWithAltTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
            stamp.setStampName(PdfName.Approved);
            stamp.getPdfObject().put(PdfName.Type, PdfName.Annot);

            pdfPage.addAnnotation(stamp);
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            tagPointer.moveToKid(0);
            tagPointer.getProperties().setAlternateDescription("Alt description");
        });
        framework.assertBothValid("stampAnnotWithAltTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotWithAltTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(screen);
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            tagPointer.moveToKid(0);
            tagPointer.getProperties().setAlternateDescription("Alt description");
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("screenAnnotWithAltTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("screenAnnotWithAltTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void inkAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfInkAnnotation ink = createInkAnnotation();
            pdfPage.addAnnotation(ink);
        });
        framework.assertBothValid("inkAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void redactAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfRedactAnnotation redact = createRedactionAnnotation();
            pdfPage.addAnnotation(redact);
        });
        framework.assertBothValid("redactAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void ua3DAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Pdf3DAnnotation annot = create3DAnnotation();
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            // Check that second Annot tag won't be added in PdfPage#addAnnotation
            tagPointer.addTag(StandardRoles.ANNOT);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("ua3DAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void richAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.SECT);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("richAnnotDirectChildOfAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trapNetAnnotNotPermittedTest(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("trapNetAnnotNotPermittedTest",
                    PdfUAExceptionMessageConstants.ANNOT_TRAP_NET_IS_NOT_PERMITTED, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("trapNetAnnotNotPermittedTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void invisibleTrapNetAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("invisibleTrapNetAnnotTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("invisibleTrapNetAnnotTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void soundAnnotDirectChildOfAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfAnnotation annot = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), new PdfStream());
            annot.setContents("some content");
            pdfDoc.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.PART);
            pdfPage.addAnnotation(annot);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("soundAnnotDirectChildOfAnnotTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("soundAnnotDirectChildOfAnnotTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void pushBtnNestedWithinFormTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
            // The rest of the tests for widgets can be found in com.itextpdf.pdfua.checkers.PdfUAFormFieldsTest
            PdfFormField button = new PushButtonFormFieldBuilder(pdfDoc, "push button")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20))
                    .setFont(loadFont())
                    .createPushButton();

            button.setAlternativeName("widget");
            acroForm.addField(button);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("pushBtnNestedWithinFormTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 PDF/UA-2 checks
            framework.assertVeraPdfFail("pushBtnNestedWithinFormTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNotDirectChildOfLinkTest2(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkTest2", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNestedWithinLinkTest(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("linkAnnotNestedWithinLinkTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotWithoutContentsTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));

            Document doc = new Document(pdfDoc);
            Paragraph p2 = new Paragraph("Text");
            p2.setFont(loadFont());
            p2.getAccessibilityProperties().setRole(StandardRoles.LINK).setAlternateDescription("Alt description");
            p2.setProperty(Property.LINK_ANNOTATION, annot);

            doc.add(p2);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("linkAnnotNestedWithinLinkWithAnAltDescr",
                    PdfUAExceptionMessageConstants.LINK_ANNOTATION_SHOULD_HAVE_CONTENTS_KEY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("linkAnnotNestedWithinLinkWithAnAltDescr", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNotDirectChildOfLinkButHiddenTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            annot.setFlag(PdfAnnotation.HIDDEN);
            page.addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkButHiddenTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNotDirectChildOfLinkButOutsideTest1(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            Rectangle rect = new Rectangle(10000, 65000, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            page.addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkButOutsideTest1", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void linkAnnotNotDirectChildOfLinkButOutsideTest2(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            page.setCropBox(new Rectangle(1000, 1000, 500, 500));

            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(PdfAction.createURI("https://itextpdf.com/"));
            annot.setContents("link annot");
            page.addAnnotation(annot);
        });
        framework.assertBothValid("linkAnnotNotDirectChildOfLinkButOutsideTest2", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationWithMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).put(PdfName.Alt, new PdfArray());
            page.addAnnotation(screen);
        });
        framework.assertBothValid("screenAnnotationWithValidMediaDataTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationAsAAWithMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAdditionalAction(PdfName.E, action);
            screen.setContents("screen annotation");
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).put(PdfName.Alt, new PdfArray());
            page.addAnnotation(screen);
        });
        framework.assertBothValid("screenAnnotationWithValidMediaDataTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationWithBEMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            String file = "sample.wav";
            String mimeType = "audio/x-wav";
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + file);
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
        });
        framework.assertBothValid("screenAnnotationWithBEMediaDataTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationWithMHMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            String file = "sample.wav";
            String mimeType = "audio/x-wav";
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + file);
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
        });
        framework.assertBothValid("screenAnnotationWithMHMediaDataTest", pdfUAConformance);
    }

    @Test
    public void screenAnnotationWithMHWithoutAltMediaDataTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "screenAnnotationWithInvalidMHMediaDataTest.pdf";
        PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf));
        PdfPage page = pdfDoc.addNewPage();
        String file = "sample.wav";
        String mimeType = "audio/x-wav";
        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + file);
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
        // Verapdf doesn't fail here but it should
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationWithoutAltInMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            page.addAnnotation(screen);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("screenAnnotationWithMediaDataTest",
                    PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("screenAnnotationWithMediaDataTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationAsAAWithoutAltInMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAdditionalAction(PdfName.E, action);
            screen.setContents("screen annotation");
            page.addAnnotation(screen);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("screenAnnotationWithMediaDataTest",
                    PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("screenAnnotationWithMediaDataTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void screenAnnotationWithoutCTInMediaDataTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, SOURCE_FOLDER + "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).put(PdfName.Alt, new PdfArray());
            action.getPdfObject().getAsDictionary(PdfName.R).getAsDictionary(PdfName.C).remove(PdfName.CT);
            page.addAnnotation(screen);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("screenAnnotationWithMediaDataTest",
                    PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("screenAnnotationWithMediaDataTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void undefinedAnnotTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage page = pdfDoc.addNewPage();

            PdfCustomAnnot annot = new PdfCustomAnnot(new Rectangle(100, 650, 400, 100));
            annot.setContents("Content of unique annot");
            page.addAnnotation(annot);
        });
        framework.assertBothValid("undefinedAnnotTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tabsEntryAbsentInPageTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfPage.addAnnotation(annot);
            pdfPage.getPdfObject().remove(PdfName.Tabs);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("tabsEntryAbsentInPageTest",
                    PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("tabsEntryAbsentInPageTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tabsEntryNotSInPageTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfPage.addAnnotation(annot);
            pdfPage.setTabOrder(PdfName.O);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("tabsEntryNotSInPageTest", PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("tabsEntryNotSInPageTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void invalidTabsEntryButAnnotInvisibleTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            annot.setFlag(PdfAnnotation.HIDDEN);
            pdfPage.addAnnotation(annot);
            pdfPage.setTabOrder(PdfName.O);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("invalidTabsEntryButAnnotInvisibleTest",
                    PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("invalidTabsEntryButAnnotInvisibleTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void printerMAnnotIsInLogicalStructureTest(PdfUAConformance pdfUAConformance) throws IOException {
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

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("printerMAnnotIsInLogicalStructureTest",
                    PdfUAExceptionMessageConstants.PRINTER_MARK_IS_NOT_PERMITTED, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            // TODO DEVSIX-8242 The layout level doesn’t throw an error
            framework.assertVeraPdfFail("printerMAnnotIsInLogicalStructureTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void printerMAnnotNotInTagStructureTest(PdfUAConformance pdfUAConformance) throws IOException {
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
        framework.assertBothValid("printerMAnnotNotInTagStructureTest", pdfUAConformance);
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
