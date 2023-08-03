package com.itextpdf.forms.fields.merging;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.exceptions.PdfException;

/**
 * A {@link OnDuplicateFormFieldNameStrategy} implementation that throws an exception if the second field has the same
 * name as the first field.
 */
public class AlwaysThrowExceptionStrategy implements OnDuplicateFormFieldNameStrategy {

    /**
     * Creates an instance of {@link AlwaysThrowExceptionStrategy}
     */
    public AlwaysThrowExceptionStrategy() {
        //Empty constructor
    }

    /**
     * executes the strategy.
     *
     * @param firstField            the first field
     * @param secondField           the second field
     * @param throwExceptionOnError if true, an exception will be thrown
     *
     * @return true if the second field was renamed successfully, false otherwise
     */
    @Override
    public boolean execute(PdfFormField firstField, PdfFormField secondField, boolean throwExceptionOnError) {
        throw new PdfException(MessageFormatUtil.format(
                FormsExceptionMessageConstant.FIELD_NAME_ALREADY_EXISTS_IN_FORM,
                firstField.getFieldName().toUnicodeString()));
    }
}
