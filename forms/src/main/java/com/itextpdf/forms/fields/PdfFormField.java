/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a single field or field group in an {@link com.itextpdf.forms.PdfAcroForm
 * AcroForm}.
 * <p>
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
    public static final int MIN_FONT_SIZE = 4;
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
    public static final int VISIBLE = 4;

    public static final int FF_READ_ONLY = makeFieldFlag(1);
    public static final int FF_REQUIRED = makeFieldFlag(2);
    public static final int FF_NO_EXPORT = makeFieldFlag(3);

    public static final float X_OFFSET = 2;

    protected static String[] typeChars = {"4", "l", "8", "u", "n", "H"};

    protected String text;
    protected ImageData img;
    protected PdfFont font;
    protected float fontSize = -1;
    protected Color color;
    protected int checkType;
    protected float borderWidth = 0;
    protected Color backgroundColor;
    protected Color borderColor;
    protected int rotation = 0;
    protected PdfFormXObject form;
    protected PdfAConformanceLevel pdfAConformanceLevel;

    protected static final String check = "0.8 0 0 0.8 0.3 0.5 cm 0 0 m\n" +
            "0.066 -0.026 l\n" +
            "0.137 -0.15 l\n" +
            "0.259 0.081 0.46 0.391 0.553 0.461 c\n" +
            "0.604 0.489 l\n" +
            "0.703 0.492 l\n" +
            "0.543 0.312 0.255 -0.205 0.154 -0.439 c\n" +
            "0.069 -0.399 l\n" +
            "0.035 -0.293 -0.039 -0.136 -0.091 -0.057 c\n" +
            "h\n" +
            "f\n";

    protected static final String circle = "1 0 0 1 0.86 0.5 cm 0 0 m\n" +
            "0 0.204 -0.166 0.371 -0.371 0.371 c\n" +
            "-0.575 0.371 -0.741 0.204 -0.741 0 c\n" +
            "-0.741 -0.204 -0.575 -0.371 -0.371 -0.371 c\n" +
            "-0.166 -0.371 0 -0.204 0 0 c\n" +
            "f\n";


    protected static final String cross = "1 0 0 1 0.80 0.8 cm 0 0 m\n" +
            "-0.172 -0.027 l\n" +
            "-0.332 -0.184 l\n" +
            "-0.443 -0.019 l\n" +
            "-0.475 -0.009 l\n" +
            "-0.568 -0.168 l\n" +
            "-0.453 -0.324 l\n" +
            "-0.58 -0.497 l\n" +
            "-0.59 -0.641 l\n" +
            "-0.549 -0.627 l\n" +
            "-0.543 -0.612 -0.457 -0.519 -0.365 -0.419 c\n" +
            "-0.163 -0.572 l\n" +
            "-0.011 -0.536 l\n" +
            "-0.004 -0.507 l\n" +
            "-0.117 -0.441 l\n" +
            "-0.246 -0.294 l\n" +
            "-0.132 -0.181 l\n" +
            "0.031 -0.04 l\n" +
            "h\n" +
            "f\n";

    protected static final String diamond = "1 0 0 1 0.5 0.12 cm 0 0 m\n" +
            "0.376 0.376 l\n" +
            "0 0.751 l\n" +
            "-0.376 0.376 l\n" +
            "h\n" +
            "f\n";

    protected static final String square = "1 0 0 1 0.835 0.835 cm 0 0 -0.669 -0.67 re\n" +
            "f\n";

    protected static final String star = "0.95 0 0 0.95 0.85 0.6 cm 0 0 m\n" +
            "-0.291 0 l\n" +
            "-0.381 0.277 l\n" +
            "-0.47 0 l\n" +
            "-0.761 0 l\n" +
            "-0.526 -0.171 l\n" +
            "-0.616 -0.448 l\n" +
            "-0.381 -0.277 l\n" +
            "-0.145 -0.448 l\n" +
            "-0.236 -0.171 l\n" +
            "h\n" +
            "f\n";

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
     *
     * @param pdfDocument The document
     */
    protected PdfFormField(PdfDocument pdfDocument) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
        PdfName formType = getFormType();
        if (formType != null) {
            put(PdfName.FT, formType);
        }
    }

    /**
     * Creates a form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget      The widget which will be a kid of the {@link PdfFormField}
     * @param pdfDocument The document
     */
    protected PdfFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
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
        return createEmptyField(doc, null);
    }

    /**
     * Creates an empty form field without a predefined set of layout or
     * behavior.
     *
     * @param doc                  the {@link PdfDocument} to create the field in
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfFormField}
     */
    public static PdfFormField createEmptyField(PdfDocument doc, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfFormField field = new PdfFormField(doc);
        field.pdfAConformanceLevel = pdfAConformanceLevel;
        return field;
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
        return createButton(doc, rect, flags, null);
    }

    /**
     * Creates an empty {@link PdfButtonFormField button form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the button field in
     * @param rect                 the location on the page for the button
     * @param flags                an <code>int</code>, containing a set of binary behavioral
     *                             flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                             flags you require.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButton(PdfDocument doc, Rectangle rect, int flags, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfButtonFormField field = new PdfButtonFormField(annot, doc);
        field.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }
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
        return createButton(doc, flags, null);
    }

    /**
     * Creates an empty {@link PdfButtonFormField button form field} with custom
     * behavior and layout.
     *
     * @param doc                  the {@link PdfDocument} to create the button field in
     * @param flags                an <code>int</code>, containing a set of binary behavioral
     *                             flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                             flags you require.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButton(PdfDocument doc, int flags, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfButtonFormField field = new PdfButtonFormField(doc);
        field.pdfAConformanceLevel = pdfAConformanceLevel;
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
        return createText(doc, (PdfAConformanceLevel) null);
    }

    /**
     * Creates an empty {@link PdfTextFormField text form field}.
     *
     * @param doc the {@link PdfDocument} to create the text field in
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfTextFormField textFormField = new PdfTextFormField(doc);
        textFormField.pdfAConformanceLevel = pdfAConformanceLevel;
        return textFormField;
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
            return createText(doc, rect, name, value, PdfFontFactory.createFont(), (float) DEFAULT_FONT_SIZE);
        } catch (IOException e) {
            throw new PdfException(e);
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
     * @param fontSize the size of the font
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, float fontSize) {
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
     * @param fontSize  the size of the font
     * @param multiline true for multiline text field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, float fontSize, boolean multiline) {
        return createText(doc, rect, name, value, font, fontSize, multiline, null);
    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     * value, with a specified font and font size.
     *
     * @param doc                  the {@link PdfDocument} to create the text field in
     * @param rect                 the location on the page for the text field
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param font                 a {@link PdfFont}
     * @param fontSize             the size of the font
     * @param multiline            true for multiline text field
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, float fontSize, boolean multiline, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfTextFormField field = new PdfTextFormField(annot, doc);

        field.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }

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
     * @param fontSize the size of the font
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createMultilineText(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, float fontSize) {
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
            return createText(doc, rect, name, value, PdfFontFactory.createFont(), (float) DEFAULT_FONT_SIZE, true);
        } catch (IOException e) {
            throw new PdfException(e);
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
        return createChoice(doc, flags, null);
    }

    /**
     * Creates an empty {@link PdfChoiceFormField choice form field}.
     *
     * @param doc                  the {@link PdfDocument} to create the choice field in
     * @param flags                an <code>int</code>, containing a set of binary behavioral
     *                             flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                             flags you require.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, int flags, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfChoiceFormField field = new PdfChoiceFormField(doc);
        field.pdfAConformanceLevel = pdfAConformanceLevel;
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
            return createChoice(doc, rect, name, value, PdfFontFactory.createFont(), (float) DEFAULT_FONT_SIZE, options, flags);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Creates a {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the choice field in
     * @param rect                 the location on the page for the choice field
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param options              an array of {@link PdfString} objects that each represent
     *                             the 'on' state of one of the choices.
     * @param flags                an <code>int</code>, containing a set of binary behavioral
     *                             flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                             flags you require.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, String name, String value, PdfArray options, int flags, PdfFont font, PdfAConformanceLevel pdfAConformanceLevel) {
        return createChoice(doc, rect, name, value, font, (float) DEFAULT_FONT_SIZE, options, flags, pdfAConformanceLevel);
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
     * @param fontSize the size of the font
     * @param options  an array of {@link PdfString} objects that each represent
     *                 the 'on' state of one of the choices.
     * @param flags    an <code>int</code>, containing a set of binary behavioral
     *                 flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                 flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, float fontSize, PdfArray options, int flags) {
        return createChoice(doc, rect, name, value, font, fontSize, options, flags, null);
    }

    /**
     * Creates a {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the choice field in
     * @param rect                 the location on the page for the choice field
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param font                 a {@link PdfFont}
     * @param fontSize             the size of the font
     * @param options              an array of {@link PdfString} objects that each represent
     *                             the 'on' state of one of the choices.
     * @param flags                an <code>int</code>, containing a set of binary behavioral
     *                             flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *                             flags you require.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, String name, String value, PdfFont font, float fontSize, PdfArray options, int flags, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfFormField field = new PdfChoiceFormField(annot, doc);
        field.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }

        field.font = font;
        field.fontSize = fontSize;
        field.put(PdfName.Opt, options);
        field.setFieldFlags(flags);
        field.setFieldName(name);
        field.getPdfObject().put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
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
        return createSignature(doc, (PdfAConformanceLevel) null);
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     *
     * @param doc                  the {@link PdfDocument} to create the signature field in
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfSignatureFormField signatureFormField = new PdfSignatureFormField(doc);
        signatureFormField.pdfAConformanceLevel = pdfAConformanceLevel;
        return signatureFormField;
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     *
     * @param doc  the {@link PdfDocument} to create the signature field in
     * @param rect the location on the page for the signature field
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc, Rectangle rect) {
        return createSignature(doc, rect, null);
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     *
     * @param doc                  the {@link PdfDocument} to create the signature field in
     * @param rect                 the location on the page for the signature field
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc, Rectangle rect, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfSignatureFormField signatureFormField = new PdfSignatureFormField(annot, doc);
        signatureFormField.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }
        return signatureFormField;
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
        return createRadioGroup(doc, name, value, null);
    }

    /**
     * Creates a {@link PdfButtonFormField radio group form field}.
     *
     * @param doc                  the {@link PdfDocument} to create the radio group in
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfButtonFormField radio group}
     */
    public static PdfButtonFormField createRadioGroup(PdfDocument doc, String name, String value, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfButtonFormField radio = createButton(doc, PdfButtonFormField.FF_RADIO);
        radio.setFieldName(name);
        radio.put(PdfName.V, new PdfName(value));
        radio.pdfAConformanceLevel = pdfAConformanceLevel;
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
     * Creates a generic {@link PdfFormField} that is added to a radio group.
     *
     * @param doc                  the {@link PdfDocument} to create the radio group in
     * @param rect                 the location on the page for the field
     * @param radioGroup           the radio button group that this field should belong to
     * @param value                the initial value
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfFormField}
     * @see #createRadioGroup(com.itextpdf.kernel.pdf.PdfDocument, java.lang.String, java.lang.String)
     */
    public static PdfFormField createRadioButton(PdfDocument doc, Rectangle rect, PdfButtonFormField radioGroup, String value, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfFormField radio = new PdfButtonFormField(annot, doc);
        radio.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }

        String name = radioGroup.getValue().toString().substring(1);
        if (name.equals(value)) {
            annot.setAppearanceState(new PdfName(value));
        } else {
            annot.setAppearanceState(new PdfName("Off"));
        }
        if (pdfAConformanceLevel != null && "1".equals(pdfAConformanceLevel.getPart())) {
            radio.drawPdfA1RadioAppearance(rect.getWidth(), rect.getHeight(), value);
        } else {
            radio.drawRadioAppearance(rect.getWidth(), rect.getHeight(), value);
        }

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
            field = createPushButton(doc, rect, name, caption, PdfFontFactory.createFont(), (float) DEFAULT_FONT_SIZE);
        } catch (IOException e) {
            throw new PdfException(e);
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
     * @param fontSize the size of the font
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption, PdfFont font, float fontSize) {
        return createPushButton(doc, rect, name, caption, font, fontSize, null);
    }

    /**
     * Creates a {@link PdfButtonFormField} as a push button without data, with
     * its caption in a custom font.
     *
     * @param doc                  the {@link PdfDocument} to create the radio group in
     * @param rect                 the location on the page for the field
     * @param name                 the name of the form field
     * @param caption              the text to display on the button
     * @param font                 a {@link PdfFont}
     * @param fontSize             the size of the font
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption, PdfFont font, float fontSize, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfButtonFormField field = new PdfButtonFormField(annot, doc);
        field.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }
        field.setPushButton(true);
        field.setFieldName(name);
        field.text = caption;
        field.font = font;
        field.fontSize = fontSize;

        PdfFormXObject xObject = field.drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), caption, font, fontSize);
        annot.setNormalAppearance(xObject.getPdfObject());

        PdfDictionary mk = new PdfDictionary();
        mk.put(PdfName.CA, new PdfString(caption));
        mk.put(PdfName.BG, new PdfArray(field.backgroundColor.getColorValue()));
        annot.setAppearanceCharacteristics(mk);

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
        return createCheckBox(doc, rect, name, value, checkType, null);
    }

    /**
     * Creates a {@link PdfButtonFormField} as a checkbox.
     *
     * @param doc                  the {@link PdfDocument} to create the radio group in
     * @param rect                 the location on the page for the field
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param checkType            the type of checkbox graphic to use.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfButtonFormField checkbox}
     */
    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect, String name, String value, int checkType, PdfAConformanceLevel pdfAConformanceLevel) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(rect);
        PdfButtonFormField check = new PdfButtonFormField(annot, doc);
        check.pdfAConformanceLevel = pdfAConformanceLevel;
        if (null != pdfAConformanceLevel) {
            annot.setFlag(PdfAnnotation.PRINT);
        }
        check.setCheckType(checkType);
        check.setFieldName(name);
        check.put(PdfName.V, new PdfName(value));
        annot.setAppearanceState(new PdfName(value));
        String pdfAVersion = pdfAConformanceLevel != null ? pdfAConformanceLevel.getPart() : "";
        switch (pdfAVersion) {
            case "1":
                check.drawPdfA1CheckAppearance(rect.getWidth(), rect.getHeight(), value.equals("Off") ? "Yes" : value, checkType);
                break;
            case "2":
                check.drawPdfA2CheckAppearance(rect.getWidth(), rect.getHeight(), value.equals("Off") ? "Yes" : value, checkType);
                break;
            case "3":
                check.drawPdfA2CheckAppearance(rect.getWidth(), rect.getHeight(), value.equals("Off") ? "Yes" : value, checkType);
                break;
            default:
                check.drawCheckAppearance(rect.getWidth(), rect.getHeight(), value.equals("Off") ? "Yes" : value);
                break;
        }

        return check;
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
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String name, String value, String[][] options) {
        try {
            return createComboBox(doc, rect, name, value, options, PdfFontFactory.createFont(), null);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Creates a {@link PdfChoiceFormField combobox} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the combobox in
     * @param rect                 the location on the page for the combobox
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param options              a two-dimensional array of Strings which will be converted
     *                             to a PdfArray.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField} as a combobox
     */
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String name, String value, String[][] options, PdfFont font, PdfAConformanceLevel pdfAConformanceLevel) {
        return createChoice(doc, rect, name, value, processOptions(options), PdfChoiceFormField.FF_COMBO, font, pdfAConformanceLevel);
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
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String name, String value, String[] options) {
        try {
            return createComboBox(doc, rect, name, value, options, PdfFontFactory.createFont(), null);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Creates a {@link PdfChoiceFormField combobox} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the combobox in
     * @param rect                 the location on the page for the combobox
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param options              an array of Strings which will be converted to a PdfArray.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField} as a combobox
     */
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String name, String value, String[] options, PdfFont font, PdfAConformanceLevel pdfAConformanceLevel) {
        return createChoice(doc, rect, name, value, processOptions(options), PdfChoiceFormField.FF_COMBO, font, pdfAConformanceLevel);
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
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String name, String value, String[][] options) {
        try {
            return createList(doc, rect, name, value, options, PdfFontFactory.createFont(), null);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Creates a {@link PdfChoiceFormField list field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the choice field in
     * @param rect                 the location on the page for the choice field
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param options              a two-dimensional array of Strings which will be converted
     *                             to a PdfArray.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField} as a list field
     */
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String name, String value, String[][] options, PdfFont font, PdfAConformanceLevel pdfAConformanceLevel) {
        return createChoice(doc, rect, name, value, processOptions(options), 0, font, pdfAConformanceLevel);
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
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String name, String value, String[] options) {
        try {
            return createList(doc, rect, name, value, options, PdfFontFactory.createFont(), null);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Creates a {@link PdfChoiceFormField list field} with custom
     * behavior and layout, on a specified location.
     *
     * @param doc                  the {@link PdfDocument} to create the list field in
     * @param rect                 the location on the page for the list field
     * @param name                 the name of the form field
     * @param value                the initial value
     * @param options              an array of Strings which will be converted to a PdfArray.
     * @param pdfAConformanceLevel the {@link PdfAConformanceLevel} of the document. {@code} null if it's no PDF/A document
     * @return a new {@link PdfChoiceFormField} as a list field
     */
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String name, String value, String[] options, PdfFont font, PdfAConformanceLevel pdfAConformanceLevel) {
        return createChoice(doc, rect, name, value, processOptions(options), 0, font, pdfAConformanceLevel);
    }

    /**
     * Creates a (subtype of) {@link PdfFormField} object. The type of the object
     * depends on the <code>FT</code> entry in the <code>pdfObject</code> parameter.
     *
     * @param pdfObject assumed to be either a {@link PdfDictionary}, or a
     *                  {@link PdfIndirectReference} to a {@link PdfDictionary}
     * @param document  the {@link PdfDocument} to create the field in
     * @return a new {@link PdfFormField}, or <code>null</code> if
     * <code>pdfObject</code> does not contain a <code>FT</code> entry
     */
    public static PdfFormField makeFormField(PdfObject pdfObject, PdfDocument document) {
        PdfFormField field = null;
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName formType = dictionary.getAsName(PdfName.FT);
            if (PdfName.Tx.equals(formType)) {
                field = new PdfTextFormField(dictionary);
            } else if (PdfName.Btn.equals(formType)) {
                field = new PdfButtonFormField(dictionary);
            } else if (PdfName.Ch.equals(formType)) {
                field = new PdfChoiceFormField(dictionary);
            } else if (PdfName.Sig.equals(formType)) {
                field = new PdfSignatureFormField(dictionary);
            } else {
                field = new PdfFormField(dictionary);
            }
        }
        if (field != null) {
            field.makeIndirect(document);
            if (document != null && document.getReader() != null && document.getReader().getPdfAConformanceLevel() != null) {
                field.pdfAConformanceLevel = document.getReader().getPdfAConformanceLevel();
            }
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
     * @param value of the field
     * @return the field
     */
    public PdfFormField setValue(String value) {
        PdfName ft = getFormType();
        if (ft == null || !ft.equals(PdfName.Btn)) {
            PdfArray kids = getKids();
            if (kids != null) {
                for (int i = 0; i < kids.size(); i++) {
                    PdfObject kid = kids.get(i);
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
     * @param value              of the field
     * @param generateAppearance set this flat to false if you want to keep the appearance of the field generated before
     * @return the field
     */
    public PdfFormField setValue(String value, boolean generateAppearance) {
        PdfName formType = getFormType();
        if (PdfName.Tx.equals(formType) || PdfName.Ch.equals(formType)) {
            put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
        } else if (PdfName.Btn.equals(formType)) {
            if ((getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) != 0) {
                try {
                    img = ImageDataFactory.create(Base64.decode(value));
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

        if (generateAppearance) {
            regenerateField();
        }

        this.setModified();
        return this;
    }

    /**
     * Set text field value with given font and size
     *
     * @param value    text value
     * @param font     a {@link PdfFont}
     * @param fontSize the size of the font
     * @return the edited field
     */
    public PdfFormField setValue(String value, PdfFont font, float fontSize) {
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
     * @param value   the field value
     * @param display the string that is used for the appearance. If <CODE>null</CODE>
     *                the <CODE>value</CODE> parameter will be used
     * @return the edited field
     */
    public PdfFormField setValue(String value, String display) {
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
        return this;
    }

    /**
     * Sets a parent {@link PdfFormField} for the current object.
     *
     * @param parent another form field that this field belongs to, usually a group field
     * @return the edited field
     */
    public PdfFormField setParent(PdfFormField parent) {
        return put(PdfName.Parent, parent.getPdfObject());
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
     * @param kid a new {@link PdfFormField} entry for the field's <code>Kids</code> array property
     * @return the edited field
     */
    public PdfFormField addKid(PdfFormField kid) {
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
     * @param kid a new {@link PdfWidgetAnnotation} entry for the field's <code>Kids</code> array property
     * @return the edited field
     */
    public PdfFormField addKid(PdfWidgetAnnotation kid) {
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
     * @param name the new field name, as a String
     * @return the edited field
     */
    public PdfFormField setFieldName(String name) {
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
            name = new PdfString(parentName + name.toUnicodeString(), PdfEncodings.UNICODE_BIG);
        }
        return name;
    }

    /**
     * Changes the alternate name of the field to the specified value. The
     * alternate is a descriptive name to be used by status messages etc.
     *
     * @param name the new alternate name, as a String
     * @return the edited field
     */
    public PdfFormField setAlternativeName(String name) {
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
     * @param name the new alternate name, as a String
     * @return the edited field
     */
    public PdfFormField setMappingName(String name) {
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
     * @param flag an <code>int</code> interpreted as a series of a binary flags
     * @return the edited field
     */
    public PdfFormField setFieldFlag(int flag) {
        return setFieldFlag(flag, true);
    }

    /**
     * Adds or removes a flag, or combination of flags, for the form field. This
     * method is intended to be used one flag at a time, but this is not
     * technically enforced. To <em>replace</em> the current value, use
     * {@link #setFieldFlags(int)}.
     *
     * @param flag  an <code>int</code> interpreted as a series of a binary flags
     * @param value if <code>true</code>, adds the flag(s). if <code>false</code>,
     *              removes the flag(s).
     * @return the edited field
     */
    public PdfFormField setFieldFlag(int flag, boolean value) {
        int flags = getFieldFlags();

        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }

        return setFieldFlags(flags);
    }

    /**
     * If true, the field can contain multiple lines of text; if false, the field's text is restricted to a single line.
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
     * @param flags an <code>int</code> interpreted as a series of a binary flags
     * @return the edited field
     */
    public PdfFormField setFieldFlags(int flags) {
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
            return f.intValue();
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
     * @param value the default value
     * @return the edited field
     */
    public PdfFormField setDefaultValue(PdfObject value) {
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
     * @param key    the dictionary key to use for storing the action
     * @param action the action
     * @return the edited field
     */
    public PdfFormField setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
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
     * @param options an array of {@link PdfString} objects that each represent
     *                the 'on' state of one of the choices.
     * @return the edited field
     */
    public PdfFormField setOptions(PdfArray options) {
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
            for (int i = 0; i < kids.size(); i++) {
                PdfObject kid = kids.get(i);
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
     * define such properties as the field's text size and color.
     *
     * @return the default appearance graphics, as a {@link PdfString}
     */
    public PdfString getDefaultAppearance() {
        PdfString defaultAppearance = getPdfObject().getAsString(PdfName.DA);
        if (defaultAppearance == null) {
            PdfDictionary parent = getParent();
            if (parent != null) {
                //If this is not merged form field we should get default appearance from the parent which actually is a
                //form field dictionary
                if (parent.containsKey(PdfName.FT)) {
                    defaultAppearance = parent.getAsString(PdfName.DA);
                }
            }
        }
        return defaultAppearance;
    }

    /**
     * Sets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field's text size and color.
     *
     * @param defaultAppearance a valid sequence of PDF content stream syntax
     * @return the edited field
     */
    public PdfFormField setDefaultAppearance(String defaultAppearance) {
        byte[] b = defaultAppearance.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        for (int k = 0; k < len; ++k) {
            if (b[k] == '\n')
                b[k] = 32;
        }
        getPdfObject().put(PdfName.DA, new PdfString(new String(b)));
        return this;
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
     * @param justification the value to set the justification attribute to
     * @return the edited field
     */
    public PdfFormField setJustification(int justification) {
        getPdfObject().put(PdfName.Q, new PdfNumber(justification));
        regenerateField();
        return this;
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
     * @param defaultStyleString a new default style for the form field
     * @return the edited field
     */
    public PdfFormField setDefaultStyle(PdfString defaultStyleString) {
        getPdfObject().put(PdfName.DS, defaultStyleString);
        return this;
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
     * @param richText a new rich text value
     * @return The edited PdfFormField
     */
    public PdfFormField setRichText(PdfObject richText) {
        getPdfObject().put(PdfName.RV, richText);
        return this;
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
     * Note that the font will be added to the document so ensure that the font is embedded
     * if it's a pdf/a document.
     *
     * @param font The new font to be set
     * @return The edited PdfFormField
     */
    public PdfFormField setFont(PdfFont font) {
        this.font = font;
        regenerateField();
        return this;
    }

    /**
     * Basic setter for the <code>fontSize</code> property. Regenerates the
     * field appearance after setting the new value.
     *
     * @param fontSize The new font size to be set
     * @return The edited PdfFormField
     */
    public PdfFormField setFontSize(float fontSize) {
        this.fontSize = fontSize;
        regenerateField();
        return this;
    }

    /**
     * Basic setter for the <code>fontSize</code> property. Regenerates the
     * field appearance after setting the new value.
     *
     * @param fontSize The new font size to be set
     * @return The edited PdfFormField
     */
    public PdfFormField setFontSize(int fontSize) {
        setFontSize((float) fontSize);
        return this;
    }

    /**
     * Combined setter for the <code>font</code> and <code>fontSize</code>
     * properties. Regenerates the field appearance after setting the new value.
     *
     * @param font     The new font to be set
     * @param fontSize The new font size to be set
     * @return The edited PdfFormField
     */
    public PdfFormField setFontAndSize(PdfFont font, int fontSize) {
        this.font = font;
        this.fontSize = fontSize;
        regenerateField();
        return this;
    }

    /**
     * Basic setter for the <code>backgroundColor</code> property. Regenerates
     * the field appearance after setting the new value.
     *
     * @param backgroundColor The new color to be set
     * @return The edited PdfFormField
     */
    public PdfFormField setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        PdfDictionary mk = getWidgets().get(0).getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
        }
        mk.put(PdfName.BG, new PdfArray(backgroundColor.getColorValue()));
        regenerateField();
        return this;
    }

    /**
     * Basic setter for the <code>degRotation</code> property. Regenerates
     * the field appearance after setting the new value.
     *
     * @param degRotation The new degRotation to be set
     * @return The edited PdfFormField
     */
    public PdfFormField setRotation(int degRotation) {
        if (degRotation % 90 != 0) {
            throw new IllegalArgumentException("degRotation.must.be.a.multiple.of.90");
        } else {
            degRotation %= 360;
            if (degRotation < 0) {
                degRotation += 360;
            }

            this.rotation = degRotation;
        }
        PdfDictionary mk = getWidgets().get(0).getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
            this.put(PdfName.MK, mk);
        }
        mk.put(PdfName.R, new PdfNumber(degRotation));

        this.rotation = degRotation;
        regenerateField();
        return this;
    }

    /**
     * Sets the action on all {@link PdfWidgetAnnotation widgets} of this form field.
     *
     * @param action The action
     * @return The edited field
     */
    public PdfFormField setAction(PdfAction action) {
        List<PdfWidgetAnnotation> widgets = getWidgets();
        if (widgets != null) {
            for (PdfWidgetAnnotation widget : widgets) {
                widget.setAction(action);
            }
        }
        return this;
    }

    /**
     * Changes the type of graphical marker used to mark a checkbox as 'on'.
     * Notice that in order to complete the change one should call
     * {@link #regenerateField() regenerateField} method
     *
     * @param checkType the new checkbox marker
     * @return The edited field
     */
    public PdfFormField setCheckType(int checkType) {
        if (checkType < TYPE_CHECK || checkType > TYPE_STAR) {
            checkType = TYPE_CROSS;
        }
        this.checkType = checkType;
        text = typeChars[checkType - 1];
        if (pdfAConformanceLevel != null) {
            return this;
        }
        try {
            font = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
        } catch (IOException e) {
            throw new PdfException(e);
        }
        return this;
    }

    /**
     * Set the visibility flags of the form field annotation
     * Options are: HIDDEN, HIDDEN_BUT_PRINTABLE, VISIBLE, VISIBLE_BUT_DOES_NOT_PRINT
     *
     * @param visibility visibility option
     * @return The edited field
     */
    public PdfFormField setVisibility(int visibility) {
        switch (visibility) {
            case HIDDEN:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.HIDDEN));
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.NO_VIEW));
                break;
            case VISIBLE:
            default:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT));
                break;
        }
        return this;
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

        PdfPage page = null;
        if (getWidgets().size() > 0) {
            page = getWidgets().get(0).getPage();
        }

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
                float fontSize = normalizeFontSize((float) fontAndSize[1], localFont, bBox, value);

                //Apply Page rotation
                int pageRotation = 0;
                if (page != null) {
                    pageRotation = page.getRotation();
                    //Clockwise, so negative
                    pageRotation *= -1;
                }
                PdfArray matrix;
                if (pageRotation % 90 == 0) {
                    //Cast angle to [-360, 360]
                    double angle = pageRotation % 360;
                    //Get angle in radians
                    angle = degreeToRadians(angle);
                    //rotate the bounding box
                    Rectangle rect = bBox.toRectangle();
                    //Calculate origin offset
                    double translationWidth = 0;
                    double translationHeight = 0;
                    if (angle >= -1 * Math.PI && angle <= -1 * Math.PI / 2) {
                        translationWidth = rect.getWidth();
                    }
                    if (angle <= -1 * Math.PI) {
                        translationHeight = rect.getHeight();
                    }

                    //Store rotation and translation in the matrix
                    matrix = new PdfArray(new double[]{Math.cos(angle), -Math.sin(angle), Math.sin(angle), Math.cos(angle), translationWidth, translationHeight});
                    //If the angle is a multiple of 90 and not a multiple of 180, height and width of the bounding box need to be switched
                    if (angle % (Math.PI / 2) == 0 && angle % (Math.PI) != 0) {
                        rect.setWidth(bBox.toRectangle().getHeight());
                        rect.setHeight(bBox.toRectangle().getWidth());
                    }
                    // Adapt origin
                    rect.setX(rect.getX() + (float) translationWidth);
                    rect.setY(rect.getY() + (float) translationHeight);
                    //Copy Bounding box
                    bBox = new PdfArray(rect);
                } else {
                    //Avoid NPE when handling corrupt pdfs
                    Logger logger = LoggerFactory.getLogger(PdfFormField.class);
                    logger.error(LogMessageConstant.INCORRECT_PAGEROTATION);
                    matrix = new PdfArray(new double[]{1, 0, 0, 1, 0, 0});
                }
                //Apply field rotation
                float fieldRotation = 0;
                if (this.getPdfObject().getAsDictionary(PdfName.MK) != null
                        && this.getPdfObject().getAsDictionary(PdfName.MK).get(PdfName.R) != null) {
                    fieldRotation = (float) this.getPdfObject().getAsDictionary(PdfName.MK).getAsFloat(PdfName.R);
                    //Get relative field rotation
                    fieldRotation += pageRotation;
                }
                if (fieldRotation % 90 == 0) {
                    //Cast angle to [-360, 360]
                    double angle = fieldRotation % 360;
                    //Get angle in radians
                    angle = degreeToRadians(angle);
                    //Calculate origin offset
                    double translationWidth = calculateTranslationWidthAfterFieldRot(bBox.toRectangle(), degreeToRadians(pageRotation), angle);
                    double translationHeight = calculateTranslationHeightAfterFieldRot(bBox.toRectangle(), degreeToRadians(pageRotation), angle);

                    //Concatenate rotation and translation into the matrix
                    Matrix currentMatrix = new Matrix(matrix.getAsNumber(0).floatValue(), matrix.getAsNumber(1).floatValue(), matrix.getAsNumber(2).floatValue(), matrix.getAsNumber(3).floatValue(), matrix.getAsNumber(4).floatValue(), matrix.getAsNumber(5).floatValue());
                    Matrix toConcatenate = new Matrix((float) Math.cos(angle), (float) (-Math.sin(angle)), (float) (Math.sin(angle)), (float) (Math.cos(angle)), (float) translationWidth, (float) translationHeight);
                    currentMatrix = currentMatrix.multiply(toConcatenate);
                    matrix = new PdfArray(new float[]{currentMatrix.get(0), currentMatrix.get(1), currentMatrix.get(3), currentMatrix.get(4), currentMatrix.get(6), currentMatrix.get(7)});

                    //Construct bounding box
                    Rectangle rect = bBox.toRectangle();
                    //If the angle is a multiple of 90 and not a multiple of 180, height and width of the bounding box need to be switched
                    if (angle % (Math.PI / 2) == 0 && angle % (Math.PI) != 0) {
                        rect.setWidth(bBox.toRectangle().getHeight());
                        rect.setHeight(bBox.toRectangle().getWidth());
                    }
                    rect.setX(rect.getX() + (float) translationWidth);
                    rect.setY(rect.getY() + (float) translationHeight);
                    //Copy Bounding box
                    bBox = new PdfArray(rect);
                }
                //Create appearance
                PdfFormXObject appearance = null;
                if (asNormal != null) {
                    appearance = new PdfFormXObject(asNormal);
                    appearance.setBBox(new PdfArray(new float[]{0, 0, bBox.toRectangle().getWidth(), bBox.toRectangle().getHeight()}));
                }
                if (appearance == null) {
                    appearance = new PdfFormXObject(new Rectangle(0, 0, bBox.toRectangle().getWidth(), bBox.toRectangle().getHeight()));
                }
                if (matrix != null) {
                    appearance.put(PdfName.Matrix, matrix);
                }
                //Create text appearance
                if (PdfName.Tx.equals(type)) {
                    if (!isMultiline()) {
                        drawTextAppearance(bBox.toRectangle(), localFont, fontSize, value, appearance);
                    } else {
                        drawMultiLineTextAppearance(bBox.toRectangle(), localFont, fontSize, value, appearance);
                    }

                } else {
                    if (!getFieldFlag(PdfChoiceFormField.FF_COMBO)) {
                        PdfNumber topIndex = this.getPdfObject().getAsNumber(PdfName.TI);
                        PdfArray options = getOptions();
                        if (null != options) {
                            PdfArray visibleOptions = null != topIndex ? new PdfArray(options.subList(topIndex.intValue(), options.size() - 1)) : (PdfArray) options.clone();
                            value = optionsArrayToString(visibleOptions);
                        }
                    }
                    drawMultiLineTextAppearance(bBox.toRectangle(), localFont, fontSize, value, appearance);
                }

                appearance.getResources().addFont(getDocument(), localFont);
                appearance.setModified();
                PdfDictionary ap = new PdfDictionary();
                ap.put(PdfName.N, appearance.getPdfObject());
                ap.setModified();
                put(PdfName.AP, ap);

                return true;
            } catch (IOException e) {
                throw new PdfException(e);
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
                        float fontSize = (float) fontAndSize[1];
                        appearance = drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), value, localFont, fontSize);
                        appearance.getResources().addFont(getDocument(), localFont);
                    }

                    if (apDic == null) {
                        apDic = new PdfDictionary();
                        put(PdfName.AP, apDic);
                    }
                    apDic.put(PdfName.N, appearance.getPdfObject());
                } catch (IOException e) {
                    throw new PdfException(e);
                }
            } else if ((ff & PdfButtonFormField.FF_RADIO) != 0) {
                PdfArray kids = getKids();
                if (null != kids) {
                    for (int i = 0; i < kids.size(); i++) {
                        PdfObject kid = kids.get(i);
                        PdfFormField field = new PdfFormField((PdfDictionary) kid);
                        PdfWidgetAnnotation widget = field.getWidgets().get(0);
                        PdfDictionary buttonValues = (PdfDictionary) field.getPdfObject().getAsDictionary(PdfName.AP).get(PdfName.N);
                        String state;
                        if (buttonValues.get(new PdfName(value)) != null) {
                            state = value;
                        } else {
                            state = "Off";
                        }
                        widget.setAppearanceState(new PdfName(state));
                    }
                }
            } else {
                Rectangle rect = getRect(getPdfObject());
                setCheckType(checkType);

                String pdfAVersion = pdfAConformanceLevel != null ? pdfAConformanceLevel.getPart() : "";
                switch (pdfAVersion) {
                    case "1":
                        drawPdfA1CheckAppearance(rect.getWidth(), rect.getHeight(), value, checkType);
                        break;
                    case "2":
                        drawPdfA2CheckAppearance(rect.getWidth(), rect.getHeight(), value, checkType);
                        break;
                    case "3":
                        drawPdfA2CheckAppearance(rect.getWidth(), rect.getHeight(), value, checkType);
                        break;
                    default:
                        drawCheckAppearance(rect.getWidth(), rect.getHeight(), value);
                        break;
                }
                PdfWidgetAnnotation widget = getWidgets().get(0);
                if (widget.getNormalAppearanceObject() != null && widget.getNormalAppearanceObject().containsKey(new PdfName(value))) {
                    widget.setAppearanceState(new PdfName(value));
                } else {
                    widget.setAppearanceState(new PdfName("Off"));
                }
            }
        }
        return true;
    }

    /**
     * According to spec (ISO-32000-1, 12.7.3.3) zero font size should interpretaded as auto size.
     */
    private float normalizeFontSize(float fs, PdfFont localFont, PdfArray bBox, String value) {
        if (fs == 0) {
            if (isMultiline()) {
                fontSize = DEFAULT_FONT_SIZE;
            } else {
                float height = bBox.toRectangle().getHeight() - borderWidth * 2;
                int[] fontBbox = localFont.getFontProgram().getFontMetrics().getBbox();
                fs = height / (fontBbox[2] - fontBbox[1]) * FontProgram.UNITS_NORMALIZATION;
                float baseWidth = localFont.getWidth(value, 1);
                float offsetX = Math.max(borderWidth + X_OFFSET, 1);
                if (baseWidth != 0) {
                    fs = Math.min(fs, (bBox.toRectangle().getWidth() - X_OFFSET * 2 * offsetX) / baseWidth);
                }
            }
        }
        if (fs < MIN_FONT_SIZE) {
            fs = MIN_FONT_SIZE;
        }
        return fs;
    }

    /**
     * Calculate the necessary height offset after applying field rotation
     * so that the origin of the bounding box is the lower left corner with respect to the field text.
     *
     * @param bBox             bounding box rectangle before rotation
     * @param pageRotation     rotation of the page
     * @param relFieldRotation rotation of the field relative to the page
     * @return translation value for height
     */
    private float calculateTranslationHeightAfterFieldRot(Rectangle bBox, double pageRotation, double relFieldRotation) {
        if (relFieldRotation == 0) {
            return 0.0f;
        }
        if (pageRotation == 0) {
            if (relFieldRotation == Math.PI / 2) {
                return bBox.getHeight();
            }
            if (relFieldRotation == Math.PI) {
                return bBox.getHeight();
            }

        }
        if (pageRotation == -Math.PI / 2) {
            if (relFieldRotation == -Math.PI / 2) {
                return bBox.getWidth() - bBox.getHeight();
            }
            if (relFieldRotation == Math.PI / 2) {
                return bBox.getHeight();
            }
            if (relFieldRotation == Math.PI) {
                return bBox.getWidth();
            }

        }
        if (pageRotation == -Math.PI) {
            if (relFieldRotation == -1 * Math.PI) {
                return bBox.getHeight();
            }
            if (relFieldRotation == -1 * Math.PI / 2) {
                return bBox.getHeight() - bBox.getWidth();
            }

            if (relFieldRotation == Math.PI / 2) {
                return bBox.getWidth();
            }
        }
        if (pageRotation == -3 * Math.PI / 2) {
            if (relFieldRotation == -3 * Math.PI / 2) {
                return bBox.getWidth();
            }
            if (relFieldRotation == -Math.PI) {
                return bBox.getWidth();
            }
        }

        return 0.0f;
    }

    /**
     * Calculate the necessary width offset after applying field rotation
     * so that the origin of the bounding box is the lower left corner with respect to the field text.
     *
     * @param bBox             bounding box rectangle before rotation
     * @param pageRotation     rotation of the page
     * @param relFieldRotation rotation of the field relative to the page
     * @return translation value for width
     */
    private float calculateTranslationWidthAfterFieldRot(Rectangle bBox, double pageRotation, double relFieldRotation) {
        if (relFieldRotation == 0) {
            return 0.0f;
        }
        if (pageRotation == 0 && (relFieldRotation == Math.PI || relFieldRotation == 3 * Math.PI / 2)) {
            return bBox.getWidth();
        }
        if (pageRotation == -Math.PI / 2) {
            if (relFieldRotation == -Math.PI / 2 || relFieldRotation == Math.PI) {
                return bBox.getHeight();
            }
        }

        if (pageRotation == -Math.PI) {
            if (relFieldRotation == -1 * Math.PI) {
                return bBox.getWidth();
            }
            if (relFieldRotation == -1 * Math.PI / 2) {
                return bBox.getHeight();
            }
            if (relFieldRotation == Math.PI / 2) {
                return -1 * (bBox.getHeight() - bBox.getWidth());
            }
        }
        if (pageRotation == -3 * Math.PI / 2) {
            if (relFieldRotation == -3 * Math.PI / 2) {
                return -1 * (bBox.getWidth() - bBox.getHeight());
            }
            if (relFieldRotation == -Math.PI) {
                return bBox.getHeight();
            }
            if (relFieldRotation == -Math.PI / 2) {
                return bBox.getWidth();
            }
        }
        return 0.0f;
    }

    /**
     * Gets the border width for the field.
     *
     * @return the current border width.
     */
    public float getBorderWidth() {
        PdfDictionary bs = getWidgets().get(0).getBorderStyle();
        if (bs != null) {
            PdfNumber w = bs.getAsNumber(PdfName.W);
            if (w != null) {
                borderWidth = w.floatValue();
            }
        }
        return borderWidth;
    }

    /**
     * Sets the border width for the field.
     *
     * @param borderWidth The new border width.
     * @return The edited field
     */
    public PdfFormField setBorderWidth(float borderWidth) {
        PdfDictionary bs = getWidgets().get(0).getBorderStyle();
        if (bs == null) {
            bs = new PdfDictionary();
            put(PdfName.BS, bs);
        }
        bs.put(PdfName.W, new PdfNumber(borderWidth));
        this.borderWidth = borderWidth;
        regenerateField();
        return this;
    }

    public PdfFormField setBorderStyle(PdfDictionary style) {
        //PdfDictionary bs = getWidgets().get(0).getBorderStyle();
        getWidgets().get(0).setBorderStyle(style);
//        if (bs == null) {
//            bs = new PdfDictionary();
//            put(PdfName.BS, bs);
//        }
//        bs.put(PdfName.S, style);
        regenerateField();
        return this;
    }

    /**
     * Sets the Border Color.
     *
     * @param color the new value for the Border Color
     * @return the edited field
     */
    public PdfFormField setBorderColor(Color color) {
        borderColor = color;
        PdfDictionary mk = getWidgets().get(0).getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
            put(PdfName.MK, mk);
        }
        mk.put(PdfName.BC, new PdfArray(color.getColorValue()));
        regenerateField();
        return this;
    }

    /**
     * Sets the text color.
     *
     * @param color the new value for the Color
     * @return the edited field
     */
    public PdfFormField setColor(Color color) {
        this.color = color;
        regenerateField();
        return this;
    }

    /**
     * Sets the ReadOnly flag, specifying whether or not the field can be changed.
     *
     * @param readOnly if <code>true</code>, then the field cannot be changed.
     * @return the edited field
     */
    public PdfFormField setReadOnly(boolean readOnly) {
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
     * @param required if <code>true</code>, then the field must be filled in.
     * @return the edited field
     */
    public PdfFormField setRequired(boolean required) {
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
     * @param noExport if <code>true</code>, then exporting is <em>forbidden</em>
     * @return the edited field
     */
    public PdfFormField setNoExport(boolean noExport) {
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
     * @param pageNum the page number
     * @return the edited field
     */
    public PdfFormField setPage(int pageNum) {
        if (getWidgets().size() > 0) {
            PdfAnnotation annot = getWidgets().get(0);
            if (annot != null) {
                annot.setPage(getDocument().getPage(pageNum));
            }
        }
        return this;
    }

    /**
     * Gets the appearance state names.
     *
     * @return an array of Strings containing the names of the appearance states
     */
    public String[] getAppearanceStates() {
        Set<String> names = new LinkedHashSet<>();
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
                PdfFormField fld = new PdfFormField((PdfDictionary) kid);
                String[] states = fld.getAppearanceStates();
                for (String state : states) {
                    names.add(state);
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * Sets an appearance for (the widgets related to) the form field.
     *
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
    public PdfFormField setAppearance(PdfName appearanceType, String appearanceState, PdfStream appearanceStream) {
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

        return this;
    }

    /**
     * Sets zero font size which will be interpreted as auto-size according to ISO 32000-1, 12.7.3.3.
     *
     * @return the edited field
     */
    public PdfFormField setFontSizeAutoScale() {
        this.fontSize = 0;
        regenerateField();
        return this;
    }

    public PdfFormField put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
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

    protected static PdfArray processOptions(String[][] options) {
        PdfArray array = new PdfArray();
        for (String[] option : options) {
            PdfArray subArray = new PdfArray(new PdfString(option[0], PdfEncodings.UNICODE_BIG));
            subArray.add(new PdfString(option[1], PdfEncodings.UNICODE_BIG));
            array.add(subArray);
        }
        return array;
    }

    protected static PdfArray processOptions(String[] options) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option, PdfEncodings.UNICODE_BIG));
        }
        return array;
    }

    protected String generateDefaultAppearanceString(PdfFont font, float fontSize, Color color, PdfResources res) {
        PdfStream stream = new PdfStream();
        PdfCanvas canvas = new PdfCanvas(stream, res, getDocument());
        canvas.setFontAndSize(font, fontSize);
        if (color != null)
            canvas.setColor(color, true);
        return new String(stream.getBytes());
    }

    protected Object[] getFontAndSize(PdfDictionary asNormal) throws IOException {
        Object[] fontAndSize = new Object[2];
        PdfDictionary normalResources = null;
        PdfDictionary defaultResources = null;
        PdfDocument document = getDocument();
        if (document != null) {
            PdfDictionary acroformDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
            if (acroformDictionary != null) {
                defaultResources = acroformDictionary.getAsDictionary(PdfName.DR);
            }
        }
        if (asNormal != null) {
            normalResources = asNormal.getAsDictionary(PdfName.Resources);
        }
        if (defaultResources != null || normalResources != null) {
            PdfDictionary normalFontDic = normalResources != null ? normalResources.getAsDictionary(PdfName.Font) : null;
            PdfDictionary defaultFontDic = defaultResources != null ? defaultResources.getAsDictionary(PdfName.Font) : null;
            PdfString defaultAppearance = getDefaultAppearance();
            if ((normalFontDic != null || defaultFontDic != null) && defaultAppearance != null) {
                Object[] dab = splitDAelements(defaultAppearance.toUnicodeString());
                PdfName fontName = new PdfName(dab[DA_FONT].toString());
                PdfDictionary requiredFontDictionary = null;
                if (normalFontDic != null && null != normalFontDic.getAsDictionary(fontName)) {
                    requiredFontDictionary = normalFontDic.getAsDictionary(fontName);
                } else if (defaultFontDic != null) {
                    requiredFontDictionary = defaultFontDic.getAsDictionary(fontName);
                }
                if (font != null) {
                    fontAndSize[0] = font;
                } else {
                    PdfFont dicFont = document != null ? document.getFont(requiredFontDictionary) : PdfFontFactory.createFont(requiredFontDictionary);
                    fontAndSize[0] = dicFont;
                }
                if (fontSize >= 0) {
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
                if (fontSize >= 0) {
                    fontAndSize[1] = fontSize;
                } else {
                    fontAndSize[1] = (float) DEFAULT_FONT_SIZE;
                }
            }
        } else {
            if (font != null) {
                fontAndSize[0] = font;
            } else {
                fontAndSize[0] = PdfFontFactory.createFont();
            }
            if (fontSize >= 0) {
                fontAndSize[1] = fontSize;
            } else {
                fontAndSize[1] = (float) DEFAULT_FONT_SIZE;
            }
        }

        return fontAndSize;
    }

    protected static Object[] splitDAelements(String da) {
        PdfTokenizer tk = new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(PdfEncodings.convertToBytes(da, null))));
        List<String> stack = new ArrayList<>();
        Object[] ret = new Object[3];
        try {
            while (tk.nextToken()) {
                if (tk.getTokenType() == PdfTokenizer.TokenType.Comment)
                    continue;
                if (tk.getTokenType() == PdfTokenizer.TokenType.Other) {
                    String operator = tk.getStringValue();
                    if (operator.equals("Tf")) {
                        if (stack.size() >= 2) {
                            ret[DA_FONT] = stack.get(stack.size() - 2);
                            ret[DA_SIZE] = new Float(stack.get(stack.size() - 1));
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
     * @param rect       The location on the page for the list field
     * @param font       a {@link PdfFont}
     * @param fontSize   The size of the font
     * @param value      The initial value
     * @param appearance The appearance
     */
    protected void drawTextAppearance(Rectangle rect, PdfFont font, float fontSize, String value, PdfFormXObject appearance) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, getDocument());

        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize, color, resources));

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

        float x = X_OFFSET;
        Integer justification = getJustification();
        if (justification == null) {
            justification = 0;
        }
        TextAlignment textAlignment = TextAlignment.LEFT;
        if (justification == ALIGN_RIGHT) {
            textAlignment = TextAlignment.RIGHT;
            x = rect.getWidth();
        } else if (justification == ALIGN_CENTER) {
            textAlignment = TextAlignment.CENTER;
            x = rect.getWidth() / 2;
        }

        Canvas modelCanvas = new Canvas(canvas, getDocument(), new Rectangle(0, -height, 0, 2 * height));
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, true);

        // check if /Comb has been set
        if (this.getFieldFlag(PdfTextFormField.FF_COMB)) {
            // calculate space per character
            PdfNumber maxLenEntry = this.getPdfObject().getAsNumber(PdfName.MaxLen);
            if (maxLenEntry == null) {
                throw new PdfException(PdfException.NoMaxLenPresent);
            }
            int maxLen = maxLenEntry.intValue();
            float widthPerCharacter = width / maxLen;

            Paragraph paragraph = new Paragraph().setFont(font).setFontSize(fontSize).setMultipliedLeading(1);
            if (color != null) {
                paragraph.setFontColor(color);
            }

            int numberOfCharacters = maxLen >= value.length() ? value.length() : maxLen;

            for (int i = 0; i < numberOfCharacters; i++) {
                // Get width of each character
                String characterToPlace = value.substring(i, i + 1);
                float characterWidth = font.getWidth(characterToPlace, fontSize);
                // Find x-offset for this character so that we can place it in the center of this comb-section
                float xOffset = characterWidth == 0 ? characterWidth : (widthPerCharacter - characterWidth) / 2;

                paragraph.setPaddings(0f, xOffset, 0f, xOffset);
                paragraph.add(characterToPlace);

                modelCanvas.showTextAligned(paragraph, widthPerCharacter * i, 0, textAlignment);

                paragraph.getChildren().remove(0);
            }
        } else {
            Paragraph paragraph = new Paragraph(value).setFont(font).setFontSize(fontSize).setMultipliedLeading(1).setPaddings(0, X_OFFSET, 0, X_OFFSET);
            if (color != null) {
                paragraph.setFontColor(color);
            }
            modelCanvas.showTextAligned(paragraph, x, rect.getHeight() / 2, textAlignment, VerticalAlignment.MIDDLE);
        }
        canvas.
                restoreState().
                endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
    }

    /**
     * Draws the visual appearance of multiline text in a form field.
     *
     * @param rect       The location on the page for the list field
     * @param font       a {@link PdfFont}
     * @param fontSize   The size of the font
     * @param value      The initial value
     * @param appearance The appearance
     */
    protected void drawMultiLineTextAppearance(Rectangle rect, PdfFont font, float fontSize, String value, PdfFormXObject appearance) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, getDocument());

        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize, color, resources));

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
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, true);
        for (int index = 0; index < strings.size(); index++) {
            Boolean isFull = modelCanvas.getRenderer().getPropertyAsBoolean(Property.FULL);
            if (Boolean.TRUE.equals(isFull)) {
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
                    if (((PdfNumber) ind).getValue() == index) {
                        paragraph.setBackgroundColor(new DeviceRgb(10, 36, 106));
                        paragraph.setFontColor(ColorConstants.LIGHT_GRAY);
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
     * @param canvas  The {@link PdfCanvas} on which to draw
     * @param xObject The PdfFormXObject
     * @param width   The width of the rectangle to draw
     * @param height  The height of the rectangle to draw
     */
    protected void drawBorder(PdfCanvas canvas, PdfFormXObject xObject, float width, float height) {
        canvas.saveState();
        float borderWidth = getBorderWidth();
        PdfDictionary bs = getWidgets().get(0).getBorderStyle();
        if (borderWidth < 0) {
            borderWidth = 0;
        }
        if (borderColor == null) {
            borderColor = ColorConstants.BLACK;
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
                    setLineWidth(borderWidth);
            if (bs != null) {
                PdfName borderType = bs.getAsName(PdfName.S);
                if (borderType != null && borderType.equals(PdfName.D)) {
                    PdfArray dashArray = bs.getAsArray(PdfName.D);
                    if (dashArray != null) {
                        int unitsOn = dashArray.getAsNumber(0) != null ? dashArray.getAsNumber(0).intValue() : 0;
                        int unitsOff = dashArray.getAsNumber(1) != null ? dashArray.getAsNumber(1).intValue() : 0;
                        canvas.setLineDash(unitsOn, unitsOff, 0);
                    }
                }
            }
            canvas.
                    rectangle(0, 0, width, height).
                    stroke();
        }

        applyRotation(xObject, height, width);
        canvas.restoreState();
    }

    protected void drawRadioBorder(PdfCanvas canvas, PdfFormXObject xObject, float width, float height) {
        canvas.saveState();
        float borderWidth = getBorderWidth();
        float cx = width / 2;
        float cy = height / 2;
        if (borderWidth < 0) {
            borderWidth = 0;
        }
        float r = (Math.min(width, height) - borderWidth) / 2;

        if (backgroundColor != null) {
            canvas.
                    setFillColor(backgroundColor).
                    circle(cx, cy, r + borderWidth / 2).
                    fill();
        }

        if (borderWidth > 0 && borderColor != null) {
            borderWidth = Math.max(1, borderWidth);
            canvas.
                    setStrokeColor(borderColor).
                    setLineWidth(borderWidth).
                    circle(cx, cy, r).
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
        PdfStream streamOn = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);

        drawRadioBorder(canvasOn, xObjectOn, width, height);
        drawRadioField(canvasOn, width, height, true);

        PdfStream streamOff = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        drawRadioBorder(canvasOff, xObjectOff, width, height);
        if (pdfAConformanceLevel != null && (pdfAConformanceLevel.getPart().equals("2") || pdfAConformanceLevel.getPart().equals("3"))) {
            xObjectOn.getResources();
            xObjectOff.getResources();
        }

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        widget.setNormalAppearance(new PdfDictionary());
        widget.getNormalAppearanceObject().put(new PdfName(value), xObjectOn.getPdfObject());

        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        widget.getNormalAppearanceObject().put(new PdfName("Off"), xObjectOff.getPdfObject());
    }

    /**
     * Draws the appearance of a radio button with a specified value.
     *
     * @param width  the width of the radio button to draw
     * @param height the height of the radio button to draw
     * @param value  the value of the button
     */
    protected void drawPdfA1RadioAppearance(float width, float height, String value) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObject = new PdfFormXObject(rect);


        drawBorder(canvas, xObject, width, height);
        drawRadioField(canvas, rect.getWidth(), rect.getHeight(), !value.equals("Off"));

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());
        widget.setNormalAppearance(xObject.getPdfObject());
    }

    /**
     * Draws a radio button.
     *
     * @param canvas the {@link PdfCanvas} on which to draw
     * @param width  the width of the radio button to draw
     * @param height the height of the radio button to draw
     * @param on     required to be <code>true</code> for fulfilling the drawing operation
     */
    protected void drawRadioField(PdfCanvas canvas, float width, float height, boolean on) {
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
        PdfStream streamOn = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);

        drawBorder(canvasOn, xObjectOn, width, height);
        drawCheckBox(canvasOn, width, height, (float) DEFAULT_FONT_SIZE, true);

        PdfStream streamOff = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        drawBorder(canvasOff, xObjectOff, width, height);
        drawCheckBox(canvasOff, width, height, (float) DEFAULT_FONT_SIZE, false);

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        xObjectOn.getResources().addFont(getDocument(), getFont());
        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize <= 0 ? (float) DEFAULT_FONT_SIZE : fontSize, color, xObjectOn.getResources()));

        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        xObjectOff.getResources().addFont(getDocument(), getFont());

        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(value), xObjectOn.getPdfObject());
        normalAppearance.put(new PdfName("Off"), xObjectOff.getPdfObject());

        PdfDictionary mk = new PdfDictionary();
        mk.put(PdfName.CA, new PdfString(text));
        widget.getPdfObject().put(PdfName.MK, mk);
        widget.setNormalAppearance(normalAppearance);
    }

    protected void drawPdfA1CheckAppearance(float width, float height, String value, int checkType) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObject = new PdfFormXObject(rect);

        this.checkType = checkType;
        drawBorder(canvas, xObject, width, height);
        drawPdfACheckBox(canvas, width, height, true);

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());

        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(value), xObject.getPdfObject());

        PdfDictionary mk = new PdfDictionary();
        mk.put(PdfName.CA, new PdfString(text));
        widget.put(PdfName.MK, mk);
        widget.setNormalAppearance(xObject.getPdfObject());
    }

    protected void drawPdfA2CheckAppearance(float width, float height, String value, int checkType) {
        PdfStream streamOn = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        PdfStream streamOff = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);

        this.checkType = checkType;
        drawBorder(canvasOn, xObjectOn, width, height);
        drawPdfACheckBox(canvasOn, width, height, true);
        drawBorder(canvasOff, xObjectOff, width, height);

        PdfWidgetAnnotation widget = getWidgets().get(0);

        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        xObjectOn.getResources();
        xObjectOff.getResources();

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
     * @param fontSize the size of the font
     * @return a new {@link PdfFormXObject}
     */
    protected PdfFormXObject drawPushButtonAppearance(float width, float height, String text, PdfFont font, float fontSize) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        if (backgroundColor == null) {
            backgroundColor = ColorConstants.LIGHT_GRAY;
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
            setDefaultAppearance(generateDefaultAppearanceString(font, fontSize, color, new PdfResources()));
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
     * @param fontSize the size of the font
     */
    protected void drawButton(PdfCanvas canvas, float x, float y, float width, float height, String text, PdfFont font, float fontSize) {
        if (color == null) {
            color = ColorConstants.BLACK;
        }

        Paragraph paragraph = new Paragraph(text).setFont(font).setFontSize(fontSize).setMargin(0).setMultipliedLeading(1).
                setVerticalAlignment(VerticalAlignment.MIDDLE);
        Canvas modelCanvas = new Canvas(canvas, getDocument(), new Rectangle(0, -height, width, 2 * height));
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, true);
        modelCanvas.showTextAligned(paragraph, width / 2, height / 2, TextAlignment.CENTER, VerticalAlignment.MIDDLE);
    }

    /**
     * Performs the low-level drawing operations to draw a checkbox object.
     *
     * @param canvas   the {@link PdfCanvas} of the page to draw on.
     * @param width    the width of the button
     * @param height   the width of the button
     * @param fontSize the size of the font
     * @param on       the boolean value of the checkbox
     */
    protected void drawCheckBox(PdfCanvas canvas, float width, float height, float fontSize, boolean on) {
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

    protected void drawPdfACheckBox(PdfCanvas canvas, float width, float height, boolean on) {
        if (!on)
            return;
        String appearanceString = check;
        switch (checkType) {
            case TYPE_CHECK:
                appearanceString = check;
                break;
            case TYPE_CIRCLE:
                appearanceString = circle;
                break;
            case TYPE_CROSS:
                appearanceString = cross;
                break;
            case TYPE_DIAMOND:
                appearanceString = diamond;
                break;
            case TYPE_SQUARE:
                appearanceString = square;
                break;
            case TYPE_STAR:
                appearanceString = star;
                break;
        }
        canvas.saveState();
        canvas.resetFillColorRgb();
        canvas.concatMatrix(width, 0, 0, height, 0, 0);
        canvas.getContentStream().getOutputStream().writeBytes(appearanceString.getBytes(StandardCharsets.ISO_8859_1));
        canvas.restoreState();
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

    private static String optionsArrayToString(PdfArray options) {
        StringBuffer stringBuffer = new StringBuffer();
        for (PdfObject obj : options) {
            if (obj.isString()) {
                stringBuffer.append(((PdfString) obj).toUnicodeString()).append('\n');
            } else if (obj.isArray()) {
                PdfObject element = ((PdfArray) obj).get(1);
                if (element.isString()) {
                    stringBuffer.append(((PdfString) element).toUnicodeString()).append('\n');
                }
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1); // last '\n'
        return stringBuffer.toString();
    }

    private static double degreeToRadians(double angle) {
        return Math.PI * angle / 180.0;
    }
}
