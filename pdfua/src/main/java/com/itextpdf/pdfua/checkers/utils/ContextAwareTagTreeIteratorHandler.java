package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;

/**
 * Class that holds the validation context while iterating the tag tree structure.
 */
public abstract class ContextAwareTagTreeIteratorHandler implements ITagTreeIteratorHandler {

    protected final PdfUAValidationContext context;

    /**
     * Creates a new instance of the {@link ContextAwareTagTreeIteratorHandler}.
     *
     * @param context The validation context.
     */
    protected ContextAwareTagTreeIteratorHandler(PdfUAValidationContext context) {
        this.context = context;
    }
}
