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
package com.itextpdf.pdfua.logs;

/**
 * Class containing the log message constants.
 */
public final class PdfUALogMessageConstants {

    public static final String PAGE_FLUSHING_DISABLED = "Page flushing is disabled in PDF/UA mode to allow UA checks "
            + "to be applied. Page will only be flushed on closing.";
    public static final String PDF_TO_PDF_UA_CONVERSION_IS_NOT_SUPPORTED = "PDF to PDF/UA conversion is not supported.";
    public static final String WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN =
            "Pdf version from writer properties isn't compatible with specified PDF/UA conformance, it was overridden to {0} version.";

    private PdfUALogMessageConstants() {
        // empty constructor
    }
}
