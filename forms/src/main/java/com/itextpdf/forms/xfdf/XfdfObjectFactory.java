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
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFreeTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPolyGeomAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class XfdfObjectFactory {

    private static final Logger logger = LoggerFactory.getLogger(XfdfObjectFactory.class);

    /**
     * Extracts data from pdf document acroform and annotations into XfdfObject.
     * *
     * @param document Pdf document for data extraction.
     * @param filename The name od pdf document for data extraction.
     * @return XfdfObject containing data from pdf forms and annotations.
     */
    public XfdfObject createXfdfObject(PdfDocument document, String filename) {
        PdfAcroForm form = PdfFormCreator.getAcroForm(document, false);

        XfdfObject resultXfdf = new XfdfObject();
        FieldsObject xfdfFields = new FieldsObject();

        if (form != null && form.getRootFormFields() != null && !form.getRootFormFields().isEmpty()) {
            for (String fieldName : form.getAllFormFields().keySet()) {
                String delims = ".";
                StringTokenizer st = new StringTokenizer(fieldName, delims);
                List<String> nameParts = new ArrayList<>();
                while(st.hasMoreTokens()) {
                    nameParts.add(st.nextToken());
                }
                String name = nameParts.get(nameParts.size() - 1);
                String value = form.getField(fieldName).getValueAsString();
                FieldObject childField = new FieldObject(name, value, false);
                if(nameParts.size() > 1) {
                    FieldObject parentField = new FieldObject();
                    parentField.setName(nameParts.get(nameParts.size() - 2));
                    childField.setParent(parentField);
                }
                xfdfFields.addField(childField);
            }
        }
        resultXfdf.setFields(xfdfFields);

        String original = XfdfObjectUtils.convertIdToHexString(document.getOriginalDocumentId().getValue());
        String modified = XfdfObjectUtils.convertIdToHexString(document.getModifiedDocumentId().getValue());


        IdsObject ids = new IdsObject()
                .setOriginal(original)
                .setModified(modified);
        resultXfdf.setIds(ids);

        FObject f = new FObject(filename);
        resultXfdf.setF(f);

        addAnnotations(document, resultXfdf);

        return resultXfdf;
    }

    /**
     * Extracts data from input stream into XfdfObject. Typically input stream is based on .xfdf file
     *
     * @param xfdfInputStream The input stream containing xml-styled xfdf data.
     * @return XfdfObject containing original xfdf data.
     * @throws ParserConfigurationException if a XfdfObject cannot be created which satisfies the configuration
     *                                      requested.
     * @throws SAXException                 if any parse errors occurs.
     */
     public XfdfObject createXfdfObject(InputStream xfdfInputStream) throws ParserConfigurationException, SAXException {
        XfdfObject xfdfObject = new XfdfObject();

        Document document = XfdfFileUtils.createXfdfDocumentFromStream(xfdfInputStream);

        Element root = document.getDocumentElement();
        List<AttributeObject> xfdfRootAttributes = readXfdfRootAttributes(root);
        xfdfObject.setAttributes(xfdfRootAttributes);

        NodeList nodeList = root.getChildNodes();

        visitChildNodes(nodeList, xfdfObject);

        return xfdfObject;
    }

    private void visitFNode(Node node, XfdfObject xfdfObject) {
        if (node.getAttributes() != null) {
            Node href = node.getAttributes().getNamedItem(XfdfConstants.HREF);
            if (href != null) {
                xfdfObject.setF(new FObject(href.getNodeValue()));
            } else {
                logger.info(XfdfConstants.EMPTY_F_LEMENT);
            }
        }
    }

    private void visitIdsNode(Node node, XfdfObject xfdfObject) {
        IdsObject idsObject = new IdsObject();
        if (node.getAttributes() != null) {
            Node original = node.getAttributes().getNamedItem(XfdfConstants.ORIGINAL);
            if (original != null) {
                idsObject.setOriginal(original.getNodeValue());
            }
            Node modified = node.getAttributes().getNamedItem(XfdfConstants.MODIFIED);
            if (modified != null) {
                idsObject.setModified(modified.getNodeValue());
            }
            xfdfObject.setIds(idsObject);
        } else {
            logger.info(XfdfConstants.EMPTY_IDS_ELEMENT);
        }
    }

    private void visitElementNode(Node node, XfdfObject xfdfObject) {
        if (XfdfConstants.FIELDS.equalsIgnoreCase(node.getNodeName())) {
            FieldsObject fieldsObject = new FieldsObject();
            readFieldList(node, fieldsObject);
            xfdfObject.setFields(fieldsObject);
        }
        if (XfdfConstants.F.equalsIgnoreCase(node.getNodeName())) {
            visitFNode(node, xfdfObject);
        }
        if (XfdfConstants.IDS.equalsIgnoreCase(node.getNodeName())) {
            visitIdsNode(node, xfdfObject);
        }
        if (XfdfConstants.ANNOTS.equalsIgnoreCase(node.getNodeName())) {
            AnnotsObject annotsObject = new AnnotsObject();
            readAnnotsList(node, annotsObject);
            xfdfObject.setAnnots(annotsObject);
        }
    }

    private void visitChildNodes(NodeList nList, XfdfObject xfdfObject) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                visitElementNode(node, xfdfObject);
            }
        }
    }

    private static boolean isAnnotSupported(String nodeName) {
       return XfdfConstants.TEXT.equalsIgnoreCase(nodeName) ||
               XfdfConstants.HIGHLIGHT.equalsIgnoreCase(nodeName) ||
               XfdfConstants.UNDERLINE.equalsIgnoreCase(nodeName) ||
               XfdfConstants.STRIKEOUT.equalsIgnoreCase(nodeName) ||
               XfdfConstants.SQUIGGLY.equalsIgnoreCase(nodeName) ||
               XfdfConstants.CIRCLE.equalsIgnoreCase(nodeName) ||
               XfdfConstants.SQUARE.equalsIgnoreCase(nodeName) ||
               XfdfConstants.POLYLINE.equalsIgnoreCase(nodeName) ||
               XfdfConstants.POLYGON.equalsIgnoreCase(nodeName) ||
               XfdfConstants.STAMP.equalsIgnoreCase(nodeName) ||
//               XfdfConstants.FREETEXT.equalsIgnoreCase(nodeName) ||
               XfdfConstants.LINE.equalsIgnoreCase(nodeName);
    }

    private void readAnnotsList(Node node, AnnotsObject annotsObject) {
        NodeList annotsNodeList = node.getChildNodes();

        for (int temp = 0; temp < annotsNodeList.getLength(); temp++) {
            Node currentNode = annotsNodeList.item(temp);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE &&
                    isAnnotationSubtype(currentNode.getNodeName()) &&
                    isAnnotSupported(currentNode.getNodeName())) {
                        visitAnnotationNode(currentNode, annotsObject);
            }
        }
    }

    private void visitAnnotationNode(Node currentNode, AnnotsObject annotsObject) {
        AnnotObject annotObject = new AnnotObject();
        annotObject.setName(currentNode.getNodeName());
        if (currentNode.getAttributes() != null) {
            NamedNodeMap attributes = currentNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                addAnnotObjectAttribute(annotObject, attributes.item(i));
            }
            visitAnnotationInnerNodes(annotObject, currentNode, annotsObject);
            annotsObject.addAnnot(annotObject);
        }
    }

    private void visitAnnotationInnerNodes(AnnotObject annotObject, Node annotNode, AnnotsObject annotsObject) {
        NodeList children = annotNode.getChildNodes();

        for (int temp = 0; temp < children.getLength(); temp++) {
            Node node = children.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (XfdfConstants.CONTENTS.equalsIgnoreCase(node.getNodeName())) {
                    visitContentsSubelement(node, annotObject);
                }
                if (XfdfConstants.CONTENTS_RICHTEXT.equalsIgnoreCase(node.getNodeName())) {
                    visitContentsRichTextSubelement(node, annotObject);
                }
                if (XfdfConstants.POPUP.equalsIgnoreCase(node.getNodeName())) {
                    visitPopupSubelement(node, annotObject);
                }
                if (XfdfConstants.VERTICES.equalsIgnoreCase(node.getNodeName())) {
                    visitVerticesSubelement(node, annotObject);
                }
                if (isAnnotationSubtype(node.getNodeName()) &&
                        isAnnotSupported(node.getNodeName())) {
                    visitAnnotationNode(node, annotsObject);
                }
            }
        }
    }

    private void visitPopupSubelement(Node popupNode, AnnotObject annotObject) {
        //nothing inside
        //attr list : color, date, flags, name, rect (required), title. open
        AnnotObject popupAnnotObject = new AnnotObject();
        NamedNodeMap attributes = popupNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            addAnnotObjectAttribute(popupAnnotObject, attributes.item(i));
        }
        annotObject.setPopup(popupAnnotObject);
    }

    private void visitContentsSubelement(Node parentNode, AnnotObject annotObject) {
        //no attributes. inside a text string
        NodeList children = parentNode.getChildNodes();
        for (int temp = 0; temp < children.getLength(); temp++) {
            Node node = children.item(temp);
            if (node.getNodeType() == Node.TEXT_NODE) {
                annotObject.setContents(new PdfString(node.getNodeValue()));
            }
        }
    }

    private void visitContentsRichTextSubelement(Node parentNode, AnnotObject annotObject) {
        // no attributes, inside a text string or rich text string
        NodeList children = parentNode.getChildNodes();
        for (int temp = 0; temp < children.getLength(); temp++) {
            Node node = children.item(temp);
            if (node.getNodeType() == Node.TEXT_NODE) {
                annotObject.setContentsRichText(new PdfString(node.getNodeValue()));
            }
        }
    }

    private void visitVerticesSubelement(Node parentNode, AnnotObject annotObject) {
        //no attributes, inside a text string
        NodeList children = parentNode.getChildNodes();
        for (int temp = 0; temp < children.getLength(); temp++) {
            Node node = children.item(temp);
            if (node.getNodeType() == Node.TEXT_NODE) {
                annotObject.setVertices(node.getNodeValue());
            }
        }
    }

    private void addAnnotObjectAttribute(AnnotObject annotObject, Node attributeNode) {
        if (attributeNode != null) {
            String attributeName = attributeNode.getNodeName();
            switch (attributeName) {
                case XfdfConstants.PAGE:
                    //required
                    annotObject.addFdfAttributes(Integer.parseInt(attributeNode.getNodeValue()));
                    break;
                case XfdfConstants.COLOR:
                case XfdfConstants.DATE:
                case XfdfConstants.FLAGS:
                case XfdfConstants.NAME:
                case XfdfConstants.RECT://required
                case XfdfConstants.TITLE:

                case XfdfConstants.CREATION_DATE:
                case XfdfConstants.OPACITY:
                case XfdfConstants.SUBJECT:

                case XfdfConstants.ICON:
                case XfdfConstants.STATE:
                case XfdfConstants.STATE_MODEL:
                case XfdfConstants.IN_REPLY_TO:
                case XfdfConstants.REPLY_TYPE:
                case XfdfConstants.OPEN:
                case XfdfConstants.COORDS:
                case XfdfConstants.INTENT:
                case XfdfConstants.INTERIOR_COLOR:
                case XfdfConstants.HEAD:
                case XfdfConstants.TAIL:
                case XfdfConstants.FRINGE:
                case XfdfConstants.ROTATION:
                case XfdfConstants.JUSTIFICATION:

                case XfdfConstants.WIDTH:
                case XfdfConstants.DASHES:
                case XfdfConstants.STYLE:
                case XfdfConstants.INTENSITY:
                    annotObject.addAttribute(new AttributeObject(attributeName, attributeNode.getNodeValue()));
                    break;
                default: logger.warn(IoLogMessageConstant.XFDF_UNSUPPORTED_ANNOTATION_ATTRIBUTE);
                    break;
            }
        }
    }

    private boolean isAnnotationSubtype(String tag) {
        return XfdfConstants.TEXT.equalsIgnoreCase(tag) ||
                XfdfConstants.HIGHLIGHT.equalsIgnoreCase(tag) ||
                XfdfConstants.UNDERLINE.equalsIgnoreCase(tag) ||
                XfdfConstants.STRIKEOUT.equalsIgnoreCase(tag) ||
                XfdfConstants.SQUIGGLY.equalsIgnoreCase(tag) ||
                XfdfConstants.LINE.equalsIgnoreCase(tag) ||
                XfdfConstants.CIRCLE.equalsIgnoreCase(tag) ||
                XfdfConstants.SQUARE.equalsIgnoreCase(tag) ||
                XfdfConstants.CARET.equalsIgnoreCase(tag) ||
                XfdfConstants.POLYGON.equalsIgnoreCase(tag) ||
                XfdfConstants.POLYLINE.equalsIgnoreCase(tag) ||
                XfdfConstants.STAMP.equalsIgnoreCase(tag) ||
                XfdfConstants.INK.equalsIgnoreCase(tag) ||
                XfdfConstants.FREETEXT.equalsIgnoreCase(tag) ||
                XfdfConstants.FILEATTACHMENT.equalsIgnoreCase(tag) ||
                XfdfConstants.SOUND.equalsIgnoreCase(tag) ||
                XfdfConstants.LINK.equalsIgnoreCase(tag) ||
                XfdfConstants.REDACT.equalsIgnoreCase(tag) ||
                XfdfConstants.PROJECTION.equalsIgnoreCase(tag);
        //projection annotation is not supported in iText
    }

    private void readFieldList(Node node, FieldsObject fieldsObject) {
        NodeList fieldNodeList = node.getChildNodes();

        for (int temp = 0; temp < fieldNodeList.getLength(); temp++) {
            Node currentNode = fieldNodeList.item(temp);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && XfdfConstants.FIELD.equalsIgnoreCase(currentNode.getNodeName())) {
                FieldObject fieldObject = new FieldObject();
                visitInnerFields(fieldObject, currentNode, fieldsObject);
            }
        }
    }

    private void visitFieldElementNode(Node node, FieldObject parentField, FieldsObject fieldsObject) {
        if (XfdfConstants.VALUE.equalsIgnoreCase(node.getNodeName())) {
            Node valueTextNode = node.getFirstChild();
            if (valueTextNode != null) {
                parentField.setValue(valueTextNode.getTextContent());
            } else {
                logger.info(XfdfConstants.EMPTY_FIELD_VALUE_ELEMENT);
            }
            return;
        }
        if (XfdfConstants.FIELD.equalsIgnoreCase(node.getNodeName())) {
            FieldObject childField = new FieldObject();
            childField.setParent(parentField);
            childField.setName(parentField.getName() + "." + node.getAttributes().item(0).getNodeValue());
            if (node.getChildNodes() != null) {
                visitInnerFields(childField, node, fieldsObject);
            }
            fieldsObject.addField(childField);
        }
    }

    private void visitInnerFields(FieldObject parentField, Node parentNode, FieldsObject fieldsObject) {
        if (parentNode.getAttributes().getLength() != 0) {
            if (parentField.getName() == null) {
                parentField.setName(parentNode.getAttributes().item(0).getNodeValue());
            }
        } else {
            logger.info(XfdfConstants.EMPTY_FIELD_NAME_ELEMENT);
        }

        NodeList children = parentNode.getChildNodes();

        for (int temp = 0; temp < children.getLength(); temp++) {
            Node node = children.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                visitFieldElementNode(node, parentField, fieldsObject);
            }
        }
        fieldsObject.addField(parentField);
    }


    private List<AttributeObject> readXfdfRootAttributes(Element root) {
        NamedNodeMap attributes = root.getAttributes();
        int length = attributes.getLength();
        List<AttributeObject> attributeObjects = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Node attributeNode = attributes.item(i);
            attributeObjects.add(new AttributeObject(attributeNode.getNodeName(), attributeNode.getNodeValue()));
        }
        return attributeObjects;
    }

    private static void addPopup(PdfAnnotation pdfAnnot, AnnotsObject annots, int pageNumber) {
        if(((PdfPopupAnnotation)pdfAnnot).getParentObject() != null) {
            PdfAnnotation parentAnnotation = ((PdfPopupAnnotation)pdfAnnot).getParent();
            PdfIndirectReference parentRef = parentAnnotation.getPdfObject().getIndirectReference();
            boolean hasParentAnnot = false;
            for(AnnotObject annot: annots.getAnnotsList()) {
                if(parentRef.equals(annot.getRef())) {
                    hasParentAnnot = true;
                    annot.setHasPopup(true);
                    annot.setPopup(createXfdfAnnotation(pdfAnnot, pageNumber));
                }
            }
            if(!hasParentAnnot) {
                AnnotObject parentAnnot = new AnnotObject();
                parentAnnot.setRef(parentRef);
                parentAnnot.addFdfAttributes(pageNumber);
                parentAnnot.setHasPopup(true);
                parentAnnot.setPopup(createXfdfAnnotation(pdfAnnot, pageNumber));
                annots.addAnnot(parentAnnot);
            }
        } else {
            annots.addAnnot(createXfdfAnnotation(pdfAnnot, pageNumber));
        }
    }

    private static void addAnnotation(PdfAnnotation pdfAnnot, AnnotsObject annots, int pageNumber) {
        boolean hasCorrecpondingAnnotObject = false;
        for(AnnotObject annot : annots.getAnnotsList()) {
            if(pdfAnnot.getPdfObject().getIndirectReference().equals(annot.getRef())) {
                hasCorrecpondingAnnotObject = true;
                updateXfdfAnnotation(annot, pdfAnnot, pageNumber);
            }
        }
        if(!hasCorrecpondingAnnotObject) {
            annots.addAnnot(createXfdfAnnotation(pdfAnnot, pageNumber));
        }
    }

    private static void addAnnotations(PdfDocument pdfDoc, XfdfObject resultXfdf) {
        AnnotsObject annots = new AnnotsObject();
        int pageNumber = pdfDoc.getNumberOfPages();
        for (int i = 1; i <= pageNumber; i++) {
            PdfPage page = pdfDoc.getPage(i);
            List<PdfAnnotation> pdfAnnots = page.getAnnotations();
            for (PdfAnnotation pdfAnnot : pdfAnnots) {
                if(pdfAnnot.getSubtype() == PdfName.Popup) {
                    addPopup(pdfAnnot, annots, i);
                } else {
                   addAnnotation(pdfAnnot, annots, i);
                }
            }
        }
        resultXfdf.setAnnots(annots);
    }

    private static void updateXfdfAnnotation(AnnotObject annotObject, PdfAnnotation pdfAnnotation, int pageNumber) {
        //TODO DEVSIX-4132 implement update, refactor createXfdfAnnotation() method to accommodate the change
    }

    private static void addCommonAnnotationAttributes(AnnotObject annot, PdfAnnotation pdfAnnotation) {
        annot.setName(pdfAnnotation.getSubtype().getValue().toLowerCase());

        if (pdfAnnotation.getColorObject() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.COLOR, XfdfObjectUtils.convertColorToString(pdfAnnotation.getColorObject().toFloatArray())));
        }
        annot.addAttribute(XfdfConstants.DATE, pdfAnnotation.getDate());
        String flagsString = XfdfObjectUtils.convertFlagsToString(pdfAnnotation);
        if(flagsString != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.FLAGS, flagsString));
        }

        annot.addAttribute(XfdfConstants.NAME, pdfAnnotation.getName());
        //rect attribute is required, however iText can't create an annotation without rect anyway
        annot.addAttribute(XfdfConstants.RECT, pdfAnnotation.getRectangle().toRectangle());
        annot.addAttribute(XfdfConstants.TITLE, pdfAnnotation.getTitle());
    }

    private static void addMarkupAnnotationAttributes(AnnotObject annot, PdfMarkupAnnotation pdfMarkupAnnotation) {
        annot.addAttribute(XfdfConstants.CREATION_DATE, pdfMarkupAnnotation.getCreationDate());
        annot.addAttribute(XfdfConstants.OPACITY, pdfMarkupAnnotation.getOpacity());
        annot.addAttribute(XfdfConstants.SUBJECT, pdfMarkupAnnotation.getSubject());
    }

    private static void addBorderStyleAttributes(AnnotObject annotObject, PdfNumber width,
            PdfArray dashes, PdfName style) {
        annotObject.addAttribute(XfdfConstants.WIDTH, width);
        annotObject.addAttribute(XfdfConstants.DASHES, XfdfObjectUtils.convertDashesFromArray(dashes));
        annotObject.addAttribute(XfdfConstants.STYLE, XfdfObjectUtils.getStyleFullValue(style));
    }

    private static void createTextMarkupAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfTextMarkupAnnotation pdfTextMarkupAnnotation = (PdfTextMarkupAnnotation) pdfAnnotation;

        if (pdfTextMarkupAnnotation.getQuadPoints() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.COORDS, XfdfObjectUtils
                    .convertQuadPointsToCoordsString(pdfTextMarkupAnnotation.getQuadPoints().toFloatArray())));
        }

        if (PdfTextMarkupAnnotation.MarkupUnderline.equals(pdfTextMarkupAnnotation.getSubtype()) &&
                pdfTextMarkupAnnotation.getIntent() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTENT,
                    pdfTextMarkupAnnotation.getIntent().getValue()));
        }

        if (pdfTextMarkupAnnotation.getContents() != null) {
            annot.setContents(pdfTextMarkupAnnotation.getContents());
        }
        if (pdfTextMarkupAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfTextMarkupAnnotation.getPopup(), pageNumber));
        }
    }

    private static void createTextAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfTextAnnotation pdfTextAnnotation = ((PdfTextAnnotation) pdfAnnotation);

        annot.addAttribute(XfdfConstants.ICON, pdfTextAnnotation.getIconName());
        annot.addAttribute(XfdfConstants.STATE, pdfTextAnnotation.getState());
        annot.addAttribute(XfdfConstants.STATE_MODEL, pdfTextAnnotation.getStateModel());

        if (pdfTextAnnotation.getReplyType() != null) {
            //inreplyTo is required if replyType is present
            annot.addAttribute(new AttributeObject(XfdfConstants.IN_REPLY_TO, pdfTextAnnotation.getInReplyTo().getName().getValue()));
            annot.addAttribute(new AttributeObject(XfdfConstants.REPLY_TYPE, pdfTextAnnotation.getReplyType().getValue()));
        }

        if (pdfTextAnnotation.getContents() != null) {
            annot.setContents(pdfTextAnnotation.getContents());
        }
        if (pdfTextAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfTextAnnotation.getPopup(), pageNumber));
        }
    }

    private static void createCircleAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfCircleAnnotation pdfCircleAnnotation = (PdfCircleAnnotation) pdfAnnotation;

        PdfDictionary bs = pdfCircleAnnotation.getBorderStyle();
        if (bs != null) {
            addBorderStyleAttributes(annot, bs.getAsNumber(PdfName.W),
                    bs.getAsArray(PdfName.D), bs.getAsName(PdfName.S));
        }

        if (pdfCircleAnnotation.getBorderEffect() != null) {
            annot.addAttribute(XfdfConstants.INTENSITY, pdfCircleAnnotation.getBorderEffect().getAsNumber(PdfName.I));
            if (annot.getAttribute(XfdfConstants.STYLE) == null) {
                annot.addAttribute(XfdfConstants.STYLE, XfdfObjectUtils.getStyleFullValue(
                        pdfCircleAnnotation.getBorderEffect().getAsName(PdfName.S)));
            }
        }

        if (pdfCircleAnnotation.getInteriorColor() != null && pdfCircleAnnotation.getInteriorColor().getColorValue() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTERIOR_COLOR, XfdfObjectUtils.convertColorToString(pdfCircleAnnotation.getInteriorColor().getColorValue())));
        }

        if(pdfCircleAnnotation.getRectangleDifferences() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.FRINGE, XfdfObjectUtils.convertFringeToString(
                    pdfCircleAnnotation.getRectangleDifferences().toFloatArray())));
        }

        annot.setContents(pdfAnnotation.getContents());
        if (pdfCircleAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfCircleAnnotation.getPopup(), pageNumber));
        }
    }

    private static void createSquareAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfSquareAnnotation pdfSquareAnnotation = (PdfSquareAnnotation) pdfAnnotation;

        PdfDictionary bs = pdfSquareAnnotation.getBorderStyle();
        if (bs != null) {
            addBorderStyleAttributes(annot, bs.getAsNumber(PdfName.W),
                    bs.getAsArray(PdfName.D), bs.getAsName(PdfName.S));
        }

        if (pdfSquareAnnotation.getBorderEffect() != null) {
            annot.addAttribute(XfdfConstants.INTENSITY, pdfSquareAnnotation.getBorderEffect().getAsNumber(PdfName.I));
            if (annot.getAttribute(XfdfConstants.STYLE) == null) {
                annot.addAttribute(XfdfConstants.STYLE, XfdfObjectUtils.getStyleFullValue(
                        pdfSquareAnnotation.getBorderEffect().getAsName(PdfName.S)));
            }
        }

        if (pdfSquareAnnotation.getInteriorColor() != null && pdfSquareAnnotation.getInteriorColor().getColorValue() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTERIOR_COLOR, XfdfObjectUtils.convertColorToString(pdfSquareAnnotation.getInteriorColor().getColorValue())));
        }
        if(pdfSquareAnnotation.getRectangleDifferences() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.FRINGE, XfdfObjectUtils.convertFringeToString(
                    pdfSquareAnnotation.getRectangleDifferences().toFloatArray())));
        }

        annot.setContents(pdfAnnotation.getContents());
        if (pdfSquareAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfSquareAnnotation.getPopup(), pageNumber));
        }
    }

    private static void createStampAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfStampAnnotation pdfStampAnnotation = (PdfStampAnnotation) pdfAnnotation;

        annot.addAttribute(XfdfConstants.ICON, pdfStampAnnotation.getIconName());
        if (pdfStampAnnotation.getRotation() != null) {
            annot.addAttribute(XfdfConstants.ROTATION, pdfStampAnnotation.getRotation().intValue());
        }

        if (pdfStampAnnotation.getContents() != null) {
            annot.setContents(pdfStampAnnotation.getContents());
        }
        if (pdfStampAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfStampAnnotation.getPopup(), pageNumber));
        }
        if (pdfStampAnnotation.getAppearanceDictionary() != null) {
            if (pdfAnnotation.getAppearanceObject(PdfName.N) != null) {
                annot.setAppearance(pdfStampAnnotation.getAppearanceDictionary().get(PdfName.N).toString());
            } else if (pdfAnnotation.getAppearanceObject(PdfName.R) != null) {
                annot.setAppearance(pdfStampAnnotation.getAppearanceDictionary().get(PdfName.R).toString());
            } else if (pdfAnnotation.getAppearanceObject(PdfName.D) != null) {
                annot.setAppearance(pdfStampAnnotation.getAppearanceDictionary().get(PdfName.D).toString());
            }
        }
    }

    private static void createFreeTextAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot) {
        PdfFreeTextAnnotation pdfFreeTextAnnotation = (PdfFreeTextAnnotation) pdfAnnotation;

        PdfDictionary bs = pdfFreeTextAnnotation.getBorderStyle();
        if (bs != null) {
            addBorderStyleAttributes(annot, bs.getAsNumber(PdfName.W),
                    bs.getAsArray(PdfName.D), bs.getAsName(PdfName.S));
        }

        if (pdfFreeTextAnnotation.getRotation() != null) {
            annot.addAttribute(XfdfConstants.ROTATION, pdfFreeTextAnnotation.getRotation().intValue());
        }
        annot.addAttribute(new AttributeObject(XfdfConstants.JUSTIFICATION,
                XfdfObjectUtils.convertJustificationFromIntegerToString((pdfFreeTextAnnotation.getJustification()))));
        if (pdfFreeTextAnnotation.getIntent() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTENT, pdfFreeTextAnnotation.getIntent().getValue()));
        }

        if (pdfFreeTextAnnotation.getContents() != null) {
            annot.setContents(pdfFreeTextAnnotation.getContents());
        }
        //TODO DEVSIX-3215 add contents-richtext
        if (pdfFreeTextAnnotation.getDefaultAppearance() != null) {
            annot.setDefaultAppearance(pdfFreeTextAnnotation.getDefaultAppearance().getValue());
        }
        if (pdfFreeTextAnnotation.getDefaultStyleString() != null) {
            annot.setDefaultStyle(pdfFreeTextAnnotation.getDefaultStyleString().getValue());
        }
    }

    private static void createLineAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfLineAnnotation pdfLineAnnotation = (PdfLineAnnotation) pdfAnnotation;

        PdfArray line = pdfLineAnnotation.getLine();
        if (line != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.START,
                    XfdfObjectUtils.convertLineStartToString(line.toFloatArray())));
            annot.addAttribute(new AttributeObject(XfdfConstants.END,
                    XfdfObjectUtils.convertLineEndToString(line.toFloatArray())));
        }
        if (pdfLineAnnotation.getLineEndingStyles() != null) {
            if (pdfLineAnnotation.getLineEndingStyles().get(0) != null) {
                annot.addAttribute(new AttributeObject(XfdfConstants.HEAD,
                        pdfLineAnnotation.getLineEndingStyles().get(0).toString().substring(1)));
            }
            if (pdfLineAnnotation.getLineEndingStyles().get(1) != null) {
                annot.addAttribute(new AttributeObject(XfdfConstants.TAIL,
                        pdfLineAnnotation.getLineEndingStyles().get(1).toString().substring(1)));
            }

        }
        if (pdfLineAnnotation.getInteriorColor() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTERIOR_COLOR, XfdfObjectUtils.convertColorToString(pdfLineAnnotation.getInteriorColor())));
        }
        annot.addAttribute(XfdfConstants.LEADER_EXTENDED, pdfLineAnnotation.getLeaderLineExtension());
        annot.addAttribute(XfdfConstants.LEADER_LENGTH, pdfLineAnnotation.getLeaderLineLength());
        annot.addAttribute(XfdfConstants.CAPTION, pdfLineAnnotation.getContentsAsCaption());
        annot.addAttribute(XfdfConstants.INTENT, pdfLineAnnotation.getIntent());
        annot.addAttribute(XfdfConstants.LEADER_OFFSET, pdfLineAnnotation.getLeaderLineOffset());
        annot.addAttribute(XfdfConstants.CAPTION_STYLE, pdfLineAnnotation.getCaptionPosition());
        if (pdfLineAnnotation.getCaptionOffset() != null) {
            annot.addAttribute(XfdfConstants.CAPTION_OFFSET_H, pdfLineAnnotation.getCaptionOffset().get(0));
            annot.addAttribute(XfdfConstants.CAPTION_OFFSET_V, pdfLineAnnotation.getCaptionOffset().get(1));
        } else {
            annot.addAttribute(new AttributeObject(XfdfConstants.CAPTION_OFFSET_H, "0"));
            annot.addAttribute(new AttributeObject(XfdfConstants.CAPTION_OFFSET_V, "0"));
        }

        PdfDictionary bs = pdfLineAnnotation.getBorderStyle();
        if (bs != null) {
            addBorderStyleAttributes(annot, bs.getAsNumber(PdfName.W),
                    bs.getAsArray(PdfName.D), bs.getAsName(PdfName.S));
        }

        annot.setContents(pdfAnnotation.getContents());
        if (pdfLineAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfLineAnnotation.getPopup(), pageNumber));
        }
    }

    private static void createLinkAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot) {
        PdfLinkAnnotation pdfLinkAnnotation = (PdfLinkAnnotation) pdfAnnotation;

        if (pdfLinkAnnotation.getBorderStyle() != null) {
            annot.addAttribute(XfdfConstants.STYLE, pdfLinkAnnotation.getBorderStyle().getAsString(PdfName.S));
        }
        if (pdfLinkAnnotation.getHighlightMode() != null) {
            annot.addAttribute(XfdfConstants.HIGHLIGHT,
                    XfdfObjectUtils.getHighlightFullValue(pdfLinkAnnotation.getHighlightMode()));
        }
        if (pdfLinkAnnotation.getQuadPoints() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.COORDS,
                    XfdfObjectUtils.convertQuadPointsToCoordsString(pdfLinkAnnotation.getQuadPoints().toFloatArray())));
        }

        if (pdfLinkAnnotation.getContents() != null) {
            annot.setContents(pdfLinkAnnotation.getContents());
        }

        //in iText pdfLinkAnnotation doesn't have a popup sub-element

        PdfDictionary action = pdfLinkAnnotation.getAction();
        if(pdfLinkAnnotation.getAction() != null) {
            PdfName type = action.getAsName(PdfName.S);
            ActionObject actionObject = new ActionObject(type);

            if(PdfName.URI.equals(type)) {
                actionObject.setUri(action.getAsString(PdfName.URI));
                if(action.get(PdfName.IsMap) != null) {
                    actionObject.setMap((boolean) action.getAsBool(PdfName.IsMap));
                }
            }

            annot.setAction(actionObject);
        }
        PdfArray dest =  (PdfArray) pdfLinkAnnotation.getDestinationObject();
        if (dest != null) {
            createDestElement(dest, annot);
        }

        PdfArray border = pdfLinkAnnotation.getBorder();
        if(border != null) {
            BorderStyleAltObject borderStyleAltObject = new BorderStyleAltObject(border.getAsNumber(0).floatValue(),
                    border.getAsNumber(1).floatValue(),border.getAsNumber(2).floatValue());
            annot.setBorderStyleAlt(borderStyleAltObject);
        }
    }

    private static void createDestElement(PdfArray dest, AnnotObject annot) {
        DestObject destObject = new DestObject();
        PdfName type = dest.getAsName(1);
        if(PdfName.XYZ.equals(type)) {
            FitObject xyz = new FitObject(dest.get(0));
            xyz.setLeft(dest.getAsNumber(2).floatValue())
                    .setTop(dest.getAsNumber(3).floatValue())
                    .setZoom(dest.getAsNumber(4).floatValue());
            destObject.setXyz(xyz);
        } else if(PdfName.Fit.equals(type)) {
            FitObject fit = new FitObject(dest.get(0));
            destObject.setFit(fit);
        } else if(PdfName.FitB.equals(type)) {
            FitObject fitB = new FitObject(dest.get(0));
            destObject.setFitB(fitB);
        } else if(PdfName.FitR.equals(type)) {
            FitObject fitR = new FitObject(dest.get(0));
            fitR.setLeft(dest.getAsNumber(2).floatValue());
            fitR.setBottom(dest.getAsNumber(3).floatValue());
            fitR.setRight(dest.getAsNumber(4).floatValue());
            fitR.setTop(dest.getAsNumber(5).floatValue());
            destObject.setFitR(fitR);
        } else if(PdfName.FitH.equals(type)) {
            FitObject fitH = new FitObject(dest.get(0));
            fitH.setTop(dest.getAsNumber(2).floatValue());
            destObject.setFitH(fitH);
        } else if(PdfName.FitBH.equals(type)) {
            FitObject fitBH = new FitObject(dest.get(0));
            fitBH.setTop(dest.getAsNumber(2).floatValue());
            destObject.setFitBH(fitBH);
        } else if(PdfName.FitBV.equals(type)) {
            FitObject fitBV = new FitObject(dest.get(0));
            fitBV.setLeft(dest.getAsNumber(2).floatValue());
            destObject.setFitBV(fitBV);
        } else if(PdfName.FitV.equals(type)) {
            FitObject fitV = new FitObject(dest.get(0));
            fitV.setLeft(dest.getAsNumber(2).floatValue());
            destObject.setFitV(fitV);
        }
        annot.setDestination(destObject);
    }

    private static void createPolyGeomAnnotation(PdfAnnotation pdfAnnotation, AnnotObject annot, int pageNumber) {
        PdfPolyGeomAnnotation pdfPolyGeomAnnotation = (PdfPolyGeomAnnotation) pdfAnnotation;

        PdfDictionary bs = pdfPolyGeomAnnotation.getBorderStyle();
        if (bs != null) {
            addBorderStyleAttributes(annot, bs.getAsNumber(PdfName.W),
                    bs.getAsArray(PdfName.D), bs.getAsName(PdfName.S));
        }

        if (pdfPolyGeomAnnotation.getBorderEffect() != null) {
            annot.addAttribute(XfdfConstants.INTENSITY, pdfPolyGeomAnnotation.getBorderEffect().getAsNumber(PdfName.I));
            if (annot.getAttribute(XfdfConstants.STYLE) == null) {
                annot.addAttribute(XfdfConstants.STYLE, XfdfObjectUtils.getStyleFullValue(
                        pdfPolyGeomAnnotation.getBorderEffect().getAsName(PdfName.S)));
            }
        }

        if (pdfPolyGeomAnnotation.getInteriorColor() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTERIOR_COLOR,
                    XfdfObjectUtils.convertColorToString(pdfPolyGeomAnnotation.getInteriorColor())));
        }
        if (pdfPolyGeomAnnotation.getIntent() != null) {
            annot.addAttribute(new AttributeObject(XfdfConstants.INTENT, pdfPolyGeomAnnotation.getIntent().getValue()));
        }

        //Head and tail for polyline only
        if (pdfPolyGeomAnnotation.getLineEndingStyles() != null) {
            if (pdfPolyGeomAnnotation.getLineEndingStyles().get(0) != null) {
                annot.addAttribute(new AttributeObject(XfdfConstants.HEAD,
                        pdfPolyGeomAnnotation.getLineEndingStyles().get(0).toString().substring(1)));
            }
            if (pdfPolyGeomAnnotation.getLineEndingStyles().get(1) != null) {
                annot.addAttribute(new AttributeObject(XfdfConstants.TAIL,
                        pdfPolyGeomAnnotation.getLineEndingStyles().get(1).toString().substring(1)));
            }

        }

        //in xfdfd: no attributes, inside text string
        annot.setVertices(XfdfObjectUtils.convertVerticesToString(pdfPolyGeomAnnotation.getVertices().toFloatArray()));

        annot.setContents(pdfAnnotation.getContents());
        if (pdfPolyGeomAnnotation.getPopup() != null) {
            annot.setPopup(convertPdfPopupToAnnotObject(pdfPolyGeomAnnotation.getPopup(), pageNumber));
        }
    }

    private static AnnotObject createXfdfAnnotation(PdfAnnotation pdfAnnotation, int pageNumber) {
        AnnotObject annot = new AnnotObject();
        annot.setRef(pdfAnnotation.getPdfObject().getIndirectReference());
        annot.addFdfAttributes(pageNumber);

        if (pdfAnnotation instanceof PdfTextMarkupAnnotation) {
            createTextMarkupAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfTextAnnotation) {
            createTextAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfPopupAnnotation) {
            annot = convertPdfPopupToAnnotObject((PdfPopupAnnotation) pdfAnnotation, pageNumber);
        }
        if (pdfAnnotation instanceof PdfCircleAnnotation) {
            createCircleAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfSquareAnnotation) {
           createSquareAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfStampAnnotation) {
           createStampAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfFreeTextAnnotation) {
           createFreeTextAnnotation(pdfAnnotation, annot);
        }
        if (pdfAnnotation instanceof PdfLineAnnotation) {
            createLineAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfPolyGeomAnnotation) {
            createPolyGeomAnnotation(pdfAnnotation, annot, pageNumber);
        }
        if (pdfAnnotation instanceof PdfLinkAnnotation) {
            createLinkAnnotation(pdfAnnotation, annot);
        }

        if (isSupportedAnnotation(pdfAnnotation)){
            addCommonAnnotationAttributes(annot, pdfAnnotation);
            if (pdfAnnotation instanceof PdfMarkupAnnotation) {
                addMarkupAnnotationAttributes(annot, (PdfMarkupAnnotation) pdfAnnotation);
            }
        }

        return annot;
    }

    private static AnnotObject convertPdfPopupToAnnotObject(PdfPopupAnnotation pdfPopupAnnotation, int pageNumber) {
        AnnotObject annot = new AnnotObject();
        annot.addFdfAttributes(pageNumber);
        annot.setName(XfdfConstants.POPUP);
        annot.setRef(pdfPopupAnnotation.getPdfObject().getIndirectReference());

        annot.addAttribute(XfdfConstants.OPEN, pdfPopupAnnotation.getOpen());
        return annot;
    }

    private static boolean isSupportedAnnotation(PdfAnnotation pdfAnnotation) {
         return pdfAnnotation instanceof PdfTextMarkupAnnotation ||
                pdfAnnotation instanceof PdfTextAnnotation ||
                pdfAnnotation instanceof PdfCircleAnnotation ||
                pdfAnnotation instanceof PdfSquareAnnotation ||
                pdfAnnotation instanceof PdfStampAnnotation ||
                pdfAnnotation instanceof PdfFreeTextAnnotation ||
                pdfAnnotation instanceof PdfLineAnnotation ||
                pdfAnnotation instanceof PdfPolyGeomAnnotation ||
                pdfAnnotation instanceof PdfLinkAnnotation ||
                pdfAnnotation instanceof PdfPopupAnnotation;
    }
}
