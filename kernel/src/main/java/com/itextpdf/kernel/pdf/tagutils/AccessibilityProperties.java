/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
