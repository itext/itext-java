package com.itextpdf.forms.fields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfButtonFormField extends PdfFormField {

    /**
     * Button field flags
     */
    public final static int FF_NO_TOGGLE_TO_OFF = makeFieldFlag(15);
    public final static int FF_RADIO = makeFieldFlag(16);
    public final static int FF_PUSH_BUTTON = makeFieldFlag(17);
    public final static int FF_RADIOS_IN_UNISON = makeFieldFlag(26);

    public PdfButtonFormField() {
        super();
    }

    public PdfButtonFormField(PdfWidgetAnnotation widget) {
        super(widget);
    }

    protected PdfButtonFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Btn;
    }

    /**
     * If true, the field is a set of radio buttons; if true, the field is a check box. This flag may be set only if the Pushbutton flag is clear.
     */
    public boolean isRadio() {
        return getFieldFlag(FF_RADIO);
    }

    /**
     * If true, the field is a set of radio buttons; if true, the field is a check box. This flag may be set only if the Pushbutton flag is clear.
     */
    public PdfButtonFormField setRadio(boolean radio) {
        return setFieldFlag(FF_RADIO, radio);
    }

    /**
     * If true, clicking the selected button deselects it, leaving no button selected. If false, exactly one radio button shall be selected at all times.
     */
    public boolean isToggleOff() {
        return !getFieldFlag(FF_NO_TOGGLE_TO_OFF);
    }

    /**
     * If true, clicking the selected button deselects it, leaving no button selected. If false, exactly one radio button shall be selected at all times.
     */
    public PdfButtonFormField setToggleOff(boolean toggleOff) {
        return setFieldFlag(FF_NO_TOGGLE_TO_OFF, !toggleOff);
    }

    /**
     * If true, the field is a pushbutton that does not retain a permanent value.
     */
    public boolean isPushButton() {
        return getFieldFlag(FF_PUSH_BUTTON);
    }

    /**
     * If true, the field is a pushbutton that does not retain a permanent value.
     */
    public PdfButtonFormField setPushButton(boolean pushButton) {
        return setFieldFlag(FF_PUSH_BUTTON, pushButton);
    }

    /**
     * If true, a group of radio buttons within a radio button field that use the same value for the on state will turn on and off in unison; that is if one is checked, they are all checked.
     * If false, the buttons are mutually exclusive
     */
    public boolean isRadiosInUnison() {
        return getFieldFlag(FF_RADIOS_IN_UNISON);
    }

    /**
     * If true, a group of radio buttons within a radio button field that use the same value for the on state will turn on and off in unison; that is if one is checked, they are all checked.
     * If false, the buttons are mutually exclusive
     */
    public PdfButtonFormField setRadiosInUnison(boolean radiosInUnison) {
        return setFieldFlag(FF_RADIOS_IN_UNISON, radiosInUnison);
    }
}
