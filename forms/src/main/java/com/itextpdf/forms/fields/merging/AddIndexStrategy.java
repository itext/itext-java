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

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.PdfFormField;

import java.util.HashMap;

/**
 * A {@link OnDuplicateFormFieldNameStrategy} implementation that adds an index to the field name of the second field
 */
public class AddIndexStrategy implements OnDuplicateFormFieldNameStrategy {

    private static final String DEFAULT_SEPARATOR = "_";
    private final String separator;
    private final HashMap<String, Integer> countMap = new HashMap<>();
    private final String regexString;

    /**
     * Creates a new {@link AddIndexStrategy} instance.
     *
     * @param separator the separator that will be used to separate the original field name and the index
     */
    public AddIndexStrategy(String separator) {
        if (separator == null || separator.contains(".")) {
            throw new IllegalArgumentException(FormsExceptionMessageConstant.SEPARATOR_SHOULD_BE_A_VALID_VALUE);
        }
        this.separator = separator;
        this.regexString = separator + "[0-9]+$";
    }


    /**
     * Creates a new {@link AddIndexStrategy} instance.
     * The default separator will be used to separate the original field name and the index.
     * the default separator is {@value #DEFAULT_SEPARATOR}
     */
    public AddIndexStrategy() {
        this(DEFAULT_SEPARATOR);
    }


    /**
     * Renames the second field by adding an index to its name.
     *
     * @param firstField            the first field
     * @param secondField           the second field
     * @param throwExceptionOnError if true, an exception will be thrown
     *
     * @return true if the second field was renamed successfully, false otherwise
     */
    @Override
    public boolean execute(PdfFormField firstField, PdfFormField secondField, boolean throwExceptionOnError) {
        if (firstField == null || secondField == null) {
            return false;
        }
        if (firstField.getFieldName() == null || secondField.getFieldName() == null) {
            return true;
        }
        String originalFieldName = firstField.getFieldName().toUnicodeString();
        String fieldToAddNewName = originalFieldName + separator + getNextIndex(originalFieldName);
        secondField.setFieldName(fieldToAddNewName);
        return true;
    }

    int getNextIndex(String name) {
        String normalizedName = name.replaceAll(this.regexString, "");
        Integer count = countMap.get(normalizedName);
        if (count == null) {
            count = 0;
        }
        count++;
        countMap.put(normalizedName, count);
        return (int) count;
    }
}
