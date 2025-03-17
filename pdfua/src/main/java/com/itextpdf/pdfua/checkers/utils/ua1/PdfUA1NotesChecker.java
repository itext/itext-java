package com.itextpdf.pdfua.checkers.utils.ua1;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class for delegating notes checks to the correct checking logic.
 */
public final class PdfUA1NotesChecker {

    private PdfUA1NotesChecker() {
        // Empty constructor.
    }

    /**
     * Handler for checking Note elements in the TagTree.
     */
    public static class PdfUA1NotesTagHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new {@link  PdfUA1NotesChecker.PdfUA1NotesTagHandler} instance.
         *
         * @param context The validation context.
         */
        public PdfUA1NotesTagHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            final PdfStructElem structElem = context.getElementIfRoleMatches(PdfName.Note, elem);
            if (structElem == null) {
                return;
            }
            final PdfDictionary pdfObject = structElem.getPdfObject();
            if (pdfObject.get(PdfName.ID) == null) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY);
            }
        }
    }
}
