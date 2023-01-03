/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * Builder for choice form field.
 */
public class ChoiceFormFieldBuilder extends TerminalFormFieldBuilder<ChoiceFormFieldBuilder> {

    private PdfArray options = null;

    /**
     * Creates builder for {@link PdfChoiceFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public ChoiceFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets options for choice form field.
     *
     * @return {@link PdfArray} of choice form field options
     */
    public PdfArray getOptions() {
        return options;
    }

    /**
     * Sets options for choice form field.
     *
     * @param options {@link PdfArray} of choice form field options
     * @return this builder
     */
    public ChoiceFormFieldBuilder setOptions(PdfArray options) {
        this.options = options;
        return this;
    }

    /**
     * Sets options for choice form field.
     *
     * @param options array of {@link String} options
     * @return this builder
     */
    public ChoiceFormFieldBuilder setOptions(String[] options) {
        return setOptions(processOptions(options));
    }

    /**
     * Sets options for choice form field.
     *
     * @param options two-dimensional array of {@link String} options. Every inner array shall have two elements.
     * @return this builder
     */
    public ChoiceFormFieldBuilder setOptions(String[][] options) {
        return setOptions(processOptions(options));
    }

    /**
     * Creates list form field based on provided parameters.
     *
     * @return new {@link PdfChoiceFormField} instance
     */
    public PdfChoiceFormField createList() {
        return createChoice(0);
    }

    /**
     * Creates combobox form field base on provided parameters.
     *
     * @return new {@link PdfChoiceFormField} instance
     */
    public PdfChoiceFormField createComboBox() {
        return createChoice(PdfChoiceFormField.FF_COMBO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChoiceFormFieldBuilder getThis() {
        return this;
    }

    private PdfChoiceFormField createChoice(int flags) {
        PdfChoiceFormField field;
        PdfWidgetAnnotation annotation = null;
        if (getWidgetRectangle() == null) {
            field = new PdfChoiceFormField(getDocument());
        } else {
            annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (null != getConformanceLevel()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            field = new PdfChoiceFormField(annotation, getDocument());
        }
        field.pdfAConformanceLevel = getConformanceLevel();

        field.updateFontAndFontSize(getDocument().getDefaultFont(), PdfFormField.DEFAULT_FONT_SIZE);
        field.setFieldFlags(flags);
        field.setFieldName(getFormFieldName());

        if (options == null) {
            field.put(PdfName.Opt, new PdfArray());
            field.setListSelected(new String[0], false);
        } else {
            field.put(PdfName.Opt, options);
            field.setListSelected(new String[0], false);
            String optionsArrayString = "";
            if ((flags & PdfChoiceFormField.FF_COMBO) == 0) {
                optionsArrayString = PdfFormField.optionsArrayToString(options);
            }

            if (annotation != null) {
                PdfFormXObject xObject = new PdfFormXObject(
                        new Rectangle(0, 0, getWidgetRectangle().getWidth(), getWidgetRectangle().getHeight()));
                field.drawChoiceAppearance(getWidgetRectangle(), field.fontSize, optionsArrayString, xObject, 0);
                annotation.setNormalAppearance(xObject.getPdfObject());
                setPageToField(field);
            }
        }

        return field;
    }

    /**
     * Convert {@link String} multidimensional array of combo box or list options to {@link PdfArray}.
     *
     * @param options Two-dimensional array of options.
     * @return a {@link PdfArray} that contains all the options.
     */
    private static PdfArray processOptions(String[][] options) {
        PdfArray array = new PdfArray();
        for (String[] option : options) {
            if (option.length != 2) {
                throw new IllegalArgumentException(FormsExceptionMessageConstant.INNER_ARRAY_SHALL_HAVE_TWO_ELEMENTS);
            }
            PdfArray subArray = new PdfArray(new PdfString(option[0], PdfEncodings.UNICODE_BIG));
            subArray.add(new PdfString(option[1], PdfEncodings.UNICODE_BIG));
            array.add(subArray);
        }
        return array;
    }

    /**
     * Convert {@link String} array of combo box or list options to {@link PdfArray}.
     *
     * @param options array of options.
     * @return a {@link PdfArray} that contains all the options.
     */
    private static PdfArray processOptions(String[] options) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option, PdfEncodings.UNICODE_BIG));
        }
        return array;
    }
}
