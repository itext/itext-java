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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Default factory for form related instances creation.
 */
public class PdfFormFactory {

    /**
     * Create {@link PdfFormFactory} instance.
     */
    public PdfFormFactory() {
        // Empty constructor.
    }

    /**
     * Create a minimal, empty {@link PdfFormField}.
     *
     * @param document The {@link PdfDocument} instance.
     * 
     * @return created {@link PdfFormField}
     */
    public PdfFormField createFormField(PdfDocument document) {
        return new PdfFormField(document);
    }

    /**
     * Creates a form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfFormField}
     */
    public PdfFormField createFormField(PdfDictionary dictionary) {
        return new PdfFormField(dictionary);
    }

    /**
     * Creates a form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfFormField}
     */
    public PdfFormField createFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return new PdfFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfTextFormField}.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfTextFormField}
     */
    public PdfTextFormField createTextFormField(PdfDocument document) {
        return new PdfTextFormField(document);
    }

    /**
     * Creates a text form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfTextFormField}
     */
    public PdfTextFormField createTextFormField(PdfDictionary dictionary) {
        return new PdfTextFormField(dictionary);
    }

    /**
     * Creates a text form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfTextFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfTextFormField}
     */
    public PdfTextFormField createTextFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return new PdfTextFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfButtonFormField}.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfButtonFormField}
     */
    public PdfButtonFormField createButtonFormField(PdfDocument document) {
        return new PdfButtonFormField(document);
    }

    /**
     * Creates a button form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfButtonFormField}
     */
    public PdfButtonFormField createButtonFormField(PdfDictionary dictionary) {
        return new PdfButtonFormField(dictionary);
    }

    /**
     * Creates a button form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfButtonFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfButtonFormField}
     */
    public PdfButtonFormField createButtonFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return new PdfButtonFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfChoiceFormField}.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField createChoiceFormField(PdfDocument document) {
        return new PdfChoiceFormField(document);
    }

    /**
     * Creates a choice form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField createChoiceFormField(PdfDictionary dictionary) {
        return new PdfChoiceFormField(dictionary);
    }

    /**
     * Creates a choice form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfChoiceFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfChoiceFormField}
     */
    public PdfChoiceFormField createChoiceFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return new PdfChoiceFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfSignatureFormField}.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfSignatureFormField}
     */
    public PdfSignatureFormField createSignatureFormField(PdfDocument document) {
        return new PdfSignatureFormField(document);
    }

    /**
     * Creates a signature form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfSignatureFormField}
     */
    public PdfSignatureFormField createSignatureFormField(PdfDictionary dictionary) {
        return new PdfSignatureFormField(dictionary);
    }

    /**
     * Creates a signature form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfSignatureFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfSignatureFormField}
     */
    public PdfSignatureFormField createSignatureFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return new PdfSignatureFormField(widget, document);
    }

    /**
     * Creates a form field annotation as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfFormAnnotation}
     */
    public PdfFormAnnotation createFormAnnotation(PdfDictionary dictionary) {
        return new PdfFormAnnotation(dictionary);
    }

    /**
     * Creates a form field annotation as a wrapper of a {@link PdfWidgetAnnotation}.
     *
     * @param widget      The widget which will be a kid of the {@link PdfFormField}
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfFormAnnotation}
     */
    public PdfFormAnnotation createFormAnnotation(PdfWidgetAnnotation widget, PdfDocument document) {
        return new PdfFormAnnotation(widget, document);
    }

    /**
     * Retrieves AcroForm from the document. If there is no AcroForm in the
     * document Catalog and createIfNotExist flag is true then the AcroForm
     * dictionary will be created and added to the document.
     *
     * @param document         the document to retrieve the {@link PdfAcroForm} from
     * @param createIfNotExist when <code>true</code>, this method will create a {@link PdfAcroForm}
     *                         if none exists for this document
     * 
     * @return the {@link PdfDocument document}'s AcroForm, or a new one provided that <code>createIfNotExist</code>
     * parameter is <code>true</code>, otherwise <code>null</code>.
     */
    public PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
        return PdfAcroForm.getAcroForm(document, createIfNotExist);
    }
}
