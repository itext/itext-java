/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.forms.form;

/**
 * Set of constants that will be used as keys to get and set properties.
 */
public final class FormProperty {

    /** The Constant PROPERTY_START. */
    private static final int PROPERTY_START = (1 << 21);

    /** The Constant FORM_FIELD_FLATTEN for form related properties. */
    public static final int FORM_FIELD_FLATTEN = PROPERTY_START + 1;

    /** The Constant FORM_FIELD_SIZE. */
    public static final int FORM_FIELD_SIZE = PROPERTY_START + 2;

    /** The Constant FORM_FIELD_VALUE. */
    public static final int FORM_FIELD_VALUE = PROPERTY_START + 3;

    /** The Constant FORM_FIELD_PASSWORD_FLAG. */
    public static final int FORM_FIELD_PASSWORD_FLAG = PROPERTY_START + 4;

    /** The Constant FORM_FIELD_COLS. */
    public static final int FORM_FIELD_COLS = PROPERTY_START + 5;

    /** The Constant FORM_FIELD_ROWS. */
    public static final int FORM_FIELD_ROWS = PROPERTY_START + 6;

    /** The Constant FORM_FIELD_CHECKED. */
    public static final int FORM_FIELD_CHECKED = PROPERTY_START + 7;

    /** The Constant FORM_FIELD_MULTIPLE. */
    public static final int FORM_FIELD_MULTIPLE = PROPERTY_START + 8;

    /** The Constant FORM_FIELD_SELECTED. */
    public static final int FORM_FIELD_SELECTED = PROPERTY_START + 9;

    /** The Constant FORM_FIELD_LABEL. */
    public static final int FORM_FIELD_LABEL = PROPERTY_START + 10;

    /** The Constant FORM_ACCESSIBILITY_LANGUAGE. */
    @Deprecated()
    public static final int FORM_ACCESSIBILITY_LANGUAGE = PROPERTY_START + 11;

    /** The Constant FORM_FIELD_RADIO_GROUP_NAME. */
    public static final int FORM_FIELD_RADIO_GROUP_NAME = PROPERTY_START + 12;

    /** The Constant FORM_FIELD_RADIO_BORDER_CIRCLE. */
    public static final int FORM_FIELD_RADIO_BORDER_CIRCLE = PROPERTY_START + 13;

    /**
     * The Constant FORM_CHECKBOX_TYPE.
     */
    public static final int FORM_CHECKBOX_TYPE = PROPERTY_START + 14;

    /** The Constant FORM_CONFORMANCE_LEVEL. */
    public static final int FORM_CONFORMANCE_LEVEL = PROPERTY_START + 15;
    
    private FormProperty() {
        // Empty constructor.
    }
}
