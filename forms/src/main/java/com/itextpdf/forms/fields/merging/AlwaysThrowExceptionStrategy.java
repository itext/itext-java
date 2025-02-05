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
