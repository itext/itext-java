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
import com.itextpdf.kernel.pdf.WellTaggedPdfConformance;
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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUA2AnnotationTypesTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUA2AnnotationTypesTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<Object[]> markupAnnotsTypes() {
        return generateAllTypesOfDocuments(
                Arrays.asList(PdfName.Text, PdfName.FreeText, PdfName.Line, PdfName.Square, PdfName.Circle,
                        PdfName.Polygon, PdfName.PolyLine, PdfName.Highlight, PdfName.Underline, PdfName.Squiggly,
                        PdfName.StrikeOut, PdfName.Caret, PdfName.Stamp, PdfName.Ink, PdfName.FileAttachment,
                        PdfName.Redaction, PdfName.Projection));
    }

    public static List<Object[]> annotTypesToCheckContents() {
        return generateAllTypesOfDocuments(Arrays.asList(PdfName.Ink, PdfName.Screen, PdfName._3D, PdfName.RichMedia));
    }

    public static List<Object[]> deprecatedAnnotTypes() {
        return generateAllTypesOfDocuments(Arrays.asList(PdfName.Sound, PdfName.Movie, PdfName.TrapNet));
    }


    public static List<PdfConformance> conformances() {
        return UaValidationTestFramework.getConformanceList(false);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void annotationContentsAndStructureElementAltTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
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
                PdfUAExceptionMessageConstants.CONTENTS_AND_ALT_SHALL_BE_IDENTICAL);

    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void pageWithTaggedAnnotTabOrderTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            pdfPage.setTabOrder(PdfName.C);
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("Contents description");
            pdfPage.addAnnotation(screen);
        });
        framework.assertBothFail("pageWithTaggedAnnotTabOrder",
                PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_VALID_CONTENT);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void pageWithNotTaggedAnnotTabOrderTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            pdfPage.setTabOrder(PdfName.R);
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("Contents description");
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(screen.getPdfObject()));
        });
        framework.assertBothFail("pageWithNotTaggedAnnotTabOrder",
                PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_VALID_CONTENT);

    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationIsNotTaggedTest(PdfName annotType, PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annotation = new PdfDictionary();
            annotation.put(PdfName.Type, PdfName.Annot);
            annotation.put(PdfName.Subtype, annotType);
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(annotation));
        });
        if (PdfName.Redaction.equals(annotType) || PdfName.Projection.equals(annotType)) {
            framework.assertOnlyITextFail("markupAnnotationIsNotTagged_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT);
        } else {
            framework.assertBothFail("markupAnnotationIsNotTagged_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT, false);
        }
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationIsNotTaggedAsAnnotTest(PdfName annotType, PdfConformance conformance)
            throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
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
            framework.assertOnlyITextFail("markupAnnotationIsNotTaggedAsAnnot_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT);
        } else {
            framework.assertBothFail("markupAnnotationIsNotTaggedAsAnnot_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT, false);
        }
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationRCAndContentsTest(PdfName annotType, PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            String richText = "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p style=\"color:#FF0000;\">Some&#13;</p>"
                    + "<p style=\"color:#1E487C;\">Rich Text&#13;</p></body>";
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            annot.put(PdfName.RC, new PdfString(richText, PdfEncodings.PDF_DOC_ENCODING));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            annotation.setContents("Different");
            pdfPage.addAnnotation(annotation);
        });
        if (!conformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            if (PdfName.Redaction.equals(annotType) || PdfName.Projection.equals(annotType)) {
                framework.assertOnlyITextFail("markupAnnotationRCAndContents_" + annotType.getValue(),
                        PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS);
            } else {
                framework.assertBothFail("markupAnnotationRCAndContents_" + annotType.getValue(),
                        PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS, false);
            }
        }
        if (conformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertOnlyITextFail("markupAnnotationRCAndContents_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("markupAnnotsTypes")
    public void markupAnnotationValidTest(PdfName annotType, PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            String value = "Red\rBlue\r";
            String richText = "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p style=\"color:#FF0000;\">Red&#13;</p>"
                    + "<p style=\"color:#1E487C;\">Blue&#13;</p></body>";
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            annot.put(PdfName.RC, new PdfString(richText, PdfEncodings.PDF_DOC_ENCODING));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            annotation.setContents(value);
            pdfPage.addAnnotation(annotation);
        });
        framework.assertBothValid("markupAnnotation_" + annotType.getValue());
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void rubberStampAnnotationNoNameAndContentsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(stamp);
        });
        if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_2
                || conformance.conformsTo(WellTaggedPdfConformance.FOR_ACCESSIBILITY)) {
            framework.assertBothFail("rubberStampAnnotationNoNameAndContents",
                    PdfUAExceptionMessageConstants.STAMP_ANNOT_SHALL_SPECIFY_NAME_OR_CONTENTS, false);
        } else {
            framework.assertOnlyITextFail("rubberStampAnnotationNoNameAndContents",
                    PdfUAExceptionMessageConstants.STAMP_ANNOT_SHALL_SPECIFY_NAME_OR_CONTENTS);
        }
    }

    @ParameterizedTest
    @MethodSource("annotTypesToCheckContents")
    public void annotationNoContentsTest(PdfName annotType, PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            pdfPage.addAnnotation(annotation);
        });
        if ((annotType == PdfName._3D || annotType == PdfName.RichMedia || annotType == PdfName.Ink)
                && conformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            framework.assertBothValid("annotationNoContents_" + annotType.getValue());
        } else {
            framework.assertBothFail("annotationNoContents_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY, false);
        }
    }

    @ParameterizedTest
    @MethodSource("annotTypesToCheckContents")
    public void annotationEmptyContentsTest(PdfName annotType, PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
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
        if (conformance.conformsTo(PdfConformance.WELL_TAGGED_PDF_FOR_REUSE)) {
            if (annotType == PdfName._3D || annotType == PdfName.RichMedia || annotType == PdfName.Ink) {
                framework.assertBothValid("annotationEmptyContents_" + annotType.getValue());
            } else {
                framework.assertOnlyITextFail("annotationEmptyContents_" + annotType.getValue(),
                        PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY);
            }
        } else {
            framework.assertOnlyITextFail("annotationEmptyContents_" + annotType.getValue(),
                    PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY);
        }
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void popupAnnotationTaggedAsAnnotTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfPopupAnnotation popupAnnotation = new PdfPopupAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(popupAnnotation);
        });
        framework.assertBothFail("popupAnnotationTaggedAsAnnot",
                PdfUAExceptionMessageConstants.POPUP_ANNOTATIONS_ARE_NOT_ALLOWED, false);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void popupAnnotationTaggedAsArtifactTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfPopupAnnotation popupAnnotation = new PdfPopupAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(popupAnnotation);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(pdfPage.getPdfObject(), 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ARTIFACT);
        });
        framework.assertBothFail("popupAnnotationTaggedAsArtifact",
                PdfUAExceptionMessageConstants.POPUP_ANNOTATIONS_ARE_NOT_ALLOWED, false);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void fileAttachmentAnnotationValidTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, "file".getBytes(StandardCharsets.UTF_8),
                    "description", "file.txt", null, null, null);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothValid("fileAttachmentAnnotationValid");
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void fileAttachmentAnnotationInvalidFileSpecTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, "file".getBytes(StandardCharsets.UTF_8),
                    "description", "file.txt", null, null, null);
            PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
            fsDict.remove(PdfName.AFRelationship);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            pdfPage.addAnnotation(annot);
        });
        framework.assertBothFail("fileAttachmentAnnotationInvalidFileSpec",
                PdfUAExceptionMessageConstants.FILE_SPEC_SHALL_CONTAIN_AFRELATIONSHIP, false);
    }

    @ParameterizedTest
    @MethodSource("deprecatedAnnotTypes")
    public void deprecatedAnnotationTypeTest(PdfName annotType, PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfDictionary annot = new PdfDictionary();
            annot.put(PdfName.Type, PdfName.Annot);
            annot.put(PdfName.Subtype, annotType);
            annot.put(PdfName.Rect, new PdfArray(new Rectangle(100, 100, 100, 100)));
            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
            pdfPage.addAnnotation(annotation);
        });
        framework.assertBothFail("deprecatedAnnotationType_" + annotType.getValue(),
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.DEPRECATED_ANNOTATIONS_ARE_NOT_ALLOWED,
                        annotType.getValue()), false);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void widgetAnnotationZeroWidthAndHeightTaggedAsFormTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(document -> {
            CheckBox cb = new CheckBox("name");
            cb.setAlternativeDescription("Contents");
            cb.setProperty(Property.WIDTH, UnitValue.createPointValue(0));
            cb.setProperty(Property.HEIGHT, UnitValue.createPointValue(0));
            cb.setPdfConformance(PdfConformance.PDF_UA_2);
            cb.setInteractive(true);
            return cb;
        });
        framework.assertBothFail("widgetAnnotationZeroWidthAndHeightTaggedAsForm",
                PdfUAExceptionMessageConstants.WIDGET_WITH_ZERO_HEIGHT_SHALL_BE_AN_ARTIFACT, false);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void printerMarkAnnotationTaggedAsAnnotTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas.saveState().circle(265, 795, 5).setColor(ColorConstants.GREEN, true).fill().restoreState();
            canvas.release();
            PdfPrinterMarkAnnotation printerMark = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            pdfPage.addAnnotation(printerMark);

            PdfObjRef objRef = pdfDoc.getStructTreeRoot().findObjRefByStructParentIndex(pdfPage.getPdfObject(), 0);
            TagTreePointer p = pdfDoc.getTagStructureContext()
                    .createPointerForStructElem((PdfStructElem) objRef.getParent());
            p.setRole(StandardRoles.ANNOT);
        });
        framework.assertBothFail("printerMarkAnnotationTaggedAsAnnot",
                PdfUAExceptionMessageConstants.PRINTER_MARK_SHALL_BE_AN_ARTIFACT, false);
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void printerMarkAnnotationTaggedAsArtifactTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas.saveState().circle(265, 795, 5).setColor(ColorConstants.GREEN, true).fill().restoreState();
            canvas.release();
            PdfPrinterMarkAnnotation printerMark = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            pdfPage.addAnnotation(printerMark);
        });
        framework.assertBothValid("printerMarkAnnotationTaggedAsArtifact");
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void printerMarkAnnotationNotTaggedTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDoc);
            canvas.saveState().circle(265, 795, 5).setColor(ColorConstants.GREEN, true).fill().restoreState();
            canvas.release();
            PdfPrinterMarkAnnotation printerMark = new PdfPrinterMarkAnnotation(PageSize.A4, form);
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(printerMark.getPdfObject()));
        });
        framework.assertBothValid("printerMarkAnnotationNotTagged");
    }

    @ParameterizedTest
    @MethodSource("conformances")
    public void watermarkAnnotationAsRealContentTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfWatermarkAnnotation annot = new PdfWatermarkAnnotation(new Rectangle(100, 100));
            annot.setContents("Contents");
            annot.put(PdfName.RC, new PdfString("<p>Rich text</p>"));
            pdfPage.addAnnotation(annot);
        });
        framework.assertOnlyITextFail("watermarkAnnotationAsRealContent",
                PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS);
    }


    @ParameterizedTest
    @MethodSource("conformances")
    public void watermarkAnnotationAsArtifactTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfPage pdfPage = pdfDoc.addNewPage();
            PdfWatermarkAnnotation annot = new PdfWatermarkAnnotation(new Rectangle(100, 100));
            annot.setContents("Contents");
            annot.put(PdfName.RC, new PdfString("<p>Rich text</p>"));
            pdfPage.getPdfObject().put(PdfName.Annots, new PdfArray(annot.getPdfObject()));
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            tagPointer.addTag(StandardRoles.ARTIFACT);
            tagPointer.setPageForTagging(pdfPage).addAnnotationTag(annot);
        });
        framework.assertBothValid("watermarkAnnotationAsArtifact");
    }

    private static List<Object[]> generateAllTypesOfDocuments(Iterable<PdfName> things) {
        List<Object[]> list = new ArrayList<>();
        for (PdfConformance conformance : UaValidationTestFramework.getConformanceList(false)) {
            for (PdfName thing : things) {
                list.add(new Object[] {thing, conformance});
            }
        }
        return list;
    }
}
