package com.itextpdf.canvas;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.tagging.PdfMcr;

public class CanvasTag {
    protected PdfName role;
    protected Integer mcid;
    protected PdfDictionary properties;

    public CanvasTag(PdfName role) {
        this.role = role;
    }

    /**
     * Creates a tag that is referenced to the document's tag structure (i.e. logical structure)
     * @param role
     * @param mcid marked content id which serves as a reference to the document's logical structure
     */
    public CanvasTag(PdfName role, Integer mcid) {
        this.role = role;
        this.mcid = mcid;

        addProperty(PdfName.MCID, new PdfNumber(mcid));
    }

    public CanvasTag(PdfMcr mcr) {
        this(mcr.getRole(), mcr.getMcid());
    }

    public PdfName getRole() {
        return role;
    }

    public Integer getMcid() {
        return mcid;
    }

    public CanvasTag addProperties(PdfDictionary properties) {
        if (properties == null)
            return this;

        ensurePropertiesInit();
        properties.putAll(properties);
        return this;
    }

    public CanvasTag addProperty(PdfName name, PdfObject value) {
        ensurePropertiesInit();

        properties.put(name, value);
        return this;
    }

    public CanvasTag removeProperty(PdfName name) {
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

    private void ensurePropertiesInit() {
        if (properties == null) {
            properties = new PdfDictionary();
        }
    }
}
