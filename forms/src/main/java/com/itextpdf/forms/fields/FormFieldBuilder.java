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
package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Builder for form field.
 *
 * @param <T> specific form field builder which extends this class.
 */
public abstract class FormFieldBuilder<T extends FormFieldBuilder<T>> {

    /**
     * Document to be used for form field creation.
     */
    private final PdfDocument document;
    /**
     * Name of the form field.
     */
    private final String formFieldName;
    /**
     * Conformance of the form field.
     */
    private PdfConformance conformance = null;

    /**
     * Creates builder for {@link PdfFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    protected FormFieldBuilder(PdfDocument document, String formFieldName) {
        this.document = document;
        this.formFieldName = formFieldName;
        if (document != null) {
            this.conformance = document.getConformance();
        }
    }

    /**
     * Gets document to be used for form field creation.
     *
     * @return {@link PdfDocument} instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Gets name of the form field.
     *
     * @return name to be used for form field creation
     */
    public String getFormFieldName() {
        return formFieldName;
    }

    /**
     * Gets conformance for form field creation.
     *
     * @return instance of {@link PdfConformance} to be used for form field creation
     */
    public PdfConformance getConformance() {
        return conformance;
    }

    /**
     * Sets conformance for form field creation.
     *
     * @param conformance Instance of {@link PdfConformance} to be used for form field creation.
     *
     * @return this builder
     */
    public T setConformance(PdfConformance conformance) {
        this.conformance = conformance;
        return getThis();
    }

    /**
     * Returns this builder object. Required for superclass methods.
     *
     * @return this builder
     */
    protected abstract T getThis();
}
