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

import com.itextpdf.forms.exceptions.AttributeNotFoundException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents annotation, a child element of annots tag in Xfdf document structure.
 * For more details see part 6.4 in Xfdf specification.
 */
public class AnnotObject {

    /**
     * Represents the type of annotation. Possible values: {@link XfdfConstants#CARET}, {@link XfdfConstants#CIRCLE},
     * {@link XfdfConstants#FILEATTACHMENT}, {@link XfdfConstants#FREETEXT}, {@link XfdfConstants#HIGHLIGHT},
     * {@link XfdfConstants#INK}, {@link XfdfConstants#LINE}, {@link XfdfConstants#POLYGON}, {@link XfdfConstants#POLYLINE},
     * {@link XfdfConstants#SOUND}, {@link XfdfConstants#SQUARE}, {@link XfdfConstants#SQUIGGLY},
     * {@link XfdfConstants#STAMP}, {@link XfdfConstants#STRIKEOUT}, {@link XfdfConstants#TEXT}, {@link XfdfConstants#UNDERLINE}.
     */
    private String name;

    /**
     * Represents a list of attributes of the annotation.
     */
    private List<AttributeObject> attributes;

    /**
     * Represents contents tag in Xfdf document structure. Is a child of caret, circle, fileattachment, freetext,
     * highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout, text, and
     * underline elements.
     * Corresponds to Contents key in annotation dictionary.
     * Content model: a string or a rich text string.
     * For more details see paragraph 6.5.4 in Xfdf document specification.
     */
    private PdfString contents;

    /**
     * Represents contents-richtext tag in Xfdf document structure. Is a child of caret, circle, fileattachment, freetext,
     * highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout, text, and
     * underline elements.
     * Corresponds to RC key in annotation dictionary.
     * Content model: text string.
     * For more details see paragraph 6.5.5 in Xfdf document specification.
     */
    private PdfString contentsRichText;

    /**
     * A boolean, indicating if annotation has inner popup element.
     */
    private boolean hasPopup;

    /**
     * Represents a popup annotation, an inner element of the annotation element.
     */
    private AnnotObject popup;

    /**
     * Represents Action element, a child of OnActivation element of the link annotation.
     * Corresponds to the A key in the link annotation dictionary.
     */
    private ActionObject action;

    /**
     * Represents Dest element, a child element of link, GoTo, GoToR elements.
     * Corresponds to the Dest key in link annotation dictionary.
     */
    private DestObject destination;

    /**
     * Represents appearance element,  a child element of stamp element.
     * Corresponds to the AP key in the annotation dictionary.
     * Content model: Base64 encoded string.
     * For more details see paragraph 6.5.1 in Xfdf document specification.
     */
    private String appearance;

    /**
     * Represents the defaultappearance element, a child of the caret and freetext elements.
     * Corresponds to the DA key in the free text annotation dictionary.
     * Content model: text string.
     * For more details see paragraph 6.5.7 in Xfdf document specification.
     */
    private String defaultAppearance;

    /**
     * Represents defaultstyle element, a child of the freetext element.
     * Corresponds to the DS key in the free text annotation dictionary.
     * Content model : a text string.
     * For more details see paragraph 6.5.9 in Xfdf document specification.
     */
    private String defaultStyle;

    /**
     * Represents the BorderStyleAlt element, a child of the link element.
     * Corresponds to the Border key in the common annotation dictionary.
     * For more details see paragraph 6.5.3 in Xfdf document specification.
     */
    private BorderStyleAltObject borderStyleAlt;

    /**
     * Represents the string, containing vertices element, a child of the polygon and polyline elements.
     * Corresponds to the Vertices key in the polygon or polyline annotation dictionary.
     * For more details see paragraph 6.5.31 in Xfdf document specification.
     */
    private String vertices;

    /**
     * The reference to the source {@link PdfAnnotation}. Used for attaching popups in case of reading data from pdf file.
     */
    private PdfIndirectReference ref;

    /**
     * Creates an instance of {@link AnnotObject}.
     */
    public AnnotObject() {
        this.attributes = new ArrayList<>();
    }

