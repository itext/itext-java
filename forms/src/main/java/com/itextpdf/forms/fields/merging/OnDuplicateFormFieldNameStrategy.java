package com.itextpdf.forms.fields.merging;

import com.itextpdf.forms.fields.PdfFormField;

/**
 * A {@link OnDuplicateFormFieldNameStrategy} implementation that throws an exception if the second field has the same
 * name as the first field.
 */
public interface OnDuplicateFormFieldNameStrategy {

    /**
     * executes the strategy.
     *
     * @param firstField            the first field
     * @param secondField           the second field
     * @param throwExceptionOnError if true, an exception will be thrown
     *
     * @return true if the second field was renamed successfully, false otherwise
     */
    boolean execute(PdfFormField firstField, PdfFormField secondField, boolean throwExceptionOnError);
}


