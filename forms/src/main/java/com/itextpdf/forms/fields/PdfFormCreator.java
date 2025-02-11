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
 * Creator which shall be used in order to create all form related instances. By default {@link PdfFormFactory} is used.
 */
public final class PdfFormCreator {
    
    private static PdfFormFactory factory = new PdfFormFactory();
    
    private PdfFormCreator() {
    }

    /**
     * Set {@link PdfFormFactory} to be used for form related instances creation.
     * 
     * @param factory {@link PdfFormFactory} to set
     */
    public static void setFactory(PdfFormFactory factory) {
        PdfFormCreator.factory = factory;
    }

    /**
     * Create a minimal, empty {@link PdfFormField} using provided factory.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfFormField}
     */
    public static PdfFormField createFormField(PdfDocument document) {
        return factory.createFormField(document);
    }

    /**
     * Creates a form field as a wrapper object around a {@link PdfDictionary} using provided factory.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfFormField}
     */
    public static PdfFormField createFormField(PdfDictionary dictionary) {
        return factory.createFormField(dictionary);
    }

    /**
     * Creates a form field as a parent of a {@link PdfWidgetAnnotation} using provided factory.
     *
     * @param widget The widget which will be a kid of the {@link PdfFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfFormField}
     */
    public static PdfFormField createFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return factory.createFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfTextFormField} using provided factory.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfTextFormField}
     */
    public static PdfTextFormField createTextFormField(PdfDocument document) {
        return factory.createTextFormField(document);
    }

    /**
     * Creates a text form field as a wrapper object around a {@link PdfDictionary} using provided factory.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfTextFormField}
     */
    public static PdfTextFormField createTextFormField(PdfDictionary dictionary) {
        return factory.createTextFormField(dictionary);
    }

    /**
     * Creates a text form field as a parent of a {@link PdfWidgetAnnotation} using provided factory.
     *
     * @param widget The widget which will be a kid of the {@link PdfTextFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfTextFormField}
     */
    public static PdfTextFormField createTextFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return factory.createTextFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfButtonFormField} using provided factory.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButtonFormField(PdfDocument document) {
        return factory.createButtonFormField(document);
    }

    /**
     * Creates a button form field as a wrapper object around a {@link PdfDictionary} using provided factory.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButtonFormField(PdfDictionary dictionary) {
        return factory.createButtonFormField(dictionary);
    }

    /**
     * Creates a button form field as a parent of a {@link PdfWidgetAnnotation} using provided factory.
     *
     * @param widget The widget which will be a kid of the {@link PdfButtonFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfButtonFormField}
     */
    public static PdfButtonFormField createButtonFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return factory.createButtonFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfChoiceFormField} using provided factory.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoiceFormField(PdfDocument document) {
        return factory.createChoiceFormField(document);
    }

    /**
     * Creates a choice form field as a wrapper object around a {@link PdfDictionary} using provided factory.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoiceFormField(PdfDictionary dictionary) {
        return factory.createChoiceFormField(dictionary);
    }

    /**
     * Creates a choice form field as a parent of a {@link PdfWidgetAnnotation} using provided factory.
     *
     * @param widget The widget which will be a kid of the {@link PdfChoiceFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfChoiceFormField}
     */
    public static PdfChoiceFormField createChoiceFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return factory.createChoiceFormField(widget, document);
    }

    /**
     * Create a minimal, empty {@link PdfSignatureFormField} using provided factory.
     *
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignatureFormField(PdfDocument document) {
        return factory.createSignatureFormField(document);
    }

    /**
     * Creates a signature form field as a wrapper object around a {@link PdfDictionary} using provided factory.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignatureFormField(PdfDictionary dictionary) {
        return factory.createSignatureFormField(dictionary);
    }

    /**
     * Creates a signature form field as a parent of a {@link PdfWidgetAnnotation} using provided factory.
     *
     * @param widget The widget which will be a kid of the {@link PdfSignatureFormField}.
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfSignatureFormField}
     */
    public static PdfSignatureFormField createSignatureFormField(PdfWidgetAnnotation widget, PdfDocument document) {
        return factory.createSignatureFormField(widget, document);
    }

    /**
     * Creates a form field annotation as a wrapper object around a {@link PdfDictionary} using provided factory.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param dictionary the dictionary to be wrapped, must have an indirect reference.
     *
     * @return created {@link PdfFormAnnotation}
     */
    public static PdfFormAnnotation createFormAnnotation(PdfDictionary dictionary) {
        return factory.createFormAnnotation(dictionary);
    }

    /**
     * Creates a form field annotation as a wrapper of a {@link PdfWidgetAnnotation} using provided factory.
     *
     * @param widget      The widget which will be a kid of the {@link PdfFormField}
     * @param document The {@link PdfDocument} instance.
     *
     * @return created {@link PdfFormAnnotation}
     */
    public static PdfFormAnnotation createFormAnnotation(PdfWidgetAnnotation widget, PdfDocument document) {
        return factory.createFormAnnotation(widget, document);
    }

    /**
     * Retrieves AcroForm from the document using provided factory. If there is no AcroForm in the
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
    public static PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
        return factory.getAcroForm(document, createIfNotExist);
    }
}
