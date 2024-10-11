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
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for signature form field.
 */
public class SignatureFormFieldBuilder extends TerminalFormFieldBuilder<SignatureFormFieldBuilder> {

    /**
     * Creates builder for {@link PdfSignatureFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public SignatureFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Creates signature form field based on provided parameters.
     *
     * @return new {@link PdfSignatureFormField} instance.
     */
    public PdfSignatureFormField createSignature() {
        PdfSignatureFormField signatureFormField;
        if (getWidgetRectangle() == null) {
            signatureFormField = PdfFormCreator.createSignatureFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (getConformance() != null && getConformance().isPdfAOrUa()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            signatureFormField = PdfFormCreator.createSignatureFormField(annotation, getDocument());
            setPageToField(signatureFormField);
        }
        // we can't use setFont() here, because the signature values can only be created one time on first
        // appearance generation, so we avoid the generation call until the moment we have all the necessary data
        if (getFont() != null) {
            signatureFormField.font = getFont();
        }
        signatureFormField.pdfConformance = getConformance();
        signatureFormField.setFieldName(getFormFieldName());
        return signatureFormField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SignatureFormFieldBuilder getThis() {
        return this;
    }
}
