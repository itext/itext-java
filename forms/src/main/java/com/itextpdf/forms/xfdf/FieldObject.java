/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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


/**
 * Represents the field element, a child of the fields and field elements.
 * The field element corresponds to a form field.
 * Content model: ( field* | value* | ( value? & value-richtext? )).
 * Required attributes: name.
 * For more details see paragraph 6.3.2 in Xfdf document specification.
 */
public class FieldObject {

    /**
     * Represents the name attribute of the field element.
     * Corresponds to the T key in the field dictionary.
     * In a hierarchical form field, the name is the partial field name.
     * For more details see paragraph 6.3.2.2 in Xfdf document specification.
     */
    private String name;

    /**
     * Represents the value element, a child of the field element and contains the field's value, whose format may
     * vary depending on the field type.
     * Corresponds to the V key in the FDF field dictionary.
     * Content model: text string.
     * For more details see paragraph 6.3.3 in Xfdf document specification.
     */
    private String value;

    /**
     * Represents the value-richtext element, a child of the field element and contains the field's value formatted as a
     * rich text string.
     * Corresponds to the RV key in the variable text field dictionary.
     * Content model: text strign or rich text string.
     * Attributes: none.
     * For more details see paragraph 6.3.4 in Xfdf document specification.
     */
    private String richTextValue;

    /**
     * Indicates if a value-richtext element is present inside the field.
     */
    private boolean containsRichText;

    /**
     * Parent field of current field.
     */
    private FieldObject parent;

    public FieldObject() {
    }

    public FieldObject(String name, String value, boolean containsRichText) {
        this.name = name;
        this.containsRichText = containsRichText;
        if(containsRichText) {
            this.richTextValue = value;
        } else {
            this.value = value;
        }
    }

    /**
     * Gets the string value of the name attribute of the field element.
     * Corresponds to the T key in the field dictionary.
     * In a hierarchical form field, the name is the partial field name.
     * For more details see paragraph 6.3.2.2 in Xfdf document specification.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the string value of the name attribute of the field element.
     * Corresponds to the T key in the field dictionary.
     * In a hierarchical form field, the name is the partial field name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the string representation of the value element, a child of the field element and contains the field's value, whose format may
     * vary depending on the field type.
     * Corresponds to the V key in the FDF field dictionary.
     * For more details see paragraph 6.3.3 in Xfdf document specification.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the string representation of the value element, a child of the field element and contains the field's value, whose format may
     * vary depending on the field type.
     * Corresponds to the V key in the FDF field dictionary.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the string representation of the value-richtext element, a child of the field element and contains the field's value formatted as a
     * rich text string.
     * Corresponds to the RV key in the variable text field dictionary.
     * Content model: text strign or rich text string.
     * For more details see paragraph 6.3.4 in Xfdf document specification.
     */
    public String getRichTextValue() {
        return richTextValue;
    }

    /**
     * Sets the string representation of the value-richtext element, a child of the field element and contains the field's value formatted as a
     * rich text string.
     * Corresponds to the RV key in the variable text field dictionary.
     * Content model: text strign or rich text string.
     */
    public void setRichTextValue(String richTextValue) {
        this.richTextValue = richTextValue;
    }

    /**
     * Gets a boolean indicating if a value-richtext element is present inside the field.
     */
    public boolean isContainsRichText() {
        return containsRichText;
    }

    /**
     * Sets a boolean indicating if a value-richtext element is present inside the field.
     */
    public void setContainsRichText(boolean containsRichText) {
        this.containsRichText = containsRichText;
    }

    /**
     * Gets a parent field of current field.
     */
    public FieldObject getParent() {
        return parent;
    }

    /**
     * Sets a parent field of current field.
     */
    public void setParent(FieldObject parent) {
        this.parent = parent;
    }
}
