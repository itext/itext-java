/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for radio form field.
 */
public class RadioFormFieldBuilder extends TerminalFormFieldBuilder<RadioFormFieldBuilder> {

    /**
     * Creates builder for radio form field creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public RadioFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    // TODO DEVSIX-6319 Remove this constructor when radio buttons will become widgets instead of form fields.
    /**
     * Creates builder for radio button creation.
     *
     * @param document document to be used for form field creation
     */
    public RadioFormFieldBuilder(PdfDocument document) {
        super(document, null);
    }

    /**
     * Creates radio group form field instance based on provided parameters.
     *
     * @return new {@link PdfButtonFormField} instance
     */
    public PdfButtonFormField createRadioGroup() {
        PdfButtonFormField radio = new PdfButtonFormField(getDocument());
        radio.updateFontAndFontSize(getDocument().getDefaultFont(), PdfFormField.DEFAULT_FONT_SIZE);
        radio.pdfAConformanceLevel = getConformanceLevel();
        radio.setFieldName(getFormFieldName());
        radio.setFieldFlags(PdfButtonFormField.FF_RADIO);
        return radio;
    }

    /**
     * Creates radio button form field instance based on provided parameters.
     *
     * @param radioGroup radio group to which new radio button will be added
     * @param appearanceName name of the "on" appearance state.
     * @return new radio button instance
     */
    public PdfFormField createRadioButton(PdfButtonFormField radioGroup, String appearanceName) {
        PdfFormField radio;
        if (getWidgetRectangle() == null) {
            radio = new PdfButtonFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (null != getConformanceLevel()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            PdfObject radioGroupValue = radioGroup.getValue();
            PdfName appearanceState = new PdfName(appearanceName);
            if (appearanceState.equals(radioGroupValue)) {
                annotation.setAppearanceState(appearanceState);
            } else {
                annotation.setAppearanceState(new PdfName(PdfFormField.OFF_STATE_VALUE));
            }
            radio = new PdfButtonFormField(annotation, getDocument());
        }
        radio.pdfAConformanceLevel = getConformanceLevel();

        radio.updateFontAndFontSize(getDocument().getDefaultFont(), PdfFormField.DEFAULT_FONT_SIZE);
        if (getWidgetRectangle() != null) {
            radio.drawRadioAppearance(
                    getWidgetRectangle().getWidth(), getWidgetRectangle().getHeight(), appearanceName);
            setPageToField(radio);
        }

        radioGroup.addKid(radio);
        return radio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RadioFormFieldBuilder getThis() {
        return this;
    }
}
