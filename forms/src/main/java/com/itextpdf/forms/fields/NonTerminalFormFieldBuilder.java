/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Builder for non-terminal form field.
 */
public class NonTerminalFormFieldBuilder extends FormFieldBuilder<NonTerminalFormFieldBuilder> {

    /**
     * Creates builder for non-terminal {@link PdfFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public NonTerminalFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Creates non-terminal form field based on provided parameters.
     *
     * @return new {@link PdfFormField} instance
     */
    public PdfFormField createNonTerminalFormField() {
        PdfFormField field = PdfFormCreator.createFormField(getDocument());
        field.pdfConformanceLevel = getConformanceLevel();
        field.setFieldName(getFormFieldName());
        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NonTerminalFormFieldBuilder getThis() {
        return this;
    }
}