    /**
     * Gets the string value of the type of annotation. Possible values: {@link XfdfConstants#CARET}, {@link XfdfConstants#CIRCLE},
     * {@link XfdfConstants#FILEATTACHMENT}, {@link XfdfConstants#FREETEXT}, {@link XfdfConstants#HIGHLIGHT},
     * {@link XfdfConstants#INK}, {@link XfdfConstants#LINE}, {@link XfdfConstants#POLYGON}, {@link XfdfConstants#POLYLINE},
     * {@link XfdfConstants#SOUND}, {@link XfdfConstants#SQUARE}, {@link XfdfConstants#SQUIGGLY},
     * {@link XfdfConstants#STAMP}, {@link XfdfConstants#STRIKEOUT}, {@link XfdfConstants#TEXT}, {@link XfdfConstants#UNDERLINE}.
     *
     * @return {@link String} value of the type of annotation.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the string value of the type of annotation. Possible values: {@link XfdfConstants#CARET}, {@link XfdfConstants#CIRCLE},
     * {@link XfdfConstants#FILEATTACHMENT}, {@link XfdfConstants#FREETEXT}, {@link XfdfConstants#HIGHLIGHT},
     * {@link XfdfConstants#INK}, {@link XfdfConstants#LINE}, {@link XfdfConstants#POLYGON}, {@link XfdfConstants#POLYLINE},
     * {@link XfdfConstants#SOUND}, {@link XfdfConstants#SQUARE}, {@link XfdfConstants#SQUIGGLY},
     * {@link XfdfConstants#STAMP}, {@link XfdfConstants#STRIKEOUT}, {@link XfdfConstants#TEXT}, {@link XfdfConstants#UNDERLINE}.
     *
     * @param name {@link String} value of the type of annotation
     *
     * @return {@link AnnotObject annotation object} with set name.
     */
    public AnnotObject setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets a list of all attributes of the annotation.
     *
     * @return {@link List list} containing all {@link AttributeObject attribute objects} of the annotation.
     */
    public List<AttributeObject> getAttributes() {
        return attributes;
    }

    /**
     * Finds the attribute by name in attributes list.
     *
     * @param name the name of the attribute to look for
     *
     * @return {@link AttributeObject} with the given name, or null, if no object with this name was found.
     */
    public AttributeObject getAttribute(String name) {
        for (AttributeObject attr : attributes) {
            if (attr.getName().equals(name)) {
                return attr;
            }
        }
        return null;
    }

    /**
     * Finds the attribute by name in attributes list and return its string value.
     *
     * @param name the name of the attribute to look for
     *
     * @return the value of the {@link AttributeObject} with the given name,
     * or null, if no object with this name was found.
     */
    public String getAttributeValue(String name) {
        for (AttributeObject attr : attributes) {
            if (attr.getName().equals(name)) {
                return attr.getValue();
            }
        }
        return null;
    }

    /**
     * Gets the popup annotation, an inner element of the annotation element.
     *
     * @return {@link AnnotObject} representing the inner popup annotation.
     */
    public AnnotObject getPopup() {
        return popup;
    }

    /**
     * Sets the popup annotation, an inner element of the annotation element.
     *
     * @param popup {@link AnnotObject annotation object} representing inner popup annotation
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setPopup(AnnotObject popup) {
        this.popup = popup;
        return this;
    }

    /**
     * Gets the boolean, indicating if annotation has an inner popup element.
     *
     * @return true if annotation has an inner popup element, false otherwise.
     */
    public boolean isHasPopup() {
        return hasPopup;
    }

    /**
     * Sets the boolean, indicating if annotation has inner popup element.
     *
     * @param hasPopup a boolean indicating if annotation has inner popup element
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setHasPopup(boolean hasPopup) {
        this.hasPopup = hasPopup;
        return this;
    }

    /**
     * Gets the string value of contents tag in Xfdf document structure. Contents is a child of caret, circle,
     * fileattachment, freetext, highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout,
     * text, and underline elements.
     * Corresponds to Contents key in annotation dictionary.
     * Content model: a string or a rich text string.
     * For more details see paragraph 6.5.4 in Xfdf document specification.
     *
     * @return {@link PdfString} value of inner contents element of current annotation object.
     */
    public PdfString getContents() {
        return contents;
    }

