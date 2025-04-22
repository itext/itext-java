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
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPrinterMarkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWatermarkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Tag("IntegrationTest")
public class PdfUA2AnnotationTypesTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUA2AnnotationTypesTest/";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    public static List<PdfName> markupAnnotsTypes() {
        return Arrays.asList(PdfName.Text, PdfName.FreeText, PdfName.Line, PdfName.Square, PdfName.Circle,
                PdfName.Polygon, PdfName.PolyLine, PdfName.Highlight, PdfName.Underline, PdfName.Squiggly,
                PdfName.StrikeOut, PdfName.Caret, PdfName.Stamp, PdfName.Ink, PdfName.FileAttachment,
                PdfName.Redaction, PdfName.Projection);
    }

    public static List<PdfName> annotTypesToCheckContents() {
        return Arrays.asList(PdfName.Ink, PdfName.Screen, PdfName._3D, PdfName.RichMedia);
    }

    public static List<PdfName> deprecatedAnnotTypes() {
        return Arrays.asList(PdfName.Sound, PdfName.Movie, PdfName.TrapNet);
    }

    @Test
    public void annotationContentsAndStructureElementAltTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("Contents description");
            pdfPage.addAnnotation(screen);
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            tagPointer.moveToKid(0);
            tagPointer.getProperties().setAlternateDescription("Alt description");
        });
        framework.assertBothFail("annotationContentsAndStructureElementAlt",
                PdfUAExceptionMessageConstants.CONTENTS_AND_ALT_SHALL_BE_IDENTICAL, false, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void pageWithTaggedAnnotTabOrderTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            pdfPage.setTabOrder(PdfName.C);
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("Contents description");
            pdfPage.addAnnotation(screen);
        });
        framework.assertBothFail("pageWithTaggedAnnotTabOrder", PdfUAExceptionMessageConstants.
                PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_VALID_CONTENT, false, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void pageWithNotTaggedAnnotTabOrderTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            pdfPage.setTabOrder(PdfName.R);
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("Contents description");
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(screen.getPdfObject()));
        });
        framework.assertBothFail("pageWithNotTaggedAnnotTabOrder", PdfUAExceptionMessageConstants.
                PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_VALID_CONTENT, false, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationIsNotTaggedTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annotation = new PdfDictionary();
            annotation.put(PdfName.Type, PdfName.Annot);
            annotation.put(PdfName.Subtype, annotType);
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(annotation));
        });
        if (PdfName.Redaction.equals(annotType) || PdfName.Projection.equals(annotType)) {
            framework.assertITextFail("markupAnnotationIsNotTagged_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT, PdfUAConformance.PDF_UA_2);
            framework.assertVeraPdfValid("markupAnnotationIsNotTagged_" + annotType.getValue(),
                    PdfUAConformance.PDF_UA_2);
        } else {
            framework.assertBothFail("markupAnnotationIsNotTagged_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT, false,
                    PdfUAConformance.PDF_UA_2);
        }
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationIsNotTaggedAsAnnotTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            annotation.setContents("Contents description");
            pdfPage.addAnnotation(annotation);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(pdfPage.getPdfObject(), 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ARTIFACT);
        });
        if (PdfName.Redaction.equals(annotType) || PdfName.Projection.equals(annotType)) {
            framework.assertITextFail("markupAnnotationIsNotTaggedAsAnnot_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT, PdfUAConformance.PDF_UA_2);
            framework.assertVeraPdfValid("markupAnnotationIsNotTaggedAsAnnot_" + annotType.getValue(),
                    PdfUAConformance.PDF_UA_2);
        } else {
            framework.assertBothFail("markupAnnotationIsNotTaggedAsAnnot_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT, false,
                    PdfUAConformance.PDF_UA_2);
        }
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationRCAndContentsTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            String richText = "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p style=\"color:#FF0000;\">Some&#13;</p>" +
                    "<p style=\"color:#1E487C;\">Rich Text&#13;</p></body>";
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            annot.put(PdfName.RC, new PdfString(richText, PdfEncodings.PDF_DOC_ENCODING));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            annotation.setContents("Different");
            pdfPage.addAnnotation(annotation);
        });
        if (PdfName.Redaction.equals(annotType) || PdfName.Projection.equals(annotType)) {
            framework.assertITextFail("markupAnnotationRCAndContents_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS, PdfUAConformance.PDF_UA_2);
            framework.assertVeraPdfValid("markupAnnotationRCAndContents_" + annotType.getValue(),
                    PdfUAConformance.PDF_UA_2);
        } else {
            framework.assertBothFail("markupAnnotationRCAndContents_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS, false,
                    PdfUAConformance.PDF_UA_2);
        }
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationValidTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            String value = "Red\rBlue\r";
            String richText = "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p style=\"color:#FF0000;\">Red&#13;</p>" +
                    "<p style=\"color:#1E487C;\">Blue&#13;</p></body>";
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            annot.put(PdfName.RC, new PdfString(richText, PdfEncodings.PDF_DOC_ENCODING));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            annotation.setContents(value);
            pdfPage.addAnnotation(annotation);
        });
        framework.assertBothValid("markupAnnotation_" + annotType.getValue(), PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void rubberStampAnnotationNoNameAndContentsTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(stamp);
        });
        framework.assertBothFail("rubberStampAnnotationNoNameAndContents", PdfUAExceptionMessageConstants.
                STAMP_ANNOT_SHALL_SPECIFY_NAME_OR_CONTENTS, false, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("annotTypesToCheckContents")
    public void annotationNoContentsTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            pdfPage.addAnnotation(annotation);
        });
        framework.assertBothFail("annotationNoContents_" + annotType.getValue(),
                PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY, false, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("annotTypesToCheckContents")
    public void annotationEmptyContentsTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            annotation.setContents("");
            pdfPage.addAnnotation(annotation);
        });
        framework.assertITextFail("annotationEmptyContents_" + annotType.getValue(),
                PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY, PdfUAConformance.PDF_UA_2);
        framework.assertVeraPdfValid("annotationEmptyContents_" + annotType.getValue(), PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void popupAnnotationTaggedAsAnnotTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfPopupAnnotation popupAnnotation = new PdfPopupAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(popupAnnotation);
        });
        framework.assertBothFail("popupAnnotationTaggedAsAnnot", PdfUAExceptionMessageConstants.
                POPUP_ANNOTATIONS_ARE_NOT_ALLOWED, false, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void popupAnnotationTaggedAsArtifactTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfPopupAnnotation popupAnnotation = new PdfPopupAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(popupAnnotation);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(pdfPage.getPdfObject(), 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ARTIFACT);
        });
        framework.assertBothFail("popupAnnotationTaggedAsArtifact", PdfUAExceptionMessageConstants.
                POPUP_ANNOTATIONS_ARE_NOT_ALLOWED, false, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void fileAttachmentAnnotationValidTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, "file".getBytes(), "description",
                    "file.txt", null, null, null);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("fileAttachmentAnnotationValid", PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void fileAttachmentAnnotationInvalidFileSpecTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, "file".getBytes(), "description",
                    "file.txt", null, null, null);
            PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
            fsDict.remove(PdfName.AFRelationship);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothFail("fileAttachmentAnnotationInvalidFileSpec", PdfUAExceptionMessageConstants.
                FILE_SPEC_SHALL_CONTAIN_AFRELATIONSHIP, false, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("deprecatedAnnotTypes")
    public void deprecatedAnnotationTypeTest(PdfName annotType) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            pdfPage.addAnnotation(annotation);
        });
        framework.assertBothFail("deprecatedAnnotationType_" + annotType.getValue(), MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.DEPRECATED_ANNOTATIONS_ARE_NOT_ALLOWED, annotType.getValue()), false,
                PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void widgetAnnotationZeroWidthAndHeightTaggedAsFormTest() throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                CheckBox cb = new CheckBox("name");
                cb.setAlternativeDescription("Contents");
                cb.setProperty(Property.WIDTH, UnitValue.createPointValue(0));
                cb.setProperty(Property.HEIGHT, UnitValue.createPointValue(0));
                cb.setPdfConformance(PdfConformance.PDF_UA_2);
                cb.setInteractive(true);
                return cb;
            }
        });
        framework.assertBothFail("widgetAnnotationZeroWidthAndHeightTaggedAsForm", PdfUAExceptionMessageConstants.
                WIDGET_WITH_ZERO_HEIGHT_SHALL_BE_AN_ARTIFACT, false, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void printerMarkAnnotationTaggedAsAnnotTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
            PdfPrinterMarkAnnotation printerMark = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            pdfPage.addAnnotation(printerMark);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(pdfPage.getPdfObject(), 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ANNOT);
        });
        framework.assertBothFail("printerMarkAnnotationTaggedAsAnnot", PdfUAExceptionMessageConstants.
                PRINTER_MARK_SHALL_BE_AN_ARTIFACT, false, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void printerMarkAnnotationTaggedAsArtifactTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
            PdfPrinterMarkAnnotation printerMark = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            pdfPage.addAnnotation(printerMark);
        });
        framework.assertBothValid("printerMarkAnnotationTaggedAsArtifact", PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void printerMarkAnnotationNotTaggedTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
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
            PdfPrinterMarkAnnotation printerMark = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(printerMark.getPdfObject()));
        });
        framework.assertBothValid("printerMarkAnnotationNotTagged", PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void watermarkAnnotationAsRealContentTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfWatermarkAnnotation annot = new PdfWatermarkAnnotation(new Rectangle(100, 100));
            annot.setContents("Contents");
            annot.put(PdfName.RC, new PdfString("<p>Rich text</p>"));
            pdfPage.addAnnotation(annot);
        });
        framework.assertITextFail("watermarkAnnotationAsRealContent",
                PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS, PdfUAConformance.PDF_UA_2);
        framework.assertVeraPdfValid("watermarkAnnotationAsRealContent", PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void watermarkAnnotationAsArtifactTest() throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfWatermarkAnnotation annot = new PdfWatermarkAnnotation(new Rectangle(100, 100));
            annot.setContents("Contents");
            annot.put(PdfName.RC, new PdfString("<p>Rich text</p>"));
            pdfPage.addAnnotation(annot);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(pdfPage.getPdfObject(), 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ARTIFACT);
        });
        framework.assertBothValid("watermarkAnnotationAsArtifact", PdfUAConformance.PDF_UA_2);
    }
}
