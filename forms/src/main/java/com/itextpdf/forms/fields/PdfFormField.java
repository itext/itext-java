/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.Image;
import com.itextpdf.io.image.ImageFactory;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.element.*;

import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class represents a single field or field group in an {@link com.itextpdf.forms.PdfAcroForm
 * AcroForm}.
 *
 * <br><br>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfFormField extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Flag that designates, if set, that the field can contain multiple lines
     * of text.
     */
    public static final int FF_MULTILINE = makeFieldFlag(13);

    /**
     * Flag that designates, if set, that the field's contents must be obfuscated.
     */
    public static final int FF_PASSWORD = makeFieldFlag(14);
    /**
     * Size of text in form fields when font size is not explicitly set.
     */
    public static final int DEFAULT_FONT_SIZE = 12;
    public static final int DA_FONT = 0;
    public static final int DA_SIZE = 1;
    public static final int DA_COLOR = 2;

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    /**
     * A field with the symbol check
     */
    public static final int TYPE_CHECK = 1;
    /**
     * A field with the symbol circle
     */
    public static final int TYPE_CIRCLE = 2;
    /**
     * A field with the symbol cross
     */
    public static final int TYPE_CROSS = 3;
    /**
     * A field with the symbol diamond
     */
    public static final int TYPE_DIAMOND = 4;
    /**
     * A field with the symbol square
     */
    public static final int TYPE_SQUARE = 5;
    /**
     * A field with the symbol star
     */
    public static final int TYPE_STAR = 6;

    public static final int HIDDEN = 1;
    public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    public static final int HIDDEN_BUT_PRINTABLE = 3;

    public static final int FF_READ_ONLY = makeFieldFlag(1);
    public static final int FF_REQUIRED = makeFieldFlag(2);
    public static final int FF_NO_EXPORT = makeFieldFlag(3);

    protected static String typeChars[] = {"4", "l", "8", "u", "n", "H"};

    protected String text;
    protected Image img;
    protected PdfFont font;
    protected int fontSize;
    protected Color color;
    protected int checkType;
    protected float borderWidth = 1;
    protected Color backgroundColor;
    protected Color borderColor = Color.BLACK;
    protected int rotation = 0;
    protected PdfFormXObject form;

    /**
     * Creates a form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param pdfObject the dictionary to be wrapped, must have an indirect reference.
     */
    public PdfFormField(PdfDictionary pdfObject) {
        super(pdfObject);
        ensureObjectIsAddedToDocument(pdfObject);
        setForbidRelease();
    }

    /**
     * Creates a minimal {@link PdfFormField}.
     */
    protected PdfFormField(PdfDocument pdfDocument) {
        this(new PdfDictionary().makeIndirect(pdfDocument));
        put(PdfName.FT, getFormType());
    }

    /**
     * Creates a form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget the widget which will be a kid of the {@link PdfFormField}
     */
    protected PdfFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        this(new PdfDictionary().makeIndirect(pdfDocument));
        widget.makeIndirect(pdfDocument);
        addKid(widget);
        put(PdfName.FT, getFormType());
    }

    /**
     * Makes a field flag by bit position. Bit positions are numbered 1 to 32.
     * But position 0 corresponds to flag 1, position 3 corresponds to flag 4 etc.
     *
     * @param bitPosition bit position of a flag in range 1 to 32 from the pdf specification.
     * @return corresponding field flag.
     */
    public static int makeFieldFlag(int bitPosition) {
        return (1 << (bitPosition - 1));
    }

    /**
     * Creates an empty form field without a predefined set of layout or
     * behavior.
     *
     * @param doc the {@link PdfDocument} to create the field in
     * @return a new {@link PdfFormField}
     */
    public static PdfFormField createEmptyField(PdfDocument doc) {
        return new PdfFormField(doc);
    }

    /**
     * Creates an empty {@link PdfButtonFormField button form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc   the {@link PdfDocument} to create the button field in
     * @param rect  the location on the page for the button
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *              flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *              flags you require.
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButton(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfButtonFormField field = new PdfButtonFormField(annot, doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates an empty {@link PdfButtonFormField button form field} with custom
     * behavior and layout.
     *
     * @param doc   the {@link PdfDocument} to create the button field in
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *              flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *              flags you require.
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButton(PdfDocument doc, int flags) {
        PdfButtonFormField field = new PdfButtonFormField(doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates an empty {@link PdfTextFormField text form field}.
     *
     * @param doc the {@link PdfDocument} to create the text field in
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc) {
        return new PdfTextFormField(doc);
    }

    /**
     * Creates an empty {@link PdfTextFormField text form field}.
     *
     * @param doc  the {@link PdfDocument} to create the text field in
     * @param rect the location on the page for the text field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        return new PdfTextFormField(annot, doc);
    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     * value, and the form's default font specified in
     * {@link com.itextpdf.forms.PdfAcroForm#getDefaultResources}.
     *
     * @param doc  the {@link PdfDocument} to create the text field in
     * @param rect the location on the page for the text field
     * @param name the name of the form field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name) {
        return createText(doc, rect, name, "");
    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     * value, and the form's default font specified in
     * {@link com.itextpdf.forms.PdfAcroForm#getDefaultResources}.
     *
     * @param doc   the {@link PdfDocument} to create the text field in
     * @param rect  the location on the page for the text field
     * @param name  the name of the form field
     * @param value the initial value
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name, String value) {
        try {
            return createText(doc, rect, name, value, PdfFontFactory.createFont(), DEFAULT_FONT_SIZE);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     * value, with a specified font and font size.
     *
     * @param doc      the {@link PdfDocument} to create the text field in
     * @param rect     the location on the page for the text field
     * @param name     the name of the form field
     * @param value    the initial value
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, int fontSize) {
        return createText(doc, rect, name, value, font, fontSize, false);
    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     * value, with a specified font and font size.
     *
     * @param doc       the {@link PdfDocument} to create the text field in
     * @param rect      the location on the page for the text field
     * @param name      the name of the form field
     * @param value     the initial value
     * @param font      a {@link PdfFont}
     * @param fontSize  a positive integer
     * @param multiline true for multiline text field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, int fontSize, boolean multiline) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfTextFormField field = new PdfTextFormField(annot, doc);
        field.setMultiline(multiline);
        field.font = font;
        field.fontSize = fontSize;
        field.setValue(value);
        field.setFieldName(name);

        return field;
    }

    /**
     * Creates a named {@link PdfTextFormField multilined text form field} with an initial
     * value, with a specified font and font size.
     *
     * @param doc      the {@link PdfDocument} to create the text field in
     * @param rect     the location on the page for the text field
     * @param name     the name of the form field
     * @param value    the initial value
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createMultilineText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, int fontSize) {
        return createText(doc, rect, name, value, font, fontSize, true);
    }

    /**
     * Creates a named {@link PdfTextFormField multiline text form field} with an initial
     * value, and the form's default font specified in
     * {@link com.itextpdf.forms.PdfAcroForm#getDefaultResources}.
     *
     * @param doc   the {@link PdfDocument} to create the text field in
     * @param rect  the location on the page for the text field
     * @param name  the name of the form field
     * @param value the initial value
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createMultilineText(PdfDocument doc, Rectangle rect, String name, String value) {
        try {
            return createText(doc, rect, name, value, PdfFontFactory.createFont(), DEFAULT_FONT_SIZE, true);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    /**
     * Creates an empty {@link PdfChoiceFormField choice form field}.
     *
     * @param doc   the {@link PdfDocument} to create the choice field in
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *              flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *              flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, int flags) {
        PdfChoiceFormField field = new PdfChoiceFormField(doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates an empty {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc   the {@link PdfDocument} to create the choice field in
     * @param rect  the location on the page for the choice field
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *              flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *              flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfChoiceFormField field = new PdfChoiceFormField(annot, doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates a {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc     the {@link PdfDocument} to create the choice field in
     * @param rect    the location on the page for the choice field
     * @param name    the name of the form field
     * @param value   the initial value
     * @param options an array of {@link PdfString} objects that each represent
     *                the 'on' state of one of the choices.
     * @param flags   an <code>int</code>, containing a set of binary behavioral
     *                flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, String name, String value, PdfArray options, int flags) {
        try {
            return createChoice(doc, rect, name, value, PdfFontFactory.createFont(), DEFAULT_FONT_SIZE, options, flags);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    /**
     * Creates a {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc      the {@link PdfDocument} to create the choice field in
     * @param rect     the location on the page for the choice field
     * @param name     the name of the form field
     * @param value    the initial value
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @param options  an array of {@link PdfString} objects that each represent
     *                 the 'on' state of one of the choices.
     * @param flags    an <code>int</code>, containing a set of binary behavioral
     *                 flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                 flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, int fontSize, PdfArray options, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfFormField field = new PdfChoiceFormField(annot, doc);
        field.font = font;
        field.fontSize = fontSize;
        field.put(PdfName.Opt, options);
        field.setFieldFlags(flags);
        field.setFieldName(name);
        field.getPdfObject().put(PdfName.V, new PdfString(value));
        if ((flags & PdfChoiceFormField.FF_COMBO) == 0) {
            value = field.optionsArrayToString(options);
        }

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, rect.getWidth(), rect.getHeight()));
        field.drawMultiLineTextAppearance(rect, font, fontSize, value, xObject);
        xObject.getResources().addFont(doc, font);
        annot.setNormalAppearance(xObject.getPdfObject());

        return (PdfChoiceFormField) field;
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     *
     * @param doc the {@link PdfDocument} to create the signature field in
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc) {
        return new PdfSignatureFormField(doc);
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     *
     * @param doc  the {@link PdfDocument} to create the signature field in
     * @param rect the location on the page for the signature field
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        return new PdfSignatureFormField(annot, doc);
    }

    /**
     * Creates a {@link PdfButtonFormField radio group form field}.
     *
     * @param doc   the {@link PdfDocument} to create the radio group in
     * @param name  the name of the form field
     * @param value the initial value
     * @return a new {@link PdfButtonFormField radio group}
     */
    public static PdfButtonFormField createRadioGroup(PdfDocument doc, String name, String value) {
        PdfButtonFormField radio = createButton(doc, PdfButtonFormField.FF_RADIO);
        radio.setFieldName(name);
        radio.put(PdfName.V, new PdfName(value));
        return radio;
    }


    /**
     * Creates a generic {@link PdfFormField} that is added to a radio group.
     *
     * @param doc        the {@link PdfDocument} to create the radio group in
     * @param rect       the location on the page for the field
     * @param radioGroup the radio button group that this field should belong to
     * @param value      the initial value
     * @return a new {@link PdfFormField}
     * @see #createRadioGroup(com.itextpdf.kernel.pdf.PdfDocument, java.lang.String, java.lang.String)
     */
    public static PdfFormField createRadioButton(PdfDocument doc, Rectangle rect, PdfButtonFormField radioGroup, String value) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfFormField radio = new PdfButtonFormField(annot, doc);

        String name = radioGroup.getValue().toString().substring(1);
        if (name.equals(value)) {
            annot.setAppearanceState(new PdfName(value));
        } else {
            annot.setAppearanceState(new PdfName("Off"));
        }
        radio.drawRadioAppearance(rect.getWidth(), rect.getHeight(), value);
        radioGroup.addKid(radio);
        return radio;
    }

    /**
     * Creates a {@link PdfButtonFormField} as a push button without data.
     *
     * @param doc     the {@link PdfDocument} to create the radio group in
     * @param rect    the location on the page for the field
     * @param name    the name of the form field
     * @param caption the text to display on the button
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption) {
        PdfButtonFormField field;
        try {
            field = createPushButton(doc, rect, name, caption, PdfFontFactory.createFont(), DEFAULT_FONT_SIZE);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
        return field;
    }

    /**
     * Creates a {@link PdfButtonFormField} as a push button without data, with
     * its caption in a custom font.
     *
     * @param doc      the {@link PdfDocument} to create the radio group in
     * @param rect     the location on the page for the field
     * @param name     the name of the form field
     * @param caption  the text to display on the button
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption, PdfFont font, int fontSize) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfButtonFormField field = new PdfButtonFormField(annot, doc);
        field.setPushButton(true);
        field.setFieldName(name);
        field.text = caption;
        field.font = font;
        field.fontSize = fontSize;

        PdfFormXObject xObject = field.drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), caption, font, fontSize);
        annot.setNormalAppearance(xObject.getPdfObject());

        return field;
    }

    /**
     * Creates a {@link PdfButtonFormField} as a checkbox.
     *
     * @param doc   the {@link PdfDocument} to create the radio group in
     * @param rect  the location on the page for the field
     * @param name  the name of the form field
     * @param value the initial value
     * @return a new {@link PdfButtonFormField checkbox}
     */
    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect, String name, String value) {
        return createCheckBox(doc, rect, name, value, TYPE_CROSS);
    }

    /**
     * Creates a {@link PdfButtonFormField} as a checkbox.
     *
     * @param doc       the {@link PdfDocument} to create the radio group in
     * @param rect      the location on the page for the field
     * @param name      the name of the form field
     * @param value     the initial value
     * @param checkType the type of checkbox graphic to use.
     * @return a new {@link PdfButtonFormField checkbox}
     */
    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect, String name, String value, int checkType) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfFormField check = new PdfButtonFormField(annot, doc);
        check.setCheckType(checkType);
        check.setFieldName(name);
        check.setValue(value);
        annot.setAppearanceState(new PdfName(value));
        check.drawCheckAppearance(rect.getWidth(), rect.getHeight(), value.equals("Off") ? "Yes": value);

        return (PdfButtonFormField) check;
    }

    /**
     * Creates a {@link PdfChoiceFormField combobox} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc     the {@link PdfDocument} to create the combobox in
     * @param rect    the location on the page for the combobox
     * @param name    the name of the form field
     * @param value   the initial value
     * @param options a two-dimensional array of Strings which will be converted
     *                to a PdfArray.
     * @return a new {@link PdfChoiceFormField} as a combobox
     */
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String name, String value, String options[][]) {
        return createChoice(doc, rect, name, value, processOptions(options), PdfChoiceFormField.FF_COMBO);
    }

    /**
     * Creates a {@link PdfChoiceFormField combobox} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc     the {@link PdfDocument} to create the combobox in
     * @param rect    the location on the page for the combobox
     * @param name    the name of the form field
     * @param value   the initial value
     * @param options an array of Strings which will be converted to a PdfArray.
     * @return a new {@link PdfChoiceFormField} as a combobox
     */
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String name, String value, String options[]) {
        return createChoice(doc, rect, name, value, processOptions(options), PdfChoiceFormField.FF_COMBO);
    }

    /**
     * Creates a {@link PdfChoiceFormField list field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc     the {@link PdfDocument} to create the choice field in
     * @param rect    the location on the page for the choice field
     * @param name    the name of the form field
     * @param value   the initial value
     * @param options a two-dimensional array of Strings which will be converted
     *                to a PdfArray.
     * @return a new {@link PdfChoiceFormField} as a list field
     */
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String name, String value, String options[][]) {
        return createChoice(doc, rect, name, value, processOptions(options), 0);
    }

    /**
     * Creates a {@link PdfChoiceFormField list field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc     the {@link PdfDocument} to create the list field in
     * @param rect    the location on the page for the list field
     * @param name    the name of the form field
     * @param value   the initial value
     * @param options an array of Strings which will be converted to a PdfArray.
     * @return a new {@link PdfChoiceFormField} as a list field
     */
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String name, String value, String options[]) {
        return createChoice(doc, rect, name, value, processOptions(options), 0);
    }

    /**
     * Creates a (subtype of) {@link PdfFormField} object. The type of the object
     * depends on the <code>FT</code> entry in the <code>pdfObject</code> parameter.
     *
     * @param <T>       an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param pdfObject assumed to be either a {@link PdfDictionary}, or a
     *                  {@link PdfIndirectReference} to a {@link PdfDictionary}
     * @param document  the {@link PdfDocument} to create the field in
     * @return a new {@link PdfFormField}, or <code>null</code> if
     * <code>pdfObject</code> does not contain a <code>FT</code> entry
     */
    public static <T extends PdfFormField> T makeFormField(PdfObject pdfObject, PdfDocument document) {
        T field = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName formType = dictionary.getAsName(PdfName.FT);
            if (PdfName.Tx.equals(formType))
                field = new PdfTextFormField(dictionary).makeIndirect(document);
            else if (PdfName.Btn.equals(formType))
                field = new PdfButtonFormField(dictionary).makeIndirect(document);
            else if (PdfName.Ch.equals(formType))
                field = new PdfChoiceFormField(dictionary).makeIndirect(document);
            else if (PdfName.Sig.equals(formType))
                field = new PdfSignatureFormField(dictionary).makeIndirect(document);
            else
                field = new PdfFormField(dictionary).makeIndirect(document);
        }

        return field;
    }

    /**
     * Returns the type of the <p>Parent</p> form field, or of the wrapped
     * &lt;PdfDictionary&gt; object.
     *
     * @return the form type, as a {@link PdfName}
     */
    public PdfName getFormType() {
        return getTypeFromParent(getPdfObject());
    }

    /**
     * Sets a value to the field and generating field appearance if needed.
     *
     * @param <T>   an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value of the field
     * @return the field
     */
    public <T extends PdfFormField> T setValue(String value) {
        PdfName ft = getFormType();
        if (ft == null || !ft.equals(PdfName.Btn)) {
            PdfArray kids = getKids();
            if (kids != null) {
                for (PdfObject kid : kids) {
                    if (kid.isIndirectReference()) {
                        kid = ((PdfIndirectReference) kid).getRefersTo();
                    }
                    PdfFormField field = new PdfFormField((PdfDictionary) kid);
                    field.font = font;
                    field.fontSize = fontSize;
                    field.setValue(value);
                }
            }
        }

        return setValue(value, true);
    }

    /**
     * Sets a value to the field and generating field appearance if needed.
     *
     * @param <T>                an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value              of the field
     * @param generateAppearance set this flat to false if you want to keep the appearance of the field generated before
     * @return the field
     */
    public <T extends PdfFormField> T setValue(String value, boolean generateAppearance) {
        PdfName formType = getFormType();
        if (PdfName.Tx.equals(formType) || PdfName.Ch.equals(formType)) {
            put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
        } else if (PdfName.Btn.equals(formType)) {
            if ((getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) != 0) {
                try {
                    img = ImageFactory.getImage(Base64.decode(value));
                } catch (Exception e) {
                    text = value;
                }
            } else {
                put(PdfName.V, new PdfName(value));
                for (String as : getAppearanceStates()) {
                    if (as.equals(value)) {
                        put(PdfName.AS, new PdfName(value));
                        break;
                    }
                }
            }
        } else {
            put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
        }

        if (PdfName.Btn.equals(formType) && (getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) == 0) {
            if (generateAppearance) {
                regenerateField();
            }
        } else {
            regenerateField();
        }
        return (T) this;
    }

    /**
     * Set text field value with given font and size
     *
     * @param <T>      an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value    text value
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @return the edited field
     */
    public <T extends PdfFormField> T setValue(String value, PdfFont font, int fontSize) {
        PdfName formType = getFormType();
        if (!formType.equals(PdfName.Tx) && !formType.equals(PdfName.Ch)) {
            return setValue(value);
        }
        PdfArray bBox = getPdfObject().getAsArray(PdfName.Rect);
        if (bBox == null) {
            PdfArray kids = getKids();
            if (kids == null) {
                throw new PdfException(PdfException.WrongFormFieldAddAnnotationToTheField);
            }
            bBox = ((PdfDictionary) kids.get(0)).getAsArray(PdfName.Rect);
        }


        PdfFormXObject appearance = new PdfFormXObject(new Rectangle(0, 0, bBox.toRectangle().getWidth(), bBox.toRectangle().getHeight()));
        if (formType.equals(PdfName.Tx)) {
            drawTextAppearance(bBox.toRectangle(), font, fontSize, value, appearance);
//            appearance.getPdfObject().setData(drawTextAppearance(bBox.toRectangle(), font, fontSize, value, appearance));
        } else {
            drawMultiLineTextAppearance(bBox.toRectangle(), font, fontSize, value, appearance);
//            appearance = drawMultiLineTextAppearance(bBox.toRectangle(), font, fontSize, value, new PdfResources());
        }

        appearance.getResources().addFont(getDocument(), font);
        PdfDictionary ap = new PdfDictionary();
        ap.put(PdfName.N, appearance.getPdfObject());
        getPdfObject().put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));

        return put(PdfName.AP, ap);
    }

    /**
     * Sets the field value and the display string. The display string
     * is used to build the appearance.
     *
     * @param <T>     an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value   the field value
     * @param display the string that is used for the appearance. If <CODE>null</CODE>
     *                the <CODE>value</CODE> parameter will be used
     * @return the edited field
     */
    public <T extends PdfFormField> T setValue(String value, String display) {
        if (display == null) {
            return setValue(value);
        }
        setValue(display, true);
        PdfName formType = getFormType();
        if (PdfName.Tx.equals(formType) || PdfName.Ch.equals(formType)) {
            put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
        } else if (PdfName.Btn.equals(formType)) {
            if ((getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) != 0) {
                text = value;
            } else {
                put(PdfName.V, new PdfName(value));
            }
        } else {
            put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
        }

        return (T) this;
    }

    /**
     * Sets a parent {@link PdfFormField} for the current object.
     *
     * @param <T>    an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param parent another form field that this field belongs to, usually a group field
     * @return the edited field
     */
    public <T extends PdfFormField> T setParent(PdfFormField parent) {
        return put(PdfName.Parent, parent);
    }

    /**
     * Gets the parent dictionary.
     *
     * @return another form field that this field belongs to, usually a group field
     */
    public PdfDictionary getParent() {
        return getPdfObject().getAsDictionary(PdfName.Parent);
    }

    /**
     * Gets the kids of this object.
     *
     * @return contents of the dictionary's <code>Kids</code> property, as a {@link PdfArray}
     */
    public PdfArray getKids() {
        return getPdfObject().getAsArray(PdfName.Kids);
    }

    /**
     * Adds a new kid to the <code>Kids</code> array property from a
     * {@link PdfFormField}. Also sets the kid's <code>Parent</code> property to this object.
     *
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param kid a new {@link PdfFormField} entry for the field's <code>Kids</code> array property
     * @return the edited field
     */
    public <T extends PdfFormField> T addKid(PdfFormField kid) {
        kid.setParent(this);
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());

        return put(PdfName.Kids, kids);
    }

    /**
     * Adds a new kid to the <code>Kids</code> array property from a
     * {@link PdfWidgetAnnotation}. Also sets the kid's <code>Parent</code> property to this object.
     *
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param kid a new {@link PdfWidgetAnnotation} entry for the field's <code>Kids</code> array property
     * @return the edited field
     */
    public <T extends PdfFormField> T addKid(PdfWidgetAnnotation kid) {
        kid.setParent(getPdfObject());
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());
        return put(PdfName.Kids, kids);
    }

    /**
     * Changes the name of the field to the specified value.
     *
     * @param <T>  an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param name the new field name, as a String
     * @return the edited field
     */
    public <T extends PdfFormField> T setFieldName(String name) {
        return put(PdfName.T, new PdfString(name));
    }

    /**
     * Gets the current field name.
     *
     * @return the current field name, as a {@link PdfString}
     */
    public PdfString getFieldName() {
        String parentName = "";
        PdfDictionary parent = getParent();
        if (parent != null) {
            PdfFormField parentField = PdfFormField.makeFormField(getParent(), getDocument());
            PdfString pName = parentField.getFieldName();
            if (pName != null) {
                parentName = pName.toUnicodeString() + ".";
            }
        }
        PdfString name = getPdfObject().getAsString(PdfName.T);
        if (name != null) {
            name = new PdfString(parentName + name.toUnicodeString());
        }
        return name;
    }

    /**
     * Changes the alternate name of the field to the specified value. The
     * alternate is a descriptive name to be used by status messages etc.
     *
     * @param <T>  an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param name the new alternate name, as a String
     * @return the edited field
     */
    public <T extends PdfFormField> T setAlternativeName(String name) {
        return put(PdfName.TU, new PdfString(name));
    }

    /**
     * Gets the current alternate name. The alternate is a descriptive name to
     * be used by status messages etc.
     *
     * @return the current alternate name, as a {@link PdfString}
     */
    public PdfString getAlternativeName() {
        return getPdfObject().getAsString(PdfName.TU);
    }

    /**
     * Changes the mapping name of the field to the specified value. The
     * mapping name can be used when exporting the form data in the document.
     *
     * @param <T>  an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param name the new alternate name, as a String
     * @return the edited field
     */
    public <T extends PdfFormField> T setMappingName(String name) {
        return put(PdfName.TM, new PdfString(name));
    }

    /**
     * Gets the current mapping name. The mapping name can be used when
     * exporting the form data in the document.
     *
     * @return the current mapping name, as a {@link PdfString}
     */
    public PdfString getMappingName() {
        return getPdfObject().getAsString(PdfName.TM);
    }

    /**
     * Checks whether a certain flag, or any of a combination of flags, is set
     * for this form field.
     *
     * @param flag an <code>int</code> interpreted as a series of a binary flags
     * @return true if any of the flags specified in the parameter is also set
     * in the form field.
     */
    public boolean getFieldFlag(int flag) {
        return (getFieldFlags() & flag) != 0;
    }

    /**
     * Adds a flag, or combination of flags, for the form field. This method is
     * intended to be used one flag at a time, but this is not technically
     * enforced. To <em>replace</em> the current value, use
     * {@link #setFieldFlags(int)}.
     *
     * @param <T>  an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param flag an <code>int</code> interpreted as a series of a binary flags
     * @return the edited field
     */
    public <T extends PdfFormField> T setFieldFlag(int flag) {
        return setFieldFlag(flag, true);
    }

    /**
     * Adds or removes a flag, or combination of flags, for the form field. This
     * method is intended to be used one flag at a time, but this is not
     * technically enforced. To <em>replace</em> the current value, use
     * {@link #setFieldFlags(int)}.
     *
     * @param <T>   an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param flag  an <code>int</code> interpreted as a series of a binary flags
     * @param value if <code>true</code>, adds the flag(s). if <code>false</code>,
     *              removes the flag(s).
     * @return the edited field
     */
    public <T extends PdfFormField> T setFieldFlag(int flag, boolean value) {
        int flags = getFieldFlags();

        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }

        return setFieldFlags(flags);
    }

    /**
     * If true, the field can contain multiple lines of text; if false, the field???s text is restricted to a single line.
     *
     * @return whether the field can span over multiple lines.
     */
    public boolean isMultiline() {
        return getFieldFlag(FF_MULTILINE);
    }

    /**
     * If true, the field is intended for entering a secure password that should not be echoed visibly to the screen.
     * Characters typed from the keyboard should instead be echoed in some unreadable form, such as asterisks or bullet characters.
     *
     * @return whether or not the contents of the field must be obfuscated
     */
    public boolean isPassword() {
        return getFieldFlag(FF_PASSWORD);
    }

    /**
     * Sets a flag, or combination of flags, for the form field. This method
     * <em>replaces</em> the previous value. Compare with {@link #setFieldFlag(int)}
     * which <em>adds</em> a flag to the existing flags.
     *
     * @param <T>   an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param flags an <code>int</code> interpreted as a series of a binary flags
     * @return the edited field
     */
    public <T extends PdfFormField> T setFieldFlags(int flags) {
        return put(PdfName.Ff, new PdfNumber(flags));
    }

    /**
     * Gets the current list of PDF form field flags.
     *
     * @return the current list of flags, encoded as an <code>int</code>
     */
    public int getFieldFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.Ff);
        if (f != null) {
            return f.getIntValue();
        } else {
            PdfDictionary parent = getParent();
            if (parent != null) {
                return new PdfFormField(parent).getFieldFlags();
            } else {
                return 0;
            }
        }
    }

    /**
     * Gets the current value contained in the form field.
     *
     * @return the current value, as a {@link PdfObject}
     */
    public PdfObject getValue() {
        return getPdfObject().get(PdfName.V);
    }

    /**
     * Gets the current value contained in the form field.
     *
     * @return the current value, as a {@link String}
     */
    public String getValueAsString() {
        PdfObject value = getPdfObject().get(PdfName.V);
        if (value == null) {
            return "";
        } else if (value instanceof PdfStream) {
            return new String(((PdfStream) value).getBytes());
        } else if (value instanceof PdfName) {
            return ((PdfName) value).getValue();
        } else if (value instanceof PdfString) {
            return ((PdfString) value).toUnicodeString();
        } else {
            return "";
        }
    }

    /**
     * Sets the default fallback value for the form field.
     *
     * @param <T>   an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value the default value
     * @return the edited field
     */
    public <T extends PdfFormField> T setDefaultValue(PdfObject value) {
        return put(PdfName.DV, value);
    }

    /**
     * Gets the default fallback value for the form field.
     *
     * @return the default value
     */
    public PdfObject getDefaultValue() {
        return getPdfObject().get(PdfName.DV);
    }

    /**
     * Sets an additional action for the form field.
     *
     * @param <T>    an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param key    the dictionary key to use for storing the action
     * @param action the action
     * @return the edited field
     */
    public <T extends PdfFormField> T setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    /**
     * Gets the currently additional action dictionary for the form field.
     *
     * @return the additional action dictionary
     */
    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    /**
     * Sets options for the form field. Only to be used for checkboxes and radio buttons.
     *
     * @param <T>     an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param options an array of {@link PdfString} objects that each represent
     *                the 'on' state of one of the choices.
     * @return the edited field
     */
    public <T extends PdfFormField> T setOptions(PdfArray options) {
        return put(PdfName.Opt, options);
    }

    /**
     * Gets options for the form field. Should only return usable values for
     * checkboxes and radio buttons.
     *
     * @return the options, as an {@link PdfArray} of {@link PdfString} objects
     */
    public PdfArray getOptions() {
        return getPdfObject().getAsArray(PdfName.Opt);
    }

    /**
     * Gets all {@link PdfWidgetAnnotation} that this form field and its
     * {@link #getKids() kids} refer to.
     *
     * @return a list of {@link PdfWidgetAnnotation}
     */
    public List<PdfWidgetAnnotation> getWidgets() {
        List<PdfWidgetAnnotation> widgets = new ArrayList<>();

        PdfName subType = getPdfObject().getAsName(PdfName.Subtype);
        if (subType != null && subType.equals(PdfName.Widget)) {
            widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(getPdfObject()));
        }

        PdfArray kids = getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                if (kid.isIndirectReference()) {
                    kid = ((PdfIndirectReference) kid).getRefersTo();
                }
                subType = ((PdfDictionary) kid).getAsName(PdfName.Subtype);
                if (subType != null && subType.equals(PdfName.Widget)) {
                    widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(kid));
                }
            }
        }

        return widgets;
    }

    /**
     * Gets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field???s text size and color.
     *
     * @return the default appearance graphics, as a {@link PdfString}
     */
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * Sets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field???s text size and color.
     *
     * @param <T>               an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param defaultAppearance a valid sequence of PDF content stream syntax
     * @return the edited field
     */
    public <T extends PdfFormField> T setDefaultAppearance(String defaultAppearance) {
        byte[] b = defaultAppearance.getBytes();
        int len = b.length;
        for (int k = 0; k < len; ++k) {
            if (b[k] == '\n')
                b[k] = 32;
        }
        getPdfObject().put(PdfName.DA, new PdfString(new String(b)));
        return (T) this;
    }

    /**
     * Gets a code specifying the form of quadding (justification) to be used in displaying the text:
     * 0 Left-justified
     * 1 Centered
     * 2 Right-justified
     *
     * @return the current justification attribute
     */
    public Integer getJustification() {
        return getPdfObject().getAsInt(PdfName.Q);
    }

    /**
     * Sets a code specifying the form of quadding (justification) to be used in displaying the text:
     * 0 Left-justified
     * 1 Centered
     * 2 Right-justified
     *
     * @param <T>           an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param justification the value to set the justification attribute to
     * @return the edited field
     */
    public <T extends PdfFormField> T setJustification(int justification) {
        getPdfObject().put(PdfName.Q, new PdfNumber(justification));
        regenerateField();
        return (T) this;
    }

    /**
     * Gets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     *
     * @return the default style, as a {@link PdfString}
     */
    public PdfString getDefaultStyle() {
        return getPdfObject().getAsString(PdfName.DS);
    }

    /**
     * Sets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     *
     * @param <T>                an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param defaultStyleString a new default style for the form field
     * @return the edited field
     */
    public <T extends PdfFormField> T setDefaultStyle(PdfString defaultStyleString) {
        getPdfObject().put(PdfName.DS, defaultStyleString);
        return (T) this;
    }

    /**
     * Gets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     *
     * @return the current rich text value
     */
    public PdfObject getRichText() {
        return getPdfObject().get(PdfName.RV);
    }

    /**
     * Sets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     *
     * @param <T>      an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param richText a new rich text value
     * @return the edited field
     */
    public <T extends PdfFormField> T setRichText(PdfObject richText) {
        getPdfObject().put(PdfName.RV, richText);
        return (T) this;
    }

    /**
     * Gets the current font of the form field.
     *
     * @return the current {@link PdfFont font}
     */
    public PdfFont getFont() {
        return font;
    }

    /**
     * Basic setter for the <code>font</code> property. Regenerates the field
     * appearance after setting the new value.
     *
     * @param font the new font to be set
     */
    public void setFont(PdfFont font) {
        this.font = font;
        regenerateField();
    }

    /**
     * Basic setter for the <code>fontSize</code> property. Regenerates the
     * field appearance after setting the new value.
     *
     * @param fontSize the new font size to be set
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        regenerateField();
    }

    /**
     * Combined setter for the <code>font</code> and <code>fontSize</code>
     * properties. Regenerates the field appearance after setting the new value.
     *
     * @param font     the new font to be set
     * @param fontSize the new font size to be set
     */
    public void setFontAndSize(PdfFont font, int fontSize) {
        this.font = font;
        this.fontSize = fontSize;
        regenerateField();
    }

    /**
     * Basic setter for the <code>backgroundColor</code> property. Regenerates
     * the field appearance after setting the new value.
     *
     * @param backgroundColor the new color to be set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        regenerateField();
    }

    /**
     * Basic setter for the <code>degRotation</code> property. Regenerates
     * the field appearance after setting the new value.
     *
     * @param degRotation the new degRotation to be set
     */
    public void setRotation(int degRotation) {
        if(degRotation % 90 != 0) {
            throw new IllegalArgumentException("degRotation.must.be.a.multiple.of.90");
        } else {
            degRotation %= 360;
            if(degRotation < 0) {
                degRotation += 360;
            }

            this.rotation = degRotation;
        }
        this.rotation = degRotation;
        regenerateField();
    }

    /**
     * Sets the action on all {@link PdfWidgetAnnotation widgets} of this form field.
     *
     * @param <T>    an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param action the action
     * @return the edited field
     */
    public <T extends PdfFormField> T setAction(PdfAction action) {
        List<PdfWidgetAnnotation> widgets = getWidgets();
        if (widgets != null) {
            for (PdfWidgetAnnotation widget : widgets) {
                widget.setAction(action);
            }
        }
        return (T) this;
    }

    /**
     * Changes the type of graphical marker used to mark a checkbox as 'on'.
     * Notice that in order to complete the change one should call
     * {@link #regenerateField() regenerateField} method
     *
     * @param checkType the new checkbox marker
     */
    public void setCheckType(int checkType) {
        if (checkType < TYPE_CHECK || checkType > TYPE_STAR) {
            checkType = TYPE_CROSS;
        }
        this.checkType = checkType;
        text = typeChars[checkType - 1];
        try {
            font = PdfFontFactory.createFont(FontConstants.ZAPFDINGBATS);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    /**
     * @param <T>        an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param visibility
     * @return the edited field
     */
    public <T extends PdfFormField> T setVisibility(int visibility) {
        switch (visibility) {
            case HIDDEN:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.HIDDEN));
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.NO_VIEW));
                break;
            default:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT));
                break;
        }
        return (T) this;
    }

    /**
     * This method regenerates appearance stream of the field. Use it if you
     * changed any field parameters and didn't use setValue method which
     * generates appearance by itself.
     *
     * @return whether or not the regeneration was successful.
     */
    public boolean regenerateField() {
        PdfName type = getFormType();
        String value = getValueAsString();

        if (PdfName.Tx.equals(type) || PdfName.Ch.equals(type)) {
            try {
                PdfDictionary apDic = getPdfObject().getAsDictionary(PdfName.AP);
                PdfStream asNormal = null;
                if (apDic != null) {
                    asNormal = apDic.getAsStream(PdfName.N);
                }
                PdfArray bBox = getPdfObject().getAsArray(PdfName.Rect);
                if (bBox == null) {
                    PdfArray kids = getKids();
                    if (kids == null) {
                        throw new PdfException(PdfException.WrongFormFieldAddAnnotationToTheField);
                    }
                    bBox = ((PdfDictionary) kids.get(0)).getAsArray(PdfName.Rect);
                }

                Object[] fontAndSize = getFontAndSize(asNormal);
                PdfFont localFont = (PdfFont) fontAndSize[0];
                int fontSz = (int) fontAndSize[1];
                if (fontSz == 0) {
                    fontSz = DEFAULT_FONT_SIZE;
                }

                PdfFormXObject appearance = null;
                if (asNormal != null) {
                    appearance = new PdfFormXObject(asNormal);
                    appearance.setBBox(new PdfArray(new float[]{0, 0, bBox.toRectangle().getWidth(), bBox.toRectangle().getHeight()}));
                }
                if (appearance == null) {
                    appearance = new PdfFormXObject(new Rectangle(0, 0, bBox.toRectangle().getWidth(), bBox.toRectangle().getHeight()));
                }

                if (PdfName.Tx.equals(type)) {
                    if (!isMultiline()) {
                        drawTextAppearance(bBox.toRectangle(), localFont, fontSz, value, appearance);
                    } else {
                        drawMultiLineTextAppearance(bBox.toRectangle(), localFont, fontSz, value, appearance);
                    }

                } else {
                    if (!getFieldFlag(PdfChoiceFormField.FF_COMBO)) {
                        PdfNumber topIndex =((PdfChoiceFormField)this).getTopIndex();
                        PdfArray options = (PdfArray) getOptions().clone();
                        if (topIndex != null) {
                            PdfObject object = options.get(topIndex.getIntValue());
                            options.remove(topIndex.getIntValue());
                            options.add(0, object);
                        }
                        value = optionsArrayToString(options);
                    }
                    drawMultiLineTextAppearance(bBox.toRectangle(), localFont, fontSz, value, appearance);
                }

                appearance.getResources().addFont(getDocument(), localFont);
                PdfDictionary ap = new PdfDictionary();
                ap.put(PdfName.N, appearance.getPdfObject());
                put(PdfName.AP, ap);

                return true;
            } catch (IOException e) {
                throw new PdfException(e.getLocalizedMessage());
            }

        } else if (PdfName.Btn.equals(type)) {
            int ff = getFieldFlags();
            if ((ff & PdfButtonFormField.FF_PUSH_BUTTON) != 0) {
                try {
                    value = text;
                    PdfFormXObject appearance;
                    Rectangle rect = getRect(getPdfObject());
                    PdfDictionary apDic = getPdfObject().getAsDictionary(PdfName.AP);
                    if (apDic == null) {
                        List<PdfWidgetAnnotation> widgets = getWidgets();
                        if (widgets.size() == 1) {
                            apDic = widgets.get(0).getPdfObject().getAsDictionary(PdfName.AP);
                        }
                    }
                    if (img != null) {
                        appearance = drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), value, null, 0);
                    } else if (form != null) {
                        appearance = drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), value, null, 0);
                    } else {
                        PdfStream asNormal = null;
                        if (apDic != null) {
                            asNormal = apDic.getAsStream(PdfName.N);
                        }
                        Object[] fontAndSize = getFontAndSize(asNormal);
                        PdfFont localFont = (PdfFont) fontAndSize[0];
                        int fontSz = (int) fontAndSize[1];
                        appearance = drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), value, localFont, fontSz);
                        appearance.getResources().addFont(getDocument(), localFont);
                    }

                    if (apDic == null) {
                        apDic = new PdfDictionary();
                        put(PdfName.AP, apDic);
                    }
                    apDic.put(PdfName.N, appearance.getPdfObject());
                } catch (IOException e) {
                    throw new PdfException(e.getLocalizedMessage());
                }
            } else if ((ff & PdfButtonFormField.FF_RADIO) != 0) {
                PdfArray kids = getKids();
                for (PdfObject kid : kids) {
                    if (kid.isIndirectReference()) {
                        kid = ((PdfIndirectReference) kid).getRefersTo();
                    }
                    PdfFormField field = new PdfFormField((PdfDictionary) kid);
                    PdfWidgetAnnotation widget = field.getWidgets().get(0);
                    PdfDictionary buttonValues = field.getPdfObject().getAsDictionary(PdfName.AP).getAsDictionary(PdfName.N);
                    String state;
                    if (buttonValues.get(new PdfName(value)) != null) {
                        state = value;
                    } else {
                        state = "Off";

                    }
                    widget.setAppearanceState(new PdfName(state));
                }
            } else {
                Rectangle rect = getRect(getPdfObject());
                setCheckType(checkType);
                drawCheckAppearance(rect.getWidth(), rect.getHeight(), value);
                PdfWidgetAnnotation widget = getWidgets().get(0);
                if (widget.getNormalAppearanceObject().containsKey(new PdfName(value))) {
                    widget.setAppearanceState(new PdfName(value));
                } else {
                    widget.setAppearanceState(new PdfName("Off"));
                }
            }
        }
        return true;
    }

    /**
     * Gets the border width for the field.
     *
     * @return the current border width.
     */
    public float getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the border width for the field.
     *
     * @param borderWidth the new border width.
     */
    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Sets the Border Color.
     *
     * @param <T>   an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param color the new value for the Border Color
     * @return the edited field
     */
    public <T extends PdfFormField> T setBorderColor(Color color) {
        borderColor = color;
        regenerateField();
        return (T) this;
    }

    /**
     * Sets the text color.
     * @param color the new value for the Color
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @return the edited field
     */
    public <T extends PdfFormField> T setColor(Color color) {
        this.color = color;
        regenerateField();
        return (T) this;
    }

    /**
     * Sets the ReadOnly flag, specifying whether or not the field can be changed.
     *
     * @param <T>      an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param readOnly if <code>true</code>, then the field cannot be changed.
     * @return the edited field
     */
    public <T extends PdfFormField> T setReadOnly(boolean readOnly) {
        return setFieldFlag(FF_READ_ONLY, readOnly);
    }

    /**
     * Gets the ReadOnly flag, specifying whether or not the field can be changed.
     *
     * @return <code>true</code> if the field cannot be changed.
     */
    public boolean isReadOnly() {
        return getFieldFlag(FF_READ_ONLY);
    }

    /**
     * Sets the Required flag, specifying whether or not the field must be filled in.
     *
     * @param <T>      an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param required if <code>true</code>, then the field must be filled in.
     * @return the edited field
     */
    public <T extends PdfFormField> T setRequired(boolean required) {
        return setFieldFlag(FF_REQUIRED, required);
    }

    /**
     * Gets the Required flag, specifying whether or not the field must be filled in.
     *
     * @return <code>true</code> if the field must be filled in.
     */
    public boolean isRequired() {
        return getFieldFlag(FF_REQUIRED);
    }

    /**
     * Sets the NoExport flag, specifying whether or not exporting is forbidden.
     *
     * @param <T>      an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param noExport if <code>true</code>, then exporting is <em>forbidden</em>
     * @return the edited field
     */
    public <T extends PdfFormField> T setNoExport(boolean noExport) {
        return setFieldFlag(FF_NO_EXPORT, noExport);
    }

    /**
     * Gets the NoExport attribute.
     *
     * @return whether exporting the value following a form action is forbidden.
     */
    public boolean isNoExport() {
        return getFieldFlag(FF_NO_EXPORT);
    }

    /**
     * Specifies on which page the form field's widget must be shown.
     *
     * @param <T>     an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param pageNum the page number
     * @return the edited field
     */
    public <T extends PdfFormField> T setPage(int pageNum) {
        if (!getWidgets().isEmpty()) {
            PdfAnnotation annot = getWidgets().get(0);
            if (annot != null) {
                annot.setPage(getDocument().getPage(pageNum));
            }
        }
        return (T) this;
    }

    /**
     * Gets the appearance state names.
     *
     * @return an array of Strings containing the names of the appearance states
     */
    public String[] getAppearanceStates() {
        Set<String> names = new HashSet<>();
        PdfString stringOpt = getPdfObject().getAsString(PdfName.Opt);
        if (stringOpt != null) {
            names.add(stringOpt.toUnicodeString());
        } else {
            PdfArray arrayOpt = getPdfObject().getAsArray(PdfName.Opt);
            if (arrayOpt != null) {
                for (PdfObject pdfObject : arrayOpt) {
                    PdfString valStr = null;

                    if (pdfObject.isArray()) {
                        valStr = ((PdfArray) pdfObject).getAsString(1);
                    } else if (pdfObject.isString()) {
                        valStr = (PdfString) pdfObject;
                    }
                    if (valStr != null) {
                        names.add(valStr.toUnicodeString());
                    }
                }
            }
        }

        PdfDictionary dic = getPdfObject();
        dic = dic.getAsDictionary(PdfName.AP);
        if (dic != null) {
            dic = dic.getAsDictionary(PdfName.N);
            if (dic != null) {
                for (PdfName state : dic.keySet()) {
                    names.add(state.getValue());
                }
            }
        }


        PdfArray kids = getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                PdfDictionary kidDic;
                if (kid.isIndirectReference()) {
                    kidDic = (PdfDictionary) ((PdfIndirectReference) kid).getRefersTo();
                } else {
                    kidDic = (PdfDictionary) kid;
                }
                PdfFormField fld = new PdfFormField(kidDic);
                String[] states = fld.getAppearanceStates();
                for (String state : states) {
                    names.add(state);
                }
            }
        }
        String out[] = new String[names.size()];

        return names.toArray(out);
    }

    /**
     * Sets an appearance for (the widgets related to) the form field.
     *
     * @param <T>              an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param appearanceType   the type of appearance stream to be added
     *                         <ul>
     *                         <li> PdfName.N: normal appearance</li>
     *                         <li> PdfName.R: rollover appearance</li>
     *                         <li> PdfName.D: down appearance</li>
     *                         </ul>
     * @param appearanceState  the state of the form field that needs to be true
     *                         for the appearance to be used. Differentiates between several streams
     *                         of the same type.
     * @param appearanceStream the appearance instructions, as a {@link PdfStream}
     * @return the edited field
     */
    public <T extends PdfFormField> T setAppearance(PdfName appearanceType, String appearanceState, PdfStream appearanceStream) {
        PdfWidgetAnnotation widget = getWidgets().get(0);
        PdfDictionary dic;
        if (widget != null) {
            dic = widget.getPdfObject();
        } else {
            dic = getPdfObject();
        }
        PdfDictionary ap = dic.getAsDictionary(PdfName.AP);
        if (ap != null) {
            PdfDictionary appearanceDictionary = ap.getAsDictionary(appearanceType);
            if (appearanceDictionary == null) {
                ap.put(appearanceType, appearanceStream);
            } else {
                appearanceDictionary.put(new PdfName(appearanceState), appearanceStream);
            }
        }

        return (T) this;
    }

    /**
     * Releases underlying pdf object and other pdf entities used by wrapper.
     * This method should be called instead of direct call to {@link PdfObject#release()} if the wrapper is used.
     */
    public void release() {
        unsetForbidRelease();
        getPdfObject().release();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    protected Rectangle getRect(PdfDictionary field) {
        PdfArray rect = field.getAsArray(PdfName.Rect);
        if (rect == null) {
            PdfArray kids = field.getAsArray(PdfName.Kids);
            if (kids == null) {
                throw new PdfException(PdfException.WrongFormFieldAddAnnotationToTheField);
            }
            rect = ((PdfDictionary) kids.get(0)).getAsArray(PdfName.Rect);
        }

        return rect.toRectangle();
    }

    protected static PdfArray processOptions(String options[][]) {
        PdfArray array = new PdfArray();
        for (String[] option : options) {
            PdfArray subArray = new PdfArray(new PdfString(option[0]));
            subArray.add(new PdfString(option[1]));
            array.add(subArray);
        }
        return array;
    }

    protected static PdfArray processOptions(String options[]) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option));
        }
        return array;
    }

    protected String generateDefaultAppearanceString(PdfFont font, int fontSize, PdfResources res) {
        PdfStream stream = new PdfStream();
        PdfCanvas canvas = new PdfCanvas(stream, res, getDocument());
        canvas.setFontAndSize(font, fontSize).resetFillColorRgb();
        return new String(stream.getBytes());
    }

    protected Object[] getFontAndSize(PdfDictionary asNormal) throws IOException {
        Object[] fontAndSize = new Object[2];
        PdfDictionary resources = null;
        if (asNormal != null) {
            resources = asNormal.getAsDictionary(PdfName.Resources);
        }
        if (resources != null) {
            PdfDictionary fontDic = resources.getAsDictionary(PdfName.Font);
            if (fontDic != null) {
                String str = getDefaultAppearance().toUnicodeString();
                Object[] dab = splitDAelements(str);
                PdfName fontName = new PdfName(dab[DA_FONT].toString());
                if (font != null) {
                    fontAndSize[0] = font;
                } else {
                    fontAndSize[0] = PdfFontFactory.createFont(fontDic.getAsDictionary(fontName));
                }
                if (fontSize != 0) {
                    fontAndSize[1] = fontSize;
                } else {
                    fontAndSize[1] = dab[DA_SIZE];
                }
                if (color == null) {
                    color = (Color) dab[DA_COLOR];
                }
            } else {
                if (font != null) {
                    fontAndSize[0] = font;
                } else {
                    fontAndSize[0] = PdfFontFactory.createFont();
                }
                if (fontSize != 0) {
                    fontAndSize[1] = fontSize;
                } else {
                    fontAndSize[1] = DEFAULT_FONT_SIZE;
                }
            }
        } else {
            if (font != null) {
                fontAndSize[0] = font;
            } else {
                fontAndSize[0] = PdfFontFactory.createFont();
            }
            if (fontSize != 0) {
                fontAndSize[1] = fontSize;
            } else {
                fontAndSize[1] = DEFAULT_FONT_SIZE;
            }
        }

        return fontAndSize;
    }

    protected static Object[] splitDAelements(String da) {
        PdfTokenizer tk = new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(PdfEncodings.convertToBytes(da, null))));
        List<String> stack = new ArrayList<>();
        Object ret[] = new Object[3];
        try {
            while (tk.nextToken()) {
                if (tk.getTokenType() == PdfTokenizer.TokenType.Comment)
                    continue;
                if (tk.getTokenType() == PdfTokenizer.TokenType.Other) {
                    String operator = tk.getStringValue();
                    if (operator.equals("Tf")) {
                        if (stack.size() >= 2) {
                            ret[DA_FONT] = stack.get(stack.size() - 2);
                            ret[DA_SIZE] = new Integer(stack.get(stack.size() - 1));
                        }
                    } else if (operator.equals("g")) {
                        if (stack.size() >= 1) {
                            float gray = new Float(stack.get(stack.size() - 1));
                            if (gray != 0) {
                                ret[DA_COLOR] = new DeviceGray(gray);
                            }
                        }
                    } else if (operator.equals("rg")) {
                        if (stack.size() >= 3) {
                            float red = new Float(stack.get(stack.size() - 3));
                            float green = new Float(stack.get(stack.size() - 2));
                            float blue = new Float(stack.get(stack.size() - 1));
                            ret[DA_COLOR] = new DeviceRgb(red, green, blue);
                        }
                    } else if (operator.equals("k")) {
                        if (stack.size() >= 4) {
                            float cyan = new Float(stack.get(stack.size() - 4));
                            float magenta = new Float(stack.get(stack.size() - 3));
                            float yellow = new Float(stack.get(stack.size() - 2));
                            float black = new Float(stack.get(stack.size() - 1));
                            ret[DA_COLOR] = new DeviceCmyk(cyan, magenta, yellow, black);
                        }
                    }
                    stack.clear();
                } else {
                    stack.add(tk.getStringValue());
                }
            }
        } catch (IOException e) {

        }
        return ret;
    }

    /**
     * Draws the visual appearance of text in a form field.
     *
     * @param rect     the location on the page for the list field
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @param value    the initial value
     * @return the {@link PdfFormXObject Form XObject} that was drawn
     */
    protected void drawTextAppearance(Rectangle rect, PdfFont font, int fontSize, String value, PdfFormXObject appearance) {
        PdfStream stream = new PdfStream().makeIndirect(getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, getDocument());

        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize, resources));

        float height = rect.getHeight();
        float width = rect.getWidth();
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        drawBorder(canvas, xObject, width, height);
        if (isPassword()) {
            value = obfuscatePassword(value);
        }

        canvas.
                beginVariableText().
                saveState().
                newPath();

        Paragraph paragraph = new Paragraph(value).setFont(font).setFontSize(fontSize).setMultipliedLeading(1).setPaddings(0, 2, 0, 2);
        if (color != null) {
            paragraph.setFontColor(color);
        }
        Integer justification = getJustification();
        if (justification == null) {
            justification = 0;
        }
        float x = 0;
        Property.TextAlignment textAlignment = Property.TextAlignment.LEFT;
        if (justification == ALIGN_RIGHT) {
            textAlignment = Property.TextAlignment.RIGHT;
            x = rect.getWidth();
        } else if (justification == ALIGN_CENTER) {
            textAlignment = Property.TextAlignment.CENTER;
            x = rect.getWidth() / 2;
        }
        new Canvas(canvas, getDocument(), new Rectangle(0, -height, 0, 2 * height)).showTextAligned(paragraph, x, rect.getHeight() / 2, textAlignment, Property.VerticalAlignment.MIDDLE);

        canvas.
                restoreState().
                endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
    }

    /**
     * Draws the visual appearance of multiline text in a form field.
     *
     * @param rect     the location on the page for the list field
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @param value    the initial value
     */
    protected void drawMultiLineTextAppearance(Rectangle rect, PdfFont font, int fontSize, String value, PdfFormXObject appearance) {
        PdfStream stream = new PdfStream().makeIndirect(getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, getDocument());

        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize, resources));

        float width = rect.getWidth();
        float height = rect.getHeight();

        List<String> strings = font.splitString(value, fontSize, width - 6);

        drawBorder(canvas, appearance, width, height);
        canvas.
                beginVariableText().
                saveState().
                rectangle(3, 3, width - 6, height - 6).
                clip().
                newPath();

        Canvas modelCanvas = new Canvas(canvas, getDocument(), new Rectangle(3, 0, Math.max(0, width - 6), Math.max(0, height - 2)));
        for (int index = 0; index < strings.size(); index++) {
            Boolean isFull = modelCanvas.getRenderer().getPropertyAsBoolean(Property.FULL);
            if (isFull != null && isFull) {
                break;
            }
            Paragraph paragraph = new Paragraph(strings.get(index)).setFont(font).setFontSize(fontSize).setMargins(0, 0, 0, 0).setMultipliedLeading(1);
            paragraph.setProperty(Property.FORCED_PLACEMENT, true);

            if (color != null) {
                paragraph.setFontColor(color);
            }
            PdfArray indices = getPdfObject().getAsArray(PdfName.I);
            if (indices != null && indices.size() > 0) {
                for (PdfObject ind : indices) {
                    if (!ind.isNumber())
                        continue;
                    if (((PdfNumber)ind).getValue() == index) {
                        paragraph.setBackgroundColor(new DeviceRgb(10, 36, 106));
                        paragraph.setFontColor(Color.LIGHT_GRAY);
                    }
                }
            }
            modelCanvas.add(paragraph);
        }
        canvas.
                restoreState().
                endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
    }

    /**
     * Draws a border using the borderWidth and borderColor of the form field.
     *
     * @param canvas the {@link PdfCanvas} on which to draw
     * @param width  the width of the rectangle to draw
     * @param height the height of the rectangle to draw
     */
    protected void drawBorder(PdfCanvas canvas, PdfFormXObject xObject, float width, float height) {
        canvas.saveState();
        if (borderWidth < 0) {
            borderWidth = 0;
        }
        if (borderColor == null) {
            borderColor = Color.BLACK;
        }

        if (backgroundColor != null) {
            canvas.
                    setFillColor(backgroundColor).
                    rectangle(borderWidth / 2, borderWidth / 2, width - borderWidth, height - borderWidth).
                    fill();
        }

        if (borderWidth > 0) {
            borderWidth = Math.max(1, borderWidth);
            canvas.
                    setStrokeColor(borderColor).
                    setLineWidth(borderWidth).
                    rectangle(0, 0, width, height).
                    stroke();
        }

        applyRotation(xObject, height, width);
        canvas.restoreState();
    }

    /**
     * Draws the appearance of a radio button with a specified value.
     *
     * @param width  the width of the radio button to draw
     * @param height the height of the radio button to draw
     * @param value  the value of the button
     */
    protected void drawRadioAppearance(float width, float height, String value) {
        PdfStream streamOn = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);

        drawBorder(canvasOn, xObjectOn, width, height);
        drawRadioField(canvasOn, 0, 0, width, height, true);

        PdfStream streamOff = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        drawBorder(canvasOff, xObjectOff, width, height);

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        widget.setNormalAppearance(new PdfDictionary());
        widget.getNormalAppearanceObject().put(new PdfName(value), xObjectOn.getPdfObject());

        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        widget.getNormalAppearanceObject().put(new PdfName("Off"), xObjectOff.getPdfObject());
    }

    /**
     * Draws a radio button.
     *
     * @param canvas the {@link PdfCanvas} on which to draw
     * @param width  the width of the radio button to draw
     * @param height the height of the radio button to draw
     * @param on     required to be <code>true</code> for fulfilling the drawing operation
     */
    protected void drawRadioField(PdfCanvas canvas, final float x, final float y, final float width, final float height, final boolean on) {
        canvas.saveState();
        if (on) {
            canvas.
                    resetFillColorRgb().
                    circle(width / 2, height / 2, Math.min(width, height) / 4).
                    fill();
        }
        canvas.restoreState();
    }

    /**
     * Draws the appearance of a checkbox with a specified state value.
     *
     * @param width  the width of the checkbox to draw
     * @param height the height of the checkbox to draw
     * @param value  the state of the form field that will be drawn
     */
    protected void drawCheckAppearance(float width, float height, String value) {
        PdfStream streamOn = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);

        drawBorder(canvasOn, xObjectOn, width, height);
        drawCheckBox(canvasOn, width, height, DEFAULT_FONT_SIZE, true);

        PdfStream streamOff = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        drawBorder(canvasOff, xObjectOff, width, height);
        drawCheckBox(canvasOff, width, height, DEFAULT_FONT_SIZE, false);

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        xObjectOn.getResources().addFont(getDocument(), getFont());
        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize == 0 ? DEFAULT_FONT_SIZE : fontSize, xObjectOn.getResources()));

        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        xObjectOff.getResources().addFont(getDocument(), getFont());

        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(value), xObjectOn.getPdfObject());
        normalAppearance.put(new PdfName("Off"), xObjectOff.getPdfObject());

        PdfDictionary mk = new PdfDictionary();
        mk.put(PdfName.CA, new PdfString(text));
        widget.put(PdfName.MK, mk);
        widget.setNormalAppearance(normalAppearance);
    }

    /**
     * Draws the appearance for a push button.
     *
     * @param width    the width of the pushbutton
     * @param height   the width of the pushbutton
     * @param text     the text to display on the button
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     * @return a new {@link PdfFormXObject}
     */
    protected PdfFormXObject drawPushButtonAppearance(float width, float height, String text, PdfFont font, int fontSize) {
        PdfStream stream = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        if (backgroundColor == null) {
            backgroundColor = Color.LIGHT_GRAY;
        }
        drawBorder(canvas, xObject, width, height);

        if (img != null) {
            PdfImageXObject imgXObj = new PdfImageXObject(img);
            canvas.addXObject(imgXObj, width - borderWidth, 0, 0, height - borderWidth, borderWidth / 2, borderWidth / 2);
            xObject.getResources().addImage(imgXObj);
        } else if (form != null) {
            canvas.addXObject(form, (height - borderWidth) / form.getHeight(), 0, 0, (height - borderWidth) / form.getHeight(), borderWidth / 2, borderWidth / 2);
            xObject.getResources().addForm(form);
        } else {
            drawButton(canvas, 0, 0, width, height, text, font, fontSize);
            setDefaultAppearance(generateDefaultAppearanceString(font, fontSize, new PdfResources()));
            xObject.getResources().addFont(getDocument(), font);
        }
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());

        return xObject;
    }

    /**
     * Performs the low-level drawing operations to draw a button object.
     *
     * @param canvas   the {@link PdfCanvas} of the page to draw on.
     * @param x        the x coordinate of the lower left corner of the button rectangle
     * @param y        the y coordinate of the lower left corner of the button rectangle
     * @param width    the width of the button
     * @param height   the width of the button
     * @param text     the text to display on the button
     * @param font     a {@link PdfFont}
     * @param fontSize a positive integer
     */
    protected void drawButton(PdfCanvas canvas, float x, float y, float width, float height, String text, PdfFont font, int fontSize) {
        if (color == null) {
            color = Color.BLACK;
        }

        Paragraph paragraph = new Paragraph(text).setFont(font).setFontSize(fontSize).setMargin(0).setMultipliedLeading(1).
                setVerticalAlignment(Property.VerticalAlignment.MIDDLE);
        new Canvas(canvas, getDocument(), new Rectangle(0, -height, width, 2 * height)).showTextAligned(paragraph, width / 2, height / 2, Property.TextAlignment.CENTER, Property.VerticalAlignment.MIDDLE);
    }

    /**
     * Performs the low-level drawing operations to draw a checkbox object.
     *
     * @param canvas   the {@link PdfCanvas} of the page to draw on.
     * @param width    the width of the button
     * @param height   the width of the button
     * @param fontSize a positive integer
     * @param on       the boolean value of the checkbox
     */
    protected void drawCheckBox(PdfCanvas canvas, float width, float height, int fontSize, boolean on) {
        if (!on) {
            return;
        }

        if (checkType == TYPE_CROSS) {
            float offset = borderWidth * 2;
            canvas.
                    moveTo((width - height) / 2 + offset, height - offset).
                    lineTo((width + height) / 2 - offset, offset).
                    moveTo((width + height) / 2 - offset, height - offset).
                    lineTo((width - height) / 2 + offset, offset).
                    stroke();
            return;
        }
        PdfFont ufont = getFont();
        // PdfFont gets all width in 1000 normalized units
        canvas.
                beginText().
                setFontAndSize(ufont, fontSize).
                resetFillColorRgb().
                setTextMatrix((width - ufont.getWidth(text, fontSize)) / 2, (height - ufont.getAscent(text, fontSize)) / 2).
                showText(text).
                endText();
    }

    private PdfName getTypeFromParent(PdfDictionary field) {
        PdfDictionary parent = field.getAsDictionary(PdfName.Parent);
        PdfName formType = field.getAsName(PdfName.FT);
        if (parent != null) {
            formType = parent.getAsName(PdfName.FT);
            if (formType == null) {
                formType = getTypeFromParent(parent);
            }
        }
        return formType;
    }

    private String obfuscatePassword(String text) {
        char[] pchar = new char[text.length()];
        for (int i = 0; i < text.length(); i++)
            pchar[i] = '*';
        return new String(pchar);
    }

    private void applyRotation(PdfFormXObject xObject, float height, float width) {
        switch (rotation) {
            case 90:
                xObject.getPdfObject().put(PdfName.Matrix, new PdfArray(new float[]{0, 1, -1, 0, height, 0}));
                break;
            case 180:
                xObject.getPdfObject().put(PdfName.Matrix, new PdfArray(new float[]{-1, 0, 0, -1, width, height}));
                break;
            case 270:
                xObject.getPdfObject().put(PdfName.Matrix, new PdfArray(new float[]{0, -1, 1, 0, 0, width}));
                break;
        }
    }

    private String optionsArrayToString(PdfArray options) {
        String value = "";
        for (PdfObject obj : options) {
            if (obj.isString()) {
                value += ((PdfString)obj).toUnicodeString() + '\n';
            }
            else if (obj.isArray()) {
                PdfObject element = ((PdfArray)obj).get(1);
                if (element.isString()) {
                    value += ((PdfString)element).toUnicodeString() + '\n';
                }
            }
        }
        value = value.substring(0, value.length() - 1);

        return value;
    }
}
