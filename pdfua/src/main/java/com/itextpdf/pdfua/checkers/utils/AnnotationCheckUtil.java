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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Class that provides methods for checking PDF/UA compliance of annotations.
 */
public final class AnnotationCheckUtil {
    private AnnotationCheckUtil() {
        // Empty constructor.
    }

    /**
     * Is annotation visible: {@code true} if hidden flag isn't
     * set and annotation intersects CropBox (default value is MediaBox).
     *
     * @param annotDict annotation to check
     *
     * @return {@code true} if annotation should be checked, otherwise {@code false}
     */
    public static boolean isAnnotationVisible(PdfDictionary annotDict) {
        if (annotDict.getAsNumber(PdfName.F) != null) {
            int flags = annotDict.getAsNumber(PdfName.F).intValue();
            if ((flags & PdfAnnotation.HIDDEN) != 0) {
                return false;
            }
        }
        if (annotDict.getAsDictionary(PdfName.P) != null) {
            PdfDictionary page = annotDict.getAsDictionary(PdfName.P);
            PdfArray pageBox = page.getAsArray(PdfName.CropBox) == null ? page.getAsArray(PdfName.MediaBox) : page.getAsArray(PdfName.CropBox);
            if (pageBox != null && annotDict.getAsArray(PdfName.Rect) != null) {
                PdfArray annotBox = annotDict.getAsArray(PdfName.Rect);
                try {
                    if (pageBox.toRectangle().getIntersection(annotBox.toRectangle()) == null) {
                        return false;
                    }
                } catch (PdfException ignore) {
                    // ignore
                }
            }
        }
        return true;
    }

    /**
     * Helper class that checks the conformance of annotations while iterating the tag tree structure.
     */
    public static class AnnotationHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new instance of the {@link AnnotationCheckUtil.AnnotationHandler}.
         *
         * @param context The validation context.
         */
        public AnnotationHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            if (!(elem instanceof PdfObjRef)) {
                return;
            }
            PdfObjRef objRef = (PdfObjRef) elem;
            PdfDictionary annotObj = objRef.getReferencedObject();
            if (annotObj == null) {
                return;
            }

            if (annotObj.getAsDictionary(PdfName.P) != null) {
                PdfDictionary pageDict = annotObj.getAsDictionary(PdfName.P);
                if (!PdfName.S.equals(pageDict.getAsName(PdfName.Tabs))) {
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_S);
                }
            }

            PdfName subtype = annotObj.getAsName(PdfName.Subtype);

            if (!isAnnotationVisible(annotObj) || PdfName.Popup.equals(subtype)) {
                return;
            }

            if (PdfName.PrinterMark.equals(subtype)) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PRINTER_MARK_IS_NOT_PERMITTED);
            }

            if (PdfName.TrapNet.equals(subtype)) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.ANNOT_TRAP_NET_IS_NOT_PERMITTED);
            }

            if (!PdfName.Widget.equals(subtype) && !(annotObj.containsKey(PdfName.Contents)
                    || annotObj.containsKey(PdfName.Alt))) {
                throw new PdfUAConformanceException(MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_OR_ALT_KEY, subtype.getValue()));
            }

            if (PdfName.Link.equals(subtype)) {
                PdfStructElem parentLink = context.getElementIfRoleMatches(PdfName.Link, objRef.getParent());
                if (parentLink == null) {
                    throw new PdfUAConformanceException(
                            PdfUAExceptionMessageConstants.LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK);
                }
                if (!annotObj.containsKey(PdfName.Contents)) {
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.LINK_ANNOTATION_SHOULD_HAVE_CONTENTS_KEY);
                }
            }

            if (PdfName.Screen.equals(subtype)) {
                PdfDictionary action = annotObj.getAsDictionary(PdfName.A);
                PdfDictionary additionalActions = annotObj.getAsDictionary(PdfName.AA);
                ActionCheckUtil.checkAction(action);
                checkAAEntry(additionalActions);
            }
        }

        private static void checkAAEntry(PdfDictionary additionalActions) {
            if (additionalActions != null) {
                for (PdfObject val : additionalActions.values()) {
                    if (val instanceof PdfDictionary) {
                        ActionCheckUtil.checkAction((PdfDictionary) val);
                    }
                }
            }
        }
    }
}
