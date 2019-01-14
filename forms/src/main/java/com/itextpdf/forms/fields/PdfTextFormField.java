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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * An AcroForm field containing textual data.
 */
public class PdfTextFormField extends PdfFormField {

    public static final int FF_FILE_SELECT = makeFieldFlag(21);
    public static final int FF_DO_NOT_SPELL_CHECK = makeFieldFlag(23);
    public static final int FF_DO_NOT_SCROLL = makeFieldFlag(24);
    public static final int FF_COMB = makeFieldFlag(25);
    public static final int FF_RICH_TEXT = makeFieldFlag(26);

    protected PdfTextFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    protected PdfTextFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        super(widget, pdfDocument);
    }

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
     * This is an optional parameter, so if it is not specified, <code>null</code> will be returned.
     * @return the current maximum text length
     */
    public int getMaxLen() {
        PdfNumber number = getPdfObject().getAsNumber(PdfName.MaxLen);
        return number != null ? number.intValue() : 0;
    }

    /**
     * Sets the maximum length of the field?s text, in characters.
     * @param maxLen the maximum text length
     * @return current
     */
    public PdfTextFormField setMaxLen(int maxLen) {
        return (PdfTextFormField) put(PdfName.MaxLen, new PdfNumber(maxLen));
    }
}
