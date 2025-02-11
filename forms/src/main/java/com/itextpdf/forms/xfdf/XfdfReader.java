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
package com.itextpdf.forms.xfdf;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPolyGeomAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFreeTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XfdfReader {

    private static final Logger logger = LoggerFactory.getLogger(XfdfReader.class);

    private final Map<AnnotObject, PdfTextAnnotation> annotationsWithInReplyTo = new HashMap<>();

    /**
     * Merges existing XfdfObject into pdf document associated with it.
     *
     * @param xfdfObject      The object to be merged.
     * @param pdfDocument     The associated pdf document.
     * @param pdfDocumentName The name of the associated pdf document.
     */
    void mergeXfdfIntoPdf(XfdfObject xfdfObject, PdfDocument pdfDocument, String pdfDocumentName) {
        if (xfdfObject.getF() != null && xfdfObject.getF().getHref() != null) {
            if (pdfDocumentName.equalsIgnoreCase(xfdfObject.getF().getHref())) {
                logger.info("Xfdf href and pdf name are equal. Continue merge");
            } else {
                logger.warn(IoLogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT);
            }
        } else {
            logger.warn(IoLogMessageConstant.XFDF_NO_F_OBJECT_TO_COMPARE);
        }
        //TODO DEVSIX-4026 check for ids original/modified compatability with those in pdf document

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, false);
        if (form != null) {
            mergeFields(xfdfObject.getFields(), form);
            mergeAnnotations(xfdfObject.getAnnots(), pdfDocument);
        }

    }

    /**
     * Merges existing FieldsObject and children FieldObject entities into the form of the pdf document
     * associated with it.
     *
     * @param fieldsObject object containing acroform fields data to be merged.
     * @param form         acroform to be filled with xfdf data.
     */
    private void mergeFields(FieldsObject fieldsObject, PdfAcroForm form) {
        if (fieldsObject != null && fieldsObject.getFieldList() != null && !fieldsObject.getFieldList().isEmpty()) {

            Map<String, PdfFormField> formFields = form.getAllFormFields();

            for (FieldObject xfdfField : fieldsObject.getFieldList()) {
                String name = xfdfField.getName();
                if (formFields.get(name) != null && xfdfField.getValue() != null) {
                    formFields.get(name).setValue(xfdfField.getValue());
                } else {
                    logger.error(IoLogMessageConstant.XFDF_NO_SUCH_FIELD_IN_PDF_DOCUMENT);
                }
            }
        }
    }

    /**
     * Merges existing XfdfObject into pdf document associated with it.
     *
     * @param annotsObject The AnnotsObject with children AnnotObject entities to be mapped into PdfAnnotations.
     * @param pdfDocument  The associated pdf document.
     */
    private void mergeAnnotations(AnnotsObject annotsObject, PdfDocument pdfDocument) {
        List<AnnotObject> annotList = null;
        if (annotsObject != null) {
            annotList = annotsObject.getAnnotsList();
        }

        if (annotList != null && !annotList.isEmpty()) {
            for (AnnotObject annot : annotList) {
                addAnnotationToPdf(annot, pdfDocument);
            }
        }
        setInReplyTo(pdfDocument);
    }

    private void setInReplyTo(PdfDocument pdfDocument) {
        for (Map.Entry<AnnotObject, PdfTextAnnotation> annots : annotationsWithInReplyTo.entrySet()) {
            AnnotObject xfdfAnnot = annots.getKey();
            String inReplyTo = xfdfAnnot.getAttributeValue(XfdfConstants.IN_REPLY_TO);
            String replyType = xfdfAnnot.getAttributeValue(XfdfConstants.REPLY_TYPE);

            for (PdfAnnotation pdfAnnotation : pdfDocument.getPage(Integer.parseInt(xfdfAnnot
                    .getAttributeValue(XfdfConstants.PAGE))).getAnnotations()) {
                if (pdfAnnotation.getName() != null && inReplyTo.equals(pdfAnnotation.getName().getValue())) {
                    annots.getValue().setInReplyTo(pdfAnnotation);
                    if (replyType != null) {
                        annots.getValue().setReplyType(new PdfName(replyType));
                    }
                }
            }
        }
    }

    private void addCommonAnnotationAttributes(PdfAnnotation annotation, AnnotObject annotObject) {
        String flags = annotObject.getAttributeValue(XfdfConstants.FLAGS);
        String color = annotObject.getAttributeValue(XfdfConstants.COLOR);
        String date = annotObject.getAttributeValue(XfdfConstants.DATE);
        String name = annotObject.getAttributeValue(XfdfConstants.NAME);
        String title = annotObject.getAttributeValue(XfdfConstants.TITLE);
        if (flags != null) {
            annotation.setFlags(XfdfObjectUtils.convertFlagsFromString(flags));
        }
        if (color != null) {
            annotation.setColor(XfdfObjectUtils.convertColorFloatsFromString(
                    annotObject.getAttributeValue(XfdfConstants.COLOR)));
        }
        if (date != null) {
            annotation.setDate(new PdfString(annotObject.getAttributeValue(XfdfConstants.DATE)));
        }
        if (name != null) {
            annotation.setName(new PdfString(annotObject.getAttributeValue(XfdfConstants.NAME)));
        }
        if (title != null) {
            annotation.setTitle(new PdfString(annotObject.getAttributeValue(XfdfConstants.TITLE)));
        }
    }

    private void addMarkupAnnotationAttributes(PdfMarkupAnnotation annotation, AnnotObject annotObject) {
        String creationDate = annotObject.getAttributeValue(XfdfConstants.CREATION_DATE);
        String opacity = annotObject.getAttributeValue(XfdfConstants.OPACITY);
        String subject = annotObject.getAttributeValue(XfdfConstants.SUBJECT);
        if (creationDate != null) {
            annotation.setCreationDate(new PdfString(creationDate));
        }
        if (opacity != null) {
            annotation.setOpacity(new PdfNumber(Double.parseDouble(opacity)));
        }
        if (subject != null) {
            annotation.setSubject(new PdfString(subject));
        }
    }

    private void addBorderStyleAttributes(PdfAnnotation annotation, AnnotObject annotObject) {
        PdfDictionary borderStyle = annotation.getPdfObject().getAsDictionary(PdfName.BS);
        if (borderStyle == null) {
            borderStyle = new PdfDictionary();
        }

        String width = annotObject.getAttributeValue(XfdfConstants.WIDTH);
        String dashes = annotObject.getAttributeValue(XfdfConstants.DASHES);
        String style = annotObject.getAttributeValue(XfdfConstants.STYLE);
        if (width != null) {
            borderStyle.put(PdfName.W, new PdfNumber(Double.parseDouble(width)));
        }
        if (dashes != null) {
            borderStyle.put(PdfName.D, XfdfObjectUtils.convertDashesFromString(dashes));
        }
        if (style != null && !"cloudy".equals(style)) {
            borderStyle.put(PdfName.S, new PdfName(style.substring(0, 1).toUpperCase()));
        }
        if (borderStyle.size() > 0) {
            annotation.put(PdfName.BS, borderStyle);
        }
    }

    private void addBorderEffectAttributes(PdfAnnotation annotation, AnnotObject annotObject) {
        PdfDictionary borderEffect = annotation.getPdfObject().getAsDictionary(PdfName.BE);
        if (borderEffect == null) {
            borderEffect = new PdfDictionary();
        }
        String intensity = annotObject.getAttributeValue(XfdfConstants.INTENSITY);
        boolean isCloudyEffectSet = intensity != null;
        if (isCloudyEffectSet) {
            borderEffect.put(PdfName.S, new PdfName("C"));
            borderEffect.put(PdfName.I, new PdfNumber(Double.parseDouble(intensity)));
            annotation.put(PdfName.BE, borderEffect);
        }
    }

    private void addAnnotationToPdf(AnnotObject annotObject, PdfDocument pdfDocument) {
        String annotName = annotObject.getName();
        if (annotName != null) {
            switch (annotName) {
                //TODO DEVSIX-4027 add all attributes properly one by one
                case XfdfConstants.TEXT:
                    PdfTextAnnotation pdfTextAnnotation = new PdfTextAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)));
                    addCommonAnnotationAttributes(pdfTextAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfTextAnnotation, annotObject);

                    String icon = annotObject.getAttributeValue(XfdfConstants.ICON);
                    String state = annotObject.getAttributeValue(XfdfConstants.STATE);
                    String stateModel = annotObject.getAttributeValue(XfdfConstants.STATE_MODEL);

                    if (icon != null) {
                        pdfTextAnnotation.setIconName(new PdfName(icon));
                    }
                    if (stateModel != null) {
                        pdfTextAnnotation.setStateModel(new PdfString(stateModel));
                        if (state == null) {
                            state = "Marked".equals(stateModel) ? "Unmarked" : "None";
                        }
                        pdfTextAnnotation.setState(new PdfString(state));
                    }

                    String inReplyTo = annotObject.getAttributeValue(XfdfConstants.IN_REPLY_TO);
                    if (inReplyTo != null) {
                        annotationsWithInReplyTo.put(annotObject, pdfTextAnnotation);
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttributeValue(XfdfConstants.PAGE)))
                            .addAnnotation(pdfTextAnnotation);
                    break;
                case XfdfConstants.HIGHLIGHT:
                    PdfTextMarkupAnnotation pdfHighLightAnnotation = new PdfTextMarkupAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.Highlight, XfdfObjectUtils.convertQuadPointsFromCoordsString(
                            annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfHighLightAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfHighLightAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfHighLightAnnotation);
                    break;
                case XfdfConstants.UNDERLINE:
                    PdfTextMarkupAnnotation pdfUnderlineAnnotation = new PdfTextMarkupAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.Underline, XfdfObjectUtils.convertQuadPointsFromCoordsString(
                            annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfUnderlineAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfUnderlineAnnotation, annotObject);

                    String intent = annotObject.getAttributeValue(XfdfConstants.INTENT);
                    if (intent != null) {
                        pdfUnderlineAnnotation.setIntent(new PdfName(intent));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfUnderlineAnnotation);
                    break;
                case XfdfConstants.STRIKEOUT:
                    PdfTextMarkupAnnotation pdfStrikeoutAnnotation = new PdfTextMarkupAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.StrikeOut, XfdfObjectUtils.convertQuadPointsFromCoordsString(
                            annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfStrikeoutAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfStrikeoutAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfStrikeoutAnnotation);
                    break;
                case XfdfConstants.SQUIGGLY:
                    PdfTextMarkupAnnotation pdfSquigglyAnnotation = new PdfTextMarkupAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.Squiggly, XfdfObjectUtils.convertQuadPointsFromCoordsString(
                            annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfSquigglyAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfSquigglyAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfSquigglyAnnotation);
                    break;
