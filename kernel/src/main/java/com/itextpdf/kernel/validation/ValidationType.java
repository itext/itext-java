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
package com.itextpdf.kernel.validation;

/**
 * Type of object to validate.
 */
public enum ValidationType {
    PDF_DOCUMENT,
    // PDF/A Enums
    CANVAS_STACK,
    FILL_COLOR,
    EXTENDED_GRAPHICS_STATE,
    INLINE_IMAGE,
    PDF_PAGE,
    PDF_OBJECT,
    RENDERING_INTENT,
    STROKE_COLOR,
    TAG_STRUCTURE_ELEMENT,
    FONT_GLYPHS,
    XREF_TABLE,
    SIGNATURE,
    SIGNATURE_TYPE,
    CRYPTO,
    FONT,
    // PDF/UA Enums
    CANVAS_BEGIN_MARKED_CONTENT,
    CANVAS_WRITING_CONTENT,
    LAYOUT,
    DUPLICATE_ID_ENTRY
}
