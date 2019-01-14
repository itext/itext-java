/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultAccessibilityProperties extends AccessibilityProperties {

    private static final long serialVersionUID = 3139055327755008473L;

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

}
