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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Class that provides methods for checking PDF/UA compliance of actions.
 */
public class ActionCheckUtil {
    private ActionCheckUtil() {
        // Empty constructor.
    }

    /**
     * Check PDF/UA compliance of an action
     *
     * @param action action to check
     */
    public static void checkAction(PdfDictionary action)  {
        if (action == null) {
            return;
        }
        PdfName s = action.getAsName(PdfName.S);
        PdfDictionary rendition = action.getAsDictionary(PdfName.R);
        if (PdfName.Rendition.equals(s) && rendition != null) {
            checkRenditionMedia(rendition.getAsDictionary(PdfName.BE) != null ? rendition.getAsDictionary(PdfName.BE).getAsDictionary(PdfName.C) : null);
            checkRenditionMedia(rendition.getAsDictionary(PdfName.MH) != null ? rendition.getAsDictionary(PdfName.MH).getAsDictionary(PdfName.C) : null);
            checkRenditionMedia(rendition.getAsDictionary(PdfName.C));
        }
    }

    private static void checkRenditionMedia(PdfDictionary mediaClipDict) {
        if (mediaClipDict != null && (mediaClipDict.get(PdfName.CT) == null || mediaClipDict.get(PdfName.Alt) == null)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.CT_OR_ALT_ENTRY_IS_MISSING_IN_MEDIA_CLIP);
        }
    }
}
