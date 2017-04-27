/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccessibilityProperties implements Serializable {

    private static final long serialVersionUID = 3139055327755008473L;

    protected String language;
    protected String actualText;
    protected String alternateDescription;
    protected String expansion;
    protected List<PdfDictionary> attributesList = new ArrayList<>();

    protected String phoneme;
    protected PdfName phoneticAlphabet;
    protected PdfNamespace namespace;
    protected List<TagTreePointer> refs = new ArrayList<>();


    public String getLanguage() {
        return language;
    }

    public AccessibilityProperties setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getActualText() {
        return actualText;
    }

    public AccessibilityProperties setActualText(String actualText) {
        this.actualText = actualText;
        return this;
    }

    public String getAlternateDescription() {
        return alternateDescription;
    }

    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        this.alternateDescription = alternateDescription;
        return this;
    }

    public String getExpansion() {
        return expansion;
    }

    public AccessibilityProperties setExpansion(String expansion) {
        this.expansion = expansion;
        return this;
    }

    public AccessibilityProperties addAttributes(PdfDictionary attributes) {
        return addAttributes(-1, attributes);
    }

    public AccessibilityProperties addAttributes(int index, PdfDictionary attributes) {
        if (attributes != null) {
            if (index > 0) {
                attributesList.add(index, attributes);
            } else {
                attributesList.add(attributes);
            }
        }
        return this;
    }

    public AccessibilityProperties clearAttributes() {
        attributesList.clear();
        return this;
    }

    public List<PdfDictionary> getAttributesList() {
        return attributesList;
    }

    public AccessibilityProperties setPhoneme(String phoneme) {
        this.phoneme = phoneme;
        return this;
    }

    public String getPhoneme() {
        return this.phoneme;
    }

    public AccessibilityProperties setPhoneticAlphabet(PdfName phoneticAlphabet) {
        this.phoneticAlphabet = phoneticAlphabet;
        return this;
    }

    public PdfName getPhoneticAlphabet() {
        return this.phoneticAlphabet;
    }

    public AccessibilityProperties setNamespace(PdfNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    public PdfNamespace getNamespace() {
        return this.namespace;
    }

    public AccessibilityProperties addRef(TagTreePointer treePointer) {
        refs.add(new TagTreePointer(treePointer));
        return this;
    }

    public List<TagTreePointer> getRefsList() {
        return Collections.unmodifiableList(refs);
    }

    public AccessibilityProperties clearRefs() {
        refs.clear();
        return this;
    }

    void setToStructElem(PdfStructElem elem) {
        if (getActualText() != null) {
            elem.setActualText(new PdfString(getActualText()));
        }
        if (getAlternateDescription() != null) {
            elem.setAlt(new PdfString(getAlternateDescription()));
        }
        if (getExpansion() != null) {
            elem.setE(new PdfString(getExpansion()));
        }
        if (getLanguage() != null) {
            elem.setLang(new PdfString(getLanguage()));
        }

        List<PdfDictionary> newAttributesList = getAttributesList();
        if (newAttributesList.size() > 0) {
            PdfObject attributesObject = elem.getAttributes(false);

            PdfObject combinedAttributes = combineAttributesList(attributesObject, -1, newAttributesList, elem.getPdfObject().getAsNumber(PdfName.R));
            elem.setAttributes(combinedAttributes);
        }

        if (getPhoneme() != null) {
            elem.setPhoneme(new PdfString(getPhoneme()));
        }
        if (getPhoneticAlphabet() != null) {
            elem.setPhoneticAlphabet(getPhoneticAlphabet());
        }
        if (getNamespace() != null) {
            elem.setNamespace(getNamespace());
        }
        for (TagTreePointer ref : refs) {
            elem.addRef(ref.getCurrentStructElem());
        }
    }

    @Deprecated
    protected PdfObject combineAttributesList(PdfObject attributesObject, List<PdfDictionary> newAttributesList, PdfNumber revision) {
        return combineAttributesList(attributesObject, -1, newAttributesList, revision);
    }

    @Deprecated
    protected void addNewAttributesToAttributesArray(List<PdfDictionary> newAttributesList, PdfNumber revision, PdfArray attributesArray) {
        addNewAttributesToAttributesArray(-1, newAttributesList, revision, attributesArray);
    }

    protected static PdfObject combineAttributesList(PdfObject attributesObject, int insertIndex, List<PdfDictionary> newAttributesList, PdfNumber revision) {
        PdfObject combinedAttributes;

        if (attributesObject instanceof PdfDictionary) {
            PdfArray combinedAttributesArray = new PdfArray();
            combinedAttributesArray.add(attributesObject);
            addNewAttributesToAttributesArray(insertIndex, newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else if (attributesObject instanceof PdfArray) {
            PdfArray combinedAttributesArray = (PdfArray) attributesObject;
            addNewAttributesToAttributesArray(insertIndex, newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else {
            if (newAttributesList.size() == 1) {
                if (insertIndex > 0) {
                    throw new IndexOutOfBoundsException();
                }
                combinedAttributes = newAttributesList.get(0);
            } else {
                combinedAttributes = new PdfArray();
                addNewAttributesToAttributesArray(insertIndex, newAttributesList, revision, (PdfArray) combinedAttributes);
            }
        }

        return combinedAttributes;
    }

    protected static void addNewAttributesToAttributesArray(int insertIndex, List<PdfDictionary> newAttributesList, PdfNumber revision, PdfArray attributesArray) {
        if (insertIndex < 0) {
            insertIndex = attributesArray.size();
        }
        if (revision != null) {
            for (PdfDictionary attributes : newAttributesList) {
                attributesArray.add(insertIndex++, attributes);
                attributesArray.add(insertIndex++, revision);
            }
        } else {
            for (PdfDictionary newAttribute : newAttributesList) {
                attributesArray.add(insertIndex++, newAttribute);
            }
        }
    }
}
