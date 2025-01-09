/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * An AcroForm field containing textual data.
 */
public class PdfTextFormField extends PdfFormField {

    /**
     * constant which determines whether field currently represents a path.
     */
    public static final int FF_FILE_SELECT = makeFieldFlag(21);
    /**
     * constant which determines whether spell-checking is currently enabled
     */
    public static final int FF_DO_NOT_SPELL_CHECK = makeFieldFlag(23);
    /**
     * constant which determines whether longer texts are currently allowed.
     */
    public static final int FF_DO_NOT_SCROLL = makeFieldFlag(24);
    /**
     * constant which determines maximum length of the field's text.
     */
    public static final int FF_COMB = makeFieldFlag(25);
    /**
     * constant which determines whether text is currently represented as rich text.
     */
    public static final int FF_RICH_TEXT = makeFieldFlag(26);

    /**
     * Creates a minimal {@link PdfTextFormField}.
     *
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfTextFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    /**
     * Creates a text form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfTextFormField}.
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfTextFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        super(widget, pdfDocument);
    }

    /**
     * Creates a text form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param pdfObject the dictionary to be wrapped, must have an indirect reference.
     */
    protected PdfTextFormField(PdfDictionary pdfObject) {
        super(pdfObject);
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
     * If true, the field can contain multiple lines of text; if false, the field?s text is restricted to a single line.
     * @param multiline whether or not the file can contain multiple lines of text
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setMultiline(boolean multiline) {
        return (PdfTextFormField) setFieldFlag(FF_MULTILINE, multiline);
    }

    /**
     * If true, the field is intended for entering a secure password that should not be echoed visibly to the screen.
     * Characters typed from the keyboard should instead be echoed in some unreadable form, such as asterisks or bullet characters.
     * @param password whether or not to obscure the typed characters
     * @return current {@link PdfTextFormField}
     */
    public PdfTextFormField setPassword(boolean password) {
        return (PdfTextFormField) setFieldFlag(FF_PASSWORD, password);
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
        return (PdfTextFormField) setFieldFlag(FF_FILE_SELECT, fileSelect);
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
        return (PdfTextFormField) setFieldFlag(FF_DO_NOT_SPELL_CHECK, !spellCheck);
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
        return (PdfTextFormField) setFieldFlag(FF_DO_NOT_SCROLL, !scroll);
    }

    /**
     * Meaningful only if the MaxLen entry is present in the text field dictionary
     * and if the Multiline, Password, and FileSelect flags are clear.
     * If true, the field is automatically divided into as many equally spaced positions,
     * or combs, as the value of MaxLen, and the text is laid out into those combs.
     *
     * @return {@code true} if combing is enabled, {@code false} otherwise
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
        return (PdfTextFormField) setFieldFlag(FF_COMB, comb);
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
        return (PdfTextFormField) setFieldFlag(FF_RICH_TEXT, richText);
    }

    /**
     * Gets the maximum length of the field's text, in characters.
     * This is an optional parameter, so if it is not specified, 0 value will be returned.
     * @return the current maximum text length
     */
    public int getMaxLen() {
        PdfNumber maxLenEntry = this.getPdfObject().getAsNumber(PdfName.MaxLen);
        if (maxLenEntry != null) {
            return maxLenEntry.intValue();
        } else {
            PdfDictionary parent = getParent();
            // MaxLen is an inherited form field property, therefore we try to recursively extract it from the ancestors
            if (parent != null) {
                return PdfFormCreator.createTextFormField(parent).getMaxLen();
            } else {
                return 0;
            }
        }
    }

    /**
     * Sets the maximum length of the field's text, in characters.
     * @param maxLen the maximum text length
     * @return current
     */
    public PdfTextFormField setMaxLen(int maxLen) {
        put(PdfName.MaxLen, new PdfNumber(maxLen));
        if (getFieldFlag(FF_COMB))
            regenerateField();
        return this;
    }
}
