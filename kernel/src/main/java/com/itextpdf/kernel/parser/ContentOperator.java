package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfObject;

import java.util.List;

/**
 * Root interface for a series of handlers for content stream operators.
 */
public interface ContentOperator {

    /**
     * Called when a content operator should be processed.
     * @param processor	The processor that is dealing with the PDF content stream.
     * @param operator The literal PDF syntax of the operator.
     * @param operands The operands that come with the operator.
     */
    void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands);
}
