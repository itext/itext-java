package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

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
        return put(PdfName.TI, new PdfNumber(index));
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
        return put(PdfName.I, indices);
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
        return setFieldFlag(FF_COMBO, combo);
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
        return setFieldFlag(FF_EDIT, edit);
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
     * If true, the field’s option items shall be sorted alphabetically.
     * This flag is intended for use by writers, not by readers.
     * @param sort whether or not to sort the items
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setSort(boolean sort) {
        return setFieldFlag(FF_SORT, sort);
    }

    /**
     * If true, the field’s option items shall be sorted alphabetically.
     * This flag is intended for use by writers, not by readers.
     * @return whether or not the items are currently sorted
     */
    public boolean isSort() {
        return getFieldFlag(FF_SORT);
    }

    /**
     * If true, more than one of the field’s option items may be selected
     * simultaneously; if false, at most one item shall be selected.
     * @param multiSelect whether or not to allow multiple selection
     * @return current {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField setMultiSelect(boolean multiSelect) {
        return setFieldFlag(FF_MULTI_SELECT, multiSelect);
    }

    /**
     * If true, more than one of the field’s option items may be selected simultaneously; if false, at most one item shall be selected.
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
        return setFieldFlag(FF_DO_NOT_SPELL_CHECK, !spellCheck);
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
        return setFieldFlag(FF_COMMIT_ON_SEL_CHANGE, commitOnSelChange);
    }

    /**
     * If true, the new value shall be committed as soon as a selection is made (commonly with the pointing device).
     * @return whether or not to save changes immediately
     */
    public boolean isCommitOnSelChange() {
        return getFieldFlag(FF_COMMIT_ON_SEL_CHANGE);
    }
}
