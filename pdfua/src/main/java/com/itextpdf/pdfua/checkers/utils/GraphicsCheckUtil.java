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
import com.itextpdf.layout.element.Image;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Class that provides methods for checking PDF/UA compliance of graphics elements.
 */
public final class GraphicsCheckUtil {

    private final PdfUAValidationContext context;

    /**
     * Creates a new {@link GraphicsCheckUtil} instance.
     *
     * @param context The validation context.
     */
    public GraphicsCheckUtil(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if image has alternative description or actual text.
     *
     * @param image The image to check
     */
    public void checkLayoutElement(Image image) {
        if (image.getAccessibilityProperties() == null) {
            throw new IllegalStateException();
        }
        if (!StandardRoles.FIGURE.equals(context.resolveToStandardRole(image.getAccessibilityProperties().getRole()))) {
            // image is not a figure tag, so we don't need to check it
            return;
        }
        AccessibilityProperties props = image.getAccessibilityProperties();
        boolean hasSomeValue = hasAtleastOneValidValue(props.getAlternateDescription(), props.getActualText());
        if (!hasSomeValue) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
        }
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

    /**
     * Helper class that checks the conformance of graphics tags while iterating the tag tree structure.
     */
    public static class GraphicsHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new instance of the {@link GraphicsHandler}.
         *
         * @param context The validation context.
         */
        public GraphicsHandler(PdfUAValidationContext context) {
            super(context);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean nextElement(IStructureNode elem) {
            final PdfStructElem structElem = context.getElementIfRoleMatches(PdfName.Figure, elem);
            if (structElem == null) {
                return true;
            }
            final PdfDictionary pdfObject = structElem.getPdfObject();

            if (!hasAtleastOneValidValue(pdfObject.getAsString(PdfName.Alt),
                    pdfObject.getAsString(PdfName.ActualText))) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT);
            }
            return true;
        }
    }
}
