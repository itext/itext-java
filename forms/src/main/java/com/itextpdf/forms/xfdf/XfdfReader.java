/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
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

import java.util.List;
import java.util.Map;

class XfdfReader {

    private static Logger logger = LoggerFactory.getLogger(XfdfReader.class);

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
                logger.warn(LogMessageConstant.XFDF_HREF_ATTRIBUTE_AND_PDF_DOCUMENT_NAME_ARE_DIFFERENT);
            }
        } else {
            logger.warn(LogMessageConstant.XFDF_NO_F_OBJECT_TO_COMPARE);
        }
        //TODO DEVSIX-4026 check for ids original/modified compatability with those in pdf document

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, false);
        if (form != null) {
            mergeFields(xfdfObject.getFields(), form);
            mergeAnnotations(xfdfObject.getAnnots(), pdfDocument);
        }

    }

    /**
     * Merges existing FieldsObject and children FieldObject entities into the form of the pdf document
     * associated with it.
     * @param fieldsObject object containing acroform fields data to be merged.
     * @param form acroform to be filled with xfdf data.
     */
    private void mergeFields(FieldsObject fieldsObject, PdfAcroForm form) {
        if (fieldsObject != null && fieldsObject.getFieldList() != null && !fieldsObject.getFieldList().isEmpty()) {

            Map<String, PdfFormField> formFields = form.getFormFields();

            for (FieldObject xfdfField : fieldsObject.getFieldList()) {
                String name = xfdfField.getName();
                if (formFields.get(name) != null && xfdfField.getValue() != null) {
                    formFields.get(name).setValue(xfdfField.getValue());
                } else {
                    logger.error(LogMessageConstant.XFDF_NO_SUCH_FIELD_IN_PDF_DOCUMENT);
                }
            }
        }
    }

    /**
     * Merges existing XfdfObject into pdf document associated with it.
     *
     * @param annotsObject    The AnnotsObject with children AnnotObject entities to be mapped into PdfAnnotations.
     * @param pdfDocument     The associated pdf document.
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
    }

    private void addCommonAnnotationAttributes(PdfAnnotation annotation, AnnotObject annotObject) {
        annotation.setFlags(XfdfObjectUtils.convertFlagsFromString(annotObject.getAttributeValue(XfdfConstants.FLAGS)));
        annotation.setColor(XfdfObjectUtils.convertColorFloatsFromString(annotObject.getAttributeValue(XfdfConstants.COLOR)));
        annotation.setDate(new PdfString(annotObject.getAttributeValue(XfdfConstants.DATE)));
        annotation.setName(new PdfString(annotObject.getAttributeValue(XfdfConstants.NAME)));
        annotation.setTitle(new PdfString(annotObject.getAttributeValue(XfdfConstants.TITLE)));
    }

    private void addMarkupAnnotationAttributes(PdfMarkupAnnotation annotation, AnnotObject annotObject) {
        annotation.setCreationDate(new PdfString(annotObject.getAttributeValue(XfdfConstants.CREATION_DATE)));
        annotation.setSubject(new PdfString(annotObject.getAttributeValue(XfdfConstants.SUBJECT)));
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

                    pdfTextAnnotation.setIconName(new PdfName(annotObject.getAttributeValue(XfdfConstants.ICON)));
                    if(annotObject.getAttributeValue(XfdfConstants.STATE) != null) {
                        pdfTextAnnotation.setState(new PdfString(annotObject.getAttributeValue(XfdfConstants.STATE)));
                    }
                    if(annotObject.getAttributeValue(XfdfConstants.STATE_MODEL) != null) {
                        pdfTextAnnotation.setStateModel(new PdfString(annotObject.getAttributeValue(XfdfConstants.STATE_MODEL)));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttributeValue(XfdfConstants.PAGE)))
                            .addAnnotation(pdfTextAnnotation);
                    break;
                case XfdfConstants.HIGHLIGHT:
                    PdfTextMarkupAnnotation pdfHighLightAnnotation = new PdfTextMarkupAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.Highlight, XfdfObjectUtils.convertQuadPointsFromCoordsString(annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfHighLightAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfHighLightAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfHighLightAnnotation);
                    break;
                case XfdfConstants.UNDERLINE:
                    PdfTextMarkupAnnotation pdfUnderlineAnnotation = new PdfTextMarkupAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.Underline, XfdfObjectUtils.convertQuadPointsFromCoordsString(annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfUnderlineAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfUnderlineAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfUnderlineAnnotation);
                    break;
                case XfdfConstants.STRIKEOUT:
                    PdfTextMarkupAnnotation pdfStrikeoutAnnotation = new PdfTextMarkupAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.StrikeOut, XfdfObjectUtils.convertQuadPointsFromCoordsString(annotObject.getAttributeValue(XfdfConstants.COORDS)));

                    addCommonAnnotationAttributes(pdfStrikeoutAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfStrikeoutAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfStrikeoutAnnotation);
                    break;
                case XfdfConstants.SQUIGGLY:
                    PdfTextMarkupAnnotation pdfSquigglyAnnotation = new PdfTextMarkupAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                            PdfName.Squiggly, XfdfObjectUtils.convertQuadPointsFromCoordsString(annotObject.getAttributeValue(XfdfConstants.COORDS)));

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
                    PdfCircleAnnotation pdfCircleAnnotation = new PdfCircleAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)));

                    addCommonAnnotationAttributes(pdfCircleAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfCircleAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.FRINGE) != null) {
                        pdfCircleAnnotation.setRectangleDifferences(XfdfObjectUtils.convertFringeFromString(annotObject.getAttributeValue(XfdfConstants.FRINGE)));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfCircleAnnotation);
                    break;
                case XfdfConstants.SQUARE:
                    PdfSquareAnnotation pdfSquareAnnotation = new PdfSquareAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)));

                    addCommonAnnotationAttributes(pdfSquareAnnotation, annotObject);
                    addMarkupAnnotationAttributes(pdfSquareAnnotation, annotObject);

                    if (annotObject.getAttributeValue(XfdfConstants.FRINGE) != null) {
                        pdfSquareAnnotation.setRectangleDifferences(XfdfObjectUtils.convertFringeFromString(annotObject.getAttributeValue(XfdfConstants.FRINGE)));
                    }

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(pdfSquareAnnotation);
                    break;
                //XfdfConstants.CARET
                case XfdfConstants.POLYGON:
                    Rectangle rect = XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT));
                    float[] vertices =  XfdfObjectUtils.convertVerticesFromString(annotObject.getVertices());
                    PdfPolyGeomAnnotation polygonAnnotation = PdfPolyGeomAnnotation.createPolygon(rect, vertices);

                    addCommonAnnotationAttributes(polygonAnnotation, annotObject);
                    addMarkupAnnotationAttributes(polygonAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(polygonAnnotation);
                    break;
                case XfdfConstants.POLYLINE:
                    Rectangle polylineRect = XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT));
                    float[] polylineVertices =  XfdfObjectUtils.convertVerticesFromString(annotObject.getVertices());
                    PdfPolyGeomAnnotation polylineAnnotation = PdfPolyGeomAnnotation.createPolyLine(polylineRect, polylineVertices);

                    addCommonAnnotationAttributes(polylineAnnotation, annotObject);
                    addMarkupAnnotationAttributes(polylineAnnotation, annotObject);

                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(polylineAnnotation);
                    break;
                case XfdfConstants.STAMP:
                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(new PdfStampAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT))));
                    break;
                //XfdfConstants.INK
                case XfdfConstants.FREETEXT:
                    pdfDocument.getPage(Integer.parseInt(annotObject.getAttribute(XfdfConstants.PAGE).getValue()))
                            .addAnnotation(new PdfFreeTextAnnotation(XfdfObjectUtils.convertRectFromString(annotObject.getAttributeValue(XfdfConstants.RECT)),
                                    annotObject.getContents()));
                    break;
                //XfdfConstants.FILEATTACHMENT
                //XfdfConstants.SOUND
                //XfdfConstants.LINK
                //XfdfConstants.REDACT
                //XfdfConstants.PROJECTION
                default: logger.warn(MessageFormatUtil.format(LogMessageConstant.XFDF_ANNOTATION_IS_NOT_SUPPORTED, annotName));
                    break;
            }

        }
    }
}