//                case XfdfConstants.LINE:
//                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
//                            .addAnnotation(new PdfLineAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)), XfdfObjectUtils.convertVerticesFromString(annotObject.getVertices())));
//                    break;
                case XfdfConstants.CIRCLE:
                    PdfCircleAnnotation pdfCircleAnnotation = new PdfCircleAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)));

                    addCommonAnnotationAttributes(pdfCircleAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfCircleAnnotation, annotObject);

                    addBorderStyleAttributes(pdfCircleAnnotation, annotObject);
                    addBorderEffectAttributes(pdfCircleAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR) != null) {
                        pdfCircleAnnotation.setInteriorColor(XfdfObjectUtils.convertColorFloatsFromString(
                                annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR)));
                    }

                    if (annotObject.getAttributeValue(XfdfConstants.FRINGE) != null) {
                        pdfCircleAnnotation.setRectangleDifferences(XfdfObjectUtils.convertFringeFromString(
                                annotObject.getAttributeValue(XfdfConstants.FRINGE)));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfCircleAnnotation);
                    break;
                case XfdfConstants.SQUARE:
                    PdfSquareAnnotation pdfSquareAnnotation = new PdfSquareAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)));

                    addCommonAnnotationAttributes(pdfSquareAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfSquareAnnotation, annotObject);

                    addBorderStyleAttributes(pdfSquareAnnotation, annotObject);
                    addBorderEffectAttributes(pdfSquareAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR) != null) {
                        pdfSquareAnnotation.setInteriorColor(XfdfObjectUtils.convertColorFloatsFromString(
                                annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR)));
                    }

                    if (annotObject.getAttributeValue(XfdfConstants.FRINGE) != null) {
                        pdfSquareAnnotation.setRectangleDifferences(XfdfObjectUtils.convertFringeFromString(
                                annotObject.getAttributeValue(XfdfConstants.FRINGE)));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfSquareAnnotation);
                    break;
                //XfdfConstants.CARET
                case XfdfConstants.POLYGON:
                    Rectangle rect = XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT));
                    float[] vertices = XfdfObjectUtils.convertVerticesFromString(annotObject.getVertices());
                    PdfPolyGeomAnnotation polygonAnnotation = PdfPolyGeomAnnotation.createPolygon(rect, vertices);

                    addCommonAnnotationAttributes(polygonAnnotation, annotObject);
                    addMarkupAnnotationAttributes(polygonAnnotation, annotObject);

                    addBorderStyleAttributes(polygonAnnotation, annotObject);
                    addBorderEffectAttributes(polygonAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR) != null) {
                        polygonAnnotation.setInteriorColor(XfdfObjectUtils.convertColorFloatsFromString(
                                annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR)));
                    }
                    if (annotObject.getAttributeValue(XfdfConstants.INTENT) != null) {
                        polygonAnnotation.setIntent(new PdfName(annotObject.getAttributeValue(XfdfConstants.INTENT)));
                    }
                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(polygonAnnotation);
                    break;
                case XfdfConstants.POLYLINE:
                    Rectangle polylineRect = XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT));
                    float[] polylineVertices = XfdfObjectUtils.convertVerticesFromString(annotObject.getVertices());
                    PdfPolyGeomAnnotation polylineAnnotation = PdfPolyGeomAnnotation.createPolyLine(polylineRect, polylineVertices);

                    addCommonAnnotationAttributes(polylineAnnotation, annotObject);
                    addMarkupAnnotationAttributes(polylineAnnotation, annotObject);

                    addBorderStyleAttributes(polylineAnnotation, annotObject);
                    addBorderEffectAttributes(polylineAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR) != null) {
                        polylineAnnotation.setInteriorColor(XfdfObjectUtils.convertColorFloatsFromString(
                                annotObject.getAttributeValue(XfdfConstants.INTERIOR_COLOR)));
                    }
                    if (annotObject.getAttributeValue(XfdfConstants.INTENT) != null) {
                        polylineAnnotation.setIntent(new PdfName(annotObject.getAttributeValue(XfdfConstants.INTENT)));
                    }
                    String head = annotObject.getAttributeValue(XfdfConstants.HEAD);
                    String tail = annotObject.getAttributeValue(XfdfConstants.TAIL);
                    if (head != null || tail != null) {
                        PdfArray lineEndingStyles = new PdfArray();
                        lineEndingStyles.add(new PdfName(head == null ? "None" : head));
                        lineEndingStyles.add(new PdfName(tail == null ? "None" : tail));
                        polylineAnnotation.setLineEndingStyles(lineEndingStyles);
                    }
                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(polylineAnnotation);
                    break;
                case XfdfConstants.STAMP:
                    PdfStampAnnotation pdfStampAnnotation = new PdfStampAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)));
                    addCommonAnnotationAttributes(pdfStampAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfStampAnnotation, annotObject);
                    if (annotObject.getAttributeValue(XfdfConstants.ICON) != null) {
                        pdfStampAnnotation.setIconName(new PdfName(annotObject.getAttributeValue(XfdfConstants.ICON)));
                    }
                    if (annotObject.getAttributeValue(XfdfConstants.ROTATION) != null) {
                        pdfStampAnnotation.setRotation(Integer.parseInt(
                                annotObject.getAttributeValue(XfdfConstants.ROTATION)));
                    }
                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfStampAnnotation);
                    break;
                //XfdfConstants.INK
                case XfdfConstants.FREETEXT:
                    PdfFreeTextAnnotation pdfFreeTextAnnotation = new PdfFreeTextAnnotation(
                            XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            annotObject.getContents());
                    addCommonAnnotationAttributes(pdfFreeTextAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfFreeTextAnnotation, annotObject);

                    addBorderStyleAttributes(pdfFreeTextAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.ROTATION) != null) {
                        pdfFreeTextAnnotation.setRotation(Integer.parseInt(
                                annotObject.getAttributeValue(XfdfConstants.ROTATION)));
                    }
                    if (annotObject.getAttributeValue(XfdfConstants.JUSTIFICATION) != null) {
                        pdfFreeTextAnnotation.setJustification(XfdfObjectUtils.convertJustificationFromStringToInteger(
                                annotObject.getAttributeValue(XfdfConstants.JUSTIFICATION)));
                    }
                    if (annotObject.getAttributeValue(XfdfConstants.INTENT) != null) {
                        pdfFreeTextAnnotation.setIntent(new PdfName(
                                annotObject.getAttributeValue(XfdfConstants.INTENT)));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfFreeTextAnnotation);
                    break;
                //XfdfConstants.FILEATTACHMENT
                //XfdfConstants.SOUND
                //XfdfConstants.LINK
                //XfdfConstants.REDACT
                //XfdfConstants.PROJECTION
                default:
                    logger.warn(
                            MessageFormatUtil.format(IoLogMessageConstant.XFDF_ANNOTATION_IS_NOT_SUPPORTED, annotName));
                    break;
            }

        }
    }
}