    /**
     * Sets the string value of contents tag in Xfdf document structure.
     *
     * @param contents {@link PdfString string} value of inner contents element
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setContents(PdfString contents) {
        this.contents = contents;
        return this;
    }

    /**
     * Gets the string value of contents-richtext tag in Xfdf document structure. It is a child of caret, circle, fileattachment,
     * freetext, highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout, text, and
     * underline elements.
     * Corresponds to RC key in annotation dictionary.
     * Content model: text string.
     * For more details see paragraph 6.5.5 in Xfdf document specification.
     *
     * @return {@link PdfString} value of inner contents-richtext element of current annotation object.
     */
    public PdfString getContentsRichText() {
        return contentsRichText;
    }

    /**
     * Sets the string value of contents-richtext tag in xfdf document structure.
     *
     * @param contentsRichRext {@link PdfString rich text string} value of inner contents-richtext element
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setContentsRichText(PdfString contentsRichRext) {
        this.contentsRichText = contentsRichRext;
        return this;
    }

    /**
     * Gets Action element, a child of OnActivation element of the link annotation.
     * Corresponds to the A key in the link annotation dictionary.
     *
     * @return inner {@link ActionObject action object} of annotation object.
     */
    public ActionObject getAction() {
        return action;
    }

    /**
     * Sets Action element, a child of OnActivation element of the link annotation.
     * Corresponds to the A key in the link annotation dictionary.
     *
     * @param action {@link ActionObject action object}, an inner element of annotation object
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setAction(ActionObject action) {
        this.action = action;
        return this;
    }

    /**
     * Adds new {@link AttributeObject} to the list of annotation attributes.
     *
     * @param attr attribute to be added.
     */
    public void addAttribute(AttributeObject attr) {
        attributes.add(attr);
    }

    /**
     * Adds new attribute with given name and boolean value converted to string.
     */
    void addAttribute(String name, boolean value) {
        String valueString = value ? "yes" : "no";
        attributes.add(new AttributeObject(name, valueString));
    }

    void addAttribute(String name, float value) {
        attributes.add(new AttributeObject(name, XfdfObjectUtils.convertFloatToString(value)));
    }

    void addAttribute(String name, Rectangle value) {
        String stringValue = XfdfObjectUtils.convertRectToString(value);
        attributes.add(new AttributeObject(name, stringValue));
    }

    /**
     * Adds new attribute by given name and value. If required attribute is present, value of the attribute can't be null.
     *
     * @param name        {@link String} attribute name
     * @param valueObject {@link PdfObject} attribute value
     * @param required    boolean indicating if the attribute is required
     */
    void addAttribute(String name, PdfObject valueObject, boolean required) {
        if (valueObject == null) {
            if (required) {
                throw new AttributeNotFoundException(name);
            }
            return;
        }
        String valueString = null;
        if (valueObject.getType() == PdfObject.BOOLEAN) {
            valueString = ((PdfBoolean) (valueObject)).getValue() ? "yes" : "no";
        } else if (valueObject.getType() == PdfObject.NAME) {
            valueString = ((PdfName) (valueObject)).getValue();
        } else if (valueObject.getType() == PdfObject.NUMBER) {
            valueString = XfdfObjectUtils.convertFloatToString((float) ((PdfNumber) (valueObject)).getValue());
        } else if (valueObject.getType() == PdfObject.STRING) {
            valueString = ((PdfString) (valueObject)).getValue();
        }

        attributes.add(new AttributeObject(name, valueString));
    }

    void addAttribute(String name, PdfObject valueObject) {
        addAttribute(name, valueObject, false);
    }

    /**
     * Adds page, required attribute of every annotation.
     */
    void addFdfAttributes(int pageNumber) {
        this.addAttribute(new AttributeObject(XfdfConstants.PAGE, String.valueOf(pageNumber)));
    }

    /**
     * Gets Dest element, a child element of link, GoTo, GoToR elements.
     * Corresponds to the Dest key in link annotation dictionary.
     *
     * @return inner {@link DestObject destination object} of annotation object
     */
    public DestObject getDestination() {
        return destination;
    }

    /**
     * Sets Dest element, a child element of link, GoTo, GoToR elements.
     * Corresponds to the Dest key in link annotation dictionary.
     *
     * @param destination {@link DestObject destination object}, an inner element of annotation object
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setDestination(DestObject destination) {
        this.destination = destination;
        return this;
    }

    /**
     * Gets the string value of the appearance element, a child element of stamp element.
     * Corresponds to the AP key in the annotation dictionary.
     * Content model: Base64 encoded string.
     * For more details see paragraph 6.5.1 in Xfdf document specification.
     *
     * @return {@link String} value of inner appearance element.
     */
    public String getAppearance() {
        return appearance;
    }

