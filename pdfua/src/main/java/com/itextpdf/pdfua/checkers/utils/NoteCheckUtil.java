package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class for delegating notes checks to the correct checking logic.
 */
public class NoteCheckUtil {
    /**
     * Handler for checking Note elements in the TagTree.
     */
    public static class NoteTagHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new {@link  NoteCheckUtil.NoteTagHandler} instance.
         * @param context The validation context.
         */
        public NoteTagHandler(PdfUAValidationContext context) {
            super(context);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void nextElement(IStructureNode elem) {
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
