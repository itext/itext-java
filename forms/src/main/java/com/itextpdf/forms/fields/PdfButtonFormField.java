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

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interactive control on the screen that raises events and/or can retain data.
 */
public class PdfButtonFormField extends PdfFormField {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfButtonFormField.class);
    /**
     * If true, clicking the selected button deselects it, leaving no button selected.
     * If false, exactly one radio button shall be selected at all times.
     */
    public static final int FF_NO_TOGGLE_TO_OFF = makeFieldFlag(15);

    /**
     * If true, the field is a set of radio buttons.
     * If false, the field is a check box.
     * This flag should be set only if the {@link PdfButtonFormField#FF_PUSH_BUTTON} flag is set to false.
     */
    public static final int FF_RADIO = makeFieldFlag(16);

    /**
     * If true, the field is a push button that does not retain a permanent value.
     */
    public static final int FF_PUSH_BUTTON = makeFieldFlag(17);

    /**
     * If true, a group of radio buttons within a radio button field,
     * that use the same value for the on state will turn on and off in unison.
     * That is if one is checked, they are all checked.
     * If false, the buttons are mutually exclusive.
     */
    public static final int FF_RADIOS_IN_UNISON = makeFieldFlag(26);

    /**
     * Creates a minimal {@link PdfButtonFormField}.
     *
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfButtonFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    /**
     * Creates a button form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfButtonFormField}.
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfButtonFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        super(widget, pdfDocument);
    }

    /**
     * Creates a button form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param pdfObject the dictionary to be wrapped, must have an indirect reference.
     */
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
     *
     * @return whether the field is currently radio buttons or a checkbox
     */
    public boolean isRadio() {
        return getFieldFlag(FF_RADIO);
    }

    /**
     * If true, the field is a set of radio buttons; if false, the field is a
     * check box. This flag should be set only if the Pushbutton flag is set to false.
     *
     * @param radio whether the field should be radio buttons or a checkbox
     *
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setRadio(boolean radio) {
        return (PdfButtonFormField) setFieldFlag(FF_RADIO, radio);
    }

    /**
     * If true, clicking the selected button deselects it, leaving no button
     * selected. If false, exactly one radio button shall be selected at all
     * times. Only valid for radio buttons.
     *
     * @return whether a radio button currently allows to choose no options
     */
    public boolean isToggleOff() {
        return !getFieldFlag(FF_NO_TOGGLE_TO_OFF);
    }

    /**
     * If true, clicking the selected button deselects it, leaving no button selected.
     * If false, exactly one radio button shall be selected at all times.
     *
     * @param toggleOff whether a radio button may allow to choose no options
     *
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setToggleOff(boolean toggleOff) {
        return (PdfButtonFormField) setFieldFlag(FF_NO_TOGGLE_TO_OFF, !toggleOff);
    }

    /**
     * If true, the field is a pushbutton that does not retain a permanent value.
     *
     * @return whether or not the field is currently a pushbutton
     */
    public boolean isPushButton() {
        return getFieldFlag(FF_PUSH_BUTTON);
    }

    /**
     * If true, the field is a pushbutton that does not retain a permanent value.
     *
     * @param pushButton whether or not to set the field to a pushbutton
     *
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
     *
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
     *
     * @param radiosInUnison whether or not buttons should turn off in unison
     *
     * @return current {@link PdfButtonFormField}
     */
    public PdfButtonFormField setRadiosInUnison(boolean radiosInUnison) {
        return (PdfButtonFormField) setFieldFlag(FF_RADIOS_IN_UNISON, radiosInUnison);
    }

    /**
     * Set image to be used as a background content in a push button.
     *
     * @param image path to the image to be used.
     *
     * @return this {@link PdfButtonFormField}
     *
     * @throws IOException if provided path to the image is not correct
     */
    public PdfButtonFormField setImage(String image) throws IOException {
        InputStream is = FileUtil.getInputStreamForFile(image);
        String str = Base64.encodeBytes(StreamUtil.inputStreamToArray(is));
        return (PdfButtonFormField) setValue(str);
    }

    /**
     * Set image to be used as a background content in a push button as {@link PdfFormXObject}.
     * 
     * @param form {@link PdfFormXObject} to be used as an image
     *
     * @return this {@link PdfButtonFormField}
     */
    public PdfButtonFormField setImageAsForm(PdfFormXObject form) {
        this.form = form;
        regenerateField();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param kid {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public PdfFormField addKid(AbstractPdfFormField kid) {
        if (isRadio() && kid instanceof PdfFormAnnotation) {
            final PdfFormAnnotation kidAsFormAnnotation = (PdfFormAnnotation) kid;
            // annotation will always be an object because of the assert in getWidget
            final PdfWidgetAnnotation annotation = kidAsFormAnnotation.getWidget();
            final PdfName appearanceState = annotation.getPdfObject().getAsName(PdfName.AS);
            if (!appearanceState.equals(getValue())) {
                annotation.setAppearanceState(new PdfName(PdfFormAnnotation.OFF_STATE_VALUE));
            }
            if (annotation.getRectangle() == null) {
                LOGGER.warn(FormsLogMessageConstants.RADIO_HAS_NO_RECTANGLE);
                return super.addKid(kid);
            }
            kidAsFormAnnotation.drawRadioButtonAndSaveAppearance(appearanceState.getValue());
        }
        return super.addKid(kid);
    }
}
