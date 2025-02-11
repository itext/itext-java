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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for push button form field.
 */
public class PushButtonFormFieldBuilder extends TerminalFormFieldBuilder<PushButtonFormFieldBuilder> {

    private String caption = "";

    /**
     * Creates builder for {@link PdfButtonFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public PushButtonFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets caption for button form field creation.
     *
     * @return caption value to be used for form field creation
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets caption for button form field creation.
     *
     * @param caption caption value to be used for form field creation
     * @return this builder
     */
    public PushButtonFormFieldBuilder setCaption(String caption) {
        this.caption = caption;
        return getThis();
    }

    /**
     * Creates push button form field base on provided parameters.
     *
     * @return new {@link PdfButtonFormField} instance
     */
    public PdfButtonFormField createPushButton() {
        PdfButtonFormField field;
        PdfWidgetAnnotation annotation = null;
        if (getWidgetRectangle() == null) {
            field = PdfFormCreator.createButtonFormField(getDocument());
        } else {
            annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            field = PdfFormCreator.createButtonFormField(annotation, getDocument());
            if (null != getConformance() && getConformance().isPdfAOrUa()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
        }
        field.disableFieldRegeneration();
        if (this.getFont() != null) {
            field.setFont(this.getFont());
        }
        field.pdfConformance = getConformance();
        field.setPushButton(true);
        field.setFieldName(getFormFieldName());
        field.text = caption;
        if (annotation != null) {
            field.getFirstFormAnnotation().backgroundColor = ColorConstants.LIGHT_GRAY;
            PdfDictionary mk = new PdfDictionary();
            mk.put(PdfName.CA, new PdfString(caption));
            mk.put(PdfName.BG, new PdfArray(field.getFirstFormAnnotation().backgroundColor.getColorValue()));
            annotation.setAppearanceCharacteristics(mk);
            setPageToField(field);
        }
        field.enableFieldRegeneration();

        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PushButtonFormFieldBuilder getThis() {
        return this;
    }
}
