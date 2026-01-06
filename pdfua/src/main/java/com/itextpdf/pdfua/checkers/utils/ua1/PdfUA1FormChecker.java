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
package com.itextpdf.pdfua.checkers.utils.ua1;

import com.itextpdf.forms.fields.PdfFormAnnotationUtil;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.List;

/**
 * Class that provides methods for checking PDF/UA-1 compliance of interactive form fields.
 */
public final class PdfUA1FormChecker {

    /**
     * Creates a new {@link PdfUA1FormChecker} instance.
     */
    private PdfUA1FormChecker() {
        // Empty constructor.
    }

    /**
     * Checks "Form" structure element.
     *
     * @param form structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect tag structure for {@code Form} tag
     */
    public static void checkFormStructElement(PdfStructElem form) {
        PdfDictionary widget = getInteractiveKidForm(form);
        if (widget == null || !PdfUA1AnnotationChecker.isAnnotationVisible(widget)) {
            // Check is also not applicable for hidden annotations.
            return;
        }

        PdfDictionary formField = widget;
        // Parent check is required for the case when form field and widget annotation are split up.
        if (!PdfFormField.isFormField(widget) && widget.containsKey(PdfName.Parent)) {
            formField = widget.getAsDictionary(PdfName.Parent);
        }

        // Element should have either alternative description or TU entry.
        final boolean fieldContainsTU = formField != null && formField.get(PdfName.TU) != null;
        if (!fieldContainsTU && form.getAlt() == null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION);
        }
    }

    /**
     * Gets a widget annotation kid if it exists.
     *
     * @param structElem parent structure element
     *
     * @return kid as {@link PdfDictionary}
     */
    private static PdfDictionary getInteractiveKidForm(PdfStructElem structElem) {
        List<IStructureNode> kids = structElem.getKids();
        boolean containsSingleWidget = false;
        if (kids.size() == 1) {
            containsSingleWidget = kids.get(0) instanceof PdfObjRef &&
                    PdfFormAnnotationUtil.isPureWidgetOrMergedField(((PdfObjRef) kids.get(0)).getReferencedObject());
        }

        if (!containsSingleWidget && !containsRole(structElem)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.FORM_STRUCT_ELEM_WITHOUT_ROLE_SHALL_CONTAIN_ONE_WIDGET);
        }
        return containsSingleWidget ? ((PdfObjRef) kids.get(0)).getReferencedObject() : null;
    }

    private static boolean containsRole(PdfStructElem structElem) {
        for (PdfStructureAttributes attributes : structElem.getAttributesList()) {
            if ("PrintField".equals(attributes.getAttributeAsEnum("O")) &&
                    attributes.getAttributeAsEnum("Role") != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handler for checking form field elements in the tag tree.
     */
    public static class PdfUA1FormTagHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new {@link PdfUA1FormChecker.PdfUA1FormTagHandler} instance.
         *
         * @param context the validation context
         */
        public PdfUA1FormTagHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            if (elem instanceof PdfObjRef &&
                    PdfFormAnnotationUtil.isPureWidgetOrMergedField(((PdfObjRef) elem).getReferencedObject()) &&
                    !StandardRoles.FORM.equals(context.resolveToStandardRole(elem.getParent()))) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
            }
            PdfStructElem form = context.getElementIfRoleMatches(PdfName.Form, elem);
            if (form == null) {
                return;
            }
            PdfUA1FormChecker.checkFormStructElement(form);
        }
    }
}
