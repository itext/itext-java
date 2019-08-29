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
 * Represents annotation, a child element of annots tag in Xfdf document structure. For more details see part 6.4 in Xfdf specification.
 */
public class AnnotObject {

    /**
     * Represents the type of annotation. Possible values: caret, circle, fileattachment, freetext,
     * highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout, text, underline.
     */
    private String name;

    /**
     * Represents a list of attributes of the annotation.
     */
    private List<AttributeObject> attributes;

    /**
     * Represents contents-richtext tag in Xfdf document structure. Is a child of caret, circle, fileattachment, freetext,
     * highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout, text, and
     * underline elements. corresponds to RC key in annotation dictionary.
     * Content model: text string.
     * For more details see paragraph 6.5.4 in Xfdf document specification.
     */
    private PdfString contents;//basically text string

    /**
     * Represents contents-richtext tag in Xfdf document structure. Is a child of caret, circle, fileattachment, freetext,
     * highlight, ink, line, polygon, polyline, sound, square, squiggly, stamp, strikeout, text, and
     * underline elements. corresponds to Contents key in annotation dictionary.
     * Content model: a string or a rich text string.
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
    private String appearance;//should be Base64String

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

    public AnnotObject() {
        this.attributes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public AnnotObject setName(String name) {
        this.name = name;
        return this;
    }

    public List<AttributeObject> getAttributes() {
        return attributes;
    }

    /**
     * The method finds the attribute by name in attributes list.
     * @param name The name of the attribute to look for.
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
     * The method finds the attribute by name in attributes list and return its strign value.
     * @param name The name of the attribute to look for.
     * @return the value of the {@link AttributeObject} with the given name, or null, if no object with this name was found.
     */
    public String getAttributeValue(String name) {
        for (AttributeObject attr : attributes) {
            if (attr.getName().equals(name)) {
                return attr.getValue();
            }
        }
        return null;
    }

    public AnnotObject getPopup() {
        return popup;
    }

    public AnnotObject setPopup(AnnotObject popup) {
        this.popup = popup;
        return this;
    }

    public boolean isHasPopup() {
        return hasPopup;
    }

    public AnnotObject setHasPopup(boolean hasPopup) {
        this.hasPopup = hasPopup;
        return this;
    }

    public PdfString getContents() {
        return contents;
    }

    public AnnotObject setContents(PdfString contents) {
        this.contents = contents;
        return this;
    }

    public PdfString getContentsRichText() {
        return contentsRichText;
    }

    public AnnotObject setContentsRichText(PdfString contentsRichRext) {
        this.contentsRichText = contentsRichRext;
        return this;
    }

    public ActionObject getAction() {
        return action;
    }

    public AnnotObject setAction(ActionObject action) {
        this.action = action;
        return this;
    }

    public void addAttribute(AttributeObject attr) {
        attributes.add(attr);
    }

    void addAttribute(String name, boolean value) {
        String valueString = value ? "yes" : "no";
        attributes.add(new AttributeObject(name, valueString));
    }

    void addAttribute(String name, float value) {
        attributes.add(new AttributeObject(name, String.valueOf(value)));
    }

    void addAttribute(String name, Rectangle value) {
        String stringValue = XfdfObjectUtils.convertRectToString(value);
        attributes.add(new AttributeObject(name, stringValue));
    }

    void addAttribute(String name, PdfObject valueObject, boolean required) {
        if (valueObject == null) {
            if (required) {
                throw new AttributeNotFoundException(name);
            }
            return;
        }
        String valueString = null;
        if (valueObject.getType() == PdfObject.BOOLEAN) {
           valueString = ((PdfBoolean)(valueObject)).getValue() ? "yes" : "no";
        } else if (valueObject.getType() == PdfObject.NAME) {
            valueString = ((PdfName)(valueObject)).getValue();
        } else if (valueObject.getType() == PdfObject.NUMBER) {
            valueString = String.valueOf(((PdfNumber)(valueObject)).getValue());
        } else if (valueObject.getType() == PdfObject.STRING) {
            valueString = ((PdfString)(valueObject)).getValue();
        }

        attributes.add(new AttributeObject(name, valueString));
    }

    void addAttribute(String name, PdfObject valueObject) {
        addAttribute(name, valueObject, false);
    }

    void addFdfAttributes(int pageNumber) {
        this.addAttribute(new AttributeObject(XfdfConstants.PAGE, String.valueOf(pageNumber)));
    }

    public DestObject getDestination() {
        return destination;
    }

    public AnnotObject setDestination(DestObject destination) {
        this.destination = destination;
        return this;
    }

    public String getAppearance() {
        return appearance;
    }

    public AnnotObject setAppearance(String appearance) {
        this.appearance = appearance;
        return this;
    }

    public String getDefaultAppearance() {
        return defaultAppearance;
    }

    public AnnotObject setDefaultAppearance(String defaultAppearance) {
        this.defaultAppearance = defaultAppearance;
        return this;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public AnnotObject setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
        return this;
    }

    public BorderStyleAltObject getBorderStyleAlt() {
        return borderStyleAlt;
    }

    public AnnotObject setBorderStyleAlt(BorderStyleAltObject borderStyleAlt) {
        this.borderStyleAlt = borderStyleAlt;
        return this;
    }

    public String getVertices() {
        return vertices;
    }

    public AnnotObject setVertices(String vertices) {
        this.vertices = vertices;
        return this;
    }

    public PdfIndirectReference getRef() {
        return ref;
    }

    public AnnotObject setRef(PdfIndirectReference ref) {
        this.ref = ref;
        return this;
    }
}
