/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
