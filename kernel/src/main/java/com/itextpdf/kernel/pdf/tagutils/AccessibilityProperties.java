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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * The accessibility properties are used to define properties of {@link PdfStructElem structure elements}
 * in Tagged PDF documents via {@link TagTreePointer} API.
 */
public abstract class AccessibilityProperties {
    /**
     * Gets the role of element.
     *
     * <p>
     * See also {@link StandardRoles}.
     *
     * @return the role
     */
    public String getRole() {
        return null;
    }

    /**
     * Sets the role of element.
     *
     * <p>
     * See also {@link StandardRoles}.
     *
     * <p>
     * Calling this method with a null argument will make the tagging on the associated layout
     * element "neutral". The effect is that all children of the layout element will be
     * tagged as if they were direct children of the parent element.
     *
     * @param role the role to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setRole(String role) {
        return this;
    }

    /**
     * Gets the language identifier of element. Should be in format xy-ZK (for example en-US).
     *
     * <p>
     * For more information see PDF Specification ISO 32000-1 section 14.9.2.
     *
     * @return the language
     */
    public String getLanguage() {
        return null;
    }

    /**
     * Sets the language identifier of element. Should be in format xy-ZK (for example en-US).
     *
     * <p>
     * For more information see PDF Specification ISO 32000-1 section 14.9.2.
     *
     * @param language the language to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setLanguage(String language) {
        return this;
    }

    /**
     * Gets the actual text of element.
     *
     * @return the actual text
     */
    public String getActualText() {
        return null;
    }

    /**
     * Sets the actual text of element.
     *
     * @param actualText the actual text to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setActualText(String actualText) {
        return this;
    }

    /**
     * Gets the alternate description of element.
     *
     * @return the alternate description
     */
    public String getAlternateDescription() {
        return null;
    }

    /**
     * Sets the alternate description of element.
     *
     * @param alternateDescription the alternation description to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        return this;
    }

    /**
     * Gets the expansion of element.
     *
     * <p>
     * Expansion it is the expanded form of an abbreviation of structure element.
     *
     * @return the expansion
     */
    public String getExpansion() {
        return null;
    }

    /**
     * Sets the expansion of element.
     *
     * <p>
     * Expansion it is the expanded form of an abbreviation of structure element.
     *
     * @param expansion the expansion to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setExpansion(String expansion) {
        return this;
    }

    /**
     * Gets the phoneme of element.
     *
     * <p>
     * For more information see {@link PdfStructElem#setPhoneme(PdfString)}.
     *
     * @return the phoneme
     */
    public String getPhoneme() {
        return null;
    }

    /**
     * Sets the phoneme of element.
     *
     * <p>
     * For more information see {@link PdfStructElem#setPhoneme(PdfString)}.
     *
     * @param phoneme the phoneme to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setPhoneme(String phoneme) {
        return this;
    }

    /**
     * Gets the phonetic alphabet of element.
     *
     * <p>
     * For more information see {@link PdfStructElem#setPhoneticAlphabet(PdfName)}.
     *
     * @return the phonetic alphabet
     */
    public String getPhoneticAlphabet() {
        return null;
    }

    /**
     * Sets the phonetic alphabet of element.
     *
     * <p>
     * For more information see {@link PdfStructElem#setPhoneticAlphabet(PdfName)}.
     *
     * @param phoneticAlphabet the phonetic alphabet to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setPhoneticAlphabet(String phoneticAlphabet) {
        return this;
    }

    /**
     * Gets the namespace of element.
     *
     * @return the namespace
     */
    public PdfNamespace getNamespace() {
        return null;
    }

    /**
     * Sets the namespace of element.
     *
     * @param namespace the namespace to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setNamespace(PdfNamespace namespace) {
        return this;
    }

    /**
     * Adds the reference to other tagged element.
     *
     * <p>
     * For more information see {@link PdfStructElem#addRef(PdfStructElem)}.
     *
     * @param treePointer the reference to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties addRef(TagTreePointer treePointer) {
        return this;
    }

    /**
     * Gets the list of references to other tagged elements.
     *
     * <p>
     * For more information see {@link PdfStructElem#addRef(PdfStructElem)}.
     *
     * @return the list of references
     */
    public List<TagTreePointer> getRefsList() {
        return Collections.<TagTreePointer>emptyList();
    }

    /**
     * Clears the list of references to other tagged elements.
     *
     * <p>
     * For more information see {@link PdfStructElem#addRef(PdfStructElem)}.
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties clearRefs() {
        return this;
    }

    /**
     * Adds the attributes to the element.
     *
     * @param attributes the attributes to be added
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties addAttributes(PdfStructureAttributes attributes) {
        return this;
    }

    /**
     * Adds the attributes to the element with specified index.
     *
     * <p>
     * If an attribute with the same O and NS entries is specified more than once, the later (in array order)
     * entry shall take precedence. For more information see PDF Specification ISO-32000 section 14.7.6.
     *
     * @param index the attributes index
     * @param attributes the attributes to be added
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties addAttributes(int index, PdfStructureAttributes attributes) {
        return this;
    }

    /**
     * Clears the list of attributes.
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties clearAttributes() {
        return this;
    }

    /**
     * Gets the attributes list.
     *
     * @return the attributes list
     */
    public List<PdfStructureAttributes> getAttributesList() {
        return Collections.<PdfStructureAttributes>emptyList();
    }

    /**
     * Gets the associated structure element's ID string, if it has one.
     *
     * <p>
     * See also {@link PdfStructElem#getStructureElementId()}.
     *
     * @return the structure element's ID string, or null if there is none
     */
    public byte[] getStructureElementId() {
        return null;
    }

    /**
     * Sets the associated structure element's ID. Passing {@code null} removes the ID.
     *
     * <p>
     * See also {@link PdfStructElem#setStructureElementId(PdfString)}.
     *
     * @param id the element's ID to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setStructureElementId(byte[] id) {
        return this;
    }

    /**
     * Sets the associated structure element's ID. Passing {@code null} removes the ID.
     * If non-null, the argument will be encoded in UTF-8 (without BOM), since ID
     * strings are considered binary data in PDF.
     *
     * <p>
     * See also {@link PdfStructElem#setStructureElementId(PdfString)}.
     *
     * @param id the element's ID to be set
     *
     * @return this {@link AccessibilityProperties} instance
     */
    public AccessibilityProperties setStructureElementIdString(String id) {
        return this.setStructureElementId(id == null ? null : id.getBytes(StandardCharsets.UTF_8));
    }
}
