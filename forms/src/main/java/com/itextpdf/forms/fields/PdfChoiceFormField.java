/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * An AcroForm field type representing any type of choice field. Choice fields
 * are to be represented by a viewer as a list box or a combo box.
 */
public class PdfChoiceFormField extends PdfFormField {

    /**
     * Choice field flags
     */
    public static final int FF_COMBO = makeFieldFlag(18);
    public static final int FF_EDIT = makeFieldFlag(19);
    public static final int FF_SORT = makeFieldFlag(20);
    public static final int FF_MULTI_SELECT = makeFieldFlag(22);
    public static final int FF_DO_NOT_SPELL_CHECK = makeFieldFlag(23);
    public static final int FF_COMMIT_ON_SEL_CHANGE = makeFieldFlag(27);

    protected PdfChoiceFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    protected PdfChoiceFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        super(widget, pdfDocument);
    }

    protected PdfChoiceFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Returns <code>Ch</code>, the form type for choice form fields.
     *
     * @return the form type, as a {@link PdfName}
     */
    @Override
    public PdfName getFormType() {
        return PdfName.Ch;
    }

    /**
     * Sets the index of the first visible option in a scrollable list.
     *
     * @param index the index of the first option
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setTopIndex(int index) {
        put(PdfName.TI, new PdfNumber(index));
        regenerateField();
        return this;
    }

    /**
     * Gets the current index of the first option in a scrollable list.
     * @return the index of the first option, as a {@link PdfNumber}
     */
    public PdfNumber getTopIndex() {
        return getPdfObject().getAsNumber(PdfName.TI);
    }

    /**
     * Sets the selected items in the field.
     *
     * @param indices a sorted array of indices representing selected items in the field
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setIndices(PdfArray indices) {
        return (PdfChoiceFormField) put(PdfName.I, indices);
    }

    /**
     * Highlights the options. If this method is used for Combo box, the first value in input array
     * will be the field value
     * @param optionValues Array of options to be highlighted
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setListSelected(String[] optionValues) {
        return setListSelected(optionValues, true);
    }

    /**
     * Highlights the options and generates field appearance if needed.. If this method is used for Combo box, the first value in input array
     * will be the field value
     * @param optionValues Array of options to be highlighted
     * @param generateAppearance if false, appearance won't be regenerated
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setListSelected(String[] optionValues, boolean generateAppearance) {
        if (optionValues.length > 1 && !isMultiSelect()) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.warn(LogMessageConstant.MULTIPLE_VALUES_ON_A_NON_MULTISELECT_FIELD);
        }
        PdfArray options = getOptions();
        PdfArray indices = new PdfArray();
        PdfArray values = new PdfArray();
        List<String> optionsToUnicodeNames = optionsToUnicodeNames();
        for (String element : optionValues) {
            if (element == null) {
                continue;
            }
            if (optionsToUnicodeNames.contains(element)) {
                int index = optionsToUnicodeNames.indexOf(element);
                indices.add(new PdfNumber(index));
                PdfObject optByIndex = options.get(index);
                values.add(optByIndex.isString() ? (PdfString) optByIndex : (PdfString) ((PdfArray) optByIndex).get(1));
            } else {
                if (!(this.isCombo() && this.isEdit())) {
                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.warn(MessageFormatUtil
                            .format(LogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY, element,
                                    this.getFieldName()));
                }
                values.add(new PdfString(element, PdfEncodings.UNICODE_BIG));
            }
        }
        if (indices.size() > 0) {
            setIndices(indices);
        } else {
            remove(PdfName.I);
        }
        if (values.size() == 1) {
            put(PdfName.V, values.get(0));
        } else {
            put(PdfName.V, values);
        }

        if (generateAppearance) {
            regenerateField();
        }
        return this;
    }

    /**
     * Highlights the options. If this method is used for Combo box, the first value in input array
     * will be the field value
     * @param optionNumbers The option numbers
     * @return              The edited {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setListSelected(int[] optionNumbers) {
        if (optionNumbers.length > 1 && !isMultiSelect()) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.warn(LogMessageConstant.MULTIPLE_VALUES_ON_A_NON_MULTISELECT_FIELD);
        }
        PdfArray indices = new PdfArray();
        PdfArray values = new PdfArray();
        PdfArray options = getOptions();
        for (int number : optionNumbers) {
            if (number >= 0 && number < options.size()) {
                indices.add(new PdfNumber(number));
                PdfObject option = options.get(number);
                if (option.isString()) {
                    values.add(option);
                } else if (option.isArray()) {
                    values.add(((PdfArray)option).get(0));
                }
            }
        }
        if (indices.size() > 0) {
            setIndices(indices);
            if (values.size() == 1) {
                put(PdfName.V, values.get(0));
            } else {
                put(PdfName.V, values);
            }
        } else {
            remove(PdfName.I);
            remove(PdfName.V);
        }
        regenerateField();
        return this;
    }

    /**
     * Gets the currently selected items in the field
     *
     * @return a sorted array of indices representing the currently selected items in the field
     */
    public PdfArray getIndices() {
        return getPdfObject().getAsArray(PdfName.I);
    }

    /**
     * If true, the field is a combo box; if false, the field is a list box.
     * @param combo whether or not the field should be a combo box
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setCombo(boolean combo) {
        return (PdfChoiceFormField) setFieldFlag(FF_COMBO, combo);
    }

    /**
     * If true, the field is a combo box; if false, the field is a list box.
     *
     * @return whether or not the field is now a combo box.
     */
    public boolean isCombo() {
        return getFieldFlag(FF_COMBO);
    }

    /**
     * If true, the combo box shall include an editable text box as well as a
     * drop-down list; if false, it shall include only a drop-down list.
     * This flag shall be used only if the Combo flag is true.
     * @param edit whether or not to add an editable text box
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setEdit(boolean edit) {
        return (PdfChoiceFormField) setFieldFlag(FF_EDIT, edit);
    }

    /**
     * If true, the combo box shall include an editable text box as well as a
     * drop-down list; if false, it shall include only a drop-down list.
     * This flag shall be used only if the Combo flag is true.
     * @return whether or not there is currently an editable text box
     */
    public boolean isEdit() {
        return getFieldFlag(FF_EDIT);
    }

    /**
     * If true, the field's option items shall be sorted alphabetically.
     * This flag is intended for use by writers, not by readers.
     * @param sort whether or not to sort the items
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setSort(boolean sort) {
        return (PdfChoiceFormField) setFieldFlag(FF_SORT, sort);
    }

    /**
     * If true, the field's option items shall be sorted alphabetically.
     * This flag is intended for use by writers, not by readers.
     * @return whether or not the items are currently sorted
     */
    public boolean isSort() {
        return getFieldFlag(FF_SORT);
    }

    /**
     * If true, more than one of the field's option items may be selected
     * simultaneously; if false, at most one item shall be selected.
     * @param multiSelect whether or not to allow multiple selection
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setMultiSelect(boolean multiSelect) {
        return (PdfChoiceFormField) setFieldFlag(FF_MULTI_SELECT, multiSelect);
    }

    /**
     * If true, more than one of the field's option items may be selected simultaneously; if false, at most one item shall be selected.
     * @return whether or not multiple selection is currently allowed
     */
    public boolean isMultiSelect() {
        return getFieldFlag(FF_MULTI_SELECT);
    }

    /**
     * If true, text entered in the field shall be spell-checked..
     * @param spellCheck whether or not to require the PDF viewer to perform a spell check
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setSpellCheck(boolean spellCheck) {
        return (PdfChoiceFormField) setFieldFlag(FF_DO_NOT_SPELL_CHECK, !spellCheck);
    }

    /**
     * If true, text entered in the field shall be spell-checked..
     * @return whether or not PDF viewer must perform a spell check
     */
    public boolean isSpellCheck() {
        return !getFieldFlag(FF_DO_NOT_SPELL_CHECK);
    }

    /**
     * If true, the new value shall be committed as soon as a selection is made (commonly with the pointing device).
     * @param commitOnSelChange whether or not to save changes immediately
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setCommitOnSelChange(boolean commitOnSelChange) {
        return (PdfChoiceFormField) setFieldFlag(FF_COMMIT_ON_SEL_CHANGE, commitOnSelChange);
    }

    /**
     * If true, the new value shall be committed as soon as a selection is made (commonly with the pointing device).
     * @return whether or not to save changes immediately
     */
    public boolean isCommitOnSelChange() {
        return getFieldFlag(FF_COMMIT_ON_SEL_CHANGE);
    }

    private List<String> optionsToUnicodeNames() {
        PdfArray options = getOptions();
        List<String> optionsToUnicodeNames = new ArrayList<String>(options.size());
        for (int index = 0; index < options.size(); index++) {
            PdfObject option = options.get(index);
            PdfString value = null;
            if (option.isString()) {
                value = (PdfString) option;
            } else if (option.isArray()) {
                value = (PdfString) ((PdfArray) option).get(1);
            }
            optionsToUnicodeNames.add(value != null ? value.toUnicodeString() : null);
        }
        return optionsToUnicodeNames;
    }
}
