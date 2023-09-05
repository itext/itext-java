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
package com.itextpdf.pdfa.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class PdfaExceptionMessageConstant {
    public static final String CANNOT_PARSE_CONTENT_STREAM = "Cannot parse content stream.";
    public static final String INVALID_STREAM_FILTER_USAGE = "Filters that are not listed in ISO 32000-2:—, 7.4, Table 6 shall not be used.";
    public static final String INVALID_INLINE_IMAGE_FILTER_USAGE = "Filters that are not listed in ISO 32000-2:—, 8.9.7, Table 92 or an array containing any such value shall not be used.";
    public static final String DOCUMENT_INFO_DICTIONARY_SHALL_ONLY_CONTAIN_MOD_DATE = "If a document information dictionary is present, it shall only contain a ModDate entry.";
    public static final String DOCUMENT_SHALL_NOT_CONTAIN_INFO_UNLESS_THERE_IS_PIECE_INFO = "The Info key shall not be present in the trailer dictionary of PDF/A-4 conforming files unless there exists a PieceInfo entry in the document catalog dictionary.";
    public static final String THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION = "The file header shall begin at byte zero and shall consist of “%PDF-{0}.n”";
    public static final String THE_CATALOG_VERSION_SHALL_CONTAIN_RIGHT_PDF_VERSION = "The catalog version key shall begin at byte zero and shall consist of “%PDF-{0}.n”";
    public static final String CANNOT_FIND_PDFA_CHECKER_FOR_SPECIFIED_NAME
            = "Can't find an appropriate checker for a specified name.";
    public static final String WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_ENTRY = "Widget annotation dictionary or field dictionary shall not include a entry";

    private PdfaExceptionMessageConstant(){}
}
