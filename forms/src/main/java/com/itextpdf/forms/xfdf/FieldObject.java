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

/**
 * Represents the field element, a child of the fields and field elements.
 * The field element corresponds to a form field.
 * Content model: ( field* | value* | ( value? &amp; value-richtext? )).
 * Required attributes: name.
 * For more details see paragraph 6.3.2 in XFDF document specification.
 */
public class FieldObject {

    /**
     * Represents the name attribute of the field element.
     * Corresponds to the T key in the field dictionary.
     * In a hierarchical form field, the name is the partial field name.
     * For more details see paragraph 6.3.2.2 in XFDF document specification.
     */
    private String name;

    /**
     * Represents the value element, a child of the field element and contains the field's value, whose format may
     * vary depending on the field type.
     * Corresponds to the V key in the FDF field dictionary.
     * Content model: text string.
     * For more details see paragraph 6.3.3 in XFDF document specification.
     */
    private String value;

    /**
     * Represents the value-richtext element, a child of the field element and contains the field's value formatted as a
     * rich text string.
     * Corresponds to the RV key in the variable text field dictionary.
     * Content model: text strign or rich text string.
     * Attributes: none.
     * For more details see paragraph 6.3.4 in XFDF document specification.
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

    /**
     * Creates an instance of {@link FieldObject}.
     */
    public FieldObject() {
    }

    /**
     * Creates an instance of {@link FieldObject}.
     *
     * @param name             the name attribute of the field element
     * @param value            the field's value
     * @param containsRichText indicates if a value-richtext element is present inside the field
     */
    public FieldObject(String name, String value, boolean containsRichText) {
        this.name = name;
        this.containsRichText = containsRichText;
        if (containsRichText) {
            this.richTextValue = value;
        } else {
            this.value = value;
        }
    }

    /**
     * Gets the string value of the name attribute of the field element.
     * Corresponds to the T key in the field dictionary.
     * In a hierarchical form field, the name is the partial field name.
     * For more details see paragraph 6.3.2.2 in XFDF document specification.
     *
     * @return {@link String} value of field name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the string value of the name attribute of the field element.
     * Corresponds to the T key in the field dictionary.
     * In a hierarchical form field, the name is the partial field name.
     *
     * @param name {@link String} value of field name attribute
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the string representation of the value element, a child of the field element and contains the field's value,
     * whose format may vary depending on the field type.
     * Corresponds to the V key in the FDF field dictionary.
     * For more details see paragraph 6.3.3 in XFDF document specification.
     *
     * @return {@link String} representation of inner value element of the field.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the string representation of the value element, a child of the field element and contains the field's value,
     * whose format may vary depending on the field type.
     * Corresponds to the V key in the FDF field dictionary.
     *
     * @param value {@link String} representation of inner value element of the field
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the string representation of the value-richtext element, a child of the field element and contains the
     * field's value formatted as a rich text string.
     * Corresponds to the RV key in the variable text field dictionary.
     * Content model: text strign or rich text string.
     * For more details see paragraph 6.3.4 in XFDF document specification.
     *
     * @return {@link String} representation of inner value-richtext element of the field.
     */
    public String getRichTextValue() {
        return richTextValue;
    }

    /**
     * Sets the string representation of the value-richtext element, a child of the field element and contains the
     * field's value formatted as a rich text string.
     * Corresponds to the RV key in the variable text field dictionary.
     * Content model: text string or rich text string.
     *
     * @param richTextValue {@link String} representation of inner value-richtext element of the field
     */
    public void setRichTextValue(String richTextValue) {
        this.richTextValue = richTextValue;
    }

    /**
     * Gets a boolean indicating if a value-richtext element is present inside the field.
     *
     * @return true if a value-richtext element is present inside the field, false otherwise.
     */
    public boolean isContainsRichText() {
        return containsRichText;
    }

    /**
     * Sets a boolean indicating if a value-richtext element is present inside the field.
     *
     * @param containsRichText a boolean indicating if a value-richtext element is present inside the field
     */
    public void setContainsRichText(boolean containsRichText) {
        this.containsRichText = containsRichText;
    }

    /**
     * Gets a parent field of current field.
     *
     * @return parent {@link FieldObject field object} of the current field.
     */
    public FieldObject getParent() {
        return parent;
    }

    /**
     * Sets a parent field of current field.
     *
     * @param parent {@link FieldObject field object} that is a parent of the current field
     */
    public void setParent(FieldObject parent) {
        this.parent = parent;
    }
}
