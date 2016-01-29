package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

public class PdfTagReference {
    protected PdfTagStructure tagStructure;
    protected int insertIndex;
    protected PdfStructElem referencedTag;

    protected PdfName role;
    protected PdfDictionary properties;

    protected PdfTagReference(PdfStructElem referencedTag, PdfTagStructure tagStructure, int insertIndex) {
        this.role = referencedTag.getRole();
        this.referencedTag = referencedTag;
        this.tagStructure = tagStructure;
        this.insertIndex = insertIndex;
    }

    public PdfName getRole() {
        return role;
    }

    public Integer createNextMcid() {
        return tagStructure.createNextMcidForStructElem(referencedTag, insertIndex);
    }

    public PdfTagReference addProperty(PdfName name, PdfObject value) {
        if (properties == null) {
            properties = new PdfDictionary();
        }

        properties.put(name, value);
        return this;
    }

    public PdfTagReference removeProperty(PdfName name) {
        if (properties != null) {
            properties.remove(name);
        }
        return this;
    }

    public PdfObject getProperty(PdfName name) {
        if (properties == null) {
            return null;
        }
        return properties.get(name);
    }

    public PdfDictionary getProperties() {
        return properties;
    }
}
