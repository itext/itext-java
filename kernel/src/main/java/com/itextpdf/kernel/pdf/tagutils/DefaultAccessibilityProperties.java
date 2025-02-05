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

import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The class represents a basic implementation of {@link AccessibilityProperties} that preserves specified
 * accessibility properties. Accessibility properties are used to define properties of 
 * {@link PdfStructElem structure elements} in Tagged PDF documents via {@link TagTreePointer} API.
 */
public class DefaultAccessibilityProperties extends AccessibilityProperties {
    protected String role;
    protected String language;
    protected String actualText;
    protected String alternateDescription;
    protected String expansion;
    protected List<PdfStructureAttributes> attributesList = new ArrayList<>();

    protected String phoneme;
    protected String phoneticAlphabet;
    protected PdfNamespace namespace;
    protected List<TagTreePointer> refs = new ArrayList<>();

    private byte[] structElemId;

    /**
     * Instantiates a new {@link DefaultAccessibilityProperties} instance based on structure element role.
     *
     * @param role the structure element role
     */
    public DefaultAccessibilityProperties(String role) {
        this.role = role;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public AccessibilityProperties setRole(String role) {
        this.role = role;
        return this;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public AccessibilityProperties setLanguage(String language) {
        this.language = language;
        return this;
    }

    @Override
    public String getActualText() {
        return actualText;
    }

    @Override
    public AccessibilityProperties setActualText(String actualText) {
        this.actualText = actualText;
        return this;
    }

    @Override
    public String getAlternateDescription() {
        return alternateDescription;
    }

    @Override
    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        this.alternateDescription = alternateDescription;
        return this;
    }

    @Override
    public String getExpansion() {
        return expansion;
    }

    @Override
    public AccessibilityProperties setExpansion(String expansion) {
        this.expansion = expansion;
        return this;
    }

    @Override
    public AccessibilityProperties addAttributes(PdfStructureAttributes attributes) {
        return addAttributes(-1, attributes);
    }

    @Override
    public AccessibilityProperties addAttributes(int index, PdfStructureAttributes attributes) {
        if (attributes != null) {
            if (index > 0) {
                attributesList.add(index, attributes);
            } else {
                attributesList.add(attributes);
            }
        }
        return this;
    }

    @Override
    public AccessibilityProperties clearAttributes() {
        attributesList.clear();
        return this;
    }

    @Override
    public List<PdfStructureAttributes> getAttributesList() {
        return attributesList;
    }

    @Override
    public String getPhoneme() {
        return this.phoneme;
    }

    @Override
    public AccessibilityProperties setPhoneme(String phoneme) {
        this.phoneme = phoneme;
        return this;
    }

    @Override
    public String getPhoneticAlphabet() {
        return this.phoneticAlphabet;
    }

    @Override
    public AccessibilityProperties setPhoneticAlphabet(String phoneticAlphabet) {
        this.phoneticAlphabet = phoneticAlphabet;
        return this;
    }

    @Override
    public PdfNamespace getNamespace() {
        return this.namespace;
    }

    @Override
    public AccessibilityProperties setNamespace(PdfNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public AccessibilityProperties addRef(TagTreePointer treePointer) {
        refs.add(new TagTreePointer(treePointer));
        return this;
    }

    @Override
    public List<TagTreePointer> getRefsList() {
        return Collections.unmodifiableList(refs);
    }

    @Override
    public AccessibilityProperties clearRefs() {
        refs.clear();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getStructureElementId() {
        return this.structElemId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties setStructureElementId(byte[] id) {
        this.structElemId = Arrays.copyOf(id, id.length);
        return this;
    }
}
