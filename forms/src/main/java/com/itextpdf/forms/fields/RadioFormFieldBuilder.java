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

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for radio form field.
 */
public class RadioFormFieldBuilder extends TerminalFormFieldBuilder<RadioFormFieldBuilder> {

    /**
     * Creates builder for radio form field creation.
     *
     * @param document      document to be used for form field creation
     * @param radioGroupFormFieldName name of the form field
     */
    public RadioFormFieldBuilder(PdfDocument document, String radioGroupFormFieldName) {
        super(document, radioGroupFormFieldName);
    }


    /**
     * Creates radio group form field instance based on provided parameters.
     *
     * @return new {@link PdfButtonFormField} instance
     */
    public PdfButtonFormField createRadioGroup() {
        PdfButtonFormField radioGroup = PdfFormCreator.createButtonFormField(getDocument());
        radioGroup.disableFieldRegeneration();
        radioGroup.pdfConformance = getConformance();
        radioGroup.setFieldName(getFormFieldName());
        radioGroup.setFieldFlags(PdfButtonFormField.FF_RADIO);
        radioGroup.enableFieldRegeneration();
        return radioGroup;
    }

    /**
     * Creates radio button form field instance based on provided parameters.
     *
     * @param appearanceName name of the "on" appearance state.
     * @param rectangle the place where the widget should be placed.
     *
     * @return new radio button instance
     */
    public PdfFormAnnotation createRadioButton(String appearanceName, Rectangle rectangle) {
        if (appearanceName == null || appearanceName.isEmpty()) {
            throw new PdfException(FormsExceptionMessageConstant.APEARANCE_NAME_MUST_BE_PROVIDED);
        }
        Rectangle widgetRectangle = getWidgetRectangle();
        if (rectangle != null) {
            widgetRectangle = rectangle;
        }
        if (widgetRectangle == null) {
            throw new PdfException(FormsExceptionMessageConstant.WIDGET_RECTANGLE_MUST_BE_PROVIDED);
        }

        final PdfName appearancePdfName = new PdfName(appearanceName);
        final PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(widgetRectangle);
        annotation.setAppearanceState(appearancePdfName);
        if (getConformance() != null && getConformance().isPdfAOrUa()) {
            annotation.setFlag(PdfAnnotation.PRINT);
        }
        PdfFormAnnotation radio = PdfFormCreator.createFormAnnotation(annotation, getDocument());
        setPageToField(radio);
        radio.pdfConformance = getConformance();
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
