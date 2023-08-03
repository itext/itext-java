package com.itextpdf.forms.fields.merging;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.itextpdf.forms.fields.PdfFormFieldMergeUtil.mergeFormFields;

/**
 * A {@link OnDuplicateFormFieldNameStrategy} implementation that merges the second field into the first field if the
 * second field has the same name as the first field.
 * This strategy is used by default.
 */
public class MergeFieldsStrategy implements OnDuplicateFormFieldNameStrategy {


    /**
     * Creates an instance of  {@link MergeFieldsStrategy}
     */
    public MergeFieldsStrategy() {
        // Empty constructor
    }

    /**
     * executes the strategy.
     *
     * @param firstField            the first field
     * @param secondField           the second field
     * @param throwExceptionOnError if true, an exception will be thrown
     *
     * @return true if the second field was merged successfully, false otherwise
     */
    @Override
    public boolean execute(PdfFormField firstField, PdfFormField secondField, boolean throwExceptionOnError) {
        PdfName firstFieldFormType = firstField.getFormType();
        PdfObject firstFieldValue = firstField.getValue();
        PdfObject secondFieldValue = secondField.getValue();
        PdfObject firstFieldDefaultValue = firstField.getDefaultValue();
        PdfObject secondFieldDefaultValue = secondField.getDefaultValue();
        if ((firstFieldFormType == null || firstFieldFormType.equals(secondField.getFormType())) &&
                (firstFieldValue == null || secondFieldValue == null || firstFieldValue.equals(secondFieldValue)) &&
                (firstFieldDefaultValue == null || secondFieldDefaultValue == null ||
                        firstFieldDefaultValue.equals(secondFieldDefaultValue))) {
            mergeFormFields(firstField, secondField, throwExceptionOnError);
        } else {
            if (throwExceptionOnError) {
                throw new PdfException(MessageFormatUtil.format(FormsExceptionMessageConstant.CANNOT_MERGE_FORMFIELDS,
                        firstField.getPartialFieldName()));
            } else {
                Logger logger = LoggerFactory.getLogger(MergeFieldsStrategy.class);
                logger.warn(MessageFormatUtil.format(FormsLogMessageConstants.CANNOT_MERGE_FORMFIELDS,
                        firstField.getPartialFieldName()));
                return false;
            }
        }
        return true;
    }
}
