package com.itextpdf.forms.fields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

/**
 * An AcroForm field containing textual data.
 */
public class PdfTextFormField extends PdfFormField {

    public static final int FF_FILE_SELECT = makeFieldFlag(21);
    public static final int FF_DO_NOT_SPELL_CHECK = makeFieldFlag(23);
    public static final int FF_DO_NOT_SCROLL = makeFieldFlag(24);
    public static final int FF_COMB = makeFieldFlag(25);
    public static final int FF_RICH_TEXT = makeFieldFlag(26);

    protected PdfTextFormField() {
        super();
    }

    protected PdfTextFormField(PdfWidgetAnnotation widget) {
        super(widget);
    }

    protected PdfTextFormField(PdfDictionary pdfObject) {
        super(pdfObject);
        setBorderWidth(0);
    }

    /**
     * Returns <code>Tx</code>, the form type for textual form fields.
     * 
     * @return the form type, as a {@link PdfName}
     */
    @Override
    public PdfName getFormType() {
        return PdfName.Tx;
    }

    /**
     * If true, the field can contain multiple lines of text; if false, the field’s text is restricted to a single line.
     * @param multiline whether or not the file can contain multiple lines of text
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setMultiline(boolean multiline) {
        return setFieldFlag(FF_MULTILINE, multiline);
    }

    /**
     * If true, the field is intended for entering a secure password that should not be echoed visibly to the screen.
     * Characters typed from the keyboard should instead be echoed in some unreadable form, such as asterisks or bullet characters.
     * @param password whether or not to obscure the typed characters
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setPassword(boolean password) {
        return setFieldFlag(FF_PASSWORD, password);
    }

    /**
     * If true, the text entered in the field represents the pathname of a file
     * whose contents are to be submitted as the value of the field.
     * @return whether or not this field currently represents a path
     */
    public boolean isFileSelect() {
        return getFieldFlag(FF_FILE_SELECT);
    }

    /**
     * If true, the text entered in the field represents the pathname of a file
     * whose contents are to be submitted as the value of the field.
     * @param fileSelect whether or not this field should represent a path
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setFileSelect(boolean fileSelect) {
        return setFieldFlag(FF_FILE_SELECT, fileSelect);
    }

    /**
     * If true, text entered in the field is spell-checked.
     * @return whether or not spell-checking is currently enabled
     */
    public boolean isSpellCheck() {
        return !getFieldFlag(FF_DO_NOT_SPELL_CHECK);
    }

    /**
     * If true, text entered in the field is spell-checked.
     * @param spellCheck whether or not to spell-check
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setSpellCheck(boolean spellCheck) {
        return setFieldFlag(FF_DO_NOT_SPELL_CHECK, !spellCheck);
    }

    /**
     * If true, the field scrolls (horizontally for single-line fields, vertically for multiple-line fields)
     * to accommodate more text than fits within its annotation rectangle.
     * Once the field is full, no further text is accepted.
     * @return whether or not longer texts are currently allowed
     */
    public boolean isScroll() {
        return !getFieldFlag(FF_DO_NOT_SCROLL);
    }

    /**
     * If true, the field scrolls (horizontally for single-line fields, vertically for multiple-line fields)
     * to accommodate more text than fits within its annotation rectangle.
     * Once the field is full, no further text is accepted.
     * @param scroll whether or not to allow longer texts
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setScroll(boolean scroll) {
        return setFieldFlag(FF_DO_NOT_SCROLL, !scroll);
    }

    /**
     * Meaningful only if the MaxLen entry is present in the text field dictionary
     * and if the Multiline, Password, and FileSelect flags are clear.
     * If true, the field is automatically divided into as many equally spaced positions,
     * or combs, as the value of MaxLen, and the text is laid out into those combs.
     * @return whether or not combing is enabled
     */
    public boolean isComb() {
        return getFieldFlag(FF_COMB);
    }

    /**
     * Meaningful only if the MaxLen entry is present in the text field dictionary
     * and if the Multiline, Password, and FileSelect flags are clear.
     * If true, the field is automatically divided into as many equally spaced positions,
     * or combs, as the value of MaxLen, and the text is laid out into those combs.
     * @param comb whether or not to enable combing
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setComb(boolean comb) {
        return setFieldFlag(FF_COMB, comb);
    }

    /**
     * If true, the value of this field should be represented as a rich text string.
     * If the field has a value, the RV entry of the field dictionary specifies the rich text string.
     * @return whether or not text is currently represented as rich text
     */
    public boolean isRichText() {
        return getFieldFlag(FF_RICH_TEXT);
    }

    /**
     * If true, the value of this field should be represented as a rich text string.
     * If the field has a value, the RV entry of the field dictionary specifies the rich text string.
     * @param richText whether or not to represent text as rich text
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setRichText(boolean richText) {
        return setFieldFlag(FF_RICH_TEXT, richText);
    }

    /**
     * Gets the maximum length of the field's text, in characters.
     * This is an optional parameter, so if it is not specified, <code>null</code> will be returned.
     * @return the current maximum text length
     */
    public Integer getMaxLen() {
        PdfNumber number = getPdfObject().getAsNumber(PdfName.MaxLen);
        return number != null ? number.getIntValue() : null;
    }

    /**
     * Sets the maximum length of the field’s text, in characters.
     * @param maxLen the maximum text length
     * @return current
     */
    public PdfTextFormField setMaxLen(int maxLen) {
        return put(PdfName.MaxLen, new PdfNumber(maxLen));
    }
}
