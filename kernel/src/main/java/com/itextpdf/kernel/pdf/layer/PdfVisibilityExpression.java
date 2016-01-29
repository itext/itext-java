package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

/**
 * An array specifying a visibility expression, used to compute visibility
 * of content based on a set of optional content groups.
 */
public class PdfVisibilityExpression extends PdfObjectWrapper<PdfArray> {

    /**
     * Constructs a new PdfVisibilityExpression instance by its raw PdfArray.
     * @param visibilityExpressionArray the array representing the visibility expression
     * @throws PdfException
     */
    public PdfVisibilityExpression(PdfArray visibilityExpressionArray) {
        super(visibilityExpressionArray);
        PdfName operator = visibilityExpressionArray.getAsName(0);
        if (visibilityExpressionArray.size() < 1 || !PdfName.Or.equals(operator)
                && !PdfName.And.equals(operator) && !PdfName.Not.equals(operator)) {
            throw new IllegalArgumentException("Invalid visibilityExpressionArray");
        }
    }

    /**
     * Creates a visibility expression.
     * @param operator should be either PdfName#And, PdfName#Or, or PdfName#Not
     */
    public PdfVisibilityExpression(PdfName operator) {
        super(new PdfArray());
        if (operator == null || !PdfName.Or.equals(operator) && !PdfName.And.equals(operator) && !PdfName.Not.equals(operator))
            throw new IllegalArgumentException("Invalid operator");
        getPdfObject().add(operator);
    }

    /**
     * Adds a new operand to the current visibility expression.
     * @param layer the layer operand to be added.
     */
    public void addOperand(PdfLayer layer) {
        getPdfObject().add(layer.getPdfObject());
        getPdfObject().setModified();
    }

    /**
     * Adds a new opeand to the current visibility expression.
     * @param expression the PdfVisibilityExpression instance operand to be added
     */
    public void addOperand(PdfVisibilityExpression expression) {
        getPdfObject().add(expression.getPdfObject());
        getPdfObject().setModified();
    }

}
