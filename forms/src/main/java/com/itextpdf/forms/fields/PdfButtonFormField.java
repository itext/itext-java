/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An interactive control on the screen that raises events and/or can retain data.
 */
public class PdfButtonFormField extends PdfFormField {

    /**
     * Button field flags
     */
    public static final int FF_NO_TOGGLE_TO_OFF = makeFieldFlag(15);
    public static final int FF_RADIO = makeFieldFlag(16);
    public static final int FF_PUSH_BUTTON = makeFieldFlag(17);
    public static final int FF_RADIOS_IN_UNISON = makeFieldFlag(26);

    protected PdfButtonFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    protected PdfButtonFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        super(widget, pdfDocument);
    }

    protected PdfButtonFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Returns <code>Btn</code>, the form type for choice form fields.
     * 
     * @return the form type, as a {@link PdfName}
     */
    @Override
    public PdfName getFormType() {
        return PdfName.Btn;
    }

    /**
     * If true, the field is a set of radio buttons; if false, the field is a
     * check box. This flag only works if the Pushbutton flag is set to false.
     * @return whether the field is currently radio buttons or a checkbox
     */
    public boolean isRadio() {
        return getFieldFlag(FF_RADIO);
    }

    /**
     * If true, the field is a set of radio buttons; if false, the field is a
     * check box. This flag should be set only if the Pushbutton flag is set to false.
     * @param radio whether the field should be radio buttons or a checkbox
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setRadio(boolean radio) {
        return (PdfButtonFormField) setFieldFlag(FF_RADIO, radio);
    }

    /**
     * If true, clicking the selected button deselects it, leaving no button
     * selected. If false, exactly one radio button shall be selected at all
     * times. Only valid for radio buttons.
     * @return whether a radio button currently allows to choose no options
     */
    public boolean isToggleOff() {
        return !getFieldFlag(FF_NO_TOGGLE_TO_OFF);
    }

    /**
     * If true, clicking the selected button deselects it, leaving no button selected.
     * If false, exactly one radio button shall be selected at all times.
     * @param toggleOff whether a radio button may allow to choose no options
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setToggleOff(boolean toggleOff) {
        return (PdfButtonFormField) setFieldFlag(FF_NO_TOGGLE_TO_OFF, !toggleOff);
    }

    /**
     * If true, the field is a pushbutton that does not retain a permanent value.
     * @return whether or not the field is currently a pushbutton
     */
    public boolean isPushButton() {
        return getFieldFlag(FF_PUSH_BUTTON);
    }

    /**
     * If true, the field is a pushbutton that does not retain a permanent value.
     * @param pushButton whether or not to set the field to a pushbutton
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setPushButton(boolean pushButton) {
        return (PdfButtonFormField) setFieldFlag(FF_PUSH_BUTTON, pushButton);
    }

    /**
     * If true, a group of radio buttons within a radio button field that use
     * the same value for the on state will turn on and off in unison;
     * that is if one is checked, they are all checked.
     * If false, the buttons are mutually exclusive
     * @return whether or not buttons are turned off in unison
     */
    public boolean isRadiosInUnison() {
        return getFieldFlag(FF_RADIOS_IN_UNISON);
    }

    /**
     * If true, a group of radio buttons within a radio button field that use
     * the same value for the on state will turn on and off in unison; that is
     * if one is checked, they are all checked.
     * If false, the buttons are mutually exclusive
     * @param radiosInUnison whether or not buttons should turn off in unison
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setRadiosInUnison(boolean radiosInUnison) {
        return (PdfButtonFormField) setFieldFlag(FF_RADIOS_IN_UNISON, radiosInUnison);
    }

    public PdfButtonFormField setImage(String image) throws IOException {
        InputStream is = new FileInputStream(image);
        String str = Base64.encodeBytes(StreamUtil.inputStreamToArray(is));
        return (PdfButtonFormField) setValue(str);
    }

    public PdfButtonFormField setImageAsForm(PdfFormXObject form) {
        this.form = form;
        regenerateField();
        return this;
    }
}
