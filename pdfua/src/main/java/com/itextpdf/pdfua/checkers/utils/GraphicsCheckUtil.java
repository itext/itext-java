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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;
import com.itextpdf.layout.element.Image;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Class that provides methods for checking PDF/UA compliance of graphics elements.
 */
public final class GraphicsCheckUtil {


    /**
     * Creates a new {@link GraphicsCheckUtil} instance.
     */
    private GraphicsCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if image has alternative description or actual text.
     *
     * @param image image to check
     *
     * @throws PdfUAConformanceException if image doesn't have alternative description or actual text
     */
    public static void checkLayoutImage(Image image) {
        if (image.getAccessibilityProperties() == null) {
            throw new IllegalStateException();
        }
        if (!StandardRoles.FIGURE.equals(image.getAccessibilityProperties().getRole())) {
            // image is not a figure tag, so we don't need to check it
            return;
        }
        AccessibilityProperties props = image.getAccessibilityProperties();
        boolean hasSomeValue = hasAtleastOneValidValue(props.getAlternateDescription(), props.getActualText());
        if (!hasSomeValue) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
    }

    /**
     * Checks if figure tag has alternative description or actual text.
     *
     * @return {@link ITagTreeIteratorHandler} handler implementation that checks if figure tag has alternative
     * description or actual text
     */
    public static ITagTreeIteratorHandler createFigureTagHandler() {
        return new ITagTreeIteratorHandler() {
            @Override
            public void nextElement(IStructureNode elem) {
                if (!PdfName.Figure.equals(elem.getRole())) {
                    return;
                }
                // we only need to check struct elems, not MCR numbers as they don't contain any useful info
                if (!(elem instanceof PdfStructElem)) {
                    return;
                }
                final PdfStructElem structElem = ((PdfStructElem) elem);
                final PdfDictionary pdfObject = structElem.getPdfObject();

                if (!hasAtleastOneValidValue(pdfObject.getAsString(PdfName.Alt),
                        pdfObject.getAsString(PdfName.ActualText))) {
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
                }
            }
        };
    }

    private static boolean hasAtleastOneValidValue(Object altText, Object actualText) {
        String altTextValue = null;
        if (altText instanceof PdfString) {
            altTextValue = ((PdfString) altText).getValue();
        }
        String actualTextValue = null;
        if (actualText instanceof PdfString) {
            actualTextValue = ((PdfString) actualText).getValue();
        }
        if (altText instanceof String) {
            altTextValue = (String) altText;
        }
        if (actualText instanceof String) {
            actualTextValue = (String) actualText;
        }
        // PDF spec is not super clear, but it seems actualText can be an empty string
        return !(altTextValue == null || altTextValue.isEmpty()) || actualTextValue != null;
    }

}
