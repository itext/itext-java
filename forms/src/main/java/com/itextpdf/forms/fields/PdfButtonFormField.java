package com.itextpdf.forms.fields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

/**
 * An interactive control on the screen that raises events and/or can retain data.
 */
public class PdfButtonFormField extends PdfFormField {

    /**
     * Button field flags
     */
    public final static int FF_NO_TOGGLE_TO_OFF = makeFieldFlag(15);
    public final static int FF_RADIO = makeFieldFlag(16);
    public final static int FF_PUSH_BUTTON = makeFieldFlag(17);
    public final static int FF_RADIOS_IN_UNISON = makeFieldFlag(26);

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
        return setFieldFlag(FF_RADIO, radio);
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
        return setFieldFlag(FF_NO_TOGGLE_TO_OFF, !toggleOff);
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
        return setFieldFlag(FF_PUSH_BUTTON, pushButton);
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
        return setFieldFlag(FF_RADIOS_IN_UNISON, radiosInUnison);
    }
}
