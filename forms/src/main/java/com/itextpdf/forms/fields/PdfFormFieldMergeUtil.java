/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (kids == null || kids.size() == 0) {
            return;
        }
        Map<String, AbstractPdfFormField> addedKids = new LinkedHashMap<>();
        List<AbstractPdfFormField> newKids = new ArrayList<>();
        for (AbstractPdfFormField kid : parentField.getChildFields()) {
            if (kid instanceof PdfFormField) {
                // Try to merge for the kid
                mergeKidsWithSameNames((PdfFormField) kid, throwExceptionOnError);

                String kidName = getPartialName(kid.getPdfObject());
                if (!addedKids.containsKey(kidName) || !mergeTwoFieldsWithTheSameNames(
                        (PdfFormField) addedKids.get(kidName), (PdfFormField) kid, throwExceptionOnError)) {
                    addedKids.put(kidName, kid);
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
        PdfName firstFieldFormType = firstField.getFormType();
        PdfObject firstFieldValue = firstField.getValue();
        PdfObject firstFieldDefaultValue = firstField.getDefaultValue();
        PdfObject secondFieldDefaultValue = secondField.getDefaultValue();
        if ((firstFieldFormType == null || firstFieldFormType.equals(secondField.getFormType())) &&
                (firstFieldValue == null || firstFieldValue.equals(secondField.getValue())) &&
                (firstFieldDefaultValue == null || secondFieldDefaultValue == null ||
                        firstFieldDefaultValue.equals(secondFieldDefaultValue))) {
            mergeFormFields(firstField, secondField, throwExceptionOnError);
        } else {
            if (throwExceptionOnError) {
                throw new PdfException(MessageFormatUtil.format(FormsExceptionMessageConstant.CANNOT_MERGE_FORMFIELDS,
                        firstField.getPartialFieldName()));
            } else {
                Logger logger = LoggerFactory.getLogger(PdfFormFieldMergeUtil.class);
                logger.warn(MessageFormatUtil.format(FormsLogMessageConstants.CANNOT_MERGE_FORMFIELDS,
                        firstField.getPartialFieldName()));
                return false;
            }
        }
        return true;
    }

    /**
     * Gets partial name for the field dictionary.
     *
     * @param fieldDict field dictionary to get name.
     *
     * @return field partial name. Also, null if passed dictionary is a pure widget,
     * empty string in case it is a field with no /T entry.
     */
    // TODO This method usages should be replaced by PdfFormField#getPartialFieldName after DEVSIX-7308 is closed.
    public static String getPartialName(PdfDictionary fieldDict) {
        if (PdfFormAnnotationUtil.isPureWidget(fieldDict)) {
            return null;
        }
        PdfString partialName = fieldDict.getAsString(PdfName.T);
        return partialName == null ? "" : partialName.toUnicodeString();
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
            if (!PdfFormAnnotationUtil.isPureWidgetOrMergedField(formDict) &&
                    field.getChildFields().size() > 0 &&
                    field.getChildFormFields().size() == 0) {
                boolean shouldBeMerged = true;

                // If parent is radio button we don't care about field related keys, always merge
                // If not - go over all fields to compare with parent's fields
                if (!(PdfName.Btn.equals(parentField.getFormType()) &&
                        parentField.getFieldFlag(PdfButtonFormField.FF_RADIO))) {
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

    private static void mergeFormFields(PdfFormField firstField, PdfFormField secondField, boolean throwExceptionOnError) {
        PdfFormAnnotationUtil.separateWidgetAndField(firstField);
        PdfFormAnnotationUtil.separateWidgetAndField(secondField);
        PdfDictionary firstFieldDict = firstField.getPdfObject();
        PdfDictionary secondFieldDict = secondField.getPdfObject();
        // Sometimes we merge field with its merged widget annotation, so secondField's /Parent is firstField.
        // It can be a problem in case firstField is a root field, that's why secondField's /Parent is removed.
        secondFieldDict.remove(PdfName.Parent);
        for (PdfName key : new ArrayList<>(secondFieldDict.keySet())) {
            if (PdfName.Kids.equals(key)) {
                // Merge kids
                for (AbstractPdfFormField kid : new ArrayList<>(secondField.getChildFields())) {
                    firstField.addKid(kid, throwExceptionOnError);
                }
            } else if (!firstFieldDict.containsKey(key)) {
                // Add all unique keys from the second field into the first field
                firstField.put(key, secondFieldDict.get(key));
            }
            // Else values of the first dictionary will remain.
        }
    }
}
