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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.util.XmlUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that provides methods for checking PDF/UA-2 compliance of annotations.
 */
public final class PdfUA2AnnotationChecker {
    private static final Set<PdfName> markupAnnotationTypes = new HashSet<>(Arrays.asList(
            PdfName.Text, PdfName.FreeText,
            PdfName.Line, PdfName.Square, PdfName.Circle, PdfName.Polygon, PdfName.PolyLine,
            PdfName.Highlight, PdfName.Underline, PdfName.Squiggly, PdfName.StrikeOut,
            PdfName.Caret, PdfName.Stamp, PdfName.Ink, PdfName.FileAttachment, PdfName.Redaction, PdfName.Projection));

    /**
     * Creates a new instance of the {@link PdfUA2AnnotationChecker}.
     */
    private PdfUA2AnnotationChecker() {
        // Empty constructor.
    }

    /**
     * Checks PDF/UA-2 compliance of the annotations.
     *
     * @param pdfDocument {@link PdfDocument} to check annotations for
     */
    public static void checkAnnotations(PdfDocument pdfDocument) {
        int amountOfPages = pdfDocument.getNumberOfPages();
        for (int i = 1; i <= amountOfPages; ++i) {
            PdfPage page = pdfDocument.getPage(i);
            List<PdfAnnotation> annotations = page.getAnnotations();
            if (!annotations.isEmpty()) {
                // PDF/UA-2 8.9.3.3 Tab order
                PdfName tabs = page.getTabOrder();
                if (!(PdfName.A.equals(tabs) || PdfName.W.equals(tabs) || PdfName.S.equals(tabs))) {
                    throw new PdfUAConformanceException(
                            PdfUAExceptionMessageConstants.PAGE_WITH_ANNOT_DOES_NOT_HAVE_TABS_WITH_VALID_CONTENT);
                }
            }
            for (final PdfAnnotation annot : annotations) {
                // Check annotations that are not tagged here, other annotations will be checked in the structure tree.
                if (!annot.getPdfObject().containsKey(PdfName.StructParent)) {
                    checkAnnotation(annot.getPdfObject(), (PdfStructElem) null);
                }
            }
        }
    }

    /**
     * Checks PDF/UA-2 compliance of the annotation.
     *
     * @param annotation the annotation dictionary to check
     * @param context    {@link PdfUAValidationContext} used to find the structure node enclosing the annotation
     *                   using its {@code StructParent} value
     */
    public static void checkAnnotation(PdfDictionary annotation, PdfUAValidationContext context) {
        PdfStructElem parent = null;
        if (annotation.getAsNumber(PdfName.StructParent) != null) {
            int structParentIndex = annotation.getAsNumber(PdfName.StructParent).intValue();
            PdfDictionary pageDict = annotation.getAsDictionary(PdfName.P);
            PdfObjRef objRef = context.findObjRefByStructParentIndex(structParentIndex, pageDict);
            if (objRef != null) {
                parent = (PdfStructElem) objRef.getParent();
            }
        }
        checkAnnotation(annotation, parent);
    }

