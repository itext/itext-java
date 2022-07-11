/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.forms.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class FormsExceptionMessageConstant {
    public static final String FIELD_FLATTENING_IS_NOT_SUPPORTED_IN_APPEND_MODE = "Field flattening is not supported "
            + "in append mode.";
    public static final String PAGE_ALREADY_FLUSHED_USE_ADD_FIELD_APPEARANCE_TO_PAGE_METHOD_BEFORE_PAGE_FLUSHING = ""
            + "The page has been already flushed. Use PdfAcroForm#addFieldAppearanceToPage() method before page "
            + "flushing.";
    public static final String WRONG_FORM_FIELD_ADD_ANNOTATION_TO_THE_FIELD = "Wrong form field. Add annotation to the "
            + "field.";

    public static final String N_ENTRY_IS_REQUIRED_FOR_APPEARANCE_DICTIONARY =
            "\\N entry is required to be present in an appearance dictionary.";

    private FormsExceptionMessageConstant(){}
}
