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
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Class that provides methods for checking PDF/UA compliance of interactive form fields.
 */
public class FormCheckUtil {

    /**
     * Creates a new {@link FormCheckUtil} instance.
     */
    private FormCheckUtil() {
        // Empty constructor
    }

    /**
     * Handler for checking form field elements in the tag tree.
     */
    public static class FormTagHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new {@link  FormulaCheckUtil.FormulaTagHandler} instance.
         *
         * @param context The validation context.
         */
        public FormTagHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            PdfStructElem form = context.getElementIfRoleMatches(PdfName.Form, elem);
            if (form == null) {
                return;
            }

            PdfDictionary formField = getInteractiveKidForm(form);
            if (formField == null) {
                return;
            }

            // Check is not applicable for hidden annotations
            if (!AnnotationCheckUtil.isAnnotationVisible(formField)) {
                return;
            }

            // Parent check is required for the case when form field and widget annotation are split up.
            // It is still not 100% correct because TU is not inheritable thus shouldn't be taken into account
            // for the case like field -> merged field with widget annotation
            // (here we must not take field.TU into account)
            final boolean parentContainsTU = formField.get(PdfName.Parent) != null
                    && formField.getAsDictionary(PdfName.Parent).get(PdfName.TU) != null;
            // Element should have either alternative description or TU entry
            if (formField.get(PdfName.TU) == null && !parentContainsTU && form.getAlt() == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
            }
        }

        /**
         * Gets a widget annotation kid if it exists.
         *
         * @param structElem Parent structure element.
         * @return Kid as PdfDictionary.
         */
        private static PdfDictionary getInteractiveKidForm(PdfStructElem structElem) {
            PdfDictionary object = structElem.getPdfObject();
            PdfDictionary kids = object.getAsDictionary(PdfName.K);
            // It's a dictionary in this particular case
            if (kids != null && kids.get(PdfName.Obj) != null && PdfName.Widget.equals(
                    ((PdfDictionary) kids.get(PdfName.Obj)).getAsName(PdfName.Subtype))) {
                return (PdfDictionary) kids.get(PdfName.Obj);
            }

            return null;
        }
    }
}
