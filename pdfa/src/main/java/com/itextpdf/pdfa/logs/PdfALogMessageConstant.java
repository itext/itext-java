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
package com.itextpdf.pdfa.logs;

/**
 * Class containing constants to be used in logging.
 */
public class PdfALogMessageConstant {
    public static final String PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED =
            "Page flushing was not performed. Pages flushing in PDF/A mode works only with explicit calls to "
                    + "PdfPage#flush(boolean) with flushResourcesContentStreams argument set to true";

    public static final String PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED =
            "Object flushing was not performed. Object in PDF/A mode can only be flushed if the document is closed or "
                    + "if this object has already been checked for compliance with PDF/A rules.";
    public static final String WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN =
            "Pdf version from writer properties isn't compatible with specified PDF/A conformance, it was overridden to {0} version.";

    private PdfALogMessageConstant() {
        //Private constructor will prevent the instantiation of this class directly
    }
}