    /**
     * Checks PDF/UA-2 compliance of the annotation.
     *
     * @param annotation the annotation dictionary to check
     * @param parent     the parent structure element
     */
    static void checkAnnotation(PdfDictionary annotation, PdfStructElem parent) {
        if (parent != null) {
            PdfString alt = parent.getAlt();
            PdfString contents = annotation.getAsString(PdfName.Contents);
            if (alt != null && contents != null && !alt.getValue().equals(contents.getValue())) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.CONTENTS_AND_ALT_SHALL_BE_IDENTICAL);
            }
        }

        PdfName parentRole = parent == null ? PdfName.Artifact : parent.getRole();

        if (!PdfName.Artifact.equals(parentRole)) {
            checkAnnotationFlags(annotation);
        }

        PdfName subtype = annotation.getAsName(PdfName.Subtype);
        if (PdfName.Widget.equals(subtype)) {
            // Form field annotations are handled by PdfUA2FormChecker.
            return;
        }

        if (markupAnnotationTypes.contains(subtype)) {
            checkMarkupAnnotations(annotation, parentRole);
        }

        if (PdfName.Stamp.equals(subtype)) {
            PdfName name = annotation.getAsName(PdfName.Name);
            PdfObject contents = annotation.get(PdfName.Contents);
            if (name == null && contents == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.STAMP_ANNOT_SHALL_SPECIFY_NAME_OR_CONTENTS);
            }
        }

        if (PdfName.Ink.equals(subtype) || PdfName.Screen.equals(subtype) ||
                PdfName._3D.equals(subtype) || PdfName.RichMedia.equals(subtype)) {
            PdfString contents = annotation.getAsString(PdfName.Contents);
            if (contents == null || contents.getValue().isEmpty()) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY);
            }
        }

        if (PdfName.Popup.equals(subtype) && parent != null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.POPUP_ANNOTATIONS_ARE_NOT_ALLOWED);
        }

        if (PdfName.FileAttachment.equals(subtype)) {
            // File specifications can be a string or a dictionary. Using the string form is equivalent
            // to the AFRelationship entry having the value of Unspecified.
            PdfDictionary fileSpec = annotation.getAsDictionary(PdfName.FS);
            if (fileSpec != null && !fileSpec.containsKey(PdfName.AFRelationship)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.FILE_SPEC_SHALL_CONTAIN_AFRELATIONSHIP);
            }
        }

        if (PdfName.Sound.equals(subtype) || PdfName.Movie.equals(subtype) || PdfName.TrapNet.equals(subtype)) {
            throw new PdfUAConformanceException(MessageFormatUtil.format(
                    PdfUAExceptionMessageConstants.DEPRECATED_ANNOTATIONS_ARE_NOT_ALLOWED, subtype.getValue()));
        }

        if (PdfName.PrinterMark.equals(subtype) && !PdfName.Artifact.equals(parentRole)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PRINTER_MARK_SHALL_BE_AN_ARTIFACT);
        }

        if (PdfName.Watermark.equals(subtype) && !PdfName.Artifact.equals(parentRole)) {
            checkMarkupAnnotations(annotation, parentRole);
        }
    }

    /**
     * Checks the PDF/UA-2 8.9.2.3 Markup annotations requirements.
     *
     * @param annotation the markup annotations
     * @param parentRole the parent role
     */
    private static void checkMarkupAnnotations(PdfDictionary annotation, PdfName parentRole) {
        if (!PdfName.Annot.equals(parentRole)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT);
        }
        PdfString contents = annotation.getAsString(PdfName.Contents);
        if (contents == null) {
            return;
        }
        String rc = PdfUA2AnnotationChecker.getRichTextStringValue(annotation.get(PdfName.RC));
        if (!rc.isEmpty() && !rc.equals(contents.getValue())) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.RC_DIFFERENT_FROM_CONTENTS);
        }
    }

    static String getRichTextStringValue(PdfObject rv) {
        String richText = PdfFormField.getStringValue(rv);
        if (richText.isEmpty()) {
            return richText;
        }
        try {
            return parseRichText(XmlUtil.initXmlDocument(new ByteArrayInputStream(
                    richText.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            throw new PdfException(e.getMessage(), e);
        }
    }

    private static String parseRichText(Node node) {
        StringBuilder richText = new StringBuilder();
        NodeList allChildren = node.getChildNodes();
        for (int k = 0; k < allChildren.getLength(); ++k) {
            Node child = allChildren.item(k);
            richText.append(child.getNodeValue() == null ? parseRichText(child) : child.getNodeValue());
        }
        return richText.toString();
    }

    private static void checkAnnotationFlags(PdfDictionary annotation) {
        PdfNumber f = annotation.getAsNumber(PdfName.F);
        if (f == null) {
            return;
        }
        int flags = f.intValue();
        if (PdfCheckersUtil.checkFlag(flags, PdfAnnotation.INVISIBLE)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.INVISIBLE_ANNOT_SHALL_BE_AN_ARTIFACT);
        }
        if (PdfCheckersUtil.checkFlag(flags, PdfAnnotation.NO_VIEW) &&
                !PdfCheckersUtil.checkFlag(flags, PdfAnnotation.TOGGLE_NO_VIEW)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.NO_VIEW_ANNOT_SHALL_BE_AN_ARTIFACT);
        }
    }

    /**
     * Handler for checking annotation elements in the tag tree.
     */
    public static class PdfUA2AnnotationHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new instance of the {@link PdfUA2AnnotationHandler}.
         *
         * @param context the validation context
         */
        public PdfUA2AnnotationHandler(PdfUAValidationContext context) {
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
            PdfDictionary annotObj = ((PdfObjRef) elem).getReferencedObject();
            if (annotObj == null || !annotObj.containsKey(PdfName.Subtype)) {
                return;
            }
            PdfStructElem parent = (PdfStructElem) elem.getParent();
            checkAnnotation(annotObj, parent);
        }
    }
}
