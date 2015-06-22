package com.itextpdf.forms.formfields;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

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

    public PdfChoiceFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    public PdfChoiceFormField(PdfDocument pdfDocument, PdfWidgetAnnotation widget) {
        super(pdfDocument, widget);
    }

    protected PdfChoiceFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfChoiceFormField(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Ch;
    }

    @Override
    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }

    public PdfChoiceFormField setTopIndex(int index) {
        return put(PdfName.TI, new PdfNumber(index));
    }

    public PdfNumber getTopIndex() {
        return getPdfObject().getAsNumber(PdfName.TI);
    }

    public PdfChoiceFormField setIndices(PdfArray indices) {
        return put(PdfName.I, indices);
    }

    public PdfArray getIndices() {
        return getPdfObject().getAsArray(PdfName.I);
    }

    /*
    If true, the field is a combo box; if false, the field is a list box.
     */
    public PdfChoiceFormField setCombo(boolean combo) {
        return setFieldFlag(FF_COMBO, combo);
    }

    /*
    If true, the field is a combo box; if false, the field is a list box.
     */
    public boolean isCombo() {
        return getFieldFlag(FF_COMBO);
    }

    /*
    If true, the combo box shall include an editable text box as well as a drop-down list; if false, it shall include only a drop-down list.
    This flag shall be used only if the Combo flag is true.
     */
    public PdfChoiceFormField setEdit(boolean edit) {
        return setFieldFlag(FF_EDIT, edit);
    }

    /*
    If true, the combo box shall include an editable text box as well as a drop-down list; if false, it shall include only a drop-down list.
    This flag shall be used only if the Combo flag is true.
     */
    public boolean isEdit() {
        return getFieldFlag(FF_EDIT);
    }

    /*
    If true, the field’s option items shall be sorted alphabetically. This flag is intended for use by writers, not by readers.
     */
    public PdfChoiceFormField setSort(boolean sort) {
        return setFieldFlag(FF_SORT, sort);
    }

    /*
    If true, the field’s option items shall be sorted alphabetically. This flag is intended for use by writers, not by readers.
     */
    public boolean isSort() {
        return getFieldFlag(FF_SORT);
    }

    /*
    If true, more than one of the field’s option items may be selected simultaneously; if false, at most one item shall be selected.
     */
    public PdfChoiceFormField setMultiSelect(boolean multiSelect) {
        return setFieldFlag(FF_MULTI_SELECT, multiSelect);
    }

    /*
    If true, more than one of the field’s option items may be selected simultaneously; if false, at most one item shall be selected.
     */
    public boolean isMultiSelect() {
        return getFieldFlag(FF_MULTI_SELECT);
    }

    /*
    If true, text entered in the field shall be spell-checked..
     */
    public PdfChoiceFormField setSpellCheck(boolean spellCheck) {
        return setFieldFlag(FF_DO_NOT_SPELL_CHECK, !spellCheck);
    }

    /*
    If true, text entered in the field shall be spell-checked..
     */
    public boolean isSpellCheck() {
        return !getFieldFlag(FF_DO_NOT_SPELL_CHECK);
    }

    /*
    If true, the new value shall be committed as soon as a selection is made (commonly with the pointing device).
     */
    public PdfChoiceFormField setCommitOnSelChange(boolean commitOnSelChange) {
        return setFieldFlag(FF_COMMIT_ON_SEL_CHANGE, commitOnSelChange);
    }

    /*
    If true, the new value shall be committed as soon as a selection is made (commonly with the pointing device).
     */
    public boolean isCommitOnSelChange() {
        return getFieldFlag(FF_COMMIT_ON_SEL_CHANGE);
    }
}
