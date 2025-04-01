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
package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.forms.fields.PdfFormAnnotationUtil;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.List;

/**
 * Class that provides methods for checking PDF/UA-2 compliance of interactive form fields.
 */
public final class PdfUA2FormChecker {
    private final PdfUAValidationContext context;

    /**
     * Creates a new {@link PdfUA2FormChecker} instance.
     *
     * @param validationContext the validation context
     */
    public PdfUA2FormChecker(PdfUAValidationContext validationContext) {
        this.context = validationContext;
    }

    /**
     * Verifies the conformity of the widget annotation present in the document.
     *
     * <p>
     * Checks that each widget annotation is either Form structure element or an Artifact; if label for a widget
     * annotation is not present or an additional action (AA) entry is present, Contents entry is provided.
     *
     * @param document the {@link PdfDocument} to check widgets from
     */
    public void checkWidgetAnnotations(PdfDocument document) {
        int amountOfPages = document.getNumberOfPages();
        for (int i = 1; i <= amountOfPages; ++i) {
            PdfPage page = document.getPage(i);
            for (final PdfAnnotation annot : page.getAnnotations()) {
                checkWidget(annot.getPdfObject());
            }
        }
    }

    /**
     * Verifies the conformity of the document Acroform dictionary.
     *
     * <p>
     * Checks that each widget annotation is either Form structure element or an Artifact; if label for a widget
     * annotation is not present or an additional action (AA) entry is present, Contents entry is provided;
     * text field {@code RV} and {@code V} values are textually equal.
     *
     * @param form the form {@link PdfDictionary} to be checked
     */
    public void checkFormFields(PdfDictionary form) {
        if (form == null) {
            return;
        }
        PdfArray fields = form.getAsArray(PdfName.Fields);
        if (fields == null) {
            return;
        }
        fields = PdfCheckersUtil.getFormFields(fields);
        for (PdfObject field : fields) {
            PdfDictionary fieldDic = (PdfDictionary) field;
            checkWidget(fieldDic);
            checkTextField(fieldDic);
        }
    }

    /**
     * Checks "Form" structure element.
     *
     * @param elem structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect tag structure for {@code Form} tag
     */
    public void checkFormStructElement(IStructureNode elem) {
        if (isWidget(elem)) {
            String role = context.resolveToStandardRole(elem.getParent());
            if (!StandardRoles.ARTIFACT.equals(role) && !StandardRoles.FORM.equals(role)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
            }
            if (StandardRoles.FORM.equals(role)) {
                PdfDictionary widget = ((PdfObjRef) elem).getReferencedObject();
                PdfArray rect = widget.getAsArray(PdfName.Rect);
                if (rect != null && rect.size() == 4) {
                    Rectangle rectangle = rect.toRectangle();
                    if (rectangle.getWidth() == 0 && rectangle.getHeight() == 0) {
                        throw new PdfUAConformanceException(
                                PdfUAExceptionMessageConstants.WIDGET_WITH_ZERO_HEIGHT_SHALL_BE_AN_ARTIFACT);
                    }
                }
            }
            return;
        }

        PdfStructElem form = context.getElementIfRoleMatches(PdfName.Form, elem);
        if (form == null) {
            return;
        }
        checkWidgetKids(form);
    }

    /**
     * Checks that {@code Form} structure element contains at most one widget annotation.
     *
     * @param form {@code Form} structure element to check
     *
     * @throws PdfUAConformanceException if {@code Form} tag has incorrect kids
     */
    private static void checkWidgetKids(IStructureNode form) {
        List<IStructureNode> kids = form.getKids();
        boolean widgetFound = false;
        for (IStructureNode node : kids) {
            if (isWidget(node)) {
                if (widgetFound) {
                    throw new PdfUAConformanceException(
                            PdfUAExceptionMessageConstants.FORM_STRUCT_ELEM_SHALL_CONTAIN_AT_MOST_ONE_WIDGET);
                }
                widgetFound = true;
            }
        }
    }

    private static boolean isWidget(IStructureNode node) {
        return node instanceof PdfObjRef &&
                PdfFormAnnotationUtil.isPureWidgetOrMergedField(((PdfObjRef) node).getReferencedObject());
    }

    private static PdfObject getValueFromParent(PdfDictionary field) {
        PdfDictionary parent = field.getAsDictionary(PdfName.Parent);
        PdfObject fieldValue = field.get(PdfName.V);
        if (parent != null) {
            fieldValue = parent.get(PdfName.V);
            if (fieldValue == null) {
                fieldValue = getValueFromParent(parent);
            }
        }
        return fieldValue;
    }

    private static void checkTextField(PdfDictionary fieldDic) {
        if (PdfName.Tx.equals(PdfFormField.getFormType(fieldDic)) && fieldDic.containsKey(PdfName.RV)) {
            String richText = PdfUA2AnnotationChecker.getRichTextStringValue(fieldDic.get(PdfName.RV));
            if (richText.isEmpty()) {
                return;
            }
            PdfObject fieldValue = fieldDic.get(PdfName.V);
            if (fieldValue == null) {
                fieldValue = getValueFromParent(fieldDic);
            }
            String value = PdfFormField.getStringValue(fieldValue);
            if (!richText.equals(value)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.TEXT_FIELD_V_AND_RV_SHALL_BE_TEXTUALLY_EQUIVALENT);
            }
        }
    }

    private void checkWidget(PdfDictionary fieldDic) {
        if (!PdfFormAnnotationUtil.isPureWidgetOrMergedField(fieldDic)) {
            return;
        }
        PdfObjRef objRef = null;
        if (fieldDic.getAsNumber(PdfName.StructParent) != null) {
            int i = fieldDic.getAsNumber(PdfName.StructParent).intValue();
            PdfDictionary pageDict = fieldDic.getAsDictionary(PdfName.P);
            objRef = context.findObjRefByStructParentIndex(i, pageDict);
        }
        if (objRef != null) {
            String role = context.resolveToStandardRole(objRef.getParent());
            if (!StandardRoles.ARTIFACT.equals(role) && !StandardRoles.FORM.equals(role)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.WIDGET_SHALL_BE_FORM_OR_ARTIFACT);
            }
        }
        if ((objRef == null || !isWidgetLabelPresent(objRef)) && !fieldDic.containsKey(PdfName.Contents)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.WIDGET_SHALL_PROVIDE_LABEL_OR_CONTENTS);
        }
        if (fieldDic.containsKey(PdfName.AA) && !fieldDic.containsKey(PdfName.Contents)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.WIDGET_WITH_AA_SHALL_PROVIDE_CONTENTS);
        }
    }

    private boolean isWidgetLabelPresent(IStructureNode widget) {
        PdfStructElem form = context.getElementIfRoleMatches(PdfName.Form, widget.getParent());
        if (form == null) {
            return false;
        }
        List<IStructureNode> kids = form.getKids();
        for (IStructureNode node : kids) {
            if (StandardRoles.LBL.equals(context.resolveToStandardRole(node))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handler for checking form field elements in the tag tree.
     */
    public static class PdfUA2FormTagHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2FormChecker pdfUA2FormChecker;

        /**
         * Creates a new {@link PdfUA2FormChecker.PdfUA2FormTagHandler} instance.
         *
         * @param context the validation context
         */
        public PdfUA2FormTagHandler(PdfUAValidationContext context) {
            super(context);
            this.pdfUA2FormChecker = new PdfUA2FormChecker(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            pdfUA2FormChecker.checkFormStructElement(elem);
        }
    }
}
