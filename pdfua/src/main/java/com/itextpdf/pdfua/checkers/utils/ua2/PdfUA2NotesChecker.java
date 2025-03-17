package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class which performs Note and FENote checks according to PDF/UA-2 specification.
 */
public final class PdfUA2NotesChecker {
    private final PdfUAValidationContext context;

    private PdfUA2NotesChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if Note and FENote elements are correct according to PDF/UA-2 specification.
     *
     * @param elem list structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect tag structure for list
     */
    public void checkStructElement(IStructureNode elem) {
        final String role = context.resolveToStandardRole(elem);
        if (role == null) {
            return;
        }

        if (StandardRoles.NOTE.equals(role)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_USES_NOTE_TAG);
        }
        PdfStructElem noteStructElem = context.getElementIfRoleMatches(PdfName.FENote, elem);
        if (noteStructElem == null) {
            if (elem instanceof PdfStructElem) {
                PdfStructElem structElem = (PdfStructElem) elem;
                if (!structElem.getRefsList().stream()
                        .filter(reference -> StandardRoles.FENOTE.equals(context.resolveToStandardRole(reference)))
                        .allMatch(reference -> reference.getRefsList().stream().anyMatch(
                                innerRef -> innerRef.getPdfObject().equals(structElem.getPdfObject())))) {
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.FE_NOTE_NOT_REFERENCING_CONTENT);
                }
            }
        } else {
            if (!noteStructElem.getRefsList().stream().allMatch(reference -> reference.getRefsList().stream().anyMatch(
                    innerRef -> innerRef.getPdfObject().equals(noteStructElem.getPdfObject())))) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.CONTENT_NOT_REFERENCING_FE_NOTE);
            }

            if (noteStructElem.getAttributesList().stream()
                    .map(attribute -> attribute.getAttributeAsEnum(PdfName.NoteType.getValue()))
                    .anyMatch(noteTypeValue -> noteTypeValue != null &&
                            !PdfName.Footnote.getValue().equals(noteTypeValue) &&
                            !PdfName.Endnote.getValue().equals(noteTypeValue) &&
                            !PdfName.None.getValue().equals(noteTypeValue))) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.INCORRECT_NOTE_TYPE_VALUE);
            }
        }
    }

    /**
     * Handler class that checks Note and FENote tags while traversing the tag tree.
     */
    public static class PdfUA2NotesHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2NotesChecker checker;

        /**
         * Creates a new instance of {@link PdfUA2NotesHandler}.
         *
         * @param context the validation context
         */
        public PdfUA2NotesHandler(PdfUAValidationContext context) {
            super(context);
            checker = new PdfUA2NotesChecker(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            checker.checkStructElement(elem);
        }
    }
}
