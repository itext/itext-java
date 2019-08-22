/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms.xfdf;

import com.itextpdf.kernel.pdf.PdfName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class XfdfWriter {

    private OutputStream outputStream;

    private static Logger logger = LoggerFactory.getLogger(XfdfWriter.class);

    /**
     * Creates a XfdfWriter for output stream specified.
     * @param outputStream A stream to write xfdf file into.
     */
    XfdfWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * The method writes data from XfdfObject into a xfdf data file.
     *
     */
    void write(XfdfObject xfdfObject) throws TransformerException, ParserConfigurationException {
        this.writeDom(xfdfObject);
    }


    private void writeDom(XfdfObject xfdfObject) throws ParserConfigurationException, TransformerException {

        Document document = XfdfFileUtils.createNewXfdfDocument();

        // root xfdf element
        Element root = document.createElement("xfdf");
        document.appendChild(root);

        //write fields
        if (xfdfObject.getFields() != null && xfdfObject.getFields().getFieldList() != null &&
                !xfdfObject.getFields().getFieldList().isEmpty()) {
            Element fields = document.createElement("fields");
            root.appendChild(fields);
            List<FieldObject> fieldList = xfdfObject.getFields().getFieldList();
            for(FieldObject fieldObject : fieldList) {
                if(fieldObject.getParent() == null) {
                    addField(fieldObject, fields, document, fieldList);
                }
            }
        }

        //write annots
        if (xfdfObject.getAnnots() != null && xfdfObject.getAnnots().getAnnotsList() != null &&
                !xfdfObject.getAnnots().getAnnotsList().isEmpty()) {
            Element annots = document.createElement("annots");
            root.appendChild(annots);

            for(AnnotObject annotObject : xfdfObject.getAnnots().getAnnotsList()) {
                addAnnot(annotObject, annots, document);
            }
        }

        //write f
        if (xfdfObject.getF() != null) {
            Element f = document.createElement("f");
            addFAttributes(xfdfObject.getF(), f);
            root.appendChild(f);
        }

        //write ids
        if (xfdfObject.getIds() != null) {
            Element ids = document.createElement("ids");
            addIdsAttributes(xfdfObject.getIds(), ids);
            root.appendChild(ids);
        }

        // create the xml file
        //transform the DOM Object to an XML File
        XfdfFileUtils.saveXfdfDocumentToFile(document, this.outputStream);
    }

    private static void addIdsAttributes(IdsObject idsObject, Element ids) {
        if (idsObject.getOriginal() != null) {
            ids.setAttribute("original", idsObject.getOriginal());
        }
        if (idsObject.getModified() != null) {
            ids.setAttribute("modified", idsObject.getModified());
        }
    }

    private static void addFAttributes(FObject fObject, Element f) {
        if (fObject.getHref() != null) {
            f.setAttribute("href", fObject.getHref());
        }
    }

    private static List<FieldObject> findChildrenFields(FieldObject field, List<FieldObject> fieldList) {
        List<FieldObject> childrenFields = new ArrayList<>();
        for (FieldObject currentField : fieldList) {
            if (currentField.getParent() != null && currentField.getParent().getName().equalsIgnoreCase(field.getName())) {
                childrenFields.add(currentField);
            }
        }
        return childrenFields;
    }

    private static void addField(FieldObject fieldObject, Element parentElement, Document document, List<FieldObject> fieldList) {
        List<FieldObject> childrenFields = findChildrenFields(fieldObject, fieldList);

        Element field = document.createElement("field");
        field.setAttribute("name", fieldObject.getName());


        if (!childrenFields.isEmpty()) {
            for (FieldObject childField : childrenFields) {
                addField(childField, field, document, fieldList);
            }
        } else {
            if (fieldObject.getValue() != null) {
                Element value = document.createElement("value");
                value.setTextContent(fieldObject.getValue());
                field.appendChild(value);
            } else {
                logger.info(XfdfConstants.EMPTY_FIELD_VALUE_ELEMENT);
            }
        }
        parentElement.appendChild(field);
    }

    private static  void addAnnot(AnnotObject annotObject, Element annots, Document document) {
        if (annotObject.getName() == null) {
            return;
        }
        Element annot = document.createElement(annotObject.getName());

        for(AttributeObject attr : annotObject.getAttributes()) {
            annot.setAttribute(attr.getName(), attr.getValue());
        }

        if (annotObject.getPopup() != null) {
            Element popup = document.createElement("popup");
            addPopup(annotObject.getPopup(), popup,  annot);
        }

        if (annotObject.getContents() != null) {
            Element contents = document.createElement("contents");
            contents.setTextContent(annotObject.getContents().toString());
            annot.appendChild(contents);
        }

        if(annotObject.getAppearance() != null) {
            Element appearance = document.createElement("appearance");
            appearance.setTextContent(annotObject.getAppearance());
            annot.appendChild(appearance);
        }
//
//        if (annotObject.getContentsRichText() != null) {
//            Element contentsRichText = document.createElement("contents-richtext");
//            contentsRichText.setNodeValue(annotObject.getContents().getValue());
//            annot.appendChild(contentsRichText);
//        }

        if (XfdfConstants.LINK.equalsIgnoreCase(annotObject.getName())) {
            if (annotObject.getDestination() != null) {
                addDest(annotObject.getDestination(), annot, document);
            } else if (annotObject.getAction() != null) {
                Element onActivation = document.createElement(XfdfConstants.ON_ACTIVATION);
                addActionObject(annotObject.getAction(), onActivation, document);
                annot.appendChild(onActivation);
            } else {
                logger.error("Dest and OnActivation elements are both missing");
            }

            if (annotObject.getBorderStyleAlt() != null) {
                addBorderStyleAlt(annotObject.getBorderStyleAlt(), annot, document);
            }
        }

        if(XfdfConstants.FREETEXT.equalsIgnoreCase(annotObject.getName())) {
            String defaultAppearanceString = annotObject.getDefaultAppearance();
            if(defaultAppearanceString != null) {
                Element defaultAppearance = document.createElement(XfdfConstants.DEFAULT_APPEARANCE);
                defaultAppearance.setTextContent(defaultAppearanceString);
                annot.appendChild(defaultAppearance);
            }
            String defaultStyleString = annotObject.getDefaultStyle();
            if(defaultStyleString != null) {
                Element defaultStyle = document.createElement(XfdfConstants.DEFAULT_STYLE);
                defaultStyle.setTextContent(defaultStyleString);
                annot.appendChild(defaultStyle);
            }
        }

        annots.appendChild(annot);
    }

    private static void addBorderStyleAlt(BorderStyleAltObject borderStyleAltObject, Element annot, Document document) {
        //BorderStyle alt contains Border style encoded in the format specified in the border style attributes as content
        //has attributes
        Element borderStyleAlt = document.createElement(XfdfConstants.BORDER_STYLE_ALT);

        //required attributes
        borderStyleAlt.setAttribute(XfdfConstants.H_CORNER_RADIUS, XfdfObjectUtils.convertFloatToString(borderStyleAltObject.getHCornerRadius()));
        borderStyleAlt.setAttribute(XfdfConstants.V_CORNER_RADIUS, XfdfObjectUtils.convertFloatToString(borderStyleAltObject.getVCornerRadius()));
        borderStyleAlt.setAttribute(XfdfConstants.WIDTH_CAPITAL, XfdfObjectUtils.convertFloatToString(borderStyleAltObject.getWidth()));

        //optional attribute
        if (borderStyleAltObject.getDashPattern() != null) {
            //TODO add real conversion from PdfArray (PdfName.D in Border dictionary) to String
            borderStyleAlt.setAttribute(XfdfConstants.DASH_PATTERN, Arrays.toString(borderStyleAltObject.getDashPattern()));
        }

        if(borderStyleAltObject.getContent() != null) {
            borderStyleAlt.setTextContent(borderStyleAltObject.getContent());
        }
        annot.appendChild(borderStyleAlt);
    }

    private static void addXYZ(FitObject xyzObject, Element dest, Document document) {
        Element xyz = document.createElement(XfdfConstants.XYZ_CAPITAL);

        //all required
        xyz.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(xyzObject.getPage()));
        xyz.setAttribute(XfdfConstants.LEFT, XfdfObjectUtils.convertFloatToString(xyzObject.getLeft()));
        xyz.setAttribute(XfdfConstants.BOTTOM, XfdfObjectUtils.convertFloatToString(xyzObject.getBottom()));
        xyz.setAttribute(XfdfConstants.RIGHT, XfdfObjectUtils.convertFloatToString(xyzObject.getRight()));
        xyz.setAttribute(XfdfConstants.TOP, XfdfObjectUtils.convertFloatToString(xyzObject.getTop()));

        dest.appendChild(xyz);
    }

    private static void addFit(FitObject fitObject, Element dest, Document document) {
        Element fit = document.createElement(XfdfConstants.FIT);

        //required
        fit.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitObject.getPage()));

        dest.appendChild(fit);
    }

    private static void addFitB(FitObject fitBObject, Element dest, Document document) {
        Element fitB = document.createElement(XfdfConstants.FIT_B);

        //required
        fitB.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitBObject.getPage()));

        dest.appendChild(fitB);
    }

    private static void addFitBH(FitObject fitBHObject, Element dest, Document document) {
        Element fitBH = document.createElement(XfdfConstants.FIT_BH);

        //all required
        fitBH.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitBHObject.getPage()));
        fitBH.setAttribute(XfdfConstants.TOP, XfdfObjectUtils.convertFloatToString(fitBHObject.getTop()));

        dest.appendChild(fitBH);
    }

    private static void addFitBV(FitObject fitBVObject, Element dest, Document document) {
        Element fitBV = document.createElement(XfdfConstants.FIT_BV);

        //all required
        fitBV.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitBVObject.getPage()));
        fitBV.setAttribute(XfdfConstants.LEFT, XfdfObjectUtils.convertFloatToString(fitBVObject.getLeft()));

        dest.appendChild(fitBV);
    }

    private static void addFitH(FitObject fitHObject, Element dest, Document document) {
        Element fitH = document.createElement(XfdfConstants.FIT_H);

        //all required
        fitH.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitHObject.getPage()));
        fitH.setAttribute(XfdfConstants.TOP, XfdfObjectUtils.convertFloatToString(fitHObject.getTop()));

        dest.appendChild(fitH);
    }

    private static void addFitR(FitObject fitRObject, Element dest, Document document) {
        Element fitR = document.createElement(XfdfConstants.FIT_R);

        //all required
        fitR.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitRObject.getPage()));
        fitR.setAttribute(XfdfConstants.LEFT, XfdfObjectUtils.convertFloatToString(fitRObject.getLeft()));
        fitR.setAttribute(XfdfConstants.BOTTOM, XfdfObjectUtils.convertFloatToString(fitRObject.getBottom()));
        fitR.setAttribute(XfdfConstants.RIGHT, XfdfObjectUtils.convertFloatToString(fitRObject.getRight()));
        fitR.setAttribute(XfdfConstants.TOP, XfdfObjectUtils.convertFloatToString(fitRObject.getTop()));

        dest.appendChild(fitR);
    }

    private static void addFitV(FitObject fitVObject, Element dest, Document document) {
        Element fitV = document.createElement(XfdfConstants.FIT_V);

        //all required
        fitV.setAttribute(XfdfConstants.PAGE_CAPITAL, String.valueOf(fitVObject.getPage()));
        fitV.setAttribute(XfdfConstants.LEFT, XfdfObjectUtils.convertFloatToString(fitVObject.getLeft()));

        dest.appendChild(fitV);
    }

    private static void addDest(DestObject destObject, Element annot, Document document) {
        Element dest = document.createElement(XfdfConstants.DEST);

        if (destObject.getName() != null) {
            Element named = document.createElement(XfdfConstants.NAMED);
            named.setAttribute(XfdfConstants.NAME, destObject.getName());
            dest.appendChild(named);
        } else if (destObject.getXyz() != null) {
           addXYZ(destObject.getXyz(), dest, document);
        } else if (destObject.getFit() != null) {
            addFit(destObject.getFit(), dest, document);
        } else if (destObject.getFitB() != null) {
            addFitB(destObject.getFitB(), dest, document);
        } else if (destObject.getFitBH() != null) {
            addFitBH(destObject.getFitBH(), dest, document);
        } else if (destObject.getFitBV() != null) {
            addFitBV(destObject.getFitBV(), dest, document);
        } else if (destObject.getFitH() != null) {
            addFitH(destObject.getFitH(), dest, document);
        } else if (destObject.getFitR() != null) {
            addFitR(destObject.getFitR(), dest, document);
        } else if (destObject.getFitV() != null) {
            addFitV(destObject.getFitV(), dest, document);
        }

        annot.appendChild(dest);
    }

    private static void addActionObject(ActionObject actionObject, Element onActivation, Document document) {
        //no attributes, children elements URI|Launch|GoTo|GoToR|Named
        Element action = document.createElement(XfdfConstants.ACTION);
        if (actionObject.getUri() != null) {
            Element uri = document.createElement(XfdfConstants.URI);
            //no children
            //required attribute Name, optional attribute IsMap
            uri.setAttribute(XfdfConstants.NAME_CAPITAL, actionObject.getUri().getValue());
            if (actionObject.isMap()) {
                uri.setAttribute(XfdfConstants.IS_MAP, "true");
            } else {
                uri.setAttribute(XfdfConstants.IS_MAP, "false");
            }
            action.appendChild(uri);

        } else if (PdfName.GoTo.equals(actionObject.getType())) {
            Element goTo = document.createElement(XfdfConstants.GO_TO);

            addDest(actionObject.getDestination(), goTo, document);

            action.appendChild(goTo);
        } else if (PdfName.GoToR.equals(actionObject.getType())) {

            Element goToR = document.createElement(XfdfConstants.GO_TO_R);

            if (actionObject.getDestination() != null) {
                addDest(actionObject.getDestination(), goToR, document);
            } else if (actionObject.getFileOriginalName() != null) {
                Element file = document.createElement(XfdfConstants.FILE);
                file.setAttribute(XfdfConstants.ORIGINAL_NAME, actionObject.getFileOriginalName());
                goToR.appendChild(file);
            } else {
                logger.error("Dest or File elements are missing.");
            }

            action.appendChild(goToR);

        } else if (PdfName.Named.equals(actionObject.getType())) {
            Element named = document.createElement(XfdfConstants.NAMED);
            named.setAttribute(XfdfConstants.NAME_CAPITAL, actionObject.getNameAction().getValue());

            action.appendChild(named);

        } else if (PdfName.Launch.equals(actionObject.getType())) {
            Element launch = document.createElement(XfdfConstants.LAUNCH);
            if (actionObject.getFileOriginalName() != null) {
                Element file = document.createElement(XfdfConstants.FILE);
                file.setAttribute(XfdfConstants.ORIGINAL_NAME, actionObject.getFileOriginalName());
                launch.appendChild(file);
            } else {
                logger.error("File element is missing");
            }
            if (actionObject.isNewWindow()) {
                launch.setAttribute(XfdfConstants.NEW_WINDOW, "true");
            }
            action.appendChild(launch);
        }

        onActivation.appendChild(action);
    }

    private static void addPopup(AnnotObject popupAnnotObject, Element popup, Element annot) {
        for(AttributeObject attr: popupAnnotObject.getAttributes()) {
            popup.setAttribute(attr.getName(), attr.getValue());
        }
        annot.appendChild(popup);
    }
}
