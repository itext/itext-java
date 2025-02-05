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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for text form field.
 */
public class TextFormFieldBuilder extends TerminalFormFieldBuilder<TextFormFieldBuilder> {

    private static final String TEXT_FORM_FIELD_DEFAULT_VALUE = "";

    /**
     * Creates builder for {@link PdfTextFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public TextFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Creates text form field based on provided parameters.
     *
     * @return new {@link PdfTextFormField} instance
     */
    public PdfTextFormField createText() {
        return createText(false);
    }

    private PdfTextFormField createText(boolean multiline) {
        PdfTextFormField field;
        if (getWidgetRectangle() == null) {
            field = PdfFormCreator.createTextFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (null != getConformance() && getConformance().isPdfAOrUa()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            field = PdfFormCreator.createTextFormField(annotation, getDocument());
            setPageToField(field);
        }
        if (null != getFont()) {
            field.setFont(getFont());
        }
        field.disableFieldRegeneration();
        field.pdfConformance = getConformance();
        field.setMultiline(multiline);
        field.setFieldName(getFormFieldName());
        field.setValue(TEXT_FORM_FIELD_DEFAULT_VALUE);
        field.enableFieldRegeneration();

        return field;
    }

    /**
     * Creates multiline text form field based on provided parameters.
     *
     * @return new {@link PdfTextFormField} instance
     */
    public PdfTextFormField createMultilineText() {
        return createText(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TextFormFieldBuilder getThis() {
        return this;
    }
}
