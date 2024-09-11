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

import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for checkbox form field.
 */
public class CheckBoxFormFieldBuilder extends TerminalFormFieldBuilder<CheckBoxFormFieldBuilder> {

    private CheckBoxType checkType = CheckBoxType.CROSS;

    /**
     * Creates builder for {@link PdfButtonFormField} creation.
     *
     * @param document      document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public CheckBoxFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets check type for checkbox form field.
     *
     * @return check type to be set for checkbox form field
     */
    public CheckBoxType getCheckType() {
        return checkType;
    }

    /**
     * Sets check type for checkbox form field. Default value is {@link CheckBoxType#CROSS}.
     *
     * @param checkType check type to be set for checkbox form field
     *
     * @return this builder
     */
    public CheckBoxFormFieldBuilder setCheckType(CheckBoxType checkType) {
        this.checkType = checkType;
        return this;
    }

    /**
     * Creates checkbox form field based on provided parameters.
     *
     * @return new {@link PdfButtonFormField} instance
     */
    public PdfButtonFormField createCheckBox() {
        PdfButtonFormField check;
        if (getWidgetRectangle() == null) {
            check = PdfFormCreator.createButtonFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            annotation.setAppearanceState(new PdfName(PdfFormAnnotation.OFF_STATE_VALUE));
            if (getConformance() != null && getConformance().isPdfAOrUa()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            check = PdfFormCreator.createButtonFormField(annotation, getDocument());
        }
        check.disableFieldRegeneration();
        check.pdfConformance = getConformance();
        check.setCheckType(checkType);
        check.setFieldName(getFormFieldName());
        // the default behavior is to automatically calculate the fontsize
        check.setFontSize(0);
        check.put(PdfName.V, new PdfName(PdfFormAnnotation.OFF_STATE_VALUE));
        if (getWidgetRectangle() != null) {
            setPageToField(check);
        }
        check.enableFieldRegeneration();
        return check;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CheckBoxFormFieldBuilder getThis() {
        return this;
    }
}
