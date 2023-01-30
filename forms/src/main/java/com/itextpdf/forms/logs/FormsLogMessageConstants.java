/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
    public static final String ANNOTATION_IN_ACROFORM_DICTIONARY = "Annotation is noticed directly in fields array "
            + "of AcroForm dictionary. It violates pdf specification.";

    public static final String CANNOT_CREATE_FORMFIELD = "Cannot create form field from a given PDF object: {0}";

    public static final String FORM_FIELD_WAS_FLUSHED =
            "A form field was flushed. There's no way to create this field in the AcroForm dictionary.";

    public static final String INCORRECT_PAGEROTATION =
            "Encounterd a page rotation that was not a multiple of 90°/ (Pi/2) when generating default appearances "
                    + "for form fields";

    public static final String NO_FIELDS_IN_ACROFORM =
            "Required AcroForm entry /Fields does not exist in the document. Empty array /Fields will be created.";

    public static final String UNSUPPORTED_COLOR_IN_DA = "Unsupported color in FormField's DA";

    private FormsLogMessageConstants() {
    }
}
