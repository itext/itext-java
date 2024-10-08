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
package com.itextpdf.forms.logs;

/**
 * Class containing constants to be used in logging in forms module.
 */
public final class FormsLogMessageConstants {

    public static final String ACROFORM_NOT_SUPPORTED_FOR_SELECT =
            "AcroForm fields creation for select fields (ComboBoxField and ListBoxField) is not supported. They will "
                    + "be flattened instead.";
    
    public static final String ANNOTATION_IN_ACROFORM_DICTIONARY = "Annotation is noticed directly in fields array "
            + "of AcroForm dictionary. It violates pdf specification.";

    public static final String CANNOT_CREATE_FORMFIELD = "Cannot create form field from a given PDF object: {0}";

    public static final String ERROR_WHILE_LAYOUT_OF_FORM_FIELD =
            "Cannot layout form field. It won't be displayed";

    public static final String ERROR_WHILE_LAYOUT_OF_FORM_FIELD_WITH_TYPE =
            "Error during layout of form field with type {0}.";

    public static final String CANNOT_MERGE_FORMFIELDS = "Cannot merge form fields with the same fully qualified "
            + "names. Partial name is {0}. Field type (FT), value (V), and default value (DV) should be the same";

    public static final String FORM_FIELD_WAS_FLUSHED =
            "A form field was flushed. There's no way to create this field in the AcroForm dictionary.";

    public static final String INCORRECT_PAGE_ROTATION =
            "Encountered a page rotation that was not a multiple of 90°/ (Pi/2) when generating default appearances "
                    + "for form fields";

    public static final String INCORRECT_WIDGET_ROTATION =
            "Encountered a widget rotation that was not a multiple of 90°/ (Pi/2) when generating default appearances "
                    + "for form fields";

    public static final String INPUT_FIELD_DOES_NOT_FIT = "Input field doesn't fit in outer object. It will be clipped";

    public static final String N_ENTRY_IS_REQUIRED_FOR_APPEARANCE_DICTIONARY =
            "\\N entry is required to be present in an appearance dictionary.";
    public static final String RADIO_HAS_NO_RECTANGLE = "Radiobutton was added without defining rectangle, it will "
            + "not be displayed";

    public static final String NO_FIELDS_IN_ACROFORM =
            "Required AcroForm entry /Fields does not exist in the document. Empty array /Fields will be created.";

    public static final String PROVIDE_FORMFIELD_NAME = "No form field name provided. Process will not be continued.";

    public static final String UNSUPPORTED_COLOR_IN_DA = "Unsupported color in FormField's DA";

    public static final String FIELDNAME_NOT_FOUND_OPERATION_CAN_NOT_BE_COMPLETED =
            "Fieldname: <{0}> not found. Operation can not be completed.";
    public static final String INVALID_VALUE_FALLBACK_TO_DEFAULT =
            "Value '{0}': <{1}> invalid. Default value will be used.";
    public static final String CHECKBOX_FONT_SIZE_IS_NOT_POSITIVE = "Shouldn't come here because then we should have "
            + "taken default size";

    public static final String FIELD_VALUE_CANNOT_BE_NULL = "Field value cannot be null.";

    public static final String FORM_FIELD_MUST_HAVE_A_NAME = "Form field must have a name."
            + " Set it using PdfFormField#setFieldName call.";
    public static final String FORM_FIELD_HAS_CYCLED_PARENT_STRUCTURE = "Form field contains parent pointing to itself."
            + " This form field parent reference will be invalidated.";
    public static final String DUPLICATE_EXPORT_VALUE = "More than one option with the same value. This is an invalid"
            + " state.";

    private FormsLogMessageConstants() {
    }
}
