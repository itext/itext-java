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
