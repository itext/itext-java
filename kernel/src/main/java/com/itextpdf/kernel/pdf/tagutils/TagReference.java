package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

public class TagReference {
    protected TagTreePointer tagPointer;
    protected int insertIndex;
    protected PdfStructElem referencedTag;

    protected PdfName role;
    protected PdfDictionary properties;

    protected TagReference(PdfStructElem referencedTag, TagTreePointer tagPointer, int insertIndex) {
        this.role = referencedTag.getRole();
        this.referencedTag = referencedTag;
        this.tagPointer = tagPointer;
        this.insertIndex = insertIndex;
    }

    public PdfName getRole() {
        return role;
    }

    public Integer createNextMcid() {
        return tagPointer.createNextMcidForStructElem(referencedTag, insertIndex);
    }

    public TagReference addProperty(PdfName name, PdfObject value) {
        if (properties == null) {
            properties = new PdfDictionary();
        }

        properties.put(name, value);
        return this;
    }

    public TagReference removeProperty(PdfName name) {
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
