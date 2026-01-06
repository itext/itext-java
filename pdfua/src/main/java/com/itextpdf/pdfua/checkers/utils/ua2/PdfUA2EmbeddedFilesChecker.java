/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.Map;

/**
 * Utility class which performs the EmbeddedFiles name tree check according to PDF/UA-2 specification.
 */
public final class PdfUA2EmbeddedFilesChecker {

    private PdfUA2EmbeddedFilesChecker() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Verify the conformity of the EmbeddedFiles name tree.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    public static void checkEmbeddedFiles(PdfCatalog catalog) {
        PdfNameTree embeddedFiles = catalog.getNameTree(PdfName.EmbeddedFiles);
        Map<PdfString, PdfObject> embeddedFilesMap = embeddedFiles.getNames();
        for (PdfObject fileSpecObject : embeddedFilesMap.values()) {
            checkFileSpec(fileSpecObject);
        }
    }

    /**
     * Verify the conformity of the file specification dictionary.
     *
     * @param obj the {@link PdfDictionary} containing file specification to be checked
     */
    private static void checkFileSpec(PdfObject obj) {
        if (obj.getType() == PdfObject.DICTIONARY) {
            PdfDictionary dict = (PdfDictionary) obj;
            PdfName type = dict.getAsName(PdfName.Type);
            if (PdfName.Filespec.equals(type) && !dict.containsKey(PdfName.Desc)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.DESC_IS_REQUIRED_ON_ALL_FILE_SPEC_FROM_THE_EMBEDDED_FILES);
            }
        }
    }
}
