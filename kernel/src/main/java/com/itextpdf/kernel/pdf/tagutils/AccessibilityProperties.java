/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public abstract class AccessibilityProperties implements Serializable {
    public String getRole() {
        return null;
    }

    public AccessibilityProperties setRole(String role) {
        return this;
    }

    public String getLanguage() {
        return null;
    }

    public AccessibilityProperties setLanguage(String language) {
        return this;
    }

    public String getActualText() {
        return null;
    }

    public AccessibilityProperties setActualText(String actualText) {
        return this;
    }

    public String getAlternateDescription() {
        return null;
    }

    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        return this;
    }

    public String getExpansion() {
        return null;
    }

    public AccessibilityProperties setExpansion(String expansion) {
        return this;
    }

    public String getPhoneme() {
        return null;
    }

    public AccessibilityProperties setPhoneme(String phoneme) {
        return this;
    }

    public String getPhoneticAlphabet() {
        return null;
    }

    public AccessibilityProperties setPhoneticAlphabet(String phoneticAlphabet) {
        return this;
    }

    public PdfNamespace getNamespace() {
        return null;
    }

    public AccessibilityProperties setNamespace(PdfNamespace namespace) {
        return this;
    }

    public AccessibilityProperties addRef(TagTreePointer treePointer) {
        return this;
    }

    public List<TagTreePointer> getRefsList() {
        return Collections.<TagTreePointer>emptyList();
    }

    public AccessibilityProperties clearRefs() {
        return this;
    }

    public AccessibilityProperties addAttributes(PdfStructureAttributes attributes) {
        return this;
    }

    public AccessibilityProperties addAttributes(int index, PdfStructureAttributes attributes) {
        return this;
    }

    public AccessibilityProperties clearAttributes() {
        return this;
    }

    public List<PdfStructureAttributes> getAttributesList() {
        return Collections.<PdfStructureAttributes>emptyList();
    }
}
