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
package com.itextpdf.forms.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class FormsExceptionMessageConstant {
    public static final String CANNOT_MERGE_FORMFIELDS = "Cannot merge form fields with the same names. Partial name " +
            "is {0}. Field dictionaries with the same fully qualified field name shall have the same field type (FT), "
            + "value (V), and default value (DV).";

    public static final String FIELD_FLATTENING_IS_NOT_SUPPORTED_IN_APPEND_MODE = "Field flattening is not supported "
            + "in append mode.";

    public static final String INNER_ARRAY_SHALL_HAVE_TWO_ELEMENTS = "Inner arrays shall have exactly two elements";

    public static final String OPTION_ELEMENT_MUST_BE_STRING_OR_ARRAY = "Option element must be a string or an array";

    public static final String PAGE_ALREADY_FLUSHED_USE_ADD_FIELD_APPEARANCE_TO_PAGE_METHOD_BEFORE_PAGE_FLUSHING = ""
            + "The page has been already flushed. Use PdfAcroForm#addFieldAppearanceToPage() method before page "
            + "flushing.";

    public static final String FORM_FIELD_MUST_HAVE_A_NAME = "Form field must have a name."
            + " Set it using PdfFormField#setFieldName call.";
    public static final String APEARANCE_NAME_MUST_BE_PROVIDED = "Appearance name must be provided";
    public static final String WIDGET_RECTANGLE_MUST_BE_PROVIDED = "Widget rectangle must be provided";
    public static final String EMPTY_RADIO_GROUP_NAME = "Radio group name cannot be empty.";
    public static final String CHECKBOX_TYPE_NOT_SUPPORTED = "Unsupported checkbox type for PDF/A";
    public static final String INVALID_ROTATION_VALUE = "Invalid rotation. Rotation must be a multiple of 90 degrees.";
    public static final String OPTION_ELEMENT_SHALL_NOT_BE_NULL = "Option element shall not be null.";
    public static final String VALUE_SHALL_NOT_BE_NULL = "Value <{0}> shall not be null";
    public static final String INDEX_OUT_OF_BOUNDS = "Index: {0}, Size: {1}";


    public static final String SEPARATOR_SHOULD_BE_A_VALID_VALUE = "Separator should be a valid value. Values that are "
            + "not allowed are null,empty string, or . ";
    public static final String FIELD_NAME_ALREADY_EXISTS_IN_FORM = "Field name {0} already exists in the form.";
    public static final String ROLE_NAME_INVALID_FOR_FORM = "Invalid formfield type: {0}, only following values are "
            + "allowed {1}.";
    ;


    private FormsExceptionMessageConstant() {
        // empty constructor
    }
}
