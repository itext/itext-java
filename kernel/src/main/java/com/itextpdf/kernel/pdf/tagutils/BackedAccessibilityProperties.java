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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BackedAccessibilityProperties extends AccessibilityProperties {

    private static final long serialVersionUID = 4080083623525383278L;

    private TagTreePointer pointerToBackingElem;

    BackedAccessibilityProperties(TagTreePointer pointerToBackingElem) {
        this.pointerToBackingElem = new TagTreePointer(pointerToBackingElem);
    }

    @Override
    public String getRole() {
        return getBackingElem().getRole().getValue();
    }

    @Override
    public AccessibilityProperties setRole(String role) {
        getBackingElem().setRole(PdfStructTreeRoot.convertRoleToPdfName(role));
        return this;
    }

    @Override
    public String getLanguage() {
        return toUnicodeString(getBackingElem().getLang());
    }

    @Override
    public AccessibilityProperties setLanguage(String language) {
        getBackingElem().setLang(new PdfString(language, PdfEncodings.UNICODE_BIG));
        return this;
    }

    @Override
    public String getActualText() {
        return toUnicodeString(getBackingElem().getActualText());
    }

    @Override
    public AccessibilityProperties setActualText(String actualText) {
        getBackingElem().setActualText(new PdfString(actualText, PdfEncodings.UNICODE_BIG));
        return this;
    }

    @Override
    public String getAlternateDescription() {
        return toUnicodeString(getBackingElem().getAlt());
    }

    @Override
    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        getBackingElem().setAlt(new PdfString(alternateDescription, PdfEncodings.UNICODE_BIG));
        return this;
    }

    @Override
    public String getExpansion() {
        return toUnicodeString(getBackingElem().getE());
    }

    @Override
    public AccessibilityProperties setExpansion(String expansion) {
        getBackingElem().setE(new PdfString(expansion, PdfEncodings.UNICODE_BIG));
        return this;
    }

    @Override
    public AccessibilityProperties addAttributes(PdfStructureAttributes attributes) {
        return addAttributes(-1, attributes);
    }

    public AccessibilityProperties addAttributes(int index, PdfStructureAttributes attributes) {
        if (attributes == null) {
            return this;
        }

        PdfObject attributesObject = getBackingElem().getAttributes(false);

        PdfObject combinedAttributes = AccessibilityPropertiesToStructElem.combineAttributesList(
                attributesObject, index, Collections.singletonList(attributes),
                getBackingElem().getPdfObject().getAsNumber(PdfName.R));
        getBackingElem().setAttributes(combinedAttributes);
        return this;
    }

    @Override
    public AccessibilityProperties clearAttributes() {
        getBackingElem().getPdfObject().remove(PdfName.A);
        return this;
    }

    @Override
    public List<PdfStructureAttributes> getAttributesList() {
        ArrayList<PdfStructureAttributes> attributesList = new ArrayList<>();
        PdfObject elemAttributesObj = getBackingElem().getAttributes(false);
        if (elemAttributesObj != null) {
            if (elemAttributesObj.isDictionary()) {
                attributesList.add(new PdfStructureAttributes((PdfDictionary) elemAttributesObj));
            } else if (elemAttributesObj.isArray()) {
                PdfArray attributesArray = (PdfArray) elemAttributesObj;
                for (PdfObject attributeObj : attributesArray) {
                    if (attributeObj.isDictionary()) {
                        attributesList.add(new PdfStructureAttributes((PdfDictionary) attributeObj));
                    }
                }
            }
        }
        return attributesList;
    }

    @Override
    public AccessibilityProperties setPhoneme(String phoneme) {
        getBackingElem().setPhoneme(new PdfString(phoneme));
        return this;
    }

    @Override
    public String getPhoneme() {
        return toUnicodeString(getBackingElem().getPhoneme());
    }

    @Override
    public AccessibilityProperties setPhoneticAlphabet(String phoneticAlphabet) {
        getBackingElem().setPhoneticAlphabet(PdfStructTreeRoot.convertRoleToPdfName(phoneticAlphabet));
        return this;
    }

    @Override
    public String getPhoneticAlphabet() {
        return getBackingElem().getPhoneticAlphabet().getValue();
    }

    public AccessibilityProperties setNamespace(PdfNamespace namespace) {
        getBackingElem().setNamespace(namespace);
        pointerToBackingElem.getContext().ensureNamespaceRegistered(namespace);
        return this;
    }

    public PdfNamespace getNamespace() {
        return getBackingElem().getNamespace();
    }

    @Override
    public AccessibilityProperties addRef(TagTreePointer treePointer) {
        getBackingElem().addRef(treePointer.getCurrentStructElem());
        return this;
    }

    @Override
    public List<TagTreePointer> getRefsList() {
        List<TagTreePointer> refsList = new ArrayList<>();
        for (PdfStructElem ref : getBackingElem().getRefsList()) {
            refsList.add(new TagTreePointer(ref, pointerToBackingElem.getDocument()));
        }
        return Collections.unmodifiableList(refsList);
    }

    @Override
    public AccessibilityProperties clearRefs() {
        getBackingElem().getPdfObject().remove(PdfName.Ref);
        return this;
    }

    private PdfStructElem getBackingElem() {
        return pointerToBackingElem.getCurrentStructElem();
    }

    private String toUnicodeString(PdfString pdfString) {
        return pdfString != null ? pdfString.toUnicodeString() : null;
    }
}