    /**
     * Gets the string value of the appearance element,  a child element of stamp element.
     * Corresponds to the AP key in the annotation dictionary.
     * Content model: Base64 encoded string.
     *
     * @param appearance {@link String} value of inner appearance element of annotation object
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setAppearance(String appearance) {
        this.appearance = appearance;
        return this;
    }

    /**
     * Gets the string value of the defaultappearance element, a child of the caret and freetext elements.
     * Corresponds to the DA key in the free text annotation dictionary.
     * Content model: text string.
     * For more details see paragraph 6.5.7 in Xfdf document specification.
     *
     * @return {@link String} value of inner default appearance element.
     */
    public String getDefaultAppearance() {
        return defaultAppearance;
    }

    /**
     * Sets the string value of the defaultappearance element, a child of the caret and freetext elements.
     * Corresponds to the DA key in the free text annotation dictionary.
     * Content model: text string.
     *
     * @param defaultAppearance {@link String} value of inner defaultappearance element of annotation object
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setDefaultAppearance(String defaultAppearance) {
        this.defaultAppearance = defaultAppearance;
        return this;
    }

    /**
     * Gets the string value of the defaultstyle element, a child of the freetext element.
     * Corresponds to the DS key in the free text annotation dictionary.
     * Content model : a text string.
     * For more details see paragraph 6.5.9 in Xfdf document specification.
     *
     * @return {@link String} value of inner defaultstyle element.
     */
    public String getDefaultStyle() {
        return defaultStyle;
    }

    /**
     * Sets the string value of the defaultstyle element, a child of the freetext element.
     * Corresponds to the DS key in the free text annotation dictionary.
     * Content model : a text string.
     *
     * @param defaultStyle {@link String} value of inner defaultstyle element of annotation object
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
        return this;
    }

    /**
     * Gets the BorderStyleAlt element, a child of the link element.
     * Corresponds to the Border key in the common annotation dictionary.
     * For more details see paragraph 6.5.3 in Xfdf document specification.
     *
     * @return inner {@link BorderStyleAltObject BorderStyleAlt object}.
     */
    public BorderStyleAltObject getBorderStyleAlt() {
        return borderStyleAlt;
    }

    /**
     * Sets the BorderStyleAlt element, a child of the link element.
     * Corresponds to the Border key in the common annotation dictionary.
     *
     * @param borderStyleAlt inner {@link BorderStyleAltObject BorderStyleAlt object}
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setBorderStyleAlt(BorderStyleAltObject borderStyleAlt) {
        this.borderStyleAlt = borderStyleAlt;
        return this;
    }

    /**
     * Gets the string, containing vertices element, a child of the polygon and polyline elements.
     * Corresponds to the Vertices key in the polygon or polyline annotation dictionary.
     * For more details see paragraph 6.5.31 in Xfdf document specification.
     *
     * @return {@link String} value of inner vertices element.
     */
    public String getVertices() {
        return vertices;
    }

    /**
     * Sets the string, containing vertices element, a child of the polygon and polyline elements.
     * Corresponds to the Vertices key in the polygon or polyline annotation dictionary.
     *
     * @param vertices {@link String} value of inner vertices element
     *
     * @return current {@link AnnotObject annotation object}.
     */
    public AnnotObject setVertices(String vertices) {
        this.vertices = vertices;
        return this;
    }

    /**
     * Gets the reference to the source {@link PdfAnnotation}. Used for attaching popups in case of reading data from pdf file.
     *
     * @return an {@link PdfIndirectReference} of the source annotation object.
     */
    public PdfIndirectReference getRef() {
        return ref;
    }

    /**
     * Sets the reference to the source {@link PdfAnnotation}. Used for attaching popups in case of reading data from pdf file.
     *
     * @param ref {@link PdfIndirectReference} of the source annotation object
     *
     * @return this {@link AnnotObject} instance.
     */
    public AnnotObject setRef(PdfIndirectReference ref) {
        this.ref = ref;
        return this;
    }
}
