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

import com.itextpdf.forms.fields.merging.OnDuplicateFormFieldNameStrategy;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to merge form fields {@link com.itextpdf.forms.fields.PdfFormField} with the same names.
 */
public final class PdfFormFieldMergeUtil {

    private PdfFormFieldMergeUtil() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * This method merges all kids with the same names for the given parent field dictionary (recursively).
     *
     * @param parentField           a field whose kids should be checked and merged in case of same partial names.
     * @param throwExceptionOnError true if the exception is expected after the merge failed, false if log is expected.
     */
    public static void mergeKidsWithSameNames(PdfFormField parentField, boolean throwExceptionOnError) {
        PdfDictionary parent = parentField.getPdfObject();
        if (parentField.isInReadingMode()) {
            // Do nothing in reading mode.
            return;
        }
        PdfArray kids = parent.getAsArray(PdfName.Kids);
        if (kids == null || kids.isEmpty()) {
            return;
        }
        Map<String, AbstractPdfFormField> addedKids = new LinkedHashMap<>();
        List<AbstractPdfFormField> newKids = new ArrayList<>();
        for (AbstractPdfFormField kid : parentField.getChildFields()) {
            if (kid instanceof PdfFormField) {
                // Try to merge for the kid
                mergeKidsWithSameNames((PdfFormField) kid, throwExceptionOnError);

                String kidName = getPartialName(kid);
                if (!addedKids.containsKey(kidName) || !mergeTwoFieldsWithTheSameNames(
                        (PdfFormField) addedKids.get(kidName), (PdfFormField) kid, throwExceptionOnError)) {
                    addedKids.put(getPartialName(kid), kid);
                    newKids.add(kid);
                }
            } else {
                // It's a pure widget
                newKids.add(kid);
            }
        }
        parentField.replaceKids(newKids);

        processDirtyAnnotations(parentField, throwExceptionOnError);
    }

    /**
     * This method merges different values from two field dictionaries into the first one and combines kids.
     *
     * @param firstField            a field into which dictionary all values will be merged.
     * @param secondField           a field whose values should be merged into the first dictionary.
     * @param throwExceptionOnError true if the exception is expected after the merge failed, false if log is expected.
     *
     * @return true if fields is successfully merged, false otherwise.
     */
    public static boolean mergeTwoFieldsWithTheSameNames(PdfFormField firstField, PdfFormField secondField,
            boolean throwExceptionOnError) {
        if (firstField.getPdfObject() == secondField.getPdfObject()) {
            // We don't need to perform any strategy on duplicated references, we can just always remove them.
            return true;
        }
        final OnDuplicateFormFieldNameStrategy onDuplicateFormFieldNameStrategy = firstField.getDocument()
                .getDiContainer()
                .getInstance(OnDuplicateFormFieldNameStrategy.class);
        return onDuplicateFormFieldNameStrategy.execute(firstField, secondField, throwExceptionOnError);
    }

    /**
     * Gets partial name for the field dictionary.
     *
     * @param field field to get name from.
     *
     * @return field partial name. Also, null if passed dictionary is a pure widget,
     * empty string in case it is a field with no /T entry.
     */
    public static String getPartialName(AbstractPdfFormField field) {
        if (PdfFormAnnotationUtil.isPureWidget(field.getPdfObject())) {
            return null;
        }
        if (field instanceof PdfFormField) {
            return ((PdfFormField) field).getPartialFieldName().toUnicodeString();
        }
        return "";
    }

    /**
     * Sometimes widgets contain field related keys, and they are the same as these field keys at parent.
     * During merge process we get something like: ParentField {@code (/DA <DA1> /Ft <Tx> /T <test> /Kids <Field>) ->}
     * Field {@code (/DA <DA1> /Kids <Annotation>) ->} Annotation (without any form fields)
     *
     * <p>
     * This method combines ParentField with Field.
     *
     * @param parentField           a field whose form field kids should be checked and merged with parent in case
     *                              all their dictionary values (except Parent and Kids) are the same
     *                              or parent is a radio button.
     * @param throwExceptionOnError true if the exception is expected after the merge failed, false if log is expected.
     */
    public static void processDirtyAnnotations(PdfFormField parentField, boolean throwExceptionOnError) {
        for (PdfFormField field : parentField.getChildFormFields()) {
            PdfDictionary formDict = field.getPdfObject();
            // Process form fields without PdfName.Widget having only annotations as children
            if (field.getChildFields().size() > 0 && field.getChildFormFields().size() == 0) {
                boolean shouldBeMerged = true;

                // If parent is radio button or signature we don't care about field related keys, always merge
                // If not - go over all fields to compare with parent's fields
                if (!(PdfName.Btn.equals(parentField.getFormType()) &&
                        parentField.getFieldFlag(PdfButtonFormField.FF_RADIO)) &&
                        !PdfName.Sig.equals(parentField.getFormType())) {
                    if (formDict.containsKey(PdfName.T)) {
                        // We only want to perform the merge if field doesn't contain any name (even empty one)
                        continue;
                    }
                    for (final PdfName key : formDict.keySet()) {
                        // Everything except Parent and Kids must be identical to allow the merge
                        if (!PdfName.Parent.equals(key) && !PdfName.Kids.equals(key) &&
                                !formDict.get(key).equals(parentField.getPdfObject().get(key))) {
                            shouldBeMerged = false;
                            break;
                        }
                    }
                }

                if (shouldBeMerged) {
                    parentField.removeChild(field);
                    formDict.remove(PdfName.Parent);
                    // We know for sure that parentField and field must be merged here
                    mergeFormFields(parentField, field, throwExceptionOnError);
                }
            }
        }
    }

    /**
     * This method combines two form fields.
     *
     * @param firstField first form field to be merged
     * @param secondField second form field to be merged
     * @param throwExceptionOnError define whether exception (true) or log (false) is expected in case kid with
     *                              the same name exists and merge of two kids failed.
     */
    public static void mergeFormFields(PdfFormField firstField, PdfFormField secondField,
            boolean throwExceptionOnError) {
        PdfFormAnnotationUtil.separateWidgetAndField(firstField);
        PdfFormAnnotationUtil.separateWidgetAndField(secondField);
        PdfDictionary firstFieldDict = firstField.getPdfObject();
        PdfDictionary secondFieldDict = secondField.getPdfObject();

        for (PdfName key : new ArrayList<>(secondFieldDict.keySet())) {
            if (PdfName.Kids.equals(key)) {
                // Merge kids
                for (AbstractPdfFormField kid : new ArrayList<>(secondField.getChildFields())) {
                    firstField.addKid(kid, throwExceptionOnError);
                }
            } else if (PdfName.Parent.equals(key)) {
                // Never copy parent
            }
            else if (!firstFieldDict.containsKey(key)) {
                // Add all unique keys from the second field into the first field
                firstField.put(key, secondFieldDict.get(key));
            }
            // Else values of the first dictionary will remain.
        }
    }
}
