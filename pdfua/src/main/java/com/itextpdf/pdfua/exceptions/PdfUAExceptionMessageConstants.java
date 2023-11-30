/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.pdfua.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class PdfUAExceptionMessageConstants {
    public static final String TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING =
            "Tag hasn't been added before adding content to the canvas.";
    public static final String CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT =
            "Content is neither marked as Artifact nor tagged as real content.";
    public static final String ARTIFACT_CANT_BE_INSIDE_REAL_CONTENT =
            "Content marked as artifact may only reside in Artifact content.";
    public static final String REAL_CONTENT_CANT_BE_INSIDE_ARTIFACT =
            "Content marked as content may not reside in Artifact content.";
    public static final String CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT =
            "Content with MCID, but MCID wasn't found in StructTreeRoot.";
    public static final String FONT_SHOULD_BE_EMBEDDED = "Following font(s) are not embedded: {0}";
    public static final String METADATA_SHALL_BE_PRESENT_IN_THE_CATALOG_DICTIONARY =
            "Metadata shall be present in the catalog dictionary";
    public static final String REAL_CONTENT_INSIDE_ARTIFACT_OR_VICE_VERSA =
            "Tagged content is present inside content marked as Artifact or vice versa.";
    public static final String SUSPECTS_ENTRY_IN_MARK_INFO_DICTIONARY_SHALL_NOT_HAVE_A_VALUE_OF_TRUE =
            "Suspects entry in mark info dictionary shall not have a value of true.";

    private PdfUAExceptionMessageConstants() {
        // Empty constructor
    }
}
