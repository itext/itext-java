package com.itextpdf.forms.fields;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.Base64;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.basics.io.PdfTokenizer;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.PdfCanvasConstants;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.color.DeviceCmyk;
import com.itextpdf.core.color.DeviceGray;
import com.itextpdf.core.color.DeviceRgb;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class represents a single field or field group in an {@link PdfAcroForm
 * AcroForm}.
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

    /** A field with the symbol check */
    public static final int TYPE_CHECK = 1;
    /** A field with the symbol circle */
    public static final int TYPE_CIRCLE = 2;
    /** A field with the symbol cross */
    public static final int TYPE_CROSS = 3;
    /** A field with the symbol diamond */
    public static final int TYPE_DIAMOND = 4;
    /** A field with the symbol square */
    public static final int TYPE_SQUARE = 5;
    /** A field with the symbol star */
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
    protected Color borderColor;

    /**
     * Creates a minimal {@link PdfFormField}.
     */
    protected PdfFormField() {
        this(new PdfDictionary());
        put(PdfName.FT, getFormType());
    }

    /**
     * Creates a form field as a parent of a {@link PdfWidgetAnnotation}.
     * @param widget the widget which will be a kid of the {@link PdfFormField}
     */
    protected PdfFormField(PdfWidgetAnnotation widget) {
        this(new PdfDictionary());
        addKid(widget);
        put(PdfName.FT, getFormType());
    }

    /**
     * Creates a form field as a wrapper object around a {@link PdfDictionary}.
     * @param pdfObject the dictionary to be wrapped
     */
    public PdfFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Makes a field flag by bit position. Bit positions are numbered 1 to 32.
     * But position 0 corresponds to flag 1, position 3 corresponds to flag 4 etc.
     * @param bitPosition bit position of a flag in range 1 to 32 from the pdf specification.
     * @return corresponding field flag.
     */
    public static int makeFieldFlag(int bitPosition) {
        return (1 << (bitPosition - 1));
    }

    /**
     * Creates an empty form field without a predefined set of layout or
     * behavior.
     * @param doc the {@link PdfDocument} to create the field in
     * @return a new {@link PdfFormField}
     */
    public static PdfFormField createEmptyField(PdfDocument doc) {
        PdfFormField field = new PdfFormField().makeIndirect(doc);
        return field;
    }

    /**
     * Creates an empty {@link PdfButtonFormField button form field} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the button field in
     * @param rect the location on the page for the button
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *     flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *     flags you require.
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButton(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfButtonFormField field = new PdfButtonFormField(annot).makeIndirect(doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates an empty {@link PdfButtonFormField button form field} with custom
     * behavior and layout.
     * @param doc the {@link PdfDocument} to create the button field in
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *     flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *     flags you require.
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButton(PdfDocument doc, int flags) {
        PdfButtonFormField field = new PdfButtonFormField().makeIndirect(doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates an empty {@link PdfTextFormField text form field}.
     * @param doc the {@link PdfDocument} to create the text field in
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc) {
        return new PdfTextFormField().makeIndirect(doc);
    }

    /**
     * Creates an empty {@link PdfTextFormField text form field}.
     * @param doc the {@link PdfDocument} to create the text field in
     * @param rect the location on the page for the text field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfTextFormField field = new PdfTextFormField(annot).makeIndirect(doc);

        return field;

    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     *     value, and the form's default font specified in
     *     {@link PdfAcroform#getDefaultResources}.
     * @param doc the {@link PdfDocument} to create the text field in
     * @param rect the location on the page for the text field
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String value, String name) {
        try{
            return createText(doc, rect, PdfFont.getDefaultFont(doc), DEFAULT_FONT_SIZE, value, name);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    /**
     * Creates a named {@link PdfTextFormField text form field} with an initial
     *     value, with a specified font and font size.
     * @param doc the {@link PdfDocument} to create the text field in
     * @param rect the location on the page for the text field
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfTextFormField}
     */
    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, PdfFont font, int fontSize, String value, String name) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfTextFormField field = new PdfTextFormField(annot).makeIndirect(doc);
        field.setValue(value);
        field.setFieldName(name);

        PdfFormXObject xObject = field.drawTextAppearance(rect, font, fontSize, value);
        xObject.getResources().addFont(doc, font);
        annot.setNormalAppearance(xObject.getPdfObject());

        return field;
    }

    /**
     * Creates an empty {@link PdfChoiceFormField choice form field}.
     * @param doc the {@link PdfDocument} to create the choice field in
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *     flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *     flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, int flags) {
        PdfChoiceFormField field = new PdfChoiceFormField().makeIndirect(doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates an empty {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the choice field in
     * @param rect the location on the page for the choice field
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *     flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *     flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfChoiceFormField field = new PdfChoiceFormField(annot).makeIndirect(doc);
        field.setFieldFlags(flags);
        return field;
    }

    /**
     * Creates a {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the choice field in
     * @param rect the location on the page for the choice field
     * @param options an array of {@link PdfString} objects that each represent
     *     the 'on' state of one of the choices.
     * @param value the initial value
     * @param name the name of the form field
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *     flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *     flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, PdfArray options, String value, String name, int flags) {
        try{
            return createChoice(doc, rect, options, value, name, PdfFont.getDefaultFont(doc), DEFAULT_FONT_SIZE, flags);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    
    /**
     * Creates a {@link PdfChoiceFormField choice form field} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the choice field in
     * @param rect the location on the page for the choice field
     * @param options an array of {@link PdfString} objects that each represent
     *     the 'on' state of one of the choices.
     * @param value the initial value
     * @param name the name of the form field
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     * @param flags an <code>int</code>, containing a set of binary behavioral
     *     flags. Do binary <code>OR</code> on this <code>int</code> to set the
     *     flags you require.
     * @return a new {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, PdfArray options, String value, String name, PdfFont font, int fontSize, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfChoiceFormField field = new PdfChoiceFormField(annot).makeIndirect(doc);
        field.put(PdfName.Opt, options);
        field.setFieldFlags(flags);
        field.setFieldName(name);
        field.setValue(value);

        PdfFormXObject xObject = field.drawMultiLineTextAppearance(rect, font, fontSize, value);
        xObject.getResources().addFont(doc, font);
        annot.setNormalAppearance(xObject.getPdfObject());

        return field;
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     * @param doc the {@link PdfDocument} to create the signature field in
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc) {
        return new PdfSignatureFormField().makeIndirect(doc);
    }

    /**
     * Creates an empty {@link PdfSignatureFormField signature form field}.
     * @param doc the {@link PdfDocument} to create the signature field in
     * @param rect the location on the page for the signature field
     * @return a new {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignature(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        return new PdfSignatureFormField(annot).makeIndirect(doc);
    }

    /**
     * Creates a {@link PdfButtonFormField radio group form field}.
     * @param doc the {@link PdfDocument} to create the radio group in
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfButtonFormField radio group}
     */
    public static PdfButtonFormField createRadioGroup(PdfDocument doc, String value, String name) {
        PdfButtonFormField radio = createButton(doc, PdfButtonFormField.FF_RADIO);
        radio.setFieldName(name);
        radio.put(PdfName.V, new PdfName(value));
        return radio;
    }

    
    /**
     * Creates a generic {@link PdfFormField} that is added to a radio group.
     * @see #createRadioGroup(com.itextpdf.core.pdf.PdfDocument, java.lang.String, java.lang.String) 
     * @param doc the {@link PdfDocument} to create the radio group in
     * @param rect the location on the page for the field
     * @param radioGroup the radio button group that this field should belong to
     * @param value the initial value
     * @return a new {@link PdfFormField}
     */
    public static PdfFormField createRadioButton(PdfDocument doc, Rectangle rect, PdfButtonFormField radioGroup, String value) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfFormField radio = new PdfFormField(annot).makeIndirect(doc);
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
     * @param doc the {@link PdfDocument} to create the radio group in
     * @param rect the location on the page for the field
     * @param name the name of the form field
     * @param caption the text to display on the button
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption) {
        PdfButtonFormField field;
        try {
            field = createPushButton(doc, rect, name, caption, PdfFont.getDefaultFont(doc), DEFAULT_FONT_SIZE);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
        return field;
    }

    /**
     * Creates a {@link PdfButtonFormField} as a push button without data, with
     * its caption in a custom font.
     * @param doc the {@link PdfDocument} to create the radio group in
     * @param rect the location on the page for the field
     * @param name the name of the form field
     * @param caption the text to display on the button
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     * @return a new {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption, PdfFont font, int fontSize) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfButtonFormField field = new PdfButtonFormField(annot).makeIndirect(doc);
        field.setPushButton(true);
        field.setFieldName(name);

        PdfFormXObject xObject = field.drawPushButtonAppearance(rect.getWidth(), rect.getHeight(), caption, font, fontSize);
        annot.setNormalAppearance(xObject.getPdfObject());

        return field;
    }

    /**
     * Creates a {@link PdfButtonFormField} as a checkbox.
     * @param doc the {@link PdfDocument} to create the radio group in
     * @param rect the location on the page for the field
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfButtonFormField checkbox}
     */
    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect, String value, String name) {
        return createCheckBox(doc, rect, value, name, PdfButtonFormField.TYPE_CROSS);
    }

    /**
     * Creates a {@link PdfButtonFormField} as a checkbox.
     * @param doc the {@link PdfDocument} to create the radio group in
     * @param rect the location on the page for the field
     * @param value the initial value
     * @param name the name of the form field
     * @param checkType the type of checkbox graphic to use. 
     * @return a new {@link PdfButtonFormField checkbox}
     */
    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect, String value, String name, int checkType) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfFormField check = new PdfButtonFormField(annot).makeIndirect(doc);
        check.setCheckType(checkType);
        check.setFieldName(name);
        check.setValue(value);
        annot.setAppearanceState(new PdfName(value));
        check.drawCheckAppearance(rect.getWidth(), rect.getHeight(), value);

        return (PdfButtonFormField) check;
    }

    /**
     * Creates a {@link PdfChoiceFormField combobox} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the combobox in
     * @param rect the location on the page for the combobox
     * @param options a two-dimensional array of Strings which will be converted
     *     to a PdfArray.
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfChoiceFormField} as a combobox
     */
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String options[][], String value, String name) {
        return createChoice(doc, rect, processOptions(options), value, name, PdfChoiceFormField.FF_COMBO);
    }

    /**
     * Creates a {@link PdfChoiceFormField combobox} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the combobox in
     * @param rect the location on the page for the combobox
     * @param options an array of Strings which will be converted to a PdfArray.
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfChoiceFormField} as a combobox
     */
    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String options[], String value, String name) {
        return createChoice(doc, rect, processOptions(options), value, name, PdfChoiceFormField.FF_COMBO);
    }

    /**
     * Creates a {@link PdfChoiceFormField list field} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the choice field in
     * @param rect the location on the page for the choice field
     * @param options a two-dimensional array of Strings which will be converted
     *     to a PdfArray.
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfChoiceFormField} as a list field
     */
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String options[][], String value, String name) {
        StringBuilder text = new StringBuilder();
        for (String[] option : options) {
            text.append(option[1]).append('\n');
        }
        return createChoice(doc, rect, processOptions(options), text.toString(), name, 0);
    }

    /**
     * Creates a {@link PdfChoiceFormField list field} with custom
     * behavior and layout, on a specified location.
     * @param doc the {@link PdfDocument} to create the list field in
     * @param rect the location on the page for the list field
     * @param options an array of Strings which will be converted to a PdfArray.
     * @param value the initial value
     * @param name the name of the form field
     * @return a new {@link PdfChoiceFormField} as a list field
     */
    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String options[], String value, String name) {
        StringBuilder text = new StringBuilder();
        for (String option : options) {
            text.append(option).append('\n');
        }
        return createChoice(doc, rect, processOptions(options), text.toString(), name, 0);
    }

    /**
     * Creates a (subtype of) {@link PdfFormField} object. The type of the object
     * depends on the <code>FT</code> entry in the <code>pdfObject</code> parameter.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param pdfObject assumed to be either a {@link PdfDictionary}, or a
     *     {@link PdfIndirectReference} to a {@link PdfDictionary}
     * @param document the {@link PdfDocument} to create the field in
     * @return a new {@link PdfFormField}, or <code>null</code> if
     *     <code>pdfObject</code> does not contain a <code>FT</code> entry
     */
    public static <T extends PdfFormField> T makeFormField(PdfObject pdfObject, PdfDocument document) {
        T field = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName formType = dictionary.getAsName(PdfName.FT);
            if (PdfName.Tx.equals(formType))
                field = (T) new PdfTextFormField(dictionary).makeIndirect(document);
            else if (PdfName.Btn.equals(formType))
                field = (T) new PdfButtonFormField(dictionary).makeIndirect(document);
            else if (PdfName.Ch.equals(formType))
                field = (T) new PdfChoiceFormField(dictionary).makeIndirect(document);
            else if (PdfName.Sig.equals(formType))
                field = (T) new PdfSignatureFormField(dictionary).makeIndirect(document);
            else
                field = (T) new PdfFormField(dictionary).makeIndirect(document);
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
                    field.setValue(value);
                }
            }
        }

        return setValue(value, true);
    }

    /**
     * Sets a value to the field and generating field appearance if needed.
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value of the field
     * @param generateAppearance set this flat to false if you want to keep the appearance of the field generated before
     * @return the field
     */
    public <T extends PdfFormField> T setValue(String value, boolean generateAppearance) {
        PdfName formType = getFormType();
        if (PdfName.Tx.equals(formType) || PdfName.Ch.equals(formType)) {
            put(PdfName.V, new PdfString(value));
        } else if (PdfName.Btn.equals(formType)) {
             if ((getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) != 0) {
                 try{
                     img = ImageFactory.getImage(Base64.decode(value));
                 } catch (Exception e) {
                     text = value;
                 }
             } else {
                 put(PdfName.V, new PdfName(value));
             }
        } else {
            put(PdfName.V, new PdfString(value));
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value text value
     * @param font a {@link PdfFont}
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


        PdfFormXObject appearance;
        if (formType.equals(PdfName.Tx)) {
            appearance = drawTextAppearance(bBox.toRectangle(), font, fontSize, value);
        } else {
            appearance = drawMultiLineTextAppearance(bBox.toRectangle(), font, fontSize, value);
        }

        appearance.getResources().addFont(getDocument(), font);
        PdfDictionary ap = new PdfDictionary();
        ap.put(PdfName.N, appearance.getPdfObject());
        getPdfObject().put(PdfName.V, new PdfString(value));

        return put(PdfName.AP, ap);
    }

    /**
     * Sets the field value and the display string. The display string
     * is used to build the appearance.
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value the field value
     * @param display the string that is used for the appearance. If <CODE>null</CODE>
     * the <CODE>value</CODE> parameter will be used
     * @return the edited field
     */
    public <T extends PdfFormField> T setValue (String value, String display) {
        if (display == null) {
            return setValue(value);
        }
        setValue(display, true);
        PdfName formType = getFormType();
        if (PdfName.Tx.equals(formType) || PdfName.Ch.equals(formType)) {
            put(PdfName.V, new PdfString(value));
        } else if (PdfName.Btn.equals(formType)) {
            if ((getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) != 0) {
                text = value;
            } else {
                put(PdfName.V, new PdfName(value));
            }
        } else {
            put(PdfName.V, new PdfString(value));
        }

        return (T) this;
    }

    /**
     * Sets a parent {@link PdfFormField} for the current object.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
        PdfString kidName = kid.getFieldName();
        if (kidName != null) {
            kid.setFieldName(getFieldName().toUnicodeString() + "." + kidName.toUnicodeString());
        }

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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param name the new field name, as a String
     * @return the edited field
     */
    public <T extends PdfFormField> T setFieldName(String name) {
        return put(PdfName.T, new PdfString(name));
    }

    /**
     * Gets the current field name.
     * @return the current field name, as a {@link PdfString}
     */
    public PdfString getFieldName() {
        return getPdfObject().getAsString(PdfName.T);
    }

    /**
     * Changes the alternate name of the field to the specified value. The
     * alternate is a descriptive name to be used by status messages etc.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param name the new alternate name, as a String
     * @return the edited field
     */
    public <T extends PdfFormField> T setAlternativeName(String name) {
        return put(PdfName.TU, new PdfString(name));
    }

    /**
     * Gets the current alternate name. The alternate is a descriptive name to
     * be used by status messages etc.
     * @return the current alternate name, as a {@link PdfString}
     */
    public PdfString getAlternativeName() {
        return getPdfObject().getAsString(PdfName.TU);
    }

    /**
     * Changes the mapping name of the field to the specified value. The
     * mapping name can be used when exporting the form data in the document.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param name the new alternate name, as a String
     * @return the edited field
     */
    public <T extends PdfFormField> T setMappingName(String name) {
        return put(PdfName.TM, new PdfString(name));
    }

    /**
     * Gets the current mapping name. The mapping name can be used when
     * exporting the form data in the document.
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param flag an <code>int</code> interpreted as a series of a binary flags
     * @param value if <code>true</code>, adds the flag(s). if <code>false</code>,
     *     removes the flag(s).
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
     * If true, the field can contain multiple lines of text; if false, the field’s text is restricted to a single line.
     * @return whether the field can span over multiple lines.
     */
    public boolean isMultiline() {
        return getFieldFlag(FF_MULTILINE);
    }

    /**
     * If true, the field is intended for entering a secure password that should not be echoed visibly to the screen.
     * Characters typed from the keyboard should instead be echoed in some unreadable form, such as asterisks or bullet characters.
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param flags an <code>int</code> interpreted as a series of a binary flags
     * @return the edited field
     */
    public <T extends PdfFormField> T setFieldFlags(int flags) {
        return put(PdfName.Ff, new PdfNumber(flags));
    }

    /**
     * Gets the current list of PDF form field flags.
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
     * @return the current value, as a {@link PdfObject}
     */
    public PdfObject getValue() {
        return getPdfObject().get(PdfName.V);
    }

    /**
     * Gets the current value contained in the form field.
     * @return the current value, as a {@link String}
     */
    public String getValueAsString() {
        PdfObject value = getPdfObject().get(PdfName.V);
        if (value == null) {
            return "";
        } else if (value instanceof PdfStream) {
            return new String(((PdfStream)value).getBytes());
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param value the default value
     * @return the edited field
     */
    public <T extends PdfFormField> T setDefaultValue(PdfObject value) {
        return put(PdfName.DV, value);
    }

    /**
     * Gets the default fallback value for the form field.
     * @return the default value
     */
    public PdfObject getDefaultValue() {
        return getPdfObject().get(PdfName.DV);
    }

    /**
     * Sets an additional action for the form field.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param key the dictionary key to use for storing the action
     * @param action the action
     * @return the edited field
     */
    public <T extends PdfFormField> T setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    /**
     * Gets the currently additional action dictionary for the form field.
     * @return the additional action dictionary
     */
    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    /**
     * Sets options for the form field. Only to be used for checkboxes and radio buttons.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param options an array of {@link PdfString} objects that each represent
     *     the 'on' state of one of the choices.
     * @return the edited field
     */
    public <T extends PdfFormField> T setOptions(PdfArray options) {
        return put(PdfName.Opt, options);
    }

    /**
     * Gets options for the form field. Should only return usable values for
     * checkboxes and radio buttons.
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
            PdfAnnotation widget = PdfAnnotation.makeAnnotation(getPdfObject(), getDocument()); //TODO what?
            widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(getPdfObject(), getDocument()));
        }

        PdfArray kids = getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                if (kid.isIndirectReference()) {
                    kid = ((PdfIndirectReference) kid).getRefersTo();
                }
                subType = ((PdfDictionary) kid).getAsName(PdfName.Subtype);
                if (subType != null && subType.equals(PdfName.Widget)) {
                    widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(kid, getDocument()));
                }
            }
        }

        return widgets;
    }

    /**
     * Gets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field’s text size and color.
     * @return the default appearance graphics, as a {@link PdfString}
     */
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * Sets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field’s text size and color.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param justification the value to set the justification attribute to
     * @return the edited field
     */
    public <T extends PdfFormField> T setJustification(int justification) {
        getPdfObject().put(PdfName.Q, new PdfNumber(justification));
        return (T) this;
    }

    /**
     * Gets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     * @return the default style, as a {@link PdfString}
     */
    public PdfString getDefaultStyle() {
        return getPdfObject().getAsString(PdfName.DS);
    }

    /**
     * Sets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
     * @return the current rich text value
     */
    public PdfObject getRichText() {
        return getPdfObject().get(PdfName.RV);
    }

    /**
     * Sets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param richText a new rich text value
     * @return the edited field
     */
    public <T extends PdfFormField> T setRichText(PdfObject richText) {
        getPdfObject().put(PdfName.RV, richText);
        return (T) this;
    }

    /**
     * Draws the visual appearance of text in a form field.
     * 
     * @param rect the location on the page for the list field
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     * @param value the initial value
     * @return the {@link PdfFormXObject Form XObject} that was drawn
     */
    public PdfFormXObject drawTextAppearance(Rectangle rect, PdfFont font, int fontSize, String value) {
        PdfStream stream = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());

        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize));

        float height = rect.getHeight();
        float width = rect.getWidth();
        drawBorder(canvas, width, height);
        if (isPassword()) {
            value = obfuscatePassword(value);
        }

        canvas.
                beginVariableText().
                saveState().
                newPath().
                beginText().
                setFontAndSize(font, fontSize);
        if (color != null) {
            canvas.setFillColor(color);
        } else {
            canvas.resetFillColorRgb();
        }
        Integer justification = getJustification();
        if (justification == null) {
            justification = 0;
        }
        drawTextAligned(canvas, justification, value, 2, height / 2 - fontSize * 0.3f, font, fontSize);
        canvas.
                endText().
                restoreState().
                endVariableText();


        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());

        return xObject;
    }

    
    /**
     * Draws the visual appearance of multiline text in a form field.
     * 
     * @param rect the location on the page for the list field
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     * @param value the initial value
     * @return the {@link PdfFormXObject Form XObject} that was drawn
     */
    public PdfFormXObject drawMultiLineTextAppearance(Rectangle rect, PdfFont font, int fontSize, String value) {
        PdfStream stream = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());

        setDefaultAppearance(generateDefaultAppearanceString(font, fontSize));

        float width = rect.getWidth();
        float height = rect.getHeight();

        List<String> strings = font.splitString(value, fontSize, width - 6);

        value = "";
        for (String str : strings) {
            value += str + '\n';
        }
        value = value.substring(0, value.length() - 1);

        drawBorder(canvas, width, height);
        canvas.
                beginVariableText().
                saveState().
                rectangle(3, 3, width - 6, height - 6).
                clip().
                newPath().
                beginText().
                setFontAndSize(font, fontSize);
        if (color != null) {
            canvas.setFillColor(color);
        } else {
            canvas.resetFillColorRgb();
        }

        canvas.setTextMatrix(4, 5);
        StringTokenizer tokenizer = new StringTokenizer(value, "\n");
        while (tokenizer.hasMoreTokens()) {
            height -= fontSize * 1.2;
            canvas.
                    setTextMatrix(3, height).
                    showText(tokenizer.nextToken());
        }
        canvas.
                endText().
                restoreState().
                endVariableText();


        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, rect.getWidth(), rect.getHeight()));
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());

        return xObject;
    }

    /**
     * Draws a border using the borderWidth and borderColor of the form field.
     * 
     * @param canvas the {@link PdfCanvas} on which to draw
     * @param width the width of the rectangle to draw
     * @param height the height of the rectangle to draw
     */
    public void drawBorder(PdfCanvas canvas, float width, float height) {
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
        canvas.restoreState();
    }

    /**
     * Draws the appearance of a radio button with a specified value.
     * @param width the width of the radio button to draw
     * @param height the height of the radio button to draw
     * @param value the value of the button
     */
    public void drawRadioAppearance(float width, float height, String value) {
        PdfStream streamOn = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        drawBorder(canvasOn, width, height);
        drawRadioField(canvasOn, 0, 0, width, height, true);

        PdfStream streamOff = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        drawBorder(canvasOff, width, height);

        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfWidgetAnnotation widget = getWidgets().get(0);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        widget.setNormalAppearance(new PdfDictionary());
        widget.getNormalAppearanceObject().put(new PdfName(value), xObjectOn.getPdfObject());

        PdfFormXObject xObjectOff = new PdfFormXObject(rect);
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        widget.getNormalAppearanceObject().put(new PdfName("Off"), xObjectOff.getPdfObject());
    }

    /**
     * Draws a radio button.
     * @param canvas the {@link PdfCanvas} on which to draw
     * @param width the width of the radio button to draw
     * @param height the height of the radio button to draw
     * @param on required to be <code>true</code> for fulfilling the drawing operation
     */
    public void drawRadioField(PdfCanvas canvas, final float x, final float y, final float width, final float height, final boolean on) {
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
     * @param width the width of the checkbox to draw
     * @param height the height of the checkbox to draw
     * @param value the state of the form field that will be drawn
     */
    public void drawCheckAppearance(float width, float height, String value) {
        PdfStream streamOn = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        drawBorder(canvasOn, width, height);
        drawCheckBox(canvasOn, width, height, DEFAULT_FONT_SIZE, true);

        PdfStream streamOff = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        drawBorder(canvasOff, width, height);
        drawCheckBox(canvasOff, width, height, DEFAULT_FONT_SIZE, false);

        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfWidgetAnnotation widget = getWidgets().get(0);
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        xObjectOn.getResources().addFont(getDocument(), getFont());
        widget.setNormalAppearance(new PdfDictionary());
        widget.getNormalAppearanceObject().put(new PdfName(value), xObjectOn.getPdfObject());

        PdfFormXObject xObjectOff = new PdfFormXObject(rect);
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        xObjectOff.getResources().addFont(getDocument(), getFont());
        widget.getNormalAppearanceObject().put(new PdfName("Off"), xObjectOff.getPdfObject());
    }
    
    /**
     * Draws the appearance for a push button.
     * 
     * @param width the width of the pushbutton
     * @param height the width of the pushbutton
     * @param text the text to display on the button
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     * @return a new {@link PdfFormXObject}
     */
    public PdfFormXObject drawPushButtonAppearance(float width, float height, String text, PdfFont font, int fontSize) {
        PdfStream stream = new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));

        if (img != null) {
            PdfImageXObject imgXObj = new PdfImageXObject(img);
            canvas.addXObject(imgXObj, width, 0, 0, height, 0, 0);
            xObject.getResources().addImage(imgXObj);
        } else {
            drawButton(canvas, 0, 0, width, height, text, font, fontSize);
            setDefaultAppearance(generateDefaultAppearanceString(font, fontSize));
            xObject.getResources().addFont(getDocument(), font);
        }
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());

        return xObject;
    }

    /**
     * Performs the low-level drawing operations to draw a button object.
     * 
     * @param canvas the {@link PdfCanvas} of the page to draw on.
     * @param x the x coordinate of the lower left corner of the button rectangle
     * @param y the y coordinate of the lower left corner of the button rectangle
     * @param width the width of the button
     * @param height the width of the button
     * @param text the text to display on the button
     * @param font a {@link PdfFont}
     * @param fontSize a positive integer
     */
    public void drawButton(PdfCanvas canvas, float x, float y, float width, float height, String text, PdfFont font, int fontSize) {
        if (backgroundColor == null) {
            backgroundColor = Color.LIGHT_GRAY;
        }
        canvas.
                saveState().
                setStrokeColor(Color.BLACK).
                setLineWidth(1).
                setLineCapStyle(PdfCanvasConstants.LineCapStyle.BUTT).
                rectangle(x, y, width, height).
                stroke().
                setLineWidth(1).
                setLineCapStyle(PdfCanvasConstants.LineCapStyle.BUTT).
                setFillColor(backgroundColor).
                rectangle(x + 0.5f, y + 0.5f, width - 1, height - 1).
                fill().
                setStrokeColor(Color.WHITE).
                setLineWidth(1).
                setLineCapStyle(PdfCanvasConstants.LineCapStyle.BUTT).
                moveTo(x + 1, y + 1).
                lineTo(x + 1, y + height - 1).
                lineTo(x + width - 1, y + height - 1).
                stroke().
                setStrokeColor(Color.GRAY).
                setLineWidth(1).
                setLineCapStyle(PdfCanvasConstants.LineCapStyle.BUTT).
                moveTo(x + 1, y + 1).
                lineTo(x + width - 1, y + 1).
                lineTo(x + width - 1, y + height - 1).
                stroke().
                resetFillColorRgb().
                beginText().
                setFontAndSize(font, fontSize).
                setTextMatrix(0, y + (height - fontSize) / 2).
                showText(text).
                endText().
                restoreState();
    }

    /**
     * Performs the low-level drawing operations to draw a checkbox object.
     * 
     * @param canvas the {@link PdfCanvas} of the page to draw on.
     * @param width the width of the button
     * @param height the width of the button
     * @param fontSize a positive integer
     * @param on the boolean value of the checkbox
     */
    public void drawCheckBox(PdfCanvas canvas, float width, float height, int fontSize, boolean on) {
        if (!on) {
            return;
        }
        PdfFont ufont = getFont();
        // PdfFont gets all width in 1000 normalized units
        float sizeCoef = (float)fontSize / FontProgram.UNITS_NORMALIZATION;
        canvas.
                beginText().
                setFontAndSize(ufont, fontSize).
                resetFillColorRgb().
                setTextMatrix((width - ufont.getWidth(text) * sizeCoef) / 2, (height - ufont.getAscent(text) * sizeCoef) / 2).
                showText(text).
                endText();
    }

    /**
     * Gets the current font of the form field.
     * @return the current {@link PdfFont font}
     */
    public PdfFont getFont() {
        return font;
    }

    /**
     * Basic setter for the <code>font</code> property. Regenerates the field
     * appearance after setting the new value.
     * @param font the new font to be set
     */
    public void setFont(PdfFont font) {
        this.font = font;
        regenerateField();
    }

    /**
     * Basic setter for the <code>fontSize</code> property. Regenerates the
     * field appearance after setting the new value.
     * @param fontSize the new font size to be set
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        regenerateField();
    }

    /**
     * Combined setter for the <code>font</code> and <code>fontSize</code>
     * properties. Regenerates the field appearance after setting the new value.
     * @param font the new font to be set
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
     * @param backgroundColor the new font to be set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        regenerateField();
    }

    /**
     * Sets the action on all {@link PdfWidgetAnnotation widgets} of this form field.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
            font = PdfFont.createStandardFont(FontConstants.ZAPFDINGBATS);
        }
        catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    /**
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param visibility
     * @return the edited field
     */
    public <T extends PdfFormField> T setVisibility(int visibility) {
        switch (visibility) {
            case HIDDEN:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.Print | PdfAnnotation.Hidden));
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.Print | PdfAnnotation.NoView));
                break;
            default:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.Print));
                break;
        }
        return (T) this;
    }

    /**
     * This method regenerates appearance stream of the field. Use it if you
     * changed any field parameters and didn't use setValue method which
     * generates appearance by itself.
     * @return whether or not the regeneration was successful.
     */
    public boolean regenerateField() {
        PdfName type = getFormType();
        String value = getValueAsString();

        if (PdfName.Tx.equals(type) || PdfName.Ch.equals(type)) {
            try{
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
                if (fontSz == 0){
                    fontSz = DEFAULT_FONT_SIZE;
                }

                PdfFormXObject appearance;
                if (PdfName.Tx.equals(type)) {
                    if (!isMultiline()) {
                        appearance = drawTextAppearance(bBox.toRectangle(), localFont, fontSz, value);
                    } else {
                        appearance = drawMultiLineTextAppearance(bBox.toRectangle(), localFont, fontSz, value);
                    }

                } else {
                    appearance = drawMultiLineTextAppearance(bBox.toRectangle(), localFont, fontSz, value);
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
                    PdfFormXObject appearance = null;
                    Rectangle rect = getRect(getPdfObject());
                    PdfDictionary apDic = getPdfObject().getAsDictionary(PdfName.AP);
                    if (img != null) {
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

                    apDic = new PdfDictionary();
                    apDic.put(PdfName.N, appearance.getPdfObject());
                    put(PdfName.AP, apDic);
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param color the new value for the Border Color
     * @return the edited field
     */
    public <T extends PdfFormField> T setBorderColor(Color color) {
        borderColor = color;
        regenerateField();
        return (T) this;
    }

    
    /**
     * Sets the ReadOnly flag, specifying whether or not the field can be changed.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param readOnly if <code>true</code>, then the field cannot be changed.
     * @return the edited field
     */
    public <T extends PdfFormField> T setReadOnly(boolean readOnly) {
        return setFieldFlag(FF_READ_ONLY, readOnly);
    }

    /**
     * Gets the ReadOnly flag, specifying whether or not the field can be changed.
     * @return <code>true</code> if the field cannot be changed.
     */
    public boolean isReadOnly() {
        return getFieldFlag(FF_READ_ONLY);
    }

    /**
     * Sets the Required flag, specifying whether or not the field must be filled in.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param required if <code>true</code>, then the field must be filled in.
     * @return the edited field
     */
    public <T extends PdfFormField> T setRequired(boolean required) {
        return setFieldFlag(FF_REQUIRED, required);
    }

    /**
     * Gets the Required flag, specifying whether or not the field must be filled in.
     * @return <code>true</code> if the field must be filled in.
     */
    public boolean isRequired() {
        return getFieldFlag(FF_REQUIRED);
    }

    /**
     * Sets the NoExport flag, specifying whether or not exporting is forbidden.
     * 
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param pageNum the page number
     * @return the edited field
     */
    public <T extends PdfFormField> T setPage(int pageNum){
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
                        valStr = ((PdfArray)pdfObject).getAsString(1);
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
        if (dic != null){
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
     * @param <T> an internal generic parameter for the return type. Extends {@link PdfFormField}
     * @param appearanceType the type of appearance stream to be added
     * <ul>
     *     <li> PdfName.N: normal appearance</li>
     *     <li> PdfName.R: rollover appearance</li>
     *     <li> PdfName.D: down appearance</li>
     * </ul>
     * @param appearanceState the state of the form field that needs to be true
     *     for the appearance to be used. Differentiates between several streams 
     *     of the same type.
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

    protected void drawTextAligned(PdfCanvas canvas, int alignment, String text, float x, float y, PdfFont font, int fontSize) {
        switch (alignment) {
            case ALIGN_CENTER:
                x = (getRect(getPdfObject()).getWidth() - font.getWidthPoint(text, fontSize)) / 2;
                break;
            case ALIGN_RIGHT:
                x = (getRect(getPdfObject()).getWidth() - font.getWidthPoint(text, fontSize));
                break;
        }
        canvas.setTextMatrix(x, y);
        canvas.showText(text);
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

    protected String generateDefaultAppearanceString(PdfFont font, int fontSize) {
        PdfStream stream = new PdfStream();
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources(), getDocument());
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
                    fontAndSize[0] =  PdfFont.createFont(fontDic.getAsDictionary(fontName));
                }
                if (fontSize != 0) {
                    fontAndSize[1] = fontSize;
                } else {
                    fontAndSize[1] = dab[DA_SIZE];
                }
                color = (Color) dab[DA_COLOR];
            } else {
                if (font != null) {
                    fontAndSize[0] = font;
                } else {
                    fontAndSize[0] = PdfFont.getDefaultFont(getDocument());
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
                fontAndSize[0] = PdfFont.getDefaultFont(getDocument());
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
                }
                else {
                    stack.add(tk.getStringValue());
                }
            }
        } catch (IOException e) {

        }
        return ret;
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
}
