package com.itextpdf.forms.fields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfTextFormField extends PdfFormField {

    public static final int FF_FILE_SELECT = makeFieldFlag(21);
    public static final int FF_DO_NOT_SPELL_CHECK = makeFieldFlag(23);
    public static final int FF_DO_NOT_SCROLL = makeFieldFlag(24);
    public static final int FF_COMB = makeFieldFlag(25);
    public static final int FF_RICH_TEXT = makeFieldFlag(26);

    public PdfTextFormField() {
        super();
    }

    public PdfTextFormField(PdfWidgetAnnotation widget) {
        super(widget);
    }

    protected PdfTextFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Tx;
    }

    /**
     * If true, the field can contain multiple lines of text; if false, the field’s text is restricted to a single line.
     */
    public PdfTextFormField setMultiline(boolean multiline) {
        return setFieldFlag(FF_MULTILINE, multiline);
    }

    /**
     * If true, the field is intended for entering a secure password that should not be echoed visibly to the screen.
     * Characters typed from the keyboard should instead be echoed in some unreadable form, such as asterisks or bullet characters.
     */
    public PdfTextFormField setPassword(boolean password) {
        return setFieldFlag(FF_PASSWORD, password);
    }

    /**
     * If true, the text entered in the field represents the pathname of a file
     * whose contents are to be submitted as the value of the field.
     */
    public boolean isFileSelect() {
        return getFieldFlag(FF_FILE_SELECT);
    }

    /**
     * If true, the text entered in the field represents the pathname of a file
     * whose contents are to be submitted as the value of the field.
     */
    public PdfTextFormField setFileSelect(boolean fileSelect) {
        return setFieldFlag(FF_FILE_SELECT, fileSelect);
    }

    /**
     * If true, text entered in the field is spell-checked.
     */
    public boolean isSpellCheck() {
        return !getFieldFlag(FF_DO_NOT_SPELL_CHECK);
    }

    /**
     * If true, text entered in the field is spell-checked.
     */
    public PdfTextFormField setSpellCheck(boolean spellCheck) {
        return setFieldFlag(FF_DO_NOT_SPELL_CHECK, !spellCheck);
    }

    /**
     * If true, the field scrolls (horizontally for single-line fields, vertically for multiple-line fields)
     * to accommodate more text than fits within its annotation rectangle.
     * Once the field is full, no further text is accepted.
     */
    public boolean isScroll() {
        return !getFieldFlag(FF_DO_NOT_SCROLL);
    }

    /**
     * If true, the field scrolls (horizontally for single-line fields, vertically for multiple-line fields)
     * to accommodate more text than fits within its annotation rectangle.
     * Once the field is full, no further text is accepted.
     */
    public PdfTextFormField setScroll(boolean scroll) {
        return setFieldFlag(FF_DO_NOT_SCROLL, !scroll);
    }

    /**
     * Meaningful only if the MaxLen entry is present in the text field dictionary
     * and if the Multiline, Password, and FileSelect flags are clear.
     * If true, the field is automatically divided into as many equally spaced positions,
     * or combs, as the value of MaxLen, and the text is laid out into those combs.
     */
    public boolean isComb() {
        return getFieldFlag(FF_COMB);
    }

    /**
     * Meaningful only if the MaxLen entry is present in the text field dictionary
     * and if the Multiline, Password, and FileSelect flags are clear.
     * If true, the field is automatically divided into as many equally spaced positions,
     * or combs, as the value of MaxLen, and the text is laid out into those combs.
     */
    public PdfTextFormField setComb(boolean comb) {
        return setFieldFlag(FF_COMB, comb);
    }

    /**
     * If true, the value of this field should be represented as a rich text string.
     * If the field has a value, the RV entry of the field dictionary specifies the rich text string.
     */
    public boolean isRichText() {
        return getFieldFlag(FF_RICH_TEXT);
    }

    /**
     * If true, the value of this field should be represented as a rich text string.
     * If the field has a value, the RV entry of the field dictionary specifies the rich text string.
     */
    public PdfTextFormField setRichText(boolean richText) {
        return setFieldFlag(FF_RICH_TEXT, richText);
    }

    /**
     * Gets the maximum length of the field's text, in characters.
     * This is an optional parameter, so if it is not specified, <code>null</code> will be returned.
     */
    public Integer getMaxLen() {
        PdfNumber number = getPdfObject().getAsNumber(PdfName.MaxLen);
        return number != null ? number.getIntValue() : null;
    }

    /**
     * Sets the maximum length of the field’s text, in characters.
     */
    public PdfTextFormField setMaxLen(int maxLen) {
        return put(PdfName.MaxLen, new PdfNumber(maxLen));
    }
}
