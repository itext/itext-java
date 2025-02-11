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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

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
        verifyOptions(options);
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
            field = PdfFormCreator.createChoiceFormField(getDocument());
        } else {
            annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (null != getConformance() && getConformance().isPdfAOrUa()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            field = PdfFormCreator.createChoiceFormField(annotation, getDocument());
        }
        field.disableFieldRegeneration();
        field.pdfConformance = getConformance();
        if (this.getFont() != null) {
            field.setFont(this.getFont());
        }
        field.setFieldFlags(flags);
        field.setFieldName(getFormFieldName());

        if (options == null) {
            field.put(PdfName.Opt, new PdfArray());
            field.setListSelected(new String[0], false);
        } else {
            field.put(PdfName.Opt, options);
            field.setListSelected(new String[0], false);
            if (annotation != null) {
                setPageToField(field);
            }
        }
        field.enableFieldRegeneration();

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

    private static void verifyOptions(PdfArray options) {
        for (PdfObject option : options) {
            if (option.isArray()) {
                PdfArray optionsArray = ((PdfArray) option);
                if (optionsArray.size() != 2) {
                    throw new IllegalArgumentException(
                            FormsExceptionMessageConstant.INNER_ARRAY_SHALL_HAVE_TWO_ELEMENTS);
                }
                if (!optionsArray.get(0).isString() || !optionsArray.get(1).isString()) {
                    throw new IllegalArgumentException(
                            FormsExceptionMessageConstant.OPTION_ELEMENT_MUST_BE_STRING_OR_ARRAY);
                }
            } else if (!option.isString()) {
                throw new IllegalArgumentException(
                        FormsExceptionMessageConstant.OPTION_ELEMENT_MUST_BE_STRING_OR_ARRAY);
            }
        }
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
