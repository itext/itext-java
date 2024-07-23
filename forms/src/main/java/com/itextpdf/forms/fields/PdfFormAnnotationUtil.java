/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to work with widget annotations {@link com.itextpdf.forms.fields.PdfFormAnnotation}
 * and its dictionaries.
 */
public final class PdfFormAnnotationUtil {

    private PdfFormAnnotationUtil() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Check that &lt;PdfDictionary&gt; object is widget annotation or merged field.
     *
     * @param fieldDict field dictionary to check.
     *
     * @return true if passed dictionary is a widget or merged field, false otherwise.
     */
    public static boolean isPureWidgetOrMergedField(PdfDictionary fieldDict) {
        if (fieldDict.isFlushed()) {
            return false;
        }
        PdfName subtype = fieldDict.getAsName(PdfName.Subtype);
        return PdfName.Widget.equals(subtype);
    }

    /**
     * Check that &lt;PdfDictionary&gt; object is pure widget annotation.
     *
     * @param fieldDict field dictionary to check.
     *
     * @return true if passed dictionary is a widget, false otherwise.
     */
    public static boolean isPureWidget(PdfDictionary fieldDict) {
        return isPureWidgetOrMergedField(fieldDict) && !PdfFormField.isFormField(fieldDict);
    }

    /**
     * Add widget annotation to the specified page.
     *
     * @param page       to which annotation should be added.
     * @param annotation widget annotation to add.
     */
    public static void addWidgetAnnotationToPage(PdfPage page, PdfAnnotation annotation) {
        addWidgetAnnotationToPage(page, annotation, -1);
    }

    /**
     * Add widget annotation to the specified page.
     *
     * @param page       to which annotation should be added.
     * @param annotation widget annotation to add.
     * @param index      the index at which specified annotation will be added. If {@code -1} then annotation
     *                   will be added to the end of an array.
     */
    public static void addWidgetAnnotationToPage(PdfPage page, PdfAnnotation annotation, int index) {
        if (page.containsAnnotation(annotation)) {
            return;
        }

        TagTreePointer tagPointer = null;
        PdfDocument document = page.getDocument();
        boolean tagged = document.isTagged();
        if (tagged) {
            tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
            if (!StandardRoles.FORM.equals(tagPointer.getRole())) {
                tagPointer.addTag(StandardRoles.FORM);
            }
        }

        page.addAnnotation(index, annotation, true);

        if (tagged) {
            tagPointer.moveToParent();
        }
    }

    /**
     * Merge single widget annotation with its parent field.
     *
     * @param field parent field.
     */
    public static void mergeWidgetWithParentField(PdfFormField field) {
        PdfArray kids = field.getKids();
        if (kids != null && kids.size() == 1) {
            PdfDictionary kidDict = (PdfDictionary) kids.get(0);
            if (isPureWidget(kidDict)) {
                // kid is not merged field with widget
                kidDict.remove(PdfName.Parent);
                field.getPdfObject().mergeDifferent(kidDict);
                field.removeChildren();
                kidDict.getIndirectReference().setFree();
                field.setChildField(PdfFormAnnotation.makeFormAnnotation(field.getPdfObject(), field.getDocument()));

                replaceAnnotationOnPage(kidDict, field.getPdfObject());
            }
        }
    }

    /**
     * Separate merged field to form field and pure widget annotation.
     * Do nothing if the incoming field is not merged field.
     *
     * @param field to separate.
     */
    public static void separateWidgetAndField(PdfFormField field) {
        PdfDictionary fieldDict = field.getPdfObject();
        // If field is merged with widget
        if (isPureWidgetOrMergedField(fieldDict)) {
            PdfDictionary widgetDict = new PdfDictionary(fieldDict);
            replaceAnnotationOnPage(fieldDict, widgetDict);
            Set<PdfName> dictKeys = new LinkedHashSet<>(fieldDict.keySet());
            // Split field dictionary onto two
            for (PdfName key : dictKeys) {
                if (PdfFormField.getFormFieldKeys().contains(key) || PdfName.Parent.equals(key)) {
                    widgetDict.remove(key);
                } else {
                    fieldDict.remove(key);
                }
            }

            List<AbstractPdfFormField> newKids = new ArrayList<>();
            newKids.add(PdfFormAnnotation.makeFormAnnotation(widgetDict, field.getDocument()));
            field.replaceKids(newKids);
        }
    }

    private static void replaceAnnotationOnPage(PdfDictionary oldAnnotDict, PdfDictionary newAnnotDict) {
        // Get page for the old annotation
        PdfAnnotation oldAnnot = PdfAnnotation.makeAnnotation(oldAnnotDict);
        PdfPage page = oldAnnot.getPage();

        // Remove old annotation and add new
        if (page != null) {
            int annotIndex = -1;
            PdfArray annots = page.getPdfObject().getAsArray(PdfName.Annots);
            if (annots != null) {
                annotIndex = annots.indexOf(oldAnnotDict);
            }
            page.removeAnnotation(oldAnnot, true);
            oldAnnotDict.remove(PdfName.P);
            if (annotIndex >= page.getAnnotsSize()) {
                annotIndex = -1;
            }
            if (newAnnotDict.get(PdfName.P) == null) {
                newAnnotDict.put(PdfName.P, page.getPdfObject());
            }
            addNewWidgetToPage(page, newAnnotDict, annotIndex);
        }
    }

    private static void addNewWidgetToPage(PdfPage currentPage, PdfDictionary field, int annotIndex) {
        PdfDictionary pageDic = field.getAsDictionary(PdfName.P);
        if (pageDic.isFlushed()) {
            return;
        }
        PdfDocument doc = pageDic.getIndirectReference().getDocument();
        PdfPage widgetPage = doc.getPage(pageDic);
        addWidgetAnnotationToPage(widgetPage == null ? currentPage : widgetPage,
                PdfAnnotation.makeAnnotation(field), annotIndex);
    }
}
